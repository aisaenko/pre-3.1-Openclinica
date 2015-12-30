/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.submit;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * @author jxu
 *  
 */
public class SubjectGroupMapDAO extends AuditableEntityDAO {
  
	private void setQueryNames() {
		this.getCurrentPKName = "getCurrentPK";
		this.getNextPKName = "getNextPK";
	}
	
  public SubjectGroupMapDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public SubjectGroupMapDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_SUBJECTGROUPMAP;
  }

  public void setTypesExpected() {
    //subject_group_map_id serial NOT NULL,
    //study_template_id numeric,
    //study_subject_id numeric,
    //study_group_id numeric,
    
    //status_id numeric,
    //owner_id numeric,
    //date_created date,
    //date_updated date,
    
    //update_id numeric,
    //notes varchar(255),
    //study_template_start_dt date
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.INT);

    this.setTypeExpected(5, TypeNames.INT);
    this.setTypeExpected(6, TypeNames.INT);
    this.setTypeExpected(7, TypeNames.DATE);
    this.setTypeExpected(8, TypeNames.DATE);
    
    this.setTypeExpected(9, TypeNames.INT);
    this.setTypeExpected(10, TypeNames.STRING);
    this.setTypeExpected(11, TypeNames.DATE);

  }

  /**
   * <p>
   * getEntityFromHashMap, the method that gets the object from the database
   * query.
   */
  public Object getEntityFromHashMap(HashMap hm) {
    SubjectGroupMapBean eb = new SubjectGroupMapBean();
    super.setEntityAuditInformation(eb, hm);
    //subject_group_map_id serial NOT NULL,
    //study_template_id numeric,
    //study_subject_id numeric,
    //study_group_id numeric,    
    //status_id numeric,
    //owner_id numeric,
    //date_created date,
    //date_updated date,
    //update_id numeric,
    //notes varchar(255),
    //study_template_start_dt date,
    eb.setId(((Integer) hm.get("subject_group_map_id")).intValue());
    eb.setStudyGroupId(((Integer) hm.get("study_group_id")).intValue());
    eb.setStudySubjectId(((Integer) hm.get("study_subject_id")).intValue());
    eb.setStudyGroupClassId(((Integer) hm.get("study_template_id")).intValue());
    eb.setNotes((String)hm.get("notes"));
    eb.setStudyTemplateStartDate((Date) hm.get("study_template_start_dt"));

    return eb;
  }

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      SubjectGroupMapBean eb = (SubjectGroupMapBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }
  
  public Collection findAllByStudySubject(int studySubjectId) {
    setTypesExpected();
    this.setTypeExpected(12, TypeNames.STRING);
    this.setTypeExpected(13, TypeNames.STRING);
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studySubjectId));
    ArrayList alist = this.select(digester.getQuery("findAllByStudySubject"),variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      HashMap hm = (HashMap)it.next();
      SubjectGroupMapBean eb = (SubjectGroupMapBean) this.getEntityFromHashMap(hm);
      eb.setStudyGroupName(((String) hm.get("group_name")));
      eb.setGroupClassName(((String) hm.get("class_name")));
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
    SubjectGroupMapBean eb = new SubjectGroupMapBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (SubjectGroupMapBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  /**
   * Creates a new subject
   */
  public EntityBean create(EntityBean eb) {
    SubjectGroupMapBean sb = (SubjectGroupMapBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
    //INSERT INTO SUBJECT_GROUP_MAP  (study_template_id,
	// study_subject_id,   study_group_id,
	// status_id, owner_id,date_created,  
    // notes, study_template_start_dt)  VALUES (?,?,?,?,?,NOW(),?,?)
    int id = getNextPK();
    variables.put(new Integer(1), new Integer(id));
    variables.put(new Integer(2), new Integer(sb.getStudyGroupClassId()));
    variables.put(new Integer(3), new Integer(sb.getStudySubjectId()));
    variables.put(new Integer(4), new Integer(sb.getStudyGroupId()));
    variables.put(new Integer(5), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(6), new Integer(sb.getOwner().getId()));
    //DATE_CREATED is now()
    variables.put(new Integer(7), sb.getNotes());
    if (sb.getStudyTemplateStartDate()==null) {
    	nullVars.put(new Integer(8), Types.DATE);
        variables.put(new Integer(8), null);
    } else {
        variables.put(new Integer(8), sb.getStudyTemplateStartDate());
    }

    this.execute(digester.getQuery("create"), variables, nullVars);

    if (isQuerySuccessful()) {
      sb.setId(id);
    }

    return sb;
  }

  /**
   * <b>update </b>, the method that returns an updated subject bean after it
   * updates the database.
   * 
   * @return sb, an updated study bean.
   */
  public EntityBean update(EntityBean eb) {
    SubjectGroupMapBean sb = (SubjectGroupMapBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
    //UPDATE SUBJECT_GROUP_MAP SET study_template_id=?,
	//	  STUDY_SUBJECT_ID=?,STUDY_GROUP_ID=?,
	//	  STATUS_ID=?,DATE_UPDATED=?, UPDATE_ID=? , notes = ?, study_template_start_dt
	//	  WHERE SUBJECT_GROUP_MAP_ID=?
    variables.put(new Integer(1), new Integer(sb.getStudyGroupClassId()));
    variables.put(new Integer(2), new Integer(sb.getStudySubjectId()));
    variables.put(new Integer(3), new Integer(sb.getStudyGroupId()));
    variables.put(new Integer(4), new Integer(sb.getStatus().getId()));

    variables.put(new Integer(5), new java.util.Date());
    variables.put(new Integer(6), new Integer(sb.getUpdater().getId()));
    variables.put(new Integer(7), sb.getNotes());
    if (sb.getStudyTemplateStartDate()==null) {
    	nullVars.put(new Integer(8), Types.DATE);
        variables.put(new Integer(8), null);
    } else {
        variables.put(new Integer(8), sb.getStudyTemplateStartDate());
    }

    //where study_template_id=
    variables.put(new Integer(9), new Integer(sb.getId()));
    String sql = digester.getQuery("update");
    this.execute(sql, variables, nullVars);

    return sb;
  }
  
  public ArrayList findAllByStudyGroupClassAndGroup(int studyGroupClassId, int studyGroupId) {
    setTypesExpected();
    this.setTypeExpected(12, TypeNames.STRING);
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyGroupClassId));
    variables.put(new Integer(2), new Integer(studyGroupId));
    ArrayList alist = this.select(digester.getQuery("findAllByStudyTemplateAndGroup"),variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      HashMap hm = (HashMap)it.next();
      SubjectGroupMapBean eb = (SubjectGroupMapBean) this.getEntityFromHashMap(hm);
      eb.setSubjectLabel(((String) hm.get("label")));
      al.add(eb);
    }
    return al;
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

  public ArrayList findAllByStudyGroupId(int studyGroupId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyGroupId));

    return executeFindAllQuery("findAllByStudyGroupId", variables);
  }
  
  public ArrayList findAllByStudyGroupClassId(int studyGroupClassId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyGroupClassId));

    return executeFindAllQuery("findAllByStudyTemplateId", variables);
  }


  
  
}