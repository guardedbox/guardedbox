import { loading as loadingF, notLoading as notLoadingF } from 'services/loading.jsx';
import { currentLanguage } from 'services/translation.jsx';
import { setSesionId, sessionId, workingWithoutSession, startWorkingWithoutSession, reset } from 'services/session.jsx';
import { t } from 'services/translation.jsx';
import { modalMessage } from 'services/modal.jsx';
import properties from 'constants/properties.json';

/**
 * REST Request.
 *
 * @param {object} options The options.
 * @param {string} options.method The request method: get, post, put, delete.
 * @param {string} options.url The URL.
 * @param {object} [options.pathVariables] The URL path variables.
 * @param {object} [options.params] The URL parameters.
 * @param {object} [options.body] The request body.
 * @param {boolean} [options.loading = true] Set the loading state during the request.
 * @param {boolean} [options.loadingChain = false] Indicates if the loading state should not be deactivated once the request has finished.
 * @param {boolean} [options.loadingChained = false] Indicates if the loading state was already active.
 * @param {function} [options.callback] Invoked once the request has finished. Receives a parameter: {object} responseJson.
 * @param {function} [options.serviceExceptionCallback] Invoked once the request has finished in case of service exception. Receives a parameter: {object} responseJson.
 */
export function rest({
    method,
    url,
    pathVariables,
    params,
    body,
    loading = true,
    loadingChain = false,
    loadingChained = false,
    callback,
    serviceExceptionCallback
}) {

    if (workingWithoutSession()) {
        if (!loading && !loadingChained) {
            if (callback) callback();
        } else {
            startWorkingWithoutSession();
        }
        return;
    }

    if (loading && !loadingChained) loadingF();

    var fullUrl = url;
    if (pathVariables) {
        Object.keys(pathVariables).forEach((pathVariable) => { fullUrl = fullUrl.replace('{' + pathVariable + '}', encodeURIComponent(pathVariables[pathVariable])) });
    }
    if (params) {
        fullUrl += '?' + Object.keys(params).map((param) => encodeURIComponent(param) + '=' + encodeURIComponent(params[param])).join('&');
    }

    var headers = {};
    if (sessionId()) headers[properties.headers.sessionId] = sessionId();
    headers['Accept'] = 'application/json';
    if (body) headers['Content-Type'] = 'application/json';
    headers[properties.headers.appLanguage] = currentLanguage();

    fetch(fullUrl, {
        method: method,
        headers: headers,
        body: body ? JSON.stringify(body) : null
    }).then((response) => {
        if (loading && (!loadingChain || response.status < 200 || response.status >= 300)) {
            notLoadingF(() => { processSuccess(callback, serviceExceptionCallback, response); });
        } else {
            processSuccess(callback, serviceExceptionCallback, response);
        }
    }).catch((error) => {
        if (loading) {
            notLoadingF(() => { processError(error); });
        } else {
            processError(error);
        }
    });

}

function processSuccess(callback, serviceExceptionCallback, response) {

    var sessionId = response.headers.get(properties.headers.sessionId);
    if (sessionId) setSesionId(sessionId);

    if (response.status >= 200 && response.status < 300) {

        if (callback) {
            response.json().then(callback);
        }

    } else if (response.status == 400) {

        response.json().then((responseJson) => {
            if (serviceExceptionCallback) {
                serviceExceptionCallback(responseJson);
            } else {
                modalMessage(t('global.error'), t(responseJson.errorCode || 'global.error-occurred', responseJson.additionalData));
            }
        });

    } else if (response.status == 401) {

        startWorkingWithoutSession();

    } else if (response.status == 403) {

        reset();

    } else {

        modalMessage(t('global.error'), t('global.error-occurred'), reset);

    }

}

function processError(error) {

    modalMessage(t('global.error'), t('global.error-occurred'), reset);

}
