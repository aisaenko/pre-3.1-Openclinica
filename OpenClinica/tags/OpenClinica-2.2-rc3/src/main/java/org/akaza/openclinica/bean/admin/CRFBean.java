/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright ¬© 2003-2005 Akaza Research
 */

package org.akaza.openclinica.bean.admin;

import org.akaza.openclinica.bean.core.*;
import java.util.ArrayList;

/**
 * <P>
 * CRFBean.java, the object for instruments in the database. Each one of these
 * is linked to a version, and are associated with studies through study events.
 * 
 * @author thickerson
 * 
 *  
 */
public class CRFBean extends AuditableEntityBean {
  private int statusId = 1;

  private String description = "";

  private ArrayList versions;// not in DB

  private boolean selected = false; //not in DB

  public CRFBean() {
    versions = new ArrayList();

  }

  /**
   * @return Returns the description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          The description to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return Returns the statusId.
   */
  public int getStatusId() {
    return statusId;
  }

  /**
   * @param statusId
   *          The statusId to set.
   */
  public void setStatusId(int statusId) {
    this.statusId = statusId;
  }

  /**
   * @return Returns the versions.
   */
  public ArrayList getVersions() {
    return versions;
  }

  /**
   * @param versions
   *          The versions to set.
   */
  public void setVersions(ArrayList versions) {
    this.versions = versions;
  }

  public int getVersionNumber() {
    return this.versions.size();
  }

  /**
   * @return Returns the selected.
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * @param selected
   *          The selected to set.
   */
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}