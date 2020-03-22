package com.guardedbox.entity.projection;

/**
 * Projection of Entity: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountPublicKeysSaltsProjection
        extends AccountBaseProjection {

    String getEncryptionSalt();

    String getSigningSalt();

}
