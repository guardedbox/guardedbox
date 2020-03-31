
var views = {};

/**
 * Registers a view.
 *
 * @param {string} name The view name.
 * @param {React.Component} component The view.
 */
export function registerView(name, view) {

    views[name] = view;

}

/**
 * @param {string} name A view name.
 * @returns {React.Component} The view corresponding to that name.
 */
export function view(name) {

    return views[name];

}

/**
 * @returns {React.Component} The view corresponding to the name 'app'.
 */
export function app() {

    return view('app');

}
