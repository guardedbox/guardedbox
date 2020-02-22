import crypto from 'crypto';
import { Uint8Array } from 'services/crypto/Uint8Array.jsx';

/**
 * Encrypts a message with an AES key and an AES initialization vector.
 * 
 * @param {object} params The parameters.
 * @param {string} params.mode The AES mode. Example: aes-256-gcm.
 * @param {(Uint8Array|string)} params.input The message to encrypt.
 * @param {string} [params.inputFormat] The format of the message to encrypt, in case it is a string. Default: utf8.
 * @param {(Uint8Array|string)} params.key The AES key used to encrypt.
 * @param {string} [params.keyFormat] The format of the AES key, in case it is a string. Default: utf8.
 * @param {(Uint8Array|string)} params.iv The AES initialization vector used to encrypt.
 * @param {string} [params.ivFormat] The format of the AES initialization vector, in case it is a string. Default: utf8.
 * @param {string} [params.outputFormat] The format of the output encrypted message. Default: Uint8Array.
 * @returns {(Uint8Array|string)} The encrypted message.
 */
export function aesEncrypt({ mode, input, inputFormat, key, keyFormat, iv, ivFormat, outputFormat }) {

    var encrypted = crypto.createCipheriv(mode, Uint8Array(key, keyFormat), Uint8Array(iv, ivFormat)).update(Uint8Array(input, inputFormat));
    return outputFormat ? encrypted.toString(outputFormat) : encrypted;

}

/**
 * Decrypts an encrypted message with an AES key and an AES initialization vector.
 * 
 * @param {object} params The parameters.
 * @param {string} params.mode The AES mode. Example: aes-256-gcm.
 * @param {(Uint8Array|string)} params.input The encrypted message to decrypt.
 * @param {string} [params.inputFormat] The format of the encrypted message to decrypt, in case it is a string. Default: utf8.
 * @param {(Uint8Array|string)} params.key The AES key used to decrypt.
 * @param {string} [params.keyFormat] The format of the AES key, in case it is a string. Default: utf8.
 * @param {(Uint8Array|string)} params.iv The AES initialization vector used to decrypt.
 * @param {string} [params.ivFormat] The format of the AES initialization vector, in case it is a string. Default: utf8.
 * @param {string} [params.outputFormat] The format of the output decrypted message. Default: Uint8Array.
 * @returns {(Uint8Array|string)} The decrypted message.
 */
export function aesDecrypt({ mode, input, inputFormat, key, keyFormat, iv, ivFormat, outputFormat }) {

    var decrypted = crypto.createDecipheriv(mode, Uint8Array(key, keyFormat), Uint8Array(iv, ivFormat)).update(Uint8Array(input, inputFormat));
    return outputFormat ? decrypted.toString(outputFormat) : decrypted;

}
