package moduls.frm.Panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class Jpan_TabbedMenu extends JTabbedPane{
	
	//Fields
	private Jpan_DisplayOptions jpo;
	private Jpan_Menu jm;
	private Jpan_GraphMenu jgm;
	private Jpan_MotifOptions jmo;
	private Jpan_PhyTreeMenu jpm;

	//Constructor
	public Jpan_TabbedMenu(Jpan_DisplayOptions jpo, Jpan_Menu jm, Jpan_GraphMenu jgm, Jpan_MotifOptions jmo, Jpan_PhyTreeMenu jpm){
		 //store data
		this.jpo = jpo;
		this.jm = jm;
		this.jpm = jpm;
		this.jgm = jgm;
		this.jmo = jmo;
		
		//add to panel
		this.getPanel();
	}
	
	//create panel
	public void getPanel(){
		//Menu tab
		//JScrollPane MenuScroll = new JScrollPane(jm);
		
		//options tab
		JPanel OptionContainerPane = new JPanel();
		OptionContainerPane.setLayout(new BorderLayout());
		OptionContainerPane.add(jpo, BorderLayout.NORTH);
		JScrollPane OptionScroll = new JScrollPane(OptionContainerPane);
		
		//Context tree menu tab
		JPanel MenuContainerPane = new JPanel();
		MenuContainerPane.setLayout(new BorderLayout());
		MenuContainerPane.add(jm, BorderLayout.NORTH);
		JScrollPane MenuScroll = new JScrollPane(MenuContainerPane);
		
		//Graph tab
		JPanel GraphContainerPane = new JPanel();
		GraphContainerPane.setLayout(new BorderLayout());
		GraphContainerPane.add(jgm, BorderLayout.NORTH);
		JScrollPane GraphScroll = new JScrollPane(GraphContainerPane);		
		
		//Phylogenetic tree tab
		JPanel PhyloContainerPane = new JPanel();
		PhyloContainerPane.setLayout(new BorderLayout());
		PhyloContainerPane.add(jpm, BorderLayout.NORTH);
		JScrollPane PhyloScroll = new JScrollPane(PhyloContainerPane);
		
		//Motif options tab
		JPanel ContainerPane = new JPanel();
		ContainerPane.setLayout(new BorderLayout());
		ContainerPane.add(jmo, BorderLayout.NORTH);
		JScrollPane MotifScroll = new JScrollPane(ContainerPane);
		
		//add tabs to JOptionPanel
		this.addTab("Options",null,OptionScroll);
		this.addTab("Tree",null,MenuScroll);
		this.addTab("Graph",null,GraphScroll);
		this.addTab("Phylogeny",null,PhyloScroll);
		this.addTab("Motifs",null,MotifScroll);
	}

	
	//setters and getters
	public Jpan_DisplayOptions getJpo() {
		return jpo;
	}

}
