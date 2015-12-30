package org.akaza.openclinica.dao.hibernate;


import org.akaza.openclinica.domain.role.RolePermission;

public class RolePermissionDao extends AbstractDomainDao<RolePermission> {

    @Override
    Class<RolePermission> domainClass() {
        // TODO Auto-generated method stub
        return RolePermission.class;
    }

}
