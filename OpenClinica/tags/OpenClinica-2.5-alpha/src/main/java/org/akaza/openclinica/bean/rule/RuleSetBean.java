/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.rule.expression.ExpressionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * RuleSetBean, the object that collects rules associated with study events.
 * </p>
 * 
 * @author Krikor Krumlian
 */
public class RuleSetBean extends AuditableEntityBean {

    // This is the target
    private String studyEventDefinitionName;
    private StudyEventDefinitionBean studyEventDefinition;

    private List<RuleSetRuleBean> ruleSetRules;
    private ExpressionBean target;

    public void addRuleSetRule(RuleBean ruleBean) {
        if (this.ruleSetRules == null)
            this.ruleSetRules = new ArrayList<RuleSetRuleBean>();
        RuleSetRuleBean ruleSetRuleBean = new RuleSetRuleBean();
        ruleSetRuleBean.setRuleBean(ruleBean);
        ruleSetRuleBean.setRuleSetBean(this);
    }

    public StudyEventDefinitionBean getStudyEventDefinition() {
        return studyEventDefinition;
    }

    public void setStudyEventDefinition(StudyEventDefinitionBean studyEventDefinition) {
        this.studyEventDefinition = studyEventDefinition;
    }

    public String getStudyEventDefinitionName() {
        return studyEventDefinitionName;
    }

    public void setStudyEventDefinitionName(String studyEventDefinitionName) {
        this.studyEventDefinitionName = studyEventDefinitionName;
    }

    public List<RuleSetRuleBean> getRuleSetRules() {
        return ruleSetRules;
    }

    public void setRuleSetRules(List<RuleSetRuleBean> ruleSetRuleAssignment) {
        this.ruleSetRules = ruleSetRuleAssignment;
    }

    public ExpressionBean getTarget() {
        return target;
    }

    public void setTarget(ExpressionBean target) {
        this.target = target;
    }
}
