/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jun Xu
 *  
 */
public class SubjectEventStatus extends Term implements Comparable {
  //waiting for the db to come in sync with our set of terms...
  public static final SubjectEventStatus NOT_SCHEDULED = new SubjectEventStatus(-1, "not scheduled"); //only used by application, not stored in database

  public static final SubjectEventStatus INVALID = new SubjectEventStatus(0, "invalid"); //used if database value is null

  public static final SubjectEventStatus PROPOSED = new SubjectEventStatus(1, "proposed");

  public static final SubjectEventStatus SCHEDULED = new SubjectEventStatus(2, "scheduled");

  public static final SubjectEventStatus IN_PROGRESS = new SubjectEventStatus(3, "in progress");

  public static final SubjectEventStatus COMPLETED = new SubjectEventStatus(4, "completed");

  public static final SubjectEventStatus CANCELED_NO_RESCHEDULE = new SubjectEventStatus(5, "canceled / no reschedule");

  public static final SubjectEventStatus CANCELED_RESCHEDULE = new SubjectEventStatus(6, "canceled / reschedule");

  public static final SubjectEventStatus NO_SHOW_NO_RESCHEDULE = new SubjectEventStatus(7, "no show / no reschedule");

  public static final SubjectEventStatus NO_SHOW_RESCHEDULE = new SubjectEventStatus(8, "no show / reschedule");

//  public static final SubjectEventStatus LOCKED = new SubjectEventStatus(9, "locked");

  private static final SubjectEventStatus[] members = { PROPOSED, SCHEDULED,
      IN_PROGRESS, COMPLETED, CANCELED_NO_RESCHEDULE, CANCELED_RESCHEDULE, NO_SHOW_NO_RESCHEDULE, NO_SHOW_RESCHEDULE };

  private static List<SubjectEventStatus> list = Arrays.asList(members);

  private SubjectEventStatus(int id, String name) {
    super(id, name);
  }

  private SubjectEventStatus() {
  }

  public static boolean contains(int id) {
    return Term.contains(id, list);
  }

  public static SubjectEventStatus get(int id) {
    Term t = Term.get(id, list);
    return (t instanceof SubjectEventStatus ? (SubjectEventStatus)t : null) ;
  }

  public static ArrayList<SubjectEventStatus> toArrayList() {
    return new ArrayList<SubjectEventStatus>(list);
  }
  
  public int compareTo(Object o) {
	if (!this.getClass().equals(o.getClass())) {
		return 0;
	}

	SubjectEventStatus arg = (SubjectEventStatus) o;
	
	return name.compareTo(arg.getName());
  }

}