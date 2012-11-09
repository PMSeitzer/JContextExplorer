package moduls.frm.Panels;

	import genomeObjects.CSDisplayData;
import genomeObjects.ContextSetDescription;
import genomeObjects.OrganismSet;
import importExport.DadesExternes;
	import importExport.FitxerDades;
	import inicial.Language;

	import java.awt.Color;
	import java.awt.Cursor;
	import java.awt.Dimension;
	import java.awt.FileDialog;
import java.awt.Font;
	import java.awt.GridBagConstraints;
	import java.awt.GridBagLayout;
	import java.awt.Insets;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.beans.PropertyChangeEvent;
	import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.Set;

	import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
	import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;
	import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
	import javax.swing.JScrollPane;
	import javax.swing.JTextField;
	import javax.swing.SwingWorker;
	import javax.swing.event.InternalFrameEvent;
	import javax.swing.event.InternalFrameListener;

	import methods.Reagrupa;
	import moduls.frm.FrmInternalFrame;
	import moduls.frm.FrmPrincipalDesk;
	import moduls.frm.InternalFrameData;
import moduls.frm.Panels.Jpan_btn.MDComputation;
	import moduls.frm.children.FrmPiz;
import moduls.frm.children.manageContextSetsv2;
	import parser.Fig_Pizarra;
	import tipus.Orientation;
	import tipus.metodo;
	import tipus.tipusDades;
	import definicions.Config;
