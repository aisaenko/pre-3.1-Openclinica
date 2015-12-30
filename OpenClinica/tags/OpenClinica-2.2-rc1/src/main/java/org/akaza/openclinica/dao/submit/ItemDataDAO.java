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
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.dao.core.*;
import org.akaza.openclinica.bean.core.Status;

/**
 * <P>
 * ItemDataDAO.java, the equivalent to AnswerDAO in the original code base.
 * 
 * @author thickerson
 * 
 *  
 */
public class ItemDataDAO extends AuditableEntityDAO {
  public Collection findMinMaxDates() {
    ArrayList al = new ArrayList();
    ArrayList alist = this.select(digester.getQuery("findMinMaxDates"));
    //al =
    return al;
  }

  // private DAODigester digester;

  private void setQueryNames() {
    getCurrentPKName = "getCurrentPK";
  }

  public ItemDataDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public ItemDataDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_ITEMDATA;
  }

  public void setTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.INT);
    this.setTypeExpected(5, TypeNames.STRING);
    this.setTypeExpected(6, TypeNames.DATE);
    this.setTypeExpected(7, TypeNames.DATE);
    this.setTypeExpected(8, TypeNames.INT);//owner id
    this.setTypeExpected(9, TypeNames.INT);//update id
    this.setTypeExpected(10, TypeNames.INT);//ordinal
  }

  public EntityBean update(EntityBean eb) {
    ItemDataBean idb = (ItemDataBean) eb;

    idb.setActive(false);

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(idb.getEventCRFId()));
    variables.put(new Integer(2), new Integer(idb.getItemId()));
    variables.put(new Integer(3), new Integer(idb.getStatus().getId()));
    variables.put(new Integer(4), idb.getValue());
    variables.put(new Integer(5), new Integer(idb.getUpdaterId()));
    variables.put(new Integer(6), new Integer(idb.getOrdinal()));
    variables.put(new Integer(7), new Integer(idb.getId()));
    this.execute(digester.getQuery("update"), variables);

    if (isQuerySuccessful()) {
      idb.setActive(true);
    }

    return idb;
  }
  
  /**
   * This will update item data value 
   * @param eb
   * @return
   */
  public EntityBean updateValue(EntityBean eb) {
    ItemDataBean idb = (ItemDataBean) eb;

    idb.setActive(false);

    HashMap variables = new HashMap();    
    variables.put(new Integer(1), new Integer(idb.getStatus().getId()));
    variables.put(new Integer(2), idb.getValue());
    variables.put(new Integer(3), new Integer(idb.getUpdaterId()));   
    variables.put(new Integer(4), new Integer(idb.getId()));
    this.execute(digester.getQuery("updateValue"), variables);

    if (isQuerySuccessful()) {
      idb.setActive(true);
    }

    return idb;
  }

  public EntityBean create(EntityBean eb) {
    ItemDataBean idb = (ItemDataBean) eb;
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(idb.getEventCRFId()));
    variables.put(new Integer(2), new Integer(idb.getItemId()));
    variables.put(new Integer(3), new Integer(idb.getStatus().getId()));
    variables.put(new Integer(4), idb.getValue());
    variables.put(new Integer(5), new Integer(idb.getOwnerId()));
    variables.put(new Integer(6), new Integer(idb.getOrdinal()));
    this.execute(digester.getQuery("create"), variables);

    if (isQuerySuccessful()) {
      idb.setId(getCurrentPK());
    }

    return idb;
  }

  public Object getEntityFromHashMap(HashMap hm) {
    ItemDataBean eb = new ItemDataBean();
    this.setEntityAuditInformation(eb, hm);
    eb.setId(((Integer) hm.get("item_data_id")).intValue());
    eb.setEventCRFId(((Integer) hm.get("event_crf_id")).intValue());
    eb.setItemId(((Integer) hm.get("item_id")).intValue());
    eb.setValue((String) hm.get("value"));
    eb.setStatus(Status.get(((Integer) hm.get("status_id")).intValue()));
    eb.setOrdinal(((Integer) hm.get("ordinal")).intValue());
    return eb;
  }

  public Collection findAll() {
    setTypesExpected();

    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      ItemDataBean eb = (ItemDataBean) this.getEntityFromHashMap((HashMap) it.next());
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
    ItemDataBean eb = new ItemDataBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (ItemDataBean) this.getEntityFromHashMap((HashMap) it.next());
    }
    return eb;
  }

  public void delete(int itemId) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(itemId));

    this.execute(digester.getQuery("delete"), variables);
    return;

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

  public ArrayList findAllBySectionIdAndEventCRFId(int sectionId, int eventCRFId) {
    setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(sectionId));
    variables.put(new Integer(2), new Integer(eventCRFId));

    return this.executeFindAllQuery("findAllBySectionIdAndEventCRFId", variables);
  }
  
  public ArrayList findAllActiveBySectionIdAndEventCRFId(int sectionId, int eventCRFId) {
    setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(sectionId));
    variables.put(new Integer(2), new Integer(eventCRFId));

    return this.executeFindAllQuery("findAllActiveBySectionIdAndEventCRFId", variables);
  }

  public ArrayList findAllByEventCRFId(int eventCRFId) {
    setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(eventCRFId));

    return this.executeFindAllQuery("findAllByEventCRFId", variables);
  }

  public ArrayList findAllBlankRequiredByEventCRFId(int eventCRFId, int crfVersionId) {
    setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(eventCRFId));
    variables.put(new Integer(2), new Integer(crfVersionId));

    return this.executeFindAllQuery("findAllBlankRequiredByEventCRFId", variables);
  }

  public void updateStatusByEventCRF(EventCRFBean eventCRF, Status s) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(s.getId()));
    variables.put(new Integer(2), new Integer(eventCRF.getId()));

    String sql = digester.getQuery("updateStatusByEventCRF");
    execute(sql, variables);

    return;
  }

  public ItemDataBean findByItemIdAndEventCRFId(int itemId, int eventCRFId) {
    setTypesExpected();
    ItemDataBean answer = new ItemDataBean();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(itemId));
    variables.put(new Integer(2), new Integer(eventCRFId));

    EntityBean eb = this.executeFindByPKQuery("findByItemIdAndEventCRFId", variables);

    if (!eb.isActive()) {
      return new ItemDataBean();
    } else {
      return (ItemDataBean) eb;
    }
  }
  
  public int findAllRequiredByEventCRFId(EventCRFBean ecb) {
    setTypesExpected();     
    int answer=0;
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ecb.getId()));
    String sql = digester.getQuery("findAllRequiredByEventCRFId");    
    ArrayList rows = this.select(sql, variables);

    if (rows.size() > 0) {    
      answer = rows.size();
    }

    return answer; 
}
  
  /**
   * Gets the maximum ordinal for item data in a given item group
   * in a given section and event crf
   * @param ecb
   * @param sb
   * @param igb
   * @return
   */
  public int getMaxOrdinalForGroup(EventCRFBean ecb, SectionBean sb, ItemGroupBean igb) {
    
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ecb.getId()));
    variables.put(new Integer(2), new Integer(sb.getId()));
    variables.put(new Integer(3), new Integer(igb.getId()));
    
    ArrayList alist = this.select(digester.getQuery("getMaxOrdinalForGroup"), variables);
    Iterator it = alist.iterator();
    if (it.hasNext()) {
        try {
            HashMap hm = (HashMap) it.next();
            Integer max = (Integer) hm.get("max_ord");
            return max.intValue();
        }
        catch (Exception e) { }
    }
    
    return 0;
  }

}