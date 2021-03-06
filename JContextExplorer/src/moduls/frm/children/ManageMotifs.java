package moduls.frm.children;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSet;
import genomeObjects.ContextSetDescription;
import genomeObjects.GenomicElement;
import genomeObjects.MotifGroup;
import genomeObjects.MotifGroupDescription;
import genomeObjects.SequenceMotif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.biojava3.core.sequence.*;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.children.manageContextSetsv2.btnLoadCS;

public class ManageMotifs extends JDialog implements ActionListener, PropertyChangeListener{

	//Fields
	//Management
	private FrmPrincipalDesk f;
	private String[] SequenceMotifsAsArray;
	private LinkedList<String> SequenceMotifsAsList;
	private ButtonGroup MSType;
	private LinkedHashMap<ButtonModel, LinkedList<Component>> RadioButtonComponents
		= new LinkedHashMap<ButtonModel, LinkedList<Component>>();
	private boolean AcceptableName = true;
	private boolean MotifFilesLoaded = false;
	private MotifGroupDescription ToAdd;
	
	//File import
	private File ReferenceDirectory = null;
	private File[] MotifFiles;
	private boolean isFile = false;
	private File SingleFile = null;
	
	//GUI
	//general use
	private JPanel jp;
	private JProgressBar progressBar;
	
	//insets
	private Insets IndentInsets = new Insets(1,20,1,1);
	private Insets NewSectionInsets = new Insets(10,1,1,1);
	
	//Associate motifs with genomic elements
	private LinkedList<Component> FindAssociationGroup;
	private JCheckBox chkAssociate;
	private String strAssociate = "Associate imported motifs with genomic elements";
	private JRadioButton radNextDownstream, radWithinRange;
	private ButtonGroup GrpAssociateMotifs;
	private String strNextDownstream = "Associate motif with the next downstream genomic element";
	private String strWithinRange = "Associate motif with all genomic elements located within range";
	private LinkedList<Component> DownstreamGroup;
	private LinkedList<Component> DownstreamGroupNext;
	private JTextField LblUpstream, LblDownstream, TxtUpstream, TxtDownstream;
	private JCheckBox chkStrandMatching, chkStrandMatchingDownStream;
	private String strchkStrandMatching = "Require same strand";
	private String strLblUpstream = "Upstream of start:";
	private String strLblDownstream = "Downstream of stop:";
	private String strTxtUpstream = "50";
	private String strTxtDownstream = "none";
	private JRadioButton radAllInternal, radRangeInternal;
	private String strAllInternal = "Include all internal motifs";
	private String strRangeInternal = "Include internal motifs within range";
	private ButtonGroup grpInternalMotifs;
	private LinkedList<Component> ConditionalInternalGroup;
	private JTextField IntLblDownstream, IntLblUpstream, IntTxtDownstream, IntTxtUpstream;
	private String strIntLblDownstream = "Downstream of start:";
	private String strIntTxtDownstream = "50";
	private String strIntTxtUpstream = "none";
	private String strIntLblUpstream = "Upstream of stop:";
	
	//(1) MSFimo
	private LinkedList<Component> MSFimo_group;
	private JRadioButton MSFimo;
	private String strMSFimo = "Load sequence motif(s) from a set of FIMO output files";
	private JButton btnMSFimo;
	private String strbtnFimo = "Load";
	
	//(2) MSCustom
	private LinkedList<Component> MSCustom_group;
	private JRadioButton MSCustom;
	private String strMSCustom = "Load sequence motif(s) from a set of tab-delimited files";
	private JButton btnMSCustom, btnMSCustomInfo;
	private String strbtnMSCustom = "Load";
	private String strbtnMSCustomInfo = "???";
	
	//Add Motifs
	private JLabel Add;
	private JButton btnAddMS;
	private String strAddMS = "Add";
	private JTextField MSName, MSNameLabel;
	private String strMSNameLabel = "Enter Name: ";
	private JTextField LoadedFileName;
	
	//Remove Motifs
	private JTextField MotifSequenceHeader;
	
	private JButton btnRemoveMotif;
	private JLabel Remove;
	private String strRemoveMotif = "Remove";
	private JComboBox<String> SequenceMotifsMenu;
	
	//okay button - close panel
	private JButton btnOK;
	private String strbtnOK = "OK";
	
	//DATA
	//associate motifs
	private int NumTxtDownstream = -1;
	private int NumTxtUpstream = -1;
	private int NumIntTxtUpstream = -1;
	private int NumIntTxtDownstream = -1;
	
