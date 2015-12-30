/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyTemplateEventDefBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/*
 * The data access object that users will access the database for
 * study template event def objects
 */
public class StudyTemplateEventDefDAO extends AuditableEntityDAO{
  protected void setQueryNames() {
    findAllByStudyName = "findAllByStudy";
    findByPKAndStudyName = "findByPKAndStudy";
    getCurrentPKName = "getCurrentPrimaryKey";
    getNextPKName = "getNextPrimaryKey";
  }
  
  public StudyTemplateEventDefDAO(DataSource ds) {
    super(ds);   
    setQueryNames();
  }

  public StudyTemplateEventDefDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  
  }
  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_STUDY_TEMPLATE_EVENT_DEF;
  }
  
  public void setTypesExpected() {
    //STUDY_TEMPLATE_EVENT_DEF_ID     NOT NULL    NUMBER(10)
    //STUDY_TEMPLATE_ID   NOT NULL    NUMBER(10)
    //STUDY_EVENT_DEFINITION_ID   NOT NULL    NUMBER(10)
    //EVENT_DURATION      NUMBER(10,4)
    //IDEAL_TIME_TO_NEXT_EVENT        NUMBER(10,4)
    //MIN_TIME_TO_NEXT_EVENT      NUMBER(10,4)
    //MAX_TIME_TO_NEXT_EVENT      NUMBER(10,4)
    
    //STATUS_ID       NUMBER(10)
    //OWNER_ID        NUMBER(10)
    //DATE_CREATED    DATE
    //UPDATE_ID       NUMBER(10)
    //DATE_UPDATED    DATE
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.FLOAT);
    this.setTypeExpected(5, TypeNames.FLOAT);
    this.setTypeExpected(6, TypeNames.FLOAT);
    this.setTypeExpected(7, TypeNames.FLOAT);
    
    this.setTypeExpected(8, TypeNames.INT);
    this.setTypeExpected(9, TypeNames.INT);
    this.setTypeExpected(10, TypeNames.DATE);
    this.setTypeExpected(11, TypeNames.INT);
    this.setTypeExpected(12, TypeNames.DATE);
    
  }
  
  /**
   * <p>
   * getEntityFromHashMap, the method that gets the object from the database
   * query.
   */
  public Object getEntityFromHashMap(HashMap hm) {
    StudyTemplateEventDefBean eb = new StudyTemplateEventDefBean();
    super.setEntityAuditInformation(eb, hm);  
    eb.setId(((Integer) hm.get("study_template_event_def_id")).intValue());
    eb.setStudyTemplateId(((Integer) hm.get("study_template_id")).intValue());
    eb.setStudyEventDefinitionId(((Integer) hm.get("study_event_definition_id")).intValue());
    eb.setEventDuration(((Float) hm.get("event_duration")));
    eb.setIdealTimeToNextEvent(((Float) hm.get("ideal_time_to_next_event")));
    eb.setMaxTimeToNextEvent(((Float) hm.get("max_time_to_next_event")));
    eb.setMinTimeToNextEvent(((Float) hm.get("min_time_to_next_event")));
         
    if (eb.getEventDuration() != null) {
      eb.setEventDurationSt(eb.getEventDuration().toString());
    }
    if (eb.getIdealTimeToNextEvent() != null) {
      eb.setIdealTimeToNextEventSt(eb.getIdealTimeToNextEvent().toString());
    }
    
    if (eb.getMaxTimeToNextEvent()!= null) {
       eb.setMaxTimeToNextEventSt(eb.getMaxTimeToNextEvent().toString());
    }
    if (eb.getMinTimeToNextEvent() != null) {
      eb.setMinTimeToNextEventSt(eb.getMinTimeToNextEvent().toString());
   }
    
    return eb;
  }
  
  public EntityBean findByPK(int id) {
    StudyTemplateEventDefBean eb = new StudyTemplateEventDefBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(id));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (StudyTemplateEventDefBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }
  
  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyTemplateEventDefBean eb = (StudyTemplateEventDefBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }
  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }
  
  
  public ArrayList findAllByStudyTemplate(int studyTemplateId) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();
    this.setTypeExpected(13, TypeNames.STRING);  
    this.setTypeExpected(14, TypeNames.INT);  

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyTemplateId));
   

    ArrayList alist = this.select(digester.getQuery("findAllByStudyTemplate"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      HashMap hm = (HashMap) it.next();
      StudyTemplateEventDefBean templateDef = (StudyTemplateEventDefBean) this.getEntityFromHashMap(hm);
      templateDef.setStudyEventDefinitionName((String)hm.get("event_name")); 
      answer.add(templateDef);
    }

    return answer;
  }
  
  
  public ArrayList findAllActiveByStudyTemplate(int studyTemplateId) {
    ArrayList answer1 = findAllByStudyTemplate(studyTemplateId);
    ArrayList answer = new ArrayList();
    for (int i=0; i<answer1.size(); i++) {
      StudyTemplateEventDefBean templateDef = (StudyTemplateEventDefBean)answer1.get(i);
      if (templateDef.getStatus().equals(Status.AVAILABLE)) {
        answer.add(templateDef);
      }      
    }
   

    return answer;
  }
  
  /**
   * Creates a new StudyTemplateEventDef Bean
   */
  public EntityBean create(EntityBean eb) {
    StudyTemplateEventDefBean sb = (StudyTemplateEventDefBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
    //INSERT INTO study_template_event_def 
    //   (study_template_id,study_Event_definition_id,
    // event_duration,ideal_time_to_next_event,
    // min_time_to_next_event,max_time_to_next_event,
    //status_id,owner_id,date_created) 
    //   VALUES (?,?,?,?,?,?,?,?,SYSDATE)
    int id = getNextPK();
    variables.put(new Integer(1), new Integer(id));
    variables.put(new Integer(2), new Integer(sb.getStudyTemplateId()));
    variables.put(new Integer(3), new Integer(sb.getStudyEventDefinitionId()));
    
    if (sb.getEventDuration() == null) {
      nullVars.put(new Integer(4), new Integer(Types.FLOAT));
      variables.put(new Integer(4), null);
    } else {
      variables.put(new Integer(4), sb.getEventDuration());
    }
    
    if (sb.getIdealTimeToNextEvent()== null) {
      nullVars.put(new Integer(5), new Integer(Types.FLOAT));
      variables.put(new Integer(5), null);
    } else {
      variables.put(new Integer(5), sb.getIdealTimeToNextEvent());
    }
    
    if (sb.getMinTimeToNextEvent()== null) {
      nullVars.put(new Integer(6), new Integer(Types.FLOAT));
      variables.put(new Integer(6), null);
    } else {
      variables.put(new Integer(6), sb.getMinTimeToNextEvent());
    }
    
    if (sb.getMaxTimeToNextEvent()== null) {
      nullVars.put(new Integer(7), new Integer(Types.FLOAT));
      variables.put(new Integer(7), null);
    } else {
      variables.put(new Integer(7), sb.getMaxTimeToNextEvent());
    }
    
    
    
    variables.put(new Integer(8), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(9), new Integer(sb.getOwner().getId()));
    this.execute(digester.getQuery("create"), variables,nullVars);
    if (isQuerySuccessful()) {
      sb.setId(id);
    }

    return sb;
  }
  
  /**
   * Updates a StudyTemplateEventDef Bean
   */
  public EntityBean update(EntityBean eb) {
    StudyTemplateEventDefBean sb = (StudyTemplateEventDefBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
   // UPDATE study_template_event_def
    //  SET study_template_id=?,study_Event_definition_id=?,
    //  event_duration=?,ideal_time_to_next_event=?,
    //    min_time_to_next_event=?,max_time_to_next_event=?
    //update_id=?,date_updated=?
    //  WHERE study_template_event_def_id=?
    variables.put(new Integer(1), new Integer(sb.getStudyTemplateId()));
    variables.put(new Integer(2), new Integer(sb.getStudyEventDefinitionId()));
    if (sb.getEventDuration() == null) {
      nullVars.put(new Integer(3), new Integer(Types.FLOAT));
      variables.put(new Integer(3), null);
    } else {
      variables.put(new Integer(3), sb.getEventDuration());
    }
    
    if (sb.getIdealTimeToNextEvent()== null) {
      nullVars.put(new Integer(4), new Integer(Types.FLOAT));
      variables.put(new Integer(4), null);
    } else {
      variables.put(new Integer(4), sb.getIdealTimeToNextEvent());
    }
    
    if (sb.getMinTimeToNextEvent()== null) {
      nullVars.put(new Integer(5), new Integer(Types.FLOAT));
      variables.put(new Integer(5), null);
    } else {
      variables.put(new Integer(5), sb.getMinTimeToNextEvent());
    }
    
    if (sb.getMaxTimeToNextEvent()== null) {
      nullVars.put(new Integer(6), new Integer(Types.FLOAT));
      variables.put(new Integer(6), null);
    } else {
      variables.put(new Integer(6), sb.getMaxTimeToNextEvent());
    }
    
    variables.put(new Integer(7), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(8), new Integer(sb.getUpdater().getId()));
    variables.put(new Integer(9), new java.util.Date());
    variables.put(new Integer(10), new Integer(sb.getId()));

    String sql = digester.getQuery("update");
    this.execute(sql, variables, nullVars);

    return sb;
  }

  public Collection findAllByPermission(Object objCurrentUser, int intActionType,
      String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
    ArrayList al = new ArrayList();

    return al;
  }
}
