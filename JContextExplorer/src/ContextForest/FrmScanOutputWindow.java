package ContextForest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import moduls.frm.FrmPrincipalDesk;

public class FrmScanOutputWindow extends JFrame implements ActionListener {

	//Fields
	
	//Data
	private FrmPrincipalDesk f;
	private QuerySet QS;
	private String TCRKey;
	private FrmScanOutputWindow fsow;
	
	//GUI
	private JTabbedPane pan_Tabbed;
	private Jpan_ScanResults pan_ScanResults;
	private Jpan_ViewResults pan_SelectDraw;
	private boolean DrawContextTree;
	
	//Constructor
	public FrmScanOutputWindow(FrmPrincipalDesk f, QuerySet QS, 
			String ComparisonName, boolean DrawContextTree){
		
		//Initializations
		this.setF(f);
		this.QS = QS;
		this.TCRKey = ComparisonName;
		this.setFsow(this);
		this.DrawContextTree = DrawContextTree;
		
		//Methods
		getPanels();
		getFrame();
		
		//TextDisplay();
		
		//final step - show visibility
		this.setVisible(true);
	}
	
	
	// ===== GUI- related ======= //
	
	//Frame
	public void getFrame(){
		
		this.setSize(800,550);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Query Set Processing Results");
		this.setResizable(true);
		this.setLocationRelativeTo(null);

	}
	
	//panel
	public void getPanels(){
		
		// GET PANELS
		
		//create new scan results panel
		pan_ScanResults = new Jpan_ScanResults(this, QS, TCRKey);
		
		//Initialize the selection pane
		pan_SelectDraw = new Jpan_ViewResults(this);
		
		if (DrawContextTree){
			//TODO: Draw context tree
		}
		
		// COORDINATE PANELS, ADD TO FRAME
		if (DrawContextTree){
			
			//create tabbed pane
			pan_Tabbed = new JTabbedPane();
			pan_Tabbed.addTab("Scan Results",null,pan_ScanResults);
			pan_Tabbed.addTab("Context Forext",null,new JPanel());
			
			//Add tabbed pane to frame
			this.getContentPane().add(pan_Tabbed, BorderLayout.CENTER);
			
		} else {
			
			//Add results only to frame
			this.getContentPane().add(pan_ScanResults, BorderLayout.CENTER);
			
		}

		//Either way, add the select draw pane
		this.getContentPane().add(pan_SelectDraw, BorderLayout.SOUTH);
		
	}
	
	//For development - textual display of table
	public void TextDisplay(){
		
		for (String s : QS.getTreeComparisons().keySet()){
			
			System.out.println("Scan: " + s);
			LinkedList<ScanReport> Reps = QS.getTreeComparisons().get(s);
			System.out.println("Query\tDissimilarity\tIdentical Sets" +
					"\tAdjustment Factor\tUnadjusted Dissimilarity\tTotal Leaves");
			for (ScanReport TCR : Reps){
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

	// --- GETTERS AND SETTERS ---- //

	public FrmPrincipalDesk getF() {
		return f;
	}


	public void setF(FrmPrincipalDesk f) {
		this.f = f;
	}


	public FrmScanOutputWindow getFsow() {
		return fsow;
	}


	public void setFsow(FrmScanOutputWindow fsow) {
		this.fsow = fsow;
	}


	public Jpan_ScanResults getPan_ScanResults() {
		return pan_ScanResults;
	}


	public void setPan_ScanResults(Jpan_ScanResults pan_ScanResults) {
		this.pan_ScanResults = pan_ScanResults;
	}


	public QuerySet getQS() {
		return QS;
	}


	public void setQS(QuerySet qS) {
		QS = qS;
	}


	public String getTCRKey() {
		return TCRKey;
	}


	public void setTCRKey(String tCRKey) {
		TCRKey = tCRKey;
	}
	
}


