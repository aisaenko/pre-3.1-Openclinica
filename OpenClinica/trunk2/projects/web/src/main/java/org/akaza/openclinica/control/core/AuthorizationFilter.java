package org.akaza.openclinica.control.core;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.web.filter.CustomUser;
import org.akaza.openclinica.web.filter.UserAuthorizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AuthorizationFilter implements Filter {
    public static final String USER_BEAN_NAME = "userBean";
    private PreAuthenticatedAuthenticationProvider authenticationManager;
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
        if(ub!=null)
        //if (SecurityContextHolder.getContext().getAuthentication() == null)
        {
            Authentication auth = doAuthentication(request,ub);
            // Place the new Authentication object in the security context.
            SecurityContextHolder.getContext().setAuthentication(auth);

        }
        HttpServletResponse response =(HttpServletResponse)res ;
        filterChain.doFilter(request, response);

    }

    private Authentication doAuthentication(HttpServletRequest req,UserAccountBean ub) {
        Authentication auth = null;
        try{
        UserAuthorizeService userDetailsService = (UserAuthorizeService) SpringServletAccess.getApplicationContext(req.getSession().getServletContext()).getBean("userAuthorizeService");
        CustomUser details = (CustomUser)userDetailsService.loadUserByUsername(ub.getName());    
        UsernamePasswordAuthenticationToken usernameAndPassword = 
            new UsernamePasswordAuthenticationToken(
                ub.getName(), ub.getPasswd(), details.getAuthorities());

       
        
        AccountStatusUserDetailsChecker checker = new AccountStatusUserDetailsChecker();
        checker.check(details);
        authenticationManager = new PreAuthenticatedAuthenticationProvider();
        authenticationManager.setUserDetailsChecker(checker);
    
        
        
        auth=  authenticationManager.authenticate(usernameAndPassword);
        }  catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
           //TODO:throw this exception away
        }
        return auth;
    }

    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}
