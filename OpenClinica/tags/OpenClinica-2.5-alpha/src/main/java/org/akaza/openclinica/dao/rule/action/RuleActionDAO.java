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
import org.akaza.openclinica.bean.rule.RuleSetBean;
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
        this.setTypeExpected(1, TypeNames.INT); // action_id
        this.setTypeExpected(2, TypeNames.INT); // rule_id

        this.setTypeExpected(3, TypeNames.INT); // action_type
        this.setTypeExpected(4, TypeNames.STRING); // message

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
        Boolean expressionEvaluates = dnActionBean.getIfExpressionEvaluates() == null ? true : dnActionBean.getIfExpressionEvaluates();

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

    public Object getEntityFromHashMap(HashMap hm) {

        int actionTypeId = ((Integer) hm.get("action_type")).intValue();
        ActionType actionType = ActionType.getByCode(actionTypeId);

        // TODO : In the future this needs to change
        DiscrepancyNoteActionBean dnAction = new DiscrepancyNoteActionBean();
        this.setEntityAuditInformation(dnAction, hm);

        dnAction.setId(((Integer) hm.get("action_id")).intValue());
        dnAction.setActionType(actionType);

        int ruleId = ((Integer) hm.get("rule_id")).intValue();
        // dnAction.setRuleSetRule((RuleBean) getRuleDao().findByPK(ruleId));
        dnAction.setMessage((String) hm.get("message"));

        return dnAction;
    }

    public Collection findAll() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAll"));
        ArrayList<RuleSetBean> ruleSetBeans = new ArrayList<RuleSetBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            RuleSetBean ruleSet = (RuleSetBean) this.getEntityFromHashMap((HashMap) it.next());
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
            action = (RuleActionBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return action;
    }

    public ArrayList<RuleBean> findByRuleSet(RuleSetBean ruleSet) {
        this.setTypesExpected();

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        Integer eventCrfBeanId = Integer.valueOf(ruleSet.getId());
        variables.put(new Integer(1), eventCrfBeanId);

        String sql = digester.getQuery("findByRuleSet");
        ArrayList alist = this.select(sql, variables);
        ArrayList<RuleBean> ruleSetBeans = new ArrayList<RuleBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            RuleBean ruleBean = (RuleBean) this.getEntityFromHashMap((HashMap) it.next());
            ruleSetBeans.add(ruleBean);
        }
        return ruleSetBeans;
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