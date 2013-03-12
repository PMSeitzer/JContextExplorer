package moduls.frm.children;

import inicial.Parametres_Inicials;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import genomeObjects.CSDisplayData;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import definicions.BoxContainer;
import definicions.Cluster;
import definicions.Config;
import definicions.Dimensions;

import parser.EscalaFigures;
import parser.EscaladoBox;
import parser.Fig_Pizarra;
import parser.figures.Cercle;
import parser.figures.Escala;
import parser.figures.Linia;
import parser.figures.Marge;
import parser.figures.NomsDendo;
import parser.figures.NomsLabelsEscala;
import tipus.Orientation;
import tipus.rotacioNoms;
import utils.BoxFont;

import newickTreeParsing.Tree;
import newickTreeParsing.TreeNode;

import moduls.frm.ContextLeaf;
import moduls.frm.FrmPrincipalDesk;
import moduls.frm.XYBox;

public class FrmPhylo extends JPanel implements MouseListener{

	//fields
	//baseline
	private FrmPrincipalDesk fr;
	private CSDisplayData CSD;
	
	// =========== Borrowed from FrmPiz ===================/
	// ---- Drawing -----
	// Conversion of tree -> figures
	private LinkedList<?>[] figures = { new LinkedList<Cercle>(),
			new LinkedList<Linia>(), new LinkedList<Marge>() };
	private static final int CERCLE = 0;
	private static final int LINIA = 1;
	private static final int MARGE = 2;
	
	//Drawing / painting related
	private Graphics2D g;
	private int LastSelectedNode = -1;
	private Rectangle2D[] RectanglesSurroundingLabels;
	
	// ----- Configurataion + Parameters ---------//
	//Configuration related
	private Config cfg = null;
	private double radi;
	private int numClusters;
	private Orientation orientacioClusters = Orientation.EAST;
	private rotacioNoms orientacioNoms = rotacioNoms.HORITZ;
	
	//Component Sizes
	private double val_Max_show;		//size to show
	private double val_Min_show;
	double width_dendograma = 0.0;		//dendrogram sizes
	double height_dendograma = 0.0;
	double width_escala = 0.0;			//scale sizes
	double height_escala = 0.0;
	double height_butlles = 0.0;		//circle sizes
	double width_butlles = 0.0;
	double width_lbl_escala = 0.0;		//scale label
	double height_lbl_escala = 0.0;
	double width_nom_nodes = 0.0;		//Node names
	double height_nom_nodes = 0.0;
	private String max_s = "";
	private double VerticalRenderScaleFactor = 1.0;		//JPanel stretching parameters
	private double HorizontalRenderScaleFactor = 1.0;
	
	//Scale boxes (for graphical rendering)
	private EscaladoBox parserDendograma = null;
	private EscaladoBox parserBulles = null;
	private EscaladoBox parserEscala = null;
	private EscaladoBox parserNoms = null;
	private EscaladoBox parserLbl = null;
	
	//constructor
	public FrmPhylo(FrmPrincipalDesk f, CSDisplayData CSD){
		this.fr = f;
		this.CSD = CSD;
		
		this.addMouseListener(this);
		
	}

	// ------------- Methods from FrmPiz -------------- //
	//====== Configuration/Setting - type methods =====//
	
	//Figure import and parsing + configuration related
	public void setFigures(final LinkedList[] lst) {
		this.setFigura(lst);
	}
	public void setFigura(LinkedList figura[]) {
		this.figures = figura;
	}
	public LinkedList[] getFigura() {
		return figures;
	}
	public void setConfig(Config cfg) {
		this.cfg = cfg;

		radi = cfg.getRadi();
		numClusters = fr.getPanBtn().getPhyloTreeLeaves();
		orientacioClusters = cfg.getOrientacioDendo();
		orientacioNoms = cfg.getOrientacioNoms();

		//old way
		val_Max_show = cfg.getValorMaxim();
		val_Min_show = cfg.getValorMinim();

//		//new way!
//		val_Max_show = fr.getPanBtn().val_Max_show_Phylo;
//		val_Min_show = 0;

	}
	
	//====== Drawing Methods =========================//

