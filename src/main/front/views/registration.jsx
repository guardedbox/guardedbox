import React, { Component } from 'react';
import { Container, Row, Col, Jumbotron, Form, FormGroup, Input, InputGroup, Alert, Button, Progress, Popover, PopoverBody } from 'reactstrap';
import { Eye, Key } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import logo from 'images/logo.png';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { loading, notLoading } from 'services/loading.jsx';
import { currentLocationParams } from 'services/location.jsx';
import { generateLoginKeys, deleteLoginKeys, getLoginPublicKey, generateSessionKeys, deleteSessionKeys, getEncryptionPublicKey, getSigningPublicKey } from 'services/crypto/crypto.jsx';
import { randomBytes } from 'services/crypto/random.jsx';
import { reset } from 'services/session.jsx';
import { messageModal } from 'services/modal.jsx';
import { secretStrength } from 'services/secret-utils.jsx';
import properties from 'constants/properties.json';

class Registration extends Component {

    state = {
        token: '',
        email: '',
        passwordVisible: false,
        passwordLength: 0,
        passwordStrength: 0,
        passwordError: null,
        passwordPopoverActive: false,
        passwordPopoverBody: '',
        repeatPasswordError: null,
        repeatPasswordPopoverActive: false,
        repeatPasswordPopoverBody: ''
    };

    txtPassword = React.createRef();
    txtRepeatPassword = React.createRef();

    constructor(props) {

        super(props);
        registerView('registration', this);

    }

    handleLocationChange = () => {

        var locationParams = currentLocationParams();
        var token = locationParams.token;

        if (!this.isRegistrationTokenValid(token)) {
            reset();
            return;
        }

        rest({
            method: 'get',
            url: '/api/registrations',
            params: {
                'token': token
            },
            callback: (response) => {

                var email = response.email;

                this.setState({
                    token: token,
                    email: email
                }, () => {
                    this.txtPassword.current.focus();
                });

            },
            serviceExceptionCallback: (response) => {

                messageModal(t('global.error'), t(response.errorCode || 'global.error-occurred', response.additionalData), reset);

            }
        });

    }

    isRegistrationTokenValid = (token) => {

        return Boolean(token) && new RegExp(properties.registration.registrationTokenPattern).test(token);

    }

    showHidePassword = () => {

        this.setState({
            passwordVisible: !this.state.passwordVisible
        }, () => {
            this.txtPassword.current.select();
        });

    }

    changePassword = (value) => {

        var newState = {
            passwordLength: value.length,
            passwordStrength: secretStrength(value),
            passwordError: null
        };

        if (newState.passwordLength < properties.registration.passwordMinLength) {
            newState.passwordError = 'registration.password-insufficient-length';
        } else if (newState.passwordStrength < properties.registration.passwordMinStrength) {
            newState.passwordError = 'registration.password-insufficient-strength';
        } else {
            newState.passwordPopoverActive = false;
            newState.passwordPopoverBody = '';
        }

        this.setState(newState, () => { this.changeRepeatPassword(this.txtRepeatPassword.current.value) });

    }

    changeRepeatPassword = (value) => {

        var newState = {
            repeatPasswordError: null
        };

        if (value !== this.txtPassword.current.value) {
            newState.repeatPasswordError = 'registration.passwords-do-not-match';
        } else {
            newState.repeatPasswordPopoverActive = false;
            newState.repeatPasswordPopoverBody = '';
        }

        this.setState(newState);

    }

    checkPasswordErrors = (focus, noErrorCallback) => {

        this.setState({
            passwordPopoverActive: false,
            passwordPopoverBody: '',
            repeatPasswordPopoverActive: false,
            repeatPasswordPopoverBody: ''
        }, () => {

            if (this.state.passwordError) {

                this.setState({
                    passwordPopoverActive: true,
                    passwordPopoverBody: this.state.passwordError
                }, () => {
                    if (focus) this.txtPassword.current.focus();
                });

            } else if (this.state.repeatPasswordError) {

                this.setState({
                    repeatPasswordPopoverActive: true,
                    repeatPasswordPopoverBody: this.state.repeatPasswordError
                }, () => {
                    if (focus) this.txtRepeatPassword.current.focus();
                });

            } else {

                if (noErrorCallback) noErrorCallback();

            }

        });

    }

    register = () => {

        this.checkPasswordErrors(true, () => {

            loading(() => {

                var token = this.state.token;
                var password = this.txtPassword.current.value;
                var loginSalt = randomBytes(properties.cryptography.length, 'base64');
                var encryptionSalt = randomBytes(properties.cryptography.length, 'base64');
                var signingSalt = randomBytes(properties.cryptography.length, 'base64');

                try {

                    generateLoginKeys(password, loginSalt);
                    var loginPublicKey = getLoginPublicKey();
                    deleteLoginKeys();

                    generateSessionKeys(password, encryptionSalt, signingSalt);
                    var encryptionPublicKey = getEncryptionPublicKey();
                    var signingPublicKey = getSigningPublicKey();
                    deleteSessionKeys();

                } catch (err) {
                    notLoading(() => { messageModal(t('global.error'), t('global.error-occurred')); });
                    return;
                }

                rest({
                    method: 'post',
                    url: '/api/accounts',
                    body: {
                        registrationToken: token,
                        loginSalt: loginSalt,
                        loginPublicKey: loginPublicKey,
                        encryptionSalt: encryptionSalt,
                        encryptionPublicKey: encryptionPublicKey,
                        signingSalt: signingSalt,
                        signingPublicKey: signingPublicKey
                    },
                    loadingChained: true,
                    callback: (response) => {

                        messageModal(t('global.success'), t('registration.registration-completed'), reset);

                    },
                    serviceExceptionCallback: (response) => {

                        messageModal(t('global.error'), t(response.errorCode || 'global.error-occurred', response.additionalData), reset);

                    }
                });

            });

        });

    }

