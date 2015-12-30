/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * View the uploaded data and verify what is going to be saved into the system
 * and what is not.
 * 
 * @author Krikor Krumlian
 */
public class VerifyImportedCRFDataServlet extends SecureController {

    Locale locale;

    /**
     * 
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        if (ub.isSysAdmin()) {
            return;
        }

        Role r = currentRole.getRole();
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR) || r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    public void createDiscrepancyNote(ItemBean itemBean, String message, EventCRFBean eventCrfBean, DisplayItemBean displayItemBean) {
        // DisplayItemBean displayItemBean) {
        DiscrepancyNoteBean note = new DiscrepancyNoteBean();
        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        note.setDescription(message);
        note.setDetailedNotes("Failed Validation Check");
        note.setOwner(ub);
        note.setCreatedDate(new Date());
        note.setResolutionStatusId(ResolutionStatus.OPEN.getId());
        note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.FAILEDVAL.getId());
        // note.setParentDnId(parentId);

        note.setField(itemBean.getName());
        note.setStudyId(currentStudy.getId());
        note.setEntityName(itemBean.getName());
        note.setEntityType("ItemData");
        note.setEntityValue(displayItemBean.getData().getValue());

        note.setEventName(eventCrfBean.getName());
        note.setEventStart(eventCrfBean.getCreatedDate());
        note.setCrfName(displayItemBean.getEventDefinitionCRF().getCrfName());

        StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(eventCrfBean.getStudySubjectId());
        note.setSubjectName(ss.getName());

        note.setEntityId(displayItemBean.getData().getId());
        note.setColumn("value");

        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        note = (DiscrepancyNoteBean) dndao.create(note);
        // so that the below method works, need to set the entity above
        logger.info("trying to create mapping with " + note.getId() + " " + note.getEntityId() + " " + note.getColumn() + " " + note.getEntityType());
        dndao.createMapping(note);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void processRequest() throws Exception {
        ItemDataDAO itemDataDao = new ItemDataDAO(sm.getDataSource());
        EventCRFDAO eventCrfDao = new EventCRFDAO(sm.getDataSource());
        CrfBusinessLogicHelper crfBusinessLogicHelper = new CrfBusinessLogicHelper(sm);
        String action = request.getParameter("action");

        FormProcessor fp = new FormProcessor(request);

        // checks which module the requests are from
        String module = fp.getString(MODULE);
        request.setAttribute(MODULE, module);

        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);

        setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"));

        setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"));
        setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"));
        setToPanel(resword.getString("CRF_spreadsheet_template"), respage.getString("br_download_blank_CRF_spreadsheet_from"));
        setToPanel(resword.getString("example_CRF_br_spreadsheets"), respage.getString("br_download_example_CRF_instructions_from"));

        if ("confirm".equalsIgnoreCase(action)) {
            List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) session.getAttribute("importedData");
            logger.info("Size of displayItemBeanWrappers : " + displayItemBeanWrappers.size());
            forwardPage(Page.VERIFY_IMPORT_CRF_DATA);
        }

        if ("save".equalsIgnoreCase(action)) {
            List<DisplayItemBeanWrapper> displayItemBeanWrappers = (List<DisplayItemBeanWrapper>) session.getAttribute("importedData");
            logger.info("Size of displayItemBeanWrappers : " + displayItemBeanWrappers.size());

            for (DisplayItemBeanWrapper wrapper : displayItemBeanWrappers) {

                int eventCrfBeanId = -1;
                EventCRFBean eventCrfBean = new EventCRFBean();

                // TODO : tom , the wrapper object has all the necessary data -
                // as you see we check the
                // is to see if this data is Savable if it is then we go ahead
                // and save it. if not we discard.
                // So the change needs to happen here , instead of discarding we
                // need to file discrepancy notes
                // and save the data. If you look in the
                // Page.VERIFY_IMPORT_CRF_DATA jsp file you can see how I am
                // pulling the errors. and use that in the same way.

                logger.info("right before we check to make sure it is savable: " + wrapper.isSavable());
                if (wrapper.isSavable()) {
                    // based on the use case: "If any of the data does not meet
                    // validations specified in the CRF
                    // Template, a discrepancy note is automatically logged.
                    // The DN will have a type of Failed Validation Check, and
                    // a message of Failed Validation check."
                    logger.info("wrapper problems found : " + wrapper.getValidationErrors().toString());
                    for (DisplayItemBean displayItemBean : wrapper.getDisplayItemBeans()) {
                        eventCrfBeanId = displayItemBean.getData().getEventCRFId();
                        eventCrfBean = (EventCRFBean) eventCrfDao.findByPK(eventCrfBeanId);
                        logger.info("found value here: " + displayItemBean.getData().getValue());
                        logger.info("found status here: " + eventCrfBean.getStatus().getName());
                        if (wrapper.isOverwrite()) {
                            ItemDataBean itemDataBean = new ItemDataBean();
                            itemDataBean = itemDataDao.findByEventCRFIdAndItemName(eventCrfBean, displayItemBean.getItem().getName());
                            itemDataBean.setUpdatedDate(new Date());
                            itemDataBean.setUpdater(ub);
                            itemDataBean.setValue(displayItemBean.getData().getValue());
                            // set status?
                            itemDataDao.update(itemDataBean);
                            logger.info("updated: " + itemDataBean.getItemId());
                            // need to set pk here in order to create dn
                            displayItemBean.getData().setId(itemDataBean.getId());
                        } else {
                            itemDataDao.create(displayItemBean.getData());
                            logger.info("created: " + displayItemBean.getData().getItemId());
                            // does this dao function work for repeating
                            // events/groups?
                            ItemDataBean itemDataBean = itemDataDao.findByEventCRFIdAndItemName(eventCrfBean, displayItemBean.getItem().getName());
                            logger.info("found: " + itemDataBean.getId());
                            displayItemBean.getData().setId(itemDataBean.getId());
                        }
                        // logger.info("created item data bean:
                        // "+displayItemBean.getData().getId());
                        // logger.info("created:
                        // "+displayItemBean.getData().getName());
                        // logger.info("continued:
                        // "+displayItemBean.getData().getItemId());
                        ItemDAO idao = new ItemDAO(sm.getDataSource());
                        ItemBean ibean = (ItemBean) idao.findByPK(displayItemBean.getData().getItemId());
                        // logger.info("continued2: getName " +
                        // ibean.getName());
                        if (wrapper.getValidationErrors().containsKey(ibean.getName())) {
                            ArrayList messageList = (ArrayList) wrapper.getValidationErrors().get(ibean.getName());
                            // could be more then one will have to iterate
                            for (int iter = 0; iter < messageList.size(); iter++) {
                                String message = (String) messageList.get(iter);
                                createDiscrepancyNote(ibean, message, eventCrfBean, displayItemBean);
                                // displayItemBean);
                            }
                        }
                        // logger.info("created:
                        // "+displayItemBean.getDbData().getName());
                    }

                    crfBusinessLogicHelper.markCRFComplete(eventCrfBean, ub);
                }

            }

            addPageMessage("Your Event CRFs have been created and updated in the database.  Please review them now.");
            forwardPage(Page.SUBMIT_DATA_SERVLET);
        }
    }
}