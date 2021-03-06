/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A type-safe enumeration class for resolution status of discrepancy notes
 * 
 * @author Jun Xu
 * 
 */
public class ResolutionStatus extends Term {
  public static final ResolutionStatus INVALID = new ResolutionStatus(0, "invalid", null);

  public static final ResolutionStatus OPEN = new ResolutionStatus(1, "New/Open", null);

  public static final ResolutionStatus UPDATED =
    new ResolutionStatus(2, "Updated",null);

  public static final ResolutionStatus CLOSED = 
    new ResolutionStatus(3, "Resolved/Closed", null);

  
  private static final ResolutionStatus[] members = 
  {OPEN,UPDATED,CLOSED};

  public static final List list = Arrays.asList(members);

  private List privileges;

  private ResolutionStatus(int id, String name, Privilege[] myPrivs) {
    super(id, name);
    
  }

  private ResolutionStatus() {
  }

  public static boolean contains(int id) {
    return Term.contains(id, list);
  }

  public static ResolutionStatus get(int id) {
    return (ResolutionStatus) Term.get(id, list);
  }

  public static ResolutionStatus getByName(String name) {
    for (int i = 0; i < list.size(); i++) {
      ResolutionStatus temp = (ResolutionStatus) list.get(i);
      if (temp.getName().equals(name)) {
        return temp;
      }
    }
    return INVALID;
  }

  public static ArrayList toArrayList() {
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

}
