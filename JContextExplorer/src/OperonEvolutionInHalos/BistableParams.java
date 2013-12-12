package OperonEvolutionInHalos;

public class BistableParams {

	public double MinDiffGrpPhyloDist; 	//Minimum phylo distance to separate two groups
	public double MaxSameGrpPhyloDist;	//Maximum phylo distance to quality two topologies as the same group
	public double MaxContentDiss;		//Maximum computed gene content dissimilarity (excludes seed)
	public int MinGrpMemSize;			//Minimum number of operonic instances per operon group
	public int MinOpSize;				//Minimum number of genes required in each operon
	
	//Constructor + with default values
	public BistableParams(){
		MinDiffGrpPhyloDist = 0.5;
		MaxSameGrpPhyloDist= 0.3;
		MaxContentDiss = 0;
		MinGrpMemSize = 3;
		MinOpSize = 3;
	}
	
}
