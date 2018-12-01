import React, { Component } from 'react';
import { withNamespaces } from 'react-i18next';
import { withRouter } from 'react-router-dom';
import { Container, Row, Col, Jumbotron, Form, FormGroup, Input, InputGroup, Alert, Button, Progress, Popover, PopoverBody } from 'reactstrap';
import Octicon, { Eye, Key } from '@githubprimer/octicons-react';
import favicon from 'images/favicon.png';
import { get, post } from 'services/rest.jsx';
import { generateKeyPair, keySeed, deleteKeyPair, getPublicKey, exportPrivateKey } from 'services/encryption.jsx';
import { modalMessage } from 'services/modal.jsx';
import { passwordStrength, isRepeated } from 'services/strength.jsx';
import { sha512 } from 'services/hash.jsx';
import { executeCaptcha } from 'services/captcha.jsx';
import queryString from 'query-string';
import properties from 'constants/properties.json';
import apiPaths from 'constants/api-paths.json';

class Registration extends Component {

    constructor(props) {

        // Props.
        super(props);

        // Global reference to this component.
        window.views.registration = this;

        // Functions binding to this.
        this.handleLocationChange = this.handleLocationChange.bind(this);
        this.showHidePassword = this.showHidePassword.bind(this);
        this.changePassword = this.changePassword.bind(this);
        this.changeSecurityQuestion = this.changeSecurityQuestion.bind(this);
        this.changeSecurityAnswer = this.changeSecurityAnswer.bind(this);
        this.preRegisterCheck = this.preRegisterCheck.bind(this);
        this.register = this.register.bind(this);

        // Refs.
        this.txtPassword = React.createRef();
        this.txtSecurityQuestion = Array(properties.registration.numberOfSecurityQuestions);
        for (var i = 0; i < this.txtSecurityQuestion.length; i++) this.txtSecurityQuestion[i] = React.createRef();
        this.txtSecurityAnswer = Array(properties.registration.numberOfSecurityQuestions);
        for (var i = 0; i < this.txtSecurityAnswer.length; i++) this.txtSecurityAnswer[i] = React.createRef();

        // State.
        this.state = {
            token: '',
            email: '',
            entropyExpander: '',
            password: '',
            passwordLength: 0,
            passwordStrength: 0,
            passwordError: null,
            passwordVisible: false,
            securityQuestions: Array(properties.registration.numberOfSecurityQuestions).fill(''),
            securityQuestionsErrors: Array(properties.registration.numberOfSecurityQuestions).fill(null),
            securityAnswers: Array(properties.registration.numberOfSecurityQuestions).fill(''),
            securityAnswersLength: Array(properties.registration.numberOfSecurityQuestions).fill(0),
            securityAnswersStrength: Array(properties.registration.numberOfSecurityQuestions).fill(0),
            securityAnswersErrors: Array(properties.registration.numberOfSecurityQuestions).fill(null),
            passwordPopoverActive: false,
            passwordPopoverBody: '',
            securityQuestionsPopoverActive: false,
            securityQuestionsPopoverBody: ''
        };

    }

    handleLocationChange() {

        var uriParams = queryString.parse(this.props.location.search);
        var token = uriParams.token;

        if (!this.isRegistrationTokenValid(token)) {
            window.views.app.resetUserData(true, true);
            return;
        }

        executeCaptcha((captchaValue) => {

            get({
                url: apiPaths.registration.getRegistrationTokenInfo,
                params: {
                    token: token
                },
                loadingChained: true,
                captchaValue: captchaValue,
                callback: (response) => {

                    this.setState({
                        token: token,
                        email: response.email,
                        entropyExpander: response.entropyExpander
                    }, () => {
                        this.txtPassword.current.focus();
                    });

                },
                serviceExceptionCallback: (response) => {

                    modalMessage('global.error', response.errorCode || 'global.error-occurred', () => {
                        window.views.app.resetUserData(true, true);
                    });

                }
            });

        }, () => {

            modalMessage('global.error', 'global.error-occurred', () => {
                window.views.app.resetUserData(true, true);
            });

        });

    }

    isRegistrationTokenValid(token) {

        return token && new RegExp(properties.registration.registrationTokenPattern).test(token);

    }

    showHidePassword() {

        this.setState({
            passwordVisible: !this.state.passwordVisible
        }, () => {
            this.txtPassword.current.select();
        });

    }

