package genomeObjects;

import java.io.Serializable;

public class ContextSetDescription implements Serializable{

	//required fields
	private String Name;
	private String Type;
	private boolean Preprocessed;
	private boolean isCassette = false;
	
	//optional fields
	//distance
	private int IntGenSpacing;
	private boolean NeedSameStrand;
	
	//Range
	private int NtRangeBefore;
	private int NtRangeAfter;
	
	//genes before/after
	private int GenesBefore;
	private int GenesAfter;
	private boolean RelativeBeforeAfter;
	
	//cassette
	private String CassetteOf; 		//Name of another ContextSetDescription

	
	//constructor
	public ContextSetDescription(){
		super();
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public boolean isPreprocessed() {
		return Preprocessed;
	}

	public void setPreprocessed(boolean preprocessed) {
		Preprocessed = preprocessed;
	}

	public int getNtRangeBefore() {
		return NtRangeBefore;
	}

	public void setNtRangeBefore(int ntRangeBefore) {
		NtRangeBefore = ntRangeBefore;
	}

	public int getNtRangeAfter() {
		return NtRangeAfter;
	}

	public void setNtRangeAfter(int ntRangeAfter) {
		NtRangeAfter = ntRangeAfter;
	}

	public int getGenesBefore() {
		return GenesBefore;
	}

	public void setGenesBefore(int genesBefore) {
		GenesBefore = genesBefore;
	}

	public int getGenesAfter() {
		return GenesAfter;
	}

	public void setGenesAfter(int genesAfter) {
		GenesAfter = genesAfter;
	}

	public boolean isCassette() {
		return isCassette;
	}

	public void setCassette(boolean isCassette) {
		this.isCassette = isCassette;
	}

	public String getCassetteOf() {
		return CassetteOf;
	}

	public void setCassetteOf(String cassetteOf) {
		CassetteOf = cassetteOf;
	}

	public boolean isRelativeBeforeAfter() {
		return RelativeBeforeAfter;
	}

	public void setRelativeBeforeAfter(boolean relativeBeforeAfter) {
		RelativeBeforeAfter = relativeBeforeAfter;
	}

	public int getIntGenSpacing() {
		return IntGenSpacing;
	}

	public void setIntGenSpacing(int intGenSpacing) {
		IntGenSpacing = intGenSpacing;
	}

	public boolean isNeedSameStrand() {
		return NeedSameStrand;
	}

	public void setNeedSameStrand(boolean needSameStrand) {
		NeedSameStrand = needSameStrand;
	}
}
