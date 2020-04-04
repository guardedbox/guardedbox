import React, { Component, Fragment } from 'react';
import { NavLink as RRNavLink } from 'react-router-dom';
import { Navbar, NavbarBrand, NavbarToggler, Collapse, Nav, NavItem, NavLink, Badge, Button, UncontrolledTooltip } from 'reactstrap';
import logo from 'images/logo.png';
import { registerView } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { sessionEmail, logout } from 'services/session.jsx';
import views from 'constants/views.json';

class NavigationBar extends Component {

    state = {
        navbarTogglerActive: false,
        email: sessionEmail(true),
        sessionExpirationTime: 0,
        workingWithoutSession: false
    };

    constructor(props) {

        super(props);
        registerView('navigationBar', this);

    }

    toggleNavbarToggler = () => {

        this.setState({
            navbarTogglerActive: !this.state.navbarTogglerActive
        });

    }

    render() {

        return (
            <div>
                <Navbar color="light" light expand="md" style={{ marginBottom: "3rem" }}>

                    <NavbarBrand><img src={logo} height="25" width="25" style={{ verticalAlign: "bottom", margin: "0 1rem 0 0.5rem" }} />
                        {t('global.app-name')}
                    </NavbarBrand>
                    <NavbarToggler onClick={this.toggleNavbarToggler} />

                    <Collapse isOpen={this.state.navbarTogglerActive} navbar>

                        <Nav className="mr-auto" navbar>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.mySecrets} activeClassName="active">
                                {t('my-secrets.title')}
                            </NavLink></NavItem>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.secretsSharedWithMe} activeClassName="active">
                                {t('secrets-shared-with-me.title')}
                            </NavLink></NavItem>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.myGroups} activeClassName="active">
                                {t('my-groups.title')}
                            </NavLink></NavItem>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.groupsIWasAddedTo} activeClassName="active">
                                {t('groups-i-was-added-to.title')}
                            </NavLink></NavItem>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.myAccount} activeClassName="active">
                                {t('my-account.title')}
                            </NavLink></NavItem>
                        </Nav>

                        <Nav className="ml-auto" navbar>
                            <span style={{ margin: '8px 0' }}>{this.state.email}</span>
                            <span className="space-between-text"></span>
                            <Badge id="navigation-bar_expiration-time-badge"
                                color={this.state.workingWithoutSession ? "warning" : "info"}
                                style={{ margin: '8px 0', padding: '0.4em', height: '22px', width: this.state.workingWithoutSession ? '60px' : '45px' }}>
                                {this.state.workingWithoutSession ?
                                    t('session.no-session') :
                                    <Fragment>
                                        {this.state.sessionExpirationTime / 60 + ' ' + t('session.minutes')}
                                        <UncontrolledTooltip placement="bottom" target="navigation-bar_expiration-time-badge">
                                            {t('session.expiration-time')}
                                        </UncontrolledTooltip>
                                    </Fragment>
                                }
                            </Badge>
                            <span className="space-between-text"></span>
                            <div className="form-inline"><Button color="secondary" size="sm" onClick={logout}>{t('global.logout')}</Button></div>
                        </Nav>

                    </Collapse>

                </Navbar>
            </div>
        );

    }

}

export default NavigationBar;
