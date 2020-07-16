import React, { Component, Fragment } from 'react';
import { withTranslation } from 'react-i18next';
import { withRouter, Route } from 'react-router-dom';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { Loader } from 'react-overlay-loader';
import reactOverlayLoaderCss from 'react-overlay-loader/styles.css';
import { Modal, ModalHeader, ModalBody, ModalFooter, Badge, Progress, Form, FormGroup, Label, Input, InputGroup, CustomInput, Table, Collapse, InputGroupButtonDropdown, DropdownToggle, DropdownMenu, UncontrolledTooltip } from 'reactstrap';
import { Info, File, Key, Check, X, Sync, ChevronUp, ChevronDown, LineArrowUp, Trashcan, Zap, Plus } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import InfoIcon from 'components/info-icon.jsx';
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
import { secretStrength, secretModalAddKeyValuePair, secretModalRemoveKeyValuePair, secretModalMoveKeyValuePairUp, secretModalMoveKeyValuePairDown, buildSecretModalSecret, secretModalGenerateRandomValue, isRandomSecretCharsetActive, toggleRandomSecretCharset, isOnlyRandomSecretCharsetActive, closeSecretModal } from 'services/secret-utils.jsx';
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
        secretModalShowPasswordOptions: [],
        secretModalGenerateRandomValueLength: [],
        secretModalGenerateRandomValueDropdownOpen: [],
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
    secretModalSwitchShowPasswordOptions = [];
    secretModalTxtGenerateRandomValueLength = [];
    participantsModalForm = React.createRef();
    participantsModalTxtEmail = React.createRef();
    groupModalTxtName = React.createRef();
    groupModalSwitchParticipantsVisible = React.createRef();
    checkKeysModalTxtEncryptionPublicKey = React.createRef();

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
                        <ButtonIcon
                            icon={Check}
                            tooltipText={t('global.yes')}
                            color="primary"
                            onClick={() => { closeConfirmationModal('yes'); }} />
                        <ButtonIcon
                            icon={X}
                            tooltipText={t('global.no')}
                            color="secondary"
                            onClick={() => { closeConfirmationModal('no'); }} />
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
                                            <InfoIcon icon={Info} className="text-info" style={{ margin: '6px' }} tooltipPlacement="right"
                                                tooltipText={t('secrets.key-value-pair-info')} />
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
                                    <InputGroup style={{ margin: '8px 0 0 0' }}>
                                        <Badge style={{ flex: '1', margin: '0 8px 5px 0', fontWeight: '400' }} color="primary">
                                            {t('global.length') + ' ' + this.state.secretModalSecretKeyValuePairs[k].valueLength + ' / ' + properties.secrets.secretValueMaxLength}
                                        </Badge>
                                        <div style={{ fontSize: '96.75%', maxHeight: '19px', margin: '-3px 8px 5px 0' }}>
                                            <CustomInput
                                                id={"app_switch-secret-modal-show-password-options-" + k}
                                                innerRef={this.secretModalSwitchShowPasswordOptions[k]}
                                                type="switch"
                                                label={t('secrets.show-password-options')}
                                                onChange={(e) => {
                                                    setStateArrayElement(this, 'secretModalShowPasswordOptions', k, !this.state.secretModalShowPasswordOptions[k]);
                                                }} />
                                        </div>
                                    </InputGroup>
                                    <Collapse isOpen={this.state.secretModalShowPasswordOptions[k]}>
                                        <Progress multi>
                                            <Progress bar
                                                color="primary"
                                                value={this.state.secretModalSecretKeyValuePairs[k].valueStrength.strength}>
                                                {this.state.secretModalSecretKeyValuePairs[k].valueStrength.strength >= 67 ? t('global.strength') + ' ' : null}
                                                {this.state.secretModalSecretKeyValuePairs[k].valueStrength.strength + ' %'}
                                            </Progress>
                                            <Progress bar
                                                color={this.state.secretModalSecretKeyValuePairs[k].valueStrength.commonPassword ? 'warning' : 'light'}
                                                className={this.state.secretModalSecretKeyValuePairs[k].valueStrength.commonPassword ? 'text-white' : 'text-primary'}
                                                value={100 - this.state.secretModalSecretKeyValuePairs[k].valueStrength.strength}>
                                                {this.state.secretModalSecretKeyValuePairs[k].valueStrength.commonPassword ? t('secrets.common-password') :
                                                    (this.state.secretModalSecretKeyValuePairs[k].valueStrength.strength < 67 ? t('global.strength') : null)}
                                            </Progress>
                                        </Progress>
                                        <fieldset disabled={Boolean(this.state.secretModalSecretKeyValuePairs[k].value)} style={{ margin: '4px 0' }}>
                                            <InputGroup className="group-spaced">
                                                <Label size="sm">{t('secrets.generate-random-value')}</Label>
                                                <Input
                                                    innerRef={this.secretModalTxtGenerateRandomValueLength[k]}
                                                    form={'app_form-secret-modal-generate-random-value' + k}
                                                    type="number"
                                                    placeholder={t('global.length')}
                                                    min={1}
                                                    max={properties.secrets.secretValueMaxLength}
                                                    pattern="[0-9]*"
                                                    required
                                                    bsSize="sm"
                                                    style={{ flexGrow: '100' }}
                                                    onChange={(e) => {
                                                        setStateArrayElement(this, 'secretModalGenerateRandomValueLength', k, e.target.value);
                                                    }} />
                                                <InputGroupButtonDropdown
                                                    direction="up"
                                                    addonType="append"
                                                    isOpen={this.state.secretModalGenerateRandomValueDropdownOpen[k]}
                                                    toggle={() => {
                                                        setStateArrayElement(this, 'secretModalGenerateRandomValueDropdownOpen', k, !this.state.secretModalGenerateRandomValueDropdownOpen[k]);
                                                    }}>
                                                    <ButtonIcon
                                                        icon={Zap}
                                                        tooltipText={t('global.generate')}
                                                        color="primary"
                                                        size="sm"
                                                        type="submit"
                                                        form={'app_form-secret-modal-generate-random-value' + k} />
                                                    <DropdownToggle split color="primary" size="sm" />
                                                    <DropdownMenu>
                                                        <div id={"app_secret-modal-charset-" + k + '-' + 0} className="dropdown-item no-hover" style={{ padding: '0.1rem 0.75rem' }}>
                                                            <CustomInput
                                                                id={"app_secret-modal-charset-switch-" + k + '-' + 0}
                                                                type="switch"
                                                                style={{ paddingLeft: '0' }}
                                                                label={t('secrets.charsets.' + properties.secrets.randomSecretCharsets[0].name)}
                                                                defaultChecked={isRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[0].name)}
                                                                onChange={(e) => {
                                                                    if (isOnlyRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[0].name)) {
                                                                        e.target.checked = true;
                                                                    } else {
                                                                        toggleRandomSecretCharset(properties.secrets.randomSecretCharsets[0].name);
                                                                    }
                                                                }} />
                                                            <UncontrolledTooltip target={"app_secret-modal-charset-" + k + '-' + 0} placement="right">
                                                                {properties.secrets.randomSecretCharsets[0].charset}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                        <div id={"app_secret-modal-charset-" + k + '-' + 1} className="dropdown-item no-hover" style={{ padding: '0.1rem 0.75rem' }}>
                                                            <CustomInput
                                                                id={"app_secret-modal-charset-switch-" + k + '-' + 1}
                                                                type="switch"
                                                                defaultChecked={isRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[1].name)}
                                                                label={t('secrets.charsets.' + properties.secrets.randomSecretCharsets[1].name)}
                                                                onChange={(e) => {
                                                                    if (isOnlyRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[1].name)) {
                                                                        e.target.checked = true;
                                                                    } else {
                                                                        toggleRandomSecretCharset(properties.secrets.randomSecretCharsets[1].name);
                                                                    }
                                                                }} />
                                                            <UncontrolledTooltip target={"app_secret-modal-charset-" + k + '-' + 1} placement="right">
                                                                {properties.secrets.randomSecretCharsets[1].charset}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                        <div id={"app_secret-modal-charset-" + k + '-' + 2} className="dropdown-item no-hover" style={{ padding: '0.1rem 0.75rem' }}>
                                                            <CustomInput
                                                                id={"app_secret-modal-charset-switch-" + k + '-' + 2}
                                                                type="switch"
                                                                defaultChecked={isRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[2].name)}
                                                                label={t('secrets.charsets.' + properties.secrets.randomSecretCharsets[2].name)}
                                                                onChange={(e) => {
                                                                    if (isOnlyRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[2].name)) {
                                                                        e.target.checked = true;
                                                                    } else {
                                                                        toggleRandomSecretCharset(properties.secrets.randomSecretCharsets[2].name);
                                                                    }
                                                                }} />
                                                            <UncontrolledTooltip target={"app_secret-modal-charset-" + k + '-' + 2} placement="right">
                                                                {properties.secrets.randomSecretCharsets[2].charset}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                        <div id={"app_secret-modal-charset-" + k + '-' + 3} className="dropdown-item no-hover" style={{ padding: '0.1rem 0.75rem' }}>
                                                            <CustomInput
                                                                id={"app_secret-modal-charset-switch-" + k + '-' + 3}
                                                                type="switch"
                                                                defaultChecked={isRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[3].name)}
                                                                label={t('secrets.charsets.' + properties.secrets.randomSecretCharsets[3].name)}
                                                                onChange={(e) => {
                                                                    if (isOnlyRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[3].name)) {
                                                                        e.target.checked = true;
                                                                    } else {
                                                                        toggleRandomSecretCharset(properties.secrets.randomSecretCharsets[3].name);
                                                                    }
                                                                }} />
                                                            <UncontrolledTooltip target={"app_secret-modal-charset-" + k + '-' + 3} placement="right">
                                                                {properties.secrets.randomSecretCharsets[3].charset}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                        <div id={"app_secret-modal-charset-" + k + '-' + 4} className="dropdown-item no-hover" style={{ padding: '0.1rem 0.75rem' }}>
                                                            <CustomInput
                                                                id={"app_secret-modal-charset-switch-" + k + '-' + 4}
                                                                type="switch"
                                                                defaultChecked={isRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[4].name)}
                                                                label={t('secrets.charsets.' + properties.secrets.randomSecretCharsets[4].name)}
                                                                onChange={(e) => {
                                                                    if (isOnlyRandomSecretCharsetActive(properties.secrets.randomSecretCharsets[4].name)) {
                                                                        e.target.checked = true;
                                                                    } else {
                                                                        toggleRandomSecretCharset(properties.secrets.randomSecretCharsets[4].name);
                                                                    }
                                                                }} />
                                                            <UncontrolledTooltip target={"app_secret-modal-charset-" + k + '-' + 4} placement="right">
                                                                {properties.secrets.randomSecretCharsets[4].charset}
                                                            </UncontrolledTooltip>
                                                        </div>
                                                    </DropdownMenu>
                                                </InputGroupButtonDropdown>
                                            </InputGroup>
                                        </fieldset>
                                    </Collapse>
                                    <div className="group-spaced" style={{ marginTop: '6px' }}>
                                        <ButtonIcon
                                            icon={ChevronUp}
                                            tooltipText={t('global.go-up')}
                                            color="success"
                                            size="sm"
                                            disabled={k == 0}
                                            type="button"
                                            onClick={() => { secretModalMoveKeyValuePairUp(k); }} />
                                        <ButtonIcon
                                            icon={ChevronDown}
                                            tooltipText={t('global.go-down')}
                                            color="success"
                                            size="sm"
                                            disabled={k == this.state.secretModalSecretKeyValuePairs.length - 1}
                                            type="button"
                                            onClick={() => { secretModalMoveKeyValuePairDown(k); }} />
                                        <ButtonIcon
                                            icon={Trashcan}
                                            tooltipText={t('secrets.remove-key-value-pair')}
                                            color="secondary"
                                            size="sm"
                                            disabled={this.state.secretModalSecretKeyValuePairs.length == 1}
                                            type="button"
                                            onClick={() => { secretModalRemoveKeyValuePair(k); }} />
                                    </div>
                                </FormGroup>
                            )}
                        </Form>
                        {
                            this.state.secretModalSecretKeyValuePairs.map((keyValuePair, k) =>
                                <Form
                                    key={'key-value-pair-' + k}
                                    id={'app_form-secret-modal-generate-random-value' + k}
                                    onSubmit={(e) => { e.preventDefault(); secretModalGenerateRandomValue(k); }}>
                                </Form>
                            )
                        }
                    </ModalBody>
                    <ModalFooter>
                        <fieldset className="w-100 group-spaced">
                            <ButtonIcon
                                icon={Plus}
                                tooltipText={t('secrets.add-key-value-pair')}
                                color="success"
                                disabled={this.state.secretModalSecretKeyValuePairs.length >= properties.secrets.secretMaxKeyValuePairs}
                                type="button"
                                onClick={() => { secretModalAddKeyValuePair() }} />
                            <ButtonIcon
                                icon={X}
                                tooltipText={t('global.cancel')}
                                color="secondary"
                                className="float-right"
                                type="button"
                                onClick={() => { closeSecretModal() }} />
                            <ButtonIcon
                                icon={Check}
                                tooltipText={t('global.accept')}
                                color="primary"
                                className="float-right"
                                type="submit"
                                form="app_form-secret-modal" />
                        </fieldset>
                    </ModalFooter>
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
                                    onChange={(e) => { this.setState({ groupModalParticipantsVisible: e.target.checked }) }} />
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <ButtonIcon
                            icon={Check}
                            tooltipText={t('global.accept')}
                            color="primary"
                            type="submit"
                            form="app_form-group-modal" />
                        <ButtonIcon
                            icon={X}
                            tooltipText={t('global.cancel')}
                            color="secondary"
                            type="button"
                            onClick={() => { closeGroupModal() }} />
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
                                                    <td style={{ width: '100%' }}>
                                                        <ActionIcon icon={File} tooltipText={t('global.copy')}
                                                            onClick={() => { copyToClipboard(account.email) }} />
                                                        {account.email}
                                                    </td>
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
                                                        <ActionIcon icon={File} tooltipText={t('global.copy')}
                                                            onClick={() => { copyToClipboard(registrationPendingAccount.receiverEmail) }} />
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
                                                        <ActionIcon icon={File} tooltipText={t('global.copy')}
                                                            onClick={() => { copyToClipboard(exMember.email) }} />
                                                        {exMember.email}
                                                        <InfoIcon icon={Info} tooltipText={t(exMember.cause)} className={(() => {
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
                                    <ButtonIcon
                                        icon={Key}
                                        tooltipText={t('accounts.check-keys')}
                                        color="secondary"
                                        type="button"
                                        onClick={() => {
                                            if (this.participantsModalForm.current.reportValidity()) checkKeysModal(this.state.participantsModalEmail);
                                        }} />
                                    <ButtonIcon
                                        icon={LineArrowUp}
                                        tooltipText={t('global.add')}
                                        color="primary"
                                        type="submit" />
                                </Form>
                                : null
                        }
                    </ModalBody>
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
                                innerRef={this.checkKeysModalTxtEncryptionPublicKey}
                                type="textarea"
                                readOnly
                                value={this.state.checkKeysEncryptionPublicKey}
                                style={{ resize: 'none' }}
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
