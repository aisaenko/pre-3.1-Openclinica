/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.managestudy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * @author jxu
 *
 * The data access object that users will access the database for
 * study template objects
 */
public class StudyTemplateDAO extends AuditableEntityDAO {
  protected void setQueryNames() {
    findAllByStudyName = "findAllByStudy";
    findByPKAndStudyName = "findByPKAndStudy";
    getCurrentPKName = "getCurrentPrimaryKey";
    getNextPKName = "getNextPrimaryKey";
  }

  public StudyTemplateDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public StudyTemplateDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_STUDYTEMPLATE;
  }

  public void setTypesExpected() {
    //study_template_id int4,
    //name varchar(30),
    //study_id numeric,
    //owner_id numeric,
    //date_created date,
    //study_template_type_id numeric,
    //status_id numeric,
    //date_updated date,
    //update_id numeric,
    //subject_assignment varchar(30),
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.STRING);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.INT);
    this.setTypeExpected(5, TypeNames.DATE);

    this.setTypeExpected(6, TypeNames.INT);
    this.setTypeExpected(7, TypeNames.INT);
    this.setTypeExpected(8, TypeNames.DATE);
    this.setTypeExpected(9, TypeNames.INT);
    this.setTypeExpected(10, TypeNames.STRING);
  }

  /**
   * <p>
   * getEntityFromHashMap, the method that gets the object from the database
   * query.
   */
  public Object getEntityFromHashMap(HashMap hm) {
    StudyTemplateBean eb = new StudyTemplateBean();
    super.setEntityAuditInformation(eb, hm);
    //STUDY_GROUP_ID NAME STUDY_ID OWNER_ID DATE_CREATED
    //STUDY_TEMPLATE_TYPE_ID STATUS_ID DATE_UPDATED UPDATE_ID
    eb.setId(((Integer) hm.get("study_template_id")).intValue());
    eb.setName((String) hm.get("name"));
    eb.setStudyId(((Integer) hm.get("study_id")).intValue());
    eb.setStudyTemplateTypeId(((Integer) hm.get("study_template_type_id")).intValue());
    eb.setSubjectAssignment((String) hm.get("subject_assignment"));
    return eb;
  }

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyTemplateBean eb = (StudyTemplateBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }
  
  public ArrayList findAllByStudy(StudyBean study) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();
    this.setTypeExpected(11, TypeNames.STRING);
    this.setTypeExpected(12, TypeNames.STRING);

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(study.getId()));
    variables.put(new Integer(2), new Integer(study.getId()));

    ArrayList alist = this.select(digester.getQuery("findAllByStudy"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      HashMap hm = (HashMap) it.next();
      StudyTemplateBean template = (StudyTemplateBean) this.getEntityFromHashMap(hm);
      template.setStudyName((String)hm.get("study_name"));
      template.setStudyTemplateTypeName((String) hm.get("type_name"));
      answer.add(template);
    }

    return answer;
  }
  
  public ArrayList findAllActiveByStudy(StudyBean study) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();
    this.setTypeExpected(11, TypeNames.STRING);
    this.setTypeExpected(12, TypeNames.STRING);

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(study.getId()));
    variables.put(new Integer(2), new Integer(study.getId()));

    ArrayList alist = this.select(digester.getQuery("findAllActiveByStudy"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      HashMap hm = (HashMap) it.next();
      StudyTemplateBean template = (StudyTemplateBean) this.getEntityFromHashMap(hm);
      template.setStudyName((String)hm.get("study_name"));
      System.out.println("study Name" + template.getStudyName());
      template.setStudyTemplateTypeName((String) hm.get("type_name"));
      answer.add(template);
    }

    return answer;
  }

  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }
  
  public EntityBean findAllByPK(int id) {
    StudyTemplateBean eb = new StudyTemplateBean();
    this.setTypesExpected();
    this.setTypeExpected(11, TypeNames.STRING);
    this.setTypeExpected(12, TypeNames.STRING);

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(id));

    String sql = digester.getQuery("findAllByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      HashMap hm = (HashMap) it.next();
      eb = (StudyTemplateBean) this.getEntityFromHashMap(hm);
      eb.setStudyName((String)hm.get("study_name"));
      eb.setStudyTemplateTypeName((String) hm.get("type_name"));
//     System.out.println("template type:" + eb.getStudyTemplateTypeName());
    }

    return eb;
  }

  public EntityBean findByPK(int id) {
    StudyTemplateBean eb = new StudyTemplateBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(id));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (StudyTemplateBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  /**
   * THIS IS NEVER USED
   * @param studyId
   * @return
   */
  public EntityBean findByStudyId(int studyId) {
    StudyTemplateBean eb = new StudyTemplateBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyId));

    String sql = digester.getQuery("findByStudyId");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (StudyTemplateBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  /**
   * Creates a new StudyTemplate
   */
  public EntityBean create(EntityBean eb) {
    StudyTemplateBean sb = (StudyTemplateBean) eb;
    HashMap variables = new HashMap();
    //INSERT INTO study_template 
	//   (NAME,STUDY_ID,OWNER_ID,DATE_CREATED, study_template_type_ID,
	//    STATUS_ID,subject_assignment) 
	//   VALUES (?,?,?,NOW(),?,?,?)
    int id = getNextPK();
    variables.put(new Integer(1), new Integer(id));
    variables.put(new Integer(2), sb.getName());
    variables.put(new Integer(3), new Integer(sb.getStudyId()));
    variables.put(new Integer(4), new Integer(sb.getOwner().getId()));
    variables.put(new Integer(5), new Integer(sb.getStudyTemplateTypeId()));
    //	Date_created is now()
    variables.put(new Integer(6), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(7), sb.getSubjectAssignment());
    this.execute(digester.getQuery("create"), variables);
    if (isQuerySuccessful()) {
      sb.setId(id);
    }

    return sb;
  }

  /**
   * Updates a StudyTemplate
   */
  public EntityBean update(EntityBean eb) {
    StudyTemplateBean sb = (StudyTemplateBean) eb;
    HashMap variables = new HashMap();

    //UPDATE study_template SET NAME=?,STUDY_ID=?, study_template_type_ID=?,
    //STATUS_ID=?, DATE_UPDATED=?,UPDATE_ID=?,
    //subject_assignment=? WHERE study_template_id=?
    variables.put(new Integer(1), sb.getName());
    variables.put(new Integer(2), new Integer(sb.getStudyId()));
    variables.put(new Integer(3), new Integer(sb.getStudyTemplateTypeId()));

    variables.put(new Integer(4), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(5), new java.util.Date());
    variables.put(new Integer(6), new Integer(sb.getUpdater().getId()));
    variables.put(new Integer(7), sb.getSubjectAssignment());
    variables.put(new Integer(8), new Integer(sb.getId()));

    String sql = digester.getQuery("update");
    this.execute(sql, variables);

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
