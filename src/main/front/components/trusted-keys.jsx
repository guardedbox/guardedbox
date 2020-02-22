import React, { Component } from 'react';
import { Container, Button, Modal, ModalHeader, ModalBody, ModalFooter, Alert, Form, FormGroup, Input, InputGroup, Table, UncontrolledTooltip } from 'reactstrap';
import Octicon, { DiffAdded, File, Pencil, X } from '@primer/octicons-react'
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { addTrustedKey, getTrustedKeys, removeTrustedKey } from 'services/trusted-keys.jsx';
import { getEncryptionPublicKey } from 'services/crypto/crypto.jsx';
import { modalConfirmation } from 'services/modal.jsx';
import { copyToClipboard } from 'services/selector.jsx';
import properties from 'constants/properties.json';

class TrustedKeys extends Component {

    state = {
        trustedKeys: getTrustedKeys(),
        newTrustedKeyEmail: '',
        newTrustedKeyEncryptionPublicKey: ''
    };

    txtMyPublicEncryptionKey = React.createRef();
    txtSetTrustedKeyEmail = React.createRef();
    txtSetTrustedKeyEncryptionPublicKey = React.createRef();

    constructor(props) {

        super(props);
        registerViewComponent('trustedKeys', this);

    }

    loadTrustedKeys = () => {

        this.setState({
            trustedKeys: getTrustedKeys()
        });

    }

    setTrustedKey = () => {

        addTrustedKey(this.state.newTrustedKeyEmail, this.state.newTrustedKeyEncryptionPublicKey, () => {
            this.loadTrustedKeys();
        });

    }

    editTrustedKey = (i, trustedKey) => {

        this.setState({
            newTrustedKeyEmail: trustedKey.email,
            newTrustedKeyEncryptionPublicKey: trustedKey.encryptionPublicKey
        }, () => {

            this.txtSetTrustedKeyEmail.current.value = trustedKey.email;
            this.txtSetTrustedKeyEncryptionPublicKey.current.value = trustedKey.encryptionPublicKey;
            this.txtSetTrustedKeyEncryptionPublicKey.current.select();

        });

    }

    removeTrustedKey = (i, trustedKey) => {

        modalConfirmation(
            t('trusted-keys.delete-key-modal-title'),
            t('trusted-keys.delete-key-modal-body'),
            () => {

                removeTrustedKey(trustedKey.email);
                this.loadTrustedKeys();

            }
        );

    }

    render = () => {

        return (
            <Container>

                <h4>{t('trusted-keys.my-public-encryption-key')}</h4><hr />
                <InputGroup>
                    <span className="space-between-text-and-icons"></span>
                    <span
                        id="trusted-keys-copy-encryption-public-key"
                        onClick={() => { copyToClipboard(getEncryptionPublicKey()) }}
                        style={{ cursor: 'pointer', marginTop: '6px' }}>
                        <Octicon icon={File} />
                    </span>
                    <UncontrolledTooltip placement="top" target="trusted-keys-copy-encryption-public-key">
                        {t('trusted-keys.copy')}
                    </UncontrolledTooltip>
                    <span className="space-between-text-and-icons"></span>
                    <Input
                        innerRef={this.txtMyPublicEncryptionKey}
                        type="text"
                        readOnly
                        value={getEncryptionPublicKey()}
                        onFocus={() => { this.txtMyPublicEncryptionKey.current.select(); }}
                    />
                </InputGroup>

                <h4 style={{ marginTop: '3rem' }}>{t('trusted-keys.trusted-keys')}</h4><hr />
                <Alert color="secondary" className="small">{t('trusted-keys.trusted-keys-info')}</Alert>
                <Table striped hover>
                    <thead>
                        <tr>
                            <th style={{ width: '50%' }}>{t('trusted-keys.email')}</th>
                            <th style={{ width: '50%' }}>{t('trusted-keys.key')}</th>
                            <th style={{ width: '5rem' }}></th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.trustedKeys.map((trustedKey, i) =>
                            <tr key={'trusted-key-' + i}>
                                <td style={{ width: '50%' }}>{trustedKey.email}</td>
                                <td style={{ width: '50%' }}>{trustedKey.encryptionPublicKey}</td>
                                <td style={{ width: '5rem' }} align="center">
                                    <span
                                        id="trusted-keys_icon-edit-trusted-key"
                                        onClick={() => { this.editTrustedKey(i, trustedKey) }}
                                        style={{ cursor: 'pointer' }}>
                                        <Octicon icon={Pencil} />
                                    </span>
                                    <UncontrolledTooltip placement="top" target="trusted-keys_icon-edit-trusted-key">
                                        {t('trusted-keys.edit')}
                                    </UncontrolledTooltip>
                                    <span className="space-between-icons"></span>
                                    <span
                                        id="trusted-keys_icon-remove-trusted-key"
                                        onClick={() => { this.removeTrustedKey(i, trustedKey) }}
                                        style={{ cursor: 'pointer' }}>
                                        <Octicon icon={X} />
                                    </span>
                                    <UncontrolledTooltip placement="top" target="trusted-keys_icon-remove-trusted-key">
                                        {t('trusted-keys.remove')}
                                    </UncontrolledTooltip>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </Table>
                <Form inline className="group-spaced" style={{ marginBottom: '3rem' }} onSubmit={(e) => { e.preventDefault(); this.setTrustedKey(); }}>
                    <Input
                        innerRef={this.txtSetTrustedKeyEmail}
                        type="email"
                        style={{ flexGrow: '50' }}
                        placeholder={t('trusted-keys.email')}
                        pattern={properties.general.emailPattern}
                        maxLength={properties.general.emailMaxLength}
                        required
                        onChange={(e) => { this.setState({ newTrustedKeyEmail: e.target.value }); }}
                    />
                    <Input
                        innerRef={this.txtSetTrustedKeyEncryptionPublicKey}
                        type="text"
                        style={{ flexGrow: '50' }}
                        placeholder={t('trusted-keys.key')}
                        pattern={properties.keys.encryptionPublicKeyPattern}
                        maxLength={properties.keys.encryptionPublicKeyMaxLength}
                        required
                        onChange={(e) => { this.setState({ newTrustedKeyEncryptionPublicKey: e.target.value }); }}
                    />
                    <Button type="submit" color="primary">{t('trusted-keys.btn-set')}</Button>
                </Form>

            </Container>
        );

    }

}

export default TrustedKeys;
