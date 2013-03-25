package moduls.frm.children;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import genomeObjects.CSDisplayData;

import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;

public class FrmGraph extends JPanel implements MouseListener{
	
	//Fields
	private FrmPrincipalDesk fr;
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

	private CSDisplayData CSD;
	
	//constructor
	public FrmGraph(final FrmPrincipalDesk f, CSDisplayData CSD){
		this.fr = f;
		this.CSD = CSD;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
}
