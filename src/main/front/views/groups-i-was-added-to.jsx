import React, { Component } from 'react';
import { Container, Table, Collapse } from 'reactstrap';
import { Sync, ChevronUp, ChevronDown, X, Organization, Key, File, Check, Eye, EyeClosed, Clock } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { notLoading } from 'services/loading.jsx';
import { workingWithoutSession } from 'services/session.jsx';
import { processSecrets, decryptSecret, copySecretValueToClipboard, showSecretValue, hideSecretValue, blinkSecretValue, hideSecretsValues } from 'services/secret-utils.jsx';
import { sortGroups } from 'services/group-utils.jsx';
import { participantsModal } from 'services/participant-utils.jsx';
import { confirmationModal } from 'services/modal.jsx';
import { checkKeysModal } from 'services/check-keys.jsx';
import { loadCollapsersOpen, expandAllCollapsers, collapseAllCollapsers, toggleCollapser } from 'services/collapsers.jsx';

class GroupsIWasAddedTo extends Component {

    state = {
        groupsIWasAddedTo: null,
        collapsersOpen: {}
    };

    constructor(props) {

        super(props);
        registerView('groupsIWasAddedTo', this);

    }

    handleLocationChange = () => {

        hideSecretsValues(this, 'groupsIWasAddedTo');

        if (!workingWithoutSession()) {
            this.loadGroups(false);
        }

    }

    loadGroups = (loading, callback) => {

        rest({
            method: 'get',
            url: '/api/groups/participant',
            loading: loading,
            callback: (response) => {

                var groupsIWasAddedTo = response;

                for (var group of groupsIWasAddedTo) {
                    group.name = decryptSecret(group.name, null, group.encryptedKey, group.ownerAccount.encryptionPublicKey).decryptedSecret
                    group.secrets = processSecrets(group.secrets, group.encryptedKey, group.ownerAccount.encryptionPublicKey);
                }

                sortGroups(groupsIWasAddedTo);

                this.setState({
                    groupsIWasAddedTo: groupsIWasAddedTo,
                    collapsersOpen: loadCollapsersOpen(this, groupsIWasAddedTo, 'groupId')
                }, callback);

            }
        });

    }

    loadGroupParticipants = (group, callback) => {

        rest({
            method: 'get',
            url: '/api/groups/{group-id}/participants',
            pathVariables: {
                'group-id': group.groupId
            },
            callback: (response) => {

                var accounts = response;

                notLoading(() => {

                    callback({
                        accounts: accounts
                    });

                });

            }
        });

    }

    exitGroup = (group) => {

        confirmationModal(
            t('global.confirmation'),
            t('groups-i-was-added-to.exit-group-modal-body', { group: group.name }),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/groups/{group-id}/participant',
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

    render = () => {

        return (
            <Container>

                {/* Title and buttons */}
                <h4>{t('groups-i-was-added-to.title')}</h4><hr />
                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
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
                        hideSecretsValues(this, 'groupsIWasAddedTo')
                    }} />
                </div>

                {/* Groups tables */}
                {
                    this.state.groupsIWasAddedTo == null ? null :
                        this.state.groupsIWasAddedTo.length == 0 ?
                            <p>{t('groups-i-was-added-to.no-groups')}</p> :
                            this.state.groupsIWasAddedTo.map((group, g) =>
                                <div key={'group-' + g}>
                                    <h5 className="view-section">
                                        <ActionIcon icon={this.state.collapsersOpen[group.groupId] ? ChevronUp : ChevronDown} style={{ textAlign: 'left' }}
                                            onClick={() => { toggleCollapser(this, group.groupId) }} />
                                        <span className="text-success">{group.name}</span>
                                        <div style={{ float: 'right', marginRight: '16px' }}>
                                            <ActionIcon icon={X} tooltipText={t('global.exit')}
                                                onClick={() => { this.exitGroup(group) }} />
                                            {group.participantsVisible ?
                                                <ActionIcon
                                                    icon={Organization}
                                                    badgeText={
                                                        group.numberOfParticipants > 0 ?
                                                            group.numberOfParticipants
                                                            : null
                                                    }
                                                    badgeColor="success"
                                                    tooltipText={
                                                        group.numberOfParticipants > 0 ?
                                                            t('groups.currently-has-participants', { n: group.numberOfParticipants }) :
                                                            t('groups.participants')
                                                    }
                                                    onClick={() => {
                                                        participantsModal(
                                                            {
                                                                header: t('groups.title-participants'),
                                                                accountsHeader: t('groups.subtitle-current-participants'),
                                                                registrationPendingAccountsHeader: t('groups.subtitle-pending-registration'),
                                                                exMembersHeader: t('groups.subtitle-ex-members'),
                                                                exMembersHeaderInfo: t('groups.subtitle-ex-members-info'),
                                                                noParticipants: t('groups.no-participants')
                                                            },
                                                            this.loadGroupParticipants,
                                                            null,
                                                            null,
                                                            group
                                                        )
                                                    }} />
                                                : null
                                            }
                                        </div>
                                    </h5>
                                    <h6>
                                        {t('global.owner') + ': ' + group.ownerAccount.email}
                                        <div style={{ float: 'right', marginRight: '16px' }}>
                                            <ActionIcon icon={Key} tooltipText={t('accounts.check-keys')}
                                                onClick={() => { checkKeysModal(group.ownerAccount.email) }} />
                                        </div>
                                    </h6>
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
                                                                                    copySecretValueToClipboard(keyValuePair, group.encryptedKey, group.ownerAccount.encryptionPublicKey, this, 'groupsIWasAddedTo', g, group)
                                                                                }} />
                                                                            <ActionIcon
                                                                                icon={keyValuePair.clearValue ? EyeClosed : Eye}
                                                                                tooltipText={keyValuePair.clearValue ? t('global.hide') : t('global.show')}
                                                                                onClick={() => {
                                                                                    if (keyValuePair.clearValue)
                                                                                        hideSecretValue(keyValuePair, this, 'groupsIWasAddedTo', g, group)
                                                                                    else
                                                                                        showSecretValue(keyValuePair, group.encryptedKey, group.ownerAccount.encryptionPublicKey, this, 'groupsIWasAddedTo', g, group)
                                                                                }} />
                                                                            <ActionIcon
                                                                                icon={Clock}
                                                                                tooltipText={t('global.blink')}
                                                                                className={keyValuePair.clearValue ? 'invisible' : ''}
                                                                                onClick={() => {
                                                                                    blinkSecretValue(keyValuePair, group.encryptedKey, group.ownerAccount.encryptionPublicKey, this, 'groupsIWasAddedTo', g, group)
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

export default GroupsIWasAddedTo;
