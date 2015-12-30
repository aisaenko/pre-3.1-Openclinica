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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.*;
import org.akaza.openclinica.dao.core.*;

/**
 * @author thickerson
 *
 * 
 */
public class ItemGroupDAO extends AuditableEntityDAO {
	public ItemGroupDAO(DataSource ds) {
		super(ds);
	}
	
	protected void setDigesterName() {
		
	}
	
	public void setTypesExpected() {
		
	}

	public EntityBean update(EntityBean eb) {
		return eb;
	}
	
	public EntityBean create(EntityBean eb) {
		return eb;
	}
	
	public Object getEntityFromHashMap(HashMap hm) {
		ItemGroupBean eb = new ItemGroupBean();
		
		return eb;
	}
	
	public Collection findAll() {
		ArrayList al = new ArrayList();
		
		return al;
	}
	
	public Collection findAll(String strOrderByColumn,
			boolean blnAscendingSort,
			String strSearchPhrase) {
		ArrayList al = new ArrayList();
		
		return al;
	}
	
	public EntityBean findByPK(int ID) {
		ItemGroupBean eb = new ItemGroupBean();
		
		return eb;
	}
	
	public Collection findAllByPermission(Object objCurrentUser,
			int intActionType, 
			String strOrderByColumn,
			boolean blnAscendingSort,
			String strSearchPhrase) {
		ArrayList al = new ArrayList();
		
		return al;
	}
	
	public Collection findAllByPermission(Object objCurrentUser,
			int intActionType) {
		ArrayList al = new ArrayList();
		
		return al;
	}
}
