package moduls.frm.Panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;

public class Jpan_DisplayOptions extends JPanel implements ActionListener{

	
	private static final long serialVersionUID = 1L;

	//fields
	//connect to parent
	private FrmPrincipalDesk f;
	
	//GUI components
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	private JLabel SelectAnalysesBanner;
	private JCheckBox DrawSearchResults;
	private JCheckBox DrawContextTree;
	private JCheckBox DrawContextGraph;
	private JCheckBox DrawPhylogeneticTree;
	private String strDrawSearchResults = "Print Search Results";
	private String strDrawContextTree = "Render Context Tree";
	private String strDrawContextGraph = "Generate Context Change Graph";
	private String strDrawPhylogeneticTree = "Display Results with Phylogeny";

	//constructor
	public Jpan_DisplayOptions(FrmPrincipalDesk f){
		this.f = f;
		
		//add to panel
		this.getPanel();
	}
	
	//create components
	public void getPanel(){
		
		//initialize panel
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Analysis Options")); // title
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.insets = new Insets(1,1,1,1);
		
		//Select Analyses Banner
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		SelectAnalysesBanner = new JLabel(" AVAILABLE ANALYSES");
		SelectAnalysesBanner.setBackground(Color.GRAY);
		SelectAnalysesBanner.setOpaque(true);
		SelectAnalysesBanner.setFont(fontStandard);
		add(SelectAnalysesBanner,c);
		gridy++;
		
		//Display Search Results
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		DrawSearchResults = new JCheckBox(strDrawSearchResults);
		DrawSearchResults.setSelected(false);
		DrawSearchResults.setFont(fontStandard);
		DrawSearchResults.addActionListener(this);
		add(DrawSearchResults,c);
		gridy++;
		
		//Render Context Tree
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		DrawContextTree = new JCheckBox(strDrawContextTree);
		DrawContextTree.setSelected(true);
		DrawContextTree.setFont(fontStandard);
		DrawContextTree.addActionListener(this);
		add(DrawContextTree,c);
		gridy++;
		
		//Create Changing Contexts Graph
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		DrawContextGraph = new JCheckBox(strDrawContextGraph);
		DrawContextGraph.setSelected(false);
		DrawContextGraph.setFont(fontStandard);
		DrawContextGraph.addActionListener(this);
		add(DrawContextGraph,c);
		gridy++;
		
		//Show on Phylogenetic tree
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		DrawPhylogeneticTree = new JCheckBox(strDrawPhylogeneticTree);
		DrawPhylogeneticTree.setSelected(false);
		DrawPhylogeneticTree.setFont(fontStandard);
		DrawPhylogeneticTree.addActionListener(this);
		add(DrawPhylogeneticTree,c);
		gridy++;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//GETTERS AND SETTERS
	
	public JCheckBox getDrawSearchResults() {
		return DrawSearchResults;
	}

	public JCheckBox getDrawContextTree() {
		return DrawContextTree;
	}

	public JCheckBox getDrawContextGraph() {
		return DrawContextGraph;
	}

	public JCheckBox getDrawPhylogeneticTree() {
		return DrawPhylogeneticTree;
	}
	
}
