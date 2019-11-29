import { rest } from 'services/rest.jsx';

const TRUSTED_KEYS_LOCAL_STORAGE_ITEM = 'trustedKeys';

var trustedKeys = null;

/**
 * @returns The trusted keys stored in the local storage.
 */
export function getTrustedKeys() {

    if (!trustedKeys) loadFromLocalStorage();

    return trustedKeys;

}

/**
 * Adds a trusted key to the local storage.
 * 
 * @param {string} email The email associated to the new trusted key.
 * @param {string} encryptionPublicKey The encryption public key of the new trusted key.
 */
export function addTrustedKey(email, encryptionPublicKey, callback) {

    if (!trustedKeys) {
        trustedKeys = [];
    }

    rest({
        method: 'get',
        url: '/api/accounts/encryption-public-key',
        params: {
            'email': email
        },
        callback: (response) => {

            var alreadyPresent = false;
            for (var trustedKey of trustedKeys) {
                if (trustedKey.email === email) {
                    trustedKey.encryptionPublicKey = encryptionPublicKey;
                    alreadyPresent = true;
                    break;
                }
            }

            if (!alreadyPresent) {
                trustedKeys.push({
                    email: email,
                    encryptionPublicKey: encryptionPublicKey
                });
            }

            saveToLocalStorage();

            if (callback) callback();

        }
    });

}

/**
 * Removes a trusted key from the local storage.
 * 
 * @param {string} email The email associated to the trusted key to be removed.
 */
export function removeTrustedKey(email) {

    if (!trustedKeys) {
        return;
    }

    for (var i = 0; i < trustedKeys.length; i++) {
        var trustedKey = trustedKeys[i];
        if (trustedKey.email === email) {
            trustedKeys.splice(i, 1);
            break;
        }
    }

    saveToLocalStorage();

}

/**
 * Checks if a public key matches an email trusted key.
 * 
 * @param {string} email The email corresponding to the trusted key.
 * @param {string} encryptionPublicKey The public key to be checked.
 */
export function checkTrustedKey(email, encryptionPublicKey) {

    if (!trustedKeys) loadFromLocalStorage();

    var trustedKey = null;
    for (var trustedKey of trustedKeys) {
        if (trustedKey.email === email) {
            break;
        }
    }

    if (!trustedKey) {
        return 'key-not-trusted';
    }

    return trustedKey && trustedKey.encryptionPublicKey === encryptionPublicKey;

}

function loadFromLocalStorage() {

    var localStorageItem = window.localStorage.getItem(TRUSTED_KEYS_LOCAL_STORAGE_ITEM);

    if (localStorageItem) {

        try {
            trustedKeys = JSON.parse(localStorageItem);
        } catch (err) {
            trustedKeys = [];
        }

    } else {

        trustedKeys = [];

    }

}

function saveToLocalStorage() {

    window.localStorage.setItem(TRUSTED_KEYS_LOCAL_STORAGE_ITEM, JSON.stringify(trustedKeys));

}
