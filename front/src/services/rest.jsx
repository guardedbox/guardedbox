import { loading, notLoading } from 'services/loading.jsx';
import { modalMessage } from 'services/modal.jsx';
import properties from 'constants/properties.json';

/**
 * REST GET.
 * 
 * @param {object} opts The options.
 * {
 *  url: {string},
 *  params: {object},
 *  captchaValue: {string},
 *  loading: {boolean},
 *  loadingChain: {boolean},
 *  loadingChained: {boolean},
 *  callback: {function(responseJson {object})},
 *  serviceExceptionCallback: {function(responseJson {object})}
 * }
 * 
 */
export function get(opts) {

    if ((opts.loading || !('loading' in opts)) && !opts.loadingChained) loading();

    var url = opts.url;
    if (opts.params) {
        url += '?' + Object.keys(opts.params)
            .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(opts.params[k])).join('&');
    }

    var headers = {
        'Accept': 'application/json'
    };
    if (window.session && window.session.csrfToken) {
        headers[window.session.csrfToken.headerName] = window.session.csrfToken.token;
    }
    if (opts.captchaValue) {
        headers[properties.captcha.header] = opts.captchaValue;
    }

    fetch(url, {
        method: 'GET',
        headers: headers
    }).then((response) => {
        if ((opts.loading || !('loading' in opts))
            && (!opts.loadingChain || response.status < 200 || response.status >= 300)) {
            notLoading(() => {
                processSuccess(opts, response);
            });
        } else {
            processSuccess(opts, response);
        }
    }).catch((error) => {
        if (opts.loading || !('loading' in opts)) {
            notLoading(() => {
                processError(opts, error);
            });
        } else {
            processError(opts, error);
        }
    });

}

/**
 * REST POST.
 * 
 * @param {object} opts The options.
 * {
 *  url: {string},
 *  body: {object},
 *  captchaValue: {string},
 *  loading: {boolean},
 *  loadingChain: {boolean},
 *  loadingChained: {boolean},
 *  callback: {function(responseJson {object})},
 *  serviceExceptionCallback: {function(responseJson {object})}
 * }
 * 
 */
export function post(opts) {

    if ((opts.loading || !('loading' in opts)) && !opts.loadingChained) loading();

    var headers = {
        'Accept': 'application/json'
    };
    if (opts.body) {
        headers['Content-Type'] = 'application/json';
    }
    if (window.session && window.session.csrfToken) {
        headers[window.session.csrfToken.headerName] = window.session.csrfToken.token;
    }
    if (opts.captchaValue) {
        headers[properties.captcha.header] = opts.captchaValue;
    }

    fetch(opts.url, {
        method: 'POST',
        headers: headers,
        body: opts.body ? JSON.stringify(opts.body) : null
    }).then((response) => {
        if ((opts.loading || !('loading' in opts))
            && (!opts.loadingChain || response.status < 200 || response.status >= 300)) {
            notLoading(() => {
                processSuccess(opts, response);
            });
        } else {
            processSuccess(opts, response);
        }
    }).catch((error) => {
        if (opts.loading || !('loading' in opts)) {
            notLoading(() => {
                processError(opts, error);
            });
        } else {
            processError(opts, error);
        }
    });

}

function processSuccess(opts, response) {

    if (response.status >= 200 && response.status < 300) {

        if (opts.callback) {
            response.json().then(opts.callback);
        }

    } else if (response.status == 400) {

        response.json().then((responseJson) => {
            if (opts.serviceExceptionCallback) {
                opts.serviceExceptionCallback(responseJson);
            } else {
                modalMessage('global.error', responseJson.errorCode || 'global.error-occurred');
            }
        });

    } else if (response.status == 401 || response.status == 403) {

        window.views.app.resetUserData(true, true);

    } else {

        modalMessage('global.error', 'global.error-occurred', () => {
            window.views.app.resetUserData(true, true);
        });

    }

}

function processError(opts, error) {

    modalMessage('global.error', 'global.error-occurred', () => {
        window.views.app.resetUserData(true, true);
    });

}
