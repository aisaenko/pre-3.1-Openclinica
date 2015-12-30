package org.akaza.openclinica.dao.submit;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.exception.OpenClinicaException;

public class PublisherDAO extends AuditableEntityDAO {

	public PublisherDAO(DataSource ds){
		super(ds);
	}
	
	public PublisherDAO(DataSource ds, DAODigester digester) {
	    super(ds);
	    this.digester = digester;
	  }
	@Override
	public void setTypesExpected() {
		// TODO Auto-generated method stub
		
		//"PUBLISHER_ID" NUMBER(10,0) NOT NULL ENABLE, 
		//"NAME" VARCHAR2(100 BYTE) NOT NULL ENABLE, 
		//"ABBREVIATION" VARCHAR2(30 BYTE), 
		//"DESCRIPTION" VARCHAR2(255 BYTE), 
		this.unsetTypeExpected();
	    this.setTypeExpected(1, TypeNames.INT);
	    this.setTypeExpected(2, TypeNames.STRING);
	    this.setTypeExpected(3, TypeNames.STRING);
	    this.setTypeExpected(4, TypeNames.STRING);
	}

	@Override
	protected void setDigesterName() {
		// TODO Auto-generated method stub
		digesterName = SQLFactory.getInstance().DAO_PUBLISHER;
	}

	public EntityBean create(EntityBean eb) throws OpenClinicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection findAll(String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase)
			throws OpenClinicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection findAll() throws OpenClinicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection findAllByPermission(Object objCurrentUser,
			int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase)
			throws OpenClinicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection findAllByPermission(Object objCurrentUser,
			int intActionType) throws OpenClinicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityBean findByPK(int id) throws OpenClinicaException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getEntityFromHashMap(HashMap hm) {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityBean update(EntityBean eb) throws OpenClinicaException {
		// TODO Auto-generated method stub
		return null;
	}
	public int getId(String name) {
	    int answer = -1;

	    this.unsetTypeExpected();
	    this.setTypeExpected(1, TypeNames.INT);

	    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
	    variables.put(new Integer(1), name);

	    String sql = digester.getQuery("getPublisherIDByName");
	    ArrayList rows = select(sql, variables);

	    if (rows.size() > 0) {
	      HashMap h = (HashMap) rows.get(0);
	      answer = ((Integer) h.get("publisher_id")).intValue();
	    }

	    return answer;
	  }
	
	public void createPublisher(String Publisher_Name, String Publisher_Abbreviation, String Publisher_Description) {
		
		HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
		HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();
		nullVars.put(new Integer(1), new Integer(Types.INTEGER));
		variables.put(new Integer(1), null);
		variables.put(new Integer(2), Publisher_Name);
		variables.put(new Integer(3), Publisher_Abbreviation);
		variables.put(new Integer(4), Publisher_Description);
    
		this.execute(digester.getQuery("create"), variables, nullVars);
    return;
  }
	
}
