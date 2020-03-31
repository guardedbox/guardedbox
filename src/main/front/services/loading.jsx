import { app } from 'services/views.jsx';

var loadingActive = 0;

/**
 * Activates the loading state.
 *
 * @param {function} [callback] This function will be passed to the app setState callback.
 */
export function loading(callback) {

    loadingActive++;

    if (loadingActive == 1) {

        document.onkeydown = (e) => { return false; }

        app().setState({
            loading: true
        }, () => {
            document.getElementsByClassName("react-overlay-loader-spinner")[0].parentElement.style.zIndex = 1060;
            if (callback) setTimeout(callback, 50);
        });

    } else if (callback) callback();

}

/**
 * Deactivates the loading state.
 *
 * @param {function} [callback] This function will be passed to the app setState callback.
 */
export function notLoading(callback) {

    if (loadingActive > 0) {

        loadingActive--;

        if (loadingActive == 0) {

            document.onkeydown = null;

            app().setState({
                loading: false
            }, () => {
                if (callback) setTimeout(callback, 50);
            });

        } else if (callback) callback();

    } else if (callback) callback();

}

/**
 * Activates the loading state for some time, and then deactivates it.
 *
 * @param {number} loadingTime The time the loading state will remain active.
 * @param {function} [loadingCallback] This function will be executed when the loading state is activated.
 * @param {function} [notLoadingCallback] This function will be executed when the loading state is deactivated.
 */
export function temporaryLoading(loadingTime, loadingCallback, notLoadingCallback) {

    loading(() => {

        setTimeout(() => {
            notLoading(notLoadingCallback);
        }, loadingTime);

        if (loadingCallback) loadingCallback();

    });

}

/**
 * @returns {boolean} True if the loading state is currently active.
 */
export function currentlyLoading() {

    return loadingActive > 0;

}
