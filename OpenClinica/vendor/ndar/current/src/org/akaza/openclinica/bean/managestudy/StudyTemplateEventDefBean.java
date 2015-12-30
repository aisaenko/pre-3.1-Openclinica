/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

public class StudyTemplateEventDefBean extends AuditableEntityBean {
  private int studyTemplateId;
  private int studyEventDefinitionId;
  private Float eventDuration;
  private Float idealTimeToNextEvent;
  private Float minTimeToNextEvent;
  private Float maxTimeToNextEvent;
  
  private String eventDurationSt =null;//not in DB
  private String idealTimeToNextEventSt= null;//not in DB
  private String minTimeToNextEventSt=null;// not in DB
  private String maxTimeToNextEventSt=null;//not in DB
  
  private String studyEventDefinitionName="";//not in DB
  
  private String selected ="";//not in DB
  /**
   * @return Returns the studyEventDefinitionName.
   */
  public String getStudyEventDefinitionName() {
    return studyEventDefinitionName;
  }
  /**
   * @param studyEventDefinitionName The studyEventDefinitionName to set.
   */
  public void setStudyEventDefinitionName(String studyEventDefinitionName) {
    this.studyEventDefinitionName = studyEventDefinitionName;
  }
  /**
   * @return Returns the eventDuration.
   */
  public Float getEventDuration() {
    return eventDuration;
  }
  /**
   * @param eventDuration The eventDuration to set.
   */
  public void setEventDuration(Float eventDuration) {
    this.eventDuration = eventDuration;
  }
  /**
   * @return Returns the ideaTimeToNextEvent.
   */
  public Float getIdealTimeToNextEvent() {
    return idealTimeToNextEvent;
  }
  /**
   * @param ideaTimeToNextEvent The ideaTimeToNextEvent to set.
   */
  public void setIdealTimeToNextEvent(Float idealTimeToNextEvent) {
    this.idealTimeToNextEvent = idealTimeToNextEvent;
  }
  /**
   * @return Returns the maxTimeToNextEvent.
   */
  public Float getMaxTimeToNextEvent() {
    return maxTimeToNextEvent;
  }
  /**
   * @param maxTimeToNextEvent The maxTimeToNextEvent to set.
   */
  public void setMaxTimeToNextEvent(Float maxTimeToNextEvent) {
    this.maxTimeToNextEvent = maxTimeToNextEvent;
  }
  /**
   * @return Returns the minTimeToNextEvent.
   */
  public Float getMinTimeToNextEvent() {
    return minTimeToNextEvent;
  }
  /**
   * @param minTimeToNextEvent The minTimeToNextEvent to set.
   */
  public void setMinTimeToNextEvent(Float minTimeToNextEvent) {
    this.minTimeToNextEvent = minTimeToNextEvent;
  }
  /**
   * @return Returns the studyEventDefinitionId.
   */
  public int getStudyEventDefinitionId() {
    return studyEventDefinitionId;
  }
  /**
   * @param studyEventDefinitionId The studyEventDefinitionId to set.
   */
  public void setStudyEventDefinitionId(int studyEventDefinitionId) {
    this.studyEventDefinitionId = studyEventDefinitionId;
  }
  /**
   * @return Returns the studyTemplateId.
   */
  public int getStudyTemplateId() {
    return studyTemplateId;
  }
  /**
   * @param studyTemplateId The studyTemplateId to set.
   */
  public void setStudyTemplateId(int studyTemplateId) {
    this.studyTemplateId = studyTemplateId;
  }
  
  /**
   * @return Returns the selected.
   */
  public String getSelected() {
    return selected;
  }
  /**
   * @param selected The selected to set.
   */
  public void setSelected(String selected) {
    this.selected = selected;
  }
  /**
   * @return Returns the eventDurationSt.
   */
  public String getEventDurationSt() {
    return eventDurationSt;
  }
  /**
   * @param eventDurationSt The eventDurationSt to set.
   */
  public void setEventDurationSt(String eventDurationSt) {
    this.eventDurationSt = eventDurationSt;
  }
  /**
   * @return Returns the ideaTimeToNextEventSt.
   */
  public String getIdealTimeToNextEventSt() {
    return idealTimeToNextEventSt;
  }
  /**
   * @param ideaTimeToNextEventSt The ideaTimeToNextEventSt to set.
   */
  public void setIdealTimeToNextEventSt(String idealTimeToNextEventSt) {
    this.idealTimeToNextEventSt = idealTimeToNextEventSt;
  }
  /**
   * @return Returns the maxTimeToNextEventSt.
   */
  public String getMaxTimeToNextEventSt() {
    return maxTimeToNextEventSt;
  }
  /**
   * @param maxTimeToNextEventSt The maxTimeToNextEventSt to set.
   */
  public void setMaxTimeToNextEventSt(String maxTimeToNextEventSt) {
    this.maxTimeToNextEventSt = maxTimeToNextEventSt;
  }
  /**
   * @return Returns the minTimeToNextEventSt.
   */
  public String getMinTimeToNextEventSt() {
    return minTimeToNextEventSt;
  }
  /**
   * @param minTimeToNextEventSt The minTimeToNextEventSt to set.
   */
  public void setMinTimeToNextEventSt(String minTimeToNextEventSt) {
    this.minTimeToNextEventSt = minTimeToNextEventSt;
  }
  
  
  
  

}
