/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.*;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Privilege extends Term {
    public static final Privilege ADMIN = new Privilege(1, "admin");
	public static final Privilege STUDYDIRECTOR = new Privilege(2, "director");
	public static final Privilege INVESTIGATOR = new Privilege(3, "investigator");
	public static final Privilege RESEARCHASSISTANT = new Privilege(4, "ra");	
	public static final Privilege GUEST = new Privilege(5, "guest");	
	
	private static final Privilege[] members = {ADMIN, STUDYDIRECTOR, INVESTIGATOR, RESEARCHASSISTANT, GUEST};
	public static final List list = Arrays.asList(members);
	
	private Privilege(int id, String name) { super(id, name); }
	private Privilege() {
	}
	
	public static boolean contains(int id) {
		return Term.contains(id, list);
	}
	
	public static Privilege get(int id) {
		return (Privilege) Term.get(id, list);
	}
	
	public static ArrayList toArrayList(){
	  return new ArrayList(list);
	}
}