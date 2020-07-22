import crypto from 'crypto';

const EPSILON_BITS = -Math.log2(Number.EPSILON);
const EPSILON_BYTES = Math.ceil(EPSILON_BITS / 8);
const FIRST_BYTE_MASK = 0b11111111 >> (EPSILON_BYTES * 8 - EPSILON_BITS);

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

    var bytes = randomBytes(EPSILON_BYTES);
    bytes[0] &= FIRST_BYTE_MASK;
    var number = bytes.readUIntBE(0, bytes.length);

    return number * Number.EPSILON;

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
