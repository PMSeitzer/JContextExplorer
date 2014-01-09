package contextViewer;

import genomeObjects.GenomicElement;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

import moduls.frm.Panels.Jpan_btn_NEW;

public class GeneColorLegendPanel extends JPanel implements MouseListener{


	private static final long serialVersionUID = 1L;
	
	//Fields
	//Content-related
	private SharedHomology[] GeneList;
	private String ECronType;
	private GeneColorLegendFrame gclf;
	private String ItemsToShow;
	private RenderedGenomesPanel rgp;
	
	public RenderedGenomesPanel getRgp() {
		return rgp;
	}

	public void setRgp(RenderedGenomesPanel rgp) {
		this.rgp = rgp;
	}

	//graphics display-related
	//to determine dimensions / sizes
	private Dimension panelDim;
	private int HeaderHeight = 30;
	private int LegendUnitHeight = 30;
	private int WholeColorMappingHeight;
	private int WholeColorMappingWidth;
	private int VerticalUnderBuffer = 20;
	private int LabelSpacer = 10;
	private int VerticalSpaceHeaderColors = 10;
	private int LabelVerticalSpacer = 15;
	private int LongestAnnotation = 0;
	private int WidthBuffer = 30;
	private int LegendWidth = (600-2*WidthBuffer);
	private int RectangleHeight = 20;
	private int RectangleWidth = 41;
	private int SelectedEntryBuffer = 3;
	
	//constant - cluster width
	private int ClusterColumnWidth = 100;
	
	//font settings
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	private Font fontHeader = new Font("Dialog", Font.BOLD, 16);
	final FontRenderContext renderContext = new FontRenderContext(null, true, true);
	
	//export menu / mouse clicked
	private Point PlaceClicked;
	private boolean RectangleSelected = false;
	private JPopupMenu ExportMenu;

	//clickable rectangles
	private boolean[] SelectedRectangles;
	private Rectangle[] Rectangles;
	private int LastSelectedNode = -1; 
	private LinkedList<SharedHomology> SelectedColors;
	
	//Constructor
	public GeneColorLegendPanel(RenderedGenomesPanel rgp, GeneColorLegendFrame gclf, LinkedList<SharedHomology> genes, String ShowOption){
		super();
		this.addMouseListener(this);

		//pre-processing
		this.rgp = rgp;
		this.gclf = gclf;
		GeneList = new SharedHomology[genes.size()];
		for (int i = 0; i <genes.size(); i++){
			GeneList[i] = genes.get(i);
		}
		this.SelectedRectangles = new boolean[genes.size()];
		Arrays.fill(SelectedRectangles, false);
		this.Rectangles = new Rectangle[genes.size()];
		
		this.ECronType = genes.get(0).getECRONType();
		this.ItemsToShow = ShowOption;
		
		if (this.ItemsToShow.contentEquals("Complete")){
			this.collectBioInfo();
		}
		
		//process ready for display
		this.computePanelDimension();
		this.computeRectangles();
		this.sortColors();
		
		//prepare export menu
		this.InitializeExportMenu();

	}
	
	// ----- pre-processing ----------------------------------//
	
	//collect additional information, if necessary
	public void collectBioInfo(){
		
		//check cluster numbers for agreement
		if (this.ECronType.contentEquals("annotation")){
			for (SharedHomology SH : this.GeneList){
					
				//collect all annotations
				HashSet<Integer> Clusters = new HashSet<Integer>();
				for (GenomicElement E : SH.getMembers()){
					Clusters.add(E.getClusterID());
				}
					
				// if there was only one cluster ID among all annotations, map this annotation to the cluster.
				if (Clusters.size() == 1){
					SH.setClusterID(SH.getMembers().get(0).getClusterID());
				} else {
					SH.setClusterID(-1); //indicates a mixed case
				}

			}
		} else {
			for (SharedHomology SH : this.GeneList){
				
				//collect all annotations
				HashSet<String> Clusters = new HashSet<String>();
				for (GenomicElement E : SH.getMembers()){
					Clusters.add(E.getAnnotation());
				}
				
				// if there was only one cluster ID among all annotations, map this annotation to the cluster.
				if (Clusters.size() == 1){
					SH.setAnnotation(SH.getMembers().get(0).getAnnotation().toUpperCase());
				} else {
					SH.setAnnotation("multiple annotations exist"); //indicates a mixed case
				}
			}
		}
	}
	
