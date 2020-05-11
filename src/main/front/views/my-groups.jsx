import React, { Component } from 'react';
import { Container, Table, Collapse } from 'reactstrap';
import { DiffAdded, Sync, ChevronUp, ChevronDown, File, Check, Eye, EyeClosed, Clock, Pencil, Trashcan, Organization } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { notLoading } from 'services/loading.jsx';
import { workingWithoutSession, sessionEmail } from 'services/session.jsx';
import { processSecrets, decryptSecret, encryptSecret, recryptSymmetricKey, secretModal, closeSecretModal, copySecretValueToClipboard, showSecretValue, hideSecretValue, blinkSecretValue, hideSecretsValues } from 'services/secret-utils.jsx';
import { sortGroups, groupModal, closeGroupModal, rotateGroupKey } from 'services/group-utils.jsx';
import { participantsModal } from 'services/participant-utils.jsx';
import { messageModal, confirmationModal } from 'services/modal.jsx';
import { loadCollapsersOpen, expandAllCollapsers, collapseAllCollapsers, toggleCollapser, expandCollapser } from 'services/collapsers.jsx';

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

        hideSecretsValues(this, 'myGroups');

        if (!workingWithoutSession()) {
            this.loadGroups(false);
        }

    }

    loadGroups = (loading, callback) => {

        rest({
            method: 'get',
            url: '/api/groups/owned',
            loading: loading,
            callback: (response) => {

                var myGroups = response;

                for (var group of myGroups) {
                    group.name = decryptSecret(group.name, null, group.encryptedKey).decryptedSecret;
                    group.secrets = processSecrets(group.secrets, group.encryptedKey);
                }

                sortGroups(myGroups);

                this.setState({
                    myGroups: myGroups,
                    collapsersOpen: loadCollapsersOpen(this, myGroups, 'groupId')
                }, callback);

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
                encryptedKey: groupEncryption.encryptedSymmetricKeyForMe,
                participantsVisible: group.participantsVisible
            },
            callback: (response) => {

                closeGroupModal(() => {
                    this.loadGroups(false);
                });

            }
        });

    }

    editGroup = (group, originalGroup) => {

        rest({
            method: 'get',
            url: '/api/groups/{groups-id}/must-rotate-key',
            pathVariables: {
                'groups-id': originalGroup.groupId
            },
            loadingChain: true,
            callback: (response) => {

                if (response.mustRotateKey) {

                    rotateGroupKey(originalGroup.groupId, group, () => {

                        notLoading(() => {

                            closeGroupModal(() => {
                                this.loadGroups(false);
                            });

                        });

                    });

                } else {

                    var groupEncryption = encryptSecret(group, null, originalGroup.encryptedKey);
                    if (!groupEncryption) { notLoading(); return; };

                    rest({
                        method: 'post',
                        url: '/api/groups/{group-id}',
                        pathVariables: {
                            'group-id': originalGroup.groupId
                        },
                        body: {
                            name: groupEncryption.encryptedSecret.name,
                            encryptedKey: groupEncryption.encryptedSymmetricKeyForMe,
                            participantsVisible: group.participantsVisible
                        },
                        loadingChained: true,
                        callback: (response) => {

                            closeGroupModal(() => {
                                this.loadGroups(false);
                            });

                        }
                    });

                }

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

                        callback(() => { this.loadGroups(false) });

                    }
                });

            },
            serviceExceptionCallback: (response) => {

                if (response.errorCode === 'accounts.email-not-registered') {

                    var email = response.additionalData.email;

                    confirmationModal(
                        t('global.information'),
                        t('accounts.email-not-registered-invite', { email: email }),
                        () => {

                            rest({
                                method: 'post',
                                url: '/api/registrations',
                                body: {
                                    email: email,
                                    fromEmail: sessionEmail(),
                                },
                                callback: (response) => {

                                    messageModal(t('accounts.invitation-success-modal-title'), t('accounts.invitation-success-modal-body', { email: email }));

                                }
                            });

                        }
                    );

                } else {

                    messageModal(t('global.error'), t(responseJson.errorCode || 'global.error-occurred', responseJson.additionalData));

                }

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

                        callback(() => { this.loadGroups(false) });

                    }
                });

            }
        );

    }

    createGroupSecret = (secretValue, _, group) => {

        rest({
            method: 'get',
            url: '/api/groups/{groups-id}/must-rotate-key',
            pathVariables: {
                'groups-id': group.groupId
            },
            loadingChain: true,
            callback: (response) => {

                var f = (encryptedKey) => {

                    var secretValueEncryption = encryptSecret(secretValue, null, encryptedKey || group.encryptedKey);
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

                            notLoading(() => {

                                closeSecretModal(() => {
                                    this.loadGroups(false);
                                });

                            });

                        }
                    });

                };

                if (response.mustRotateKey) {
                    rotateGroupKey(group.groupId, null, f);
                } else {
                    f();
                }

            }
        });

    }

    editGroupSecret = (secretValue, originalSecret, group) => {

        rest({
            method: 'get',
            url: '/api/groups/{groups-id}/must-rotate-key',
            pathVariables: {
                'groups-id': group.groupId
            },
            loadingChain: true,
            callback: (response) => {

                var f = (encryptedKey) => {

                    var secretValueEncryption = encryptSecret(secretValue, null, encryptedKey || group.encryptedKey);
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

                            notLoading(() => {

                                closeSecretModal(() => {
                                    this.loadGroups(false);
                                });

                            });

                        }
                    });

                }

                if (response.mustRotateKey) {
                    rotateGroupKey(group.groupId, null, f);
                } else {
                    f();
                }

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
                    <ButtonIcon icon={DiffAdded} tooltipText={t('my-groups.btn-new-group')} color="primary" onClick={() => {
                        groupModal(t('groups.title-new-group'), null, this.createGroup)
                    }} />
                    <ButtonIcon icon={Sync} tooltipText={t('global.reload')} color="secondary" onClick={() => {
                        this.loadGroups(true)
                    }} />
                    <ButtonIcon icon={ChevronDown} tooltipText={t('global.expand-all')} color="success" onClick={() => {
                        expandAllCollapsers(this)
                    }} />
                    <ButtonIcon icon={ChevronUp} tooltipText={t('global.collapse-all')} color="success" onClick={() => {
                        collapseAllCollapsers(this)
                    }} />
                    <ButtonIcon icon={EyeClosed} tooltipText={t('global.hide-all-secrets')} color="info" onClick={() => {
                        hideSecretsValues(this, 'myGroups')
                    }} />
                </div>

                {/* Groups tables */}
                {
                    this.state.myGroups == null ? null :
                        this.state.myGroups.length == 0 ?
                            <p>{t('my-groups.no-groups')}</p> :
                            this.state.myGroups.map((group, g) =>
                                <div key={'group-' + g}>
                                    <h5 className="view-section">
                                        <ActionIcon icon={this.state.collapsersOpen[group.groupId] ? ChevronUp : ChevronDown} style={{ textAlign: 'left' }}
                                            onClick={() => { toggleCollapser(this, group.groupId) }} />
                                        <span className="text-success">{group.name}</span>
                                        <div style={{ float: 'right', marginRight: '16px' }}>
                                            <ActionIcon icon={DiffAdded} tooltipText={t('groups.add-secret')} onClick={() => {
                                                expandCollapser(this, group.groupId);
                                                secretModal(t('secrets.title-new-secret'), null, this.createGroupSecret, group);
                                            }} />
                                            <ActionIcon icon={Pencil} tooltipText={t('global.edit')}
                                                onClick={() => { groupModal(t('groups.title-edit-group'), group, this.editGroup) }} />
                                            <ActionIcon icon={Trashcan} tooltipText={t('global.delete')}
                                                onClick={() => { this.deleteGroup(group) }} />
                                            <ActionIcon
                                                icon={Organization}
                                                badgeText={group.hadParticipants ? group.numberOfParticipants : null} badgeColor="success"
                                                tooltipText={group.hadParticipants ?
                                                    group.numberOfParticipants > 0 ?
                                                        t('groups.currently-has-participants', { n: group.numberOfParticipants }) :
                                                        t('groups.had-participants') :
                                                    t('groups.participants')
                                                }
                                                onClick={() => {
                                                    participantsModal(
                                                        t('groups.participants'),
                                                        this.loadGroupParticipants,
                                                        this.addParticipantToGroup,
                                                        this.removeParticipantFromGroup,
                                                        group
                                                    )
                                                }}
                                            />
                                        </div>
                                    </h5>
                                    <Collapse isOpen={this.state.collapsersOpen[group.groupId]}>
                                        <Table striped hover>
                                            <thead>
                                                <tr>
                                                    <th className="name-col">{t('secrets.secret-name')}</th>
                                                    <th className="values-col">{t('global.keys')}</th>
                                                    <th className="icons-3-col"></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {group.secrets.map((secret, s) =>
                                                    <tr key={'secret-' + s}>
                                                        <td className="name-col">
                                                            <span>{secret.value.name}</span>
                                                        </td>
                                                        <td className="values-col">
                                                            {secret.value.values.map((keyValuePair, k) =>
                                                                <div key={'key-value-pair-' + k}>
                                                                    {k == 0 ? null : <hr style={{ margin: '0.75rem -0.75rem' }} />}
                                                                    <div style={{ display: 'flex' }}>
                                                                        <div className="value-icons-div">
                                                                            <ActionIcon
                                                                                icon={keyValuePair.copied ? Check : File}
                                                                                tooltipText={t('global.copy')}
                                                                                onClick={() => {
                                                                                    copySecretValueToClipboard(keyValuePair, group.encryptedKey, null, this, 'myGroups', g, group)
                                                                                }} />
                                                                            <ActionIcon
                                                                                icon={keyValuePair.clearValue ? EyeClosed : Eye}
                                                                                tooltipText={keyValuePair.clearValue ? t('global.hide') : t('global.show')}
                                                                                onClick={() => {
                                                                                    if (keyValuePair.clearValue)
                                                                                        hideSecretValue(keyValuePair, this, 'myGroups', g, group)
                                                                                    else
                                                                                        showSecretValue(keyValuePair, group.encryptedKey, null, this, 'myGroups', g, group)
                                                                                }} />
                                                                            <ActionIcon
                                                                                icon={Clock}
                                                                                tooltipText={t('global.blink')}
                                                                                className={keyValuePair.clearValue ? 'invisible' : ''}
                                                                                onClick={() => {
                                                                                    blinkSecretValue(keyValuePair, group.encryptedKey, null, this, 'myGroups', g, group)
                                                                                }} />
                                                                        </div>
                                                                        <div className="value-div">
                                                                            <div>{keyValuePair.key}</div>
                                                                            {keyValuePair.clearValue ? <div className="value-txt">{keyValuePair.clearValue}</div> : null}
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            )}
                                                        </td>
                                                        <td className="icons-3-col" align="center">
                                                            <div className="icons-div">
                                                                <ActionIcon icon={Pencil} tooltipText={t('global.edit')} onClick={() => {
                                                                    secret.encryptedKey = group.encryptedKey;
                                                                    secretModal(t('secrets.title-edit-secret'), secret, this.editGroupSecret, group);
                                                                }} />
                                                                <ActionIcon icon={Trashcan} tooltipText={t('global.delete')} onClick={() => {
                                                                    this.deleteGroupSecret(secret, group)
                                                                }} />
                                                            </div>
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
