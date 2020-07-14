import React from 'react';
import { app } from 'services/views.jsx';
import { addElementsToStateArrays, setStateArrayElement, setStateArraysElements, removeStateArraysElements } from 'services/state-utils.jsx';
import { generateRandomSymmetricKey, getSymmetricKey, encrypt, decrypt } from 'services/crypto/crypto.jsx';
import { Uint8Array } from 'services/crypto/Uint8Array.jsx';
import { copyToClipboard } from 'services/selector.jsx';
import taiPasswordStrength from 'tai-password-strength';
import { randomInt } from 'services/crypto/random.jsx';
import { t } from 'services/translation.jsx';
import { messageModal } from 'services/modal.jsx';
import properties from 'constants/properties.json';

var MAX_ENTROPY = 72;
var MAX_UNIQUE_CHARS = 13;
var SEQUENCE_CHARS_MAX_DISTANCE = 2;
var MIN_SEQUENCE_LENGTH = 3;
var ABC_SEQUENCE_MAP = {
    'a': 'zb',
    'b': 'ac',
    'c': 'bd',
    'd': 'ce',
    'e': 'df',
    'f': 'eg',
    'g': 'fh',
    'h': 'gi',
    'i': 'hj',
    'j': 'ik',
    'k': 'jl',
    'l': 'km',
    'm': 'lnñ',
    'n': 'mño',
    'ñ': 'no',
    'o': 'nñp',
    'p': 'oq',
    'q': 'pr',
    'r': 'qs',
    's': 'rt',
    't': 'su',
    'u': 'tv',
    'v': 'uw',
    'w': 'vx',
    'x': 'wy',
    'y': 'xz',
    'z': 'ya',
    '0': '91',
    '1': '02',
    '2': '13',
    '3': '24',
    '4': '35',
    '5': '46',
    '6': '57',
    '7': '68',
    '8': '79',
    '9': '80'
};
var QWERTY_SEQUENCE_MAP = {
    '`': '1p\'+´',
    'º': '1',
    '1': '`º2q',
    '2': '13w',
    '3': '24e',
    '4': '35r',
    '5': '46t',
    '6': '57y',
    '7': '68u',
    '8': '79i',
    '9': '80o',
    '0': '9p-\'',
    '-': '0=[.ñ',
    '=': '-])?p',
    '\'': '0¡`;[\\',
    '¡': '\'+',
    '~': '!',
    '!': '~@qª"',
    '@': '!#w',
    '#': '@$e',
    '$': '#%r·%',
    '%': '$^t&',
    '^': '%&y',
    '&': '^*u%/y',
    '*': '&(i',
    '(': '*)o/i',
    ')': '(_p=o',
    '_': ')+',
    '+': '_`¡ç',
    'ª': '!',
    '"': '!·w',
    '·': '"$e',
    '/': '&(u.;',
    '¿': '?',
    'q': '1wa',
    'w': 'q2es',
    'e': 'w3rd',
    'r': 'e4tf',
    't': 'r5yg',
    'y': 't6uh',
    'u': 'y7ij',
    'i': 'u8ok',
    'o': 'i9pl',
    'p': 'o0ñ[`',
    '[': 'p-]\'',
    ']': '[=#',
    'a': 'qsz',
    's': 'awdx',
    'd': 'sefc',
    'f': 'drgv',
    'g': 'fthb',
    'h': 'gyjn',
    'j': 'hukm',
    'k': 'jil,',
    'l': 'ko;.ñ',
    ';': 'lp\'/',
    '\\': '\']z',
    'ñ': 'lp´.',
    '´': 'ñ`ç',
    'ç': '´+',
    '<': 'z',
    'z': 'ax',
    'x': 'zsc',
    'c': 'xdv',
    'v': 'cfb',
    'b': 'vgn',
    'n': 'bhm',
    'm': 'nj,',
    ',': 'mk.',
    '.': ',l/'
};
var SEQUENCE_MAPS = [
    { map: ABC_SEQUENCE_MAP, onlyStrictSequences: false },
    { map: QWERTY_SEQUENCE_MAP, onlyStrictSequences: false }
]

var strengthTester = new taiPasswordStrength.PasswordStrength();
strengthTester.addCommonPasswords(taiPasswordStrength.commonPasswords);

var randomSecretCharsets = [];
for (var charset of properties.secrets.randomSecretCharsets) {
    randomSecretCharsets.push({
        name: charset.name,
        charset: charset.charset,
        active: true
    });
}

