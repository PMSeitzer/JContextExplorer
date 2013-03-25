package contextViewer;

import genomeObjects.CSDisplayData;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import moduls.frm.FrmPrincipalDesk;

public class AnnotationFrame extends JFrame implements ActionListener, ComponentListener{


	//serial ID
	private static final long serialVersionUID = 1L;
	
	//parent frame
	private FrmPrincipalDesk f;
	
	//fields
	private JPanel jp1 = new JPanel();		//text frame
	private JPanel jp2 = new JPanel();		//select nodes +search field button
	private Dimension TheDimension = new Dimension(500,200);
	private JScrollPane scrpan;
	private JTextPane jtp;
	private String Title;
	
	//components
	private JTextField searchableField;
	private JButton btnSelectNodes;
	private String strSelectNodes = "Select Nodes";
	
	//constructor
	public AnnotationFrame(JTextPane JTP, String Title, FrmPrincipalDesk fr){
		
		//note imported information
		this.jtp = JTP;
		this.Title = Title;
		this.f = fr;
		this.addComponentListener(this);
		
		//initialization information
		this.setSize(450,270);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.setTitle(Title);
		this.setLocationRelativeTo(null);;
	    this.setResizable(true);
	    
		//add components
		getPanels();
	}
	
	//create panel components
	private void getPanels() {
	
		//initialize panel
		jp1.setLayout(new GridBagLayout());
		//this.setBorder(BorderFactory.createTitledBorder(""));
		GridBagConstraints c = new GridBagConstraints();
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		c.insets = new Insets(1,1,1,1);
		
		//add precomputed text field to jpanel 1
		c.gridy = 0;
	    jp1.add(jtp, c);
		
	    scrpan = new JScrollPane(jp1);
	    scrpan.setPreferredSize(TheDimension);
	    
	    //jpanel2
	    //add textfield to jpanel2
	    jp2.setLayout(new GridBagLayout());
	    c.ipadx = 300;
	    c.ipady = 7;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridwidth = 2;
	    c.gridheight = 1;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.anchor = GridBagConstraints.FIRST_LINE_START;    
	    searchableField = new JTextField("");
	    searchableField.setEditable(true);
	    jp2.add(searchableField, c);
	    
	    //add select button
	    c.gridx = 2;
	    c.ipadx = 0;
	    c.gridy = 0;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    c.ipady = 0;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    btnSelectNodes = new JButton(strSelectNodes);
	    btnSelectNodes.addActionListener(this);
	    jp2.add(btnSelectNodes,c);

		//add panels
	    this.add(scrpan, BorderLayout.NORTH);
	    this.add(jp2, BorderLayout.SOUTH);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnSelectNodes) || e.getSource().equals(searchableField)){
			if (searchableField.getText().equals("")){
				String MessageString;
				MessageString = "Please enter either one or more genera or species, separated by commas." + "\n" 
								+ "This will select all appropriate nodes in the tree.";
				JOptionPane.showMessageDialog(null, MessageString);
			} else {
				
				//retrieve currently selected nodes
				boolean[] currentlySelected = this.f.getCurrentFrame().getInternalPanel().getSelectedNodeNumbers();
				
				//create a new array, initialize to false
				boolean[] UpdatedNodes = new boolean[currentlySelected.length];
				Arrays.fill(UpdatedNodes,false);
				
				//recover query
				String Query = searchableField.getText();
				
				//parse into candidates
				String[] Queries = Query.split(",");
				
				//search for node names + annotations
				CSDisplayData CompareCSD = f.getCurrentFrame().getInternalPanel().getCSD();
				for (int i = 0; i < CompareCSD.getNodeNames().length; i++){
					
					//check node names against all queries
					for (int j =0; j < Queries.length; j++){
						//TestQuery = Queries[j].toUpperCase().replaceAll("\\s","");
						//if (CompareCSD.getNodeNames()[i].toUpperCase().contains(Queries[j].toUpperCase())){
						if (CompareCSD.getNodeNames()[i].toUpperCase().contains(Queries[j].toUpperCase().replaceAll("\\s",""))){
							UpdatedNodes[i] = true;
						} 
					}

				}
				
 				this.f.getCurrentFrame().getInternalPanel().setSelectedNodeNumbers(UpdatedNodes);
 				this.f.getCurrentFrame().getInternalPanel().repaint();
			}
				
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		this.scrpan.setPreferredSize(new Dimension((int)this.getSize().getWidth(),
				(int)(this.getSize().getHeight() - 70)));
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
