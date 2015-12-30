package org.akaza.openclinica.logic.core;

/**
 * The ScoreCalculator acts as the Controller for scoring. Tasks performed as following:
 * 
 * 1) Find out all items with "calculation" control type.
 * 2) Parse the actual formula using the <code>Parser</code>.
 * 3) According to the parser, picks the right function and instantiate this function.
 *    Currently only decode and sum are supported.
 * 4) Performs the actual calculation using the function.
 * 
 * @author Hailong Wang, Ph.D
 * @version 1.0 08/25/2006
 */

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.logic.core.function.Age;
import org.akaza.openclinica.logic.core.function.Function;
import org.akaza.openclinica.logic.core.function.LookupTB;

public class ScoreCalculator {
	private Logger logger = Logger.getLogger(getClass().getName());

	private SessionManager sm;

	private EventCRFBean ecb;

	private UserAccountBean ub;

	//A map from item name to item bean object.
	private HashMap<String, ItemBean> items;

	private HashMap<Integer, String> itemdata;

	public ScoreCalculator(SessionManager sm, EventCRFBean ecb,
			UserAccountBean ub) {
		this.sm = sm;
		this.ecb = ecb;
		this.ub = ub;
		items = new HashMap<String, ItemBean>(100);

		ItemDAO idao = new ItemDAO(sm.getDataSource());
		List itemList = idao.findAllItemsByVersionId(ecb.getCRFVersionId());
		for (int i = 0; i < itemList.size(); i++) {
			ItemBean ib = (ItemBean) itemList.get(i);
			items.put(ib.getName(), ib);
		}
		itemdata = new HashMap<Integer, String>();
		ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
		List<ItemDataBean> itemDataList = iddao
				.findAllByEventCRFId(ecb.getId());
		for (ItemDataBean bean : itemDataList) {
			itemdata.put(bean.getItemId(), bean.getValue());
		}
	}

	/**
	 * Performs the actual calculation.
	 */
	public void doCalculation() {
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
		ItemDAO idao = new ItemDAO(sm.getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

		NumberFormat nf = NumberFormat.getInstance();
		Parser parser = new Parser(items);
		try {
			List<ItemFormMetadataBean> derivedItemList = (List<ItemFormMetadataBean>)ifmdao
					.findAllByCRFVersionIdAndResponseTypeId(ecb
							.getCRFVersionId(), ResponseType.CALCULATION
							.getId());
			Collections.sort(derivedItemList);
			for (ItemFormMetadataBean ifmb: derivedItemList) {
				ItemBean ib = (ItemBean) idao.findByPK(ifmb.getItemId());
				ResponseOptionBean rob = (ResponseOptionBean) ifmb
						.getResponseSet().getOptions().get(0);

				Function f = (Function) parser.parse(rob.getValue());
				if (f instanceof Age) {
					StudySubjectDAO ssdao = new StudySubjectDAO(sm
							.getDataSource());
					SubjectDAO subjectDao = new SubjectDAO(sm.getDataSource());
					StudySubjectBean ssb = (StudySubjectBean) ssdao
							.findByPK(ecb.getStudySubjectId());
					SubjectBean subject = (SubjectBean) subjectDao.findByPK(ssb
							.getSubjectId());					
					f.addArgument(subject.getDateOfBirth());
					f.addArgument(ecb.getDateInterviewed());
				}else
				if (f instanceof LookupTB) {				
					f.addArgument(sm);
					f.addArgument(ecb);		
					f.addArgument(rob.getValue());
				}
				f.execute(itemdata);
				String value = f.getValue();
				ItemDataBean idb = iddao.findByItemIdAndEventCRFId(ifmb
						.getItemId(), ecb.getId());
				if (idb == null) {
					idb = new ItemDataBean();
				}

				ItemDataType idt = ib.getDataType();
				if (value.length() == 0) {
					idb.setValue(value);
				} else if (idt.equals(ItemDataType.INTEGER)) {
					try {
						idb.setValue(Integer.toString(nf.parse(value)
								.intValue()));
					} catch (ParseException e) {
						idb.setValue("");
						logger.severe("Number was expected in "
								+ rob.getValue() + " : " + value);
						continue;
					}
				} else {
					idb.setValue(value);
				}

				idb.setStatus(Status.UNAVAILABLE);
				if (!idb.isActive()) {
					// will this need to change for double data entry?
					idb.setCreatedDate(new Date());
					idb.setOwner(ub);
					idb.setItemId(ifmb.getItemId());
					idb.setEventCRFId(ecb.getId());

					idb = (ItemDataBean) iddao.create(idb);
				} else {
					idb = (ItemDataBean) iddao.update(idb);
				}

				itemdata.put(ib.getId(), value);
			}
		} catch (OpenClinicaException e) {
			logger.severe(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void eraseCalculations() {
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
		ItemDAO idao = new ItemDAO(sm.getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

		try {
			List<ItemFormMetadataBean> derivedItemList = (List<ItemFormMetadataBean>)ifmdao
					.findAllByCRFVersionIdAndResponseTypeId(ecb
							.getCRFVersionId(), ResponseType.CALCULATION
							.getId());
			Collections.sort(derivedItemList);
			for (ItemFormMetadataBean ifmb: derivedItemList) {
				ItemBean ib = (ItemBean) idao.findByPK(ifmb.getItemId());
				ItemDataBean idb = iddao.findByItemIdAndEventCRFId(ifmb
						.getItemId(), ecb.getId());
				if (idb == null) {
					idb = new ItemDataBean();
				}

				String value="";
				idb.setValue(value);

				idb.setStatus(Status.UNAVAILABLE);
				if (!idb.isActive()) {
					// will this need to change for double data entry?
					idb.setCreatedDate(new Date());
					idb.setOwner(ub);
					idb.setItemId(ifmb.getItemId());
					idb.setEventCRFId(ecb.getId());

					idb = (ItemDataBean) iddao.create(idb);
				} else {
					idb = (ItemDataBean) iddao.update(idb);
				}

				itemdata.put(ib.getId(), value);

			}
		} catch (OpenClinicaException e) {
			logger.severe(e.getMessage());
		}
	}
}
