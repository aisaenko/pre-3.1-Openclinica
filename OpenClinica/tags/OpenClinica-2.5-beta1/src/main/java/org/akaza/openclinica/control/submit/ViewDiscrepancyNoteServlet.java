/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * @author jxu
 *
 * View the detail of a discrepancy note on the data entry page
 */
public class ViewDiscrepancyNoteServlet extends SecureController {

    Locale locale;
    // < ResourceBundleresexception,respage;

    public static final String ENTITY_ID = "id";

    public static final String PARENT_ID = "parentId";

    public static final String ENTITY_NAME = "name";

    public static final String ENTITY_COLUMN = "column";

    public static final String ENTITY_FIELD = "field";

    public static final String DIS_NOTES = "discrepancyNotes";

    public static final String DIS_NOTE = "discrepancyNote";

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // <
        // resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
        // < respage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);

        String exceptionName = resexception.getString("no_permission_to_create_discrepancy_note");
        String noAccessMessage = respage.getString("you_may_not_create_discrepancy_note") + respage.getString("change_study_contact_sysadmin");

        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(noAccessMessage);
        throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);

        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        int entityId = fp.getInt(ENTITY_ID, true);
        String name = fp.getString(ENTITY_NAME, true);
        String column = fp.getString(ENTITY_COLUMN, true);
        String field = fp.getString(ENTITY_FIELD, true);
        
        String monitor = fp.getString("monitor");
        String blank = fp.getString("blank");
        String enterData = fp.getString("enterData");
        
        request.setAttribute("enterdata", enterData);
        request.setAttribute("monitor", monitor);
        request.setAttribute("blank", blank);  
        
        ArrayList notes = (ArrayList) dndao.findAllByEntityAndColumn(name, entityId, column);

        FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) session.getAttribute(CreateDiscrepancyNoteServlet.FORM_DISCREPANCY_NOTES_NAME);

        HashMap noteTree = new HashMap();

        if (newNotes != null && !newNotes.getNotes(field).isEmpty()) {
            ArrayList newFieldNotes = newNotes.getNotes(field);
            for (int i = 0; i < newFieldNotes.size(); i++) {
                DiscrepancyNoteBean note = (DiscrepancyNoteBean) newFieldNotes.get(i);
                note.setLastUpdator(ub);
                note.setLastDateUpdated(new Date());
                note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
                note.setSaved(false);
                int pId = note.getParentDnId();
                if (pId == 0) {
                    noteTree.put(new Integer(note.getId()), note);
                }
            }
            for (int i = 0; i < newFieldNotes.size(); i++) {
                DiscrepancyNoteBean note = (DiscrepancyNoteBean) newFieldNotes.get(i);
                int pId = note.getParentDnId();
                if (pId > 0) {
                    note.setSaved(false);
                    note.setLastUpdator(ub);
                    note.setLastDateUpdated(new Date());
                    note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
                    DiscrepancyNoteBean parent = (DiscrepancyNoteBean) noteTree.get(new Integer(pId));
                    if (parent != null) {
                        parent.getChildren().add(note);
                    }
                }
            }

        }

        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

        for (int i = 0; i < notes.size(); i++) {
            DiscrepancyNoteBean note = (DiscrepancyNoteBean) notes.get(i);
            note.setColumn(column);
            note.setEntityId(entityId);
            note.setName(name);
            note.setField(field);
            Date lastUpdatedDate = note.getCreatedDate();
            UserAccountBean lastUpdator = (UserAccountBean) udao.findByPK(note.getOwnerId());
            note.setLastUpdator(lastUpdator);
            note.setLastDateUpdated(lastUpdatedDate);
            int pId = note.getParentDnId();
            note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
            if (pId == 0) {
                noteTree.put(new Integer(note.getId()), note);
            }
        }

        for (int i = 0; i < notes.size(); i++) {
            DiscrepancyNoteBean note = (DiscrepancyNoteBean) notes.get(i);
            int pId = note.getParentDnId();
            Date lastUpdatedDate = note.getCreatedDate();
            UserAccountBean lastUpdator = (UserAccountBean) udao.findByPK(note.getOwnerId());
            note.setLastUpdator(lastUpdator);
            note.setLastDateUpdated(lastUpdatedDate);
            note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
            if (pId > 0) {
                DiscrepancyNoteBean parent = (DiscrepancyNoteBean) noteTree.get(new Integer(pId));
                if (parent != null) {
                    parent.getChildren().add(note);
                    if (!note.getCreatedDate().before(parent.getLastDateUpdated())) {
                        parent.setLastDateUpdated(note.getCreatedDate());
                    }
                }
            }
        }

        Set parents = noteTree.keySet();
        Iterator it = parents.iterator();
        while (it.hasNext()) {
            Integer key = (Integer) it.next();
            DiscrepancyNoteBean note = (DiscrepancyNoteBean) noteTree.get(key);
            note.setNumChildren(note.getChildren().size());
        }

        request.setAttribute(DIS_NOTES, noteTree);
        forwardPage(Page.VIEW_DISCREPANCY_NOTE);
    }

}
