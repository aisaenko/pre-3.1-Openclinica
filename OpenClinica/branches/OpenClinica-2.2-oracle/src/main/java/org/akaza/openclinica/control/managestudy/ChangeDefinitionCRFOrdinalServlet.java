/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.view.Page;

/**
 * Processes request to change CRF ordinals in a study event definition
 * 
 * @author jxu
 */
public class ChangeDefinitionCRFOrdinalServlet extends ChangeOrdinalServlet{
 
  /**
   * Override processRequest in super class
   */
  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int current = fp.getInt("current");
    int previous = fp.getInt("previous");
    int definitionId = fp.getInt("id");
    EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
    increase(current, previous,edcdao);
    request.setAttribute("id", new Integer(definitionId).toString());
    forwardPage(Page.VIEW_EVENT_DEFINITION_SERVLET);

  }

  /**
   * Increases the ordinal for current object and decrease the ordinal of the
   * previous one
   * 
   * @param idCurrent
   * @param idPrevious
   * @param dao
   */
  private void increase(int idCurrent, int idPrevious,EventDefinitionCRFDAO dao) {     
    
    if (idCurrent > 0) {
      EventDefinitionCRFBean current = (EventDefinitionCRFBean) dao.findByPK(idCurrent);

      int currentOrdinal = current.getOrdinal();
      current.setOrdinal(currentOrdinal - 1);
      dao.update(current);
    }
    if (idPrevious > 0) {
      EventDefinitionCRFBean previous = (EventDefinitionCRFBean) dao.findByPK(idPrevious);
      int previousOrdinal = previous.getOrdinal();
      previous.setOrdinal(previousOrdinal + 1);
      
      dao.update(previous);
    }
   
  }

}
