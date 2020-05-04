import React, { Component, Fragment } from 'react';
import { Container, Table, Collapse } from 'reactstrap';
import { Sync, ChevronUp, ChevronDown, Key, File, Eye, EyeClosed, Clock, X } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { workingWithoutSession } from 'services/session.jsx';
import { processSecrets, copySecretValueToClipboard, showSecretValue, hideSecretValue, blinkSecretValue, hideSecretsValues } from 'services/secret-utils.jsx';
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

        hideSecretsValues(this, 'secretsSharedWithMe');

        if (!workingWithoutSession()) {
            this.loadSecrets(false);
        }

    }

    loadSecrets = (loading, callback) => {

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
                }, callback);

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
                    <ButtonIcon icon={Sync} tooltipText={t('global.reload')} color="secondary" onClick={() => {
                        this.loadSecrets(true)
                    }} />
                    <ButtonIcon icon={ChevronDown} tooltipText={t('global.expand-all')} color="success" onClick={() => {
                        expandAllCollapsers(this)
                    }} />
                    <ButtonIcon icon={ChevronUp} tooltipText={t('global.collapse-all')} color="success" onClick={() => {
                        collapseAllCollapsers(this)
                    }} />
                    <ButtonIcon icon={EyeClosed} tooltipText={t('global.hide-all-secrets')} color="info" onClick={() => {
                        hideSecretsValues(this, 'secretsSharedWithMe')
                    }} />
                </div>

                {/* Shared secrets tables by owner */}
                {
                    this.state.secretsSharedWithMe == null ? null :
                        this.state.secretsSharedWithMe.length == 0 ?
                            <p>{t('secrets-shared-with-me.no-secrets')}</p> :
                            this.state.secretsSharedWithMe.map((account, a) =>
                                <div key={'account-' + a}>
                                    <h5 className="view-section">
                                        <ActionIcon icon={this.state.collapsersOpen[account.email] ? ChevronUp : ChevronDown} style={{ textAlign: 'left' }}
                                            onClick={() => { toggleCollapser(this, account.email) }} />
                                        <span className="text-success">{t('secrets-shared-with-me.title-from') + ' ' + account.email}</span>
                                        <div style={{ float: 'right', marginRight: '16px' }}>
                                            <ActionIcon icon={Key} tooltipText={t('accounts.check-keys')}
                                                onClick={() => { checkKeysModal(account.email) }} />
                                        </div>
                                    </h5>
                                    <Collapse isOpen={this.state.collapsersOpen[account.email]}>
                                        <Table striped hover>
                                            <thead>
                                                <tr>
                                                    <th className="name-col">{t('secrets.secret-name')}</th>
                                                    <th className="values-col">{t('global.keys')}</th>
                                                    <th className="icons-3-col"></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {account.secrets.map((secret, s) =>
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
                                                                            <ActionIcon icon={File} tooltipText={t('global.copy')} onClick={() => {
                                                                                copySecretValueToClipboard(keyValuePair, secret.encryptedKey, account.encryptionPublicKey)
                                                                            }} />
                                                                            {keyValuePair.clearValue ?
                                                                                <Fragment>
                                                                                    <ActionIcon icon={EyeClosed} tooltipText={t('global.hide')} onClick={() => {
                                                                                        hideSecretValue(keyValuePair, this, 'secretsSharedWithMe', a, account)
                                                                                    }} />
                                                                                </Fragment>
                                                                                :
                                                                                <Fragment>
                                                                                    <ActionIcon icon={Eye} tooltipText={t('global.show')} onClick={() => {
                                                                                        showSecretValue(keyValuePair, secret.encryptedKey, account.encryptionPublicKey, this, 'secretsSharedWithMe', a, account)
                                                                                    }} />
                                                                                    <ActionIcon icon={Clock} tooltipText={t('global.blink')} onClick={() => {
                                                                                        blinkSecretValue(keyValuePair, secret.encryptedKey, account.encryptionPublicKey, this, 'secretsSharedWithMe', a, account)
                                                                                    }} />
                                                                                </Fragment>
                                                                            }
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
                                                                <ActionIcon icon={X} tooltipText={t('global.reject')} onClick={() => {
                                                                    this.rejectSharedSecret(secret)
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

export default SecretsSharedWithMe;
