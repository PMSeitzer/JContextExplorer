package contextViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import inicial.Parametres_Inicials;
import genomeObjects.CSDisplayData;
import genomeObjects.ExtendedCRON;
import genomeObjects.OrganismSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import moduls.frm.ContextLeaf;
import moduls.frm.FrmPrincipalDesk;

public class mainFrame extends JFrame implements ComponentListener{

	//fields
	//serial ID
	private static final long serialVersionUID = 1L;
	
	//content panels
	private OptionPanel op;
	private RenderedGenomesPanel rgp;
	private Dimension dim;
	private int ScrollPaneInset = 15;
	private JScrollPane scrollPane1;
	
	//directional panels
	private JPanel pan_South, pan_North;
	
	//information
	private CSDisplayData CSD;
	//private final CSDisplayData OriginalCSD;
//	public CSDisplayData getOriginalCSD() {
//		return OriginalCSD;
//	}

	private OrganismSet OS;
	private FrmPrincipalDesk fr;
	
	//constructor: include all biological information
	public mainFrame(CSDisplayData csd, OrganismSet os, String title, FrmPrincipalDesk fr){
		
		//INITIALIZATIONS
		super(title);
		this.CSD = csd;
		//this.OriginalCSD = csd;
		this.OS = os;
		this.setFr(fr);
		this.addComponentListener(this);
		
		//RETRIEVE SYSTEM INFO
		Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
		dim = new Dimension();
		double Scale = 0.80;
		dim.setSize(pantalla.getWidth()*(Scale-0.15),pantalla.getHeight()*Scale);
		if (dim.getWidth() < 800){
			dim.setSize(800.0,dim.getHeight());
		}
		
		//CONTENT PANELS
		op = new OptionPanel(this);
		rgp = new RenderedGenomesPanel(this);
		
		//DIRECTIONAL PANELS
		pan_South = new JPanel();
		pan_South.setLayout(new BorderLayout());
		pan_South.add(op);
	
		pan_North = new JPanel();
		scrollPane1 = new JScrollPane(rgp);
		Dimension spdim = new Dimension();
		//spdim.setSize(dim.getWidth()-ScrollPaneInset, (dim.getHeight() - op.getOpdim().getHeight())-ScrollPaneInset);
		spdim.setSize(dim.getWidth()-(ScrollPaneInset*2), (dim.getHeight() - op.getOpdim().getHeight())-4*ScrollPaneInset);
		scrollPane1.setPreferredSize(spdim);
		scrollPane1.getVerticalScrollBar().setUnitIncrement(10);
		
		//scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pan_North.add(scrollPane1, BorderLayout.CENTER);
		
		//SET PROPERTIES OF DESKTOP FRAME
		JFrame.setDefaultLookAndFeelDecorated(false);
		this.setSize(dim);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		
		this.add(pan_South, BorderLayout.SOUTH);
		this.add(pan_North, BorderLayout.NORTH);
		
		//CUSTOM CLOSE OPERATION - CLOSE SUB FRAMES
		WindowListener closeSubFrames = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				
				//dispose all sub-windows
				if (rgp.getGeneInfo() != null){
					rgp.getGeneInfo().dispose();
				}
				if (rgp.getGclf() != null){
					rgp.getGclf().dispose();
				}
				e.getWindow().dispose();
			}
		};
		
		this.addWindowListener(closeSubFrames);
		
		//The main frame CSD should not be altered.
		//this.fr.getCurrentFrame().getInternalFrameData().getQD().setCSD(OriginalCSD);
		
		//update with removed information, if necessary
		if (rgp.getSplitContexts().size() > 0){
			for (String s : rgp.getSplitContexts().keySet()){
				fr.getCurrentFrame().getInternalFrameData().getQD().getCSD().getEC().getContexts().put(s, rgp.getSplitContexts().get(s));
				fr.getCurrentFrame().getInternalFrameData().getQD().getCSD().getEC().getSourceSpeciesNames().put(s, rgp.getSplitSpeciesNames().get(s));
				fr.getCurrentFrame().getInternalFrameData().getQD().getCSD().getEC().getSourceContigNames().put(s, rgp.getSplitContigNames().get(s));
			}
		}
		
//		//debugging
//		System.out.println("Main frame, before viewing");
//		for (ContextLeaf CL : fr.getCurrentFrame().getInternalFrameData().getQD().getCSD().getGraphicalContexts()){
//			System.out.println(CL.getName() + " " +fr.getCurrentFrame().getInternalFrameData().getQD().getCSD().getEC().getContexts().get(CL.getName()));
//		}
	
		this.setVisible(true);
	}

	public CSDisplayData getCSD() {
		return CSD;
	}

	public void setCSD(CSDisplayData cSD) {
		CSD = cSD;
	}

	public OrganismSet getOS() {
		return OS;
	}

	public void setOS(OrganismSet oS) {
		OS = oS;
	}

	public Dimension getDim() {
		return dim;
	}

	public void setDim(Dimension dim) {
		this.dim = dim;
	}

	public RenderedGenomesPanel getRgp() {
		return rgp;
	}

	public void setRgp(RenderedGenomesPanel rgp) {
		this.rgp = rgp;
	}

	public OptionPanel getOp() {
		return op;
	}

	public void setOp(OptionPanel op) {
		this.op = op;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		this.scrollPane1.setPreferredSize(new Dimension((int) this.getWidth()-(ScrollPaneInset*2),
				(int)(this.getSize().getHeight() - op.getOpdim().getHeight())-4*ScrollPaneInset));
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public FrmPrincipalDesk getFr() {
		return fr;
	}

	public void setFr(FrmPrincipalDesk fr) {
		this.fr = fr;
	}



	
	
}