	//Set all size parameters for all dendrogram objects.
	private void setAmplades(Graphics2D g) {
		final Orientation or = cfg.getOrientacioDendo();

		/* dendrogram */
		if (Orientation.NORTH.equals(or) || Orientation.SOUTH.equals(or)) {
			width_dendograma = this.AmpladaBoxClusters();
			height_dendograma = val_Max_show - val_Min_show;
		} else {
			width_dendograma = val_Max_show - val_Min_show;
			//width_dendograma = 400; //no effect
			height_dendograma = this.AmpladaBoxClusters();
		}

		/* show the scale */
		if (cfg.getConfigMenu().isEscalaVisible()) {
			/* size of the scale */
			if (Orientation.NORTH.equals(or) || Orientation.SOUTH.equals(or)) {
				width_escala = 2 * radi; // east and west
				height_escala = val_Max_show - val_Min_show;
			} else {
				height_escala = 2 * radi; // north and south
				width_escala = val_Max_show - val_Min_show;
				//Reading width = 1.0, height = 10.0
				//System.out.println("Width: " + width_escala + " Height: " + height_escala);
			}
		} else {
			width_escala = 0;
			height_escala = 0;
		}

		/*
		 * Comments:
		 * commenting out this whole section removes bullets, but changing the size
		 * of rr or the quantities of rr doesn't chage anything.
		 */
		/* show the bullets */ //rr = node size
		double rr = cfg.getConfigMenu().getRadiBullets();
		//System.out.println("rr is " + rr);
		if ((rr = cfg.getConfigMenu().getRadiBullets()) > 0) {
			if (Orientation.NORTH.equals(or) || Orientation.SOUTH.equals(or)) {
				width_butlles = this.AmpladaBoxClusters();
				height_butlles = 2 * rr;
			} else {
				width_butlles = 2 * rr;
				height_butlles = 2*this.AmpladaBoxClusters();
			}
		} else {
			width_butlles = 0;
			height_butlles = 0;
		}
		
		/* show the labels of the scale */
		if (cfg.getConfigMenu().isEtiquetaEscalaVisible()) {
			final BoxFont bf = new BoxFont(cfg.getConfigMenu().getFontLabels());
			String txt;
			int ent;
			Dimensions<Double> dim;
			ent = (int) Math.round(val_Max_show);
			txt = Integer.toString(ent);
			if (Orientation.EAST.equals(or) || Orientation.WEST.equals(or)) {
				if (cfg.isTipusDistancia()) {
					dim = bf.getBoxNumberNatural(90, (txt.trim()).length(),
							cfg.getAxisDecimals());
				} else {
					dim = bf.getBoxNumberEnters(90, (txt.trim()).length(),
							cfg.getAxisDecimals());
				}
			} else {
				if (cfg.isTipusDistancia()) {
					dim = bf.getBoxNumberNatural(0, (txt.trim()).length(),
							cfg.getAxisDecimals());
				} else {
					dim = bf.getBoxNumberEnters(0, (txt.trim()).length(),
							cfg.getAxisDecimals());
				}
			}
			width_lbl_escala = dim.getWidth();
			height_lbl_escala = dim.getHeight();
		} else {
			width_lbl_escala = 0;
			height_lbl_escala = 0;
		}

		/* names of the bullets */
		if (cfg.getConfigMenu().isNomsVisibles()) {
			int alf;
			final BoxFont bf = new BoxFont(cfg.getConfigMenu().getFontNoms());
			String tmp;
			Dimensions<Double> dim;

			/* width of names of the bullets */
			if (cfg.getOrientacioNoms().equals(rotacioNoms.HORITZ))
				alf = 0;
			else if (cfg.getOrientacioNoms().equals(rotacioNoms.INCLINAT))
				alf = 45;
			else
				alf = -90;
			if (max_s.equals("")) {
				final Enumeration<String> el = cfg.getHtNoms().elements();
				while (el.hasMoreElements()) {
					tmp = el.nextElement();
					if (tmp.length() > max_s.length())
						max_s = tmp;
				}
			}
			dim = bf.getBox(alf, max_s);

			width_nom_nodes = dim.getWidth();
			height_nom_nodes = dim.getHeight();
		} else {
			width_nom_nodes = 0;
			height_nom_nodes = 0;
		}
	}

	//determine a box of coordinates to contain all objects.
	private void DesplacaPantalla(final BoxContainer b, final double h_mon) {
		double h;
		h = h_mon - b.getCorner_y();
		b.setCorner_y(-h);
	}
	
	private double AmpladaBoxClusters() {
		return ((2 * radi * numClusters) + ((numClusters - 1) * radi));
	}
	
