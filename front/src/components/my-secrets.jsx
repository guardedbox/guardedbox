import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { withNamespaces } from 'react-i18next';
import { withRouter } from 'react-router-dom';
import { Container } from 'reactstrap';

class MySecrets extends Component {

    constructor(props) {

        // Props.
        super(props);

        // Global reference to this component.
        window.views.mySecrets = this;

        // Functions binding to this.
        this.handleLocationChange = this.handleLocationChange.bind(this);

        // Refs.

        // State.
        this.state = {
        };

    }

    handleLocationChange() {



    }

    render() {

        const t = this.props.t;

        return (
            <Container>

                <h4>{t('my-secrets.title')}</h4><hr />

            </Container>
        )

    }

}

export default withNamespaces()(withRouter(MySecrets));
