package org.akaza.openclinica.job;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.core.OCSpringApplicationContext;
import org.akaza.openclinica.dao.hibernate.CodingDictionaryDAO;
import org.akaza.openclinica.dao.hibernate.ThesaurusDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.domain.coding.CodingDictionaryBean;
import org.akaza.openclinica.domain.coding.ThesaurusBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.action.TriggerAutoCodingActionBean;
import org.akaza.openclinica.service.coding.BioPortalWebServiceManager;
import org.akaza.openclinica.service.coding.CodingService;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
/**
 * Quartz job to do auto-coding on CRF data save
 * For every data item of CRf specified as item to be coded, look into code for thesaurus. If not found in
 * thesaurus (not coded in past) go for dictionary lookup
 * @author pgawade
 * 
 */
public class CodingJob extends QuartzJobBean {

//    public static final String ITEM_DATA_BEAN = "itemDataBean";
    public static final String STUDY_ID = "studyId";
//    public static final String CRF_VERSION_ID = "crfVersionId";
//    public static final String CODE_ITEM_NAME = "codeItemName";
    public static final String CODE_ITEM_OID = "codeItemOID";
//    public static final String DICTIONARY_ID = "dictionaryId";
    public static final String DICTIONARY_OID = "dictionaryId";
//    public static final String CODE_ITEM_BEAN = "codeItemBean";
    public static final String VERBATIM_TERM_VALUE = "verbatimTermValue";
    public static final String EVENT_CRF_BEAN = "eventCrfBean";
//    public static final String USER_BEAN = "userBean";
    public static final String OWNER_ID = "ownerId";
    public static final String RULE_ACTION = "ruleAction";
//    public static final String VERNBATIM_ITEM_DATA_BEAN = "verbatimItemDataBean";
    public static final String VERNBATIM_ITEM_ID = "verbatimItemId";
//    public static final String CODING_DICTIONARY_DAO = "CodingDictionaryDAO"; 
    public static final String EVENT_CRF_ID = "eventCrfId";
    public static final String GROUP_ORDINAL = "group_ordinal";
    
