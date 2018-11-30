import React, { Component } from 'react';
import { withNamespaces } from 'react-i18next';
import { withRouter } from 'react-router-dom'
import { Container, Row, Col, Jumbotron, Form, FormGroup, Input, InputGroup, Button, Popover, PopoverBody, Modal, ModalHeader, ModalBody } from 'reactstrap';
import Octicon, { Eye, Key } from '@githubprimer/octicons-react'
import favicon from 'images/favicon.png';
import { get, post } from 'services/rest.jsx';
import { loading, notLoading } from 'services/loading.jsx';
import { modalMessage } from 'services/modal.jsx';
import { generateKeyPair } from 'services/encryption.jsx';
import { sha512, hashPasswordForLogin, hashPasswordForKeyGen } from 'services/hash.jsx';
import { executeCaptcha } from 'services/captcha.jsx';
import properties from 'constants/properties.json';
import apiPaths from 'constants/api-paths.json';
import componentsPaths from 'constants/components-paths.json';

class Login extends Component {

    constructor(props) {

        // Props.
        super(props);

        // Global reference to this component.
        window.views.login = this;

        // Functions binding to this.
        this.showHidePassword = this.showHidePassword.bind(this);
        this.getCode = this.getCode.bind(this);
        this.login = this.login.bind(this);
        this.cancelLogin = this.cancelLogin.bind(this);
        this.register = this.register.bind(this);

        // Refs.
        this.txtEmail = React.createRef();
        this.txtPassword = React.createRef();
        this.txtCode = React.createRef();

        // State.
        this.state = {
            email: '',
            password: '',
            passwordVisible: false,
            code: '',
            getCodeEnabled: true,
            loginEnabled: false,
            getCodePopoverActive: false,
            loginFailedPopoverActive: false,
            registerEmail: '',
            registerPopoverActive: false,
            captchaFunction: null
        };

    }

    showHidePassword() {

        this.setState({
            passwordVisible: !this.state.passwordVisible
        }, () => {
            this.txtPassword.current.select();
        });

    }

