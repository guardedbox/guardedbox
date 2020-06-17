import React, { Component, Fragment } from 'react';
import { withTranslation } from 'react-i18next';
import { withRouter, Route } from 'react-router-dom';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { Loader } from 'react-overlay-loader';
import reactOverlayLoaderCss from 'react-overlay-loader/styles.css';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Badge, Progress, Form, FormGroup, Label, Input, InputGroup, CustomInput, Table } from 'reactstrap';
import { Info, File, Key, X, Sync, LineArrowUp } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import PopoverIcon from 'components/popover-icon.jsx';
import ButtonIcon from 'components/button-icon.jsx';
import Login from 'views/login.jsx';
import Registration from 'views/registration.jsx';
import NavigationBar from 'views/navigation-bar.jsx';
import MySecrets from 'views/my-secrets.jsx';
import SecretsSharedWithMe from 'views/secrets-shared-with-me.jsx';
import MyGroups from 'views/my-groups.jsx';
import GroupsIWasAddedTo from 'views/groups-i-was-added-to.jsx';
import MyAccount from 'views/my-account.jsx';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { listenLocationChange } from 'services/location.jsx';
import { setStateArrayElement } from 'services/state-utils.jsx';
import { closeMessageModal, closeConfirmationModal } from 'services/modal.jsx';
import { secretStrength, secretModalAddKeyValuePair, secretModalRemoveKeyValuePair, secretModalGenerateRandomValue, buildSecretModalSecret, closeSecretModal } from 'services/secret-utils.jsx';
import { participantsModalAddParticipant, participantsModalRemoveParticipant, closeParticipantsModal, inviteEmail } from 'services/participant-utils.jsx';
import { buildGroupModalSecret, closeGroupModal } from 'services/group-utils.jsx';
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
        secretModalAcceptCallbackThirdArg: null,

        participantsModalActive: false,
        participantsModalLiterals: '',
        participantsModalAccounts: [],
        participantsModalRegistrationPendingAccounts: [],
        participantsModalExMembers: [],
        participantsModalEmail: '',
        participantsModalLoadParticipantsFunction: null,
        participantsModalAddParticipantFunction: null,
        participantsModalRemoveParticipantFunction: null,
        participantsModalFunctionsArg: null,

        groupModalActive: false,
        groupModalHeader: '',
        groupModalGroupName: '',
        groupModalParticipantsVisible: false,
        groupModalOriginalGroup: null,
        groupModalAcceptCallback: null,

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
    groupModalTxtName = React.createRef();
    groupModalSwitchParticipantsVisible = React.createRef();

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
                    <CacheRoute exact path={views.viewPaths.myGroups} component={MyGroups} />
                    <CacheRoute exact path={views.viewPaths.groupsIWasAddedTo} component={GroupsIWasAddedTo} />
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
                <Modal isOpen={this.state.secretModalActive}>
                    <ModalHeader>{this.state.secretModalHeader}</ModalHeader>
                    <ModalBody>
                        <Form id="app_form-secret-modal" onSubmit={(e) => {
                            e.preventDefault();
                            this.state.secretModalAcceptCallback(
                                buildSecretModalSecret(),
                                this.state.secretModalOriginalSecret,
                                this.state.secretModalAcceptCallbackThirdArg);
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
                                    <InputGroup>
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
                                        {k > 0 ? null :
                                            <PopoverIcon icon={Info} className="text-info" style={{ margin: '6px' }}
                                                popoverText={t('secrets.key-value-pair-info')} />
                                        }
                                    </InputGroup>
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
                    <ModalHeader>{this.state.participantsModalLiterals.header}</ModalHeader>
                    <ModalBody>
                        {
                            <Fragment>
                                <h6 className="text-success"><b>{this.state.participantsModalLiterals.accountsHeader}</b></h6>
                                {!this.state.participantsModalAccounts || this.state.participantsModalAccounts.length == 0 ?
                                    <div style={{ marginBottom: '1.65rem' }}>{this.state.participantsModalLiterals.noParticipants}</div> :
                                    <Table striped hover size="sm" style={{ marginBottom: '1.65rem' }}>
                                        <tbody>
                                            {this.state.participantsModalAccounts.map((account, a) =>
                                                <tr key={'account-' + a}>
                                                    <td style={{ width: '100%' }}>{account.email}</td>
                                                    <td className="icons-3-col" align="center">
                                                        <ActionIcon icon={Key} tooltipText={t('accounts.check-keys')}
                                                            onClick={() => { checkKeysModal(account.email) }} />
                                                        {this.state.participantsModalRemoveParticipantFunction ?
                                                            <ActionIcon icon={X} tooltipText={t('global.remove')}
                                                                onClick={() => {
                                                                    participantsModalRemoveParticipant({
                                                                        'email': account.email,
                                                                        'pendingRegistration': false
                                                                    })
                                                                }} />
                                                            : null
                                                        }
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </Table>
                                }
                            </Fragment>
                        }
                        {
                            !this.state.participantsModalRegistrationPendingAccounts || this.state.participantsModalRegistrationPendingAccounts.length == 0 ? null :
                                <Fragment>
                                    <h6 className="text-success"><b>{this.state.participantsModalLiterals.registrationPendingAccountsHeader}</b></h6>
                                    <Table striped hover size="sm" style={{ marginBottom: '1.65rem' }}>
                                        <tbody>
                                            {this.state.participantsModalRegistrationPendingAccounts.map((registrationPendingAccount, a) =>
                                                <tr key={'account-' + a}>
                                                    <td style={{ width: '100%' }}>
                                                        {registrationPendingAccount.receiverEmail}
                                                        {!registrationPendingAccount.emailRegistered ? null :
                                                            <Badge color='info' style={{ marginLeft: '10px' }}>{t('accounts.registered')}</Badge>
                                                        }
                                                    </td>
                                                    <td className="icons-3-col" align="center">
                                                        {registrationPendingAccount.emailRegistered ?
                                                            <Fragment>
                                                                <ActionIcon icon={LineArrowUp} className="text-info" tooltipText={t('global.add')}
                                                                    onClick={() => {
                                                                        participantsModalAddParticipant({
                                                                            'email': registrationPendingAccount.receiverEmail
                                                                        })
                                                                    }} />
                                                                <ActionIcon icon={Key} tooltipText={t('accounts.check-keys')}
                                                                    onClick={() => { checkKeysModal(registrationPendingAccount.receiverEmail) }} />
                                                            </Fragment>
                                                            :
                                                            <Fragment>
                                                                <ActionIcon icon={Sync} tooltipText={t('accounts.resend-invitation')}
                                                                    onClick={() => {
                                                                        inviteEmail(
                                                                            registrationPendingAccount.receiverEmail,
                                                                            this.state.participantsModalFunctionsArg.secretId,
                                                                            this.state.participantsModalFunctionsArg.groupId,
                                                                            true)
                                                                    }} />
                                                            </Fragment>
                                                        }
                                                        <ActionIcon icon={X} tooltipText={t('global.remove')}
                                                            onClick={() => {
                                                                participantsModalRemoveParticipant({
                                                                    'email': registrationPendingAccount.receiverEmail,
                                                                    'pendingRegistration': true
                                                                })
                                                            }} />
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </Table>
                                </Fragment>
                        }
                        {
                            !this.state.participantsModalExMembers || this.state.participantsModalExMembers.length == 0 ? null :
                                <Fragment>
                                    <h6 className="text-success">
                                        <b>{this.state.participantsModalLiterals.exMembersHeader}</b>
                                        <ActionIcon icon={Info} tooltipText={this.state.participantsModalLiterals.exMembersHeaderInfo} />
                                    </h6>
                                    <Table striped hover size="sm" style={{ marginBottom: '1.65rem' }}>
                                        <tbody>
                                            {this.state.participantsModalExMembers.map((exMember, a) =>
                                                <tr key={'member-' + a}>
                                                    <td style={{ width: '100%' }}>
                                                        {exMember.email}
                                                        <ActionIcon icon={Info} tooltipText={t(exMember.cause)} className={(() => {
                                                            switch (exMember.cause) {
                                                                case 'shared-secrets.secret-unshared-by-owner':
                                                                case 'groups.participant-removed-by-owner':
                                                                    return "text-success";
                                                                case 'shared-secrets.secret-rejected-by-receiver':
                                                                case 'groups.participant-left':
                                                                    return 'text-warning';
                                                                case 'accounts.account-was-deleted':
                                                                    return 'text-danger';
                                                            }
                                                        })()} />
                                                    </td>
                                                    <td className="icons-3-col" align="center">
                                                        <ActionIcon icon={LineArrowUp} tooltipText={t('global.add')}
                                                            onClick={() => {
                                                                participantsModalAddParticipant({
                                                                    'email': exMember.email
                                                                })
                                                            }} />
                                                        <ActionIcon icon={X} tooltipText={t('global.forget')}
                                                            onClick={() => {
                                                                participantsModalRemoveParticipant({
                                                                    'email': exMember.email,
                                                                    'exMember': true
                                                                })
                                                            }} />
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </Table>
                                </Fragment>
                        }
                        {
                            this.state.participantsModalAddParticipantFunction ?
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
                                    <Button type="submit" color="primary">{t('global.add')}</Button>
                                </Form>
                                : null
                        }
                    </ModalBody>
                </Modal>

                {/* Group modal */}
                <Modal isOpen={this.state.groupModalActive}>
                    <ModalHeader>{this.state.groupModalHeader}</ModalHeader>
                    <ModalBody>
                        <Form id="app_form-group-modal" onSubmit={(e) => {
                            e.preventDefault();
                            this.state.groupModalAcceptCallback(buildGroupModalSecret(), this.state.groupModalOriginalGroup);
                        }}>
                            <FormGroup>
                                <Input
                                    innerRef={this.groupModalTxtName}
                                    type="text"
                                    placeholder={t('global.name')}
                                    maxLength={properties.groups.groupNameMaxLength}
                                    required
                                    onChange={(e) => { this.setState({ groupModalGroupName: e.target.value }) }}
                                />
                            </FormGroup>
                            <FormGroup>
                                <CustomInput
                                    id="app_switch-group-modal-participants-visible"
                                    innerRef={this.groupModalSwitchParticipantsVisible}
                                    type="switch"
                                    label={t('groups.participants-can-see-each-other')}
                                    onChange={(e) => { this.setState({ groupModalParticipantsVisible: e.target.checked }) }}
                                />
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button type="submit" form="app_form-group-modal" color="primary">{t('global.accept')}</Button>
                        <Button color="secondary" onClick={() => { closeGroupModal() }}>{t('global.cancel')}</Button>
                    </ModalFooter>
                </Modal>

                {/* Check keys modal */}
                <Modal isOpen={this.state.checkKeysModalOpen} toggle={closeCheckKeysModal}>
                    <ModalBody>
                        <h6>{t('global.email')}</h6>
                        <InputGroup>
                            <ActionIcon icon={File} tooltipText={t('global.copy')} style={{ marginTop: '6px', width: '40px' }}
                                onClick={() => { copyToClipboard(this.state.checkKeysEmail) }} />
                            <Input
                                type="text"
                                readOnly
                                value={this.state.checkKeysEmail}
                                onFocus={(e) => { e.target.select(); }} />
                        </InputGroup>
                        <h6 style={{ marginTop: '15px' }}>{t('accounts.encryption-public-key')}</h6>
                        <InputGroup>
                            <ActionIcon icon={File} tooltipText={t('global.copy')} style={{ marginTop: '6px', width: '40px' }}
                                onClick={() => { copyToClipboard(this.state.checkKeysEncryptionPublicKey) }} />
                            <Input
                                type="text"
                                readOnly
                                value={this.state.checkKeysEncryptionPublicKey}
                                onFocus={(e) => { e.target.select(); }}
                            />
                        </InputGroup>
                        <div style={{ marginTop: '20px' }}>
                            {t('accounts.check-keys-info', { 'email': this.state.checkKeysEmail, 'view': t('my-account.title') })}
                        </div>
                    </ModalBody>
                </Modal>

            </div >
        );

    }

}

export default withTranslation()(withRouter(App));
