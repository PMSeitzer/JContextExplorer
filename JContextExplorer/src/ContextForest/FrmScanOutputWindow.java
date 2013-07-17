package ContextForest;

import importExport.DadesExternes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import parser.Fig_Pizarra;

import definicions.Config;
import definicions.MatriuDistancies;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.Panels.Jpan_Menu;
import moduls.frm.Panels.Jpan_btn_NEW;
import moduls.frm.children.FrmPiz;

public class FrmScanOutputWindow extends JFrame {

	//Fields
	
	//Data
	private FrmPrincipalDesk f;
	private QuerySet QS;
	private String TCRKey;
	private FrmScanOutputWindow fsow;
	
	//GUI
	private Jpan_ScanResults pan_ScanResults;
	private Jpan_ViewResults pan_SelectDraw;
	private JScrollPane ForestPane;
	private boolean DrawContextForest;
	private DadesExternes de;
	
	//Constructor
	public FrmScanOutputWindow(FrmPrincipalDesk f, QuerySet QS, 
			String ComparisonName, boolean DrawContextForest, DadesExternes de){
		
		//Initializations
		this.setF(f);
		this.QS = QS;
		this.TCRKey = ComparisonName;
		this.setFsow(this);
		this.DrawContextForest = DrawContextForest;
		this.de = de;
		
		//Methods
		getFrame();
		getPanels();

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
		
		//Context Forest or Scan Results
		if (DrawContextForest){
			
			//create forest pane
			CreateContextForest();
			
			//Add forest pane to frame
			this.getContentPane().add(ForestPane, BorderLayout.CENTER);
			
		} else {
			
			//create new scan results panel
			pan_ScanResults = new Jpan_ScanResults(this, QS, TCRKey);
			
			//Add results only to frame
			this.getContentPane().add(pan_ScanResults, BorderLayout.CENTER);
			
		}

		//Initialize the selection pane
		pan_SelectDraw = new Jpan_ViewResults(this);
		
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

	//Draw the forest, from the data
	public void CreateContextForest(){
		try {
			
			Config cfg = f.getConfig();
			cfg.setMatriu(de.getMatriuDistancies());//matrix
			cfg.setHtNoms(de.getTaulaNoms()); //table names
			
			
			//create a new context tree panel
			FrmPiz fPiz = new FrmPiz(f, null);
			Jpan_Menu.ajustaValors(cfg);
			Fig_Pizarra figPizarra = new Fig_Pizarra(de.getMatriuDistancies().getArrel(), cfg);
			
			// Pass figures to the window
			fPiz.setFigures(figPizarra.getFigures());
			fPiz.setConfig(cfg);

			//vertical scroll value - currently matching Jpan_btn_New
			int VerticalScrollValue = 15*de.getTaulaNoms().size() + 250;
			
			Dimension d = new Dimension(this.getWidth()-
					Jpan_btn_NEW.HorizontalScrollBuffer, VerticalScrollValue);
			fPiz.setPreferredSize(d);
			
			//scroll pane
			ForestPane = new JScrollPane(fPiz);
			//ForestPane.setSize(this.getSize());
			//ForestPane.setPreferredSize(this.getSize());
			ForestPane.getVerticalScrollBar().setUnitIncrement(Jpan_btn_NEW.ScrollInc);
		
		} catch (Exception ex){
			ex.printStackTrace();
		}
		// Set sizes
		//fPiz.setPreferredSize(pizarra.getSize());
		
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


