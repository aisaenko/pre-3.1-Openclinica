/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.dao.submit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.ScoreUsageCountDetailBean;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * 
 */
public class ScoreUsageCountDetailDAO extends EntityDAO {

  public ScoreUsageCountDetailDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public ScoreUsageCountDetailDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setQueryNames() {
    this.getCurrentPKName="getCurrentPK";
    this.getNextPKName="getNextPK";
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_SCORE_USAGE_COUNT_DETAIL;
  }

  public void setTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.DATE);
    this.setTypeExpected(5, TypeNames.INT);
  }

  public EntityBean update(EntityBean eb) {
    ScoreUsageCountDetailBean sucb = (ScoreUsageCountDetailBean) eb;

    sucb.setActive(false);

    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(sucb.getScoreUsageCountId()));
    variables.put(new Integer(2), new Integer(sucb.getUserId()));
    variables.put(new Integer(3), sucb.getDateScored());
    variables.put(new Integer(4), new Integer(sucb.getEventCRFId()));

    //where id=
    variables.put(new Integer(5), new Integer(sucb.getId()));
    this.execute(digester.getQuery("update"), variables, nullVars);

    if (isQuerySuccessful()) {
      sucb.setActive(true);
    }

    return sucb;
  }

  public EntityBean create(EntityBean eb) {
    ScoreUsageCountDetailBean sucb = (ScoreUsageCountDetailBean) eb;
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();
    int id = getNextPK();
    variables.put(new Integer(1), new Integer(id));
    variables.put(new Integer(2), new Integer(sucb.getScoreUsageCountId()));
    variables.put(new Integer(3), new Integer(sucb.getUserId()));
    variables.put(new Integer(4), sucb.getDateScored());
    variables.put(new Integer(5), new Integer(sucb.getEventCRFId()));

    execute(digester.getQuery("create"), variables, nullVars);

    if (isQuerySuccessful()) {
      sucb.setId(id);
    }

    return sucb;
  }

  public Object getEntityFromHashMap(HashMap hm) {
    ScoreUsageCountDetailBean eb = new ScoreUsageCountDetailBean();

    eb.setId(((Integer) hm.get("score_usage_count_detail_id")).intValue());
    eb.setScoreUsageCountId(((Integer) hm.get("score_usage_count_id")).intValue());
    eb.setUserId(((Integer) hm.get("user_id")).intValue());
    eb.setDateScored((Date) hm.get("date_scored"));
    eb.setEventCRFId(((Integer) hm.get("event_crf_id")).intValue());

    return eb;
  }

  public Collection<ScoreUsageCountDetailBean> findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList<ScoreUsageCountDetailBean> al = new ArrayList<ScoreUsageCountDetailBean>();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      ScoreUsageCountDetailBean eb = (ScoreUsageCountDetailBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public EntityBean findByPK(int id) {
    ScoreUsageCountDetailBean eb = new ScoreUsageCountDetailBean();
    this.setTypesExpected();

    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(id));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (ScoreUsageCountDetailBean) this.getEntityFromHashMap((HashMap) it.next());
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

  public void delete(int scoreUsageCountId) {
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(scoreUsageCountId));

    this.execute(digester.getQuery("delete"), variables);
    return;

  }
}