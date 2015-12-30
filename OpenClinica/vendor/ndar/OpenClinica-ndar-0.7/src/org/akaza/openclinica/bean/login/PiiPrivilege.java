/**
 * 
 */
package org.akaza.openclinica.bean.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.akaza.openclinica.bean.core.Term;

/**
 * @author local
 *
 */
public class PiiPrivilege extends Term {
	public static final PiiPrivilege INVALID = new PiiPrivilege(0, "invalid");

	public static final PiiPrivilege NONE = new PiiPrivilege(1, "none", "none");

	public static final PiiPrivilege VIEW = new PiiPrivilege(2, "view", "view");

	public static final PiiPrivilege EDIT = new PiiPrivilege(3, "edit", "edit");
	
	private static final PiiPrivilege[] members = {
			NONE,
			VIEW,
			EDIT
		};
	
	public static final List<PiiPrivilege> list = Arrays.asList(members);
	
	private List privileges;

	/**
	 * 
	 */
	public PiiPrivilege() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param name
	 */
	public PiiPrivilege(int id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 */
	public PiiPrivilege(int id, String name, String description) {
		super(id, name, description);
		// TODO Auto-generated constructor stub
	}
	
	public static boolean contains(int id) {
		return Term.contains(id, list);
	}
	
	public static PiiPrivilege get(int id) {
		return (PiiPrivilege) Term.get(id, list);
	}	
    
    public static ArrayList toArrayList(){
      return new ArrayList(list);
    }

}
