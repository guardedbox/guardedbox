import React, { Component } from 'react';
import { Container, Form, FormGroup, Input, InputGroup, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { File, Trashcan, Check, X } from '@primer/octicons-react'
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { updateSessionInfo, sessionEmail, workingWithoutSession, reset } from 'services/session.jsx';
import { getEncryptionPublicKey, getSigningPublicKey } from 'services/crypto/crypto.jsx';
import { messageModal } from 'services/modal.jsx';
import { copyToClipboard } from 'services/selector.jsx';
import properties from 'constants/properties.json';

class MyAccount extends Component {

    state = {
        email: '',
        encryptionPublicKey: '',
        signingPublicKey: '',
        deleteAccountModalActive: false,
        deleteAccountEmail: ''
    };

    txtEncryptionPublicKey = React.createRef();
    deleteAccountModalTxtEmail = React.createRef();

    constructor(props) {

        super(props);
        registerView('myAccount', this);

    }

    handleLocationChange = () => {

        if (!workingWithoutSession()) {
            this.loadAccountData(false);
        }

    }

    loadAccountData = (loading, callback) => {

        updateSessionInfo({
            loading: loading,
            callback: () => {

                try {

                    this.setState({
                        email: sessionEmail(),
                        encryptionPublicKey: getEncryptionPublicKey('hex'),
                        signingPublicKey: getSigningPublicKey('hex')
                    }, () => {

                        setTimeout(() => {
                            this.txtEncryptionPublicKey.current.style.height = 36 + 'px';
                            var height = this.txtEncryptionPublicKey.current.scrollHeight + 3;
                            this.txtEncryptionPublicKey.current.style.height = height + 'px';
                        }, 25);

                        if (callback) callback();

                    });

                } catch (err) {
                    messageModal(t('global.error'), t('global.error-occurred'));
                    return;
                }

            }
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
            messageModal(t('global.error'), t('my-account.incorrect-introduced-mail'), this.closeDeleteAccountModal);
            return;
        }

        rest({
            method: 'delete',
            url: '/api/accounts',
            callback: (response) => {

                messageModal(t('my-account.title-account-deleted'), t('my-account.account-deleted'), reset);

            }
        });

    }

    render = () => {

        return (
            <Container>

                <h4>{t('my-account.title')}</h4><hr />

                <h6>{t('global.email')}</h6>
                <InputGroup>
                    <ActionIcon icon={File} tooltipText={t('global.copy')} style={{ marginTop: '6px', width: '40px' }}
                        onClick={() => { copyToClipboard(this.state.email) }} />
                    <Input
                        type="text"
                        readOnly
                        value={this.state.email}
                        onFocus={(e) => { e.target.select(); }} />
                </InputGroup>
                <h6 style={{ marginTop: '15px' }}>{t('accounts.encryption-public-key')}</h6>
                <InputGroup>
                    <ActionIcon icon={File} tooltipText={t('global.copy')} style={{ marginTop: '6px', width: '40px' }}
                        onClick={() => { copyToClipboard(this.state.encryptionPublicKey) }} />
                    <Input
                        innerRef={this.txtEncryptionPublicKey}
                        type="textarea"
                        readOnly
                        value={this.state.encryptionPublicKey}
                        style={{ resize: 'none' }}
                        onFocus={(e) => { e.target.select(); }}
                    />
                </InputGroup>

                <h4 style={{ marginTop: '2.75rem' }}>{t('my-account.title-delete-account')}</h4><hr />
                <ButtonIcon
                    icon={Trashcan}
                    tooltipText={t('global.delete')}
                    color="danger"
                    onClick={this.openDeleteAccountModal} />

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
                        <ButtonIcon
                            icon={Check}
                            tooltipText={t('global.yes')}
                            color="danger"
                            type="submit"
                            form="my-account_delete-account" />
                        <ButtonIcon
                            icon={X}
                            tooltipText={t('global.no')}
                            color="secondary"
                            type="button"
                            onClick={this.closeDeleteAccountModal} />
                    </ModalFooter>
                </Modal>

            </Container>
        );

    }

}

export default MyAccount;
