/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ViewStudyUserServlet extends SecureController {
    @Override
    public void processRequest() throws Exception {
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        String name = request.getParameter("name");
        String studyIdString = request.getParameter("studyId");
       
     //if(request.getParameter("submit")!=null)
     {     
    	 
    	 if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
            addPageMessage(respage.getString("please_choose_a_user_to_view"));
            forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
     	} else {
            int studyId = Integer.valueOf(studyIdString.trim()).intValue();
            UserAccountBean user = (UserAccountBean) udao.findByUserName(name);

            request.setAttribute("user", user);

            StudyUserRoleBean uRole = udao.findRoleByUserNameAndStudyId(name, studyId);
            request.setAttribute("uRole", uRole);

            StudyDAO sdao = new StudyDAO(sm.getDataSource());
            StudyBean study = (StudyBean) sdao.findByPK(studyId);
            request.setAttribute("uStudy", study);
            request.setAttribute("siteRoleMap", Role.siteRoleMap);
            // BWP 12/7/07 >>To provide the view with the correct date format
            // pattern, locale sensitive
            String pattn = "";
            pattn = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
            request.setAttribute("dateFormatPattern", pattn);
            request.setAttribute("action","");
            forwardPage(Page.VIEW_USER_IN_STUDY);

        }
     }
    }

}