import definicions.MatriuDistancies;

	public class Jpan_btn_NEW extends JPanel implements ActionListener,
			InternalFrameListener, PropertyChangeListener {

	// ----- Fields -----------------------------------------------//
		
		private static final long serialVersionUID = 1L;

		// Desktop where the dendrogram is to be shown
		private final FrmPrincipalDesk fr;

		//this button
		protected Jpan_btn_NEW jb;

		// Text to show in the buttons
		private String strSubmit, strUpdate, strAnnsearch, strClusearch;
        private String strManageCS = "Add/Remove";
        private String strCancel = "Cancel";
        
		// radio buttons for search type
		private ButtonGroup searchType;
		private JRadioButton annotationSearch, clusterSearch;
		
		// Load and update buttons
		private static JButton btnManage, btnSubmit, btnCancel, btnUpdate;

		// Indicates if the buttons Load or Update are being clicked
		public static boolean buttonClicked = false;

		// Internal frame currently active
		private FrmInternalFrame currentInternalFrame = null;

		// File with the input data
		private static FitxerDades fitx = null; //path to file
		private DadesExternes de; //determined by annotation search / clustering
		
		private static JTextField txtFileName, contextSetHeader, searchField;
		private final Dimension searchFieldSize;
		
		// MultiDendrogram
		private MatriuDistancies multiDendro = null;

		// Progress bar for MultiDendrogram computation
		private JProgressBar progressBar;

		// Menu to select current context set
		private JComboBox contextSetMenu;
		//private LinkedList<String> ContextList = new LinkedList<String>();
		
		// Indicate if the text fields have correct values
		public static boolean precisionCorrect = false;
		public static boolean axisMinCorrect = false;
		public static boolean axisMaxCorrect = false;
		public static boolean axisSeparationCorrect = false;
		public static boolean axisEveryCorrect = false;
		public static boolean axisDecimalsCorrect = false;
		
		private boolean ProceedWithSearch = false;
		
		//Standard font + big font
		private Font fontStandard = new Font("Dialog", Font.BOLD, 10);
		private Font bigFont = new Font("Dialog", Font.BOLD, 14);
		
		//Section labels
		private JLabel ContextSetSelect, SearchGenomes;
		
		
	// ----- New Fields --------------------------------------------//	
		
		//These fields modify the new scrollable tree
		
		private int HorizontalScrollBuffer = 30;
		private int VerticalScrollValue = 1500;
		
		//organism set
		private OrganismSet OS;
		
		private String currentQuery;

		
	// ----- Methods -----------------------------------------------//	
		
		// Swing Worker MultiDendrogram computation
	 	class MDComputation extends SwingWorker<Void, Void> {
			private final String action;
			private final tipusDades typeData;
			private final metodo method;
			private final int precision;
			private final int nbElements;
			private double minBase;

			public MDComputation(final String action, final tipusDades typeData,
					final metodo method, final int precision, final int nbElements,
					double minBase) {
				this.action = action;
				this.typeData = typeData;
				this.method = method;
				this.precision = precision;
//				this.precision = 2;
				
				this.nbElements = nbElements;
				this.minBase = minBase;
//				System.out.println("Step 2");
			}

			@Override
			public Void doInBackground() {
//				System.out.println("Step 2.5");
				Reagrupa rg;
				MatriuDistancies mdNew;
				double b;
				int progress;

				// Initialize progress property
				progress = 0;
				setProgress(progress);
				//System.out.println("Cardinality is " + multiDendro.getCardinalitat());
				while (multiDendro.getCardinalitat() > 1) {
					try {
						
//						System.out.println("------");
//						System.out.println(multiDendro);
//						System.out.println(typeData);
//						System.out.println(method);
//						System.out.println(precision);
						
						//CLUSTERING FROM DISTANCES DATA
						rg = new Reagrupa(multiDendro, typeData, method, precision);
						
						mdNew = rg.Recalcula();
						//System.out.println("mdnew = " + mdNew.toString());
						
						//SET THE CURRENT MULTIDENDROGRAM TO THE RESULT FROM RG.RECALCULA()
						multiDendro = mdNew;
						
						b = multiDendro.getArrel().getBase();
						if ((b < minBase) && (b != 0)) {
							minBase = b;
						}
						progress = 100
								* (nbElements - multiDendro.getCardinalitat())
								/ (nbElements - 1);
						setProgress(progress);
					} catch (final Exception e) {
						//showError(e.getMessage());
						showError("problems in calculating dendrogram.");
					}
				}
				return null;
			}

			@Override
			public void done() {
//				System.out.println("Step 3");
//				System.out.println(minBase);
				multiDendro.getArrel().setBase(minBase);
				showCalls(action);
				progressBar.setString("");
				progressBar.setBorderPainted(false);
				progressBar.setValue(0);
				fr.setCursor(null); // turn off the wait cursor
			}
		}

		public Jpan_btn_NEW(final FrmPrincipalDesk fr) {
			super();
			this.fr = fr;
			this.jb = this;
			this.getPanel();
			this.searchFieldSize = searchField.getPreferredSize();
			this.setVisible(true);
			this.OS = fr.getOS();
		}

		private void getPanel() {
			//initialize panel
			this.setLayout(new GridBagLayout());
			this.setBorder(BorderFactory.createTitledBorder("Gene Context Search")); // File
			final GridBagConstraints c = new GridBagConstraints();
			int gridy = 0;
			
			//initial GridBagLayout parameters
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.weightx = 1;
			c.insets = new Insets(1,1,1,1);
			
			//Total grid width: 4
			//Total grid height: 9
			
			//Search genomes section heading
			c.gridx = 0;
			c.gridy = gridy;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 4;
			SearchGenomes = new JLabel(" SEARCH GENOMES");
			SearchGenomes.setBackground(Color.GRAY);
			SearchGenomes.setOpaque(true);
			SearchGenomes.setFont(fontStandard);
			add(SearchGenomes,c);
			gridy++;
			
			//search type button group definition
			strAnnsearch = "Annotation Search";
			strClusearch = "Cluster Number";
			annotationSearch = new JRadioButton(strAnnsearch);
			clusterSearch = new JRadioButton(strClusearch);
			annotationSearch.setFont(fontStandard);
			clusterSearch.setFont(fontStandard);
			searchType = new ButtonGroup();
			searchType.add(annotationSearch);
			searchType.add(clusterSearch);
			
			//set default state
			if (fr.getOS().isGeneClustersLoaded() == true){
				searchType.setSelected(clusterSearch.getModel(), true);
			} else {
				searchType.setSelected(annotationSearch.getModel(), true);
			}
			
			// display on panel
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(annotationSearch, c);
			c.gridx = 2;
			c.gridwidth = 2;
			c.gridy = gridy;
			add(clusterSearch, c);
			gridy++;
			
			
			// Searchable text
			c.ipady = 5;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			searchField = new JTextField();
			searchField.setText(""); // Enter search bar
			searchField.addActionListener(this);
			searchField.setEditable(true);
			searchField.setColumns(20); // this value may wind up changing, depending on the system.
			add(searchField, c);
			gridy++;

			//Submit search
			c.ipady = 0;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			strSubmit = "Submit Search";
			btnSubmit = new JButton(strSubmit);
			btnSubmit.addActionListener(this);
			btnSubmit.setFont(fontStandard);
			add(btnSubmit, c);
			
			//cancel button
			c.ipady = 0;
			c.gridx = 2;
			c.gridy = gridy;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			btnCancel = new JButton(strCancel);
			btnCancel.addActionListener(this);
			btnCancel.setFont(fontStandard);
			//add(btnCancel, c);
			gridy++;
			
			// progress bar
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			progressBar = new JProgressBar(0, 100);
			progressBar.setBorderPainted(false);
			progressBar.setStringPainted(false);
			progressBar.setFont(fontStandard);
			progressBar.setForeground(Color.BLUE);
			progressBar.setValue(0);
			add(progressBar, c);
			gridy++;			
			
			//Genome section heading
			c.gridx = 0;
			c.gridy = gridy;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 4;
			ContextSetSelect = new JLabel(" SELECT CONTEXT SET");
			ContextSetSelect.setBackground(Color.GRAY);
			ContextSetSelect.setOpaque(true);
			ContextSetSelect.setFont(fontStandard);
			add(ContextSetSelect,c);
			gridy++;
			
			// Context Set Text label
			c.ipady = 5;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			//c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(1, 1, 1, 1);
			contextSetHeader = new JTextField();
			contextSetHeader.setText("Context Set:"); // context set currently loaded
			contextSetHeader.addActionListener(this);
			contextSetHeader.setEditable(false);
			contextSetHeader.setFont(fontStandard);
			add(contextSetHeader, c);
			
			// drop-down menu for Context Sets
			c.ipady = 0;
			c.gridx = 1;
			c.gridy = gridy;
			c.gridwidth = 3;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			strUpdate = Language.getLabel(110); // Update
			String[] ContextArray = convertContextSets(fr.getOS().getCSDs());
			contextSetMenu = new JComboBox(ContextArray);
			contextSetMenu.addActionListener(this);
			contextSetMenu.setEnabled(true);
			contextSetMenu.setFont(fontStandard);
			add(contextSetMenu, c);
			gridy++;

			//manage context sets button
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(1, 1, 1, 1);
			btnManage = new JButton(strManageCS);
			btnManage.addActionListener(this);
			btnManage.setEnabled(true);
			btnManage.setFont(fontStandard);
			add(btnManage, c);
			gridy++;
			
//			// btn update
//			c.gridx = 2;
//			c.gridy = gridy;
//			c.gridwidth = 1;
//			c.gridheight = 1;
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.insets = new Insets(1, 1, 1, 1);
//			//strUpdate = Language.getLabel(110); // Update
//			strUpdate = "Update Settings";
//			btnUpdate = new JButton(strUpdate);
//			btnUpdate.addActionListener(this);
//			btnUpdate.setEnabled(true);
//			btnUpdate.setFont(fontStandard);
//			add(btnUpdate, c);
//			gridy++;
			
			// empty space
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			JLabel spacer = new JLabel(" ");
			spacer.setFont(fontStandard);
			add(spacer, c);
			gridy++;
			
			// btn update
			c.ipady = 10;
			gridy++;
			c.gridx = 0;
			c.gridy = gridy;
			c.gridwidth = 4;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(1, 1, 1, 1);
			//strUpdate = Language.getLabel(110); // Update
			//strUpdate = "Update Display Settings";
			strUpdate = "Update Tree";
			btnUpdate = new JButton(strUpdate);
			btnUpdate.addActionListener(this);
			btnUpdate.setEnabled(false);
			//btnUpdate.setFont(fontStandard);
			btnUpdate.setFont(bigFont);
			add(btnUpdate, c);
			gridy++;
			
//			progressBar.setStringPainted(true);
//			progressBar.setValue(100);
//			progressBar.setBackground(Color.BLUE);

		}

		public static void enableUpdate() {
			if (precisionCorrect && axisMinCorrect && axisMaxCorrect
					&& axisSeparationCorrect && axisEveryCorrect
					&& axisDecimalsCorrect) {
				btnUpdate.setEnabled(true);
			} else {
				btnUpdate.setEnabled(false);
			}
		}

		public static String getFileNameNoExt() {
			String name = "";
			if (fitx != null) {
				name = fitx.getNomNoExt();
			}
			return name;
		}

		public static void setFileName(String name) {
			txtFileName.setText(name);
		}

		public MatriuDistancies getMatriu() {
			return de.getMatriuDistancies();
		}

		// BUTTONS PUSHED -> LOAD FILE OR UPDATE TREE
		@Override
		public void actionPerformed(final ActionEvent evt) {
			String action = null;
			FitxerDades fitxTmp;
			boolean ambDades = false;
			InternalFrameData ifd;
			double minBase;
			MDComputation mdComputation;
			String query;

			/*
			 * Available actions:
			 * (1) manage contexts	 [new]
			 * (2) update fields	 [borrowed from strUpdate]
			 * (3) annotation search [take place of Reload]
			 * (4) cluster search    [take place of Reload]
			 * (5) Redraw			 [same as before]
			 */
			//System.out.println(contextSetMenu.getSelectedItem().toString());
			//CONVERT BUTTON PUSH TO ACTION
			
			if (evt.getSource().equals(searchField) || evt.getSource().equals(btnSubmit)){
				
				if (!searchField.getText().equals("")) {
					
					//System.out.println("Search field invoked with query:" + searchField.getText());
					if (searchType.getSelection().equals(annotationSearch.getModel())){
						currentQuery = "Search Query: " + searchField.getText();
					} else {
						currentQuery ="Search Query: Cluster(s) " + searchField.getText();
					}
				
					action = "Load";
					buttonClicked = true;
					ambDades = true;
				
				} else {
					showError("Please enter a query in the search bar.");
				}
				
			} else if (evt.getActionCommand().equals(strUpdate)) {
				
				//set wait cursor
				fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				// UPDATE
				buttonClicked = true;
				ifd = currentInternalFrame.getInternalFrameData();
				if ((Jpan_Menu.getTypeData() == ifd.getTypeData())
						&& (Jpan_Menu.getMethod() == ifd.getMethod())
						&& (Jpan_Menu.getPrecision() == ifd.getPrecision())
						&& (Jpan_Menu.getCbDissimilarity().getSelectedItem().toString().equals(
								currentInternalFrame.getInternalPanel().getCSD().getEC().getDissimilarityType()))) {
					action = "Redraw";
				} else {
					action = "Reload";
				}
				ambDades = true;
				
			} else if (evt.getSource().equals(btnManage)){
				action = "manage contexts";
				buttonClicked = true;
				//new manageContextSets(this.fr, fr.getOS().getCSDs(), this);
				new manageContextSetsv2(this.fr, this);
			}

			//CARRY OUT ACTION
			if (ambDades && (action.equals("Load") || action.equals("Reload"))) {
				try {
					
					//DATA SOURCE
					fitx = new FitxerDades();	
					fitx.setNom("");
					fitx.setPath("");
					
					//parse into candidates
					String[] Queries = searchField.getText().split(";");
					
					if (searchType.getSelection().equals(annotationSearch.getModel())){
						
						//before carrying out search, ask user about their search.
						String Hypo = "hypothetical protein";
						String Unk = "Unknown function";
						
						if (Hypo.contains(searchField.getText()) || Unk.contains(searchField.getText()) ||
								searchField.getText().length() <= 3){
							
							String SureYouWantToSearch = "You have entered a search query that may return a large number of results." + "\n"
									+ "Proceeding may cause this program to crash." + "\n"
									+ "Are you sure you would like to proceed?" + "\n";
							
							//ask question, and maybe proceed with search
							int SearchCheck = JOptionPane.showConfirmDialog(null,SureYouWantToSearch,
									"Proceed with search", JOptionPane.YES_NO_CANCEL_OPTION);
							
							if (SearchCheck == JOptionPane.YES_OPTION){
								this.ProceedWithSearch = true;
							} else {
								this.ProceedWithSearch = false;
								de = null; //this will effectively fast-forward to the catch statement
							}
						} else {
							this.ProceedWithSearch = true;
						}
						
						if (this.ProceedWithSearch == true){
						
						//set wait cursor
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
						//get DE from annotation search
						de = OS.AnnotationSearch(Queries,
								contextSetMenu.getSelectedItem().toString(),
								Jpan_Menu.getCbDissimilarity().getSelectedItem().toString(),
								searchField.getText());
						
						}
						//System.out.println("Got to the de.");
					} else {
						LinkedList<Integer> NumQueriesList = new LinkedList<Integer>();
						for (int i = 0; i < Queries.length; i++){
							try {
								NumQueriesList.add(Integer.parseInt(Queries[i].trim()));
							} catch (Exception ex){}
						}
						
						int[] NumQueries = new int[NumQueriesList.size()];
						for (int i = 0; i < NumQueriesList.size(); i++){
							NumQueries[i] = NumQueriesList.get(i);
						}
						
						//set wait cursor
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						de = OS.ClusterSearch(NumQueries,
								contextSetMenu.getSelectedItem().toString(),
								Jpan_Menu.getCbDissimilarity().getSelectedItem().toString(),
								searchField.getText());
						//System.out.println("Got to the de.");
					}
						
					if (action.equals("Load")) {
						Jpan_Menu.setPrecision(de.getPrecisio());
						
					//changing this value changes the computation
					//places after decimal is a function of the precision.
					//Jpan_Menu.setPrecision(2);
					}
					multiDendro = null;
					try {
						multiDendro = de.getMatriuDistancies();
						minBase = Double.MAX_VALUE;
						progressBar.setBorderPainted(true);
						progressBar.setString(null);
						fr.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						// Instances of javax.swing.SwingWorker are not reusable,
						// so we create new instances as needed.
						mdComputation = new MDComputation(action,
							Jpan_Menu.getTypeData(), Jpan_Menu.getMethod(),
							Jpan_Menu.getPrecision(),
							multiDendro.getCardinalitat(), minBase);
						mdComputation.addPropertyChangeListener(this);
						mdComputation.execute();
						
					} catch (final Exception e2) {
						buttonClicked = false;
						//showError(e2.getMessage());
						showError("Unable to call SwingWorker");
					}
				} catch (Exception e1) {
					
					fr.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					buttonClicked = false;
					//showError(e1.getMessage());
					if (searchType.getSelection().equals(annotationSearch.getModel())){
						if (this.ProceedWithSearch == true){
							showError("There were no matches to the query (or queries).");
						}
					} else {
						//String LastCluster = Integer.toString(OS.LargestCluster);
						String errMsg = "There were no matches to the query (or queries).";
						showError(errMsg);
					}
				}
			} else if (ambDades && action.equals("Redraw")) {
				showCalls(action);
			} else {
				buttonClicked = false;
			}
		} 

		private void showCalls(final String action) {
//			System.out.println("Step 4");
			if (action.equals("Reload") || action.equals("Redraw")) {
				currentInternalFrame.doDefaultCloseAction();
			}
			show(action, Jpan_Menu.getMethod(), Jpan_Menu.getPrecision());
			currentInternalFrame.doDefaultCloseAction();
			show(action, Jpan_Menu.getMethod(), Jpan_Menu.getPrecision());
			btnUpdate.setEnabled(true);
			buttonClicked = false;
		}

		public void show(String action, final metodo method, final int precision) {
//			System.out.println("Step 5");
			boolean isUpdate;
			FrmInternalFrame pizarra;
			Config cfg;
			InternalFrameData ifd;
			FrmPiz fPiz;
			Fig_Pizarra figPizarra;

			isUpdate = !action.equals("Load");
//			System.out.println("breakpoint in show");
//			if (action.equals("annotation search") ||
//					action.equals("cluster search"))
//					isUpdate = false;
//			else {
//				isUpdate = true;
//			}
			
			try {

				pizarra = fr.createInternalFrame(isUpdate, method.name());
				cfg = fr.getConfig();
				cfg.setPizarra(pizarra);
				cfg.setFitxerDades(fitx);
				cfg.setMatriu(multiDendro);
				cfg.setHtNoms(de.getTaulaNoms()); //table names

				//determine size of tree rendering based on number of elements
				setVerticalScrollValue(de.getTaulaNoms().size());
				
				if (!cfg.isTipusDistancia()) {
					if (cfg.getOrientacioDendo().equals(Orientation.NORTH)) {
						cfg.setOrientacioDendo(Orientation.SOUTH);
					} else if (cfg.getOrientacioDendo().equals(Orientation.SOUTH)) {
						cfg.setOrientacioDendo(Orientation.NORTH);
					} else if (cfg.getOrientacioDendo().equals(Orientation.EAST)) {
						cfg.setOrientacioDendo(Orientation.WEST);
					} else if (cfg.getOrientacioDendo().equals(Orientation.WEST)) {
						cfg.setOrientacioDendo(Orientation.EAST);
					}
				}
				//ifd = new InternalFrameData(fitx, multiDendro);
				ifd = new InternalFrameData(de, multiDendro);
				pizarra.setInternalFrameData(ifd);
				
				// Title for the child window
				//pizarra.setTitle(fitx.getNom() + " - " + pizarra.getTitle());
				String WindowTitle = currentQuery + " [" + contextSetMenu.getSelectedItem() + "]";
				pizarra.setTitle(WindowTitle);
				
				CSDisplayData CSD = new CSDisplayData();
				CSD.setEC(de.getEC());
				
				//create a new figure panel
				fPiz = new FrmPiz(fr, CSD);
				
				// Set sizes
				fPiz.setSize(pizarra.getSize());
				fPiz.setPreferredSize(pizarra.getSize());
				
				//determine appropriate rendering dimensions
				Dimension d = new Dimension(pizarra.getWidth()-
						HorizontalScrollBuffer, VerticalScrollValue);
				
				fPiz.setPreferredSize(d);
				
				// Call Jpan_Menu -> internalFrameActivated()
				pizarra.setVisible(true);
				if (action.equals("Load") || action.equals("Reload")) {
					Jpan_Menu.ajustaValors(cfg);
				}
				
				
				// Convert tree into figures
				figPizarra = new Fig_Pizarra(multiDendro.getArrel(), cfg);
				
				// Pass figures to the window
				fPiz.setFigures(figPizarra.getFigures());
				fPiz.setConfig(cfg);
				
				//scroll panel, with sizes
				JScrollPane fPizSP = new JScrollPane(fPiz);
				fPizSP.setSize(pizarra.getSize());
				fPizSP.setPreferredSize(pizarra.getSize());

				//unused options
				//fPizSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				//fPizSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				
				//pizarra.add(fPiz);
				pizarra.add(fPizSP);
				pizarra.setInternalPanel(fPiz);
				
				// Current internal frame is the activated frame.
				fr.setCurrentFrame(pizarra);
				
				fr.setCurrentFpizpanel(fPiz);
				//pizarra.setSelectedNodeNumbers(fPiz.getSelectedNodeNumbers());
				
			} catch (final Exception e) {
				e.printStackTrace();
//				showError(e.getMessage());
				showError("problems in show");
			}
		}

		private FitxerDades getFitxerDades() {
			return this.getFitxerDades(System.getProperty("user.dir"));
		}

		//DATA FILE
		private FitxerDades getFitxerDades(final String sPath) {
			
			//use pre-existing 'FileDialog' GUI window to retrieve file
			final FileDialog fd = new FileDialog(fr, Language.getLabel(9),
					FileDialog.LOAD);
			FitxerDades fitx; //Data file type is just a bunch of relevant strings

			fitx = new FitxerDades();
			fd.setDirectory(sPath);
			fd.setVisible(true);
			if (fd.getFile() == null) {
				fitx = null;
			} else {
				fitx.setNom(fd.getFile());
				fitx.setPath(fd.getDirectory());
			}
			return fitx; //A bunch of strings relating to the file information.
		}

		private void showError(final String msg) {
			JOptionPane.showMessageDialog(null, msg, Language.getLabel(7),
					JOptionPane.ERROR_MESSAGE);
		}

		//Interal Frame - related methods
		@Override
		public void internalFrameActivated(InternalFrameEvent e) {
			InternalFrameData ifd;

			currentInternalFrame = (FrmInternalFrame) e.getSource();
			btnUpdate.setEnabled(true);
			if (!buttonClicked) {
				fr.setCurrentFrame(currentInternalFrame);
				ifd = currentInternalFrame.getInternalFrameData();
				de = ifd.getDadesExternes();
				//fitx = de.getFitxerDades();
				multiDendro = ifd.getMultiDendrogram();
				Jpan_Menu.setConfigPanel(ifd);
			}
		}

		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			FrmInternalFrame.decreaseOpenFrameCount();
			btnUpdate.setEnabled(false);
			if (!buttonClicked) {
				Jpan_Menu.clearConfigPanel();
			}
		}

		@Override
		public void internalFrameClosed(InternalFrameEvent e) {
		}

		@Override
		public void internalFrameOpened(InternalFrameEvent e) {
		}

		@Override
		public void internalFrameIconified(InternalFrameEvent e) {
		}

		@Override
		public void internalFrameDeiconified(InternalFrameEvent e) {
		}

		@Override
		public void internalFrameDeactivated(InternalFrameEvent e) {
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName() == "progress") {
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			}
		}

	// generated methods - automatically detect scroll value
		
		public int getVerticalScrollValue() {
			return VerticalScrollValue;
		}

		public void setVerticalScrollValue(int numberOfEntries) {
			VerticalScrollValue = 15*numberOfEntries + 250;
		}

		public boolean isInteger( String input ) {
		    try {
		        Integer.parseInt( input );
		        return true;
		    }
		    catch( Exception e ) {
		        return false;
		    }
		}		

		public String[] convertContextSets(LinkedList<ContextSetDescription> ListOfContextSets){
			
			//initialize output array
			String[] ArrayOfContextSets = new String[ListOfContextSets.size()];
			
			//iterate through array
			for (int i = 0; i < ListOfContextSets.size(); i++){
				ArrayOfContextSets[i] = ListOfContextSets.get(i).getName();
			}
			
			return ArrayOfContextSets;
		}

		public JComboBox getContextSetMenu() {
			return contextSetMenu;
		}

		public void setContextSetMenu(JComboBox contextSetMenu) {
			this.contextSetMenu = contextSetMenu;
		}
	}