    changePassword(value, callback) {

        var password = value;
        var passwordLength = password.length;
        var passwordStr = passwordStrength(password);
        var passwordError = null;
        var securityQuestions = this.state.securityQuestions.slice();
        var securityQuestionsErrors = this.state.securityQuestionsErrors.slice();
        var securityAnswers = this.state.securityAnswers.slice();
        var securityAnswersErrors = this.state.securityAnswersErrors.slice();

        // Check the password length and strength.
        if (passwordLength < properties.registration.passwordMinLength) {
            if (!passwordError) passwordError = { text: 'registration.password-insufficient-length' };
        }
        if (passwordStr < properties.registration.passwordMinStrength) {
            if (!passwordError) passwordError = { text: 'registration.password-insufficient-strength' };
        }

        // Check if the password is repeated in the security questions.
        for (var j = 0; j < properties.registration.numberOfSecurityQuestions; j++) {
            var repeated = isRepeated(password, securityQuestions[j]);
            if (repeated == 1) {
                if (!passwordError) passwordError = { text: 'registration.password-repeated-in-security-question', index: j };
            } else if (repeated == 2) {
                if (!securityQuestionsErrors[j]) securityQuestionsErrors[j] = { text: 'registration.security-question-repeated-in-password' };
            }
            if (repeated != 2 && securityQuestionsErrors[j] && securityQuestionsErrors[j].text === 'registration.security-question-repeated-in-password') {
                callback = ((value, i, callback) => { return () => { this.changeSecurityQuestion(value, i, callback) } })(securityQuestions[j], j, callback);
            }
        }

        // Check if the password is repeated in the security answers.
        for (var j = 0; j < properties.registration.numberOfSecurityQuestions + 1; j++) {
            var repeated = isRepeated(password, securityAnswers[j]);
            if (repeated == 1) {
                if (!passwordError) passwordError = { text: 'registration.password-repeated-in-security-answer', index: j };
            } else if (repeated == 2) {
                if (!securityAnswersErrors[j]) securityAnswersErrors[j] = { text: 'registration.security-answer-repeated-in-password' };
            }
            if (repeated != 2 && securityAnswersErrors[j] && securityAnswersErrors[j].text === 'registration.security-answer-repeated-in-password') {
                callback = ((value, i, callback) => { return () => { this.changeSecurityAnswer(value, i, callback) } })(securityAnswers[j], j, callback);
            }
        }

        // Set state.
        this.setState({
            password: password,
            passwordLength: passwordLength,
            passwordStrength: passwordStr,
            passwordError: passwordError,
            securityQuestionsErrors: securityQuestionsErrors,
            securityAnswersErrors: securityAnswersErrors
        }, callback);

    }

    changeSecurityQuestion(value, i, callback) {

        var password = this.state.password;
        var passwordError = this.state.passwordError;
        var securityQuestion = value;
        var securityQuestions = this.state.securityQuestions.slice();
        var securityQuestionsErrors = this.state.securityQuestionsErrors.slice();
        var securityAnswers = this.state.securityAnswers.slice();
        var securityAnswersErrors = this.state.securityAnswersErrors.slice();

        securityQuestions[i] = securityQuestion;
        securityQuestionsErrors[i] = null;

        // Check if the security question is trimmed.
        if (securityQuestion !== securityQuestion.trim()) {
            if (!securityQuestionsErrors[i]) securityQuestionsErrors[i] = { text: 'registration.security-question-not-trimmed' };
        }

        // Check if the security question is repeated in the password.
        var repeated = isRepeated(securityQuestion, password);
        if (repeated == 1) {
            if (!securityQuestionsErrors[i]) securityQuestionsErrors[i] = { text: 'registration.security-question-repeated-in-password' };
        } else if (repeated == 2) {
            if (!passwordError) passwordError = { text: 'registration.password-repeated-in-security-question', index: i };
        }
        if (repeated != 2 && passwordError && passwordError.text === 'registration.password-repeated-in-security-question' && passwordError.index == i) {
            callback = ((value, callback) => { return () => { this.changePassword(value, callback) } })(password, callback);
        }

        // Check if the security question is repeated between the other security questions.
        for (var j = 0; j < properties.registration.numberOfSecurityQuestions; j++) {
            if (i != j) {
                var repeated = isRepeated(securityQuestion, securityQuestions[j]);
                if (repeated == 1) {
                    if (!securityQuestionsErrors[i]) securityQuestionsErrors[i] = { text: 'registration.security-question-repeated', index: j };
                } else if (repeated == 2) {
                    if (!securityQuestionsErrors[j]) securityQuestionsErrors[j] = { text: 'registration.security-question-repeated', index: i };
                }
                if (repeated != 2 && securityQuestionsErrors[j] && securityQuestionsErrors[j].text === 'registration.security-question-repeated' && securityQuestionsErrors[j].index == i) {
                    callback = ((value, i, callback) => { return () => { this.changeSecurityQuestion(value, i, callback) } })(securityQuestions[j], j, callback);
                }
            }
        }

        // Check if the security question is repeated between the security answers.
        for (var j = 0; j < properties.registration.numberOfSecurityQuestions; j++) {
            var repeated = isRepeated(securityQuestion, securityAnswers[j]);
            if (repeated == 1) {
                if (!securityQuestionsErrors[i]) securityQuestionsErrors[i] = { text: 'registration.security-question-repeated-in-answer', index: j };
            } else if (repeated == 2) {
                if (!securityAnswersErrors[j]) securityAnswersErrors[j] = { text: 'registration.security-answer-repeated-in-question', index: i };
            }
            if (repeated != 2 && securityAnswersErrors[j] && securityAnswersErrors[j].text === 'registration.security-answer-repeated-in-question' && securityAnswersErrors[j].index == i) {
                callback = ((value, i, callback) => { return () => { this.changeSecurityAnswer(value, i, callback) } })(securityAnswers[j], j, callback);
            }
        }

        // Set state.
        this.setState({
            passwordError: passwordError,
            securityQuestions: securityQuestions,
            securityQuestionsErrors: securityQuestionsErrors,
            securityAnswersErrors: securityAnswersErrors
        }, callback);

    }

