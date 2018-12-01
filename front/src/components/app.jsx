import React, { Component } from 'react';
import { withNamespaces } from 'react-i18next';
import { withRouter, Route, NavLink } from 'react-router-dom';
import { Loader } from 'react-overlay-loader';
import reactOverlayLoaderCss from 'react-overlay-loader/styles.css';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import ReCAPTCHA from 'react-google-recaptcha';
import Login from 'components/login.jsx';
import Registration from 'components/registration.jsx';
import NavigationBar from 'components/navigation-bar.jsx';
import MySecrets from 'components/my-secrets.jsx';
import SecretsSharedWithMe from 'components/secrets-shared-with-me.jsx';
import { get, post } from 'services/rest.jsx';
import { isKeyPairGenerated, deleteKeyPair } from 'services/encryption.jsx';
import { captchaOnChange, captchaOnErrored } from 'services/captcha.jsx';
import properties from 'constants/properties.json';
import apiPaths from 'constants/api-paths.json';
import componentsPaths from 'constants/components-paths.json';

class App extends Component {

    constructor(props) {

        // Props.
        super(props);

        // Global reference to this component.
        window.views.app = this;

        // Functions binding to this.
        this.handleLocationChange = this.handleLocationChange.bind(this);
        this.resetUserData = this.resetUserData.bind(this);
        this.disableMessageModal = this.disableMessageModal.bind(this);
        this.disableConfirmationModal = this.disableConfirmationModal.bind(this);

        // State.
        this.state = {
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
            mySecrets: null,
            secretsSharedWithMe: null,
        }

    }

    componentWillMount() {

        this.unsubscribeFromHistory = this.props.history.listen(this.handleLocationChange);
        this.handleLocationChange(this.props.history.location);

    }


    componentWillUnmount() {

        if (this.unsubscribeFromHistory) this.unsubscribeFromHistory();

    }

    handleLocationChange(location) {

        get({
            url: apiPaths.session.info,
            callback: (response) => {

                var prevEmail = window.session ? window.session.email : null;
                window.session = response;

                if (location.pathname === componentsPaths.registration) {
                    this.handleLocationChangeToComponent(window.views.registration);
                } else if (location.pathname !== componentsPaths.login) {

                    if (!response.authenticated || (prevEmail && prevEmail !== response.email) || !isKeyPairGenerated()) {
                        this.resetUserData(true, true);
                    } else if (location.pathname === '/') {
                        this.props.history.push(componentsPaths.defaultComponent);
                    } else if (location.pathname === componentsPaths.mySecrets) {
                        this.handleLocationChangeToComponent(window.views.mySecrets);
                    } else if (location.pathname === componentsPaths.secretsSharedWithMe) {
                        this.handleLocationChangeToComponent(window.views.secretsSharedWithMe);
                    }

                }

            }
        });

    }

    handleLocationChangeToComponent(component) {

        if (component && component.handleLocationChange)
            component.handleLocationChange();

    }

    resetUserData(deleteKeys, redirectToLogin, callback) {

        if (deleteKeys)
            deleteKeyPair();

        this.setState({
            mySecrets: null,
            secretsSharedWithMe: null
        }, () => {

            if (callback)
                callback();

            if (redirectToLogin && this.props.history.location.pathname !== componentsPaths.login)
                this.props.history.push(componentsPaths.login);

        });

    }

    disableMessageModal() {

        var callback = this.state.modalMessageExitCallback;

        this.setState({
            modalMessageActive: false,
            modalMessageHeader: '',
            modalMessageBody: '',
            modalMessageExitCallback: null
        }, callback);

    }

    disableConfirmationModal(button) {

        var callback = null;
        if (button === 'yes')
            callback = this.state.modalConfirmationYesCallback;
        else if (button === 'no')
            callback = this.state.modalConfirmationNoCallback;

        this.setState({
            modalConfirmationActive: false,
            modalConfirmationHeader: '',
            modalConfirmationBody: '',
            modalConfirmationYesCallback: null,
            modalConfirmationNoCallback: null
        }, callback);

    }

    render() {

        const t = this.props.t;

        return (
            <div>

                <Loader loading={this.state.loading} text={t('app.loading-text')} fullPage style={{ zIndex: '2000' }} />

                <Modal isOpen={this.state.modalMessageActive} toggle={this.disableMessageModal}>
                    <ModalHeader>{t(this.state.modalMessageHeader)}</ModalHeader>
                    <ModalBody>{t(this.state.modalMessageBody)}</ModalBody>
                </Modal>

                <Modal isOpen={this.state.modalConfirmationActive} toggle={() => { this.disableConfirmationModal('no') }}>
                    <ModalHeader>{t(this.state.modalConfirmationHeader)}</ModalHeader>
                    <ModalBody>{t(this.state.modalConfirmationBody)}</ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={() => { this.disableConfirmationModal('yes') }}>{t('global.yes')}</Button>
                        <Button color="secondary" onClick={() => { this.disableConfirmationModal('no') }}>{t('global.no')}</Button>
                    </ModalFooter>
                </Modal>

                <Route exact path={componentsPaths.login} component={Login} />
                <Route exact path={componentsPaths.registration} component={Registration} />
                <Route exact path={componentsPaths.navbarComponents} component={NavigationBar} />
                <Route exact path={componentsPaths.mySecrets} component={MySecrets} />
                <Route exact path={componentsPaths.secretsSharedWithMe} component={SecretsSharedWithMe} />

                <ReCAPTCHA sitekey={properties.captcha.siteKey} size="invisible" onChange={captchaOnChange} onErrored={captchaOnErrored} />

            </div>
        )

    }

}

export default withNamespaces()(withRouter(App));