/**
 * Opens the secret modal.
 *
 * @param {string} header The secret modal header.
 * @param {object} secret A secret that will be loaded in the secret modal.
 * @param {function} [acceptCallback] This function will be invoked when the Accept button is clicked. It will receive the value of the secret in the secret modal, the originally loaded secret and acceptCallbackThirdArg as arguments.
 * @param {any} acceptCallbackThirdArg This argument will be passed to the function acceptCallback as third argument.
 */
export function secretModal(header, secret, acceptCallback, acceptCallbackThirdArg) {

    activateAllRandomSecretChatsets();

    if (secret) {

        try {

            var secretName = secret.value.name;
            var keyValuePairs = [];
            var showPasswordOptions = [];
            var generateRandomValueLength = [];
            var generateRandomValueDropdownOpen = [];
            var symmetricKey = decryptSymmetricKey(secret.encryptedKey);

            for (var keyValuePair of secret.value.values) {

                var clearValue = decryptValue(keyValuePair.value, symmetricKey);

                keyValuePairs.push({
                    key: keyValuePair.key,
                    value: clearValue,
                    valueLength: clearValue.length,
                    valueStrength: secretStrength(clearValue)
                });

                showPasswordOptions.push(false);
                generateRandomValueLength.push(0);
                generateRandomValueDropdownOpen.push(false);

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
            valueStrength: { strength: 0 }
        }];
        var showPasswordOptions = [false];
        var generateRandomValueLength = [0];
        var generateRandomValueDropdownOpen = [false];

    }

    app().secretModalTxtKey = [];
    app().secretModalTxtValue = [];
    app().secretModalSwitchShowPasswordOptions = [];
    app().secretModalTxtGenerateRandomValueLength = [];
    for (var i in keyValuePairs) {
        app().secretModalTxtKey.push(React.createRef());
        app().secretModalTxtValue.push(React.createRef());
        app().secretModalSwitchShowPasswordOptions.push(React.createRef());
        app().secretModalTxtGenerateRandomValueLength.push(React.createRef());
    }

    app().setState({
        secretModalActive: true,
        secretModalHeader: header,
        secretModalSecretName: secretName,
        secretModalSecretKeyValuePairs: keyValuePairs,
        secretModalShowPasswordOptions: showPasswordOptions,
        secretModalGenerateRandomValueLength: generateRandomValueLength,
        secretModalGenerateRandomValueDropdownOpen: generateRandomValueDropdownOpen,
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
    app().secretModalSwitchShowPasswordOptions = [];
    app().secretModalTxtGenerateRandomValueLength = [];

    app().setState({
        secretModalActive: false,
        secretModalHeader: '',
        secretModalSecretName: '',
        secretModalSecretKeyValuePairs: [],
        secretModalShowPasswordOptions: [],
        secretModalGenerateRandomValueLength: [],
        secretModalGenerateRandomValueDropdownOpen: [],
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
    app().secretModalSwitchShowPasswordOptions.push(React.createRef());
    app().secretModalTxtGenerateRandomValueLength.push(React.createRef());

    addElementsToStateArrays(
        app(),
        [
            {
                stateAttribute: 'secretModalSecretKeyValuePairs',
                element: { key: '', value: '', valueLength: 0, valueStrength: { strength: 0 } }
            },
            {
                stateAttribute: 'secretModalShowPasswordOptions',
                element: false
            },
            {
                stateAttribute: 'secretModalGenerateRandomValueLength',
                element: 0
            }
        ],
        () => {
            setTimeout(() => {
                secretModalTxtKeyRef.current.focus();
            }, 25);
        });

}

/**
 * Removes a key value pair from the secret modal.
 *
 * @param {number} k Key value pair index.
 */
export function secretModalRemoveKeyValuePair(k) {

    app().secretModalTxtKey.splice(k, 1);
    app().secretModalTxtValue.splice(k, 1);
    app().secretModalSwitchShowPasswordOptions.splice(k, 1);
    app().secretModalTxtGenerateRandomValueLength.splice(k, 1);

    removeStateArraysElements(
        app(),
        [
            {
                stateAttribute: 'secretModalSecretKeyValuePairs',
                arrayIndex: k
            },
            {
                stateAttribute: 'secretModalShowPasswordOptions',
                arrayIndex: k
            },
            {
                stateAttribute: 'secretModalGenerateRandomValueLength',
                arrayIndex: k
            }
        ],
        () => {
            for (var i in app().state.secretModalSecretKeyValuePairs) {
                app().secretModalTxtKey[i].current.value = app().state.secretModalSecretKeyValuePairs[i].key;
                app().secretModalTxtValue[i].current.value = app().state.secretModalSecretKeyValuePairs[i].value;
                app().secretModalSwitchShowPasswordOptions[i].current.checked = app().state.secretModalShowPasswordOptions[i];
                app().secretModalTxtGenerateRandomValueLength[i].current.value = app().state.secretModalGenerateRandomValueLength[i];
            }
        });

}

/**
 * Moves a key value pair one position up in the secret modal.
 *
 * @param {number} k Current key value pair index.
 */
export function secretModalMoveKeyValuePairUp(k) {

    setStateArraysElements(
        app(),
        [
            {
                stateAttribute: 'secretModalSecretKeyValuePairs',
                arrayIndexesAndElements: [
                    { arrayIndex: k - 1, element: app().state.secretModalSecretKeyValuePairs[k] },
                    { arrayIndex: k, element: app().state.secretModalSecretKeyValuePairs[k - 1] }
                ]
            },
            {
                stateAttribute: 'secretModalShowPasswordOptions',
                arrayIndexesAndElements: [
                    { arrayIndex: k - 1, element: app().state.secretModalShowPasswordOptions[k] },
                    { arrayIndex: k, element: app().state.secretModalShowPasswordOptions[k - 1] }
                ]
            },
            {
                stateAttribute: 'secretModalGenerateRandomValueLength',
                arrayIndexesAndElements: [
                    { arrayIndex: k - 1, element: app().state.secretModalGenerateRandomValueLength[k] },
                    { arrayIndex: k, element: app().state.secretModalGenerateRandomValueLength[k - 1] }
                ]
            }
        ],
        () => {
            [app().secretModalTxtKey[k - 1].current.value, app().secretModalTxtKey[k].current.value] = [app().secretModalTxtKey[k].current.value, app().secretModalTxtKey[k - 1].current.value];
            [app().secretModalTxtValue[k - 1].current.value, app().secretModalTxtValue[k].current.value] = [app().secretModalTxtValue[k].current.value, app().secretModalTxtValue[k - 1].current.value];
            [app().secretModalSwitchShowPasswordOptions[k - 1].current.checked, app().secretModalSwitchShowPasswordOptions[k].current.checked] = [app().secretModalSwitchShowPasswordOptions[k].current.checked, app().secretModalSwitchShowPasswordOptions[k - 1].current.checked];
            [app().secretModalTxtGenerateRandomValueLength[k - 1].current.value, app().secretModalTxtGenerateRandomValueLength[k].current.value] = [app().secretModalTxtGenerateRandomValueLength[k].current.value, app().secretModalTxtGenerateRandomValueLength[k - 1].current.value];
        });

}

/**
 * Moves a key value pair one position down in the secret modal.
 *
 * @param {number} k Current key value pair index.
 */
export function secretModalMoveKeyValuePairDown(k) {

    setStateArraysElements(
        app(),
        [
            {
                stateAttribute: 'secretModalSecretKeyValuePairs',
                arrayIndexesAndElements: [
                    { arrayIndex: k + 1, element: app().state.secretModalSecretKeyValuePairs[k] },
                    { arrayIndex: k, element: app().state.secretModalSecretKeyValuePairs[k + 1] }
                ]
            },
            {
                stateAttribute: 'secretModalShowPasswordOptions',
                arrayIndexesAndElements: [
                    { arrayIndex: k + 1, element: app().state.secretModalShowPasswordOptions[k] },
                    { arrayIndex: k, element: app().state.secretModalShowPasswordOptions[k + 1] }
                ]
            },
            {
                stateAttribute: 'secretModalGenerateRandomValueLength',
                arrayIndexesAndElements: [
                    { arrayIndex: k + 1, element: app().state.secretModalGenerateRandomValueLength[k] },
                    { arrayIndex: k, element: app().state.secretModalGenerateRandomValueLength[k + 1] }
                ]
            }
        ],
        () => {
            [app().secretModalTxtKey[k + 1].current.value, app().secretModalTxtKey[k].current.value] = [app().secretModalTxtKey[k].current.value, app().secretModalTxtKey[k + 1].current.value];
            [app().secretModalTxtValue[k + 1].current.value, app().secretModalTxtValue[k].current.value] = [app().secretModalTxtValue[k].current.value, app().secretModalTxtValue[k + 1].current.value];
            [app().secretModalSwitchShowPasswordOptions[k + 1].current.checked, app().secretModalSwitchShowPasswordOptions[k].current.checked] = [app().secretModalSwitchShowPasswordOptions[k].current.checked, app().secretModalSwitchShowPasswordOptions[k + 1].current.checked];
            [app().secretModalTxtGenerateRandomValueLength[k + 1].current.value, app().secretModalTxtGenerateRandomValueLength[k].current.value] = [app().secretModalTxtGenerateRandomValueLength[k].current.value, app().secretModalTxtGenerateRandomValueLength[k + 1].current.value];
        });

}

/**
 * Generates a random value in the secret modal.
 *
 * @param {number} k Key value pair index.
 */
export function secretModalGenerateRandomValue(k) {

    var generatedRandomSecret = randomSecret(app().state.secretModalGenerateRandomValueLength[k]);

    setStateArrayElement(app(), 'secretModalSecretKeyValuePairs', k, {
        key: app().state.secretModalSecretKeyValuePairs[k].key,
        value: generatedRandomSecret.value,
        valueLength: generatedRandomSecret.length,
        valueStrength: generatedRandomSecret.strength
    }, () => {
        app().secretModalTxtValue[k].current.value = generatedRandomSecret.value;
        app().secretModalTxtValue[k].current.select();
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

    if (!secret) return { strength: 0 };

    var secretResult = strengthTester.check(secret);
    var sequenceTreatedSecret = treatSecretSequences(secret);
    var sequenceTreatedSecretResult = strengthTester.check(sequenceTreatedSecret);
    var uniqueCharacters = new Set(sequenceTreatedSecret).size;

    return {
        commonPassword: secretResult.commonPassword,
        sequenceTreatedSecret: sequenceTreatedSecret,
        shannonEntropyBits: sequenceTreatedSecretResult.shannonEntropyBits,
        uniqueCharacters: uniqueCharacters,
        strength: secretResult.commonPassword ? 0 : (Math.floor(100 *
            (Math.min(sequenceTreatedSecretResult.shannonEntropyBits, MAX_ENTROPY) / MAX_ENTROPY) *
            (Math.min(uniqueCharacters, MAX_UNIQUE_CHARS) / MAX_UNIQUE_CHARS)))
    };

}

function treatSecretSequences(secret) {

    var sequencesSubstitutions = {};

    for (var sequenceMap of SEQUENCE_MAPS) {

        for (var i = 0; i < secret.length; i++) {

            var sequenceLength = 1;
            var firstSequenceChar = secret.charAt(i);
            var lastSequenceChar = firstSequenceChar;
            var distanceFromLastSequenceChar = 0;
            var sequenceGrowingSign = 0;
            var sequenceSubstitutions = { [i]: firstSequenceChar };

            for (var j = i + 1; j < secret.length; j++) {

                var char = secret.charAt(j);
                var asciiDiff = lastSequenceChar.toLowerCase().charCodeAt(0) - char.toLowerCase().charCodeAt(0);

                if ((lastSequenceChar.toLowerCase() == char.toLowerCase()) ||
                    (sequenceMap.map[lastSequenceChar.toLowerCase()] && sequenceMap.map[lastSequenceChar.toLowerCase()].includes(char.toLowerCase()) &&
                        (!sequenceMap.onlyStrictSequences || asciiDiff * sequenceGrowingSign >= 0))) {

                    sequenceLength++;
                    lastSequenceChar = char;
                    distanceFromLastSequenceChar = 0;
                    if (sequenceGrowingSign == 0) sequenceGrowingSign = asciiDiff;

                    if (sequenceLength < MIN_SEQUENCE_LENGTH) {
                        sequenceSubstitutions[j] = char;
                    } else {
                        sequenceSubstitutions[j] = firstSequenceChar;
                    }

                } else {

                    distanceFromLastSequenceChar++;

                }

                if (distanceFromLastSequenceChar == SEQUENCE_CHARS_MAX_DISTANCE) {
                    break;
                }

            }

            if (sequenceLength >= MIN_SEQUENCE_LENGTH) {
                for (var j in sequenceSubstitutions) {
                    if (!(j in sequencesSubstitutions)) sequencesSubstitutions[j] = sequenceSubstitutions[j];
                }
            }

        }

    }

    var sequenceTreatedSecret = '';
    for (var i = 0; i < secret.length; i++) {
        if (i in sequencesSubstitutions) {
            sequenceTreatedSecret += sequencesSubstitutions[i];
        } else {
            sequenceTreatedSecret += secret.charAt(i);
        }
    }

    return sequenceTreatedSecret;

}

/**
 * @param {string} charsetName A random secret generation charset name.
 * @returns {boolean} Boolean indicating if the charset is active.
 */
export function isRandomSecretCharsetActive(charsetName) {

    for (var charset of randomSecretCharsets) {
        if (charset.name == charsetName) {
            return charset.active;
        }
    }

}

/**
 * @param {string} charsetName A random secret generation charset name.
 * @returns {boolean} Boolean indicating if the charset is the only one active.
 */
export function isOnlyRandomSecretCharsetActive(charsetName) {

    var othersInactive = true;

    for (var charset of randomSecretCharsets) {
        if (charset.name == charsetName) {
            var active = charset.active;
        } else {
            othersInactive &= !charset.active;
        }
    }

    return active && othersInactive;

}

/**
 * Toggles (activates or deactivates) one of the random secret generation charsets.
 *
 * @param {string} charsetName The charset name.
 */
export function toggleRandomSecretCharset(charsetName) {

    for (var charset of randomSecretCharsets) {
        if (charset.name == charsetName) {
            charset.active = !charset.active;
        }
    }

}

/**
 * Activates all the random secret generation charsets.
 */
export function activateAllRandomSecretChatsets() {

    for (var charset of randomSecretCharsets) {
        charset.active = true;
    }

}

/**
 * Generates a random secret using the active charsets.
 *
 * @param {number} length The secret length.
 * @returns {object} An object with the generated secret value, length and strength.
 */
export function randomSecret(length) {

    var totalCharset = '';
    for (var charset of randomSecretCharsets) {
        if (charset.active) totalCharset += charset.charset;
    }

    var iterations = length < 2 ? 1 : (length < 20 ? 5 * length : (length < 40 ? 100 : (length < 100 ? 100 - length : 1)));
    var bestStrength = -1;
    var ret = null;

    for (var i = 0; i < iterations; i++) {

        var value = "";
        for (var j = 0; j < length; j++)
            value += totalCharset.charAt(randomInt(0, totalCharset.length));

        var iteration = {
            value: value,
            length: value.length,
            strength: secretStrength(value)
        };

        if (iteration.strength.strength > bestStrength) {
            ret = iteration;
            bestStrength = iteration.strength.strength;
        }

    }

    return ret;

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
            encryptedKey: encryptedSecret.encryptedKey,
            numberOfSharings: encryptedSecret.numberOfSharings,
            numberOfExMembers: encryptedSecret.numberOfExMembers
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
 * @param {React.Component} reactComponent The React component whose state must be set in order to show the success of the action.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {number} arrayIndex The array index to set.
 * @param {any} element The element to set.
 */
export function copySecretValueToClipboard(objectWithValue, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey, reactComponent, stateAttribute, arrayIndex, element) {

    var valueDecryption = decryptSecret(objectWithValue.value, null, encryptedSymmetricKey, publicKeyToDecryptSymmetricKey);
    if (!valueDecryption) return;

    copyToClipboard(valueDecryption.decryptedSecret);

    if (reactComponent) {
        objectWithValue.copied = true;
        setStateArrayElement(reactComponent, stateAttribute, arrayIndex, element);
        setTimeout(() => {
            delete objectWithValue.copied;
            setStateArrayElement(reactComponent, stateAttribute, arrayIndex, element);
        }, properties.general.showSuccessTime);
    }

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

/**
 * Hides all the secret values in an attribute of the state of a React component.
 *
 * @param {React.Component} reactComponent The React component.
 * @param {string} stateAttribute The name of the attribute in the state of the component.
 */
export function hideSecretsValues(reactComponent, stateAttribute) {

    var objectsWithValues = reactComponent.state[stateAttribute];
    deleteClearValues(objectsWithValues);
    reactComponent.setState({ [stateAttribute]: objectsWithValues });

}

function deleteClearValues(objectsWithValues) {

    if (!objectsWithValues) {

        return;

    } else if (objectsWithValues.constructor == {}.constructor) {

        delete objectsWithValues.clearValue;

        for (var key in objectsWithValues) {
            deleteClearValues(objectsWithValues[key]);
        }

    } else if (objectsWithValues.constructor == [].constructor) {

        for (var item of objectsWithValues) {
            deleteClearValues(item);
        }

    }

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

    } else {

        return encrypt(value.toString(), symmetricKey);

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

    } else {

        return decrypt(value.toString(), symmetricKey);

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
