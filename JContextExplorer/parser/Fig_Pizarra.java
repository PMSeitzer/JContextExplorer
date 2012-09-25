/*
 * Copyright (C) Justo Montiel, David Torres, Sergio Gomez, Alberto Fernandez
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see
 * <http://www.gnu.org/licenses/>
 */

package parser;

import inicial.FesLog;
import inicial.Language;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import parser.figures.Cercle;
import parser.figures.Linia;
import parser.figures.Marge;
import tipus.tipusDades;
import definicions.Cluster;
import definicions.Config;
import definicions.Coordenada;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Convert the dendrogram to geometric figures
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class Fig_Pizarra {

// ----- Fields -----------------------------------------------//
	
	private static final int CERCLE = 0;
	private static final int LINIA = 1;
	private static final int MARGE = 2;
	private final int prec;
	private int next = 0;
	private final double radi;
	private final Cluster abre;
	private static tipusDades tip;
	private double posNodes = 0.0;
	private final double val_Max_show;

	public static String[] noms;

	private final LinkedList[] figura = { new LinkedList<Cercle>(),
			new LinkedList<Linia>(), new LinkedList<Marge>() };

	private final Hashtable<String, Integer> htNoms = new Hashtable<String, Integer>();
	public static Double[][] mat_ultrametrica;
	
	// ----- Methods -----------------------------------------------//

	public Fig_Pizarra(final Cluster c, final Config cf) throws Exception {
		abre = c;
		val_Max_show = cf.getValorMaxim();
		radi = cf.getRadi();
		Fig_Pizarra.tip = cf.getTipusMatriu();
		prec = cf.getPrecision();
		if (tip.equals(tipusDades.DISTANCIA)) {
			posNodes = 0.0;
		} else {
			posNodes = val_Max_show;
		}
		Branca(abre, cf.getConfigMenu().isFranjaVisible()); //branch
		construeixMatriuUltrametrica(c);
	}

	//		Coordinates		   Leaf
	private Coordenada<Double> Fulla(final Cluster c) {
		double x;
		final Coordenada<Double> pos = new Coordenada<Double>(0.0, 0.0);
		next++;
		x = radi * ((3 * next) - 1);
		pos.setX(x);
		pos.setY(posNodes);
		figura[Fig_Pizarra.CERCLE].add(new Cercle(pos, radi, prec, c.getNom()));

		return pos;
	}
	
	//		Coordinates 	   Branch 								//strip
	private Coordenada<Double> Branca(final Cluster c, final boolean franja)
			throws Exception {
		Coordenada<Double> pos = new Coordenada<Double>(0.0, 0.0);
		double aglo;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		if ((c.getFamily() == 1) && (c.getFills() == 1))
			pos = this.Fulla(c);
		else {
			aglo = c.getAglomeracio(); // grouping

			for (int n = 0; n < c.getFamily(); n++) {
				try {
					pos = this.Branca(c.getFill(n), franja);
				} catch (Exception e) {
					String msg_err = Language.getLabel(64) + "\n"
							+ e.getMessage();
					FesLog.LOG.throwing(msg_err, "Branca(final Cluster c)", e);
					throw new Exception(msg_err);
				}

				// change of level, line store
				figura[Fig_Pizarra.LINIA].add(new Linia(pos, c.getAlcada(),
						prec));
				FesLog.LOG
						.finer("new Linia: (" + pos.getX() + ", " + pos.getY()
								+ ", " + c.getAlcada() + ", " + prec + ")");

				min = min > pos.getX() ? pos.getX() : min;
				max = max < pos.getX() ? pos.getX() : max;
			}

			// store the group
			if (franja) {
				figura[Fig_Pizarra.MARGE].add(new Marge(min, c.getAlcada(),
						aglo, (max - min), prec));
				FesLog.LOG.finer("Marge: (" + min + ", " + c.getAlcada() + ", "
						+ aglo + ", " + (max - min));
			} else {
				figura[Fig_Pizarra.MARGE].add(new Marge(min, c.getAlcada(), 0,
						(max - min), prec));
				FesLog.LOG.finer("Marge: (" + min + ", " + c.getAlcada() + ", "
						+ 0 + ", " + (max - min));
			}

			pos.setX((min + max) / 2);

			// weights the agglomeration group downwards
			if (tip.equals(tipusDades.DISTANCIA)) {
				if (franja)
					pos.setY(c.getAlcada() + c.getAglomeracio());
				else
					pos.setY(c.getAlcada());
			} else {
				if (franja)
					pos.setY(c.getAlcada() - c.getAglomeracio());
				else
					pos.setY(c.getAlcada());
			}
		}

		return pos;
	}

	//			build ultrametric matrix
	public void construeixMatriuUltrametrica(Cluster c) {
		List<Cluster> lFills = c.getLstFills();
		List<String> lNoms = new LinkedList<String>();
		for (int i = 0; i < lFills.size(); i++)
			lNoms.add((lFills.get(i)).getNom());
		Collections.sort(lNoms);

		int mida = c.getFills(); // files/cols. de la matriu
		mat_ultrametrica = new Double[mida][mida];
		noms = new String[mida]; //mida = size
		for (int i = 0; i < lNoms.size(); i++) {
			htNoms.put(lNoms.get(i), i); // 'a'->0, 'b'->1, ...
			noms[i] = lNoms.get(i);
			
			
			//messing with the stuff below doesn't do much
//			System.out.println(noms[i]);
//			if (i == 3){
//				noms[i] = "jooky!";
//			}
//			System.out.println((i+1) + ": " + noms[i]);
			
			/*
			 * this print statement displays the names in 
			 * the order they were written in the file.
			 */
			//System.out.println(noms[i]);
			
		}

		ompleMatriuUltrametrica(c);
	}

	//			 fill ultrametric matrix
	private void ompleMatriuUltrametrica(Cluster c) {
		if (c.getFamily() > 1) {
			List<Cluster> l = c.getLstFills();
			double alc = c.getAlcada();
			for (int n = 0; n < l.size(); n++)
				for (int i = 0; i < c.getLstFills().size(); i++) {
					Cluster ci = c.getLstFills().get(i);
					int posi = htNoms.get(ci.getNom());
					for (int j = 0; j < c.getLstFills().size(); j++) {
						Cluster cj = c.getLstFills().get(j);
						int posj = htNoms.get(cj.getNom());
						if (posi == posj)
							mat_ultrametrica[posi][posj] = 0.0;
						else
							mat_ultrametrica[posi][posj] = alc;
					}
				}

			for (int n = 0; n < c.getFamily(); n++) {
				try {
					ompleMatriuUltrametrica(c.getFill(n)); //fill ultrametric matrix
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int getIndCercle() {
		return Fig_Pizarra.CERCLE;
	}

	public static int getIndLinia() {
		return Fig_Pizarra.LINIA;
	}

	public static int getIndMarge() {
		return Fig_Pizarra.MARGE;
	}

	public LinkedList[] getFigures() {
		return figura;
	}

}
