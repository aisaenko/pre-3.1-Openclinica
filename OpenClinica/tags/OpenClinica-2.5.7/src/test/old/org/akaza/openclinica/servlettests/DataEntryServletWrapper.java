package org.akaza.openclinica.servlettests;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.submit.DataEntryServlet;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A wrapper class designed to allow the implementation of a JUnit test case for
 * abstract DataEntryServlet.
 */
public class DataEntryServletWrapper extends DataEntryServlet {

    @Override
    protected boolean validateInputOnFirstRound() {
        return false;
    }

    @Override
    protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName) {
        return new DisplayItemBean();
    }

    @Override
    protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v, DisplayItemGroupBean dib, List<DisplayItemGroupBean> digbs,
            List<DisplayItemGroupBean> formGroups) {

        return new ArrayList<DisplayItemGroupBean>();
    }

    @Override
    protected Status getBlankItemStatus() {
        return Status.INVALID;
    }

    @Override
    protected Status getNonBlankItemStatus() {
        return Status.INVALID;
    }

    @Override
    protected String getEventCRFAnnotations() {
        return "";
    }

    @Override
    protected void setEventCRFAnnotations(String annotations) {

    }

    @Override
    protected Page getJSPPage() {
        // return any page
        return Page.MENU_SERVLET;
    }

    @Override
    protected Page getServletPage() {
        // return a data entry page
        return Page.INITIAL_DATA_ENTRY_NW;
    }

    @Override
    protected boolean shouldLoadDBValues(DisplayItemBean dib) {
        return false;
    }

    @Override
    protected void mayProceed() throws InsufficientPermissionException {

    }

    // For out of container testing...
    public void setRequest(HttpServletRequest _request) {
        this.request = _request;
    }

    public void setResponse(HttpServletResponse _response) {
        this.response = _response;
    }

    public void setSessionManager(SessionManager manager) {
        this.sm = manager;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public void setStudy(StudyBean studyBean) {
        this.currentStudy = studyBean;
    }

    public void setEventCRFBean(EventCRFBean bean) {
        this.ecb = bean;
    }

    public void setFormProcessor(FormProcessor processor) {
        this.fp = processor;
    }

    public void initializeMembers(Locale locale) {
        resadmin = ResourceBundleProvider.getAdminBundle(locale);
        resaudit = ResourceBundleProvider.getAuditEventsBundle(locale);
        resexception = ResourceBundleProvider.getExceptionsBundle(locale);
        resformat = ResourceBundleProvider.getFormatBundle(locale);
        restext = ResourceBundleProvider.getTextsBundle(locale);
        resterm = ResourceBundleProvider.getTermsBundle(locale);
        resword = ResourceBundleProvider.getWordsBundle(locale);
        respage = ResourceBundleProvider.getPageMessagesBundle(locale);
        resworkflow = ResourceBundleProvider.getWorkflowBundle(locale);

        local_df = new SimpleDateFormat(resformat.getString("date_format_string"));
    }

    public void processRequestWrapper() throws Exception {
        super.processRequest();
    }

    @Override
    protected boolean shouldRunRules() {
        return false;
    }

}
