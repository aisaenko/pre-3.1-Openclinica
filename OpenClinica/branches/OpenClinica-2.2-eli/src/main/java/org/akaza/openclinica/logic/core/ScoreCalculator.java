/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 

package org.akaza.openclinica.logic.core;

/**
 * The ScoreCalculator acts as the Controller for scoring. Tasks performed as following:
 * 
 * 1) Find out all items with "calculation" control type.
 * 2) Parse the actual formula using the <code>Parser</code>.
 * 3) According to the parser, picks the right function and instantiate this function.
 * 4) Performs the actual calculation using the function.
 * 
 * 
 * @author Hailong Wang, Ph.D
 * @version 1.0 08/25/2006
 * 
 * @author ywang
 * <br>
 * <p>Modified for OpenClinica version 2.2 to enable scoring for group items. 
 * For this purpose, class structure has been modified. (ywang, Jan. 2008)
 * 
 */

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.TreeSet;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.OpenClinicaException;

public class ScoreCalculator {
	private Logger logger = Logger.getLogger(getClass().getName());

	private SessionManager sm;

	private EventCRFBean ecb;

	private UserAccountBean ub;
	
	private ArrayList<String> errors = new ArrayList<String>();
	
	public ScoreCalculator(SessionManager sm, EventCRFBean ecb,
			UserAccountBean ub) {
		this.sm = sm;
		this.ecb = ecb;
		this.ub = ub;
	}

