import React from 'react';
import { app } from 'services/views.jsx';
import { addElementToStateArray, setStateArrayElement, removeStateArrayElement } from 'services/state-utils.jsx';
import { generateRandomSymmetricKey, getSymmetricKey, encrypt, decrypt } from 'services/crypto/crypto.jsx';
import { Uint8Array } from 'services/crypto/Uint8Array.jsx';
import { copyToClipboard } from 'services/selector.jsx';
import zxcvbn from 'zxcvbn';
import { randomInt } from 'services/crypto/random.jsx';
import { t } from 'services/translation.jsx';
import { messageModal } from 'services/modal.jsx';
import properties from 'constants/properties.json';

/**
 * Opens the secret modal.
 *
 * @param {string} header The secret modal header.
 * @param {object} secret A secret that will be loaded in the secret modal.
 * @param {function} [acceptCallback] This function will be invoked when the Accept button is clicked. It will receive the value of the secret in the secret modal, the originally loaded secret and acceptCallbackThirdArg as arguments.
 * @param {any} acceptCallbackThirdArg This argument will be passed to the function acceptCallback as third argument.
 */
export function secretModal(header, secret, acceptCallback, acceptCallbackThirdArg) {

    if (secret) {

        try {

            var secretName = secret.value.name;
            var keyValuePairs = [];
            var symmetricKey = decryptSymmetricKey(secret.encryptedKey);

            for (var keyValuePair of secret.value.values) {

                var clearValue = decryptValue(keyValuePair.value, symmetricKey);

                keyValuePairs.push({
                    key: keyValuePair.key,
                    value: clearValue,
                    valueLength: clearValue.length,
                    valueStrength: secretStrength(clearValue)
                });

            }

        } catch (err) {
            messageModal(t('global.error'), t('global.error-occurred'));
            return;
        }

    } else {

        var secretName = '';
        var keyValuePairs = [{
            key: '',
            value: '',
            valueLength: 0,
            valueStrength: 0
        }];

    }

    app().secretModalTxtKey = [];
    app().secretModalTxtValue = []
    for (var i in keyValuePairs) {
        app().secretModalTxtKey.push(React.createRef());
        app().secretModalTxtValue.push(React.createRef());
    }

    app().setState({
        secretModalActive: true,
        secretModalHeader: header,
        secretModalSecretName: secretName,
        secretModalSecretKeyValuePairs: keyValuePairs,
        secretModalGenerateRandomValueLength: 0,
        secretModalOriginalSecret: secret,
        secretModalAcceptCallback: acceptCallback,
        secretModalAcceptCallbackThirdArg: acceptCallbackThirdArg
    }, () => {

        setTimeout(() => {

            app().secretModalTxtName.current.value = secretName;

            for (var i in keyValuePairs) {
                app().secretModalTxtKey[i].current.value = keyValuePairs[i].key;
                app().secretModalTxtValue[i].current.value = keyValuePairs[i].value;
            }

            app().secretModalTxtName.current.focus();

        }, 25);

    });

}

/**
 * Closes the secret modal.
 *
 * @param {function} callback This function will be invoked when the secret modal is closed.
 */
export function closeSecretModal(callback) {

    app().secretModalTxtKey = [];
    app().secretModalTxtValue = [];

    app().setState({
        secretModalActive: false,
        secretModalHeader: '',
        secretModalSecretName: '',
        secretModalSecretKeyValuePairs: [],
        secretModalGenerateRandomValueLength: 0,
        secretModalOriginalSecret: null,
        secretModalAcceptCallback: null,
        secretModalAcceptCallbackThirdArg: null
    }, callback);

}

/**
 * Adds a key value pair to the secret modal.
 */
export function secretModalAddKeyValuePair() {

    var secretModalTxtKeyRef = React.createRef();
    var secretModalTxtValueRef = React.createRef();
    app().secretModalTxtKey.push(secretModalTxtKeyRef);
    app().secretModalTxtValue.push(secretModalTxtValueRef);

    addElementToStateArray(app(), 'secretModalSecretKeyValuePairs', { key: '', value: '', valueLength: 0, valueStrength: 0 }, () => {
        setTimeout(() => {
            secretModalTxtKeyRef.current.focus();
        }, 25);
    });

}

