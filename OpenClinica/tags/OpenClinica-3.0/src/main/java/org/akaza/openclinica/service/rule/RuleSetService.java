/* 
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.rule.action.RuleActionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.AuditableBeanWrapper;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainer;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainerTwo;
import org.akaza.openclinica.domain.rule.RuleSetAuditBean;
import org.akaza.openclinica.domain.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.logic.rulerunner.CrfBulkRuleRunner;
import org.akaza.openclinica.logic.rulerunner.DataEntryRuleRunner;
import org.akaza.openclinica.logic.rulerunner.RuleSetBulkRuleRunner;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

/**
 * @author krikor
 *
 */
public class RuleSetService implements RuleSetServiceInterface {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    DataSource dataSource;
    private RuleSetDao ruleSetDao;
    private RuleSetAuditDao ruleSetAuditDao;
    private RuleDao ruleDao;
    private RuleSetRuleDao ruleSetRuleDao;
    private JavaMailSenderImpl mailSender;
    // private RuleSetRuleDAO ruleSetRuleDao;

    // Jdbc based DAOs
    private StudyDAO studyDao;
    private StudyEventDefinitionDAO studyEventDefinitionDao;
    private CRFDAO crfDao;
    private CRFVersionDAO crfVersionDao;

    private RuleActionDAO ruleActionDao;
    private StudyEventDAO studyEventDao;
    private ItemDataDAO itemDataDao;
    private ExpressionService expressionService;
    private String requestURLMinusServletPath;
    private String contextPath;

    /* public RuleSetService(DataSource ds, String requestURLMinusServletPath, String contextPath) {
         this.dataSource = ds;
         this.requestURLMinusServletPath = requestURLMinusServletPath;
         this.contextPath = contextPath;
     }*/

    public RuleSetService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#saveRuleSet(org.akaza.openclinica.domain.rule.RuleSetBean)
     */

    public RuleSetBean saveRuleSet(RuleSetBean ruleSetBean) {
        RuleSetBean persistentRuleSetBean = getRuleSetDao().saveOrUpdate(ruleSetBean);
        return persistentRuleSetBean;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#saveImport(org.akaza.openclinica.domain.rule.RulesPostImportContainer)
     */
    @Transactional
    public void saveImport(RulesPostImportContainer rulesContainer) {
        for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getValidRuleDefs()) {
            getRuleDao().saveOrUpdate(ruleBeanWrapper.getAuditableBean());
        }
        for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleDefs()) {
            getRuleDao().saveOrUpdate(ruleBeanWrapper.getAuditableBean());
        }