	private void draftDendo(Graphics2D g2d) {
		
		
//		LinkedList<Marge> Marges = getFigura()[2];
//		for (Marge m : Marges){
//			System.out.println("m, Early_Draftdendo: " + m.getPhyloWeight());
//		}
		
		//boxes (on the screen) defining coordinates for rendering
		BoxContainer boxDendograma, boxBulles, boxEscala, boxEscalalbl, boxNoms;

		//Set a preference for the rendering algorithms.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		/* Symmetric frame where it won't be able to paint anything */
		final double inset_Mon = Parametres_Inicials.getMarco();// 15

		/* World size */
		final double width_Mon = this.getSize().getWidth();
		final double height_Mon = this.getSize().getHeight();

		// set widths ---?
		this.setAmplades(g2d);

		// size of the box
		Dimensions<Double> m_d, m_b, m_n, m_e, m_l;

		m_d = new Dimensions<Double>(width_dendograma, height_dendograma);
		
									// 		bullets?
		m_b = new Dimensions<Double>(width_butlles, height_butlles);
		m_n = new Dimensions<Double>(width_nom_nodes, height_nom_nodes);
		m_e = new Dimensions<Double>(width_escala, height_escala);
		m_l = new Dimensions<Double>(width_lbl_escala, height_lbl_escala);

		/* Calculates the free space and places the box on the screen */
//		final XYBox posbox = new XYBox(cfg, inset_Mon, width_Mon, height_Mon,
//				m_d, m_b, m_n, m_e, m_l);

		/*
		 * messing with these parameters affects how the dendogram info is displayed.
		 * 
		 * width_Mon = width of the dendogram figure + labels within the frame
		 * height_Mon = height ''
		 * inset_Mon = size of border around figure
		 * m_* describe the dimensions (width and height) of different quantities
		 * 
		 */

		//m_n = new Dimensions<Double>(width_nom_nodes, 10*height_nom_nodes);

		//Added render scale factors render the vertical spacing between objects accurately.
		final XYBox posbox = new XYBox(cfg, inset_Mon, HorizontalRenderScaleFactor*width_Mon, 
				VerticalRenderScaleFactor*height_Mon,
				m_d, m_b, m_n, m_e, m_l);
		
		//System.out.println("FrmPhylo() scale height,width: " + m_e.getHeight() + "," + m_e.getWidth());
		
		// define the box
		boxDendograma = posbox.getBoxDendo();
		boxBulles = posbox.getBoxBulles();
		boxEscala = posbox.getBoxEscala();
		boxEscalalbl = posbox.getBoxLabelsEscala();
		boxNoms = posbox.getBoxNames();
		
		//System.out.println("boxNoms at" + boxNoms.getVal_min_X() + " by " + boxNoms.getVal_min_Y());
		// locate on screen
		// 'DesplacaPantalla' = scroll screen
				
		//   move box
		this.DesplacaPantalla(boxDendograma, height_Mon);
		this.DesplacaPantalla(boxBulles, height_Mon);
		this.DesplacaPantalla(boxEscala, height_Mon);
		this.DesplacaPantalla(boxEscalalbl, height_Mon);
		this.DesplacaPantalla(boxNoms, height_Mon);

		// reverse the axis of growth
		g2d.scale(1, -1);
		g2d.setBackground(Color.GREEN);

		/*
		 * calculates the factor that allows coordinates to world coordinates screen
		 */
		parserDendograma = new EscaladoBox(boxDendograma);
		if (cfg.getConfigMenu().getRadiBullets() > 0)
			parserBulles = new EscaladoBox(boxBulles);
		if (cfg.getConfigMenu().isNomsVisibles())
			parserNoms = new EscaladoBox(boxNoms);
		if (cfg.getConfigMenu().isEscalaVisible())
			parserEscala = new EscaladoBox(boxEscala);
		if (cfg.getConfigMenu().isEtiquetaEscalaVisible())
			parserLbl = new EscaladoBox(boxEscalalbl);

//		System.out.println("FrmPhylo.parserLbl: " + parserLbl);
//		System.out.println("FrmPhylo.parserEscala: " + parserEscala);
//		
		// range and  data type to represent
		//EDIT!!!!!!!!!

		// --- show things ------------------------------------------------//
		
		final EscalaFigures ef = new EscalaFigures(val_Max_show, 
				val_Min_show,
				cfg.getTipusMatriu(), cfg.getPrecision());


//		Marge m; //margin
//		final Iterator<Marge> itm = ef.ParserMarge(getFigura()[MARGE])
//				.iterator();
//		while (itm.hasNext()) {
//			m = itm.next();
//			m.setEscala(parserDendograma);
//			m.setColor(cfg.getConfigMenu().getColorMarge());
//			m.setFilled(true);
//			m.dibuixa(g2d, orientacioClusters); //draw
//		}

		//horizontal lines
		Linia lin;
		final Iterator<Linia> it = ef.ParserLinies(getFigura()[LINIA])
				.iterator();
		while (it.hasNext()) {
			lin = it.next();
			lin.setEscala(parserDendograma);
			
			//adjustlines with appropriate information
			lin.setFromPhyloTree(true);
			lin.setFr(fr);
			
			//draw lines
			lin.dibuixa(g2d, orientacioClusters);
			
			//draw lines!
			//System.out.println("line: " + lin.getAlcada());
		}

		//vertical lines / boxes
		Marge m;
		final Iterator<Marge> itm2 = ef.ParserMarge(getFigura()[MARGE])
				.iterator();
		while (itm2.hasNext()) {
			m = itm2.next();
			m.setEscala(parserDendograma);
			m.setColor(cfg.getConfigMenu().getColorMarge());
			m.setFilled(false);
			
			m.setFont(cfg.getConfigMenu().getFontNoms());
			m.setFromPhyloTree(true);
			m.setFr(fr);
			
			m.dibuixa(g2d, orientacioClusters);
		}

		
		//show nodes (bullets)
		if (cfg.getConfigMenu().getRadiBullets() > 0) { 
			final Iterator<Cercle> itc = getFigura()[CERCLE].iterator();
			while (itc.hasNext()) {
				final Cercle cer = itc.next();
				cer.setEscala(parserBulles);
				cer.dibuixa(g2d, orientacioClusters);
			}
		}

		//show names of nodes
		if (cfg.getConfigMenu().isNomsVisibles()) { 
			NomsDendo nomsD;
			nomsD = new NomsDendo(getFigura()[CERCLE], cfg.getTipusMatriu());
			nomsD.setEscala(parserNoms);
			nomsD.setColor(cfg.getConfigMenu().getColorNoms());
			nomsD.setFont(cfg.getConfigMenu().getFontNoms());
			nomsD.dibuixa(g2d, orientacioClusters, orientacioNoms);
			
			//retrieve rectangles from names
			this.setRectanglesSurroundingLabels(nomsD.getRectangles());
			
			//Map info to contexts
			CSD.setPhyloCoordinates(nomsD.getRectangles());
			CSD.setPhyloNodeNames(nomsD.getNodeNames());
			
			//map each graphical context to the appropriate source species, in the phylogenetic tree.
			for (ContextLeaf CL : CSD.getGraphicalContexts()){
				for (int i = 0; i < CSD.getPhyloNodeNames().length; i++){
					if (CL.getSourceSpecies().equals(CSD.getPhyloNodeNames()[i])){
						CL.setPhyloTreeCoordinates(CSD.getPhyloCoordinates()[i]);
						CL.setPhyloTreeNodeNameNumber(i);
						break;
					}
				}
			}
//			
//			//initialize all nodes as unselected
//			boolean[] InitialNodeNumbers = new boolean[RectanglesSurroundingLabels.length];
//			Arrays.fill(InitialNodeNumbers, Boolean.FALSE);
//			this.setSelectedNodeNumbers(InitialNodeNumbers);
		}

		// show scale
		if (cfg.getConfigMenu().isEscalaVisible()) {
			Escala esc;
			if (orientacioClusters.equals(Orientation.WEST)
					|| orientacioClusters.equals(Orientation.EAST))
				esc = new Escala(boxEscala.getVal_min_X(),
						boxEscala.getVal_max_X(), cfg.getIncrement(),
						cfg.getTics());
			else
				esc = new Escala(boxEscala.getVal_min_Y(),
						boxEscala.getVal_max_Y(), cfg.getIncrement(),
						cfg.getTics());

			esc.setEscala(parserEscala);
			esc.setColor(cfg.getConfigMenu().getColorEix());
			esc.dibuixa(g2d, orientacioClusters, cfg.getTipusMatriu(),
					cfg.getTics());
		}
		
		//System.out.println("FrmPhylo.draftdendo():" + boxEscala.getVal_max_X());
		
		//show numerical scale labels
		if (cfg.getConfigMenu().isEtiquetaEscalaVisible() && cfg.getTics() > 0) { 
																																						
			NomsLabelsEscala nomsEsc;
			if (orientacioClusters.equals(Orientation.WEST)
					|| orientacioClusters.equals(Orientation.EAST)) {
				nomsEsc = new NomsLabelsEscala(boxEscalalbl.getVal_min_X(),
						boxEscalalbl.getVal_max_X(),
						boxEscalalbl.getVal_max_Y(), cfg.getIncrement(),
						cfg.getTics(), cfg.getAxisDecimals());
			} else {
				nomsEsc = new NomsLabelsEscala(boxEscalalbl.getVal_min_Y(),
						boxEscalalbl.getVal_max_Y(),
						boxEscalalbl.getVal_max_X(), cfg.getIncrement(),
						cfg.getTics(), cfg.getAxisDecimals());
			}
			nomsEsc.setEscala(parserLbl);
			nomsEsc.setColor(cfg.getConfigMenu().getColorLabels());
			nomsEsc.setFont(cfg.getConfigMenu().getFontLabels());
			nomsEsc.dibuixa(g2d, orientacioClusters, cfg.getTipusMatriu());
		}
	}

