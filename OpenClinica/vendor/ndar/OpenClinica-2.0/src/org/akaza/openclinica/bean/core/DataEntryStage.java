/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ssachs
 */
public class DataEntryStage extends Term {
	public static final DataEntryStage
		INVALID = new DataEntryStage(0, "invalid");
	public static final DataEntryStage
		UNCOMPLETED = new DataEntryStage(1, "not started","not started");
    public static final DataEntryStage
		INITIAL_DATA_ENTRY = new DataEntryStage(2, "initial data entry","data being entered");
    public static final DataEntryStage
		INITIAL_DATA_ENTRY_COMPLETE = new DataEntryStage(3, "initial data entry complete","initial data entry completed");
    public static final DataEntryStage
		DOUBLE_DATA_ENTRY = new DataEntryStage(4, "double data entry","being validated");
    public static final DataEntryStage
		DOUBLE_DATA_ENTRY_COMPLETE = new DataEntryStage(5, "data entry complete","validation completed");
    public static final DataEntryStage
		ADMINISTRATIVE_EDITING = new DataEntryStage(6, "administrative editing","completed");
    public static final DataEntryStage
		LOCKED = new DataEntryStage(6, "locked","locked");
    
	
	private static final DataEntryStage[] members = {
			UNCOMPLETED
			, INITIAL_DATA_ENTRY
			, INITIAL_DATA_ENTRY_COMPLETE
			, DOUBLE_DATA_ENTRY
			, DOUBLE_DATA_ENTRY_COMPLETE
			, ADMINISTRATIVE_EDITING
			, LOCKED
		};
	
	public static final List list = Arrays.asList(members);

	private List privileges;
	
	private DataEntryStage(int id, String name) {
		super(id, name);
	}
	
	private DataEntryStage(int id, String name, String description) {
		super(id, name, description);
	}
	
	private DataEntryStage() {
	}
	
	public static boolean contains(int id) {
		return Term.contains(id, list);
	}
	
	public static DataEntryStage get(int id) {
		return (DataEntryStage) Term.get(id, list);
	}	
}
