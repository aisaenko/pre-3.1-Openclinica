package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * 
 * @author thickerson
 *
 */
public class UpdatePrivilegesServlet extends SecureController {
	Locale locale;
	
	private static final String SUBMIT_AND_SAVE_MORE = "SubmitAndSaveMore";
	private static final String SUBMIT_AND_CONTINUE = "SubmitAndContinue";
	@Override
	/*
	 * same as from the manage privileges servlet
	 */
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

	@Override
	protected void processRequest() throws Exception {
		boolean saveAndContinue = false;
		boolean saveAndAddMore = false;
		// below taken from manage privs servlet
		CRFDAO crfDao = new CRFDAO(sm.getDataSource());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        
		String crfId = request.getParameter("crfId");
        String defId = request.getParameter("defId");
        int intCrfId = Integer.parseInt(crfId);
        int intDefId = Integer.parseInt(defId);

        CRFBean crf = new CRFBean();
        StudyEventDefinitionBean sedBean = new StudyEventDefinitionBean();
        
		crf = (CRFBean) crfDao.findByPK(intCrfId);
        sedBean = (StudyEventDefinitionBean) seddao.findByPK(intDefId);
        ItemDAO itemDao = new ItemDAO(sm.getDataSource());
        
        ArrayList items = itemDao.findAllActiveByCRF(crf);
		// above taken from manage privs servlet
        FilterDAO filterDao = new FilterDAO(sm.getDataSource());
        for (int i = 0; i < items.size(); i++) {
            ItemBean item = (ItemBean) items.get(i);
            for (int j = 0; j < Role.list.size(); j++) {
            	Role role = (Role) Role.list.get(j);
            	String key = sedBean.getId() + "_" + crf.getId() + "_" + item.getId() + "_" + role.getId() + "_" + role.getName();
            	// >> tbh workaround for changing 'investigator' to 'Data Specialist'
            	if (role.getName().equals("Data Specialist")) {
            		key = sedBean.getId() + "_" + crf.getId() + "_" + item.getId() + "_" + role.getId() + "_" + "investigator";
            	}
            	System.out.println("found " + key);
            	if (request.getParameter(key) != null) {
            		String permit = request.getParameter(key);
            		int intPermit = 1;
            		if (filterDao.isPermissionInDB(sedBean.getId(), crf.getId(), item.getId(), role.getId())) {
    	    			filterDao.updatePermission(sedBean.getId(), crf.getId(), item.getId(), role.getId(), intPermit, ub.getId());
    	    		} else {
    	    			filterDao.createPermission(sedBean.getId(), crf.getId(), item.getId(), role.getId(), intPermit, ub.getId());
    	    		}
                } else {
                	int intPermit = 0;
                	filterDao.createPermission(sedBean.getId(), crf.getId(), item.getId(), role.getId(), intPermit, ub.getId());
                }
            }
        }
        
//		java.util.Enumeration names = request.getAttributeNames();
//		while (names.hasMoreElements()) {
//			System.out.println(names.nextElement());
//		}
		java.util.Enumeration names = request.getParameterNames();
		
		while (names.hasMoreElements()) {
			// System.out.println(names.nextElement());
			String strName = (String)names.nextElement();
			// System.out.println(strName);
			if (strName.equals(SUBMIT_AND_SAVE_MORE)) {
				saveAndAddMore = true;
			}
			
			if (strName.equals(SUBMIT_AND_CONTINUE)) {
				saveAndContinue = true;
			}
			// run through all the rest to save here, tbh

		}
		String toContinue = request.getParameter(SUBMIT_AND_CONTINUE);
		String toSave = request.getParameter(SUBMIT_AND_SAVE_MORE);
		System.out.println("found continue: " + saveAndContinue + ": " + toContinue + " and add more: " + saveAndAddMore + ": " + toSave);

		if (saveAndAddMore) {
			// add a message here
		    addPageMessage("You have successfully saved the privileges.  " + 
		            "You can pick another CRF from the list of Study Events " + 
		            "below to change more privileges now.");
		    // add the study events here
		    HashMap events = new LinkedHashMap();
	        events = generateEventCRFList();
		    request.setAttribute("eventlist", events);
			forwardPage(Page.UPDATE_PRIVILEGES);
		} else {
			// add another message here
		    addPageMessage("You have successfully saved the privileges.");
			forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
		}
	}
	
	/*
     * taken from create dataset servlet, tbh 08/2009
     * TODO exists in three places, need to DRY.
     * this is slightly different than manage, since we're not assigning any SEDs or CRFs.
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
            }
        }
        return events;
    }


}
