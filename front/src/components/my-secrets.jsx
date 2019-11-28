import React, { Component } from 'react';
import { Container, Button, Modal, ModalHeader, ModalBody, ModalFooter, Badge, Progress, Form, FormGroup, Label, Input, InputGroup, Table, UncontrolledTooltip } from 'reactstrap';
import Octicon, { DiffAdded, Sync, File, History, ShieldLock, Shield, Pencil, Trashcan, FileSymlinkFile, X } from '@primer/octicons-react'
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { encrypt, decrypt } from 'services/crypto/crypto.jsx';
import { addElementToStateArray, setStateArrayElement, removeStateArrayElement } from 'services/state-utils.jsx';
import { modalConfirmation } from 'services/modal.jsx';
import { copyToClipboard, selectTableBodyCell } from 'services/selector.jsx';
import { secretStrength, randomSecret } from 'services/secret-utils.jsx';
import properties from 'constants/properties.json';

class MySecrets extends Component {

    state = {
        mySecrets: null,
        newSecretName: '',
        newSecretValue: '',
        newSecretValueLength: 0,
        newSecretValueStrength: 0,
        newSecretModalActive: false,
        newSecretGenerateRandomValueLength: 0,
        editSecret: null,
        editSecretRowIndex: -1,
        editSecretName: '',
        editSecretValue: '',
        editSecretValueLength: 0,
        editSecretValueStrength: 0,
        editSecretModalActive: false,
        editSecretGenerateRandomValueLength: 0,
        shareSecret: null,
        shareSecretRowIndex: -1,
        shareSecretName: '',
        shareSecretValue: '',
        shareSecretAccounts: null,
        shareSecretModalActive: false,
        shareSecretReceiverEmail: ''
    };

    secretsTableBody = React.createRef();
    newSecretModalTxtName = React.createRef();
    newSecretModalTxtValue = React.createRef();
    editSecretModalTxtName = React.createRef();
    editSecretModalTxtValue = React.createRef();
    shareSecretModalTxtEmail = React.createRef();

    constructor(props) {

        super(props);
        registerViewComponent('mySecrets', this);

    }

    handleLocationChange = () => {

        if (this.state.mySecrets == null)
            this.loadSecrets();

    }

    loadSecrets = () => {

        rest({
            method: 'get',
            url: '/api/secrets',
            callback: (response) => {

                var mySecrets = response;

                this.setState({
                    mySecrets: mySecrets
                });

            }
        });

    }

    newSecret = () => {

        this.setState({
            newSecretName: '',
            newSecretValue: '',
            newSecretValueLength: 0,
            newSecretValueStrength: 0,
            newSecretModalActive: true
        }, () => {
            setTimeout(() => {
                this.newSecretModalTxtName.current.focus();
            }, 25);
        });

    }

