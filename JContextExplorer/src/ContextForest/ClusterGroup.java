package ContextForest;

import java.io.Serializable;
import java.util.LinkedList;

import definicions.Cluster;

public class ClusterGroup implements Serializable {

	//Fields
	private LinkedList<Cluster> RetainGroup;
	private LinkedList<Cluster> SegGroup;
	
	
	public LinkedList<Cluster> getRetainGroup() {
		return RetainGroup;
	}
	public void setRetainGroup(LinkedList<Cluster> retainGroup) {
		RetainGroup = retainGroup;
	}
	public LinkedList<Cluster> getSegGroup() {
		return SegGroup;
	}
	public void setSegGroup(LinkedList<Cluster> segGroup) {
		SegGroup = segGroup;
	}
}
