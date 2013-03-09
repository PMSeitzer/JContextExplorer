/*
 * Copyright (C) Justo Montiel, David Torres, Sergio Gomez, Alberto Fernandez
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see
 * <http://www.gnu.org/licenses/>
 */

package inicial;


import genomeObjects.OrganismSet;
import inicial.FesLog.TipLog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import moduls.frm.FrmPrincipalDesk;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Main of MultiDendrograms application
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class Dendrograma {

	//Current organism set
	private OrganismSet OS;
	private JMenuBar MB;
	private FrmPrincipalDesk f;
	
	public Dendrograma(OrganismSet theOrganismSet) {

		//carry along OS, create Frm
		this.OS = theOrganismSet;
		this.f = new FrmPrincipalDesk("JContextExplorer (1.1): Main Window", theOrganismSet);
		
		//Frm specifications
		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension d = new Dimension();
		double Scale = 0.75;
		//double Scale = 1;
		d.setSize(pantalla.getWidth()*Scale,pantalla.getHeight()*Scale);
		f.setSize(d);
		f.setLocationRelativeTo(null);
		f.setResizable(true);
		
		//add menu bar
		CreateAndAddMenuBar();
		
		//set visible
		f.setVisible(true);

	}

	//create menu bar method
	public void CreateAndAddMenuBar(){
		this.MB = new JMenuBar();
		
		//load stuff menu
		JMenu LoadMenu = new JMenu("Load");

		//Genomic Working Set sub-menu
		JMenu GenomicWorkingSetMenu = new JMenu("Genomic Working Set");
		
		JMenuItem GFF = new JMenuItem("From a set of .GFF files");
		JMenuItem Genbank = new JMenuItem("From a set of genbank files");
		JMenuItem AccessionID = new JMenuItem ("From a set of genbank IDs");
		JMenuItem Preexisting = new JMenuItem("Retrieve pre-existing GWS");
		JMenuItem Ncbi = new JMenuItem("Connect to NCBI server");
		
		GenomicWorkingSetMenu.add(GFF);
		GenomicWorkingSetMenu.add(Genbank);
		GenomicWorkingSetMenu.add(AccessionID);
		GenomicWorkingSetMenu.addSeparator();
		GenomicWorkingSetMenu.add(Preexisting);
		GenomicWorkingSetMenu.add(Ncbi);
		
		//other menu items
		JMenuItem HomologyClusterMenu = new JMenuItem("Homology Clusters");
		JMenuItem GeneIDs = new JMenuItem("Gene IDs");
		JMenuItem ContextSet = new JMenuItem("Context Set");
		JMenuItem DissMeas = new JMenuItem("Dissimilarity Measure");
		JMenuItem Phylo = new JMenuItem("Phylogenetic Tree");
		JMenuItem Motifs = new JMenuItem("Sequence Motifs");
		
		//add items to menu.
		LoadMenu.add(GenomicWorkingSetMenu);
		LoadMenu.add(HomologyClusterMenu);
		LoadMenu.add(GeneIDs);
		LoadMenu.add(ContextSet);
		LoadMenu.add(DissMeas);
		LoadMenu.add(Phylo);
		LoadMenu.add(Motifs);
		
		JMenu HelpMenu = new JMenu("Help");
		
		JMenuItem About = new JMenuItem("About JContextExplorer");
		JMenuItem Manual = new JMenuItem("User's Manual");
		JMenuItem Video = new JMenuItem("Video Tutorials");
		JMenuItem DataSets = new JMenuItem("Existing Datasets");
		
		HelpMenu.add(About);
		HelpMenu.addSeparator();
		HelpMenu.add(Manual);
		HelpMenu.add(Video);
		HelpMenu.add(DataSets);
		
		MB.add(LoadMenu);
		MB.add(HelpMenu);
		
		f.setJMenuBar(MB);
	}
	
// ------ Setters + Getters ------------------------------ //

	public OrganismSet getOS() {
		return OS;
	}

	public void setOS(OrganismSet oS) {
		OS = oS;
	}
	
	// main method
//	public static void main(String args[]){
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//					new Dendrograma(null);
//			}
//		});
//	}
}

