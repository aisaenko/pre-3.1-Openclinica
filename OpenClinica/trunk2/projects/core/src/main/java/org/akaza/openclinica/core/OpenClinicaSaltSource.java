package org.akaza.openclinica.core;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.dao.SystemWideSaltSource;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * {@link SaltSource} implementation for OpenClinica. Similar to {@link SystemWideSaltSource} but allows an empty
 * salt to be used.
 *
 * @author Doug Rodrigues (drodrigues@openclinica.com)
 * @see SystemWideSaltSource
 *
 */
public class OpenClinicaSaltSource implements SaltSource, Serializable {

    private static final long serialVersionUID = 6207200788478809178L;

    private String salt;

    public Object getSalt(UserDetails user) {
        if (StringUtils.isEmpty(salt)) {
            return StringUtils.EMPTY;
        }
        return salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