    getCode() {

        this.setState({
            loginFailedPopoverActive: false
        }, () => {

            executeCaptcha((captchaValue) => {

                get({
                    url: apiPaths.session.info,
                    loadingChained: true,
                    loadingChain: true,
                    callback: (response) => {

                        window.session = response;

                        post({
                            url: apiPaths.session.obtainLoginCode,
                            loadingChained: true,
                            body: {
                                email: this.state.email,
                                password: sha512(this.state.email + ':' + this.state.password)
                            },
                            captchaValue: captchaValue,
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

            });

        });

    }

    login() {

        this.setState({
            getCodePopoverActive: false
        }, () => {

            executeCaptcha((captchaValue) => {

                get({
                    url: apiPaths.session.info,
                    loadingChained: true,
                    loadingChain: true,
                    callback: (response) => {

                        window.session = response;

                        post({
                            url: apiPaths.session.login,
                            loadingChain: true,
                            loadingChained: true,
                            body: {
                                email: this.state.email,
                                password: hashPasswordForLogin(this.state.email, this.state.password),
                                code: this.state.code
                            },
                            captchaValue: captchaValue,
                            callback: (response) => {

                                if (response.success) {

                                    window.views.app.resetUserData(false, () => {

                                        generateKeyPair(hashPasswordForKeyGen(this.state.email, this.state.password, ''));

                                        notLoading(() => {
                                            this.props.history.push(componentsPaths.defaultComponent);
                                        });

                                    });

                                } else {

                                    notLoading(() => {

                                        this.txtCode.current.value = this.txtCode.current.defaultValue;

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

                    }
                });

            });

        });

    }

    cancelLogin() {

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

    register() {

        executeCaptcha((captchaValue) => {

            get({
                url: apiPaths.session.info,
                loadingChained: true,
                loadingChain: true,
                callback: (response) => {

                    window.session = response;

                    post({
                        url: apiPaths.registration.obtainRegistrationToken,
                        body: {
                            email: this.state.registerEmail
                        },
                        captchaValue: captchaValue,
                        loadingChained: true,
                        callback: (response) => {

                            modalMessage('login.register-success-modal-title', 'login.register-success-modal-body');

                        }
                    });

                }
            });

        });

    }

    render() {

        const t = this.props.t;

        return (
            <Container>
                <Row>

                    <Col style={{ marginTop: '25%' }}>
                        <div className="text-center">
                            <h1>{t('login.h-title')}</h1>
                            <img src={favicon} style={{ marginTop: '5em', marginBottom: '5em' }} />
                        </div>
                    </Col>

                    <Col style={{ marginTop: '8%' }}>
                        <Jumbotron className="text-center">

                            <h4>{t('login.h-login')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.getCode(); }}>
                                <fieldset disabled={!this.state.getCodeEnabled}>
                                    <FormGroup><Input innerRef={this.txtEmail} type="email" autoComplete="section-login username" placeholder={t('login.txt-email')} pattern={properties.constraints.emailPattern} maxLength={properties.constraints.emailMaxLength} required autoFocus onChange={(e) => { this.setState({ email: e.target.value }) }} /></FormGroup>
                                    <FormGroup>
                                        <InputGroup>
                                            <Input innerRef={this.txtPassword} type={this.state.passwordVisible ? "text" : "password"} autoComplete="section-login current-password" placeholder={t('login.txt-password')} required onChange={(e) => { this.setState({ password: e.target.value }) }} />
                                            <span className="icon-inline" onClick={this.showHidePassword} style={{ cursor: 'pointer' }}><Octicon icon={this.state.passwordVisible ? Key : Eye} /></span>
                                        </InputGroup>
                                    </FormGroup>
                                    <FormGroup className="group-spaced">
                                        <Button id="login_btn-get-code" type="submit" color="primary">{t('login.btn-get-code')}</Button>
                                        <Button onClick={this.lostPassword} color="primary">{t('login.btn-lost-password')}</Button>
                                    </FormGroup>
                                </fieldset>
                                <Popover target="login_btn-get-code" placement="right" isOpen={this.state.getCodePopoverActive} toggle={() => { this.setState({ getCodePopoverActive: false }) }}>
                                    <PopoverBody>{t('login.popover-get-code')}</PopoverBody>
                                </Popover>
                            </Form>

                            <Form onSubmit={(e) => { e.preventDefault(); this.login(); }}>
                                <fieldset disabled={!this.state.loginEnabled}>
                                    <FormGroup><Input innerRef={this.txtCode} type="text" autoComplete="off" placeholder={t('login.txt-code')} pattern={properties.login.loginCodePattern} maxLength={properties.login.loginCodeLength} required onChange={(e) => { this.setState({ code: e.target.value }) }} /></FormGroup>
                                    <FormGroup className="group-spaced">
                                        <Button id="login_btn-login" type="submit" color="primary">{t('login.btn-login')}</Button>
                                        <Button onClick={this.cancelLogin} color="primary">{t('login.btn-cancel-login')}</Button>
                                    </FormGroup>
                                </fieldset>
                                <Popover target="login_btn-login" placement="right" isOpen={this.state.loginFailedPopoverActive} toggle={() => { this.setState({ loginFailedPopoverActive: false }) }}>
                                    <PopoverBody>{t('login.popover-login-failed')}</PopoverBody>
                                </Popover>
                            </Form>

                            <h4 style={{ marginTop: '4.325em' }}>{t('login.h-register')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.register(); }}>
                                <FormGroup><Input type="email" autoComplete="off" placeholder={t('login.txt-register-email')} pattern={properties.constraints.emailPattern} maxLength={properties.constraints.emailMaxLength} required onChange={(e) => { this.setState({ registerEmail: e.target.value }) }} /></FormGroup>
                                <FormGroup><Button id="login_btn-register" type="submit" color="primary">{t('login.btn-register')}</Button></FormGroup>
                            </Form>

                        </Jumbotron>
                    </Col>

                </Row>
            </Container>
        )

    }

}

export default withNamespaces()(withRouter(Login));
