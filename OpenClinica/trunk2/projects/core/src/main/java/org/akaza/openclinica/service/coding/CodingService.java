package org.akaza.openclinica.service.coding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.coding.CodeItemData;
import org.akaza.openclinica.domain.EntityType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.dao.core.OCSpringApplicationContext;
import org.akaza.openclinica.dao.hibernate.CodingDictionaryDAO;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.ThesaurusDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.coding.CodingDictionaryBean;
import org.akaza.openclinica.domain.coding.ThesaurusBean;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean;
import org.akaza.openclinica.domain.rule.action.TriggerAutoCodingActionBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.job.CodingJob;
import org.akaza.openclinica.logic.score.ScoreValidator;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdScheduler;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.transaction.annotation.Transactional;


public class CodingService {
	private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());
	
	DataSource dataSource;
    CodingDictionaryDAO codingDictionaryDAO;
    ThesaurusDAO thesaurusDAO;
    RuleSetServiceInterface ruleSetService;
    RulesPostImportContainerService rulesPostImportContainerService;
//    private StdScheduler scheduler;
    
    public static String TRIGGER_GROUP_NAME = "CodingTriggers";
    
    public CodingService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public CodingService() {        
    }
	
	public ArrayList<CodingDictionaryBean> getAllSupportedDict(){
		return codingDictionaryDAO.findAll();
	}

//	public DataSource getDataSource() {
//		return dataSource;
//	}
//
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}

	
//	public void setCodingDictionaryDAO(CodingDictionaryDAO codingDictionaryDAO) {
//		this.codingDictionaryDAO = codingDictionaryDAO;
//	}
	
//	 private CodingDictionaryDAO getCodingDictionaryDAO(ServletContext context) {
//		 codingDictionaryDAO =
//	            this.codingDictionaryDAO != null ? codingDictionaryDAO : (CodingDictionaryDAO) SpringServletAccess.getApplicationContext(context).getBean(
//	                    "usageStatsServiceDAO");
//	        return codingDictionaryDAO;
//	    }
	
	
	 
	/**
	 * Validate coding expression to check following things:
	 * 1. valid start of expression
	 * 2. valid end of expression
	 * 3. Valid name of the coding function ("coding")
	 * 4. GROUP_LABEL specified for coding item matches that with the associated verbatim term
	 * 5. Name of the item used as verbatim term appears on one of the rows of spreadsheet before the coding item itself 
	 * 6. dictionary name specified matches with one of the supported coding dictionaries by OpenClinica
	 * @param isRepeating
	 * @param resValues
	 * @param errors
	 * @param htmlErrors
	 * @param resPageMsg
	 * @param itemName
	 * @param j
	 * @param k
	 * @param groupLabel
	 * @param locale
	 * @param allItems
	 */
	public void validateCodingExpression(boolean isRepeating, String resValues, ArrayList errors, HashMap htmlErrors, ResourceBundle resPageMsg, String itemName, 
			 Integer sheetNum, Integer rowNum, String groupLabel, Locale locale, HashMap<String, String> allItems){
		//check if expression starts with "func:" or "func: "
		if(!resValues.startsWith("func:") && !resValues.startsWith("func :")){
			errors.add(resPageMsg.getString("coding_expression_not_start_with_func_colon_at") + " " + rowNum + ", "
                    + resPageMsg.getString("items_worksheet") + ".");
        	if(isRepeating){                
            htmlErrors.put(sheetNum + "," + rowNum + ",16", resPageMsg.getString("INVALID_FIELD"));
        	}
        	else{
                 htmlErrors.put(sheetNum + "," + rowNum + ",15", resPageMsg.getString("INVALID_FIELD"));
        	}
		}
//		if (resValues.contains(":")) {
//            String[] s = resValues.split(":");
//            if (!"func".equalsIgnoreCase(s[0].trim())) {
//            	errors.add(resPageMsg.getString("expression_not_start_with_func_at") + " " + rowNum + ", "
//                        + resPageMsg.getString("items_worksheet") + ".");
//            	if(isRepeating){                
//                htmlErrors.put(sheetNum + "," + rowNum + ",16", resPageMsg.getString("INVALID_FIELD"));
//            	}
//            	else{
//                     htmlErrors.put(sheetNum + "," + rowNum + ",15", resPageMsg.getString("INVALID_FIELD"));
//            	}
//            }
//        }
		//validate 
        String exp = resValues;
        // make both \\, and , works for functions
        exp = exp.replace("\\\\,", "##");
        exp = exp.replace("##", ",");
        exp = exp.replace(",", "\\\\,");
        resValues = exp;
        if (exp.startsWith("func:")) {
            exp = exp.substring(5).trim();
        }
        exp = exp.replace("\\\\,", "##");
        StringBuffer err = new StringBuffer();
        ArrayList<String> variables = new ArrayList<String>();
        ScoreValidator scoreValidator = new ScoreValidator(locale);
        if (!scoreValidator.isValidCodingExpression(exp, err, variables)) {
        	errors.add(resPageMsg.getString("expression_invalid_at") + " " + rowNum + ", " + resPageMsg.getString("items_worksheet") + ": "
	                + err);
        	if(isRepeating){
	            htmlErrors.put(sheetNum + "," + rowNum + ",16", resPageMsg.getString("INVALID_FIELD"));
        	}
        	else{
        		htmlErrors.put(sheetNum + "," + rowNum + ",15", resPageMsg.getString("INVALID_FIELD"));
        	}
        }
//        if (exp.startsWith("getexternalvalue") || exp.startsWith("getExternalValue")) {
//            // do a different set of validations here, tbh
//        } 
//        else {
        
        
            String group = isRepeating ? (groupLabel.length() > 0 ? groupLabel : "Ungrouped") : "Ungrouped";
            //separate out the last 2 parameter to the function to get list of item name
            // parameters to validate if they are part of the CRF
            ArrayList<String> itemVariables = new ArrayList<String>();
            if((null != variables) && (variables.size() > 0)){
            	for (int i=0; i<variables.size()-2; i++){
            		itemVariables.add(variables.get(i));
            	}
            }
            for (String v : itemVariables) {
                if (!allItems.containsKey(v)) {
                	errors.add("Item '" + v + "' must be listed before the item '" + itemName + "' at row " + rowNum + ", items worksheet. ");
                	if(isRepeating){
	                    htmlErrors.put(sheetNum + "," + rowNum + ",16", "INVALID FIELD");
                	}
                	else{                		
	                    htmlErrors.put(sheetNum + "," + rowNum + ",15", "INVALID FIELD");
                	}
                } else {
                	if(!allItems.get(v).equalsIgnoreCase(group)){
//                    if (responseTypeId == 8 && !allItems.get(v).equalsIgnoreCase(group)) {
                	errors.add("Item '" + v + "' and item '" + itemName + "' must have a same GROUP_LABEL at row " + rowNum
                            + ", items worksheet. ");
                	if(isRepeating){                        
                        htmlErrors.put(sheetNum + "," + rowNum + ",16", "INVALID FIELD");
                	}
                	else{
                		 htmlErrors.put(sheetNum + "," + rowNum + ",15", "INVALID FIELD");
                	}
//                    } 
//                    else if (responseTypeId == 9) {
//                        String g = allItems.get(v);
//                        if (!g.equalsIgnoreCase("ungrouped") && g.equalsIgnoreCase(group)) {
//                            errors.add("Item '" + v + "' and item '" + itemName + "' should not have a same GROUP_LABEL at row " + rowNum
//                                + ", items worksheet. ");
//                            htmlErrors.put(sheetNum + "," + rowNum + ",16", "INVALID FIELD");
//                        }
//                    }
                	}
                }
            }
//        }
          //Check the coding dictionary specified is one of the valid dictionaries supported
            if(!validateDictionary(variables)){
            	errors.add(resPageMsg.getString("coding_invalid_dict") + " " + rowNum + ", " + resPageMsg.getString("items_worksheet") + ": "
                        + err);
            	if(isRepeating){
                    htmlErrors.put(sheetNum + "," + rowNum + ",16", resPageMsg.getString("INVALID_FIELD"));
            	}
            	else{
            		htmlErrors.put(sheetNum + "," + rowNum + ",15", resPageMsg.getString("INVALID_FIELD"));
            	}
            }
            //validate auto coding option
            if(!validateAutoCdoingOption(variables)){
            	errors.add(resPageMsg.getString("coding_invalid_autocoding_option") + " " + rowNum + ", " + resPageMsg.getString("items_worksheet") + ": "
                        + err);
            	if(isRepeating){
                    htmlErrors.put(sheetNum + "," + rowNum + ",16", resPageMsg.getString("INVALID_FIELD"));
            	}
            	else{
            		htmlErrors.put(sheetNum + "," + rowNum + ",15", resPageMsg.getString("INVALID_FIELD"));
            	}
            }
	}
	
	
	public boolean validateDictionary(ArrayList<String> variables ){
		boolean isDictValid = false;
		
		ArrayList<CodingDictionaryBean> dictList = getAllSupportedDict();
		CodingDictionaryBean dictBean = null;
		String dictionaryName = null;
		
		if( (variables != null) && (variables.size() >= 2)){
			dictionaryName = variables.get(variables.size()-2);
		}
		
		if (dictionaryName != null){
			//remove single quotes around the dictinary name
			if(dictionaryName.contains("'")){
				dictionaryName = dictionaryName.replaceAll("'", "");
			}
			for (int i=0; i<dictList.size(); i++){
				dictBean = dictList.get(i);
				if( (dictBean != null) && (dictBean.getName() != null) 
						&& (dictBean.getName().trim().equalsIgnoreCase(dictionaryName))){
					isDictValid = true;
					break;
				}
			}
		}
		
		return isDictValid;
	}
	public boolean validateAutoCdoingOption(ArrayList<String> variables ){
		boolean isAutoCodingOptValid = false;
		
		ArrayList<CodingDictionaryBean> dictList = getAllSupportedDict();
		CodingDictionaryBean dictBean = null;
		String autoCodingOpt = null;
		
		if( (variables != null) && (variables.size() >= 2)){
			autoCodingOpt = variables.get(variables.size()-1);
		}
		
		if (autoCodingOpt != null){
			autoCodingOpt = autoCodingOpt.trim();
			if((autoCodingOpt.equalsIgnoreCase("y")) || (autoCodingOpt.equalsIgnoreCase("n"))){
				isAutoCodingOptValid = true;
			}
			
		}
		
		return isAutoCodingOptValid;
	}

	/**
	 * Auto coding is applicable to data item if - 
        1. response type = "hidden-code"
        2. if opted for auto-coding when uploaded the CRF
	 * @param idb
	 * @return
	 */
	public boolean isAutoCodingApplicableForItem(ItemDataBean idb){
		boolean isAutoCodingApplicable = false;
		String codingFunc = null;
		if(null != idb){
			ItemDataDAO iddao = new ItemDataDAO(getDataSource());
			ItemBean itemBean = iddao.getItem(idb.getItemId());
			//check if response type is "hidden-code"
			if( (null != itemBean) && (null != itemBean.getItemMeta())){
				ItemFormMetadataBean itemMetadata = itemBean.getItemMeta();
				if((null != itemMetadata) && (null != itemMetadata.getResponseSet()) && (null != itemMetadata.getResponseSet().getResponseType())){
					if(itemMetadata.getResponseSet().getResponseType().getId() != ResponseType.MEDICAL_CODE.getId()){
						return false; 
					}
					else{
						ArrayList options = itemMetadata.getResponseSet().getOptions();
						if((null != options) && (options.size() != 0)){
							codingFunc = (String)options.get(0);
							//check if auto-coding was skipped during CRF upload
							if(isAutoCodingSkipped(codingFunc)){
								return false; 
							}
							else{
								isAutoCodingApplicable = true;
							}
						}						
					}
				}				
			}
		}		
		return isAutoCodingApplicable;
	}

	public boolean isAutoCodingSkipped(String codingFuncValue){
		boolean isAutoCodingSkipped = false;
		if(null != codingFuncValue){
			char contents[] = codingFuncValue.toCharArray();
			if(contents.length != 0){
				char autoCodingOpt = contents[contents.length - 2];
				if(autoCodingOpt == 'N' || autoCodingOpt == 'n'){//'Y'/'y'
					isAutoCodingSkipped = true;
				}
			}
		}
		
		return isAutoCodingSkipped;
	}
	
	
	public String getDictNameFromItemRespValues(String itemRespValues){
		String dictionary = null;
		if(null != itemRespValues){
			String[] tokens = itemRespValues.split(",");
		     dictionary = tokens[1];
		     if(null != dictionary){
		    	 dictionary = dictionary.trim();
		    	 if(dictionary.contains("'")){
		    		 dictionary = dictionary.replaceAll("'", "");
		 		}
		     }

		}
		return dictionary;
	}
	
	public String getItemNameFromRespValues(String itemRespValues){
		String itemNameStr = null;
		String itemName = null;
		if(null != itemRespValues){
			String[] tokens = itemRespValues.split(",");
			itemNameStr = tokens[0];
		     if(null != itemNameStr){
		    	 itemNameStr = itemNameStr.trim();
		    	 if(itemNameStr.contains("(")){
		    		 String[] tokens2 = itemNameStr.split("\\(");
		    		 if(tokens2.length != 0){
		    			 itemName = tokens2[2];
		    		 }
		    	 }		    	
		     }

		}
		itemNameStr = null;		
		return itemName;
	}
	
	public String getDictVersionFromStudyCodingConfig(/*String itemRespValues*/){
		String dictVersion = null;
		
		//ToDo hardcoded temporarily
		dictVersion = "1.3";
		return dictVersion;
	}
	@Transactional
	public String getCodeFromThesaurus(String verbatimTerm, int studyId, int dictId/*, String dictVersion*/){
		String code = null;
		ArrayList<ThesaurusBean> codeLstFromThesaurus = thesaurusDAO.getCodeFromThesaurus(verbatimTerm, studyId, dictId/*, dictVersion*/);
		if((null != codeLstFromThesaurus) && (codeLstFromThesaurus.size() != 0)){
			//there should be only one code for the verbtim term in thesaurus (for the specified dictionary and it's version)
			//Do we need to check this and throw the business exception here?
			if(null != codeLstFromThesaurus.get(0)){
				code = codeLstFromThesaurus.get(0).getCode();
			}
		}
		
		return code;
	}
	
