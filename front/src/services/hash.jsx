import crypto from 'crypto';

/**
 * Computes and returns the SHA512 hash of a text.
 * 
 * @param {string} text The text.
 * 
 */
export function sha512(text) {

    return crypto.createHash('sha512').update(text, 'utf8').digest('hex');

}
