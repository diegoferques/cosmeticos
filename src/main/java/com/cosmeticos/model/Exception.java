package com.cosmeticos.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Entity
@Data
public class Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //@Transient
    private String stackTrace;

    private String email;

    private String deviceModel;

    private String osVersion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exception)) return false;
        if (!super.equals(o)) return false;

        Exception exception = (Exception) o;

        return getId() == exception.getId();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (getId() ^ (getId() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Exception{" +
                "id=" + id +
                '}';
    }
}
