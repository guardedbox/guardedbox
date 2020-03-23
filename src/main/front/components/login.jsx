import React, { Component } from 'react';
import { Container, Row, Col, Jumbotron, Form, FormGroup, Input, InputGroup, Button, Popover, PopoverBody } from 'reactstrap';
import Octicon, { Eye, Key } from '@primer/octicons-react'
import logo from 'images/logo.png';
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { loading, notLoading } from 'services/loading.jsx';
import { setSessionInfo } from 'services/session.jsx';
import { changeLocation } from 'services/location.jsx';
import { modalMessage } from 'services/modal.jsx';
import { generateLoginKeys, deleteLoginKeys, generateSessionKeys, sign } from 'services/crypto/crypto.jsx';
import properties from 'constants/properties.json';
import views from 'constants/views.json';

class Login extends Component {

    state = {
        email: '',
        password: '',
        passwordVisible: false,
        code: '',
        getCodeEnabled: true,
        loginEnabled: false,
        getCodePopoverActive: false,
        loginFailedPopoverActive: false,
        registerEmail: '',
        registerPopoverActive: false
    };

    txtEmail = React.createRef();
    txtPassword = React.createRef();
    txtCode = React.createRef();

    constructor(props) {

        super(props);
        registerViewComponent('login', this);

    }

    showHidePassword = () => {

        this.setState({
            passwordVisible: !this.state.passwordVisible
        }, () => {

            if (this.state.getCodeEnabled) {
                this.txtPassword.current.select();
            } else if (this.state.loginEnabled) {
                this.txtCode.current.select();
            }

        });

    }

    getCode = () => {

        var email = this.state.email;
        var password = this.state.password;

        this.setState({
            loginFailedPopoverActive: false
        }, () => {

            rest({
                method: 'get',
                url: '/api/accounts/login-salt',
                params: {
                    'email': email
                },
                loadingChain: true,
                callback: (response) => {

                    var loginSalt = response.loginSalt;

                    rest({
                        method: 'post',
                        url: '/api/session/challenge',
                        loadingChained: true,
                        loadingChain: true,
                        callback: (response) => {

                            var challenge = response.challenge;

                            generateLoginKeys(password, loginSalt);
                            var signedChallengeResponse = sign(challenge, true, 'base64');
                            deleteLoginKeys();

                            rest({
                                method: 'post',
                                url: '/api/session/otp',
                                loadingChained: true,
                                body: {
                                    email: this.state.email,
                                    signedChallengeResponse: signedChallengeResponse
                                },
                                callback: (response) => {

                                    this.setState({
                                        getCodeEnabled: false,
                                        loginEnabled: true,
                                        getCodePopoverActive: true
                                    }, () => {
                                        this.txtCode.current.focus();
                                    });

                                }
                            });

                        }
                    });

                }
            });

        });

    }

    login = () => {

        var code = this.state.code;
        var password = this.state.password;

        this.setState({
            getCodePopoverActive: false
        }, () => {

            rest({
                method: 'post',
                url: '/api/session/login',
                body: {
                    otp: code
                },
                loadingChain: true,
                callback: (response) => {

                    if (response.success) {

                        setSessionInfo(response);

                        rest({
                            method: 'get',
                            url: '/api/accounts/public-keys-salts',
                            loadingChained: true,
                            loadingChain: true,
                            callback: (response) => {

                                var encryptionSalt = response.encryptionSalt;
                                var signingSalt = response.signingSalt;
                                generateSessionKeys(password, encryptionSalt, signingSalt);

                                notLoading(() => { changeLocation(views.defaultPath); });

                            }
                        })

                    } else {

                        notLoading(() => {

                            this.txtCode.current.value = '';

                            this.setState({
                                getCodeEnabled: true,
                                loginEnabled: false,
                                loginFailedPopoverActive: true
                            }, () => {
                                this.txtEmail.current.focus();
                            });

                        });

                    }

                }
            });

        });

    }

    cancelLogin = () => {

        this.txtCode.current.value = this.txtCode.current.defaultValue;

        this.setState({
            getCodeEnabled: true,
            loginEnabled: false,
            getCodePopoverActive: false,
            loginFailedPopoverActive: false
        }, () => {
            this.txtEmail.current.focus();
        });

    }

    register = () => {

        var registerEmail = this.state.registerEmail;

        rest({
            method: 'post',
            url: '/api/registrations',
            body: {
                email: registerEmail
            },
            callback: (response) => {

                modalMessage(t('login.register-success-modal-title'), t('login.register-success-modal-body'));

            }
        });

    }

