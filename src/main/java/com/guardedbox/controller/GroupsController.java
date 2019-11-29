package com.guardedbox.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.CreateGroupDto;
import com.guardedbox.dto.GroupDto;
import com.guardedbox.service.SessionAccountService;
import com.guardedbox.service.transactional.GroupsService;

/**
 * Controller: Groups.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/groups")
@Validated
public class GroupsController {

    /** GroupsService. */
    private final GroupsService groupsService;

    /** SessionAccountService. */
    private final SessionAccountService sessionAccount;

    /**
     * Constructor with Attributes.
     *
     * @param groupsService GroupsService.
     * @param sessionAccount SessionAccountService.
     */
    public GroupsController(
            @Autowired GroupsService groupsService,
            @Autowired SessionAccountService sessionAccount) {
        this.groupsService = groupsService;
        this.sessionAccount = sessionAccount;
    }

    /**
     * @return All the groups belonging to the current session account.
     */
    @GetMapping("/owned")
    public List<GroupDto> getOwnedGroups() {

        return groupsService.getGroupsByOwnerAccountId(sessionAccount.getAccountId());

    }

    /**
     * @return All the groups in which the current session account is participant.
     */
    @GetMapping("/invited")
    public List<GroupDto> getInvitedGroups() {

        return null; // TODO

    }

    /**
     * Creates Group, belonging to the current session account.
     *
     * @param createGroupDto Object with the necessary data to create a Group.
     * @return Object with the stored group data.
     */
    @PostMapping()
    public GroupDto createGroup(
            @RequestBody(required = true) @Valid CreateGroupDto createGroupDto) {

        return groupsService.createGroup(sessionAccount.getAccountId(), createGroupDto);

    }

}
