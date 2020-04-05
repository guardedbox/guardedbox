import React, { Component } from 'react';
import { Container, Table, Collapse } from 'reactstrap';
import { DiffAdded, Sync, ChevronUp, ChevronDown, Pencil, Trashcan, Organization, File, Eye } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { notLoading } from 'services/loading.jsx';
import { workingWithoutSession } from 'services/session.jsx';
import { processSecrets, decryptSecret, encryptSecret, recryptSymmetricKey, secretModal, closeSecretModal, copySecretValueToClipboard, blinkSecretValue } from 'services/secret-utils.jsx';
import { sortGroups, groupModal, closeGroupModal } from 'services/group-utils.jsx';
import { participantsModal } from 'services/participant-utils.jsx';
import { confirmationModal } from 'services/modal.jsx';
import { loadCollapsersOpen, expandAllCollapsers, collapseAllCollapsers, toggleCollapser } from 'services/collapsers.jsx';

class MyGroups extends Component {

    state = {
        myGroups: null,
        newGroupModalActive: false,
        newGroupName: '',
        collapsersOpen: {}
    };

    constructor(props) {

        super(props);
        registerView('myGroups', this);

    }

    handleLocationChange = () => {

        if (!workingWithoutSession()) {
            this.loadGroups(false);
        }

    }

    loadGroups = (loading) => {

        rest({
            method: 'get',
            url: '/api/groups/owned',
            loading: loading,
            callback: (response) => {

                var myGroups = response;

                for (var group of myGroups) {
                    group.name = decryptSecret(group.name, null, group.encryptedKey).decryptedSecret
                    group.secrets = processSecrets(group.secrets, group.encryptedKey);
                }

                sortGroups(myGroups);

                this.setState({
                    myGroups: myGroups,
                    collapsersOpen: loadCollapsersOpen(this, myGroups, 'groupId')
                });

            }
        });

    }

    createGroup = (group) => {

        var groupEncryption = encryptSecret(group);
        if (!groupEncryption) return;

        rest({
            method: 'post',
            url: '/api/groups',
            body: {
                name: groupEncryption.encryptedSecret.name,
                encryptedKey: groupEncryption.encryptedSymmetricKeyForMe
            },
            callback: (response) => {

                closeGroupModal(() => {
                    this.loadGroups(false);
                });

            }
        });

    }

    editGroup = (group, originalGroup) => {

        var groupEncryption = encryptSecret(group, null, originalGroup.encryptedKey);
        if (!groupEncryption) return;

        rest({
            method: 'post',
            url: '/api/groups/{group-id}',
            pathVariables: {
                'group-id': originalGroup.groupId
            },
            body: {
                name: groupEncryption.encryptedSecret.name,
                encryptedKey: groupEncryption.encryptedSymmetricKeyForMe
            },
            callback: (response) => {

                closeGroupModal(() => {
                    this.loadGroups(false);
                });

            }
        });

    }

