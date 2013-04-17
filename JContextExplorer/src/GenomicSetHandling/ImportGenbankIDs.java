package GenomicSetHandling;

import genomeObjects.AnnotatedGenome;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.biojava3.core.sequence.RNASequence;

public class ImportGenbankIDs extends JDialog implements ActionListener{

	//Fields
	public static String URLBase = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nucleotide&id=";
	public static String QueryBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nuccore&term=";
	public static String EntrezBase = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&rettype=gb&retmode=text&WebEnv=";
	private String WebEnv;
	private String query_key;
	
	//Constructor
	public ImportGenbankIDs(){
		this.getFrame();
		RetrieveGenomeFromNCBI("NC_008095");
		this.setVisible(true);
	}
	
	//===== Components ======/
	public void getFrame(){
		this.setSize(800,500);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setTitle("Retrieve genomes by genbank ID");
		this.setResizable(true);
	}
	
	public void getPanel(){
		
	}
	
	//retrieve genome from NCBI
	public AnnotatedGenome RetrieveGenomeFromNCBI(String AccessionID){
		
		//TODO: Finish this!
		
		//Initialize
		AnnotatedGenome AG = null;
		String FetchURL = QueryBase + AccessionID + "&usehistory=y";
		try {
			
			// ======== Retrieve key parameter information ======== //
			
			URL inputURL = new URL(FetchURL);
			HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String Line = null;
			String QueryResults = "";
			while((Line = br.readLine()) != null){
				System.out.println(Line);
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
			
			String GetGenbankURL = EntrezBase + WebEnv + "&query_key=" + query_key;
			System.out.println(GetGenbankURL);

			URL GbURL = new URL(GetGenbankURL);
			InputStream is = GbURL.openStream();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(is));
			String Line2 = null;
			
			// ======== Parse Genbank File ======== //
			
			//Currently just printing - need to parse this!
			while((Line2 = br2.readLine()) != null){
				System.out.println(Line2);
			}
			
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
	
	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
