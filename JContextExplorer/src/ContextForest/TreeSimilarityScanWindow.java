package ContextForest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JTable;

import moduls.frm.FrmPrincipalDesk;

public class TreeSimilarityScanWindow extends JFrame implements ActionListener {

	//Fields
	
	//Data
	private FrmPrincipalDesk f;
	private QuerySet QS;
	
	//GUI
	private JTable Tbl;
	
	//Constructor
	public TreeSimilarityScanWindow(FrmPrincipalDesk f, QuerySet QS){
		
		//Initializations
		this.f = f;
		this.QS = QS;
		
		//Methods
		getFrame();
		
		TextDisplay();
		
		//final step - show visibility
		this.setVisible(true);
	}
	
	
	// ===== GUI- related ======= //
	
	//Frame
	public void getFrame(){
		
		this.setSize(700,550);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Tree Similarity Scan Results");
		this.setResizable(true);
		this.setLocationRelativeTo(null);

	}
	
	//panel
	public void getPanel(){
		
		
		
	}
	
	public void TextDisplay(){
		
		for (String s : QS.getTreeComparisons().keySet()){
			
			System.out.println("Scan: " + s);
			LinkedList<TreeCompareReport> Reps = QS.getTreeComparisons().get(s);
			
			for (TreeCompareReport TCR : Reps){
				System.out.println("Query: " + TCR.getQueryName() + "\t" 
						+ TCR.getDissimilarity() + "\t" 
						+ TCR.isAdjusted() + "\t"
						+ TCR.getPreAdjustedDissimilarity() + "\t"
						+ TCR.getAdjustmentFactor() + "\t"
						);
			}
			
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}