//	public CodingDictionaryBean getDictionaryForDictNameAndVersion(String dictionaryName, String dictionaryVersion){
//		CodingDictionaryBean dict = null;
//		if((null != dictionaryName) && (null != dictionaryVersion)){
//			ArrayList<CodingDictionaryBean> dictionaryList = codingDictionaryDAO.getDictionaryForNameAndVersion(dictionaryName, dictionaryVersion);
//			if((null != dictionaryList) && (dictionaryList.size() != 0)){
//				dict = dictionaryList.get(0);			
//			}
//		}
//		return dict;
//	}
//	@Transactional
//	public CodingDictionaryBean getDictionaryForId(int dictionaryId){
//		CodingDictionaryBean dict = null;
////		CodingDictionaryDAO codingDictionaryDAO = getCodingDictionaryDAO();
//		dict = codingDictionaryDAO.findById(dictionaryId);
//		return dict;
//	}
	
	@Transactional
	public CodingDictionaryBean getDictionaryForOID(String dictionaryOID){
		CodingDictionaryBean dict = null;
//		CodingDictionaryDAO codingDictionaryDAO = getCodingDictionaryDAO();
		dict = codingDictionaryDAO.findByOID(dictionaryOID);
		return dict;
	}
	
//	public String getOntologyIdForDictNameAndVersion(String dictionaryName, String dictionaryVersion){
//		String ontologyId = null;
//		if((null != dictionaryName) && (null != dictionaryVersion)){
//			ArrayList<CodingDictionaryBean> dictionaryList = codingDictionaryDAO.getDictionaryForNameAndVersion(dictionaryName, dictionaryVersion);
//			if((null != dictionaryList) && (dictionaryList.size() != 0)){
//				CodingDictionaryBean dict = dictionaryList.get(0);
//				if(null != dict){
//					ontologyId = dict.getOntology_id();
//				}
//			}
//		}
//		return ontologyId;
//	}
	
	/**
	 * Schedule the quartz job to auto-code item data value
	 * @return
	 */
	public void scheduleAutoCodingJob(StdScheduler scheduler, RuleActionBean ruleAction,/* String itemData, */StudyBean currentStudy, UserAccountBean ub, ItemDataBean itemDataBean){
		JobDetailBean jobDetailBean = new JobDetailBean();
//		int crfVersionId = 0;
		//get the crfVersionId for item
		
		if(null != itemDataBean){
			//ToDo fetch the item object for code_item
//			ItemBean codeItem = null;
			TriggerAutoCodingActionBean autoCodingActionBean = (TriggerAutoCodingActionBean)ruleAction;
	    	List<PropertyBean> properties = autoCodingActionBean.getProperties();
    		String codeItemOID = getCodeItemOIDFromRule(properties);
//    		String dictionaryId = getDictionaryIdFromRule(properties);
    		String dictionaryOID = getDictionaryOIDFromRule(properties);
    		
	        SimpleTrigger simpleTrigger = generateCodingTrigger(/*ruleAction,*/ this.TRIGGER_GROUP_NAME, currentStudy.getId(),/* crfVersionId, itemData,*/ itemDataBean, codeItemOID, /*dictionaryId */ dictionaryOID, ub);//is event oid need to be included in job name?
	        
	        jobDetailBean = new JobDetailBean();
	        jobDetailBean.setGroup(this.TRIGGER_GROUP_NAME);
	        
//	        String simpleDatePattern = "yyyy-MM-dd-HHmmssSSS";
//	        SimpleDateFormat sdf = new SimpleDateFormat(simpleDatePattern);
//	        String endOfJobName =  sdf.format(new java.util.Date());
//	        jobDetailBean.setName(simpleTrigger.getName()+ "_" + endOfJobName);
	        jobDetailBean.setName(simpleTrigger.getName());
	        
	        jobDetailBean.setJobClass(org.akaza.openclinica.job.CodingJob.class);
	        jobDetailBean.setJobDataMap(simpleTrigger.getJobDataMap());
	        jobDetailBean.setDurability(true); // need durability? YES - we will want to see if it's finished
	        jobDetailBean.setVolatility(false);
	        
	        try {
	            Date dateStart = scheduler.scheduleJob(jobDetailBean, simpleTrigger);            
	            logger.debug("coding job date: " + dateStart.toString());
	            
	        } catch (SchedulerException se) {
	            se.printStackTrace();
	        }
		}
        
	}
	
	public SimpleTrigger generateCodingTrigger(/*RuleActionBean ruleAction, ItemBean codeItem,*/ String triggerGroupName, int studyId,/* int crfVersionId, String verbatimTermValue,*/ ItemDataBean itemDataBean, String codeItemOID, String /*dictionaryId */dictionaryOID, UserAccountBean ub) {
        Date startDateTime = new Date(System.currentTimeMillis());
//        TriggerAutoCodingActionBean autoCodingActionBean = (TriggerAutoCodingActionBean)ruleAction;
//        List<PropertyBean> properties = autoCodingActionBean.getProperties();
//        String codeItemOID = getCodeItemOIDFromRule(properties);
        String jobName =  codeItemOID + "";
        String simpleDatePattern = "yyyy-MM-dd-HHmmssSSS";
        SimpleDateFormat sdf = new SimpleDateFormat(simpleDatePattern);
        String endOfJobName =  sdf.format(new java.util.Date());
        jobName = jobName + "_" + endOfJobName;
        
//        if(triggerGroupName!=null)
//            TRIGGER_GROUP_NAME = triggerGroupName;
        
        SimpleTrigger trigger = new SimpleTrigger(jobName, triggerGroupName, 0, 1);
        
        trigger.setStartTime(startDateTime);
        trigger.setName(jobName);
        trigger.setGroup(triggerGroupName);
        //ToDo check what should be value of following misfire instruction
        trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
      //add the data required for job execution here into JobDataMap 
        
        jobDataMap.put(CodingJob.VERNBATIM_ITEM_ID, itemDataBean.getItemId());
        jobDataMap.put(CodingJob.VERBATIM_TERM_VALUE, itemDataBean.getValue());
        jobDataMap.put(CodingJob.STUDY_ID, studyId);
        jobDataMap.put(CodingJob.OWNER_ID, ub.getId());
//        jobDataMap.put(CodingJob.CRF_VERSION_ID, crfVersionId);
        
//        jobDataMap.put(CodingJob.USER_BEAN, ub);//commented out to see if it is OK without userBean; quartz job threw an error that userBean is not serializable
        
//        List<PropertyBean> properties = ((TriggerAutoCodingActionBean) ruleAction).getProperties();
//        jobDataMap.put(CodingJob.CODE_ITEM_OID, getCodeItemOIDFromRule(properties));
//        jobDataMap.put(CodingJob.RULE_ACTION, ruleAction);
          jobDataMap.put(CodingJob.CODE_ITEM_OID, codeItemOID);
//          jobDataMap.put(CodingJob.DICTIONARY_ID, dictionaryId);
          jobDataMap.put(CodingJob.DICTIONARY_OID, dictionaryOID);
        
//        ItemDataDAO iddao = new ItemDataDAO(dataSource);        
//        EventCRFBean eventCrfBean = (EventCRFBean) iddao.findByPK(itemDataBean.getEventCRFId());
//        jobDataMap.put(CodingJob.EVENT_CRF_BEAN,eventCrfBean); 
//        jobDataMap.put(CodingJob.VERNBATIM_ITEM_DATA_BEAN,itemDataBean); 
        jobDataMap.put(CodingJob.EVENT_CRF_ID,itemDataBean.getEventCRFId()); 
        jobDataMap.put(CodingJob.GROUP_ORDINAL,itemDataBean.getOrdinal());
//        jobDataMap.put(CodingJob.CODING_DICTIONARY_DAO, getCodingDictionaryDAO());
        trigger.setJobDataMap(jobDataMap);
        trigger.setVolatility(false);
        
        return trigger;
    }
	
