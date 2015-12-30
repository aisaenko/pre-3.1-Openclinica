/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.admin.*;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.*;

/**
 * Prepares to update study event definition
 * 
 * @author jxu
 * 
 */
public class InitUpdateEventDefinitionServlet extends SecureController {

  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
    StudyEventDAO sdao = new StudyEventDAO(sm.getDataSource());
    //get current studyid
    int studyId = currentStudy.getId();

    if (ub.hasRoleInStudy(studyId)) {
      Role r = ub.getRoleByStudy(studyId).getRole();
      if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
        return;
      } else {
        addPageMessage("You do not have permission to update a Study Event Definition."
            + " Please change your active study or contact the System Administrator.");
        throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, "not study director", "1");
       
      }
    }

    //To Do: the following code doesn't apply to admin for now
    String idString = request.getParameter("id");
    int defId = Integer.valueOf(idString.trim()).intValue();
    logger.info("defId" + defId);
    ArrayList events = (ArrayList) sdao.findAllByDefinition(defId);
    if (events != null && events.size() > 0) {
      logger.info("has events");
      for (int i = 0; i < events.size(); i++) {
        StudyEventBean sb = (StudyEventBean) events.get(i);
        if (!sb.getStatus().equals(Status.DELETED)) {
          logger.info("found one event");
          addPageMessage("Sorry, but at this time you may not modify a Study Event "
              + "Definition once data has been added to it. If you need to edit it, "
              + "please remove all event data associated with this Study Event Definition first.");
          throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
              "not unpopulated", "1");
        }
      }
    }

  }

  public void processRequest() throws Exception {

    StudyEventDefinitionDAO sdao = new StudyEventDefinitionDAO(sm.getDataSource());
    String idString = request.getParameter("id");
    logger.info("definition id:" + idString);
    if (StringUtil.isBlank(idString)) {
      addPageMessage("Please choose a definition to edit.");
      forwardPage(Page.LIST_DEFINITION_SERVLET);
    } else {
      //definition id
      int defId = Integer.valueOf(idString.trim()).intValue();
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);

      EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(sm.getDataSource());
      ArrayList eventDefinitionCRFs = (ArrayList) edao.findAllByDefinition(defId);

      CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
        EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
        ArrayList versions = (ArrayList) cvdao.findAllActiveByCRF(edc.getCrfId());
        edc.setVersions(versions);
        CRFBean crf = (CRFBean) cdao.findByPK(edc.getCrfId());
        edc.setCrfName(crf.getName());
        //TO DO: use a better way on JSP page,eg.function tag
        edc.setNullFlags(processNullValues(edc));
       
        CRFVersionBean defaultVersion = (CRFVersionBean)cvdao.findByPK(edc.getDefaultVersionId());
        edc.setDefaultVersionName(defaultVersion.getName());   
      }

      session.setAttribute("definition", sed);
      session.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);

      forwardPage(Page.UPDATE_EVENT_DEFINITION1);
    }

  }

  private HashMap processNullValues(EventDefinitionCRFBean edc) {
    HashMap flags = new LinkedHashMap();
    String s = edc.getNullValues();
    //logger.info("********:" + s );
    if (s != null) {
      for (int i = 1; i <= NullValue.toArrayList().size(); i++) {
        String nv = (NullValue.get(i)).getName().toUpperCase();
        if (s.indexOf(nv) >= 0) {
          flags.put(nv, "1");
          //logger.info("********1:" + nv );
        } else {
          flags.put(nv, "0");
          //logger.info("********0:" + nv );
        }

      }
    }

    return flags;
  }
}