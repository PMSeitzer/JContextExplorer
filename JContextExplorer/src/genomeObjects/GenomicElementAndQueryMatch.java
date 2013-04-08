package genomeObjects;

import java.io.Serializable;

public class GenomicElementAndQueryMatch implements Serializable{

	//fields
	private GenomicElement E;
	private boolean QueryMatch;
	
	//constructor
	public GenomicElementAndQueryMatch(){
		super();
	}

	//setters and getters
	public GenomicElement getE() {
		return E;
	}

	public void setE(GenomicElement e) {
		E = e;
	}

	public boolean isQueryMatch() {
		return QueryMatch;
	}

	public void setQueryMatch(boolean queryMatch) {
		QueryMatch = queryMatch;
	}
	
	
}
