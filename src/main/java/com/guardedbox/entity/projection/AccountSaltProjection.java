package com.guardedbox.entity.projection;

/**
 * Projection of Entity: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountSaltProjection
        extends AccountBaseProjection {

    String getSalt();

}