//	private StdScheduler getScheduler(HttpServletRequest request) {
//        scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(request.getSession().getServletContext()).getBean(SCHEDULER);
//        return scheduler;
//    }
	
	public void createCodingRulesForEventDef(ArrayList eventDefinitionCRFs, StudyBean currentStudy, ResourceBundle respage, UserAccountBean ub) throws Exception{
		logger.debug("inside createCodingRulesForEventDef");
		EventDefinitionCRFBean edc = null; 
		
		ItemFormMetadataDAO metadataDAO = null;
		ItemFormMetadataBean itemFormMetadataBean1 = null;
		ItemFormMetadataBean itemFormMetadataBean2 = null;
		ArrayList<ItemFormMetadataBean> itemFormMetadataBeanLst1= null;
		ArrayList<ItemFormMetadataBean> itemFormMetadataBeanLst2= null;
		ItemDAO itemDAO = new ItemDAO(dataSource);
		int crfId = 0;
		int itemId = 0;
		int studyId;
		String codeItemOID = null;
		try{
			if((null != eventDefinitionCRFs) && (eventDefinitionCRFs.size() != 0)){
				for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
					edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
					if(edc != null){
						studyId = edc.getStudyId();
						crfId = edc.getCrfId();
						if(crfId != 0){
							 metadataDAO = new ItemFormMetadataDAO(dataSource);
							 itemFormMetadataBeanLst1 = (ArrayList<ItemFormMetadataBean>)metadataDAO.findUniqueItemsForCrf(crfId);
							 if((null != itemFormMetadataBeanLst1) && (itemFormMetadataBeanLst1.size() != 0)){
								 for(int j=0; j<itemFormMetadataBeanLst1.size(); j++){
									 itemFormMetadataBean1 = itemFormMetadataBeanLst1.get(j);
									 if(null != itemFormMetadataBean1){
										 itemId = itemFormMetadataBean1.getItemId();
//										 if(!areCodingRulesCreatedOnItem(itemId, currentStudy)){
//											 logger.debug("creating the coding rules for item with id " + itemId);
											 ItemBean codeItemBean = (ItemBean)itemDAO.findByPK(itemId);
											 if(null != codeItemBean){
												 codeItemOID = codeItemBean.getOid();
											 }
											 itemFormMetadataBeanLst2 = metadataDAO.findCodingResponseOptionsForItem(itemId);
											 if((null != itemFormMetadataBeanLst2) && (itemFormMetadataBeanLst2.size() != 0)){
												/* only 1 element from this list is fetched because at this point 
												 of time no change in the previously uploaded coding item response 
												 options will be allowed when uploading another CRF version. 
												 so all the items in this list will have same response options values 
												 i.e. same coding function parameters. */
												 itemFormMetadataBean2 = itemFormMetadataBeanLst2.get(0);
												 if((null != itemFormMetadataBean2)){
//													 if(!areCodingRulesCreatedOnItem(itemId, currentStudy)){
														 //logger.debug("passing response option values to create coding rule for item id: " + itemId);
														 RulesPostImportContainer importedRules =  createBothRulesForCodingReponseOption(itemFormMetadataBean2.getResponseSet(), currentStudy, crfId, codeItemOID, itemId);
														 if(null != importedRules){
															 importedRules = getRulesPostImportContainerService(currentStudy, respage, ub).validateRuleDefs(importedRules);
												             importedRules = getRulesPostImportContainerService(currentStudy, respage, ub).validateRuleSetDefs(importedRules);
															 logger.debug("saving the rules created");
															 getRuleSetService().saveImport(importedRules);
														 }
														 else{
															 logger.debug("RulesPostImportContainer instance for rule created is null" );
														 }
											 	}
												else{
													logger.error("no reponse options found in database for coding item with id: " + itemId);
												}
											 }
//									 	} 
										 
									 }
								 }
							 }
						}
					}
				}
			}
		}
		catch(Exception e){
			logger.error("Exception in method createCodingRulesForEventDef, error message: " + e.getMessage());
			logger.error("Stack trace: ");
			e.printStackTrace();
			throw e;
		}
	}
	
	private RulesPostImportContainer createBothRulesForCodingReponseOption(ResponseSetBean responseSet, StudyBean currentStudy, int crfId, String codeItemOID, int itemId){
		RulesPostImportContainer importedRules = null;
		if(null != responseSet){
			ArrayList options = responseSet.getOptions();
			ResponseOptionBean resposeBeanVerbatim = null;
			ResponseOptionBean resposeBeanDict = null;
			ResponseOptionBean resposeBeanAutoCoding = null;
			String verbatimTermOID = null;	
			String dictName = null;
			String dictVersion = null;
			String autoCodingOpt = null;			
			Integer verbatimTermItemId = null; 
			if(null != options){
				resposeBeanVerbatim = (ResponseOptionBean)options.get(0);
				
//				verbatimTermOID = getVerbatimTermOID(resposeBeanVerbatim, studyId, crfId);
//				ItemBean verbatimTermItem = getVerbatimTermItem(resposeBeanVerbatim, studyId, crfId);
				HashMap verbatimTermItemData = getVerbatimTermItem(resposeBeanVerbatim, currentStudy.getId(), crfId);
//				if(null != verbatimTermItem){
				if(null != verbatimTermItemData){
//					verbatimTermOID = verbatimTermItem.getOid();
//					verbatimTermItemId = verbatimTermItem.getId();
					verbatimTermOID = (String)verbatimTermItemData.get("oc_oid");
					verbatimTermItemId = (Integer)verbatimTermItemData.get("item_id");
				}
				
				logger.debug("verbatimTermOID: " + verbatimTermOID);
				//=============================
				if(!areCodingRulesCreatedOnItem(verbatimTermItemId, currentStudy)){
					resposeBeanDict = (ResponseOptionBean)options.get(1);
					dictName = getDictionaryOID(resposeBeanDict);
					logger.debug("dictName: " + dictName);
					dictVersion = getDictVersionFromStudyCodingConfig(); 
					CodingDictionaryDAO codingDictionaryDAO = (CodingDictionaryDAO)OCSpringApplicationContext.getBean("codingDictionaryDAO");
	//				CodingDictionaryBean dictionaryBean = codingDictionaryDAO.findByOID(dictName);
					CodingDictionaryBean dictionaryBean = null;
					List<CodingDictionaryBean> dictionaryLst = codingDictionaryDAO.getDictionaryForNameAndVersion(dictName, dictVersion);
					if((null != dictionaryLst) && (dictionaryLst.size() != 0)){
						dictionaryBean = dictionaryLst.get(0);//assumption is that the combination of dictionary name and dictionary 
						//version values will not be repeated in the table coding_dcitionary. Do we need to have unique constraint for this in DB?
						
					}
					String codeListOID = null;				
					if(null != dictionaryBean){
						codeListOID = dictionaryBean.getOid();
					}
					
					resposeBeanAutoCoding = (ResponseOptionBean)options.get(2);
					autoCodingOpt = getAutoCodingOptValue(resposeBeanAutoCoding);
									
					logger.debug("autoCodingOpt: " + autoCodingOpt);
					//generate rule only if auto coding options is set to 'Y'
					if((null != autoCodingOpt) && (autoCodingOpt.equalsIgnoreCase("y"))){
						//create first rule to get the value of medical code when verbatim term value is not blank
						RuleBean ruleBean1 = generateRuleBeanForCodeRule(verbatimTermOID, currentStudy.getId());
						//check if the generated rule OID is unique
						ruleBean1 = getRuleWithUniqueOID(ruleBean1);
						if(null != ruleBean1){
							logger.debug("For item id " + verbatimTermItemId + " generated code rule OID: " + ruleBean1.getOid());
						}
						
//						String ruleOID1 = generateCodeRuleOID(verbatimTermOID);
						RuleSetRuleBean ruleSetRuleBean1 = generateRuleSetRuleBeanForCodeRule(/*ruleOID1,*/ ruleBean1, codeItemOID, codeListOID);
						List<RuleSetRuleBean> ruleSetRules1 = new ArrayList<RuleSetRuleBean>();
						ruleSetRules1.add(ruleSetRuleBean1);
						ruleBean1.setRuleSetRules(ruleSetRules1);
						
						RuleSetBean ruleSet = generateRuleSetBeanForCodeRule(verbatimTermOID, verbatimTermItemId, ruleSetRules1);
						
						importedRules = new RulesPostImportContainer();
						ArrayList<RuleBean> ruleDefs = new ArrayList<RuleBean>();
						ruleDefs.add(ruleBean1);
						ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
	//					ruleSets.add(ruleSet1);
						
						
						//create second rule to clear out the value of medical code when verbatim term value is blank
						RuleBean ruleBean2 = generateRuleBeanForClearCodeRule(verbatimTermOID, currentStudy.getId());
						//check if the generated rule OID is unique
						ruleBean2 = getRuleWithUniqueOID(ruleBean2);
						if(null != ruleBean2){
							logger.debug("For item id " + verbatimTermItemId + " generated code rule OID: " + ruleBean2.getOid());
						}
						
						String ruleOID2 = generateClearCodeRuleOID(verbatimTermOID);
						RuleSetRuleBean ruleSetRuleBean2 = generateRuleSetRuleBeanForClearCodeRule(/*ruleOID2, */ruleBean2, codeItemOID);
						List<RuleSetRuleBean> ruleSetRules2 = new ArrayList<RuleSetRuleBean>();
						ruleSetRules2.add(ruleSetRuleBean2);
						ruleBean2.setRuleSetRules(ruleSetRules2);
						
	//					RuleSetBean ruleSet2 = generateRuleSetBeanForCodeRule(verbatimTermOID, verbatimTermItemId, ruleSetRules2);
						
						List<RuleSetRuleBean> totalRuleSetRules = new ArrayList<RuleSetRuleBean>();
						totalRuleSetRules.add(ruleSetRuleBean1);
						totalRuleSetRules.add(ruleSetRuleBean2);
						
						ruleSet.setRuleSetRules(totalRuleSetRules);
						
						ruleSet.setItemId(itemId);
						ruleDefs.add(ruleBean2);
	//					ruleSets.add(ruleSet2);
						ruleSets.add(ruleSet);
						
						importedRules.setRuleDefs(ruleDefs);
						importedRules.setRuleSets(ruleSets);
					}
				}
				else{
					logger.debug("rules are already created for all item with id " + verbatimTermItemId);
				}
			}
		}
		return importedRules;
	}
	
	private RulesPostImportContainer createCodingRuleForCodingReponseOption(ResponseSetBean responseSet, int studyId, int crfId, String codeItemOID){
		RulesPostImportContainer importedRules = null;
		if(null != responseSet){
			ArrayList options = responseSet.getOptions();
			ResponseOptionBean resposeBeanVerbatim = null;
			ResponseOptionBean resposeBeanDict = null;
			ResponseOptionBean resposeBeanAutoCoding = null;
			String verbatimTermOID = null;	
			String dictName = null;
			String dictVersion = null;
			String autoCodingOpt = null;			
			Integer verbatimTermItemId = null; 
			if(null != options){
				resposeBeanVerbatim = (ResponseOptionBean)options.get(0);
				
//				verbatimTermOID = getVerbatimTermOID(resposeBeanVerbatim, studyId, crfId);
//				ItemBean verbatimTermItem = getVerbatimTermItem(resposeBeanVerbatim, studyId, crfId);
				HashMap verbatimTermItemData = getVerbatimTermItem(resposeBeanVerbatim, studyId, crfId);
//				if(null != verbatimTermItem){
				if(null != verbatimTermItemData){
//					verbatimTermOID = verbatimTermItem.getOid();
//					verbatimTermItemId = verbatimTermItem.getId();
					verbatimTermOID = (String)verbatimTermItemData.get("oc_oid");
					verbatimTermItemId = (Integer)verbatimTermItemData.get("item_id");
				}
				
				logger.debug("verbatimTermOID: " + verbatimTermOID);
			
				resposeBeanDict = (ResponseOptionBean)options.get(1);
				dictName = getDictionaryOID(resposeBeanDict);
				logger.debug("dictName: " + dictName);
				dictVersion = getDictVersionFromStudyCodingConfig(); 
				CodingDictionaryDAO codingDictionaryDAO = (CodingDictionaryDAO)OCSpringApplicationContext.getBean("codingDictionaryDAO");
//				CodingDictionaryBean dictionaryBean = codingDictionaryDAO.findByOID(dictName);
				CodingDictionaryBean dictionaryBean = null;
				List<CodingDictionaryBean> dictionaryLst = codingDictionaryDAO.getDictionaryForNameAndVersion(dictName, dictVersion);
				if((null != dictionaryLst) && (dictionaryLst.size() != 0)){
					dictionaryBean = dictionaryLst.get(0);//assumption is that the combination of dictionary name and dictionary 
					//version values will not be repeated in the table coding_dcitionary. Do we need to have unique constraint for this in DB?
					
				}
				String codeListOID = null;				
				if(null != dictionaryBean){
					codeListOID = dictionaryBean.getOid();
				}
				
				resposeBeanAutoCoding = (ResponseOptionBean)options.get(2);
				autoCodingOpt = getAutoCodingOptValue(resposeBeanAutoCoding);
								
				logger.debug("autoCodingOpt: " + autoCodingOpt);
				//generate rule only if auto coding options is set to 'Y'
				if((null != autoCodingOpt) && (autoCodingOpt.equalsIgnoreCase("y"))){
					//create first rule to get the value of medical code when verbatim term value is not blank
					RuleBean ruleBean1 = generateRuleBeanForCodeRule(verbatimTermOID, studyId);
					//check if the generated rule OID is unique
					ruleBean1 = getRuleWithUniqueOID(ruleBean1);
					if(null != ruleBean1){
						logger.debug("For item id " + verbatimTermItemId + " generated code rule OID: " + ruleBean1.getOid());
					}
					
					String ruleOID1 = generateCodeRuleOID(verbatimTermOID);
					RuleSetRuleBean ruleSetRuleBean1 = generateRuleSetRuleBeanForCodeRule(/*ruleOID1, */ruleBean1, codeItemOID, codeListOID);
					List<RuleSetRuleBean> ruleSetRules1 = new ArrayList<RuleSetRuleBean>();
					ruleSetRules1.add(ruleSetRuleBean1);
					ruleBean1.setRuleSetRules(ruleSetRules1);
					
					RuleSetBean ruleSet = generateRuleSetBeanForCodeRule(verbatimTermOID, verbatimTermItemId, ruleSetRules1);
					
					importedRules = new RulesPostImportContainer();
					ArrayList<RuleBean> ruleDefs = new ArrayList<RuleBean>();
					ruleDefs.add(ruleBean1);
					ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
					ruleSets.add(ruleSet);
					
					importedRules.setRuleDefs(ruleDefs);
					importedRules.setRuleSets(ruleSets);
				}
			}
		}
		return importedRules;
	}
	
