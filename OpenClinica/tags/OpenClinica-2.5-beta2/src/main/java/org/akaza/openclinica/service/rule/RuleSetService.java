/* 
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleBulkExecuteContainer;
import org.akaza.openclinica.bean.rule.RuleBulkExecuteContainerTwo;
import org.akaza.openclinica.bean.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;
import org.akaza.openclinica.bean.rule.action.ActionProcessor;
import org.akaza.openclinica.bean.rule.action.ActionProcessorFacade;
import org.akaza.openclinica.bean.rule.action.RuleActionBean;
import org.akaza.openclinica.bean.rule.expression.ExpressionBean;
import org.akaza.openclinica.bean.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.rule.RuleSetDAO;
import org.akaza.openclinica.dao.rule.RuleSetRuleDAO;
import org.akaza.openclinica.dao.rule.action.RuleActionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.logic.expressionTree.OpenClinicaExpressionParser;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

public class RuleSetService {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    DataSource ds;
    private RuleSetDAO ruleSetDao;
    private RuleSetRuleDAO ruleSetRuleDao;
    private RuleActionDAO ruleActionDao;
    private CRFDAO crfDao;
    private CRFVersionDAO crfVersionDao;
    private StudyEventDAO studyEventDao;
    private ItemDataDAO itemDataDao;
    private ExpressionService expressionService;
    private EventCRFDAO eventCrfDao;
    private StudySubjectDAO studySubjectDao;
    private ItemFormMetadataDAO itemFormMetadataDao;
    private SectionDAO sectionDao;
    private String requestURLMinusServletPath;
    private String contextPath;

    public RuleSetService(DataSource ds, String requestURLMinusServletPath, String contextPath) {
        this.ds = ds;
        this.requestURLMinusServletPath = requestURLMinusServletPath;
        this.contextPath = contextPath;
    }

    /**
     * Now I know why ORM are pretty cool. Takes care of saving the whole object graph.
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
        logger.info("RuleSet Id " + persistentRuleSetBean.getId());
        getRuleSetRuleDao().removeByRuleSet(persistentRuleSetBean);

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

    public HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesInBulk(String crfId, Boolean dryRun,
            StudyBean currentStudy, UserAccountBean ub) {
        CRFBean crf = new CRFBean();
        crf.setId(Integer.valueOf(crfId));
        List<RuleSetBean> ruleSets = getRuleSetsByCrfAndStudy(crf, currentStudy);
        ruleSets = filterByStatusEqualsAvailable(ruleSets);
        ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets);
        ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
        return runRulesBulk(ruleSets, dryRun, currentStudy, null, ub);
    }

    public HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesInBulk(String ruleSetRuleId, String crfVersionId,
            Boolean dryRun, StudyBean currentStudy, UserAccountBean ub) {

        List<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
        RuleSetBean ruleSet = getRuleSetBeanByRuleSetRuleAndSubstituteCrfVersion(ruleSetRuleId, crfVersionId, currentStudy);
        if (ruleSet != null) {
            ruleSets.add(ruleSet);
        }
        ruleSets = filterByStatusEqualsAvailable(ruleSets);
        ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets);
        ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
        return runRulesBulk(ruleSets, dryRun, currentStudy, null, ub);
    }

    public List<RuleSetBasedViewContainer> runRulesInBulk(List<RuleSetBean> ruleSets, Boolean dryRun, StudyBean currentStudy, UserAccountBean ub) {
        ruleSets = filterByStatusEqualsAvailable(ruleSets);
        ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets);
        ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
        return runRulesBulkFromRuleSetScreen(ruleSets, dryRun, currentStudy, null, ub);
    }

    public List<RuleSetBean> getRuleSetsByCrfStudyAndStudyEventDefinition(StudyBean study, StudyEventDefinitionBean sed, CRFVersionBean crfVersion) {
        CRFBean crf = getCrfDao().findByVersionId(crfVersion.getId());
        logger.debug("crfVersionID : " + crfVersion.getId() + " studyId : " + study.getId() + " studyEventDefinition : " + sed.getId());
        List<RuleSetBean> ruleSets = getRuleSetDao().findByCrfVersionOrCrfAndStudyAndStudyEventDefinition(crfVersion, crf, study, sed);
        logger.info("getRuleSetsByCrfStudyAndStudyEventDefinition() : ruleSets Size {} : ", ruleSets.size());
        return eagerFetchRuleSet(ruleSets);
    }

    /*
     * Used to Manage RuleSets ,Hence will return all RuleSets whether removed or not
     * 
     */
    public List<RuleSetBean> getRuleSetsByStudy(StudyBean study) {
        logger.debug(" Study Id {} ", study.getId());
        List<RuleSetBean> ruleSets = getRuleSetDao().findAllByStudy(study);
        logger.info("getRuleSetsByStudy() : ruleSets Size : {}", ruleSets.size());
        return eagerFetchRuleSet(ruleSets);
    }

    public RuleSetBean getRuleSetByIdForManagement(StudyBean study, String id, RuleBean ruleBean) {
        logger.debug(" Study Id {} ", study.getId());
        RuleSetBean ruleSetBean = (RuleSetBean) getRuleSetDao().findByPK(Integer.valueOf(id));
        if (ruleSetBean != null) {
            ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
            ruleSets.add(ruleSetBean);
            ruleSetBean = ruleBean == null ? eagerFetchRuleSet(ruleSets).get(0) : eagerFetchRuleSet(ruleSets, ruleBean).get(0);
        }
        return ruleSetBean;
    }

    public RuleSetBean getRuleSetById(StudyBean study, String id, RuleBean ruleBean) {
        logger.debug(" Study Id {} ", study.getId());
        RuleSetBean ruleSetBean = (RuleSetBean) getRuleSetDao().findByPK(Integer.valueOf(id));
        if (ruleSetBean != null) {
            ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
            ruleSets.add(ruleSetBean);
            ruleSetBean = ruleBean == null ? eagerFetchRuleSet(ruleSets).get(0) : eagerFetchRuleSet(ruleSets, ruleBean).get(0);
        }
        return ruleSetBean;
    }

    public List<RuleSetBean> getRuleSetsByCrfAndStudy(CRFBean crfBean, StudyBean study) {
        List<RuleSetBean> ruleSets = getRuleSetDao().findByCrf(crfBean, study);
        return eagerFetchRuleSet(ruleSets);
    }

    private RuleSetBean getRuleSetBeanByRuleSetRuleAndSubstituteCrfVersion(String ruleSetRuleId, String crfVersionId, StudyBean currentStudy) {
        RuleSetBean ruleSetBean = null;
        if (ruleSetRuleId != null && crfVersionId != null && ruleSetRuleId.length() > 0 && crfVersionId.length() > 0) {
            RuleSetRuleBean ruleSetRule = (RuleSetRuleBean) getRuleSetRuleDao().findByPK(Integer.valueOf(ruleSetRuleId));
            ruleSetBean = getRuleSetById(currentStudy, String.valueOf(ruleSetRule.getRuleSetBean().getId()), ruleSetRule.getRuleBean());
            CRFVersionBean crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(Integer.valueOf(crfVersionId));
            ruleSetBean = replaceCrfOidInTargetExpression(ruleSetBean, crfVersion.getOid());
        }
        return ruleSetBean;
    }

    private List<RuleSetBean> eagerFetchRuleSet(List<RuleSetBean> ruleSets) {
        for (RuleSetBean ruleSetBean : ruleSets) {
            List<RuleSetRuleBean> ruleSetRules = getRuleSetRuleDao().findByRuleSet(ruleSetBean);
            for (RuleSetRuleBean ruleSetRuleBean : ruleSetRules) {
                List<RuleActionBean> ruleActions = getRuleActionDao().findByRuleSetRule(ruleSetRuleBean);
                ruleSetRuleBean.setActions(ruleActions);
            }
            logger.debug("Total Number of ruleSetRules in ruleSet {} is : {}", ruleSetBean.getId(), ruleSetRules.size());
            ruleSetBean.setRuleSetRules(ruleSetRules);
        }
        return ruleSets;
    }

    private List<RuleSetBean> eagerFetchRuleSet(List<RuleSetBean> ruleSets, RuleBean rule) {
        for (RuleSetBean ruleSetBean : ruleSets) {
            List<RuleSetRuleBean> ruleSetRules = getRuleSetRuleDao().findByRuleSetAndRule(ruleSetBean, rule);
            for (RuleSetRuleBean ruleSetRuleBean : ruleSetRules) {
                List<RuleActionBean> ruleActions = getRuleActionDao().findByRuleSetRule(ruleSetRuleBean);
                ruleSetRuleBean.setActions(ruleActions);
            }
            ruleSetBean.setRuleSetRules(ruleSetRules);
        }
        return ruleSets;
    }

    private ExpressionBean replaceSEDOrdinal(ExpressionBean targetExpression, StudyEventBean studyEvent) {
        ExpressionBean expression = new ExpressionBean(targetExpression.getContext(), targetExpression.getValue());
        expression.setValue(getExpressionService().replaceStudyEventDefinitionOIDWith(expression.getValue(), String.valueOf(studyEvent.getId())));
        return expression;
    }

    public List<RuleSetBean> filterByStatusEqualsAvailableOnlyRuleSetRules(List<RuleSetBean> ruleSets) {
        for (RuleSetBean ruleSet : ruleSets) {
            for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();)
                if (i.next().getStatus() != Status.AVAILABLE) {
                    i.remove();
                }
        }
        return ruleSets;
    }

    public List<RuleSetBean> filterByStatusEqualsAvailable(List<RuleSetBean> ruleSets) {
        for (Iterator<RuleSetBean> j = ruleSets.iterator(); j.hasNext();) {
            RuleSetBean ruleSet = j.next();
            if (ruleSet.getStatus() == Status.AVAILABLE) {
                for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();)
                    if (i.next().getStatus() != Status.AVAILABLE) {
                        i.remove();
                    }
            } else {
                j.remove();
            }
        }
        return ruleSets;
    }

    /**
     * Use in DataEntry Rule Execution Scenarios
     * 
     * A RuleSet has a Target with Value which is provided by rule Creator. value might be :
     * SE_TESTINGF[ALL].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64  
     * OR SE_TESTINGF[1].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64
     * OR SE_TESTINGF.F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64
     * in which case it would need to be transformed to
     * SE_TESTINGF[x].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[ALL].I_AGEN_DOSEDATE64 where x is the studyEventId.
     * 
     * @param ruleSets
     * @param studyEvent
     * @return
     */
    public List<RuleSetBean> filterRuleSetsByStudyEventOrdinal(List<RuleSetBean> ruleSets, StudyEventBean studyEvent) {
        ArrayList<RuleSetBean> validRuleSets = new ArrayList<RuleSetBean>();
        for (RuleSetBean ruleSetBean : ruleSets) {
            String studyEventDefinitionOrdinal = getExpressionService().getStudyEventDefinitionOrdninalCurated(ruleSetBean.getTarget().getValue());
            if (studyEventDefinitionOrdinal.equals("")) {
                ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
                validRuleSets.add(ruleSetBean);
            }
            if (studyEventDefinitionOrdinal.equals(studyEvent.getSampleOrdinal())) {
                ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
                validRuleSets.add(ruleSetBean);
            }
        }
        logger.debug("Size of RuleSets post filterRuleSetsByStudyEventOrdinal() {} ", validRuleSets.size());
        return validRuleSets;
    }

    @SuppressWarnings("unchecked")
    public List<RuleSetBean> filterRuleSetsByStudyEventOrdinal(List<RuleSetBean> ruleSets) {
        ArrayList<RuleSetBean> validRuleSets = new ArrayList<RuleSetBean>();
        for (RuleSetBean ruleSetBean : ruleSets) {
            String studyEventDefinitionOrdinal = getExpressionService().getStudyEventDefinitionOrdninalCurated(ruleSetBean.getTarget().getValue());
            String studyEventDefinitionOid = getExpressionService().getStudyEventDefenitionOid(ruleSetBean.getTarget().getValue());
            String crfOrCrfVersionOid = getExpressionService().getCrfOid(ruleSetBean.getTarget().getValue());
            List<StudyEventBean> studyEvents = getStudyEventDao().findAllByStudyEventDefinitionAndCrfOids(studyEventDefinitionOid, crfOrCrfVersionOid);
            logger.debug("studyEventDefinitionOrdinal {} , studyEventDefinitionOid {} , crfOrCrfVersionOid {} , studyEvents {}", new Object[] {
                studyEventDefinitionOrdinal, studyEventDefinitionOid, crfOrCrfVersionOid, studyEvents.size() });

            if (studyEventDefinitionOrdinal.equals("") && studyEvents.size() > 0) {
                for (StudyEventBean studyEvent : studyEvents) {
                    ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
                }
                validRuleSets.add(ruleSetBean);
            } else {
                for (StudyEventBean studyEvent : studyEvents) {
                    if (studyEventDefinitionOrdinal.equals(studyEvent.getSampleOrdinal())) {
                        ruleSetBean.addExpression(replaceSEDOrdinal(ruleSetBean.getTarget(), studyEvent));
                        validRuleSets.add(ruleSetBean);
                    }
                }
            }
        }
        logExpressions(validRuleSets);
        logger.debug("Size of RuleSets post filterRuleSetsByStudyEventOrdinal() {} ", validRuleSets.size());
        return validRuleSets;
    }

    private void logExpressions(List<RuleSetBean> validRuleSets) {
        if (logger.isDebugEnabled()) {
            for (RuleSetBean ruleSetBean : validRuleSets) {
                logger.debug("Expression : {} ", ruleSetBean.getTarget().getValue());
                for (ExpressionBean expression : ruleSetBean.getExpressions()) {
                    logger.debug("Expression post filtering SEDs : {} ", expression.getValue());
                }
            }
        }
    }

    /**
     * Iterate over ruleSet.getExpressions(). Given the following expression
     * SE_TESTINGF[studyEventId].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[X].I_AGEN_DOSEDATE64
     * X could be : ALL , "" , Number
     * if ALL or "" then iterate over all group ordinals if they exist and add.
     * if Number just add the number 
     *  
     * @param ruleSets
     * @param grouped
     * @return
     */
    public List<RuleSetBean> solidifyGroupOrdinalsUsingFormProperties(List<RuleSetBean> ruleSets, HashMap<String, Integer> grouped) {
        for (RuleSetBean ruleSet : ruleSets) {
            ArrayList<ExpressionBean> expressionsWithCorrectGroupOrdinal = new ArrayList<ExpressionBean>();
            for (ExpressionBean expression : ruleSet.getExpressions()) {
                logger.debug("solidifyGroupOrdinals: Expression Value : " + expression.getValue());
                String groupOIDConcatItemOID = getExpressionService().getGroupOidConcatWithItemOid(expression.getValue());
                String itemOID = getExpressionService().getItemOid(expression.getValue());
                String groupOrdinal = getExpressionService().getGroupOrdninalCurated(expression.getValue());

                if (grouped.containsKey(groupOIDConcatItemOID) && groupOrdinal.equals("")) {
                    for (int i = 0; i < grouped.get(groupOIDConcatItemOID); i++) {
                        ExpressionBean expBean = new ExpressionBean();
                        expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(expression.getValue(), (i + 1)));
                        expBean.setContext(expression.getContext());
                        expressionsWithCorrectGroupOrdinal.add(expBean);
                    }
                } else if (grouped.containsKey(groupOIDConcatItemOID) && !groupOrdinal.equals("")) {
                    ExpressionBean expBean = new ExpressionBean();
                    expBean.setValue(expression.getValue());
                    expBean.setContext(expression.getContext());
                    expressionsWithCorrectGroupOrdinal.add(expBean);
                } else if (grouped.containsKey(itemOID)) {
                    ExpressionBean expBean = new ExpressionBean();
                    expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(expression.getValue(), null));
                    expBean.setContext(expression.getContext());
                    expressionsWithCorrectGroupOrdinal.add(expBean);
                }
            }
            ruleSet.setExpressions(expressionsWithCorrectGroupOrdinal);
            for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
                logger.debug("expressionBean value : {} ", expressionBean.getValue());
            }
        }
        return ruleSets;
    }

    public List<RuleSetBean> filterRuleSetsBySectionAndGroupOrdinal(List<RuleSetBean> ruleSets, HashMap<String, Integer> grouped) {
        List<RuleSetBean> ruleSetsInThisSection = new ArrayList<RuleSetBean>();
        for (RuleSetBean ruleSet : ruleSets) {
            String expWithGroup = getExpressionService().getGroupOidConcatWithItemOid(ruleSet.getTarget().getValue());
            String expWithoutGroup = getExpressionService().getItemOid(ruleSet.getTarget().getValue());
            if (grouped.containsKey(expWithGroup)) {
                String ordinal = getExpressionService().getGroupOrdninalCurated(ruleSet.getTarget().getValue());
                if (ordinal.length() == 0 || grouped.get(expWithGroup) >= Integer.valueOf(ordinal)) {
                    ruleSetsInThisSection.add(ruleSet);
                }
            }
            if (grouped.containsKey(expWithoutGroup)) {
                ruleSetsInThisSection.add(ruleSet);
            }
        }
        logger.info("filterRuleSetsBySectionAndGroupOrdinal : ruleSets affecting the Whole Form : {} , ruleSets affecting this Section {} ", ruleSets.size(),
                ruleSetsInThisSection.size());
        return ruleSetsInThisSection;
    }

    /**
     * Iterate over ruleSet.getExpressions(). Given the following expression
     * SE_TESTINGF[studyEventId].F_AGEN_8_V204.IG_AGEN_DOSETABLE6[X].I_AGEN_DOSEDATE64
     * X could be : ALL , "" , Number
     * case 1 : if "" then iterate over itemDatas if they exist add.
     * case 2 : if Number just add the number 
     *  
     * @param ruleSets
     * @param grouped
     * @return
     */
    public List<RuleSetBean> filterRuleSetsByGroupOrdinal(List<RuleSetBean> ruleSets) {

        for (RuleSetBean ruleSetBean : ruleSets) {
            List<ExpressionBean> expressionsWithCorrectGroupOrdinal = new ArrayList<ExpressionBean>();
            for (ExpressionBean expression : ruleSetBean.getExpressions()) {
                String studyEventId = getExpressionService().getStudyEventDefenitionOrdninalCurated(expression.getValue());
                String itemOid = getExpressionService().getItemOid(expression.getValue());
                String itemGroupOid = getExpressionService().getItemGroupOid(expression.getValue());
                String groupOrdinal = getExpressionService().getGroupOrdninalCurated(expression.getValue());
                List<ItemDataBean> itemDatas = getItemDataDao().findByStudyEventAndOids(Integer.valueOf(studyEventId), itemOid, itemGroupOid);
                logger.debug("studyEventId {} , itemOid {} , itemGroupOid {} , groupOrdinal {} , itemDatas {}", new Object[] { studyEventId, itemOid,
                    itemGroupOid, groupOrdinal, itemDatas.size() });

                // case 1 : group ordinal = ""
                if (groupOrdinal.equals("") && itemDatas.size() > 0) {
                    for (int k = 0; k < itemDatas.size(); k++) {
                        ExpressionBean expBean = new ExpressionBean();
                        expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(expression.getValue(), (k + 1)));
                        expBean.setContext(expression.getContext());
                        expressionsWithCorrectGroupOrdinal.add(expBean);
                    }
                }
                // case 2 : group ordinal = x and itemDatas should be size >= x
                if (!groupOrdinal.equals("") && itemDatas.size() >= Integer.valueOf(groupOrdinal)) {
                    ExpressionBean expBean = new ExpressionBean();
                    expBean.setValue(getExpressionService().replaceGroupOidOrdinalInExpression(expression.getValue(), null));
                    expBean.setContext(expression.getContext());
                    expressionsWithCorrectGroupOrdinal.add(expBean);
                }
            }
            ruleSetBean.setExpressions(expressionsWithCorrectGroupOrdinal);
        }
        logExpressions(ruleSets);
        return ruleSets;
    }

    public HashMap<String, ArrayList<String>> runRules(List<RuleSetBean> ruleSets, Boolean dryRun, StudyBean currentStudy,
            HashMap<String, String> variableAndValue, UserAccountBean ub) {

        if (variableAndValue == null || variableAndValue.isEmpty()) {
            logger.warn("You must be executing Rules in Batch");
            variableAndValue = new HashMap<String, String>();
        }

        HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid = new HashMap<String, ArrayList<String>>();
        for (RuleSetBean ruleSet : ruleSets) {
            for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
                ruleSet.setTarget(expressionBean);

                for (RuleSetRuleBean ruleSetRule : ruleSet.getRuleSetRules()) {
                    String result = null;
                    RuleBean rule = ruleSetRule.getRuleBean();
                    ExpressionObjectWrapper eow = new ExpressionObjectWrapper(ds, currentStudy, rule.getExpression(), ruleSet, variableAndValue);
                    try {
                        OpenClinicaExpressionParser oep = new OpenClinicaExpressionParser(eow);
                        result = oep.parseAndEvaluateExpression(rule.getExpression().getValue());

                        HashMap<String, ArrayList<String>> messagesOfActionsToBeExecuted = ruleSetRule.getActionsAsKeyPair(result);
                        logger.info("RuleSet with target  : {} , Ran Rule : {}  The Result was : {} , Based on that {} action will be executed ", new Object[] {
                            ruleSet.getTarget().getValue(), rule.getName(), result, messagesOfActionsToBeExecuted.size() });

                        // Write Action messages into groupOrdinalPLusItemOid so we can display on Screen
                        groupOrdinalPLusItemOid =
                            populate(groupOrdinalPLusItemOid, messagesOfActionsToBeExecuted, getExpressionService().getGroupOrdninalConcatWithItemOid(
                                    ruleSet.getTarget().getValue()));
                        // If not a dryRun(Meaning don't execute Actions) and if actions exist then execute the Action
                        if (!dryRun && messagesOfActionsToBeExecuted.size() > 0) {
                            for (RuleActionBean ruleAction : ruleSetRule.getActions()) {
                                int itemDataBeanId = getExpressionService().getItemDataBeanFromDb(ruleSet.getTarget().getValue()).getId();
                                // getDiscrepancyNoteService().saveFieldNotes(ruleAction.getSummary(), itemDataBeanId, "ItemData", currentStudy, ub);
                                ActionProcessor ap = ActionProcessorFacade.getActionProcessor(ruleAction.getActionType(), ds);
                                ap.execute(ruleAction, itemDataBeanId, "ItemData", currentStudy, ub, prepareEmailContents(ruleSet, ruleSetRule, currentStudy,
                                        ruleAction));
                            }
                        }
                    } catch (OpenClinicaSystemException osa) {
                        // TODO: Auditing might happen here failed rule
                        logger.warn("RuleSet with target  : {} , Ran Rule : {} , It resulted in an error due to : {}", new Object[] {
                            ruleSet.getTarget().getValue(), rule.getName(), osa.getMessage() });
                    }
                }
            }
        }
        return groupOrdinalPLusItemOid;
    }

    public HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesBulk(List<RuleSetBean> ruleSets, Boolean dryRun,
            StudyBean currentStudy, HashMap<String, String> variableAndValue, UserAccountBean ub) {

        if (variableAndValue == null || variableAndValue.isEmpty()) {
            logger.warn("You must be executing Rules in Batch");
            variableAndValue = new HashMap<String, String>();
        }

        HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> crfViewSpecificOrderedObjects =
            new HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>>();
        for (RuleSetBean ruleSet : ruleSets) {
            for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
                ruleSet.setTarget(expressionBean);

                for (RuleSetRuleBean ruleSetRule : ruleSet.getRuleSetRules()) {
                    String result = null;
                    RuleBean rule = ruleSetRule.getRuleBean();
                    ExpressionObjectWrapper eow = new ExpressionObjectWrapper(ds, currentStudy, rule.getExpression(), ruleSet, variableAndValue);
                    try {
                        OpenClinicaExpressionParser oep = new OpenClinicaExpressionParser(eow);
                        result = oep.parseAndEvaluateExpression(rule.getExpression().getValue());

                        // Actions
                        List<RuleActionBean> actionListBasedOnRuleExecutionResult = ruleSetRule.getActions(result);

                        if (dryRun && actionListBasedOnRuleExecutionResult.size() > 0) {
                            crfViewSpecificOrderedObjects =
                                populateForCrfBasedRulesView(crfViewSpecificOrderedObjects, ruleSet, rule, result, currentStudy,
                                        actionListBasedOnRuleExecutionResult);
                        }

                        // If not a dryRun meaning run Actions
                        if (!dryRun) {
                            for (RuleActionBean ruleAction : actionListBasedOnRuleExecutionResult) {
                                int itemDataBeanId = getExpressionService().getItemDataBeanFromDb(ruleSet.getTarget().getValue()).getId();
                                // getDiscrepancyNoteService().saveFieldNotes(ruleAction.getSummary(), itemDataBeanId, "ItemData", currentStudy, ub);
                                ActionProcessor ap = ActionProcessorFacade.getActionProcessor(ruleAction.getActionType(), ds);
                                ap.execute(ruleAction, itemDataBeanId, "ItemData", currentStudy, ub, prepareEmailContents(ruleSet, ruleSetRule, currentStudy,
                                        ruleAction));

                            }
                        }
                    } catch (OpenClinicaSystemException osa) {
                        // TODO: Auditing might happen here failed rule
                        logger.warn("RuleSet with target  : {} , Ran Rule : {} , It resulted in an error due to : {}", new Object[] {
                            ruleSet.getTarget().getValue(), rule.getName(), osa.getMessage() });
                    }
                }
            }
        }
        logCrfViewSpecificOrderedObjects(crfViewSpecificOrderedObjects);
        return crfViewSpecificOrderedObjects;
    }

    public List<RuleSetBasedViewContainer> runRulesBulkFromRuleSetScreen(List<RuleSetBean> ruleSets, Boolean dryRun, StudyBean currentStudy,
            HashMap<String, String> variableAndValue, UserAccountBean ub) {

        if (variableAndValue == null || variableAndValue.isEmpty()) {
            logger.warn("You must be executing Rules in Batch");
            variableAndValue = new HashMap<String, String>();
        }

        List<RuleSetBasedViewContainer> ruleSetBasedView = new ArrayList<RuleSetBasedViewContainer>();
        for (RuleSetBean ruleSet : ruleSets) {
            for (ExpressionBean expressionBean : ruleSet.getExpressions()) {
                ruleSet.setTarget(expressionBean);

                for (RuleSetRuleBean ruleSetRule : ruleSet.getRuleSetRules()) {
                    String result = null;
                    RuleBean rule = ruleSetRule.getRuleBean();
                    ExpressionObjectWrapper eow = new ExpressionObjectWrapper(ds, currentStudy, rule.getExpression(), ruleSet, variableAndValue);
                    try {
                        OpenClinicaExpressionParser oep = new OpenClinicaExpressionParser(eow);
                        result = oep.parseAndEvaluateExpression(rule.getExpression().getValue());

                        HashMap<String, ArrayList<RuleActionBean>> actionsToBeExecuted = ruleSetRule.getAllActionsWithEvaluatesToAsKey(result);
                        logger.info("RuleSet with target  : {} , Ran Rule : {}  The Result was : {} , Based on that {} action will be executed ", new Object[] {
                            ruleSet.getTarget().getValue(), rule.getName(), result, actionsToBeExecuted.size() });

                        if (dryRun && actionsToBeExecuted.size() > 0) {
                            ruleSetBasedView = populateForRuleSetBasedView(ruleSetBasedView, ruleSet, rule, actionsToBeExecuted);
                        }

                        // If not a dryRun(Meaning don't execute Actions) and if actions exist then execute the Action
                        if (!dryRun && actionsToBeExecuted.size() > 0) {
                            for (RuleActionBean ruleAction : ruleSetRule.getActions()) {
                                int itemDataBeanId = getExpressionService().getItemDataBeanFromDb(ruleSet.getTarget().getValue()).getId();
                                ActionProcessor ap = ActionProcessorFacade.getActionProcessor(ruleAction.getActionType(), ds);
                                ap.execute(ruleAction, itemDataBeanId, "ItemData", currentStudy, ub, prepareEmailContents(ruleSet, ruleSetRule, currentStudy,
                                        ruleAction));
                                // getDiscrepancyNoteService().saveFieldNotes(ruleAction.getSummary(), itemDataBeanId, "ItemData", currentStudy, ub);
                            }
                        }
                    } catch (OpenClinicaSystemException osa) {
                        // TODO: Auditing might happen here failed rule
                        logger.warn("RuleSet with target  : {} , Ran Rule : {} , It resulted in an error due to : {}", new Object[] {
                            ruleSet.getTarget().getValue(), rule.getName(), osa.getMessage() });
                    }
                }
            }
        }
        return ruleSetBasedView;
    }

    private void logCrfViewSpecificOrderedObjects(
            HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> crfViewSpecificOrderedObjects) {
        for (RuleBulkExecuteContainer key1 : crfViewSpecificOrderedObjects.keySet()) {
            for (RuleBulkExecuteContainerTwo key2 : crfViewSpecificOrderedObjects.get(key1).keySet()) {
                String studySubjects = "";
                for (String studySubjectIds : crfViewSpecificOrderedObjects.get(key1).get(key2)) {
                    studySubjects += studySubjectIds + " : ";
                }
                logger.debug("key1 {} , key2 {} , studySubjectId {}", new Object[] { key1.toString(), key2.toString(), studySubjects });
            }
        }
    }

    private List<RuleSetBasedViewContainer> populateForRuleSetBasedView(List<RuleSetBasedViewContainer> theList, RuleSetBean ruleSet, RuleBean rule,
            HashMap<String, ArrayList<RuleActionBean>> actionsToBeExecuted) {

        StudyEventBean studyEvent =
            (StudyEventBean) getStudyEventDao().findByPK(
                    Integer.valueOf(getExpressionService().getStudyEventDefenitionOrdninalCurated(ruleSet.getTarget().getValue())));

        for (String akey : actionsToBeExecuted.keySet()) {
            for (RuleActionBean ruleAction : actionsToBeExecuted.get(akey)) {
                RuleSetBasedViewContainer container =
                    new RuleSetBasedViewContainer(rule.getName(), rule.getExpression().getValue(), akey, ruleAction.getActionType().toString(), ruleAction
                            .getSummary());
                if (!theList.contains(container)) {
                    theList.add(container);
                }
                StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDao().findByPK(studyEvent.getStudySubjectId());
                theList.get(theList.indexOf(container)).addSubject(studySubject.getLabel());
            }
        }
        return theList;
    }

    /**
     * Organize objects in a certain way so that we can show to Users on UI. 
     * step1 : Get StudyEvent , eventCrf , crfVersion from studyEventId. 
     * 
     * @param crfViewSpecificOrderedObjects
     * @param ruleSet
     * @param rule
     * @return
     */
    private HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> populateForCrfBasedRulesView(
            HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> crfViewSpecificOrderedObjects, RuleSetBean ruleSet,
            RuleBean rule, String result, StudyBean currentStudy, List<RuleActionBean> actions) {

        // step1
        StudyEventBean studyEvent =
            (StudyEventBean) getStudyEventDao().findByPK(
                    Integer.valueOf(getExpressionService().getStudyEventDefenitionOrdninalCurated(ruleSet.getTarget().getValue())));
        EventCRFBean eventCrf = (EventCRFBean) getEventCrfDao().findAllByStudyEvent(studyEvent).get(0);
        CRFVersionBean crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(eventCrf.getCRFVersionId());

        RuleBulkExecuteContainer key = new RuleBulkExecuteContainer(crfVersion.getName(), rule, result, actions);
        String key2String = getExpressionService().getCustomExpressionUsedToCreateView(ruleSet.getTarget().getValue(), studyEvent.getSampleOrdinal());
        String studyEventDefinitionName = getExpressionService().getStudyEventDefinitionFromExpression(ruleSet.getTarget().getValue(), currentStudy).getName();
        studyEventDefinitionName += " [" + studyEvent.getSampleOrdinal() + "]";
        String itemGroupName = getExpressionService().getItemGroupNameAndOrdinal(ruleSet.getTarget().getValue());
        String itemName = getExpressionService().getItemGroupExpression(ruleSet.getTarget().getValue()).getName();
        RuleBulkExecuteContainerTwo key2 = new RuleBulkExecuteContainerTwo(key2String, studyEvent, studyEventDefinitionName, itemGroupName, itemName);
        StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDao().findByPK(studyEvent.getStudySubjectId());

        if (crfViewSpecificOrderedObjects.containsKey(key)) {
            HashMap<RuleBulkExecuteContainerTwo, Set<String>> k = crfViewSpecificOrderedObjects.get(key);
            if (k.containsKey(key2)) {
                k.get(key2).add(String.valueOf(studySubject.getLabel()));
            } else {
                HashSet<String> values = new HashSet<String>();
                values.add(String.valueOf(studySubject.getLabel()));
                k.put(key2, values);
            }
        } else {
            HashMap<RuleBulkExecuteContainerTwo, Set<String>> k = new HashMap<RuleBulkExecuteContainerTwo, Set<String>>();
            HashSet<String> values = new HashSet<String>();
            values.add(String.valueOf(studyEvent.getStudySubjectId()));
            k.put(key2, values);
            crfViewSpecificOrderedObjects.put(key, k);
        }
        return crfViewSpecificOrderedObjects;
    }

    private HashMap<String, ArrayList<String>> populate(HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid,
            HashMap<String, ArrayList<String>> messagesOfActionsToBeExecuted, String groupOrdinalConcatWithItemOid) {

        if (messagesOfActionsToBeExecuted.size() > 0) {
            ArrayList<String> actionMessages = new ArrayList<String>();

            for (String key : messagesOfActionsToBeExecuted.keySet()) {
                actionMessages.addAll(messagesOfActionsToBeExecuted.get(key));
            }
            if (groupOrdinalPLusItemOid.containsKey(groupOrdinalConcatWithItemOid)) {
                groupOrdinalPLusItemOid.get(groupOrdinalConcatWithItemOid).addAll(actionMessages);
            } else {
                groupOrdinalPLusItemOid.put(groupOrdinalConcatWithItemOid, actionMessages);
            }
        }
        return groupOrdinalPLusItemOid;
    }

    private HashMap<String, String> prepareEmailContents(RuleSetBean ruleSet, RuleSetRuleBean ruleSetRule, StudyBean currentStudy, RuleActionBean ruleAction) {

        // get the Study Event
        StudyEventBean studyEvent =
            (StudyEventBean) getStudyEventDao().findByPK(
                    Integer.valueOf(getExpressionService().getStudyEventDefenitionOrdninalCurated(ruleSet.getTarget().getValue())));
        // get the Study Subject
        StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDao().findByPK(studyEvent.getStudySubjectId());
        // get the eventCrf & subsequently the CRF Version
        EventCRFBean eventCrf = (EventCRFBean) getEventCrfDao().findAllByStudyEvent(studyEvent).get(0);
        CRFVersionBean crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(eventCrf.getCRFVersionId());
        CRFBean crf = (CRFBean) getCrfDao().findByPK(crfVersion.getCrfId());

        String studyEventDefinitionName = getExpressionService().getStudyEventDefinitionFromExpression(ruleSet.getTarget().getValue(), currentStudy).getName();
        studyEventDefinitionName += " [" + studyEvent.getSampleOrdinal() + "]";

        String itemGroupName = getExpressionService().getItemGroupNameAndOrdinal(ruleSet.getTarget().getValue());
        ItemGroupBean itemGroupBean = getExpressionService().getItemGroupExpression(ruleSet.getTarget().getValue());
        ItemBean itemBean = getExpressionService().getItemExpression(ruleSet.getTarget().getValue(), itemGroupBean);
        String itemName = itemBean.getName();

        SectionBean section =
            (SectionBean) getSectionDAO().findByPK(getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemBean.getId(), crfVersion.getId()).getSectionId());

        StringBuffer sb = new StringBuffer();

        sb.append(SecureController.respage.getString("email_header_1"));
        sb.append(" " + contextPath + " ");
        sb.append(SecureController.respage.getString("email_header_2"));
        sb.append(" '" + currentStudy.getName() + "' ");
        sb.append(SecureController.respage.getString("email_header_3"));
        sb.append(" \n\n ");

        sb.append(SecureController.respage.getString("email_body_1") + " " + currentStudy.getName() + " \n ");
        sb.append(SecureController.respage.getString("email_body_2") + " " + studySubject.getName() + " \n ");
        sb.append(SecureController.respage.getString("email_body_3") + " " + studyEventDefinitionName + " \n ");
        sb.append(SecureController.respage.getString("email_body_4") + " " + crf.getName() + " " + crfVersion.getName() + " \n ");
        sb.append(SecureController.respage.getString("email_body_5") + " " + section.getTitle() + " \n ");
        sb.append(SecureController.respage.getString("email_body_6") + " " + itemGroupName + " \n ");
        sb.append(SecureController.respage.getString("email_body_7") + " " + itemName + " \n ");
        sb.append(SecureController.respage.getString("email_body_8") + " " + ruleAction.getSummary() + " \n ");

        sb.append(" \n\n ");
        sb.append(SecureController.respage.getString("email_body_9"));
        sb.append(" " + contextPath + " ");
        sb.append(SecureController.respage.getString("email_body_10"));
        sb.append(" \n");

        String requestURLMinusServletPath = getRequestURLMinusServletPath() == null ? "" : getRequestURLMinusServletPath();

        sb.append(requestURLMinusServletPath + "/ViewSectionDataEntry?ecId=" + eventCrf.getId() + "&sectionId=" + section.getId() + "&tabId="
            + section.getOrdinal());
        // &eventId="+ studyEvent.getId());
        sb.append("\n\n");
        sb.append(SecureController.respage.getString("email_footer"));

        String subject = contextPath + " - [" + currentStudy.getName() + "] ";
        String message = ruleAction.getSummary().length() < 20 ? ruleAction.getSummary() : ruleAction.getSummary().substring(0, 20) + " ... ";
        subject += message;

        HashMap<String, String> emailContents = new HashMap<String, String>();
        emailContents.put("body", sb.toString());
        emailContents.put("subject", subject);

        return emailContents;
    }

    public List<String> getGroupOrdinalPlusItemOids(List<RuleSetBean> ruleSets) {
        List<String> groupOrdinalPlusItemOid = new ArrayList<String>();
        for (RuleSetBean ruleSetBean : ruleSets) {
            String text = getExpressionService().getGroupOrdninalConcatWithItemOid(ruleSetBean.getTarget().getValue());
            groupOrdinalPlusItemOid.add(text);
            logger.debug("ruleSet id {} groupOrdinalPlusItemOid : {}", ruleSetBean.getId(), text);
        }
        return groupOrdinalPlusItemOid;
    }

    public RuleSetBean replaceCrfOidInTargetExpression(RuleSetBean ruleSetBean, String replacementCrfOid) {
        String expression = getExpressionService().replaceCRFOidInExpression(ruleSetBean.getTarget().getValue(), replacementCrfOid);
        ruleSetBean.getTarget().setValue(expression);
        return ruleSetBean;
    }

    public String getRequestURLMinusServletPath() {
        return requestURLMinusServletPath;
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

    private StudyEventDAO getStudyEventDao() {
        studyEventDao = this.studyEventDao != null ? studyEventDao : new StudyEventDAO(ds);
        return studyEventDao;
    }

    private ExpressionService getExpressionService() {
        expressionService = this.expressionService != null ? expressionService : new ExpressionService(ds);
        return expressionService;
    }

    private ItemDataDAO getItemDataDao() {
        itemDataDao = this.itemDataDao != null ? itemDataDao : new ItemDataDAO(ds);
        return itemDataDao;
    }

    private EventCRFDAO getEventCrfDao() {
        eventCrfDao = this.eventCrfDao != null ? eventCrfDao : new EventCRFDAO(ds);
        return eventCrfDao;
    }

    private CRFVersionDAO getCrfVersionDao() {
        crfVersionDao = this.crfVersionDao != null ? crfVersionDao : new CRFVersionDAO(ds);
        return crfVersionDao;
    }

    private StudySubjectDAO getStudySubjectDao() {
        studySubjectDao = this.studySubjectDao != null ? studySubjectDao : new StudySubjectDAO(ds);
        return studySubjectDao;
    }

    private ItemFormMetadataDAO getItemFormMetadataDAO() {
        itemFormMetadataDao = this.itemFormMetadataDao != null ? itemFormMetadataDao : new ItemFormMetadataDAO(ds);
        return itemFormMetadataDao;
    }

    private SectionDAO getSectionDAO() {
        sectionDao = this.sectionDao != null ? sectionDao : new SectionDAO(ds);
        return sectionDao;
    }

}
