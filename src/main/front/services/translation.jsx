import { getViewComponent } from 'services/view-components.jsx';

/**
 * @param {string} key A translation key.
 * @param {object} replacements A set of replacements.
 * @returns {string} The translation corresponding to the key, with the replacements between {} replaced by its value.
 */
export function t(key, replacements) {

    var translation = getViewComponent('app').props.t(key);

    for (var replacement in replacements) {
        translation = translation.replace(new RegExp('{' + replacement + '}', 'gi'), replacements[replacement]);
    }

    return translation;

}
