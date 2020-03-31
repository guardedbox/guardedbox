import React, { Component } from 'react';
import { withTranslation } from 'react-i18next';
import { withRouter, Route } from 'react-router-dom';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { Loader } from 'react-overlay-loader';
import reactOverlayLoaderCss from 'react-overlay-loader/styles.css';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Badge, Progress, Form, FormGroup, Label, Input, InputGroup, Table } from 'reactstrap';
import { File, Key, X } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import Login from 'views/login.jsx';
import Registration from 'views/registration.jsx';
import NavigationBar from 'views/navigation-bar.jsx';
import MySecrets from 'views/my-secrets.jsx';
import SecretsSharedWithMe from 'views/secrets-shared-with-me.jsx';
import Groups from 'views/groups.jsx';
import MyAccount from 'views/my-account.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { listenLocationChange } from 'services/location.jsx';
import { setStateArrayElement } from 'services/state-utils.jsx';
import { closeMessageModal, closeConfirmationModal } from 'services/modal.jsx';
import { secretStrength, secretModalAddKeyValuePair, secretModalRemoveKeyValuePair, secretModalGenerateRandomValue, buildSecretModalSecret, closeSecretModal } from 'services/secret-utils.jsx';
import { participantsModalAddParticipant, participantsModalRemoveParticipant, closeParticipantsModal } from 'services/participant-utils.jsx';
import { checkKeysModal, closeCheckKeysModal } from 'services/check-keys.jsx';
import { copyToClipboard } from 'services/selector.jsx';
import properties from 'constants/properties.json';
import views from 'constants/views.json';

class App extends Component {

    state = {
        loading: false,
        messageModalActive: false,
        messageModalHeader: '',
        messageModalBody: '',
        messageModalExitCallback: null,
        confirmationModalActive: false,
        confirmationModalHeader: '',
        confirmationModalBody: '',
        confirmationModalYesCallback: null,
        confirmationModalNoCallback: null,
        confirmationModalLock: false,
        secretModalActive: false,
        secretModalHeader: '',
        secretModalSecretName: '',
        secretModalSecretKeyValuePairs: [],
        secretModalGenerateRandomValueLength: 0,
        secretModalOriginalSecret: null,
        secretModalAcceptCallback: null,
        participantsModalActive: false,
        participantsModalHeader: '',
        participantsModalAccounts: [],
        participantsModalEmail: '',
        participantsModalLoadParticipantsFunction: null,
        participantsModalAddParticipantFunction: null,
        participantsModalRemoveParticipantFunction: null,
        participantsModalFunctionsArg: null,
        checkKeysModalOpen: false,
        checkKeysEmail: '',
        checkKeysEncryptionPublicKey: '',
        checkKeysSigningPublicKey: ''
    };

    secretModalTxtName = React.createRef();
    secretModalTxtKey = [];
    secretModalTxtValue = [];
    participantsModalForm = React.createRef();
    participantsModalTxtEmail = React.createRef();

    constructor(props) {

        super(props);
        registerView('app', this);

    }

    componentDidMount() {

        listenLocationChange();

    }

