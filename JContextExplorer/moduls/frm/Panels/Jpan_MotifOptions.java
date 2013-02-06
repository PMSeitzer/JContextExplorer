package moduls.frm.Panels;

import genomeObjects.OrganismSet;

import inicial.Language;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.children.ManageMotifs;

public class Jpan_MotifOptions extends JPanel implements ActionListener{

	//fields
	private FrmPrincipalDesk f;
	private OrganismSet OS;
	private LinkedList<String> LoadedMotifs = new LinkedList<String>();
	
	//GUI components
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	private JLabel IncludeMotifsBanner;
	private JCheckBox IncludeMotifsComp;
	private JCheckBox IncludeMotifsDisp;
	private JLabel LoadMotifsBanner;
	private JButton btnLoadMotifs;
	private JComboBox<String> menuLoadedMotifs;
	private JButton btnGetInfo;
	
	//constructor
	public Jpan_MotifOptions(FrmPrincipalDesk f){
		this.f = f;
		this.OS = f.getOS();
		
		//build components
		this.getPanel();
	}
	
	//create components
	public void getPanel(){
		
		//initialize panel
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Sequence Motif Management")); // title
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.insets = new Insets(1,1,1,1);
		
		//Check boxes banner
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		IncludeMotifsBanner = new JLabel(" MOTIF INCLUSION OPTIONS");
		IncludeMotifsBanner.setBackground(Color.GRAY);
		IncludeMotifsBanner.setOpaque(true);
		IncludeMotifsBanner.setFont(fontStandard);
		add(IncludeMotifsBanner,c);
		gridy++;
		
		//Include Motifs in computation check box
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		IncludeMotifsComp = new JCheckBox("Include Motifs in Computations");
		IncludeMotifsComp.setSelected(false);
		IncludeMotifsComp.setFont(fontStandard);
		IncludeMotifsComp.addActionListener(this);
		add(IncludeMotifsComp,c);
		gridy++;
		
		//Include Motifs for display check box
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		IncludeMotifsDisp = new JCheckBox("Include Motifs in Context Display");
		IncludeMotifsDisp.setSelected(false);
		IncludeMotifsDisp.setFont(fontStandard);
		IncludeMotifsDisp.addActionListener(this);
		add(IncludeMotifsDisp,c);
		gridy++;
		
		c.insets = new Insets(2,1,2,1);
		
		//Load Motifs banner
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		LoadMotifsBanner = new JLabel(" ADD/REMOVE SEQUENCE MOTIFS ");
		LoadMotifsBanner.setBackground(Color.GRAY);
		LoadMotifsBanner.setOpaque(true);
		LoadMotifsBanner.setFont(fontStandard);
		add(LoadMotifsBanner,c);
		gridy++;
		
		//Load Motifs button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 4;
		btnLoadMotifs = new JButton("Manage Motifs");
		btnLoadMotifs.setFont(fontStandard);
		btnLoadMotifs.addActionListener(this);
		add(btnLoadMotifs,c);
		gridy++;
		
		//Currently Loaded banner
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		LoadMotifsBanner = new JLabel(" CURRENTLY ENABLED MOTIFS");
		LoadMotifsBanner.setBackground(Color.GRAY);
		LoadMotifsBanner.setOpaque(true);
		LoadMotifsBanner.setFont(fontStandard);
		add(LoadMotifsBanner,c);
		gridy++;
		
		// currently loaded motifs drop-down menu
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		menuLoadedMotifs = new JComboBox<String>(getLoadedMotifs());
		menuLoadedMotifs.addActionListener(this);
		menuLoadedMotifs.setEnabled(true);
		menuLoadedMotifs.setFont(fontStandard);
		add(menuLoadedMotifs, c);
		gridy++;
		
		//Load Motifs button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 4;
		btnGetInfo = new JButton("View Profile");
		btnGetInfo.setFont(fontStandard);
		btnGetInfo.addActionListener(this);
		add(btnGetInfo,c);
		gridy++;

	}

	//convert motifs from linked list to integer array
	public String[] getLoadedMotifs(){
		String Motifs[];
		if (LoadedMotifs.size() > 0){
			Motifs = new String[LoadedMotifs.size()];
			for (int i = 0; i < LoadedMotifs.size(); i++){
				Motifs[i] = LoadedMotifs.get(i);
			}
		} else {
			Motifs =  new String[1];
			Motifs[0] = "<none>";
		}
		
		return Motifs;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//update check boxes for motif search/display options.
		if (e.getSource().equals(IncludeMotifsComp)){
			f.setIncludeMotifs(IncludeMotifsComp.isSelected());
		}
		
		if (e.getSource().equals(IncludeMotifsDisp)){
			f.setDisplayMotifs(IncludeMotifsDisp.isSelected());
		}
		
		//launch add/remove motifs window
		if (e.getSource().equals(btnLoadMotifs)){
			new ManageMotifs(f);
		}
		
		//launch motif viewer window
		if (e.getSource().equals(btnGetInfo)){
			
		}
	}

	public JComboBox<String> getMenuOfMotifs() {
		return this.menuLoadedMotifs;
	}
	
	public String[] getMenuLoadedMotifs() {
		String[] NamesOfMotifs = new String[menuLoadedMotifs.getItemCount()];
		for (int i = 0; i < menuLoadedMotifs.getItemCount(); i++){
			NamesOfMotifs[i] = menuLoadedMotifs.getItemAt(i);
		}
		return NamesOfMotifs;
	}

	public void setMenuLoadedMotifs(JComboBox<String> menuLoadedMotifs) {
		this.menuLoadedMotifs = menuLoadedMotifs;
	}

	public JCheckBox getIncludeMotifsDisp() {
		return IncludeMotifsDisp;
	}

	public void setIncludeMotifsDisp(JCheckBox includeMotifsDisp) {
		IncludeMotifsDisp = includeMotifsDisp;
	}

	public void setLoadedMotifs(LinkedList<String> loadedMotifs) {
		LoadedMotifs = loadedMotifs;
	}
}
