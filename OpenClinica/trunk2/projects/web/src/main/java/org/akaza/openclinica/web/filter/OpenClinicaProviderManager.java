package org.akaza.openclinica.web.filter;

import java.security.ProviderException;

import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class OpenClinicaProviderManager extends ProviderManager {
           
    
    
    public Authentication doAuthentication(Authentication authentication, String url) throws ProviderException{
      
        System.out.println("*****************************ENTERING 111111111");
        Authentication auth =  super.doAuthentication(authentication);
      System.out.println("*****************************ENTERING 13333333222222222");
        OpenClinicaAuthenticationProvider daoAuthProvider = (OpenClinicaAuthenticationProvider)super.getProviders().get(1);//second provider should always be daoAuthentication provider
        daoAuthProvider.setUrl_access(url);
         
        return daoAuthProvider.authenticate(authentication,url);
    }
}
