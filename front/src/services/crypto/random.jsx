import crypto from 'crypto';
import { Uint8Array } from 'services/crypto/Uint8Array.jsx';

/**
 * Generates an Uint8Array of random values.
 * 
 * @param {number} size The size of the generated Uint8Array.
 * @param {string} [params.outputFormat] The format of the output. Default: Uint8Array.
 * @returns {(Uint8Array|string)} The generated Uint8Array.
 */
export function randomBytes(size, outputFormat) {

    var bytes = crypto.randomBytes(size);
    return outputFormat ? bytes.toString(outputFormat) : bytes;

}

/**
 * Generates a random decimal between 0 (included) and 1 (not included).
 */
export function randomDecimal() {

    const BYTES = 6;
    const MAX = 281474976710656; // 2 ^ (6 * BYTES)

    var bytes = randomBytes(BYTES);
    var number = bytes.readUIntBE(0, bytes.length);

    return number / MAX;

}

/**
 * Generates a random integer.
 * 
 * @param {number} min Minimum value (included).
 * @param {number} max Maximum value (not included).
 */
export function randomInt(min, max) {

    var decimal = randomDecimal();

    return Math.floor(decimal * (max - min) + min);

}