	//determine appropriate dimension for panel, based on number of colors
	public void computePanelDimension(){
		
		//default width: size of window
		Dimension d = this.gclf.getDim();
		int DimTotalWidth = (int) d.getWidth() - 2*this.WidthBuffer;
		
		//longer width, if appropriate
		//annotation or annotation + cluster cases
		if (this.ItemsToShow.contentEquals("Annotations")){
			for (int i = 0; i <this.GeneList.length; i++){
				//null catching
				if (this.GeneList[i].getAnnotation() == null){
					this.GeneList[i].setAnnotation("");
				}
				TextLayout label = new TextLayout(this.GeneList[i].getAnnotation(),fontStandard,renderContext);

				//determine longest annotation, and re-do rendering
				if ((int)label.getBounds().getWidth() > this.LongestAnnotation){
					this.LongestAnnotation = (int)label.getBounds().getWidth();
				}
			}
		
		} else if (this.ItemsToShow.contentEquals("Complete")) {
			for (int i = 0; i <this.GeneList.length; i++){
				
				//null catching - no annotation provided
				if (this.GeneList[i].getAnnotation() == null){
					this.GeneList[i].setAnnotation("none");
				}
				
				TextLayout label = new TextLayout(this.GeneList[i].getAnnotation(),fontStandard,renderContext);

				//determine longest annotation, and re-do rendering
				if ((int)label.getBounds().getWidth() > this.LongestAnnotation){
					this.LongestAnnotation = (int)label.getBounds().getWidth();
				}
			}
		}
		
		//longsize parameter
		int LongSize = this.LongestAnnotation + this.RectangleWidth + 
				this.LabelSpacer + 2 * this.WidthBuffer + this.ClusterColumnWidth;
		
		//update the width according to longest case
		if (LongSize > DimTotalWidth){
			DimTotalWidth = LongSize;
		}
		
		//determine height of the table area
		this.WholeColorMappingHeight =
				(this.LegendUnitHeight * this.GeneList.length);
				
		//total height + width depend on other parameters
		int DimTotalHeight = this.HeaderHeight +
				this.VerticalUnderBuffer +
				(this.LegendUnitHeight * this.GeneList.length);
		
		//set dimension
		this.setPanelDim(new Dimension(DimTotalWidth,DimTotalHeight));
		
		//set size of this panel.
		this.setPreferredSize(this.panelDim);
		
	}
	
	//add clickable rectangles
	public void computeRectangles(){
		
		//add a rectangle for each
		for (int i = 0; i < GeneList.length; i++){
			if (this.ItemsToShow.contentEquals("Clusters")){
				Rectangles[i] = (new Rectangle(this.WidthBuffer - this.SelectedEntryBuffer, 
						HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors - this.SelectedEntryBuffer,
						(int) (ClusterColumnWidth * 1.5),
						RectangleHeight + 2*this.SelectedEntryBuffer));
			} else {
				Rectangles[i] = (new Rectangle(this.WidthBuffer - this.SelectedEntryBuffer, 
						HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors - this.SelectedEntryBuffer,
						(int)this.panelDim.getWidth() - 2* this.WidthBuffer + 2*this.SelectedEntryBuffer,
						RectangleHeight + 2*this.SelectedEntryBuffer));
			}
		}

	}
	
	//sort colors by increasing cluster ID number or alphabetical annotations
	public void sortColors(){
		if (this.ItemsToShow.contentEquals("Clusters") ||
				this.ItemsToShow.contentEquals("Complete")){
			
			//bubble-sorting by cluster ID
			for (int i = 0; i < this.GeneList.length-1; i++){
				for (int j = 0; j <this.GeneList.length-1; j++){
					if (this.GeneList[j].getClusterID() > this.GeneList[j+1].getClusterID()){		
						SharedHomology sh = this.GeneList[j];
						this.GeneList[j] = this.GeneList[j+1];
						this.GeneList[j+1] = sh;
					}
				}
			}
			
		} else {
			
			//bubble-sorting alphabetically by annotations
			for (int i = 0; i < this.GeneList.length-1; i++){
				for (int j = 0; j <this.GeneList.length-1; j++){
					int Comparison = this.GeneList[j].getAnnotation().compareTo( this.GeneList[j+1].getAnnotation());
					if (Comparison > 0){		
						SharedHomology sh = this.GeneList[j];
						this.GeneList[j] = this.GeneList[j+1];
						this.GeneList[j+1] = sh;
					}
				}
			}
			
		}
	}
	
	// ----- drawing-related ---------------------------------//
	
	//paint all components
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;

