package org.akaza.openclinica.dao;

import org.akaza.openclinica.dao.hibernate.PermissionDao;
import org.akaza.openclinica.domain.role.Permissions;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;
/**
 * 
 * @author jnyayapathi
 *
 */
public class PermissionTest extends HibernateOcDbTestCase{
    public void testfindById() {
    
        PermissionDao permissionDao = (PermissionDao)getContext().getBean("permissionDAO");
        Permissions permissions =  permissionDao.findById(1);
        assertEquals("UserName should be testUser", "pages/login/login", permissions.getAccess_url());
    }
    public void testSaveOrUpdate(){
        Permissions permissions = new Permissions();
        permissions.setAccess_url("/testjames");
        PermissionDao permissionDao = (PermissionDao)getContext().getBean("permissionDAO");
        Permissions permit = permissionDao.saveOrUpdate(permissions);
        assertNotNull(permit);
        
      
    }
}
