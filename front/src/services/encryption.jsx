import cryptico, { RSAKey } from 'cryptico-js';
import { Base64 } from 'js-base64';
import { modalMessage } from 'services/modal.jsx';
import properties from 'constants/properties.json';

var privateKey = null
var publicKey = '';

/**
 * Generates and internally stores a key pair derived from a password.
 * 
 * @param {string} password The password.
 * 
 */
export function generateKeyPair(password) {

    privateKey = cryptico.generateRSAKey(password, properties.encryption.rsaKeyBits);
    publicKey = cryptico.publicKeyString(privateKey);

    Math.seedrandom();

}

/**
 * Deletes the stored key pair.
 */
export function deleteKeyPair() {

    privateKey = null;
    publicKey = '';

}

/**
 * Checks if a key pair has been generated and is stored.
 */
export function isKeyPairGenerated() {

    return Boolean(privateKey && publicKey);

}

/**
 * Returns the stored public key.
 */
export function getPublicKey() {

    return publicKey;

}

/**
 * Encrypts a message and returns it encrypted.
 * 
 * @param {string} plainText The message to encrypt.
 * @param {string} pubKey The public key used to encrypt. Defaults to the stored public key.
 * 
 */
export function encrypt(plainText, pubKey) {

    var enc = Base64.encode(plainText);

    if (enc) {

        var result = cryptico.encrypt(enc, pubKey || publicKey);

        if (result.status === 'success') {
            return result.cipher;
        }

    }

    modalMessage('global.error', 'global.error-occurred');
    return '';

}

/**
 * Decrypts an encrypted message and returns it decrypted.
 * 
 * @param {string} cipherText The encrypted message to decrypt.
 * @param {Cryptico.RSAKey} privKey The private key used to decrypt. Defaults to the stored private key.
 * 
 */
export function decrypt(cipherText, privKey) {

    var result = cryptico.decrypt(cipherText, privKey || privateKey);

    if (result.status === 'success') {

        var dec = Base64.decode(result.plaintext);

        if (dec) {
            return dec;
        }

    }

    modalMessage('global.error', 'global.error-occurred');
    return '';

}

/**
 * Exports the stored private key, encrypted with a public key derived from a password.
 * 
 * @param {string} password The password.
 * 
 */
export function exportPrivateKey(password) {

    var pubKey = cryptico.publicKeyString(cryptico.generateRSAKey(password, properties.encryption.rsaKeyBits));
    var privateKeyPlainText = JSON.stringify(privateKey);
    var privateKeyCipherText = encrypt(privateKeyPlainText, pubKey);

    Math.seedrandom();

    return privateKeyCipherText;

}

/**
 * 
 * Decrypts a private key with another private key derived from a password, and internally stores its key pair.
 * 
 * @param {string} exportedPrivateKey The private key to import.
 * @param {string} password The password that was used to encrypt the private key when it was exported.
 * 
 */
export function importPrivateKey(exportedPrivateKey, password) {

    var privKey = cryptico.generateRSAKey(password, properties.encryption.rsaKeyBits);
    var privateKeyPlainText = decrypt(exportedPrivateKey, privKey);

    if (privateKeyPlainText) {
        privateKey = RSAKey.parse(privateKeyPlainText);
        publicKey = cryptico.publicKeyString(privateKey);
    }

    Math.seedrandom();

}