/**
 * Removes a key value pair from the secret modal.
 *
 * @param {number} i Key value pair index.
 */
export function secretModalRemoveKeyValuePair(i) {

    app().secretModalTxtKey.splice(i, 1);
    app().secretModalTxtValue.splice(i, 1);

    removeStateArrayElement(app(), 'secretModalSecretKeyValuePairs', i, () => {
        for (var i in app().state.secretModalSecretKeyValuePairs) {
            var keyValuePair = app().state.secretModalSecretKeyValuePairs[i];
            app().secretModalTxtKey[i].current.value = keyValuePair.key;
            app().secretModalTxtValue[i].current.value = keyValuePair.value;
        }
    });

}

/**
 * Generates a random value in the secret modal.
 *
 * @param {number} i Key value pair index.
 */
export function secretModalGenerateRandomValue(i) {

    var generatedRandomSecret = randomSecret(app().state.secretModalGenerateRandomValueLength);

    setStateArrayElement(app(), 'secretModalSecretKeyValuePairs', i, {
        key: app().state.secretModalSecretKeyValuePairs[i].key,
        value: generatedRandomSecret.value,
        valueLength: generatedRandomSecret.length,
        valueStrength: generatedRandomSecret.strength
    }, () => {
        app().secretModalTxtValue[i].current.value = generatedRandomSecret.value;
        app().secretModalTxtValue[i].current.select();
    });

}

/**
 * @returns {object} The json object corresponding to the current state of the secret modal.
 */
export function buildSecretModalSecret() {

    var secret = {
        name: app().state.secretModalSecretName,
        values: []
    };

    for (var keyValuePair of app().state.secretModalSecretKeyValuePairs) {
        secret.values.push({
            key: keyValuePair.key,
            value: keyValuePair.value
        });
    }

    return secret;

}

/**
 * Computes the strength of a secret.
 *
 * @param {string} secret The secret.
 * @returns {number} The strength of the secret as a number between 0 and 100.
 */
export function secretStrength(secret) {

    if (!secret) return 0;

    var maxLength = properties.secrets.secretStrengthMaxLength;
    var value = secret.length > maxLength ? secret.substr(0, maxLength) : secret;

    return zxcvbn(value).score * 25;

}

/**
 * Generates a random secret.
 *
 * @param {number} length The secret length.
 * @returns {object} An object with the generated secret value, length and strength.
 */
export function randomSecret(length) {

    var charset = properties.secrets.randomSecretCharset;

    var value = "";
    for (var i = 0; i < length; i++)
        value += charset.charAt(randomInt(0, charset.length));

    return {
        value: value,
        length: value.length,
        strength: secretStrength(value)
    };

}

/**
 * Sorts an array of secrets.
 *
 * @param {array} secretsArray The array of secrets.
 */
export function sortSecrets(secretsArray) {

    secretsArray.sort((secret1, secret2) => {
        return secret1.value.name > secret2.value.name ? 1 : secret1.value.name < secret2.value.name ? -1 : 0;
    });

}

/**
 * Parses an array of secrets and decrypts its names and keys.
 *
 * @param {array} encryptedSecrets The array of secrets.
 * @param {(string|Uint8Array} [encryptedSymmetricKey] The secrets encrypted symmetric key. If not introduced, the one in each secret encyptedKey attribute will be used.
 * @param {(string|Uint8Array} [publicKeyToDecryptSymmetricKey] The public key to decrypt the secrets encrypted symmetric keys, as a base64 string on an Uint8Array. If not introduced, the current session keys one will be used.
 * @returns {array} The array of processed secrets.
 */
