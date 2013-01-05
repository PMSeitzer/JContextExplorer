package moduls.frm.Panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class Jpan_TabbedMenu extends JTabbedPane{
	
	//Fields
	private Jpan_Menu jm;
	private Jpan_GraphMenu jgm;
	private Jpan_MotifOptions jmo;
	
	//Constructor
	public Jpan_TabbedMenu(Jpan_Menu jm, Jpan_GraphMenu jgm, Jpan_MotifOptions jmo){
		 //store data
		this.jm = jm;
		this.jgm = jgm;
		this.jmo = jmo;
		
		//add to panel
		this.getPanel();
	}
	
	//create panel
	public void getPanel(){
		//Menu tab
		JScrollPane MenuScroll = new JScrollPane(jm);
		
		//Motif options tab
		JPanel ContainerPane = new JPanel();
		ContainerPane.setLayout(new BorderLayout());
		ContainerPane.add(jmo, BorderLayout.NORTH);
		JScrollPane MotifScroll = new JScrollPane(ContainerPane);
		
		//add tabs to JOptionPanel
		this.addTab("Tree",null,MenuScroll);
		this.addTab("Graph",null,jgm);
		this.addTab("Motifs",null,MotifScroll);
	}

}
