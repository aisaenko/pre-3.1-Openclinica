package org.akaza.openclinica.dao.rule;

import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;

import java.util.List;

public class RuleSetRuleDaoTest extends HibernateOcDbTestCase {

    public RuleSetRuleDaoTest() {
        super();
    }

    public void testFindById() {
        RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) getContext().getBean("ruleSetRuleDao");
        RuleSetRuleBean ruleSetRuleBean = null;
        ruleSetRuleBean = ruleSetRuleDao.findById(3);

        // Test RuleSetRule
        assertNotNull("RuleSet is null", ruleSetRuleBean);
        assertEquals("The id of the retrieved RuleSet should be 1", new Integer(3), ruleSetRuleBean.getId());

    }

    public void testFindByIdEmptyResultSet() {
        RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) getContext().getBean("ruleSetRuleDao");

        RuleSetRuleBean ruleSetRuleBean = null;
        ruleSetRuleBean = ruleSetRuleDao.findById(6);

        // Test Rule
        assertNull("RuleSet is null", ruleSetRuleBean);
    }

    public void testFindByRuleSetBeanAndRuleBean() {
        RuleDao ruleDao = (RuleDao) getContext().getBean("ruleDao");
        RuleSetDao ruleSetDao = (RuleSetDao) getContext().getBean("ruleSetDao");
        RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) getContext().getBean("ruleSetRuleDao");
        RuleBean persistentRuleBean = ruleDao.findById(-1);
        RuleSetBean persistentRuleSetBean = ruleSetDao.findById(-1);
        List<RuleSetRuleBean> ruleSetRules = ruleSetRuleDao.findByRuleSetBeanAndRuleBean(persistentRuleSetBean, persistentRuleBean);

        assertNotNull("RuleSetRules is null", ruleSetRules);
        assertEquals("The size of RuleSetRules should be 1", new Integer(1), new Integer(ruleSetRules.size()));
    }
}