    private DataSource dataSource;
    private ThesaurusDAO thesaurusDAO;
    private EventCRFDAO eventCRFDAO;
    private ExpressionService expressionService;
    private DynamicsMetadataService metadataService;
    private CodingDictionaryDAO codingDictionaryDAO;
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //data needed
    	//CRF data entered during data entry, dictionary, dict version, OID of code item to update the database
    	//Process
    	//For every data item of CRf specified as item to be coded, look into code for thesaurus. If not found in
    	// thesaurus (not coded in past) go for dictionary lookup
    	try{
    		ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
	    	JobDataMap dataMap = context.getMergedJobDataMap();
//	    	ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
	    	dataSource = (DataSource) appContext.getBean("dataSource");
	    	codingDictionaryDAO = (CodingDictionaryDAO)appContext.getBean("codingDictionaryDAO");
	    	thesaurusDAO = (ThesaurusDAO)appContext.getBean("thesaurusDAO");
//	    	CodingService codingService =  new CodingService(dataSource);
	    	CodingService codingService = (CodingService) OCSpringApplicationContext.getBean("codingService");
	    	String verbatimTermValue = (String)dataMap.getString(VERBATIM_TERM_VALUE);
	    	Integer verbatimItemId = (Integer)dataMap.getInt(VERNBATIM_ITEM_ID);
//	    	ItemDataBean verbatimTermItemBean = (ItemDataBean)dataMap.get(ITEM_DATA_BEAN);
	    	ItemDataDAO iddao = new ItemDataDAO(dataSource);
	    	
//	    	if(null != verbatimTermItemBean){
	    	if(null != verbatimTermValue){
//	    		int dictionaryId = 0;
//	    		String dictionaryId = null;
	    		String dictionaryOID = null;
	    		String ontologyId = null; //this will be used to access the coding tool web service
	    		String finalCode = null;
	    		String codeHierarchyDetails = null;
	    		String codeFromThesaurus = null;
	    		String codeFromDict = null;
	    		Integer group_ordinal = dataMap.getInt(GROUP_ORDINAL);
	    		Integer ownerId = dataMap.getInt(OWNER_ID);
	    		int studyId = dataMap.getInt(STUDY_ID);
//	    		ItemDataBean verbatimItemDataBean = (ItemDataBean)dataMap.get(VERNBATIM_ITEM_DATA_BEAN);
	    		int eventCrfId = dataMap.getInt(EVENT_CRF_ID);
//	    		UserAccountBean ub = (UserAccountBean)dataMap.get(USER_BEAN);
//	    		String codeItemName = dataMap.getString(CODE_ITEM_NAME);
//	    		String codeItemOID = dataMap.getString(CODE_ITEM_OID);
//	    		TriggerAutoCodingActionBean autoCodingActionBean = (TriggerAutoCodingActionBean)dataMap.get(RULE_ACTION);
//		    	List<PropertyBean> properties = autoCodingActionBean.getProperties();
//	    		String codeItemOID = codingService.getCodeItemOIDFromRule(properties);
	    		String codeItemOID = dataMap.getString(CODE_ITEM_OID);
	    		
	    		eventCRFDAO = new EventCRFDAO(dataSource);
	    		EventCRFBean eventCrfBean = (EventCRFBean) eventCRFDAO.findByPK(/*verbatimItemDataBean.getEventCRFId()*/eventCrfId);
	    		//fetch verbatim term item data bean
	    		ItemDataBean verbatimItemDataBean = iddao.findByItemIdAndEventCRFIdAndOrdinal(verbatimItemId, eventCrfBean.getId(), group_ordinal);
	    											
//	    		EventCRFBean eventCrfBean = (EventCRFBean)dataMap.get(EVENT_CRF_BEAN);
	    		
//	    		if(null != verbatimTermItemBean.getValue()){
//	    			verbatimTermValue = verbatimTermItemBean.getValue().trim();
//	    			verbatimTermId = verbatimTermItemBean.getId();
//	    		}   
	    		
	    		
//	    		ItemDAO itemDAO = new ItemDAO(dataSource);
//	    		ItemBean codeItemBean = null;
//	    		if((null != codeItemName) && (!codeItemName.equalsIgnoreCase(""))){
//	    			codeItemBean = (ItemBean)itemDAO.findByNameCrfVersionAndStudy(studyId, crfVersionId, codeItemName);
//	    		}
	    		
//				ItemBean itemBean = iddao.getItem(verbatimTermItemBean.getItemId());
//	    		ItemBean codeItemBean = (ItemBean)dataMap.get(CODE_ITEM_BEAN);
//				if( (null != codeItemBean) && (null != codeItemBean.getItemMeta())){
//					ItemFormMetadataBean itemMetadata = codeItemBean.getItemMeta();
//					ArrayList options = itemMetadata.getResponseSet().getOptions();
////					if((null != options) && (options.size() != 0)){
//						codingFunc = (String)options.get(0);
						
//						if(null != verbatimTermValue){
//	    				dictionaryId = codingService.getDictionaryIdFromRule(properties);
	    		
	    				//******* If verbatim term value is not blank; do the auto-coding
	    				if(!verbatimTermValue.equalsIgnoreCase("")){
//		    				dictionaryId = dataMap.getString(DICTIONARY_ID);
	    					dictionaryOID = dataMap.getString(DICTIONARY_OID);
	    					//get dictionaryId by dictionary name and version from database
	//							CodingDictionaryBean dictionary = codingService.getDictionaryForDictNameAndVersion(dictionaryName, dictionaryVersion);
//							int dictIdInt = Integer.parseInt(dictionaryId);
	    					
							codingService.setCodingDictionaryDAO(codingDictionaryDAO);
//							CodingDictionaryBean dictionary = codingService.getDictionaryForId(dictIdInt);
							CodingDictionaryBean dictionary = codingService.getDictionaryForOID(dictionaryOID);
							int dictIdInt = 0;
							if(null != dictionary){
								dictIdInt = dictionary.getId();
							}
							else{
								logger.error("No dictionary found for the codelistID: " + dictionaryOID);
							}
							
							boolean isCodedFromThesaurus = true;
							boolean isCodeFoundInDict = false;
							if(null != dictionary){
	//								dictionaryId = dictionary.getId();
								
	//								thesaurusDAO = (ThesaurusDAO) OCSpringApplicationContext.getBean("thesaurusDAO");
								codingService.setThesaurusDAO(thesaurusDAO);
								//dictionary version Id is not passed as parameter when fetching the entry from thesaurus
								// because internally every dictionary version will be considered as a separate dictionary uniquely identified by 
								//dictionryId in coding_dictionary table								
								codeFromThesaurus = codingService.getCodeFromThesaurus(verbatimTermValue, studyId, dictIdInt/*, dictionaryVersion*/);
							
								if(codeFromThesaurus == null){//no code found in the thesaurus; lookup in the dictionary
									isCodedFromThesaurus = false;
//									ontologyId = dictionary.getOntology_id();
									ontologyId = dictionary.getOid();
									codeFromDict = BioPortalWebServiceManager.findMatch(ontologyId, verbatimTermValue, false);
									finalCode = codeFromDict;
									if((null != codeFromDict) && (!codeFromDict.equals(""))){
										isCodeFoundInDict = true;
									}
								}
								else{
									isCodedFromThesaurus = true;
									finalCode = codeFromThesaurus;		
									codeHierarchyDetails = codingService.getCodeHierarchyDetails(ontologyId, "42280", finalCode);//temporarily hard coded the ontology version id
								}
								//save the code to item_data table
								//get the value for codeItemDataId (item_data_id for code item associated with the verbatim term item)
								//fetch the existing data from database for code of the verbatim term
								
								if((null != finalCode) && (!finalCode.equalsIgnoreCase(""))){									
									//get ItemDataBean using codeItemOID
									ItemBean codingItemBean = getExpressionService().getItemBeanFromExpression(codeItemOID);
	//									ItemDataBean codeItemDataBean  = (ItemDataBean)iddao.findByPK(codeItemDataId);//does this need to be findByPKAndStudy?
									
									ItemDataBean oidBasedItemData = iddao.findByItemIdAndEventCRFIdAndOrdinal(codingItemBean.getId(), eventCrfBean.getId(), group_ordinal);
									
							        if (oidBasedItemData.getId() == 0) {
							        	metadataService = getMetadataService(appContext);
							            oidBasedItemData = metadataService.createItemData(oidBasedItemData, codingItemBean, group_ordinal, eventCrfBean/*, ub*/, ownerId);
							        }
	//									if(null != codeItemDataBean){
	//										codeItemDataBean.setValue(finalCode);
	//										iddao.updateValue(codeItemDataBean);
	//									}
							        //update code item data value
							        oidBasedItemData.setValue(finalCode);							        
							        getItemDataDAO().updateValue(oidBasedItemData, "yyyy-MM-dd");
							        //update verbatim term item coding status.
							        updateItemDataStatus(verbatimItemDataBean, Status.CODED.getId(), "yyyy-MM-dd");
							        
							        if(!isCodedFromThesaurus){
							        	//insert a row for this code into thesaurus table
							        	ThesaurusBean thesaurusBean = new ThesaurusBean();
							        	thesaurusBean.setStudy_id(studyId);
							        	thesaurusBean.setDictionary_id(dictIdInt);
							        	thesaurusBean.setVerbatim_term(verbatimTermValue);
							        	thesaurusBean.setDate_updated(new java.sql.Timestamp(new Date().getTime()));
							        	thesaurusBean.setCode(finalCode);
							        	
//							        	String codeHierarchyDetails = codingService.getCodeHierarchyDetails(ontologyId, "42280", finalCode);//temporarily hard coded the ontology version id
							        	thesaurusBean.setCode_details(codeHierarchyDetails);
							        	thesaurusDAO.saveOrUpdate(thesaurusBean);
							        	
							        }
								}
//								if(!isCodeFoundInDict){//********* clear out the previously stored code from database as code is not found in the dictionary
								else{//********* clear out the previously stored code from database as code is not found in the dictionary
									//ToDo do we need to audit change in the code item value?
									clearCodeInDB(appContext, codeItemOID, eventCrfId, group_ordinal, ownerId, true);
									if((null == finalCode)){
										//update verbatim term item coding status to match not found.
								        updateItemDataStatus(verbatimItemDataBean, Status.MATCH_NOT_FOUND.getId(), "yyyy-MM-dd");
									}
									else if(finalCode.equalsIgnoreCase("")){
										//update verbatim term item coding status to multiple matches found.
								        updateItemDataStatus(verbatimItemDataBean, Status.NO_EXACT_MATCH.getId(), "yyyy-MM-dd");
									}
									
									
								}
								 
							}//if(null != dictionary)
	    				}//if(!verbatimTermValue.equalsIgnoreCase(""))
	    				else{//********* clear out the previously stored code from database as verbatim term is blank
	    					clearCodeInDB(appContext, codeItemOID, eventCrfId, group_ordinal, ownerId, false);
	    					updateItemDataStatus(verbatimItemDataBean, Status.NOT_CODED.getId(), "yyyy-MM-dd");
	    				}
							
//						}
//					}
//				}
				
	    		
	    	}
    	}
//    	catch(SchedulerException se){
//    		se.printStackTrace();
//            logger.error(se.getStackTrace().toString());
//    	}
    	catch(Exception e){
    		e.printStackTrace();
            logger.error(e.getStackTrace().toString());
    	}
    	

    }

    private void updateItemDataStatus(ItemDataBean itemData, int statusId, String current_df_string){
    	itemData.setCodingStatusId(statusId);
    	getItemDataDAO().updateValueWithCodingStatus(itemData, current_df_string);
    }
    
    private ItemDataDAO getItemDataDAO() {
    /*    itemDataDAO = this.itemDataDAO != null ? itemDataDAO : new ItemDataDAO(ds);
        return itemDataDAO;*/
        return new ItemDataDAO(dataSource);
	}
    
    private ExpressionService getExpressionService() {
        expressionService = this.expressionService != null ? expressionService : new ExpressionService(dataSource);
        return expressionService;
    }
    
    private DynamicsMetadataService getMetadataService(ApplicationContext appContext) {
    	metadataService = this.metadataService != null ? metadataService : (DynamicsMetadataService)appContext.getBean("dynamicsMetadataService");;
        return metadataService;
    }
    
    private void clearCodeInDB(ApplicationContext appContext, String codeItemOID, /*ItemDataBean verbatimItemDataBean*/ int eventCrfId, int ordinal, int ownerId, boolean createNewRow){
    	ItemBean codingItemBean = getExpressionService().getItemBeanFromExpression(codeItemOID);
//		ItemDataBean codeItemDataBean  = (ItemDataBean)iddao.findByPK(codeItemDataId);//does this need to be findByPKAndStudy?
		
		eventCRFDAO = new EventCRFDAO(dataSource);
		EventCRFBean eventCrfBean = (EventCRFBean) eventCRFDAO.findByPK(eventCrfId);
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		ItemDataBean oidBasedItemData = iddao.findByItemIdAndEventCRFIdAndOrdinal(codingItemBean.getId(), eventCrfBean.getId(), ordinal);
		if (oidBasedItemData.getId() != 0) {
			oidBasedItemData.setValue("");
			oidBasedItemData.setStatus(Status.MATCH_NOT_FOUND);
	        getItemDataDAO().updateValue(oidBasedItemData, "yyyy-MM-dd");
		}
		else{
			if(createNewRow){
				metadataService = getMetadataService(appContext);
	            oidBasedItemData = metadataService.createItemData(oidBasedItemData, codingItemBean, ordinal, eventCrfBean/*, ub*/, ownerId);
			}
			else{
				
			}
		}
    }
    
      
}
