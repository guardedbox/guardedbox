import React, { Component } from 'react';
import { Container, Button, Modal, ModalHeader, ModalBody, ModalFooter, Alert, Form, FormGroup, Input, InputGroup, Table, UncontrolledTooltip } from 'reactstrap';
import Octicon, { DiffAdded, Sync, Organization, Lock, Pencil, Trashcan, File, History, ShieldLock, Shield, Key, X } from '@primer/octicons-react'
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { encrypt, decrypt, encryptSymmetric, decryptSymmetric } from 'services/crypto/crypto.jsx';
import { randomBytes } from 'services/crypto/random.jsx';
import { addElementToStateArray, setStateArrayElement, removeStateArrayElement } from 'services/state-utils.jsx';
import { modalConfirmation } from 'services/modal.jsx';
import { openCheckKeysModal } from 'services/check-keys.jsx';
import { copyToClipboard, selectTableBodyCell } from 'services/selector.jsx';
import properties from 'constants/properties.json';

class Groups extends Component {

    state = {
        ownedGroups: null,
        newGroupName: '',
        newGroupModalActive: false,
        ownedGroup: null,
        ownedGroupRowIndex: -1,
        ownedGroupName: '',
        ownedGroupKey: '',
        ownedGroupParticipants: null,
        ownedGroupParticipantsModalActive: false,
        ownedGroupAddParticipantEmail: '',
        ownedGroupSecrets: null,
        ownedGroupSecretsModalActive: false,
        ownedGroupAddSecretName: '',
        ownedGroupAddSecretValue: '',
        participantGroups: null,
        participantGroup: null,
        participantGroupRowIndex: -1,
        participantGroupName: '',
        participantGroupKey: '',
        participantGroupParticipants: null,
        participantGroupParticipantsModalActive: false
    };

    newGroupModalTxtName = React.createRef();
    ownedGroupParticipantsModalForm = React.createRef();
    ownedGroupParticipantsModalTxtEmail = React.createRef();
    ownedGroupSecretsModalTableBody = React.createRef();
    ownedGroupSecretsModalTxtName = React.createRef();
    participantGroupSecretsModalTableBody = React.createRef();

    constructor(props) {

        super(props);
        registerViewComponent('groups', this);

    }

    handleLocationChange = () => {

        if (this.state.ownedGroups == null) {
            this.loadOwnedGroups(() => {

                if (this.state.participantGroups == null) {
                    this.loadParticipantGroups();
                }

            });
        }

    }

    loadOwnedGroups = (callback) => {

        rest({
            method: 'get',
            url: '/api/groups/owned',
            callback: (response) => {

                var ownedGroups = response;

                this.setState({
                    ownedGroups: ownedGroups
                }, () => {
                    if (callback) callback();
                });

            }
        });

    }

    loadParticipantGroups = (callback) => {

        rest({
            method: 'get',
            url: '/api/groups/invited',
            callback: (response) => {

                var participantGroups = response;

                this.setState({
                    participantGroups: participantGroups
                }, () => {
                    if (callback) callback();
                });

            }
        });

    }

    newGroup = () => {

        this.setState({
            newGroupName: '',
            newGroupModalActive: true
        }, () => {
            setTimeout(() => {
                this.newGroupModalTxtName.current.focus();
            }, 25);
        });

    }

    commitNewGroup = () => {

        var name = this.state.newGroupName;

        var groupKey = randomBytes(properties.cryptography.length);
        var encryptedGroupKey = encrypt(groupKey);
        if (encryptedGroupKey == '') return;

        rest({
            method: 'post',
            url: '/api/groups',
            body: {
                name: name,
                encryptedGroupKey: encryptedGroupKey
            },
            callback: (response) => {

                var newGroup = response;

                this.setState({
                    newGroupName: '',
                    newGroupModalActive: false
                }, () => {
                    addElementToStateArray(this, 'ownedGroups', newGroup);
                });

            }
        });

    }

    cancelNewGroup = () => {

        this.setState({
            newGroupName: '',
            newGroupModalActive: false
        });

    }

