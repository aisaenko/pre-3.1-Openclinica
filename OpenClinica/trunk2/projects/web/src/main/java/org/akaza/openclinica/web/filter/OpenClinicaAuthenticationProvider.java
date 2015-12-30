package org.akaza.openclinica.web.filter;


import java.security.ProviderException;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class OpenClinicaAuthenticationProvider extends DaoAuthenticationProvider {

    private String url_access;

    public String getUrl_access() {
        return url_access;
    }

    public void setUrl_access(String url_access) {
        this.url_access = url_access;
    }
    
    public Authentication authenticate(Authentication auth, String url)throws ProviderException{
        if(auth.getAuthorities().contains(url)){
            return super.authenticate(auth);
        }
        else{
            throw new ProviderException("Not Authorized!!!");
        }
        
    }

   
}
