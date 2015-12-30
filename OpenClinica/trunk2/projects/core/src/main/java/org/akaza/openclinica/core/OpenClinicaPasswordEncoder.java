package org.akaza.openclinica.core;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

public class OpenClinicaPasswordEncoder implements PasswordEncoder {

    private PasswordEncoder passwordEncoder;
    private PasswordEncoder legacySHAEncoder;
    private PasswordEncoder legacyMD5Encoder;

    public OpenClinicaPasswordEncoder() {
    }

    public String encodePassword(String rawPass, Object salt) throws DataAccessException {
        return passwordEncoder.encodePassword(rawPass, salt);
    }

    public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
        return (passwordEncoder.isPasswordValid(encPass, rawPass, salt) ||
                legacySHAEncoder.isPasswordValid(encPass, rawPass, null) ||
                legacyMD5Encoder.isPasswordValid(encPass, rawPass, null));
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordEncoder getLegacySHAEncoder() {
        return legacySHAEncoder;
    }

    public void setLegacySHAEncoder(PasswordEncoder legacySHAEncoder) {
        this.legacySHAEncoder = legacySHAEncoder;
    }

    public PasswordEncoder getLegacyMD5Encoder() {
        return legacyMD5Encoder;
    }

    public void setLegacyMD5Encoder(PasswordEncoder legacyMD5Encoder) {
        this.legacyMD5Encoder = legacyMD5Encoder;
    }

}
