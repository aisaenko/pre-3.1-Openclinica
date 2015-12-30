package org.akaza.openclinica.dao;

import org.akaza.openclinica.dao.hibernate.RoleDao;
import org.akaza.openclinica.domain.role.Role;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;

public class RoleTest extends HibernateOcDbTestCase {
static int count =0;
    public RoleTest(){
        super();
        count++;
        System.out.println("count**********"+count);
    }
 
    public void testfindById() {
        RoleDao roleDao = (RoleDao) getContext().getBean("roleDAO");
        Role role= roleDao.findById(-1);

        assertEquals("UserName should be testUser", "testUser", role.getRoleName());
    }
    public void testSaveOrUpdate(){
        RoleDao roleDao = (RoleDao) getContext().getBean("roleDAO");
        Role role = new Role();
     //   role.setRole_id(2);
    //    role.setId(-3);
        
        role.setRoleName("testNewStudyRole");
       
       role =  roleDao.saveOrUpdate(role);
       
       assertNotNull(role.getId());
       assertEquals("UserName should be testUser", "testNewStudyRole", role.getRoleName());
      
    }

   
    
   
}
