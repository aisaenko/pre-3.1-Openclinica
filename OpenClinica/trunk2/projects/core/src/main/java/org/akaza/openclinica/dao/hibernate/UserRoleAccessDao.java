package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.role.UserRoleAccess;

public class UserRoleAccessDao  extends AbstractDomainDao<UserRoleAccess>{

    @Override
    Class<UserRoleAccess> domainClass() {
        // TODO Auto-generated method stub
        return UserRoleAccess.class;
    }

    /*public UserRoleAccess runUpdateWithGroupInfoQuery(String NamedQuery,Integer groupId,Integer roleId,Integer userId){
         getCurrentSession().createSQLQuery("");
        
    }*/
}
