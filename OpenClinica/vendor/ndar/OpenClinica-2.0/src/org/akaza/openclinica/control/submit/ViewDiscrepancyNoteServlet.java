/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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

/**
 * @author jxu
 * 
 * View the detail of a discrepancy note on the data entry page
 */
public class ViewDiscrepancyNoteServlet extends SecureController {
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
  protected void mayProceed() throws InsufficientPermissionException {
    String exceptionName = "no permission to create a discrepancy note";
    String noAccessMessage = "You may not create a discrepancy note.  "
        + "Please change your active study or contact the System Administrator.";

    if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
      return;
    }

    addPageMessage(noAccessMessage);
    throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
  }

  protected void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
   
    DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
    int entityId = fp.getInt(ENTITY_ID, true);
    String name = fp.getString(ENTITY_NAME, true);
    String column = fp.getString(ENTITY_COLUMN, true);
    String field = fp.getString(ENTITY_FIELD, true);
    ArrayList notes =(ArrayList) dndao.findAllByEntityAndColumn(name, entityId, column);
    
    FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) session
	.getAttribute(CreateDiscrepancyNoteServlet.FORM_DISCREPANCY_NOTES_NAME);
    
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
      UserAccountBean lastUpdator = (UserAccountBean)udao.findByPK(note.getOwnerId());
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
      UserAccountBean lastUpdator = (UserAccountBean)udao.findByPK(note.getOwnerId());
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
    while(it.hasNext()) {  
      Integer key = (Integer)it.next();
      DiscrepancyNoteBean note = (DiscrepancyNoteBean) noteTree.get(key);
      note.setNumChildren(note.getChildren().size());
    }
   
    request.setAttribute(DIS_NOTES, noteTree);
    forwardPage(Page.VIEW_DISCREPANCY_NOTE);
  }
  
  

}