    changeSecurityAnswer(value, i, callback) {

        var password = this.state.password;
        var passwordError = this.state.passwordError;
        var securityQuestions = this.state.securityQuestions.slice();
        var securityQuestionsErrors = this.state.securityQuestionsErrors.slice();
        var securityAnswer = value;
        var securityAnswerLength = securityAnswer.length;
        var securityAnswerStrength = passwordStrength(securityAnswer);
        var securityAnswers = this.state.securityAnswers.slice();
        var securityAnswersErrors = this.state.securityAnswersErrors.slice();

        securityAnswers[i] = securityAnswer;
        securityAnswersErrors[i] = null;

        // Check if the security answer is trimmed.
        if (securityAnswer !== securityAnswer.trim()) {
            if (!securityAnswersErrors[i]) securityAnswersErrors[i] = { text: 'registration.security-answer-not-trimmed' };
        }

        // Check the security answer length and strength.
        if (securityAnswerLength < properties.registration.securityAnswerMinLength) {
            if (!securityAnswersErrors[i]) securityAnswersErrors[i] = { text: 'registration.security-answer-insufficient-length' };
        }
        if (securityAnswerStrength < properties.registration.securityAnswerMinStrength) {
            if (!securityAnswersErrors[i]) securityAnswersErrors[i] = { text: 'registration.security-answer-insufficient-strength' };
        }

        // Check if the security answer is repeated in the password.
        var repeated = isRepeated(securityAnswer, password);
        if (repeated == 1) {
            if (!securityAnswersErrors[i]) securityAnswersErrors[i] = { text: 'registration.security-answer-repeated-in-password' };
        } else if (repeated == 2) {
            if (!passwordError) passwordError = { text: 'registration.password-repeated-in-security-answer', index: i };
        }
        if (repeated != 2 && passwordError && passwordError.text === 'registration.password-repeated-in-security-answer' && passwordError.index == i) {
            callback = ((value, callback) => { return () => { this.changePassword(value, callback) } })(password, callback);
        }

        // Check if the security answer is repeated between the other security answers.
        for (var j = 0; j < properties.registration.numberOfSecurityQuestions; j++) {
            if (i != j) {
                var repeated = isRepeated(securityAnswer, securityAnswers[j]);
                if (repeated == 1) {
                    if (!securityAnswersErrors[i]) securityAnswersErrors[i] = { text: 'registration.security-answer-repeated', index: j };
                } else if (repeated == 2) {
                    if (!securityAnswersErrors[j]) securityAnswersErrors[j] = { text: 'registration.security-answer-repeated', index: i };
                }
                if (repeated != 2 && securityAnswersErrors[j] && securityAnswersErrors[j].text === 'registration.security-answer-repeated' && securityAnswersErrors[j].index == i) {
                    callback = ((value, i, callback) => { return () => { this.changeSecurityAnswer(value, i, callback) } })(securityAnswers[j], j, callback);
                }
            }
        }

        // Check if the security answer is repeated between the security questions.
        for (var j = 0; j < properties.registration.numberOfSecurityQuestions; j++) {
            var repeated = isRepeated(securityAnswer, securityQuestions[j]);
            if (repeated == 1) {
                if (!securityAnswersErrors[i]) securityAnswersErrors[i] = { text: 'registration.security-answer-repeated-in-question', index: j };
            } else if (repeated == 2) {
                if (!securityQuestionsErrors[j]) securityQuestionsErrors[j] = { text: 'registration.security-question-repeated-in-answer', index: i };
            }
            if (repeated != 2 && securityQuestionsErrors[j] && securityQuestionsErrors[j].text === 'registration.security-question-repeated-in-answer' && securityQuestionsErrors[j].index == i) {
                callback = ((value, i, callback) => { return () => { this.changeSecurityQuestion(value, i, callback) } })(securityQuestions[j], j, callback);
            }
        }

        // Set state.
        this.setState({
            passwordError: passwordError,
            securityQuestionsErrors: securityQuestionsErrors,
            securityAnswers: securityAnswers,
            securityAnswersErrors: securityAnswersErrors
        }, callback);

    }

