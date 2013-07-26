package GenomicSetHandling;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class RemoveGenomes extends JFrame implements ActionListener{

	//Fields
	
	//Data
	private CurrentGenomeSet CGS;
	
	//GUI
	private JPanel jp, jpN, jpS, jpE;
	private JScrollPane jscrp;
	private DefaultListModel<String> GenomeNames;
	private JList<String> NameListGUI;
	private String strSelect = "Select Genomes to Remove";
										//width height
	private Dimension d1 = new Dimension(400,250);
	private Dimension d2 = new Dimension(420,330);
	
	//panel information
	private JButton btnRemove, btnOK;
	
	//Constructor
	public RemoveGenomes(final CurrentGenomeSet CGS){
		
		//data adjustment
		this.CGS = CGS;
		
		WindowListener setRGtonull = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				
				CGS.setRG(null);
				e.getWindow().dispose();
			}
		};
		
		this.addWindowListener(setRGtonull);
		
		//initialize genome names
		this.BuildList();
		
		//panels + frames
		this.getPanel();
		this.getFrame();
		
		//set visible!
		this.setVisible(true);
	}
	
	//build list
	public void BuildList(){
		
		//Initialize
		GenomeNames = new DefaultListModel<String>();
		
		//Iterate through list, add all items.
		for (int i = 1; i < CGS.getMenuGenomes().getItemCount(); i++){
			String s = CGS.getMenuGenomes().getItemAt(i);
			GenomeNames.addElement(s);
		}
		
		//GUI
		NameListGUI = new JList<String>(GenomeNames);
		NameListGUI.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
	}
	
	//panels
	public void getPanel(){
		
		//North panel
        jpN = new JPanel(new BorderLayout());
        jpN.setBorder(BorderFactory.createTitledBorder(strSelect));
		
		//Initialize panel
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());

		//add list to panel
		jp.add(NameListGUI);
		
		//add list to panel, with scroll panel
		jscrp = new JScrollPane(jp);
		jscrp.setPreferredSize(d1);
		jpN.add(jscrp);
		
		//South panel
		jpS = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		btnRemove = new JButton("Remove Selected");
		btnRemove.addActionListener(this);
		jpS.add(btnRemove, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		jpS.add(btnOK, c);
		
		//assemble panels
		jpE = new JPanel();
		jpE.setLayout(new BorderLayout());
		jpE.add(jpN, BorderLayout.NORTH);
		jpE.add(jpS, BorderLayout.SOUTH);
		this.add(jpE);
	}
	
	//frame
	public void getFrame(){
		this.setTitle("Remove Genomes");
		this.setSize(d2);
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//remove genomes button
		if (e.getSource().equals(btnRemove)){
			
			//retrieve selected
			List<String> Selected = NameListGUI.getSelectedValuesList();
			
			//schedule for removal
			for (String s : Selected){
				CGS.getGenomes2Remove().add(s);
			}
			
			//Initialize the counter
			int GenomeCounter = 0;
			
			//create a list of the unselected genomes
			LinkedList<String> Unselected = new LinkedList<String>();
			for (int i = 0; i < GenomeNames.getSize(); i++){
				String s = GenomeNames.get(i);
				if (!Selected.contains(s)){
					Unselected.add(s);
					GenomeCounter++;
				}
			}
			
			//reset names
			GenomeNames.removeAllElements();
			
			//remove all, then re-add unselected to list
			CGS.getMenuGenomes().removeAllItems();
			CGS.getMenuGenomes().addItem(CGS.getStrSelectGenome());
			for (String s : Unselected){
				CGS.getMenuGenomes().addItem(s);
				GenomeNames.addElement(s);
			}
			
			//reset quantity
			String NumGenomes = CGS.getStrGenomes() + " (" + String.valueOf(GenomeCounter) + ")";
			CGS.getLblGenomes().setText(NumGenomes);

		}
		
		//okay button
		if (e.getSource().equals(btnOK)){
			CGS.setRG(null);
			this.dispose();
		}
	}
	
}