export function processSecrets(encryptedSecrets, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey) {

    var decryptedSecrets = [];

    for (var encryptedSecret of encryptedSecrets) {

        var secretValueDecryption = decryptSecret(JSON.parse(encryptedSecret.value), null, encryptedSymmetricKey || encryptedSecret.encryptedKey, publicKeyToDecryptSymmetricKey, true);
        if (!secretValueDecryption) return;

        decryptedSecrets.push({
            secretId: encryptedSecret.secretId,
            value: secretValueDecryption.decryptedSecret,
            encryptedKey: encryptedSecret.encryptedKey
        });

    }

    sortSecrets(decryptedSecrets);

    return decryptedSecrets;

}

/**
 * Encrypts a secret.
 *
 * @param {(string|Uint8Array|Array|Object)} secret The secret.
 * @param {(string|Uint8Array)} [symmetricKey] The symmetric key to encrypt the secret, as a base64 string or an Uint8Array. If this is not introduced, the encryptedSymmetricKey will be used.
 * @param {(string|Uint8Array)} [encryptedSymmetricKey] The encrypted symmetric key to encrypt the secret, as a base64 string or an Uint8Array. This will be decrypted and used in case no symmetricKey is introduced. If this is also not introduced, a random symmetric key will be generated.
 * @param {(string|Uint8Array} [publicKeyToDecryptSymmetricKey] The public key to decrypt the encrypted symmetric key, as a base64 string on an Uint8Array. If not introduced, the current session keys one will be used.
 * @param {(string|object)} [publicKeyToEncryptSymmetricKey] A public key to encrypt the symmetric key, as a base64 string on an Uint8Array, or an array of email+publicKey objects.
 * @returns {object} An object with the encrypted secret and the encrypted symmetric key for the current session keys and for the introduced public key(s) in publicKeyToEncryptSymmetricKey.
 */
export function encryptSecret(secret, symmetricKey, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey, publicKeyToEncryptSymmetricKey) {

    try {

        if (!symmetricKey) {
            if (encryptedSymmetricKey) {
                symmetricKey = decryptSymmetricKey(encryptedSymmetricKey, publicKeyToDecryptSymmetricKey);
            } else {
                symmetricKey = generateRandomSymmetricKey();
            }
        }

        return {
            encryptedSecret: encryptValue(secret, symmetricKey),
            encryptedSymmetricKeyForMe: encryptSymmetricKey(symmetricKey),
            encryptedSymmetricKeyForOthers: publicKeyToEncryptSymmetricKey ? encryptSymmetricKey(symmetricKey, publicKeyToEncryptSymmetricKey) : null
        };

    } catch (err) {
        messageModal(t('global.error'), t('global.error-occurred'));
        return null;
    }

}

/**
 * Decrypts an encrypted secret.
 *
 * @param {(string|Uint8Array|Array|Object)} encryptedSecret The encrypted secret.
 * @param {(string|Uint8Array)} [symmetricKey] The symmetric key to decrypt the secret, as a base64 string or an Uint8Array. If this is not introduced, the encryptedSymmetricKey will be used.
 * @param {(string|Uint8Array)} [encryptedSymmetricKey] The encrypted symmetric key to decrypt the secret, as a base64 string or an Uint8Array. This will be decrypted and used in case no symmetricKey is introduced.
 * @param {(string|Uint8Array)} [publicKeyToDecryptSymmetricKey] The public key to decrypt the symmetric key, as a base64 string on an Uint8Array. If not introduced, the current session keys one will be used.
 * @param {boolean} [onlyNamesAndKeys] True to decrypt only object attributes named "name" or "key". Default: true.
 * @returns {object} An object with the decrypted secret, and the symmetric key.
 */
export function decryptSecret(encryptedSecret, symmetricKey, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey, onlyNamesAndKeys) {

    try {

        if (!symmetricKey) {
            symmetricKey = decryptSymmetricKey(encryptedSymmetricKey, publicKeyToDecryptSymmetricKey);
        }

        return {
            decryptedSecret: decryptValue(encryptedSecret, symmetricKey, onlyNamesAndKeys)
        };

    } catch (err) {
        messageModal(t('global.error'), t('global.error-occurred'));
        return null;
    }

}

