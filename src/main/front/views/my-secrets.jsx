import React, { Component } from 'react';
import { Container, Table } from 'reactstrap';
import { DiffAdded, Sync, File, Eye, Pencil, Trashcan, Organization } from '@primer/octicons-react'
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { notLoading } from 'services/loading.jsx';
import { workingWithoutSession } from 'services/session.jsx';
import { processSecrets, secretModal, closeSecretModal, encryptSecret, recryptSymmetricKey, copySecretValueToClipboard, blinkSecretValue } from 'services/secret-utils.jsx';
import { participantsModal } from 'services/participant-utils.jsx';
import { confirmationModal } from 'services/modal.jsx';

class MySecrets extends Component {

    state = {
        mySecrets: null,
    };

    constructor(props) {

        super(props);
        registerView('mySecrets', this);

    }

    handleLocationChange = () => {

        if (!workingWithoutSession()) {
            this.loadSecrets(false);
        }

    }

    loadSecrets = (loading, callback) => {

        rest({
            method: 'get',
            url: '/api/secrets',
            loading: loading,
            callback: (response) => {

                this.setState({
                    mySecrets: processSecrets(response)
                }, callback);

            }
        });

    }

    createSecret = (secretValue) => {

        var secretValueEncryption = encryptSecret(secretValue);
        if (!secretValueEncryption) return;

        rest({
            method: 'post',
            url: '/api/secrets',
            body: {
                value: JSON.stringify(secretValueEncryption.encryptedSecret),
                encryptedKey: secretValueEncryption.encryptedSymmetricKeyForMe
            },
            callback: (response) => {

                closeSecretModal(() => {
                    this.loadSecrets(false);
                });

            }
        });

    }

    editSecret = (secretValue, originalSecret) => {

        rest({
            method: 'get',
            url: '/api/shared-secrets/sent/{secret-id}/receiver-accounts',
            pathVariables: {
                'secret-id': originalSecret.secretId
            },
            loadingChain: true,
            callback: (response) => {

                var receiverAccounts = response;
                var secretValueEncryption = encryptSecret(secretValue, null, originalSecret.encryptedKey, null, receiverAccounts);
                if (!secretValueEncryption) return;

                rest({
                    method: 'post',
                    url: '/api/secrets/{secret-id}',
                    pathVariables: {
                        'secret-id': originalSecret.secretId
                    },
                    body: {
                        value: JSON.stringify(secretValueEncryption.encryptedSecret),
                        encryptedKey: secretValueEncryption.encryptedSymmetricKeyForMe,
                        sharings: secretValueEncryption.encryptedSymmetricKeyForOthers
                    },
                    loadingChained: true,
                    callback: (response) => {

                        closeSecretModal(() => {
                            this.loadSecrets(false);
                        });

                    }
                });

            }
        });

        // var secretValueEncryption = encryptSecret(secretValue, null, originalSecret.encryptedKey);
        // if (!secretValueEncryption) return;

        // rest({
        //     method: 'post',
        //     url: '/api/secrets/{secret-id}',
        //     pathVariables: {
        //         'secret-id': originalSecret.secretId
        //     },
        //     body: {
        //         value: JSON.stringify(secretValueEncryption.encryptedSecret),
        //         encryptedKey: secretValueEncryption.encryptedSymmetricKeyForMe
        //     },
        //     callback: (response) => {

        //         closeSecretModal(() => {
        //             this.loadSecrets(false);
        //         });

        //     }
        // });

    }

