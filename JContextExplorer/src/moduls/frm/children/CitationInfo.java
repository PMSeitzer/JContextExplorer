package moduls.frm.children;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class CitationInfo extends JDialog implements ActionListener{

	//Fields
	private JTextArea Info;
	private JTextPane Instructions;
	private JPanel jp;
	
	//constructor
	public CitationInfo(){
		
		//this.getInfo();
		this.getCitation();
		this.getFrame();
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	//Methods
	
	//Create frame
	public void getFrame(){
								//width, height
		this.setSize(new Dimension(300, 300));
		this.setTitle("JContextExplorer Citation");
		
	}
	
	//Create info
	public void getInfo(){
		
		String strInfo =
				"JContextExplorer genome context interrogation tool.\n\n"+
				"Version: 2.0\n" +
				"Release Date: May 22, 2013\n\n"+
				"JContextExplorer is free software.\n" +
				"The source code is available at\n" +
				"https://github.com/PMSeitzer/JContextExplorer\n\n" +
				"Questions?\n" +
				"Contact: Phillip Seitzer (pmseitzer@ucdavis.edu)\n\n"+
				"This software was developed by the Facciotti Laboratory,\n"+
				"at UC Davis.\n" +
				"website: " +
				"http://www.bme.ucdavis.edu/facciotti/";
						
		
		Info = new JTextArea(strInfo);
		
		jp = new JPanel();
		jp.add(Info);
		this.add(jp);
	}
	
	//Get citation
	public void getCitation(){
		// create a JTextPane + add settings
		Instructions = new JTextPane();
		Instructions.setEditable(false);
					
		//retrieve document, and add styles
		StyledDocument doc = Instructions.getStyledDocument();	        
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
        Style b = doc.addStyle("bold", regular);
        StyleConstants.setBold(b, true);
        Style i = doc.addStyle("italic", regular);
        StyleConstants.setItalic(i, true);
        
        //text into document
        try {
        		
        	doc.insertString(doc.getLength(), "Citation:\n\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "Seitzer, P., Huynh, T. A., & Facciotti, M. T. (2013). JContextExplorer: \n", doc.getStyle("regular"));
        	doc.insertString(doc.getLength(), "\ta tree-based approach to facilitate cross-species\n", doc.getStyle("regular"));
           	doc.insertString(doc.getLength(), "\tgenomic context comparison.  ", doc.getStyle("regular"));
          	doc.insertString(doc.getLength(), "BMC bioinformatics, 14(1), ", doc.getStyle("italic"));
          	doc.insertString(doc.getLength(), "\n\t18. BMC Bioinformatics.", doc.getStyle("regular"));
          	doc.insertString(doc.getLength(), " doi:10.1186/1471-2105-14-18\n", doc.getStyle("regular"));

						
			jp = new JPanel();
			jp.add(Instructions);
			this.add(jp);
			
        } catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}

}

