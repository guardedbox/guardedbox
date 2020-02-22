import React, { Component } from 'react';
import { Container, Row, Col, Jumbotron, Form, FormGroup, Input, InputGroup, Alert, Button, Progress, Popover, PopoverBody } from 'reactstrap';
import Octicon, { Eye, Key } from '@primer/octicons-react';
import logo from 'images/logo.png';
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { currentLocationParams } from 'services/location.jsx';
import { generateSessionKeys, deleteSessionKeys, getEncryptionPublicKey, getSigningPublicKey, mine } from 'services/crypto/crypto.jsx';
import { randomBytes } from 'services/crypto/random.jsx';
import { reset } from 'services/session.jsx';
import { modalMessage } from 'services/modal.jsx';
import { secretStrength } from 'services/secret-utils.jsx';
import properties from 'constants/properties.json';

class Registration extends Component {

    state = {
        token: '',
        email: '',
        password: '',
        passwordVisible: false,
        passwordLength: 0,
        passwordStrength: 0,
        passwordError: null,
        passwordPopoverActive: false,
        passwordPopoverBody: '',
        repeatPassword: '',
        repeatPasswordError: null,
        repeatPasswordPopoverActive: false,
        repeatPasswordPopoverBody: ''
    };

    txtPassword = React.createRef();
    txtRepeatPassword = React.createRef();

    constructor(props) {

        super(props);
        registerViewComponent('registration', this);

    }

