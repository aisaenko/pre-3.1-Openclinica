/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.rule;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.rule.RuleExecutionBusinessObject;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * Execute a Cross Edit Check
 * 
 * @author Krikor Krumlian
 * 
 */
public class ExecuteCrossEditCheckServlet extends SecureController {

    Locale locale;

    public static final String DIS_TYPES = "discrepancyTypes";
    public static final String RES_STATUSES = "resolutionStatuses";
    public static final String ENTITY_ID = "id";
    public static final String PARENT_ID = "parentId";// parent note id
    public static final String ENTITY_TYPE = "name";
    public static final String ENTITY_COLUMN = "column";
    public static final String ENTITY_FIELD = "field";
    public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";
    public static final String DIS_NOTE = "discrepancyNote";
    public static final String WRITE_TO_DB = "writeToDB";
    public static final String PRESET_RES_STATUS = "strResStatus";

    @Override
    protected void processRequest() throws Exception {
        // FormProcessor fp = new FormProcessor(request);

        String eventCrfId = request.getParameter("eventCrfId");
        RuleExecutionBusinessObject ruleExecutionBusinessObject = new RuleExecutionBusinessObject(sm, currentStudy, ub);
        ruleExecutionBusinessObject.runRule(Integer.parseInt(eventCrfId));
        // forwardPage(Page.SUBMIT_DATA_SERVLET);
        forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET);
        // >> changed tbh, 06/2009

    }
}
