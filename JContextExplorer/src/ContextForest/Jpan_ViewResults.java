package ContextForest;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import moduls.frm.PostSearchAnalyses;
import moduls.frm.QueryData;

public class Jpan_ViewResults extends JPanel implements ActionListener{

	//Data
	private FrmScanOutputWindow fsow;
	//private LinkedList<QueryData> CurrentlySelectedQueries;
	
	//GUI components
	private JButton btnSelectQR, btnDrawCT;
	private JTextField selectQueryResults;
	private String strDrawCT = "Draw Context Trees";
	private String strSelectQR = "Select Query Results";
	
	//Fonts
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	private Font fontSearch = new Font("Dialog", Font.PLAIN, 14);
	
	
	//Constructor
	public Jpan_ViewResults(FrmScanOutputWindow fsow){
		
		//Data
		this.fsow = fsow;
		
		//Create panel
		getPanel();
		
	}
	
	//create panel
	public void getPanel(){
		
		//initialize panel
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Select Query Results"));
		final GridBagConstraints c = new GridBagConstraints();
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
		//search for nodes bar
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.ipady = 7;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		selectQueryResults = new JTextField("");
		selectQueryResults.setFont(fontSearch);
		selectQueryResults.setPreferredSize(new Dimension(400, 26));
		selectQueryResults.addActionListener(this);
		add(selectQueryResults, c);
		
		//search for nodes button
		c.ipady = 5;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		btnSelectQR = new JButton(strSelectQR);
		btnSelectQR.setFont(fontStandard);
		btnSelectQR.addActionListener(this);
		add(btnSelectQR, c);
		
		//Select All
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		btnDrawCT = new JButton(strDrawCT);
		btnDrawCT.addActionListener(this);
		btnDrawCT.setFont(fontStandard);
		add(btnDrawCT, c);
		
	}

	// ======== Actions ======== //
	
	//main actions
	@Override
	public void actionPerformed(ActionEvent e) {

		//Select Query Results
		if (e.getSource().equals(btnSelectQR) || e.getSource().equals(selectQueryResults)){
			System.out.println("Select Results!");
		}
		
		//Draw Context Trees
		if (e.getSource().equals(btnDrawCT)){
			System.out.println("Draw Context Trees!");
		}
	}
	
	//retrieve selected query sets
	public void RetriveSelectedQueryResults(){
		
	}
	
	//draw a bunch of queries
	public void DrawContextTrees(LinkedList<QueryData> L){
		for (QueryData QD : L){
			
			//Adjust search analyses
			PostSearchAnalyses P = new PostSearchAnalyses(
					fsow.getF().getPanMenuTab().getJpo().getDrawSearchResults().isSelected(), //search results
					true, //draw context tree
					fsow.getF().getPanMenuTab().getJpo().getDrawContextGraph().isSelected(), //draw context graph
					fsow.getF().getPanMenuTab().getJpo().getDrawPhylogeneticTree().isSelected() //phylogeny
					);
			
			//update query list
			QD.setAnalysesList(P);
			
			//draw trees
			fsow.getF().getPanBtn().showCalls("Load", QD);
		}
	}
}
