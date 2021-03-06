package contextViewer;

import genomeObjects.AnnotatedGenome;
import genomeObjects.CSDisplayData;
import genomeObjects.GenomicElement;
import genomeObjects.GenomicElementAndQueryMatch;
import genomeObjects.MotifGroup;
import genomeObjects.SequenceMotif;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import moduls.frm.ContextLeaf;
import moduls.frm.Panels.Jpan_btn_NEW;

import org.biojava3.core.sequence.Strand;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

public class RenderedGenomesPanel extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	//fields
	//biological + parent info
	private GenomicSegment[] GS; 		//Genomic segment information
	private mainFrame mf;				//Genomes + other biological information
	private RenderedGenomesPanel rgp;	//this;
	private HashMap<String, LinkedList<GenomicElementAndQueryMatch>> contexts;			//Contexts
	private HashMap<String, String> SourceSpecies;	//Species Names
	private HashMap<String, HashSet<String>> SourceContigs;	//Contig Names
	private String ECRONType; 			//Either "annotation" or "cluster"
	
	//Range-limited related
	private int RangeLimit = 50000;		//Do not display a genomic region of this or more
	private int SplitLimit = 50000;		//segment contexts when they are further away from this value.
	private LinkedList<String> ExceededRangeLimit;	//nodes that are excluded
	private boolean ContextsExcluded = false; 		//initially, none are excluded
	
	//holding of split values
	private LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> SplitContexts;
	private LinkedHashMap<String, String> SplitSpeciesNames;
	private LinkedHashMap<String, HashSet<String>> SplitContigNames;
	
	//display - related
	private int GenomicDisplayRange;	//nt range to display on each GS
	private int CoordinateBarEvery; 	//nt block to display
	private int BeforeBuffer;	//nt range before actual context range
	private int AfterBuffer;	//nt range after actual context range
	private double CenterScaleValue; 	//fraction of whole range where center of CS occurs
	private double genome2displayRatio;	//gene coordinate * <this> = xcoordinate to display
	
	//export image stuff
	private JPopupMenu ExportMenu;	//export frame information
	private Point PlaceClicked;		//essential for export frame
	
	//Display dimensions information
	private Dimension dim;			//Overall dimensions of display area
	private int GSHeight = 50; 	//height of genomic segment
	private int GSSpacer = 30;		//vertical space between segments
	private double GSStartFromLeft = 0.05;
	private double GSSBufferOnRight = 0.05;
	private int GenomeLineThickness = 2;
	private int WholeWidthBuffer = 30;
	private int GSWidth;
	private int CoordinateBarWidth = 1;
	private int ContigBoundaryWidth = 2;
	private int ArrowLength = 20;
	private int ArrowHeight = 10;
	private int LabelAboveGS = 5;
	
	//motifs
	private boolean Motifs2Scale = false;
	private int MotifWidthMinimum = (int) (GSHeight/6);
	
	//boolean variables for check box repainting
	private boolean ShowSurrounding = true;
	private boolean ColorSurrounding = false;
	private boolean StrandNormalize = true;
	private boolean ShowCoordinates = true;

	//mouse-related info
	private boolean GeneInformationIsBeingDisplayed = false;
	private JFrame GeneInfo;
	private GeneColorLegendFrame gclf;
	private GeneColorLegendPanel gclp;
	private int FrameMoveDown = 38;
	private int FrameMoveRight = 20;
	private boolean ShowAnnotation = false;
	private boolean ShowType = false;
	private boolean ShowStart=  false;
	private boolean ShowStop = false;
	private boolean ShowSize = false;
	private boolean ShowClusterID = false;
	private int CharacterMax = 20;
	private boolean HomologyGroupSelected = false;
	private DrawObject CurrentMiddleClickedDrawObject;
	private DrawObject CurrentLeftClickedDrawObject;
	private boolean ClickedOnLegend = false;
	
	//legend panel info
	private LinkedList<Color> CurrentlySelectedGeneColors;
	private LinkedList<SharedHomology> GeneColorList;
	private LinkedList<SharedHomology> DisplayedGeneColorList;
	private LinkedList<SharedHomology> MotifColorList;
	private LinkedList<SharedHomology> DisplayedMotifColorList;
	
	private int OffSetPoint = 0;
	
	//formatting information
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	final FontRenderContext renderContext = new FontRenderContext(null, true, true);

	//Constructor
	public RenderedGenomesPanel(mainFrame mfr){
		super();
		this.mf = mfr;
		this.rgp = this;
		this.addMouseListener(this);

		//Retrieve settings
		this.RangeLimit = mf.getFr().getCVS().RangeLimit;
		this.SplitLimit = mf.getFr().getCVS().SplitLimit;
		this.BeforeBuffer = mf.getFr().getCVS().DefaultRangeAround;
		this.AfterBuffer = mf.getFr().getCVS().DefaultRangeAround;
		
		//write in info from option panel
		//this.BeforeBuffer = Integer.parseInt(this.mf.getOp().getStrbeforeRangeValue());
		//this.AfterBuffer = Integer.parseInt(this.mf.getOp().getStrafterRangeValue());
		
		//Splitting/editing
		
		//Initialize holding places
		SplitContexts = new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
		SplitSpeciesNames = new LinkedHashMap<String, String>();
		SplitContigNames = new LinkedHashMap<String, HashSet<String>>();
		
		splitCrossContigGroupings();	//Adjust contexts
		splitLargeGapGroupings();		//Adjust for large gaps within groupings		

		//computing segment info
		this.dim = computeGenomicSegments();
		this.setPreferredSize(dim);		 //key: preferredsize, not size
		
		//computing information for display.
		computeNucleotideRangesOnSegments();
		addDrawGenes();
		addMotifs();
		addHomologyColors();
		addCoordinateBars();
		
		//create the Jpopupmenu
		this.InitializeExportMenu();
		
		//show missed
		if (this.ContextsExcluded){
			showExludedContexts();
		}
	}

	// ----- export-related ------------------------------------------//
	
	//create the pop-up menu object
	private void InitializeExportMenu(){
		
		//create action listener
		ActionListener exportAction = new ActionListener(){
			
			public void actionPerformed(final ActionEvent evt) {
				
				//initialize context region
				Integer ContextRegion = -1;
				
				if (evt.getActionCommand().equals("Save contexts as JPG") ||
						evt.getActionCommand().equals("Save contexts as PNG") ||
						evt.getActionCommand().equals("Save contexts as EPS")) {
					
					// SAVE WHOLE CONTEXT
					try {
						final BufferedImage buff; 
						//draw image and save picture
						if (evt.getActionCommand().equals("Save contexts as JPG")){
							buff = drawBufferedImage("jpg");
							savePicture(buff, "jpg");
						} else if (evt.getActionCommand().equals("Save contexts as PNG")){
							buff = drawBufferedImage("png");
							savePicture(buff, "png");
						} else if (evt.getActionCommand().equals("Save contexts as EPS")){
							String EPSString = drawEPS();
							saveEPS(EPSString);
						}
						
					} catch (Exception e) {
					}
				
				//create a legend frame/
				} else if (evt.getActionCommand().equals("Show Legend - Complete") ||
						evt.getActionCommand().equals("Show Legend - Annotations") ||
						evt.getActionCommand().equals("Show Legend - Clusters")){
					
					//invoke the color list, unless there are no colors to report.
					 if (GeneColorList != null){
						 DetermineDisplayGeneColors();
						
						 if (evt.getActionCommand().equals("Show Legend - Complete")){
							 gclf  = new GeneColorLegendFrame(rgp,DisplayedGeneColorList,"Complete");
							 gclp = gclf.getGclp();
						 } else if (evt.getActionCommand().equals("Show Legend - Annotations")){
							 gclf  = new GeneColorLegendFrame(rgp,DisplayedGeneColorList,"Annotations");
							 gclp = gclf.getGclp();
						 } else if (evt.getActionCommand().equals("Show Legend - Clusters")){
							 gclf  = new GeneColorLegendFrame(rgp,DisplayedGeneColorList,"Clusters");
							 gclp = gclf.getGclp();
						 }
						
						 
					 }
				}
			}

		};
		
		//set export menu
		this.ExportMenu = new JPopupMenu();
		
		//create menu items
		final JMenuItem me0 = new JMenuItem("Save contexts as JPG");
		final JMenuItem me1 = new JMenuItem("Save contexts as PNG");
		final JMenuItem me2 = new JMenuItem("Save contexts as EPS");
		final JMenuItem me3 = new JMenuItem("Show Legend - Complete");
		final JMenuItem me4 = new JMenuItem("Show Legend - Annotations");
		final JMenuItem me5 = new JMenuItem("Show Legend - Clusters");
		
		//add action listeners
		me0.addActionListener(exportAction);
		me1.addActionListener(exportAction);
		me2.addActionListener(exportAction);
		me3.addActionListener(exportAction);
		me4.addActionListener(exportAction);
		me5.addActionListener(exportAction);
		
		//build menu
		ExportMenu.add(me0);
		ExportMenu.add(me1);
		ExportMenu.add(me2);
		ExportMenu.addSeparator();
		ExportMenu.add(me3);
		if (this.ECRONType.contentEquals("annotation")){
			ExportMenu.add(me4);
		} else {
			ExportMenu.add(me5);
		}

	}
	
	//determine displayed gene colors
	private void DetermineDisplayGeneColors(){
		
		//initialize output
		DisplayedGeneColorList = new LinkedList<SharedHomology>();
		
		// all genes in the range are displayed
		if (ShowSurrounding == true && ColorSurrounding == true){
			DisplayedGeneColorList = GeneColorList;
		} else { //only show query matches
			
			//initialize a hash set
			HashSet<SharedHomology> SHash = new HashSet<SharedHomology>();
			
			//add all appropriate colored genes to the hashset
			for (int i = 0; i < GS.length; i++){
				for (DrawGene dg : GS[i].getDg()){
					if (dg.getMembership() == 0){
						for (SharedHomology SH : GeneColorList){
							if (ECRONType.equals("annotation")){
								if (SH.getAnnotation().contentEquals(dg.getBioInfo().getAnnotation().toUpperCase())){
									SHash.add(SH);
									break;
								}
							} else {
								if (SH.getClusterID()==dg.getBioInfo().getClusterID()){
									SHash.add(SH);
									break;
								}
							}
						}
					}

				}
			}
			
			//add all appropriate motifs to the hashset
			for (int i = 0; i < GS.length; i++){
				for (DrawMotif dm : GS[i].getDm()){
					if (dm.getMembership() == 0){
						for (SharedHomology SH : MotifColorList){
							if (SH.getAnnotation().contentEquals(dm.getBioInfo().getMotifName().toUpperCase())){
								SHash.add(SH);
								break;
							}
						}
					}
				}
			}

			//create an iterator for the HashSet, remove duplicate entries
			Iterator<SharedHomology> it = SHash.iterator();
			while (it.hasNext()){
				SharedHomology SH = it.next();
				DisplayedGeneColorList.add(SH);
			}

		}
	}
	
	//method to save picture
	private void savePicture(BufferedImage buff, String extension) {
		String sPath;
		String sNameNoExt = Jpan_btn_NEW.getFileNameNoExt();
		final FileDialog fd = new FileDialog(mf, "Export " +
				extension.toUpperCase() + " Image", FileDialog.SAVE);
		fd.setFile(sNameNoExt + "." + extension);
		fd.setVisible(true);
		
		if (fd.getFile() != null){
			sPath = fd.getDirectory() + fd.getFile();
			final File OutputFile = new File(sPath);
			try {
				ImageIO.write(buff, extension, OutputFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Image Writing Error",
						"The picture could not be created.",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
		
	//generate JPG or PNG image
	private BufferedImage drawBufferedImage(String extension){
		
		if (extension.contentEquals("jpg") || 
				extension.contentEquals("png")) {
			
			Graphics2D g2d;
			final double width_Mon = this.getSize().getWidth();
			final double height_Mon = this.getSize().getHeight();
			final BufferedImage buff = new BufferedImage((int) width_Mon,
					(int) height_Mon, BufferedImage.TYPE_INT_RGB);
			g2d = buff.createGraphics();
			this.paintComponent(g2d);
			g2d.dispose();

			return buff;
			
		} else {
			return null;
		}
	}
	
	//produce an EPS
	private String drawEPS(){
		EpsGraphics2D g2d = new EpsGraphics2D();
		this.paintComponent(g2d);
		return g2d.toString();
	}

	//save an EPS
	private void saveEPS(String EPS){
		String sPath;
		String sNameNoExt = Jpan_btn_NEW.getFileNameNoExt();
		final FileDialog fd = new FileDialog(mf, "Export EPS Image", FileDialog.SAVE);
		fd.setFile(sNameNoExt + ".eps");
		fd.setVisible(true);
		
		if (fd.getFile() != null){
			sPath = fd.getDirectory() + fd.getFile();
			final File OutputFile = new File(sPath);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(OutputFile));
				bw.write(EPS); bw.flush(); bw.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Image Saving Error",
						"The picture could not be saved.",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	// ----- pre-drawing computations -----------------------------------//
	//split cross-contig genomic groupings
	public void splitCrossContigGroupings(){

		//Retrieve original data
		CSDisplayData CSD = this.mf.getCSD();
		
		//Initialize updated list
		LinkedList<ContextLeaf> UpdatedCL = new LinkedList<ContextLeaf>();
		
		//split contexts based on common contig.
		for (ContextLeaf CL : CSD.getGraphicalContexts()){
			if (CL.isSelected()){
				
				//Retrieve original set
				String LeafName = CL.getName();
				LinkedList<GenomicElementAndQueryMatch> LL = CSD.getEC().getContexts().get(LeafName);
				
				//split the set, based on contig
				LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>
					SequenceElementMapping = new LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>>();
				
				//iterate through all contig names
				Iterator<String> it = CSD.getEC().getSourceContigNames().get(LeafName).iterator();
				while (it.hasNext()){
					
					//current contig
					String Contig = it.next();
					
					//create output
					LinkedList<GenomicElementAndQueryMatch> SL = new LinkedList<GenomicElementAndQueryMatch>();
					
					//pull out subset
					for (GenomicElementAndQueryMatch GandE : LL){
						if (GandE.getE().getContig().equals(Contig)){
							SL.add(GandE);
						}
					}
					
					//map
					SequenceElementMapping.put(Contig, SL);

				}
				
				//remake set, if appropriate
				if (SequenceElementMapping.size() > 1){

					//retrieve information
					HashSet<String> ContigName = CSD.getEC().getSourceContigNames().get(LeafName);
					String SourceName= CSD.getEC().getSourceSpeciesNames().get(LeafName);
					
					//store information, prior to removal, if it is basic
					if (CL.isSearchReturnedContext()){
						SplitContexts.put(LeafName, LL);
						SplitContigNames.put(LeafName, ContigName);
						SplitSpeciesNames.put(LeafName, SourceName);
					}
					
					//remove old sets
					CSD.getEC().getContexts().remove(LeafName);
					CSD.getEC().getSourceContigNames().remove(LeafName);
					CSD.getEC().getSourceSpeciesNames().remove(LeafName);
					
					//initialize counter
					int ContigCounter = 0;
					
					for (String CN : SequenceElementMapping.keySet()){
						
						//increment counter
						ContigCounter++;
						
						//retrieve output
						LinkedList<GenomicElementAndQueryMatch> SL = SequenceElementMapping.get(CN);
						
						//Updated leaf name
						String UpdatedLeafName = LeafName + ", Sequence " + String.valueOf(ContigCounter);
						
						//all other information is the same, except for name
						ContextLeaf NewLeaf = new ContextLeaf();
						NewLeaf.setSelected(true);
						NewLeaf.setName(UpdatedLeafName);
						NewLeaf.setSearchReturnedContext(false);

						//update contexts, species, and sequence names
						CSD.getEC().getContexts().put(UpdatedLeafName, SL);
						CSD.getEC().getSourceSpeciesNames().put(UpdatedLeafName, SourceName);
						
						//map to a new set of contigs
						HashSet<String> UpdatedContigNames = new HashSet<String>();
						UpdatedContigNames.add(CN);
						
						CSD.getEC().getSourceContigNames().put(UpdatedLeafName, UpdatedContigNames);
						
						//add to list
						UpdatedCL.add(NewLeaf);

					}
					
				} else {
					UpdatedCL.add(CL);
				}
				
			} else {
				UpdatedCL.add(CL);
			}
		}
		
		//reformat into an array.
		ContextLeaf[] UpdatedContexts = new ContextLeaf[UpdatedCL.size()];
		for (int i = 0; i < UpdatedContexts.length; i++){
			UpdatedContexts[i] = UpdatedCL.get(i);
		}
		
		//update data structure
		CSD.setGraphicalContexts(UpdatedContexts);
		
		//return data
		this.mf.setCSD(CSD);
		
	}
	
	//split gene groupings with elements that span a large gap
	public void splitLargeGapGroupings(){
		
		//Retrieve original data
		CSDisplayData CSD = this.mf.getCSD();
		
		//Initialize updated list
		LinkedList<ContextLeaf> UpdatedCL = new LinkedList<ContextLeaf>();
		
		for (ContextLeaf CL : CSD.getGraphicalContexts()){
			
			if (CL.isSelected()){
				
				//Retrieve original set
				String LeafName = CL.getName();
				LinkedList<GenomicElementAndQueryMatch> LL = CSD.getEC().getContexts().get(LeafName);
				
				//split the set, based on size
				LinkedHashMap<int[], LinkedList<GenomicElementAndQueryMatch>>
					SequenceElementMapping = new LinkedHashMap<int[], LinkedList<GenomicElementAndQueryMatch>>();
				
				for (GenomicElementAndQueryMatch GandE : LL){
					
					//mapped or not
					boolean ElementMapped = false;
					
					//check all existing groups
					for (int[] Key : SequenceElementMapping.keySet()){
						
						//compare to key
						//(1) gene falls within existing limits
						//(2) adding this gene and updating limits does not change much.
						if (((GandE.getE().getStart() > Key[0] && GandE.getE().getStop() < Key[1])) ||
								Math.max(Key[1], GandE.getE().getStop())-Math.min(Key[0],GandE.getE().getStart()) <= SplitLimit){
							
							//update the key, for reloading in the hash map.
							int[] UpdatedKey = {Math.min(Key[0], GandE.getE().getStart()), Math.max(Key[1], GandE.getE().getStop())};
							
							//retrieve entries + update
							LinkedList<GenomicElementAndQueryMatch> Entries = SequenceElementMapping.get(Key);
							Entries.add(GandE);
							
							//update hash map
							SequenceElementMapping.remove(Key);
							SequenceElementMapping.put(UpdatedKey, Entries);
							
							//break out of the loop -> this entry has been mapped.
							ElementMapped = true;
							break;
							
						}
					}
					
					//map the element, if needed.
					if (!ElementMapped){
						//if need be, create a new entry.
						//create key
						int[] NewKey = {GandE.getE().getStart(), GandE.getE().getStop()};
						
						//create value
						LinkedList<GenomicElementAndQueryMatch> NewGroup = 
								new LinkedList<GenomicElementAndQueryMatch>();
						NewGroup.add(GandE);
						
						//insert into hashmap
						SequenceElementMapping.put(NewKey,NewGroup);
					}

				}

				//remake set, if appropriate
				if (SequenceElementMapping.size() > 1){
					
					//retrieve information
					HashSet<String> ContigName = CSD.getEC().getSourceContigNames().get(LeafName);
					String SourceName= CSD.getEC().getSourceSpeciesNames().get(LeafName);
					
					//store information, prior to removal
					SplitContexts.put(LeafName, LL);
					SplitContigNames.put(LeafName, ContigName);
					SplitSpeciesNames.put(LeafName, SourceName);
					
					//remove old sets
					CSD.getEC().getContexts().remove(LeafName);
					CSD.getEC().getSourceContigNames().remove(LeafName);
					CSD.getEC().getSourceSpeciesNames().remove(LeafName);
					
					//initialize counter
					int ContigCounter = 0;
					
					for (LinkedList<GenomicElementAndQueryMatch> SL : SequenceElementMapping.values()){
						
						//increment counter
						ContigCounter++;
						
						//Updated leaf name
						String UpdatedLeafName = LeafName + ", Segment " + String.valueOf(ContigCounter);
						
						//all other information is the same, except for name
						ContextLeaf NewLeaf = new ContextLeaf();
						NewLeaf.setSelected(true);
						NewLeaf.setName(UpdatedLeafName);
						NewLeaf.setSearchReturnedContext(false);

						//update contexts, species, and sequence names
						CSD.getEC().getContexts().put(UpdatedLeafName, SL);
						CSD.getEC().getSourceSpeciesNames().put(UpdatedLeafName, SourceName);
						CSD.getEC().getSourceContigNames().put(UpdatedLeafName, ContigName);
						
						//add to list
						UpdatedCL.add(NewLeaf);

					}
					
				} else {
					UpdatedCL.add(CL);
				}
				
			} else {
				UpdatedCL.add(CL);
			}
		}
		
		//reformat into an array.
		ContextLeaf[] UpdatedContexts = new ContextLeaf[UpdatedCL.size()];
		for (int i = 0; i < UpdatedContexts.length; i++){
			UpdatedContexts[i] = UpdatedCL.get(i);
		}
		
		//update data structure
		CSD.setGraphicalContexts(UpdatedContexts);
		
		//return data
		this.mf.setCSD(CSD);
	}
	
	//create genomic segments
	private Dimension computeGenomicSegments() {
		
		//acceptable segments + unacceptable segments
		LinkedList<GenomicSegment> AcceptableSegments = 
				new LinkedList<GenomicSegment>();
		
		ExceededRangeLimit = new LinkedList<String>();
		
		//determine number of segments
		int SegmentstoDraw = 0;
		for (ContextLeaf CL : this.mf.getCSD().getGraphicalContexts()){
			if (CL.isSelected()){
				SegmentstoDraw++;
			}
		}
		
		//initialize output variable, and dimension info
		Dimension ThisDimension = new Dimension();
		int DimTotalHeight = 0;
		Dimension d = this.mf.getDim();
		int DimTotalWidth = (int) d.getWidth() - WholeWidthBuffer;
		GSWidth = (int)(DimTotalWidth*(1-GSStartFromLeft-GSSBufferOnRight));
		
		//initialize array
		GS = new GenomicSegment[SegmentstoDraw];
		
		//retrieve biological information
		contexts = mf.getCSD().getEC().getContexts();
		SourceSpecies = mf.getCSD().getEC().getSourceSpeciesNames();
		SourceContigs = mf.getCSD().getEC().getSourceContigNames();
		
		//What to do about this? should the display type possibly be an option?
		ECRONType = mf.getCSD().getEC().getSearchType();
				
//		if (mf.getOS().isGeneClustersLoaded()){
//			ECRONType = "cluster";
//		} else {
//			ECRONType = "annotation";
//		}
		
		int LongestRange = 0; 
		
		//add information
		int CoordinateNumber = -1;
		for (ContextLeaf CL : this.mf.getCSD().getGraphicalContexts()){
			if (CL.isSelected()){
				
				//initialize a new genomic segment
				GenomicSegment GSelement = new GenomicSegment();
				
				//note query match info
				int QueryStrandPlus = 0; int QueryStrandMinus = 0;
				
				//add node name
				GSelement.setLabel(CL.getName());
				
				//determine the longest range
				LinkedList<GenomicElementAndQueryMatch> LL = contexts.get(GSelement.getLabel());
				
				//reset start and stop variables for every genomic element processed
				int Start = 99999999; int Stop = -1;

				//determine range information + query match information.
				for (GenomicElementAndQueryMatch e : LL){
					
					//determine range information
					if (e.getE().getStart() < Start){
						Start = e.getE().getStart();
					}
					if (e.getE().getStop() > Stop){
						Stop = e.getE().getStop();
					}

					//query-match orientation information
					if (e.isQueryMatch()){
						if (e.getE().getStrand().equals(Strand.POSITIVE)){
							QueryStrandPlus++;
						} else {
							QueryStrandMinus++;
						}
					}
					
				}
				
				//query match-related
				//setting to flip all genes in the event of strand-normalized display
				if (QueryStrandMinus > QueryStrandPlus){
					GSelement.setStrRevFlipGenes(true);
				}
				
				//only add this element to the list if the range is acceptable.
				if ((Stop - Start) < RangeLimit) {
					
					//increment coordinate number
					CoordinateNumber++;
					
					//set values for this CS
					GSelement.setStartCS(Start);
					GSelement.setStartAfterBuffer(Stop);
					GSelement.setCenterofCS((int) Math.round((double)(0.5*(Start+Stop))));
					
					//bounding rectangle
					Rectangle2D rect = 
							new Rectangle((int) (DimTotalWidth*GSStartFromLeft), 
									(int)(((GSHeight+GSSpacer)*CoordinateNumber)+GSSpacer),
									GSWidth,GSHeight);
					
					//add bounding rectangle to the set of GS elements
					GSelement.setBoundingRect(rect);
					
					//compare to current longest range
					if ((Stop-Start) > LongestRange){
						LongestRange = Stop-Start;
					}
					
					//segment is acceptable
					AcceptableSegments.add(GSelement);
					
				} else {
					//ExceededRangeLimit.add(this.mf.getCSD().getNodeNames()[i]);
					ExceededRangeLimit.add(CL.getName());
					ContextsExcluded = true;
				}
			}
		}
		
		CoordinateNumber = -1;
		
		//from the list of acceptable nodes, determine rendering regions.
		GS = new GenomicSegment[AcceptableSegments.size()];
		for (int i = 0; i < AcceptableSegments.size(); i++){
			GS[i] = AcceptableSegments.get(i);
			CoordinateNumber++;
		}
		
		//determine the total height
		DimTotalHeight = (GSHeight + GSSpacer)*(CoordinateNumber+1)+ 2*GSSpacer;

		//genomic display ranges
		GenomicDisplayRange = LongestRange + BeforeBuffer + AfterBuffer;
		CenterScaleValue = (BeforeBuffer + 0.5*((double)LongestRange))/GenomicDisplayRange;
		genome2displayRatio = ((double)GSWidth/(double)GenomicDisplayRange);
		//Determine where to put center of CS, relative to whole size
		
		
		//optional print statements
//		System.out.println("Display Range: " + GenomicDisplayRange + " nt.");
//		System.out.println(CenterScaleValue);
//		System.out.println(genome2displayRatio);
		
		ThisDimension.setSize(DimTotalWidth,DimTotalHeight); //dimension of panel
		return ThisDimension;
	}

	//add range information
	private void computeNucleotideRangesOnSegments() {
		int NtBeforeCenter; int NtAfterCenter;
		int GSStart; int GSEnd;
		for (int i = 0; i <GS.length; i++){
			
			//determine values
			NtBeforeCenter = (int) (CenterScaleValue*GenomicDisplayRange);
			GSStart = GS[i].getCenterofCS() - NtBeforeCenter;
			NtAfterCenter = (int) ((1 - CenterScaleValue)*GenomicDisplayRange);
			GSEnd = GS[i].getCenterofCS() + NtAfterCenter;
			
			//set values
			GS[i].setStartBeforeBuffer(GSStart);
			GS[i].setEndRange(GSEnd);
		}
	}
	
	//add genes to bounding rectangles (no other features)
	private void addDrawGenes(){

		for (int i = 0; i <GS.length; i++){
			
			//Initialize drawgenes output structures
			LinkedList<DrawGene> dgs = new LinkedList<DrawGene>();
			
			//retrieve species + contig name
			String SpeciesName = SourceSpecies.get(GS[i].getLabel());
			String ContigName = SourceContigs.get(GS[i].getLabel()).iterator().next();

			//debugging: print statements
			//System.out.println(i + ": " + SpeciesName + "-" + ContigName);
			
			//TODO: modifications, of course!
			//retrieve genome - load if necessary
			mf.getOS().AdjustAvailableSpecies(SpeciesName);
			AnnotatedGenome AG = mf.getOS().getSpecies().get(SpeciesName);
			
			//information
			int GeneX; int GeneY;  int GeneWidth; int GeneHeight;
			//iterate through elements, and add coordinates
			//this approach assumes an unsorted list

			//iterate through all genes
			for (GenomicElement e : AG.getElements()){
				
				//check and see if this element should be retained at all
				//check include types
				boolean DisplayElement = false;
				for (String s : AG.getIncludeTypes()){
					if (e.getType().contentEquals(s)){
						DisplayElement = true;
						break;
					}
				}
				//if this fails, check for display only types
				if (!DisplayElement){
					for (String s : AG.getDisplayOnlyTypes()){
						if (e.getType().contentEquals(s)){
							DisplayElement = true;
							break;
						}
					}
				}
				
				if ((((e.getStart() < GS[i].getStartBeforeBuffer() && e.getStop() < GS[i].getStartBeforeBuffer()) ||
						(e.getStart() > GS[i].getEndRange() && e.getStop() > GS[i].getEndRange())) == false) &&
						(DisplayElement) && e.getContig().equals(ContigName)){
					
					//upon discovering a single gene, initialize a new "drawgene"
					DrawGene dg = new DrawGene();
					
					//add information relevant for coloring
					dg.setBioInfo(e);
					dg.setSourceSpecies(AG.getSpecies());
					
					//determine rendering coordinates of the rectangle
					boolean TruncatedStart = false;
					
					//x-coordinates
					//truncate beginning
					if (e.getStart() < GS[i].getStartBeforeBuffer()){
						GeneX = (int) GS[i].getBoundingRect().getMinX();
						TruncatedStart = true;
					} else {
						GeneX = (int) ((genome2displayRatio * (e.getStart()-GS[i].getStartBeforeBuffer()))
								+ GS[i].getBoundingRect().getMinX());
					}
					
					//y-coordinate (here, consider strandedness)
					GeneY = (int) GS[i].getBoundingRect().getCenterY();
										
					//width (consider truncating the end)
					if (e.getStop() > GS[i].getEndRange()){
						GeneWidth = (int) (GS[i].getBoundingRect().getMaxX()) - GeneX;
					} else {
						if (TruncatedStart == false){
							GeneWidth = (int)(genome2displayRatio * (e.getStop() - e.getStart()));
						} else {
							GeneWidth = (int)(genome2displayRatio * (e.getStop() - GS[i].getStartBeforeBuffer()));
						}
					}
					
					//Height (always the same)
					GeneHeight = (int) (GSHeight/3);
					
					//Y-coordinate starting point (strandedness matters)
					if (e.getStrand().equals(Strand.POSITIVE)){
						GeneY = (int) GS[i].getBoundingRect().getCenterY() - GeneHeight;
					} else {
						//GeneY = (int) GS[i].getBoundingRect().getCenterY(); //original
						GeneY = (int) GS[i].getBoundingRect().getCenterY()+1; //add 1 for display problems
					}
					
					//create rectangle with appropriate values
					Rectangle gene = new Rectangle(GeneX, GeneY, GeneWidth, GeneHeight);
					dg.setCoordinates(gene);

					//strand-reversed case
					if (GS[i].isStrRevFlipGenes()){
						
						//height+width do not change
						int GeneHeightFlip = GeneHeight;
						int GeneWidthFlip = GeneWidth;
						int GeneYFlip;
						
						//determine Y-coordinate
						if (GeneY == (int) GS[i].getBoundingRect().getCenterY()+1){ //edit with +1 for display problem
							GeneYFlip = (int) GS[i].getBoundingRect().getCenterY() - GeneHeight;
						} else {
							//GeneYFlip = (int) GS[i].getBoundingRect().getCenterY();// original
							GeneYFlip = (int) GS[i].getBoundingRect().getCenterY()+1;// add 1 for display problems
						}
						
						//determine X-coordinate
						double Dist2Center = Math.abs(GeneX - GS[i].getBoundingRect().getCenterX());
						int GeneXFlip;
						if (GeneX > GS[i].getBoundingRect().getCenterX()){
							GeneXFlip = (int) (GeneX - 2*Dist2Center - GeneWidth);
						} else {
							GeneXFlip = (int) (GeneX + 2*Dist2Center - GeneWidth);
						}
						
						//draw rectangle, and store
						Rectangle geneFlip = new Rectangle(GeneXFlip, GeneYFlip, GeneWidthFlip, GeneHeightFlip);
						dg.setStrRevCoordinates(geneFlip);
						dg.setStrRevChange(true);
						
					} else {
						dg.setStrRevCoordinates(gene);
						dg.setStrRevChange(false);
					}
					
					//determine membership
					//Update: compare to ECRON as opposed to range boundaries
					LinkedList<GenomicElementAndQueryMatch> LL = contexts.get(GS[i].getLabel());
					
					boolean MemberOfContextSet = false;
					for (int j = 0; j <LL.size(); j++){
						if (e.getStart() == LL.get(j).getE().getStart() &&
								e.getStop() == LL.get(j).getE().getStop()){
							MemberOfContextSet = true;
						}
					}
					
					if (MemberOfContextSet == true){
						dg.setMembership(0);
					} else {
						dg.setMembership(-1);
					}
					
					//set color to default
					dg.setColor(Color.LIGHT_GRAY);
					
					//add this draw gene to the set
					dgs.add(dg);
				}
			}
			//update draw genes
			GS[i].setDg(dgs);
		}
	}
	
	//add motifs to contexts
	private void addMotifs(){
		
		for (int i = 0; i <GS.length; i++){
			
			//Initialize drawgenes output structures
			LinkedList<DrawMotif> dms = new LinkedList<DrawMotif>();
			
			//retrieve species + contig name
			String SpeciesName = SourceSpecies.get(GS[i].getLabel());
			String ContigName = SourceContigs.get(GS[i].getLabel()).iterator().next();

			//retrieve genome
			AnnotatedGenome AG = mf.getOS().getSpecies().get(SpeciesName);
			
			//information
			int MotifX; int MotifY;  int MotifWidth; int MotifHeight;
			//iterate through elements, and add coordinates
			//this approach assumes an unsorted list

			//iterate through all significant sequences
			for (MotifGroup MG : AG.getMotifs()){
				for (SequenceMotif SM : MG.getMotifInstances()){
					if ((((SM.getStart() < GS[i].getStartBeforeBuffer() && SM.getStop() < GS[i].getStartBeforeBuffer()) ||
							(SM.getStart() > GS[i].getEndRange() && SM.getStop() > GS[i].getEndRange()))==false) &&
							SM.getContig().equals(ContigName)){
						
						//Debugging: display motif
						//System.out.println("DrawMotif: " + AG.getSpecies() + "," + SM.getContig() + " " + SM.getStart() + ":" + SM.getStop() + " " + SM.getStrand().toString());
						
						//Initialize draw motif
						DrawMotif dm = new DrawMotif();
						
						//set information relevant for coloring
						dm.setBioInfo(SM);
						
						//determine rendering coordinates of the ellipse
						boolean TruncatedStart = false;
						
						//x-coordinates
						//truncate beginning
						if (SM.getStart() < GS[i].getStartBeforeBuffer()){
							MotifX = (int) GS[i].getBoundingRect().getMinX();
							TruncatedStart = true;
						} else {
							MotifX = (int) ((genome2displayRatio * (SM.getStart()-GS[i].getStartBeforeBuffer()))
									+ GS[i].getBoundingRect().getMinX());
						}
						
						//y-coordinate (here, consider strandedness)
						MotifY = (int) GS[i].getBoundingRect().getCenterY();
											
						//width (consider truncating the end)
						if (SM.getStop() > GS[i].getEndRange()){
							MotifWidth = (int) (GS[i].getBoundingRect().getMaxX()) - MotifX;
						} else {
							if (TruncatedStart == false){
								MotifWidth = (int)(genome2displayRatio * (SM.getStop() - SM.getStart()));
							} else {
								MotifWidth = (int)(genome2displayRatio * (SM.getStop() - GS[i].getStartBeforeBuffer()));
							}
						}
						
						//Height (always the same)
						MotifHeight = (int) (GSHeight/3);
						
						//Y-coordinate starting point (strandedness matters)
						if (SM.getStrand().equals(Strand.POSITIVE)){
							MotifY = (int) GS[i].getBoundingRect().getCenterY() - MotifHeight;
						} else {
							//GeneY = (int) GS[i].getBoundingRect().getCenterY(); //original
							MotifY = (int) GS[i].getBoundingRect().getCenterY()+1; //add 1 for display problems
						}
						
						//note original motif width
						int OriginalMotifWidth = MotifWidth;
						
						//update motif width, if appropriate
						if (!Motifs2Scale)
							if (MotifWidth < MotifWidthMinimum){
								MotifWidth = MotifWidthMinimum;
							}
								
						
						//create ellipse with appropriate values
						Ellipse2D motif = new Ellipse2D.Double((double)MotifX, (double)MotifY, (double)MotifWidth, (double)MotifHeight);
						dm.setCoordinates(motif);
						
						//strand-reversed case
						if (GS[i].isStrRevFlipGenes()){
							
							//height+width do not change
							int MotifHeightFlip = MotifHeight;
							int MotifWidthFlip = OriginalMotifWidth;
							int MotifYFlip;
							
							//determine Y-coordinate
							if (MotifY == (int) GS[i].getBoundingRect().getCenterY()+1){ //edit with +1 for display problem
								MotifYFlip = (int) GS[i].getBoundingRect().getCenterY() - MotifHeight;
							} else {
								MotifYFlip = (int) GS[i].getBoundingRect().getCenterY()+1;// add 1 for display problems
							}
							
							//determine X-coordinate
							double Dist2Center = Math.abs(MotifX - GS[i].getBoundingRect().getCenterX());
							int MotifXFlip;
							if (MotifX > GS[i].getBoundingRect().getCenterX()){
								MotifXFlip = (int) (MotifX - 2*Dist2Center - OriginalMotifWidth);
							} else {
								MotifXFlip = (int) (MotifX + 2*Dist2Center - OriginalMotifWidth);
							}
							
							//update flipped motif width, if appropriate
							if (!Motifs2Scale)
								if (MotifWidthFlip < MotifWidthMinimum){
									MotifWidthFlip = MotifWidthMinimum;
								}
							
							//create ellipse with appropriate values
							Ellipse2D motifFlip = new Ellipse2D.Double((double)MotifXFlip, (double)MotifYFlip, (double)MotifWidthFlip, (double)MotifHeightFlip);
							dm.setStrRevCoordinates(motifFlip);
							dm.setStrRevChange(true);

						} else {
							dm.setStrRevCoordinates(motif);
							dm.setStrRevChange(false);
						}
						
						//determine membership
						LinkedList<GenomicElementAndQueryMatch> LL = contexts.get(GS[i].getLabel());
						
						boolean MemberOfContextSet = false;
						for (int j = 0; j <LL.size(); j++){
							if (LL.get(j).getE().getAssociatedMotifs().contains(SM)){
								MemberOfContextSet = true;
							}
						}
						
						if (MemberOfContextSet == true){
							dm.setMembership(0);
						} else {
							dm.setMembership(-1);
						}
						
						//set color to default
						dm.setColor(Color.LIGHT_GRAY);
						
						//add this draw gene to the set
						dms.add(dm);
						
					}
				}
			}

			//update draw genes
			GS[i].setDm(dms);
		}	
	}

	//add appropriate colors according to homology
	private void addHomologyColors() {
		
		//reset offset point
		this.OffSetPoint = 0;
		
		if (ECRONType.equals("annotation")){
			
			//define a new counting array
			Count<String, Integer> AnnColors = new Count<String, Integer>();
			
			//collect all colors 
			for (int i = 0; i < GS.length; i++){
				for (int j = 0; j <GS[i].getDg().size(); j++){
					AnnColors.add(GS[i].getDg().get(j).getBioInfo().getAnnotation().toUpperCase());
				}
			}
			
	        //sort annotations into a linked list
	        LinkedList<SharedHomology> AnnColorsSorted = SortAndAddColors2Ann(AnnColors);
	        
	        //set 
	        this.GeneColorList = AnnColorsSorted;
	        
	        //add these colors back to the elements
	        for (int i = 0; i < GS.length; i++){
	        	for (int j = 0; j < GS[i].getDg().size(); j++){
	        		for (int k = 0; k < AnnColorsSorted.size(); k++){
	        			if (AnnColorsSorted.get(k).getAnnotation().equals(GS[i].getDg().get(j).getBioInfo().getAnnotation().toUpperCase())){
	        				//set color appropriately
	        				GS[i].getDg().get(j).setColor(AnnColorsSorted.get(k).getColor());
	        				
	        				//add all elements to the shared homology colors for later parsing
	        				AnnColorsSorted.get(k).getMembers().add(GS[i].getDg().get(j).getBioInfo());
	        			}
	        		}
	        	}
	        }
	        
	        
		} else if (ECRONType.equals("cluster")){
			
			//Debugging
			//System.out.println("Type is cluster!!");
			
			//define a new counting array
			Count<Integer, Integer> AnnColors = new Count<Integer, Integer>();
			
			//collect all colors 
			for (int i = 0; i < GS.length; i++){
				for (int j = 0; j <GS[i].getDg().size(); j++){
					AnnColors.add(GS[i].getDg().get(j).getBioInfo().getClusterID());
				}
			}
			
	        //sort Cluster IDs into a linked list
	        LinkedList<SharedHomology> AnnColorsSorted = SortAndAddColors2Cluster(AnnColors);
	        this.GeneColorList = AnnColorsSorted;
	        
	        //debugging
			//System.out.println("assigning colors.");
			
	        //add these colors back to the elements
	        for (int i = 0; i < GS.length; i++){
	        	for (int j = 0; j < GS[i].getDg().size(); j++){
	        		for (int k = 0; k < AnnColorsSorted.size(); k++){
	        			if (AnnColorsSorted.get(k).getClusterID() == GS[i].getDg().get(j).getBioInfo().getClusterID()){
	        				//set color appropriately
	        				GS[i].getDg().get(j).setColor(AnnColorsSorted.get(k).getColor());
	        				
//	        				//for debugging - problem here?
//	        				if (GS[i].getDg().get(j).getBioInfo().getClusterID() == 144){
//	        					DrawGene DG = GS[i].getDg().get(j);
//    		        			String str = "Org= "+ DG.getSourceSpecies() + 
//    		        					" Contig= " + DG.getBioInfo().getContig() +
//    		        					" Color= " + DG.getColor().toString();
//    		        	        System.out.println(str);
//	        				}	        				
	        				
	        				//add all elements to the shared homology colors for later parsing
	        				AnnColorsSorted.get(k).getMembers().add(GS[i].getDg().get(j).getBioInfo());
	        			}
	        		}
	        	}
	        }
			
		}
		
		//adjust offset point
		this.OffSetPoint = this.GeneColorList.size();
		
		//Add colors to motifs
		//define a new counting array
		Count<String, Integer> MotifColors = new Count<String, Integer>();
		
		//collect all colors 
		for (int i = 0; i < GS.length; i++){
			for (int j = 0; j <GS[i].getDm().size(); j++){
				MotifColors.add(GS[i].getDm().get(j).getBioInfo().getMotifName().toUpperCase());
			}
		}
		
        //sort annotations into a linked list
        LinkedList<SharedHomology> MotifColorsSorted = SortAndAddColors2Ann(MotifColors);
        
        //set 
        this.MotifColorList = MotifColorsSorted;

        //add these colors back to the elements
        for (int i = 0; i < GS.length; i++){
        	if (GS[i].getDm() != null){
            	for (int j = 0; j < GS[i].getDm().size(); j++){
            		for (int k = 0; k < MotifColorsSorted.size(); k++){
            			if (MotifColorsSorted.get(k).getAnnotation().equals(GS[i].getDm().get(j).getBioInfo().getMotifName().toUpperCase())){
            				//set color appropriately
            				GS[i].getDm().get(j).setColor(MotifColorsSorted.get(k).getColor());
            				
            				//add all elements to the shared homology colors for later parsing
            				MotifColorsSorted.get(k).getMotifMembers().add(GS[i].getDm().get(j).getBioInfo());
            			}
            		}
            	}
        	}

        }
        
        //reset offset point
        this.OffSetPoint = 0;
        
//        //Debugging - everything showing up as blue (which is correct)
//        for (int i = 0; i < GS.length; i++){
//        	for (DrawGene DG : GS[i].getDg()){
//        		if (DG.getBioInfo().getClusterID() == 144){
//        			String str = "Org= "+ DG.getSourceSpecies() + 
//        					" Contig= " + DG.getBioInfo().getContig() +
//        					" Color= " + DG.getColor().toString();
//        	        System.out.println(str);
//        		}
//        	}
//        }
       
	}
	
	//add coordinate bars
	private void addCoordinateBars(){
		
		//coordinates to show depends on the size of the display range
		//subject to change
		if (GenomicDisplayRange < 1000){
			CoordinateBarEvery = 100;
		} else if (GenomicDisplayRange < 2000){
			CoordinateBarEvery = 200;
		} else if (GenomicDisplayRange < 3000){
			CoordinateBarEvery = 500;
		} else if (GenomicDisplayRange < 5000){
			CoordinateBarEvery = 1000;
		} else if (GenomicDisplayRange < 10000){
			CoordinateBarEvery = 2000;
		} else if (GenomicDisplayRange < 15000){
			CoordinateBarEvery = 3000;
		} else if (GenomicDisplayRange < 20000){
			CoordinateBarEvery = 5000;
		} else if (GenomicDisplayRange < 50000){
			CoordinateBarEvery = 10000;
		} else {
			CoordinateBarEvery = 20000;
		}

		//add bars for non-strand corrected case
		for (int i = 0; i < GS.length; i++){
			
			int StartValue;
			
			//round to nearest hundredth, thousandth, or ten-thousandth.
//			if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 1000){
//				StartValue = 10 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/10.0);
//			} else if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 10000){
//				StartValue = 100 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/100.0);
//			} else if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 100000){
//				StartValue = 1000 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/1000.0);
//			} else if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 1000000) {
//				StartValue = 10000 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/10000.0);
//			} else {
//				StartValue = 100000 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/100000.0);
//			}
			
			//round to nearest 1000, if possible
//			
//			System.out.println("Start: " + GS[i].getStartBeforeBuffer());
//			System.out.println("Ceiling: " + (int) (1000 * Math.ceil(GS[i].getStartBeforeBuffer()/1000)+1000));
//			if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 1000){
//				StartValue = 100 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/100.0);
//			} else if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 10000){
//				StartValue = 1000 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/1000.0);
//			} else if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 100000){
//				StartValue = 1000 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/1000.0);
//			} else if (GS[i].getEndRange()-GS[i].getStartAfterBuffer() < 1000000) {
//				StartValue = 1000 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/1000.0);
//			} else {
//				StartValue = 1000 * (int) Math.ceil(GS[i].getStartBeforeBuffer()/1000.0);
//			}
			
			//round to nearest 1000
			if (GS[i].getEndRange() > 1000){
				StartValue = (int) (1000 * Math.ceil(GS[i].getStartBeforeBuffer()/1000)+1000);
			} else {
				StartValue = 0;
			}
			
//			//optional print statement
//			System.out.println("Span: " + GS[i].getStartBeforeBuffer() 
//					+ ":" + GS[i].getEndRange() + " , First: " + StartValue);
			
			//add coordinate bars
			LinkedList<Integer> barpositions = new LinkedList<Integer>();
			LinkedList<Integer> barvalues = new LinkedList<Integer>();
			
			//write info to output
			while (StartValue < GS[i].getEndRange()){
				barvalues.add(StartValue);
				barpositions.add((int)(GS[i].getBoundingRect().getMinX()+(genome2displayRatio*(StartValue-GS[i].getStartBeforeBuffer()))));

				//optional print statements
//				System.out.println("min x: " + GS[i].getBoundingRect().getMinX());
//				System.out.println("value: " + (int)(GS[i].getBoundingRect().getMinX()+(genome2displayRatio*(StartValue-GS[i].getStartBeforeBuffer()))));
//				System.out.println(GS[i].getEndRange()-StartValue);
//				System.out.println(GS[i].getBoundingRect().getMinX()); 
//				System.out.println((GS[i].getEndRange()-StartValue));
				//+ GS[i].getEndRange()-StartValue)
				//System.out.println("Value: " + (int)(GS[i].getBoundingRect().getMinX()+(genome2displayRatio*(GS[i].getEndRange()-StartValue))));
				StartValue = StartValue + CoordinateBarEvery;
			}
			
			//store appropriate values
			GS[i].setBarPositions(barpositions);
			GS[i].setBarValues(barvalues);

			//initialize list
			LinkedList<Integer> ContigBoundaries = new LinkedList<Integer>();
			
			//contig boundary 1: zero boundary
			if (GS[i].getStartBeforeBuffer() <= 0){
				ContigBoundaries.add((int)(GS[i].getBoundingRect().getMinX()+
						(genome2displayRatio*(-1.0*GS[i].getStartBeforeBuffer()))));
			}
			
			//contig boundary 2: end of contig
			//retrieve contig end
			int ContigLimit = -1;
			if (mf.getFr().getOS().getSpecies().get(GS[i].getDg().get(0).getSourceSpecies()).getContigEnds().get(GS[i].getDg().get(0).getBioInfo().getContig()) != null){
				ContigLimit = mf.getFr().getOS().getSpecies().get(GS[i].getDg().get(0).getSourceSpecies()).getContigEnds().get(GS[i].getDg().get(0).getBioInfo().getContig());				
			}
			
			//draw a line, if appropriate
			if (ContigLimit != -1){
				
				//add lines, if appropriate
				if (ContigLimit < GS[i].getEndRange()){
					ContigBoundaries.add((int)(GS[i].getBoundingRect().getMinX()+
							(genome2displayRatio*(ContigLimit-GS[i].getStartBeforeBuffer()))));
				}
			}
			
			//add
			if (ContigBoundaries.size() > 0){
				GS[i].setContigBoundaries(ContigBoundaries);
			}
			
			//strand-reversed case
			if (GS[i].isStrRevFlipGenes() == false){
				
				GS[i].setBarPositionsRev(barpositions);
				GS[i].setContigBoundariesRev(ContigBoundaries);
				
			} else {
				
				//initialize output
				LinkedList<Integer> revbarpositions = new LinkedList<Integer>();
				LinkedList<Integer> revcontigboundaries = new LinkedList<Integer>();
				
				for (int j = 0; j < barpositions.size(); j++){
					
					//retrieve original bar x-position
					int BarX = barpositions.get(j);
					
					//determine dist to center
					double Dist2Center = Math.abs(BarX - GS[i].getBoundingRect().getCenterX());
					
					//determine flipped X-coordinate
					int BarXFlip;
					if (BarX > GS[i].getBoundingRect().getCenterX()){
						BarXFlip = (int) (BarX - 2*Dist2Center - CoordinateBarWidth);
					} else {
						BarXFlip = (int) (BarX + 2*Dist2Center - CoordinateBarWidth);
					}
					
					//add to the output
					revbarpositions.add(BarXFlip);
					
				}

				//write to output genomic segment
				GS[i].setBarPositionsRev(revbarpositions);
				
				for (int j = 0; j < ContigBoundaries.size(); j++){
					
					//retrieve original bar x-position
					int BarX = ContigBoundaries.get(j);
					
					//determine dist to center
					double Dist2Center = Math.abs(BarX - GS[i].getBoundingRect().getCenterX());
					
					//determine flipped X-coordinate
					int BarXFlip;
					if (BarX > GS[i].getBoundingRect().getCenterX()){
						BarXFlip = (int) (BarX - 2*Dist2Center - CoordinateBarWidth);
					} else {
						BarXFlip = (int) (BarX + 2*Dist2Center - CoordinateBarWidth);
					}
					
					//add to output
					revcontigboundaries.add(BarXFlip);
				}
				
				//write to output genomic segment
				if (revcontigboundaries.size() > 0){
					GS[i].setContigBoundariesRev(revcontigboundaries);
				}
			}
			
		}
	}
	
	//perform all initialization activities.
	
	//Recalculate, redraw
	public void ReComputeWithNewSegments(int BeforeValue, int AfterValue){
		
		//set new values
		this.BeforeBuffer = BeforeValue;
		this.AfterBuffer = AfterValue;
		//this.addMouseListener(this);
		
		//take care of mouse-clicked info windows
		if (GeneInformationIsBeingDisplayed == true){
			GeneInfo.dispose();
		}
		this.GeneInformationIsBeingDisplayed = false;

		//adjust inputs
		splitCrossContigGroupings();	//Adjust contexts
		splitLargeGapGroupings();		//Adjust for large gaps within groupings
		
		//computing segment info
		this.dim = computeGenomicSegments();
		this.setPreferredSize(dim);		 //key: preferredsize, not size

		//computing information for display.
		computeNucleotideRangesOnSegments();
		addDrawGenes();
		addMotifs();
		addHomologyColors();
		addCoordinateBars();
		
	}
	
	
	// ------ painting components ----------------------------------------//
	
	//paint method
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;

		this.setPreferredSize(dim);
		
		draftBackgrounds(g2d); 			//draw background for each genomic segment
		if (ShowCoordinates == true){
			draftCoordinates(g2d);		//draw numerical coordinates spanning genomic segment
		}
		draftGenes(g2d);				//draw genes

		if (this.mf.getFr().getPanMotifOptions().getIncludeMotifsDisp().isSelected()){
			draftMotifs(g2d);			//draw motifs, if desired.
		}
		draftLines(g2d); 				//draw centerline
		
		draftLabels(g2d);				//draw label associated with each genomic segment
		
		//middle clicked genes
		draftMiddleClickGenes(g2d);		//draw selected homology groups
		
		//The main frame CSD should not be altered.
		//this.mf.getFr().getCurrentFrame().getInternalFrameData().getQD().setCSD(this.mf.getOriginalCSD());

        //Debugging - everything showing up as blue (which is correct)
//		System.out.println("paintComponent entered.");
//        for (int i = 0; i < GS.length; i++){
//        	for (DrawGene DG : GS[i].getDg()){
//        		if (DG.getBioInfo().getClusterID() == 144){
//        			String str = "Org= "+ DG.getSourceSpecies() + 
//        					" Contig= " + DG.getBioInfo().getContig() +
//        					" Color= " + DG.getColor().toString();
//        	        System.out.println(str);
//        		}
//        	}
//        }
		
	}

	//draw label associated with each genomic segment
	private void draftLabels(Graphics2D g) {
				
		for (int i = 0; i <this.GS.length; i++){
			
			//retrieve string
			String txt = GS[i].getLabel();
			
			//create a text layout object
			TextLayout tl = new TextLayout(txt,fontStandard,renderContext); 

			//render labels
			int textX = (int) GS[i].getBoundingRect().getMinX();
			int textY = (int) GS[i].getBoundingRect().getMinY() - LabelAboveGS;
			tl.draw(g, textX, textY);
		}
	}

	//draw background for each genomic segment
	private void draftBackgrounds(Graphics2D g) {
		g.setPaint(Color.WHITE);
		
		//successful set the background!
		//g.fillRect(0, 0, getWidth(), getHeight());
		//System.out.println("width: " + getWidth() + " height: " + getHeight());
		for (int i = 0; i <this.GS.length; i++){
			//System.out.println("Bounding Rect: " + GS[i].getBoundingRect());
			g.fill(GS[i].getBoundingRect());
		}
	}

	//draw genes on each genomic context
	private void draftGenes(Graphics2D g) {
		
		//Debugging
		//System.out.println("draftGenes.");
		
		//add all genes to all backgrounds
		for (int i = 0; i <this.GS.length; i++){
			for (int j = 0; j <this.GS[i].getDg().size(); j++){
				
				//debugging.
				//DrawGene DG = GS[i].getDg().get(j);
//        		if (DG.getBioInfo().getClusterID() == 144){
//    			String str = "Org= "+ DG.getSourceSpecies() + 
//    					" Contig= " + DG.getBioInfo().getContig() +
//    					" Color= " + DG.getColor().toString();
//    	        System.out.println(str);
//        		}
        		
//        		if (DG.getSourceSpecies().equals("CVA-1_ORF_Final") &&
//        				DG.getBioInfo().getStart()==90483){
//        			String str = "Org= "+ DG.getSourceSpecies() + 
//        					" Contig= " + DG.getBioInfo().getContig() +
//        					" Color= " + DG.getColor().toString();
//        	        System.out.println(str);
//        		}
				
				//options to display some or all 
				if ((isShowSurrounding() == true) || 
						((isShowSurrounding() == false) && (this.GS[i].getDg().get(j).getMembership() == 0))) {
					
					//options to color extra regions
					if (this.GS[i].getDg().get(j).getMembership() == 0){
						
						//always color core genes with assigned homology color
						g.setPaint(this.GS[i].getDg().get(j).getColor());
						
					} else {
						
						//depending on options, color light gray or original color.
						if (isColorSurrounding() == true){
							g.setPaint(this.GS[i].getDg().get(j).getColor());
						} else {
							g.setPaint(Color.LIGHT_GRAY);
						}
						
					}

					//render original rectangle, or inverted strand
					if (StrandNormalize == true){
						
						//draw rectangle, with black border
						g.fill(this.GS[i].getDg().get(j).getStrRevCoordinates());
						
						//surround with black border
						g.setPaint(Color.BLACK);
						g.draw(this.GS[i].getDg().get(j).getStrRevCoordinates());
						
					} else {
					
						//draw rectangle
						g.fill(this.GS[i].getDg().get(j).getCoordinates());
					
						//surround with black border
						g.setPaint(Color.BLACK);
						g.draw(this.GS[i].getDg().get(j).getCoordinates());
					
					}
					
				}

			}
		}
		
	}
		
	//draw genes selected in click
	private void draftMiddleClickGenes(Graphics2D g){
		
		//adjust stroke
		Stroke DefaultStroke = g.getStroke();
		g.setStroke(new BasicStroke(6.0f));
		g.setColor(Color.RED);
		
		//color genes
		
		if (CurrentlySelectedGeneColors != null){
			for (Color c : CurrentlySelectedGeneColors){
				
				//check all draw objects for this color.
			    for (int i = 0; i <GS.length; i++){
			    	
			    	//check all genes
			    	for (int j = 0; j <GS[i].getDg().size(); j++){
			    		if (GS[i].getDg().get(j).getColor().equals(c)){
				    		
			    			//normal strand case
				    		if (StrandNormalize == false){
				    			g.draw(this.GS[i].getDg().get(j).getCoordinates());
				    		} else {
				    			g.draw(this.GS[i].getDg().get(j).getStrRevCoordinates());
				    		}
			    		}
			    	}
			    	
			    	//check all motifs
			    	for (int j = 0; j <GS[i].getDm().size(); j++){
			    		if (GS[i].getDm().get(j).getColor().equals(c)){
			    			
			    			if (StrandNormalize == false){
			    				g.draw(this.GS[i].getDm().get(j).getCoordinates());
			    			} else {
			    				g.draw(this.GS[i].getDm().get(j).getStrRevCoordinates());
			    			}
			    		}
			    	}
			    }
			    
			}
		}

		//color corresponding entries in legend
		
		//return to default settings
		g.setStroke(DefaultStroke);
		g.setColor(Color.BLACK);
	}

	//draw center line
	private void draftLines(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < this.GS.length; i++){
			g2d.fillRect((int) GS[i].getBoundingRect().getX(), (int)Math.round(GS[i].getBoundingRect().getCenterY()),
					(int) GS[i].getBoundingRect().getWidth(), GenomeLineThickness);
		}
	}
	
	//draw motifs
	private void draftMotifs(Graphics2D g){
		
		//add all motifs to all backgrounds
		for (int i = 0; i <this.GS.length; i++){
			if (this.GS[i].getDm() != null){	//some motifs exist on this segment
				for (int j = 0; j <this.GS[i].getDm().size(); j++){
					
					//options to display some or all 
					if ((isShowSurrounding() == true) || 
							((isShowSurrounding() == false) && (this.GS[i].getDm().get(j).getMembership() == 0))) {
						
						//options to color extra regions
						if (this.GS[i].getDm().get(j).getMembership() == 0){
							
							//always color core genes with assigned homology color
							g.setPaint(this.GS[i].getDm().get(j).getColor());
							
						} else {
							
							//depending on options, color light gray or original color.
							if (isColorSurrounding() == true){
								g.setPaint(this.GS[i].getDm().get(j).getColor());
							} else {
								g.setPaint(Color.LIGHT_GRAY);
							}
							
						}

						//to Surround with a black border or not?
						boolean SurroundWithBorder = true;
//						if (g.getPaint().equals(Color.LIGHT_GRAY)){
//							SurroundWithBorder = true;
//						}
						//currently = always
						
						//render original ellipse, or inverted strand
						if (StrandNormalize == true){
							
							//draw ellipse
							g.fill(this.GS[i].getDm().get(j).getStrRevCoordinates());
							
							if (SurroundWithBorder){//surround with black border
								g.setPaint(Color.BLACK);
								g.draw(this.GS[i].getDm().get(j).getStrRevCoordinates());
							}

						} else {
						
							//draw ellipse
							g.fill(this.GS[i].getDm().get(j).getCoordinates());
						
							if (SurroundWithBorder){//surround with black border
								g.setPaint(Color.BLACK);
								g.draw(this.GS[i].getDm().get(j).getCoordinates());
							}

						}
						
					}

				}
			}

		}

	}
	
	//draw coordinates
	private void draftCoordinates(Graphics2D g){
		
		//paint vertical bars
		g.setPaint(Color.BLACK);

		if (StrandNormalize == false) {
		
			for (int i = 0; i < GS.length; i++){
			
				g.setPaint(Color.BLACK);
				
				//paint lines + add labels
				//for (int j  = GS[i].getBarPositions().size()-1; j >= 0;  j--){
				for (int j  = 0; j < GS[i].getBarPositions().size();  j++){
					g.fillRect(GS[i].getBarPositions().get(j), (int) GS[i].getBoundingRect().getMinY(), 
							CoordinateBarWidth, GSHeight);
				
					//write label to appropriate place
					TextLayout tl = new TextLayout(GS[i].getBarValues().get(j).toString(),fontStandard,renderContext); 
					tl.draw(g, GS[i].getBarPositions().get(j), (int) GS[i].getBoundingRect().getMaxY()+5);
					
				}
				
				//write contig boundaries
				if (GS[i].getContigBoundaries() != null){
					
					//change color
					g.setPaint(Color.RED);
					
					//paint boundaries
					for (int j = 0; j <GS[i].getContigBoundaries().size(); j++){
						g.fillRect(GS[i].getContigBoundaries().get(j), (int) GS[i].getBoundingRect().getMinY(),
							ContigBoundaryWidth, GSHeight);
					}
				}
				
				g.setPaint(Color.BLACK);
				
				//paint a forward arrow
				int[] xPoints = {(int) (GS[i].getBoundingRect().getMaxX() - ArrowLength),
						(int) (GS[i].getBoundingRect().getMaxX() - ArrowLength), 
						(int) GS[i].getBoundingRect().getMaxX()};
				int[] yPoints = {(int) GS[i].getBoundingRect().getMinY()-ArrowHeight - LabelAboveGS,
						(int) GS[i].getBoundingRect().getMinY() - LabelAboveGS,
						(int)(GS[i].getBoundingRect().getMinY()-(ArrowHeight/2.0) - LabelAboveGS)};
				g.fillPolygon(xPoints, yPoints, 3);
				
				//paint contig name
				TextLayout t2 = new TextLayout(GS[i].getDg().get(0).getBioInfo().getContig(),fontStandard,renderContext);
				
				//paint to panel
				int ContigX = (int) GS[i].getBoundingRect().getMaxX() - ArrowLength - (int) t2.getBounds().getWidth() - 10;
				int ContigY = (int) GS[i].getBoundingRect().getMinY() - LabelAboveGS;// - (int) t2.getBounds().getHeight();
				t2.draw(g, ContigX, ContigY);
			}
				
		} else {
		
			for (int i = 0; i < GS.length; i++){
				
				g.setPaint(Color.BLACK);
				
				//paint lines + add labels
				//for (int j  = GS[i].getBarPositions().size()-1; j >= 0;  j--){
				for (int j  = 0; j < GS[i].getBarPositions().size();  j++){
					g.fillRect(GS[i].getBarPositionsRev().get(j), (int) GS[i].getBoundingRect().getMinY(), 
							CoordinateBarWidth, GSHeight);
				
					//write label to appropriate place
					TextLayout tl = new TextLayout(GS[i].getBarValues().get(j).toString(),fontStandard,renderContext); 
					tl.draw(g, GS[i].getBarPositionsRev().get(j), (int) GS[i].getBoundingRect().getMaxY()+5);
				}				
				
				//write contig boundaries
				if (GS[i].getContigBoundariesRev() != null){
					
					//change color
					g.setPaint(Color.RED);
					
					//paint boundaries
					for (int j = 0; j <GS[i].getContigBoundariesRev().size(); j++){
						g.fillRect(GS[i].getContigBoundariesRev().get(j), (int) GS[i].getBoundingRect().getMinY(),
							ContigBoundaryWidth, GSHeight);
					}
				}

				if (GS[i].isStrRevFlipGenes()){
					
					//reverse arrows are red
					g.setPaint(Color.RED);
					
					//paint a reverse arrow
					int[] xPoints = {(int) GS[i].getBoundingRect().getMaxX(),
							(int) (GS[i].getBoundingRect().getMaxX()),
							(int) (GS[i].getBoundingRect().getMaxX() - ArrowLength), 
							};
					int[] yPoints = {(int)(GS[i].getBoundingRect().getMinY() - LabelAboveGS),
							(int) GS[i].getBoundingRect().getMinY()-ArrowHeight - LabelAboveGS,
							(int) (GS[i].getBoundingRect().getMinY()-(ArrowHeight/2.0) - LabelAboveGS),
							};
					g.fillPolygon(xPoints, yPoints, 3);
					
					g.setPaint(Color.BLACK);
					
					//paint contig name
					TextLayout t2 = new TextLayout(GS[i].getDg().get(0).getBioInfo().getContig(),fontStandard,renderContext);
					
					//paint to panel
					int ContigX = (int) GS[i].getBoundingRect().getMaxX() - ArrowLength - (int) t2.getBounds().getWidth() - 10;
					int ContigY = (int) GS[i].getBoundingRect().getMinY() - LabelAboveGS;// - (int) t2.getBounds().getHeight();
					t2.draw(g, ContigX, ContigY);
					
				} else {
					
					//forward arrows are black
					g.setPaint(Color.BLACK);
					
					//paint a forward arrow
					int[] xPoints = {(int) (GS[i].getBoundingRect().getMaxX() - ArrowLength),
							(int) (GS[i].getBoundingRect().getMaxX() - ArrowLength), 
							(int) GS[i].getBoundingRect().getMaxX()};
					int[] yPoints = {(int) GS[i].getBoundingRect().getMinY()-ArrowHeight - LabelAboveGS,
							(int) GS[i].getBoundingRect().getMinY() - LabelAboveGS,
							(int)(GS[i].getBoundingRect().getMinY()-(ArrowHeight/2.0) - LabelAboveGS)};
					g.fillPolygon(xPoints, yPoints, 3);
					
					//paint contig name
					TextLayout t2 = new TextLayout(GS[i].getDg().get(0).getBioInfo().getContig(),fontStandard,renderContext);
					
					//paint to panel
					int ContigX = (int) GS[i].getBoundingRect().getMaxX() - ArrowLength - (int) t2.getBounds().getWidth() - 10;
					int ContigY = (int) GS[i].getBoundingRect().getMinY() - LabelAboveGS;// - (int) t2.getBounds().getHeight();
					t2.draw(g, ContigX, ContigY);
					
				}
				

			}
			
		}

	}
	
	
	// ----- Classes + methods related to coloring -----------------------//
	
	//class to determine counts of various objects
	public class Count<K, V> extends HashMap<K, V> {

		//serial ID
		private static final long serialVersionUID = 1L;

		// Counts unique objects
	    public void add(K o) {
	        int count = this.containsKey(o) ? ((Integer)this.get(o)).intValue() + 1 : 1;
	        super.put(o, (V) new Integer(count));
	    }
	}
	
	//transform a <String, Int> hashmap into a linked list, sorted by values
	public LinkedList<SharedHomology> SortAndAddColors2Ann(HashMap<String, Integer> passedMap) {

		//retrieve information, separate into appropriate keys + values
		List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		List<Integer> mapValues = new ArrayList<Integer>();
		
		//retrieve values in the same order as keys
		for (int i = 0; i < mapKeys.size(); i++){
			mapValues.add(passedMap.get(mapKeys.get(i)));
		}

	    //initialize output
	   LinkedList<SharedHomology> sortedList = new LinkedList<SharedHomology>();
	    
	    //bubble sorting separate lists
	    for (int i = 0; i < mapValues.size()-1; i++){
	    	for (int j = 0; j < mapValues.size()-1; j++){
	    		
	    		//switch, if possible
	    		if (mapValues.get(j) < mapValues.get(j+1)){
	    		
	    			//store variables in temp
	    			int tempInt = mapValues.get(j);
	    			String tempString = mapKeys.get(j);
	    		
	    			//substitute variables
	    			mapValues.set(j, mapValues.get(j+1));
	    			mapKeys.set(j, mapKeys.get(j+1));
	    		
	    			//replace temp
	    			mapValues.set(j+1, tempInt);
	    			mapKeys.set(j+1, tempString);
	    		}
	    	}
	    }
	    //import a color map
	    Color[] c2 = {new Color(255,0,0),new Color(255,255,255),new Color(0,0,255)};

	    //add to output list
	    for (int i = 0; i < mapValues.size(); i++){
	    	SharedHomology SH = new SharedHomology();
	    	SH.setAnnotation(mapKeys.get(i));
	    	SH.setFrequency(mapValues.get(i));
	    	SH.setOffSetPoint(OffSetPoint);
	    	SH.addColor(i);
	    	SH.setECRONType("annotation");
	    	sortedList.add(SH);
	    }
	    
	    //return list
	    return sortedList;
	}

	//transform a <Int, Int> hashmap into a linked list, sorted by values
	private LinkedList<SharedHomology> SortAndAddColors2Cluster(HashMap<Integer, Integer> passedMap) {
		//retrieve information, separate into appropriate keys + values
		List<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
		List<Integer> mapValues = new ArrayList<Integer>();
		
		//retrieve values in the same order as keys
		for (int i = 0; i < mapKeys.size(); i++){
			mapValues.add(passedMap.get(mapKeys.get(i)));
		}

	    //initialize output
	   LinkedList<SharedHomology> sortedList = new LinkedList<SharedHomology>();
	    
	    //bubble sorting separate lists
	    for (int i = 0; i < mapValues.size()-1; i++){
	    	for (int j = 0; j < mapValues.size()-1; j++){
	    		
	    		//switch, if possible
	    		if (mapValues.get(j) < mapValues.get(j+1)){
	    		
	    			//store variables in temp
	    			int tempInt = mapValues.get(j);
	    			int tempCluster = mapKeys.get(j);
	    		
	    			//substitute variables
	    			mapValues.set(j, mapValues.get(j+1));
	    			mapKeys.set(j, mapKeys.get(j+1));
	    		
	    			//replace temp
	    			mapValues.set(j+1, tempInt);
	    			mapKeys.set(j+1, tempCluster);
	    		}
	    	}
	    }

	    //add to output list
	    for (int i = 0; i < mapValues.size(); i++){
	    	SharedHomology SH = new SharedHomology();
	    	SH.setClusterID(mapKeys.get(i));
	    	SH.setFrequency(mapValues.get(i));
	    	SH.setOffSetPoint(OffSetPoint);
	    	SH.addColor(i);
	    	SH.setECRONType("cluster");
	    	sortedList.add(SH);
	    }
	    
	    //return list
	    return sortedList;
	}

	// ---- Warning Messages -------------------------------------- //

	//contexts with overly large gene groupings are not displayed
	private void showExludedContexts(){
		String Msg = "The following nodes refer to a genomic grouping of size " + RangeLimit + " nt or more,\n";
		Msg = Msg + "and so are not displayed:\n";
		for (String s: ExceededRangeLimit){
			Msg = Msg + s + "\n";
		}
		JOptionPane.showMessageDialog(null, Msg, "Gene Grouping Size Limit Exceeded", JOptionPane.INFORMATION_MESSAGE);
	}
	
	// ---- Genome Display Options -------------------------------------- //
	
	public boolean isShowSurrounding() {
		return ShowSurrounding;
	}

	public void setShowSurrounding(boolean showSurrounding) {
		ShowSurrounding = showSurrounding;
	}

	public boolean isColorSurrounding() {
		return ColorSurrounding;
	}

	public void setColorSurrounding(boolean colorSurrounding) {
		ColorSurrounding = colorSurrounding;
	}

	public boolean isStrandNormalize() {
		return StrandNormalize;
	}

	public void setStrandNormalize(boolean strandNormalize) {
		StrandNormalize = strandNormalize;
	}

	public boolean isShowCoordinates() {
		return ShowCoordinates;
	}

	public void setShowCoordinates(boolean showCoordinates) {
		ShowCoordinates = showCoordinates;
	}
	
	public int getCoordinateBarEvery() {
		return CoordinateBarEvery;
	}

	public void setCoordinateBarEvery(int coordinateBarEvery) {
		CoordinateBarEvery = coordinateBarEvery;
	}

	public JFrame getGeneInfo() {
		return GeneInfo;
	}

	// ----- Mouse Click info  -------------------------------------------//
	
	public LinkedList<SharedHomology> getGeneList() {
		return GeneColorList;
	}
	
	public void setGeneList(LinkedList<SharedHomology> geneList) {
		GeneColorList = geneList;
	}
	
	public GeneColorLegendFrame getGclf() {
		return gclf;
	}

	public void setGclf(GeneColorLegendFrame gclf) {
		this.gclf = gclf;
	}

	public boolean isClickedOnLegend() {
		return ClickedOnLegend;
	}

	public void setClickedOnLegend(boolean clickedOnLegend) {
		ClickedOnLegend = clickedOnLegend;
	}

	public LinkedList<Color> getCurrentlySelectedGeneColors() {
		return CurrentlySelectedGeneColors;
	}

	public void setCurrentlySelectedGeneColors(
			LinkedList<Color> currentlySelectedGeneColors) {
		CurrentlySelectedGeneColors = currentlySelectedGeneColors;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		//left click: genomic information
		if(SwingUtilities.isLeftMouseButton(e)){
		
			//recover clicked draw gene
			DrawObject NewlyClicked = FindCurrentDrawObject(e);
			
			//null case (no gene clicked): release all.
			if (NewlyClicked == null){
				
				//close current information frame, if it exists.
				if (GeneInfo != null){
					GeneInfo.dispose();
				}
				
				GeneInformationIsBeingDisplayed = false;
				
			//current gene case: release all.
			} else if (NewlyClicked == CurrentLeftClickedDrawObject){
				
				// update
				if (GeneInformationIsBeingDisplayed){
					//close current information frame, if it exists.
					if (GeneInfo != null){
						GeneInfo.dispose();
						GeneInformationIsBeingDisplayed = false;
					}
				} else {
					//create new frame
					JFrame Info = MakeGeneInfo(e);
					
					//bring to front
					Info.toFront();
					this.repaint();
					
					//set JFrame to the field.
					this.GeneInfo = Info;
					GeneInformationIsBeingDisplayed = true;
				}

				
			//new location case: assign current clicked here.
			} else {
				
				//close current information frame, if it exists.
				if (GeneInfo != null){
					GeneInfo.dispose();
				}
				
				CurrentLeftClickedDrawObject = NewlyClicked;
				
				//create new frame
				JFrame Info = MakeGeneInfo(e);
				
				//bring to front
				Info.toFront();
				this.repaint();
				
				//set JFrame to the field.
				this.GeneInfo = Info;
				GeneInformationIsBeingDisplayed = true;
			}
			
			//update graphical display
			this.repaint();
		
		//right click: export menu
		} else if (SwingUtilities.isRightMouseButton(e)){
			//check moduls.frm.children -> Frmpiz ->.initComponentsMenu() <201>
						
			//update place clicked
			this.PlaceClicked = e.getPoint();
			
			//trigger pop-up menu display
			this.ExportMenu.show(e.getComponent(),
					e.getXOnScreen(), e.getYOnScreen());

			//reposition appropriately
			this.ExportMenu.setLocation(e.getXOnScreen(),e.getYOnScreen());
		
		//center click: select all by common homology
		} else if (SwingUtilities.isMiddleMouseButton(e)){
			
			//recover clicked draw gene
			DrawObject NewlyClicked = FindCurrentDrawObject(e);

			//null case (no gene clicked): release all.
			if (NewlyClicked == null){
				
				//reset all colors
				CurrentlySelectedGeneColors = new LinkedList<Color>();
				CurrentMiddleClickedDrawObject = null;
									
			//new location case: assign current clicked here.
			} else {
				
				//new location
				CurrentMiddleClickedDrawObject = NewlyClicked;

				//check for shift/ctrl buttons
				if (e.isShiftDown() || e.isControlDown()){
					
					//check and see if this is in the existing list.
					boolean AlreadyInTheList = false;
					if (CurrentlySelectedGeneColors != null){
						for (Color c : CurrentlySelectedGeneColors){
							if (CurrentMiddleClickedDrawObject.getColor().equals(c)){
								AlreadyInTheList = true;
							}
						}
						
						if (AlreadyInTheList){
							CurrentlySelectedGeneColors.remove((Color) CurrentMiddleClickedDrawObject.getColor());
						} else {
							//add to existing list
							CurrentlySelectedGeneColors.add((Color) CurrentMiddleClickedDrawObject.getColor());
						}
					} else {
						//initialize a new list with a single entry.
						this.CurrentlySelectedGeneColors = new LinkedList<Color>();
						CurrentlySelectedGeneColors.add((Color) CurrentMiddleClickedDrawObject.getColor());
					}

				} else {
					//replace existing list with single entry
					this.CurrentlySelectedGeneColors = new LinkedList<Color>();
					CurrentlySelectedGeneColors.add((Color) CurrentMiddleClickedDrawObject.getColor());
				}

			}
			
			//update list in legend panel, if it exists.
			if (this.gclp != null){
				//LinkedList<SharedHomology> UpdatedLegendColors = new LinkedList<SharedHomology>();
				boolean[] UpdatedSelectedRectangles = new boolean[gclp.getSelectedRectangles().length];
				Arrays.fill(UpdatedSelectedRectangles,false);
				for (Color c : CurrentlySelectedGeneColors){
					for (int i = 0; i < UpdatedSelectedRectangles.length; i++){
						if (gclp.getGeneList()[i].getColor().equals(c)){
							UpdatedSelectedRectangles[i] = true;
						}
					}	
				}
				this.gclp.setSelectedRectangles(UpdatedSelectedRectangles);
				this.gclp.repaint();
			}
			
			//update graphical display
			this.repaint();

			//bring gene tags to center
			if (this.GeneInfo != null){
				this.GeneInfo.toFront();
			}
		}

	}

	//Determine the identity of the clicked on gene.
	private DrawObject FindCurrentDrawObject(MouseEvent e) {
	    
		DrawObject SelectedObject = null;
		
		//Check for a gene in the selected area.
	    for (int i = 0; i <GS.length; i++){
	    	for (int j = 0; j <GS[i].getDg().size(); j++){
	    	
	    		//normal strand case
	    		if (StrandNormalize == false){
	    			if (GS[i].getDg().get(j).getCoordinates().contains(e.getPoint())){
	    				SelectedObject = GS[i].getDg().get(j);
	    				break;
	    			}
	    		} else {
	    			if (GS[i].getDg().get(j).getStrRevCoordinates().contains(e.getPoint())){
	    				SelectedObject = GS[i].getDg().get(j);
	    				break;
	    			}
	    		}
	    	}
	    }
	    
	    //Check for a motif - precedence over genes
	    for (int i = 0; i < GS.length; i++){
	    	for (int j = 0; j < GS[i].getDm().size(); j++){
	    		if (StrandNormalize == false){
	    			if (GS[i].getDm().get(j).getCoordinates().contains(e.getPoint())){
	    				SelectedObject = GS[i].getDm().get(j);
	    				break;
	    			}
	    		} else {
	    			if (GS[i].getDm().get(j).getStrRevCoordinates().contains(e.getPoint())){
	    				SelectedObject = GS[i].getDm().get(j);
	    				break;
	    			}
	    		}
	    	}
	    }
	    
	    //return the current object
		return SelectedObject;
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
	
	public JFrame MakeGeneInfo(MouseEvent e){
		
		//create new frame
		JFrame Info = new JFrame();
		Info.setUndecorated(true);
		Info.setBackground(Color.YELLOW);
		Info.setLocation((int)MouseInfo.getPointerInfo().getLocation().getX()+1,(int)MouseInfo.getPointerInfo().getLocation().getY()+1);
		
		//compute + import text to display
		JTextPanewLineNumbers txtToDisplay = ComputeJTextField(e);
		if (txtToDisplay.getText().equals("")){
			Info.setVisible(false);
		} else {
			Info.setVisible(true);
		}
			
		//Create JPanel
		JPanel jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(1,1,1,1);
		jp.add(txtToDisplay, c);
		
		//wrap jpanel around scroll pane
	    JScrollPane scrpan = new JScrollPane(jp);
	    
	    //size details
	    scrpan.setPreferredSize(new Dimension(1000,400));
		Info.setSize(new Dimension(250,txtToDisplay.getNumberOfLines()*18));
		
		Info.add(scrpan);
		jp.setBackground(Color.YELLOW);
		
		return Info;
	}

	public JTextPanewLineNumbers ComputeJTextField(MouseEvent e){
		
		//determine number of headers/footers etc
		int NumberToInclude = 0;
		boolean Options[] = new boolean[6];
		String[] OptionNames = {"Annotation:\n","ClusterID:","Size:","Start:","Stop:","Type:"};
		
		//1
		if (mf.getOp().getCbAnnotation().isSelected()){
			NumberToInclude++;
			this.ShowAnnotation = true;
		} else {
			this.ShowAnnotation = false;
		}
		Options[0] = this.ShowAnnotation;
		
		//2
		if (mf.getOp().getCbClusterID().isSelected()){
			NumberToInclude++;
			this.ShowClusterID = true;
		} else {
			this.ShowClusterID = false;
		}
		Options[1] = this.ShowClusterID;
		
		//3
		if (mf.getOp().getCbSize().isSelected()){
			NumberToInclude++;
			this.ShowSize = true;
		} else {
			this.ShowSize = false;
		}
		Options[2] = this.ShowSize;
		
		//4
		if (mf.getOp().getCbStart().isSelected()){
			NumberToInclude++;
			this.ShowStart = true;
		} else {
			this.ShowStart = false;
		}
		Options[3] = this.ShowStart;
		
		//5
		if (mf.getOp().getCbStop().isSelected()){
			NumberToInclude++;
			this.ShowStop = true;
		} else
			this.ShowStop = false;
		Options[4] = this.ShowStop;
		
		//6
		if (mf.getOp().getCbType().isSelected()){
			NumberToInclude++;
			this.ShowType = true;
		} else {
			this.ShowType = false;
		}
		Options[5] = this.ShowType;

		//JTextField computation
		String[] Headers = new String[NumberToInclude];
		String[] Values = new String[NumberToInclude];
		
		//number of lines
		int NumberOfLines = 1;
		
	    //find the gene in the selected area.
	    for (int i = 0; i <GS.length; i++){
	    	for (int j = 0; j <GS[i].getDg().size(); j++){
	    	
	    		//natural strand case
	    		if (StrandNormalize == false){
	    			if (GS[i].getDg().get(j).getCoordinates().contains(e.getPoint())){
	    				
	    				//re-set count for appropriate sizing.
	    				NumberOfLines = 1;
	    				
	    				int Counter = 0;
	    				for (int k = 0; k <Options.length; k++){
	    					if (Options[k] == true){
	    						
	    						//write option name
	    						Headers[Counter] = OptionNames[k];
	    						String EntryString;
	    						
	    						//retrieve the stored value
	    						if (k == 0){
	    							
	    							//format string into multiple lines
	    							String TheAnnotation = GS[i].getDg().get(j).getBioInfo().getAnnotation();
	    							String[] SegmentedAnnotation = TheAnnotation.split(" ");
	    							
	    							//counters
	    							int WordCounter = 0;
	    							NumberOfLines++;
	    							
	    							//line for entry
	    							String EntryLine = ""; EntryString = "";
	    							Boolean FirstWordOfLine = true;
	    							
//	    							System.out.println(TheAnnotation);
	    							
	    							//while words still remain
	    							while (WordCounter < SegmentedAnnotation.length){
	    								
	    								//build the line
	    								while (EntryLine.length() < CharacterMax && WordCounter < SegmentedAnnotation.length){
	    									if (FirstWordOfLine == true){
	    										EntryLine = SegmentedAnnotation[WordCounter];
	    										FirstWordOfLine = false;
	    									} else {
	    										EntryLine = EntryLine + " " + SegmentedAnnotation[WordCounter];
	    									}
//	    									System.out.println("word: " + SegmentedAnnotation[WordCounter]);
//	    									System.out.println("line: " + EntryLine);
	    									WordCounter++;
	    								}
	    								//System.out.println(EntryLine);
	    								
	    								//add to existing set, and add new line.
	    								EntryString = EntryString + EntryLine + "\n";
	    								EntryLine = "";
	    								FirstWordOfLine = true;
	    								NumberOfLines++;
	    							}
	    							
//	    							System.out.println("out of the while loop!");
//	    							System.out.println(EntryString);
	    							
	    						} else if (k == 1){
	    							int TheCluster = GS[i].getDg().get(j).getBioInfo().getClusterID();
	    							if (TheCluster == 0){
	    								EntryString = "none";
	    							} else {
	    								EntryString = Integer.toString(TheCluster);
	    							}
	    						} else if (k == 2) {
	    							EntryString = Integer.toString((GS[i].getDg().get(j).getBioInfo().getStop()
	    											-GS[i].getDg().get(j).getBioInfo().getStart()+1)) + " nt";
	    						} else if (k == 3) {
	    							EntryString = Integer.toString(GS[i].getDg().get(j).getBioInfo().getStart());
	    						} else if (k == 4) {
	    							EntryString = Integer.toString(GS[i].getDg().get(j).getBioInfo().getStop());
	    						} else {
	    							EntryString = GS[i].getDg().get(j).getBioInfo().getType();
	    						}
	    						
	    						//add a new line after the entry.
	    						if (k != 0){
	    							Values[Counter] = " " + EntryString + "\n";
	    							NumberOfLines++;
	    						} else {
	    							Values[Counter] = " " + EntryString;
	    						}
	    						
	    						//increment counter				
	    						Counter++;
	    					}
	    				}
	    			}
	    			
	    		//corrected strand case
	    		} else {
	    			
	    			if (GS[i].getDg().get(j).getStrRevCoordinates().contains(e.getPoint())){
	    				
	    				//re-set count for appropriate sizing.
	    				NumberOfLines = 1;
	    				
	    				int Counter = 0;
	    				for (int k = 0; k <Options.length; k++){
	    					if (Options[k] == true){
	    						
	    						//write option name
	    						Headers[Counter] = OptionNames[k];
	    						
	    						String EntryString;
	    						//retrieve the stored value
	    						if (k == 0){
	    							
	    							//format string into multiple lines
	    							String TheAnnotation = GS[i].getDg().get(j).getBioInfo().getAnnotation();
	    							String[] SegmentedAnnotation = TheAnnotation.split(" ");
	    							
	    							//counters
	    							int WordCounter = 0;
	    							NumberOfLines++;

	    							//line for entry
	    							String EntryLine = ""; EntryString = "";
	    							Boolean FirstWordOfLine = true;
	    							
	    							//while words still remain
	    							while (WordCounter < SegmentedAnnotation.length){
	    								
	    								//build the line
	    								while (EntryLine.length() < CharacterMax && WordCounter < SegmentedAnnotation.length){
	    									if (FirstWordOfLine == true){
	    										EntryLine = SegmentedAnnotation[WordCounter];
	    										FirstWordOfLine = false;
	    									} else {
	    										EntryLine = EntryLine + " " + SegmentedAnnotation[WordCounter];
	    									}
	    									WordCounter++;
	    								}
	    								//System.out.println(EntryLine);
	    								
	    								//add to existing set, and add new line.
	    								EntryString = EntryString + EntryLine + "\n";
	    								EntryLine = "";
	    								FirstWordOfLine = true;
	    								NumberOfLines++;
	    							}
	    							
	    						} else if (k == 1){
	    							int TheCluster = GS[i].getDg().get(j).getBioInfo().getClusterID();
	    							if (TheCluster == 0){
	    								EntryString = "none";
	    							} else {
	    								EntryString = Integer.toString(TheCluster);
	    							}
	    						} else if (k == 2) {
	    							EntryString = Integer.toString((GS[i].getDg().get(j).getBioInfo().getStop()
	    											-GS[i].getDg().get(j).getBioInfo().getStart()+1)) + " nt";
	    						} else if (k == 3) {
	    							EntryString = Integer.toString(GS[i].getDg().get(j).getBioInfo().getStart());
	    						} else if (k == 4) {
	    							EntryString = Integer.toString(GS[i].getDg().get(j).getBioInfo().getStop());
	    						} else {
	    							EntryString = GS[i].getDg().get(j).getBioInfo().getType();
	    						}
	    						
	    						//add a new line after the entry.
	    						if (k != 0){
	    							Values[Counter] = " " + EntryString + "\n";
	    							NumberOfLines++;
	    						} else {
	    							Values[Counter] = " " + EntryString;
	    						}
	    						
	    						//increment counter				
	    						Counter++;
	    					}
	    				}
	    			}
	    			
	    		}
	    	}
	    	
	    	//check for motifs
	    	if (GS[i].getDm() != null){
	    		//System.out.println("Clicked a motif");
		    	for (int j = 0; j <GS[i].getDm().size(); j++){
		    		
		    		//natural strand case
		    		if (StrandNormalize == false){
		    			if (GS[i].getDm().get(j).getCoordinates().contains(e.getPoint())){
		    				
		    				//re-set count for appropriate sizing.
		    				NumberOfLines = 1;
		    				
		    				int MotifCounter = 0;
		    				for (int k = 0; k <Options.length; k++){
		    					if (Options[k] == true){
		    						
		    						//Headers
			    					Headers[MotifCounter] = OptionNames[k];

			    					String EntryString;
			    					if (k == 0){
			    						Headers[MotifCounter] = "Motif:";
			    						EntryString = GS[i].getDm().get(j).getBioInfo().getMotifName();
			    					} else if (k == 1) {
			    						Headers[MotifCounter] = "Sequence:";
			    						EntryString = GS[i].getDm().get(j).getBioInfo().getSequence();
			    					} else if (k == 2){
		    							EntryString = Integer.toString((GS[i].getDm().get(j).getBioInfo().getStop()
												-GS[i].getDm().get(j).getBioInfo().getStart()+1)) + " nt";
			    					} else if (k == 3) {
			    						EntryString = Integer.toString(GS[i].getDm().get(j).getBioInfo().getStart());
			    					} else if (k == 4) {
			    						EntryString = Integer.toString(GS[i].getDm().get(j).getBioInfo().getStop());
			    					} else {
			    						Headers[MotifCounter] = "Source:";
			    						EntryString = GS[i].getDm().get(j).getBioInfo().getSource();
			    					}
			    						    						
		    						//increment Counter + write info
		    						Values[MotifCounter] = " " + EntryString + "\n";	    							
	    							NumberOfLines++;
		    						MotifCounter++;
		    					}
		    				}
		    			}
		    		} else {
		    			if (GS[i].getDm().get(j).getStrRevCoordinates().contains(e.getPoint())){
		    				
		    				//re-set count for appropriate sizing.
		    				NumberOfLines = 1;
		    				
		    				int MotifCounter = 0;
		    				for (int k = 0; k <Options.length; k++){
		    					if (Options[k] == true){
			    					
		    						//Headers
			    					Headers[MotifCounter] = OptionNames[k];

			    					String EntryString;
			    					if (k == 0){
			    						Headers[MotifCounter] = "Motif:";
			    						EntryString = GS[i].getDm().get(j).getBioInfo().getMotifName();
			    					} else if (k ==1) {
			    						Headers[MotifCounter] = "Sequence:";
			    						EntryString = GS[i].getDm().get(j).getBioInfo().getSequence();
			    					} else if (k == 2){
		    							EntryString = Integer.toString((GS[i].getDm().get(j).getBioInfo().getStop()
												-GS[i].getDm().get(j).getBioInfo().getStart()+1)) + " nt";
			    					} else if (k == 3) {
			    						EntryString = Integer.toString(GS[i].getDm().get(j).getBioInfo().getStart());
			    					} else if (k == 4) {
			    						EntryString = Integer.toString(GS[i].getDm().get(j).getBioInfo().getStop());
			    					} else {
			    						Headers[MotifCounter] = "Source:";
			    						EntryString = GS[i].getDm().get(j).getBioInfo().getSource();
			    					}
			    						    						
		    						//increment Counter + write info
		    						Values[MotifCounter] = " " + EntryString + "\n";	    							
	    							NumberOfLines++;
		    						MotifCounter++;
		    					}
		    				}
		    			}
		    		}
		    	}
	    	}

	    }
	    
		// create a JTextPane + add settings
	    JTextPanewLineNumbers jtp = new JTextPanewLineNumbers();
		jtp.setEditable(false);
		jtp.setBackground(Color.YELLOW);
		
		//retrieve document, and add styles
        StyledDocument doc = jtp.getStyledDocument();
        
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        
        Style s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
        
        //write styles
        for (int i=0; i <Headers.length; i++) {
            try {
				doc.insertString(doc.getLength(), Headers[i], doc.getStyle("bold"));
				doc.insertString(doc.getLength(), Values[i], doc.getStyle("regular"));
			} catch (BadLocationException e1) {
				System.out.println("bad location exception");
			}
       
        }
		
        //note information to compute size
        jtp.setNumberOfLines(NumberOfLines);
        jtp.setCharacterWidthLimit(CharacterMax);
        
		//return computed JTextfield
		return jtp;
		
	}




	class JTextPanewLineNumbers extends JTextPane{
		
		//serial ID
		private static final long serialVersionUID = 1L;
		private int CharacterWidthLimit;
		private int NumberOfLines;
		
		public int getCharacterWidthLimit() {
			return CharacterWidthLimit;
		}
		public void setCharacterWidthLimit(int characterWidthLimit) {
			CharacterWidthLimit = characterWidthLimit;
		}
		public int getNumberOfLines() {
			return NumberOfLines;
		}
		public void setNumberOfLines(int numberOfLines) {
			NumberOfLines = numberOfLines;
		}
		
	}

	public LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> getSplitContexts() {
		return SplitContexts;
	}

	public void setSplitContexts(
			LinkedHashMap<String, LinkedList<GenomicElementAndQueryMatch>> splitContexts) {
		SplitContexts = splitContexts;
	}

	public LinkedHashMap<String, String> getSplitSpeciesNames() {
		return SplitSpeciesNames;
	}

	public void setSplitSpeciesNames(LinkedHashMap<String, String> splitSpeciesNames) {
		SplitSpeciesNames = splitSpeciesNames;
	}

	public LinkedHashMap<String, HashSet<String>> getSplitContigNames() {
		return SplitContigNames;
	}

	public void setSplitContigNames(LinkedHashMap<String, HashSet<String>> splitContigNames) {
		SplitContigNames = splitContigNames;
	}
	
}
