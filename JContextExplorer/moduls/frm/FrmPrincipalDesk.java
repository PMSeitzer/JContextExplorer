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

package moduls.frm;

import genomeObjects.CSDisplayData;
import genomeObjects.OrganismSet;
import inicial.FesLog;
import inicial.Language;
import inicial.Parametres_Inicials;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import moduls.frm.Panels.Jpan_DisplayOptions;
import moduls.frm.Panels.Jpan_GraphMenu;
import moduls.frm.Panels.Jpan_Menu;
import moduls.frm.Panels.Jpan_MotifOptions;
import moduls.frm.Panels.Jpan_PhyTreeMenu;
import moduls.frm.Panels.Jpan_TabbedMenu;
import moduls.frm.Panels.Jpan_btn;
import moduls.frm.Panels.Jpan_btnExit;
import moduls.frm.Panels.Jpan_btn_NEW;
import moduls.frm.Panels.Jpan_genome;
import moduls.frm.children.DeviationMeasuresBox;
import moduls.frm.children.FrmPiz;
import parser.ToNewick;
import parser.ToNewick2;
import parser.ToTXT;
import parser.Ultrametric;
import parser.EPS.EPSExporter;
import tipus.tipusDades;
import utils.MiMath;
import definicions.Cluster;
import definicions.Config;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Main frame window
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class FrmPrincipalDesk extends JFrame implements InternalFrameListener{
	
	// ----- Fields ---------------------------------------------------//
	
	private static final long serialVersionUID = 1L;

	private final JPanel pan_West, pan_Exit, pan_South; //Segment space into different groups
	
	private final JPanel pan_Center;
										//pan_Exit = About + Exit buttons
	private final JDesktopPane pan_Desk; //Desktop Pane

	//private final Jpan_btn panBtn; //Load and Update buttons
	private Jpan_btn_NEW panBtn;
	
	private final Jpan_Menu panMenu; //Settings panel
	
	//version 2.0
	private final Jpan_TabbedMenu panMenuTab;
	private final Jpan_GraphMenu panGraphMenu;
	private final Jpan_MotifOptions panMotifOptions;
	private final Jpan_PhyTreeMenu panPhyTreeMenu;

	private final Jpan_DisplayOptions panDisplayOptions;
	
	private final Jpan_genome panGenome; // genome-viewing frame

	private Config cfg; 	//context trees
	private Config cfgPhylo;	//phylo trees
	
	//private JInternalFrame currentFpiz; //internal frame that contains tree pane
	private FrmInternalFrame currentFpiz;
	
	private FrmPiz currentFpizpanel;
	
	private OrganismSet OS; //OrganismSet information
	
	//data necessary to render contexts
	private boolean[] SelectedNodeNumbers;
	private CSDisplayData CSD;

	// ----- New Fields (2.0) ------------------------------------------//
	
	private boolean IncludeMotifs = false;
	private boolean DisplayMotifs = false;
	private String SelectedAnalysisType = "Search Results";
	private File FileChooserSource;
	private int InternalFrameID = 0;	//for debugging
	
	// ----- Methods ---------------------------------------------------//		

	public int getInternalFrameID() {
		return InternalFrameID;
	}


	public void setInternalFrameID(int internalFrameID) {
		InternalFrameID = internalFrameID;
	}


	//This is the main GUI window.
	public FrmPrincipalDesk(final String title, OrganismSet theOrganismSet) {
		
		//INITIALIZATIONS
		super(title);
		this.OS = theOrganismSet;
		this.FileChooserSource = OS.getSourceDirectory();
		
		//DESKTOP FRAME INFORMATION
		pan_Desk = new JDesktopPane();
		pan_Desk.setBackground(Color.LIGHT_GRAY);
		pan_Desk.setBorder(BorderFactory.createTitledBorder(""));

		//CREATE COMPONENT PANELS
		panMenu = new Jpan_Menu(this); 			//Settings panel (West)
		pan_Exit = new Jpan_btnExit(this); 		//About + Exit (SouthWest)
		panBtn = new Jpan_btn_NEW(this);
		panGenome = new Jpan_genome(this);		//scrollable genome view
		
		//ORIENTATION PANELS
		//CENTER: THIS
		pan_Center = new JPanel();
		pan_Center.setLayout(new BorderLayout());
		pan_Center.add(pan_Desk, BorderLayout.CENTER);
		
		//WEST: Update Settings, Searching
		pan_West = new JPanel();
		pan_West.setLayout(new BorderLayout());
		pan_West.add(panBtn, BorderLayout.NORTH);
	
		//switch 1
//			JScrollPane scrollPane1 = new JScrollPane(panMenu);	
//		pan_West.add(scrollPane1, BorderLayout.CENTER);
		
		//options/menus
		panGraphMenu = new Jpan_GraphMenu(this);		//Graph menu
		panMotifOptions = new Jpan_MotifOptions(this);	//Motif Options tab
		panPhyTreeMenu = new Jpan_PhyTreeMenu(this);	//Loadable phylogenetic tree
		panDisplayOptions = new Jpan_DisplayOptions(this);//options panel
		panMenuTab = new Jpan_TabbedMenu(panDisplayOptions, panMenu, panGraphMenu,
				panMotifOptions,panPhyTreeMenu);
		pan_West.add(panMenuTab, BorderLayout.CENTER);

		//SOUTH: Genome context viewing
		pan_South = new JPanel();
		pan_South.setLayout(new BorderLayout());
		pan_South.add(panGenome);
		
		pan_Center.add(pan_South, BorderLayout.SOUTH);
		
		//set precision value
		Jpan_Menu.setPrecision(18);
		
		//SET PROPERTIES OF DESKTOP FRAME
		//JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame.setDefaultLookAndFeelDecorated(false);
		final int width_win = Parametres_Inicials.getWidth_frmPrincipal();
		final int height_win = Parametres_Inicials.getHeight_frmPrincipal();
		this.setSize(width_win, height_win);
		//this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//ADD ORIENTATION FRAMES TO DESKTOP FRAME
		this.add(pan_West, BorderLayout.WEST);
		this.add(pan_Center, BorderLayout.CENTER);

	}


	public Jpan_MotifOptions getPanMotifOptions() {
		return panMotifOptions;
	}

	public Jpan_btn_NEW getPanBtn() {
		return panBtn;
	}

	public Config getConfig() {
		//System.out.println("enter getconfig");
		cfg = new Config(Jpan_Menu.getCfgPanel());
		//System.out.println("made a config");
		
		//problem 11-
		try {
			cfg.setMatriu(panBtn.getMatriu());
		} catch (Exception ex) {}

		//System.out.println("set matrix");
		if (cfg.getValorMaxim() == 0) {
			cfg.getConfigMenu().setValMax(cfg.getCimDendograma());
			//System.out.println("set valmax");
		}
		//System.out.println("return");
		return cfg;
	}

	//Create an internal frame
	public FrmInternalFrame createInternalFrame(boolean isUpdate,
			String methodName) {
		int x, y, width, height;
		this.InternalFrameID = InternalFrameID + 1;
		FrmInternalFrame pizarra;
		
//		if (isUpdate) {
//			x = currentFpiz.getX();
//			y = currentFpiz.getY();
//			width = currentFpiz.getWidth();
//			height = currentFpiz.getHeight();
//			x = 0;
//			y = 0;
//			width = Parametres_Inicials.getWidth_frmDesk();
//			height = Parametres_Inicials.getHeight_frmDesk();
//			
//		} else {
//			x = 0;
//			y = 0;
//			width = Parametres_Inicials.getWidth_frmDesk();
//			height = Parametres_Inicials.getHeight_frmDesk();
//		}
		
		x = 0;
		y = 0;
		width = Parametres_Inicials.getWidth_frmDesk();
		height = Parametres_Inicials.getHeight_frmDesk();
		
		//pizarra translates to "slate" - internal tree frame
		pizarra = new FrmInternalFrame(methodName, isUpdate, x, y, this);
		pizarra.setSize(width, height);
		pizarra.setBackground(Color.BLUE);
		pizarra.setLayout(new BorderLayout());
		pizarra.addInternalFrameListener(panBtn);
		pizarra.addInternalFrameListener(panGenome);
		pan_Desk.add(pizarra, BorderLayout.CENTER);
				
		//desktop manager maximizes frame.
		DefaultDesktopManager ddm = new DefaultDesktopManager();
		ddm.maximizeFrame(pizarra);
		
		//add to listener
		//pizarra.addInternalFrameListener(this);

		return pizarra;
	}

	//Are you sure you want to Exit?
	public void toGoOut() {
		final String msg = Language.getLabel(0);
		int opt;
		opt = JOptionPane.showConfirmDialog(null, msg, Language.getLabel(46),
				JOptionPane.YES_NO_OPTION);
		if (opt == JOptionPane.YES_OPTION) {
			FesLog.LOG.info("Exit");
			System.exit(0);
		}
	}

	//Save stuff
	public void savePicture(final BufferedImage buff, final String tipus)
			throws Exception {
		String sPath;
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		final FileDialog fd = new FileDialog(this, Language.getLabel(75) + " "
				+ tipus.toUpperCase(), FileDialog.SAVE);
		fd.setFile(sNameNoExt + "." + tipus);
		fd.setVisible(true);

		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			final File fil = new File(sPath);
			try {
				ImageIO.write(buff, tipus, fil);
//				FesLog.LOG.info("Imatge Emmagatzemada amb exit");
			} catch (final IOException e) {
				String msg_err = Language.getLabel(76);
//				FesLog.LOG
//						.throwing(
//								"FrmPrincipalDesk",
//								"savePicture(final BufferedImage buff, final String tipus)",
//								e);
				throw new Exception(msg_err);
			} catch (Exception e) {
				String msg_err = Language.getLabel(77);
//				FesLog.LOG
//						.throwing(
//								"FrmPrincipalDesk",
//								"savePicture(final BufferedImage buff, final String tipus)",
//								e);
				throw new Exception(msg_err);
			}
		}
	}

	public void savePostSript(FrmPiz frmpiz) throws Exception {
		String sPath;
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		final FileDialog fd = new FileDialog(this, Language.getLabel(75)
				+ " EPS", FileDialog.SAVE);
		fd.setFile(sNameNoExt + ".eps");
		fd.setVisible(true);

		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			try {
				new EPSExporter(cfg, frmpiz, sPath);

//				FesLog.LOG.info("Imatge EPS emmagatzemada amb exit");
			} catch (Exception e) {
				System.out.println("Exception 291 FrmPrincipalDesk");
				String msg_err = Language.getLabel(77);
//				FesLog.LOG.throwing("FrmPrincipalDesk",
//						"savePostScript(final BufferedImage buff)", e);
				throw new Exception(msg_err);
			}
		}
	}

	public void saveTXT(Cluster arrel, int precisio, tipusDades tip)
			throws Exception {
		String sPath, msg_box = Language.getLabel(80) + " TXT";
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		FileDialog fd = new FileDialog(this, msg_box, FileDialog.SAVE);
		fd.setFile(sNameNoExt + "-tree.txt");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			ToTXT saveTXT = new ToTXT(arrel, precisio, tip);
			saveTXT.saveAsTXT(sPath);
		}
	}

	//save Newick tree format
	public void saveNewick(Cluster root, int precision, tipusDades typeData)
			throws Exception {
		String msgBox, sPath;
		FileDialog fd;
		double heightBottom, heightMin, heightMax, extraSpace;
		//ToNewick toNewick;

		msgBox = Language.getLabel(80) + " Newick";
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		fd = new FileDialog(this, msgBox, FileDialog.SAVE);
		fd.setFile(sNameNoExt + "-Newick.txt");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			if (cfg.getTipusMatriu().equals(tipusDades.DISTANCIA)) {
				heightBottom = 0.0;
			} else {
				heightMin = cfg.getBaseDendograma();
				heightMax = cfg.getCimDendograma();
				extraSpace = (heightMax - heightMin)
						* (0.05 * MiMath.Arodoneix((heightMax - heightMin),
								precision));
				extraSpace = MiMath.Arodoneix(extraSpace, precision);
				heightBottom = heightMax + extraSpace;
			}
			//old version
//			toNewick = new ToNewick(root, precision, typeData, heightBottom);
//			toNewick.saveAsNewick(sPath);
			
			//new version
			ToNewick2 toNewick2 = new ToNewick2(root, precision, typeData, heightBottom);
			toNewick2.saveAsNewick(sPath);

		}
	}

	public void saveUltrametricTXT() throws Exception {
		String sPath, msg_box = Language.getLabel(80) + " TXT";
		String sNameNoExt = Jpan_btn.getFileNameNoExt();
		FileDialog fd = new FileDialog(this, msg_box, FileDialog.SAVE);
		fd.setFile(sNameNoExt + "-ultrametric.txt");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			sPath = fd.getDirectory() + fd.getFile();
			Ultrametric um = new Ultrametric();
			um.saveAsTXT(sPath, cfg.getPrecision());
		}
	}

	public void showUltrametricErrors() {
		DeviationMeasuresBox box = new DeviationMeasuresBox(this);
		box.setVisible(true);
	}

	//Getters and Setters
	public Jpan_Menu getPan_Menu() {
		return this.panMenu;
	}

	public JDesktopPane getPan_Desk() {
		return this.pan_Desk;
	}

	public void setCurrentFrame(FrmInternalFrame internalFrame) {
		this.currentFpiz = internalFrame;
	}
	
	public FrmInternalFrame getCurrentFrame(){
		return currentFpiz;
	}
	
	public OrganismSet getOS() {
		return OS;
	}

	public void setOS(OrganismSet oS) {
		OS = oS;
	}

	public boolean[] getSelectedNodeNumbers() {
		return SelectedNodeNumbers;
	}

	public void setSelectedNodeNumbers(boolean[] selectedNodeNumbers) {
		SelectedNodeNumbers = selectedNodeNumbers;
	}

