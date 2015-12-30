/**
 * 
 */
package org.akaza.openclinica.bean.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author local
 *
 */
public class SummaryDataEntryStatus extends Term implements Comparable {
	  public static final SummaryDataEntryStatus INVALID = new SummaryDataEntryStatus(0, "invalid");

	  public static final SummaryDataEntryStatus NOT_STARTED = new SummaryDataEntryStatus(1, "not started");

	  public static final SummaryDataEntryStatus DATA_ENTRY_STARTED = new SummaryDataEntryStatus(2, "started");

	  public static final SummaryDataEntryStatus COMPLETED = new SummaryDataEntryStatus(3, "completed");

	  private static final SummaryDataEntryStatus[] members = { NOT_STARTED, DATA_ENTRY_STARTED, COMPLETED };

	  private static List<SummaryDataEntryStatus> list = Arrays.asList(members);

	  private SummaryDataEntryStatus(int id, String name) {
	    super(id, name);
	  }

	  private SummaryDataEntryStatus() {
	  }

	  public static boolean contains(int id) {
	    return Term.contains(id, list);
	  }

	  public static SummaryDataEntryStatus get(int id) {
		Term t = Term.get(id, list);
	    return (t instanceof SummaryDataEntryStatus ? (SummaryDataEntryStatus)t : null);
	  }

	  public static ArrayList<SummaryDataEntryStatus> toArrayList() {
	    return new ArrayList<SummaryDataEntryStatus>(list);
	  }
	  
	  public int compareTo(Object o) {
		if (!this.getClass().equals(o.getClass())) {
		  return 0;
		}

		SummaryDataEntryStatus arg = (SummaryDataEntryStatus) o;
		
		return name.compareTo(arg.getName());
	  }
}
