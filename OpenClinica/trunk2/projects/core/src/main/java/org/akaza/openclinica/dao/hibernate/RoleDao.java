package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.role.Role;

public class RoleDao extends AbstractDomainDao<Role>{

    @Override
    Class<Role> domainClass() {
        // TODO Auto-generated method stub
        return Role.class;
    }

}
