import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { withNamespaces } from 'react-i18next';
import { withRouter } from 'react-router-dom';
import { Container, Button, Modal, ModalHeader, ModalBody, ModalFooter, Badge, Progress, Form, FormGroup, Label, Input, InputGroup, Table } from 'reactstrap';
import Octicon, { DiffAdded, Sync, Shield, Pencil, Trashcan, FileSymlinkFile, X } from '@githubprimer/octicons-react'
import { get, post } from 'services/rest.jsx';
import { encrypt, decrypt } from 'services/encryption.jsx';
import { addElementToStateArray, setStateArrayElement, removeStateArrayElement } from 'services/state.jsx';
import { modalConfirmation } from 'services/modal.jsx';
import { selectTableBodyCell } from 'services/selector.jsx';
import { passwordStrength, generateRandomPassword } from 'services/strength.jsx';
import properties from 'constants/properties.json';
import apiPaths from 'constants/api-paths.json';

class MySecrets extends Component {

    constructor(props) {

        // Props.
        super(props);

        // Global reference to this component.
        window.views.mySecrets = this;

        // Functions binding to this.
        this.handleLocationChange = this.handleLocationChange.bind(this);
        this.loadSecrets = this.loadSecrets.bind(this);
        this.newSecret = this.newSecret.bind(this);
        this.commitNewSecret = this.commitNewSecret.bind(this);
        this.cancelNewSecret = this.cancelNewSecret.bind(this);
        this.newSecretGenerateRandomValue = this.newSecretGenerateRandomValue.bind(this);
        this.editSecret = this.editSecret.bind(this);
        this.commitEditSecret = this.commitEditSecret.bind(this);
        this.cancelEditSecret = this.cancelEditSecret.bind(this);
        this.editSecretGenerateRandomValue = this.editSecretGenerateRandomValue.bind(this);
        this.decipherSecret = this.decipherSecret.bind(this);
        this.deleteSecret = this.deleteSecret.bind(this);
        this.shareSecret = this.shareSecret.bind(this);
        this.commitShareSecret = this.commitShareSecret.bind(this);
        this.unshareSecret = this.unshareSecret.bind(this);
        this.closeShareSecretModal = this.closeShareSecretModal.bind(this);

        // Refs.
        this.secretsTableBody = React.createRef();
        this.newSecretModalTxtName = React.createRef();
        this.newSecretModalTxtValue = React.createRef();
        this.editSecretModalTxtName = React.createRef();
        this.editSecretModalTxtValue = React.createRef();
        this.shareSecretModalTxtEmail = React.createRef();

        // State.
        this.state = {
            newSecretModalActive: false,
            newSecretName: '',
            newSecretValue: '',
            newSecretValueLength: 0,
            newSecretValueStrength: 0,
            newSecretGenerateRandomValueLength: 0,
            editSecret: null,
            editSecretRowIndex: -1,
            editSecretModalActive: false,
            editSecretName: '',
            editSecretValue: '',
            editSecretValueLength: 0,
            editSecretValueStrength: 0,
            editSecretGenerateRandomValueLength: 0,
            shareSecret: null,
            shareSecretRowIndex: -1,
            shareSecretModalActive: false,
            shareSecretAccounts: null,
            shareSecretEmail: ''
        };

    }

    handleLocationChange() {

        if (window.views.app.state.mySecrets == null)
            this.loadSecrets();

    }

    loadSecrets() {

        get({
            url: apiPaths.mySecrets.getAll,
            callback: (response) => {
                var secrets = response;
                window.views.app.setState({
                    mySecrets: secrets
                });
            }
        });

    }

    newSecret() {

        this.setState({
            newSecretModalActive: true,
            newSecretName: '',
            newSecretValue: '',
            newSecretValueLength: 0,
            newSecretValueStrength: 0
        }, () => {
            this.newSecretModalTxtName.current.focus();
        });

    }

