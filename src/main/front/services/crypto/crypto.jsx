import { Uint8Array, concatenate, split } from 'services/crypto/Uint8Array.jsx';
import { pbkdf2 } from 'services/crypto/hash.jsx';
import { generateEcdhKeyPair, generateEddsaKeyPair } from 'services/crypto/ecc.jsx';
import { aesEncrypt, aesDecrypt } from 'services/crypto/aes.jsx';
import { randomBytes } from 'services/crypto/random.jsx';
import { checkTrustedKey } from 'services/trusted-keys.jsx';
import { notLoading } from 'services/loading.jsx';
import { t } from 'services/translation.jsx';
import { modalMessage } from 'services/modal.jsx';
import properties from 'constants/properties.json';

var loginKeys = {
    eddsaKeyPair: null
}

var currentSessionKeys = {
    ecdhKeyPair: null,
    eddsaKeyPair: null,
    selfSecret: null
};

/**
 * Generates and internally stores the login keys, derived from a password, and a login salt.
 *
 * @param {string} password The password,.
 * @param {(Uint8Array|string)} loginSalt The encryption salt.
 * @param {string} [passwordFormat] The format of the password, in case it is a string. Default: utf8.
 * @param {string} [loginSaltFormat] The format of the login salt, in case it is a string. Default: base64.
 */
export function generateLoginKeys(password, loginSalt, passwordFormat = 'utf8', loginSaltFormat = 'base64') {

    deleteLoginKeys();

    try {

        var eddsaKeyPair = generateEddsaKeyPair({
            curve: properties.cryptography.ecc.eddsaCurve,
            privateKey: pbkdf2({
                algorithm: properties.cryptography.ecc.pbkdf2Hash,
                iterations: properties.cryptography.ecc.pbkdf2Iterations,
                password: password,
                passwordFormat: passwordFormat,
                salt: loginSalt,
                saltFormat: loginSaltFormat,
                outputLength: properties.cryptography.length
            })
        });

        loginKeys.eddsaKeyPair = eddsaKeyPair;

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
    }

}

/**
 * @returns {boolean} Boolean indicating if the login keys have been generated.
 */
export function areLoginKeysGenerated() {

    return Boolean(loginKeys)
        && Boolean(loginKeys.eddsaKeyPair);

}

/**
 * Deletes the login keys.
 */
export function deleteLoginKeys() {

    loginKeys.eddsaKeyPair = null;

}

/**
 * @param {string} [outputFormat] The format of the output login public key. Default: base64.
 * @returns {string} The login public key.
 */
