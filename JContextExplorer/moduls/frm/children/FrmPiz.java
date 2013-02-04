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

package moduls.frm.children;

import genomeObjects.CSDisplayData;
import inicial.FesLog;
import inicial.Language;
import inicial.Parametres_Inicials;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

import moduls.frm.ContextLeaf;
import moduls.frm.FrmPrincipalDesk;
import moduls.frm.XYBox;
import moduls.frm.Panels.Jpan_btn_NEW;
import parser.EscalaFigures;
import parser.EscaladoBox;
import parser.figures.Cercle;
import parser.figures.Escala;
import parser.figures.Linia;
import parser.figures.Marge;
import parser.figures.NomsDendo;
import parser.figures.NomsLabelsEscala;
import tipus.Orientation;
import tipus.rotacioNoms;
import utils.BoxFont;
import definicions.BoxContainer;
import definicions.Config;
import definicions.Dimensions;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Dendrogram frame
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class FrmPiz extends JPanel implements MouseListener, MouseMotionListener{
// ----- Fields -----------------------------------------------//
	
	private static final long serialVersionUID = 1L;
	private static final int CERCLE = 0;
	private static final int LINIA = 1;
	private static final int MARGE = 2;
	private ActionListener al;
	private JPopupMenu menu;
	private Rectangle2D[] RectanglesSurroundingLabels;

	private EscaladoBox parserDendograma = null;
	private EscaladoBox parserBulles = null;
	private EscaladoBox parserEscala = null;
	private EscaladoBox parserNoms = null;
	private EscaladoBox parserLbl = null;

	// numbers that accompany the scale
	double width_lbl_escala = 0.0;
	double height_lbl_escala = 0.0;

	// node names
	double width_nom_nodes = 0.0;
	double height_nom_nodes = 0.0;

	// draw circles
	double height_butlles = 0.0;
	double width_butlles = 0.0;
	
	// draw scale
	double width_escala = 0.0;
	double height_escala = 0.0;

	// draw dendo
	double width_dendograma = 0.0;
	double height_dendograma = 0.0;

	private Config cfg = null;

	private int numClusters;
	private double radi;

	/*
	 * the figures show only the part that is within these ranges
	 */
	private double val_Max_show;
	private double val_Min_show;
	
	//private Orientation orientacioClusters = Orientation.NORTH;
	private Orientation orientacioClusters = Orientation.EAST;
	private rotacioNoms orientacioNoms = rotacioNoms.HORITZ;
	private String max_s = "";
	private LinkedList<?>[] figures = { new LinkedList<Cercle>(),
			new LinkedList<Linia>(), new LinkedList<Marge>() };
	protected FrmPiz frmpiz;
	private final FrmPrincipalDesk frm; // main window
	
	//added variable to experiment with box sizes
	boolean InitialBoxes = true;
	
	// ----- New Fields --------------------------------------------//
	
	//SF > 1 : render larger ; SF < 1 : render smaller 
	private double VerticalRenderScaleFactor = 1.0;
	private double HorizontalRenderScaleFactor = 1.0;

	private Graphics2D g; //graphics2d object? make it a field?
	
	private boolean[] SelectedNodeNumbers; //selected nodes names
	private Rectangle SelectionRectangle;
	private int LastSelectedNode = -1; //for click + shift
	private boolean isMousePressed = false; // for drawing box
	private int PivotX;
	private int PivotY;
	
	private CSDisplayData CSD;
		
	// ----- Methods -----------------------------------------------//

	//constructor
	public FrmPiz(final FrmPrincipalDesk f, CSDisplayData CSD) {
		super();
		frm = f;
		this.CSD = CSD;
		this.addMouseListener(this);
		this.initComponentsMenu();
		this.frmpiz = this;
		 
//		System.out.println("Step 7");

	}
	
	private void initComponentsMenu() {
		al = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				String errMsg;

				if (evt.getActionCommand().equals(Language.getLabel(96))) {
					// SAVE TO JPG
					try {
						final BufferedImage buff = FrmPiz.this.dibu();
						frm.savePicture(buff, "jpg");
					} catch (Exception e) {
						errMsg = Language.getLabel(81);
//						FesLog.LOG
//								.throwing("FrmPiz", "initComponentsMenu()", e);
						JOptionPane.showInternalMessageDialog(
								frm.getPan_Desk(), errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (evt.getActionCommand().equals(Language.getLabel(97))) {
					// SAVE TO PNG
					try {
						final BufferedImage buff = FrmPiz.this.dibu();
						frm.savePicture(buff, "png");
					} catch (Exception e) {
						errMsg = Language.getLabel(81);
						JOptionPane.showInternalMessageDialog(
								frm.getPan_Desk(), errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (evt.getActionCommand().equals(Language.getLabel(95))) {
					// VIEW TREE
					try {
						new PrnArrelHTML(cfg.getMatriu().getArrel(),
								cfg.getPrecision(), cfg);
					} catch (Exception e) {
//						FesLog.LOG
//								.throwing("FrmPiz", "initComponentsMenu()", e);
						errMsg = Language.getLabel(76);
						JOptionPane.showMessageDialog(frm.getPan_Desk(),
								errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (evt.getActionCommand().equals(Language.getLabel(98))) {
					// SAVE TO TXT
					try {
						frm.saveTXT(cfg.getMatriu().getArrel(),
								cfg.getPrecision(), cfg.getTipusMatriu());
					} catch (Exception e) {
						errMsg = Language.getLabel(81);
//						FesLog.LOG
//								.throwing("FrmPiz", "initComponentsMenu()", e);
						JOptionPane.showInternalMessageDialog(
								frm.getPan_Desk(), errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (evt.getActionCommand().equals(Language.getLabel(87))) {
					// SAVE TO NEWICK
					try {
						frm.saveNewick(cfg.getMatriu().getArrel(),
								cfg.getPrecision(), cfg.getTipusMatriu());
					} catch (Exception e) {
						errMsg = Language.getLabel(81);
//						FesLog.LOG
//								.throwing("FrmPiz", "initComponentsMenu()", e);
						JOptionPane.showInternalMessageDialog(
								frm.getPan_Desk(), errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (evt.getActionCommand().equals(Language.getLabel(99))) {
					// SAVE TO EPS
					try {
						frm.savePostSript(frmpiz);
					} catch (Exception e) {
						System.out.println("Exception 262 FrmPiz");
						errMsg = Language.getLabel(81);
//						FesLog.LOG
//								.throwing("FrmPiz", "initComponentsMenu()", e);
						JOptionPane.showInternalMessageDialog(
								frm.getPan_Desk(), errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (evt.getActionCommand()
						.equals(Language.getLabel(116))) {
					// SAVE ULTRAMETRIC AS TXT
					try {
						frm.saveUltrametricTXT();
					} catch (Exception e) {
						errMsg = Language.getLabel(81);
						FesLog.LOG
								.throwing("FrmPiz", "initComponentsMenu()", e);
						JOptionPane.showInternalMessageDialog(
								frm.getPan_Desk(), errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (evt.getActionCommand()
						.equals(Language.getLabel(117))) {
					// SHOW ULTRAMETRIC ERRORS
					try {
						frm.showUltrametricErrors();
					} catch (Exception e) {
						errMsg = Language.getLabel(81);
						FesLog.LOG
								.throwing("FrmPiz", "initComponentsMenu()", e);
						JOptionPane.showInternalMessageDialog(
								frm.getPan_Desk(), errMsg, "MultiDendrograms",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};

		menu = new JPopupMenu();
		final JMenuItem me0 = new JMenuItem();
		final JMenuItem me1 = new JMenuItem();
		final JMenuItem me2 = new JMenuItem();
		final JMenuItem me3 = new JMenuItem();
		final JMenuItem me4 = new JMenuItem();
		final JMenuItem me5 = new JMenuItem();
		final JMenuItem me6 = new JMenuItem();
		final JMenuItem me7 = new JMenuItem();

		me0.setText(Language.getLabel(87)); // save newick
		me1.setText(Language.getLabel(95)); // show dendr. details
		me2.setText(Language.getLabel(98)); // save txt
		me3.setText(Language.getLabel(96)); // save jpg
		me4.setText(Language.getLabel(97)); // save png
		me5.setText(Language.getLabel(99)); // save eps
		me6.setText(Language.getLabel(116)); // save ultra as txt
		me7.setText(Language.getLabel(117)); // show ultra details

		me0.addActionListener(al);
		me1.addActionListener(al);
		me2.addActionListener(al);
		me3.addActionListener(al);
		me4.addActionListener(al);
		me5.addActionListener(al);
		me6.addActionListener(al);
		me7.addActionListener(al);

		menu.add(me7);
		menu.add(me1);
		menu.addSeparator();
		menu.add(me6);
		menu.add(me2);
		menu.add(me0);
		menu.addSeparator();
		menu.add(me3);
		menu.add(me4);
		menu.add(me5);

		//this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	@Override
	protected void processMouseEvent(final MouseEvent evt) {
		if (evt.isPopupTrigger())
			menu.show(evt.getComponent(), evt.getX(), evt.getY());
		else
			super.processMouseEvent(evt);
	}

	public void setConfig(Config cfg) {
		this.cfg = cfg;

		radi = cfg.getRadi();
		numClusters = cfg.getMatriu().getArrel().getFills();
		orientacioClusters = cfg.getOrientacioDendo();
		orientacioNoms = cfg.getOrientacioNoms();

		val_Max_show = cfg.getValorMaxim();
		val_Min_show = cfg.getValorMinim();

	}

	public void setFigures(final LinkedList[] lst) {
		setFigura(lst);
//		FesLog.LOG.finest("Assinada les figures: (" + lst[CERCLE].size() + ", "
//				+ lst[LINIA].size() + ", " + lst[MARGE].size() + ")");
	}

	public LinkedList<Object>[] getFigures() {
		return getFigura();
	}

	@Override
	public void update(Graphics arg0) {
		super.update(arg0);
	}

	//			 setWidths
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

	@Override
	public void paint(Graphics arg0) {

		//update painting?
		//this.setConfig(frm.getCfg());
		
		//parameters
		//System.out.println("FrmPiz.paint(): valmax = " + val_Max_show);
		
		//basic painting parameters
		super.paint(arg0);
		Graphics2D g2d = (Graphics2D) arg0;
		this.g = g2d;
		this.draftDendo(g);
		g.setPaint(Color.RED);
		
		//boxes around nodes
		for (ContextLeaf CL : CSD.getGraphicalContexts()){
			if (CL.isSelected()){
				g.draw(CL.getContextTreeCoordinates());
			} 
		}
		
		//reset color
		g.setPaint(Color.BLACK);

	}

	//render the image, using info and calling this.draftDendo()
	private BufferedImage dibu() {
		Graphics2D g2d;
		final double width_Mon = this.getSize().getWidth();
		final double height_Mon = this.getSize().getHeight();
		final BufferedImage buff = new BufferedImage((int) width_Mon,
				(int) height_Mon, BufferedImage.TYPE_INT_RGB);
		g2d = buff.createGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, (int) width_Mon, (int) height_Mon);
		this.draftDendo(g2d);
		g2d.dispose();

		return buff;
	}
		
	private void draftDendo(Graphics2D g2d) {
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
		
		//test - doesn't do anything.
		//m_d = new Dimensions<Double>(0.5*width_dendograma, 0.5*height_dendograma);
		
		//System.out.println("m_d is " + m_d);
		
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
		
		//System.out.println("FrmPiz() scale height,width: " + m_e.getHeight() + "," + m_e.getWidth());
		
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

		// range and  data type to represent
		//EDIT!!!!!!!!!
		
		final EscalaFigures ef = new EscalaFigures(val_Max_show, 
				val_Min_show,
				cfg.getTipusMatriu(), cfg.getPrecision());

		Marge m; //margin
		final Iterator<Marge> itm = ef.ParserMarge(getFigura()[MARGE])
				.iterator();
		while (itm.hasNext()) {
			m = itm.next();
			m.setEscala(parserDendograma);
			m.setColor(cfg.getConfigMenu().getColorMarge());
			m.setFilled(true);
			m.dibuixa(g2d, orientacioClusters); //draw
		}

		Linia lin;
		final Iterator<Linia> it = ef.ParserLinies(getFigura()[LINIA])
				.iterator();
		while (it.hasNext()) {
			lin = it.next();
			lin.setEscala(parserDendograma);
			lin.dibuixa(g2d, orientacioClusters);
		}

		final Iterator<Marge> itm2 = ef.ParserMarge(getFigura()[MARGE])
				.iterator();
		while (itm2.hasNext()) {
			m = itm2.next();
			m.setEscala(parserDendograma);
			m.setColor(cfg.getConfigMenu().getColorMarge());
			m.setFilled(false);
			m.dibuixa(g2d, orientacioClusters);
		}

		// --- show things ------------------------------------------------//
		
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
			CSD.setCoordinates(nomsD.getRectangles());
			CSD.setNodeNames(nomsD.getNodeNames());
			
			//write data to graphical contexts
			for (ContextLeaf CL : CSD.getGraphicalContexts()){
				for (int i = 0; i < CSD.getNodeNames().length; i++){
					if (CL.getName().equals(CSD.getNodeNames()[i])){
						CL.setContextTreeCoordinates(CSD.getCoordinates()[i]);
						break;
					}
				}
			}
			
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
		
		//System.out.println("FrmPiz.draftdendo():" + boxEscala.getVal_max_X());
		
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

	private double AmpladaBoxClusters() {
		return ((2 * radi * numClusters) + ((numClusters - 1) * radi));
	}

	private void DesplacaPantalla(final BoxContainer b, final double h_mon) {
		double h;
		h = h_mon - b.getCorner_y();
		b.setCorner_y(-h);
	}

	public void setFigura(LinkedList figura[]) {
		this.figures = figura;
	}

	public LinkedList[] getFigura() {
		return figures;
	}

	// ----- New Methods -----------------------------------------------//
	
	public double getVerticalRenderScaleFactor() {
		return VerticalRenderScaleFactor;
	}

	public void setVerticalRenderScaleFactor(double verticalRenderScaleFactor) {
		VerticalRenderScaleFactor = verticalRenderScaleFactor;
	}

	public double getHorizontalRenderScaleFactor() {
		return HorizontalRenderScaleFactor;
	}

	public void setHorizontalRenderScaleFactor(double horizontalRenderScaleFactor) {
		HorizontalRenderScaleFactor = horizontalRenderScaleFactor;
	}

	public Rectangle2D[] getRectanglesSurroundingLabels() {
		return RectanglesSurroundingLabels;
	}

	public void setRectanglesSurroundingLabels(
			Rectangle2D[] rectanglesSurroundingLabels) {
		RectanglesSurroundingLabels = rectanglesSurroundingLabels;
	}

	public boolean[] getSelectedNodeNumbers() {		
		return SelectedNodeNumbers;
	}

	//the setter updates the data to be sent outwards
	public void setSelectedNodeNumbers(boolean[] selectedNodeNumbers) {
		this.SelectedNodeNumbers = selectedNodeNumbers;
		this.frm.setSelectedNodeNumbers(selectedNodeNumbers);
		this.CSD.setSelectedNodes(selectedNodeNumbers);
		this.frm.setCSD(this.CSD);
		
//		//update selected nodes -> internal frame data
//		this.SelectedNodeNumbers = selectedNodeNumbers;
//		
//		LinkedHashMap<String,Boolean> CurrentlySelectedNodes = new LinkedHashMap<String,Boolean>();
//		
//		for (int i = 0; i < selectedNodeNumbers.length; i++){
//			CurrentlySelectedNodes.put(arg0, selectedNodeNumbers[i]);
//		}
	}

	public void UpdateNodes(){
				
		//retrieve most current set of selected nodes
		this.CSD = frm.getCurrentFrame().getInternalFrameData().getQD().getCSD();
		
		//repaint nodes
		this.repaint();
		
	}
	
	// ----- Mouse Events --------------------------------------------//
	
	@Override
	public void mouseClicked(MouseEvent e){
		
		//left click
		if (SwingUtilities.isLeftMouseButton(e)){			
			
			//update CSD
			this.CSD = frm.getCurrentFrame().getInternalFrameData().getQD().getCSD();
			
			int x ,y;
			
			x = e.getX();    
			y = e.getY(); 
			
			//initialize
			boolean[] SelectedAfterClick = new boolean[RectanglesSurroundingLabels.length];
			Arrays.fill(SelectedAfterClick, Boolean.FALSE);
			
			//update with current existing set (if appropriate)
			if (e.isShiftDown() == true || e.isControlDown() == true){
				for (ContextLeaf CL : CSD.getGraphicalContexts()){
					for (int i = 0; i < CSD.getNodeNames().length; i++){
						if (CL.getName().equals(CSD.getNodeNames()[i])){
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
				for (int i = 0; i < CSD.getNodeNames().length; i++){
					if (CL.getName().equals(CSD.getNodeNames()[i])){
						CL.setSelected(SelectedAfterClick[i]);
					}
				}
			}
			
			//update master CSD
			frm.getCurrentFrame().getInternalFrameData().getQD().setCSD(CSD);
			
			//call main frame to update this and all other panels
			this.frm.UpdateSelectedNodes();

		}
		
		//right click
		if (SwingUtilities.isRightMouseButton(e)){

			//trigger pop-up menu display
			this.menu.show(e.getComponent(),
					e.getXOnScreen(), e.getYOnScreen());
			
			//reposition appropriately
			this.menu.setLocation(e.getXOnScreen(),e.getYOnScreen());
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		//System.out.println("Mouse is pressed");
		
//		//set mouse pressed to true
//		isMousePressed = true;
//		
//		//note selection point
//		PivotX = e.getX();
//		PivotY = e.getY();
//		
//		//initialize a new rectangle
//										//(X,  Y,  Width, Height);
//		SelectionRectangle = new Rectangle(PivotX,PivotY,0,0);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isMousePressed = false;
	}

    void eventOutput(String eventDescription, MouseEvent e) {
        String Line = (eventDescription
                + " (" + e.getX() + "," + e.getY() + ")"
                + " detected on "
                + e.getComponent().getClass().getName());
       System.out.println(Line);
    }
    
    public void mouseMoved(MouseEvent e) {
        eventOutput("Mouse moved", e);
    }
    
    public void mouseDragged(MouseEvent e) {
        eventOutput("Mouse dragged", e);
    }

    public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
	}

}
