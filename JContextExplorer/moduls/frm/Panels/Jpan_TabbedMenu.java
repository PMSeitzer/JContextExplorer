package moduls.frm.Panels;

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
		JScrollPane MenuScroll = new JScrollPane(jm);
		this.addTab("Tree",null,MenuScroll);
		this.addTab("Graph",null,jgm);
		this.addTab("Motifs",null,jmo);
	}

}
