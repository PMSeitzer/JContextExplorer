package moduls.frm.children;

import java.util.LinkedList;

import genomeObjects.CSDisplayData;

import javax.swing.JPanel;

import parser.Fig_Pizarra;

import newickTreeParsing.Tree;

import moduls.frm.FrmPrincipalDesk;

public class FrmPhylo extends FrmPiz{

	//fields
	private FrmPrincipalDesk fr;
	private CSDisplayData CSD;
	private Tree PhyloTree;
	
	//constructor
	public FrmPhylo(FrmPrincipalDesk f, CSDisplayData CSD){
		super(f, CSD);
		this.fr = f;
		this.CSD = CSD;
		
		//
	}

	public LinkedList[] GenerateFiguresFromTree(){
		LinkedList[] Figures = null;
		
		//Retrieve tree from main menu
		PhyloTree = fr.getPanPhyTreeMenu().getCurrentParsedTree();
		
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
