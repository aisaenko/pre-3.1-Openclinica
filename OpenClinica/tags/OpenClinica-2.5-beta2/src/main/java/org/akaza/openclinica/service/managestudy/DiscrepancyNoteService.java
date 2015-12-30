/* 
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.managestudy;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.rule.RuleSetDAO;
import org.akaza.openclinica.dao.rule.RuleSetRuleDAO;
import org.akaza.openclinica.dao.rule.action.RuleActionDAO;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DiscrepancyNoteService {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    DataSource ds;
    private RuleSetDAO ruleSetDao;
    private RuleSetRuleDAO ruleSetRuleDao;
    private RuleActionDAO ruleActionDao;
    private CRFDAO crfDao;
    private OidGenerator oidGenerator;
    private ExpressionService expressionService;
    private DiscrepancyNoteDAO discrepancyNoteDao;

    public DiscrepancyNoteService(DataSource ds) {
        this.ds = ds;
    }

    public void saveFieldNotes(String description, int entityId, String entityType, StudyBean sb, UserAccountBean ub) {

        DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
        dnb.setEntityId(entityId);
        dnb.setStudyId(sb.getId());
        dnb.setEntityType(entityType);
        dnb.setDescription(description);
        dnb.setDiscrepancyNoteTypeId(1);
        dnb.setResolutionStatusId(1);
        dnb.setColumn("value");
        dnb.setOwner(ub);
        dnb = (DiscrepancyNoteBean) getDiscrepancyNoteDao().create(dnb);
        getDiscrepancyNoteDao().createMapping(dnb);
    }

    private RuleSetDAO getRuleSetDao() {
        ruleSetDao = this.ruleSetDao != null ? ruleSetDao : new RuleSetDAO(ds);
        return ruleSetDao;
    }

    private CRFDAO getCrfDao() {
        crfDao = this.crfDao != null ? crfDao : new CRFDAO(ds);
        return crfDao;
    }

    private RuleSetRuleDAO getRuleSetRuleDao() {
        ruleSetRuleDao = this.ruleSetRuleDao != null ? ruleSetRuleDao : new RuleSetRuleDAO(ds);
        return ruleSetRuleDao;
    }

    private RuleActionDAO getRuleActionDao() {
        ruleActionDao = this.ruleActionDao != null ? ruleActionDao : new RuleActionDAO(ds);
        return ruleActionDao;
    }

    private ExpressionService getExpressionService() {
        expressionService = this.expressionService != null ? expressionService : new ExpressionService(ds);
        return expressionService;
    }

    private DiscrepancyNoteDAO getDiscrepancyNoteDao() {
        discrepancyNoteDao = this.discrepancyNoteDao != null ? discrepancyNoteDao : new DiscrepancyNoteDAO(ds);
        return discrepancyNoteDao;
    }

}