    handleLocationChange = () => {

        var locationParams = currentLocationParams();
        var token = locationParams.token;

        if (!this.isRegistrationTokenValid(token)) {
            reset();
            return;
        }

        rest({
            method: 'post',
            url: '/api/session/challenge',
            loadingChain: true,
            callback: (response) => {

                var challenge = response.challenge;
                var minedChallengeResponse = mine(challenge, 'base64');

                rest({
                    method: 'get',
                    url: '/api/registrations',
                    params: {
                        'token': token,
                        'mined-challenge-response': minedChallengeResponse
                    },
                    loadingChained: true,
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

                        modalMessage(t('global.error'), t(response.errorCode || 'global.error-occurred', response.additionalData), reset);

                    }
                });

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
            password: value,
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

        this.setState(newState, () => { this.changeRepeatPassword(this.state.repeatPassword) });

    }

    changeRepeatPassword = (value) => {

        var newState = {
            repeatPassword: value,
            repeatPasswordError: null
        };

        if (value !== this.state.password) {
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

            rest({
                method: 'post',
                url: '/api/session/challenge',
                loadingChain: true,
                callback: (response) => {

                    var challenge = response.challenge;
                    var minedChallengeResponse = mine(challenge, 'base64');

                    var token = this.state.token;
                    var password = this.state.password;
                    var salt = randomBytes(properties.registration.saltBytes, 'base64');

                    generateSessionKeys(password, salt);
                    var encryptionPublicKey = getEncryptionPublicKey();
                    var signingPublicKey = getSigningPublicKey();

                    rest({
                        method: 'post',
                        url: '/api/accounts',
                        body: {
                            registrationToken: token,
                            salt: salt,
                            encryptionPublicKey: encryptionPublicKey,
                            signingPublicKey: signingPublicKey,
                            minedChallengeResponse: minedChallengeResponse
                        },
                        loadingChained: true,
                        callback: (response) => {

                            modalMessage(t('global.success'), t('registration.registration-completed'), reset);

                        },
                        serviceExceptionCallback: (response) => {

                            modalMessage(t('global.error'), t(response.errorCode || 'global.error-occurred', response.additionalData), reset);

                        }
                    });

                    deleteSessionKeys();

                }
            });

        });

    }

    render = () => {

        return (
            <Container>
                <Row>

                    <Col className="logo-col">
                        <div className="text-center">
                            <h1>{t('registration.h-title')}</h1>
                            <img src={logo} style={{ marginTop: '5em', marginBottom: '5em' }} />
                        </div>
                    </Col>

                    <Col className="main-col">
                        <Jumbotron className="text-center">

                            <h4>{t('registration.h-registration')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.register(); }}>
                                <FormGroup>
                                    <Input
                                        type="email"
                                        autoComplete="section-registration username"
                                        placeholder={t('registration.txt-email')}
                                        defaultValue={this.state.email}
                                        required
                                        readOnly
                                    />
                                </FormGroup>
                                <Alert color="secondary" className="small">
                                    {t('registration.info-password', { passwordMinlength: properties.registration.passwordMinLength })}
                                </Alert>
                                <FormGroup>
                                    <Input
                                        id="registration_txt-password"
                                        innerRef={this.txtPassword}
                                        type={this.state.passwordVisible ? "text" : "password"}
                                        autoComplete="section-registration new-password"
                                        placeholder={t('registration.txt-password')}
                                        valid={Boolean(this.state.password) && !Boolean(this.state.passwordError)}
                                        invalid={Boolean(this.state.password) && Boolean(this.state.passwordError)}
                                        required
                                        onChange={(e) => { this.changePassword(e.target.value) }}
                                        onBlur={(e) => { this.checkPasswordErrors() }}
                                    />
                                </FormGroup>
                                <FormGroup>
                                    <Input
                                        id="registration_txt-repeat-password"
                                        innerRef={this.txtRepeatPassword}
                                        type={this.state.passwordVisible ? "text" : "password"}
                                        autoComplete="section-registration new-password"
                                        placeholder={t('registration.txt-repeat-password')}
                                        valid={Boolean(this.state.repeatPassword) && !Boolean(this.state.repeatPasswordError)}
                                        invalid={Boolean(this.state.repeatPassword) && Boolean(this.state.repeatPasswordError)}
                                        required
                                        onChange={(e) => { this.changeRepeatPassword(e.target.value) }}
                                        onBlur={(e) => { this.checkPasswordErrors() }}
                                    />
                                </FormGroup>
                                <div>
                                    <Progress color="primary" style={{ marginTop: '10px' }} value={this.state.passwordLength * 100 / properties.registration.passwordMinLength}>
                                        {t('registration.password-length') + this.state.passwordLength + ' / ' + properties.registration.passwordMinLength}
                                    </Progress>
                                    <Progress color="primary" style={{ marginTop: '5px' }} value={this.state.passwordStrength}>
                                        {t('registration.password-strength') + this.state.passwordStrength + '%'}
                                    </Progress>
                                </div>
                                <FormGroup style={{ marginTop: '1.8rem' }}>
                                    <span className="icon-inline float-left"></span>
                                    <Button type="submit" color="primary">{t('registration.btn-register')}</Button>
                                    <span className="icon-inline float-right" onClick={this.showHidePassword} style={{ cursor: 'pointer' }}>
                                        <Octicon icon={this.state.passwordVisible ? Key : Eye} />
                                    </span>
                                </FormGroup>
                                <Popover
                                    target="registration_txt-password"
                                    trigger="legacy"
                                    placement="left"
                                    isOpen={this.state.passwordPopoverActive}
                                    toggle={() => { this.setState({ passwordPopoverActive: false, passwordPopoverBody: '' }) }}
                                >
                                    <PopoverBody>{t(this.state.passwordPopoverBody, { passwordMinLength: properties.registration.passwordMinLength })}</PopoverBody>
                                </Popover>
                                <Popover
                                    target="registration_txt-repeat-password"
                                    trigger="legacy"
                                    placement="left"
                                    isOpen={this.state.repeatPasswordPopoverActive}
                                    toggle={() => { this.setState({ repeatPasswordPopoverActive: false, repeatPasswordPopoverBody: '' }) }}
                                >
                                    <PopoverBody>{t(this.state.repeatPasswordPopoverBody)}</PopoverBody>
                                </Popover>
                            </Form>

                        </Jumbotron>
                    </Col>

                </Row>
            </Container>
        );

    }

}

export default Registration;
