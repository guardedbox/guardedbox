import crypto from 'crypto';
import { Uint8Array } from 'services/crypto/Uint8Array.jsx';

/**
 * Computes the hash of a message.
 * 
 * @param {object} params The parameters.
 * @param {string} params.algorithm The hash algorithm. Example: sha256.
 * @param {(Uint8Array|string)} params.input The message to hash.
 * @param {string} [params.inputFormat] The format of the message to hash, in case it is a string. Default: utf8.
 * @param {string} [params.outputFormat] The format of the output hash. Default: Uint8Array.
 * @returns {(Uint8Array|string)} The hash of the message.
 */
export function hash({ algorithm, input, inputFormat, outputFormat }) {

    return crypto.createHash(algorithm).update(Uint8Array(input, inputFormat)).digest(outputFormat);

}

/**
 * Computes the HMAC of a message with a key.
 * 
 * @param {object} params The parameters.
 * @param {string} params.algorithm The hash algorithm. Example: sha256.
 * @param {(Uint8Array|string)} params.input The message to hash.
 * @param {string} [params.inputFormat] The format of the message to hash, in case it is a string. Default: utf8.
 * @param {(Uint8Array|string)} params.key The key.
 * @param {string} [params.keyFormat] The format of the key, in case it is a string. Default: utf8.
 * @param {string} [params.outputFormat] The format of the output hash. Default: Uint8Array.
 * @returns {(Uint8Array|string)} The HMAC of the message.
 */
export function hmac({ algorithm, input, inputFormat, key, keyFormat, outputFormat }) {

    return crypto.createHmac(algorithm, Uint8Array(key, keyFormat)).update(Uint8Array(input, inputFormat)).digest(outputFormat);

}

/**
 * Computes the PBKDF2 of a password with a salt.
 * 
 * @param {object} params The parameters.
 * @param {string} params.algorithm The hash algorithm. Example: sha256.
 * @param {number} params.iterations The number of iterations.
 * @param {(Uint8Array|string)} params.password The password.
 * @param {string} [params.passwordFormat] The format of the password, in case it is a string. Default: utf8.
 * @param {(Uint8Array|string)} params.salt The salt.
 * @param {string} [params.saltFormat] The format of the salt, in case it is a string. Default: utf8.
 * @param {number} params.outputLength The output number of bytes.
 * @param {string} [params.outputFormat] The format of the output derivation. Default: Uint8Array.
 */
export function pbkdf2({ algorithm, iterations, password, passwordFormat, salt, saltFormat, outputLength, outputFormat }) {

    var derivation = crypto.pbkdf2Sync(Uint8Array(password, passwordFormat), Uint8Array(salt, saltFormat), iterations, outputLength, algorithm);
    return outputFormat ? derivation.toString(outputFormat) : derivation;

}
