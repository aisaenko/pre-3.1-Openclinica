/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.akaza.openclinica.bean.core.Term;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GroupType  extends Term {
  public static final GroupType INVALID = new GroupType(0, "invalid");
  public static final GroupType TREATMENT = new GroupType(1, "treatment");
  public static final GroupType CONTROL = new GroupType(1, "control");
  

  private static final GroupType[] members = {TREATMENT,CONTROL};

  public static final List list = Arrays.asList(members);

  private List privileges;

  private GroupType(int id, String name) {
    super(id, name);
  }

  private GroupType() {
  }

  public static boolean contains(int id) {
    return Term.contains(id, list);
  }

  public static GroupType get(int id) {
    return (GroupType) Term.get(id, list);
  }

 

  public static ArrayList toArrayList() {
    return new ArrayList(list);
  }


}