    render = () => {

        return (
            <Container>
                <Row>

                    {/* Left part */}
                    <Col className="logo-col">
                        <div className="text-center" style={{ marginBottom: '2.5em' }}>
                            <h1>{t('global.app-name')}</h1>
                            <img src={logo} style={{ marginTop: '5em', marginBottom: '5em' }} />
                            <h6>{t('global.copyright-slogan')}</h6>
                            <h6>{t('global.copyright-reference')}</h6>
                            <h6>{t('global.copyright')}</h6>
                        </div>
                    </Col>

                    {/* Right part */}
                    <Col className="main-col">
                        <Jumbotron className="text-center">

                            {/* Register */}
                            <h4>{t('registration.title-register')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.register(); }}>
                                <FormGroup>
                                    <Input
                                        type="email"
                                        autoComplete="section-registration username"
                                        placeholder={t('global.email')}
                                        defaultValue={this.state.email}
                                        required
                                        readOnly
                                    />
                                </FormGroup>
                                <Alert color="secondary" className="small">
                                    {t('registration.info-password', { passwordMinlength: properties.registration.passwordMinLength })}
                                </Alert>
                                <div>
                                    <Progress color="primary" value={this.state.passwordLength * 100 / properties.registration.passwordMinLength}>
                                        {t('global.length') + ' ' + this.state.passwordLength + ' / ' + properties.registration.passwordMinLength}
                                    </Progress>
                                    <Progress color="primary" style={{ marginTop: '5px', marginBottom: '14px' }} value={this.state.passwordStrength}>
                                        {t('global.strength') + ' ' + this.state.passwordStrength + '%'}
                                    </Progress>
                                </div>
                                <FormGroup>
                                    <Input
                                        id="registration_txt-password"
                                        innerRef={this.txtPassword}
                                        type={this.state.passwordVisible ? "text" : "password"}
                                        autoComplete="section-registration new-password"
                                        placeholder={t('global.password')}
                                        valid={(Boolean(this.txtPassword.current) && Boolean(this.txtPassword.current.value)) && !Boolean(this.state.passwordError)}
                                        invalid={(Boolean(this.txtPassword.current) && Boolean(this.txtPassword.current.value)) && Boolean(this.state.passwordError)}
                                        required
                                        onChange={(e) => { this.changePassword(e.target.value) }}
                                        onBlur={(e) => { this.checkPasswordErrors() }}
                                    />
                                    <Popover
                                        target="registration_txt-password"
                                        trigger="legacy"
                                        placement="left"
                                        isOpen={this.state.passwordPopoverActive}
                                        toggle={() => { this.setState({ passwordPopoverActive: false, passwordPopoverBody: '' }) }}
                                    >
                                        <PopoverBody>{t(this.state.passwordPopoverBody, { passwordMinLength: properties.registration.passwordMinLength })}</PopoverBody>
                                    </Popover>
                                </FormGroup>
                                <FormGroup>
                                    <Input
                                        id="registration_txt-repeat-password"
                                        innerRef={this.txtRepeatPassword}
                                        type={this.state.passwordVisible ? "text" : "password"}
                                        autoComplete="section-registration new-password"
                                        placeholder={t('registration.txt-repeat-password')}
                                        valid={(Boolean(this.txtRepeatPassword.current) && Boolean(this.txtRepeatPassword.current.value)) && !Boolean(this.state.repeatPasswordError)}
                                        invalid={(Boolean(this.txtRepeatPassword.current) && Boolean(this.txtRepeatPassword.current.value)) && Boolean(this.state.repeatPasswordError)}
                                        required
                                        onChange={(e) => { this.changeRepeatPassword(e.target.value) }}
                                        onBlur={(e) => { this.checkPasswordErrors() }}
                                    />
                                    <Popover
                                        target="registration_txt-repeat-password"
                                        trigger="legacy"
                                        placement="left"
                                        isOpen={this.state.repeatPasswordPopoverActive}
                                        toggle={() => { this.setState({ repeatPasswordPopoverActive: false, repeatPasswordPopoverBody: '' }) }}
                                    >
                                        <PopoverBody>{t(this.state.repeatPasswordPopoverBody)}</PopoverBody>
                                    </Popover>
                                </FormGroup>
                                <FormGroup>
                                    <span className="icon-inline float-left"></span>
                                    <Button type="submit" color="primary">{t('registration.btn-register')}</Button>
                                    <ActionIcon icon={this.state.passwordVisible ? Key : Eye} className="icon-inline float-right"
                                        onClick={this.showHidePassword} />
                                </FormGroup>
                            </Form>

                        </Jumbotron>
                    </Col>

                </Row>
            </Container >
        );

    }

}

export default Registration;
