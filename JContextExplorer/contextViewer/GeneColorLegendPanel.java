package contextViewer;

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

import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

import moduls.frm.Panels.Jpan_btn_NEW;

public class GeneColorLegendPanel extends JPanel implements MouseListener{


	private static final long serialVersionUID = 1L;
	
	//Fields
	//Content-related
	private LinkedList<SharedHomology> GeneList;
	private String ECronType;
	private GeneColorLegendFrame gclf;
	
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
	
	//font settings
	private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
	private Font fontHeader = new Font("Dialog", Font.BOLD, 16);
	final FontRenderContext renderContext = new FontRenderContext(null, true, true);
	
	//export menu / mouse clicked
	private Point PlaceClicked;
	private JPopupMenu ExportMenu;
	
	//Constructor
	public GeneColorLegendPanel(GeneColorLegendFrame gclf, LinkedList<SharedHomology> genes){
		super();
		this.addMouseListener(this);
		
		//pre-processing
		this.gclf = gclf;
		this.setGeneList(genes);
		this.ECronType = genes.get(0).getECRONType();
		
		//process ready for display
		this.computePanelDimension();
		this.sortColors();
		
		//prepare export menu
		this.InitializeExportMenu();

	}
	
	// ----- pre-processing ----------------------------------//
	
	//determine appropriate dimension for panel, based on number of colors
	public void computePanelDimension(){
		
		//default width: size of window
		Dimension d = this.gclf.getDim();
		int DimTotalWidth = (int) d.getWidth() - 2*this.WidthBuffer;
		
		//longer width, if appropriate
		if (this.ECronType.contentEquals("annotation")){
			for (int i = 0; i <this.GeneList.size(); i++){
				TextLayout label = new TextLayout(this.GeneList.get(i).getAnnotation(),fontStandard,renderContext);

				//determine longest annotation, and re-do rendering
				if ((int)label.getBounds().getWidth() > this.LongestAnnotation){
					this.LongestAnnotation = (int)label.getBounds().getWidth();
				}
			}
		}
		
		if (this.LongestAnnotation + this.RectangleWidth + this.LabelSpacer +2*this.WidthBuffer> DimTotalWidth){
			DimTotalWidth = this.LongestAnnotation + this.RectangleWidth + this.LabelSpacer + 2*this.WidthBuffer; 
		}
		
		//determine height of the table area
		this.WholeColorMappingHeight =
				(this.LegendUnitHeight * this.GeneList.size());
				
		//total height + width depend on other parameters
		int DimTotalHeight = this.HeaderHeight +
				this.VerticalUnderBuffer +
				(this.LegendUnitHeight * this.GeneList.size());
		
		//set dimension
		this.setPanelDim(new Dimension(DimTotalWidth,DimTotalHeight));
		
		//set size of this panel.
		this.setPreferredSize(this.panelDim);
		
	}
	
	//sort colors by increasing cluster ID number or alphabetical annotations
	public void sortColors(){
		if (this.ECronType.contentEquals("cluster")){
			
			//bubble-sorting
			for (int i = 0; i < this.GeneList.size()-1; i++){
				for (int j = 0; j <this.GeneList.size()-1; j++){
					if (this.GeneList.get(j).getClusterID() > this.GeneList.get(j+1).getClusterID()){		
						SharedHomology sh = this.GeneList.get(j);
						this.GeneList.set(j, this.GeneList.get(j+1));
						this.GeneList.set(j+1,sh);
					}
				}
			}
			
		} else {
			
			//bubble-sorting
			for (int i = 0; i < this.GeneList.size()-1; i++){
				for (int j = 0; j <this.GeneList.size()-1; j++){
					int Comparison = this.GeneList.get(j).getAnnotation().compareTo( this.GeneList.get(j+1).getAnnotation());
					if (Comparison > 0){		
						SharedHomology sh = this.GeneList.get(j);
						this.GeneList.set(j, this.GeneList.get(j+1));
						this.GeneList.set(j+1,sh);
					}
				}
			}
			
		}
	}
	
	// ----- drawing-related ---------------------------------//
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;

		this.draftLabels(g2d);
		this.draftColorEntries(g2d);
	}
	
	public void draftLabels(Graphics2D g2d){
		
		//color label
		TextLayout color = new TextLayout("Color",fontHeader,renderContext);
		color.draw(g2d,this.WidthBuffer,this.HeaderHeight);
		this.RectangleWidth = (int) color.getBounds().getWidth();
		
		//appropriate header
		TextLayout header;
		if (this.ECronType.contentEquals("annotation")){
			header = new TextLayout("Annotation",fontHeader,renderContext); 
		} else {
			header = new TextLayout("Cluster ID",fontHeader,renderContext); 
		}

		header.draw(g2d, this.WidthBuffer + this.RectangleWidth + this.LabelSpacer, this.HeaderHeight);
		
		//labels
		TextLayout label;
		if (this.ECronType.contentEquals("annotation")){
			
			for (int i = 0; i <this.GeneList.size(); i++){
				label = new TextLayout(this.GeneList.get(i).getAnnotation(),fontStandard,renderContext);
				label.draw(g2d,this.WidthBuffer + this.RectangleWidth + this.LabelSpacer,
						HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors+LabelVerticalSpacer);
			}
			
		} else {
			for (int i = 0; i <this.GeneList.size(); i++){
			label = new TextLayout(Integer.toString(this.GeneList.get(i).getClusterID()),fontHeader,renderContext);
			label.draw(g2d,this.WidthBuffer + this.RectangleWidth + this.LabelSpacer + (int)(header.getBounds().getWidth()/3.0),
					HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors+LabelVerticalSpacer);
			}
		}
	}
	
	public void draftColorEntries(Graphics2D g2d){
		//first, draw a big white rectangle.
//		g2d.setColor(Color.WHITE);
//		g2d.fillRect(this.WidthBuffer, this.HeaderHeight,
//				this.LegendWidth, this.WholeColorMappingHeight);
		
		//print each color
		for (int i = 0; i <this.GeneList.size(); i++){
			g2d.setColor(this.GeneList.get(i).getColor());
			g2d.fillRect(WidthBuffer,HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors, RectangleWidth, RectangleHeight);
			g2d.setColor(Color.BLACK);
			g2d.drawRect(WidthBuffer,HeaderHeight+(LegendUnitHeight*i)+VerticalSpaceHeaderColors, RectangleWidth, RectangleHeight);
		}
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
		
	
	// ----- mouse-click related -----------------------------//
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//update place clicked
		this.PlaceClicked = e.getPoint();
		
		//trigger pop-up menu display
		this.ExportMenu.show(e.getComponent(),
				e.getXOnScreen(), e.getYOnScreen());
		
		//reposition appropriately
		this.ExportMenu.setLocation(e.getXOnScreen(),e.getYOnScreen());
		
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
	
	public LinkedList<SharedHomology> getGeneList() {
		return GeneList;
	}

	public void setGeneList(LinkedList<SharedHomology> geneList) {
		GeneList = geneList;
	}
	

	public Dimension getPanelDim() {
		return panelDim;
	}

	
	public void setPanelDim(Dimension panelDim) {
		this.panelDim = panelDim;
	}

}
