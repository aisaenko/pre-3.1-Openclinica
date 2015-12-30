package org.akaza.openclinica.bean.subject;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.login.UserAccountRow;
import org.akaza.openclinica.core.EntityBeanRow;

public class SubjectEntryLabelRow extends EntityBeanRow {
    private Logger logger = Logger.getLogger(getClass().getName());
    public static final int COL_LABEL = 0;
    public static final int COL_DESC = 1;
    public static final int COL_OWNER = 2;
    public static final int COL_CREATE_DATE = 3;
    
    @Override
    protected int compareColumn(Object row, int sortingColumn) {
        if (!row.getClass().equals(SubjectEntryLabelRow.class)) {
            return 0;
        }
        
        SubjectEntryLabelBean selb = (SubjectEntryLabelBean) bean; 
        SubjectEntryLabelBean selb2 = (SubjectEntryLabelBean) ((SubjectEntryLabelRow)row).bean; 

        int answer = 0;
        switch (sortingColumn) {
            case COL_LABEL:
                answer = selb.getName().toLowerCase().compareTo(selb2.getName().toLowerCase());
                break;
            case COL_DESC:
                answer = selb.getDescription().toLowerCase().compareTo(selb.getDescription().toLowerCase());
                break;
            case COL_OWNER:
                answer = selb.getOwner().getName().toLowerCase().compareTo(selb.getOwner().getName().toLowerCase());
                break;
            case COL_CREATE_DATE:
                answer = selb.getCreatedDate().compareTo(selb.getCreatedDate());
                break;
        }
        
        return answer;
    }

    @Override
    public ArrayList generatRowsFromBeans(ArrayList beans) {
        ArrayList answer = new ArrayList();
        
        for (int i = 0; i < beans.size(); i++) {
            try {
                SubjectEntryLabelRow row = new SubjectEntryLabelRow();
                row.setBean((SubjectEntryLabelBean) beans.get(i));
                answer.add(row);
            } catch (Exception e) { 
                logger.severe(e.getMessage());
            }
        }
        
        return answer;
    }

}
