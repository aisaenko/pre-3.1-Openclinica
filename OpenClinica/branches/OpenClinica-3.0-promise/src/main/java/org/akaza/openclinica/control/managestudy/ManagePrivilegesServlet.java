/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.PermissionsMatrixBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.extract.CreateDatasetServlet;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.Iterator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Manage Privileges Servlet, by Tom Hickerson
 * For the Promise-PEP project, August 2009
 * @author thickerson
 * Intention is to manage Data Extract Privileges for a given Study.
 * This will involve grabbing a list of CRFs and displaying this for the end user to choose from.
 * 
 */
public class ManagePrivilegesServlet extends SecureController {
    Locale locale;
    int firstCrfId;
    StudyEventDefinitionBean firstSedBean;
    /* (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        locale = request.getLocale();

        if (ub.isSysAdmin()) {
            return;
        }

        Role r = currentRole.getRole();
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, restext.getString("not_study_director"), "1");

    }
    
    /*
     * taken from create dataset servlet, tbh 08/2009
     */
    public HashMap generateEventCRFList() throws Exception {
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        StudyBean studyWithEventDefinitions = currentStudy;
        if (currentStudy.getParentStudyId() > 0) {
            studyWithEventDefinitions = new StudyBean();
            studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

        }
        ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);

        CRFDAO crfdao = new CRFDAO(sm.getDataSource());
        HashMap events = new LinkedHashMap();
        for (int i = 0; i < seds.size(); i++) {
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
            ArrayList crfs = (ArrayList) crfdao.findAllActiveByDefinition(sed);
            if (!crfs.isEmpty()) {
                events.put(sed, crfs);
                CRFBean crf = (CRFBean) crfs.get(0);
                firstCrfId = crf.getId();
                firstSedBean = sed;
            }
        }
        return events;
    }

    /* (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#processRequest()
     */
    @Override
    protected void processRequest() throws Exception {
        // you cannot do this at the site level
        if (currentStudy.getParentStudyId() > 0) {
            addPageMessage(respage.getString("no_crf_available_study_is_a_site"));
            forwardPage(Page.MENU_SERVLET);
            return;
        }
        
        CRFDAO crfDao = new CRFDAO(sm.getDataSource());
        CRFVersionDAO crfVersionDao = new CRFVersionDAO(sm.getDataSource());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        
        ArrayList crfs = new ArrayList(); // (ArrayList) crfDao.findAll();
        
        CRFVersionBean crfVersion = new CRFVersionBean();
        CRFBean crf = new CRFBean();
        StudyEventDefinitionBean sedBean = new StudyEventDefinitionBean();
        // String crfid = request.getParameter("crfid");
        HashMap events = new LinkedHashMap();
        events = generateEventCRFList();
        
        String crfId = request.getParameter("crfId");
        String defId = request.getParameter("defId");
        if (StringUtil.isBlank(crfId) && StringUtil.isBlank(defId)) {
            crf = (CRFBean) crfDao.findByPK(firstCrfId);
            sedBean = firstSedBean;
        } else {
            int intCrfId = Integer.parseInt(crfId);
            int intDefId = Integer.parseInt(defId);
            crf = (CRFBean) crfDao.findByPK(intCrfId);
            sedBean = (StudyEventDefinitionBean) seddao.findByPK(intDefId);
        }
        
        // we will have to grab all the items for the CRF and generate rows from them
        ItemDAO itemDao = new ItemDAO(sm.getDataSource());
        ItemFormMetadataDAO metadataDao = new ItemFormMetadataDAO(sm.getDataSource());
        ArrayList items = itemDao.findAllActiveByCRF(crf);
        for (int i = 0; i < items.size(); i++) {
            ItemBean item = (ItemBean) items.get(i);
            /*
             * logger.info("testing on item id "+ item.getId()+ " crf
             * version id "+ item.getItemMeta().getCrfVersionId());
             */
            ItemFormMetadataBean meta = metadataDao.findByItemIdAndCRFVersionId(item.getId(), item.getItemMeta().getCrfVersionId());
            // TODO change the above data access function, tbh
            // ArrayList metas = imfdao.findAllByItemId(item.getId());
            meta.setCrfVersionName(item.getItemMeta().getCrfVersionName());
            // logger.info("crf versionname" + meta.getCrfVersionName());
            item.getItemMetas().add(meta);
            // item.setItemMetas(metas);
        }
        HashMap itemMap = new HashMap();
        for (int i = 0; i < items.size(); i++) {
            ItemBean item = (ItemBean) items.get(i);

            if (!itemMap.containsKey(defId + "_" + item.getId())) {
//                if (db.getItemMap().containsKey(defId + "_" + item.getId())) {
//                    item.setSelected(true);
//                    item.setDatasetItemMapKey(defId + "_" + item.getId());
//                    // logger.info("Item got selected already11");
//                }
                // itemMap.put(new Integer(item.getId()), item);
                itemMap.put(defId + "_" + item.getId(), item);
            } else {
                // same item,combine the metadata
                ItemBean uniqueItem = (ItemBean) itemMap.get(defId + "_" + item.getId());
                uniqueItem.getItemMetas().add(item.getItemMetas().get(0));
//                if (db.getItemMap().containsKey(defId + "_" + uniqueItem.getId())) {
//                    uniqueItem.setSelected(true);
//                    // logger.info("Item got selected already22");
//                }
            }
            // how to check if we already have items/crfs/seds covered here?
            // we will need to parse the sql statement, or do something
            // equally inventive, tbh
            // maybe an additional map of datasets to seds?
            // already done for us in datasetDAO.initialDatasetData(int id)
            // db.getEventIds() should have all the def ids
            // so then we can 'load' alerts into the system.
            // tbh, 08/2009
        }
        ArrayList itemArray = new ArrayList(itemMap.values());
        Collections.sort(itemArray);
        PermissionsMatrixBean permissions = new PermissionsMatrixBean();
        FilterDAO filterDao = new FilterDAO(sm.getDataSource());
        permissions = filterDao.findPermissionsByStudy(currentStudy);
        request.setAttribute("permissions", permissions);
        // trying to get the events to show, tbh
        request.setAttribute("eventlist", events);

        session.setAttribute(CreateDatasetServlet.EVENTS_FOR_CREATE_DATASET, events);
        // request.setAttribute("crfs", crfs);
        request.setAttribute("crf", crf);
        // request.setAttribute("crfVersion", crfVersion);
        request.setAttribute("items", itemArray); // in the place of items
        
        request.setAttribute("roles", Role.list);
        
        request.setAttribute("def", sedBean);
        request.setAttribute("ManagePrivileges", true);
        // << tbh 01/2010 to make sure a link does not show
        forwardPage(Page.MANAGE_PRIVILEGES);
    }

}
