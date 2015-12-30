/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.rule.AuditableBeanWrapper;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;
import org.akaza.openclinica.bean.rule.RulesPostImportContainer;
import org.akaza.openclinica.bean.rule.expression.Context;
import org.akaza.openclinica.bean.rule.expression.ExpressionBean;
import org.akaza.openclinica.bean.rule.expression.ExpressionProcessor;
import org.akaza.openclinica.bean.rule.expression.ExpressionProcessorFactory;
import org.akaza.openclinica.dao.rule.RuleDAO;
import org.akaza.openclinica.dao.rule.RuleSetDAO;

import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * @author Krikor Krumlian
 * 
 */
public class RulesPostImportContainerService {

    protected final Logger logger = Logger.getLogger(getClass().getName());
    DataSource ds;
    private RuleDAO ruleDao;
    private RuleSetDAO ruleSetDao;
    private OidGenerator oidGenerator;
    ExpressionProcessor ep;

    private final String EXPRESSION_INVALID = "The expression is inValid";

    public RulesPostImportContainerService(DataSource ds) {
        oidGenerator = new GenericOidGenerator();
        this.ds = ds;
    }

    public RulesPostImportContainer validateRuleSetDefs(RulesPostImportContainer importContainer) {
        for (RuleSetBean ruleSetBean : importContainer.getRuleSets()) {
            AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper = new AuditableBeanWrapper<RuleSetBean>(ruleSetBean);
            if (isRuleSetExpressionValid(ruleSetBeanWrapper)) {
                RuleSetBean persistentRuleSetBean = getRuleSetDao().findByExpression(ruleSetBean);

                if (persistentRuleSetBean != null) {
                    ruleSetBeanWrapper.getAuditableBean().setId(persistentRuleSetBean.getId());
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

            if (isRuleDefValid(ruleBeanWrapper) /*
                                                 * &&
                                                 * isRuleExpressionValid(ruleBeanWrapper)
                                                 */) {
                RuleBean persistentRuleBean = getRuleDao().findByOid(ruleBeanWrapper.getAuditableBean());
                if (persistentRuleBean != null) {
                    ruleBeanWrapper.getAuditableBean().setId(persistentRuleBean.getId());
                    ruleBeanWrapper.getAuditableBean().getExpression().setId(persistentRuleBean.getExpression().getId());
                }
            }
            putRuleInCorrectContainer(ruleBeanWrapper, importContainer);
        }
        logger.info("# of Valid RuleDefs : " + importContainer.getValidRuleDefs().size());
        logger.info("# of InValid RuleDefs : " + importContainer.getInValidRuleDefs().size());
        logger.info("# of Overwritable RuleDefs : " + importContainer.getDuplicateRuleDefs().size());
        return importContainer;
    }

    private void putRuleSetInCorrectContainer(AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper, RulesPostImportContainer importContainer) {
        if (!ruleSetBeanWrapper.isSavable()) {
            importContainer.getInValidRuleSetDefs().add(ruleSetBeanWrapper);
        } else if (ruleSetBeanWrapper.getAuditableBean().getId() == 0) {
            importContainer.getValidRuleSetDefs().add(ruleSetBeanWrapper);
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
        }
    }

    private boolean isRuleExpressionValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper) {
        return isExpressionValid(ruleBeanWrapper.getAuditableBean().getExpression(), ruleBeanWrapper);
    }

    private boolean isRuleSetExpressionValid(AuditableBeanWrapper<RuleSetBean> beanWrapper) {
        return isExpressionValid(beanWrapper.getAuditableBean().getTarget(), beanWrapper);
    }

    private boolean isExpressionValid(ExpressionBean expressionBean, AuditableBeanWrapper<?> beanWrapper) {
        boolean isValid = true;

        if (expressionBean.getContextName() == null && expressionBean.getContext() == null) {
            expressionBean.setContext(Context.OC_RULES_V1);
        }
        if (expressionBean.getContextName() != null && expressionBean.getContext() == null) {
            beanWrapper.warning("The Context you selected is not support we will use the default one");
            expressionBean.setContext(Context.OC_RULES_V1);
        }

        ep = ExpressionProcessorFactory.createExpressionProcessor(expressionBean);
        if (!ep.process()) {
            beanWrapper.error(EXPRESSION_INVALID);
            isValid = false;
        }

        return isValid;
    }

    public void validateRuleImport(RulesPostImportContainer rulesContainer) {

        for (RuleBean rule : rulesContainer.getRuleDefs()) {
            ep = ExpressionProcessorFactory.createExpressionProcessor(rule.getExpression());
            ep.process(); // Do Processing here
        }
    }

    private boolean isRuleDefValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper) {
        boolean isValid = true;
        try {
            oidGenerator.validate(ruleBeanWrapper.getAuditableBean().getOid());
        } catch (Exception e) {
            ruleBeanWrapper.error("Oid is not Valid");
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

}
