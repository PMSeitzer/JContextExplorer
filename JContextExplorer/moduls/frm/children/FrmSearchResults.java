package moduls.frm.children;

import genomeObjects.CSDisplayData;

import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;

public class FrmSearchResults extends JPanel{

	//Fields
	private FrmPrincipalDesk fr;
	private CSDisplayData CSD;
	
	//constructor
	public FrmSearchResults(final FrmPrincipalDesk f, CSDisplayData CSD){
		this.fr = f;
		this.CSD = CSD;
	}
}
