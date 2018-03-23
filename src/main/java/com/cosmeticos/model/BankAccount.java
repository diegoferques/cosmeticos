package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Vinicius on 30/06/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
public class BankAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Type {
		CONTA_CORRENTE, CONTA_POUPANCA
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Professional professional;

    /**
     * Caso o profissional esteja usando dados bancarios de um pai, mae, esposa, etc..
     */
    @NotEmpty(message = "Nome do titular da conta deve ser preenchido.")
    private String ownerName;

    /**
     * Caso o profissional esteja usando dados bancarios de um pai, mae, esposa, etc..
     */
    @NotEmpty(message = "CPF do titular da conta deve ser preenchido.")
    private String ownerCPF;

    @NotEmpty(message = "Numero da agencia deve ser preenchido.")
    private String agency;

    @NotEmpty(message = "Numero da conta deve ser preenchido.")
    private String accountNumber;

    @NotEmpty(message = "Nome do banco (instuição bancária) deve ser preenchido.")
    private String financialInstitute;

    @NotNull(message = "Tipo de conta (corrente/poupança) deve ser preenchido.")
    private Type type;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BankAccount)) {
            return false;
        }
        BankAccount other = (BankAccount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BankAccount[ id=" + id + " ]";
    }

}
