package org.hkijena.mcat.api;

public class MCATResultObject {
	
	private String subject;
	private String treatment;
	private String roi;
	private int downsamplingFactor;
	private int channelOfInterest;
	private MCATClusteringHierarchy hierarchy;
	private int k;
	private MCATPostprocessingMethod postprocessingMethod;
	private double auc;
	
	
	public MCATResultObject() {}
	
	public MCATResultObject(String subject, String treatment, String roi, int downsamplingFactor, int channelOfInterest,
			MCATClusteringHierarchy hierarchy, int k, MCATPostprocessingMethod postprocessingMethod, double auc) {
		super();
		this.subject = subject;
		this.treatment = treatment;
		this.roi = roi;
		this.downsamplingFactor = downsamplingFactor;
		this.channelOfInterest = channelOfInterest;
		this.hierarchy = hierarchy;
		this.k = k;
		this.postprocessingMethod = postprocessingMethod;
		this.auc = auc;
	}

	
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public String getRoi() {
		return roi;
	}

	public void setRoi(String roi) {
		this.roi = roi;
	}

	public int getDownsamplingFactor() {
		return downsamplingFactor;
	}

	public void setDownsamplingFactor(int downsamplingFactor) {
		this.downsamplingFactor = downsamplingFactor;
	}

	public int getChannelOfInterest() {
		return channelOfInterest;
	}

	public void setChannelOfInterest(int channelOfInterest) {
		this.channelOfInterest = channelOfInterest;
	}

	public MCATClusteringHierarchy getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(MCATClusteringHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public MCATPostprocessingMethod getPostprocessingMethod() {
		return postprocessingMethod;
	}

	public void setPostprocessingMethod(MCATPostprocessingMethod postprocessingMethod) {
		this.postprocessingMethod = postprocessingMethod;
	}

	public double getAuc() {
		return auc;
	}

	public void setAuc(double auc) {
		this.auc = auc;
	}
	
	@Override
	public String toString() {
		return this.subject + ";" + this.treatment + ";" + this.roi + ";" + 
				this.downsamplingFactor + ";" +	this.channelOfInterest + ";" + 
				this.hierarchy + ";" + this.k + ";" + 
				this.postprocessingMethod + ";" + this.auc;
	}
}
