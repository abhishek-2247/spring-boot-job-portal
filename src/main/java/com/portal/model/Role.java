package com.portal.model;

/**
 * Role enum — determines which dashboard and permissions a user gets.
 * Maps to Spring Security authorities: ROLE_SEEKER, ROLE_EMPLOYER, ROLE_ADMIN
 */
public enum Role {
    SEEKER,
    EMPLOYER,
    ADMIN
}
