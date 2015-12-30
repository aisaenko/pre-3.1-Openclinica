/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 


package org.akaza.openclinica.control.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.BreadcrumbTrail;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.view.StudyInfoPanelLine;

/**
 * This class enhances the Controller in several ways.
 * 
 * <ol>
 * <li> The method mayProceed, for which the class is named, is declared abstract and
 * is called before processRequest.  This method indicates whether the user may proceed
 * with the action he wishes to perform (as indicated by various attributes or parameters in request or session).
 * Note, howeveer, that the method has a void return, and throws InsufficientPermissionException.
 * The intention is that if the user may not proceed with his desired action, the method
 * should throw an exception.  InsufficientPermissionException will accept a Page object
 * which indicates where the user should be redirected in order to be informed that he
 * has insufficient permission, and the process method enforces this redirection by
 * catching an InsufficientPermissionException object.
 * 
 * <li> Four new members, session, request, response, and the UserAccountBean object ub have
 * been declared protected, and are set in the process method.  This allows developers
 * to avoid passing these objects between methods, and moreover it accurately encodes the
 * fact that these objects represent the state of the servlet.
 * 
 * <br/>In particular, please note that it is no longer necessary to generate a bean for
 * the session manager, the current user or the current study.
 * 
 * <li> The method processRequest has been declared abstract.  This change is unlikely
 * to affect most code, since by custom processRequest is declared in each subclass anyway.
 * 
 * <li> The standard try-catch block within most processRequest methods has been included
 * in the process method, which calls the processRequest method.  Therefore, subclasses
 * may throw an Exception in the processRequest method without having to handle it.
 * 
 * <li> The addPageMessage method has been declared to streamline the process of setting
 * page-level messages.  The accompanying showPageMessages.jsp file in jsp/include/ automatically
 * displays all of the page messages; the developer need only include this file in the jsp.
 * 
 * <li> The addEntityList method makes it easy to add a Collection of EntityBeans to the request.
 * Note that this method should only be used for Collections from which one EntityBean must be selected by the user.
 * If the Collection is empty, this method will throw an InconsistentStateException, taking the user
 * to an error page and settting a page message indicating that the user may not proceed because no
 * entities are present.  Note that the error page and the error message must be specified.
 * </ol>
 *
 * @author ssachs
 */
public abstract class SecureController extends HttpServlet implements SingleThreadModel {
  protected ServletContext context;
  protected SessionManager sm;
  protected final Logger logger = Logger.getLogger(getClass().getName());
  protected String logDir;
  protected String logLevel;
  protected HttpSession session;
  protected HttpServletRequest request;
  protected HttpServletResponse response;
  protected UserAccountBean ub;
  protected StudyBean currentStudy;
  protected StudyUserRoleBean currentRole;
  protected HashMap errors = new HashMap();
  protected SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
  protected StudyInfoPanel panel = new StudyInfoPanel();
  
  public static final String PAGE_MESSAGE = "pageMessages";//for showing page
                                                           // wide message

  public static final String INPUT_MESSAGES = "formMessages"; // for showing
                                                              // input-specific
                                                              // messages

  public static final String PRESET_VALUES = "presetValues"; // for setting
                                                             // preset values

  public static final String ADMIN_SERVLET_CODE = "admin";
  
  public static final String BEAN_TABLE = "table";
  
  public static final String STUDY_INFO_PANEL = "panel"; //for setting the side panel

  public static final String BREADCRUMB_TRAIL = "breadcrumbs";
  
  public static final String POP_UP_URL = "popUpURL";
  
  //for setting the breadcrumb trail
  // protected HashMap errors = new HashMap();//error messages on the page

  protected void addPageMessage(String message) {
    ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);

    if (pageMessages == null) {
      pageMessages = new ArrayList();
    }