    showOwnedGroupParticipants = (rowIndex, group) => {

        var groupId = group.groupId;

        var groupKey = decrypt(group.encryptedGroupKey, null, 'base64', 'base64', 'base64');
        if (groupKey == '') return;

        rest({
            method: 'get',
            url: '/api/groups/{group-id}/participants',
            pathVariables: {
                'group-id': groupId
            },
            callback: (response) => {

                var ownedGroupParticipants = response;

                this.setState({
                    ownedGroup: group,
                    ownedGroupRowIndex: rowIndex,
                    ownedGroupName: group.name,
                    ownedGroupKey: groupKey,
                    ownedGroupParticipants: ownedGroupParticipants,
                    ownedGroupParticipantsModalActive: true,
                    ownedGroupAddParticipantEmail: ''
                }, () => {
                    setTimeout(() => {
                        this.ownedGroupParticipantsModalTxtEmail.current.focus();
                    }, 25);
                });

            }
        });

    }

    ownedGroupParticipantCheckKeys = () => {

        if (!this.ownedGroupParticipantsModalForm.current.reportValidity()) return;

        openCheckKeysModal(this.state.ownedGroupAddParticipantEmail);

    }

    addParticipantToGroup = () => {

        var groupId = this.state.ownedGroup.groupId;
        var participantEmail = this.state.ownedGroupAddParticipantEmail;

        rest({
            method: 'get',
            url: '/api/accounts/public-keys',
            params: {
                'email': participantEmail
            },
            loadingChain: true,
            callback: (response) => {

                var participant = response;

                var participantEncryptedGroupKey = encrypt(this.state.ownedGroupKey, participant.encryptionPublicKey, 'base64');
                if (participantEncryptedGroupKey == '') return;

                rest({
                    method: 'post',
                    url: '/api/groups/{group-id}/participants',
                    pathVariables: {
                        'group-id': groupId
                    },
                    body: {
                        email: participantEmail,
                        encryptedGroupKey: participantEncryptedGroupKey
                    },
                    loadingChained: true,
                    callback: (response) => {

                        addElementToStateArray(this, 'ownedGroupParticipants', participant, () => {
                            this.ownedGroupParticipantsModalTxtEmail.current.select();
                        });

                    }
                });

            }
        });

    }

    removeParticipantFromGroup = (rowIndex, participant) => {

        var groupId = this.state.ownedGroup.groupId;
        var email = participant.email;

        rest({
            method: 'delete',
            url: '/api/groups/{group-id}/participants',
            pathVariables: {
                'group-id': groupId
            },
            params: {
                'email': email
            },
            callback: (response) => {

                removeStateArrayElement(this, 'ownedGroupParticipants', rowIndex, () => {
                    this.ownedGroupParticipantsModalTxtEmail.current.focus();
                });

            }
        });

    }

    closeOwnedGroupParticipantsModal = () => {

        this.setState({
            ownedGroup: null,
            ownedGroupRowIndex: -1,
            ownedGroupName: '',
            ownedGroupKey: '',
            ownedGroupParticipants: null,
            ownedGroupParticipantsModalActive: false,
            ownedGroupAddParticipantEmail: ''
        });

    }

    showOwnedGroupSecrets = (rowIndex, group) => {

        var groupId = group.groupId;

        var groupKey = decrypt(group.encryptedGroupKey, null, 'base64', 'base64', 'base64');
        if (groupKey == '') return;

        rest({
            method: 'get',
            url: '/api/groups/{group-id}/secrets',
            pathVariables: {
                'group-id': groupId
            },
            callback: (response) => {

                var ownedGroupSecrets = response;

                this.setState({
                    ownedGroup: group,
                    ownedGroupRowIndex: rowIndex,
                    ownedGroupName: group.name,
                    ownedGroupKey: groupKey,
                    ownedGroupSecrets: ownedGroupSecrets,
                    ownedGroupSecretsModalActive: true,
                    ownedGroupAddSecretName: '',
                    ownedGroupAddSecretValue: ''
                }, () => {
                    setTimeout(() => {
                        this.ownedGroupSecretsModalTxtName.current.focus();
                    }, 25);
                });

            }
        });

    }

