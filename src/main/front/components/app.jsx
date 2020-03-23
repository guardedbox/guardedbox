import React, { Component } from 'react';
import { withTranslation } from 'react-i18next';
import { withRouter, Route } from 'react-router-dom';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { Loader } from 'react-overlay-loader';
import reactOverlayLoaderCss from 'react-overlay-loader/styles.css';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, InputGroup, Input, UncontrolledTooltip } from 'reactstrap';
import Octicon, { File } from '@primer/octicons-react'
import Login from 'components/login.jsx';
import Registration from 'components/registration.jsx';
import NavigationBar from 'components/navigation-bar.jsx';
import MySecrets from 'components/my-secrets.jsx';
import SecretsSharedWithMe from 'components/secrets-shared-with-me.jsx';
import Groups from 'components/groups.jsx';
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { listenLocationChange } from 'services/location.jsx';
import { closeModalMessage, closeModalConfirmation } from 'services/modal.jsx';
import { closeCheckKeysModal } from 'services/check-keys.jsx';
import { copyToClipboard } from 'services/selector.jsx';
import views from 'constants/views.json';

class App extends Component {

    state = {
        loading: false,
        modalMessageActive: false,
        modalMessageHeader: '',
        modalMessageBody: '',
        modalMessageExitCallback: null,
        modalConfirmationActive: false,
        modalConfirmationHeader: '',
        modalConfirmationBody: '',
        modalConfirmationYesCallback: null,
        modalConfirmationNoCallback: null,
        checkKeysModalOpen: false,
        checkKeysEmail: '',
        checkKeysEncryptionPublicKey: '',
        checkKeysSigningPublicKey: ''
    };

    constructor(props) {

        super(props);
        registerViewComponent('app', this);

    }

    componentDidMount() {

        listenLocationChange();

    }

    render() {

        return (
            <div>

                <Route exact path={views.viewPaths.login} component={Login} />
                <Route exact path={views.viewPaths.registration} component={Registration} />
                <Route exact path={views.navbarPaths} component={NavigationBar} />
                <CacheSwitch>
                    <CacheRoute exact path={views.viewPaths.mySecrets} component={MySecrets} />
                    <CacheRoute exact path={views.viewPaths.secretsSharedWithMe} component={SecretsSharedWithMe} />
                    <CacheRoute exact path={views.viewPaths.groups} component={Groups} />
                </CacheSwitch>

                <Loader loading={this.state.loading} text={t('global.loading-spinner-text')} fullPage />

                <Modal isOpen={this.state.modalMessageActive} toggle={closeModalMessage}>
                    <ModalHeader>{this.state.modalMessageHeader}</ModalHeader>
                    <ModalBody>{this.state.modalMessageBody}</ModalBody>
                </Modal>

                <Modal isOpen={this.state.modalConfirmationActive} toggle={() => { closeModalConfirmation('no') }}>
                    <ModalHeader>{this.state.modalConfirmationHeader}</ModalHeader>
                    <ModalBody>{this.state.modalConfirmationBody}</ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={() => { closeModalConfirmation('yes') }}>{t('global.yes')}</Button>
                        <Button color="secondary" onClick={() => { closeModalConfirmation('no') }}>{t('global.no')}</Button>
                    </ModalFooter>
                </Modal>

                <Modal isOpen={this.state.checkKeysModalOpen} toggle={closeCheckKeysModal}>
                    <ModalBody>
                        <h6>{t('global.email')}</h6>
                        <InputGroup>
                            <span className="space-between-text-and-icons"></span>
                            <span
                                id="app-copy-email"
                                onClick={() => { copyToClipboard(this.state.checkKeysEmail) }}
                                style={{ cursor: 'pointer', marginTop: '6px' }}>
                                <Octicon icon={File} />
                            </span>
                            <UncontrolledTooltip placement="top" target="app-copy-email">
                                {t('global.copy')}
                            </UncontrolledTooltip>
                            <span className="space-between-text-and-icons"></span>
                            <Input
                                type="text"
                                readOnly
                                value={this.state.checkKeysEmail}
                                onFocus={(e) => { e.target.select(); }}
                            />
                        </InputGroup>
                        <h6 style={{ marginTop: '15px' }}>{t('accounts.encryption-public-key')}</h6>
                        <InputGroup>
                            <span className="space-between-text-and-icons"></span>
                            <span
                                id="app-copy-encryption-public-key"
                                onClick={() => { copyToClipboard(this.state.checkKeysEncryptionPublicKey) }}
                                style={{ cursor: 'pointer', marginTop: '6px' }}>
                                <Octicon icon={File} />
                            </span>
                            <UncontrolledTooltip placement="top" target="app-copy-encryption-public-key">
                                {t('global.copy')}
                            </UncontrolledTooltip>
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
            </div >
        );

    }

}

export default withTranslation()(withRouter(App));
