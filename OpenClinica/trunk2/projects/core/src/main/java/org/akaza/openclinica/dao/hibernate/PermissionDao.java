package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.role.Permissions;
/**
 * 
 * @author jnyayapathi
 *
 */
public class PermissionDao extends AbstractDomainDao<Permissions>{

    @Override
    Class<Permissions> domainClass() {
        return Permissions.class;
    }

}
