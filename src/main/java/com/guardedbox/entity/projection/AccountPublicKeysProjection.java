package com.guardedbox.entity.projection;

/**
 * Projection of Entity: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountPublicKeysProjection
        extends AccountBaseProjection {

    String getEncryptionPublicKey();

    String getSigningPublicKey();

}
