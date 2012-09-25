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

package moduls.frm.Panels;

import genomeObjects.CSDisplayData;
import importExport.DadesExternes;
import importExport.FitxerDades;
import inicial.Language;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import methods.Reagrupa;
import moduls.frm.FrmInternalFrame;
import moduls.frm.FrmPrincipalDesk;
import moduls.frm.InternalFrameData;
import moduls.frm.children.FrmPiz;
import parser.Fig_Pizarra;
import tipus.Orientation;
import tipus.metodo;
import tipus.tipusDades;
import definicions.Config;
import definicions.MatriuDistancies;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * Load and Update buttons
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class Jpan_btn extends JPanel implements ActionListener,
		InternalFrameListener, PropertyChangeListener {

// ----- Fields -----------------------------------------------//
	
	private static final long serialVersionUID = 1L;

	// Desktop where the dendrogram is to be shown
	private final FrmPrincipalDesk fr;

	//this button
	protected Jpan_btn jb;

	// Text to show in the buttons
	private String strLoad, strUpdate;

	// Load and update buttons
	private static JButton btnLoad, btnUpdate;

	// Indicates if the buttons Load or Update are being clicked
	public static boolean buttonClicked = false;

	// Indicate if the text fields have correct values
	public static boolean precisionCorrect = false;
	public static boolean axisMinCorrect = false;
	public static boolean axisMaxCorrect = false;
	public static boolean axisSeparationCorrect = false;
	public static boolean axisEveryCorrect = false;
	public static boolean axisDecimalsCorrect = false;

	// Internal frame currently active
	private FrmInternalFrame currentInternalFrame = null;

	// File with the input data
	private static FitxerDades fitx = null; //path to file
	private DadesExternes de; //ACTUAL DATA -> must be imported from file

	// Text box for the file name
	private static JTextField txtFileName;

	// MultiDendrogram
	private MatriuDistancies multiDendro = null;

	// Progress bar for MultiDendrogram computation
	private JProgressBar progressBar;

	//Search bar + Search submit buttons
	private JTextField searchField;
	private JButton submitSearch;

// ----- New Fields --------------------------------------------//	
	
	//These fields modify the new scrollable tree
	
	private int HorizontalScrollBuffer = 30;
	private int VerticalScrollValue = 1500;
	
// ----- Methods -----------------------------------------------//	
	
	// Swing Worker MultiDendrogram computation
 	class MDComputation extends SwingWorker<Void, Void> {
		private final String action;
		private final tipusDades typeData;
		private final metodo method;
		private final int precision;
		private final int nbElements;
		private double minBase;

		public MDComputation(final String action, final tipusDades typeData,
				final metodo method, final int precision, final int nbElements,
				double minBase) {
			this.action = action;
			this.typeData = typeData;
			this.method = method;
			this.precision = precision;
			this.nbElements = nbElements;
			this.minBase = minBase;
//			System.out.println("Step 2");
		}

		@Override
		public Void doInBackground() {
//			System.out.println("Step 2.5");
			Reagrupa rg;
			MatriuDistancies mdNew;
			double b;
			int progress;

			// Initialize progress property
			progress = 0;
			setProgress(progress);
			while (multiDendro.getCardinalitat() > 1) {
				try {
					
					//CLUSTERING FROM DISTANCES DATA
					rg = new Reagrupa(multiDendro, typeData, method, precision);
					mdNew = rg.Recalcula();
					
					//SET THE CURRENT MULTIDENDROGRAM TO THE RESULT FROM RG.RECALCULA()
					multiDendro = mdNew;
					
					b = multiDendro.getArrel().getBase();
					if ((b < minBase) && (b != 0)) {
						minBase = b;
					}
					progress = 100
							* (nbElements - multiDendro.getCardinalitat())
							/ (nbElements - 1);
					setProgress(progress);
				} catch (final Exception e) {
					showError(e.getMessage());
				}
			}
			return null;
		}

		@Override
		public void done() {
//			System.out.println("Step 3");
			multiDendro.getArrel().setBase(minBase);
			showCalls(action);
			progressBar.setString("");
			progressBar.setBorderPainted(false);
			progressBar.setValue(0);
			fr.setCursor(null); // turn off the wait cursor
		}
	}

	public Jpan_btn(final FrmPrincipalDesk fr) {
		super();
		this.fr = fr;
		this.jb = this;
		this.getPanel();
		this.setVisible(true);
	}

	private void getPanel() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder(Language.getLabel(20))); // File
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;

		// btn load
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		strLoad = Language.getLabel(21); // Load
		btnLoad = new JButton(strLoad);
		btnLoad.addActionListener(this);
		add(btnLoad, c);
		
		// btn update
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		strUpdate = Language.getLabel(110); // Update
		btnUpdate = new JButton(strUpdate);
		btnUpdate.addActionListener(this);
		btnUpdate.setEnabled(false);
		add(btnUpdate, c);
		gridy++;

		// txt file name
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		txtFileName = new JTextField();
		txtFileName.setText(Language.getLabel(112)); // No file loaded
		txtFileName.addActionListener(this);
		txtFileName.setEditable(false);
		add(txtFileName, c);
		gridy++;

		// progress bar
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		progressBar.setBorderPainted(false);
		progressBar.setValue(0);
		add(progressBar, c);
		gridy++;
		
		// Searchable text
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		searchField = new JTextField();
		searchField.setText(""); // Enter search bar
		searchField.addActionListener(this);
		searchField.setEditable(true);
		add(searchField, c);
		gridy++;
		
		//Submit search
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		submitSearch = new JButton("Submit Search");
		submitSearch.addActionListener(this);
		add(submitSearch, c);
	}

	public static void enableUpdate() {
		if (precisionCorrect && axisMinCorrect && axisMaxCorrect
				&& axisSeparationCorrect && axisEveryCorrect
				&& axisDecimalsCorrect) {
			btnUpdate.setEnabled(true);
		} else {
			btnUpdate.setEnabled(false);
		}
	}

	public static String getFileNameNoExt() {
		String name = "";
		if (fitx != null) {
			name = fitx.getNomNoExt();
		}
		return name;
	}

	public static void setFileName(String name) {
		txtFileName.setText(name);
	}

	public MatriuDistancies getMatriu() {
		return de.getMatriuDistancies();
	}

	// BUTTONS PUSHED -> LOAD FILE OR UPDATE TREE
	@Override
	public void actionPerformed(final ActionEvent evt) {
		String action = null;
		FitxerDades fitxTmp;
		boolean ambDades = false;
		InternalFrameData ifd;
		double minBase;
		MDComputation mdComputation;

//		System.out.println("Step 1");
		
		//DETERMINE ACTION
		if (evt.getActionCommand().equals(strLoad)) {
			// LOAD
			buttonClicked = true;
			action = "Load";
			
			// Load data from file
			if (fitx == null) {
				/*
				 * get the data file - the 'getFitxerDades()' method calls the
				 * built-in GUI file retrieval window 'FileDialog'
				 */
				fitxTmp = getFitxerDades();
			} else {
				// Last directory
				fitxTmp = getFitxerDades(fitx.getPath());
			}
			if (fitxTmp == null) {
				// Cancel pressed
				ambDades = false;
			} else {
				fitx = fitxTmp;
				ambDades = true;
			}
			
		} else if (evt.getActionCommand().equals("Submit Search")) {
			
			//SUBMIT BUTTON
			buttonClicked = true;
			action = "Load";
			
			if (searchField.getText().equals("a")) {
			
				fitx = new FitxerDades();
				fitx.setNom("SmallHexokinase.txt");
				fitx.setPath("C:/Research/ECRON_lists/");	

			} else if (searchField.getText().equals("b")){

				fitx = new FitxerDades();
				fitx.setNom("LargeMatrix.txt");
				fitx.setPath("C:/Research/ECRON_lists/");	
				
			} else {
				
				fitx = new FitxerDades();
				fitx.setNom("TestMatrix2.txt");
				fitx.setPath("C:/Research/ECRON_lists/");
				
			}
	
			ambDades = true; //ambDades = "with data"
			
			//System.out.println(fitx.getPath());
		} else if (evt.getActionCommand().equals(strUpdate)) {
			// UPDATE
			buttonClicked = true;
			ifd = currentInternalFrame.getInternalFrameData();
			if ((Jpan_Menu.getTypeData() == ifd.getTypeData())
					&& (Jpan_Menu.getMethod() == ifd.getMethod())
					&& (Jpan_Menu.getPrecision() == ifd.getPrecision())) {
				action = "Redraw";
			} else {
				action = "Reload";
			}
			ambDades = true;
		}
		
		//CARRY OUT ACTION
		if (ambDades && (action.equals("Load") || action.equals("Reload"))) {
			try {
				/*
				 * KEY - DADESEXTERNES converts a data file (bunch of strings) to
				 * internal mathematical objects used in processing
				 */
				de = new DadesExternes(fitx);
				/*
				 * 
				 */
				if (action.equals("Load")) {
					Jpan_Menu.setPrecision(de.getPrecisio());
					
					//changing this value changes the computation
					//places after decimal is a function of the precision.
					//Jpan_Menu.setPrecision(2);
				}
				multiDendro = null;
				try {
					multiDendro = de.getMatriuDistancies();
					minBase = Double.MAX_VALUE;
					progressBar.setBorderPainted(true);
					progressBar.setString(null);
					fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					// Instances of javax.swing.SwingWorker are not reusable,
					// so we create new instances as needed.
					mdComputation = new MDComputation(action,
							Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
							Jpan_Menu.getPrecision(),
							multiDendro.getCardinalitat(), minBase);
					mdComputation.addPropertyChangeListener(this);
					mdComputation.execute();
					
				} catch (final Exception e2) {
					buttonClicked = false;
					showError(e2.getMessage());
				}
			} catch (Exception e1) {
				buttonClicked = false;
				//showError(e1.getMessage());
				showError("Who let the dogs out?");
			}
		} else if (ambDades && action.equals("Redraw")) {
			showCalls(action);
		} else {
			buttonClicked = false;
		}
	}

	private void showCalls(final String action) {
//		System.out.println("Step 4");
		if (action.equals("Reload") || action.equals("Redraw")) {
			currentInternalFrame.doDefaultCloseAction();
		}
		show(action, Jpan_Menu.getMethod(), Jpan_Menu.getPrecision());
		currentInternalFrame.doDefaultCloseAction();
		show(action, Jpan_Menu.getMethod(), Jpan_Menu.getPrecision());
		txtFileName.setText(fitx.getNom());
		btnUpdate.setEnabled(true);
		buttonClicked = false;
	}

	public void show(String action, final metodo method, final int precision) {
//		System.out.println("Step 5");
		boolean isUpdate;
		FrmInternalFrame pizarra;
		Config cfg;
		InternalFrameData ifd;
		FrmPiz fPiz;
		Fig_Pizarra figPizarra;

		isUpdate = !action.equals("Load");
		try {

			pizarra = fr.createInternalFrame(isUpdate, method.name());
			cfg = fr.getConfig();
			cfg.setPizarra(pizarra);
			cfg.setFitxerDades(fitx);
			cfg.setMatriu(multiDendro);
			cfg.setHtNoms(de.getTaulaNoms()); //table names

			//determine size of tree rendering based on number of elements
			setVerticalScrollValue(de.getTaulaNoms().size());
			
			if (!cfg.isTipusDistancia()) {
				if (cfg.getOrientacioDendo().equals(Orientation.NORTH)) {
					cfg.setOrientacioDendo(Orientation.SOUTH);
				} else if (cfg.getOrientacioDendo().equals(Orientation.SOUTH)) {
					cfg.setOrientacioDendo(Orientation.NORTH);
				} else if (cfg.getOrientacioDendo().equals(Orientation.EAST)) {
					cfg.setOrientacioDendo(Orientation.WEST);
				} else if (cfg.getOrientacioDendo().equals(Orientation.WEST)) {
					cfg.setOrientacioDendo(Orientation.EAST);
				}
			}
			ifd = new InternalFrameData(fitx, multiDendro);
			pizarra.setInternalFrameData(ifd);
			
			// Title for the child window
			pizarra.setTitle(fitx.getNom() + " - " + pizarra.getTitle());

			
			//WARNING - modified constructor to avoid problems 8/2/2012
			//create a new figure frame
			CSDisplayData CSD = new CSDisplayData();
			fPiz = new FrmPiz(fr, CSD);
			
			// Set sizes
			fPiz.setSize(pizarra.getSize());
			fPiz.setPreferredSize(pizarra.getSize());
			
			//determine appropriate rendering dimensions
			Dimension d = new Dimension(pizarra.getWidth()-
					HorizontalScrollBuffer, VerticalScrollValue);
			
			fPiz.setPreferredSize(d);
			
			// Call Jpan_Menu -> internalFrameActivated()
			pizarra.setVisible(true);
			if (action.equals("Load") || action.equals("Reload")) {
				Jpan_Menu.ajustaValors(cfg);
			}
			
			// Current internal frame is the activated frame.
			fr.setCurrentFrame(pizarra);
			
			// Convert tree into figures
			figPizarra = new Fig_Pizarra(multiDendro.getArrel(), cfg);
			
			// Pass figures to the window
			fPiz.setFigures(figPizarra.getFigures());
			fPiz.setConfig(cfg);
			
			//scroll panel, with sizes
			JScrollPane fPizSP = new JScrollPane(fPiz);
			fPizSP.setSize(pizarra.getSize());
			fPizSP.setPreferredSize(pizarra.getSize());

			//unused options
			//fPizSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			//fPizSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			//pizarra.add(fPiz);
			pizarra.add(fPizSP);
			
		} catch (final Exception e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
	}

	private FitxerDades getFitxerDades() {
		return this.getFitxerDades(System.getProperty("user.dir"));
	}

	//DATA FILE
	private FitxerDades getFitxerDades(final String sPath) {
		
		//use pre-existing 'FileDialog' GUI window to retrieve file
		final FileDialog fd = new FileDialog(fr, Language.getLabel(9),
				FileDialog.LOAD);
		FitxerDades fitx; //Data file type is just a bunch of relevant strings

		fitx = new FitxerDades();
		fd.setDirectory(sPath);
		fd.setVisible(true);
		if (fd.getFile() == null) {
			fitx = null;
		} else {
			fitx.setNom(fd.getFile());
			fitx.setPath(fd.getDirectory());
		}
		return fitx; //A bunch of strings relating to the file information.
	}

	private void showError(final String msg) {
		JOptionPane.showMessageDialog(null, msg, Language.getLabel(7),
				JOptionPane.ERROR_MESSAGE);
	}

	//Interal Frame - related methods
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		InternalFrameData ifd;

		currentInternalFrame = (FrmInternalFrame) e.getSource();
		btnUpdate.setEnabled(true);
		if (!buttonClicked) {
			fr.setCurrentFrame(currentInternalFrame);
			ifd = currentInternalFrame.getInternalFrameData();
			de = ifd.getDadesExternes();
			fitx = de.getFitxerDades();
			setFileName(fitx.getNom());
			multiDendro = ifd.getMultiDendrogram();
			Jpan_Menu.setConfigPanel(ifd);
		}
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		FrmInternalFrame.decreaseOpenFrameCount();
		btnUpdate.setEnabled(false);
		txtFileName.setText(Language.getLabel(112)); // No file loaded
		if (!buttonClicked) {
			Jpan_Menu.clearConfigPanel();
		}
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress") {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	

// generated methods - automatically detect scroll value
	
	public int getVerticalScrollValue() {
		return VerticalScrollValue;
	}
	

	public void setVerticalScrollValue(int numberOfEntries) {
		VerticalScrollValue = 15*numberOfEntries + 250;
	}

}