    clipboardOwnedGroupSecretName = (rowIndex, secret) => {

        copyToClipboard(secret.name);

    }

    clipboardOwnedGroupSecretValue = (rowIndex, secret) => {

        var clearValue = decryptSymmetric(secret.value, this.state.ownedGroupKey);
        if (clearValue == '') return;

        copyToClipboard(clearValue);

    }

    blinkOwnedGroupSecretValue = (rowIndex, secret) => {

        this.showOwnedGroupSecretValue(rowIndex, secret);
        setTimeout(() => { this.hideOwnedGroupSecretValue(rowIndex, secret); }, properties.secrets.showSecretTime);

    }

    showOwnedGroupSecretValue = (rowIndex, secret) => {

        var clearValue = decryptSymmetric(secret.value, this.state.ownedGroupKey);
        if (clearValue == '') return;

        secret.clearValue = clearValue;

        setStateArrayElement(this, 'ownedGroupSecrets', rowIndex, secret, () => {
            selectTableBodyCell(this.ownedGroupSecretsModalTableBody.current, rowIndex, 1);
        });

    }

    hideOwnedGroupSecretValue = (rowIndex, secret) => {

        secret.clearValue = null;

        setStateArrayElement(this, 'ownedGroupSecrets', rowIndex, secret, () => {
            selectTableBodyCell(this.ownedGroupSecretsModalTableBody.current, rowIndex, 1);
        });

    }

    addSecretToGroup = () => {

        var groupId = this.state.ownedGroup.groupId;
        var groupKey = this.state.ownedGroupKey;
        var name = this.state.ownedGroupAddSecretName;

        var value = encryptSymmetric(this.state.ownedGroupAddSecretValue, groupKey);
        if (value == '') return;

        rest({
            method: 'post',
            url: '/api/groups/{group-id}/secrets',
            pathVariables: {
                'group-id': groupId
            },
            body: {
                name: name,
                value: value
            },
            loadingChained: true,
            callback: (response) => {

                var secret = response;

                addElementToStateArray(this, 'ownedGroupSecrets', secret, () => {
                    this.ownedGroupSecretsModalTxtName.current.select();
                });

            }
        });

    }

    deleteSecretFromGroup = (rowIndex, secret) => {

        var groupId = this.state.ownedGroup.groupId;
        var secretId = secret.secretId;

        rest({
            method: 'delete',
            url: '/api/groups/{group-id}/secrets/{secret-id}',
            pathVariables: {
                'group-id': groupId,
                'secret-id': secretId
            },
            callback: (response) => {

                removeStateArrayElement(this, 'ownedGroupSecrets', rowIndex, () => {
                    this.ownedGroupSecretsModalTxtName.current.focus();
                });

            }
        });

    }

    closeOwnedGroupSecretsModal = () => {

        this.setState({
            ownedGroup: null,
            ownedGroupRowIndex: -1,
            ownedGroupName: '',
            ownedGroupKey: '',
            ownedGroupSecrets: null,
            ownedGroupSecretsModalActive: false,
            ownedGroupAddSecretName: '',
            ownedGroupAddSecretValue: ''
        });

    }

