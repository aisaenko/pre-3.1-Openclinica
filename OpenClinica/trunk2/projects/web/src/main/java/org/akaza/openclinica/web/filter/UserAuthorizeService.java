package org.akaza.openclinica.web.filter;

import javax.sql.DataSource;

import org.akaza.openclinica.dao.hibernate.UserAccountDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.role.UserAccount;
import org.akaza.openclinica.domain.role.UserRoleAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *  Custom User AuthorizeService
 * @author jnyayapathi
 *
 */
@Service("userDetailsService")
public class UserAuthorizeService implements UserDetailsService {

    @Autowired
    private UserAccountDao userAccountDAO;

    
    public UserAccountDao getUserAccountDAO() {
        return userAccountDAO;
    }


    public void setUserAccountDAO(UserAccountDao userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }


    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
       
       UserAccount userAccount = userAccountDAO.findByColumnName(userName, "userName");
          StudyDAO studyDao = new StudyDAO(getDataSource());
          
        CustomUser customUser = new CustomUser();
       // customUser.setUserRoleAccess(userAccount.getUserRoleAccess());
        customUser.setUserAccount(userAccount);
        customUser.setStudyDAO(studyDao);
        customUser.setRoles(userAccount.getRole());
        return customUser;
    }
    DataSource dataSource;
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
