package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.role.GroupAuthDefinition;
/**
 * 
 * @author jnyayapathi
 *
 */
public class GroupAuthenticationDao extends AbstractDomainDao<GroupAuthDefinition>{

    @Override
    Class<GroupAuthDefinition> domainClass() {
        // TODO Auto-generated method stub
        return GroupAuthDefinition.class;
    }
    

}
