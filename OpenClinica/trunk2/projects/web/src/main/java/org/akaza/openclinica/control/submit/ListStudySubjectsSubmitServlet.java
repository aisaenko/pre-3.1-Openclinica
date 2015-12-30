/*
 * Created on Jan 21, 2005
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.managestudy.ListStudySubjectServlet;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * @author ssachs
 */
public class ListStudySubjectsSubmitServlet extends ListStudySubjectServlet {

    Locale locale;

    // < ResourceBundleresexception,respage;

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getJSP()
     */
    @Override
    protected Page getJSP() {
        return Page.SUBMIT_DATA;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getBaseURL()
     */
    @Override
    protected String getBaseURL() {
        return "ListStudySubjectsSubmit";
    }
}