    deleteGroup = (group) => {

        confirmationModal(
            t('global.confirmation'),
            t('my-groups.delete-group-modal-body', { group: group.name }),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/groups/{group-id}',
                    pathVariables: {
                        'group-id': group.groupId
                    },
                    callback: (response) => {

                        this.loadGroups(false);

                    }
                });

            }
        );

    }

    loadGroupParticipants = (group, callback) => {

        rest({
            method: 'get',
            url: '/api/groups/{group-id}/participants',
            pathVariables: {
                'group-id': group.groupId
            },
            callback: (response) => {

                notLoading(() => {

                    callback(response);

                });

            }
        });

    }

    addParticipantToGroup = (group, account, callback) => {

        rest({
            method: 'get',
            url: '/api/accounts/public-keys',
            params: {
                'email': account.email
            },
            loadingChain: true,
            callback: (response) => {

                account = response;

                var encryptedKey = recryptSymmetricKey(null, group.encryptedKey, null, account.encryptionPublicKey);
                if (!encryptedKey) return;

                rest({
                    method: 'post',
                    url: '/api/groups/{group-id}/participants',
                    pathVariables: {
                        'group-id': group.groupId
                    },
                    body: {
                        email: account.email,
                        encryptedKey: encryptedKey
                    },
                    loadingChained: true,
                    loadingChain: true,
                    callback: (response) => {

                        callback(response);

                    }
                });

            }
        });

    }

    removeParticipantFromGroup = (group, account, callback) => {

        confirmationModal(
            t('global.confirmation'),
            t('groups.remove-participant', { group: group.name, email: account.email }),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/groups/{group-id}/participants',
                    pathVariables: {
                        'group-id': group.groupId
                    },
                    params: {
                        'email': account.email
                    },
                    callback: (response) => {

                        callback(response);
                    }
                });

            }
        );

    }

    createGroupSecret = (secretValue, _, group) => {

        var secretValueEncryption = encryptSecret(secretValue, null, group.encryptedKey);
        if (!secretValueEncryption) return;

        rest({
            method: 'post',
            url: '/api/groups/{group-id}/secrets',
            pathVariables: {
                'group-id': group.groupId
            },
            body: {
                value: JSON.stringify(secretValueEncryption.encryptedSecret)
            },
            callback: (response) => {

                closeSecretModal(() => {
                    this.loadGroups(false);
                });

            }
        });

    }

    editGroupSecret = (secretValue, originalSecret, group) => {

        var secretValueEncryption = encryptSecret(secretValue, null, group.encryptedKey);
        if (!secretValueEncryption) return;

        rest({
            method: 'post',
            url: '/api/groups/{group-id}/secrets/{secret-id}',
            pathVariables: {
                'group-id': group.groupId,
                'secret-id': originalSecret.secretId
            },
            body: {
                value: JSON.stringify(secretValueEncryption.encryptedSecret)
            },
            callback: (response) => {

                closeSecretModal(() => {
                    this.loadGroups(false);
                });

            }
        });

    }

    deleteGroupSecret = (secret, group) => {

        confirmationModal(
            t('global.confirmation'),
            t('groups.delete-secret', { secret: secret.value.name, group: group.name }),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/groups/{group-id}/secrets/{secret-id}',
                    pathVariables: {
                        'group-id': group.groupId,
                        'secret-id': secret.secretId
                    },
                    callback: (response) => {

                        closeSecretModal(() => {
                            this.loadGroups(false);
                        });

                    }
                });

            }
        );

    }

    render = () => {

        return (
            <Container>

                {/* Title and buttons */}
                <h4>{t('my-groups.title')}</h4><hr />
                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <ButtonIcon icon={DiffAdded} tooltipText={t('my-groups.btn-new-group')} color="primary"
                        onClick={() => { groupModal(t('groups.title-new-group'), null, this.createGroup) }} />
                    <ButtonIcon icon={Sync} tooltipText={t('global.reload')} color="secondary"
                        onClick={() => { this.loadGroups(true) }} />
                    <ButtonIcon icon={ChevronDown} tooltipText={t('global.expand-all')} color="success"
                        onClick={() => { expandAllCollapsers(this) }} />
                    <ButtonIcon icon={ChevronUp} tooltipText={t('global.collapse-all')} color="success"
                        onClick={() => { collapseAllCollapsers(this) }} />
                </div>

                {/* Groups tables */}
                {
                    this.state.myGroups == null ? null :
                        this.state.myGroups.length == 0 ?
                            <p>{t('my-groups.no-groups')}</p> :
                            this.state.myGroups.map((group, g) =>
                                <div key={'group-' + g}>
                                    <h5 className="view-section">
                                        <ActionIcon icon={this.state.collapsersOpen[group.groupId] ? ChevronUp : ChevronDown}
                                            onClick={() => { toggleCollapser(this, group.groupId) }} />
                                        <span className="space-between-text-and-icons"></span>
                                        <span className="text-success">{group.name}</span>
                                        <span className="space-between-text-and-icons"></span>
                                        <div style={{ float: 'right', marginRight: '16px' }}>
                                            <ActionIcon icon={DiffAdded} tooltipText={t('groups.add-secret')}
                                                onClick={() => { secretModal(t('secrets.title-new-secret'), null, this.createGroupSecret, group) }} />
                                            <span className="space-between-icons"></span>
                                            <ActionIcon icon={Pencil} tooltipText={t('global.edit')}
                                                onClick={() => { groupModal(t('groups.title-edit-group'), group, this.editGroup) }} />
                                            <span className="space-between-icons"></span>
                                            <ActionIcon icon={Trashcan} tooltipText={t('global.delete')}
                                                onClick={() => { this.deleteGroup(group) }} />
                                            <span className="space-between-icons"></span>
                                            <ActionIcon icon={Organization} tooltipText={t('groups.participants')} onClick={() => {
                                                participantsModal(
                                                    t('groups.participants'),
                                                    this.loadGroupParticipants,
                                                    this.addParticipantToGroup,
                                                    this.removeParticipantFromGroup,
                                                    group
                                                )
                                            }} />
                                        </div>
                                    </h5>
                                    <Collapse isOpen={this.state.collapsersOpen[group.groupId]}>
                                        <Table striped hover>
                                            <thead>
                                                <tr>
                                                    <th style={{ width: '40%' }}>{t('secrets.secret-name')}</th>
                                                    <th style={{ width: '60%' }}>{t('global.keys')}</th>
                                                    <th style={{ width: '6.5rem' }}></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {group.secrets.map((secret, s) =>
                                                    <tr key={'secret-' + s}>
                                                        <td style={{ width: '40%' }}>
                                                            <span>{secret.value.name}</span>
                                                        </td>
                                                        <td style={{ width: '60%' }}>
                                                            {secret.value.values.map((keyValuePair, k) =>
                                                                <div key={'key-value-pair-' + k}>
                                                                    {k == 0 ? null : <hr style={{ margin: '0.75rem -0.75rem' }} />}
                                                                    {keyValuePair.clearValue ?
                                                                        <span>{keyValuePair.clearValue}</span>
                                                                        :
                                                                        <span>
                                                                            <ActionIcon icon={File} tooltipText={t('global.copy')} onClick={() => {
                                                                                copySecretValueToClipboard(keyValuePair, group.encryptedKey)
                                                                            }} />
                                                                            <span className="space-between-icons"></span>
                                                                            <ActionIcon icon={Eye} tooltipText={t('global.show')} onClick={() => {
                                                                                blinkSecretValue(keyValuePair, group.encryptedKey, null, this, 'myGroups', g, group)
                                                                            }} />
                                                                            <span className="space-between-text-and-icons"></span>
                                                                            <span>{keyValuePair.key}</span>
                                                                        </span>
                                                                    }
                                                                </div>
                                                            )}
                                                        </td>
                                                        <td style={{ width: '6.5rem' }} align="center">
                                                            <ActionIcon icon={Pencil} tooltipText={t('global.edit')} onClick={() => {
                                                                secret.encryptedKey = group.encryptedKey;
                                                                secretModal(t('secrets.title-edit-secret'), secret, this.editGroupSecret, group);
                                                            }} />
                                                            <span className="space-between-icons"></span>
                                                            <ActionIcon icon={Trashcan} tooltipText={t('global.delete')}
                                                                onClick={() => { this.deleteGroupSecret(secret, group) }} />
                                                        </td>
                                                    </tr>
                                                )}
                                            </tbody>
                                        </Table>
                                    </Collapse>
                                </div>
                            )
                }

            </Container>
        );

    }

}

export default MyGroups;
