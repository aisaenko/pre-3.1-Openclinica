/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.Arrays;
import java.util.List;

/**
 * BL - Boolean
 * 
 * BN – BooleanNonNull
 * 
 * ED – Encapsulated Data (Files w/ specified MIME type e.g. ED-pdf, ED-jpg
 * defined in a separate ED Types table in the future)
 * 
 * TEL - A telecommunication address (TEL), such as a URL for HTTP or FTP, which
 * will resolve to precisely the same binary data that could as well have been
 * provided as inline data.
 * 
 * ST – Character String
 * 
 * INTEGER – Integer
 * 
 * REAL – Floating
 * 
 * SET - a value that contains other distinct values in no particular order.
 * 
 * DATE - a date type
 * @author ssachs
 */
public class ItemDataType extends Term {
  public static final ItemDataType INVALID = new ItemDataType(0, "invalid", "Invalid Type");
  public static final ItemDataType BL = new ItemDataType(1, "bl", "Boolean");

  public static final ItemDataType BN = new ItemDataType(2, "bln", "Boolean Non-Null");

  public static final ItemDataType ED = new ItemDataType(3, "ed", "encapsulated data");

  public static final ItemDataType TEL = new ItemDataType(4, "tel", "URL");

  public static final ItemDataType ST = new ItemDataType(5, "st", "character string");

  public static final ItemDataType INTEGER = new ItemDataType(6, "int", "integer");

  public static final ItemDataType REAL = new ItemDataType(7, "real", "floating");

  public static final ItemDataType SET = new ItemDataType(8, "set", "set");
  public static final ItemDataType DATE = new ItemDataType(9, "date", "date");

  private static final ItemDataType[] members = { BL, BN, ED, TEL, ST, INTEGER, REAL, SET,DATE };

  public static final List list = Arrays.asList(members);

  private ItemDataType(int id, String name, String description) {
    super(id, name, description);
  }

  private ItemDataType() {
  }

  public static boolean contains(int id) {
    return Term.contains(id, list);
  }

  public static ItemDataType get(int id) {
    return (ItemDataType) Term.get(id, list);
  }

  public static ItemDataType getByName(String name) {
    for (int i = 0; i < list.size(); i++) {
      ItemDataType temp = (ItemDataType) list.get(i);
      if (temp.getName().equals(name)) {
        return temp;
      }
    }
    return ItemDataType.INVALID;
  }

  public static boolean findByName(String name) {
    for (int i = 0; i < list.size(); i++) {
      ItemDataType temp = (ItemDataType) list.get(i);
      if (temp.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
}