	//Constructor
	public ManageMotifs(FrmPrincipalDesk f){
		super();
		this.f = f ;
		this.ReferenceDirectory = f.getFileChooserSource();
		
		//Initialize Sequence Motifs array + list
		SequenceMotifsAsArray = this.f.getPanMotifOptions().getMenuLoadedMotifs();
		SequenceMotifsAsList = new LinkedList<String>();
		for (int i = 0; i <SequenceMotifsAsArray.length; i++){
			SequenceMotifsAsList.add(SequenceMotifsAsArray[i]);
		}
		
		this.setSize(830,600);
		
		this.setTitle("Manage Sequence Motifs");
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		this.setModal(true);
		
		//add panel components
		//attempt - add nested panel structure for inherent organization
		this.getPanel();
		this.DisableComponents();
		this.setContentPane(jp);
		//this.pack(); //to pack or not to pack?

		
		//turn off all checkbox options
		for (Component c : this.FindAssociationGroup){
			c.setEnabled(false);
		}
		for (Component c : DownstreamGroup){
			c.setEnabled(false);
		}
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	//SwingWorkers
	//load from fimo file
	class btnLoadFiles extends SwingWorker<Void,Void>{

		private int OrganismsMapped;
		private int SearchBubbleRange = 10000;		//Start looking for motifs only at 10K
		private boolean isFimo;
		private boolean isDir;
		
		//constructor
		public btnLoadFiles(boolean isFimo, boolean isFile){
			this.isFimo = isFimo;
			this.isDir = !isFile;
		}
		
		//Fields
		@Override
		protected Void doInBackground() throws Exception {
	
			//wait cursor
			f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			LoadedFileName.setVisible(false);
			
			//prepare parameters for association (if appropriate)
			determineAssociationRanges();
			
			if (isDir){
				importFromDir();
			} else {
				importFromSingleFile();
			}
			
			return null;
		}
		
		//import from a directory
		protected void importFromDir(){
			
			//re-initialize loading
			MotifFilesLoaded = false;
			
			//initialize motif group description
			ToAdd = new MotifGroupDescription();
			ToAdd.setName(MSName.getText());
			LinkedList<String> SpeciesNames = new LinkedList<String>();
			
			//Initialize progress bar type things
			MSName.setEditable(false);
			progressBar.setValue(0);
			progressBar.setVisible(true);
			int OrganismsCompleted = 0;
			this.OrganismsMapped = 0;
			int ComputeProgress = 0;
			int TotalFilesForProcess = MotifFiles.length;
			
			// retrieve all files
			for (File fi : MotifFiles) {
				
				//increment counter
				OrganismsCompleted++;
				
				//check if file or directory
				String FimoFile = null;
				String SpeciesName = null;
				
				if (fi.isDirectory()){
					FimoFile = fi.getAbsolutePath() + "/fimo.txt";
					SpeciesName = fi.getName();
				} else {
					FimoFile = fi.getAbsolutePath();
					SpeciesName = fi.getName().replaceFirst("[.][^.]+$", "");
				}
				
				//map this to an organism
				boolean FoundOrganism = false;
				for (String s : f.getOS().getSpeciesNames()){
					if (s.equals(SpeciesName)){
						FoundOrganism = true;
						break;
					}
				}
				
				//if there is an organism to map to, proceed.
				if (FoundOrganism){
					
					//try to read in information from this fimo file + map to organism
					try {
						BufferedReader br = new BufferedReader(new FileReader(FimoFile));
						String Line = null;
						
						//define a new motif group
						MotifGroup MG = new MotifGroup();
						MG.setName(MSName.getText());
						MG.setFileName(fi.getAbsolutePath());
						
						//add all sequences
						while ((Line = br.readLine()) != null){
							
							//ignore commented lines
							if (!Line.startsWith("#")){
								
								//determine imported line
								String ImportedLine[] = Line.split("\t");
								
								//build sequence motif
								SequenceMotif SM = new SequenceMotif();
								
								if (isFimo){
								
								SM.setSource("FIMO");
								SM.setMotifName(MSName.getText());
								SM.setContig(ImportedLine[1]);
								SM.setStart(Integer.parseInt(ImportedLine[2]));
								SM.setStop(Integer.parseInt(ImportedLine[3]));
								SM.setScore(Double.parseDouble(ImportedLine[4]));
								SM.setPvalue(Double.parseDouble(ImportedLine[5]));
								SM.setQvalue(Double.parseDouble(ImportedLine[6]));
								SM.setSequence(ImportedLine[7].toUpperCase());
								
								} else {
									
								SM.setSource("Custom");
								SM.setMotifName(MSName.getText());
								SM.setContig(ImportedLine[0]);
								SM.setStart(Integer.parseInt(ImportedLine[1]));
								SM.setStop(Integer.parseInt(ImportedLine[2]));
								
								//optional: sequence
								if (ImportedLine.length > 3){
									SM.setSequence(ImportedLine[3]);
									
									//optional: annotation
									if (ImportedLine.length > 4){
										SM.setNotes(ImportedLine[4]);
									}
								}
								
								}
								
								//set strand based on order of elements.
								if (SM.getStart() < SM.getStop()){
									SM.setStrand(Strand.POSITIVE);
								} else {
									SM.setStrand(Strand.NEGATIVE);
									
									//in the case of a negative strand, flip positive and negative coordinates.
									int TempStart = SM.getStart();
									SM.setStart(SM.getStop());
									SM.setStop(TempStart);
									
									//also, adjust the string to reverse complement, if appropriate.
									if (isFimo){
										DNASequence d = new DNASequence(SM.getSequence());
										SM.setSequence(d.getReverseComplement().getSequenceAsString().toUpperCase());
									}

								}
								
								//add to list
								MG.getMotifInstances().add(SM);
								
								//Add associations
								addAssociation(SpeciesName, SM);

							}

						}
						
						//add this motif grouping to the species list.
						f.getOS().getSpecies().get(SpeciesName).getMotifs().add(MG);
						
						//increment counter
						OrganismsMapped++;
						SpeciesNames.add(SpeciesName);
						
					} catch (Exception ex) {
						//ex.printStackTrace();
						JOptionPane.showMessageDialog(null, "One or more fields incorrectly formatted, or the input file is not correctly formatted.",
								"Format Error",JOptionPane.ERROR_MESSAGE);
						//System.out.println("Unable to map file: " + FimoFile + " to an organism in the genomic working set.");
					}
				}

				//update progress bar
				ComputeProgress = (int) Math.round(100*((double)OrganismsCompleted/(double)TotalFilesForProcess));
				this.setProgress(ComputeProgress);
			}
			
			//Update final info
			ToAdd.setSpecies(SpeciesNames);
			if (isFimo){
				ToAdd.setSource("FIMO");
			} else {
				ToAdd.setSource("Custom");
			}
			
		}
		
		//import from a single file
		protected void importFromSingleFile(){
			
			//Initialize progress bar type things
			MSName.setEditable(false);
			progressBar.setValue(0);
			progressBar.setVisible(true);
			int LinesCompleted = 0;
			this.OrganismsMapped = 0;
			int ComputeProgress = 0;
			
			//re-initialize loading
			MotifFilesLoaded = false;
			
			//initialize motif group description
			ToAdd = new MotifGroupDescription();
			ToAdd.setName(MSName.getText());
			LinkedList<String> SpeciesNames = new LinkedList<String>();
			
			try {
				//new input file stream
				BufferedReader br = new BufferedReader(new FileReader(SingleFile));
				
				//get total number of lines in file
				String Line = null;
				int LineCounter = 0;
				while ((Line = br.readLine()) != null){
					LineCounter++;
				}
				br.close();
				
				//new input file stream to parse file
				BufferedReader br2 = new BufferedReader(new FileReader(SingleFile));
				Line = null;
				while ((Line = br2.readLine()) != null){
					LinesCompleted++;
					String[] L = Line.split("\t");
					
					//retrieve species
					AnnotatedGenome AG = f.getOS().getSpecies().get(L[0]);
					
					if (AG != null){
						
						//find an original
						boolean AlreadyThere = false;
						MotifGroup MG = null;
						
						for (MotifGroup MGt : AG.getMotifs()){
							if (MGt.getName().equals(MSName.getText())){
								MG = MGt;
								AlreadyThere = true;
							}
						}
						
						//remove the original
						if (AlreadyThere){
							AG.getMotifs().remove(MG);
						}
						
						//if no current MotifGroup exists, initialize a new one
						if (MG == null){
							MG = new MotifGroup();
							MG.setName(MSName.getText());
							MG.setFileName(SingleFile.getAbsolutePath());
						}
						
						//build sequence motif
						SequenceMotif SM = new SequenceMotif();
						SM.setContig(L[1]);
						SM.setStart(Integer.parseInt(L[2]));
						SM.setStop(Integer.parseInt(L[3]));
						SM.setMotifName(MSName.getText());
						SM.setNotes(L[4]);
						SM.setSource("Custom");
						
						//strandedness
						if (SM.getStart() > SM.getStop()){
							SM.setStrand(Strand.NEGATIVE);
						} else {
							SM.setStrand(Strand.POSITIVE);
						}
						
						//add associations
						this.addAssociation(L[0], SM);
						
						//add sequence motif to list
						MG.getMotifInstances().add(SM);

						//add this motif grouping to the species list.
						AG.getMotifs().add(MG);
						
						//increment counter
						OrganismsMapped++;
						SpeciesNames.add(L[0]);
						
						//update progress bar
						ComputeProgress = (int) Math.round(100*((double)LinesCompleted/(double)LineCounter));
						this.setProgress(ComputeProgress);
					}
				}
			} catch (Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "One or more fields incorrectly formatted, or the input file is not correctly formatted.",
						"Format Error",JOptionPane.ERROR_MESSAGE);
				this.setProgress(0);
			}
			
			//Update final info
			ToAdd.setSpecies(SpeciesNames);
			if (isFimo){
				ToAdd.setSource("FIMO");
			} else {
				ToAdd.setSource("Custom");
			}

		}
		
		//Determine nucleotide proximity for localization
		protected void determineAssociationRanges(){
			
			//non-integer parses as -1
			
			//upstream of gene (external)
			try {
				NumTxtUpstream = Integer.parseInt(TxtUpstream.getText());
			} catch (Exception ex){}
			
			//downstream of gene (external)
			try {
				NumTxtDownstream = Integer.parseInt(TxtDownstream.getText());
			} catch (Exception ex) {}
			
			//downstream of gene - internal
			try {
				NumIntTxtDownstream = Integer.parseInt(IntTxtDownstream.getText());
			} catch (Exception ex) {}
			
			//upstream of gene - internal
			try {
				NumIntTxtUpstream = Integer.parseInt(IntTxtUpstream.getText());
			} catch (Exception ex) {}
		}
		
		//Associate a particular motif with one or more genomic elements
		protected void addAssociation(String SpeciesName, SequenceMotif SM){
			
			//option: associate with an annotated genome
			if (chkAssociate.isSelected()){
				AnnotatedGenome AG = f.getOS().getSpecies().get(SpeciesName);
				int DistE_SM;
				GenomicElement TempE = null;
				
				//Center of motif
				double value = 0.5 * ( (double) SM.getStart() + (double) SM.getStop() );
				long Center = Math.round(value);
				
				for (GenomicElement E : AG.getElements()){
					if (radNextDownstream.isSelected()){
						if (E.getContig().contentEquals(SM.getContig())){ // same contig

							//same strand check
							if (chkStrandMatchingDownStream.isSelected()){
								
								if (E.getStrand().equals(SM.getStrand())){ //same strand
									
									if (SM.getStrand().equals(Strand.POSITIVE)){
										
										//case: internal motif
										if (E.getStart() < SM.getStart() && E.getStop() > SM.getStop()){
											E.addAMotif(SM);
											break;

										//case: partially overlapping
										} else if (SM.getStart() < E.getStart() && SM.getStop() > E.getStart()){
											E.addAMotif(SM);
											break;
											
										//case: distance from source
										} else {
											DistE_SM = E.getStart() - SM.getStop();
											if (DistE_SM > 0){
												E.addAMotif(SM);
												break;
											}
										}
										
									} else {
										
										//case: internal motif
										if (E.getStart() < SM.getStart() && E.getStop() > SM.getStop()){
											E.addAMotif(SM);
											break;

										//case: partially overlapping
										} else if (SM.getStart() < E.getStop() && SM.getStop() > E.getStop()){
											E.addAMotif(SM);
											break;
											
										//case: distance from source
										} else {
											DistE_SM =  E.getStart() - SM.getStop();
											if (DistE_SM > 0){
												if (TempE != null){
													TempE.addAMotif(SM);
													TempE = null;
													break;
												}
											} else {
												//store value, in case it is needed.
												TempE = E;
											}
										}
									}

								}
								
							} else {
								
								//case: internal motif
								if (E.getStart() < SM.getStart() && E.getStop() > SM.getStop()){
									E.addAMotif(SM);
									break;

								//case: partially overlapping
								} else if (SM.getStart() < E.getStart() && SM.getStop() > E.getStart()){
									E.addAMotif(SM);
									break;
									
								//case: distance from source
								} else {
									DistE_SM = E.getStart() - SM.getStop();
									if (DistE_SM > 0){
										E.addAMotif(SM);
										break;
									}
								}
								
							}
							
						}
						
					//just search for nearby	
					} else if (radWithinRange.isSelected()) {
						if (E.getContig().contentEquals(SM.getContig())){ // same contig

							//Center of motif
							double valueofE = 0.5 * ( (double) E.getStart() + (double) E.getStop() );
							long CenterofE = Math.round(valueofE);
							
							//Once passed through the search bubble, stop searching - E will only get larger.
							if (CenterofE - Center > SearchBubbleRange){
								break;
							}
														
							//don't even consider the motif if it's out of the search bubble range
							if (Math.abs(Center - CenterofE) < SearchBubbleRange){
							     
								//don't look if a strand-agreement condition is violated, when appropriate.
								if ((chkStrandMatching.isSelected() && SM.getStrand().equals(E.getStrand())) ||
										chkStrandMatching.isSelected() == false){
									
									//positive stranded case
									if (E.getStrand().equals(Strand.POSITIVE)){
										
										//upstream case
										if (NumTxtUpstream >= 0){
											if (E.getStart() - Center <= NumTxtUpstream &&
													E.getStart() - Center >= 0){
												E.addAMotif(SM);
											}
										}
										
										//downstream case
										if (NumTxtDownstream >= 0){
											if (Center - E.getStop() <= NumTxtDownstream &&
													Center - E.getStop() >= 0){
												E.addAMotif(SM);
											}
										}
										
										//internal motif case
										if (radAllInternal.isSelected()){
											if (E.getStop() > Center && Center > E.getStart()){
												E.addAMotif(SM);
											}
										} else {
											if (NumIntTxtDownstream >= 0){
												if (Center - E.getStart() <= NumIntTxtDownstream &&
														Center - E.getStart() >= 0){
													E.addAMotif(SM);
												}
											}
											if (NumIntTxtUpstream >= 0){
												if (E.getStop() - Center <= NumIntTxtUpstream &&
														E.getStop() - Center >= 0){
													E.addAMotif(SM);
												}
											}
										}

										
									} else {
										
										//upstream case
										if (NumTxtUpstream >= 0){
											if (Center - E.getStop() <= NumTxtUpstream &&
													Center - E.getStop() >= 0){
												E.addAMotif(SM);
											}
										}
										
										//downstream case
										if (NumTxtDownstream >= 0){
											if (E.getStart() - Center <= NumTxtDownstream &&
													E.getStart() - Center >= 0){
												E.addAMotif(SM);
											}
										}
										
										//internal motif case
										if (radAllInternal.isSelected()){
											if (E.getStop() > Center && Center > E.getStart()){
												E.addAMotif(SM);
											}
										} else {
											if (NumIntTxtDownstream >= 0){
												if (E.getStop() - Center <= NumIntTxtDownstream &&
														E.getStop() - Center >= 0){
													E.addAMotif(SM);
												}
											}
											if (NumIntTxtUpstream >= 0){
												if (Center - E.getStart() <= NumIntTxtUpstream &&
														Center - E.getStart() >= 0){
													E.addAMotif(SM);
												}
											}
										}
									}
								}
							}
						}
					}
				}	
			}
		}
		
		//conclude all processes
		protected void done(){
			//add this list to the set, for update.
			//SequenceMotifsAsList.add(MSName.getText());
			MotifFilesLoaded = true;
			progressBar.setVisible(false);
			if (isDir){
				LoadedFileName.setText("Sequence motif \"" + MSName.getText() 
						+ "\" successfully mapped to " + this.OrganismsMapped + " organisms.");
			} else {
				LoadedFileName.setText("Sequence motif \"" + MSName.getText() 
						+ "\" successfully mapped " + this.OrganismsMapped + " times.");
			}
			LoadedFileName.setVisible(true);
			btnAddMS.setEnabled(true);
			MSName.setEditable(true);
			
			//wait cursor
			f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}

	//Panel
	private void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		//initial GridBagLayout parameters
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1;
		
		
		/*
		 * ADD MOTIFS
		 */
		
		//Add Motifs Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 6;
		Add = new JLabel(" ADD A SEQUENCE MOTIF");
		Add.setBackground(Color.GRAY);
		Add.setOpaque(true);
		jp.add(Add,c);
		gridy++;
		
		//Name Label
		c.ipady = 7;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(3,3,3,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		MSNameLabel = new JTextField(strMSNameLabel);
		MSNameLabel.setEditable(false);
		jp.add(MSNameLabel,c);
		
		//the name itself text field
		c.ipady = 7;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.insets = new Insets(3,2,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		MSName = new JTextField("");
		MSName.setEditable(true);
		MSName.addActionListener(this);
		//CSName.setColumns(200);
		jp.add(MSName, c);
		gridy++;
		
		//create radio buttons
		//search type button group definition
		MSFimo = new JRadioButton(strMSFimo);
		MSCustom = new JRadioButton(strMSCustom);

		//define button group
		MSType = new ButtonGroup(); 
			MSType.add(MSFimo);
			MSType.add(MSCustom);
		
			
		/*
		 * ASSOCIATION OPTION	
		 */
		
		//radio button designations
		FindAssociationGroup = new LinkedList<Component>();
		DownstreamGroupNext = new LinkedList<Component>();
		DownstreamGroup = new LinkedList<Component>();
		radNextDownstream = new JRadioButton(strNextDownstream);
		radWithinRange = new JRadioButton (strWithinRange);
		GrpAssociateMotifs = new ButtonGroup();
		GrpAssociateMotifs.add(radNextDownstream);
		GrpAssociateMotifs.add(radWithinRange);
		
		c.ipady = 0;
		//check box
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 6;
		c.fill = GridBagConstraints.NONE;
		c.insets = NewSectionInsets;
		chkAssociate = new JCheckBox(strAssociate);
		chkAssociate.setSelected(false);
		chkAssociate.addActionListener(this);
		jp.add(chkAssociate, c);
		gridy++;
		
		//next upstream radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 6;
		c.fill = GridBagConstraints.NONE;
		c.insets = this.IndentInsets;
		radNextDownstream.setSelected(true);
		radNextDownstream.addActionListener(this);
		FindAssociationGroup.add(radNextDownstream);
		jp.add(radNextDownstream, c);
		gridy++;
		
		//Strand matching for downstream
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = IndentInsets;
		chkStrandMatchingDownStream = new JCheckBox(strchkStrandMatching);
		chkStrandMatchingDownStream.setSelected(false);
		DownstreamGroupNext.add(chkStrandMatchingDownStream);
		FindAssociationGroup.add(chkStrandMatchingDownStream);
		jp.add(chkStrandMatchingDownStream, c);
		gridy++;
		
		//within range radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 6;
		c.fill = GridBagConstraints.NONE;
		c.insets = IndentInsets;
		radWithinRange.addActionListener(this);
		FindAssociationGroup.add(radWithinRange);
		jp.add(radWithinRange, c);
		gridy++;
		
		//Require strand matching
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = IndentInsets;
		chkStrandMatching = new JCheckBox(strchkStrandMatching);
		chkStrandMatching.setEnabled(true);
		this.FindAssociationGroup.add(chkStrandMatching);
		this.DownstreamGroup.add(chkStrandMatching);
		jp.add(chkStrandMatching, c);
		
		//options
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = IndentInsets;
		LblUpstream = new JTextField(strLblUpstream);
		LblUpstream.setEditable(false);
		LblUpstream.setHorizontalAlignment(JTextField.RIGHT);
		LblUpstream.setBorder(null);
		this.FindAssociationGroup.add(LblUpstream);
		this.DownstreamGroup.add(LblUpstream);
		jp.add(LblUpstream, c);
		
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		TxtUpstream = new JTextField(strTxtUpstream);
		TxtUpstream.setEditable(true);
		this.FindAssociationGroup.add(TxtUpstream);
		this.DownstreamGroup.add(TxtUpstream);
		jp.add(TxtUpstream, c);
		
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		LblDownstream = new JTextField(strLblDownstream);
		LblDownstream.setEditable(false);
		LblDownstream.setBorder(null);
		LblDownstream.setHorizontalAlignment(JTextField.RIGHT);
		this.FindAssociationGroup.add(LblDownstream);
		this.DownstreamGroup.add(LblDownstream);
		jp.add(LblDownstream, c);
		
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		TxtDownstream = new JTextField(strTxtDownstream);
		TxtDownstream.setEditable(true);
		this.FindAssociationGroup.add(TxtDownstream);
		this.DownstreamGroup.add(TxtDownstream);
		jp.add(TxtDownstream, c);
		gridy++;
		
		//Create button group
		radAllInternal = new JRadioButton(strAllInternal);
		radRangeInternal = new JRadioButton(strRangeInternal);
		grpInternalMotifs = new ButtonGroup();
		grpInternalMotifs.add(radAllInternal);
		grpInternalMotifs.add(radRangeInternal);
		
		//add radio buttons
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = IndentInsets;
		radAllInternal.setSelected(true);
		radAllInternal.addActionListener(this);
		this.FindAssociationGroup.add(radAllInternal);
		this.DownstreamGroup.add(radAllInternal);
		jp.add(radAllInternal, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		radRangeInternal.addActionListener(this);
		this.FindAssociationGroup.add(radRangeInternal);
		this.DownstreamGroup.add(radRangeInternal);
		jp.add(radRangeInternal, c);
		gridy++;
		
		ConditionalInternalGroup = new LinkedList<Component>();
		
		//internal options
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = IndentInsets;
		IntLblDownstream = new JTextField(strIntLblDownstream);
		IntLblDownstream.setEditable(false);
		IntLblDownstream.setHorizontalAlignment(JTextField.RIGHT);
		IntLblDownstream.setBorder(null);
		this.FindAssociationGroup.add(IntLblDownstream);
		this.DownstreamGroup.add(IntLblDownstream);
		ConditionalInternalGroup.add(IntLblDownstream);
		jp.add(IntLblDownstream, c);
		
		c.gridx = 2;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		IntTxtDownstream = new JTextField(strIntTxtDownstream);
		IntTxtDownstream.setEditable(true);
		this.FindAssociationGroup.add(IntTxtDownstream);
		this.DownstreamGroup.add(IntTxtDownstream);
		ConditionalInternalGroup.add(IntTxtDownstream);
		jp.add(IntTxtDownstream, c);
		
		c.gridx = 3;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		IntLblUpstream = new JTextField(strIntLblUpstream);
		IntLblUpstream.setEditable(false);
		IntLblUpstream.setBorder(null);
		IntLblUpstream.setHorizontalAlignment(JTextField.RIGHT);
		this.FindAssociationGroup.add(IntLblUpstream);
		this.DownstreamGroup.add(IntLblUpstream);
		ConditionalInternalGroup.add(IntLblUpstream);
		jp.add(IntLblUpstream, c);
		
		c.gridx = 4;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		IntTxtUpstream = new JTextField(strIntTxtUpstream);
		IntTxtUpstream.setEditable(true);
		this.FindAssociationGroup.add(IntTxtUpstream);
		this.DownstreamGroup.add(IntTxtUpstream);
		ConditionalInternalGroup.add(IntTxtUpstream);
		jp.add(IntTxtUpstream, c);
		gridy++;
			
		//(1) MSFIMO
		c.ipady = 7;
		//grouping
		MSFimo_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 6;
		c.insets = new Insets(10,1,1,1);
		jp.add(MSFimo, c);
		MSFimo.addActionListener(this);
		gridy++;
		
		// load motif sequences from fimo files
		c.ipadx = 0;
		c.insets = new Insets(1,20,1,1);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = gridy;
		btnMSFimo = new JButton(strbtnFimo);
		btnMSFimo.addActionListener(this);
		jp.add(btnMSFimo, c);
		MSFimo_group.add(btnMSFimo);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(MSFimo.getModel(), MSFimo_group);

		//(2) MSCustom
		
		//grouping
		MSCustom_group = new LinkedList<Component>();
		
		//add radio button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.insets = new Insets(10,1,1,1);
		jp.add(MSCustom, c);
		MSCustom.addActionListener(this);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.insets = new Insets(1,1,1,1);
		btnMSCustomInfo = new JButton(strbtnMSCustomInfo);
		btnMSCustomInfo.addActionListener(this);
		btnMSCustomInfo.setHorizontalAlignment(SwingConstants.LEFT);
		//jp.add(btnMSCustomInfo, c);
		MSCustom_group.add(btnMSCustomInfo);
		gridy++;
		
		// load motif sequences from custom files
		c.ipadx = 0;
		c.insets = new Insets(1,20,1,1);
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = gridy;
		btnMSCustom = new JButton(strbtnMSCustom);
		btnMSCustom.addActionListener(this);
		jp.add(btnMSCustom, c);
		MSCustom_group.add(btnMSCustom);
		gridy++;
		
		//add this mapping to hash map.
		RadioButtonComponents.put(MSCustom.getModel(), MSCustom_group);
		
		// progress bar
		c.insets = new Insets(10,1,1,1);
		c.ipady = 0;
		c.gridy = gridy;
		c.gridx = 0;
		c.gridwidth = 4;
		c.ipady = 12;
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(false);
		progressBar.setBorderPainted(false);
		progressBar.setValue(0);
		progressBar.setForeground(Color.BLUE);
		progressBar.setVisible(false);
		jp.add(progressBar, c);
		
		// Loaded File Name
		c.insets = new Insets(10,1,1,1);
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		LoadedFileName = new JTextField();
		LoadedFileName.setText(""); // No file loaded
		LoadedFileName.addActionListener(this);
		LoadedFileName.setEditable(false);
		jp.add(LoadedFileName, c);
		
		//add context set 
		c.gridx = 4;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.ipady = 0;
		c.insets = new Insets(10,1,1,1);
		c.fill = GridBagConstraints.HORIZONTAL;
		btnAddMS = new JButton(strAddMS);
		btnAddMS.addActionListener(this);
		btnAddMS.setEnabled(false);
		jp.add(btnAddMS, c);
		gridy++;
		
		/*
		 * REMOVE MOTIFS
		 */
		
		//Remove Motifs Heading
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 6;
		c.insets = new Insets(3,3,3,3);
		Remove = new JLabel(" REMOVE A SEQUENCE MOTIF");
		Remove.setBackground(Color.GRAY);
		Remove.setOpaque(true);
		jp.add(Remove,c);
		gridy++;
		
		// Sequence Motif Text label
		c.ipady = 5;
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1,1,1,1);
		MotifSequenceHeader = new JTextField();
		MotifSequenceHeader.setText("Sequence Motif:"); // context set currently loaded
		MotifSequenceHeader.addActionListener(this);
		MotifSequenceHeader.setEditable(false);
		jp.add(MotifSequenceHeader, c);
		
		// drop-down menu for Context Sets
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		SequenceMotifsMenu = new JComboBox<String>(SequenceMotifsAsArray);
		SequenceMotifsMenu.addActionListener(this);
		SequenceMotifsMenu.setEnabled(true);
		jp.add(SequenceMotifsMenu, c);
		
		//remove button
		c.gridx = 4;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		btnRemoveMotif = new JButton(strRemoveMotif);
		btnRemoveMotif.addActionListener(this);
		btnRemoveMotif.setEnabled(true);
		jp.add(btnRemoveMotif, c);
		gridy++;
		gridy++;
		
		//submit button
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,1,1,1);
		btnOK = new JButton(strbtnOK);
		btnOK.addActionListener(this);
		jp.add(btnOK, c);
		
		//Finally, add to panel.
		this.getContentPane().add(jp, BorderLayout.NORTH);
		
	}
	
	//disable all components
	private void DisableComponents(){
		for (LinkedList<Component> LLC : RadioButtonComponents.values()){
			for (Component c : LLC){
				c.setEnabled(false);
			}
		}
	}
	
	//enable all components within a single button group
	public void EnableComponents(ButtonModel selectedGroup){

		//enable appropriate  components
		for (ButtonModel bm : RadioButtonComponents.keySet()){
			LinkedList<Component> LL = RadioButtonComponents.get(bm);
			if (bm.equals(selectedGroup)){
				for (Component c : LL){
					c.setEnabled(true);
				}
			} else {
				for (Component c : LL){
					c.setEnabled(false);
				}
			}
		}
	}
	
	//retrieve either directory or data file
	private void getMotifFiles(){
		
		//initialize output
		JFileChooser GetMotifFiles = new JFileChooser();
		GetMotifFiles.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		GetMotifFiles.setDialogTitle("Select directory containing FIMO output or custom motif files (or directories)");

		if (f.getFileChooserSource() != null){
			GetMotifFiles.setCurrentDirectory(f.getFileChooserSource());
		} else {
			GetMotifFiles.setCurrentDirectory(new File("."));
		}
		GetMotifFiles.showOpenDialog(GetMotifFiles);
		
		//retrieve directory containing fimo files
		File ParentDirectory = GetMotifFiles.getSelectedFile();
		this.ReferenceDirectory = GetMotifFiles.getCurrentDirectory();
		
		//check if file could be received
		if (ParentDirectory != null){
	
			//update current directory
			f.setFileChooserSource(GetMotifFiles.getCurrentDirectory());
			
			if (ParentDirectory.isDirectory()){
				
				//retrieve directory
				this.MotifFiles = ParentDirectory.listFiles();
				this.isFile = false;
				
			} else {
				
				//retrieve single file
				this.isFile = true;
				SingleFile = ParentDirectory;
			}
		
		} else {
			
			//no files are currently loaded.
			this.MotifFiles = null;
			this.isFile = false;
		}

	}

	public String[] convertSequenceMotifs(LinkedList<ContextSetDescription> ListOfContextSets){
		
		//initialize output array
		String[] ArrayOfContextSets = new String[ListOfContextSets.size()];
		
		//iterate through array
		for (int i = 0; i < ListOfContextSets.size(); i++){
			ArrayOfContextSets[i] = ListOfContextSets.get(i).getName();
		}
		
		return ArrayOfContextSets;
	}
	@Override
	public void actionPerformed(ActionEvent evt) {

		//check boxes: enable + disable appropriate components
		EnableComponents(MSType.getSelection());
		
		//update appropriate message box
		UpdateMessageBox();
		
		//Load motifs from FIMO files
		if (evt.getSource().equals(btnMSFimo)){
			
			//check if name is acceptable
			CheckName();
			
			if (AcceptableName == true){
				
				//retrieve directory
				getMotifFiles();
				
				//create a new swing worker
				btnLoadFiles LF = new btnLoadFiles(true, isFile);
				LF.addPropertyChangeListener(this);
				LF.execute();
			} 

			
		} 
		
		if (evt.getSource().equals(btnMSCustom)){
			
			//check if name is acceptable
			CheckName();
			
			if (AcceptableName == true){
				
				//retrieve directory
				getMotifFiles();
				
				//create a new swing worker
				btnLoadFiles LF = new btnLoadFiles(false, isFile);
				LF.addPropertyChangeListener(this);
				LF.execute();
			}
		}
		
		if (evt.getSource().equals(this.MSFimo) || evt.getSource().equals(this.MSCustom)){
			this.btnAddMS.setEnabled(true);
		}
		
		//active/deactivate components depending on checkbox
		if (evt.getSource().equals(this.chkAssociate)){
			
			for (Component c : FindAssociationGroup){
				if (!this.chkAssociate.isSelected()){
					c.setEnabled(false);
				} else {
					if (DownstreamGroup.contains(c)){
						if (radWithinRange.isSelected()){
							if (radAllInternal.isSelected()){
								if (!this.ConditionalInternalGroup.contains(c)){
									c.setEnabled(true);
								}
							} else {
								c.setEnabled(true);
							}
						}
					} else {
						c.setEnabled(true);
					}
						
				}
			}

		}
		
		
		if (evt.getSource().equals(radNextDownstream)){
			for (Component c : DownstreamGroup){
				c.setEnabled(false);
			}
			for (Component c : DownstreamGroupNext){
				c.setEnabled(true);
			}
		}
		
		if (evt.getSource().equals(radWithinRange)){
			for (Component c : DownstreamGroup){
				if (radAllInternal.isSelected()){
					if (!this.ConditionalInternalGroup.contains(c)){
						c.setEnabled(true);
					}
				} else {
					c.setEnabled(true);
				}

			}
			for (Component c : DownstreamGroupNext){
				c.setEnabled(false);
			}
		}
		
		if (evt.getSource().equals(radRangeInternal)){
			for (Component c : this.ConditionalInternalGroup){
				c.setEnabled(true);
			}
		}
		
		if (evt.getSource().equals(this.radAllInternal)){
			for (Component c : this.ConditionalInternalGroup){
				c.setEnabled(false);
			}
		}
		
		if (evt.getSource().equals(btnAddMS)){
			
			CheckName();

			if (AcceptableName == true){
				
				//check cases that are not working
				if (!MotifFilesLoaded && MSType.isSelected(MSFimo.getModel())) {
					JOptionPane.showMessageDialog(null, 
							"Select the load button to load fimo-output motif files", "Fimo files not loaded",
							JOptionPane.ERROR_MESSAGE);
				} else if (!MotifFilesLoaded && MSType.isSelected(MSCustom.getModel())){
					JOptionPane.showMessageDialog(null, 
							"Select the load button to load custom-determined motif files", "Custom files not loaded",
							JOptionPane.ERROR_MESSAGE);
				} else {
					
					//add description to the OS
					f.getOS().getMGDescriptions().add(ToAdd);
					SequenceMotifsAsList.add(ToAdd.getName());
					//SequenceMotifsAsArray.add(ToAdd.getName());
					
					//rebuild appropriately
					SequenceMotifsMenu.removeAllItems();
					for (String s : SequenceMotifsAsList){
						if (!s.equals("<none>")){
							SequenceMotifsMenu.addItem(s);
						}
					}

					//update OS
					f.getOS().setMotifNames(SequenceMotifsAsList);
					if (f.getOS().getMotifNames().contains("<none>")){
						f.getOS().getMotifNames().remove("<none>");
					}
					
					//pre-processed sets are reset
					MotifFilesLoaded = false;
					LoadedFileName.setText("Motif sequence \"" + ToAdd.getName() + "\" successfully added to the genomic working set!");
					
				}
				
				
			}

		}
		
		//REMOVE BUTTON
		if (evt.getSource().equals(btnRemoveMotif)){
			
			//set wait cursor
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			Object Item = SequenceMotifsMenu.getSelectedItem();
			
			//update motifs
			//OS level
			for (int i = 0; i < f.getOS().getMGDescriptions().size(); i++){
				if (f.getOS().getMGDescriptions().get(i).getName().equals(SequenceMotifsMenu.getSelectedItem())){
					
					//remove from all annotated genomes if necessary
					for (AnnotatedGenome AG : f.getOS().getSpecies().values()){
						for (MotifGroup MG : AG.getMotifs()){
							if (MG.getName().equals(f.getOS().getCSDs().get(i).getName())){
								AG.getGroupings().remove(MG);
							}
						}
						for (GenomicElement E : AG.getElements()){
							E.removeAMotifByName(f.getOS().getMGDescriptions().get(i).getName());
						}
					}

					//remove the organism set-wide context set description
					f.getOS().getMGDescriptions().remove(i);
				}
			}
			
			//remove from list
			SequenceMotifsAsList.remove(Item);
			
			//remove from JComboBoxes
			//add/remove menu
			SequenceMotifsMenu.removeItem(Item);
			
			//add a new tag, if there are none
			if (SequenceMotifsMenu.getItemCount() == 0){
				SequenceMotifsMenu.addItem("<none>");
			}
			
			//remove from parent frame
			f.getPanMotifOptions().getMenuOfMotifs().removeItem(Item);
			
			//set default cursor
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		//close panel, after updating list.
		if (evt.getSource().equals(btnOK)){
			
			//set the main frame menu to the existing menu.
			//remove all items, then add all items back.
			this.f.getPanMotifOptions().getMenuOfMotifs().removeAllItems();
			LinkedList<String> MotifNames = new LinkedList<String>();
			for (int i = 0; i < f.getOS().getMGDescriptions().size(); i++){
				this.f.getPanMotifOptions().getMenuOfMotifs().addItem(f.getOS().getMGDescriptions().get(i).getName());
				MotifNames.add(f.getOS().getMGDescriptions().get(i).getName());
			}
			
			//if no motifs, reflect this.
			if (this.f.getPanMotifOptions().getMenuLoadedMotifs().length == 0){
				this.f.getPanMotifOptions().getMenuOfMotifs().addItem("<none>");
			}

			//update loaded motifs list in main frame
			this.f.getPanMotifOptions().setLoadedMotifs(MotifNames);
			
			//close this window.
			this.dispose();

		}
		
		//information about formatting.
		if (evt.getSource().equals(this.btnMSCustomInfo)){
			//new InfoFrame();
		}
	}

	//update message box
	public void UpdateMessageBox(){
		
	}
	
	//check if name is acceptable
	public void CheckName(){
		
		//set name as acceptable.
		this.AcceptableName = true;
		
		//name is unacceptable if non-unique
		for (String s : SequenceMotifsAsList){
			if (s.equals(MSName.getText())){
				AcceptableName = false;
			}
		}
		
		//name is also unacceptable if field is empty
		if (MSName.getText().equals("")){
			AcceptableName = false;
		}

		//show error message if appropriate
		if (AcceptableName == false){
			JOptionPane.showMessageDialog(null, "Please give the context set a unique name.",
					"Name Missing or non-unique",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	//update progress bar
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress") {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

}
