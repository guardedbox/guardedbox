import React, { Component } from 'react';
import { Container, Button, Table, UncontrolledTooltip } from 'reactstrap';
import Octicon, { Sync, File, History, ShieldLock, Shield, X } from '@primer/octicons-react'
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { decrypt } from 'services/crypto/crypto.jsx';
import { setStateArrayElement, removeStateArrayElement } from 'services/state-utils.jsx';
import { modalConfirmation } from 'services/modal.jsx';
import { copyToClipboard, selectTableBodyCell } from 'services/selector.jsx';
import properties from 'constants/properties.json';

class SecretsSharedWithMe extends Component {

    state = {
        secretsSharedWithMe: null
    };

    secretsTableBody = null;

    constructor(props) {

        super(props);
        registerViewComponent('secretsSharedWithMe', this);

    }

    handleLocationChange = () => {

        if (this.state.secretsSharedWithMe == null)
            this.loadSecrets();

    }

    loadSecrets = () => {

        rest({
            method: 'get',
            url: '/api/shared-secrets/received',
            callback: (response) => {

                var secretsSharedWithMe = response;

                this.setState({
                    secretsSharedWithMe: secretsSharedWithMe
                });

            }
        });

    }

    clipboardSecretName = (accountRowIndex, account, secretRowIndex, secret) => {

        copyToClipboard(secret.name);

    }

    clipboardSecretValue = (accountRowIndex, account, secretRowIndex, secret) => {

        var clearValue = decrypt(secret.value, account.encryptionPublicKey);
        if (clearValue == '') return;

        copyToClipboard(clearValue);

    }

    blinkSecretValue = (accountRowIndex, account, secretRowIndex, secret) => {

        this.showSecretValue(accountRowIndex, account, secretRowIndex, secret);
        setTimeout(() => { this.hideSecretValue(accountRowIndex, account, secretRowIndex, secret); }, properties.secrets.showSecretTime);

    }

    showSecretValue = (accountRowIndex, account, secretRowIndex, secret) => {

        var clearValue = decrypt(secret.value, account.encryptionPublicKey);
        if (clearValue == '') return;

        secret.clearValue = clearValue;
        account.secrets[secretRowIndex] = secret;

        setStateArrayElement(this, 'secretsSharedWithMe', accountRowIndex, account, () => {
            selectTableBodyCell(this.secretsTableBody[accountRowIndex].current, secretRowIndex, 1);
        });

    }

    hideSecretValue = (accountRowIndex, account, secretRowIndex, secret) => {

        secret.clearValue = null;
        account.secrets[secretRowIndex] = secret;

        setStateArrayElement(this, 'secretsSharedWithMe', accountRowIndex, account, () => {
            selectTableBodyCell(this.secretsTableBody[accountRowIndex].current, secretRowIndex, 1);
        });

    }

