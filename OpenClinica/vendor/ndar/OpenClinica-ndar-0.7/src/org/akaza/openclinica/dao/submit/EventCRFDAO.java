/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.dao.submit;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.dao.core.*;

/**
 * <P>
 * EventCRFDAO.java, data access object for an instance of an event being filled
 * out on a subject. Was originally individual_instrument table in OpenClinica
 * v.1.
 * 
 * @author thickerson
 * 
 * TODO test create and update first thing
 */
public class EventCRFDAO extends AuditableEntityDAO {
  // private DAODigester digester;

  private void setQueryNames() {
    this.findByPKAndStudyName = "findByPKAndStudy";
    this.getCurrentPKName = "getCurrentPK";
    this.getNextPKName = "getNextPK";
  }

  public EventCRFDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public EventCRFDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_EVENTCRF;
  }

  public void setTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.DATE);
    this.setTypeExpected(5, TypeNames.STRING);
    this.setTypeExpected(6, TypeNames.INT);
    this.setTypeExpected(7, TypeNames.INT);
    this.setTypeExpected(8, TypeNames.STRING);//annotations
    this.setTypeExpected(9, TypeNames.TIMESTAMP);//completed
    this.setTypeExpected(10, TypeNames.INT);//validator id
    this.setTypeExpected(11, TypeNames.DATE);//date validate
    this.setTypeExpected(12, TypeNames.TIMESTAMP);//date val. completed
    this.setTypeExpected(13, TypeNames.STRING);
    this.setTypeExpected(14, TypeNames.STRING);
    this.setTypeExpected(15, TypeNames.INT);//owner id
    this.setTypeExpected(16, TypeNames.DATE);
    this.setTypeExpected(17, TypeNames.INT);//subject id
    this.setTypeExpected(18, TypeNames.DATE);//date updated
    this.setTypeExpected(19, TypeNames.INT);//updater
    this.setTypeExpected(20, TypeNames.INT);//form_usage_count_id
  }

  public EntityBean update(EntityBean eb) {
    EventCRFBean ecb = (EventCRFBean) eb;

    ecb.setActive(false);

    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
    variables.put(new Integer(1), new Integer(ecb.getStudyEventId()));
    variables.put(new Integer(2), new Integer(ecb.getCRFVersionId()));
    if (ecb.getDateInterviewed() == null) {
      nullVars.put(new Integer(3), new Integer(Types.DATE));
      variables.put(new Integer(3), null);
    } else {
      variables.put(new Integer(3), ecb.getDateInterviewed());
    }
    variables.put(new Integer(4), ecb.getInterviewerName());
    if (ecb.getCompletionStatusId() == EventCRFBean.COMPLETION_STATUS_NULL) {
      nullVars.put(new Integer(5), new Integer(Types.INTEGER));
      variables.put(new Integer(5), null);
    } else {
      variables.put(new Integer(5), new Integer(ecb.getCompletionStatusId()));
    }
    variables.put(new Integer(6), new Integer(ecb.getStatus().getId()));
    variables.put(new Integer(7), ecb.getAnnotations());
    if (ecb.getDateCompleted() == null) {
      nullVars.put(new Integer(8), new Integer(Types.TIMESTAMP));
      variables.put(new Integer(8), null);
    } else {
      variables.put(new Integer(8), new java.sql.Timestamp(ecb.getDateCompleted().getTime()));
    }
    //variables.put(new Integer(8),ecb.getDateCompleted());

    if (ecb.getValidatorId() == 0) {
      nullVars.put(new Integer(9), new Integer(Types.INTEGER));
      variables.put(new Integer(9), null);
    } else {
      variables.put(new Integer(9), new Integer(ecb.getValidatorId()));
    }

    if (ecb.getDateValidate() == null) {
      nullVars.put(new Integer(10), new Integer(Types.DATE));
      variables.put(new Integer(10), null);
    } else {
      variables.put(new Integer(10), ecb.getDateValidate());
    }
    //variables.put(new Integer(10),ecb.getDateValidate());

    if (ecb.getDateValidateCompleted() == null) {
      nullVars.put(new Integer(11), new Integer(Types.TIMESTAMP));
      variables.put(new Integer(11), null);
    } else {
      variables.put(new Integer(11), new Timestamp(ecb.getDateValidateCompleted().getTime()));
    }
    //variables.put(new Integer(11),ecb.getDateValidateCompleted());
    variables.put(new Integer(12), ecb.getValidatorAnnotations());
    variables.put(new Integer(13), ecb.getValidateString());
    variables.put(new Integer(14), new Integer(ecb.getStudySubjectId()));
    variables.put(new Integer(15), new Integer(ecb.getUpdaterId()));
    if (ecb.getFormUsageCountId() == 0) {
      nullVars.put(new Integer(16), new Integer(Types.INTEGER));
      variables.put(new Integer(16), null);
    } else {
      variables.put(new Integer(16), new Integer(ecb.getFormUsageCountId()));
    }

    //where id=
    variables.put(new Integer(17), new Integer(ecb.getId()));
    this.execute(digester.getQuery("update"), variables, nullVars);

    if (isQuerySuccessful()) {
      ecb.setActive(true);
    }

    return ecb;
  }

  public void markComplete(EventCRFBean ecb, boolean ide) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ecb.getId()));

    if (ide) {
      execute(digester.getQuery("markCompleteIDE"), variables);
    } else {
      execute(digester.getQuery("markCompleteDDE"), variables);
    }
  }

  public EntityBean create(EntityBean eb) {
    EventCRFBean ecb = (EventCRFBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
    int id = getNextPK();
    variables.put(new Integer(1), new Integer(id));
    variables.put(new Integer(2), new Integer(ecb.getStudyEventId()));
    variables.put(new Integer(3), new Integer(ecb.getCRFVersionId()));

    Date interviewed = ecb.getDateInterviewed();
    if (interviewed != null) {
      variables.put(new Integer(4), ecb.getDateInterviewed());
    } else {
      variables.put(new Integer(4), null);
      nullVars.put(new Integer(4), new Integer(Types.DATE));
    }
    //logger.info("ecb.getInterviewerName()" +ecb.getInterviewerName());
    variables.put(new Integer(5), ecb.getInterviewerName());
    
    if (ecb.getCompletionStatusId() == EventCRFBean.COMPLETION_STATUS_NULL) {
        nullVars.put(new Integer(6), new Integer(Types.INTEGER));
        variables.put(new Integer(6), null);
      } else {
        variables.put(new Integer(6), new Integer(ecb.getCompletionStatusId()));
      }
    variables.put(new Integer(7), new Integer(ecb.getStatus().getId()));
    variables.put(new Integer(8), ecb.getAnnotations());
    variables.put(new Integer(9), new Integer(ecb.getOwnerId()));
    variables.put(new Integer(10), new Integer(ecb.getStudySubjectId()));
    variables.put(new Integer(11), ecb.getValidateString());
    variables.put(new Integer(12), ecb.getValidatorAnnotations());
    if (ecb.getFormUsageCountId() == 0) {
      nullVars.put(new Integer(13), new Integer(Types.INTEGER));
      variables.put(new Integer(13), null);
    } else {
      variables.put(new Integer(13), new Integer(ecb.getFormUsageCountId()));
    }

    execute(digester.getQuery("create"), variables, nullVars);

    if (isQuerySuccessful()) {
      ecb.setId(id);
    }

    return ecb;
  }

  public Object getEntityFromHashMap(HashMap hm) {
    EventCRFBean eb = new EventCRFBean();
    this.setEntityAuditInformation(eb, hm);

    eb.setId(((Integer) hm.get("event_crf_id")).intValue());
    eb.setStudyEventId(((Integer) hm.get("study_event_id")).intValue());
    eb.setCRFVersionId(((Integer) hm.get("crf_version_id")).intValue());
    eb.setDateInterviewed((Date) hm.get("date_interviewed"));
    eb.setInterviewerName((String) hm.get("interviewer_name"));
    eb.setCompletionStatusId(((Integer) hm.get("completion_status_id")).intValue());
    eb.setAnnotations((String) hm.get("annotations"));
    eb.setDateCompleted((Date) hm.get("date_completed"));
    eb.setValidatorId(((Integer) hm.get("validator_id")).intValue());
    eb.setDateValidate((Date) hm.get("date_validate"));
    eb.setDateValidateCompleted((Date) hm.get("date_validate_completed"));
    eb.setValidatorAnnotations((String) hm.get("validator_annotations"));
    eb.setValidateString((String) hm.get("validate_string"));
    eb.setStudySubjectId(((Integer) hm.get("study_subject_id")).intValue());
    eb.setFormUsageCountId(hm.get("form_usage_count_id")==null ? 0 : ((Integer) hm.get("form_usage_count_id")).intValue());

    return eb;
  }

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      EventCRFBean eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public EntityBean findByPK(int ID) {
    EventCRFBean eb = new EventCRFBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (EventCRFBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
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

  public ArrayList findAllByStudyEvent(StudyEventBean studyEvent) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyEvent.getId()));

    return executeFindAllQuery("findAllByStudyEvent", variables);
  }

  public ArrayList findAllByCRF(int crfId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(crfId));

    return executeFindAllQuery("findAllByCRF", variables);
  }

  public ArrayList findAllByCRFVersion(int versionId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(versionId));

    return executeFindAllQuery("findAllByCRFVersion", variables);
  }

  public int countAllRequiredByStudyEvent(StudyEventBean studyEvent) {
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(studyEvent.getId()));
    return executeCountQuery("countAllRequiredByStudyEvent", variables);
  }

  public void delete(int eventCRFId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(eventCRFId));

    //TODO: subtract usage count? --AJF
    this.execute(digester.getQuery("delete"), variables);
    return;

  }
}