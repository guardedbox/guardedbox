import React, { Component } from 'react';
import { withNamespaces } from 'react-i18next';
import { withRouter } from 'react-router-dom';
import { Container, Button, Table } from 'reactstrap';
import Octicon, { Sync, Shield, X } from '@githubprimer/octicons-react'
import { get, post } from 'services/rest.jsx';
import { encrypt, decrypt } from 'services/encryption.jsx';
import { addElementToStateArray, setStateArrayElement, removeStateArrayElement } from 'services/state.jsx';
import { modalConfirmation } from 'services/modal.jsx';
import { selectTableBodyCell } from 'services/selector.jsx';
import apiPaths from 'constants/api-paths.json';

class SecretsSharedWithMe extends Component {

    constructor(props) {

        // Props.
        super(props);

        // Global reference to this component.
        window.views.secretsSharedWithMe = this;

        // Functions binding to this.
        this.handleLocationChange = this.handleLocationChange.bind(this);
        this.loadSecrets = this.loadSecrets.bind(this);
        this.decipherSecret = this.decipherSecret.bind(this);
        this.rejectSharedSecret = this.rejectSharedSecret.bind(this);

        // Refs.
        this.secretsTableBody = null;

        // State.
        this.state = {
        };

    }

    handleLocationChange() {

        if (window.views.app.state.secretsSharedWithMe == null)
            this.loadSecrets();

    }

    loadSecrets() {

        get({
            url: apiPaths.sharedSecrets.getAllSecretsSharedWithMe,
            callback: (response) => {
                var secrets = response;
                window.views.app.setState({
                    secretsSharedWithMe: secrets
                });
            }
        });

    }

    decipherSecret(accountRowIndex, account, secretRowIndex, secret, callback) {

        if (!secret.clearValue) {

            var clearValue = decrypt(secret.value);
            if (clearValue == '') return;

            secret.clearValue = clearValue;
            account.secrets[secretRowIndex] = secret;

            setStateArrayElement(window.views.app, 'secretsSharedWithMe', accountRowIndex, account, () => {
                if (callback) callback();
                else selectTableBodyCell(this.secretsTableBody[accountRowIndex].current, secretRowIndex, 1);
            });

        } else {

            callback();

        }

    }

    rejectSharedSecret(accountRowIndex, account, secretRowIndex, secret, callback) {

        const t = this.props.t;

        modalConfirmation(
            t('secrets-shared-with-me.reject-shared-secret-modal-title'),
            t('secrets-shared-with-me.reject-shared-secret-modal-body'),
            () => {

                post({
                    url: apiPaths.sharedSecrets.rejectSharedSecret,
                    body: {
                        secretId: secret.secretId
                    },
                    callback: (response) => {

                        account.secrets.splice(secretRowIndex, 1);

                        if (account.secrets.length > 0)
                            setStateArrayElement(window.views.app, 'secretsSharedWithMe', accountRowIndex, account);
                        else
                            removeStateArrayElement(window.views.app, 'secretsSharedWithMe', accountRowIndex);

                    }
                });

            }
        );

    }

    render() {

        const t = this.props.t;

        this.secretsTableBody = window.views.app.state.secretsSharedWithMe ? Array(window.views.app.state.secretsSharedWithMe.length) : null;
        if (this.secretsTableBody) for (var i = 0; i < this.secretsTableBody.length; i++) this.secretsTableBody[i] = React.createRef();

        return (
            <Container>

                <h4>{t('secrets-shared-with-me.title')}</h4><hr />

                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <Button color="secondary" onClick={this.loadSecrets}><Octicon className="button-icon" icon={Sync} />{t('secrets-shared-with-me.btn-reload')}</Button>
                </div>

                {
                    window.views.app.state.secretsSharedWithMe == null ?
                        null :
                        window.views.app.state.secretsSharedWithMe.length == 0 ?
                            <p>{t('secrets-shared-with-me.no-secrets')}</p> :
                            window.views.app.state.secretsSharedWithMe.map((account, j) =>
                                <div key={'account-' + account.email}>
                                    <h5 style={{ marginTop: '2em' }}>{t('secrets-shared-with-me.h-from') + account.email}</h5>
                                    <Table striped hover>
                                        <thead>
                                            <tr>
                                                <th style={{ width: '40%' }}>{t('secrets-shared-with-me.secrets-table-name')}</th>
                                                <th style={{ width: '60%' }}>{t('secrets-shared-with-me.secrets-table-value')}</th>
                                                <th style={{ width: '3rem' }}></th>
                                            </tr>
                                        </thead>
                                        <tbody ref={this.secretsTableBody[j]}>
                                            {account.secrets.map((secret, i) =>
                                                <tr key={'secret-' + secret.secretId}>
                                                    <td style={{ width: '40%' }}>{secret.name}</td>
                                                    <td style={{ width: '60%' }}>{secret.clearValue || <span onClick={() => { this.decipherSecret(j, account, i, secret) }} style={{ cursor: 'pointer' }}><Octicon icon={Shield} /></span>}</td>
                                                    <td style={{ width: '3rem' }} align="center">
                                                        <span onClick={() => { this.rejectSharedSecret(j, account, i, secret) }} style={{ cursor: 'pointer' }}><Octicon icon={X} /></span>
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </Table>
                                </div>
                            )
                }

            </Container>
        )

    }

}

export default withNamespaces()(withRouter(SecretsSharedWithMe));
