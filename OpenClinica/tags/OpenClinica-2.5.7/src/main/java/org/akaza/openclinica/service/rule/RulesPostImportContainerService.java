/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.rule.AuditableBeanWrapper;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;
import org.akaza.openclinica.bean.rule.RulesPostImportContainer;
import org.akaza.openclinica.bean.rule.expression.Context;
import org.akaza.openclinica.bean.rule.expression.ExpressionBean;
import org.akaza.openclinica.bean.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.bean.rule.expression.ExpressionProcessor;
import org.akaza.openclinica.bean.rule.expression.ExpressionProcessorFactory;
import org.akaza.openclinica.dao.rule.RuleDAO;
import org.akaza.openclinica.dao.rule.RuleSetDAO;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * @author Krikor Krumlian
 * 
 */
public class RulesPostImportContainerService {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    DataSource ds;
    private RuleDAO ruleDao;
    private RuleSetDAO ruleSetDao;
    private OidGenerator oidGenerator;
    private StudyBean currentStudy;
    private ExpressionService expressionService;

    public RulesPostImportContainerService(DataSource ds, StudyBean currentStudy) {
        oidGenerator = new GenericOidGenerator();
        this.ds = ds;
        this.currentStudy = currentStudy;
    }

    public RulesPostImportContainer validateRuleSetDefs(RulesPostImportContainer importContainer) {
        for (RuleSetBean ruleSetBean : importContainer.getRuleSets()) {
            AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper = new AuditableBeanWrapper<RuleSetBean>(ruleSetBean);
            ruleSetBeanWrapper.getAuditableBean().setStudy(currentStudy);
            if (isRuleSetExpressionValid(ruleSetBeanWrapper)) {
                RuleSetBean persistentRuleSetBean = getRuleSetDao().findByExpression(ruleSetBean);

                if (persistentRuleSetBean != null) {
                    ruleSetBeanWrapper.getAuditableBean().setId(persistentRuleSetBean.getId());
                } else {
                    if (importContainer.getValidRuleSetExpressionValues().contains(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue())) {
                        ruleSetBeanWrapper.error("You have two rule assignments with exact same Target, Combine and try again");
                    }
                    ruleSetBeanWrapper.getAuditableBean().setStudyEventDefinition(
                            getExpressionService().getStudyEventDefinitionFromExpression(ruleSetBean.getTarget().getValue()));
                    ruleSetBeanWrapper.getAuditableBean().setCrf(getExpressionService().getCRFFromExpression(ruleSetBean.getTarget().getValue()));
                    ruleSetBeanWrapper.getAuditableBean().setCrfVersion(getExpressionService().getCRFVersionFromExpression(ruleSetBean.getTarget().getValue()));
                }
                isRuleSetRuleValid(importContainer, ruleSetBeanWrapper);
            }
            putRuleSetInCorrectContainer(ruleSetBeanWrapper, importContainer);
        }
        logger.info("# of Valid RuleSetDefs : " + importContainer.getValidRuleSetDefs().size());
        logger.info("# of InValid RuleSetDefs : " + importContainer.getInValidRuleSetDefs().size());
        logger.info("# of Overwritable RuleSetDefs : " + importContainer.getDuplicateRuleSetDefs().size());
        return importContainer;
    }

    public RulesPostImportContainer validateRuleDefs(RulesPostImportContainer importContainer) {
        for (RuleBean ruleBean : importContainer.getRuleDefs()) {
            AuditableBeanWrapper<RuleBean> ruleBeanWrapper = new AuditableBeanWrapper<RuleBean>(ruleBean);

            if (isRuleOidValid(ruleBeanWrapper) && isRuleExpressionValid(ruleBeanWrapper, null)) {
                RuleBean persistentRuleBean = getRuleDao().findByOid(ruleBeanWrapper.getAuditableBean());
                if (persistentRuleBean != null) {
                    ruleBeanWrapper.getAuditableBean().setId(persistentRuleBean.getId());
                    ruleBeanWrapper.getAuditableBean().getExpression().setId(persistentRuleBean.getExpression().getId());
                }
            }
            putRuleInCorrectContainer(ruleBeanWrapper, importContainer);
        }
        logger.info("# of Valid RuleDefs : {} , # of InValid RuleDefs : {} , # of Overwritable RuleDefs : {}", new Object[] {
            importContainer.getValidRuleDefs().size(), importContainer.getInValidRuleDefs().size(), importContainer.getDuplicateRuleDefs().size() });
        return importContainer;
    }

    private void putRuleSetInCorrectContainer(AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper, RulesPostImportContainer importContainer) {
        if (!ruleSetBeanWrapper.isSavable()) {
            importContainer.getInValidRuleSetDefs().add(ruleSetBeanWrapper);
        } else if (ruleSetBeanWrapper.getAuditableBean().getId() == 0) {
            importContainer.getValidRuleSetDefs().add(ruleSetBeanWrapper);
            importContainer.getValidRuleSetExpressionValues().add(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue());
        } else if (ruleSetBeanWrapper.getAuditableBean().getId() != 0) {
            importContainer.getDuplicateRuleSetDefs().add(ruleSetBeanWrapper);
        }
    }

