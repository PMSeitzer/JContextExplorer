package haloGUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

public class InfoFrame extends JFrame implements ActionListener{

	//fields
	private JTextPane JTP;
	private String Title;
	private LoadGenomesPanelv2 lgp;
	private JPanel jp;
	private JScrollPane jsp;
	private Dimension ScrPanDim = new Dimension(350, 150);
	private JButton btnOK;
	private int yPosAdj;
	
	//constructor
	public InfoFrame(JTextPane JTP, String Title, LoadGenomesPanelv2 lgp, int yPosAdj){
		
		//store imported info
		this.JTP = JTP;
		this.Title = Title;
		this.lgp = lgp;
		this.yPosAdj = yPosAdj;
		
		//construct frame + panel
		this.getFrame();
		this.getPanel();
		
		//pack frame
		this.pack();
		
		//make frame visible
		this.setVisible(true);
		
	}
		
	//Create JFrame
	private void getFrame() {
		this.setSize(400,400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//this.setLocationRelativeTo(lgp);
		Point p = lgp.getLocationOnScreen();
		p.x = (int)p.getX() + 470;
		p.y = (int)p.getY() + yPosAdj;
		this.setLocation(p);
		this.setTitle(this.Title);
		this.setResizable(false);
	}

	//Create JPanel
	private void getPanel() {
		
		//initialize panel\
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		c.insets = new Insets(1,1,1,1);
		
		//add pre-computed text field to panel		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(5,5,5,5);
	    jsp = new JScrollPane(JTP);
	    jsp.setPreferredSize(ScrPanDim);
	    JTP.setCaretPosition(0);
	    jp.add(jsp, c);
	    
	    //add close button
	    c.gridy = 1;
	    c.gridx = 0;
	    c.gridwidth = 1;
	    c.fill = GridBagConstraints.NONE;
	    btnOK = new JButton("OK");
	    btnOK.addActionListener(this);
	    jp.add(btnOK, c);
	    
	    //add frame to panel
	    this.add(jp);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}

}
