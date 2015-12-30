package org.akaza.openclinica.bean.rule.action;

import org.akaza.openclinica.core.SessionManager;

public class DiscrepancyNoteActionProcessor implements ActionProcessor {

    SessionManager sm;

    public DiscrepancyNoteActionProcessor(SessionManager sm) {
        this.sm = sm;
    }

}
