/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Krikor Krumlian
 */
public class RunRuleSetServlet extends SecureController {

    private static String RULESET_ID = "ruleSetId";
    private static String RULE_ID = "ruleId";
    private static String RULESET = "ruleSet";
    private static String RULESET_RESULT = "ruleSetResult";
    private RuleSetService ruleSetService;

    /**
     * 
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        String ruleSetId = request.getParameter(RULESET_ID);
        String ruleId = request.getParameter(RULE_ID);
        String dryRun = request.getParameter("dryRun");

        RuleSetBean ruleSetBean = getRuleSetBean(ruleSetId, ruleId);
        if (ruleSetBean != null) {
            List<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
            ruleSets.add(ruleSetBean);
            if (dryRun != null && dryRun.equals("no")) {
                List<RuleSetBasedViewContainer> resultOfRunningRules = getRuleSetService().runRulesInBulk(ruleSets, false, currentStudy, ub);
                forwardPage(Page.LIST_RULE_SETS_SERVLET);

            } else {
                List<RuleSetBasedViewContainer> resultOfRunningRules = getRuleSetService().runRulesInBulk(ruleSets, true, currentStudy, ub);
                request.setAttribute(RULESET, ruleSetBean);
                request.setAttribute(RULESET_RESULT, resultOfRunningRules);
                forwardPage(Page.VIEW_EXECUTED_RULES);

            }

        } else {
            addPageMessage(respage.getString("please_choose_a_CRF_to_view"));
            forwardPage(Page.CRF_LIST);
        }
    }

    private RuleSetBean getRuleSetBean(String ruleSetId, String ruleId) {
        RuleSetBean ruleSetBean = null;
        if (ruleId != null && ruleSetId != null && ruleId.length() > 0 && ruleSetId.length() > 0) {
            RuleBean ruleBean = new RuleBean();
            ruleBean.setId(Integer.valueOf(ruleId));
            ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId, ruleBean);
        } else if (ruleSetId != null && ruleSetId.length() > 0) {
            ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId, null);
        }
        return ruleSetBean;
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

    private RuleSetService getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : new RuleSetService(sm.getDataSource(), getRequestURLMinusServletPath(), getContextPath());
        return ruleSetService;
    }

}
