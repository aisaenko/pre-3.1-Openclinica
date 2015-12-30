package org.akaza.openclinica.domain;

import org.akaza.openclinica.domain.enumsupport.CodedEnum;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
  * @author pgawade
  * This is an enum to represent the entity types present in database table entity_type
  * Presently this does not read/write from the database table directly. There is a separate liquibase script to 
  * create the table and insert static data into it. The problem I thought behind preserving this enum class using
  * Hibernate or some thing else is that to implement the comparisons in the java code like as example to
  * decide if it is a user generated entity or not, there needs to be a separate set of constants defined in the
  * java code which needs to be any way changed when adding a new entity type in future. So I found this approach 
  * as better where the enum is not directly connected to the database. With other enums used in OpenClinica like
  * one for Status values, they are not connected to database directly either. So any change in future can be\
  * applied to all these enums at the same time.
 **/
public enum EntityType implements CodedEnum {

	INVALID(0, "invalid"), USER_CREATED(1, "User-created"), SYSTEM_CREATED_FOR_CODING(2, "System-generated");

    private int code;
    private String description;

    EntityType() {
    }

    EntityType(int code) {
        this(code, null);
    }

    EntityType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();
        return resterm.getString(getDescription());
    }

    public static EntityType getByName(String name) {
        return EntityType.valueOf(EntityType.class, name);
    }

    public static EntityType getByCode(Integer code) {
        HashMap<Integer, EntityType> enumObjects = new HashMap<Integer, EntityType>();
        for (EntityType theEnum : EntityType.values()) {
            enumObjects.put(theEnum.getCode(), theEnum);
        }
        return enumObjects.get(Integer.valueOf(code));
    }

    /**
     * A wrapper for name() method to be used in JSPs
     */
    public String getName() {
        return this.name();
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
