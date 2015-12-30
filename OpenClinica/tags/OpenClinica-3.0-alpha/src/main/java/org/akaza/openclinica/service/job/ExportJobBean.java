package org.akaza.openclinica.service.job;

public class ExportJobBean {
	private int datasetId;
	private String tab;
	private String cdisc;
	private String spss;
	private String email;
	
	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}
	public void setTab(String tab) {
		this.tab = tab;
	}
	public void setCdisc(String cdisc) {
		this.cdisc = cdisc;
	}
	public void setSpss(String spss) {
		this.spss = spss;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void execute() {
		//todo set up the logic here
		System.out.println("a job is running");
	}
}
