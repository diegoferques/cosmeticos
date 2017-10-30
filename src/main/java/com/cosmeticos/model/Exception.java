package com.cosmeticos.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Entity
@Data
public class Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Status{
        RESOLVED, UNRESOLVED, FAILED_SENDING_MAIL, UNRESOLVED_BUT_NOTIFIED
    }

    //@Transient
    private String stackTrace;

    private String deviceModel;

    private String osVersion;

    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

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
