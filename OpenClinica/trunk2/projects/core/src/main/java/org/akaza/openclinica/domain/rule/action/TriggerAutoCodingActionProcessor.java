package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.core.OCSpringApplicationContext;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner.RuleRunnerMode;
import org.akaza.openclinica.service.coding.CodingService;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class TriggerAutoCodingActionProcessor implements ActionProcessor {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    DataSource ds;
    DynamicsMetadataService itemMetadataService;
    RuleActionRunLogDao ruleActionRunLogDao;
    RuleSetBean ruleSet;
    RuleSetRuleBean ruleSetRule;

    public TriggerAutoCodingActionProcessor(DataSource ds, DynamicsMetadataService itemMetadataService, RuleActionRunLogDao ruleActionRunLogDao, RuleSetBean ruleSet,
            RuleSetRuleBean ruleSetRule) {
        this.itemMetadataService = itemMetadataService;
        this.ruleSet = ruleSet;
        this.ruleSetRule = ruleSetRule;
        this.ruleActionRunLogDao = ruleActionRunLogDao;
        this.ds = ds;
    }

    public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode, RuleActionBean ruleAction, ItemDataBean itemDataBean,
            String itemData, StudyBean currentStudy, UserAccountBean ub, Object... arguments) {
    	
    	StdScheduler scheduler = (StdScheduler) OCSpringApplicationContext.getBean("schedulerFactoryBean");

        switch (executionMode) {
        case DRY_RUN: {
            if (ruleRunnerMode == RuleRunnerMode.DATA_ENTRY) {
                return null;
            } else {
                dryRun(ruleAction, itemDataBean, itemData, currentStudy, ub);
            }
        }
        case SAVE: {        	
//            if (ruleRunnerMode == RuleRunnerMode.DATA_ENTRY) {
                //Schedule the auto-coding scheduled job
            	logger.debug("executing the TriggerAutoCodingAction in save mode for data entry");
            	//pass on code item name, study bean, crf version id, verbatim term value to schedule the job
//            	CodingService codingService = new CodingService();
            	CodingService codingService = (CodingService) OCSpringApplicationContext.getBean("codingService");
            	codingService.scheduleAutoCodingJob(scheduler, ruleAction,/* itemData,*/ currentStudy, ub, itemDataBean);
            	
//            } else {

//            }
            
            RuleActionRunLogBean ruleActionRunLog =
                new RuleActionRunLogBean(ruleAction.getActionType(), itemDataBean, itemDataBean.getValue(), ruleSetRule.getRuleBean().getOid());
            if (ruleActionRunLogDao.findCountByRuleActionRunLogBean(ruleActionRunLog) > 0) {
            } else {
                ruleActionRunLogDao.saveOrUpdate(ruleActionRunLog);
            }
        }
        default:
            return null;
        }
    }
    
    /**
     */
    private void schduleAutoCoding(){
    	
    }

    private RuleActionBean dryRun(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy, UserAccountBean ub) {
        return ruleAction;
    }

    private DynamicsMetadataService getItemMetadataService() {
        return itemMetadataService;
    }

}
