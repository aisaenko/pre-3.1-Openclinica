package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.OcDbTestCase;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.rule.RuleSetRuleBean;
import org.akaza.openclinica.bean.rule.RulesPostImportContainer;
import org.akaza.openclinica.bean.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.bean.rule.expression.Context;
import org.akaza.openclinica.bean.rule.expression.ExpressionBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;

import java.util.ArrayList;

public class RulesPostImportContainerServiceTest extends OcDbTestCase {

    public RulesPostImportContainerServiceTest() {
        super();
    }

    public void testDuplicationRuleSetDefs() {
        StudyDAO studyDao = new StudyDAO(getDataSource());
        StudyBean study = (StudyBean) studyDao.findByPK(1);
        RulesPostImportContainerService postImportContainerService = new RulesPostImportContainerService(getDataSource(), study);
        RulesPostImportContainer container = prepareContainer();

        container = postImportContainerService.validateRuleDefs(container);

        assertEquals(0, container.getDuplicateRuleDefs().size());
        assertEquals(0, container.getInValidRuleDefs().size());
        assertEquals(1, container.getValidRuleDefs().size());

        container = postImportContainerService.validateRuleSetDefs(container);
        assertEquals(1, container.getDuplicateRuleSetDefs().size());
        assertEquals(0, container.getInValidRuleSetDefs().size());
        assertEquals(0, container.getValidRuleSetDefs().size());
    }

    private RulesPostImportContainer prepareContainer() {
        RulesPostImportContainer container = new RulesPostImportContainer();
        ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
        ArrayList<RuleBean> ruleDefs = new ArrayList<RuleBean>();

        RuleBean rule = createRuleBean();
        RuleSetBean ruleSet = getRuleSet(rule.getOid());
        ruleSets.add(ruleSet);
        ruleDefs.add(rule);
        container.setRuleSets(ruleSets);
        container.setRuleDefs(ruleDefs);
        return container;

    }

    private RuleSetBean getRuleSet(String ruleOid) {
        RuleSetBean ruleSet = new RuleSetBean();
        ruleSet.setTarget(createExpression(Context.OC_RULES_V1, "SE_ED2REPEA.F_CONC_V20.IG_CONC_CONCOMITANTMEDICATIONS.I_CONC_CON_MED_NAME"));
        RuleSetRuleBean ruleSetRule = createRuleSetRule(ruleSet, ruleOid);
        ruleSet.addRuleSetRule(ruleSetRule);
        return ruleSet;

    }

    private RuleSetRuleBean createRuleSetRule(RuleSetBean ruleSet, String ruleOid) {
        RuleSetRuleBean ruleSetRule = new RuleSetRuleBean();
        DiscrepancyNoteActionBean ruleAction = new DiscrepancyNoteActionBean();
        ruleAction.setMessage("HELLO WORLD");
        ruleAction.setExpressionEvaluatesTo(true);
        ruleSetRule.addAction(ruleAction);
        ruleSetRule.setRuleSetBean(ruleSet);
        ruleSetRule.setOid(ruleOid);

        return ruleSetRule;
    }

    private RuleBean createRuleBean() {
        RuleBean ruleBean = new RuleBean();
        ruleBean.setName("TEST");
        ruleBean.setOid("BOY");
        ruleBean.setDescription("Yellow");
        ruleBean.setExpression(createExpression(Context.OC_RULES_V1,
                "SE_ED1NONRE.F_AGEN.IG_AGEN_UNGROUPED[1].I_AGEN_PERIODSTART eq \"07/01/2008\" and I_CONC_CON_MED_NAME eq \"Tylenol\""));
        return ruleBean;
    }

    private ExpressionBean createExpression(Context context, String value) {
        ExpressionBean expression = new ExpressionBean();
        expression.setContext(context);
        expression.setValue(value);
        return expression;
    }
}