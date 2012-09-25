package moduls.frm.children;

import java.util.LinkedList;
import java.util.List;

import genomeObjects.CSDisplayData;
import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ViewAnnotations extends JPanel{

	//serial ID
	private static final long serialVersionUID = 1L;

	//relevant data
	CSDisplayData CSD;
	
	//display fields
    JTextArea textArea;
    static final String NEWLINE = System.getProperty("line.separator");
    JButton OKButton;
    
    //constructor
    public ViewAnnotations(CSDisplayData csd){
    	this.CSD = csd;
    	retrieveAnnotations();
    	createAndshowGUI();
    }

	private void createAndshowGUI() {
		// TODO Auto-generated method stub
		
	}

	private void retrieveAnnotations() {
		for (int i = 0; i<CSD.getSelectedNodes().length; i++){
			if (CSD.getSelectedNodes()[i] == true){
				
				//recover all relevant annotations
				List<GenomicElementAndQueryMatch> LL = CSD.getEC().getContexts().get(CSD.getNodeNames()[i]);
				
//				textArea.append(CSD.getNodeNames()[i] + ": "
//						+ );
			}
		}
	}
    
    
}
