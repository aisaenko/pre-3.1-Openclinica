package org.akaza.openclinica.service.sdv;

import org.jmesa.core.filter.FilterMatcher;

/**
 * Created by IntelliJ IDEA.
 * User: bruceperry
 * Date: May 19, 2009
 * Time: 11:14:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class SubjectStatusMatcher implements FilterMatcher {
   public boolean evaluate(Object itemValue, String filterValue) {

      String item = String.valueOf(itemValue);
      String filter = String.valueOf(filterValue);

     return filter.equalsIgnoreCase(item);
   }
}
