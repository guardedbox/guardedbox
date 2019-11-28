import { Uint8Array, concatenate, split } from 'services/crypto/Uint8Array.jsx';
import { pbkdf2, hmac } from 'services/crypto/hash.jsx';
import { generateEcdhKeyPair, generateEddsaKeyPair } from 'services/crypto/ecc.jsx';
import { aesEncrypt, aesDecrypt } from 'services/crypto/aes.jsx';
import { randomBytes } from 'services/crypto/random.jsx';
import { t } from 'services/translation.jsx';
import { modalMessage } from 'services/modal.jsx';

const MASTER_SECRET = {
    length: 32, // bytes
    pbkdf2Hash: 'sha256',
    pbkdf2Iterations: 10000
};

const ECC = {
    ecdhCurve: 'curve25519',
    eddsaCurve: 'ed25519'
};

const AES = {
    mode: 'aes-256-gcm',
    ivLength: 12 // bytes
};

var currentSessionKeys = {
    ecdhKeyPair: null,
    eddsaKeyPair: null,
    selfSecret: null
};

/**
 * Generates and internally stores the current session keys, derived from a password and a salt.
 * 
 * @param {string} password The password,.
 * @param {(Uint8Array|string)} salt The salt.
 * @param {string} [passwordFormat] The format of the password, in case it is a string. Default: utf8.
 * @param {string} [saltFormat] The format of the salt, in case it is a string. Default: base64.
 */
export function generateSessionKeys(password, salt, passwordFormat = 'utf8', saltFormat = 'base64') {

    deleteSessionKeys();

    try {

        var masterSecret = pbkdf2({
            algorithm: MASTER_SECRET.pbkdf2Hash,
            iterations: MASTER_SECRET.pbkdf2Iterations,
            password: password,
            passwordFormat: 'utf8',
            salt: salt,
            saltFormat: saltFormat,
            outputLength: MASTER_SECRET.length
        });

        var ecdhKeyPair = generateEcdhKeyPair({ curve: ECC.ecdhCurve, privateKey: masterSecret });
        var eddsaKeyPair = generateEddsaKeyPair({ curve: ECC.eddsaCurve, privateKey: masterSecret });
        var selfSecret = ecdhKeyPair.computeSecret({ publicKey: ecdhKeyPair.getPublicKey() });

        currentSessionKeys.ecdhKeyPair = ecdhKeyPair;
        currentSessionKeys.eddsaKeyPair = eddsaKeyPair;
        currentSessionKeys.selfSecret = selfSecret;

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
    }

}

/**
 * @returns {boolean} Boolean indicating if the current session keys have been generated.
 */
export function areSessionKeysGenerated() {

    return Boolean(currentSessionKeys)
        && Boolean(currentSessionKeys.ecdhKeyPair)
        && Boolean(currentSessionKeys.eddsaKeyPair)
        && Boolean(currentSessionKeys.selfSecret);

}

/**
 * Deletes the current session keys.
 */
export function deleteSessionKeys() {

    currentSessionKeys.selfSecret = null;
    currentSessionKeys.ecdhKeyPair = null;
    currentSessionKeys.eddsaKeyPair = null;

}

/**
 * @param {string} [outputFormat] The format of the output encryption public key. Default: base64.
 * @returns {string} The current session encryption public key.
 */
export function getEncryptionPublicKey(outputFormat = 'base64') {

    if (!areSessionKeysGenerated()) return '';

    try {

        return currentSessionKeys.ecdhKeyPair.getPublicKey(outputFormat);

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

/**
 * @param {string} [outputFormat] The format of the output signing public key. Default: base64.
 * @returns {string} The current session signing public key.
 */
export function getSigningPublicKey(outputFormat = 'base64') {

    if (!areSessionKeysGenerated()) return '';

    try {

        return currentSessionKeys.eddsaKeyPair.getPublicKey(outputFormat);

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

/**
 * Encrypts a message.
 * 
 * @param {(Uint8Array|string)} plainText The message to encrypt.
 * @param {string} [publicKey] If not introduced, the current session master secret is used to encrypt. If introduced, a secret is computed using the current session ECDH private key.
 * @param {string} [plainTextFormat] The format of the message to encrypt, in case it is a string. Default: utf8.
 * @param {string} [publicKeyFormat] The format of the public key, in case it is a string. Default: base64.
 * @param {string} [outputFormat] The format of the output encrypted message. Default: base64.
 * @returns {string} The encrypted message.
 */
export function encrypt(plainText, publicKey, plainTextFormat = 'utf8', publicKeyFormat = 'base64', outputFormat = 'base64') {

    try {

        if (publicKey == null) {
            var key = currentSessionKeys.selfSecret;
        } else {
            var key = currentSessionKeys.ecdhKeyPair.computeSecret({ publicKey: publicKey, publicKeyFormat: publicKeyFormat });
        }

        var iv = randomBytes(AES.ivLength);
        var encryptedMessage = aesEncrypt({ mode: AES.mode, input: plainText, inputFormat: plainTextFormat, key: key, iv: iv });
        var cipherText = concatenate(iv, encryptedMessage);

        return outputFormat ? cipherText.toString(outputFormat) : cipherText;

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

/**
 * Decrypts an encrypted message.
 * 
 * @param {string} cipherText The encrypted message to decrypt.
 * @param {string} [publicKey] If not introduced, the current session master secret is used to decrypt. If introduced, a secret is computed using the current session ECDH private key.
 * @param {string} [cipherTextFormat] The format of the encrypted message to decrypt, in case it is a string. Default: base64.
 * @param {string} [publicKeyFormat] The format of the public key, in case it is a string. Default: base64.
 * @param {string} [outputFormat] The format of the output decrypted message. Default: utf8.
 * @returns {(Uint8Array|string)} The decrypted message.
 */
export function decrypt(cipherText, publicKey, cipherTextFormat = 'base64', publicKeyFormat = 'base64', outputFormat = 'utf8') {

    try {

        if (publicKey == null) {
            var key = currentSessionKeys.selfSecret;
        } else {
            var key = currentSessionKeys.ecdhKeyPair.computeSecret({ publicKey: publicKey, publicKeyFormat: publicKeyFormat });
        }

        var [iv, encryptedMessage] = split(Uint8Array(cipherText, cipherTextFormat), AES.ivLength);
        var plainText = aesDecrypt({ mode: AES.mode, input: encryptedMessage, key: key, iv: iv });

        return outputFormat ? plainText.toString(outputFormat) : plainText;

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

/**
 * Signs a message with the current session EDDSA private key.
 * 
 * @param {(Uint8Array|string)} plainText The message to sign.
 * @param {string} [plainTextFormat] The format of the message to sign, in case it is a string. Default: utf8.
 * @param {string} [outputFormat] The format of the output signature. Default: base64.
 * @returns {(Uint8Array|string)} The signature of the message.
 */
export function sign(plainText, plainTextFormat = 'utf8', outputFormat = 'base64') {

    try {

        return currentSessionKeys.eddsaKeyPair.sign({ input: plainText, inputFormat: plainTextFormat, outputFormat: outputFormat });

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}