		this.draftLabels(g2d);
		this.draftColorEntries(g2d);
		this.draftBoundingRectangles(g2d);
		
	}
	
	//draw labels
	public void draftLabels(Graphics2D g2d){
		
		//color label
		TextLayout color = new TextLayout("Color",fontHeader,renderContext);
		color.draw(g2d,this.WidthBuffer,this.HeaderHeight);
		this.RectangleWidth = (int) color.getBounds().getWidth();
		
		//appropriate header
		TextLayout header;
		if (this.ItemsToShow.contentEquals("Annotations")){
			header = new TextLayout("Annotation",fontHeader,renderContext); 
		} else if (this.ItemsToShow.contentEquals("Clusters")){
			header = new TextLayout("Cluster ID",fontHeader,renderContext); 
		} else {
			header = new TextLayout("Cluster ID",fontHeader,renderContext);
			TextLayout header2;
			header2 = new TextLayout("Annotation",fontHeader,renderContext);
			header2.draw(g2d,this.WidthBuffer + this.RectangleWidth + this.LabelSpacer +
					this.ClusterColumnWidth, this.HeaderHeight);
		}

		//paint headers on the panel
		header.draw(g2d, this.WidthBuffer + this.RectangleWidth + this.LabelSpacer, this.HeaderHeight);

		//labels
		TextLayout label;
		
		if (this.ItemsToShow.contentEquals("Annotations")){
			
			for (int i = 0; i <this.GeneList.length; i++){
				label = new TextLayout(this.GeneList[i].getAnnotation(),fontStandard,renderContext);
				label.draw(g2d,this.WidthBuffer + this.RectangleWidth + this.LabelSpacer,
						HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors+LabelVerticalSpacer);
			}
			
		} else if ((this.ItemsToShow.contentEquals("Clusters"))){
			for (int i = 0; i <this.GeneList.length; i++){
				
				String ClusterNumber;
				if (this.GeneList[i].getClusterID() == -1){
					ClusterNumber = "mixed";
				} else if (this.GeneList[i].getClusterID() == 0){
					ClusterNumber = "none";
				} else {
					ClusterNumber = Integer.toString(this.GeneList[i].getClusterID());
				}
				
			label = new TextLayout(ClusterNumber,fontHeader,renderContext);
			label.draw(g2d,this.WidthBuffer + this.RectangleWidth + this.LabelSpacer,
					HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors+LabelVerticalSpacer);
			}
			
		} else {
			for (int i = 0; i <this.GeneList.length; i++){
			
				String ClusterNumber;
				if (this.GeneList[i].getClusterID() == -1){
					ClusterNumber = "mixed";
				} else if (this.GeneList[i].getClusterID() == 0){
					ClusterNumber = "none";
				} else {
					ClusterNumber = Integer.toString(this.GeneList[i].getClusterID());
				}
					
			label = new TextLayout(ClusterNumber,fontHeader,renderContext);
			label.draw(g2d,this.WidthBuffer + this.RectangleWidth + this.LabelSpacer,
					HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors+LabelVerticalSpacer);
			TextLayout label2 = new TextLayout(this.GeneList[i].getAnnotation(),fontStandard,renderContext);
			label2.draw(g2d,this.WidthBuffer + this.RectangleWidth + this.LabelSpacer +
					this.ClusterColumnWidth, HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors+LabelVerticalSpacer);
			}
			
		}
	}
	
	//draw color entries
	public void draftColorEntries(Graphics2D g2d){

		//print each color
		for (int i = 0; i <this.GeneList.length; i++){
			g2d.setColor(this.GeneList[i].getColor());
			g2d.fillRect(WidthBuffer,HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors, RectangleWidth, RectangleHeight);
			g2d.setColor(Color.BLACK);
			g2d.drawRect(WidthBuffer,HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors, RectangleWidth, RectangleHeight);
		}
	}

	//draft bounding rectangles
	public void draftBoundingRectangles(Graphics2D g2d){
		g2d.setColor(Color.RED);
		//paint appropriate nodenumbers
		for (int i = 0; i < SelectedRectangles.length; i++) {
			if (SelectedRectangles[i] == true){
				g2d.draw(Rectangles[i]);
			} 
		}
		g2d.setColor(Color.BLACK);
	}
	
	// ----- export menu-related ---------------------------------//
	
	//create the pop-up menu object
	private void InitializeExportMenu(){
		
		//create action listener
		ActionListener exportAction = new ActionListener(){
			public void actionPerformed(final ActionEvent evt) {

					// SAVE WHOLE CONTEXT
					try {
						final BufferedImage buff; 
						//draw image and save picture
						if (evt.getActionCommand().equals("Save legend as JPG")){
							buff = drawBufferedImage("jpg");
							savePicture(buff, "jpg");
						} else if (evt.getActionCommand().equals("Save legend as PNG")){
							buff = drawBufferedImage("png");
							savePicture(buff, "png");
						} else if (evt.getActionCommand().equals("Save legend as EPS")){
							String EPSString = drawEPS();
							saveEPS(EPSString);
						}
						
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to export the legend as an image.",
								"Image Writing Problem",JOptionPane.ERROR_MESSAGE);
					}

			}

		};
		
		//set export menu
		this.ExportMenu = new JPopupMenu();
		
		//create menu items
		final JMenuItem me0 = new JMenuItem("Save legend as JPG");
		final JMenuItem me1 = new JMenuItem("Save legend as PNG");
		final JMenuItem me2 = new JMenuItem("Save legend as EPS");
		
		//add action listeners
		me0.addActionListener(exportAction);
		me1.addActionListener(exportAction);
		me2.addActionListener(exportAction);
		
		//build menu
		ExportMenu.add(me0);
		ExportMenu.add(me1);
		ExportMenu.add(me2);

	}
	
	//method to save picture
	private void savePicture(BufferedImage buff, String extension) {
		String sPath;
		String sNameNoExt = Jpan_btn_NEW.getFileNameNoExt();
		final FileDialog fd = new FileDialog(gclf, "Export " +
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
			final FileDialog fd = new FileDialog(gclf, "Export EPS Image", FileDialog.SAVE);
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
		
	
	//mouse click methods
	// ----- mouse-click related -----------------------------//
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		//update place clicked
		this.PlaceClicked = e.getPoint();
		
		//select a set of genes / clusters in the main panel
		if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)){
			
			//clicked on the legend.
			this.rgp.setClickedOnLegend(true);
			
			//initialize
			boolean[] SelectedAfterClick = new boolean[SelectedRectangles.length];
			Arrays.fill(SelectedAfterClick, Boolean.FALSE);
			
			//reset selected color list appropriately.
			SelectedColors = new LinkedList<SharedHomology>();
			
			//update with the current existing set, if appropriate
			if (SelectedRectangles != null){
				if (e.isShiftDown() == true || e.isControlDown() == true){
					SelectedAfterClick = SelectedRectangles;
				}
			}		
			
			//draw a box around the correct coordinate
			for (int i = 0; i < this.Rectangles.length; i++){
				//System.out.println(RectanglesSurroundingLabels[i].getMinX() + " and " + RectanglesSurroundingLabels[i].getMinY());
				if (Rectangles[i].contains(PlaceClicked)){
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
					
					//update
					SelectedRectangles = SelectedAfterClick;
					
					//update last selected node
					LastSelectedNode = i;
				} 
			}

			//update list in other frame
			LinkedList<Color> currentlySelectedGeneColors = new LinkedList<Color>();
			
			//update list
			for (int i = 0; i <SelectedRectangles.length; i++){
				if (SelectedRectangles[i]){
					SelectedColors.add(GeneList[i]);
					currentlySelectedGeneColors.add(GeneList[i].getColor());
				}
			}
			
			//update list in other frame.
			rgp.setCurrentlySelectedGeneColors(currentlySelectedGeneColors);
			
			//redraw
			this.repaint();
			this.rgp.repaint();
			
		}
		
		//export pop-up menu
		if (SwingUtilities.isRightMouseButton(e)){

			//trigger pop-up menu display
			this.ExportMenu.show(e.getComponent(),
					e.getXOnScreen(), e.getYOnScreen());
			
			//reposition appropriately
			this.ExportMenu.setLocation(e.getXOnScreen(),e.getYOnScreen());
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


	// ----- getters + setters --------------------------------//
	
	public SharedHomology[] getGeneList() {
		return GeneList;
	}

	public void setGeneList(SharedHomology[] geneList) {
		GeneList = geneList;
	}
	
	public Dimension getPanelDim() {
		return panelDim;
	}

	public void setPanelDim(Dimension panelDim) {
		this.panelDim = panelDim;
	}

	public LinkedList<SharedHomology> getSelectedColors() {
		return SelectedColors;
	}

	public void setSelectedColors(LinkedList<SharedHomology> selectedColors) {
		SelectedColors = selectedColors;
	}

	public boolean[] getSelectedRectangles() {
		return SelectedRectangles;
	}

	public void setSelectedRectangles(boolean[] selectedRectangles) {
		SelectedRectangles = selectedRectangles;
	}



}
