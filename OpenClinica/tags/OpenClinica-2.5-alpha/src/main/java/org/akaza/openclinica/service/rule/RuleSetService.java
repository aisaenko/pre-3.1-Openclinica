/* 
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;
import org.akaza.openclinica.bean.rule.action.RuleActionBean;
import org.akaza.openclinica.dao.rule.RuleSetDAO;
import org.akaza.openclinica.dao.rule.RuleSetRuleDAO;
import org.akaza.openclinica.dao.rule.action.RuleActionDAO;

import java.util.logging.Logger;

import javax.sql.DataSource;

public class RuleSetService {

    protected final Logger logger = Logger.getLogger(getClass().getName());
    DataSource ds;
    private RuleSetDAO ruleSetDao;
    private RuleSetRuleDAO ruleSetRuleDao;
    private RuleActionDAO ruleActionDao;
    private OidGenerator oidGenerator;

    public RuleSetService(DataSource ds) {
        oidGenerator = new GenericOidGenerator();
        this.ds = ds;
    }

    public boolean enableRules(RuleSetBean ruleSet) {
        return true;
    }

    public boolean disableRules() {
        return true;

    }

    /**
     * Now I know why ORM are pretty cool. Takes care of saving the whole object
     * graph.
     * 
     * @param ruleSetBean
     * @return
     */
    public RuleSetBean saveRuleSet(RuleSetBean ruleSetBean) {
        RuleSetBean persistentRuleSetBean = (RuleSetBean) getRuleSetDao().create(ruleSetBean);
        // Save RuleSetRules
        for (RuleSetRuleBean ruleSetRule : persistentRuleSetBean.getRuleSetRules()) {
            ruleSetRule.setRuleSetBean(persistentRuleSetBean);
            getRuleSetRuleDao().create(ruleSetRule);
            // Save Actions
            for (RuleActionBean action : ruleSetRule.getActions()) {
                action.setRuleSetRule(ruleSetRule);
                getRuleActionDao().create(action);
            }
        }
        return persistentRuleSetBean;
    }

    public RuleSetBean replaceRuleSet(RuleSetBean ruleSetBean) {
        RuleSetBean persistentRuleSetBean = ruleSetBean;

        // Invalidate Previous RuleSetRules
        System.out.println("RuleSet Id " + persistentRuleSetBean.getId());
        getRuleSetRuleDao().updateStatus(persistentRuleSetBean);

        // Save RuleSetRules
        for (RuleSetRuleBean ruleSetRule : persistentRuleSetBean.getRuleSetRules()) {
            ruleSetRule.setRuleSetBean(persistentRuleSetBean);
            getRuleSetRuleDao().create(ruleSetRule);
            // Save Actions
            for (RuleActionBean action : ruleSetRule.getActions()) {
                action.setRuleSetRule(ruleSetRule);
                getRuleActionDao().create(action);
            }
        }
        return persistentRuleSetBean;
    }

    private RuleSetDAO getRuleSetDao() {
        ruleSetDao = this.ruleSetDao != null ? ruleSetDao : new RuleSetDAO(ds);
        return ruleSetDao;
    }

    private RuleSetRuleDAO getRuleSetRuleDao() {
        ruleSetRuleDao = this.ruleSetRuleDao != null ? ruleSetRuleDao : new RuleSetRuleDAO(ds);
        return ruleSetRuleDao;
    }

    private RuleActionDAO getRuleActionDao() {
        ruleActionDao = this.ruleActionDao != null ? ruleActionDao : new RuleActionDAO(ds);
        return ruleActionDao;
    }

}