    commitNewSecret() {

        post({
            url: apiPaths.mySecrets.newSecret,
            body: {
                name: this.state.newSecretName,
                value: encrypt(this.state.newSecretValue)
            },
            callback: (response) => {

                var newSecret = response;

                this.setState({
                    newSecretModalActive: false,
                    newSecretName: '',
                    newSecretValue: '',
                    newSecretValueLength: 0,
                    newSecretValueStrength: 0
                }, () => {
                    newSecret.clearValue = decrypt(newSecret.value);
                    addElementToStateArray(window.views.app, 'mySecrets', newSecret);
                });

            }
        });

    }

    cancelNewSecret() {

        this.setState({
            newSecretModalActive: false,
            newSecretName: '',
            newSecretValue: '',
            newSecretValueLength: 0,
            newSecretValueStrength: 0
        });

    }

    newSecretGenerateRandomValue() {

        var randomPassword = generateRandomPassword(this.state.newSecretGenerateRandomValueLength);
        var txt = this.newSecretModalTxtValue.current;

        txt.value = randomPassword.value;

        this.setState({
            newSecretValue: randomPassword.value,
            newSecretValueLength: randomPassword.length,
            newSecretValueStrength: randomPassword.strength
        }, () => {
            txt.select();
        });

    }

    editSecret(rowIndex, secret) {

        this.decipherSecret(rowIndex, secret, () => {

            this.setState({
                editSecret: secret,
                editSecretRowIndex: rowIndex,
                editSecretModalActive: true,
                editSecretName: secret.name,
                editSecretValue: secret.clearValue,
                editSecretValueLength: secret.clearValue.length,
                editSecretValueStrength: passwordStrength(secret.clearValue)
            }, () => {
                this.editSecretModalTxtName.current.value = secret.name;
                this.editSecretModalTxtValue.current.value = secret.clearValue;
                this.editSecretModalTxtName.current.focus();
            });

        });

    }

    commitEditSecret() {

        get({
            url: apiPaths.sharedSecrets.getSharedSecretAccounts,
            params: { 'secret-id': this.state.editSecret.secretId },
            loadingChain: true,
            callback: (response) => {

                var accounts = response;
                var sharings = [];

                for (var account of accounts) {
                    sharings.push({
                        email: account.email,
                        value: encrypt(this.state.editSecretValue, account.publicKey)
                    });
                }

                post({
                    url: apiPaths.mySecrets.editSecret,
                    body: {
                        secretId: this.state.editSecret.secretId,
                        name: this.state.editSecretName,
                        value: encrypt(this.state.editSecretValue),
                        sharings: sharings
                    },
                    loadingChained: true,
                    callback: (response) => {

                        var editedSecret = response;
                        var rowIndex = this.state.editSecretRowIndex;

                        this.setState({
                            editSecret: null,
                            editSecretRowIndex: -1,
                            editSecretModalActive: false,
                            editSecretName: '',
                            editSecretValue: '',
                            editSecretValueLength: 0,
                            editSecretValueStrength: 0
                        }, () => {
                            editedSecret.clearValue = decrypt(editedSecret.value);
                            setStateArrayElement(window.views.app, 'mySecrets', rowIndex, editedSecret);
                        });

                    }
                });

            }
        });

    }

    cancelEditSecret() {

        this.setState({
            editSecret: null,
            editSecretRowIndex: -1,
            editSecretModalActive: false,
            editSecretName: '',
            editSecretValue: '',
            editSecretValueLength: 0,
            editSecretValueStrength: 0
        });

    }

    editSecretGenerateRandomValue() {

        var randomPassword = generateRandomPassword(this.state.editSecretGenerateRandomValueLength);
        var txt = this.editSecretModalTxtValue.current;

        txt.value = randomPassword.value;

        this.setState({
            editSecretValue: randomPassword.value,
            editSecretValueLength: randomPassword.length,
            editSecretValueStrength: randomPassword.strength
        }, () => {
            txt.select();
        });

    }

    decipherSecret(rowIndex, secret, callback) {

        if (!secret.clearValue) {

            var clearValue = decrypt(secret.value);
            if (clearValue == '') return;

            secret.clearValue = clearValue;

            setStateArrayElement(window.views.app, 'mySecrets', rowIndex, secret, () => {
                if (callback) callback();
                else selectTableBodyCell(this.secretsTableBody.current, rowIndex, 1);
            });

        } else {

            callback();

        }

    }

