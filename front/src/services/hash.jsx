import crypto from 'crypto';

/**
 * Computes the SHA512 hash of a text.
 * 
 * @param {string} text The text.
 */
export function sha512(text) {

    return crypto.createHash('sha512').update(text, 'utf8').digest('hex');

}

/**
 * Computes the HMAC-SHA512 of a text, with a key.
 * 
 * @param {string} text The text.
 * @param {string} key The key.
 */
export function hmacSha512(text, key) {

    return crypto.createHmac('sha512', key).update(text, 'utf8').digest('hex');

}