        for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getValidRuleSetDefs()) {
            loadRuleSetRuleWithPersistentRules(ruleBeanWrapper.getAuditableBean());
            saveRuleSet(ruleBeanWrapper.getAuditableBean());
        }

        for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleSetDefs()) {
            loadRuleSetRuleWithPersistentRules(ruleBeanWrapper.getAuditableBean());
            replaceRuleSet(ruleBeanWrapper.getAuditableBean());
        }
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#updateRuleSet(org.akaza.openclinica.domain.rule.RuleSetBean, org.akaza.openclinica.bean.login.UserAccountBean, org.akaza.openclinica.domain.Status)
     */
    public RuleSetBean updateRuleSet(RuleSetBean ruleSetBean, UserAccountBean user, Status status) {
        ruleSetBean.setStatus(status);
        ruleSetBean.setUpdater(user);
        for (RuleSetRuleBean ruleSetRuleBean : ruleSetBean.getRuleSetRules()) {
            ruleSetRuleBean.setStatus(status);
            ruleSetRuleBean.setUpdater(user);
        }
        ruleSetBean = saveRuleSet(ruleSetBean);
        ruleSetAuditDao.saveOrUpdate(createRuleSetAuditBean(ruleSetBean, user, status));
        return ruleSetBean;

    }

    private RuleSetAuditBean createRuleSetAuditBean(RuleSetBean ruleSetBean, UserAccountBean user, Status status) {
        RuleSetAuditBean ruleSetAuditBean = new RuleSetAuditBean();
        ruleSetAuditBean.setRuleSetBean(ruleSetBean);
        ruleSetAuditBean.setStatus(status);
        ruleSetAuditBean.setUpdater(user);
        return ruleSetAuditBean;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#loadRuleSetRuleWithPersistentRules(org.akaza.openclinica.domain.rule.RuleSetBean)
     */
    public void loadRuleSetRuleWithPersistentRules(RuleSetBean ruleSetBean) {
        for (RuleSetRuleBean ruleSetRule : ruleSetBean.getRuleSetRules()) {
            if (ruleSetRule.getId() == null) {
                String ruleOid = ruleSetRule.getOid();
                ruleSetRule.setRuleBean(ruleDao.findByOid(ruleOid));
            }
        }
    }

    /*
    public RuleSetBean replaceRuleSet(RuleSetBean ruleSetBean) {
        RuleSetBean persistentRuleSetBean = ruleSetBean;

        // Invalidate Previous RuleSetRules
        logger.info("RuleSet Id " + persistentRuleSetBean.getId());
        getRuleSetRuleDao().removeByRuleSet(persistentRuleSetBean);

        // Save RuleSetRules
        for (RuleSetRuleBean ruleSetRule : persistentRuleSetBean.getRuleSetRules()) {
            ruleSetRule.setRuleSetBean(persistentRuleSetBean);
            getRuleSetRuleDao().saveOrUpdate(ruleSetRule);
            // Save Actions
            for (RuleActionBean action : ruleSetRule.getActions()) {
                action.setRuleSetRule(ruleSetRule);
                getRuleActionDao().saveOrUpdate(action);
            }
        }
        return persistentRuleSetBean;
    }*/

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#replaceRuleSet(org.akaza.openclinica.domain.rule.RuleSetBean)
     */
    public RuleSetBean replaceRuleSet(RuleSetBean ruleSetBean) {
        RuleSetBean detachedRuleSetBean = ruleSetBean;

        for (RuleSetRuleBean ruleSetRuleBean : detachedRuleSetBean.getRuleSetRules()) {
            if (ruleSetRuleBean.getId() != null) {
                ruleSetRuleBean.setStatus(org.akaza.openclinica.domain.Status.DELETED);
            }
        }
        return getRuleSetDao().saveOrUpdate(detachedRuleSetBean);
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#runRulesInBulk(java.lang.String, java.lang.Boolean, org.akaza.openclinica.bean.managestudy.StudyBean, org.akaza.openclinica.bean.login.UserAccountBean)
     */
    public HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> runRulesInBulk(String crfId, Boolean dryRun,
            StudyBean currentStudy, UserAccountBean ub) {
        CRFBean crf = new CRFBean();
        crf.setId(Integer.valueOf(crfId));
        List<RuleSetBean> ruleSets = getRuleSetsByCrfAndStudy(crf, currentStudy);
        ruleSets = filterByStatusEqualsAvailable(ruleSets);
        ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets);
        ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
        CrfBulkRuleRunner ruleRunner = new CrfBulkRuleRunner(dataSource, requestURLMinusServletPath, contextPath, mailSender);
        return ruleRunner.runRulesBulk(ruleSets, dryRun, currentStudy, null, ub);
        // return runRulesBulk(ruleSets, dryRun, currentStudy, null, ub);
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#runRulesInBulk(java.lang.String, java.lang.String, java.lang.Boolean, org.akaza.openclinica.bean.managestudy.StudyBean, org.akaza.openclinica.bean.login.UserAccountBean)
     */
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
        CrfBulkRuleRunner ruleRunner = new CrfBulkRuleRunner(dataSource, requestURLMinusServletPath, contextPath, mailSender);
        return ruleRunner.runRulesBulk(ruleSets, dryRun, currentStudy, null, ub);
        // return runRulesBulk(ruleSets, dryRun, currentStudy, null, ub);
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#runRulesInBulk(java.util.List, java.lang.Boolean, org.akaza.openclinica.bean.managestudy.StudyBean, org.akaza.openclinica.bean.login.UserAccountBean)
     */
    public List<RuleSetBasedViewContainer> runRulesInBulk(List<RuleSetBean> ruleSets, Boolean dryRun, StudyBean currentStudy, UserAccountBean ub) {
        ruleSets = filterByStatusEqualsAvailable(ruleSets);
        ruleSets = filterRuleSetsByStudyEventOrdinal(ruleSets);
        ruleSets = filterRuleSetsByGroupOrdinal(ruleSets);
        RuleSetBulkRuleRunner ruleRunner = new RuleSetBulkRuleRunner(dataSource, requestURLMinusServletPath, contextPath, mailSender);
        return ruleRunner.runRulesBulkFromRuleSetScreen(ruleSets, dryRun, currentStudy, null, ub);
        // return runRulesBulkFromRuleSetScreen(ruleSets, dryRun, currentStudy, null, ub);
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#runRulesInDataEntry(java.util.List, java.lang.Boolean, org.akaza.openclinica.bean.managestudy.StudyBean, org.akaza.openclinica.bean.login.UserAccountBean, java.util.HashMap)
     */
    public HashMap<String, ArrayList<String>> runRulesInDataEntry(List<RuleSetBean> ruleSets, Boolean dryRun, StudyBean currentStudy, UserAccountBean ub,
            HashMap<String, String> variableAndValue) {
        DataEntryRuleRunner ruleRunner = new DataEntryRuleRunner(dataSource, requestURLMinusServletPath, contextPath, mailSender);
        return ruleRunner.runRules(ruleSets, dryRun, currentStudy, variableAndValue, ub);
        // return runRules(ruleSets, dryRun, currentStudy, c.variableAndValue, ub);
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#getRuleSetsByCrfStudyAndStudyEventDefinition(org.akaza.openclinica.bean.managestudy.StudyBean, org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean, org.akaza.openclinica.bean.submit.CRFVersionBean)
     */
    public List<RuleSetBean> getRuleSetsByCrfStudyAndStudyEventDefinition(StudyBean study, StudyEventDefinitionBean sed, CRFVersionBean crfVersion) {
        CRFBean crf = getCrfDao().findByVersionId(crfVersion.getId());
        logger.debug("crfVersionID : " + crfVersion.getId() + " studyId : " + study.getId() + " studyEventDefinition : " + sed.getId());
        List<RuleSetBean> ruleSets = getRuleSetDao().findByCrfVersionOrCrfAndStudyAndStudyEventDefinition(crfVersion, crf, study, sed);
        logger.info("getRuleSetsByCrfStudyAndStudyEventDefinition() : ruleSets Size {} : ", ruleSets.size());
        for (RuleSetBean ruleSetBean : ruleSets) {
            getObjects(ruleSetBean);
        }
        return ruleSets;
        // return eagerFetchRuleSet(ruleSets);
    }

    /*
     * Used to Manage RuleSets ,Hence will return all RuleSets whether removed or not
     * 
     */
    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#getRuleSetsByStudy(org.akaza.openclinica.bean.managestudy.StudyBean)
     */
    public List<RuleSetBean> getRuleSetsByStudy(StudyBean study) {
        logger.debug(" Study Id {} ", study.getId());
        List<RuleSetBean> ruleSets = getRuleSetDao().findAllByStudy(study);
        for (RuleSetBean ruleSetBean : ruleSets) {
            getObjects(ruleSetBean);
        }
        logger.info("getRuleSetsByStudy() : ruleSets Size : {}", ruleSets.size());
        return ruleSets;
        // return eagerFetchRuleSet(ruleSets);
    }

    /* public RuleSetBean getRuleSetByIdForManagement(StudyBean study, String id, RuleBean ruleBean) {
         logger.debug(" Study Id {} ", study.getId());
         RuleSetBean ruleSetBean = getRuleSetDao().findById(Integer.valueOf(id));
         if (ruleSetBean != null) {
             ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
             ruleSets.add(ruleSetBean);
             ruleSetBean = ruleBean == null ? eagerFetchRuleSet(ruleSets).get(0) : eagerFetchRuleSet(ruleSets, ruleBean).get(0);
         }
         return ruleSetBean;
     }*/

    // . TODO: why are we including study but not using it in query
    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#getRuleSetById(org.akaza.openclinica.bean.managestudy.StudyBean, java.lang.String)
     */
    public RuleSetBean getRuleSetById(StudyBean study, String id) {
        logger.debug(" Study Id {} ", study.getId());
        RuleSetBean ruleSetBean = getRuleSetDao().findById(Integer.valueOf(id));
        if (ruleSetBean != null) {
            getObjects(ruleSetBean);
        }
        return ruleSetBean;

    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#getRuleSetById(org.akaza.openclinica.bean.managestudy.StudyBean, java.lang.String, org.akaza.openclinica.domain.rule.RuleBean)
     */
    public List<RuleSetRuleBean> getRuleSetById(StudyBean study, String id, RuleBean ruleBean) {
        logger.debug(" Study Id {} ", study.getId());
        RuleSetBean ruleSetBean = getRuleSetDao().findById(Integer.valueOf(id));
        return getRuleSetRuleDao().findByRuleSetBeanAndRuleBean(ruleSetBean, ruleBean);
    }

    /* public RuleSetBean getRuleSetById(StudyBean study, String id, RuleBean ruleBean) {
         logger.debug(" Study Id {} ", study.getId());
         RuleSetBean ruleSetBean = getRuleSetDao().findById(Integer.valueOf(id));
         if (ruleSetBean != null) {
             ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
             ruleSets.add(ruleSetBean);
             ruleSetBean = ruleBean == null ? eagerFetchRuleSet(ruleSets).get(0) : eagerFetchRuleSet(ruleSets, ruleBean).get(0);
         }
         return ruleSetBean;
     }*/

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#getRuleSetsByCrfAndStudy(org.akaza.openclinica.bean.admin.CRFBean, org.akaza.openclinica.bean.managestudy.StudyBean)
     */
    public List<RuleSetBean> getRuleSetsByCrfAndStudy(CRFBean crfBean, StudyBean study) {
        List<RuleSetBean> ruleSets = getRuleSetDao().findByCrf(crfBean, study);
        for (RuleSetBean ruleSetBean : ruleSets) {
            getObjects(ruleSetBean);
        }
        // return eagerFetchRuleSet(ruleSets);
        return ruleSets;
    }

    private RuleSetBean getObjects(RuleSetBean ruleSetBean) {
        ruleSetBean.setStudy((StudyBean) getStudyDao().findByPK(ruleSetBean.getStudyId()));
        ruleSetBean.setStudyEventDefinition((StudyEventDefinitionBean) getStudyEventDefinitionDao().findByPK(ruleSetBean.getStudyEventDefinitionId()));
        ruleSetBean.setCrf((CRFBean) getCrfDao().findByPK(ruleSetBean.getCrfId()));
        if (ruleSetBean.getCrfVersionId() != null) {
            ruleSetBean.setCrfVersion((CRFVersionBean) getCrfVersionDao().findByPK(ruleSetBean.getCrfVersionId()));
        }
        ruleSetBean.setItemGroup(getExpressionService().getItemGroupExpression(ruleSetBean.getTarget().getValue()));
        ruleSetBean.setItem(getExpressionService().getItemExpression(ruleSetBean.getTarget().getValue(), ruleSetBean.getItemGroup()));

        return ruleSetBean;

    }

    // TODO: look into the commented line make sure logic doesn't break
    private RuleSetBean getRuleSetBeanByRuleSetRuleAndSubstituteCrfVersion(String ruleSetRuleId, String crfVersionId, StudyBean currentStudy) {
        RuleSetBean ruleSetBean = null;
        if (ruleSetRuleId != null && crfVersionId != null && ruleSetRuleId.length() > 0 && crfVersionId.length() > 0) {
            RuleSetRuleBean ruleSetRule = getRuleSetRuleDao().findById(Integer.valueOf(ruleSetRuleId));
            // ruleSetBean = getRuleSetById(currentStudy, String.valueOf(ruleSetRule.getRuleSetBean().getId()), ruleSetRule.getRuleBean());
            ruleSetBean = ruleSetRule.getRuleSetBean();
            filterByRules(ruleSetBean, ruleSetRule.getRuleBean().getId());
            CRFVersionBean crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(Integer.valueOf(crfVersionId));
            ruleSetBean = replaceCrfOidInTargetExpression(ruleSetBean, crfVersion.getOid());
        }
        return ruleSetBean;
    }

    /*private List<RuleSetBean> eagerFetchRuleSet(List<RuleSetBean> ruleSets) {
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
    }*/

    private ExpressionBean replaceSEDOrdinal(ExpressionBean targetExpression, StudyEventBean studyEvent) {
        ExpressionBean expression = new ExpressionBean(targetExpression.getContext(), targetExpression.getValue());
        expression.setValue(getExpressionService().replaceStudyEventDefinitionOIDWith(expression.getValue(), String.valueOf(studyEvent.getId())));
        return expression;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#filterByStatusEqualsAvailableOnlyRuleSetRules(java.util.List)
     */
    public List<RuleSetBean> filterByStatusEqualsAvailableOnlyRuleSetRules(List<RuleSetBean> ruleSets) {
        for (RuleSetBean ruleSet : ruleSets) {
            for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();)
                if (i.next().getStatus() != org.akaza.openclinica.domain.Status.AVAILABLE) {
                    i.remove();
                }
        }
        return ruleSets;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#filterByStatusEqualsAvailable(java.util.List)
     */
    public List<RuleSetBean> filterByStatusEqualsAvailable(List<RuleSetBean> ruleSets) {
        for (Iterator<RuleSetBean> j = ruleSets.iterator(); j.hasNext();) {
            RuleSetBean ruleSet = j.next();
            if (ruleSet.getStatus() == org.akaza.openclinica.domain.Status.AVAILABLE) {
                for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();)
                    if (i.next().getStatus() != org.akaza.openclinica.domain.Status.AVAILABLE) {
                        i.remove();
                    }
            } else {
                j.remove();
            }
        }
        return ruleSets;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#filterByRules(org.akaza.openclinica.domain.rule.RuleSetBean, java.lang.Integer)
     */
    public RuleSetBean filterByRules(RuleSetBean ruleSet, Integer ruleBeanId) {

        for (Iterator<RuleSetRuleBean> i = ruleSet.getRuleSetRules().iterator(); i.hasNext();) {
            if (!i.next().getRuleBean().getId().equals(ruleBeanId)) {
                i.remove();
            }
        }
        return ruleSet;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#filterRuleSetsByStudyEventOrdinal(java.util.List, org.akaza.openclinica.bean.managestudy.StudyEventBean)
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

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#filterRuleSetsByStudyEventOrdinal(java.util.List)
     */
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

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#solidifyGroupOrdinalsUsingFormProperties(java.util.List, java.util.HashMap)
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

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#filterRuleSetsBySectionAndGroupOrdinal(java.util.List, java.util.HashMap)
     */
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

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#filterRuleSetsByGroupOrdinal(java.util.List)
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

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#getGroupOrdinalPlusItemOids(java.util.List)
     */
    public List<String> getGroupOrdinalPlusItemOids(List<RuleSetBean> ruleSets) {
        List<String> groupOrdinalPlusItemOid = new ArrayList<String>();
        for (RuleSetBean ruleSetBean : ruleSets) {
            String text = getExpressionService().getGroupOrdninalConcatWithItemOid(ruleSetBean.getTarget().getValue());
            groupOrdinalPlusItemOid.add(text);
            logger.debug("ruleSet id {} groupOrdinalPlusItemOid : {}", ruleSetBean.getId(), text);
        }
        return groupOrdinalPlusItemOid;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.service.rule.RuleSetServiceInterface#replaceCrfOidInTargetExpression(org.akaza.openclinica.domain.rule.RuleSetBean, java.lang.String)
     */
    public RuleSetBean replaceCrfOidInTargetExpression(RuleSetBean ruleSetBean, String replacementCrfOid) {
        String expression = getExpressionService().replaceCRFOidInExpression(ruleSetBean.getTarget().getValue(), replacementCrfOid);
        ruleSetBean.getTarget().setValue(expression);
        return ruleSetBean;
    }

    /**
     * @return the contextPath
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * @param contextPath the contextPath to set
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * @param requestURLMinusServletPath the requestURLMinusServletPath to set
     */
    public void setRequestURLMinusServletPath(String requestURLMinusServletPath) {
        this.requestURLMinusServletPath = requestURLMinusServletPath;
    }

    public String getRequestURLMinusServletPath() {
        return requestURLMinusServletPath;
    }

    public void setStudyDao(StudyDAO studyDao) {
        this.studyDao = studyDao;
    }

    public RuleSetDao getRuleSetDao() {
        return ruleSetDao;
    }

    public void setRuleSetDao(RuleSetDao ruleSetDao) {
        this.ruleSetDao = ruleSetDao;
    }

    public void setRuleSetRuleDao(RuleSetRuleDao ruleSetRuleDao) {
        this.ruleSetRuleDao = ruleSetRuleDao;
    }

    public RuleSetRuleDao getRuleSetRuleDao() {
        return ruleSetRuleDao;
    }

    public RuleDao getRuleDao() {
        return ruleDao;
    }

    public void setRuleDao(RuleDao ruleDao) {
        this.ruleDao = ruleDao;
    }

    private CRFDAO getCrfDao() {
        crfDao = this.crfDao != null ? crfDao : new CRFDAO(dataSource);
        return crfDao;
    }

    private StudyEventDAO getStudyEventDao() {
        studyEventDao = this.studyEventDao != null ? studyEventDao : new StudyEventDAO(dataSource);
        return studyEventDao;
    }

    private ExpressionService getExpressionService() {
        expressionService = this.expressionService != null ? expressionService : new ExpressionService(dataSource);
        return expressionService;
    }

    public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
        studyEventDefinitionDao = this.studyEventDefinitionDao != null ? studyEventDefinitionDao : new StudyEventDefinitionDAO(dataSource);
        return studyEventDefinitionDao;
    }

    public StudyDAO getStudyDao() {
        studyDao = this.studyDao != null ? studyDao : new StudyDAO(dataSource);
        return studyDao;
    }

    private ItemDataDAO getItemDataDao() {
        itemDataDao = this.itemDataDao != null ? itemDataDao : new ItemDataDAO(dataSource);
        return itemDataDao;
    }

    private CRFVersionDAO getCrfVersionDao() {
        crfVersionDao = this.crfVersionDao != null ? crfVersionDao : new CRFVersionDAO(dataSource);
        return crfVersionDao;
    }

    public RuleSetAuditDao getRuleSetAuditDao() {
        return ruleSetAuditDao;
    }

    public void setRuleSetAuditDao(RuleSetAuditDao ruleSetAuditDao) {
        this.ruleSetAuditDao = ruleSetAuditDao;
    }

    public JavaMailSenderImpl getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

}
