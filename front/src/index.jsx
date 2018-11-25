import React from 'react';
import ReactDOM from 'react-dom';
import i18next from 'i18next';
import { I18nextProvider } from 'react-i18next';
import i18nextConfig from 'i18n/i18next.config.jsx';
import { HashRouter } from 'react-router-dom';
import App from 'components/app.jsx';
import bootstrapCss from 'style/bootstrap.min.css';
import customCss from 'style/custom.css';
import favicon from 'images/favicon.png';

// Initialization.
window.views = {};

// Rendering.
ReactDOM.render(
    <I18nextProvider i18n={i18next}>
        <HashRouter>
            <App />
        </HashRouter>
    </I18nextProvider>,
    document.getElementById('app')
);
