package moduls.frm.children;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import moduls.frm.FrmPrincipalDesk;

public class ManageCustomSeq extends JDialog implements ActionListener{

	//Fields
	//data
	public FrmSearchResults FSR;
	public boolean SelectedStart = false;
	public boolean SelectedStop = false;
	
	//GUI
	public JPanel jp;
	public JTextPane Instructions;
	public JScrollPane EnclosingPane, InstructionPane;
	public JLabel LblStart, LblStop;
	private Dimension InstructionDimensions = new Dimension(500,150);
	
	public String strLblStart = " EXPORT START POSITION:";
	public String strLblStop = " EXPORT STOP POSITION:";
	
	private JRadioButton btnStart_beforeStart, btnStart_afterStart, btnStart_beforeStop, btnStart_afterStop,
		btnStop_beforeStart, btnStop_afterStart, btnStop_beforeStop, btnStop_afterStop;
	private ButtonGroup StartGroup, StopGroup;
	private JTextField txtStart_beforeStart, txtStart_afterStart, txtStart_beforeStop, txtStart_afterStop,
	txtStop_beforeStart, txtStop_afterStart, txtStop_beforeStop, txtStop_afterStop;
	private LinkedHashMap<JRadioButton,Component> StartComponents;
	private LinkedHashMap<JRadioButton,Component> StopComponents;
	
	//buttons - for processing
	private JButton btnOK, btnCancel;
	private String strbtnOK = "OK";
	private String strbtnCancel = "Cancel";
	
	//labels - radio buttons
	private String str_beforeStart = "nt before gene start:";
	private String str_afterStart = "nt after gene start:";
	private String str_beforeStop = "nt before gene stop:";
	private String str_afterStop = "nt after gene stop:";
	
	//labels - initial values
	private String str_Start_txtbeforeStart = "50";
	private String str_Start_txtafterStart = "0";
	private String str_Start_txtbeforeStop = "10";
	private String str_Start_txtafterStop = "0";
	private String str_Stop_txtbeforeStart = "1";
	private String str_Stop_txtafterStart = "10";
	private String str_Stop_txtbeforeStop = "0";
	private String str_Stop_txtafterStop = "10";
	
	//Insets
	public Insets sm = new Insets(1,1,1,1);
	public Insets rad = new Insets(1,20,1,1);
	
	//constructor
	public ManageCustomSeq(FrmSearchResults FSR){
		super();
		
		//associate
		this.FSR = FSR;
		
		//create instructions
		this.CreateInstructions();
		
		//get frame
		this.getFrame();
		
		//build + initialize panel
		this.getPanel();
		
		//Appropraite initializations
		this.GUIInitializations();
	}
	
	//retrieve frame
	public void getFrame(){
		this.setSize(500,400);
		this.setTitle("Sequence Export Parameters");
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		this.setModal(true);
	}
		
