package com.guardedbox.dto;

import java.io.Serializable;

/**
 * DTO: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class GroupDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3630417901231355279L;

    /** Group ID. */
    private Long groupId;

    /** Owner Account. */
    private AccountWithEncryptionPublicKeyDto ownerAccount;

    /** Name. */
    private String name;

    /** Encrypted Group Key. */
    private String encryptedGroupKey;

    /**
     * @return The groupId.
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId The groupId to set.
     */
    public void setGroupId(
            Long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return The ownerAccount.
     */
    public AccountWithEncryptionPublicKeyDto getOwnerAccount() {
        return ownerAccount;
    }

    /**
     * @param ownerAccount The ownerAccount to set.
     */
    public void setOwnerAccount(
            AccountWithEncryptionPublicKeyDto ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(
            String name) {
        this.name = name;
    }

    /**
     * @return The encryptedGroupKey.
     */
    public String getEncryptedGroupKey() {
        return encryptedGroupKey;
    }

    /**
     * @param encryptedGroupKey The encryptedGroupKey to set.
     */
    public void setEncryptedGroupKey(
            String encryptedGroupKey) {
        this.encryptedGroupKey = encryptedGroupKey;
    }

}
