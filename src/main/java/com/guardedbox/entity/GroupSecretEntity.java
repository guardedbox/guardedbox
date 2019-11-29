package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_NAME_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Entity: GroupSecret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "group_secret")
public class GroupSecretEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3662560674824178640L;

    /** Group ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_secret_id")
    @Positive
    private Long groupSecret;

    /** Group. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    @NotNull
    @Valid
    private GroupEntity group;

    /** Name. */
    @Column(name = "name")
    @NotBlank
    @Size(max = SECRET_NAME_MAX_LENGTH)
    private String name;

    /** Value. */
    @Column(name = "value")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

    /**
     * @return The groupSecret.
     */
    public Long getGroupSecret() {
        return groupSecret;
    }

    /**
     * @param groupSecret The groupSecret to set.
     */
    public void setGroupSecret(
            Long groupSecret) {
        this.groupSecret = groupSecret;
    }

    /**
     * @return The group.
     */
    public GroupEntity getGroup() {
        return group;
    }

    /**
     * @param group The group to set.
     */
    public void setGroup(
            GroupEntity group) {
        this.group = group;
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
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(
            String value) {
        this.value = value;
    }

}