    render = () => {

        return (
            <Container>
                <Row>

                    <Col className="logo-col">
                        <div className="text-center">
                            <h1>{t('login.h-title')}</h1>
                            <img src={logo} style={{ marginTop: '5em', marginBottom: '5em' }} />
                        </div>
                    </Col>

                    <Col className="main-col">
                        <Jumbotron className="text-center">

                            <h4>{t('login.h-login')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.getCode(); }} >
                                <FormGroup>
                                    <Input
                                        id="login_txt-email"
                                        innerRef={this.txtEmail}
                                        type="email"
                                        autoComplete="section-login username"
                                        placeholder={t('login.txt-email')}
                                        pattern={properties.general.emailPattern}
                                        maxLength={properties.general.emailMaxLength}
                                        required
                                        autoFocus
                                        onChange={(e) => { this.setState({ email: e.target.value }) }}
                                        disabled={!this.state.getCodeEnabled}
                                    />
                                </FormGroup>
                                <FormGroup>
                                    <Input
                                        innerRef={this.txtPassword}
                                        type={this.state.passwordVisible ? "text" : "password"}
                                        autoComplete="section-login current-password"
                                        placeholder={t('login.txt-password')}
                                        required
                                        onChange={(e) => { this.setState({ password: e.target.value }) }}
                                        disabled={!this.state.getCodeEnabled}
                                    />
                                </FormGroup>
                                <FormGroup className="group-spaced">
                                    <Button type="submit" color="primary" disabled={!this.state.getCodeEnabled}>{t('login.btn-get-code')}</Button>
                                </FormGroup>
                                <Popover
                                    target="login_txt-email"
                                    trigger="legacy"
                                    placement="left-end"
                                    isOpen={this.state.loginFailedPopoverActive}
                                    toggle={() => { this.setState({ loginFailedPopoverActive: false }) }}
                                >
                                    <PopoverBody>{t('login.popover-login-failed')}</PopoverBody>
                                </Popover>
                            </Form>

                            <Form onSubmit={(e) => { e.preventDefault(); this.login(); }}>
                                <FormGroup style={{ display: 'none' }}>
                                    <Input
                                        type="email"
                                        autoComplete="section-login username"
                                        value={this.state.email}
                                        readOnly
                                    />
                                    <Input
                                        type="password"
                                        autoComplete="section-login current-password"
                                        value={this.state.password}
                                        readOnly
                                    />
                                </FormGroup>
                                <FormGroup>
                                    <Input
                                        id="login_txt-code"
                                        innerRef={this.txtCode}
                                        type={this.state.passwordVisible ? "text" : "password"}
                                        autoComplete="section-login one-time-code"
                                        placeholder={t('login.txt-code')}
                                        required
                                        onChange={(e) => { this.setState({ code: e.target.value }) }}
                                        disabled={!this.state.loginEnabled}
                                    />
                                </FormGroup>
                                <FormGroup className="group-spaced">
                                    <span className="icon-inline float-left"></span>
                                    <Button type="submit" color="primary" disabled={!this.state.loginEnabled}>{t('login.btn-login')}</Button>
                                    <Button onClick={this.cancelLogin} color="primary" disabled={!this.state.loginEnabled}>{t('login.btn-cancel-login')}</Button>
                                    <span className="icon-inline float-right" onClick={this.showHidePassword} style={{ cursor: 'pointer' }}>
                                        <Octicon icon={this.state.passwordVisible ? Key : Eye} />
                                    </span>
                                </FormGroup>
                                <Popover
                                    target="login_txt-code"
                                    trigger="legacy"
                                    placement="left-end"
                                    isOpen={this.state.getCodePopoverActive}
                                    toggle={() => { this.setState({ getCodePopoverActive: false }) }}
                                >
                                    <PopoverBody>{t('login.popover-get-code')}</PopoverBody>
                                </Popover>
                            </Form>

                            <h4 style={{ marginTop: '2.75rem' }}>{t('login.h-register')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.register(); }}>
                                <FormGroup>
                                    <Input
                                        type="email"
                                        autoComplete="off"
                                        placeholder={t('login.txt-register-email')}
                                        pattern={properties.general.emailPattern}
                                        maxLength={properties.general.emailMaxLength}
                                        required
                                        onChange={(e) => { this.setState({ registerEmail: e.target.value }) }}
                                    />
                                </FormGroup>
                                <FormGroup className="group-spaced">
                                    <Button type="submit" color="primary">{t('login.btn-register')}</Button>
                                </FormGroup>
                            </Form>

                        </Jumbotron>
                    </Col>

                </Row>
            </Container >
        );

    }

}

export default Login;
