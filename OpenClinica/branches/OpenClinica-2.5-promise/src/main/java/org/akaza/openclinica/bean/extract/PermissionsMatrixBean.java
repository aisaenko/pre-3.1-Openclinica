package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.HashMap;

public class PermissionsMatrixBean extends AuditableEntityBean {
    // a hashMap giving us 0
    // (not permitted access) or 1 (permitted access)
	// key to the hashmap is the following:
	// defid _ crfid _ itemid _ roleid
	// which in this bean are the variables x, y, z, and a
    private HashMap<String, Integer> permissions;
    
    public PermissionsMatrixBean() {
        permissions = new HashMap<String, Integer>();
    }

    public boolean isPermitted(int x, int y, int z, int a) {
    	String permit = x + "_" + y + "_" + z + "_" + a;
    	if (permissions.get(permit) != null) {
	        int intPermit = new Integer(permissions.get(permit)).intValue();
	        if (intPermit == 0) {
	            return false;
	        } else {
	            return true;
	        }
    	} else {
    		return false;
    	}
    }
    
    public void setPermission(int x, int y, int z, int a, int permit) {
    	String strPermit = x + "_" + y + "_" + z + "_" + a;
        Integer intPermit = new Integer(permit);
        permissions.put(strPermit, intPermit);
    }

	public HashMap<String, Integer> getPermissions() {
		return permissions;
	}
    
    
}