    deleteOwnedGroup = (rowIndex, group) => {

        var groupId = group.groupId;

        modalConfirmation(
            t('global.confirmation'),
            t('groups.delete-group-modal-body'),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/groups/{group-id}',
                    pathVariables: {
                        'group-id': groupId
                    },
                    callback: (response) => {

                        removeStateArrayElement(this, 'ownedGroups', rowIndex);

                    }
                });

            }
        );

    }

    showParticipantGroupParticipants = (rowIndex, group) => {

        var groupId = group.groupId;

        var groupKey = decrypt(group.encryptedGroupKey, group.ownerAccount.encryptionPublicKey, 'base64', 'base64', 'base64');
        if (groupKey == '') return;

        rest({
            method: 'get',
            url: '/api/groups/{group-id}/participants',
            pathVariables: {
                'group-id': groupId
            },
            callback: (response) => {

                var participantGroupParticipants = response;

                this.setState({
                    participantGroup: group,
                    participantGroupRowIndex: rowIndex,
                    participantGroupName: group.name,
                    participantGroupKey: groupKey,
                    participantGroupParticipants: participantGroupParticipants,
                    participantGroupParticipantsModalActive: true
                });

            }
        });

    }

    closeParticipantGroupParticipantsModal = () => {

        this.setState({
            participantGroup: null,
            participantGroupRowIndex: -1,
            participantGroupName: '',
            participantGroupKey: '',
            participantGroupParticipants: null,
            participantGroupParticipantsModalActive: false
        });

    }

    showParticipantGroupSecrets = (rowIndex, group) => {

        var groupId = group.groupId;

        var groupKey = decrypt(group.encryptedGroupKey, group.ownerAccount.encryptionPublicKey, 'base64', 'base64', 'base64');
        if (groupKey == '') return;

        rest({
            method: 'get',
            url: '/api/groups/{group-id}/secrets',
            pathVariables: {
                'group-id': groupId
            },
            callback: (response) => {

                var participantGroupSecrets = response;

                this.setState({
                    participantGroup: group,
                    participantGroupRowIndex: rowIndex,
                    participantGroupName: group.name,
                    participantGroupKey: groupKey,
                    participantGroupSecrets: participantGroupSecrets,
                    participantGroupSecretsModalActive: true
                });

            }
        });

    }

    clipboardParticipantGroupSecretName = (rowIndex, secret) => {

        copyToClipboard(secret.name);

    }

    clipboardParticipantGroupSecretValue = (rowIndex, secret) => {

        var clearValue = decryptSymmetric(secret.value, this.state.participantGroupKey);
        if (clearValue == '') return;

        copyToClipboard(clearValue);

    }

    blinkParticipantGroupSecretValue = (rowIndex, secret) => {

        this.showParticipantGroupSecretValue(rowIndex, secret);
        setTimeout(() => { this.hideParticipantGroupSecretValue(rowIndex, secret); }, properties.secrets.showSecretTime);

    }

    showParticipantGroupSecretValue = (rowIndex, secret) => {

        var clearValue = decryptSymmetric(secret.value, this.state.participantGroupKey);
        if (clearValue == '') return;

        secret.clearValue = clearValue;

        setStateArrayElement(this, 'participantGroupSecrets', rowIndex, secret, () => {
            selectTableBodyCell(this.participantGroupSecretsModalTableBody.current, rowIndex, 1);
        });

    }

    hideParticipantGroupSecretValue = (rowIndex, secret) => {

        secret.clearValue = null;

        setStateArrayElement(this, 'participantGroupSecrets', rowIndex, secret, () => {
            selectTableBodyCell(this.participantGroupSecretsModalTableBody.current, rowIndex, 1);
        });

    }

    closeParticipantGroupSecretsModal = () => {

        this.setState({
            participantGroup: null,
            participantGroupRowIndex: -1,
            participantGroupName: '',
            participantGroupKey: '',
            participantGroupSecrets: null,
            participantGroupSecretsModalActive: false
        });

    }

    render = () => {

        return (
            <Container>

                <h4>{t('groups.owned-groups')}</h4><hr />

                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <Button color="primary" onClick={this.newGroup}><Octicon className="button-icon" icon={DiffAdded} />{t('groups.new-group')}</Button>
                    <Button color="secondary" onClick={() => { this.loadOwnedGroups() }}><Octicon className="button-icon" icon={Sync} />{t('global.reload')}</Button>
                </div>

                {
                    this.state.ownedGroups == null ?
                        null :
                        this.state.ownedGroups.length == 0 ?
                            <p>{t('groups.no-owned-groups')}</p> :
                            <Table striped hover>
                                <thead>
                                    <tr>
                                        <th style={{ width: '100%' }}>{t('global.name')}</th>
                                        <th style={{ width: '4rem' }}></th>
                                        <th style={{ width: '4rem' }}></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {this.state.ownedGroups.map((group, i) =>
                                        <tr key={'owned-group-' + group.groupId}>
                                            <td style={{ width: '100%' }}>{group.name}</td>
                                            <td style={{ width: '4rem' }}>
                                                <span
                                                    id={"groups_icon-show-owned-group-participants-" + i}
                                                    onClick={() => { this.showOwnedGroupParticipants(i, group) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Organization} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"groups_icon-show-owned-group-participants-" + i}>
                                                    {t('groups.participants')}
                                                </UncontrolledTooltip>
                                                <span className="space-between-icons"></span>
                                                <span
                                                    id={"groups_icon-show-owned-group-secrets-" + i}
                                                    onClick={() => { this.showOwnedGroupSecrets(i, group) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Lock} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"groups_icon-show-owned-group-secrets-" + i}>
                                                    {t('groups.secrets')}
                                                </UncontrolledTooltip>
                                            </td>
                                            <td style={{ width: '4rem' }}>
                                                <span className="space-between-icons"></span>
                                                <span
                                                    id={"groups_icon-delete-owned-group-" + i}
                                                    onClick={() => { this.deleteOwnedGroup(i, group) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Trashcan} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"groups_icon-delete-owned-group-" + i}>
                                                    {t('global.delete')}
                                                </UncontrolledTooltip>
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </Table>
                }

                <Modal isOpen={this.state.newGroupModalActive} toggle={this.cancelNewGroup}>
                    <ModalHeader>{t('groups.new-group')}</ModalHeader>
                    <ModalBody>
                        <Form id="groups_form-new-group" onSubmit={(e) => { e.preventDefault(); this.commitNewGroup(); }}>
                            <FormGroup>
                                <Input
                                    innerRef={this.newGroupModalTxtName}
                                    type="text"
                                    placeholder={t('global.name')}
                                    maxLength={properties.groups.groupNameMaxLength}
                                    required
                                    onChange={(e) => { this.setState({ newGroupName: e.target.value }) }}
                                />
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button type="submit" form="groups_form-new-group" color="primary">{t('global.create')}</Button>
                        <Button color="secondary" onClick={this.cancelNewGroup}>{t('global.cancel')}</Button>
                    </ModalFooter>
                </Modal>

                <Modal isOpen={this.state.ownedGroupParticipantsModalActive} toggle={this.closeOwnedGroupParticipantsModal}>
                    <ModalHeader>{t('groups.participants')}</ModalHeader>
                    <ModalBody>
                        {
                            !this.state.ownedGroupParticipants || this.state.ownedGroupParticipants.length == 0 ?
                                null :
                                <Table striped hover size="sm">
                                    <tbody>
                                        {this.state.ownedGroupParticipants.map((participant, i) =>
                                            <tr key={'participant-' + participant.email}>
                                                <td style={{ width: '100%' }}>{participant.email}</td>
                                                <td style={{ width: '4rem' }} align="center">
                                                    <span
                                                        id={"groups_icon-check-keys-owned-group-participant-" + i}
                                                        onClick={() => { openCheckKeysModal(participant.email) }}
                                                        style={{ cursor: 'pointer' }}>
                                                        <Octicon icon={Key} />
                                                    </span>
                                                    <UncontrolledTooltip placement="top" target={"groups_icon-check-keys-owned-group-participant-" + i}>
                                                        {t('accounts.check-keys')}
                                                    </UncontrolledTooltip>
                                                    <span className="space-between-icons"></span>
                                                    <span
                                                        id={"groups_icon-remove-owned-group-participant-" + i}
                                                        onClick={() => { this.removeParticipantFromGroup(i, participant) }}
                                                        style={{ cursor: 'pointer' }}>
                                                        <Octicon icon={X} />
                                                    </span>
                                                    <UncontrolledTooltip placement="top" target={"groups_icon-remove-owned-group-participant-" + i}>
                                                        {t('global.remove')}
                                                    </UncontrolledTooltip>
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                        }
                        <Form
                            innerRef={this.ownedGroupParticipantsModalForm}
                            inline
                            className="group-spaced"
                            onSubmit={(e) => { e.preventDefault(); this.addParticipantToGroup(); }}>
                            <Input
                                innerRef={this.ownedGroupParticipantsModalTxtEmail}
                                type="email"
                                style={{ flexGrow: '100' }}
                                placeholder={t('global.email')}
                                pattern={properties.general.emailPattern}
                                maxLength={properties.general.emailMaxLength}
                                required
                                onChange={(e) => { this.setState({ ownedGroupAddParticipantEmail: e.target.value }); }}
                            />
                            <Button onClick={this.ownedGroupParticipantCheckKeys} color="secondary">{t('accounts.check-keys')}</Button>
                            <Button type="submit" color="primary">{t('global.add')}</Button>
                        </Form>
                    </ModalBody>
                </Modal>

                <Modal isOpen={this.state.ownedGroupSecretsModalActive} toggle={this.closeOwnedGroupSecretsModal}>
                    <ModalHeader>{t('groups.secrets')}</ModalHeader>
                    <ModalBody>
                        {
                            !this.state.ownedGroupSecrets || this.state.ownedGroupSecrets.length == 0 ?
                                null :
                                <Table striped hover size="sm">
                                    <thead>
                                        <tr>
                                            <th style={{ width: '50%' }}>{t('global.name')}</th>
                                            <th style={{ width: '50%' }}>{t('global.value')}</th>
                                            <th style={{ width: '5rem' }}></th>
                                        </tr>
                                    </thead>
                                    <tbody ref={this.ownedGroupSecretsModalTableBody}>
                                        {this.state.ownedGroupSecrets.map((secret, i) =>
                                            <tr key={'secret-' + secret.secretId}>
                                                <td style={{ width: '50%' }}>
                                                    <div>
                                                        {secret.name}
                                                        <span className="space-between-text-and-icons"></span>
                                                        <span
                                                            id={"groups_icon-copy-owned-group-secret-name-" + i}
                                                            onClick={() => { this.clipboardOwnedGroupSecretName(i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={File} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"groups_icon-copy-owned-group-secret-name-" + i}>
                                                            {t('global.copy')}
                                                        </UncontrolledTooltip>
                                                    </div>
                                                </td>
                                                <td style={{ width: '50%' }}>
                                                    {secret.clearValue ?
                                                        <div>
                                                            {secret.clearValue}
                                                            <span className="space-between-text-and-icons"></span>
                                                            <span
                                                                id={"groups_icon-copy-owned-group-secret-value-" + i}
                                                                onClick={() => { this.clipboardOwnedGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={File} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-copy-owned-group-secret-value-" + i}>
                                                                {t('global.copy')}
                                                            </UncontrolledTooltip>
                                                            <span className="space-between-icons"></span>
                                                            <span
                                                                id={"groups_icon_icon-hide-owned-group-secret-value-" + i}
                                                                onClick={() => { this.hideOwnedGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={Shield} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon_icon-hide-owned-group-secret-value-" + i}>
                                                                {t('global.hide')}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                        :
                                                        <div>
                                                            <span
                                                                id={"groups_icon-copy-owned-group-secret-value-" + i}
                                                                onClick={() => { this.clipboardOwnedGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={File} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-copy-owned-group-secret-value-" + i}>
                                                                {t('global.copy')}
                                                            </UncontrolledTooltip>
                                                            <span className="space-between-icons"></span>
                                                            <span
                                                                id={"groups_icon-blink-owned-group-secret-value-" + i}
                                                                onClick={() => { this.blinkOwnedGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={History} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-blink-owned-group-secret-value-" + i}>
                                                                {t('global.blink')}
                                                            </UncontrolledTooltip>
                                                            <span className="space-between-icons"></span>
                                                            <span
                                                                id={"groups_icon-show-owned-group-secret-value-" + i}
                                                                onClick={() => { this.showOwnedGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={ShieldLock} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-show-owned-group-secret-value-" + i}>
                                                                {t('global.show')}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                    }
                                                </td>
                                                <td style={{ width: '5rem' }} align="center">
                                                    <span onClick={() => { this.deleteSecretFromGroup(i, secret) }} style={{ cursor: 'pointer' }}>
                                                        <Octicon icon={X} />
                                                    </span>
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                        }
                        <Form inline className="group-spaced" onSubmit={(e) => { e.preventDefault(); this.addSecretToGroup(); }}>
                            <Input
                                innerRef={this.ownedGroupSecretsModalTxtName}
                                type="text"
                                style={{ flexGrow: '50' }}
                                placeholder={t('global.name')}
                                maxLength={properties.secrets.secretNameMaxLength}
                                required
                                onChange={(e) => { this.setState({ ownedGroupAddSecretName: e.target.value }); }}
                            />
                            <Input
                                type="text"
                                style={{ flexGrow: '50' }}
                                placeholder={t('global.value')}
                                maxLength={properties.secrets.secretValueMaxLength}
                                required
                                onChange={(e) => { this.setState({ ownedGroupAddSecretValue: e.target.value }); }}
                            />
                            <Button type="submit" color="primary">{t('global.add')}</Button>
                        </Form>
                    </ModalBody>
                </Modal>

                <h4 style={{ marginTop: '4rem' }}>{t('groups.participant-groups')}</h4><hr />

                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <Button color="secondary" onClick={() => { this.loadParticipantGroups() }}><Octicon className="button-icon" icon={Sync} />{t('global.reload')}</Button>
                </div>

                {
                    this.state.participantGroups == null ?
                        null :
                        this.state.participantGroups.length == 0 ?
                            <p>{t('groups.no-participant-groups')}</p> :
                            <Table striped hover>
                                <thead>
                                    <tr>
                                        <th style={{ width: '50%' }}>{t('global.name')}</th>
                                        <th style={{ width: '50%' }}>{t('global.owner')}</th>
                                        <th style={{ width: '4rem' }}></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {this.state.participantGroups.map((group, i) =>
                                        <tr key={'participant-group-' + group.groupId}>
                                            <td style={{ width: '50%' }}>{group.name}</td>
                                            <td style={{ width: '50%' }}>
                                                {group.ownerAccount.email}
                                                <span className="space-between-text-and-icons"></span>
                                                <span
                                                    id={"groups_icon-check-keys-participant-group-owner-" + i}
                                                    onClick={() => { openCheckKeysModal(group.ownerAccount.email) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Key} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"groups_icon-check-keys-participant-group-owner-" + i}>
                                                    {t('accounts.check-keys')}
                                                </UncontrolledTooltip>
                                            </td>
                                            <td style={{ width: '4rem' }}>
                                                <span
                                                    id={"groups_icon-show-participant-group-participants-" + i}
                                                    onClick={() => { this.showParticipantGroupParticipants(i, group) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Organization} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"groups_icon-show-participant-group-participants-" + i}>
                                                    {t('groups.participants')}
                                                </UncontrolledTooltip>
                                                <span className="space-between-icons"></span>
                                                <span
                                                    id={"groups_icon-show-participant-group-secrets-" + i}
                                                    onClick={() => { this.showParticipantGroupSecrets(i, group) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Lock} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"groups_icon-show-participant-group-secrets-" + i}>
                                                    {t('groups.secrets')}
                                                </UncontrolledTooltip>
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </Table>
                }

                <Modal isOpen={this.state.participantGroupParticipantsModalActive} toggle={this.closeParticipantGroupParticipantsModal}>
                    <ModalHeader>{t('groups.participants')}</ModalHeader>
                    <ModalBody>
                        {
                            !this.state.participantGroupParticipants || this.state.participantGroupParticipants.length == 0 ?
                                null :
                                <Table striped hover size="sm">
                                    <tbody>
                                        {this.state.participantGroupParticipants.map((participant, i) =>
                                            <tr key={'participant-' + participant.email}>
                                                <td style={{ width: '100%' }}>{participant.email}</td>
                                                <td style={{ width: '4rem' }} align="center">
                                                    <span
                                                        id={"groups_icon-check-keys-participant-group-participant-" + i}
                                                        onClick={() => { openCheckKeysModal(participant.email) }}
                                                        style={{ cursor: 'pointer' }}>
                                                        <Octicon icon={Key} />
                                                    </span>
                                                    <UncontrolledTooltip placement="top" target={"groups_icon-check-keys-participant-group-participant-" + i}>
                                                        {t('accounts.check-keys')}
                                                    </UncontrolledTooltip>
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                        }
                    </ModalBody>
                </Modal>

                <Modal isOpen={this.state.participantGroupSecretsModalActive} toggle={this.closeParticipantGroupSecretsModal}>
                    <ModalHeader>{t('groups.secrets')}</ModalHeader>
                    <ModalBody>
                        {
                            !this.state.participantGroupSecrets || this.state.participantGroupSecrets.length == 0 ?
                                null :
                                <Table striped hover size="sm">
                                    <thead>
                                        <tr>
                                            <th style={{ width: '50%' }}>{t('global.name')}</th>
                                            <th style={{ width: '50%' }}>{t('global.value')}</th>
                                        </tr>
                                    </thead>
                                    <tbody ref={this.participantGroupSecretsModalTableBody}>
                                        {this.state.participantGroupSecrets.map((secret, i) =>
                                            <tr key={'secret-' + secret.secretId}>
                                                <td style={{ width: '50%' }}>
                                                    <div>
                                                        {secret.name}
                                                        <span className="space-between-text-and-icons"></span>
                                                        <span
                                                            id={"groups_icon-copy-participant-group-secret-name-" + i}
                                                            onClick={() => { this.clipboardParticipantGroupSecretName(i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={File} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"groups_icon-copy-participant-group-secret-name-" + i}>
                                                            {t('global.copy')}
                                                        </UncontrolledTooltip>
                                                    </div>
                                                </td>
                                                <td style={{ width: '50%' }}>
                                                    {secret.clearValue ?
                                                        <div>
                                                            {secret.clearValue}
                                                            <span className="space-between-text-and-icons"></span>
                                                            <span
                                                                id={"groups_icon-copy-participant-group-secret-value-" + i}
                                                                onClick={() => { this.clipboardParticipantGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={File} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-copy-participant-group-secret-value-" + i}>
                                                                {t('global.copy')}
                                                            </UncontrolledTooltip>
                                                            <span className="space-between-icons"></span>
                                                            <span
                                                                id={"groups_icon-hide-participant-group-secret-value-" + i}
                                                                onClick={() => { this.hideParticipantGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={Shield} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-hide-participant-group-secret-value-" + i}>
                                                                {t('global.hide')}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                        :
                                                        <div>
                                                            <span
                                                                id={"groups_icon-copy-participant-group-secret-value-" + i}
                                                                onClick={() => { this.clipboardParticipantGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={File} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-copy-participant-group-secret-value-" + i}>
                                                                {t('global.copy')}
                                                            </UncontrolledTooltip>
                                                            <span className="space-between-icons"></span>
                                                            <span
                                                                id={"groups_icon-blink-participant-group-secret-value-" + i}
                                                                onClick={() => { this.blinkParticipantGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={History} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-blink-participant-group-secret-value-" + i}>
                                                                {t('global.blink')}
                                                            </UncontrolledTooltip>
                                                            <span className="space-between-icons"></span>
                                                            <span
                                                                id={"groups_icon-show-participant-group-secret-value-" + i}
                                                                onClick={() => { this.showParticipantGroupSecretValue(i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={ShieldLock} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"groups_icon-show-participant-group-secret-value-" + i}>
                                                                {t('global.show')}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                    }
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                        }
                    </ModalBody>
                </Modal>

            </Container>
        );

    }

}

export default Groups;