    preRegisterCheck() {

        if (this.state.passwordError) {
            this.setState({
                passwordPopoverActive: true,
                passwordPopoverBody: this.state.passwordError.text
            }, () => {
                this.txtPassword.current.focus();
            });
            return false;
        }
        for (var i = 0; i < properties.registration.numberOfSecurityQuestions; i++) {
            if (this.state.securityQuestionsErrors[i]) {
                this.setState({
                    securityQuestionsPopoverActive: true,
                    securityQuestionsPopoverBody: this.state.securityQuestionsErrors[i].text
                }, () => {
                    this.txtSecurityQuestion[i].current.focus();
                });
                return false;
            }
            if (this.state.securityAnswersErrors[i]) {
                this.setState({
                    securityQuestionsPopoverActive: true,
                    securityQuestionsPopoverBody: this.state.securityAnswersErrors[i].text
                }, () => {
                    this.txtSecurityAnswer[i].current.focus();
                });
                return false;
            }
        }

        return true;

    }

    register() {

        this.setState({
            passwordPopoverActive: false,
            passwordPopoverBody: '',
            securityQuestionsPopoverActive: false,
            securityQuestionsPopoverBody: ''
        }, () => {

            if (!this.preRegisterCheck())
                return;

            executeCaptcha((captchaValue) => {

                get({
                    url: apiPaths.session.info,
                    loadingChained: true,
                    loadingChain: true,
                    callback: (response) => {

                        window.session = response;

                        generateKeyPair(keySeed(this.state.email, this.state.password, this.state.entropyExpander));
                        var publicKeyFromPassword = getPublicKey();

                        var securityAnswers = this.state.securityAnswers.slice();
                        for (var i = 0; i < securityAnswers.length; i++) securityAnswers[i] = securityAnswers[i].toLowerCase();
                        var exportedPrivateKey = exportPrivateKey(keySeed(this.state.email, JSON.stringify(securityAnswers), this.state.entropyExpander));

                        post({
                            url: apiPaths.registration.registerAccount,
                            body: {
                                registrationToken: this.state.token,
                                publicKey: publicKeyFromPassword,
                                securityQuestions: this.state.securityQuestions,
                                encryptedPrivateKey: exportedPrivateKey.encryptedPrivateKey,
                                publicKeyFromSecurityAnswers: exportedPrivateKey.publicKey
                            },
                            loadingChained: true,
                            captchaValue: captchaValue,
                            callback: (response) => {

                                modalMessage('global.success', 'registration.registration-completed', () => {
                                    window.views.app.resetUserData(true, true);
                                });

                            },
                            serviceExceptionCallback: (response) => {

                                modalMessage('global.error', response.errorCode || 'global.error-occurred', () => {
                                    window.views.app.resetUserData(true, true);
                                });

                            }
                        });

                        deleteKeyPair();

                    }
                });

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
                            <h1>{t('registration.h-title')}</h1>
                            <img src={favicon} style={{ marginTop: '5em', marginBottom: '5em' }} />
                        </div>
                    </Col>

                    <Col style={{ marginTop: '8%' }}>
                        <Jumbotron className="text-center">

                            <h4>{t('registration.h-registration')}</h4><hr />
                            <Form onSubmit={(e) => { e.preventDefault(); this.register(); }}>

                                <Input type="email" autoComplete="section-registration username" placeholder={t('registration.txt-email')} defaultValue={this.state.email} required readOnly />

                                <Alert id="registration_alert-password" color="secondary" style={{ marginTop: '1.8rem' }} className="small">{t('registration.info-password').replace('{passwordMinlength}', properties.registration.passwordMinLength)}</Alert>
                                <Input innerRef={this.txtPassword} type={this.state.passwordVisible ? "text" : "password"} autoComplete="section-registration new-password" placeholder={t('registration.txt-password')} valid={Boolean(this.state.password) && !Boolean(this.state.passwordError)} invalid={Boolean(this.state.password) && Boolean(this.state.passwordError)} required onChange={(e) => { this.changePassword(e.target.value) }} />
                                <div>
                                    <Progress color="primary" style={{ marginTop: '10px' }} value={this.state.passwordLength * 100 / properties.registration.passwordMinLength}>{t('registration.password-length') + this.state.passwordLength + ' / ' + properties.registration.passwordMinLength}</Progress>
                                    <Progress color="primary" style={{ marginTop: '5px' }} value={this.state.passwordStrength}>{t('registration.password-strength') + this.state.passwordStrength + '%'}</Progress>
                                </div>
                                <Popover target="registration_alert-password" placement="left" isOpen={this.state.passwordPopoverActive} toggle={() => { this.setState({ passwordPopoverActive: false, passwordPopoverBody: '' }) }}>
                                    <PopoverBody>{t(this.state.passwordPopoverBody).replace('{passwordMinLength}', properties.registration.passwordMinLength)}</PopoverBody>
                                </Popover>

                                <Alert id="registration_alert-security-questions" color="secondary" style={{ marginTop: '1.8rem' }} className="small">{t('registration.info-security-questions').replace('{numberOfSecurityQuestions}', properties.registration.numberOfSecurityQuestions).replace('{securityAnswerMinLength}', properties.registration.securityAnswerMinLength)}</Alert>
                                {[...Array(properties.registration.numberOfSecurityQuestions)].map((e, i) =>
                                    <InputGroup key={'security-question-' + i}>
                                        <Input innerRef={this.txtSecurityQuestion[i]} type="text" autoComplete="off" placeholder={t('registration.txt-security-question').replace('{i}', (i + 1))} maxLength={properties.registration.securityQuestionMaxLength} valid={Boolean(this.state.securityQuestions[i]) && !Boolean(this.state.securityQuestionsErrors[i])} invalid={Boolean(this.state.securityQuestions[i]) && Boolean(this.state.securityQuestionsErrors[i])} required onChange={(e) => { this.changeSecurityQuestion(e.target.value, i) }} />
                                        <Input innerRef={this.txtSecurityAnswer[i]} type={this.state.passwordVisible ? "text" : "password"} autoComplete="off" placeholder={t('registration.txt-security-answer').replace('{i}', (i + 1))} valid={Boolean(this.state.securityAnswers[i]) && !Boolean(this.state.securityAnswersErrors[i])} invalid={Boolean(this.state.securityAnswers[i]) && Boolean(this.state.securityAnswersErrors[i])} required onChange={(e) => { this.changeSecurityAnswer(e.target.value, i) }} />
                                    </InputGroup>
                                )}
                                <Popover target="registration_alert-security-questions" placement="left" isOpen={this.state.securityQuestionsPopoverActive} toggle={() => { this.setState({ securityQuestionsPopoverActive: false, securityQuestionsPopoverBody: '' }) }}>
                                    <PopoverBody>{t(this.state.securityQuestionsPopoverBody).replace('{securityAnswerMinLength}', properties.registration.securityAnswerMinLength)}</PopoverBody>
                                </Popover>

                                <FormGroup style={{ marginTop: '1.8rem' }}>
                                    <span className="icon-inline float-left"></span>
                                    <Button type="submit" color="primary">{t('registration.btn-register')}</Button>
                                    <span className="icon-inline float-right" onClick={this.showHidePassword} style={{ cursor: 'pointer' }}><Octicon icon={this.state.passwordVisible ? Key : Eye} /></span>
                                </FormGroup>

                            </Form>

                        </Jumbotron>
                    </Col>

                </Row>
            </Container>
        )

    }

}

export default withNamespaces()(withRouter(Registration));
