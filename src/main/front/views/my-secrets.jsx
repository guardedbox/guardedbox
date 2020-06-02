import React, { Component } from 'react';
import { Container, Table } from 'reactstrap';
import { DiffAdded, Sync, File, Check, Eye, EyeClosed, Clock, Pencil, Trashcan, Organization } from '@primer/octicons-react'
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { notLoading } from 'services/loading.jsx';
import { workingWithoutSession } from 'services/session.jsx';
import { processSecrets, secretModal, closeSecretModal, encryptSecret, recryptSymmetricKey, copySecretValueToClipboard, showSecretValue, hideSecretValue, blinkSecretValue, hideSecretsValues } from 'services/secret-utils.jsx';
import { participantsModal, inviteEmail } from 'services/participant-utils.jsx';
import { messageModal, confirmationModal } from 'services/modal.jsx';

class MySecrets extends Component {

    state = {
        mySecrets: null,
    };

    constructor(props) {

        super(props);
        registerView('mySecrets', this);

    }

    handleLocationChange = () => {

        hideSecretsValues(this, 'mySecrets');

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
            url: '/api/secrets/{secret-id}/must-rotate-key',
            pathVariables: {
                'secret-id': originalSecret.secretId
            },
            loadingChain: true,
            callback: (response) => {

                if (response.mustRotateKey) {

                    rest({
                        method: 'get',
                        url: '/api/shared-secrets/sent/{secret-id}/receiver-accounts',
                        pathVariables: {
                            'secret-id': originalSecret.secretId
                        },
                        loadingChained: true,
                        loadingChain: true,
                        callback: (response) => {

                            var receiverAccounts = response;
                            var secretValueEncryption = encryptSecret(secretValue, null, originalSecret.encryptedKey, null, receiverAccounts);
                            if (!secretValueEncryption) { notLoading(); return; }

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

                } else {

                    var secretValueEncryption = encryptSecret(secretValue, null, originalSecret.encryptedKey);
                    if (!secretValueEncryption) { notLoading(); return; }

                    rest({
                        method: 'post',
                        url: '/api/secrets/{secret-id}',
                        pathVariables: {
                            'secret-id': originalSecret.secretId
                        },
                        body: {
                            value: JSON.stringify(secretValueEncryption.encryptedSecret),
                            encryptedKey: secretValueEncryption.encryptedSymmetricKeyForMe
                        },
                        loadingChained: true,
                        callback: (response) => {

                            closeSecretModal(() => {
                                this.loadSecrets(false);
                            });

                        }
                    });

                }

            }
        });

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
            loadingChain: true,
            callback: (response) => {

                var accounts = response;

                rest({
                    method: 'get',
                    url: '/api/invitation-pending-action/secret/{secret-id}',
                    pathVariables: {
                        'secret-id': secret.secretId
                    },
                    loadingChained: true,
                    callback: (response) => {

                        var registrationPendingAccounts = response;

                        notLoading(() => {

                            callback({
                                accounts: accounts,
                                registrationPendingAccounts: registrationPendingAccounts
                            });

                        });

                    }
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

            },
            serviceExceptionCallback: (response) => {

                if (response.errorCode === 'accounts.email-not-registered') {
                    inviteEmail(response.additionalData.email, secret.secretId, null);
                } else {
                    messageModal(t('global.error'), t(responseJson.errorCode || 'global.error-occurred', responseJson.additionalData));
                }

            }
        });

    }

    unshareSecret = (secret, account, callback) => {

        confirmationModal(
            t('global.confirmation'),
            t(account.pendingRegistration ? 'shared-secrets.remove-pending-registration-receiver' : 'shared-secrets.remove-receiver', { secret: secret.value.name, email: account.email }),
            () => {

                if (account.pendingRegistration) {

                    rest({
                        method: 'delete',
                        url: '/api/invitation-pending-action/secret/{secret-id}',
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

                } else {

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

            }
        );

    }

    render = () => {

        return (
            <Container>

                {/* Title and buttons */}
                <h4>{t('my-secrets.title')}</h4><hr />
                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <ButtonIcon icon={DiffAdded} tooltipText={t('my-secrets.btn-new-secret')} color="primary" onClick={() => {
                        secretModal(t('secrets.title-new-secret'), null, this.createSecret)
                    }} />
                    <ButtonIcon icon={Sync} tooltipText={t('global.reload')} color="secondary" onClick={() => {
                        this.loadSecrets(true)
                    }} />
                    <ButtonIcon icon={EyeClosed} tooltipText={t('global.hide-all-secrets')} color="info" onClick={() => {
                        hideSecretsValues(this, 'mySecrets')
                    }} />
                </div>

                {/* Secrets table */}
                {
                    this.state.mySecrets == null ? null :
                        this.state.mySecrets.length == 0 ?
                            <p>{t('my-secrets.no-secrets')}</p> :
                            <Table striped hover>
                                <thead>
                                    <tr>
                                        <th className="name-col">{t('secrets.secret-name')}</th>
                                        <th className="values-col">{t('global.keys')}</th>
                                        <th className="icons-3-col"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {this.state.mySecrets.map((secret, s) =>
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
                                                                        copySecretValueToClipboard(keyValuePair, secret.encryptedKey, null, this, 'mySecrets', s, secret)
                                                                    }} />
                                                                <ActionIcon
                                                                    icon={keyValuePair.clearValue ? EyeClosed : Eye}
                                                                    tooltipText={keyValuePair.clearValue ? t('global.hide') : t('global.show')}
                                                                    onClick={() => {
                                                                        if (keyValuePair.clearValue)
                                                                            hideSecretValue(keyValuePair, this, 'mySecrets', s, secret)
                                                                        else
                                                                            showSecretValue(keyValuePair, secret.encryptedKey, null, this, 'mySecrets', s, secret)
                                                                    }} />
                                                                <ActionIcon
                                                                    icon={Clock}
                                                                    tooltipText={t('global.blink')}
                                                                    className={keyValuePair.clearValue ? 'invisible' : ''}
                                                                    onClick={() => {
                                                                        blinkSecretValue(keyValuePair, secret.encryptedKey, null, this, 'mySecrets', s, secret)
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
                                                        secretModal(t('secrets.title-edit-secret'), secret, this.editSecret)
                                                    }} />
                                                    <ActionIcon icon={Trashcan} tooltipText={t('global.delete')} onClick={() => {
                                                        this.deleteSecret(secret)
                                                    }} />
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
                                                                {
                                                                    header: t('secrets.title-share-secret'),
                                                                    accountsHeader: t('secrets.subtitle-shared-with'),
                                                                    registrationPendingAccountsHeader: t('secrets.subtitle-pending-registration'),
                                                                    noParticipants: t('secrets.not-shared')
                                                                },
                                                                this.loadSharedSecretReceiverAccounts,
                                                                this.shareSecret,
                                                                this.unshareSecret,
                                                                secret
                                                            )
                                                        }}
                                                    />
                                                </div>
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
