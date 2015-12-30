/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.CRFRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jxu
 * 
 * Defines a new study event
 */
public class DefineStudyEventServlet extends SecureController {

    /**
     * Checks whether the user has the correct privilege
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"));

        if (currentStudy.getParentStudyId() > 0) {
            addPageMessage(respage.getString("SED_may_only_added_top_level") + respage.getString("please_contact_sysadmin_questions"));
            throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST, resexception.getString("not_top_study"), "1");
        }

        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_persmission_add_SED_to_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.STUDY_EVENT_DEFINITION_LIST, resexception.getString("not_study_director"), "1");

    }

    /**
     * Processes the 'define study event' request
     * 
     */
    @Override
    public void processRequest() throws Exception {
        FormProcessor fpr = new FormProcessor(request);
        // logger.info("action*******" + fpr.getString("action"));
        // logger.info("pageNum*******" + fpr.getString("pageNum"));

        String action = request.getParameter("action");
        ArrayList crfsWithVersion = (ArrayList) session.getAttribute("crfsWithVersion");
        if (crfsWithVersion == null) {
            crfsWithVersion = new ArrayList();
            CRFDAO cdao = new CRFDAO(sm.getDataSource());
            CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
            ArrayList crfs = (ArrayList) cdao.findAllByStatus(Status.AVAILABLE);

            for (int i = 0; i < crfs.size(); i++) {
                CRFBean crf = (CRFBean) crfs.get(i);
                ArrayList versions = cvdao.findAllByCRFId(crf.getId());
                if (!versions.isEmpty()) {
                    crfsWithVersion.add(crf);
                }

            }
            session.setAttribute("crfsWithVersion", crfsWithVersion);
        }
        if (StringUtil.isBlank(action)) {
            StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
            sed.setStudyId(currentStudy.getId());
            session.setAttribute("definition", sed);
            session.removeAttribute("tmpCRFIdMap");
            forwardPage(Page.DEFINE_STUDY_EVENT1);
        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                confirmWholeDefinition();

            } else if ("submit".equalsIgnoreCase(action)) {
                // put a try catch in here to fix task # 1642 in Mantis, added
                // 092007 tbh
                try {
                    Integer nextAction = Integer.valueOf(request.getParameter("nextAction"));
                    if (nextAction != null) {
                        if (nextAction.intValue() == 1) {
                            session.removeAttribute("definition");
                            addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"));
                            forwardPage(Page.LIST_DEFINITION_SERVLET);
                        } else if (nextAction.intValue() == 2) {
                            submitDefinition();
                            //forwardPage(Page.LIST_DEFINITION_SERVLET);
                            ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
                            session.setAttribute("pageMessages", pageMessages);
                            response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
                            //forwardPage(Page.MANAGE_STUDY_MODULE);
//                            request.getRequestDispatcher("/pages/studymodule").forward(request, response);
//                            org.akaza.openclinica.service.sdv.SDVUtil sdvUtil = new org.akaza.openclinica.service.sdv.SDVUtil();
//                            sdvUtil.forwardRequestFromController(request,response,"http://localhost:8080/OpenClinica-SNAPSHOT/pages/studymodule");

                            //This last part is necessary because the compiler will complain about the return;
                            //statement in the absence of the "if" [the following statements are "reachable"]
//                            boolean redir = "y".equalsIgnoreCase((String)request.getParameter("r"));
//                            if(redir)  { return;}
                        } else {
                            logger.info("action ==> 3");
                            submitDefinition();
                            StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
                            sed.setStudyId(currentStudy.getId());
                            session.setAttribute("definition", sed);
                            forwardPage(Page.DEFINE_STUDY_EVENT1);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"));
                    forwardPage(Page.LIST_DEFINITION_SERVLET);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    addPageMessage(respage.getString("the_new_event_definition_creation_cancelled"));
                    forwardPage(Page.LIST_DEFINITION_SERVLET);
                }
                // above added 092007 tbh

            } else if ("next".equalsIgnoreCase(action)) {
                Integer pageNumber = Integer.valueOf(request.getParameter("pageNum"));
                if (pageNumber != null) {
                    if (pageNumber.intValue() == 2) {
                        String nextListPage = request.getParameter("next_list_page");
                        if (nextListPage != null && nextListPage.equalsIgnoreCase("true")) {
                            confirmDefinition1();
                        } else {
                            confirmDefinition2();
                        }
                    } else {
                        confirmDefinition1();
                    }
                } else {
                    if (session.getAttribute("definition") == null) {
                        StudyEventDefinitionBean sed = new StudyEventDefinitionBean();
                        sed.setStudyId(currentStudy.getId());
                        session.setAttribute("definition", sed);
                    }
                    forwardPage(Page.DEFINE_STUDY_EVENT1);
                }
            }
        }
    }

    /**
     * Validates the first section of definition inputs
     * 
     * @throws Exception
     */
    private void confirmDefinition1() throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);