/**
 * Encrypts a symmetric key.
 *
 * @param {(string|Uint8Array)} [symmetricKey] The symmetric key. If this is not introduced, the encryptedSymmetricKey will be used.
 * @param {(string|Uint8Array)} [encryptedSymmetricKey] The encrypted symmetric key, as a base64 string or an Uint8Array. This will be decrypted and used in case no symmetricKey is introduced.
 * @param {(string|Uint8Array} [publicKeyToDecryptSymmetricKey] The public key to decrypt the encrypted symmetric key, as a base64 string on an Uint8Array. If not introduced, the current session keys one will be used.
 * @param {(string|object)} [publicKeyToEncryptSymmetricKey] A public key to encrypt the symmetric key, as a base64 string on an Uint8Array, or an array of email+publicKey objects.
 * @returns {(string|object)} The encrypted symmetric key for the introduced public key(s) in publicKeyToEncryptSymmetricKey.
 */
export function recryptSymmetricKey(symmetricKey, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey, publicKeyToEncryptSymmetricKey) {

    try {

        if (!symmetricKey) {
            symmetricKey = decryptSymmetricKey(encryptedSymmetricKey, publicKeyToDecryptSymmetricKey);
        }

        return encryptSymmetricKey(symmetricKey, publicKeyToEncryptSymmetricKey);

    } catch (err) {
        messageModal(t('global.error'), t('global.error-occurred'));
        return null;
    }

}

/**
 * Decrypts an encrypted secret and copies its value to the clipboard.
 *
 * @param {object} objectWithValue Object containing the attribute 'value' with the encrypted secret.
 * @param {(string|Uint8Array)} encryptedSymmetricKey The encrypted symmetric key to decrypt the secret, as a base64 string or an Uint8Array.
 * @param {(string|Uint8Array)} [publicKeyToDecryptSymmetricKey] The public key to decrypt the symmetric key, as a base64 string on an Uint8Array. If not introduced, the current session keys one will be used.
 */
export function copySecretValueToClipboard(objectWithValue, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey) {

    var valueDecryption = decryptSecret(objectWithValue.value, null, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey);
    if (!valueDecryption) return;

    copyToClipboard(valueDecryption.decryptedSecret);

}

/**
 * Decrypts a secret and shows its value.
 *
 * @param {object} objectWithValue Object containing the attribute 'value' with the encrypted secret. The decrypted secret will be placed at the attribute 'clearValue' of this object.
 * @param {(string|Uint8Array)} encryptedSymmetricKey The encrypted symmetric key to decrypt the secret, as a base64 string or an Uint8Array.
 * @param {(string|Uint8Array)} [publicKeyToDecryptSymmetricKey] The public key to decrypt the symmetric key, as a base64 string on an Uint8Array. If not introduced, the current session keys one will be used.
 * @param {React.Component} reactComponent The React component whose state must be set in order to show the secret value.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {number} arrayIndex The array index to set.
 * @param {any} element The element to set.
 */
export function showSecretValue(objectWithValue, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey, reactComponent, stateAttribute, arrayIndex, element) {

    var valueDecryption = decryptSecret(objectWithValue.value, null, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey);
    if (!valueDecryption) return;

    objectWithValue.clearValue = valueDecryption.decryptedSecret;
    setStateArrayElement(reactComponent, stateAttribute, arrayIndex, element);

}

/**
 * Hides a secret value.
 *
 * @param {object} objectWithValue Object containing the attribute 'clearValue' with the decrypted secret. This attribute will be deleted.
 * @param {React.Component} reactComponent The React component whose state must be set in order to hide the secret value.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {number} arrayIndex The array index to set.
 * @param {any} element The element to set.
 */
export function hideSecretValue(objectWithValue, reactComponent, stateAttribute, arrayIndex, element) {

    delete objectWithValue.clearValue;
    setStateArrayElement(reactComponent, stateAttribute, arrayIndex, element);

}

