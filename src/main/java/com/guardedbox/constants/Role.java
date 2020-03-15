package com.guardedbox.constants;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Roles Enum.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RequiredArgsConstructor
@Getter
public enum Role {

    /** Account. */
    ACCOUNT(new SimpleGrantedAuthority("ROLE_ACCOUNT"));

    /** Authority. */
    private final GrantedAuthority authority;

}
