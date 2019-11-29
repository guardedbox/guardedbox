import React, { Component } from 'react';
import { withTranslation } from 'react-i18next';
import { withRouter, Route } from 'react-router-dom';
import CacheRoute, { CacheSwitch } from 'react-router-cache-route';
import { Loader } from 'react-overlay-loader';
import reactOverlayLoaderCss from 'react-overlay-loader/styles.css';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import Login from 'components/login.jsx';
import Registration from 'components/registration.jsx';
import NavigationBar from 'components/navigation-bar.jsx';
import MySecrets from 'components/my-secrets.jsx';
import SecretsSharedWithMe from 'components/secrets-shared-with-me.jsx';
import TrustedKeys from 'components/trusted-keys.jsx';
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { listenLocationChange } from 'services/location.jsx';
import { closeModalMessage, closeModalConfirmation } from 'services/modal.jsx';
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
        modalConfirmationNoCallback: null
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
                    <CacheRoute exact path={views.viewPaths.trustedKeys} component={TrustedKeys} />
                </CacheSwitch>

                <Loader loading={this.state.loading} text={t('app.loading-text')} fullPage />

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

            </div>
        );

    }

}

export default withTranslation()(withRouter(App));
