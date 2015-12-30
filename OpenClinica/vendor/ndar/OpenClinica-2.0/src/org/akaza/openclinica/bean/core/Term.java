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
 * Superclass for controlled vocabulary terms like status, role, etc.
 */
public class Term extends EntityBean {
	protected String description;
	
	public Term() { super(); }
	
	public Term(int id, String name) {
		setId(id);
		setName(name);
		setDescription("");
	}
	
	public Term(int id, String name, String description) {
		setId(id);
		setName(name);
		setDescription(description);
	}
	
	
	
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean equals(Term t) {
		return id == t.id;
	}
	
	public int hashCode() {
		return id;
	}
	
	public static boolean contains(int id, List list) {
		Term t = new Term(id, "");
		
		for (int i = 0; i < list.size(); i++) {
			Term temp = (Term) list.get(i);
			if (temp.equals(t)) {             
				return true;
			}
		}
		return false;
	}
	
	public static Term get(int id, List list) {
		Term t = new Term(id, "");
		
		for (int i = 0; i < list.size(); i++) {
			Term temp = (Term) list.get(i);
			if (temp.equals(t)) {
               //System.out.println("temp id" + temp.getId());
               //System.out.println("t id" + t.getId());
				return temp;
			}
		}

		return new Term();
	}
}