    private void putRuleInCorrectContainer(AuditableBeanWrapper<RuleBean> ruleBeanWrapper, RulesPostImportContainer importContainer) {
        if (!ruleBeanWrapper.isSavable()) {
            importContainer.getInValidRuleDefs().add(ruleBeanWrapper);
            importContainer.getInValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
        } else if (ruleBeanWrapper.getAuditableBean().getId() == 0) {
            importContainer.getValidRuleDefs().add(ruleBeanWrapper);
            importContainer.getValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
        } else if (ruleBeanWrapper.getAuditableBean().getId() != 0) {
            importContainer.getDuplicateRuleDefs().add(ruleBeanWrapper);
            importContainer.getValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
        }
    }

    /**
     * If the RuleSet contains any RuleSetRule object with an invalid RuleRef
     * OID (OID that is not in DB or in the Valid Rule Lists) , Then add an
     * error to the ruleSetBeanWrapper, which in terms will make the RuleSet
     * inValid.
     * 
     * @param importContainer
     * @param ruleSetBeanWrapper
     */
    private void isRuleSetRuleValid(RulesPostImportContainer importContainer, AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper) {
        for (RuleSetRuleBean ruleSetRuleBean : ruleSetBeanWrapper.getAuditableBean().getRuleSetRules()) {
            String ruleDefOid = ruleSetRuleBean.getOid();

            if (importContainer.getInValidRules().get(ruleDefOid) != null || importContainer.getValidRules().get(ruleDefOid) == null
                && getRuleDao().findByOid(ruleDefOid) == null) {
                ruleSetBeanWrapper.error("The Rule you are trying to reference does not exist or is Invalid");
            }
            if (importContainer.getValidRules().get(ruleDefOid) != null) {
                AuditableBeanWrapper<RuleBean> r = importContainer.getValidRules().get(ruleDefOid);
                if (!isRuleExpressionValid(r, ruleSetBeanWrapper.getAuditableBean()))
                {
                    removeInvalidRule(r, importContainer,ruleDefOid);
                    ruleSetBeanWrapper
                            .error("The Contextual expression in one of the Rules does not validate against the Target expression in the Current RuleSet");
                }
            }
        }
    }
    
    private void removeInvalidRule(AuditableBeanWrapper<RuleBean> r,RulesPostImportContainer importContainer, String ruleDefOid){
        if (importContainer.getDuplicateRuleDefs().contains(r)) {
            importContainer.getDuplicateRuleDefs().remove(r);
        }
        if (importContainer.getValidRuleDefs().contains(r)) {
            importContainer.getValidRuleDefs().remove(r);
        }
        importContainer.getValidRules().remove(r);
        importContainer.getInValidRuleDefs().add(r);
        importContainer.getInValidRules().put(ruleDefOid, r);
    }

    private boolean isRuleExpressionValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper, RuleSetBean ruleSet) {
        boolean isValid = true;
        ExpressionBean expressionBean = isExpressionValid(ruleBeanWrapper.getAuditableBean().getExpression(), ruleBeanWrapper);
        ExpressionObjectWrapper eow = new ExpressionObjectWrapper(ds, currentStudy, expressionBean, ruleSet);
        ExpressionProcessor ep = ExpressionProcessorFactory.createExpressionProcessor(eow);
        String errorString = ep.isRuleExpressionValid();
        if (errorString != null) {
            ruleBeanWrapper.error(errorString);
            isValid = false;
        }
        return isValid;
    }

    private boolean isRuleSetExpressionValid(AuditableBeanWrapper<RuleSetBean> beanWrapper) {
        boolean isValid = true;
        ExpressionBean expressionBean = isExpressionValid(beanWrapper.getAuditableBean().getTarget(), beanWrapper);
        ExpressionObjectWrapper eow = new ExpressionObjectWrapper(ds, currentStudy, expressionBean);
        ExpressionProcessor ep = ExpressionProcessorFactory.createExpressionProcessor(eow);
        String errorString = ep.isRuleAssignmentExpressionValid();
        if (errorString != null) {
            beanWrapper.error(errorString);
            isValid = false;
        }
        return isValid;
    }

    private ExpressionBean isExpressionValid(ExpressionBean expressionBean, AuditableBeanWrapper<?> beanWrapper) {

        if (expressionBean.getContextName() == null && expressionBean.getContext() == null) {
            expressionBean.setContext(Context.OC_RULES_V1);
        }
        if (expressionBean.getContextName() != null && expressionBean.getContext() == null) {
            beanWrapper.warning("The Context you selected is not support we will use the default one");
            expressionBean.setContext(Context.OC_RULES_V1);
        }
        return expressionBean;
    }

    private boolean isRuleOidValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper) {
        boolean isValid = true;
        try {
            oidGenerator.validate(ruleBeanWrapper.getAuditableBean().getOid());
        } catch (Exception e) {
            ruleBeanWrapper.error("OID is not Valid, The OID can only be made of A-Z_0-9");
            isValid = false;
        }
        return isValid;
    }

    private RuleDAO getRuleDao() {
        ruleDao = this.ruleDao != null ? ruleDao : new RuleDAO(ds);
        return ruleDao;
    }

    private RuleSetDAO getRuleSetDao() {
        ruleSetDao = this.ruleSetDao != null ? ruleSetDao : new RuleSetDAO(ds);
        return ruleSetDao;
    }

    private ExpressionService getExpressionService() {
        expressionService =
            this.expressionService != null ? expressionService : new ExpressionService(new ExpressionObjectWrapper(ds, currentStudy, null, null));
        return expressionService;
    }
}
