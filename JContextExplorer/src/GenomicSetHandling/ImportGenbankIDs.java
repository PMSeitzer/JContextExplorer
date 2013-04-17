package GenomicSetHandling;

import genomeObjects.AnnotatedGenome;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.biojava3.core.sequence.RNASequence;

public class ImportGenbankIDs extends JDialog implements ActionListener{

	//Fields
	public static String URLBase = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nucleotide&id=";
	
	//Constructor
	public ImportGenbankIDs(){
		this.getFrame();
//		RetrieveGenomeFromNCBI("NC_008095");
//		String S = "open "+URLBase+"NC_008095";
//		try {
//			Runtime.getRuntime().exec(S);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
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

		//fetch string
		String FetchURL = URLBase + AccessionID;
		
		try {
//			InputStream input = new URL(FetchURL).openStream();
//			BufferedReader r = new BufferedReader(new InputStreamReader(input, "UTF-8"));
//			String Line = null;
//			while ((Line = r.readLine())!= null){
//				System.out.println(Line);
//			}
			
			URL inputURL = new URL(FetchURL);
			HttpURLConnection c = (HttpURLConnection) inputURL.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String Line = null;
			while((Line = br.readLine()) != null){
				System.out.println(Line);
			}
			
			c.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		//Return
		return AG;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
