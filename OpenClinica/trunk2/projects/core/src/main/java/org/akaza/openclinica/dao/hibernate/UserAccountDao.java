package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.role.UserAccount;
import org.hibernate.SQLQuery;
/**
 * Domain Object for User_account
 * @author jnyayapathi
 *
 */
public class UserAccountDao  extends AbstractDomainDao<UserAccount>{

    @Override
    Class<UserAccount> domainClass() {
        // TODO Auto-generated method stub
        return UserAccount.class;
    }

    public UserAccount forceUpdate(UserAccount domainObject){
        getCurrentSession().evict(domainObject);
        getCurrentSession().update(domainObject);
        return domainObject;
    }
    
    public void runUpdateGroupInfo(UserAccount domainObject, Integer groupId,Integer roleId,Integer userId){
       SQLQuery query =  getCurrentSession().createSQLQuery("update user_role_access set group_id = "+groupId+"where role_id = "+roleId+" and user_id="+userId);
       query.executeUpdate();
    }
}