	public void setRectanglesSurroundingLabels(
			Rectangle2D[] rectanglesSurroundingLabels) {
		RectanglesSurroundingLabels = rectanglesSurroundingLabels;
	}

	public void paint(Graphics arg0) {

		//update configuration information
		//val_Max_show = fr.getPanBtn().val_Max_show_Phylo;
		//System.out.println("FrmPhylo.paint(): valmax = " + val_Max_show);
		
		//this does not seem to change much.
		//this.setConfig(fr.getCurrentFrame().getInternalFrameData().getCfgp());
		
		//basic painting parameters
		super.paint(arg0);
		Graphics2D g2d = (Graphics2D) arg0;
		this.g = g2d;
		this.draftDendo(g);
		
		//change color for drawing
		g.setPaint(Color.RED);
		
		//boxes around nodes
		for (ContextLeaf CL : CSD.getGraphicalContexts()){
			if (CL.isSelected()){
				if (CL.getPhyloTreeCoordinates() != null){
					g.draw(CL.getPhyloTreeCoordinates());
				}
			} 
		}
		
		//reset color
		g.setPaint(Color.BLACK);

	}
	
	//-------------- Getters and Setters -------------- //
	
	public FrmPrincipalDesk getFr() {
		return fr;
	}

	public void setFr(FrmPrincipalDesk fr) {
		this.fr = fr;
	}

