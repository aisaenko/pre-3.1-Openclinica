package org.akaza.openclinica.logic.core;

/**
 * The <code>Skipper</code> acts as a controller to control the skipping behavior.
 * 
 * @author Hailong Wang, Ph.D
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.logic.SkipRuleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplaySkipRuleBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.logic.SkipRuleDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.logic.core.function.Function;

public class Skipper {
    private Logger logger = Logger.getLogger(getClass().getName());
    
    private SessionManager sm;
    private EventCRFBean ecb;
    private UserAccountBean ub;
    private EventDefinitionCRFBean edcb;
    
    private HashMap<String, ItemBean> map;
    private HashMap<Integer, String> itemdata;
    private List<DisplaySkipRuleBean> skipRules;

    public Skipper(SessionManager sm, EventCRFBean ecb, EventDefinitionCRFBean edcb, UserAccountBean ub){
        this.sm = sm;
        this.ecb = ecb;
        this.edcb = edcb;
        this.ub = ub;
        
        map = new HashMap<String, ItemBean>(100);
        ItemDAO idao = new ItemDAO(sm.getDataSource());
        List itemList = idao.findAllItemsByVersionId(ecb.getCRFVersionId());
        for(int i = 0; i < itemList.size(); i++){
            ItemBean ib = (ItemBean)itemList.get(i);
            map.put(ib.getName(), ib);
        }
        skipRules = new ArrayList<DisplaySkipRuleBean>(20);

        itemdata = new HashMap<Integer, String>();
        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
        List<ItemDataBean> itemDataList = iddao.findAllByEventCRFId(ecb.getId());
        for(ItemDataBean bean : itemDataList){
            itemdata.put(bean.getItemId(), bean.getValue());
        }
}
    
    public void doSkip(){
        skipRules = createDisplaySkipRules();
    }

    /**
     * Creates the list of DispalySkipRuleBean.
     * @return the list of DisplaySkipRuleBean.
     */
    public List<DisplaySkipRuleBean> createDisplaySkipRules(){
        List<DisplaySkipRuleBean> skipRuleBeans = new ArrayList<DisplaySkipRuleBean>();
        SkipRuleDAO rd = new SkipRuleDAO(sm.getDataSource());
        Collection rules = null;
        try{
            rules = rd.findAllByCRFVersionId(ecb.getCRFVersionId());
        }catch(OpenClinicaException e){
            //continue
        }
        if(rules == null || rules.size() == 0){
            logger.info("There are no skip rules related to this CRF version: "
                    + ecb.getCrfVersion().getName());
            return null;
        }
        
        Parser parser = new Parser(map);
        for(Iterator iter = rules.iterator(); iter.hasNext();){
            SkipRuleBean srb = (SkipRuleBean)iter.next();
            DisplaySkipRuleBean dsrb = new DisplaySkipRuleBean(srb);
            Function condition = (Function)parser.parse(srb.getCondition());
            dsrb.setTriggerItems(new ArrayList<ItemBean>(condition.getVariables(map.values())));
            dsrb.setJsExpression(condition.getScript());
            Function action = (Function)parser.parse(srb.getSkipAction());
            dsrb.setAssignments(action.getAssignments().entrySet());
            skipRuleBeans.add(dsrb);
        }
        return skipRuleBeans;
    }
    
    /**
     * @return the list of DisplaySkipRuleBean.
     */
    public List<DisplaySkipRuleBean> getDisplayBeans() {
        return skipRules;
    }
    
    public List<DisplayItemBean> update(List<DisplayItemBean> displayItemBeans) {
        
        List<DisplayItemBean> otherDisplayItemBeans = new ArrayList<DisplayItemBean>();
        
        HashMap<Integer, DisplayItemBean> map = new HashMap<Integer, DisplayItemBean>();

        // Uses the item data from the form to update the item data map.
        for (DisplayItemBean dib : displayItemBeans) {
            itemdata.put(dib.getItem().getId(), dib.getData().getValue());
            map.put(dib.getItem().getId(), dib);
        }

        // skipRules = createDisplaySkipRules();
        ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());

        if (skipRules!=null) {
            for (DisplaySkipRuleBean rule : skipRules) {
                if (evaluateRule(rule)) {
                    for (Entry<ItemBean, String> assignment : rule.getAssignments()) {
                        ItemBean ib = assignment.getKey();

                        if (map.containsKey(ib.getId())) {
                            DisplayItemBean dib = map.get(ib.getId());
                            dib.getData().setValue(assignment.getValue());
                        } else {
                            DisplayItemBean dib = new DisplayItemBean();

                            // sets the EventDefinitionCRF
                            dib.setEventDefinitionCRF(edcb);

                            // sets the item bean
                            dib.setItem(ib);

                            // sets the item form metadata bean
                            dib.setMetadata(ifmdao.findByItemIdAndCRFVersionId(
                                    ib.getId(), ecb.getCRFVersionId()));

                            ItemDataBean idb = new ItemDataBean();
                            idb.setItemId(ib.getId());
                            idb.setStatus(Status.UNAVAILABLE);
                            if (!idb.isActive()) {
                                // will this need to change for double data
                                // entry?
                                idb.setCreatedDate(new Date());
                                idb.setOwner(ub);
                                idb.setEventCRFId(ecb.getId());
                            }
                            // sets the item data bean
                            dib.setData(idb);
                            otherDisplayItemBeans.add(dib);
                        }
                    }
                }
            }
        }
        return otherDisplayItemBeans;
    }

    public List<DisplaySkipRuleBean> getDisplayBeans(List<DisplayItemBean> matches) {
    	List<ItemBean> items = new ArrayList<ItemBean>();
    	for (DisplayItemBean dispItem : matches) {
			items.add(dispItem.getItem());
		}
    	List<DisplaySkipRuleBean> rules = new ArrayList<DisplaySkipRuleBean>();
    	if (skipRules!=null) {
        	for (DisplaySkipRuleBean rule : skipRules) {
            	//only include rules where all items in matches
        		if (items.containsAll(rule.getTriggerItems())) {
                	rules.add(rule);
        		}
    		}
    	}
        return rules;
    }
    
    public boolean evaluateRule(DisplaySkipRuleBean rule) {
        Parser parser = new Parser(map);
        Function condition = (Function)parser.parse(rule.getSkipRule().getCondition());
        condition.execute(itemdata);
    	return Boolean.parseBoolean(condition.getValue());
    }

    /**
	 * @param items
	 */
	public void setSkipped(List<DisplayItemBean> items) {
		HashMap<Integer, DisplayItemBean> itemBeanMap=new HashMap<Integer, DisplayItemBean>();
        for (DisplayItemBean displayItem : items) {
            itemBeanMap.put(displayItem.getItem().getId(), displayItem);
        }

        if (skipRules!=null) {
            for (DisplaySkipRuleBean rule : skipRules) {
                if (evaluateRule(rule)) {
                    for (Entry<ItemBean,String> assignment : rule.getAssignments()) {
                        ItemBean item=assignment.getKey();
                        logger.info(item.getName()+"="+itemBeanMap.containsKey(item));
                        if(itemBeanMap.containsKey(item.getId())){
                            DisplayItemBean dib = itemBeanMap.get(item.getId());
                            dib.setSkipped(true);
                            dib.loadFormValue(assignment.getValue());
                        }
                    }
                }
            }
        }
	}
}
