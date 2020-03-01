package com.guardedbox.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.RegistrationEntity;

/**
 * Repository: Registration.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface RegistrationEntitiesRepository
        extends JpaRepository<RegistrationEntity, UUID>,
        JpaSpecificationExecutor<RegistrationEntity> {

    /**
     * @param token RegistrationEntity.token.
     * @return Boolean indicating if a RegistrationEntity corresponding to the introduced token exists.
     */
    boolean existsByToken(
            String token);

    /**
     * @param token RegistrationEntity.token.
     * @return The RegistrationEntity corresponding to the introduced token.
     */
    RegistrationEntity findByToken(
            String token);

    /**
     * @param email RegistrationEntity.email.
     * @return The RegistrationEntity corresponding to the introduced email.
     */
    RegistrationEntity findByEmail(
            String email);

}
