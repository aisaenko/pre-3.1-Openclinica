package org.akaza.openclinica.bean.logic;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

public class SkipRuleBean extends AuditableEntityBean {
    private String instruction;
    private String condition;
    private String skipAction;
    private int crfVersionId;
    
    public SkipRuleBean(){
        super();
        instruction = "";
        condition = "";
        skipAction = "";
        crfVersionId = 0;
    }
    
    public String getInstruction(){
        return instruction;
    }
    
    public void setInstruction(String instruction){
        this.instruction = instruction;
    }
    
    public String getCondition(){
        return condition;
    }
    
    public void setCondition(String condition){
        this.condition = condition;
    }
    
    public String getSkipAction(){
        return skipAction;
    }
    
    public void setSkipAction(String skipAction){
        this.skipAction = skipAction;
    }
    
    public int getCrfVersionId(){
        return crfVersionId;
    }
    
    public void setCrfVersionId(int crfVersionId){
        this.crfVersionId = crfVersionId;
    }
}