//	private RulesPostImportContainer createClearCodeRuleForCodingReponseOption(ResponseSetBean responseSet, int studyId, int crfId, String codeItemOID){
//		RulesPostImportContainer importedRules = null;
//		if(null != responseSet){
//			ArrayList options = responseSet.getOptions();
//			ResponseOptionBean resposeBeanVerbatim = null;
//			ResponseOptionBean resposeBeanDict = null;
//			ResponseOptionBean resposeBeanAutoCoding = null;
//			String verbatimTermOID = null;	
//			String dictName = null;
//			String dictVersion = null;
//			String autoCodingOpt = null;			
//			Integer verbatimTermItemId = null; 
//			if(null != options){
//				resposeBeanVerbatim = (ResponseOptionBean)options.get(0);
//				
////				verbatimTermOID = getVerbatimTermOID(resposeBeanVerbatim, studyId, crfId);
////				ItemBean verbatimTermItem = getVerbatimTermItem(resposeBeanVerbatim, studyId, crfId);
//				HashMap verbatimTermItemData = getVerbatimTermItem(resposeBeanVerbatim, studyId, crfId);
////				if(null != verbatimTermItem){
//				if(null != verbatimTermItemData){
////					verbatimTermOID = verbatimTermItem.getOid();
////					verbatimTermItemId = verbatimTermItem.getId();
//					verbatimTermOID = (String)verbatimTermItemData.get("oc_oid");
//					verbatimTermItemId = (Integer)verbatimTermItemData.get("item_id");
//				}
//				
//				logger.debug("verbatimTermOID: " + verbatimTermOID);
//			
//				resposeBeanDict = (ResponseOptionBean)options.get(1);
//				dictName = getDictionaryOID(resposeBeanDict);
//				logger.debug("dictName: " + dictName);
//				dictVersion = getDictVersionFromStudyCodingConfig(); 
//				CodingDictionaryDAO codingDictionaryDAO = (CodingDictionaryDAO)OCSpringApplicationContext.getBean("codingDictionaryDAO");
////				CodingDictionaryBean dictionaryBean = codingDictionaryDAO.findByOID(dictName);
//				CodingDictionaryBean dictionaryBean = null;
//				List<CodingDictionaryBean> dictionaryLst = codingDictionaryDAO.getDictionaryForNameAndVersion(dictName, dictVersion);
//				if((null != dictionaryLst) && (dictionaryLst.size() != 0)){
//					dictionaryBean = dictionaryLst.get(0);//assumption is that the combination of dictionary name and dictionary 
//					//version values will not be repeated in the table coding_dcitionary. Do we need to have unique constraint for this in DB?
//					
//				}
//				String codeListOID = null;				
//				if(null != dictionaryBean){
//					codeListOID = dictionaryBean.getOid();
//				}
//				
//				resposeBeanAutoCoding = (ResponseOptionBean)options.get(2);
//				autoCodingOpt = getAutoCodingOptValue(resposeBeanAutoCoding);
//								
//				logger.debug("autoCodingOpt: " + autoCodingOpt);
//				//generate rule only if auto coding options is set to 'Y'
//				if((null != autoCodingOpt) && (autoCodingOpt.equalsIgnoreCase("y"))){
//					//create first rule to get the value of medical code when verbatim term value is not blank
//					RuleBean ruleBean1 = generateRuleBeanForCodeRule(verbatimTermOID, studyId);
//					//check if the generated rule OID is unique
//					ruleBean1 = getRuleWithUniqueOID(ruleBean1);
//					if(null != ruleBean1){
//						logger.debug("For item id " + verbatimTermItemId + " generated code rule OID: " + ruleBean1.getOid());
//					}
//					
//					
//					
//					
//					
//					importedRules = new RulesPostImportContainer();
//					ArrayList<RuleBean> ruleDefs = new ArrayList<RuleBean>();
//					
//					ArrayList<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
////					ruleSets.add(ruleSet1);
//					
//					
//					//create second rule to clear out the value of medical code when verbatim term value is blank
//					RuleBean ruleBean2 = generateRuleBeanForClearCodeRule(verbatimTermOID, studyId);
//					//check if the generated rule OID is unique
//					ruleBean2 = getRuleWithUniqueOID(ruleBean2);
//					if(null != ruleBean2){
//						logger.debug("For item id " + verbatimTermItemId + " generated code rule OID: " + ruleBean2.getOid());
//					}
//					
//					String ruleOID2 = generateClearCodeRuleOID(verbatimTermOID);
//					RuleSetRuleBean ruleSetRuleBean2 = generateRuleSetRuleBeanForClearCodeRule(ruleOID2, ruleBean2, codeItemOID);
//					List<RuleSetRuleBean> ruleSetRules2 = new ArrayList<RuleSetRuleBean>();
//					ruleSetRules2.add(ruleSetRuleBean2);
//					ruleBean2.setRuleSetRules(ruleSetRules2);
//					
//					RuleSetBean ruleSet = generateRuleSetBeanForCodeRule(verbatimTermOID, verbatimTermItemId, ruleSetRules1);
//					ruleDefs.add(ruleBean2);
////					ruleSets.add(ruleSet2);
//					ruleSets.add(ruleSet);
//					
//					importedRules.setRuleDefs(ruleDefs);
//					importedRules.setRuleSets(ruleSets);
//				}
//			}
//		}
//		return importedRules;
//	}
	
	private RuleBean getRuleWithUniqueOID(RuleBean rule){
		RuleDao ruleDAO = (RuleDao)OCSpringApplicationContext.getBean("ruleDao");
		if(null != rule){
			RuleBean ruleBeanFromDB = ruleDAO.findByOid(rule);
			if(null != ruleBeanFromDB){
				logger.debug("generated rule OID already exists in the database, generating the randomized OID");
				 String oidPreRandomization = rule.getOid();
				OidGenerator oidGenerator = rule.getOidGenerator();
				String oid = oidGenerator.randomizeOid(oidPreRandomization);
				rule.setOid(oid);
			}
		}
		return rule;
	}
	
	private RuleSetBean generateRuleSetBeanForCodeRule(String verbatimTermOID, Integer itemId, List<RuleSetRuleBean> ruleSetRules){
		RuleSetBean ruleSet = new RuleSetBean();
		
		ExpressionBean target = new ExpressionBean(Context.OC_RULES_V1, verbatimTermOID);
		ruleSet.setTarget(target);
		ruleSet.setOriginalTarget(target);
		ruleSet.setItemId(itemId);
		ruleSet.setRuleSetRules(ruleSetRules);
		return ruleSet;
	}
	
	private RuleBean generateRuleBeanForCodeRule(String verbatimTermOID, int studyId){
		RuleBean ruleBean = new RuleBean();
		
		ruleBean.setName(generateCodeRuleName(verbatimTermOID));
		ruleBean.setDescription(generateCodeRuleName(verbatimTermOID));
		ruleBean.setOid(generateCodeRuleOID(verbatimTermOID));
		ExpressionBean codeRuleExpresion = new ExpressionBean(Context.OC_RULES_V1, generateCodeRuleExpression(verbatimTermOID));
		ruleBean.setExpression(codeRuleExpresion);
		ruleBean.setEnabled(true);
		ruleBean.setStudyId(studyId);
		ruleBean.setRuleType(EntityType.SYSTEM_CREATED_FOR_CODING);
		return ruleBean;
	}
	
	private RuleBean generateRuleBeanForClearCodeRule(String verbatimTermOID, int studyId){
		RuleBean ruleBean = new RuleBean();
		
		ruleBean.setName(generateClearCodeRuleName(verbatimTermOID));
		ruleBean.setDescription(generateClearCodeRuleName(verbatimTermOID));
		ruleBean.setOid(generateClearCodeRuleOID(verbatimTermOID));
		ExpressionBean codeRuleExpresion = new ExpressionBean(Context.OC_RULES_V1, generateClearCodeRuleExpression(verbatimTermOID));
		ruleBean.setExpression(codeRuleExpresion);
		ruleBean.setEnabled(true);
		ruleBean.setStudyId(studyId);
		ruleBean.setRuleType(EntityType.SYSTEM_CREATED_FOR_CODING);
		return ruleBean;
	}
	
	private RuleSetRuleBean generateRuleSetRuleBeanForCodeRule(/*String ruleOID, */RuleBean ruleBean, String codeItemOID, String codeListId){
		RuleSetRuleBean ruleSetRuleBean = new RuleSetRuleBean();
		ruleSetRuleBean.setRuleBean(ruleBean);
		List<RuleActionBean> actions = new ArrayList<RuleActionBean>();
		actions.add(generateCodeRuleAction(codeItemOID, codeListId));
		ruleSetRuleBean.setActions(actions);
		ruleSetRuleBean.setOid(ruleBean.getOid());
		return ruleSetRuleBean;
	}
	
	private RuleSetRuleBean generateRuleSetRuleBeanForClearCodeRule(/*String ruleOID, */RuleBean ruleBean, String codeItemOID){
		RuleSetRuleBean ruleSetRuleBean = new RuleSetRuleBean();
		ruleSetRuleBean.setRuleBean(ruleBean);
		List<RuleActionBean> actions = new ArrayList<RuleActionBean>();
		actions.add(generateClearCodeRuleAction(codeItemOID));
		ruleSetRuleBean.setActions(actions);
		ruleSetRuleBean.setOid(ruleBean.getOid());
		return ruleSetRuleBean;
	}
	
	private RuleActionBean generateCodeRuleAction(String codeItemOID, String codeListId){
		TriggerAutoCodingActionBean codeRuleAction = new TriggerAutoCodingActionBean();
		
		PropertyBean property = new PropertyBean();
		if(codeItemOID != null){
			property.setOid(codeItemOID.trim());
		}
		property.setCodeListOID(codeListId);
		List<PropertyBean> properties = new ArrayList<PropertyBean>();
		properties.add(property);
		codeRuleAction.setProperties(properties);
		codeRuleAction.setActionType(ActionType.TRIGGER_AUTO_CODING);
		
//		String simpleDatePattern = "yyyy-MM-dd";
//        SimpleDateFormat sdf = new SimpleDateFormat(simpleDatePattern);
//        String dateCreated =  sdf.format(new java.util.Date());
		codeRuleAction.setCreatedDate(new Date());
		codeRuleAction.setUpdatedDate(new Date());
		
		RuleActionRunBean ruleActionRun = new RuleActionRunBean(true, true, true, true, true);
		codeRuleAction.setRuleActionRun(ruleActionRun);
		codeRuleAction.setExpressionEvaluatesTo(true);
		return codeRuleAction;
		
	}
	
	private RuleActionBean generateClearCodeRuleAction(String codeItemOID){
		TriggerAutoCodingActionBean codeRuleAction = new TriggerAutoCodingActionBean();
		
		PropertyBean property = new PropertyBean();
		if(codeItemOID != null){
			property.setOid(codeItemOID.trim());
		}
		property.setValue("");
		List<PropertyBean> properties = new ArrayList<PropertyBean>();
		properties.add(property);
		codeRuleAction.setProperties(properties);
		codeRuleAction.setActionType(ActionType.TRIGGER_AUTO_CODING);
		
//		String simpleDatePattern = "yyyy-MM-dd";
//        SimpleDateFormat sdf = new SimpleDateFormat(simpleDatePattern);
//        String dateCreated =  sdf.format(new java.util.Date());
		codeRuleAction.setCreatedDate(new Date());
		codeRuleAction.setUpdatedDate(new Date());
		
		RuleActionRunBean ruleActionRun = new RuleActionRunBean(true, true, true, true, true);
		codeRuleAction.setRuleActionRun(ruleActionRun);
		codeRuleAction.setExpressionEvaluatesTo(true);
		return codeRuleAction;
		
	}
	
	private String generateCodeRuleExpression(String verbatimTermOID){
		String codeRuleExpression = null;
		if(null != verbatimTermOID){
			codeRuleExpression = verbatimTermOID.trim() + " ne \" \"";
		}
		return codeRuleExpression;
	}
	private String generateClearCodeRuleExpression(String verbatimTermOID){
		String codeRuleExpression = null;
		if(null != verbatimTermOID){
			codeRuleExpression = verbatimTermOID.trim() + " eq \" \"";
		}
		return codeRuleExpression;
	}
	private String generateCodeRuleOID(String verbatimTermOID){
		String codeRuleOID = null;
		if(null != verbatimTermOID){
			codeRuleOID = "R1" + verbatimTermOID.trim();
		}	
		
		return codeRuleOID;
	}
	private String generateClearCodeRuleOID(String verbatimTermOID){
		String clearCodeRuleOID = null;
		if(null != verbatimTermOID){
			clearCodeRuleOID = "R2" + verbatimTermOID.trim();
		}		
		return clearCodeRuleOID;
	}
	private String generateCodeRuleName(String verbatimTermOID){
		String codeRuleName = null;
		if(null != verbatimTermOID){
			codeRuleName = verbatimTermOID.trim() + " not empty";
		}		
		return codeRuleName;
	}
	private String generateClearCodeRuleName(String verbatimTermOID){
		String codeRuleName = null;
		if(null != verbatimTermOID){
			codeRuleName = verbatimTermOID.trim() + " empty";
		}		
		return codeRuleName;
	}
	private String generateCodeRuleDesc(String verbatimTermOID){
		return generateClearCodeRuleName(verbatimTermOID);
	}
	private String generateClearCodeRuleDesc(String verbatimTermOID){
		return generateClearCodeRuleName(verbatimTermOID);
	}
	
	private HashMap getVerbatimTermItem(ResponseOptionBean resposeBeanVerbatim, int studyId, int crfId){
		HashMap verbatimTermItemData = null;
		String verbatimRespValue = null;
		String verbatimTermOID = null;
		ItemDAO itemDAO = null;
		ArrayList verbatimTermItemLst = null;
		ItemBean ib = null;
				
		if (null != resposeBeanVerbatim){
			verbatimRespValue = resposeBeanVerbatim.getValue();
			logger.debug("resposeBeanVerbatim text: " + resposeBeanVerbatim.getText());
			logger.debug("resposeBeanVerbatim value: " + verbatimRespValue);
			//get the name of the verbatim term item
			String verbatimItemName = null;
			String[] tokens = verbatimRespValue.split("\\(");
			if(tokens.length > 0){
				verbatimItemName = tokens[2];
				logger.debug("verbatimItemName: " + verbatimItemName);
			}
			//get OID of verbatim term
			if(null != verbatimItemName){
				itemDAO = new ItemDAO(dataSource);
				verbatimTermItemLst = itemDAO.findByNameCrfVersionAndStudy(studyId, crfId, verbatimItemName);
				if ((verbatimTermItemLst != null) && (verbatimTermItemLst.size() != 0)){
//					ib = (ItemBean)verbatimTermItemLst.get(0);
					verbatimTermItemData =  (HashMap)verbatimTermItemLst.get(0);
//					if(null != ib){
//						verbatimTermOID = ib.getOid();
//					}
				}
			}
			else{
				logger.error("name of verbatim item in coding function is null in crf with id: " + crfId);
			}
			
		}	
		return verbatimTermItemData;
	}
	
	private String getDictionaryOID(ResponseOptionBean resposeBeanDict){
		String dictRespValue = null;
		String dictName = null;
		if (null != resposeBeanDict){
			logger.debug("resposeBeanDict text: " + resposeBeanDict.getText());
			logger.debug("resposeBeanDict value: " + resposeBeanDict.getValue());
			//get dictionary OID
			dictRespValue = resposeBeanDict.getValue();
			if((dictRespValue != null) && (dictRespValue.contains("'"))){
				dictName = dictRespValue.replaceAll("'", "");
			}			
		}
		return dictName;
	}
	
	private String getAutoCodingOptValue(ResponseOptionBean resposeBeanAutoCoding){
		String autoCodingOptRespValue = null;
		String autoCodingOpt = null;		
		if (null != resposeBeanAutoCoding){
			logger.debug("resposeBeanAutoCoding text: " + resposeBeanAutoCoding.getText());
			logger.debug("resposeBeanAutoCoding value: " + resposeBeanAutoCoding.getValue());
			autoCodingOptRespValue = resposeBeanAutoCoding.getValue();
			if(autoCodingOptRespValue != null){
				autoCodingOpt = autoCodingOptRespValue.trim();
				if(autoCodingOpt.contains(")")){
					autoCodingOpt = autoCodingOpt.replaceAll("\\)", "");
				}
				
			}
		}
		return autoCodingOpt;
	}
	
	
	
	public String getCodeItemOIDFromRule(List<PropertyBean> properties){
		String codeItemOID = null;
		
		PropertyBean propertyBean = properties.get(0);
		if(null != propertyBean){
			codeItemOID = propertyBean.getOid();
		}
		return codeItemOID;
	}
	
	
	private boolean areCodingRulesCreatedOnItem (int itemId, StudyBean currentStudy){
		boolean areCodingRulesCreatedOnItem = false;
		int cntSystemGeneratedRules = 0;
		RuleSetServiceInterface ruleSetService = (RuleSetServiceInterface)OCSpringApplicationContext.getBean("ruleSetService");
		ArrayList<RuleSetBean> ruleSets = ruleSetService.findRuleSetsByItemId(itemId, currentStudy);
		if((null != ruleSets) && (ruleSets.size() != 0)){
			RuleSetBean ruleSet = null;
			List<RuleSetRuleBean> ruleSetRules = null;
			for(int i=0; i<ruleSets.size(); i++){
				ruleSet = ruleSets.get(i);
				if(null != ruleSet){
					ruleSetRules = ruleSet.getRuleSetRules();
					if((null != ruleSetRules) && (ruleSetRules.size() != 0)){
						RuleSetRuleBean ruleSetRule = null;
						RuleBean rule = null;
						for(int j=0; j<ruleSetRules.size(); j++){
							ruleSetRule = ruleSetRules.get(j);
							if(null != ruleSetRule){
								rule = ruleSetRule.getRuleBean();
								if(null != rule){
									if(rule.getRuleType().getCode() == EntityType.SYSTEM_CREATED_FOR_CODING.getCode()){
											if (ruleSet.getStatus().getCode() == org.akaza.openclinica.domain.Status.DELETED.getCode()){
												//create new rules
											}
//											else if(ruleSet.getStatus().getCode() == org.akaza.openclinica.domain.Status.AVAILABLE.getCode()){
											else {
												cntSystemGeneratedRules++;//don't create new rules
											}
									}
								}
							}
						}						
					}
				}
			}
		}
		if(cntSystemGeneratedRules == 2){
			areCodingRulesCreatedOnItem = true;
		}
		return areCodingRulesCreatedOnItem;
	}
