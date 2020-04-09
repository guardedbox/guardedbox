package com.guardedbox.entity.projection;

/**
 * Projection of Entity: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface SecretMustRotateKeyProjection
        extends SecretBaseProjection {

    Boolean getMustRotateKey();

}
