/**
 * 
 */
package org.akaza.openclinica.bean.core;

/**
 * @author pgawade
 *
 */
public class EntityType extends Term {
	
	protected String typeName;
	protected String subTypeName;
	
	public static final EntityType USER_CREATED = new EntityType(1, "USER_CREATED", "");
	public static final EntityType SYSTEM_CREATED_FOR_CODING = new EntityType(2, "SYSTEM_GENERATED", "MEDICAL_CODING");
	
	
	private EntityType(int id, String typeName, String subTypeName) {
	    this.id = id;
	    this.typeName = typeName;
	    this.subTypeName = subTypeName;
	}
	
	private EntityType() {
	}	

}