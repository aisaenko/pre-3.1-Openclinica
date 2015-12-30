package org.akaza.openclinica.web.control.submit;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.domain.technicaladmin.LoginStatus;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.filter.OpenClinicaAuthenticationProcessingFilter;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.LockedException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.util.TextUtils;

public class OpenClinicaTestAuthenticationProcessFilter extends
		OpenClinicaAuthenticationProcessingFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        // Place the last username attempted into HttpSession for views
        HttpSession session = request.getSession(false);

        if (session != null || getAllowSessionCreation()) {
            request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextUtils.escapeEntities(username));
        }

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        Authentication authentication = null;
        UserAccountBean userAccountBean = null;
        ResourceBundleProvider.updateLocale(new Locale("en_US"));
        try {
            EntityBean eb = getUserAccountDao().findByUserName(username);
            userAccountBean = eb.getId() != 0 ? (UserAccountBean) eb : null;
            authentication = this.getAuthenticationManager().authenticate(authRequest);
            //auditUserLogin(username, LoginStatus.SUCCESSFUL_LOGIN, userAccountBean);
           // resetLockCounter(username, LoginStatus.SUCCESSFUL_LOGIN, userAccountBean);
        } catch (LockedException le) {
          //// auditUserLogin(username, LoginStatus.FAILED_LOGIN_LOCKED, userAccountBean);
            throw le;
        } catch (BadCredentialsException au) {
           // auditUserLogin(username, LoginStatus.FAILED_LOGIN, userAccountBean);
           // lockAccount(username, LoginStatus.FAILED_LOGIN, userAccountBean);
            throw au;
        } catch (AuthenticationException ae) {
            throw ae;
        }
        return authentication;
    }
}
