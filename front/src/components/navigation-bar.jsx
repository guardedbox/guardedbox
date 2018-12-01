import React, { Component } from 'react';
import { withNamespaces } from 'react-i18next';
import { withRouter, NavLink as RRNavLink } from 'react-router-dom';
import { Navbar, NavbarBrand, NavbarToggler, Collapse, Nav, NavItem, NavLink, Button } from 'reactstrap';
import favicon from 'images/favicon.png';
import { get, post } from 'services/rest.jsx';
import apiPaths from 'constants/api-paths.json';
import componentsPaths from 'constants/components-paths.json';

class NavigationBar extends Component {

    constructor(props) {

        // Props.
        super(props);

        // Global reference to this component.
        window.views.navigationBar = this;

        // Functions binding to this.
        this.logout = this.logout.bind(this);
        this.toggleNavbarToggler = this.toggleNavbarToggler.bind(this);

        // State.
        this.state = {
            navbarTogglerActive: false,
            email: window.session ? window.session.email : ''
        };

    }

    logout() {

        post({
            url: apiPaths.session.logout,
            callback: (response) => {

                window.views.app.resetUserData(true, true);

            }
        });

    }

    toggleNavbarToggler() {

        this.setState({
            navbarTogglerActive: !this.state.navbarTogglerActive
        });

    }

    render() {

        const t = this.props.t;

        return (
            <div>
                <Navbar color="light" light expand="md" style={{ marginBottom: "3rem" }}>

                    <NavbarBrand><img src={favicon} height="25" width="25" style={{ verticalAlign: "bottom", margin: "0 1rem 0 0.5rem" }} />{t('navigation-bar.brand')}</NavbarBrand>
                    <NavbarToggler onClick={this.toggleNavbarToggler} />

                    <Collapse isOpen={this.state.navbarTogglerActive} navbar>

                        <Nav className="mr-auto" navbar>
                            <NavItem><NavLink tag={RRNavLink} to={componentsPaths.mySecrets} activeClassName="active">{t('navigation-bar.my-secrets')}</NavLink></NavItem>
                            <NavItem><NavLink tag={RRNavLink} to={componentsPaths.secretsSharedWithMe} activeClassName="active">{t('navigation-bar.secrets-shared-with-me')}</NavLink></NavItem>
                        </Nav>

                        <Nav className="ml-auto" navbar>
                            <NavItem><NavLink tag={RRNavLink} to='/' activeClassName="active">{this.state.email}</NavLink></NavItem>
                            <div className="form-inline"><Button color="secondary" size="sm" onClick={this.logout}>{t('navigation-bar.logout')}</Button></div>
                        </Nav>

                    </Collapse>

                </Navbar>
            </div>
        )

    }

}

export default withNamespaces()(withRouter(NavigationBar));