    commitNewSecret = () => {

        var name = this.state.newSecretName;

        var value = encrypt(this.state.newSecretValue);
        if (value == '') return;

        rest({
            method: 'post',
            url: '/api/secrets',
            body: {
                name: name,
                value: value
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
                    addElementToStateArray(this, 'mySecrets', newSecret);
                });

            }
        });

    }

    newSecretGenerateRandomValue = () => {

        var randomPassword = randomSecret(this.state.newSecretGenerateRandomValueLength);
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

    cancelNewSecret = () => {

        this.setState({
            newSecretName: '',
            newSecretValue: '',
            newSecretValueLength: 0,
            newSecretValueStrength: 0,
            newSecretModalActive: false
        });

    }

    clipboardSecretName = (rowIndex, secret) => {

        copyToClipboard(secret.name);

    }

    clipboardSecretValue = (rowIndex, secret) => {

        var clearValue = decrypt(secret.value);
        if (clearValue == '') return;

        copyToClipboard(clearValue);

    }

    blinkSecretValue = (rowIndex, secret) => {

        this.showSecretValue(rowIndex, secret);
        setTimeout(() => { this.hideSecretValue(rowIndex, secret); }, properties.secrets.showSecretTime);

    }

    showSecretValue = (rowIndex, secret) => {

        var clearValue = decrypt(secret.value);
        if (clearValue == '') return;

        secret.clearValue = clearValue;

        setStateArrayElement(this, 'mySecrets', rowIndex, secret, () => {
            selectTableBodyCell(this.secretsTableBody.current, rowIndex, 1);
        });

    }

    hideSecretValue = (rowIndex, secret) => {

        secret.clearValue = null;

        setStateArrayElement(this, 'mySecrets', rowIndex, secret, () => {
            selectTableBodyCell(this.secretsTableBody.current, rowIndex, 1);
        });

    }

    editSecret = (rowIndex, secret) => {

        var clearValue = decrypt(secret.value);
        if (clearValue == '') return;

        this.setState({
            editSecret: secret,
            editSecretRowIndex: rowIndex,
            editSecretName: secret.name,
            editSecretValue: clearValue,
            editSecretValueLength: clearValue.length,
            editSecretValueStrength: secretStrength(clearValue),
            editSecretModalActive: true
        }, () => {
            setTimeout(() => {
                this.editSecretModalTxtName.current.value = secret.name;
                this.editSecretModalTxtValue.current.value = clearValue;
                this.editSecretModalTxtName.current.focus();
            }, 25);
        });

    }

    commitEditSecret = () => {

        var secretId = this.state.editSecret.secretId;
        var name = this.state.editSecretName;

        var value = encrypt(this.state.editSecretValue);
        if (value == '') return;

        rest({
            method: 'get',
            url: '/api/shared-secrets/sent/{secret-id}/receiver-accounts',
            pathVariables: {
                'secret-id': secretId
            },
            loadingChain: true,
            callback: (response) => {

                var accounts = response;
                var sharings = [];

                for (var account of accounts) {
                    sharings.push({
                        receiverEmail: account.email,
                        value: encrypt(this.state.editSecretValue, account.publicKey)
                    });
                }

                rest({
                    method: 'post',
                    url: '/api/secrets/{secret-id}',
                    pathVariables: {
                        'secret-id': secretId
                    },
                    body: {
                        name: name,
                        value: value,
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
                            setStateArrayElement(this, 'mySecrets', rowIndex, editedSecret);
                        });

                    }
                });

            }
        });

    }

    editSecretGenerateRandomValue = () => {

        var randomPassword = randomSecret(this.state.editSecretGenerateRandomValueLength);
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

    cancelEditSecret = () => {

        this.setState({
            editSecret: null,
            editSecretRowIndex: -1,
            editSecretName: '',
            editSecretValue: '',
            editSecretValueLength: 0,
            editSecretValueStrength: 0,
            editSecretModalActive: false
        });

    }

    deleteSecret = (rowIndex, secret) => {

        var secretId = secret.secretId;

        modalConfirmation(
            t('my-secrets.delete-secret-modal-title'),
            t('my-secrets.delete-secret-modal-body'),
            () => {

                rest({
                    method: 'delete',
                    url: '/api/secrets/{secret-id}',
                    pathVariables: {
                        'secret-id': secretId
                    },
                    callback: (response) => {

                        removeStateArrayElement(this, 'mySecrets', rowIndex);

                    }
                });

            }
        );

    }

    shareSecret = (rowIndex, secret) => {

        var secretId = secret.secretId;

        var clearValue = decrypt(secret.value);
        if (clearValue == '') return;

        rest({
            method: 'get',
            url: '/api/shared-secrets/sent/{secret-id}/receiver-accounts',
            pathVariables: {
                'secret-id': secretId
            },
            callback: (response) => {

                var shareSecretAccounts = response;

                this.setState({
                    shareSecret: secret,
                    shareSecretRowIndex: rowIndex,
                    shareSecretName: secret.name,
                    shareSecretValue: clearValue,
                    shareSecretAccounts: shareSecretAccounts,
                    shareSecretModalActive: true,
                    shareSecretEmail: ''
                }, () => {
                    setTimeout(() => {
                        this.shareSecretModalTxtEmail.current.focus();
                    }, 25);
                });

            }
        });

    }

    commitShareSecret = () => {

        var shareSecretId = this.state.shareSecret.secretId;
        var shareSecretReceiverEmail = this.state.shareSecretReceiverEmail;

        rest({
            method: 'get',
            url: '/api/accounts/encryption-public-key',
            params: {
                'email': shareSecretReceiverEmail
            },
            loadingChain: true,
            callback: (response) => {

                var account = response;

                var shareSecretValue = encrypt(this.state.shareSecretValue, account.publicKey);
                if (shareSecretValue == '') return;

                rest({
                    method: 'post',
                    url: '/api/shared-secrets/sent/{secret-id}',
                    pathVariables: {
                        'secret-id': shareSecretId
                    },
                    body: {
                        receiverEmail: shareSecretReceiverEmail,
                        value: shareSecretValue
                    },
                    loadingChained: true,
                    callback: (response) => {

                        addElementToStateArray(this, 'shareSecretAccounts', account, () => {
                            this.shareSecretModalTxtEmail.current.select();
                        });

                    }
                });

            }
        });

    }

    unshareSecret = (rowIndex, account) => {

        var secretId = this.state.shareSecret.secretId;
        var receiverEmail = account.email;

        rest({
            method: 'delete',
            url: '/api/shared-secrets/sent/{secret-id}',
            pathVariables: {
                'secret-id': secretId
            },
            params: {
                'receiver-email': receiverEmail
            },
            callback: (response) => {

                removeStateArrayElement(this, 'shareSecretAccounts', rowIndex, () => {
                    this.shareSecretModalTxtEmail.current.focus();
                });

            }
        });

    }

    closeShareSecretModal = () => {

        this.setState({
            shareSecret: null,
            shareSecretRowIndex: -1,
            shareSecretName: '',
            shareSecretValue: '',
            shareSecretAccounts: null,
            shareSecretModalActive: false,
            shareSecretReceiverEmail: ''
        });

    }

    render = () => {

        return (
            <Container>

                <h4>{t('my-secrets.title')}</h4><hr />

                <div className="group-spaced" style={{ margin: '1.5rem 0' }}>
                    <Button color="primary" onClick={this.newSecret}><Octicon className="button-icon" icon={DiffAdded} />{t('my-secrets.btn-new-secret')}</Button>
                    <Button color="secondary" onClick={this.loadSecrets}><Octicon className="button-icon" icon={Sync} />{t('my-secrets.btn-reload')}</Button>
                </div>

                {
                    this.state.mySecrets == null ?
                        null :
                        this.state.mySecrets.length == 0 ?
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
                                    {this.state.mySecrets.map((secret, i) =>
                                        <tr key={'secret-' + secret.secretId}>
                                            <td style={{ width: '40%' }}>
                                                <div>
                                                    {secret.name}
                                                    <span className="space-between-text-and-icons"></span>
                                                    <span
                                                        id={"my-secrets_icon-copy-secret-name-" + i}
                                                        onClick={() => { this.clipboardSecretName(i, secret) }} style={{ cursor: 'pointer' }}>
                                                        <Octicon icon={File} />
                                                    </span>
                                                    <UncontrolledTooltip placement="top" target={"my-secrets_icon-copy-secret-name-" + i}>
                                                        {t('my-secrets.copy')}
                                                    </UncontrolledTooltip>
                                                </div>
                                            </td>
                                            <td style={{ width: '60%' }}>
                                                {secret.clearValue ?
                                                    <div>
                                                        {secret.clearValue}
                                                        <span className="space-between-text-and-icons"></span>
                                                        <span
                                                            id={"my-secrets_icon-copy-secret-value-" + i}
                                                            onClick={() => { this.clipboardSecretValue(i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={File} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"my-secrets_icon-copy-secret-value-" + i}>
                                                            {t('my-secrets.copy')}
                                                        </UncontrolledTooltip>
                                                        <span className="space-between-icons"></span>
                                                        <span
                                                            id={"my-secrets_icon-hide-secret-value-" + i}
                                                            onClick={() => { this.hideSecretValue(i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={Shield} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"my-secrets_icon-hide-secret-value-" + i}>
                                                            {t('my-secrets.hide')}
                                                        </UncontrolledTooltip>
                                                    </div>
                                                    :
                                                    <div>
                                                        <span
                                                            id={"my-secrets_icon-copy-secret-value-" + i}
                                                            onClick={() => { this.clipboardSecretValue(i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={File} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"my-secrets_icon-copy-secret-value-" + i}>
                                                            {t('my-secrets.copy')}
                                                        </UncontrolledTooltip>
                                                        <span className="space-between-icons"></span>
                                                        <span
                                                            id={"my-secrets_icon-blink-secret-value-" + i}
                                                            onClick={() => { this.blinkSecretValue(i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={History} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"my-secrets_icon-blink-secret-value-" + i}>
                                                            {t('my-secrets.blink')}
                                                        </UncontrolledTooltip>
                                                        <span className="space-between-icons"></span>
                                                        <span
                                                            id={"my-secrets_icon-show-secret-value-" + i}
                                                            onClick={() => { this.showSecretValue(i, secret) }}
                                                            style={{ cursor: 'pointer' }}>
                                                            <Octicon icon={ShieldLock} />
                                                        </span>
                                                        <UncontrolledTooltip placement="top" target={"my-secrets_icon-show-secret-value-" + i}>
                                                            {t('my-secrets.show')}
                                                        </UncontrolledTooltip>
                                                    </div>
                                                }
                                            </td>
                                            <td style={{ width: '5rem' }} align="center">
                                                <span
                                                    id={"my-secrets_icon-edit-secret-" + i}
                                                    onClick={() => { this.editSecret(i, secret) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Pencil} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"my-secrets_icon-edit-secret-" + i}>
                                                    {t('my-secrets.edit')}
                                                </UncontrolledTooltip>
                                                <span className="space-between-icons"></span>
                                                <span
                                                    id={"my-secrets_icon-delete-secret-" + i}
                                                    onClick={() => { this.deleteSecret(i, secret) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={Trashcan} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"my-secrets_icon-delete-secret-" + i}>
                                                    {t('my-secrets.delete')}
                                                </UncontrolledTooltip>
                                                <span className="space-between-icons"></span>
                                                <span
                                                    id={"my-secrets_icon-share-secret-" + i}
                                                    onClick={() => { this.shareSecret(i, secret) }}
                                                    style={{ cursor: 'pointer' }}>
                                                    <Octicon icon={FileSymlinkFile} />
                                                </span>
                                                <UncontrolledTooltip placement="top" target={"my-secrets_icon-share-secret-" + i}>
                                                    {t('my-secrets.share')}
                                                </UncontrolledTooltip>
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
                                <Input
                                    innerRef={this.newSecretModalTxtName}
                                    type="text"
                                    placeholder={t('my-secrets.new-secret-modal-txt-name')}
                                    maxLength={properties.secrets.secretNameMaxLength}
                                    required
                                    onChange={(e) => { this.setState({ newSecretName: e.target.value }) }}
                                />
                            </FormGroup>
                            <FormGroup>
                                <Input
                                    innerRef={this.newSecretModalTxtValue}
                                    type="textarea"
                                    placeholder={t('my-secrets.new-secret-modal-txt-value')}
                                    maxLength={properties.secrets.secretValueMaxLength}
                                    required
                                    onChange={(e) => {
                                        var value = e.target.value;
                                        this.setState({
                                            newSecretValue: value,
                                            newSecretValueLength: value.length,
                                            newSecretValueStrength: secretStrength(value)
                                        });
                                    }}
                                />
                                <InputGroup style={{ marginTop: '.4rem' }}>
                                    <Badge color="primary" className="badge-progress" style={{ width: '30%' }}>
                                        {t('my-secrets.new-secret-modal-value-length') + this.state.newSecretValueLength + ' / ' + properties.secrets.secretValueMaxLength}
                                    </Badge>
                                    <div style={{ width: '1%' }}></div>
                                    <Progress color="primary" value={this.state.newSecretValueStrength} style={{ width: '69%' }}>
                                        {t('my-secrets.new-secret-modal-value-strength') + this.state.newSecretValueStrength + '%'}
                                    </Progress>
                                </InputGroup>
                            </FormGroup>
                        </Form>
                        <fieldset disabled={Boolean(this.state.newSecretValue)}>
                            <Form inline className="group-spaced" onSubmit={(e) => { e.preventDefault(); this.newSecretGenerateRandomValue(); }}>
                                <Label size="sm">{t('my-secrets.new-secret-modal-label-generate-random-value')}</Label>
                                <Input
                                    type="number"
                                    placeholder={t('my-secrets.new-secret-modal-txt-generate-random-value-length')}
                                    min={1}
                                    max={properties.secrets.secretValueMaxLength}
                                    pattern="[0-9]*"
                                    required
                                    bsSize="sm"
                                    style={{ flexGrow: '100' }}
                                    onChange={(e) => { this.setState({ newSecretGenerateRandomValueLength: e.target.value }); }}
                                />
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
                                <Input
                                    innerRef={this.editSecretModalTxtName}
                                    type="text"
                                    placeholder={t('my-secrets.edit-secret-modal-txt-name')}
                                    maxLength={properties.secrets.secretNameMaxLength}
                                    required
                                    onChange={(e) => { this.setState({ editSecretName: e.target.value }) }}
                                />
                            </FormGroup>
                            <FormGroup>
                                <Input
                                    innerRef={this.editSecretModalTxtValue}
                                    type="textarea"
                                    placeholder={t('my-secrets.edit-secret-modal-txt-value')}
                                    maxLength={properties.secrets.secretValueMaxLength}
                                    required
                                    onChange={(e) => {
                                        var value = e.target.value;
                                        this.setState({
                                            editSecretValue: value,
                                            editSecretValueLength: value.length,
                                            editSecretValueStrength: secretStrength(value)
                                        });
                                    }}
                                />
                                <InputGroup style={{ marginTop: '.4rem' }}>
                                    <Badge color="primary" className="badge-progress" style={{ width: '30%' }}>
                                        {t('my-secrets.edit-secret-modal-value-length') + this.state.editSecretValueLength + ' / ' + properties.secrets.secretValueMaxLength}
                                    </Badge>
                                    <div style={{ width: '1%' }}></div>
                                    <Progress color="primary" value={this.state.editSecretValueStrength} style={{ width: '69%' }}>
                                        {t('my-secrets.edit-secret-modal-value-strength') + this.state.editSecretValueStrength + '%'}
                                    </Progress>
                                </InputGroup>
                            </FormGroup>
                        </Form>
                        <fieldset disabled={Boolean(this.state.editSecretValue)}>
                            <Form inline className="group-spaced" onSubmit={(e) => { e.preventDefault(); this.editSecretGenerateRandomValue(); }}>
                                <Label size="sm">{t('my-secrets.edit-secret-modal-label-generate-random-value')}</Label>
                                <Input
                                    type="number"
                                    placeholder={t('my-secrets.edit-secret-modal-txt-generate-random-value-length')}
                                    min={1}
                                    max={properties.secrets.secretValueMaxLength}
                                    pattern="[0-9]*"
                                    required
                                    bsSize="sm"
                                    style={{ flexGrow: '100' }}
                                    onChange={(e) => { this.setState({ editSecretGenerateRandomValueLength: e.target.value }); }}
                                />
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
                                                    <span onClick={() => { this.unshareSecret(i, account) }} style={{ cursor: 'pointer' }}>
                                                        <Octicon icon={X} />
                                                    </span>
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                        }
                        <Form inline className="group-spaced" onSubmit={(e) => { e.preventDefault(); this.commitShareSecret(); }}>
                            <Input
                                innerRef={this.shareSecretModalTxtEmail}
                                type="email"
                                style={{ flexGrow: '100' }}
                                placeholder={t('my-secrets.share-secret-modal-txt-email')}
                                pattern={properties.general.emailPattern}
                                maxLength={properties.general.emailMaxLength}
                                required
                                onChange={(e) => { this.setState({ shareSecretReceiverEmail: e.target.value }); }}
                            />
                            <Button type="submit" color="primary">{t('my-secrets.share-secret-modal-btn-share')}</Button>
                        </Form>
                    </ModalBody>
                </Modal>

            </Container>
        );

    }

}

export default MySecrets;
