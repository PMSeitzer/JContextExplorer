package GenomicSetHandling;

import genomeObjects.AnnotatedGenome;
import genomeObjects.ContextSetDescription;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.FrmPrincipalDesk.LoadGenomesWorker;

import org.biojava3.core.sequence.RNASequence;

public class ImportGenbankIDs extends JDialog implements ActionListener, FocusListener, PropertyChangeListener{

	//Management
	public FrmPrincipalDesk f;
	
	//Fields
	//Data Retrieval
	public static String NCBIQueryBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nuccore&term=";
	public static String NCBIIDSummaryBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=nuccore&id=";
	public static String GenbankIDSearchBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nuccore&term=";
	public static String GenbankIDwParametersBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&rettype=gb&retmode=text&WebEnv=";
	
	private String WebEnv;
	private String query_key;
	private boolean AccessedTextArea = false;
	private boolean AccessedSearchBar = false;
	
	//Internal Data Storage
	private LinkedHashMap<String, String> GenBanks 
		= new LinkedHashMap<String, String>();
	
	//<Key, Value> = <GenbankID, OrganismName>
	
	//GUI Stuff
	private JPanel jp, jpEnclosing;
	private JTextField LblSearch, TxtSearch, LblGenbankIDs;
	private JTextArea GenbankInfo;
	private JProgressBar progressBar;
	private JButton btnSearchNCBI, btnAddIDsFromFile, btnImport, btnOK, btnExportGenbanksToTxt;
	
	private String strSearch = "Search NCBI Genomes:";
	private String strIDs = "Organism and Genbank IDs:";
	private String strbtnLoad = "Load Genbank IDs from file";
	private String strbtnImport = "Add genomes to current genome set";
	private String strTextAreaTxt = "";
	private String strExport = "Export Genbank Files";
	
	private String strGenbankInfoInitial = "Organism1\tGenbank_ID1\nOrganism2\tGenbank_ID2";
	private String strSearchInitial = "Enter genus, species, or strain information.";
	
	//===== SwingWorkers ======/
	
	//Retrieve data from NCBI
	public class QueryNCBIWorker extends SwingWorker<Void, Void>{

		//Fields
		protected String Query;
		protected LinkedList<String> LL;
		protected LinkedHashMap<String, String> NCBI_Matches =
				new LinkedHashMap<String, String>();
		protected String GenbankIDs;
		protected String msgErr = "No annotated genomes matching your query\n" +
				"Are currently publically available in the NCBI Genbank database.";

		
		//Constructor
		public QueryNCBIWorker(String Query){
			this.Query = Query;
		}
		
		//methods
		@Override
		protected Void doInBackground() throws Exception {
			
			//format Query
			String ToEntrez = AssembleURL(Query);
			
			//Send to Entrez, and return IDs
			HashSet<String> IDs = RetrieveIDList(ToEntrez);

			//Variables for progress bar
			double AllHits = (double) IDs.size();
			double IDCounter = 0;
			
			//iterate through all IDs, note IDs with usable data
			for (String s : IDs){
				
				//parse this ID
				parseUID(s);
				
				//increment counter, update progress bar
				IDCounter++;
				int progress = (int) Math
						.round(100 * (IDCounter / AllHits));
				setProgress(progress);
			}
			
			return null;
		}
		
		//Finished all processes, update windows etc
		public void done(){
			setProgress(0);
			UpdateGenbankIDs();
		}
		
		//Extra methods
		//convert query to URL to send to Entrez database
		public String AssembleURL(String Query){
			
			String[] Components = Query.split("\\s+");
			LL = new LinkedList<String>();
			for (String s : Components){
				if (!LL.contains(s)){
					LL.add(s);
				}
			}
			Collections.sort(LL);
			String NewQuery = "";
			for (String s : LL){
				NewQuery = NewQuery + "+" + s;
			}
			NewQuery = NewQuery.substring(1);
			
			String ToEntrez = NCBIQueryBase + NewQuery + "&usehistory=y";
			
			return ToEntrez;
		}
		
