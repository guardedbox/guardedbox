import React, { Component } from 'react';
import { NavLink as RRNavLink } from 'react-router-dom';
import { Navbar, NavbarBrand, NavbarToggler, Collapse, Nav, NavItem, NavLink, Badge, UncontrolledTooltip } from 'reactstrap';
import { Person, SignOut } from '@primer/octicons-react';
import ActionIcon from 'components/action-icon.jsx';
import InfoIcon from 'components/info-icon.jsx';
import logo from 'images/logo-long-dark.png';
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
                <Navbar light color="light" expand="md">

                    <NavbarBrand style={{ margin: '-7px 16px 0 5px' }}>
                        <img src={logo} style={{ height: '28px', verticalAlign: 'bottom' }} />
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
                            <span style={{ margin: '8px 6px' }}>
                                <span style={{ marginRight: '16px', fontSize: '93%' }} className="md-to-xl-hidden">{this.state.email}</span>
                                <InfoIcon icon={Person} className="md-to-xl-visible" tooltipText={this.state.email} tooltipPlacement="bottom" />
                            </span>
                            <div className="form-inline" style={{ flexFlow: 'row nowrap' }}>
                                <Badge
                                    id="navigation-bar_expiration-time-badge"
                                    color={this.state.workingWithoutSession ? "warning" : "info"}
                                    style={{ marginRight: '6px', padding: '0.5em', height: '22px', width: this.state.workingWithoutSession ? '65px' : '45px' }}>
                                    {this.state.workingWithoutSession ?
                                        t('session.no-session') :
                                        this.state.sessionExpirationTime / 60 + ' ' + t('session.minutes')}
                                    <UncontrolledTooltip placement="bottom" target="navigation-bar_expiration-time-badge">
                                        {this.state.workingWithoutSession ?
                                            t('session.no-session-info') :
                                            t('session.expiration-time')}
                                    </UncontrolledTooltip>
                                </Badge>
                                <span style={{ width: '10px' }} className="md-to-xl-hidden"></span>
                                <ActionIcon icon={SignOut} tooltipText={t('global.logout')} tooltipPlacement="bottom" onClick={logout} />
                                <span style={{ width: '4px' }} className="md-to-xl-hidden"></span>
                            </div>
                        </Nav>

                    </Collapse>

                </Navbar>
            </div>
        );

    }

}

export default NavigationBar;
