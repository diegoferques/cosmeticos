/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author magarrett.dias
 */
@AllArgsConstructor
@Builder
@Data
@Entity
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    @NotEmpty(message="Role name cannot be empty")
    private String name;

    // TODO: role pertence a User, logo, o mappedBy deve ficar aqui e nao em User.
    @JoinTable(name = "UserRoles", joinColumns = {
        @JoinColumn(name = "idRole", referencedColumnName = "idRole")}, inverseJoinColumns = {
        @JoinColumn(name = "idLogin", referencedColumnName = "idLogin")})
    @ManyToMany
    private Collection<User> userCollection;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRole != null ? idRole.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Role)) {
            return false;
        }
        Role other = (Role) object;
        if ((this.idRole == null && other.idRole != null) || (this.idRole != null && !this.idRole.equals(other.idRole))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Role[ idRole=" + idRole + " ]";
    }

    public enum Names {
        ROLE_USER
    }
}
