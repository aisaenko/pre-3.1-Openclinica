/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.dao.submit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.ScoreUsageCountBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * 
 */
public class ScoreUsageCountDAO extends AuditableEntityDAO {

  public ScoreUsageCountDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public ScoreUsageCountDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setQueryNames() {
    this.getCurrentPKName="getCurrentPK";
    this.getNextPKName="getNextPK";
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_SCORE_USAGE_COUNT;
  }

  public void setTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.INT);
  }

  public EntityBean update(EntityBean eb) {
    ScoreUsageCountBean sucb = (ScoreUsageCountBean) eb;

    sucb.setActive(false);

    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(sucb.getCRFVersionId()));
    variables.put(new Integer(2), new Integer(sucb.getCountUsed()));
    variables.put(new Integer(3), new Integer(sucb.getCountPurchased()));

    //where id=
    variables.put(new Integer(4), new Integer(sucb.getId()));
    this.execute(digester.getQuery("update"), variables, nullVars);

    if (isQuerySuccessful()) {
      sucb.setActive(true);
    }

    return sucb;
  }

  public void updateUsageByCrfVersion(int crfVersionId, int count) {
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(crfVersionId));
    variables.put(new Integer(2), new Integer(count));

    execute(digester.getQuery("updateUsageByCrfVersion"), variables);
  }

  public boolean incrementCountUsageByCrfVersion(ScoreUsageCountBean sucb) {
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(sucb.getCRFVersionId()));

    execute(digester.getQuery("incrementCountUsageByCrfVersion"), variables);
    ScoreUsageCountBean sucbFromDb = (ScoreUsageCountBean) findByCRFVersion(sucb.getCRFVersionId());
    sucb.setCountPurchased(sucbFromDb.getCountPurchased());
    sucb.setCountUsed(sucbFromDb.getCountUsed());
    return (sucbFromDb.getCountUsed() <= sucbFromDb.getCountPurchased());
  }

  public EntityBean create(EntityBean eb) {
    ScoreUsageCountBean sucb = (ScoreUsageCountBean) eb;
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();
    int id = getNextPK();
    variables.put(new Integer(1), new Integer(id));
    variables.put(new Integer(2), new Integer(sucb.getCRFVersionId()));
    variables.put(new Integer(3), new Integer(sucb.getCountPurchased()));
    variables.put(new Integer(4), new Integer(sucb.getCountUsed()));

    execute(digester.getQuery("create"), variables, nullVars);

    if (isQuerySuccessful()) {
      sucb.setId(id);
    }

    return sucb;
  }

  public Object getEntityFromHashMap(HashMap hm) {
    ScoreUsageCountBean eb = new ScoreUsageCountBean();
//    this.setEntityAuditInformation(eb, hm);

    eb.setId(((Integer) hm.get("score_usage_count_id")).intValue());
    eb.setCRFVersionId(((Integer) hm.get("crf_version_id")).intValue());
    eb.setCountPurchased(hm.get("count_purchased")==null ? 0 : ((Integer) hm.get("count_purchased")).intValue());
    eb.setCountUsed(hm.get("count_used")==null ? 0 : ((Integer) hm.get("count_used")).intValue());

    return eb;
  }

  public Collection<ScoreUsageCountBean> findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList<ScoreUsageCountBean> al = new ArrayList<ScoreUsageCountBean>();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      ScoreUsageCountBean eb = (ScoreUsageCountBean) this.getEntityFromHashMap((HashMap) it.next());
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
    ScoreUsageCountBean eb = new ScoreUsageCountBean();
    this.setTypesExpected();

    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(id));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (ScoreUsageCountBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  public ArrayList findAllByCRF(int crfId) {
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(crfId));

    return executeFindAllQuery("findAllByCRF", variables);
  }

  public ArrayList findAllByPublisher(int publisherId) {
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(publisherId));

    return executeFindAllQuery("findAllByPublisher", variables);
  }

  public EntityBean findByCRFVersion(int crfVersionId) {
    ScoreUsageCountBean eb = new ScoreUsageCountBean();
    this.setTypesExpected();

    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(crfVersionId));

    String sql = digester.getQuery("findByCrfVersion");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (ScoreUsageCountBean) this.getEntityFromHashMap((HashMap) it.next());
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