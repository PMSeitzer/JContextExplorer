package contextViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class GeneColorLegendFrame extends JFrame implements ComponentListener{

	//fields
	//serial ID
	private static final long serialVersionUID = 1L;
	
	//content panels
	private GeneColorLegendPanel gclp;
	private Dimension dim;
	private int ScrollPaneInset = 15;
	private JScrollPane scrp;
	private String ShowOption;
	
	//directional panels
	private JPanel pan_North;
	
	//formatting information
	private Font fontStandard = new Font("Dialog", Font.BOLD, 18);
	
	//constructor
	public GeneColorLegendFrame(RenderedGenomesPanel rgp, LinkedList<SharedHomology> GeneColorList, String ShowOption){
		
		//INITIALIZATIONS
		super("Gene Color Legend");
		this.addComponentListener(this);
		
		//SET SIZE
		//long size - annotations, short size- cluster IDs
		dim = new Dimension(600,300);
//		if (GeneColorList.get(0).getECRONType().contentEquals("annotation")){
//			dim.setSize(600,300);
//		} else {
//			dim.setSize(250,300);
//		}
		//CONTENT PANELS
		this.ShowOption = ShowOption;
		this.gclp = new GeneColorLegendPanel(rgp, this, GeneColorList, ShowOption);
		
		//DIRECTIONAL PANELS
		this.pan_North = new JPanel();
		scrp = new JScrollPane(this.gclp);
		Dimension scrpDim = new Dimension();
		scrpDim.setSize(dim.getWidth()-(ScrollPaneInset*2), dim.getHeight()-4*ScrollPaneInset);
		scrp.setPreferredSize(scrpDim);
		
		//add northern panel
		this.pan_North.add(scrp, BorderLayout.CENTER);
		
		//JFrame properties
		JFrame.setDefaultLookAndFeelDecorated(false);
		this.setSize(dim);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		
		//CUSTOM CLOSE OPERATION - CLOSE SUB FRAMES
		WindowListener DeSelectGenes = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				
				//deselect all selected genes.
				if (gclp.getSelectedColors() != null){
					gclp.setSelectedColors(null);
					gclp.getRgp().repaint();
				}
				
				e.getWindow().dispose();
			}
		};
		
		this.addWindowListener(DeSelectGenes);
		
		//add directionality panels
		this.add(pan_North, BorderLayout.NORTH);

	}

	//new close operation
	
	// ----- setters + getters -------------------------------------//
	
	public Dimension getDim() {
		return dim;
	}

	public void setDim(Dimension dim) {
		this.dim = dim;
	}

	//------- component listener related -------------------------------//
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
		this.scrp.setPreferredSize(new Dimension((int) this.getWidth()-(ScrollPaneInset*2),
				(int)(this.getSize().getHeight()-4*ScrollPaneInset)));
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//gclp information
	public GeneColorLegendPanel getGclp() {
		return gclp;
	}

	public void setGclp(GeneColorLegendPanel gclp) {
		this.gclp = gclp;
	}
}
