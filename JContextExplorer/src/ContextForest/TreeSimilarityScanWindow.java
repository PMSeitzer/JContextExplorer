package ContextForest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import moduls.frm.FrmPrincipalDesk;

public class TreeSimilarityScanWindow extends JFrame implements ActionListener {

	//Fields
	
	//Data
	private FrmPrincipalDesk f;
	private QuerySet QS;
	
	//GUI
	private ScanResultsPanel jp;
	
	//Constructor
	public TreeSimilarityScanWindow(FrmPrincipalDesk f, QuerySet QS){
		
		//Initializations
		this.f = f;
		this.QS = QS;
		
		//Methods
		getFrame();
		getPanel();
		
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
		
		//create new scan results panel
		jp = new ScanResultsPanel(QS);
		
		
		//Add panel to frame
		this.add(jp);
		
	}
	
	public void TextDisplay(){
		
		for (String s : QS.getTreeComparisons().keySet()){
			
			System.out.println("Scan: " + s);
			LinkedList<TreeCompareReport> Reps = QS.getTreeComparisons().get(s);
			System.out.println("Query\tDissimilarity\tIdentical Sets" +
					"\tAdjustment Factor\tUnadjusted Dissimilarity\tTotal Leaves");
			for (TreeCompareReport TCR : Reps){
				System.out.println(TCR.getQueryName() + "\t" 
						+ TCR.getDissimilarity() + "\t" 
						+ TCR.isIdenticalDataSet() + "\t"
						+ TCR.getAdjustmentFactor() + "\t"
						+ TCR.getPreAdjustedDissimilarity() + "\t"
						+ TCR.getTotalLeaves() + "\t"
						);
			}
			
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}


