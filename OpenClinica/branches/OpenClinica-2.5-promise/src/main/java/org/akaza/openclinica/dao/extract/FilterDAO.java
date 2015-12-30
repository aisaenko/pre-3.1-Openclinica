/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.extract;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.bean.extract.FilterObjectBean;
import org.akaza.openclinica.bean.extract.PermissionsMatrixBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

/**
 * The data access object for filters.
 *
 * @author thickerson
 *
 */
public class FilterDAO extends AuditableEntityDAO {
    private DAODigester digester;

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_FILTER;
    }

    protected void setQueryNames() {
        getCurrentPKName = "getCurrentPK";
        getNextPKName = "getNextPK";
        // TODO figure out the error with current primary keys?
    }

    public FilterDAO(DataSource ds) {
        super(ds);
        digester = SQLFactory.getInstance().getDigester(digesterName);
        this.setQueryNames();
    }

    /**
     * creator object to be used during testing, tbh
     *
     * @param ds
     * @param digester
     */
    public FilterDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
    }
    
    // new methods to extract the permissions matrix here, tbh 08/2009
    public void setPermissionTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// def id
        this.setTypeExpected(2, TypeNames.INT);// id
        this.setTypeExpected(3, TypeNames.INT);// id
        this.setTypeExpected(4, TypeNames.INT);// id
        this.setTypeExpected(5, TypeNames.INT);// permit
        this.setTypeExpected(6, TypeNames.INT);// status id
        this.setTypeExpected(7, TypeNames.DATE);// created
        this.setTypeExpected(8, TypeNames.DATE);// updated
        this.setTypeExpected(9, TypeNames.INT);// owner id
        this.setTypeExpected(10, TypeNames.INT);// update id
    }
    
    /*
     * insert into permissions_filter (study_event_definition_id, 
		crf_id, item_id, role_id, permit, owner_id, date_created) values (?,?,?,?,?,?, now())
     */
    public void createPermissions(PermissionsMatrixBean permissions) {
    	HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    	HashMap<String, Integer> permissionMap = permissions.getPermissions();
    	Iterator it = permissionMap.keySet().iterator();
    	while (it.hasNext()) {
    		String key = (String)it.next();
    		String[] keys = key.split("_");
    		// should be in the order by sed, crf, item and role
    		Integer sedId = new Integer(keys[0]);
    		Integer crfId = new Integer(keys[1]);
    		Integer itemId = new Integer(keys[2]);
    		Integer roleId = new Integer(keys[3]);
    		Integer permit = permissions.getPermissions().get(key);
    		variables.put(new Integer(1), sedId);
    		variables.put(new Integer(2), crfId);
    		variables.put(new Integer(3), itemId);
    		variables.put(new Integer(4), roleId);
    		variables.put(new Integer(5), permit);
    		variables.put(new Integer(6), new Integer(permissions.getOwnerId()));
    		this.execute(digester.getQuery("createPermissions"), variables);
    	}
    	
    }
    
    public void createPermission(int sedId, int crfId, int itemId, int roleId, int permit, int ownerId) {
    	HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    	variables.put(new Integer(1), new Integer(sedId));
		variables.put(new Integer(2), new Integer(crfId));
		variables.put(new Integer(3), new Integer(itemId));
		variables.put(new Integer(4), new Integer(roleId));
		variables.put(new Integer(5), new Integer(permit));
		variables.put(new Integer(6), new Integer(ownerId));
		this.execute(digester.getQuery("createPermissions"), variables);
    }
    
    public PermissionsMatrixBean findPermissionsByStudy(StudyBean study) {
        this.setPermissionTypesExpected();
        HashMap variables = new HashMap();
        if (study.getParentStudyId() > 0) {
        	variables.put(new Integer(1), new Integer(study.getParentStudyId()));
        } else {
        	variables.put(new Integer(1), new Integer(study.getId()));
        }
        ArrayList alist = this.select(digester.getQuery("findPermissionsByStudy"), variables);

        Iterator it = alist.iterator();
        PermissionsMatrixBean pmb = new PermissionsMatrixBean();
        while (it.hasNext()) {
        	HashMap hm = (HashMap) it.next();
        	//aeb.setStatus(Status.get(statusId.intValue()));
        	hm.put("status_id", new Integer(Status.AVAILABLE.getId()));
        	this.setEntityAuditInformation(pmb, hm);
            // repeats a lot, last row will be the one who is set
            int crfId = ((Integer) hm.get("crf_id")).intValue();
            int roleId = ((Integer) hm.get("role_id")).intValue();
            int itemId = ((Integer) hm.get("item_id")).intValue();
            int sedId = ((Integer) hm.get("study_event_definition_id")).intValue();
            int permit = ((Integer) hm.get("permit")).intValue();
            pmb.setPermission(sedId, crfId, itemId, roleId, permit);
        }
        return pmb;
    }
    
    public boolean isPermissionInDB(int sedId, int crfId, int itemId, int roleId) {
    	this.setPermissionTypesExpected();
    	HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    	variables.put(new Integer(1), new Integer(sedId));
    	variables.put(new Integer(2), new Integer(crfId));
    	variables.put(new Integer(3), new Integer(itemId));
    	variables.put(new Integer(4), new Integer(roleId));
    	ArrayList alist = this.select(digester.getQuery("isPermissionInDB"), variables);
    	if (alist.isEmpty()) {
    		return false;
    	} else {
    		return true;
    	}
    	
    }
    /*
     * <name>updatePermissions</name>
		<sql>update permissions_filter set permit = ?, date_updated = now(), update_id = ? 
		where study_event_definition_id = ? and
		crf_id = ? and item_id = ? and role_id = ?</sql>
     */
    public void updatePermissions(PermissionsMatrixBean pmb) {
    	
    	HashMap<String, Integer> permissionMap = pmb.getPermissions();
    	Iterator it = permissionMap.keySet().iterator();
    	while (it.hasNext()) {
    		String key = (String)it.next();
    		String[] keys = key.split("_");
    		// should be in the order by sed, crf, item and role
    		Integer sedId = new Integer(keys[0]);
    		Integer crfId = new Integer(keys[1]);
    		Integer itemId = new Integer(keys[2]);
    		Integer roleId = new Integer(keys[3]);
    		Integer permit = pmb.getPermissions().get(key);
    		if (isPermissionInDB(sedId.intValue(), crfId.intValue(), itemId.intValue(), roleId.intValue())) {
    			HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        		variables.put(new Integer(1), permit);
        		variables.put(new Integer(2), new Integer(pmb.getUpdaterId()));
        		variables.put(new Integer(3), sedId);
        		variables.put(new Integer(4), crfId);
        		variables.put(new Integer(5), itemId);
        		variables.put(new Integer(6), roleId);
        		this.execute(digester.getQuery("updatePermissions"), variables);
    		}
    		
    	}
    }
    
    /*
     * <name>updatePermissions</name>
		<sql>update permissions_filter set permit = ?, date_updated = now(), update_id = ? 
		where study_event_definition_id = ? and
		crf_id = ? and item_id = ? and role_id = ?</sql>
     */
    public void updatePermission(int sedId, int crfId, int itemId, int roleId, int permit, int updaterId) {
    	HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		variables.put(new Integer(1), new Integer(permit));
		variables.put(new Integer(2), new Integer(updaterId));
		variables.put(new Integer(3), new Integer(sedId));
		variables.put(new Integer(4), new Integer(crfId));
		variables.put(new Integer(5), new Integer(itemId));
		variables.put(new Integer(6), new Integer(roleId));
		this.execute(digester.getQuery("updatePermissions"), variables);
    }
   
    // << tbh 08/2009

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);// filter id
        this.setTypeExpected(2, TypeNames.STRING);// name
        this.setTypeExpected(3, TypeNames.STRING);// description
        this.setTypeExpected(4, TypeNames.STRING);// sql statement?
        this.setTypeExpected(5, TypeNames.INT);// status id
        this.setTypeExpected(6, TypeNames.DATE);// created
        this.setTypeExpected(7, TypeNames.DATE);// updated
        this.setTypeExpected(8, TypeNames.INT);// owner id
        this.setTypeExpected(9, TypeNames.INT);// update id
    }

    public EntityBean update(EntityBean eb) {
        FilterBean fb = (FilterBean) eb;
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();
        variables.put(new Integer(1), fb.getName());
        variables.put(new Integer(2), fb.getDescription());
        variables.put(new Integer(3), new Integer(fb.getStatus().getId()));
        variables.put(new Integer(4), fb.getSQLStatement());// string, updateid,
        // filterid
        variables.put(new Integer(5), new Integer(fb.getUpdaterId()));
        variables.put(new Integer(6), new Integer(fb.getId()));
        this.execute(digester.getQuery("update"), variables, nullVars);
        return fb;
    }

    public EntityBean create(EntityBean eb) {
        FilterBean fb = (FilterBean) eb;
        logger.info("logged following owner id: " + fb.getOwnerId() + " vs. " + fb.getOwner().getId());
        HashMap variables = new HashMap();
        int id = getNextPK();
        // HashMap nullVars = new HashMap();
        variables.put(new Integer(1), fb.getId());
        variables.put(new Integer(2), fb.getName());
        // name desc sql, status id owner id
        variables.put(new Integer(3), fb.getDescription());
        variables.put(new Integer(4), fb.getSQLStatement());
        variables.put(new Integer(5), new Integer(fb.getStatus().getId()));
        variables.put(new Integer(6), new Integer(fb.getOwner().getId()));
        // changed from get owner id, tbh

        this.execute(digester.getQuery("create"), variables);

        fb.setId(id);
        return fb;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        FilterBean fb = new FilterBean();
        this.setEntityAuditInformation(fb, hm);
        fb.setDescription((String) hm.get("description"));
        fb.setName((String) hm.get("name"));
        fb.setId(((Integer) hm.get("filter_id")).intValue());
        fb.setSQLStatement((String) hm.get("sql_statement"));
        return fb;
    }

    public Collection findAll() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAll"));
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            FilterBean fb = (FilterBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(fb);
        }
        return al;
    }

    public Collection findAllAdmin() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAllAdmin"));
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            FilterBean fb = (FilterBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(fb);
        }
        return al;
    }

    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public EntityBean findByPK(int ID) {
        FilterBean fb = new FilterBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(ID));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            fb = (FilterBean) this.getEntityFromHashMap((HashMap) it.next());
        }

        return fb;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

    /**
     * To be created with the header string and an array list of
     * FilterObjectBeans, which contain the number, type and value of criteria
     * to apply to the existing filter.
     *
     * fundamental change 06-03: adding the new query formation will change the
     * SQL so that
     *
     * @param oldSQLStatement
     * @param connector
     * @param filterObjs
     */
    public String genSQLStatement(String oldSQLStatement, String connector, ArrayList filterObjs) {
        StringBuffer sb = new StringBuffer();
        // sb.append(" and subject_id in "+
        // "(select subject_id from extract_data_table where ");
        if (oldSQLStatement != null) {
            sb.append(oldSQLStatement);
        } else {
            sb.append(" and subject_id in " + "(select subject_id from extract_data_table where ");
        }
        String tailEnd = "";
        Iterator it = filterObjs.iterator();
        int count = 0;
        while (it.hasNext()) {
            FilterObjectBean fob = (FilterObjectBean) it.next();
            tailEnd = "(" + tailEnd;
            if (count != 0) {
                tailEnd = tailEnd + " " + connector + " ";// fob.getOperand();
            }
            count++;
            // TODO add this to create like operators, maybe move this to else
            // where?
            if (fob.getOperand().equals(" like ") || fob.getOperand().equals(" not like ")) {
                fob.setValue("%" + fob.getValue() + "%");
            }
            tailEnd = tailEnd + "(item_id = " + fob.getItemId() + " and value " + fob.getOperand() + " '" + fob.getValue() + "'))";
        }
        if (oldSQLStatement != null) {
            sb.append(" and ");
            // rearrange sql here, and above, so that
            // filter can be changed
        }
        sb.append(tailEnd);
        // sb.append(")");
        // and a parens at the very end!
        return sb.toString();
    }

    /**
     * Will generate an explanation stating that this filter will look for value
     * x at question y AND value like z at question a OR value not like a at
     * question c...
     *
     * @param oldExplanation
     * @param connector
     * @param filterObjs
     */
    public ArrayList genExplanation(ArrayList oldExplanation, String connector, ArrayList filterObjs) {
        ArrayList sb = new ArrayList();
        if (oldExplanation != null) {
            sb.addAll(oldExplanation);
        } else {
            sb.add("This Filter will look for:");
        }
        Iterator it = filterObjs.iterator();
        int count = 0;
        while (it.hasNext()) {
            FilterObjectBean fob = (FilterObjectBean) it.next();
            String answerLine = "A value " + fob.getOperand() + " " + fob.getValue() + " " + "for question " + fob.getItemName();

            sb.add(answerLine);
            count++;
            if (count < filterObjs.size()) {
                sb.add(connector + " ");
            }
        }
        return sb;
    }
}
