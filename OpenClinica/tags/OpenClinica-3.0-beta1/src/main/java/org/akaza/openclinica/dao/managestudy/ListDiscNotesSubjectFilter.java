package org.akaza.openclinica.dao.managestudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListDiscNotesSubjectFilter implements CriteriaCommand {

    List<Filter> filters = new ArrayList<Filter>();
    HashMap<String, String> columnMapping = new HashMap<String, String>();

    public ListDiscNotesSubjectFilter() {
        columnMapping.put("studySubject.label", "ss.label");
        columnMapping.put("studySubject.status", "ss.status_id");
        columnMapping.put("enrolledAt", "ST.unique_identifier");
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
            if (property.equals("dn.discrepancy_note_type_id")) {
                int typeId = Integer.valueOf(value.toString());
                if(typeId > 0 && typeId < 10 ) {
                    criteria += " and " + property + " = " + value.toString() + " ";
                }
            } else if (property.equals("dn.resolution_status_id")) {
                criteria += value.toString();
            } else if (property.equals("status")) {
                criteria = criteria + " and ";
                criteria = criteria + " " + columnMapping.get(property) + " = " + value.toString() + " ";
            } else if (property.startsWith("sed_")) {
                if (!value.equals("2")) {
                    criteria += " and ";
                    criteria += " ( se.study_event_definition_id = " + property.substring(4);
                    criteria += " and se.subject_event_status_id = " + value + " )";
                } else {
                    criteria += " AND (se.study_subject_id is null or (se.study_event_definition_id != " + property.substring(4);
                    criteria += " AND (select count(*) from  study_subject ss1 LEFT JOIN study_event ON ss1.study_subject_id = study_event.study_subject_id";
                    criteria +=
                        " where  study_event.study_event_definition_id =" + property.substring(4) + " and ss.study_subject_id = ss1.study_subject_id) =0))";

                }
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