export function getLoginPublicKey(outputFormat = 'base64') {

    if (!areLoginKeysGenerated()) return '';

    try {

        return loginKeys.eddsaKeyPair.getPublicKey(outputFormat);

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

/**
 * Generates and internally stores the current session keys, derived from a password, a encryption salt, and a signing salt.
 *
 * @param {string} password The password,.
 * @param {(Uint8Array|string)} encryptionSalt The encryption salt.
 * @param {(Uint8Array|string)} signingSalt The signing salt.
 * @param {string} [passwordFormat] The format of the password, in case it is a string. Default: utf8.
 * @param {string} [encryptionSaltFormat] The format of the encryption salt, in case it is a string. Default: base64.
 * @param {string} [signingSaltFormat] The format of the signing salt, in case it is a string. Default: base64.
 */
export function generateSessionKeys(password, encryptionSalt, signingSalt,
    passwordFormat = 'utf8', encryptionSaltFormat = 'base64', signingSaltFormat = 'base64') {

    deleteSessionKeys();

    try {

        var ecdhKeyPair = generateEcdhKeyPair({
            curve: properties.cryptography.ecc.ecdhCurve,
            privateKey: pbkdf2({
                algorithm: properties.cryptography.ecc.pbkdf2Hash,
                iterations: properties.cryptography.ecc.pbkdf2Iterations,
                password: password,
                passwordFormat: passwordFormat,
                salt: encryptionSalt,
                saltFormat: encryptionSaltFormat,
                outputLength: properties.cryptography.length
            })
        });

        var eddsaKeyPair = generateEddsaKeyPair({
            curve: properties.cryptography.ecc.eddsaCurve,
            privateKey: pbkdf2({
                algorithm: properties.cryptography.ecc.pbkdf2Hash,
                iterations: properties.cryptography.ecc.pbkdf2Iterations,
                password: password,
                passwordFormat: passwordFormat,
                salt: signingSalt,
                saltFormat: signingSaltFormat,
                outputLength: properties.cryptography.length
            })
        });

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
 * @param {string} [publicKeyEmail] The email associated to the publicKey, in case it is introduced, to check the trusted key.
 * @param {string} [plainTextFormat] The format of the message to encrypt, in case it is a string. Default: utf8.
 * @param {string} [publicKeyFormat] The format of the public key, in case it is a string. Default: base64.
 * @param {string} [outputFormat] The format of the output encrypted message. Default: base64.
 * @returns {string} The encrypted message.
 */
export function encrypt(plainText, publicKey, publicKeyEmail, plainTextFormat = 'utf8', publicKeyFormat = 'base64', outputFormat = 'base64') {

    if (publicKey && publicKeyEmail) {
        var check = checkTrustedKey(publicKeyEmail, publicKey);
        if (!check || check === 'key-not-trusted') {
            notLoading();
            modalMessage(
                t('global.error'),
                t(check === 'key-not-trusted' ? 'trusted-keys.key-not-trusted' : 'trusted-keys.trusted-key-missmatch', { email: publicKeyEmail }));
            return '';
        }
    }

    try {

        if (publicKey == null) {
            var key = currentSessionKeys.selfSecret;
        } else {
            var key = currentSessionKeys.ecdhKeyPair.computeSecret({ publicKey: publicKey, publicKeyFormat: publicKeyFormat });
        }

        var iv = randomBytes(properties.cryptography.aes.ivLength);
        var encryptedMessage = aesEncrypt({ mode: properties.cryptography.aes.mode, input: plainText, inputFormat: plainTextFormat, key: key, iv: iv });
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
 * @param {string} [publicKeyEmail] The email associated to the publicKey, in case it is introduced, to check the trusted key.
 * @param {string} [cipherTextFormat] The format of the encrypted message to decrypt, in case it is a string. Default: base64.
 * @param {string} [publicKeyFormat] The format of the public key, in case it is a string. Default: base64.
 * @param {string} [outputFormat] The format of the output decrypted message. Default: utf8.
 * @returns {(Uint8Array|string)} The decrypted message.
 */
export function decrypt(cipherText, publicKey, publicKeyEmail, cipherTextFormat = 'base64', publicKeyFormat = 'base64', outputFormat = 'utf8') {

    try {

        if (publicKey == null) {
            var key = currentSessionKeys.selfSecret;
        } else {
            var key = currentSessionKeys.ecdhKeyPair.computeSecret({ publicKey: publicKey, publicKeyFormat: publicKeyFormat });
        }

        var [iv, encryptedMessage] = split(Uint8Array(cipherText, cipherTextFormat), properties.cryptography.aes.ivLength);
        var plainText = aesDecrypt({ mode: properties.cryptography.aes.mode, input: encryptedMessage, key: key, iv: iv });

        return outputFormat ? plainText.toString(outputFormat) : plainText;

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

export function encryptSymmetric(plainText, symmetricKey, plainTextFormat = 'utf8', symmetricKeyFormat = 'base64', outputFormat = 'base64') {

    try {

        var iv = randomBytes(properties.cryptography.aes.ivLength);
        var encryptedMessage = aesEncrypt({ mode: properties.cryptography.aes.mode, input: plainText, inputFormat: plainTextFormat, key: symmetricKey, keyFormat: symmetricKeyFormat, iv: iv });
        var cipherText = concatenate(iv, encryptedMessage);

        return outputFormat ? cipherText.toString(outputFormat) : cipherText;

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

export function decryptSymmetric(cipherText, symmetricKey, cipherTextFormat = 'base64', symmetricKeyFormat = 'base64', outputFormat = 'utf8') {

    try {

        var [iv, encryptedMessage] = split(Uint8Array(cipherText, cipherTextFormat), properties.cryptography.aes.ivLength);
        var plainText = aesDecrypt({ mode: properties.cryptography.aes.mode, input: encryptedMessage, key: symmetricKey, keyFormat: symmetricKeyFormat, iv: iv });

        return outputFormat ? plainText.toString(outputFormat) : plainText;

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}

/**
 * Signs a message with the login or current session EDDSA private key.
 *
 * @param {(Uint8Array|string)} plainText The message to sign.
 * @param {boolean} login Indicates if the login private key should be used, or the current session one instead.
 * @param {string} [plainTextFormat] The format of the message to sign, in case it is a string. Default: utf8.
 * @param {string} [outputFormat] The format of the output signature. Default: base64.
 * @returns {(Uint8Array|string)} The signature of the message.
 */
export function sign(plainText, login, plainTextFormat = 'utf8', outputFormat = 'base64') {

    try {

        var eddsaKeyPair = login ? loginKeys.eddsaKeyPair : currentSessionKeys.eddsaKeyPair;

        return eddsaKeyPair.sign({ input: plainText, inputFormat: plainTextFormat, outputFormat: outputFormat });

    } catch (err) {
        modalMessage(t('global.error'), t('global.error-occurred'));
        return '';
    }

}
