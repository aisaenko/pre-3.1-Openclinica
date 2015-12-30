/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * @author ssachs
 */
public class ListStudySubjectsManageServlet extends ListStudySubjectServlet {

    Locale locale;

    // < ResourceBundleresexception,respage;

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getJSP()
     */
    @Override
    protected Page getJSP() {
        return Page.LIST_STUDY_SUBJECT;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.managestudy.ListStudySubjectServlet#getBaseURL()
     */
    @Override
    protected String getBaseURL() {
        return "ListStudySubjects";
    }
}
