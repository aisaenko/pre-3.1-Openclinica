package org.akaza.openclinica.bean.rule.action;

public class DiscrepancyNoteActionBean extends RuleActionBean {

    private String message;

    public DiscrepancyNoteActionBean() {
        setActionType(ActionType.FILE_DISCREPANCY_NOTE);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getSummary() {
        // TODO Auto-generated method stub
        return this.message;
    }
}
