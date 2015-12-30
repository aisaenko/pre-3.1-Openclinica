package org.akaza.openclinica.dao.managestudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindSubjectsFilter implements CriteriaCommand {

    List<Filter> filters = new ArrayList<Filter>();
    HashMap<String, String> columnMapping = new HashMap<String, String>();

    public FindSubjectsFilter() {
        columnMapping.put("label", "ss.label");
        columnMapping.put("status", "ss.status_id");
        columnMapping.put("oid", "ss.oc_oid");
        columnMapping.put("secondaryLabel", "ss.secondary_label");
        columnMapping.put("subject.charGender", "s.gender");

    }

    public void addFilter(String property, Object value) {
        filters.add(new Filter(property, value));
    }

    public String execute(String criteria) {
        String theCriteria = "";
        for (Filter filter : filters) {
            theCriteria += buildCriteria(criteria, filter.getProperty(), filter.getValue());
        }

        return theCriteria;
    }

    private String buildCriteria(String criteria, String property, Object value) {
        if (value != null) {
            if (property.equals("status")) {
                criteria = criteria + " and ";
                criteria = criteria + " " + columnMapping.get(property) + " = " + value.toString() + " ";
            } else {
                criteria = criteria + " and ";
                criteria = criteria + " " + columnMapping.get(property) + " like '%" + value.toString() + "%'" + " ";
            }
        }
        return criteria;
    }

    private static class Filter {
        private final String property;
        private final Object value;

        public Filter(String property, Object value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public Object getValue() {
            return value;
        }
    }

}
