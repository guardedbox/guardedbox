
var viewComponents = {};

/**
 * Registers a view component.
 * 
 * @param {string} name The view component name.
 * @param {React.Component} component The view component.
 */
export function registerViewComponent(name, component) {

    viewComponents[name] = component;

}

/**
 * @param {string} name A view component name.
 * @returns {React.Component} The view component corresponding to that name.
 */
export function getViewComponent(name) {

    return viewComponents[name];

}
