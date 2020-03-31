import { app } from 'services/views.jsx';

/**
 * @param {string} key A translation key.
 * @param {object} replacements A set of replacements.
 * @returns {string} The translation corresponding to the key, with the replacements between {} replaced by its value.
 */
export function t(key, replacements) {

    var translation = app().props.t(key);

    for (var replacement in replacements) {
        translation = translation.replace(new RegExp('{' + replacement + '}', 'gi'), replacements[replacement]);
    }

    return translation;

}

/**
 * @returns The current language of the app.
 */
export function currentLanguage() {

    return app().props.i18n.language.split(/\W/)[0];

}
