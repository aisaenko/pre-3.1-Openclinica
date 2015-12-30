/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.*;

//Internationalized name and description in Term.getName and Term.getDescription()

public class Status extends Term implements Comparable {
	// waiting for the db to come in sync with our set of terms...
    public static final Status INVALID = new Status(0, "invalid");
	public static final Status AVAILABLE = new Status(1, "available");
	public static final Status PENDING = new Status(4, "pending");
	public static final Status PRIVATE = new Status(3, "private");
	public static final Status UNAVAILABLE = new Status(2, "unavailable");
	public static final Status DELETED =  new Status(5, "removed");
	public static final Status LOCKED =  new Status(6, "locked");
    public static final Status AUTO_DELETED =  new Status(7, "auto-removed");
	
	private static final Status[] members = { AVAILABLE, PENDING, PRIVATE, UNAVAILABLE, LOCKED, DELETED, AUTO_DELETED};
	private static List list = Arrays.asList(members);
    
    private static final Status[] activeMembers = { AVAILABLE, PENDING, PRIVATE, UNAVAILABLE, LOCKED};
    private static List activeList = Arrays.asList(activeMembers);
	
	private Status(int id, String name) { super(id, name); }
	private Status() {
	}
	
	public static boolean contains(int id) {
		return Term.contains(id, list);
	}
	
	public static Status get(int id) {
		return (Status) Term.get(id, list);
	}
	
	public static ArrayList toArrayList(){
		return new ArrayList(list);
	}
    
    public static ArrayList toActiveArrayList(){
        return new ArrayList(activeList);
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (!this.getClass().equals(o.getClass())) {
			return 0;
		}

		Status arg = (Status) o;
		
		return name.compareTo(arg.getName());
		
//
//		int thisInd = list.indexOf(arg);
//		int argInd = list.indexOf(arg);
//
//		// note that an INVALID status will result in an index of -1;
//		// all other statuses will result in a non-negative index
//		// consider the cases:
//		// 1. thisInd != -1, argInd != -1: compareTo orders the statuses according to their ordering in list (as expected)
//		// 2. thisInd == -1, argInd != -1: thisInd < argInd => compareTo says that INVALID < a normal status, as expected 
//		// 3. thisInd != -1, argInd == -1: thisInd > argInd => compareTo says that a normal status > INVALID, as expected
//		// 4. thisInd == -1, argInd == -1: thisInd == argInd => compareTo says that INVALID == INVALID, as expected
//		
//		if (thisInd < argInd) {
//			return -1;
//		}
//		else if (thisInd > argInd) {
//			return 1;
//		}
//		else {
//			return 0;
//		}
	}
	
	public boolean isInvalid(){
		return this==Status.INVALID;
	}
	public boolean isAvailable(){
		return this==Status.AVAILABLE;
	}
	public boolean isPending(){
		return this==Status.PENDING;
	}
	public boolean isPrivate(){
		return this==Status.PRIVATE;
	}
	public boolean isUnavailable(){
		return this==Status.UNAVAILABLE;
	}
	public boolean isDeleted(){
		return (this==Status.DELETED || this==Status.AUTO_DELETED) ;
	}
	
	public boolean isLocked(){
		return this==Status.LOCKED;
	}
	
	
	
}
