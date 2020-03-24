import React, { Component } from 'react';
import { Container, Button, Form, FormGroup, Input, InputGroup, UncontrolledTooltip, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import Octicon, { File } from '@primer/octicons-react'
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { sessionEmail, reset } from 'services/session.jsx';
import { getEncryptionPublicKey, getSigningPublicKey } from 'services/crypto/crypto.jsx';
import { modalMessage } from 'services/modal.jsx';
import { copyToClipboard } from 'services/selector.jsx';
import properties from 'constants/properties.json';

class MySecrets extends Component {

    state = {
        email: '',
        encryptionPublicKey: '',
        signingPublicKey: '',
        deleteAccountModalActive: false,
        deleteAccountEmail: ''
    };

    deleteAccountModalTxtEmail = React.createRef();

    constructor(props) {

        super(props);
        registerViewComponent('myAccount', this);

    }

    handleLocationChange = () => {

        this.setState({
            email: sessionEmail(),
            encryptionPublicKey: getEncryptionPublicKey('hex'),
            signingPublicKey: getSigningPublicKey('hex')
        });

    }

    openDeleteAccountModal = () => {

        this.setState({
            deleteAccountModalActive: true
        }, () => {
            setTimeout(() => {
                this.deleteAccountModalTxtEmail.current.focus();
            }, 25);
        });

    }

    closeDeleteAccountModal = () => {

        this.setState({
            deleteAccountModalActive: false,
            deleteAccountEmail: ''
        });

    }

    deleteAccount = () => {

        if (this.state.deleteAccountEmail !== this.state.email) {
            modalMessage(t('global.error'), t('my-account.incorrect-introduced-mail'), this.closeDeleteAccountModal);
            return;
        }

        rest({
            method: 'delete',
            url: '/api/accounts',
            callback: (response) => {

                modalMessage(t('my-account.h-account-deleted'), t('my-account.account-deleted'), reset);

            }
        });

    }

    render = () => {

        return (
            <Container>

                <h4>{t('my-account.title')}</h4><hr />

                <h6>{t('global.email')}</h6>
                <InputGroup>
                    <span className="space-between-text-and-icons"></span>
                    <span
                        id="my-account-copy-email"
                        onClick={() => { copyToClipboard(this.state.email) }}
                        style={{ cursor: 'pointer', marginTop: '6px' }}>
                        <Octicon icon={File} />
                    </span>
                    <UncontrolledTooltip placement="top" target="my-account-copy-email">
                        {t('global.copy')}
                    </UncontrolledTooltip>
                    <span className="space-between-text-and-icons"></span>
                    <Input
                        type="text"
                        readOnly
                        value={this.state.email}
                        onFocus={(e) => { e.target.select(); }}
                    />
                </InputGroup>
                <h6 style={{ marginTop: '15px' }}>{t('accounts.encryption-public-key')}</h6>
                <InputGroup>
                    <span className="space-between-text-and-icons"></span>
                    <span
                        id="my-account-copy-encryption-public-key"
                        onClick={() => { copyToClipboard(this.state.encryptionPublicKey) }}
                        style={{ cursor: 'pointer', marginTop: '6px' }}>
                        <Octicon icon={File} />
                    </span>
                    <UncontrolledTooltip placement="top" target="my-account-copy-encryption-public-key">
                        {t('global.copy')}
                    </UncontrolledTooltip>
                    <span className="space-between-text-and-icons"></span>
                    <Input
                        type="text"
                        readOnly
                        value={this.state.encryptionPublicKey}
                        onFocus={(e) => { e.target.select(); }}
                    />
                </InputGroup>

                <h4 style={{ marginTop: '2.75rem' }}>{t('my-account.h-delete-account')}</h4><hr />
                <Button onClick={this.openDeleteAccountModal} color="danger">{t('global.delete')}</Button>

                <Modal isOpen={this.state.deleteAccountModalActive} toggle={this.closeDeleteAccountModal}>
                    <ModalHeader>{t('global.confirmation')}</ModalHeader>
                    <ModalBody>
                        <p>{t('my-account.delete-account-modal-body', { email: this.state.email })}</p>
                        <Form id="my-account_delete-account" onSubmit={(e) => { e.preventDefault(); this.deleteAccount(); }}>
                            <FormGroup>
                                <Input
                                    innerRef={this.deleteAccountModalTxtEmail}
                                    type="email"
                                    placeholder={t('my-account.introduce-email')}
                                    pattern={properties.general.emailPattern}
                                    maxLength={properties.general.emailMaxLength}
                                    required
                                    autoFocus
                                    onChange={(e) => { this.setState({ deleteAccountEmail: e.target.value }) }}
                                />
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button type="submit" form="my-account_delete-account" color="danger">{t('global.yes')}</Button>
                        <Button onClick={this.closeDeleteAccountModal} color="secondary">{t('global.no')}</Button>
                    </ModalFooter>
                </Modal>

            </Container>
        );

    }

}

export default MySecrets;
