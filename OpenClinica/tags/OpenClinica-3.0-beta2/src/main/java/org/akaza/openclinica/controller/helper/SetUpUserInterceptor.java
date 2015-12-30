package org.akaza.openclinica.controller.helper;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.Locale;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;

/**
 * An "interceptor" class that sets up a UserAccount and stores it in the Session, before
 * another class is initialized and potentially uses that UserAccount.
 */
public class SetUpUserInterceptor extends HandlerInterceptorAdapter {

    public static final String USER_BEAN_NAME = "userBean";

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object o) throws Exception {

        Locale locale = ResourceBundleProvider.localeMap.get(Thread.currentThread());
        if(locale == null) {
            ResourceBundleProvider.updateLocale(httpServletRequest.getLocale());
        }

        //Set up the user account bean: check the Session first
        HttpSession currentSession = httpServletRequest.getSession();
        UserAccountBean userBean = (UserAccountBean) currentSession.getAttribute("userBean");
        String userName = "";
        boolean userBeanIsInvalid;

        if(userBean == null){
            UserAccountDAO  userAccountDAO = new UserAccountDAO(dataSource);
            userName= httpServletRequest.getRemoteUser();
            userBeanIsInvalid = "".equalsIgnoreCase(userName);
            if(!  userBeanIsInvalid){
                userBean = (UserAccountBean) userAccountDAO.findByUserName(userName);
                userBeanIsInvalid = (userBean == null);
                if(!  userBeanIsInvalid){
                    currentSession.setAttribute(USER_BEAN_NAME,userBean);
                }

            }
 }

        //The user bean could still be null at this point
        if( userBean == null ) {
            userBean = new UserAccountBean();
            userBean.setName("unknown");
            currentSession.setAttribute(USER_BEAN_NAME,userBean);
        }

        SetUpStudyRole setupStudy = new SetUpStudyRole(dataSource);
        setupStudy.setUp(currentSession,userBean);

        return true;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