/**
 * Shortly shows a secret value.
 *
 * @param {object} objectWithValue Object containing the attribute 'value' with the encrypted secret. The decrypted secret will be shortly placed at the attribute 'clearValue' of this object.
 * @param {(string|Uint8Array)} encryptedSymmetricKey The encrypted symmetric key to decrypt the secret, as a base64 string or an Uint8Array.
 * @param {(string|Uint8Array)} [publicKeyToDecryptSymmetricKey] The public key to decrypt the symmetric key, as a base64 string on an Uint8Array. If not introduced, the current session keys one will be used.
 * @param {React.Component} reactComponent The React component whose state must be set in order to show the secret value.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {number} arrayIndex The array index to set.
 * @param {any} element The element to set.
 */
export function blinkSecretValue(objectWithValue, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey, reactComponent, stateAttribute, arrayIndex, element) {

    showSecretValue(objectWithValue, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey, reactComponent, stateAttribute, arrayIndex, element);

    setTimeout(() => {
        hideSecretValue(objectWithValue, reactComponent, stateAttribute, arrayIndex, element);
    }, properties.secrets.showSecretTime);

}

function encryptValue(value, symmetricKey) {

    if (value.constructor == ''.constructor) {

        return encrypt(value, symmetricKey);

    } else if (value.constructor == Uint8Array().constructor) {

        return encrypt(value, symmetricKey);

    } else if (value.constructor == [].constructor) {

        var encryptedValue = [];

        for (var item of value) {
            encryptedValue.push(encryptValue(item, symmetricKey));
        }

        return encryptedValue;

    } else if (value.constructor == {}.constructor) {

        var encryptedValue = {};

        for (var key in value) {
            encryptedValue[key] = encryptValue(value[key], symmetricKey);
        }

        return encryptedValue;

    }
}

function decryptValue(value, symmetricKey, onlyNamesAndKeys = true) {

    if (value.constructor == ''.constructor) {

        return decrypt(value, symmetricKey);

    } else if (value.constructor == Uint8Array().constructor) {

        return decrypt(value, symmetricKey);

    } else if (value.constructor == [].constructor) {

        var decryptedValue = [];

        for (var item of value) {
            if (!onlyNamesAndKeys || (item.constructor != ''.constructor && item.constructor != Uint8Array().constructor)) {
                decryptedValue.push(decryptValue(item, symmetricKey, onlyNamesAndKeys));
            } else {
                decryptedValue.push(item);
            }
        }

        return decryptedValue;

    } else if (value.constructor == {}.constructor) {

        var decryptedValue = {};

        for (var key in value) {
            if (!onlyNamesAndKeys || (value[key].constructor != ''.constructor && value[key].constructor != Uint8Array().constructor)
                || key === 'name' || key === 'key') {
                decryptedValue[key] = decryptValue(value[key], symmetricKey, onlyNamesAndKeys);
            } else {
                decryptedValue[key] = value[key];
            }
        }

        return decryptedValue;

    }

}

function encryptSymmetricKey(symmetricKey, publicKeyToEncryptSymmetricKey) {

    if (!publicKeyToEncryptSymmetricKey) {

        return encrypt(symmetricKey, getSymmetricKey(), 'base64');

    } else if (publicKeyToEncryptSymmetricKey.constructor == ''.constructor) {

        return encrypt(symmetricKey, getSymmetricKey(publicKeyToEncryptSymmetricKey), 'base64');

    } else if (publicKeyToEncryptSymmetricKey.constructor == Uint8Array().constructor) {

        return encrypt(symmetricKey, getSymmetricKey(publicKeyToEncryptSymmetricKey), 'base64');

    } else if (publicKeyToEncryptSymmetricKey.constructor == [].constructor) {

        var encryptedSymmetricKeys = [];

        for (var account of publicKeyToEncryptSymmetricKey) {
            encryptedSymmetricKeys.push({
                email: account.email,
                encryptedKey: encrypt(symmetricKey, getSymmetricKey(account.encryptionPublicKey), 'base64')
            });
        }

        return encryptedSymmetricKeys;

    }

}

function decryptSymmetricKey(encryptedSymmetricKey, publicKeyToDecryptSymmetricKey) {

    return decrypt(encryptedSymmetricKey, getSymmetricKey(publicKeyToDecryptSymmetricKey), 'base64', 'base64', 'base64');

}