	/*
	 * Perform all calculations in a CRFVersion. The parameter 'itemdata' might be overwritten.
	 * 
	 * @param calcItemGroupSizes
	 * @param items
	 * @param itemdata
	 * @param itemGroupSizes
	 * @param databaseErrors
	 * @return ArrayList<String> which records left_item_text of items who failed to be updated into database.
	 * 
	 
	 //this method has not been fully tested yet
	  
	  
	public ArrayList<String> doCalculations(
			HashMap<String,ItemBean> items, HashMap<String,String> itemdata, 
			HashMap<Integer,TreeSet<Integer>> itemOrdinals) {
		ArrayList<String> updateFailedItems = new ArrayList<String>();
 		if(itemdata==null) {
 			//here, actually only record errors but doesn't return errors.
			logger.severe("In scoreCalculator doCalculations(), items are empty!");
 			errors.add("Calculation cannot be started because needed items are empty!");
 			return updateFailedItems;
 		}
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
		ItemDAO idao = new ItemDAO(sm.getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

		NumberFormat nf = NumberFormat.getInstance();
		Parser parser = new Parser(items,itemdata);
		try {
			//for calculation type
			List<ItemFormMetadataBean> derivedItemList = ifmdao.findAllByCRFVersionIdAndResponseTypeId(
							ecb.getCRFVersionId(), ResponseType.CALCULATION.getId());
			if(derivedItemList.size()>0) {
				Collections.sort(derivedItemList);
				for (ItemFormMetadataBean ifmb: derivedItemList) {
					ItemBean ib = (ItemBean)idao.findByPK(ifmb.getItemId());
					ResponseOptionBean rob = (ResponseOptionBean)ifmb.getResponseSet().getOptions().get(0);
					//YW, 1-16-2008, << enable: 1. evaluate combination of expression and functions 2. scoring for group items.
					//int groupsize = iddao.getMaxOrdinalForGroup(ecb, (SectionBean)sdao.findByPK(ifmb.getSectionId()), 
					//		(ItemGroupBean)igdao.findByName(ifmb.getGroupLabel()));
					int groupsize = 1;
					if(itemOrdinals.containsKey(ib.getId())) {
						groupsize = (itemOrdinals.get(ib.getId())).size();
					}
					String value = "";
					String parsedExp = "";
					for(int i=0; i<groupsize; ++i) {
						ItemDataBean idb = (ItemDataBean)iddao.findByItemIdAndEventCRFIdAndOrdinal(ifmb
								.getItemId(), ecb.getId(), i+1);
						StringBuffer err = new StringBuffer();
						parsedExp = parser.parse(rob.getValue(), i+1);
						if(parser.getErrors().length()>0) {
							err.append(parser.getErrors());
							parser.setErrors(new StringBuffer());
						} else {
							value = ScoreUtil.eval(parsedExp, err);
					//YW >>
							String exp = rob.getValue();
							exp = exp.replace("##", ",");
							if(writeToDB(ib,idb,exp,value,err)) {
								itemdata.put(ib.getId()+"_"+(i+1), idb.getValue());
							}else {
								String key = (i+1)>1 ? ifmb.getLeftItemText()+"_"+(i+1) : ifmb.getLeftItemText();
								updateFailedItems.add(key);
							}
						}
						if(err.length()>0) {
							String key = (i+1)>1 ? ifmb.getLeftItemText()+"_"+(i+1) : ifmb.getLeftItemText();
							errors.add("Item " + key + " contains calculation errors: " +  err.toString());
						}
					}
				}
			}
			
			//YW, 1-16-2008, for group-calculation type. Current restrictions:
			//1. an item with group-calculation type is not repeatable.
			//2. only calculate sum(), avg(), min(), max(), median(), stdev()
			//3. formula arguments only contain item beans
			//4. only one item bean per argument
			List<ItemFormMetadataBean> itemList = ifmdao.findAllByCRFVersionIdAndResponseTypeId(
								ecb.getCRFVersionId(), ResponseType.GROUP_CALCULATION.getId());
			if(itemList.size()>0) {
				Collections.sort(itemList);
				for (ItemFormMetadataBean ifmb: itemList) {
					ItemBean ib = (ItemBean)idao.findByPK(ifmb.getItemId());
					ResponseOptionBean rob = (ResponseOptionBean)ifmb.getResponseSet().getOptions().get(0);
					StringBuffer err = new StringBuffer();
					parser.setErrors(err);
					String parsedExp = parser.parse(rob.getValue(),itemOrdinals);
					String value = "";
					if(parser.getErrors().length()>0) {
						err.append(parser.getErrors());
					}else {
						value = ScoreUtil.eval(parsedExp,err);
					
						ItemDataBean idb = (ItemDataBean)iddao.findByItemIdAndEventCRFIdAndOrdinal(ifmb
								.getItemId(), ecb.getId(), 1);
						String exp = rob.getValue();
						exp = exp.replace("##", ",");
						if(writeToDB(ib,idb,exp,value,err)) {
							itemdata.put(ib.getId()+"_"+idb.getOrdinal(), idb.getValue());
						} else {
							updateFailedItems.add(ifmb.getLeftItemText());
						}
					}

					if(err.length()>0) {
						errors.add("Item " + ifmb.getLeftItemText() + " contains calculation errors: " + err.toString());
					}
				}
			}
		} catch (OpenClinicaException e) {
			logger.severe(e.getMessage());
		}
        //ecb.setNeedsRecalc(false);
        //EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        //ecb = ecdao.update(ecb);
		
		return updateFailedItems;
	}
	*/
	
