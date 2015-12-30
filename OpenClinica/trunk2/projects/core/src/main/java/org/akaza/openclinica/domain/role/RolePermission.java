package org.akaza.openclinica.domain.role;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.util.Set;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
/**
 * 
 * @author jnyayapathi
 * Mapping table role_permission Domain Object, acts as interface between role and permissions and has many-many relationship with both.  
 */
@Entity
@Table(name = "role_permission")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "role_permission_id_seq") })
@Inheritance(strategy = InheritanceType.JOINED)
public class RolePermission extends AbstractMutableDomainObject {



@JoinColumn(name="ROLE_ID")
private Set<Role> roles;

@JoinColumn(name="PERMISSION_ID")
private Set<Permissions> permissions;



private Integer permission_id;

public Integer getPermission_id() {
    return permission_id;
}
public void setPermission_id(Integer permission_id) {
    this.permission_id = permission_id;
}


private Integer role_id;

public Integer getRole_id() {
    return role_id;
}
public void setRole_id(Integer role_id) {
    this.role_id = role_id;
}


}
