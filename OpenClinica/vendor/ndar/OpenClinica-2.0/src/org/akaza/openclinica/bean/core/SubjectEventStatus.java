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
  public static final SubjectEventStatus INVALID = new SubjectEventStatus(0, "invalid");

  public static final SubjectEventStatus SCHEDULED = new SubjectEventStatus(1, "scheduled");

  public static final SubjectEventStatus NOT_SCHEDULED = new SubjectEventStatus(2, "not scheduled");

  public static final SubjectEventStatus DATA_ENTRY_STARTED = new SubjectEventStatus(3,
      "data entry started");

  public static final SubjectEventStatus COMPLETED = new SubjectEventStatus(4, "completed");

  public static final SubjectEventStatus STOPPED = new SubjectEventStatus(5, "stopped");

  public static final SubjectEventStatus SKIPPED = new SubjectEventStatus(6, "skipped");

  public static final SubjectEventStatus LOCKED = new SubjectEventStatus(7, "locked");

  private static final SubjectEventStatus[] members = { SCHEDULED, NOT_SCHEDULED,
      DATA_ENTRY_STARTED, COMPLETED, STOPPED, SKIPPED, LOCKED };

  private static List list = Arrays.asList(members);

  private SubjectEventStatus(int id, String name) {
    super(id, name);
  }

  private SubjectEventStatus() {
  }

  public static boolean contains(int id) {
    return Term.contains(id, list);
  }

  public static SubjectEventStatus get(int id) {
    return (SubjectEventStatus) Term.get(id, list);
  }

  public static ArrayList toArrayList() {
    return new ArrayList(list);
  }
  
  public int compareTo(Object o) {
	if (!this.getClass().equals(o.getClass())) {
		return 0;
	}

	SubjectEventStatus arg = (SubjectEventStatus) o;
	
	return name.compareTo(arg.getName());
  }

}