//	public String getDictionaryIdFromRule(List<PropertyBean> properties){
//		String dictionaryId = null;
//		
//		PropertyBean propertyBean = properties.get(0);
//		if(null != propertyBean){
//			dictionaryId = propertyBean.getCoding_dictionary_id();
//		}
//		return dictionaryId;
//	}
	public String getDictionaryOIDFromRule(List<PropertyBean> properties){
		String dictionaryOID = null;
		
		PropertyBean propertyBean = properties.get(0);
		if(null != propertyBean){
			dictionaryOID = propertyBean.getCodeListOID();
		}
		return dictionaryOID;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public ThesaurusDAO getThesaurusDAO() {
//		return thesaurusDAO;
		return (ThesaurusDAO) OCSpringApplicationContext.getBean("thesaurusDAO");
	}
	public void setThesaurusDAO(ThesaurusDAO thesaurusDAO) {
		this.thesaurusDAO = thesaurusDAO;
	}
	
	public CodingDictionaryDAO getCodingDictionaryDAO() {
		return (CodingDictionaryDAO) OCSpringApplicationContext.getBean("codingDictionaryDAO");
	}
	public void setCodingDictionaryDAO(CodingDictionaryDAO codingDictionaryDAO) {
		this.codingDictionaryDAO = codingDictionaryDAO;
	}
	
	private RuleSetServiceInterface getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) OCSpringApplicationContext.getBean(
                    "ruleSetService");
        // TODO: Add getRequestURLMinusServletPath(),getContextPath()
        return ruleSetService;
    }
	
	private RulesPostImportContainerService getRulesPostImportContainerService(StudyBean currentStudy, ResourceBundle respage, UserAccountBean ub) {
        rulesPostImportContainerService =
            this.rulesPostImportContainerService != null ? rulesPostImportContainerService : (RulesPostImportContainerService) OCSpringApplicationContext.getBean("rulesPostImportContainerService");
        rulesPostImportContainerService.setCurrentStudy(currentStudy);
        rulesPostImportContainerService.setRespage(respage);
        rulesPostImportContainerService.setUserAccount(ub);
        return rulesPostImportContainerService;
    }
	
	public ArrayList<CodeItemData> getMedicalCodeDataPerStudySubject(int studyId){
		ItemDataDAO iddao = new ItemDataDAO(dataSource); 
		ArrayList<CodeItemData> codeData = iddao.getMedicalCodeDataPerStudySubject(studyId);
		if((null != codeData) && (codeData.size() != 0)){
			CodeItemData codeItem = null;
			Integer crfId = 0;
			Integer eventCrfId = 0;
			Integer verbatimTermItemId = null;
			ItemDataBean verbatimTermItemData = null;
			ItemDAO itemDAO = new ItemDAO(dataSource);
			ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
			ArrayList verbatimTermItemLst = null;
			HashMap verbatimTermItemDataMap = null;
			ArrayList<ItemDataBean> verbatimTermItemDataList = null;
			int groupOrdinal = 0;
			for(int i=0; i<codeData.size(); i++){
				codeItem = codeData.get(i);
				if(null != codeItem){
					String respOptVal = codeItem.getResponseOptionValues();
					crfId = codeItem.getCrfId();
					eventCrfId = codeItem.getEventCrfId();
					groupOrdinal = codeItem.getGroupOrdinal();
					if(null != respOptVal){
						String verbatimTermItemName = getItemNameFromRespValues(respOptVal);
						if(null != verbatimTermItemName){
							verbatimTermItemName = verbatimTermItemName.trim();
							//set verbatim term name
							codeItem.setVerbatimTermItemName(verbatimTermItemName);
							
							verbatimTermItemLst = itemDAO.findByNameCrfVersionAndStudy(studyId, crfId, verbatimTermItemName);
							if ((verbatimTermItemLst != null) && (verbatimTermItemLst.size() != 0)){
								verbatimTermItemDataMap = (HashMap)verbatimTermItemLst.get(0);
								if(null != verbatimTermItemDataMap){
									verbatimTermItemId = (Integer)verbatimTermItemDataMap.get("item_id");
									verbatimTermItemData = itemDataDAO.findByItemIdAndEventCRFIdAndOrdinal(verbatimTermItemId, eventCrfId, groupOrdinal);
											if(null != verbatimTermItemData){
												//set verbatim term value
												codeItem.setVerbatimTermItemValue(verbatimTermItemData.getValue());
												//set coding status
												Integer codingStatusId = verbatimTermItemData.getCodingStatusId();
												if(null != codingStatusId){
													if(codingStatusId == Status.CODED.getCode()){
														codeItem.setStatus(Status.CODED.getName());
													}
													else if(codingStatusId == Status.NOT_CODED.getCode()){
														codeItem.setStatus(Status.NOT_CODED.getName());
													}
													else if(codingStatusId == Status.MATCH_NOT_FOUND.getCode()){
														codeItem.setStatus(Status.MATCH_NOT_FOUND.getName());
													}
													else if(codingStatusId == Status.NOT_CODED.getCode()){
														codeItem.setStatus(Status.NOT_CODED.getName());
													}
//													else if(codingStatusId == Status.MULTIPLE_MATCHES.getCode()){
//														codeItem.setStatus(Status.MULTIPLE_MATCHES.getName());
//													}
													else if(codingStatusId == Status.NO_EXACT_MATCH.getCode()){
														codeItem.setStatus(Status.NO_EXACT_MATCH.getName());
													}
												}
												//set code root path details from the terms hierarchy in dictionary
												ThesaurusDAO thesaurusDAO = (ThesaurusDAO)OCSpringApplicationContext.getBean("thesaurusDAO");
												String dictName = getDictNameFromItemRespValues(respOptVal);
												String dictVersion = getDictVersionFromStudyCodingConfig(); 
												CodingDictionaryDAO codingDictionaryDAO = (CodingDictionaryDAO)OCSpringApplicationContext.getBean("codingDictionaryDAO");
								//				CodingDictionaryBean dictionaryBean = codingDictionaryDAO.findByOID(dictName);
												CodingDictionaryBean dictionaryBean = null;
												List<CodingDictionaryBean> dictionaryLst = codingDictionaryDAO.getDictionaryForNameAndVersion(dictName, dictVersion);
												if((null != dictionaryLst) && (dictionaryLst.size() != 0)){
													dictionaryBean = dictionaryLst.get(0);//assumption is that the combination of dictionary name and dictionary 
													//version values will not be repeated in the table coding_dcitionary. Do we need to have unique constraint for this in DB?													
												}
												int dictId = 0;
												if(null != dictionaryBean){
													dictId = dictionaryBean.getId();
												}
												ArrayList<ThesaurusBean> thesaurusEntries = thesaurusDAO.getCodeFromThesaurus(verbatimTermItemData.getValue(), studyId, dictId);
												ThesaurusBean thesaurusBean = null;
												if((null != thesaurusEntries) && (thesaurusEntries.size() != 0)){
													thesaurusBean = thesaurusEntries.get(0);//assumption : thesaurus should always contain only one row for a combination 
																			//of dictionary, verbatim term, study id
												}
												if (null != thesaurusBean){
													codeItem.setCodeRootPath(thesaurusBean.getCode_details());
												}
												
											}									
								}
							}
						}
						
					}
				}
			}//end for loop
			codeItem = null;
			crfId = null;
			eventCrfId = null;
			verbatimTermItemId = null;
			verbatimTermItemData = null;
			itemDAO = null;
			itemDataDAO = null;
			verbatimTermItemLst = null;
			verbatimTermItemDataMap = null;
			verbatimTermItemDataList = null;
		}
		return codeData;
		
	}
	
	public String getCodeHierarchyDetails(String ontologyId, String ontologyVersionId, String conceptId){
		String codeHierarchyDetails = "";
		
		//get root path structure
    	String rootPath = BioPortalWebServiceManager.findConceptRootPath(ontologyId, conceptId);
    	if(rootPath == null){
    		logger.error("concept rootpath could not be retrieved for conceptId: " + conceptId);
//    		codingStatus = "concept rootpath could not be retrieved";  
    	}
    	else{
//    		logger.debug("rootPath: " + rootPath);
    		String[] tokens = rootPath.split("\\.");
//    		int levelCnt = 1;
    		for(int i=0; i<tokens.length; i++){
    			String conceptIdTmp = tokens[i];
    			String codeDetailsStr = BioPortalWebServiceManager.findConceptLabel(ontologyVersionId, conceptIdTmp);
    			if((null != codeDetailsStr) && (!codeDetailsStr.equals(""))){
    				if(i != (tokens.length-1)){
    					codeHierarchyDetails += codeDetailsStr + ":";
    				}
    				else{
    					codeHierarchyDetails += codeDetailsStr;
    				}
    			}
    		}
    	}
    	return codeHierarchyDetails;
	}
}
