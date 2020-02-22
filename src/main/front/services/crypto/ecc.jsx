import { ec as ECDH, eddsa as EdDSA } from 'elliptic';
import { Uint8Array, FILL_MODE_RIGHT } from 'services/crypto/Uint8Array.jsx';
import { randomBytes } from 'services/crypto/random.jsx';

/**
 * Generates an ECDH key pair.
 * 
 * @param {object} params The parameters.
 * @param {string} curve The ECDH curve name. Example: curve25519.
 * @param {(Uint8Array|string)} [privateKey] The ECDH private key. If not introduced, a random private key is used.
 * @param {string} [privateKeyFormat] The format of the private key, in case it is a string. Default: utf8.
 * @returns {object} An EcdhKeyPair object.
 */
export function generateEcdhKeyPair({ curve, privateKey, privateKeyFormat }) {

    var ecdh = new ECDH(curve);
    var keySize = ecdh.curve.p.toArray().length;
    var secretSize = keySize;

    if (privateKey) {
        var keyPair = ecdh.keyFromPrivate(Uint8Array(privateKey, privateKeyFormat));
    } else {
        var keyPair = ecdh.genKeyPair();
    }

    return new EcdhKeyPair(curve, keySize, secretSize, keyPair);

}

class EcdhKeyPair {

    constructor(curveName, keySize, secretSize, keyPair) {

        this._curveName = curveName;
        this._keySize = keySize;
        this._secretSize = secretSize;
        this._keyPair = keyPair;

    }

    /**
     * @param {string} [outputFormat] The format of the output public key. Default: Uint8Array.
     * @returns {(Uint8Array|string)} The public key corresponding to this ECDH key pair.
     */
    getPublicKey(outputFormat) {

        var publicKey = Uint8Array(this._keySize, this._keyPair.getPublic().encode(), FILL_MODE_RIGHT);
        return outputFormat ? publicKey.toString(outputFormat) : publicKey;

    }

    /**
     * @param {string} [outputFormat] The format of the output private key. Default: Uint8Array.
     * @returns {(Uint8Array|string)} The private key corresponding to this ECDH key pair.
     */
    getPrivateKey(outputFormat) {

        var privateKey = Uint8Array(this._keySize, this._keyPair.getPrivate().toArray(), FILL_MODE_RIGHT);
        return outputFormat ? privateKey.toString(outputFormat) : privateKey;

    }

    /**
     * Derives the shared secret between this ECDH private key and another ECDH public key.
     * 
     * @param {object} params The parameters.
     * @param {(Uint8Array|string)} publicKey The other ECDH public key.
     * @param {string} [publicKeyFormat] The format of the other ECDH public key, in case it is a string. Default: utf8.
     * @param {string} [outputFormat] The format of the output secret. Default: Uint8Array.
     * @returns {(Uint8Array|string)} The derived shared secret.
     */
    computeSecret({ publicKey, publicKeyFormat, outputFormat }) {

        var secret = Uint8Array(this._secretSize, this._keyPair.derive(new ECDH(this._curveName).keyFromPublic(Uint8Array(publicKey, publicKeyFormat)).getPublic()).toArray(), FILL_MODE_RIGHT);
        return outputFormat ? secret.toString(outputFormat) : secret;

    }

}

/**
 * Generates an EdDSA key pair.
 * 
 * @param {object} params The parameters.
 * @param {string} curve The EdDSA curve name. Example: ed25519.
 * @param {(Uint8Array|string)} [privateKey] The EdDSA private key. If not introduced, a random private key is used.
 * @param {string} [privateKeyFormat] The format of the private key, in case it is a string. Default: utf8.
 * @returns {object} An EddsaKeyPair object.
 */
export function generateEddsaKeyPair({ curve, privateKey, privateKeyFormat }) {

    var eddsa = new EdDSA(curve);
    var keySize = eddsa.curve.p.toArray().length;
    var signatureSize = eddsa.hash.outSize / 8;

    if (!privateKey) {
        privateKey = randomBytes(keySize);
    }

    var keyPair = eddsa.keyFromSecret(privateKey);

    return new EddsaKeyPair(curve, keySize, signatureSize, keyPair);

}

class EddsaKeyPair {

    constructor(curveName, keySize, signatureSize, keyPair) {

        this._curveName = curveName;
        this._keySize = keySize;
        this._signatureSize = signatureSize;
        this._keyPair = keyPair;

    }

    /**
     * @param {string} [outputFormat] The format of the output public key. Default: Uint8Array.
     * @returns {(Uint8Array|string)} The public key corresponding to this EdDSA key pair.
     */
    getPublicKey(outputFormat) {

        var publicKey = Uint8Array(this._keySize, this._keyPair.getPublic(), FILL_MODE_RIGHT);
        return outputFormat ? publicKey.toString(outputFormat) : publicKey;

    }

    /**
     * @param {string} [outputFormat] The format of the output private key. Default: Uint8Array.
     * @returns {(Uint8Array|string)} The private key corresponding to this EdDSA key pair.
     */
    getPrivateKey(outputFormat) {

        var privateKey = Uint8Array(this._keySize, this._keyPair.getSecret(), FILL_MODE_RIGHT);
        return outputFormat ? privateKey.toString(outputFormat) : privateKey;

    }

    /**
     * Signs a message with the private key corresponding to this EdDSA key pair.
     * 
     * @param {object} params The parameters.
     * @param {(Uint8Array|string)} params.input The message to sign.
     * @param {string} [params.inputFormat] The format of the message to sign, in case it is a string. Default: utf8.
     * @param {string} [params.outputFormat] The format of the output signature. Default: Uint8Array.
     * @returns {(Uint8Array|string)} The signature of the message.
     */
    sign({ input, inputFormat, outputFormat }) {

        var signature = Uint8Array(this._signatureSize, this._keyPair.sign(Uint8Array(input, inputFormat)).toBytes(), FILL_MODE_RIGHT);
        return outputFormat ? signature.toString(outputFormat) : signature;

    }

}
