package haloGUI;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.ContextSetDescription;
import genomeObjects.OrganismSet;
//import importExport.FitxerDades;
import inicial.Dendrograma;
//import inicial.Language;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import contextViewer.DrawGene;

//import methods.Reagrupa;
//import moduls.frm.FrmPrincipalDesk;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Iterator;
import java.awt.Window;

@SuppressWarnings("serial")
public class LoadGenomesPanelv2 extends JLayeredPane implements ActionListener,
		PropertyChangeListener {

	// parent
	private StartFrame sf;
	private GFFChecker gffc;

	// GUI components
	private JLabel Genomes, GeneClusters;
	private JProgressBar progressBar, progressBarClusters;
	private JButton btnLoad, btnClusterLoad, btnSubmit;
	private JTextField GenomeWorkingSetFileName, ClusterFileName;
	private String strGWS = " Genomic Working Set (required)";
	private String strHC = " Homology Clusters (optional)";
	private String strLoad = "Load";
	private String clusterLoad = "Load";
	private String strNoFileLoaded = "No file currently loaded.";
	private String strCancelled = strNoFileLoaded;
	private JButton LoadInfo, ClusterInfo;
	private Font HeaderFont = new Font("Arial", 1, 13);
	private int HeaderPadding = 11;
	private String strInfo = "???";

	// Switches to determine operations able to be performed
	private boolean LoadingGenomeFiles = false;
	private boolean LoadingGeneClusters = false;
	private boolean GenomeWorkingSetLoaded = false;
	private boolean GeneClustersLoaded = false;
	private boolean ReadyToSubmit = false;

	// read in files or directories
	private boolean GenomesAsSingleFile = false;

	// improperly-loaded switches
	private boolean ClusterFileImproperlyLoaded = false;
	private boolean GenomeWorkingSetFileImproperlyLoaded = false;

	// Loaded Organism Set + corresponding information
	private OrganismSet OS;
	private int TotalOrganisms;
	private LinkedList<String> IncludeTypes;
	private LinkedList<String> DisplayOnlyTypes;

	// loaded file names, with path
	private String GenomeWorkingSetFile;
	private String ClustersFile;
	private File ReferenceDirectory;

	// loaded file names no path
	private String GenomeWorkingSetFile_NoPath;
	private String ClustersFile_NoPath;

	// dummy labels for spacing columns
	private JLabel d1, d2, d3, d4, d5;

	private File[] GenomeFiles;

	// ----- Building this frame ----------------------------------//

	// constructor
	public LoadGenomesPanelv2(StartFrame startframe) {
		this.getPanel();
		this.setVisible(true);
		this.sf = startframe;
	}

	// panel components
	public void getPanel() {

		// Define GridBagLayout
		this.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;

		// initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.insets = new Insets(6, 3, 3, 3);

		// dummy labels, to artificially normalize column widths
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d1 = new JLabel(" ");
		d1.setBackground(Color.LIGHT_GRAY);
		d1.setOpaque(false);
		add(d1, c);

		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d2 = new JLabel(" ");
		d2.setBackground(Color.LIGHT_GRAY);
		d2.setOpaque(false);
		add(d2, c);

		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d3 = new JLabel(" ");
		d3.setBackground(Color.LIGHT_GRAY);
		d3.setOpaque(false);
		add(d3, c);

		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d4 = new JLabel(" ");
		d4.setBackground(Color.LIGHT_GRAY);
		d4.setOpaque(false);
		add(d4, c);

		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		d5 = new JLabel(" ");
		d5.setBackground(Color.LIGHT_GRAY);
		d5.setOpaque(false);
		add(d5, c);

		// Genome section heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 5;
		c.ipady = HeaderPadding;
		Genomes = new JLabel(strGWS);
		Genomes.setBackground(Color.LIGHT_GRAY);
		Genomes.setOpaque(true);
		Genomes.setFont(HeaderFont);
		add(Genomes, c);

		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.gridx = 4;
		c.ipady = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.REMAINDER;
		LoadInfo = new JButton(strInfo);
		LoadInfo.addActionListener(this);
		add(LoadInfo, c);
		this.moveToFront(LoadInfo);
		gridy++;

		// Load File button
		c.insets = new Insets(3, 3, 3, 3);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		// c.fill = GridBagConstraints.NONE;
		c.gridy = gridy;
		btnLoad = new JButton(strLoad);
		btnLoad.addActionListener(this);
		add(btnLoad, c);

		// Genomic Working Set File Name
		c.insets = new Insets(3, 3, 3, 3);
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		GenomeWorkingSetFileName = new JTextField();
		GenomeWorkingSetFileName.setText(strNoFileLoaded); // No file loaded
		GenomeWorkingSetFileName.addActionListener(this);
		GenomeWorkingSetFileName.setEditable(false);
		add(GenomeWorkingSetFileName, c);
		// gridy++;

		// loading genomes progress bar
		c.insets = new Insets(3, 3, 3, 3);
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBar = new JProgressBar(0, 100);
		progressBar.setBorderPainted(false);
		progressBar.setStringPainted(false);
		progressBar.setValue(0);
		progressBar.setForeground(Color.BLUE);
		progressBar.setVisible(false);
		add(progressBar, c);
		gridy++;

		// Gene clusters section heading
		c.insets = new Insets(10, 3, 3, 3);
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.ipady = HeaderPadding;
		c.fill = GridBagConstraints.HORIZONTAL;
		GeneClusters = new JLabel(strHC);
		GeneClusters.setBackground(Color.LIGHT_GRAY);
		GeneClusters.setOpaque(true);
		GeneClusters.setFont(HeaderFont);
		add(GeneClusters, c);

		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.gridx = 4;
		c.ipady = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.REMAINDER;
		ClusterInfo = new JButton(strInfo);
		ClusterInfo.addActionListener(this);
		add(ClusterInfo, c);
		this.moveToFront(ClusterInfo);
		// this.add(LoadInfo, 2);
		gridy++;

		// Load Cluster File button
		c.insets = new Insets(3, 3, 3, 3);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		// c.fill = GridBagConstraints.NONE;
		c.gridy = gridy;
		btnClusterLoad = new JButton(clusterLoad);
		btnClusterLoad.addActionListener(this);
		add(btnClusterLoad, c);

		// gene clusters progress bar
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBarClusters = new JProgressBar(0, 100);
		progressBarClusters.setStringPainted(false);
		progressBarClusters.setBorderPainted(false);
		progressBarClusters.setValue(0);
		progressBarClusters.setForeground(Color.BLUE);
		progressBarClusters.setVisible(false);
		add(progressBarClusters, c);
		// gridy++;

		// clusters file name
		c.ipady = 5;
		c.gridx = 1;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		ClusterFileName = new JTextField();
		ClusterFileName.setText(strNoFileLoaded); // No file loaded
		ClusterFileName.setEditable(false);
		add(ClusterFileName, c);
		gridy++;

		// Submit button
		c.insets = new Insets(10, 3, 3, 3);
		c.gridx = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.ipady = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.NONE;
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(this);
		add(btnSubmit, c);

	}

	// Instructions for launching program
	public JTextPane getGenomicWorkingSetInfo() {

		// create a JTextPane + add settings
		JTextPane Instructions = new JTextPane();
		Instructions.setEditable(false);

		// retrieve document, and add styles
		StyledDocument doc = Instructions.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
		Style s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);

		// text into document
		try {
			doc.insertString(doc.getLength(), "Instructions:\n\n",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "A ", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Genomic Working Set",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					" is a collection of annotated genomes. When performing searches in JContextExplorer, "
							+ "JContextExplorer will query all genomes in the loaded genomic working set.\n\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"To load a genomic working set, push the \"load\" button below and either\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"(1) Select a directory containing individual annotated genome files\n",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "or\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"(2) Select a genomic working set file.\n\n",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					"Individual annotated genomes should be formatted in ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"General Feature Format (or .GFF) [version 2],",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					" a standard tab-delimited text file format. GFF files should have the file extension \".gff\". "
							+ "\n\nEach line in the GFF file describes a single annotated feature, and is split into 9 columns."
							+ "  This program only reads in columns ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "1, 3, 4, 5, 7,",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " and ", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "9, ", doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					"which contain the following information:\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 1:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Sequence name\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 3:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Feature Type\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 4:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Feature Start Position\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 5:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Feature End Position\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 7:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Strand\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 9:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Annotation\n\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"If you specify a directory of .GFF files,"
							+ " JContextExplorer will name each genome according to the name of the file. "
							+ "\n\nFor example,", doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"\n/SomeDirectory/CollectionOfGenomes/Organism1.gff",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "\nwill be named ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "\nOrganism1",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					".\nPlease avoid names containing "
							+ "white spaces (instead, use underscores).\n\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"Instead of specifying a directory of .gff files, you may specify a single"
							+ " genomic working set file.  This file must be a 1 or 2-column tab-delimited text file. In the first column,"
							+ " please specify the file path to all annotated genome files you would like to include in your genomic working set."
							+ "  If you do not include a second column, each genome will be named according to the name of the file."
							+ " The optional second column consists of a customized name for each genome.\n\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "WARNING!\n\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"When specifying file paths of individual genome files, please be sure to either specify ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "(1) The absolute path",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " or ", doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					" (2) The path relative to the directory from which JContextExplorer was launched.",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"  JContextExplorer will be unable to import files if the file paths are not correctly specified.\n\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"For additional help and examples, please consult the JContextExplorer manual.",
					doc.getStyle("regular"));
			// doc.insertString(doc.getLength(),
			// "A genomic working set file is either a 1 or 2-column tab-delimited text file.",
			// doc.getStyle("regular"));

			// doc.insertString(doc.getLength(),
			// " is a collection of annotated genomes. When performing searches in JContextExplorer, "
			// +
			// "JContextExplorer will query all genomes in the genomic working set. If you would like to work on "
			// +
			// "a subset of genomes in your genomic working set, please define a new Genomic Working Set containing only "
			// +
			// "your genomes of interest, and launch a new instance of JContextExplorer, using only these genomes.\n\n",
			// doc.getStyle("regular"));

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return Instructions;

	}

	// Instructions for cluster file
	public JTextPane getClusterInfo() {
		// create a JTextPane + add settings
		JTextPane Instructions = new JTextPane();
		Instructions.setEditable(false);

		// retrieve document, and add styles
		StyledDocument doc = Instructions.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
		Style s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);

		// text into document
		try {
			doc.insertString(doc.getLength(), "Instructions:\n\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"Within a single genomic working set, certain annotated features may be "
							+ "homologous to one another.  This may occur both within a single species and across multiple species.  "
							+ "A group of homologous features is often referred to as a ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Homologous Gene Cluster",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), ", or simply a ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Homology Cluster",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					".  Numerous methods exist to detect homology across and within genomes,"
							+ " and to cluster annotated features in a set of genomes into homology cluster groups.  Often, but "
							+ "not necessarily, these homology cluster groups are ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "non-overlapping",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					". That is, each annotated feature may belong to a maximum of one homology cluster.\n\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"For all homology cluster-associated processes, JContextExplorer assumes non-overlapping homology clusters",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), ".\n\n", doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"When JContextExpolorer searches for annotated features in a genomic working set, "
							+ "it may do so either by\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"(1) Matching a textual query to individual genomic feature annotations",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "\nor\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"(2) Matching a homology cluster ID number.\n\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"Textual annotations may be unreliable (especially if a "
							+ "genomic working set contains contains genomes annotated by different groups), so it may be worthwhile to compute homology clusters"
							+ " and load these computed homology clusters into JContextExplorer.",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "\n\nWARNING!\n\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"JContextExplorer cannot compute homology clusters from a set of sequenced genomes, only search a set"
							+ " of pre-computed, loaded homology clusters.\n\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"To load a set of pre-computed homology clusters",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), ", click the ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "load", doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					" button below the banner, and select the appropriate file.  Homology clusters may be defined according to gene name "
							+ "or precise feature coordinates.",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"   All files must be tab-delimited, and ",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"each line in the file "
							+ "describes an individual feature - homology group relationship.",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"  Depending on the number of columns provided, each line is parsed differently.  "
							+ "Lines in the file that do not "
							+ "follow the specifications described below will be ignored.",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"\n\nThere are 5 acceptable line formats:\n\n",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "(1) Five-Column Format\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"If there are 5 tab-delimited entries in the line, entries take on the following values:\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 1:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Genome Name\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 2:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Sequence Name\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 3:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Feature Start Position\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 4:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Feature End Position\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 5:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					" Homology Cluster ID Number\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "If a feature starts at ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Feature Start Position",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " and stops at ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Feature Stop Position",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), ", on the sequence named ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Sequence Name",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), ", in the genome named ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Genome Name",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					", this feature is assigned the provided ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Homology Cluster ID Number",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), ".\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "(2) Four-Column Format\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"If there are 4 tab-delimited entries in the line, entries take on the following values:\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 1:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Genome Name\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 2:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Sequence Name\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 3:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Annotation Key\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 4:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					" Homology Cluster ID Number\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(),
					"If a feature contains the string ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Annotation Key",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					" in it's annotation, and is found on the sequence named ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Sequence Name",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " in the genome named ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Genome Name",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					", this feature is assigned the provided ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Homology Cluster ID Number",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					".\n\nIn the Annotation Key field, please use underscores instead of spaces.\n\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "(3) Three-Column Format\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"If there are 3 tab-delimited entries in the line, entries take on the following values:\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 1:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Genome Name\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 2:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Annotation Key\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 3:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					" Homology Cluster ID Number\n\n", doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"This format is identical to Four-column format, however does not check for agreement in the sequence name.\n\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "(4) Two-Column Format\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"If there are 2 tab-delimited entries in the line, entries take on the following values:\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 1:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " Annotation Key\n",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Column 3:", doc.getStyle("bold"));
			doc.insertString(doc.getLength(),
					" Homology Cluster ID Number\n\n", doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"All features in all genomes in the genomic working set with an annotation that"
							+ " contains the ", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Annotation Key",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), " are assigned the provided ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Homology Cluster ID Number",
					doc.getStyle("bold"));
			doc.insertString(doc.getLength(), ".\n\n", doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "(5) Single Column Format\n",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					"If there is only a single entry in the line, This entry is taken to be the ",
					doc.getStyle("regular"));
			doc.insertString(doc.getLength(), "Annotation Key",
					doc.getStyle("bold"));
			doc.insertString(
					doc.getLength(),
					". All annotated features that contain the annotation key are given a homology cluster ID number,"
							+ " which is determined by the line number in the file.\n\n",
					doc.getStyle("regular"));
			doc.insertString(
					doc.getLength(),
					"For additional help and examples, please consult the JContextExplorer manual.",
					doc.getStyle("regular"));

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return Instructions;
	}

	// All Actions
	@Override
	public void actionPerformed(ActionEvent evt) {

		// retrieve info
		if (evt.getSource().equals(LoadInfo)) {
			new InfoFrame(getGenomicWorkingSetInfo(),
					"Genomic Working Set Info", this, -170);
		}

		if (evt.getSource().equals(ClusterInfo)) {
			new InfoFrame(getClusterInfo(), "Homology Clusters Info", this, 70);
		}

		// specify GFF file format details
		if (evt.getSource().equals(btnLoad)) {
			gffc = new GFFChecker(this);
		}

		// load genome files, after determining GFF file format stuff.
		try {
			if (evt.getSource().equals(gffc.getBtnSubmit())) {

				// set switches to appropriate state
				LoadingGenomeFiles = true;
				GenomeWorkingSetLoaded = false;
				LoadingGeneClusters = false;
				ReadyToSubmit = false;
				GeneClustersLoaded = false;

				// reset clusters
				progressBarClusters.setValue(0);
				progressBarClusters.setStringPainted(false);

				// String fileName = getGenomeWorkingSetFile();
				String fileName = this.getGenomes();

				// System.out.println("fileName: " + fileName);
				if (fileName != null) {

					if (!fileName.equals(GenomeWorkingSetFile)) {

						ClusterFileName.setText(strNoFileLoaded);

						LoadGenomesWorker lg = new LoadGenomesWorker(fileName);
						lg.addPropertyChangeListener(this);
						lg.execute();

					} else {
						GenomeWorkingSetLoaded = true;
						ReadyToSubmit = true;
					}

				} else {

					// set everything back to zero
					progressBar.setValue(0);
					progressBar.setStringPainted(false);
					GenomeWorkingSetFileName.setText(strCancelled);
					ClusterFileName.setText(strCancelled);

					// turn everything off - back to square one
					LoadingGenomeFiles = false;
					GenomeWorkingSetLoaded = false;
					LoadingGeneClusters = false;
					ReadyToSubmit = false;
					GeneClustersLoaded = false;

					GenomeWorkingSetFile = null;
					ClustersFile = null;

				}

			}

		} catch (Exception ex) {
			// System.out.println("Gffc exception.");
			// set everything back to zero
			progressBar.setValue(0);
			progressBar.setStringPainted(false);
			GenomeWorkingSetFileName.setText(strCancelled);
			ClusterFileName.setText(strCancelled);

			// turn everything off - back to square one
			LoadingGenomeFiles = false;
			GenomeWorkingSetLoaded = false;
			LoadingGeneClusters = false;
			ReadyToSubmit = false;
			GeneClustersLoaded = false;

			GenomeWorkingSetFile = null;
			ClustersFile = null;
		}

		// load clusters file
		if (evt.getSource().equals(btnClusterLoad)) {
			LoadingGenomeFiles = false;
			LoadingGeneClusters = true;

			String clusterfileName;// = null;
			if (GenomeWorkingSetLoaded == true) {
				clusterfileName = getClustersFile();
			} else {
				JOptionPane
						.showMessageDialog(
								null,
								"You must load a genomic working set before loading pre-computed gene clusters.",
								"No Valid Genomic Working Set Loaded",
								JOptionPane.ERROR_MESSAGE);
				clusterfileName = null;
			}

			if (clusterfileName != null) {

				if (!clusterfileName.equals(ClustersFile)) {

					LoadClustersWorker lc = new LoadClustersWorker(
							clusterfileName);
					lc.addPropertyChangeListener(this);
					lc.execute();

				}

			} else {

				progressBarClusters.setValue(0);
				progressBarClusters.setStringPainted(false);
				LoadingGenomeFiles = false;
				if (OS != null) {
					OS.setGeneClustersLoaded(false);
				}
				ClusterFileName.setText(strCancelled);
				ClustersFile = null;
				GeneClustersLoaded = false;
			}

		}

		if (evt.getSource().equals(btnSubmit)) {
			// if (evt.getSource().equals(this.getBtnSubmit())){
			if (ReadyToSubmit == true) {

				// close this window
				sf.dispose();

				// open the dendrogram window
				invokeDendrograma();

			} else {
				JOptionPane
						.showMessageDialog(
								null,
								"You must load a genomic working set before proceeding to the main window.",
								"No Valid Genomic Working Set Loaded",
								JOptionPane.ERROR_MESSAGE);
			}

		}

	}

	// ----- Import Data Files ----------------------------------//

	// retrieve a data file
	private String getGenomeWorkingSetFile() {

		// use pre-existing 'FileDialog' GUI window to retrieve file
		final FileDialog fd = new FileDialog(sf, "English", FileDialog.LOAD);

		fd.setVisible(true);
		String GenomeWorkingSetFile = fd.getDirectory() + fd.getFile();
		this.GenomeWorkingSetFile_NoPath = fd.getFile();
		// String GenomeWorkingSetFile = fd.getFile();
		if (fd.getFile() == null) {
			GenomeWorkingSetFile = null;
		}
		GenomesAsSingleFile = true;
		return GenomeWorkingSetFile; // file name
	}

	// retrieve either directory or data file
	private String getGenomes() {

		// initialize output
		JFileChooser GetGenomes = new JFileChooser();
		try {
			// GetGenomes.setLUIManager.getLookAndFeel()
		} catch (Exception ex) {

		}
		GetGenomes.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		GetGenomes
				.setDialogTitle("Select Annotated Genomes Directory or Genome Working Set File");

		if (this.ReferenceDirectory != null) {
			GetGenomes.setCurrentDirectory(ReferenceDirectory);
		} else {
			GetGenomes.setCurrentDirectory(new File("."));
		}
		GetGenomes.showOpenDialog(GetGenomes);

		// retrieve a directory
		// File[] AllFiles = GetGenomes.getSelectedFiles();
		File DirectoryOrGWSFile = GetGenomes.getSelectedFile();
		this.GenomeWorkingSetFile_NoPath = DirectoryOrGWSFile.getName();

		// note current directory for next time
		if (GetGenomes.getCurrentDirectory() != null) {
			this.ReferenceDirectory = GetGenomes.getCurrentDirectory();
		}

		// check if file could be received
		if (DirectoryOrGWSFile != null) {

			// determine if file or directory loaded
			if (DirectoryOrGWSFile.isDirectory()) {

				// retrieving info as a directory.
				this.GenomesAsSingleFile = false;

				// retrieve directory
				this.GenomeFiles = DirectoryOrGWSFile.listFiles();

			} else {

				// all information stored in a single genome working set file.
				this.GenomesAsSingleFile = true;

			}
		}

		// System.out.println(DirectoryOrGWSFile.getAbsolutePath());
		// for (int i = 0; i < GenomeFiles.length; i++){
		// System.out.println(GenomeFiles[i]);
		// }

		// return the information.
		return DirectoryOrGWSFile.getAbsolutePath();
	}

	// retrieve clusters file
	private String getClustersFile() {

		// use pre-existing 'FileDialog' GUI window to retrieve file
		final FileDialog fd = new FileDialog(sf, "English", FileDialog.LOAD);

		// set reference directory to match annotated genomes directory
		if (ReferenceDirectory != null) {
			fd.setDirectory(this.ReferenceDirectory.getAbsolutePath());
		}

		fd.setVisible(true);
		String ClustersFile = fd.getDirectory() + fd.getFile();
		this.ClustersFile_NoPath = fd.getFile();
		// String GenomeWorkingSetFile = fd.getFile();
		if (fd.getFile() == null) {
			GenomeWorkingSetFile = null;
			return null;
		} else {
			return ClustersFile; // file name
		}

	}

	// ----- SwingWorker-related ----------------------------------//

	// Perform File Loading + Operon computation tasks
	class LoadGenomesWorker extends SwingWorker<Void, Void> {

		public LoadGenomesWorker(String filename) {
			GenomeWorkingSetFile = filename;
		}

		@Override
		protected Void doInBackground() throws Exception {

			// disable all buttons
			btnLoad.setEnabled(false);
			btnClusterLoad.setEnabled(false);
			btnSubmit.setEnabled(false);

			// LOAD GENOME SET
			GenomeWorkingSetFileName.setVisible(false);
			progressBar.setVisible(true);
			int progress = 0;
			setProgress(progress);
			progressBar.setStringPainted(true);

			// import
			OS = new OrganismSet();
			OS.setIncludeTypes(IncludeTypes);
			OS.setDisplayOnlyTypes(DisplayOnlyTypes);
			int OrganismsCompleted = 0;

			// define a new linked list, for each annotated genome
			LinkedHashMap<String, AnnotatedGenome> Species = new LinkedHashMap<String, AnnotatedGenome>();

			// define a new list, for each species name
			LinkedList<String> SpeciesNames = new LinkedList<String>();

			// import a single genomic working set file
			if (GenomesAsSingleFile) {

				// determine number of total organisms from the single file
				TotalOrganisms = OS
						.determineNumberOfSpecies(GenomeWorkingSetFile);

				try {
					// import buffered reader
					BufferedReader br = new BufferedReader(new FileReader(
							GenomeWorkingSetFile));
					String Line = null;

					while ((Line = br.readLine()) != null) {

						// if a line or two in the file are incorrectly
						// formatted, no worries.
						try {
							// initialize species name
							String SpeciesName;

							// parse input
							String[] ImportedLine = Line.split("\t");

							// create a new AnnotatedGenome
							AnnotatedGenome AG = new AnnotatedGenome();
							AG.setIncludeTypes(IncludeTypes);
							AG.setDisplayOnlyTypes(DisplayOnlyTypes);

							// middle line is the sequence line
							if (ImportedLine.length == 3) {

								// Annotation information
								AG.importElements(ImportedLine[0]);

								// reference to genome file
								AG.setGenomeFile(new File(ImportedLine[1]));

								// set species name
								SpeciesName = ImportedLine[2];

							} else if (ImportedLine.length > 1) { // first =
																	// annotation
																	// file,
																	// last =
																	// species
																	// name
								AG.importElements(ImportedLine[0]);
								SpeciesName = ImportedLine[(ImportedLine.length - 1)];
								AG.setGenomeFile(new File(""));
							} else {

								// import elements
								AG.importElements(ImportedLine[0]);

								// retrieve species name
								String SpeciesNameElements[] = ImportedLine[0]
										.split("/");
								String SpecName[] = SpeciesNameElements[SpeciesNameElements.length - 1]
										.split(".gff");
								SpeciesName = SpecName[0];

								// genome file
								AG.setGenomeFile(new File(""));
							}

							// set species name
							AG.setSpecies(SpeciesName);

							// Genus name
							String SpeciesAndGenus[] = SpeciesName.split("_");
							AG.setGenus(SpeciesAndGenus[0]);

							// add Context set
							AG.MakeSingleGeneContextSet("SingleGene");

							// add to hash map
							Species.put(SpeciesName, AG);

							// add name to array of species
							SpeciesNames.add(SpeciesName);

						} catch (Exception ex) {
						}

						// update progress bar
						OrganismsCompleted++;
						progress = (int) Math
								.round(100 * ((double) OrganismsCompleted / (double) TotalOrganisms));
						setProgress(progress);
						// progressBar.setValue(progress);
					}
					br.close();

					// save results to OS structure.
					// imported data
					OS.setSpecies(Species);
					OS.setSpeciesNames(SpeciesNames);

					// context set information descriptions in OS
					LinkedList<ContextSetDescription> CSD = new LinkedList<ContextSetDescription>();
					ContextSetDescription Initial = new ContextSetDescription();
					Initial.setName("SingleGene");
					Initial.setPreprocessed(true);
					Initial.setType("IntergenicDist");
					CSD.add(Initial);
					OS.setCSDs(CSD);

					progressBar.setValue(100);
					progressBar.setVisible(false);

					GenomeWorkingSetFileName.setVisible(true);
					GenomeWorkingSetFileName
							.setText(GenomeWorkingSetFile_NoPath);

				} catch (Exception ex) {
					progressBar.setStringPainted(false);
					progressBar.setValue(0);
					GenomeWorkingSetFileImproperlyLoaded = true;
					JOptionPane
							.showMessageDialog(
									null,
									"The file could not be loaded or was improperly formatted.",
									"Invalid File Format",
									JOptionPane.ERROR_MESSAGE);
					GenomeWorkingSetFileName.setText(strCancelled);

				}

			} else {

				try {

					// determine number of total organisms
					TotalOrganisms = 0;
					for (File f : GenomeFiles) {
						if (f.getName().contains(".gff")) {
							TotalOrganisms++;
						}
					}

					// retrieve all files
					for (File f : GenomeFiles) {
						if (f.getName().contains(".gff")) {

							// new annotated genome
							AnnotatedGenome AG = new AnnotatedGenome();

							// set appropriate types to import
							AG.setIncludeTypes(IncludeTypes);
							AG.setDisplayOnlyTypes(DisplayOnlyTypes);

							// Annotation information
							AG.importElements(f.getAbsolutePath());

							// reference to genome file
							AG.setGenomeFile(f);

							// Species Name + genus
							String[] SpeciesName = f.getName().split(".gff");
							String TheName = SpeciesName[0];
							AG.setSpecies(TheName);

							String[] Genus = SpeciesName[0].split("_");
							String TheGenus = Genus[0];
							AG.setGenus(TheGenus);

							// add Context set
							AG.MakeSingleGeneContextSet("SingleGene");

							// add to hash map
							Species.put(TheName, AG);

							// add name to array of species
							SpeciesNames.add(TheName);

							// update progress bar
							OrganismsCompleted++;
							progress = (int) Math
									.round(100 * ((double) OrganismsCompleted / (double) TotalOrganisms));
							setProgress(progress);
						}
					}

					// save results to OS structure.
					// imported data
					OS.setSpecies(Species);
					OS.setSpeciesNames(SpeciesNames);

					// context set information descriptions in OS
					LinkedList<ContextSetDescription> CSD = new LinkedList<ContextSetDescription>();
					ContextSetDescription Initial = new ContextSetDescription();
					Initial.setName("SingleGene");
					Initial.setPreprocessed(true);
					Initial.setType("IntergenicDist");
					CSD.add(Initial);
					OS.setCSDs(CSD);

					progressBar.setValue(100);
					progressBar.setVisible(false);

					GenomeWorkingSetFileName.setVisible(true);
					GenomeWorkingSetFileName
							.setText(GenomeWorkingSetFile_NoPath);

				} catch (Exception ex) {
					progressBar.setStringPainted(false);
					progressBar.setValue(0);
					GenomeWorkingSetFileImproperlyLoaded = true;
					JOptionPane
							.showMessageDialog(
									null,
									"The file could not be loaded or was improperly formatted.",
									"Invalid File Format",
									JOptionPane.ERROR_MESSAGE);
					GenomeWorkingSetFileName.setText(strCancelled);
				}

			}
			return null;
		}

		public void done() {

			// adjust switches
			LoadingGenomeFiles = false;
			GenomeWorkingSetLoaded = true;
			LoadingGeneClusters = false;
			GeneClustersLoaded = false;
			ReadyToSubmit = true;

			// adjust buttons
			btnLoad.setEnabled(true);
			btnClusterLoad.setEnabled(true);
			btnSubmit.setEnabled(true);

			if (ClusterFileImproperlyLoaded == true) {
				ClusterFileName.setVisible(true);
				ClusterFileName.setText(strNoFileLoaded);
			}
			ClusterFileImproperlyLoaded = false;
			if (GenomeWorkingSetFileImproperlyLoaded == true) {
				GenomeWorkingSetFileName.setVisible(true);
				GenomeWorkingSetFileName.setText(strNoFileLoaded);
			}
			GenomeWorkingSetFileImproperlyLoaded = false;

			// all progress bars are made invisible
			progressBar.setVisible(false);
			progressBarClusters.setVisible(false);

		}

	}

	// Load homology clusters
	class LoadClustersWorker extends SwingWorker<Void, Void> {

		public LoadClustersWorker(String filename) {
			ClustersFile = filename;
		}

		@Override
		protected Void doInBackground() throws Exception {

			// disable all buttons
			btnLoad.setEnabled(false);
			btnClusterLoad.setEnabled(false);
			btnSubmit.setEnabled(false);

			if (GenomeWorkingSetLoaded == true) {

				ClusterFileName.setVisible(false);
				progressBarClusters.setVisible(true);
				progressBarClusters.setStringPainted(true);
				progressBarClusters.setValue(0);

				int LineCounter = 0;
				int clusterProgress = 0;
				setProgress(clusterProgress);

				try {

					// First: count lines in the file
					// import buffered reader
					BufferedReader br_count = new BufferedReader(
							new FileReader(ClustersFile));
					int TotalLines = 0;

					// count lines
					while (br_count.readLine() != null) {
						TotalLines++;
					}

					// Second: import/process lines in the file
					// import buffered reader
					BufferedReader br = new BufferedReader(new FileReader(
							ClustersFile));
					String Line = null;
					int ClusterNumCounter = 0;

					while ((Line = br.readLine()) != null) {

						// import each line
						String[] ImportedLine = Line.split("\t");

						// increment cluster counter.
						ClusterNumCounter++;

						// try to parse every line
						try {
							// Gene Name
							if (ImportedLine.length == 1) {

								// add cluster number
								for (AnnotatedGenome AG : OS.getSpecies()
										.values()) {
									AG.addClusterNumber(
											ImportedLine[0].replace("_ ", " "),
											ClusterNumCounter);
								}

								// largest cluster designation is always the
								// last
								OS.LargestCluster = TotalLines;

								// Gene Name - Cluster Number
							} else if (ImportedLine.length == 2) {

								// recover bioinfo
								int GeneClusterNum = Integer
										.parseInt(ImportedLine[1]);

								// set largest cluster number
								if (OS.LargestCluster < GeneClusterNum) {
									OS.LargestCluster = GeneClusterNum;
								}

								// add cluster number
								for (AnnotatedGenome AG : OS.getSpecies()
										.values()) {
									AG.addClusterNumber(
											ImportedLine[0].replace("_", " "),
											GeneClusterNum);
								}

								// Organism - Gene Name - Cluster Number
							} else if (ImportedLine.length == 3) {

								// recover bioinfo
								int GeneClusterNum = Integer
										.parseInt(ImportedLine[2]);

								// set largest cluster number
								if (OS.LargestCluster < GeneClusterNum) {
									OS.LargestCluster = GeneClusterNum;
								}

								// add cluster number
								OS.getSpecies()
										.get(ImportedLine[0])
										.addClusterNumber(
												ImportedLine[1].replace("_",
														" "), GeneClusterNum);

								// Organism - Contig - Gene Name - Cluster
								// Number
							} else if (ImportedLine.length == 4) {

								// recover bioinfo
								int GeneClusterNum = Integer
										.parseInt(ImportedLine[3]);

								// set largest cluster number
								if (OS.LargestCluster < GeneClusterNum) {
									OS.LargestCluster = GeneClusterNum;
								}

								// add cluster number
								OS.getSpecies()
										.get(ImportedLine[0])
										.addClusterNumber(
												ImportedLine[1],
												ImportedLine[2].replace("_",
														" "), GeneClusterNum);

								// Organism - Contig - Gene Start - Gene Stop -
								// Cluster Number
							} else if (ImportedLine.length == 5) {

								// recover bioinfo
								int GeneStart = Integer
										.parseInt(ImportedLine[2]);
								int GeneStop = Integer
										.parseInt(ImportedLine[3]);
								int GeneClusterNum = Integer
										.parseInt(ImportedLine[4]);

								// set largest cluster number
								if (OS.LargestCluster < GeneClusterNum) {
									OS.LargestCluster = GeneClusterNum;
								}

								// add cluster number
								OS.getSpecies()
										.get(ImportedLine[0])
										.addClusterNumber(ImportedLine[1],
												GeneStart, GeneStop,
												GeneClusterNum);

							} else {
								throw new Exception();
							}
						} catch (Exception ex) {
						}

						// report to SwingWorker
						LineCounter++;

						// update progress
						clusterProgress = (int) Math
								.round(100 * ((double) LineCounter / (double) TotalLines));
						setProgress(clusterProgress);

					}

					// set status of 'gene clusters loaded' to true
					OS.setGeneClustersLoaded(true);
					ClusterFileImproperlyLoaded = false;
					progressBarClusters.setVisible(false);
					ClusterFileName.setVisible(true);
					ClusterFileName.setText(ClustersFile_NoPath);

				} catch (Exception ex) {

					progressBarClusters.setStringPainted(false);

					JOptionPane
							.showMessageDialog(
									null,
									"The file could not be loaded or was improperly formatted.",
									"Invalid File Format",
									JOptionPane.ERROR_MESSAGE);

					ClusterFileImproperlyLoaded = true;
					LoadingGeneClusters = false;
					OS.setGeneClustersLoaded(false);
					ClusterFileName.setText(strCancelled);
				}

			} else {
				JOptionPane
						.showMessageDialog(
								null,
								"You must load a genomic working set before loading homologous gene clusters.",
								"No Valid Genomic Working Set Loaded",
								JOptionPane.ERROR_MESSAGE);
			}

			return null;
		}

		public void done() {

			// adjust switches
			LoadingGenomeFiles = false;
			GenomeWorkingSetLoaded = true;
			LoadingGeneClusters = false;
			GeneClustersLoaded = true;
			ReadyToSubmit = true;

			// adjust buttons
			btnLoad.setEnabled(true);
			btnClusterLoad.setEnabled(true);
			btnSubmit.setEnabled(true);

			// all progress bars are made invisible, all files visible
			progressBar.setVisible(false);
			progressBarClusters.setVisible(false);
			ClusterFileName.setVisible(true);
		}
	}

	// scroll bar signaling
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress")

			// adjust either loading genomes progress bar or loading clusters
			// progress bar
			if (GenomeWorkingSetLoaded == false) {
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			} else {
				int progress = (Integer) evt.getNewValue();
				progressBarClusters.setValue(progress);
			}
	}

	// ----- Getters + Setters ----------------------------------//

	public JButton getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(JButton btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

	public LinkedList<String> getIncludeTypes() {
		return IncludeTypes;
	}

	public void setIncludeTypes(LinkedList<String> includeTypes) {
		IncludeTypes = includeTypes;
	}

	public LinkedList<String> getDisplayOnlyTypes() {
		return DisplayOnlyTypes;
	}

	public void setDisplayOnlyTypes(LinkedList<String> displayOnlyTypes) {
		DisplayOnlyTypes = displayOnlyTypes;
	}

	// ----- Launch main frame ----------------------------------//

	// create a new dendrogram window, with the loaded OS
	public void invokeDendrograma() {

		// System.out.println("Breakpoint!");

		new Dendrograma(OS);

		// //optional print statements (working)
		// System.out.println(OS.getSpeciesNames());
		// System.out.println(OS.getSpecies().get("Haloarcula_amylolytica").getGroupings().get(0).getName());
	}
}