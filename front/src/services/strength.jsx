import zxcvbn from 'zxcvbn';
import properties from 'constants/properties.json';

/**
 * Returns the strength of a password as a number between 0 and 100.
 * 
 * @param {string} password The password.
 * 
 */
export function passwordStrength(password) {

    if (!password) return 0;
    if (password.length > properties.passwordStrength.maxLength) password = password.substr(0, properties.passwordStrength.maxLength);

    return zxcvbn(password).score * 25;

}

/**
 * Checks if a string is contained into another one and vice-versa, ignoring case.
 * 
 * @param {string} string1 A string.
 * @param {string} string2 Another string.
 * 
 */
export function isRepeated(string1, string2) {

    if (!string1 || !string2) return false;

    var str1 = string1.toLowerCase();
    var str2 = string2.toLowerCase();

    if (str1.indexOf(str2) >= 0) return 2;
    else if (str2.indexOf(str1) >= 0) return 1;
    else return 0;

}
