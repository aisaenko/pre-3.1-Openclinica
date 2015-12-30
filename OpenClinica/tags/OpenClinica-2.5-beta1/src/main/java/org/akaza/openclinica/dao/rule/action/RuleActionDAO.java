/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.dao.rule.action;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;
import org.akaza.openclinica.bean.rule.action.ActionType;
import org.akaza.openclinica.bean.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.bean.rule.action.RuleActionBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.rule.RuleDAO;
import org.akaza.openclinica.dao.rule.RuleSetDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

/**
 * <p>
 * Manage Actions
 * 
 * 
 * @author Krikor Krumlian
 * 
 */
public class RuleActionDAO extends AuditableEntityDAO {

    private EventCRFDAO eventCrfDao;
    private RuleSetDAO ruleSetDao;
    private RuleDAO ruleDao;
    private ItemDataDAO itemDataDao;
    private StudyEventDefinitionDAO studyEventDefinitionDao;
    private CRFVersionDAO crfVersionDao;

    private void setQueryNames() {
        this.findByPKAndStudyName = "findByPKAndStudy";
        this.getCurrentPKName = "getCurrentPK";
    }

    public RuleActionDAO(DataSource ds) {
        super(ds);
        setQueryNames();
    }

    private StudyEventDefinitionDAO getStudyEventDefinitionDao() {
        return this.studyEventDefinitionDao != null ? this.studyEventDefinitionDao : new StudyEventDefinitionDAO(ds);
    }

    private RuleSetDAO getRuleSetDao() {
        return this.ruleSetDao != null ? this.ruleSetDao : new RuleSetDAO(ds);
    }

    private RuleDAO getRuleDao() {
        return this.ruleDao != null ? this.ruleDao : new RuleDAO(ds);
    }

    private EventCRFDAO getEventCrfDao() {
        return this.eventCrfDao != null ? this.eventCrfDao : new EventCRFDAO(ds);
    }

    private CRFVersionDAO getCrfVersionDao() {
        return this.crfVersionDao != null ? this.crfVersionDao : new CRFVersionDAO(ds);
    }

    private ItemDataDAO getItemDataDao() {
        return this.itemDataDao != null ? this.itemDataDao : new ItemDataDAO(ds);
    }

    public RuleActionDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
        setQueryNames();
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_RULE_ACTION;
    }

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT); // rule_action_id
        this.setTypeExpected(2, TypeNames.INT); // rule_set_rule_id
        this.setTypeExpected(3, TypeNames.INT); // action_type
        this.setTypeExpected(4, TypeNames.BOOL); // expression_evaluates_to
        this.setTypeExpected(5, TypeNames.STRING); // message

        this.setTypeExpected(6, TypeNames.INT);// owner_id
        this.setTypeExpected(7, TypeNames.DATE); // date_created
        this.setTypeExpected(8, TypeNames.DATE);// date_updated
        this.setTypeExpected(9, TypeNames.INT);// updater_id
        this.setTypeExpected(10, TypeNames.INT);// status_id

    }

    public EntityBean update(EntityBean eb) {
        RuleBean ruleBean = (RuleBean) eb;

        ruleBean.setActive(false);

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        HashMap nullVars = new HashMap();
        variables.put(new Integer(1), ruleBean.getName());

        this.execute(digester.getQuery("update"), variables, nullVars);

        if (isQuerySuccessful()) {
            ruleBean.setActive(true);
        }

        return ruleBean;
    }

    public EntityBean create(EntityBean eb) {
        DiscrepancyNoteActionBean dnActionBean = (DiscrepancyNoteActionBean) eb;
        Boolean expressionEvaluates = dnActionBean.getExpressionEvaluatesTo() == null ? true : dnActionBean.getExpressionEvaluatesTo();

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();

        variables.put(new Integer(1), new Integer(dnActionBean.getRuleSetRule().getId()));
        variables.put(new Integer(2), dnActionBean.getActionType().getCode());
        variables.put(new Integer(3), expressionEvaluates);
        variables.put(new Integer(4), dnActionBean.getMessage());

        variables.put(new Integer(5), new Integer(dnActionBean.getOwnerId()));
        variables.put(new Integer(6), new Integer(Status.AVAILABLE.getId()));

        execute(digester.getQuery("create"), variables, nullVars);

        if (isQuerySuccessful()) {
            dnActionBean.setId(getCurrentPK());
        }

        return dnActionBean;
    }

    public RuleActionBean getEntityFromHashMap(HashMap hm) {

        int actionTypeId = ((Integer) hm.get("action_type")).intValue();
        ActionType actionType = ActionType.getByCode(actionTypeId);

        // TODO : In the future actionType will dictate what kind of ActionBean
        // is needs to be created
        DiscrepancyNoteActionBean dnAction = new DiscrepancyNoteActionBean();
        this.setEntityAuditInformation(dnAction, hm);

        dnAction.setId(((Integer) hm.get("rule_action_id")).intValue());
        dnAction.setActionType(actionType);
        dnAction.setExpressionEvaluatesTo(((Boolean) hm.get("expression_evaluates_to")).booleanValue());
        dnAction.setMessage(((String) hm.get("message")));

        return dnAction;
    }

    public Collection findAll() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAll"));
        ArrayList<RuleActionBean> ruleSetBeans = new ArrayList<RuleActionBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            RuleActionBean ruleSet = this.getEntityFromHashMap((HashMap) it.next());
            ruleSetBeans.add(ruleSet);
        }
        return ruleSetBeans;
    }

    public EntityBean findByPK(int ID) {
        RuleActionBean action = new RuleActionBean();
        this.setTypesExpected();

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), new Integer(ID));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            action = this.getEntityFromHashMap((HashMap) it.next());
        }

        return action;
    }

    public ArrayList<RuleActionBean> findByRuleSetRule(RuleSetRuleBean ruleSetRule) {
        this.setTypesExpected();

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        Integer ruleSetRuleId = Integer.valueOf(ruleSetRule.getId());
        variables.put(new Integer(1), ruleSetRuleId);

        String sql = digester.getQuery("findByRuleSetRule");
        ArrayList<?> alist = this.select(sql, variables);
        ArrayList<RuleActionBean> ruleActionBeans = new ArrayList<RuleActionBean>();
        Iterator<?> it = alist.iterator();
        while (it.hasNext()) {
            RuleActionBean ruleActionBean = this.getEntityFromHashMap((HashMap<?, ?>) it.next());
            ruleActionBean.setRuleSetRule(ruleSetRule);
            ruleActionBeans.add(ruleActionBean);
        }
        return ruleActionBeans;
    }

    /*
     * Why should we even have these in here if they are not needed? TODO:
     * refactor super class to remove dependency.
     */
    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    /*
     * Why should we even have these in here if they are not needed? TODO:
     * refactor super class to remove dependency.
     */
    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    /*
     * Why should we even have these in here if they are not needed? TODO:
     * refactor super class to remove dependency.
     */
    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

}