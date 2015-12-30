package org.akaza.openclinica.service.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class ExampleJob implements Job {

	Date startDate = new Date();
	String tab = "";
    String cdisc = "";
    String spss = "";
    String email = "";
    
	
	public void execute(JobExecutionContext context)
    	throws JobExecutionException {
		if (tab!=null) {
			//generate a file in tab format
		}
		if (cdisc!=null) {
			//generate a file in cdisc format
		}
		if (spss!=null) {
			//generate a file in spss format
		}
		// TODO will have to split this into three jobs maybe
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getCdisc() {
		return cdisc;
	}

	public void setCdisc(String cdisc) {
		this.cdisc = cdisc;
	}

	public String getSpss() {
		return spss;
	}

	public void setSpss(String spss) {
		this.spss = spss;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void create(String tab, String cdisc, String spss, Date startDate, String email) throws Exception {
		this.tab = tab;
		this.spss = spss;
		this.email = email;
		this.cdisc = cdisc;
		this.startDate = startDate;
		//execute();
	}
}