    deleteSecret = (secret) => {

        confirmationModal(
            t('global.confirmation'),
            t('my-secrets.delete-secret-modal-body', { secret: secret.value.name }),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/secrets/{secret-id}',
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

    loadSharedSecretReceiverAccounts = (secret, callback) => {

        rest({
            method: 'get',
            url: '/api/shared-secrets/sent/{secret-id}/receiver-accounts',
            pathVariables: {
                'secret-id': secret.secretId
            },
            callback: (response) => {

                notLoading(() => {

                    callback(response);

                });

            }
        });

    }

    shareSecret = (secret, account, callback) => {

        rest({
            method: 'get',
            url: '/api/accounts/public-keys',
            params: {
                'email': account.email
            },
            loadingChain: true,
            callback: (response) => {

                account = response;

                var encryptedKey = recryptSymmetricKey(null, secret.encryptedKey, null, account.encryptionPublicKey);
                if (!encryptedKey) return;

                rest({
                    method: 'post',
                    url: '/api/shared-secrets/sent/{secret-id}',
                    pathVariables: {
                        'secret-id': secret.secretId
                    },
                    body: {
                        email: account.email,
                        encryptedKey: encryptedKey
                    },
                    loadingChained: true,
                    loadingChain: true,
                    callback: (response) => {

                        callback(() => { this.loadSecrets(false) });

                    }
                });

            }
        });

    }

    unshareSecret = (secret, account, callback) => {

        confirmationModal(
            t('global.confirmation'),
            t('shared-secrets.remove-receiver', { secret: secret.value.name, email: account.email }),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/shared-secrets/sent/{secret-id}',
                    pathVariables: {
                        'secret-id': secret.secretId
                    },
                    params: {
                        'receiver-email': account.email
                    },
                    loadingChain: true,
                    callback: (response) => {

                        callback(() => { this.loadSecrets(false) });

                    }
                });

            }
        );

    }

    render = () => {

        return (
            <Container>

                {/* Title and buttons */}
                <h4>{t('my-secrets.title')}</h4><hr />
                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <ButtonIcon icon={DiffAdded} tooltipText={t('my-secrets.btn-new-secret')} color="primary"
                        onClick={() => { secretModal(t('secrets.title-new-secret'), null, this.createSecret) }} />
                    <ButtonIcon icon={Sync} tooltipText={t('global.reload')} color="secondary"
                        onClick={() => { this.loadSecrets(true) }} />
                </div>

                {/* Secrets table */}
                {
                    this.state.mySecrets == null ? null :
                        this.state.mySecrets.length == 0 ?
                            <p>{t('my-secrets.no-secrets')}</p> :
                            <Table striped hover>
                                <thead>
                                    <tr>
                                        <th style={{ width: '40%' }}>{t('secrets.secret-name')}</th>
                                        <th style={{ width: '60%' }}>{t('global.keys')}</th>
                                        <th style={{ width: '6.5rem' }}></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {this.state.mySecrets.map((secret, s) =>
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
                                                                <ActionIcon icon={File} tooltipText={t('global.copy')}
                                                                    onClick={() => { copySecretValueToClipboard(keyValuePair, secret.encryptedKey) }} />
                                                                <span className="space-between-icons"></span>
                                                                <ActionIcon icon={Eye} tooltipText={t('global.show')}
                                                                    onClick={() => { blinkSecretValue(keyValuePair, secret.encryptedKey, null, this, 'mySecrets', s, secret) }} />
                                                                <span className="space-between-text-and-icons"></span>
                                                                <span>{keyValuePair.key}</span>
                                                            </span>
                                                        }
                                                    </div>
                                                )}
                                            </td>
                                            <td style={{ width: '6.5rem' }} align="center">
                                                <ActionIcon icon={Pencil} tooltipText={t('global.edit')}
                                                    onClick={() => { secretModal(t('secrets.title-edit-secret'), secret, this.editSecret) }} />
                                                <span className="space-between-icons"></span>
                                                <ActionIcon icon={Trashcan} tooltipText={t('global.delete')}
                                                    onClick={() => { this.deleteSecret(secret) }} />
                                                <span className="space-between-icons"></span>
                                                <ActionIcon
                                                    icon={Organization}
                                                    badgeText={secret.wasShared ? secret.numberOfSharings : null} badgeColor="success"
                                                    tooltipText={secret.wasShared ?
                                                        secret.numberOfSharings > 0 ?
                                                            t('shared-secrets.currently-shared', { n: secret.numberOfSharings }) :
                                                            t('shared-secrets.was-shared') :
                                                        t('global.share')
                                                    }
                                                    onClick={() => {
                                                        participantsModal(
                                                            t('secrets.title-share-secret'),
                                                            this.loadSharedSecretReceiverAccounts,
                                                            this.shareSecret,
                                                            this.unshareSecret,
                                                            secret
                                                        )
                                                    }}
                                                />
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </Table>
                }

            </Container>
        );

    }

}

export default MySecrets;