	//all appropraite gui initializations
	public void GUIInitializations(){
		
		//disable radio buttons
		for (Component c : StartComponents.values()){
			c.setEnabled(false);
		}
		for (Component c : StopComponents.values()){
			c.setEnabled(false);
		}
		
		//centralize + make visible.
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	//Build instructional message
	public void CreateInstructions(){
		
		// create a JTextPane + add settings
		Instructions = new JTextPane();
		Instructions.setEditable(false);
					
		//retrieve document, and add styles
		StyledDocument doc = Instructions.getStyledDocument();	        
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
        Style s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
        
        //text into document
        try {
			doc.insertString(doc.getLength(), "Instructions:\n\n", doc.getStyle("bold"));
			doc.insertString(doc.getLength(), "As an alternative to exporting only the DNA sequence associated with a gene, " +
					"this tool may be used to export nearby sequence in addition to or instead of the DNA sequence associated with the gene of interest.\n\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "Please specify the range of sequence you would like to export by defining a \"start\" and \"stop\" position " +
					"based on the predicted start or stop site of the gene of interest.\n\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "The \"start\" and \"stop\" in this context refers to the true biological start and stop - so, if the" +
					" gene exists on the reverse strand, the true start position exists at a later genomic position than the true stop position.\n\n", doc.getStyle("Regular"));
			doc.insertString(doc.getLength(), "For example, defining a start at \"nt before gene start: 50\" and a stop at \"nt before gene start position: 1\" will yield the 50" +
					"nt of sequence before the predicted start site, NOT INCLUDING the first nucleotide in the gene of interest.", doc.getStyle("Regular"));	
			doc.insertString(doc.getLength(), "  Defining a start at \"nt before gene start: 50\" and a stop at \"nt before gene stop: 0\" will yield the 50 nt of sequence " +
					"upstream of the gene of interest as well as the entire gene of interest.", doc.getStyle("Regular"));

        } catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	//retrieve panel
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
        /*
         * Instructions / Explanations
         */
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		InstructionPane= new JScrollPane(Instructions);
		InstructionPane.setPreferredSize(InstructionDimensions);
		Instructions.setCaretPosition(0);
		jp.add(InstructionPane, c);		
		gridy++;

		
		/*
		 * START Position
		 */
		
		//Header
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		LblStart = new JLabel(strLblStart);
		LblStart.setBackground(Color.GRAY);
		LblStart.setOpaque(true);
		jp.add(LblStart,c);
		gridy++;
		
		//radio buttons - start group
		btnStart_beforeStart = new JRadioButton(str_beforeStart);
		btnStart_afterStart = new JRadioButton(str_afterStart);
		btnStart_beforeStop = new JRadioButton(str_beforeStop);
		btnStart_afterStop = new JRadioButton(str_afterStop);
		StartGroup = new ButtonGroup();
		StartGroup.add(btnStart_beforeStart);
		StartGroup.add(btnStart_afterStart);
		StartGroup.add(btnStart_beforeStop);
		StartGroup.add(btnStart_afterStop);
		
		//(1)
		//Radio button: Start - before start
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStart_beforeStart.addActionListener(this);
		jp.add(btnStart_beforeStart,c);
		
		//Text field: Start - before start
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStart_beforeStart = new JTextField(str_Start_txtbeforeStart);
		txtStart_beforeStart.setEditable(true);
		jp.add(txtStart_beforeStart,c);
		gridy++;
		
		//(2)
		//Radio button: Start - after start
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStart_afterStart.addActionListener(this);
		jp.add(btnStart_afterStart,c);
		
		//Text field: Start - after start
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStart_afterStart = new JTextField(str_Start_txtafterStart);
		txtStart_afterStart.setEditable(true);
		jp.add(txtStart_afterStart,c);
		gridy++;
		
		//(3)
		//Radio button: Start - before stop
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStart_beforeStop.addActionListener(this);
		jp.add(btnStart_beforeStop,c);
		
		//Text field: Start - before stop
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStart_beforeStop = new JTextField(str_Start_txtbeforeStop);
		txtStart_beforeStop.setEditable(true);
		jp.add(txtStart_beforeStop,c);
		gridy++;
		
		//(4)
		//Radio button: Start - after stop
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStart_afterStop.addActionListener(this);
		jp.add(btnStart_afterStop,c);
		
		//Text field: Start - before stop
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStart_afterStop = new JTextField(str_Start_txtafterStop);
		txtStart_afterStop.setEditable(true);
		jp.add(txtStart_afterStop,c);
		gridy++;
		
		//build hash map - for action listening
		StartComponents = new LinkedHashMap<JRadioButton,Component>();
		StartComponents.put(btnStart_beforeStart,txtStart_beforeStart);
		StartComponents.put(btnStart_afterStart,txtStart_afterStart);
		StartComponents.put(btnStart_beforeStop,txtStart_beforeStop);
		StartComponents.put(btnStart_afterStop,txtStart_afterStop);
		
		/*
		 * STOP Position
		 */
		
		//Header
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		LblStop = new JLabel(strLblStop);
		LblStop.setBackground(Color.GRAY);
		LblStop.setOpaque(true);
		jp.add(LblStop,c);
		gridy++;
		
		//radio buttons - start group
		btnStop_beforeStart = new JRadioButton(str_beforeStart);
		btnStop_afterStart = new JRadioButton(str_afterStart);
		btnStop_beforeStop = new JRadioButton(str_beforeStop);
		btnStop_afterStop = new JRadioButton(str_afterStop);
		StopGroup = new ButtonGroup();
		StopGroup.add(btnStop_beforeStart);
		StopGroup.add(btnStop_afterStart);
		StopGroup.add(btnStop_beforeStop);
		StopGroup.add(btnStop_afterStop);
		
		//(1)
		//Radio button: Stop - before start
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStop_beforeStart.addActionListener(this);
		jp.add(btnStop_beforeStart,c);
		
		//Text field: Start - before start
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStop_beforeStart = new JTextField(str_Stop_txtbeforeStart);
		txtStop_beforeStart.setEditable(true);
		jp.add(txtStop_beforeStart,c);
		gridy++;
		
		//(2)
		//Radio button: Stop - after start
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStop_afterStart.addActionListener(this);
		jp.add(btnStop_afterStart,c);
		
		//Text field: Start - after start
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStop_afterStart = new JTextField(str_Stop_txtafterStart);
		txtStop_afterStart.setEditable(true);
		jp.add(txtStop_afterStart,c);
		gridy++;
		
		//(3)
		//Radio button: Stop - before stop
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStop_beforeStop.addActionListener(this);
		jp.add(btnStop_beforeStop,c);
		
		//Text field: Start - before stop
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStop_beforeStop = new JTextField(str_Stop_txtbeforeStop);
		txtStop_beforeStop.setEditable(true);
		jp.add(txtStop_beforeStop,c);
		gridy++;
		
		//(4)
		//Radio button: Stop - after stop
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = rad;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		btnStop_afterStop.addActionListener(this);
		jp.add(btnStop_afterStop,c);
		
		//Text field: Start - before stop
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		txtStop_afterStop = new JTextField(str_Stop_txtafterStop);
		txtStop_afterStop.setEditable(true);
		jp.add(txtStop_afterStop,c);
		gridy++;
		
		//build hash map - for action listening
		StopComponents = new LinkedHashMap<JRadioButton,Component>();
		StopComponents.put(btnStop_beforeStart,txtStop_beforeStart);
		StopComponents.put(btnStop_afterStart,txtStop_afterStart);
		StopComponents.put(btnStop_beforeStop,txtStop_beforeStop);
		StopComponents.put(btnStop_afterStop,txtStop_afterStop);
		
		/*
		 * BUTTONS
		 */
		
		//ok button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		btnOK = new JButton(strbtnOK);
		btnOK.addActionListener(this);
		jp.add(btnOK,c);
		
		//cancel button
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = sm;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		btnCancel = new JButton(strbtnCancel);
		btnCancel.addActionListener(this);
		jp.add(btnCancel,c);
		
		/*
		 * Add data to frame
		 */
		
		//add data to frame
		EnclosingPane = new JScrollPane(jp);
		this.getContentPane().add(EnclosingPane, BorderLayout.NORTH);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//enable / disable text boxes as appropriate
		for (JRadioButton rad : StartComponents.keySet()){
			Component c = StartComponents.get(rad);
			if (rad.isSelected()){
				c.setEnabled(true);
				SelectedStart = true;
			} else {
				c.setEnabled(false);
			}
		}
		
		for (JRadioButton rad : StopComponents.keySet()){
			Component c = StopComponents.get(rad);
			if (rad.isSelected()){
				c.setEnabled(true);
				SelectedStop = true;
			} else {
				c.setEnabled(false);
			}
		}
		
		//proceed to sequence export
		if (e.getSource().equals(btnOK)){
			
			//first, ensure a selection
			if (SelectedStart && SelectedStop){
				

				//Identify the start and stop positions achieved - ensure proper formatting
				try {
					
					//retrieve data, and proceed
					CustomSeqExportData csed = new CustomSeqExportData();
					
					// ====================== //
					// === Start Position === //
					// ====================== //
					if (btnStart_beforeStart.isSelected()){
						
						//start before the start site.
						csed.start_Before = Integer.parseInt(txtStart_beforeStart.getText().trim());
						csed.start_Start = true;
						
					} else if (btnStart_afterStart.isSelected()){
						
						//start after the start site.
						csed.start_Before = -1 * Integer.parseInt(txtStart_afterStart.getText().trim());
						csed.start_Start = true;
						
					} else if (btnStart_beforeStop.isSelected()){
						
						//start before the stop site.
						csed.start_Before = Integer.parseInt(txtStart_beforeStop.getText().trim());
						csed.start_Start = false;
						
					} else if (btnStart_afterStart.isSelected()){
						
						//start after the stop site.
						csed.start_Before = -1* Integer.parseInt(txtStart_afterStop.getText().trim());
						csed.start_Start = false;
						
					}
					
					// ====================== //
					// === Stop Position ==== //
					// ====================== //
					if (btnStop_beforeStart.isSelected()){
						
						//stop before the start site.
						csed.stop_Before = Integer.parseInt(txtStop_beforeStart.getText().trim());
						csed.stop_Stop = false;
						
					} else if (btnStop_afterStart.isSelected()){
						
						//stop after the start site.
						csed.stop_Before = -1 * Integer.parseInt(txtStop_afterStart.getText().trim());
						csed.stop_Stop = false;
						
					} else if (btnStop_beforeStop.isSelected()){
						
						//stop before the stop site.
						csed.stop_Before = Integer.parseInt(txtStop_beforeStop.getText().trim());
						csed.stop_Stop = true;
						
					} else if (btnStop_afterStart.isSelected()){
						
						//stop after the stop site.
						csed.stop_Before = -1* Integer.parseInt(txtStop_afterStop.getText().trim());
						csed.stop_Stop = true;
						
					}
					
					//debugging: display
					//csed.Display();
					
					//update this data in the parent frame
					FSR.CSED = csed;
					
					//discard this dialog box, which allows sequence export to continue.
					this.dispose();
					
					
				} catch (NumberFormatException ex){
					JOptionPane.showMessageDialog(null,"All coordinates must be described as integers.",
							"Non-integer coordinate value",JOptionPane.ERROR_MESSAGE);
				}

			} else{
				JOptionPane.showMessageDialog(null, "Please select both an export start and stop position.",
						"Start and Stop position not selected",JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
		//avoid sequence export
		if (e.getSource().equals(btnCancel)){
			FSR.CSED = null;
			this.dispose();
		}
		
	}
}
