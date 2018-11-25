package com.guardedbox.constants;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Roles Enumeration.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public final class Roles {

    /** Role: Account. */
    public static final GrantedAuthority ROLE_ACCOUNT = new SimpleGrantedAuthority("ROLE_ACCOUNT");

}