	/**
	 * Re-do calculations if func included item(s) changed. Re-calculated funcs are not included in the section.
	 * If calculation can not sucessfully redo, old value will be erased and "<erased>" will be saved in database. 
	 * <br>
	 * The parameter 'itemdata' might be overwritten.
	 * 
	 * @param itemGroupSizes
	 * @param items
	 * @param itemdata
	 * @param oldItemdata
	 * @param updatedData
	 * @param sectionId
	 * @return ArrayList<String> which records left_item_text of items who failed to be updated into database.
	 */
	public ArrayList<String> redoCalculations(HashMap<String,ItemBean> items, HashMap<String,String> itemdata,
			TreeSet<String> changedItems, HashMap<Integer,TreeSet<Integer>> itemOrdinals, int sectionId) {
		ArrayList<String> updateFailedItems = new ArrayList<String>();
 		if(itemdata==null) {
			logger.severe("In ScoreCalculator redoCalculations(), itemdata is empty!");
			errors.add("In ScoreCalculator redoCalculations(), 'itemdata' map is empty!");
 			return updateFailedItems;
 		}
 		if(changedItems==null) {
			logger.severe("In ScoreCalculator redoCalculations(), 'changeItems' set is empty!");
			errors.add("In ScoreCalculator redoCalculations(), 'changeItems' set is empty!");
 			return updateFailedItems;
 		}
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
		ItemDAO idao = new ItemDAO(sm.getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

		NumberFormat nf = NumberFormat.getInstance();
		Parser parser = new Parser(items,itemdata);
		try {
			//for calculation type
			List<ItemFormMetadataBean> derivedItemList = ifmdao.findAllByCRFVersionIdAndResponseTypeId(
							ecb.getCRFVersionId(), ResponseType.CALCULATION.getId());
			if(derivedItemList.size()>0) {
				Collections.sort(derivedItemList);
				for (ItemFormMetadataBean ifmb: derivedItemList) {
					if(ifmb.getSectionId()!=sectionId) {
						ItemBean ib = (ItemBean)idao.findByPK(ifmb.getItemId());
						ResponseOptionBean rob = (ResponseOptionBean)ifmb.getResponseSet().getOptions().get(0);
						int groupsize = 1;
						if(itemOrdinals.containsKey(ib.getId())) {
							groupsize = itemOrdinals.get(ib.getId()).size();
						}
						String value = "";
						String parsedExp = "";
						for(int i=0; i<groupsize; ++i) {
							ItemDataBean idb = (ItemDataBean)iddao.findByItemIdAndEventCRFIdAndOrdinal(ifmb
									.getItemId(), ecb.getId(), i+1);
							//is there any changed item
							Parser p = new Parser(items,itemdata);
							if(p.isChanged(changedItems, rob.getValue(), i+1)) {
								StringBuffer err = new StringBuffer();
								parsedExp = parser.parse(rob.getValue(), i+1);
								//if parser has error and has been calculated before, set "<erased>" 
								if(parser.getErrors().length()>0) {
									err.append(parser.getErrors());
									if(idb.isActive()) {
										idb.setValue("<erased>");
										idb.setStatus(Status.UNAVAILABLE);
										idb = (ItemDataBean)iddao.update(idb);
										if(!idb.isActive()) {
											String key = (i+1)>1 ? ifmb.getLeftItemText()+"_"+(i+1) : ifmb.getLeftItemText();
											updateFailedItems.add(key);
										}
									}
									parser.setErrors(new StringBuffer());
								}
								//otherwise do calculation
								else {
									value = ScoreUtil.eval(parsedExp, err);
									
									String exp = rob.getValue();
									exp = exp.replace("##", ",");
									if(writeToDB(ib,idb,exp,value,err)) {
										changedItems.add(ib.getName());
										itemdata.put(ib.getId()+"_"+(i+1), idb.getValue());
									} else {
										String key = (i+1)>1 ? ifmb.getLeftItemText()+"_"+(i+1) : ifmb.getLeftItemText();
										updateFailedItems.add(key);
									}
								}
								if(err.length()>0) {
									String key = (i+1)>1 ? ifmb.getLeftItemText()+"_"+(i+1) : ifmb.getLeftItemText();
									errors.add("Item " + key + " contains calculation errors: " +  err.toString());
								}
							}
						}
					}
				}
			}
			
			
			List<ItemFormMetadataBean> itemList = ifmdao.findAllByCRFVersionIdAndResponseTypeId(
								ecb.getCRFVersionId(), ResponseType.GROUP_CALCULATION.getId());
			if(itemList.size()>0) {
				Collections.sort(itemList);
				for (ItemFormMetadataBean ifmb: itemList) {
					if(ifmb.getSectionId()!=sectionId) {
						ItemBean ib = (ItemBean)idao.findByPK(ifmb.getItemId());
						ResponseOptionBean rob = (ResponseOptionBean)ifmb.getResponseSet().getOptions().get(0);
						String value = "";
						Parser p = new Parser(items, itemdata);
						if(p.isChanged(changedItems, rob.getValue())) {
							StringBuffer err = new StringBuffer();
							parser.setErrors(err);
							String parsedExp = parser.parse(rob.getValue(),itemOrdinals);
							ItemDataBean idb = (ItemDataBean)iddao.findByItemIdAndEventCRFIdAndOrdinal(ifmb
									.getItemId(), ecb.getId(), 1);
							if(parser.getErrors().length()>0) {
								err.append(parser.getErrors());
								if(idb.isActive()) {
									idb.setValue("<erased>");
									idb.setStatus(Status.UNAVAILABLE);
									idb = (ItemDataBean)iddao.update(idb);
									if(!idb.isActive()) {
										updateFailedItems.add(ifmb.getLeftItemText());
									}
								}
							} else {
								value = ScoreUtil.eval(parsedExp,err);

								String exp = rob.getValue();
								exp = exp.replace("##", ",");
								if(writeToDB(ib,idb,exp,value,err)) {
									changedItems.add(ib.getName());
									itemdata.put(ib.getId()+"_"+idb.getOrdinal(), idb.getValue());
								}else {
									updateFailedItems.add(ifmb.getLeftItemText());
								}
								
							}	
							if(err.length()>0) {
								errors.add("Item " + ifmb.getLeftItemText() + " contains calculation errors: " + err.toString());
							}
						}
					}
				}
			}
		} catch (OpenClinicaException e) {
			logger.severe(e.getMessage());
		}
		
		return updateFailedItems;
	}
	
	protected boolean writeToDB(ItemBean ib, ItemDataBean idb, String exp, String value, StringBuffer err) {
		ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
		NumberFormat nf = NumberFormat.getInstance();
		
		if (idb == null) {
			idb = new ItemDataBean();
		}

		ItemDataType idt = ib.getDataType();
		if (value==null || value.length() == 0) {
			if(idb.isActive() && !"".equals(idb.getValue())) {
				idb.setValue("<erased>");
			} else {
				idb.setValue("");
			}
			err.append("Result is empty in" +" " + exp + "; ");
		} else if (idt.equals(ItemDataType.INTEGER)) {
			try {
				idb.setValue(Integer.toString(nf.parse(value)
						.intValue()));
				idb.setValue(value);
			} catch (ParseException e) {
				if(idb.isActive() && !"".equals(idb.getValue())) {
					idb.setValue("<erased>");
				} else {
					idb.setValue("");
				}
				logger.severe("Number was expected in "
						+ exp + " : " + value);
				err.append("Number was expected in" +" " + exp + " : " + value + "; ");
				}
		}else if(idt.equals(ItemDataType.REAL)) {
			try{
				int scale = BigDecimal.valueOf((Double.parseDouble(value))).scale();
				if(scale>=4 && Math.abs(Double.parseDouble(value))>=0.01) {
					scale = 4;
					value = ""+new BigDecimal((Double.parseDouble(value))).setScale(scale,BigDecimal.ROUND_HALF_UP);
				}
				idb.setValue(value);
			} catch(Exception ee) {
				if(idb.isActive() && !"".equals(idb.getValue())) {
					idb.setValue("<erased>");
				} else {
					idb.setValue("");
				}
				logger.severe("Number was expected in" +" "+ exp + " : " + value);
				err.append("Number was expected in" + " "	+ exp + " : " + value + "; ");
				}
		} else {
			idb.setValue(value);
		}
		
		idb.setStatus(Status.UNAVAILABLE);
		//idb.setNeedsRecalc(false);
		if (!idb.isActive()) {
			// will this need to change for double data entry?
			idb.setCreatedDate(new Date());
			idb.setOwner(ub);
			idb.setItemId(ib.getId());
			idb.setEventCRFId(ecb.getId());

			idb = (ItemDataBean)iddao.create(idb);
		} else {
			idb = (ItemDataBean)iddao.update(idb);
			}
		
		return idb.isActive();
		
	}
	

}
