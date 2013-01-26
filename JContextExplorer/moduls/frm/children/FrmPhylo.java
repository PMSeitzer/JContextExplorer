package moduls.frm.children;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import genomeObjects.CSDisplayData;

import javax.swing.JPanel;

import definicions.Cluster;

import parser.Fig_Pizarra;

import newickTreeParsing.Tree;
import newickTreeParsing.TreeNode;

import moduls.frm.FrmPrincipalDesk;

public class FrmPhylo extends FrmPiz{

	//fields
	private FrmPrincipalDesk fr;
	private CSDisplayData CSD;
	private Tree PhyloTree;
	public Cluster ComputedRootCluster;
	
	//constructor
	public FrmPhylo(FrmPrincipalDesk f, CSDisplayData CSD, Cluster root){
		super(f, CSD);
		this.fr = f;
		this.CSD = CSD;

//		
//		//Retrieve tree from main menu
//		PhyloTree = fr.getPanPhyTreeMenu().getCurrentParsedTree();
//		Tree PhyTree = PhyloTree;
//		TreeNode RootNode = PhyloTree.getRoot();
//		
//		//iterate through nodes, determine heights
//		Tree T = PhyloTree;
//		boolean HasParent;
//		double TotalWeights;
//		double LongestJourney = 0;
//		int MaxHeight = 1;
//		
//		for (int i = 0; i < T.nodes.size(); i++){
//			//Retrieve Tree Node
//			TreeNode TN = T.getNodeByKey(i);
//
//			//re-initialize
//			if (TN.isRoot()){
//				HasParent = false;
//			} else {
//				HasParent = true;
//			}
//
//			TotalWeights = 0;
//			
//			//CN = Current Node, initially, this particular tree node TN
//			TreeNode CN = TN;
//			TreeNode PN;
//			
//			//Determine Height
//			while (HasParent){
//				PN = CN.parent();
//				if (PN != null){
//					TotalWeights = TotalWeights + CN.weight;
//					CN = PN;
//				} else {
//					HasParent = false;
//				}
//					
//			}
//			
//			//update total length
//			if (TotalWeights > LongestJourney){
//				LongestJourney = TotalWeights;
//			}
//			
//			//update max height
//			if (TN.height > MaxHeight){
//				MaxHeight = TN.height;
//			}
//		}
//		
//		for (int i = 0; i < T.nodes.size(); i++){
//			TreeNode TN = T.getNodeByKey(i);
//			
//			//re-initialize
//			if (TN.isRoot()){
//				HasParent = false;
//			} else {
//				HasParent = true;
//			}
//			
//			//CN = Current Node, initially, this particular tree node TN
//			TreeNode CN = TN;
//			TreeNode PN;
//			TotalWeights = 0;
//			
//			//Determine Height
//			while (HasParent){
//				PN = CN.parent();
//				if (PN != null){
//					TotalWeights = TotalWeights + CN.weight;
//					CN = PN;
//				} else {
//					HasParent = false;
//				}
//			}
//			
//			//update Alcada <height> value
//			TN.Alcada = 1 - (TotalWeights/LongestJourney);
//			
////			//display
////			System.out.println("Node " + i  + ": " + TN.label + ": (" + i + " @ " + TN.height +") " + "Alcada: " + TN.Alcada);
////			
//		}
//
//		// Create intermediate cluster nodes at every step, starting from the leaf nodes.
//					//<Key,Value> = <ID, cluster>
//		LinkedHashMap<Integer,Cluster> CreatedClusters = new LinkedHashMap<Integer,Cluster>();
//		HashSet<TreeNode> CurrentBatch = new HashSet<TreeNode>();
//		int NodesReceived = 0;
//		int RootKey = 0;
//
//		System.out.println("Before making the leaves.");		
//		
//		//recover all children
//		for (int i = 0; i <T.nodes.size(); i++){
//			TreeNode TN = T.getNodeByKey(i);
//			if (TN.isLeaf()){
//				
//				//update lists
//				CurrentBatch.add(TN);
//				NodesReceived++;
//				
//				//Initialize cluster info
//				Cluster c = new Cluster();
//				c.setAlcada(TN.Alcada);
//				c.setNom(TN.label);
//				CreatedClusters.put(i, c);
//				if (TN.isRoot()){
//					RootKey = i;
//				}
//			}
//		}
//		
//		System.out.println("Before the iterative cluster adding.");		
//		
//		//keep scanning, organizing, until not possible any more
//		while (NodesReceived < T.nodes.size()){
//			
//			//intialize parents
//			HashSet<TreeNode> Parents = new HashSet<TreeNode>();
//			
//			//add all parents
//			for (TreeNode TN : CurrentBatch){
//				Parents.add(TN.parent);
//			}
//			
//			//update counts
//			NodesReceived = NodesReceived + Parents.size();
//			
//			for (TreeNode TN : Parents){
//				
//				//Initialize cluster info for the parent
//				Cluster c = new Cluster();
//				c.setAlcada(TN.Alcada);
//				c.setNom(TN.label);
//				
//				try {
//					
//					//find all children for each parent
//					for (TreeNode CN : CurrentBatch){
//						if (CN.parent.getKey() == TN.getKey()){
//							System.out.println("parent: " + CN.parent + ", current:" + TN);
//							c.addCluster(CreatedClusters.get(CN.getKey()));
//						}
//					}
//					
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//				
//				//mark root key
//				if (TN.isRoot()){
//					RootKey = TN.getKey();
//				}
//				
//				//add to hash map
//				CreatedClusters.put(TN.getKey(), c);
//
//			}
//			
//			//update
//			CurrentBatch = Parents;
//		}
//
//		Cluster RootCluster = CreatedClusters.get(RootKey);
//		
//		System.out.println("Break!");
	}

	public LinkedList[] GenerateFiguresFromTree(){
		LinkedList[] Figures = null;
		

		
		return Figures;
	}
	
	public FrmPrincipalDesk getFr() {
		return fr;
	}

	public void setFr(FrmPrincipalDesk fr) {
		this.fr = fr;
	}

	public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
	}

}
