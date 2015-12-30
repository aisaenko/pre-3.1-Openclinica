package org.akaza.openclinica.domain.role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.util.Set;

import org.akaza.openclinica.domain.AbstractMutableDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
@Entity
@Table(name = "permission")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "permission_id_seq") })
public class Permissions extends AbstractMutableDomainObject {

 
    public String getAccess_url() {
        return access_url;
    }   
    public void setAccess_url(String access_url) {
        this.access_url = access_url;
    }
   
    
    private String access_url;
  
    private Set<Role> role;
    
    @ManyToMany(targetEntity=org.akaza.openclinica.domain.role.Role.class) 
    @JoinTable(name="role_permission", joinColumns= {  @JoinColumn(name="permission_id") },inverseJoinColumns = { @JoinColumn(name = "role_id")})
    public Set<Role> getRole() {
        return role;
    }
    public void setRole(Set<Role> role) {
        this.role = role;
    }
    
  
    private String access_parameters;

    public String getAccess_parameters() {
        return access_parameters;
    }
    public void setAccess_parameters(String access_parameters) {
        this.access_parameters = access_parameters;
    }
  
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
   @Column()
    public Boolean getIsAdmin() {
        return isAdmin;
    }


    private Boolean isAdmin=false;
    
}
