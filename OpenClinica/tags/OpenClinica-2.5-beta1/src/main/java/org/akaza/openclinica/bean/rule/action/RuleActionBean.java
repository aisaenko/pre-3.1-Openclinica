/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule.action;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;

/**
 * @author Krikor Krumlian
 */
public class RuleActionBean extends AuditableEntityBean {

    private RuleSetRuleBean ruleSetRule;
    private ActionType actionType; // Type of Action should be Enum ,
    // fileDiscrepancy , email , etc.
    private Boolean expressionEvaluatesTo;
    private String summary;

    /**
     * @return the summary
     */
    public String getSummary() {
        return "krikor";
    }

    /**
     * @param summary
     *            the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /*
     * // FUTURE features private String triggerType; // Was it bulk executed or per data entry ?? private String executionType; // Allow execution by Bulk or
     * per data entry ??
     */
    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Boolean getExpressionEvaluatesTo() {
        return expressionEvaluatesTo;
    }

    public void setExpressionEvaluatesTo(Boolean ifExpressionEvaluates) {
        this.expressionEvaluatesTo = ifExpressionEvaluates;
    }

    public RuleSetRuleBean getRuleSetRule() {
        return ruleSetRule;
    }

    public void setRuleSetRule(RuleSetRuleBean ruleSetRule) {
        this.ruleSetRule = ruleSetRule;
    }

}