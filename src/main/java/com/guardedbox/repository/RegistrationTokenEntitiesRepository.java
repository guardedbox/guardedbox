package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.RegistrationTokenEntity;

/**
 * Repository: RegistrationToken.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public interface RegistrationTokenEntitiesRepository
        extends JpaRepository<RegistrationTokenEntity, Long>,
        JpaSpecificationExecutor<RegistrationTokenEntity> {

    /**
     * @param token RegistrationTokenEntity.token.
     * @return Boolean indicating if a RegistrationTokenEntity corresponding to the introduced token exists.
     */
    boolean existsByToken(
            String token);

    /**
     * @param email RegistrationTokenEntity.email.
     * @return The RegistrationTokenEntity corresponding to the introduced email.
     */
    RegistrationTokenEntity findByEmail(
            String email);

    /**
     * @param token RegistrationTokenEntity.token.
     * @return The RegistrationTokenEntity corresponding to the introduced token.
     */
    RegistrationTokenEntity findByToken(
            String token);

}
