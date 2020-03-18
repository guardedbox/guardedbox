package com.guardedbox.entity.projection;

import java.util.UUID;

/**
 * Projection of Entity: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountBaseProjection {

    UUID getAccountId();

    String getEmail();

}