    deleteSecret(rowIndex, secret) {

        const t = this.props.t;

        modalConfirmation(
            t('my-secrets.delete-secret-modal-title'),
            t('my-secrets.delete-secret-modal-body'),
            () => {

                post({
                    url: apiPaths.mySecrets.deleteSecret,
                    body: {
                        secretId: secret.secretId
                    },
                    callback: (response) => {
                        removeStateArrayElement(window.views.app, 'mySecrets', rowIndex);
                    }
                });

            }
        );

    }

    shareSecret(rowIndex, secret) {

        this.decipherSecret(rowIndex, secret, () => {

            get({
                url: apiPaths.sharedSecrets.getSharedSecretAccounts,
                params: { 'secret-id': secret.secretId },
                callback: (response) => {

                    this.setState({
                        shareSecret: secret,
                        shareSecretRowIndex: rowIndex,
                        shareSecretModalActive: true,
                        shareSecretAccounts: response,
                        shareSecretEmail: ''
                    }, () => {
                        this.shareSecretModalTxtEmail.current.focus();
                    });

                }
            });

        });

    }

    commitShareSecret() {

        get({
            url: apiPaths.sharedSecrets.getAccountPublicKey,
            params: { 'email': this.state.shareSecretEmail },
            loadingChain: true,
            callback: (response) => {

                var account = response;

                post({
                    url: apiPaths.sharedSecrets.shareSecret,
                    body: {
                        secretId: this.state.shareSecret.secretId,
                        email: this.state.shareSecretEmail,
                        value: encrypt(this.state.shareSecret.clearValue, account.publicKey)
                    },
                    loadingChained: true,
                    callback: (response) => {

                        addElementToStateArray(this, 'shareSecretAccounts', account, () => {

                            this.shareSecretModalTxtEmail.current.select();

                            window.views.app.setState({
                                secretsSharedByMe: null
                            });

                        });

                    }
                });

            }
        });

    }

    unshareSecret(rowIndex, account) {

        post({
            url: apiPaths.sharedSecrets.unshareSecret,
            body: {
                secretId: this.state.shareSecret.secretId,
                email: account.email
            },
            callback: (response) => {

                removeStateArrayElement(this, 'shareSecretAccounts', rowIndex);

                window.views.app.setState({
                    secretsSharedByMe: null
                });


            }
        });

    }

    closeShareSecretModal() {

        this.setState({
            shareSecret: null,
            shareSecretRowIndex: -1,
            shareSecretModalActive: false,
            shareSecretAccounts: null,
            shareSecretEmail: ''
        });

    }