		//Query NCBI, and return possible hits in the form of uids
		public HashSet<String> RetrieveIDList(String ToEntrez){
			
			// ====== Initialize =======//
			
			HashSet<String> IDs = new HashSet<String>();
			String QueryResults = "";
			
			// ====== Retrieve Query Results =======//
			
			try {
				
				URL inputURL = new URL(ToEntrez);
				HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
				String Line = null;
				while((Line = br.readLine()) != null){
					QueryResults = QueryResults + Line;
				}
				
				br.close();
				c.disconnect();
				
			} catch (Exception ex){
				ex.printStackTrace();
			}
			
			// ====== Extract IDs out of results =======//
			
			Pattern QIDs = Pattern.compile("<Id>(.+?)</Id>");
			Matcher QIDm = QIDs.matcher(QueryResults);

			while (QIDm.find()){
				IDs.add((String) QIDm.group().subSequence(4, QIDm.group().length()-5));
			}
			
			return IDs;
		}
		
		//Parse uid
		public void parseUID(String UID){
			
			// ====== Return Summary ====== //
			
			String IDURL = NCBIIDSummaryBase + UID;
			String UIDQuery = "";
			String Name = "";
			String GenbankID = "";
			boolean KeepMatch = false;
			try {
				
				URL inputURL = new URL(IDURL);
				HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
				String Line = null;
				while((Line = br.readLine()) != null){
					UIDQuery = UIDQuery + Line;
					//System.out.println(Line);
				}
				
				br.close();
				c.disconnect();
			
			// ===== Extract Information ===== //
				
				Pattern GBID = Pattern.compile("<Item Name=\"Caption\" Type=\"String\">(.+?)</Item>");
				Matcher GBIDm = GBID.matcher(UIDQuery);

				while (GBIDm.find()){
					GenbankID = (String) GBIDm.group().subSequence(35, GBIDm.group().length()-7);
				}
				
				Pattern GBName = Pattern.compile("<Item Name=\"Title\" Type=\"String\">(.+?)</Item>");
				Matcher GBName_m = GBName.matcher(UIDQuery);

				while (GBName_m.find()){
					Name = (String) GBName_m.group().subSequence(33, GBName_m.group().length()-7);
				}
				
				//only keep matches that make sense
				
				//text match
				for (String s : LL){
					if (Name.toUpperCase().contains(s.toUpperCase())){
						KeepMatch = true;
						break;
					}
				}
				
				//Store this match
				if (KeepMatch){

					String NameNoWhiteSpace = Name.replaceAll("\\s", "_");
					NCBI_Matches.put(NameNoWhiteSpace, GenbankID);

				}
				
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	
		//update current list of genbank IDs
		public void UpdateGenbankIDs(){
			
			//only update if at least one match was found.
			if (NCBI_Matches.size() > 0){
				
				//Retrieve current text
				strTextAreaTxt = GenbankInfo.getText();
				if (!strTextAreaTxt.endsWith("\n")){
					strTextAreaTxt = strTextAreaTxt + "\n";
				}
				
				if (!AccessedTextArea){
					strTextAreaTxt = "";
				}
				
				//Add newly retrieved IDs
				for (String s : NCBI_Matches.keySet()){
					strTextAreaTxt = strTextAreaTxt + s + "\t" + NCBI_Matches.get(s) + "\n";
				}
				
				//update display
				GenbankInfo.setForeground(Color.BLACK);
				GenbankInfo.setText(strTextAreaTxt);
				AccessedTextArea = true;
				
			} else {
				JOptionPane.showMessageDialog(null, msgErr,
						"No Matches Found",JOptionPane.INFORMATION_MESSAGE);
			}

		}
	}
	
	//Import data from 
	public class ImportGBKWorker extends SwingWorker<Void, Void>{

		//fields
		protected boolean LoadGenomes;
		protected String ExportDir;
		
		//Constructor
		protected ImportGBKWorker(boolean loadGenomes, String exportDir){
			LoadGenomes = loadGenomes;
			ExportDir = exportDir;
		}
		
		//do in background
		@Override
		protected Void doInBackground() throws Exception {
			
			//disable okay button
			btnOK.setEnabled(false);
			
			//switch cursor
			Component glassPane = getRootPane().getGlassPane();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			
			//Initialize counter
			int Counter = 0;
			
			//import genomes
			for (String s : GenBanks.keySet()){
				
				if (LoadGenomes){
					
					//Retrieve Genome
					AnnotatedGenome AG = RetrieveGenomeFromNCBI(s, GenBanks.get(s));
					
					// add to hash map
					f.getOS().getSpecies().put(AG.getSpecies(), AG);

					// add name to array of species
					f.getOS().getSpeciesNames().add(AG.getSpecies());
					f.getOS().getAGLoaded().put(AG.getSpecies(), true);
					f.getOS().getGenomeDescriptions().put(AG.getSpecies(), AG.getTextDescription());

				} else {	//just export
					
					//Define file name and export
					String FileName = ExportDir + "/" + GenBanks.get(s).replace(" ","_") + ".gb";
					RetrieveGenbankFromNCBI(s, FileName);
					
				}

				//increment counter
				Counter++;
				
				//update progress bar
				int progress = (int) Math
						.round(100 * ((double)Counter / (double) GenBanks.keySet().size()));
				setProgress(progress);
				
			}
			
			//add a context set description, if appropriate
			boolean MissingSingleGene = true;
			for (ContextSetDescription CSD : f.getOS().getCSDs()){
				if (CSD.getName().equals("SingleGene")){
					MissingSingleGene = false;
				}
			}
			
			//create default single gene set
			if (MissingSingleGene){
				
				//add to OS
				ContextSetDescription CSD = new ContextSetDescription();
				CSD.setName("SingleGene");
				CSD.setType("SingleGene");
				CSD.setPreprocessed(false);
				f.getOS().getCSDs().add(CSD);
				
				//add to menu
				f.getPanBtn().getContextSetMenu().addItem("SingleGene");
				f.getPanBtn().getContextSetMenu().removeItem("<none>");

			}
			
			//switch cursor
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			glassPane.setVisible(false);
			
			return null;
		}
		
		//post processing.
		public void done(){
			//re-set progress bar
			setProgress(0);
			
			//re-enable okay button
			btnOK.setEnabled(true);
			
			String msg = "";
			if (LoadGenomes){
				msg = "All Genomes have been successfully imported into the current Genome Set.";
			} else {
				msg = "All Genomes have been successfully exported in genbank format into the directory\n"
						+ ExportDir + ".";
			}
			//helpful message
			JOptionPane.showMessageDialog(null,msg,
					"Import Complete", JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	//===== Constructor ======/
	public ImportGenbankIDs(FrmPrincipalDesk f){
		this.f = f;
		this.getFrame();
		this.getPanel();
		//RetrieveGenomeFromNCBI("NC_008095","Silly_Guy!");
		this.setVisible(true);
		
		//RetrieveGenomeFromNCBI("NC_010162","Org1");
	}
	
	//===== Components ======/
	//Frame
	public void getFrame(){
		this.setSize(600,450);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Retrieve genomes from NCBI Genbank Database");
		this.setResizable(true);
	}
	
	//Panel
	public void getPanel(){
		
		//initial GridBagLayout parameters
		jp = new JPanel();
		jp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(0,0,0,0);
		
		//search header
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		LblSearch = new JTextField(strSearch);
		LblSearch.setEditable(false);
		jp.add(LblSearch, c);
		gridy++;
		
		//search bar
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.ipady = 10;
		TxtSearch = new JTextField(strSearchInitial);
		TxtSearch.setForeground(Color.GRAY);
		TxtSearch.setEditable(true);
		TxtSearch.addActionListener(this);
		TxtSearch.addFocusListener(this);
		jp.add(TxtSearch, c);
		gridy++;
		c.ipady = 0;
		
		//button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		btnSearchNCBI = new JButton("Search");
		btnSearchNCBI.addActionListener(this);
		btnSearchNCBI.setHorizontalAlignment(SwingConstants.LEFT);
		jp.add(btnSearchNCBI, c);
		gridy++;
		gridy++;
		
		//Enter points header
		//search header
		c.gridx = 0;
		c.gridy = gridy;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.insets = new Insets(20,0,0,0);
		LblGenbankIDs = new JTextField(strIDs);
		LblGenbankIDs.setEditable(false);
		jp.add(LblGenbankIDs, c);
		gridy++;
		
		//Actual enter points form
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.insets = new Insets(2,3,2,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		GenbankInfo = new JTextArea(strGenbankInfoInitial);
		GenbankInfo.setForeground(Color.GRAY);
		GenbankInfo.addFocusListener(this);
		GenbankInfo.setEditable(true);
		JScrollPane ptsscroll = new JScrollPane(GenbankInfo);
		ptsscroll.setPreferredSize(new Dimension(TxtSearch.getColumns(), 150));
		jp.add(ptsscroll, c);
		gridy++;
		
		//load to file
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.NONE;
		btnAddIDsFromFile = new JButton(strbtnLoad);
		btnAddIDsFromFile.addActionListener(this);
		jp.add(btnAddIDsFromFile, c);
		gridy++;

		//progress bar
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.insets = new Insets(2,5,0,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		jp.add(progressBar, c);
		gridy++;
		
		//Import genomes button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(2,0,0,0);
		c.fill = GridBagConstraints.NONE;
		btnImport = new JButton(strbtnImport);
		btnImport.addActionListener(this);
		jp.add(btnImport, c);
		
		c.gridx = 1;
		c.gridy = gridy;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(1,1,1,1);
		c.fill = GridBagConstraints.NONE;
		btnExportGenbanksToTxt = new JButton(strExport);
		btnExportGenbanksToTxt.addActionListener(this);
		jp.add(btnExportGenbanksToTxt, c);		
		gridy++;
		
		//Close frame button
		c.gridx = 0;
		c.gridy = gridy;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.insets = new Insets(20,0,0,0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		btnOK = new JButton("OK");
		btnOK.addActionListener(this);
		jp.add(btnOK, c);
		
		//panels 2 frame
		jpEnclosing = new JPanel();
		jpEnclosing.setLayout(new BorderLayout());
		jpEnclosing.add(jp, BorderLayout.NORTH);
		this.add(jpEnclosing);
	}
	
	//===== Entrez Queries =====//
	//retrieve genome from NCBI + load to data structure
	public AnnotatedGenome RetrieveGenomeFromNCBI(String AccessionID, String SpeciesName){
		
		//Initialize
		AnnotatedGenome AG = null;
		String FetchURL = GenbankIDSearchBase + AccessionID + "&usehistory=y";
		try {
			
			// ======== Retrieve key parameter information ======== //
			
			URL inputURL = new URL(FetchURL);
			HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String Line = null;
			String QueryResults = "";
			while((Line = br.readLine()) != null){
				//System.out.println(Line);
				QueryResults = QueryResults + Line;
			}
			
			br.close();
			c.disconnect();

			// ======= Retrieve Query Key and WebEnv ======= //
			
			Pattern QK = Pattern.compile("<QueryKey>.*</QueryKey>");
			Matcher QKm = QK.matcher(QueryResults);

			while (QKm.find()){
				query_key = (String) QKm.group().subSequence(10, QKm.group().length()-11);
				//System.out.println(query_key);
			}
			
			Pattern WE = Pattern.compile("<WebEnv>.*</WebEnv>");
			Matcher WEm = WE.matcher(QueryResults);
			while (WEm.find()){
				WebEnv = (String) WEm.group().subSequence(8, WEm.group().length()-9);
				//System.out.println(WebEnv);
			}
			
			// ======== Retrieve Genbank File ======== //
			
			String GetGenbankURL = GenbankIDwParametersBase + WebEnv + "&query_key=" + query_key;
			//System.out.println(GetGenbankURL);

			URL GbURL = new URL(GetGenbankURL);
			InputStream is = GbURL.openStream();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(is));
			String Line2 = null;
			
			// ======== Parse Genbank File ======== //
			// set appropriate types to import
			
			//new annotated genome!
			AG = new AnnotatedGenome();
			
			//Species name
			AG.setSpecies(SpeciesName);
			
			//adjust types, names, etc
			AG.setIncludeTypes(f.getGFFIncludeTypes());
			AG.setDisplayOnlyTypes(f.getGFFDisplayTypes());
			AG.setGFM(f.getGBKFields());
			
			//Import data
			AG.importFromGBKReader(br2);
			
			//close file stream
			br2.close();

			
		} catch (MalformedURLException e) {
			String msgID = "The Genbank ID " + AccessionID + " could not be found.";
			JOptionPane.showMessageDialog(null, msgID, "Genbank ID Not Found", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to import genbank data from file.", 
					"Import Genbank Error", JOptionPane.ERROR_MESSAGE);
		}
		
		//Return
		return AG;
	}
	
	//retrieve genomes from NCBI + print to files
	public void RetrieveGenbankFromNCBI(String AccessionID, String FileName){
		
		//Initialize
		String FetchURL = GenbankIDSearchBase + AccessionID + "&usehistory=y";
		
		try {
			
			// ======== Retrieve key parameter information ======== //
			
			URL inputURL = new URL(FetchURL);
			HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String Line = null;
			String QueryResults = "";
			while((Line = br.readLine()) != null){
				//System.out.println(Line);
				QueryResults = QueryResults + Line;
			}
			
			br.close();
			c.disconnect();

			// ======= Retrieve Query Key and WebEnv ======= //
			
			Pattern QK = Pattern.compile("<QueryKey>.*</QueryKey>");
			Matcher QKm = QK.matcher(QueryResults);

			while (QKm.find()){
				query_key = (String) QKm.group().subSequence(10, QKm.group().length()-11);
				//System.out.println(query_key);
			}
			
			Pattern WE = Pattern.compile("<WebEnv>.*</WebEnv>");
			Matcher WEm = WE.matcher(QueryResults);
			while (WEm.find()){
				WebEnv = (String) WEm.group().subSequence(8, WEm.group().length()-9);
				//System.out.println(WebEnv);
			}
			
			// ======== Define Output Stream ======== //
			BufferedWriter bw = new BufferedWriter(new FileWriter(FileName));
			
			// ======== Retrieve Genbank File ======== //
			
			String GetGenbankURL = GenbankIDwParametersBase + WebEnv + "&query_key=" + query_key;
			//System.out.println(GetGenbankURL);

			URL GbURL = new URL(GetGenbankURL);
			InputStream is = GbURL.openStream();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(is));
			String Line2 = null;
			
			//write URL stream to output stream
			while ((Line2 = br2.readLine())!= null){
				bw.write(Line2);
				bw.write("\n");
				bw.flush();
			}
			
			//close file streams
			br2.close();
			bw.close();
			
		} catch (MalformedURLException e) {
			String msgID = "The Genbank ID " + AccessionID + " could not be found.";
			JOptionPane.showMessageDialog(null, msgID, "Genbank ID Not Found", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to import genbank data from file.", 
					"Import Genbank Error", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	//Query NCBI for various things
	public void QueryNCBI(String Query){
		
		// ======= Parse Query ======= //
		String[] Components = Query.split("\\s+");
		LinkedList<String> LL = new LinkedList();
		for (String s : Components){
			if (!LL.contains(s)){
				LL.add(s);
			}
		}
		Collections.sort(LL);
		String NewQuery = "";
		for (String s : LL){
			NewQuery = NewQuery + "+" + s;
		}
		NewQuery = NewQuery.substring(1);
		//System.out.println(NewQuery);
		
		String ToEntrez = NCBIQueryBase + NewQuery + "&usehistory=y";
		
		// ======== Retrieve key parameter information ======== //
		
		try {
			
			// ======== Retrieve key parameter information ======== //
			
			URL inputURL = new URL(ToEntrez);
			HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String Line = null;
			String QueryResults = "";
			while((Line = br.readLine()) != null){
				//System.out.println(Line);
				//QueryResults = QueryResults + Line;
			}
			
			br.close();
			c.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	//==== Action and Focus Activities =====//
	
	//actions
	@Override
	public void actionPerformed(ActionEvent e) {

		//Query NCBI database
		if (e.getSource().equals(TxtSearch) || e.getSource().equals(btnSearchNCBI)){
			if (!TxtSearch.equals("") && !TxtSearch.equals(strSearchInitial)){
				QueryNCBIWorker Q = new QueryNCBIWorker(TxtSearch.getText());
				Q.addPropertyChangeListener(this);
				Q.execute();
			} else{
				JOptionPane.showMessageDialog(null, "Please type one or more key words in the search bar.",
						"No Search Entered",JOptionPane.ERROR_MESSAGE);
			}
		}
		
		//Add list of genbank IDs to window
		if (e.getSource().equals(btnAddIDsFromFile)){
			ImportFileList();
		}
		
		//Import Genbank files + add to set
		if (e.getSource().equals(btnImport)){
			if (!GenbankInfo.getText().equals("") &&
					!GenbankInfo.getText().equals(strGenbankInfoInitial)){
				
				//parse contents
				ParseGenBankTextArea();
				
				//Create new genome set, if appropriate
				if (f.getOS() == null){
					f.MakeDefaultGenomeSet("Default Genome Set");
				}
				
				//invoke SwingWorker
				ImportGBKWorker I = new ImportGBKWorker(true,null);
				I.addPropertyChangeListener(this);
				I.execute();
				
			} else {
				JOptionPane.showMessageDialog(null, "Please type one or more Species - GenBank Accession ID pairs in the text window.",
						"No Organisms Entered",JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if (e.getSource().equals(btnExportGenbanksToTxt)){
			if (!GenbankInfo.getText().equals("") &&
					!GenbankInfo.getText().equals(strGenbankInfoInitial)){
				
				// initialize output
				JFileChooser GetGenomes = new JFileChooser();
				
				GetGenomes.setMultiSelectionEnabled(true);
				GetGenomes.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				GetGenomes
						.setDialogTitle("Select Output Directory for Genbank Files");

				//retrieve directory
				if (f.getFileChooserSource() != null) {
					GetGenomes.setCurrentDirectory(f.getFileChooserSource());
				} else {
					GetGenomes.setCurrentDirectory(new File("."));
				}
			
				GetGenomes.showOpenDialog(GetGenomes);
				
				// set directory + proceed
				if (GetGenomes.getSelectedFile() != null) {
							
					//set directory for next time
					f.setFileChooserSource(GetGenomes.getCurrentDirectory());
					
					//parse contents
					ParseGenBankTextArea();
					
					//invoke SwingWorker
					ImportGBKWorker I = new ImportGBKWorker(false,GetGenomes.getSelectedFile().getAbsolutePath());
					I.addPropertyChangeListener(this);
					I.execute();
					
				}
				
			} else {
				JOptionPane.showMessageDialog(null, "Please type one or more Species - GenBank Accession ID pairs in the text window.",
						"No Organisms Entered",JOptionPane.ERROR_MESSAGE);
			}
		}
		
		//close window
		if (e.getSource().equals(btnOK)){
			this.dispose();
		}
		
	}

	//import file
	public void ImportFileList(){
		JFileChooser GetGenbankIDs = new JFileChooser();
		
		GetGenbankIDs.setFileSelectionMode(JFileChooser.FILES_ONLY);
		GetGenbankIDs
				.setDialogTitle("Select A File Containing Genbank IDs and Organism Names");

		//retrieve directory
		if (f.getFileChooserSource() != null) {
			GetGenbankIDs.setCurrentDirectory(f.getFileChooserSource());
		} else {
			GetGenbankIDs.setCurrentDirectory(new File("."));
		}
		
		GetGenbankIDs.showOpenDialog(GetGenbankIDs);
		
		// note current directory for next time
		if (GetGenbankIDs.getCurrentDirectory() != null) {
			f.setFileChooserSource(GetGenbankIDs.getCurrentDirectory());
		}
		
		//import IDs
		if (GetGenbankIDs.getSelectedFile() != null){
			AddFiles2GenbankIDList(GetGenbankIDs.getSelectedFile());
		}


	}
	
	// ====== Modify Text Area ===== //
	
	//add contents of file to text area
	public void AddFiles2GenbankIDList(File f){
		
		//Retrieve current text
		strTextAreaTxt = GenbankInfo.getText();
		if (!strTextAreaTxt.endsWith("\n")){
			strTextAreaTxt = strTextAreaTxt + "\n";
		}
		
		if (!AccessedTextArea){
			strTextAreaTxt = "";
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String Line = null;
			String OrgName = "";
			String IDNum = "";
			int Counter = 0;
			boolean AddToList = false;
			while ((Line = br.readLine()) != null){
				//avoid comments
				if (!Line.startsWith("#")){
					//split by tabs
					String[] L = Line.split("\t");
					if (L.length == 2){
						OrgName = L[0];
						IDNum = L[1];
						AddToList = true;
					} else if (L.length == 1){
						Counter++;
						OrgName = "Organism_" + String.valueOf(Counter);
						IDNum = L[0];
						AddToList = true;
					} else {
						AddToList = false;
					}
					
					//update list
					if (AddToList){
						strTextAreaTxt = strTextAreaTxt + OrgName + "\t" + IDNum + "\n";
					}

				}
			}
			
			//Update JTextArea
			AccessedTextArea = true;
			GenbankInfo.setForeground(Color.BLACK);
			GenbankInfo.setText(strTextAreaTxt);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//Transfer contents of text area to hash map
	public void ParseGenBankTextArea(){
		
		//Reset hashmap
		GenBanks = new LinkedHashMap<String, String>();
		
		//Parse
		//Retrieve current text, split, add to hash map
		strTextAreaTxt = GenbankInfo.getText();
		String[] NewLines = strTextAreaTxt.split("\n");
		for (String NL : NewLines){
			String[] Entry = NL.split("\t");
			if (Entry.length == 2){
				GenBanks.put(Entry[1], Entry[0]);
			}
		}
	}
	
	// ====== Focus + Property Change Listeners ===== //
	
	@Override
	public void focusGained(FocusEvent e) {
		
		//Prepare to type in Genbank field
		if (e.getSource().equals(GenbankInfo) && !AccessedTextArea){
			GenbankInfo.setText("");
			GenbankInfo.setForeground(Color.BLACK);
			AccessedTextArea = true;
		}
		
		//Prepare to type in search field
		if (e.getSource().equals(TxtSearch) && !AccessedSearchBar){
			TxtSearch.setText("");
			TxtSearch.setForeground(Color.BLACK);
			AccessedSearchBar = true;
		}
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		
		//Reset genbank data field
		if (e.getSource().equals(GenbankInfo) && GenbankInfo.getText().equals("")){
			GenbankInfo.setText(strGenbankInfoInitial);
			GenbankInfo.setForeground(Color.GRAY);
			AccessedTextArea = false;
		}
		
		//Reset search field
		if (e.getSource().equals(TxtSearch) && TxtSearch.getText().equals("")){
			TxtSearch.setText(strSearchInitial);
			TxtSearch.setForeground(Color.GRAY);
			AccessedSearchBar = false;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == "progress") {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

}
