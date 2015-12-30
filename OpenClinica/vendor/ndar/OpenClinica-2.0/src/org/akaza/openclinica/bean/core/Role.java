/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.*;

public class Role extends Term {
	
	public static final Role INVALID = new Role(0, "invalid", "invalid", null);
    public static final Role ADMIN = new Role(1, "admin", "System Administrator", null);
	public static final Role COORDINATOR = new Role(2, "coordinator", "Study Coordinator", null);
    public static final Role STUDYDIRECTOR = new Role(3, "director", "Study Director", null);
	public static final Role INVESTIGATOR = new Role(4, "investigator", "Investigator", null);
	public static final Role RESEARCHASSISTANT = new Role(5, "ra", "Research Assistant", null);
	public static final Role GUEST = new Role(6, "guest", "Guest", null);
	
	private static final Role[] members = {ADMIN, COORDINATOR, STUDYDIRECTOR, INVESTIGATOR, RESEARCHASSISTANT, GUEST};
	public static final List list = Arrays.asList(members);

	private List privileges;
	
	private Role(int id, String name, String description, Privilege[] myPrivs) {
		super(id, name, description);
		//privileges = Arrays.asList(myPrivs);
	}
	
	private Role() {
	}
	
	public static boolean contains(int id) {
		return Term.contains(id, list);
	}
	
	public static Role get(int id) {
		return (Role) Term.get(id, list);
	}
	
	public static Role getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			Role temp = (Role) list.get(i);
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return INVALID;
	}
	
	public static ArrayList toArrayList(){
	  return new ArrayList(list);
	}
	
	public boolean hasPrivilege(Privilege p) {
		Iterator it = privileges.iterator();
		
		while (it.hasNext()) {
			Privilege myPriv = (Privilege) it.next();
			if (myPriv.equals(p)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Implicitly orders the Role set in the following way:
	 * <ul>
	 * <li> null is the lowest possible Role
	 * <li> INVALID is the next lowest possible Role
	 * <li> The max of two non-null, non-INVALID roles r1 and r2 is
	 * the role with the lowest id.
	 * </ul>
	 * @param r1
	 * @param r2
	 * @return The maximum of (r1, r2).
	 */
	public static Role max(Role r1, Role r2) {
	  if (r1 == null) { return r2; }
	  if (r2 == null) { return r1; }
	  if (r1 == INVALID) { return r2; }
	  if (r2 == INVALID) { return r1; }
	  
	  if (r1.getId() < r2.getId()) {
	    return r1;
	  }
	  return r2;
	}
}