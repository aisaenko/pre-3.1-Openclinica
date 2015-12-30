/**
 * 
 */
package org.akaza.openclinica.web.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * @author jnyayapathi
 *
 */
public class OpenClinicaWebAuthenticationDetails extends WebAuthenticationDetails {

    public OpenClinicaWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
    
    }

    public void doPopulateAdditionalInformation(HttpServletRequest request){
       setRequestURL( request.getRequestURL().toString());
    }
    
    private String requestURL;
    
    public void setRequestURL(String url){
        this.requestURL = url;
    }
    public String getRequestURL(){
        return this.requestURL;
    }
}
