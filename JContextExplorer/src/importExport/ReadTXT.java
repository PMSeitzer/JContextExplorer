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

package importExport;

import genomeObjects.ExtendedCRON;
import inicial.FesLog;
import inicial.Language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import ContextForest.DissimilarityMatrixData;

import errors.FitxerIncompatible;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Reads a text file containing a distances matrix in either list or matrix
 * format, with and without headers
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class ReadTXT { //just a program to read in .txt files

	private final String nomfitx;
	private int numElements = 0;
	private String[] TaulaNoms;
	private final LinkedList<String[]> lstdades;
	private LinkedList<StructIn<String>> lst;
	private static final double NULL = -1.0;
	private final ExtendedCRON EC;
	
	//							file path
	public ReadTXT(final String pathFichero) throws Exception {
		boolean tipusA, tipusB;
		nomfitx = pathFichero;
		lstdades = this.PosaEnMemoria();
		String[] tmpC;
		int nl, nc;
		EC = new ExtendedCRON();

		tmpC = lstdades.get(0);
		nc = tmpC.length;
		nl = lstdades.size();
		// Matrix or list format
		if ((nc > 3) || ((nc == 3) && (nl == 4)))
			lst = this.llegeixMatriu();
		else if ((nc == 3) && (nl == 3)) {
			LinkedList<StructIn<String>> lstA, lstM;
			try {
				lstA = this.llegeixAparellat();
				tipusA = true;
			} catch (final Exception e) {
				tipusA = false;
				lstA = null;
			}

			try {
				lstM = this.llegeixMatriu();
				tipusB = true;
			} catch (final Exception e) {
				tipusB = false;
				lstM = null;
			}

			if (tipusA && tipusB) {
				// Unable to determine format
				final String msg = Language.getLabel(10);
				JOptionPane.showMessageDialog(null, msg, "Warning",
						JOptionPane.WARNING_MESSAGE);
				lst = lstA;
			} else if (tipusA)
				lst = lstA;
			else if (tipusB)
				lst = lstM;
			else
				throw new FitxerIncompatible(Language.getLabel(11));

		} else if (nc == 3) {
			lst = this.llegeixAparellat();
		}
//		if (FesLog.LOG.getLevel().equals(Level.FINER)) {
//			FesLog.LOG.finer("---------- INTRODUCED DATA ----------");
//			for (final StructIn<?> s : lst)
//				FesLog.LOG.finer(s.getC1() + "\t" + s.getC2() + "\t"
//						+ s.getVal());
//		}
	}

	public ReadTXT(final ExtendedCRON EC) throws Exception{
		this.EC = EC;
		nomfitx = "";
		lstdades = this.PosaEnMemoria_noFile();
		String[] tmpC;
		int nl, nc;
		boolean tipusA, tipusB;

		tmpC = lstdades.get(0);
		nc = tmpC.length;
		nl = lstdades.size();
		// Matrix or list format
		if ((nc > 3) || ((nc == 3) && (nl == 4)))
			lst = this.llegeixMatriu();
		else if ((nc == 3) && (nl == 3)) {
			LinkedList<StructIn<String>> lstA, lstM;
			try {
				lstA = this.llegeixAparellat();
				tipusA = true;
			} catch (final Exception e) {
				tipusA = false;
				lstA = null;
			}

			try {
				lstM = this.llegeixMatriu();
				tipusB = true;
			} catch (final Exception e) {
				tipusB = false;
				lstM = null;
			}

			if (tipusA && tipusB) {
				// Unable to determine format
				final String msg = Language.getLabel(10);
				JOptionPane.showMessageDialog(null, msg, "Warning",
						JOptionPane.WARNING_MESSAGE);
				lst = lstA;
			} else if (tipusA)
				lst = lstA;
			else if (tipusB)
				lst = lstM;
			else
				throw new FitxerIncompatible(Language.getLabel(11));

		} else if (nc == 3) {
			lst = this.llegeixAparellat();
		}
		
//		if (FesLog.LOG.getLevel().equals(Level.FINER)) {
//			FesLog.LOG.finer("---------- INTRODUCED DATA ----------");
//			for (final StructIn<?> s : lst)
//				FesLog.LOG.finer(s.getC1() + "\t" + s.getC2() + "\t"
//						+ s.getVal());
//		}
	}
	
	public ReadTXT(DissimilarityMatrixData DMD) throws Exception{
		this.EC = null;
		nomfitx = "";
		lstdades = this.PosaEnMemoria_CF(DMD);
		String[] tmpC;
		int nl, nc;
		boolean tipusA, tipusB;

		tmpC = lstdades.get(0);
		nc = tmpC.length;
		nl = lstdades.size();
		// Matrix or list format
		if ((nc > 3) || ((nc == 3) && (nl == 4)))
			lst = this.llegeixMatriu();
		else if ((nc == 3) && (nl == 3)) {
			LinkedList<StructIn<String>> lstA, lstM;
			try {
				lstA = this.llegeixAparellat();
				tipusA = true;
			} catch (final Exception e) {
				tipusA = false;
				lstA = null;
			}

			try {
				lstM = this.llegeixMatriu();
				tipusB = true;
			} catch (final Exception e) {
				tipusB = false;
				lstM = null;
			}

			if (tipusA && tipusB) {
				// Unable to determine format
				final String msg = Language.getLabel(10);
				JOptionPane.showMessageDialog(null, msg, "Warning",
						JOptionPane.WARNING_MESSAGE);
				lst = lstA;
			} else if (tipusA)
				lst = lstA;
			else if (tipusB)
				lst = lstM;
			else
				throw new FitxerIncompatible(Language.getLabel(11));

		} else if (nc == 3) {
			lst = this.llegeixAparellat();
		}
	}
	
	public LinkedList<StructIn<String>> read() {
		return lst;
	}

	private LinkedList<StructIn<String>> llegeixAparellat() throws Exception {
		LinkedList<StructIn<String>> lstd, lstTmp;
		String a, b;
		final Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
		Double v = null;
		int numLinia = 1;
		int ind = 0;
		int ncols;
		double[][] dades;
		String[] noms;

		lstTmp = new LinkedList<StructIn<String>>();
		for (final String[] s : lstdades) {
			a = s[0];
			b = s[1];
			try {
				v = Double.parseDouble(s[2]);
			} catch (final NumberFormatException e) {
				// Type error in third column
				throw new FitxerIncompatible(Language.getLabel(13) + numLinia
						+ Language.getLabel(14));
			}
			if (!ht.containsKey(a)) {
				ht.put(a, ind++);
			}
			if (!ht.containsKey(b)) {
				ht.put(b, ind++);
			}
			lstTmp.add(new StructIn<String>(a, b, v));
			numLinia++;
		}

		ncols = ht.size();
		ind = 0;
		noms = new String[ncols];
		final Enumeration<String> e = ht.keys();
		String snom;
		while (e.hasMoreElements()) {
			snom = e.nextElement();
			noms[ht.get(snom)] = snom;
		}

		// Initialize matrix
		dades = new double[ncols][ncols];
		for (int r = 0; r < ncols - 1; r++) {
			for (int c = r; c < ncols; c++) {
				dades[r][c] = ReadTXT.NULL;
				dades[c][r] = ReadTXT.NULL;
			}
		}

		lstd = new LinkedList<StructIn<String>>();
		int i, ii, iaux;
		for (final StructIn<?> s : lstTmp) {
			i = ht.get(s.getC1());
			ii = ht.get(s.getC2());
			v = s.getVal();
			if (i > ii) {
				iaux = i;
				i = ii;
				ii = iaux;
			}
			if ((dades[i][ii] != -1) && (dades[i][ii] != v)) {

			} else {
				dades[i][ii] = v;
				lstd.add(new StructIn<String>(noms[i], noms[ii], v));
			}
		}

		for (int r = 0; r < ncols - 1; r++) {
			for (int c = r + 1; c < ncols; c++) {
				// Unassigned distances error
				if (dades[r][c] == ReadTXT.NULL)
					throw new FitxerIncompatible(Language.getLabel(15));
			}
		}

		numElements = ncols;
		TaulaNoms = noms;
		return lstd;
	}

	private LinkedList<StructIn<String>> llegeixMatriu() throws Exception {
		LinkedList<StructIn<String>> lstd = null;
		int nl, nc, row = 0, col = 0;
		double[][] dades;
		Iterator<String[]> it;
		String[] noms = null, tmp;
		nl = lstdades.size();
		nc = lstdades.get(0).length;
		dades = new double[nc][nc];
		it = lstdades.iterator();
		int numLinia = 1;

		if (nl >= nc)
		{
			if (nl > nc) {
				noms = it.next();
			} else {
				noms = new String[nc];
				for (int n = 1; n <= nc; n++) {
					noms[n - 1] = Integer.toString(n);
				}
				numLinia--;
			}

			while (it.hasNext()) {
				numLinia++;
				tmp = it.next();
				if (row >= nc)
					throw new FitxerIncompatible(Language.getLabel(100));
				for (col = 0; col < nc; col++) {
					if (col == row) {
						if (Double.parseDouble(tmp[col]) != 0) {
							throw new FitxerIncompatible(Language.getLabel(101)
									+ numLinia + ")");
						} else
							dades[row][col] = -1.0;
					} else {
						try {
							dades[row][col] = Double.parseDouble(tmp[col]);
						} catch (NumberFormatException e) {
							throw new FitxerIncompatible(Language.getLabel(102));
						}
					}
				}
				row++;
			}

			lstd = new LinkedList<StructIn<String>>();
			for (int r = 0; r < nc - 1; r++) {
				for (int c = r + 1; c < nc; c++) {
					if (dades[r][c] != dades[c][r]) {
						// Non-symmetric matrix error
						throw new FitxerIncompatible(Language.getLabel(12));
					} else
						lstd.add(new StructIn<String>(noms[r], noms[c],
								dades[r][c]));
				}
			}
		}

		else if (nl < nc)
		{
			noms = new String[nl];
			while (it.hasNext()) {
				tmp = it.next();
				for (col = 0; col < nc; col++) {
					if (col == 0)
						noms[row] = tmp[col];
					else {
						if (col == row + 1) {
							if (Double.parseDouble(tmp[col]) != 0) {
								throw new FitxerIncompatible(
										Language.getLabel(101) + numLinia + ")");
							} else
								dades[row][col - 1] = -1.0;
						} else {
							try {
								dades[row][col - 1] = Double
										.parseDouble(tmp[col]);
							} catch (NumberFormatException e) {
								throw new FitxerIncompatible(
										Language.getLabel(102));
							}
						}
					}
				}
				numLinia++;
				row++;
			}

			lstd = new LinkedList<StructIn<String>>();
			for (int r = 0; r < nl - 1; r++) {
				for (int c = r + 1; c < nl; c++) {
					if (dades[r][c] != dades[c][r]) {
						// Non-symmetric matrix error
						throw new FitxerIncompatible(Language.getLabel(12));
					} else
						lstd.add(new StructIn<String>(noms[r], noms[c],
								dades[r][c]));
				}
			}
		}

		numElements = nc;
		TaulaNoms = noms;
		return lstd;
	}

	public int getNumElements() {
		return numElements;
	}

	public String[] getTaulaNoms() {
		return TaulaNoms;
	}

	private LinkedList<String[]> PosaEnMemoria() throws FitxerIncompatible {
		int tmpCol, numCols = 0, numLinia = 1;
		String[] dadesLinia;
		final LinkedList<String[]> lstDades = new LinkedList<String[]>();
		String linia;
		String delims = " ,;|\t\n";
		final File fichero = new File(nomfitx);

		try {
			final FileReader freader = new FileReader(fichero);
			BufferedReader buff = new BufferedReader(freader);

			// Reading headers
			if ((linia = buff.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(linia, delims);
				dadesLinia = new String[numCols];

				numCols = st.countTokens();
				if (numCols < 3) {
					throw new FitxerIncompatible(Language.getLabel(104) + " 1"
							+ Language.getLabel(105) + " '" + fichero.getName()
							+ "'");
				}
				dadesLinia = new String[numCols];

				for (int c = 0; c < numCols; c++) {
					String str = st.nextToken();
					dadesLinia[c] = str;
				}

				lstDades.add(dadesLinia);
			} else {
				// Empty file
				throw new FitxerIncompatible(Language.getLabel(103) + " '"
						+ fichero.getName() + "'");
			}

			while ((linia = buff.readLine()) != null) {
				numLinia++;

				StringTokenizer st = new StringTokenizer(linia, delims);
				dadesLinia = new String[numCols];

				tmpCol = st.countTokens();

				if (tmpCol != numCols) {
					// Number of columns error
					throw new FitxerIncompatible(Language.getLabel(104) + " "
							+ numLinia + Language.getLabel(105) + " '"
							+ fichero.getName() + "'");
				}

				dadesLinia = new String[tmpCol];
				for (int c = 0; c < tmpCol; c++) {
					String str = st.nextToken();
					dadesLinia[c] = str;
				}
				lstDades.add(dadesLinia);
			}
		} catch (IOException e) {
			
		}

		return lstDades;
	}

	private LinkedList<String[]> PosaEnMemoria_noFile(){
		
		int tmpCol, numCols = 0;
		String[] dadesLinia;
		final LinkedList<String[]> lstDades = new LinkedList<String[]>();
		String delims = " ,;|\t\n";

			for (int i = 0; i<EC.getDissimilarities().size(); i++){

				// Reading headers
				StringTokenizer st = new StringTokenizer(EC.getDissimilarities().get(i), delims);
				dadesLinia = new String[numCols];

				numCols = st.countTokens();
				dadesLinia = new String[numCols];

				for (int c = 0; c < numCols; c++) {
					String str = st.nextToken();
					dadesLinia[c] = str;
				}

				lstDades.add(dadesLinia);

				}
			
			return lstDades;			

	}
	
	private LinkedList<String[]> PosaEnMemoria_CF(DissimilarityMatrixData DMD){
		int tmpCol, numCols = 0;
		String[] dadesLinia;
		final LinkedList<String[]> lstDades = new LinkedList<String[]>();
		String delims = " ,;|\t\n";

			for (int i = 0; i< DMD.getFormattedDissimilarities().size(); i++){

				// Reading headers
				StringTokenizer st = new StringTokenizer(DMD.getFormattedDissimilarities().get(i), delims);
				dadesLinia = new String[numCols];

				numCols = st.countTokens();
				dadesLinia = new String[numCols];

				for (int c = 0; c < numCols; c++) {
					String str = st.nextToken();
					dadesLinia[c] = str;
				}

				lstDades.add(dadesLinia);

				}
			
			return lstDades;
	}
	
} //completed classbody
