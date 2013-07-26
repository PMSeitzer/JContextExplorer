package GenomicSetHandling;

import genomeObjects.OrganismSet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;

import moduls.frm.FrmPrincipalDesk;

public class CurrentGenomeSet extends JFrame implements ActionListener, ComponentListener{

	//fields
	//data/base
	private FrmPrincipalDesk f;
	private RemoveGenomes RG = null;
	
	//GUI
	private JPanel jp, jp2, jpEnclosing;
	private JTextField LblName, TxtName, LblNotes, LblGenomes;
	private JTextArea OrganismSetNotes, LblInfo;
	private JComboBox<String> menuGenomes;
	private JScrollPane ptsscroll, ptsscroll2;
	private JButton btnOK, btnRemoveGenomes;
	private int ScrollPaneInset = 15;
	private int HeightInset = 160;
	
	private String strGenomes = "Genomes";
	private String strSelectGenome = "Select Genome";
	
	//Constructor
	public CurrentGenomeSet(FrmPrincipalDesk f){
		//
		this.f = f;
		this.getFrame();
		this.getPanel();
		this.getData();
		this.pack();
		this.setMinimumSize(this.getSize());
		
		WindowListener closeSubFrames = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				
				//dispose all sub-windows
				if (RG != null){
					RG.dispose();
				}
				e.getWindow().dispose();
			}
		};
		this.addWindowListener(closeSubFrames);
		
		//this.setModalityType(ModalityType.DOCUMENT_MODAL);
		this.addComponentListener(this);
		this.setVisible(true);

	}
	
	//frame
	public void getFrame(){
		this.setSize(400,400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Current Genome Set");
		this.setResizable(true);
	}
	
	//panel
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		c.weightx = 1;
		c.insets = new Insets(2,2,2,2);
		
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblName = new JTextField("Name:");
		LblName.setEditable(false);
		jp.add(LblName, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		TxtName = new JTextField("");
		TxtName.setEditable(false);
		TxtName.setColumns(30);
		jp.add(TxtName, c);
		
		gridy++;
		
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblNotes = new JTextField("Notes:");
		LblNotes.setEditable(false);
		jp.add(LblNotes, c);
		
		//Enter notes here
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		OrganismSetNotes = new JTextArea("");
		OrganismSetNotes.setEditable(true);
		ptsscroll = new JScrollPane(OrganismSetNotes);
		ptsscroll.setPreferredSize(new Dimension(TxtName.getColumns()*12+8, 50));
		jp.add(ptsscroll, c);
		gridy++;
		
		//Genomes 
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		LblGenomes = new JTextField(strGenomes);
		LblGenomes.setEditable(false);
		jp.add(LblGenomes, c);
		
		//Pull-down menu
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		String[] Empty = {"<none>"};
		menuGenomes = new JComboBox<String>(Empty);
		jp.add(menuGenomes, c);
		gridy++;
		
		//information bar
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.ipady = 7;
		LblInfo = new JTextArea("");
		LblInfo.setEditable(false);
		ptsscroll2 = new JScrollPane(LblInfo);
		ptsscroll2.setPreferredSize(new Dimension(50, 100));
		jp.add(ptsscroll2, c);
		gridy++;
		
		//remove genomes button
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.ipady = 0;
		btnRemoveGenomes = new JButton("Remove Genomes");
		btnRemoveGenomes.addActionListener(this);
		jp.add(btnRemoveGenomes, c);
		
		//central frame
		jp2 = new JPanel();
		jp2.setLayout(new GridBagLayout());
		GridBagConstraints d = new GridBagConstraints();
		gridy = 0;

		//OK button
		d.gridheight = 1;
		d.gridx = 0;
		d.gridy = gridy;
		d.fill = GridBagConstraints.NONE;
		d.anchor = GridBagConstraints.CENTER;
		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		jp2.add(btnOK, d);
		
		//add to frame
		jpEnclosing = new JPanel();
		jpEnclosing.setLayout(new BorderLayout());
		jpEnclosing.add(jp, BorderLayout.NORTH);
		jpEnclosing.add(jp2, BorderLayout.SOUTH);
		this.add(jpEnclosing);
		
	}

	//Data for components
	public void getData(){
		TxtName.setText(f.getOS().getName());
		OrganismSetNotes.setText(f.getOS().getNotes());
		
		//Update genomes
		String GenomeswNum = strGenomes + " (" + String.valueOf(f.getOS().getSpeciesNames().size()) + "):";
		LblGenomes.setText(GenomeswNum);
		if (f.getOS().getSpeciesNames().size() > 0){
			menuGenomes.removeItemAt(0);
			menuGenomes.addItem(strSelectGenome);
			for (String s : f.getOS().getSpeciesNames()){
				menuGenomes.addItem(s);
			}
			menuGenomes.addActionListener(this);
		}

	}
	
	//Actions!
	@Override
	public void actionPerformed(ActionEvent e) {

		//Show organism data
		if (e.getSource().equals(menuGenomes)){
			
			
			//this try/catch block is associated with the remove-genomes panel.
			try {
				
				for (String s : f.getOS().getSpeciesNames()){
					
					//find appropriate species
					if (menuGenomes.getSelectedItem().equals(s)){
						showGenomeInfo(s);
					}

				}
				
				if (menuGenomes.getSelectedItem().equals(strSelectGenome)){
					LblInfo.setText("");
				}
				
			} catch (Exception ex) {}
			
		}
		
		//Remove one or more genomes from the set
		if (e.getSource().equals(btnRemoveGenomes)){
			if (RG == null){
				RG = new RemoveGenomes(this);	
			}
		}
		
		//View Statistics, Update
		if (e.getSource().equals(btnOK)){

			//Update fields
			f.getOS().setNotes(OrganismSetNotes.getText());
			
			//Update GI information
			f.getGenomeSets().get(f.getOS().getName()).setGSNotes(OrganismSetNotes.getText());
			
			//close subordinate + this frame
			if (RG != null){
				RG.dispose();
			}
			this.dispose();
			
			//TODO: update data across all fields
			
		}
			

			

		
	}
	
	//display per-genome information
	public void showGenomeInfo(String GenomeName){
		
		LblInfo.setText(f.getOS().getGenomeDescriptions().get(GenomeName));
		
		//LblInfo.setText(GenomeName);
		//LblInfo.setText(GenomeName + "\n" + f.getOS().getNotes());
	}

	@Override
	//adjust frame for readability
	public void componentResized(ComponentEvent e) {
		ptsscroll.setPreferredSize(new Dimension((int) menuGenomes.getSize().getWidth()-5,
				50));

		ptsscroll2.setPreferredSize(new Dimension((int) this.getWidth()-(ScrollPaneInset*2),
				(int)(this.getSize().getHeight()) - HeightInset - btnOK.getHeight() 
				- this.btnRemoveGenomes.getHeight()));

		this.repaint();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public JComboBox<String> getMenuGenomes() {
		return menuGenomes;
	}

	public void setMenuGenomes(JComboBox<String> menuGenomes) {
		this.menuGenomes = menuGenomes;
	}

	public String getStrSelectGenome() {
		return strSelectGenome;
	}

	public void setStrSelectGenome(String strSelectGenome) {
		this.strSelectGenome = strSelectGenome;
	}

	public JTextField getLblGenomes() {
		return LblGenomes;
	}

	public void setLblGenomes(JTextField lblGenomes) {
		LblGenomes = lblGenomes;
	}

	public String getStrGenomes() {
		return strGenomes;
	}

	public void setStrGenomes(String strGenomes) {
		this.strGenomes = strGenomes;
	}

	public RemoveGenomes getRG() {
		return RG;
	}

	public void setRG(RemoveGenomes rG) {
		RG = rG;
	}
}
