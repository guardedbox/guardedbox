import React, { Component } from 'react';
import { Container, Row, Col, Jumbotron, Form, FormGroup, Input, InputGroup, Button, Popover, PopoverHeader, PopoverBody } from 'reactstrap';
import { Eye, Key } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import logo from 'images/logo.png';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { notLoading } from 'services/loading.jsx';
import { setSessionInfo } from 'services/session.jsx';
import { changeLocation } from 'services/location.jsx';
import { messageModal } from 'services/modal.jsx';
import { generateLoginKeys, deleteLoginKeys, generateSessionKeys, sign } from 'services/crypto/crypto.jsx';
import properties from 'constants/properties.json';
import views from 'constants/views.json';

class Login extends Component {

    state = {
        passwordVisible: false,
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
        registerView('login', this);

    }

    showHidePassword = () => {

        this.setState({
            passwordVisible: !this.state.passwordVisible
        }, () => {
            this.txtPassword.current.select();
        });

    }

    getCode = () => {

        var email = this.txtEmail.current.value;
        var password = this.txtPassword.current.value;

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

                            try {

                                generateLoginKeys(password, loginSalt);
                                var signedChallengeResponse = sign(challenge, true, 'base64');
                                deleteLoginKeys();

                            } catch (err) {
                                notLoading(() => { messageModal(t('global.error'), t('global.error-occurred')); });
                                return;
                            }

                            rest({
                                method: 'post',
                                url: '/api/session/otp',
                                loadingChained: true,
                                body: {
                                    email: email,
                                    signedChallengeResponse: signedChallengeResponse
                                },
                                callback: (response) => {

                                    this.setState({
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

        var code = this.txtCode.current.value;
        var password = this.txtPassword.current.value;

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

                                try {

                                    generateSessionKeys(password, encryptionSalt, signingSalt);

                                } catch (err) {
                                    notLoading(() => { messageModal(t('global.error'), t('global.error-occurred')); });
                                    return;
                                }

                                notLoading(() => { changeLocation(views.defaultPath); });

                            }
                        })

                    } else {

                        notLoading(() => {

                            this.txtCode.current.value = '';

                            this.setState({
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

    register = () => {

        var registerEmail = this.state.registerEmail;

        rest({
            method: 'post',
            url: '/api/registrations',
            body: {
                email: registerEmail
            },
            callback: (response) => {

                messageModal(t('login.register-success-modal-title'), t('login.register-success-modal-body'));

            }
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

                    {/* Rigth part */}
                    <Col className="main-col">
                        <Jumbotron className="text-center">

                            {/* Email and password */}
                            <h4>{t('login.title-login')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.getCode(); }} >
                                <FormGroup>
                                    <Input
                                        id="login_txt-email"
                                        innerRef={this.txtEmail}
                                        type="email"
                                        autoComplete="section-login username"
                                        placeholder={t('global.email')}
                                        pattern={properties.general.emailPattern}
                                        maxLength={properties.general.emailMaxLength}
                                        required
                                        autoFocus />
                                </FormGroup>
                                <FormGroup>
                                    <Input
                                        innerRef={this.txtPassword}
                                        type={this.state.passwordVisible ? "text" : "password"}
                                        autoComplete="section-login current-password"
                                        placeholder={t('global.password')}
                                        required />
                                </FormGroup>
                                <FormGroup className="group-spaced">
                                    <span className="icon-inline float-left"></span>
                                    <Button type="submit" color="primary">{t('login.btn-get-code')}</Button>
                                    <ActionIcon icon={this.state.passwordVisible ? Key : Eye} className="icon-inline float-right"
                                        onClick={this.showHidePassword} />
                                </FormGroup>
                                <Popover
                                    target="login_txt-email"
                                    trigger="legacy"
                                    placement="left-end"
                                    isOpen={this.state.loginFailedPopoverActive}
                                    toggle={() => { this.setState({ loginFailedPopoverActive: false }) }}>
                                    <PopoverHeader><span className="text-warning">{t('login.popover-login-failed-title')}</span></PopoverHeader>
                                    <PopoverBody>{t('login.popover-login-failed')}</PopoverBody>
                                </Popover>
                            </Form>

                            {/* Code */}
                            <Form onSubmit={(e) => { e.preventDefault(); this.login(); }}>
                                <FormGroup>
                                    <Input
                                        id="login_txt-code"
                                        innerRef={this.txtCode}
                                        type="text"
                                        autoComplete="off"
                                        placeholder={t('login.txt-code')}
                                        required
                                        disabled={!this.state.loginEnabled} />
                                </FormGroup>
                                <FormGroup className="group-spaced">
                                    <Button type="submit" color="primary" disabled={!this.state.loginEnabled}>{t('login.btn-login')}</Button>
                                </FormGroup>
                                <Popover
                                    target="login_txt-code"
                                    trigger="legacy"
                                    placement="left-end"
                                    isOpen={this.state.getCodePopoverActive}
                                    toggle={() => { this.setState({ getCodePopoverActive: false }) }}>
                                    <PopoverHeader><span className="text-info">{t('login.popover-get-code-title')}</span></PopoverHeader>
                                    <PopoverBody>{t('login.popover-get-code')}</PopoverBody>
                                </Popover>
                            </Form>

                            {/* Register */}
                            <h4 style={{ marginTop: '2.75rem' }}>{t('login.title-register')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.register(); }}>
                                <FormGroup>
                                    <Input
                                        type="email"
                                        autoComplete="off"
                                        placeholder={t('global.email')}
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
