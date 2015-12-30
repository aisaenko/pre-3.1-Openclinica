package org.akaza.openclinica.bean.subject;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

public class SubjectLabelMapBean extends AuditableEntityBean {
    private int subjectId;
    private int subjectEntryLabelId;
    
    public int getSubjectId(){
        return subjectId;
    }
    
    public void setSubjectId(int subjectId){
        this.subjectId = subjectId;
    }
    
    public int getSubjectEntryLabelId(){
        return subjectEntryLabelId;
    }
    
    public void setSubjectEntryLabelId(int subjectEntryLabelId){
        this.subjectEntryLabelId = subjectEntryLabelId;
    }
}
