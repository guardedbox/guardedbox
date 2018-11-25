import { loading, notLoading, temporaryLoading } from 'services/loading.jsx';
import { modalMessage } from 'services/modal.jsx';

var captchaExecutionCallback = null;
var captchaNotSolvedCallback = null;
var captchaContainer = null;
var captchaObserver = null;

/**
 * Executes the recaptcha, setting the loading state first, and invoking a callback function once the catpcha execution is finished.
 * 
 * @param {function} callback This function will be invoked once the captcha execution is finished, in case it is successful.
 * @param {function} notSolvedCallback This function will be invoked if the captcha is not solved.
 * 
 */
export function executeCaptcha(callback, notSolvedCallback) {

    captchaExecutionCallback = callback;
    captchaNotSolvedCallback = notSolvedCallback;

    resetObserver();

    loading(() => { commitExecuteCaptcha(); });

}

function commitExecuteCaptcha() {

    if (!window.grecaptcha || !window.grecaptcha.execute || !setupCaptchaObserver()) {
        setTimeout(() => { commitExecuteCaptcha(); }, 300);
        return;
    }

    window.grecaptcha.execute();

}

function setupCaptchaObserver() {

    var iframe = document.querySelector('iframe[src^="https://www.google.com/recaptcha"][src*="bframe"]');
    if (!iframe) return false;

    captchaContainer = iframe.parentNode.parentNode;
    if (!captchaContainer) return false;

    captchaObserver = new MutationObserver((mutations) => {
        if (captchaContainer.style.visibility === 'hidden') {
            notLoading(() => {
                if (captchaNotSolvedCallback) captchaNotSolvedCallback();
            });
        }
    });
    captchaObserver.observe(captchaContainer, { attributes: true, attributeFilter: ['style'] });

    return true;

}

/**
 * Handles the recaptcha onChange event. Invokes the callback function that was introduced in the executeCaptcha call. Does not remove the loading state.
 * 
 * @param {string} captchaValue Captcha response. It will be passed as argument to the function that was introduced in the executeCaptcha call.
 * 
 */
export function captchaOnChange(captchaValue) {

    if (captchaExecutionCallback) {
        captchaExecutionCallback(captchaValue || 'error');
    }

    resetObserver();
    resetCaptcha();

}

/**
 * Handles the recaptcha onErrored event. Removes the loading state and shows an error modal message.
 */
export function captchaOnErrored() {

    notLoading(() => {

        if (captchaNotSolvedCallback) captchaNotSolvedCallback();
        else modalMessage('global.error', 'global.error-occurred');

        resetObserver();
        resetCaptcha();

    });

}

function resetObserver() {

    if (captchaObserver)
        captchaObserver.disconnect();

    captchaContainer = null;
    captchaObserver = null;

}

function resetCaptcha() {

    if (window.grecaptcha)
        window.grecaptcha.reset();

    captchaExecutionCallback = null;
    captchaNotSolvedCallback = null;

}
