/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author jxu
 *
 * View a single discrepancy note from note list page
 */
public class ViewNoteServlet extends SecureController {

    public static final String NOTE_ID = "id";

    public static final String DIS_NOTE = "singleNote";

    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_permission_to_view_discrepancies") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director_or_study_cordinator"), "1");

    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);

        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        int noteId = fp.getInt(NOTE_ID, true);

        DiscrepancyNoteBean note = (DiscrepancyNoteBean) dndao.findByPK(noteId);

        String entityType = note.getEntityType();
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

        ArrayList notes = dndao.findAllEntityByPK(note.getEntityType(), noteId);

        Date lastUpdatedDate = note.getCreatedDate();
        UserAccountBean lastUpdator = (UserAccountBean) udao.findByPK(note.getOwnerId());

        for (int i = 0; i < notes.size(); i++) {
            DiscrepancyNoteBean n = (DiscrepancyNoteBean) notes.get(i);
            int pId = n.getParentDnId();
            if (pId == 0) {
                note = n;
                note.setLastUpdator((UserAccountBean) udao.findByPK(n.getOwnerId()));
                note.setLastDateUpdated(n.getCreatedDate());
                lastUpdatedDate = note.getLastDateUpdated();
                lastUpdator = note.getLastUpdator();
            }
        }

        for (int i = 0; i < notes.size(); i++) {
            DiscrepancyNoteBean n = (DiscrepancyNoteBean) notes.get(i);
            int pId = n.getParentDnId();
            if (pId > 0) {
                note.getChildren().add(n);
                if (!n.getCreatedDate().before(lastUpdatedDate)) {
                    lastUpdatedDate = n.getCreatedDate();
                    lastUpdator = (UserAccountBean) udao.findByPK(n.getOwnerId());
                    note.setLastUpdator(lastUpdator);
                    note.setLastDateUpdated(lastUpdatedDate);
                    note.setResolutionStatusId(n.getResolutionStatusId());
                    note.setResStatus(ResolutionStatus.get(n.getResolutionStatusId()));
                }
            }
        }
        note.setNumChildren(note.getChildren().size());
        note.setDisType(DiscrepancyNoteType.get(note.getDiscrepancyNoteTypeId()));
        logger.info("Just set Note: " + note.getCrfName() + " column " + note.getColumn());
        request.setAttribute(DIS_NOTE, note);
        forwardPage(Page.VIEW_SINGLE_NOTE);
    }

}
