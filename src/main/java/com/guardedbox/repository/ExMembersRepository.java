package com.guardedbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.ExMemberEntity;

/**
 * Repository: ExMember.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface ExMembersRepository
        extends JpaRepository<ExMemberEntity, UUID>,
        JpaSpecificationExecutor<ExMemberEntity> {

    /**
     * @param secretId ExMemberEntity.secretId.
     * @param email ExMemberEntity.email.
     * @return Boolean indicating if an ExMemberEntity corresponding to the introduced secretId and email exists.
     */
    boolean existsBySecretSecretIdAndEmail(
            UUID secretId,
            String email);

    /**
     * @param groupId ExMemberEntity.groupId.
     * @param email ExMemberEntity.email.
     * @return Boolean indicating if an ExMemberEntity corresponding to the introduced groupId and email exists.
     */
    boolean existsByGroupGroupIdAndEmail(
            UUID groupId,
            String email);

    /**
     * @param email ExMemberEntity.email.
     * @return The List of ExMemberEntities corresponding to the introduced email.
     */
    List<ExMemberEntity> findByEmail(
            String email);

    /**
     * Deletes an ExMemberEntity by secretId and email.
     *
     * @param secretId ExMemberEntity.secretId.
     * @param email ExMemberEntity.email.
     */
    void deleteBySecretSecretIdAndEmail(
            UUID secretId,
            String email);

    /**
     * Deletes an ExMemberEntity by groupId and email.
     *
     * @param groupId ExMemberEntity.groupId.
     * @param email ExMemberEntity.email.
     */
    void deleteByGroupGroupIdAndEmail(
            UUID groupId,
            String email);

}