    render() {

        return (
            <div>

                {/* Views */}
                <Route exact path={views.viewPaths.login} component={Login} />
                <Route exact path={views.viewPaths.registration} component={Registration} />
                <Route exact path={views.navbarPaths} component={NavigationBar} />
                <CacheSwitch>
                    <CacheRoute exact path={views.viewPaths.mySecrets} component={MySecrets} />
                    <CacheRoute exact path={views.viewPaths.secretsSharedWithMe} component={SecretsSharedWithMe} />
                    <CacheRoute exact path={views.viewPaths.groups} component={Groups} />
                    <CacheRoute exact path={views.viewPaths.myAccount} component={MyAccount} />
                </CacheSwitch>

                {/* Loading spinner */}
                <Loader loading={this.state.loading} text={t('global.loading-spinner-text')} fullPage />

                {/* Message and confirmation modals */}
                <Modal isOpen={this.state.messageModalActive} toggle={closeMessageModal}>
                    <ModalHeader>{this.state.messageModalHeader}</ModalHeader>
                    <ModalBody>{this.state.messageModalBody}</ModalBody>
                </Modal>

                <Modal isOpen={this.state.confirmationModalActive} toggle={() => { if (!this.state.confirmationModalLock) closeConfirmationModal('no') }}>
                    <ModalHeader>{this.state.confirmationModalHeader}</ModalHeader>
                    <ModalBody>{this.state.confirmationModalBody}</ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={() => { closeConfirmationModal('yes') }}>{t('global.yes')}</Button>
                        <Button color="secondary" onClick={() => { closeConfirmationModal('no') }}>{t('global.no')}</Button>
                    </ModalFooter>
                </Modal>

                {/* Secret modal */}
                <Modal isOpen={this.state.secretModalActive} toggle={() => { closeSecretModal() }}>
                    <ModalHeader>{this.state.secretModalHeader}</ModalHeader>
                    <ModalBody>
                        <Form id="app_form-secret-modal" onSubmit={(e) => {
                            e.preventDefault();
                            this.state.secretModalAcceptCallback(buildSecretModalSecret(), this.state.secretModalOriginalSecret);
                        }}>
                            <FormGroup style={{ marginTop: '8px' }}>
                                <Input
                                    innerRef={this.secretModalTxtName}
                                    type="text"
                                    placeholder={t('secrets.secret-name')}
                                    maxLength={properties.secrets.secretNameMaxLength}
                                    required
                                    onChange={(e) => { this.setState({ secretModalSecretName: e.target.value }) }}
                                />
                            </FormGroup>
                            {this.state.secretModalSecretKeyValuePairs.map((keyValuePair, k) =>
                                <FormGroup key={'key-value-pair-' + k}>
                                    <hr style={{ margin: '1.5rem -1rem' }} />
                                    <Input
                                        innerRef={this.secretModalTxtKey[k]}
                                        type="text"
                                        placeholder={t('global.key') + ' ' + (k + 1)}
                                        maxLength={properties.secrets.secretKeyMaxLength}
                                        required
                                        onChange={(e) => {
                                            setStateArrayElement(this, 'secretModalSecretKeyValuePairs', k, {
                                                key: e.target.value,
                                                value: this.state.secretModalSecretKeyValuePairs[k].value,
                                                valueLength: this.state.secretModalSecretKeyValuePairs[k].valueLength,
                                                valueStrength: this.state.secretModalSecretKeyValuePairs[k].valueStrength
                                            });
                                        }}
                                    />
                                    <Input
                                        innerRef={this.secretModalTxtValue[k]}
                                        type="textarea"
                                        placeholder={t('global.value') + ' ' + (k + 1)}
                                        maxLength={properties.secrets.secretValueMaxLength}
                                        required
                                        onChange={(e) => {
                                            setStateArrayElement(this, 'secretModalSecretKeyValuePairs', k, {
                                                key: this.state.secretModalSecretKeyValuePairs[k].key,
                                                value: e.target.value,
                                                valueLength: e.target.value.length,
                                                valueStrength: secretStrength(e.target.value)
                                            });
                                        }}
                                    />
                                    <InputGroup style={{ margin: '.4rem 0' }}>
                                        <Badge color="primary" className="badge-progress" style={{ width: '30%' }}>
                                            {t('global.length') + ' ' + this.state.secretModalSecretKeyValuePairs[k].valueLength + ' / ' + properties.secrets.secretValueMaxLength}
                                        </Badge>
                                        <div style={{ width: '1%' }}></div>
                                        <Progress color="primary" value={this.state.secretModalSecretKeyValuePairs[k].valueStrength} style={{ width: '69%' }}>
                                            {t('global.strength') + ' ' + this.state.secretModalSecretKeyValuePairs[k].valueStrength + '%'}
                                        </Progress>
                                    </InputGroup>
                                    <fieldset disabled={Boolean(this.state.secretModalSecretKeyValuePairs[k].value)}>
                                        <InputGroup className="group-spaced">
                                            <Label size="sm">{t('secrets.generate-random-value')}</Label>
                                            <Input
                                                form={'app_form-secret-modal-generate-random-value' + k}
                                                type="number"
                                                placeholder={t('global.length')}
                                                min={1}
                                                max={properties.secrets.secretValueMaxLength}
                                                pattern="[0-9]*"
                                                required
                                                bsSize="sm"
                                                style={{ flexGrow: '100' }}
                                                onChange={(e) => { this.setState({ secretModalGenerateRandomValueLength: e.target.value }); }} />
                                            <Button
                                                type="submit"
                                                form={'app_form-secret-modal-generate-random-value' + k}
                                                color="secondary"
                                                size="sm">
                                                {t('global.generate')}
                                            </Button>
                                        </InputGroup>
                                    </fieldset>
                                    <Button
                                        onClick={() => { secretModalRemoveKeyValuePair(k) }}
                                        color="secondary"
                                        size="sm" style={{ marginTop: '6px' }}
                                        disabled={this.state.secretModalSecretKeyValuePairs.length == 1}>
                                        {t('secrets.remove-key-value-pair')}
                                    </Button>
                                </FormGroup>
                            )}
                        </Form>
                        {this.state.secretModalSecretKeyValuePairs.map((keyValuePair, k) =>
                            <Form
                                key={'key-value-pair-' + k}
                                id={'app_form-secret-modal-generate-random-value' + k}
                                onSubmit={(e) => { e.preventDefault(); secretModalGenerateRandomValue(k); }}>
                            </Form>
                        )}
                    </ModalBody>
                    <ModalFooter>
                        <fieldset className="w-100 group-spaced">
                            <Button
                                onClick={() => { secretModalAddKeyValuePair() }}
                                color="primary"
                                disabled={this.state.secretModalSecretKeyValuePairs.length >= properties.secrets.secretMaxKeyValuePairs}>
                                {t('secrets.add-key-value-pair')}
                            </Button>
                            <Button onClick={() => { closeSecretModal() }} color="secondary" className="float-right">{t('global.cancel')}</Button>
                            <Button type="submit" form="app_form-secret-modal" color="primary" className="float-right">{t('global.accept')}</Button>
                        </fieldset>
                    </ModalFooter>
                </Modal>

                {/* Participants modal */}
                <Modal isOpen={this.state.participantsModalActive} toggle={() => { closeParticipantsModal() }}>
                    <ModalHeader>{this.state.participantsModalHeader}</ModalHeader>
                    <ModalBody>
                        {
                            !this.state.participantsModalAccounts || this.state.participantsModalAccounts.length == 0 ? null :
                                <Table striped hover size="sm">
                                    <tbody>
                                        {this.state.participantsModalAccounts.map((account, a) =>
                                            <tr key={'account-' + a}>
                                                <td style={{ width: '100%' }}>{account.email}</td>
                                                <td style={{ width: '4rem' }} align="center">
                                                    <ActionIcon icon={Key} tooltipText={t('accounts.check-keys')}
                                                        onClick={() => { checkKeysModal(account.email) }} />
                                                    <span className="space-between-icons"></span>
                                                    <ActionIcon icon={X} tooltipText={t('global.remove')}
                                                        onClick={() => { participantsModalRemoveParticipant({ 'email': account.email }) }} />
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </Table>
                        }
                        <Form innerRef={this.participantsModalForm} inline className="group-spaced" onSubmit={(e) => {
                            e.preventDefault();
                            participantsModalAddParticipant({ 'email': this.state.participantsModalEmail })
                        }}>
                            <Input
                                innerRef={this.participantsModalTxtEmail}
                                type="email"
                                style={{ flexGrow: '100' }}
                                placeholder={t('global.email')}
                                pattern={properties.general.emailPattern}
                                maxLength={properties.general.emailMaxLength}
                                required
                                onChange={(e) => { this.setState({ participantsModalEmail: e.target.value }); }}
                            />
                            <ButtonIcon icon={Key} tooltipText={t('accounts.check-keys')} color="secondary"
                                onClick={() => { if (this.participantsModalForm.current.reportValidity()) checkKeysModal(this.state.participantsModalEmail) }} />
                            <Button type="submit" color="primary">{t('global.share')}</Button>
                        </Form>
                    </ModalBody>
                </Modal>

                {/* Check keys modal */}
                <Modal isOpen={this.state.checkKeysModalOpen} toggle={closeCheckKeysModal}>
                    <ModalBody>
                        <h6>{t('global.email')}</h6>
                        <InputGroup>
                            <span className="space-between-text-and-icons"></span>
                            <ActionIcon icon={File} tooltipText={t('global.copy')} style={{ marginTop: '6px' }}
                                onClick={() => { copyToClipboard(this.state.checkKeysEmail) }} />
                            <span className="space-between-text-and-icons"></span>
                            <Input
                                type="text"
                                readOnly
                                value={this.state.checkKeysEmail}
                                onFocus={(e) => { e.target.select(); }} />
                        </InputGroup>
                        <h6 style={{ marginTop: '15px' }}>{t('accounts.encryption-public-key')}</h6>
                        <InputGroup>
                            <span className="space-between-text-and-icons"></span>
                            <ActionIcon icon={File} tooltipText={t('global.copy')} style={{ marginTop: '6px' }}
                                onClick={() => { copyToClipboard(this.state.checkKeysEncryptionPublicKey) }} />
                            <span className="space-between-text-and-icons"></span>
                            <Input
                                type="text"
                                readOnly
                                value={this.state.checkKeysEncryptionPublicKey}
                                onFocus={(e) => { e.target.select(); }}
                            />
                        </InputGroup>
                    </ModalBody>
                </Modal>

            </div>
        );

    }

}

export default withTranslation()(withRouter(App));
