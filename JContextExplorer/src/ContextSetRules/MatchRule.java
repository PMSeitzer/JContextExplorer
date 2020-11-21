package ContextSetRules;

import genomeObjects.GenomicElement;

public class MatchRule {
	
	//Annotation Match
	public static boolean AnnotationMatch(GenomicElement E, String Query){
		
		if (E.getAnnotation().contains(Query.trim().toUpperCase())){
			return true;
		} else {
			return false;
		}
		
	}
	
	//Homology cluster
	public static boolean HomologyClusterMatch(GenomicElement E, String Query){

		try {
			int Val = Integer.parseInt(Query);
			if (E.getClusterID() == Val){
				return true;
			} else {
				return false;
			}
		} catch (Exception ex){
			return false;
		}
		
	}
	
}
