package com.almahealth.app.security;

import java.util.Collection;
import org.springframework.security.core.userdetails.User;

public class MedicationUser extends User {

    private final Long userId;

    public MedicationUser(String username, String password, Collection authorities, Long userId) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
