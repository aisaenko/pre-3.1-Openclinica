package org.akaza.openclinica.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.coding.CodeItemData;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.controller.helper.CodedTermBean;
import org.akaza.openclinica.controller.helper.SearchCodingTermsBioPortal;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.coding.CodingService;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Implement the functionality for displaying a table of Event CRFs for Source Data
 * Verification. This is an autowired, multiaction Controller.
 */
@Controller("reviewCodesController")
public class ReviewCodesController {
	private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this
			.getClass().getName());
    
    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    
    //Autowire the class that handles the sidebar structure with a configured
    //bean named "sidebarInit"
    @Autowired
    @Qualifier("sidebarInit")
    private SidebarInit sidebarInit;

    public ReviewCodesController() {
    }


    @RequestMapping("/reviewCodes")
    public ModelMap reviewCodes(HttpServletRequest request, @RequestParam("studyId") int studyId, HttpServletResponse response) {
    
       /* if(!mayProceed(request)){
            try{
                response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }*/
        ResourceBundleProvider.updateLocale(request.getLocale());
        // Reseting the side info panel set by SecureControler Mantis Issue: 8680.
        // Todo need something to reset panel from all the Spring Controllers 
        StudyInfoPanel panel = new StudyInfoPanel();
        panel.reset();
        request.getSession().setAttribute("panel", panel);

        ModelMap gridMap = new ModelMap();
        //set up request attributes for sidebar
        //Not necessary when using old page design...
        // setUpSidebar(request);
        boolean showMoreLink = false;
        if(request.getParameter("showMoreLink")!=null){
            showMoreLink = Boolean.parseBoolean(request.getParameter("showMoreLink").toString());
        }else{
            showMoreLink = true;
        }
        request.setAttribute("showMoreLink", showMoreLink+"");
        request.setAttribute("studyId", studyId);


        ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
        if (pageMessages == null) {
            pageMessages = new ArrayList<String>();
        }

        request.setAttribute("pageMessages", pageMessages);
        //Fetch the data to be displayed on the page (for temporary simple UI to start testing)
        CodingService codingService = new CodingService(this.dataSource);        
        ArrayList<CodeItemData> codeItemDataList = codingService.getMedicalCodeDataPerStudySubject(studyId);
        request.setAttribute("codeItemDataList", codeItemDataList);

      return gridMap;
    }
    
    @RequestMapping("/searchExactMatch")
    public String searchExactMatch(HttpServletRequest request, HttpServletResponse response, @RequestParam("studyId") int studyId,
            @RequestParam("redirection") String redirection, ModelMap model, @RequestParam("verbatimTerm") String verbatimTerm) {
    	
    	ModelMap searchResultMap = new ModelMap();
    	String SOC = "";
    	String HLGT = "";
    	String HLT = "";
    	String PT = "";
    	
//    	String result = null;
//    	String verbatimTerm = request.getParameter("verbatimTerm");
    	logger.debug("verbatimTerm: " + verbatimTerm);
    	
    	CodedTermBean codedTermBean = new CodedTermBean();
    	codedTermBean.setLLT(verbatimTerm);
    	String conceptId = "";
    	String codingStatus = "";
    	conceptId = SearchCodingTermsBioPortal.findExactMatch(verbatimTerm);
    	if (null != conceptId){
    		codedTermBean.setConceptId(conceptId);    
    		
    		//get root path structure
        	String rootPath = SearchCodingTermsBioPortal.findConceptRootPath(conceptId);
        	if(rootPath == null){
        		logger.error("concept rootpath could not be retrieved");
        		codingStatus = "concept rootpath could not be retrieved";  
        	}
        	else{
        		//assuming that a PT level term is searched and there will only 3 more levels up in the 
    			//hierarchy, there will be only 3 dot (".") separated concept Ids in the root path
        		String[] tokens = rootPath.split("\\.");
        		if(tokens.length != 3){
        			logger.error("Number of levels in the root path hierarchy of the concept are " + verbatimTerm + " are: " + tokens.length);
        			codingStatus = "concept rootpath could not be retrieved correctly";
        		} 
        		else{       			
//        			String HLTconceptId = tokens[2];
//        			HLT = SearchCodingTermsBioPortal.findConceptLabel(HLTconceptId);
//        			logger.debug("HLT: " + HLT);
        			String PTconceptId = tokens[2];
        			PT = SearchCodingTermsBioPortal.findConceptLabel(PTconceptId);
        			logger.debug("PT: " + PT);
        			
//        			String HLGTconceptId = tokens[1];
//        			HLGT = SearchCodingTermsBioPortal.findConceptLabel(HLGTconceptId);
        			String HLTconceptId = tokens[1];
        			HLT = SearchCodingTermsBioPortal.findConceptLabel(HLTconceptId);
        			logger.debug("HLT: " + HLT);
        			
        			String SOCconceptId = tokens[0];
        			SOC = SearchCodingTermsBioPortal.findConceptLabel(SOCconceptId);
        			logger.debug("SOC: " + SOC);
        			
        			codedTermBean.setHLT(HLT);
        			codedTermBean.setHLGT(HLGT);
        			codedTermBean.setSOC(SOC);
        			codingStatus = "Complete";   
        		}        	
        	}
    		 		
    	}
    	else{
    		codingStatus = "Not found";    	
    	}
    	logger.debug("codedTermBean: " + codedTermBean);
    	logger.debug("codingStatus: " + codingStatus);
    	request.setAttribute("codedTermBean", codedTermBean);
    	request.setAttribute("codingStatus", codingStatus);
    	request.setAttribute("verbatimTerm", verbatimTerm);
//    	searchResultMap.put("codedTermBean", codedTermBean);
//    	searchResultMap.put("codingStatus", codingStatus);
    	
    	
    	
    	try{
    		request.getRequestDispatcher(redirection).forward(request, response);
    	}
    	catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/* private boolean mayProceed(HttpServletRequest request) {
        StudyUserRoleBean currentRole = (StudyUserRoleBean)request.getSession().getAttribute("userRole"); 
        Role r = currentRole.getRole();

        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
            return true;
        }

        return false;
    }*/

}
