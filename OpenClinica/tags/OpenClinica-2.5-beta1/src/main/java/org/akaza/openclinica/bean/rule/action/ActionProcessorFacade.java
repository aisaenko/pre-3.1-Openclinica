package org.akaza.openclinica.bean.rule.action;

import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.exception.OpenClinicaException;

public class ActionProcessorFacade {

    public static ActionProcessor getActionProcessor(ActionType actionType, SessionManager sm) throws OpenClinicaException {
        switch (actionType) {
        case FILE_DISCREPANCY_NOTE:
            return new DiscrepancyNoteActionProcessor(sm);
        default:
            throw new OpenClinicaException("actionType", "Unrecognized action type!");
        }
    }
}