    pageMessages.add(message);
    request.setAttribute(PAGE_MESSAGE, pageMessages);
  }
  
  protected void resetPanel() {
  	panel.reset();
  }
  
  protected void setToPanel(String title, String info) {
  	if (panel.isOrderedData()) {
  		ArrayList data = panel.getUserOrderedData();
  		data.add(new StudyInfoPanelLine(title, info));
  		panel.setUserOrderedData(data);
  	}
  	else {
  		panel.setData(title, info);
  	}
  	request.setAttribute(STUDY_INFO_PANEL, panel);
  }

  protected void setInputMessages(HashMap messages) {
    request.setAttribute(INPUT_MESSAGES, messages);
  }

  protected void setPresetValues(HashMap presetValues) {
    request.setAttribute(PRESET_VALUES, presetValues);
  }
  
  protected void setTable(EntityBeanTable table) {
  	request.setAttribute(BEAN_TABLE, table);
  }

  public void init() throws ServletException {
    context = getServletContext();
  }

  /**
   * Process request   
   *
   * @throws Exception
   */
  protected abstract void processRequest() throws Exception;

  protected abstract void mayProceed() throws InsufficientPermissionException;

  public static final String USER_BEAN_NAME = "userBean";
  
  private void process(HttpServletRequest request, HttpServletResponse response)
      throws OpenClinicaException {
    session = request.getSession();
    session.setMaxInactiveInterval(60 * 60 * 3);
    logger.setLevel(Level.ALL);

    ub = (UserAccountBean) session.getAttribute(USER_BEAN_NAME);
    currentStudy = (StudyBean) session.getAttribute("study");
    currentRole = (StudyUserRoleBean) session.getAttribute("userRole");

    try {
      String userName = request.getRemoteUser();

      sm = new SessionManager(ub, userName);
      ub = sm.getUserBean();
      session.setAttribute("userBean", ub);
      if (logger.isLoggable(Level.INFO)) {
        //logger.info("user bean from SessionManager:" + ub.getName());
      }

      if ((currentStudy == null) || (currentStudy.getId() <= 0)) {
        if (ub.getId() > 0 && ub.getActiveStudyId() > 0) {
          StudyDAO sdao = new StudyDAO(sm.getDataSource());
          StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
          currentStudy = (StudyBean) sdao.findByPK(ub.getActiveStudyId());
          
          ArrayList studyParameters = spvdao.findParamConfigByStudy(currentStudy);
          
          currentStudy.setStudyParameters(studyParameters);
          
          StudyConfigService scs = new StudyConfigService(sm.getDataSource());
          if (currentStudy.getParentStudyId() <=0) {//top study           
            scs.setParametersForStudy(currentStudy);           
            
          } else {              
            scs.setParametersForSite(currentStudy);                 
             
          }
          
          //set up the panel here, tbh
          panel.reset();
          /*panel.setData("Study", currentStudy.getName());
          panel.setData("Summary", currentStudy.getSummary());
          panel.setData("Start Date", 
          		sdf.format(currentStudy.getDatePlannedStart()));
          panel.setData("End Date",
          		sdf.format(currentStudy.getDatePlannedEnd()));
          panel.setData("Principal Investigator",
          		currentStudy.getPrincipalInvestigator());*/
          session.setAttribute(STUDY_INFO_PANEL, new StudyInfoPanel());
        } else {
          currentStudy = new StudyBean();
        }
        session.setAttribute("study", currentStudy);
      }

      if ((currentRole == null) || (currentRole.getId() <= 0)) {
        if (ub.getId() > 0 && currentStudy.getId() > 0) {
          currentRole = ub.getRoleByStudy(currentStudy.getId());
          if (currentStudy.getParentStudyId() > 0) {
            StudyUserRoleBean roleInParent = ub.getRoleByStudy(currentStudy.getParentStudyId());
            //inherited role from parent study, pick the higher role
            currentRole.setRole(Role.max(currentRole.getRole(), roleInParent.getRole()));

          }
          //logger.info("currentRole:" + currentRole.getRoleName());
        } else {
          currentRole = new StudyUserRoleBean();
        }
        session.setAttribute("userRole", currentRole);
      }

      request.setAttribute("isAdminServlet", getAdminServlet());

      this.request = request;
      this.response = response;
      
      mayProceed();
      processRequest();
    } catch (InconsistentStateException ise) {
      ise.printStackTrace();
      logger.warning("InconsistentStateException: org.akaza.openclinica.control.SecureController: "
          + ise.getMessage());

      addPageMessage(ise.getOpenClinicaMessage());
      forwardPage(ise.getGoTo());
    } catch (InsufficientPermissionException ipe) {
      ipe.printStackTrace();
      logger.warning("InsufficientPermissionException: org.akaza.openclinica.control.SecureController: "
          + ipe.getMessage());

      //addPageMessage(ipe.getOpenClinicaMessage());
      forwardPage(ipe.getGoTo());
    } catch (Exception e) {
      e.printStackTrace();
      logger.warning("OpenClinicaException:: org.akaza.openclinica.control.SecureController:: " + e.getMessage());

      forwardPage(Page.ERROR);
    }
  }

  /**
   * Handles the HTTP <code>GET</code> method.
   * 
   * @param request
   * @param response
   * @throws ServletException
   * @throws java.io.IOException
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   * 
   * @param request
   *          servlet request
   * @param response
   *          servlet response
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * <P>Forwards to a jsp page.  Additions to the forwardPage() method
   * involve checking the session for the bread crumb trail and setting
   * it, if necessary.  Setting it here allows the developer to only have
   * to update the <code>BreadcrumbTrail</code> class.
   * 
   * @param jspPage
   *          The page to go to.
   * @param checkTrail
   * 		  The command to check for, and set a trail in the session.
   */
  protected void forwardPage(Page jspPage, boolean checkTrail) {
  	if (request.getAttribute(POP_UP_URL) == null) {
  		request.setAttribute(POP_UP_URL, "");
  	}
  	
    try {
    	//Added 01/19/2005 for breadcrumbs, tbh
    	if (checkTrail) {
    		BreadcrumbTrail bt = new BreadcrumbTrail();
    		if (session != null ){//added bu jxu, fixed bug for log out
    			ArrayList trail = (ArrayList)session.getAttribute("trail");
    			if (trail==null) {
    				trail = bt.generateTrail(jspPage, request);
    			} else {
    				bt.setTrail(trail);
    				trail = bt.generateTrail(jspPage, request);
    			}
    			session.setAttribute("trail",trail);
    			StudyInfoPanel sip = (StudyInfoPanel)session.getAttribute(STUDY_INFO_PANEL);
    			if (sip==null) {
    				sip = new StudyInfoPanel();
    				sip.setData(jspPage,session,request);
    			} else {
    				sip.setData(jspPage,session,request);
    			}
    			/*88888888888888888888888888888888888888888888888888888*/
    			StudySubjectDAO ssDAO = new StudySubjectDAO(sm.getDataSource());
    			//TODO find the right place for this, this is not the right place
    			Collection subjects = ssDAO.findAllByStudyId(currentStudy.getId());
    			int numOfSubjects = subjects.size();
    			//logger.warning("found subjects "+numOfSubjects);
    			if (sip.isStudyInfoShown()){
    			  sip.setData("Number of Subjects Enrolled",
    					new Integer(numOfSubjects).toString());
    			}
    			/*888888888888888888888888888888888888888888888888888*/
        		session.setAttribute(STUDY_INFO_PANEL, sip);
    		}
    		//we are also using checkTrail to update the panel, tbh 01/31/2005
    		
    	}
      //above added 01/19/2005, tbh
      context.getRequestDispatcher(jspPage.getFileName()).forward(request, response);
    } catch (Exception se) {
      se.printStackTrace();
    }

  }

  protected void forwardPage(Page jspPage) {
  	this.forwardPage(jspPage, true);
  }
  /**
   * This method supports functionality of the type "if a list of entities is
   * empty, then jump to some page and display an error message."
   * This prevents users from seeing empty drop-down lists and being given error messages when
   * they can't choose an entity from the drop-down list. Use, e.g.:
   * <code>addEntityList("groups", allGroups, "There are no groups to display, so you cannot add a subject to this Study.", 
   * Page.SUBMIT_DATA)</code>
   * 
   * @param beanName
   *          The name of the entity list as it should be stored in the request
   *          object.
   * @param list
   *          The Collection of entities.
   * @param messageIfEmpty
   *          The message to display if the collection is empty.
   * @param destinationIfEmpty
   *          The Page to go to if the collection is empty.
   * @throws InconsistentStateException
   */
  protected void addEntityList(String beanName, Collection list, String messageIfEmpty,
      Page destinationIfEmpty) throws InconsistentStateException {
    if (list.isEmpty()) {
      throw new InconsistentStateException(destinationIfEmpty, messageIfEmpty);
    }

    request.setAttribute(beanName, list);
  }

  /**
   * @return A blank String if this servlet is not an Administer System servlet.
   * SecureController.ADMIN_SERVLET_CODE otherwise.
   */
  protected String getAdminServlet() {
    return "";
  }

  protected void setPopUpURL(String url) {
    if ((url != null) && (request != null)) {
      request.setAttribute(POP_UP_URL, url);
    }
  }
}