//	//internal frame methods - added, + highly experimental
//	public FrmPiz getCurrentFpizpanel() {
//		return currentFpizpanel;
//	}
//
//	public void setCurrentFpizpanel(FrmPiz currentFpizpanel) {
//		this.currentFpizpanel = currentFpizpanel;
//	}

	//centralized select node update source
	public void UpdateSelectedNodes() {
		
		//search results frame
		if (this.getCurrentFrame().getInternalFrameData().getSearchResultsFrame() != null){
			this.getCurrentFrame().getInternalFrameData().getSearchResultsFrame().UpdateNodes();
		}
		
		//context tree update
		if (this.getCurrentFrame().getInternalFrameData().getContextTreePanel() != null){
			this.getCurrentFrame().getInternalFrameData().getContextTreePanel().UpdateNodes();
		}
		
		//phylo tree update
		if (this.getCurrentFrame().getInternalFrameData().getPhyloTreePanel() != null){
			this.getCurrentFrame().getInternalFrameData().getPhyloTreePanel().UpdateNodes();
		}
		
	}

	public Jpan_genome getPanGenome() {
		return panGenome;
	}

	public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
//		System.out.println("Selected Nodes:" );
//		if (CSD.getSelectedNodes() != null){
//			for (int i = 0; i<CSD.getSelectedNodes().length; i++){
//				System.out.println(i + ": " + CSD.getSelectedNodes()[i]);
//			}
//		} else {
//			System.out.println("none");
//		}
	}

	public Jpan_Menu getPanMenu() {
		return panMenu;
	}

	public boolean isIncludeMotifs() {
		return IncludeMotifs;
	}

	public void setIncludeMotifs(boolean includeMotifs) {
		IncludeMotifs = includeMotifs;
	}

	public Jpan_TabbedMenu getPanMenuTab() {
		return panMenuTab;
	}

	public Jpan_PhyTreeMenu getPanPhyTreeMenu() {
		return panPhyTreeMenu;
	}

	//try to return a phylo config, if possible
	public Config getCfgPhylo() {
		try {
			return cfgPhylo;
		} catch (Exception ex){
			cfgPhylo = new Config(Jpan_Menu.getCfgPanel());
			//System.out.println("made a config");
			
			//problem 11-
			try {
				cfgPhylo.setMatriu(panBtn.getMatriu());
			} catch (Exception ex2) {}

			//System.out.println("set matrix");
			if (cfgPhylo.getValorMaxim() == 0) {
				cfgPhylo.getConfigMenu().setValMax(cfgPhylo.getCimDendograma());
				//System.out.println("set valmax");
			}
			
			return cfgPhylo;
		}
		

	}

	public void setCfgPhylo(Config cfgPhylo) {
		this.cfgPhylo = cfgPhylo;
	}

	public Config getCfg() {
		return cfg;
	}

	public void setCfg(Config cfg) {
		this.cfg = cfg;
	}

	public String getSelectedAnalysisType() {
		return SelectedAnalysisType;
	}

	public void setSelectedAnalysisType(String selectedAnalysisType) {
		SelectedAnalysisType = selectedAnalysisType;
	}

	public boolean isDisplayMotifs() {
		return DisplayMotifs;
	}

	public void setDisplayMotifs(boolean displayMotifs) {
		DisplayMotifs = displayMotifs;
	}

	public File getFileChooserSource() {
		return FileChooserSource;
	}

	public void setFileChooserSource(File fileChooserSource) {
		FileChooserSource = fileChooserSource;
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
	
		FrmInternalFrame CurrentFrame = (FrmInternalFrame) e.getSource();
		this.setCurrentFrame(CurrentFrame);
		System.out.println(CurrentFrame.getInternalFrameData().getQD().getName());
		
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