    rejectSharedSecret = (accountRowIndex, account, secretRowIndex, secret, callback) => {

        var secretId = secret.secretId;

        modalConfirmation(
            t('global.confirmation'),
            t('secrets-shared-with-me.reject-shared-secret-modal-body'),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/shared-secrets/received/{secret-id}',
                    pathVariables: {
                        'secret-id': secretId
                    },
                    callback: (response) => {

                        account.secrets.splice(secretRowIndex, 1);

                        if (account.secrets.length > 0) {
                            setStateArrayElement(this, 'secretsSharedWithMe', accountRowIndex, account);
                        } else {
                            removeStateArrayElement(this, 'secretsSharedWithMe', accountRowIndex);
                        }

                    }
                });

            }
        );

    }

    render = () => {

        this.secretsTableBody = this.state.secretsSharedWithMe ? Array(this.state.secretsSharedWithMe.length) : null;
        if (this.secretsTableBody) for (var i = 0; i < this.secretsTableBody.length; i++) this.secretsTableBody[i] = React.createRef();

        return (
            <Container>

                <h4>{t('secrets-shared-with-me.title')}</h4><hr />

                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <Button color="secondary" onClick={this.loadSecrets}><Octicon className="button-icon" icon={Sync} />{t('global.reload')}</Button>
                </div>

                {
                    this.state.secretsSharedWithMe == null ?
                        null :
                        this.state.secretsSharedWithMe.length == 0 ?
                            <p>{t('secrets-shared-with-me.no-secrets')}</p> :
                            this.state.secretsSharedWithMe.map((account, j) =>
                                <div key={'account-' + account.email}>
                                    <h5 style={{ marginTop: '2em' }}>{t('secrets-shared-with-me.h-from') + ' ' + account.email}</h5>
                                    <Table striped hover>
                                        <thead>
                                            <tr>
                                                <th style={{ width: '40%' }}>{t('global.name')}</th>
                                                <th style={{ width: '60%' }}>{t('global.value')}</th>
                                                <th style={{ width: '3rem' }}></th>
                                            </tr>
                                        </thead>
                                        <tbody ref={this.secretsTableBody[j]}>
                                            {account.secrets.map((secret, i) =>
                                                <tr key={'secret-' + secret.secretId}>
                                                    <td style={{ width: '40%' }}>
                                                        <div>
                                                            {secret.name}
                                                            <span className="space-between-text-and-icons"></span>
                                                            <span
                                                                id={"secrets-shared-with-me_icon-copy-secret-name-" + i}
                                                                onClick={() => { this.clipboardSecretName(j, account, i, secret) }}
                                                                style={{ cursor: 'pointer' }}>
                                                                <Octicon icon={File} />
                                                            </span>
                                                            <UncontrolledTooltip placement="top" target={"secrets-shared-with-me_icon-copy-secret-name-" + i}>
                                                                {t('global.copy')}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                    </td>
                                                    <td style={{ width: '60%' }}>
                                                        {secret.clearValue ?
                                                            <div>
                                                                {secret.clearValue}
                                                                <span className="space-between-text-and-icons"></span>
                                                                <span
                                                                    id={"secrets-shared-with-me_icon-copy-secret-value-" + i}
                                                                    onClick={() => { this.clipboardSecretValue(j, account, i, secret) }}
                                                                    style={{ cursor: 'pointer' }}>
                                                                    <Octicon icon={File} />
                                                                </span>
                                                                <UncontrolledTooltip placement="top" target={"secrets-shared-with-me_icon-copy-secret-value-" + i}>
                                                                    {t('global.copy')}
                                                                </UncontrolledTooltip>
                                                                <span className="space-between-icons"></span>
                                                                <span
                                                                    id={"secrets-shared-with-me_icon-hide-secret-value-" + i}
                                                                    onClick={() => { this.hideSecretValue(j, account, i, secret) }}
                                                                    style={{ cursor: 'pointer' }}>
                                                                    <Octicon icon={Shield} />
                                                                </span>
                                                                <UncontrolledTooltip placement="top" target={"secrets-shared-with-me_icon-hide-secret-value-" + i}>
                                                                    {t('global.hide')}
                                                                </UncontrolledTooltip>
                                                            </div>
                                                            :
                                                            <div>
                                                                <span
                                                                    id={"secrets-shared-with-me_icon-copy-secret-value-" + i}
                                                                    onClick={() => { this.clipboardSecretValue(j, account, i, secret) }}
                                                                    style={{ cursor: 'pointer' }}>
                                                                    <Octicon icon={File} />
                                                                </span>
                                                                <UncontrolledTooltip placement="top" target={"secrets-shared-with-me_icon-copy-secret-value-" + i}>
                                                                    {t('global.copy')}
                                                                </UncontrolledTooltip>
                                                                <span className="space-between-icons"></span>
                                                                <span
                                                                    id={"secrets-shared-with-me_icon-blink-secret-value-" + i}
                                                                    onClick={() => { this.blinkSecretValue(j, account, i, secret) }}
                                                                    style={{ cursor: 'pointer' }}>
                                                                    <Octicon icon={History} />
                                                                </span>
                                                                <UncontrolledTooltip placement="top" target={"secrets-shared-with-me_icon-blink-secret-value-" + i}>
                                                                    {t('global.blink')}
                                                                </UncontrolledTooltip>
                                                                <span className="space-between-icons"></span>
                                                                <span
                                                                    id={"secrets-shared-with-me_icon-show-secret-value-" + i}
                                                                    onClick={() => { this.showSecretValue(j, account, i, secret) }}
                                                                    style={{ cursor: 'pointer' }}>
                                                                    <Octicon icon={ShieldLock} />
                                                                </span>
                                                                <UncontrolledTooltip placement="top" target={"secrets-shared-with-me_icon-show-secret-value-" + i}>
                                                                    {t('global.show')}
                                                                </UncontrolledTooltip>
                                                            </div>
                                                        }
                                                    </td>
                                                    <td style={{ width: '3rem' }} align="center">
                                                        <span
                                                            id={"secrets-shared-with-me_icon-reject-shared-secret-" + i}
                                                            onClick={() => { this.rejectSharedSecret(j, account, i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={X} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"secrets-shared-with-me_icon-reject-shared-secret-" + i}>
                                                            {t('global.reject')}
                                                        </UncontrolledTooltip>
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </Table>
                                </div>
                            )
                }

            </Container>
        );

    }

}

export default SecretsSharedWithMe;
