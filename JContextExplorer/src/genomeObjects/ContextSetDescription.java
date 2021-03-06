package genomeObjects;

import java.io.Serializable;

public class ContextSetDescription implements Serializable{

	//required fields
	private String Name;
	private String Type;
	private boolean Preprocessed;
	private boolean isCassette = false;
	
	//optional fields
	
	//ALL
	private boolean SingleOrganismAmalgamation;
	private boolean SingleOrganismAmalgamationKeepEmpty;
	private boolean RetainFractionEnabled;
	private double RetainFraction;
	public boolean RequireSameSizeHomologs = false;
	
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
	
	//genes between limiters
	//(1) gap limits
	private boolean GapLimit;
	private int GapLimitSize;
	
	// internal genes
	public boolean InternalGeneNumberLimit;
	public int MaxNumInternalGenes;
	
	//(3) operon expansions
	public boolean isOperonExpansion;
	public boolean SameStrandRequired = true;
	public int IntergenicGapLimit;
	
	//cassette
	private String CassetteOf; 		//Name of another ContextSetDescription
	private boolean isNearbyOnly;
	private int NearbyLimit;
	
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

	public boolean isSingleOrganismAmalgamation() {
		return SingleOrganismAmalgamation;
	}

	public void setSingleOrganismAmalgamation(boolean singleOrganismAmalgamation) {
		SingleOrganismAmalgamation = singleOrganismAmalgamation;
	}

	public boolean isGapLimit() {
		return GapLimit;
	}

	public void setGapLimit(boolean gapLimit) {
		GapLimit = gapLimit;
	}

	public int getGapLimitSize() {
		return GapLimitSize;
	}

	public void setGapLimitSize(int gapLimitSize) {
		GapLimitSize = gapLimitSize;
	}

	public boolean isRetainFractionEnabled() {
		return RetainFractionEnabled;
	}

	public void setRetainFractionEnabled(boolean retainFractionEnabled) {
		RetainFractionEnabled = retainFractionEnabled;
	}

	public double getRetainFraction() {
		return RetainFraction;
	}

	public void setRetainFraction(double retainFraction) {
		RetainFraction = retainFraction;
	}

	public boolean isNearbyOnly() {
		return isNearbyOnly;
	}

	public void setNearbyOnly(boolean isNearbyOnly) {
		this.isNearbyOnly = isNearbyOnly;
	}

	public int getNearbyLimit() {
		return NearbyLimit;
	}

	public void setNearbyLimit(int nearbyLimit) {
		NearbyLimit = nearbyLimit;
	}

	public boolean isSingleOrganismAmalgamationKeepEmpty() {
		return SingleOrganismAmalgamationKeepEmpty;
	}

	public void setSingleOrganismAmalgamationKeepEmpty(
			boolean singleOrganismAmalgamationKeepEmpty) {
		SingleOrganismAmalgamationKeepEmpty = singleOrganismAmalgamationKeepEmpty;
	}
}
