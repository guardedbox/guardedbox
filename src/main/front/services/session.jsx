import { rest } from 'services/rest.jsx';
import { currentLocationPath, isLocationPublic } from 'services/location.jsx';
import { areSessionKeysGenerated } from 'services/crypto/crypto.jsx';
import { t } from 'services/translation.jsx';
import { messageModal, confirmationModal } from 'services/modal.jsx';
import { loading } from 'services/loading.jsx';

var currentSessionId = null;
var currentSession = null;
var authenticatedOnce = false;
var currentlyWorkingWithoutSession = false;

/**
 * Sets the current session ID.
 *
 * @param {string} sessionId
 */
export function setSesionId(sessionId) {

    currentSessionId = sessionId;

}

/**
 * @returns {string} The current session ID.
 */
export function sessionId() {

    return currentSessionId;

}

/**
 * Updates the current session info.
 *
 * @param {object} options The options.
 * @param {boolean} [options.loading = true] Set the loading state while the session info is being updated.
 * @param {boolean} [options.loadingChain = false] Indicates if the loading state should not be deactivated once the session info has been updated.
 * @param {boolean} [options.loadingChained = false] Indicates if the loading state was already active.
 * @param {function} [options.callback] Invoked once the session info has been updated.
 */
export function updateSessionInfo({
    loading = false,
    loadingChain = false,
    loadingChained = false,
    callback
}) {

    if (workingWithoutSession()) {
        if (callback) setTimeout(callback, 25);
        return;
    }

    rest({
        method: 'get',
        url: '/api/session',
        loading: loading,
        loadingChain: loadingChain,
        loadingChained: loadingChained,
        callback: (response) => {

            setSessionInfo(response);

            if (isAuthenticated()) authenticatedOnce = true;

            if (isLocationPublic(currentLocationPath()) || isAuthenticated()) {
                if (callback) callback();
            } else {
                startWorkingWithoutSession(callback);
            }

        }
    });

}

/**
 * Sets the current session info.
 *
 * @param {object} sessionInfo The session info to set.
 */
export function setSessionInfo(sessionInfo) {

    if (sessionInfo) {

        currentSession = {
            authenticated: sessionInfo.authenticated,
            email: sessionInfo.email || (currentSession ? currentSession.email : null)
        };

    } else {

        currentSession = null;

    }

}

/**
 * @returns {boolean} Indicates if the current session is authenticated.
 */
export function isAuthenticated() {

    return Boolean(currentSessionId)
        && Boolean(currentSession)
        && currentSession.authenticated
        && areSessionKeysGenerated();

}

/**
 * @param {boolean} [obfuscate] Boolean indicating if the email must be returned obfuscated.
 * @returns {string} The current session email, in case it is authenticated, or null otherwise.
 */
export function sessionEmail(obfuscate = false) {

    var email = currentSession ? currentSession.email : null;

    if (email && obfuscate) {
        try {
            var regexExec = /^(.)(.*?)(.@.+)$/.exec(sessionEmail());
            return regexExec[1] + '*'.repeat(regexExec[2].length) + regexExec[3];
        } catch (e) { }
    }

    return email;

}

/**
 * Asks the user if he would like to start working without session.
 *
 * @param {function} callback Invoked if the user says yes.
 */
export function startWorkingWithoutSession(callback) {

    if (!authenticatedOnce) reset();

    if (!currentlyWorkingWithoutSession) {

        confirmationModal(
            t('session.title-session-expired'),
            t('session.body-session-expired'),
            () => {
                currentlyWorkingWithoutSession = true;
                if (callback) callback();
            },
            reset,
            true);

    } else {

        messageModal(
            t('session.title-working-without-session'),
            t('session.body-working-without-session'),
            () => {
                if (callback) callback();
            });

    }

}

/**
 * @returns {boolean} If the app is currently working without session.
 */
export function workingWithoutSession() {

    return currentlyWorkingWithoutSession;

}

/**
 * Terminates the current session.
 */
export function logout() {

    if (workingWithoutSession()) reset();

    rest({
        method: 'post',
        url: '/api/session/logout',
        callback: (response) => {

            reset();

        }
    });

}

/**
 * Resets the webpage.
 */
export function reset() {

    loading(() => {
        window.location = '/';
    });

}
