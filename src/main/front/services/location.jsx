import queryString from 'query-string';
import { isAuthenticated, workingWithoutSession, reset } from 'services/session.jsx';
import { view, app } from 'services/views.jsx';
import views from 'constants/views.json';

/**
 * Sets the location change listener up.
 */
export function listenLocationChange() {

    history().listen(handleLocationChange);
    handleLocationChange(location());

}

function handleLocationChange(location) {

    var path = location.pathname;

    if (path === '/') {
        changeLocation(isAuthenticated() ? views.defaultPath : views.viewPaths.login);
    } else if (path === views.viewPaths.login && isAuthenticated()) {
        changeLocation(views.defaultPath);
    } else if (path === views.viewPaths.login && workingWithoutSession()) {
        reset()
    } else {
        setTimeout(() => { handleLocationChangeToPath(path) }, 25);
    }

}

function handleLocationChangeToPath(path) {

    for (var viewName in views.viewPaths) {
        if (views.viewPaths[viewName] === path) {
            var component = view(viewName);
            if (component && component.handleLocationChange) {
                component.handleLocationChange();
            }
            return;
        }
    }

}

/**
 * @returns {string} The current location path.
 */
export function currentLocationPath() {

    return location().pathname;

}

/**
 * @returns {object} The current location parameters.
 */
export function currentLocationParams() {

    var search = window.location.search;

    if (!search) {
        var hash = window.location.hash;
        var hashSplit = hash.split('?');
        if (hashSplit.length == 2) search = hashSplit[1];
    }

    return search ? queryString.parse(search) : {};

}

/**
 * @param {string} path A location path.
 * @returns {boolean} Boolean indicating if the location path is public.
 */
export function isLocationPublic(path) {

    return views.publicPaths.indexOf(path) >= 0;

}

/**
 * Changes the current location path.
 *
 * @param {string} path The new path.
 */
export function changeLocation(path) {

    if (path !== currentLocationPath()) {
        history().push(path);
    }

}

function history() {

    return app().props.history;

}

function location() {

    return history().location;

}
