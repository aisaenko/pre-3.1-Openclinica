/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.rule.RuleSetDAO;
import org.akaza.openclinica.dao.rule.RuleSetRuleDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.Page;

/**
 * @author Krikor Krumlian
 *
 */
public class UpdateRuleSetRuleServlet extends SecureController {

    private static final long serialVersionUID = 1L;
    RuleSetDAO ruleSetDao;
    RuleSetService ruleSetService;
    RuleSetRuleDAO ruleSetRuleDao;

    private static String RULESET_ID = "ruleSetId";
    private static String RULESETRULE_ID = "ruleSetRuleId";
    private static String RULESET = "ruleSet";
    private static String ACTION = "action";

    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        String ruleSetId = request.getParameter(RULESET_ID);
        String ruleSetRuleId = request.getParameter(RULESETRULE_ID);
        String action = request.getParameter(ACTION);
        RuleSetRuleBean ruleSetRule = (RuleSetRuleBean) getRuleSetRuleDao().findByPK(Integer.valueOf(ruleSetRuleId));
        if (ruleSetRuleId != null && action.equals("remove")) {
            getRuleSetRuleDao().remove(ruleSetRule, ub);
        } else if (ruleSetRuleId != null && action.equals("restore")) {
            getRuleSetRuleDao().restore(ruleSetRule, ub);
        }
        forwardPage(Page.LIST_RULE_SETS_SERVLET);
    }

    private RuleSetRuleDAO getRuleSetRuleDao() {
        ruleSetRuleDao = this.ruleSetRuleDao != null ? ruleSetRuleDao : new RuleSetRuleDAO(sm.getDataSource());
        return ruleSetRuleDao;
    }

}
