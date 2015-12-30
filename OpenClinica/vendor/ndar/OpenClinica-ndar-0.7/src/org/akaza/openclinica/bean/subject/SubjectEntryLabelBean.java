package org.akaza.openclinica.bean.subject;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

public class SubjectEntryLabelBean extends AuditableEntityBean {
    private String description;
    public SubjectEntryLabelBean(){
        
    }
    
    public String getDescription(){
        return description;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
}