        v.addValidation("name", Validator.NO_BLANKS);
        v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
        v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
        v.addValidation("category", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);

        errors = v.validate();

        session.setAttribute("definition", createStudyEventDefinition());

        if (errors.isEmpty()) {
            logger.info("no errors in the first section");
            // logger.info("action*******" + fp.getString("action"));
            // logger.info("pageNum*******" + fp.getString("pageNum"));
            CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
            ArrayList crfArray = new ArrayList();
            /*
             * The tmpCRFIdMap will hold all the selected CRFs in the session
             * when the user is navigating through the list. This has been done
             * so that when the user moves to the next page of CRF list, the
             * selection made in the previous page doesn't get lost.
             */
            Map tmpCRFIdMap = (HashMap) session.getAttribute("tmpCRFIdMap");
            if (tmpCRFIdMap == null) {
                tmpCRFIdMap = new HashMap();
            }
            ArrayList crfsWithVersion = (ArrayList) session.getAttribute("crfsWithVersion");
            for (int i = 0; i < crfsWithVersion.size(); i++) {
                int id = fp.getInt("id" + i);
                String name = fp.getString("name" + i);
                String selected = fp.getString("selected" + i);
                if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
                    tmpCRFIdMap.put(id, name);
                } else {
                    // Removing the elements from session which has been
                    // deselected.
                    if (tmpCRFIdMap.containsKey(id)) {
                        tmpCRFIdMap.remove(id);
                    }
                }
            }
            session.setAttribute("tmpCRFIdMap", tmpCRFIdMap);

            EntityBeanTable table = fp.getEntityBeanTable();
            ArrayList allRows = CRFRow.generateRowsFromBeans(crfsWithVersion);
            String[] columns =
                { resword.getString("CRF_name"), resword.getString("date_created"), resword.getString("owner"), resword.getString("date_updated"),
                    resword.getString("last_updated_by"), resword.getString("selected") };
            table.setColumns(new ArrayList(Arrays.asList(columns)));
            table.hideColumnLink(5);
            StudyEventDefinitionBean def1 = (StudyEventDefinitionBean) session.getAttribute("definition");
            HashMap args = new HashMap();
            args.put("action", "next");
            args.put("pageNum", "1");
            args.put("name", def1.getName());
            args.put("repeating", new Boolean(def1.isRepeating()).toString());
            args.put("category", def1.getCategory());
            args.put("description", def1.getDescription());
            args.put("type", def1.getType());
            table.setQuery("DefineStudyEvent", args);
            table.setRows(allRows);
            table.computeDisplay();

            request.setAttribute("table", table);
            // request.setAttribute("crfs", crfs);

