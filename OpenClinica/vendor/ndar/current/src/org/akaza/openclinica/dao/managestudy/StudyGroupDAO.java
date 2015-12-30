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
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyTemplateBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * @author jxu
 */
public class StudyGroupDAO extends AuditableEntityDAO {
  // private DAODigester digester;

  protected void setQueryNames() {
    findAllByStudyName = "findAllByStudy";
    findByPKAndStudyName = "findByPKAndStudy";
  }

  public StudyGroupDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public StudyGroupDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_STUDYGROUP;
  }

  public void setTypesExpected() {
    //study_group_id int4 ,
    //name varchar(255),
    //description varchar(1000),
    //study_template_id numeric,
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.STRING);
    this.setTypeExpected(3, TypeNames.STRING);
    this.setTypeExpected(4, TypeNames.INT);    
  }

  /**
   * <p>
   * getEntityFromHashMap, the method that gets the object from the database
   * query.
   */
  public Object getEntityFromHashMap(HashMap hm) {
    StudyGroupBean eb = new StudyGroupBean();
    eb.setId(((Integer) hm.get("study_group_id")).intValue());
    eb.setName((String) hm.get("name"));
    eb.setDescription((String)hm.get("description"));
    eb.setStudyGroupClassId(((Integer)hm.get("study_template_id")).intValue());

    return eb;
  }

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyGroupBean eb = (StudyGroupBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }
  
  public ArrayList findAllByGroupClass(StudyTemplateBean group) {
    ArrayList answer = new ArrayList();
    this.setTypesExpected();    

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(group.getId()));  

    ArrayList alist = this.select(digester.getQuery("findAllByGroupClass"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      HashMap hm = (HashMap) it.next();
      StudyGroupBean g = (StudyGroupBean) this.getEntityFromHashMap(hm);     
      answer.add(g);
    }

    return answer;
  }

  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public EntityBean findByPK(int id) {
    StudyGroupBean eb = new StudyGroupBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(id));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (StudyGroupBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  public EntityBean findByStudyId(int studyId) {
    StudyGroupBean eb = new StudyGroupBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyId));

    String sql = digester.getQuery("findByStudyId");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (StudyGroupBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  /**
   * Creates a new StudyGroup
   */
  public EntityBean create(EntityBean eb) {
    StudyGroupBean sb = (StudyGroupBean) eb;
    HashMap variables = new HashMap();
   
    variables.put(new Integer(1), sb.getName());
    variables.put(new Integer(2), sb.getDescription());   
    variables.put(new Integer(3), new Integer(sb.getStudyGroupClassId()));  

    this.execute(digester.getQuery("create"), variables);

    return sb;
  }

  /**
   * Updates a StudyGroup
   */
  public EntityBean update(EntityBean eb) {
    StudyGroupBean sb = (StudyGroupBean) eb;
    HashMap variables = new HashMap();

    //UPDATE study_group SET study_template_id=?, name=?,
    //    description=?
    //WHERE study_group_id=?
    variables.put(new Integer(1), new Integer(sb.getStudyGroupClassId()));
    variables.put(new Integer(2), sb.getName());
    variables.put(new Integer(3), sb.getDescription());
    variables.put(new Integer(4), new Integer(sb.getId()));
   

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