    render() {

        const t = this.props.t;

        return (
            <Container>

                <h4>{t('my-secrets.title')}</h4><hr />

                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <Button color="primary" onClick={this.newSecret}><Octicon className="button-icon" icon={DiffAdded} />{t('my-secrets.btn-new-secret')}</Button>
                    <Button color="secondary" onClick={this.loadSecrets}><Octicon className="button-icon" icon={Sync} />{t('my-secrets.btn-reload')}</Button>
                </div>

                {
                    window.views.app.state.mySecrets == null ?
                        null :
                        window.views.app.state.mySecrets.length == 0 ?
                            <p>{t('my-secrets.no-secrets')}</p> :
                            <Table striped hover>
                                <thead>
                                    <tr>
                                        <th style={{ width: '40%' }}>{t('my-secrets.secrets-table-name')}</th>
                                        <th style={{ width: '60%' }}>{t('my-secrets.secrets-table-value')}</th>
                                        <th style={{ width: '5rem' }}></th>
                                    </tr>
                                </thead>
                                <tbody ref={this.secretsTableBody}>
                                    {window.views.app.state.mySecrets.map((secret, i) =>
                                        <tr key={'secret-' + secret.secretId}>
                                            <td style={{ width: '40%' }}>{secret.name}</td>
                                            <td style={{ width: '60%' }}>{secret.clearValue || <span onClick={() => { this.decipherSecret(i, secret) }} style={{ cursor: 'pointer' }}><Octicon icon={Shield} /></span>}</td>
                                            <td style={{ width: '5rem' }} align="center">
                                                <span onClick={() => { this.editSecret(i, secret) }} style={{ cursor: 'pointer' }}><Octicon icon={Pencil} /></span>
                                                <span className="space-between-icons"></span>
                                                <span onClick={() => { this.deleteSecret(i, secret) }} style={{ cursor: 'pointer' }}><Octicon icon={Trashcan} /></span>
                                                <span className="space-between-icons"></span>
                                                <span onClick={() => { this.shareSecret(i, secret) }} style={{ cursor: 'pointer' }}><Octicon icon={FileSymlinkFile} /></span>
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </Table>
                }

                <Modal isOpen={this.state.newSecretModalActive} toggle={this.cancelNewSecret}>
                    <ModalHeader>{t('my-secrets.new-secret-modal-title')}</ModalHeader>
                    <ModalBody>
                        <Form id="my-secrets_form-new-secret" onSubmit={(e) => { e.preventDefault(); this.commitNewSecret(); }}>
                            <FormGroup>
                                <Input innerRef={this.newSecretModalTxtName} type="text" placeholder={t('my-secrets.new-secret-modal-txt-name')} maxLength={properties.constraints.secretNameMaxLength} required autoFocus onChange={(e) => { this.setState({ newSecretName: e.target.value }) }} />
                            </FormGroup>
                            <FormGroup>
                                <Input innerRef={this.newSecretModalTxtValue} type="textarea" placeholder={t('my-secrets.new-secret-modal-txt-value')} maxLength={properties.constraints.secretValueMaxLength} required onChange={(e) => { var value = e.target.value; this.setState({ newSecretValue: value, newSecretValueLength: value.length, newSecretValueStrength: passwordStrength(value) }) }} />
                                <InputGroup style={{ marginTop: '.4rem' }}>
                                    <Badge color="primary" className="badge-progress" style={{ width: '30%' }}>{t('my-secrets.new-secret-modal-value-length') + this.state.newSecretValueLength + ' / ' + properties.constraints.secretValueMaxLength}</Badge>
                                    <div style={{ width: '1%' }}></div>
                                    <Progress color="primary" value={this.state.newSecretValueStrength} style={{ width: '69%' }}>{t('my-secrets.new-secret-modal-value-strength') + this.state.newSecretValueStrength + '%'}</Progress>
                                </InputGroup>
                            </FormGroup>
                        </Form>
                        <fieldset disabled={Boolean(this.state.newSecretValue)}>
                            <Form inline className="group-spaced" onSubmit={(e) => { e.preventDefault(); this.newSecretGenerateRandomValue(); }}>
                                <Label size="sm">{t('my-secrets.new-secret-modal-label-generate-random-value')}</Label>
                                <Input type="number" placeholder={t('my-secrets.new-secret-modal-txt-generate-random-value-length')} min={1} max={properties.constraints.secretValueMaxLength} pattern="[0-9]*" required bsSize="sm" style={{ flexGrow: '100' }} onChange={(e) => { this.setState({ newSecretGenerateRandomValueLength: e.target.value }); }}></Input>
                                <Button type="submit" color="secondary" size="sm">{t('my-secrets.new-secret-modal-btn-generate-random-value')}</Button>
                            </Form>
                        </fieldset>
                    </ModalBody>
                    <ModalFooter>
                        <Button type="submit" form="my-secrets_form-new-secret" color="primary">{t('my-secrets.new-secret-modal-btn-create')}</Button>
                        <Button color="secondary" onClick={this.cancelNewSecret}>{t('my-secrets.new-secret-modal-btn-cancel')}</Button>
                    </ModalFooter>
                </Modal>

                <Modal isOpen={this.state.editSecretModalActive} toggle={this.cancelEditSecret}>
                    <ModalHeader>{t('my-secrets.edit-secret-modal-title')}</ModalHeader>
                    <ModalBody>
                        <Form id="my-secrets_form-edit-secret" onSubmit={(e) => { e.preventDefault(); this.commitEditSecret(); }}>
                            <FormGroup>
                                <Input innerRef={this.editSecretModalTxtName} type="text" placeholder={t('my-secrets.edit-secret-modal-txt-name')} maxLength={properties.constraints.secretNameMaxLength} required autoFocus onChange={(e) => { this.setState({ editSecretName: e.target.value }) }} />
                            </FormGroup>
                            <FormGroup>
                                <Input innerRef={this.editSecretModalTxtValue} type="textarea" placeholder={t('my-secrets.edit-secret-modal-txt-value')} maxLength={properties.constraints.secretValueMaxLength} required onChange={(e) => { var value = e.target.value; this.setState({ editSecretValue: value, editSecretValueLength: value.length, editSecretValueStrength: passwordStrength(value) }) }} />
                                <InputGroup style={{ marginTop: '.4rem' }}>
                                    <Badge color="primary" className="badge-progress" style={{ width: '30%' }}>{t('my-secrets.edit-secret-modal-value-length') + this.state.editSecretValueLength + ' / ' + properties.constraints.secretValueMaxLength}</Badge>
                                    <div style={{ width: '1%' }}></div>
                                    <Progress color="primary" value={this.state.editSecretValueStrength} style={{ width: '69%' }}>{t('my-secrets.edit-secret-modal-value-strength') + this.state.editSecretValueStrength + '%'}</Progress>
                                </InputGroup>
                            </FormGroup>
                        </Form>
                        <fieldset disabled={Boolean(this.state.editSecretValue)}>
                            <Form inline className="group-spaced" onSubmit={(e) => { e.preventDefault(); this.editSecretGenerateRandomValue(); }}>
                                <Label size="sm">{t('my-secrets.edit-secret-modal-label-generate-random-value')}</Label>
                                <Input type="number" placeholder={t('my-secrets.edit-secret-modal-txt-generate-random-value-length')} min={1} max={properties.constraints.secretValueMaxLength} pattern="[0-9]*" required bsSize="sm" style={{ flexGrow: '100' }} onChange={(e) => { this.setState({ editSecretGenerateRandomValueLength: e.target.value }); }}></Input>
                                <Button type="submit" color="secondary" size="sm">{t('my-secrets.edit-secret-modal-btn-generate-random-value')}</Button>
                            </Form>
                        </fieldset>
                    </ModalBody>
                    <ModalFooter>
                        <Button type="submit" form="my-secrets_form-edit-secret" color="primary">{t('my-secrets.edit-secret-modal-btn-edit')}</Button>
                        <Button color="secondary" onClick={this.cancelEditSecret}>{t('my-secrets.edit-secret-modal-btn-cancel')}</Button>
                    </ModalFooter>
                </Modal>

                <Modal isOpen={this.state.shareSecretModalActive} toggle={this.closeShareSecretModal}>
                    <ModalHeader>{t('my-secrets.share-secret-modal-title')}</ModalHeader>
                    <ModalBody>
                        {
                            !this.state.shareSecretAccounts || this.state.shareSecretAccounts.length == 0 ?
                                null :
                                <Table striped hover size="sm">
                                    <tbody>
                                        {this.state.shareSecretAccounts.map((account, i) =>
                                            <tr key={'account-' + account.email}>
                                                <td style={{ width: '100%' }}>{account.email}</td>
                                                <td style={{ width: '4rem' }} align="center">
                                                    <span onClick={() => { this.unshareSecret(i, account) }} style={{ cursor: 'pointer' }}><Octicon icon={X} /></span>
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                        }
                        <Form inline className="group-spaced" onSubmit={(e) => { e.preventDefault(); this.commitShareSecret(); }}>
                            <Input innerRef={this.shareSecretModalTxtEmail} type="email" style={{ flexGrow: '100' }} placeholder={t('my-secrets.share-secret-modal-txt-email')} pattern={properties.constraints.emailPattern} maxLength={properties.constraints.emailMaxLength} required onChange={(e) => { this.setState({ shareSecretEmail: e.target.value }); }} />
                            <Button type="submit" color="primary">{t('my-secrets.share-secret-modal-btn-share')}</Button>
                        </Form>
                    </ModalBody>
                </Modal>

            </Container>
        )

    }

}

export default withNamespaces()(withRouter(MySecrets));