            // YW <<
            forwardPage(Page.DEFINE_STUDY_EVENT2);

        } else {
            logger.info("has validation errors in the first section");
            request.setAttribute("formMessages", errors);
            forwardPage(Page.DEFINE_STUDY_EVENT1);
        }
    }

    /**
     * Validates the entire definition
     * 
     * @throws Exception
     */
    private void confirmWholeDefinition() throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
        ArrayList eventDefinitionCRFs = new ArrayList();
        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
        for (int i = 0; i < sed.getCrfs().size(); i++) {
            EventDefinitionCRFBean edcBean = new EventDefinitionCRFBean();
            int crfId = fp.getInt("crfId" + i);
            int defaultVersionId = fp.getInt("defaultVersionId" + i);
            edcBean.setCrfId(crfId);
            edcBean.setDefaultVersionId(defaultVersionId);
            CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
            edcBean.setDefaultVersionName(defaultVersion.getName());

            String crfName = fp.getString("crfName" + i);
            // String crfLabel = fp.getString("crfLabel" + i);
            edcBean.setCrfName(crfName);
            // edcBean.setCrfLabel(crfLabel);

            String requiredCRF = fp.getString("requiredCRF" + i);
            String doubleEntry = fp.getString("doubleEntry" + i);
            String decisionCondition = fp.getString("decisionCondition" + i);
            String electronicSignature = fp.getString("electronicSignature" + i);

            // issue 312 BWP<<
            String hiddenCrf = fp.getString("hiddenCrf" + i);
            // hideCRF is false by default in the bean
            if (!StringUtil.isBlank(hiddenCrf) && "yes".equalsIgnoreCase(hiddenCrf.trim())) {
                edcBean.setHideCrf(true);
            }
            // >>
            String sdvOption = fp.getString("sdvOption" + i);
            if (!StringUtil.isBlank(sdvOption)) {
                int id = Integer.valueOf(sdvOption);
                edcBean.setSourceDataVerification(SourceDataVerification.getByCode(id));
            }
            if (!StringUtil.isBlank(requiredCRF) && "yes".equalsIgnoreCase(requiredCRF.trim())) {
                edcBean.setRequiredCRF(true);
            } else {
                edcBean.setRequiredCRF(false);
            }
            if (!StringUtil.isBlank(doubleEntry) && "yes".equalsIgnoreCase(doubleEntry.trim())) {
                edcBean.setDoubleEntry(true);
            } else {
                edcBean.setDoubleEntry(false);
            }
            if (!StringUtil.isBlank(decisionCondition) && "yes".equalsIgnoreCase(decisionCondition.trim())) {
                edcBean.setDecisionCondition(true);
            } else {
                edcBean.setDecisionCondition(false);
            }

            if (!StringUtil.isBlank(electronicSignature) && "yes".equalsIgnoreCase(electronicSignature.trim())) {
                edcBean.setElectronicSignature(true);
            } else {
                edcBean.setElectronicSignature(false);
            }

            String nullString = "";
            // process null values
            ArrayList nulls = NullValue.toArrayList();
            for (int a = 0; a < nulls.size(); a++) {
                NullValue n = (NullValue) nulls.get(a);
                String myNull = fp.getString(n.getName().toLowerCase() + i);
                if (!StringUtil.isBlank(myNull) && "yes".equalsIgnoreCase(myNull.trim())) {
                    nullString = nullString + n.getName().toUpperCase() + ",";
                }

            }

            edcBean.setNullValues(nullString);
            edcBean.setStudyId(ub.getActiveStudyId());
            eventDefinitionCRFs.add(edcBean);
        }
        request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
        session.setAttribute("edCRFs", eventDefinitionCRFs);// not used on page
        forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM);

    }

    /**
     * Constructs study bean from request-first section
     * 
     * @return
     */
    private StudyEventDefinitionBean createStudyEventDefinition() {
        FormProcessor fp = new FormProcessor(request);
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
        sed.setName(fp.getString("name"));
        // YW <<
        String temp = fp.getString("repeating");
        if ("true".equalsIgnoreCase(temp) || "1".equals(temp)) {
            sed.setRepeating(true);
        } else if ("false".equalsIgnoreCase(temp) || "0".equals(temp)) {
            sed.setRepeating(false);
        }
        // YW >>
        sed.setCategory(fp.getString("category"));
        sed.setDescription(fp.getString("description"));
        sed.setType(fp.getString("type"));
        return sed;

    }

    private void confirmDefinition2() throws Exception {

        FormProcessor fp = new FormProcessor(request);
        CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
        ArrayList crfArray = new ArrayList();
        Map tmpCRFIdMap = (HashMap) session.getAttribute("tmpCRFIdMap");
        ArrayList crfsWithVersion = (ArrayList) session.getAttribute("crfsWithVersion");
        for (int i = 0; i < crfsWithVersion.size(); i++) {
            int id = fp.getInt("id" + i);
            String name = fp.getString("name" + i);
            // String label = fp.getString("label" + i);
            String selected = fp.getString("selected" + i);
            // logger.info("selected:" + selected);
            if (!StringUtil.isBlank(selected) && "yes".equalsIgnoreCase(selected.trim())) {
                logger.info("one crf selected");
                CRFBean cb = new CRFBean();
                cb.setId(id);
                cb.setName(name);

                // only find active verions
                ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
                cb.setVersions(versions);

                crfArray.add(cb);
            } else {
                if (tmpCRFIdMap.containsKey(id)) {
                    tmpCRFIdMap.remove(id);
                }
            }
        }

        for (Iterator tmpCRFIterator = tmpCRFIdMap.keySet().iterator(); tmpCRFIterator.hasNext();) {
            int id = (Integer) tmpCRFIterator.next();
            String name = (String) tmpCRFIdMap.get(id);
            boolean isExists = false;
            for (Iterator it = crfArray.iterator(); it.hasNext();) {
                CRFBean cb = (CRFBean) it.next();
                if (id == cb.getId()) {
                    isExists = true;
                }
            }
            if (!isExists) {
                CRFBean cb = new CRFBean();
                cb.setId(id);
                cb.setName(name);

                // only find active verions
                ArrayList versions = (ArrayList) vdao.findAllActiveByCRF(cb.getId());
                cb.setVersions(versions);

                crfArray.add(cb);
            }
        }
        session.removeAttribute("tmpCRFIdMap");

        if (crfArray.size() == 0) {// no crf seleted
            // addPageMessage("At least one CRF must be selected.");
            // request.setAttribute("crfs", crfs);
            addPageMessage(respage.getString("no_CRF_selected_for_definition_add_later"));
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
            sed.setCrfs(new ArrayList());
            session.setAttribute("definition", sed);
            request.setAttribute("eventDefinitionCRFs", new ArrayList());
            session.setAttribute("edCRFs", new ArrayList());// not used on page
            forwardPage(Page.DEFINE_STUDY_EVENT_CONFIRM);
            // forwardPage(Page.DEFINE_STUDY_EVENT2);

        } else {
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
            sed.setCrfs(crfArray);// crfs selected by user
            session.setAttribute("definition", sed);

            ArrayList<String> sdvOptions = new ArrayList<String>();
            sdvOptions.add(SourceDataVerification.AllREQUIRED.toString());
            sdvOptions.add(SourceDataVerification.PARTIALREQUIRED.toString());
            sdvOptions.add(SourceDataVerification.NOTREQUIRED.toString());
            sdvOptions.add(SourceDataVerification.NOTAPPLICABLE.toString());
            request.setAttribute("sdvOptions", sdvOptions);

            forwardPage(Page.DEFINE_STUDY_EVENT3);
        }
    }

    /**
     * Inserts the new study into database NullPointer catch added by tbh
     * 092007, mean to fix task #1642 in Mantis
     * 
     */
    private void submitDefinition() throws NullPointerException {
        StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
        // added tbh 092007, to catch bug # 1531
        if (sed.getName() == "" || sed.getName() == null) {
            throw new NullPointerException();
        }
        logger.info("Definition bean to be created:" + sed.getName() + sed.getStudyId());

        // fine the last one's ordinal
        ArrayList defs = edao.findAllByStudy(currentStudy);
        if (defs == null || defs.isEmpty()) {
            sed.setOrdinal(1);
        } else {
            int lastCount = defs.size() - 1;
            StudyEventDefinitionBean last = (StudyEventDefinitionBean) defs.get(lastCount);
            sed.setOrdinal(last.getOrdinal() + 1);
        }
        sed.setOwner(ub);
        sed.setCreatedDate(new Date());
        sed.setStatus(Status.AVAILABLE);
        StudyEventDefinitionBean sed1 = (StudyEventDefinitionBean) edao.create(sed);

        EventDefinitionCRFDAO cdao = new EventDefinitionCRFDAO(sm.getDataSource());
        ArrayList eventDefinitionCRFs = new ArrayList();
        if (session.getAttribute("edCRFs") != null) {
            eventDefinitionCRFs = (ArrayList) session.getAttribute("edCRFs");
        }
        for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
            EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
            edc.setOwner(ub);
            edc.setCreatedDate(new Date());
            edc.setStatus(Status.AVAILABLE);
            edc.setStudyEventDefinitionId(sed1.getId());
            edc.setOrdinal(i + 1);
            cdao.create(edc);
        }

        session.removeAttribute("definition");
        session.removeAttribute("edCRFs");
        session.removeAttribute("crfsWithVersion");

        addPageMessage(respage.getString("the_new_event_definition_created_succesfully"));

    }

}
