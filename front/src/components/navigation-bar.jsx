import React, { Component } from 'react';
import { NavLink as RRNavLink } from 'react-router-dom';
import { Navbar, NavbarBrand, NavbarToggler, Collapse, Nav, NavItem, NavLink, Button } from 'reactstrap';
import logo from 'images/logo.png';
import { registerViewComponent, getViewComponent } from 'services/view-components.jsx';
import { t } from 'services/translation.jsx';
import { sessionEmail, logout } from 'services/session.jsx';
import views from 'constants/views.json';

class NavigationBar extends Component {

    state = {
        navbarTogglerActive: false,
        email: sessionEmail()
    };

    constructor(props) {

        super(props);
        registerViewComponent('navigationBar', this);

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
                        {t('navigation-bar.brand')}
                    </NavbarBrand>
                    <NavbarToggler onClick={this.toggleNavbarToggler} />

                    <Collapse isOpen={this.state.navbarTogglerActive} navbar>

                        <Nav className="mr-auto" navbar>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.mySecrets} activeClassName="active">
                                {t('navigation-bar.my-secrets')}
                            </NavLink></NavItem>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.secretsSharedWithMe} activeClassName="active">
                                {t('navigation-bar.secrets-shared-with-me')}
                            </NavLink></NavItem>
                            <NavItem><NavLink tag={RRNavLink} to={views.viewPaths.trustedKeys} activeClassName="active">
                                {t('navigation-bar.trusted-keys')}
                            </NavLink></NavItem>
                        </Nav>

                        <Nav className="ml-auto" navbar>
                            <NavItem><NavLink tag={RRNavLink} to='/' activeClassName="active">{this.state.email}</NavLink></NavItem>
                            <div className="form-inline"><Button color="secondary" size="sm" onClick={logout}>{t('navigation-bar.logout')}</Button></div>
                        </Nav>

                    </Collapse>

                </Navbar>
            </div>
        );

    }

}

export default NavigationBar;
