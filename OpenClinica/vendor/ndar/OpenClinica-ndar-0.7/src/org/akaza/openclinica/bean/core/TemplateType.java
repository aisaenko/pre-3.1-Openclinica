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
 * Type safe enumeration of template types
 * 
 * @author Jun Xu
 */
public class TemplateType extends Term {
  public static final TemplateType INVALID = new TemplateType(0, "invalid");
  public static final TemplateType ARM = new TemplateType(1, "Arm");

  public static final TemplateType FAMILY = new TemplateType(2, "Family/Pedigree");

  public static final TemplateType DEMOGRAPHIC = new TemplateType(3, "Demographic");
  
  public static final TemplateType OTHER = new TemplateType(4, "Other");

  
  private static final TemplateType[] members = {ARM, FAMILY,DEMOGRAPHIC,OTHER};

  public static final List list = Arrays.asList(members);

  private TemplateType(int id, String name) {
    super(id, name);
  }

  private TemplateType() {
  }

  public static boolean contains(int id) {
    return Term.contains(id, list);
  }

  public static TemplateType get(int id) {
    Term t = Term.get(id, list);

    if (!t.isActive()) {
      return INVALID;
    }
    return (TemplateType) t;
  }

  public static boolean findByName(String name) {
    for (int i = 0; i < list.size(); i++) {
      TemplateType temp = (TemplateType) list.get(i);
      if (temp.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
  
  public static TemplateType getByName(String name) {
    for (int i = 0; i < list.size(); i++) {
      TemplateType temp = (TemplateType) list.get(i);
      if (temp.getName().equals(name)) {
        return temp;
      }
    }
    return TemplateType.INVALID;
  }
  
  public static ArrayList toArrayList(){
	return new ArrayList(list);
}

}
