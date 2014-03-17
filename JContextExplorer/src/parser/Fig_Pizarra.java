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

import inicial.Language;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import newickTreeParsing.Tree;
import newickTreeParsing.TreeNode;

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
	
	
	//New fields
	public Cluster ComputedRootCluster;
	public Cluster DendrogramCluster;
	private double LongestBranch = 0.0;
	
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
		
		//JCE-appropriate
//		if (cf != null){
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
			
			if (c.getFills() > 200){
				construeixMatriuUltrametrica(c, true); 	//don't fill in the matrix
			} else {
				construeixMatriuUltrametrica(c, false); // do fill in the matrix (time-consuming)
			}
			
//		//should only be null when calling from other classes.
//		} else {
//			val_Max_show = 1;
//			radi = 1;
//			prec = 18;
//			
//			Branca(abre, true); //branch
//		}


	}

	public Fig_Pizarra(Tree ImportedNewickTree, final Config cf) throws Exception {
		
		//convert the tree to a cluster
		Cluster c = ConvertTree2Cluster(ImportedNewickTree);
		
		//Usual cluster processing
		abre = c;
		val_Max_show = cf.getValorMaxim();
		//val_Max_show = 1.0;
		radi = cf.getRadi();
		Fig_Pizarra.tip = cf.getTipusMatriu();
		prec = cf.getPrecision();
		if (tip.equals(tipusDades.DISTANCIA)) {
			posNodes = 0.0;
		} else {
			posNodes = val_Max_show;
		}
		Branca(abre, cf.getConfigMenu().isFranjaVisible()); //branch
		
		//time-consuming point! 
		construeixMatriuUltrametrica(c, true);
		
		//save the value as a field.
		this.ComputedRootCluster = c;

	}

	//method to convert between data types
	private Cluster ConvertTree2Cluster(Tree T) {

		//iterate through nodes, determine heights
		boolean HasParent;
		double TotalWeights;
		double LongestJourney = 0;
		int MaxHeight = 1;
		
		for (int i = 0; i < T.nodes.size(); i++){
			//Retrieve Tree Node
			TreeNode TN = T.getNodeByKey(i);

			//re-initialize
			if (TN.isRoot()){
				HasParent = false;
			} else {
				HasParent = true;
			}

			TotalWeights = 0;
			
			//CN = Current Node, initially, this particular tree node TN
			TreeNode CN = TN;
			TreeNode PN;
			
			//Determine Height
			while (HasParent){
				PN = CN.parent();
				if (PN != null){
					TotalWeights = TotalWeights + CN.weight;
					CN = PN;
				} else {
					HasParent = false;
				}
					
			}
			
			//update total length
			if (TotalWeights > LongestJourney){
				LongestJourney = TotalWeights;
			}
			
			//update max height
			if (TN.height > MaxHeight){
				MaxHeight = TN.height;
			}
		}
		
		//update longest branch
		this.LongestBranch = LongestJourney;
		
		for (int i = 0; i < T.nodes.size(); i++){
			TreeNode TN = T.getNodeByKey(i);
			
			//re-initialize
			if (TN.isRoot()){
				HasParent = false;
			} else {
				HasParent = true;
			}
			
			//CN = Current Node, initially, this particular tree node TN
			TreeNode CN = TN;
			TreeNode PN;
			TotalWeights = 0;
			
			//Determine Height
			while (HasParent){
				PN = CN.parent();
				if (PN != null){
					TotalWeights = TotalWeights + CN.weight;
					CN = PN;
				} else {
					HasParent = false;
				}
			}
			
			//update Alcada <height> value
			//TN.Alcada = 1 - (TotalWeights/LongestJourney);
			TN.Alcada = LongestBranch - TotalWeights;
			
		}

		// Create intermediate cluster nodes at every step, starting from the leaf nodes.
					//<Key,Value> = <ID, cluster>
		LinkedHashMap<Integer,Cluster> CreatedClusters = new LinkedHashMap<Integer,Cluster>();
		HashSet<TreeNode> Children = new HashSet<TreeNode>();
		int RootKey = 0;

		//recover all nodes at the deepest level - initial children set
		for (int i = 0; i <T.nodes.size(); i++){
			TreeNode TN = T.getNodeByKey(i);
			if (TN.height == MaxHeight){
				
				//update list of children
				Children.add(TN);
				
				//Initialize cluster info
				Cluster c = new Cluster();
				c.setAlcada(TN.Alcada);
				c.setNom(TN.label);
				c.setNado(false);
				c.setPhyloSupport(TN.getSupport());
				CreatedClusters.put(TN.getKey(), c);
				if (TN.isRoot()){
					RootKey = i;
				}
			}
		}

		HashSet<TreeNode> Parents;

		//keep scanning, organizing, until not possible any more
		//while (NodesReceived < T.nodes.size()){
		for (int CurrentHeight = MaxHeight-1; CurrentHeight > 0; CurrentHeight--){
			
			//determine parents of current children
			Parents = new HashSet<TreeNode>();
			for (int i = 0; i <T.nodes.size(); i++){
				TreeNode TN = T.getNodeByKey(i);
				if (TN.height == CurrentHeight){
					Parents.add(TN);
				}
			
			}

			//Translate each parent tree node into a cluster structure
			for (TreeNode TN : Parents){
				
				//Initialize cluster info for the parent
				Cluster c = new Cluster();
				c.setAlcada(TN.Alcada);
				c.setPhyloSupport(TN.getSupport());
				if (TN.isLeaf()){
					c.setNom(TN.label);
					c.setNado(false);
				} else {
					c.setNom(Integer.toString(TN.getKey()));

					try {
						
						boolean SetNado = true;
						//find all children for this node
						for (TreeNode CN : Children){
							if (CN.parent.getKey() == TN.getKey()){
								if (CN.isLeaf()){
									SetNado = false;
								}
								c.addCluster(CreatedClusters.get(CN.getKey()));
							}
						}
						c.setNado(SetNado);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
				//mark root key
				if (TN.isRoot()){
					RootKey = TN.getKey();
				}
				
				//add to hash map
				CreatedClusters.put(TN.getKey(), c);

			}
			
			//update
			Children = Parents;
		}

		return CreatedClusters.get(RootKey);
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
					//FesLog.LOG.throwing(msg_err, "Branca(final Cluster c)", e);
					throw new Exception(msg_err);
				}

				// create line
				Linia Lin = new Linia(pos, c.getAlcada(), prec);

				//note if line extends to leaf, and determine factor
				if (c.getFill(n).getFills() == 1){
					
					//write phylo fraction
					Lin.setExtendsToLeaf(true);
					double PhyFrac = 1.0 - ((c.getAlcada() - c.getFill(n).getAlcada())/LongestBranch);

					Lin.setPhyloFraction(PhyFrac);
				}
				
				figura[Fig_Pizarra.LINIA].add(Lin);
				
//				FesLog.LOG
//						.finer("new Linia: (" + pos.getX() + ", " + pos.getY()
//								+ ", " + c.getAlcada() + ", " + prec + ")");

				min = min > pos.getX() ? pos.getX() : min;
				max = max < pos.getX() ? pos.getX() : max;
			}

			//innitialize group
			Marge m;
			
			// store the group
			if (franja) { 		//strip
				
				m = new Marge(min, c.getAlcada(), aglo, (max-min), prec);
				m.setPhyloWeight(c.getPhyloSupport());
				
//				figura[Fig_Pizarra.MARGE].add(new Marge(min, c.getAlcada(),
//						aglo, (max - min), prec));
//				FesLog.LOG.finer("Marge: (" + min + ", " + c.getAlcada() + ", "
//						+ aglo + ", " + (max - min));
			} else {
				
				m = new Marge(min, c.getAlcada(), 0, (max-min), prec);
				m.setPhyloWeight(c.getPhyloSupport());
				
//				figura[Fig_Pizarra.MARGE].add(new Marge(min, c.getAlcada(), 0,
//						(max - min), prec));
//				FesLog.LOG.finer("Marge: (" + min + ", " + c.getAlcada() + ", "
//						+ 0 + ", " + (max - min));
			}

//			System.out.println("Fig_Pizarra m: " + m.getPhyloWeight());

			//add Marge
			figura[Fig_Pizarra.MARGE].add(m);
			
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
	public void construeixMatriuUltrametrica(Cluster c, boolean isPhylo) {
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

		}

		//Only need to fill matrix when computing context tree.
		if (!isPhylo){
			ompleMatriuUltrametrica(c);
		}

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

	public Hashtable<Integer, String> getHtNoms() {
		// Need to switch arguments for output
		Hashtable<Integer, String> HtNomsSwitched = new Hashtable<Integer, String>();
		Enumeration Names = htNoms.keys();
		while (Names.hasMoreElements()){
			String Key = (String) Names.nextElement();
			HtNomsSwitched.put(htNoms.get(Key),Key);
		}
		return HtNomsSwitched;
	}

	public double getLongestBranch() {
		return LongestBranch;
	}

	public void setLongestBranch(double longestBranch) {
		LongestBranch = longestBranch;
	}

}