	public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		//left click
		if (SwingUtilities.isLeftMouseButton(e)){			
			
			//update CSD
			this.CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
			
			int x ,y;
			
			x = e.getX();    
			y = e.getY(); 
			
			//initialize
			boolean[] SelectedAfterClick = new boolean[RectanglesSurroundingLabels.length];
			Arrays.fill(SelectedAfterClick, Boolean.FALSE);

			//update with current existing set (if appropriate)
			if (e.isShiftDown() == true || e.isControlDown() == true){
				for (ContextLeaf CL : CSD.getGraphicalContexts()){
					for (int i = 0; i < CSD.getPhyloNodeNames().length; i++){
						if (CL.getSourceSpecies().equals(CSD.getPhyloNodeNames()[i])){
							SelectedAfterClick[i] = CL.isSelected();
							break;
						}
					}
				}
			}

			//draw a box around the correct coordinate
			for (int i = 0; i < RectanglesSurroundingLabels.length; i++){
				
				Point p = new Point(x,-y);
			
				if (RectanglesSurroundingLabels[i].contains(p)){
					if (e.isShiftDown() == false && e.isControlDown() == false){
						SelectedAfterClick[i] = true; //no button
					} else if (e.isShiftDown() == false  && e.isControlDown() == true){
						if (SelectedAfterClick[i] == true){
							SelectedAfterClick[i] = false;
						} else {
							SelectedAfterClick[i] = true;
						}
					} else {
						if (LastSelectedNode != -1){
							
							//determine relative location of selected node to current shift+clicked node
							if (LastSelectedNode <= i){
								for (int j = LastSelectedNode; j<= i; j++){
									SelectedAfterClick[j] = true;
								}
							} else {
								for (int j = LastSelectedNode; j >= i; j--){
									SelectedAfterClick[j] = true;
								}
							}
							
						} else {
							SelectedAfterClick[i] = true; //no previous selected node
						}
					}
					
					//update last selected node
					LastSelectedNode = i;
				} 
			}
			
			//update status of currently selected nodes
			for (ContextLeaf CL : CSD.getGraphicalContexts()){
				//initially, de-select.
				CL.setSelected(false);
				
				//option to re-select?
				for (int i = 0; i < CSD.getPhyloNodeNames().length; i++){
					if (CL.getSourceSpecies().equals(CSD.getPhyloNodeNames()[i])){
						CL.setSelected(SelectedAfterClick[i]);
					}
				}
			}
			
			//update master CSD
			fr.getCurrentFrame().getInternalFrameData().getQD().setCSD(CSD);
			
			//call main frame to update this and all other panels
			this.fr.UpdateSelectedNodes();

		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//update display
	public void UpdateNodes(){
		
		//retrieve most current set of selected nodes
		this.CSD = fr.getCurrentFrame().getInternalFrameData().getQD().getCSD();
		
		//repaint nodes
		this.repaint();
		
	}
	
}
