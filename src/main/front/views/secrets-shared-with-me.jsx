import React, { Component } from 'react';
import { Container, Table, Collapse } from 'reactstrap';
import { Sync, ChevronUp, ChevronDown, Key, File, Eye, X } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { workingWithoutSession } from 'services/session.jsx';
import { processSecrets, copySecretValueToClipboard, blinkSecretValue } from 'services/secret-utils.jsx';
import { sortAccounts } from 'services/participant-utils.jsx';
import { confirmationModal } from 'services/modal.jsx';
import { checkKeysModal } from 'services/check-keys.jsx';
import { loadCollapsersOpen, expandAllCollapsers, collapseAllCollapsers, toggleCollapser } from 'services/collapsers.jsx';

class SecretsSharedWithMe extends Component {

    state = {
        secretsSharedWithMe: null,
        collapsersOpen: {}
    };

    constructor(props) {

        super(props);
        registerView('secretsSharedWithMe', this);

    }

    handleLocationChange = () => {

        if (!workingWithoutSession()) {
            this.loadSecrets(false);
        }

    }

    loadSecrets = (loading) => {

        rest({
            method: 'get',
            url: '/api/shared-secrets/received',
            loading: loading,
            callback: (response) => {

                var secretsSharedWithMe = response;

                for (var account of secretsSharedWithMe) {
                    account.secrets = processSecrets(account.secrets, null, account.encryptionPublicKey);
                }

                sortAccounts(secretsSharedWithMe);

                this.setState({
                    secretsSharedWithMe: secretsSharedWithMe,
                    collapsersOpen: loadCollapsersOpen(this, secretsSharedWithMe, 'email')
                });

            }
        });

    }

    rejectSharedSecret = (secret) => {

        confirmationModal(
            t('global.confirmation'),
            t('secrets-shared-with-me.reject-shared-secret-modal-body', { secret: secret.value.name }),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/shared-secrets/received/{secret-id}',
                    pathVariables: {
                        'secret-id': secret.secretId
                    },
                    callback: (response) => {

                        this.loadSecrets(false);

                    }
                });

            }
        );

    }

    render = () => {

        return (
            <Container>

                {/* Title and buttons */}
                <h4>{t('secrets-shared-with-me.title')}</h4><hr />
                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <ButtonIcon icon={Sync} tooltipText={t('global.reload')} color="secondary"
                        onClick={() => { this.loadSecrets(true) }} />
                    <ButtonIcon icon={ChevronDown} tooltipText={t('global.expand-all')} color="success"
                        onClick={() => { expandAllCollapsers(this) }} />
                    <ButtonIcon icon={ChevronUp} tooltipText={t('global.collapse-all')} color="success"
                        onClick={() => { collapseAllCollapsers(this) }} />
                </div>

                {/* Shared secrets tables by owner */}
                {
                    this.state.secretsSharedWithMe == null ? null :
                        this.state.secretsSharedWithMe.length == 0 ?
                            <p>{t('secrets-shared-with-me.no-secrets')}</p> :
                            this.state.secretsSharedWithMe.map((account, a) =>
                                <div key={'account-' + a}>
                                    <h5 className="view-section">
                                        <ActionIcon icon={this.state.collapsersOpen[account.email] ? ChevronUp : ChevronDown}
                                            onClick={() => { toggleCollapser(this, account.email) }} />
                                        <span className="space-between-text-and-icons"></span>
                                        <span className="text-success">{t('secrets-shared-with-me.title-from') + ' ' + account.email}</span>
                                        <span className="space-between-text-and-icons"></span>
                                        <div style={{ float: 'right', marginRight: '16px' }}>
                                            <ActionIcon icon={Key} tooltipText={t('accounts.check-keys')}
                                                onClick={() => { checkKeysModal(account.email) }} />
                                        </div>
                                    </h5>
                                    <Collapse isOpen={this.state.collapsersOpen[account.email]}>
                                        <Table striped hover>
                                            <thead>
                                                <tr>
                                                    <th style={{ width: '40%' }}>{t('secrets.secret-name')}</th>
                                                    <th style={{ width: '60%' }}>{t('global.keys')}</th>
                                                    <th style={{ width: '6.5rem' }}></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {account.secrets.map((secret, s) =>
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
                                                                                copySecretValueToClipboard(keyValuePair, secret.encryptedKey, account.encryptionPublicKey)
                                                                            }} />
                                                                            <span className="space-between-icons"></span>
                                                                            <ActionIcon icon={Eye} tooltipText={t('global.show')} onClick={() => {
                                                                                blinkSecretValue(keyValuePair, secret.encryptedKey, account.encryptionPublicKey, this, 'secretsSharedWithMe', a, account)
                                                                            }} />
                                                                            <span className="space-between-text-and-icons"></span>
                                                                            <span>{keyValuePair.key}</span>
                                                                        </span>
                                                                    }
                                                                </div>
                                                            )}
                                                        </td>
                                                        <td style={{ width: '6.5rem' }} align="center">
                                                            <ActionIcon icon={X} tooltipText={t('global.reject')}
                                                                onClick={() => { this.rejectSharedSecret(secret) }} />
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

export default SecretsSharedWithMe;
