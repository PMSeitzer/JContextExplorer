package genomeObjects;

public enum CompVariables {
	
	//enumerated lists
	WINDOWS("C:/Research/RNAseqEight/MappingFile.txt",
			"C:/Research/EightyHalophiles/MappingFile.txt",
			"C:/Research/Klebsiella_NCBI/MappingFile.txt"),
	MACOSX("/Users/phillipseitzer/Documents/Halophiles_2012/RNAseqEight/MappingFile.txt",
			"/Users/phillipseitzer/Documents/Halophiles_2012/EightyHalophiles/MappingFile.txt",
			"/Users/phillipseitzer/Documents/Halophiles_2012/Klebsiella_NCBI_ContextRange/MappingFile.txt");
	
	//fields
	private final String RNAseqEightMappingFile;
	private final String EightyHalophilesMappingFile;
	private final String KlebsiellaContextMappingFile;
	
	//constructors
	CompVariables(String RNAseq, String EightyHalophiles, String KlebsiellaContext){
		RNAseqEightMappingFile = RNAseq;
		EightyHalophilesMappingFile = EightyHalophiles;
		KlebsiellaContextMappingFile = KlebsiellaContext;
	}
	
	//getter methods
	public String getRNAseqEightMappingFile() {
		return RNAseqEightMappingFile;
	}

	public String getEightyHalophilesMappingFile() {
		return EightyHalophilesMappingFile;
	}

	public String getKlebsiellaContextMappingFile() {
		return KlebsiellaContextMappingFile;
	}
}
