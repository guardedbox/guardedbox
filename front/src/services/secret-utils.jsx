import zxcvbn from 'zxcvbn';
import { randomInt } from 'services/crypto/random.jsx';
import properties from 'constants/properties.json';

/**
 * Computes the strength of a secret.
 * 
 * @param {string} secret The secret.
 * @returns {number} The strength of the secret as a number between 0 and 100.
 */
export function secretStrength(secret) {

    if (!secret) return 0;

    var maxLength = properties.secrets.secretStrengthMaxLength;
    var value = secret.length > maxLength ? secret.substr(0, maxLength) : secret;

    return zxcvbn(value).score * 25;

}

/**
 * Generates a random secret.
 * 
 * @param {number} length The secret length.
 * @returns {object} An object with the generated secret value, length and strength.
 */
export function randomSecret(length) {

    var charset = properties.secrets.randomSecretCharset;

    var value = "";
    for (var i = 0; i < length; i++)
        value += charset.charAt(randomInt(0, charset.length));

    return {
        value: value,
        length: value.length,
        strength: secretStrength(value)
    };

}
