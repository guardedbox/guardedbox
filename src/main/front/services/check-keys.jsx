import { getViewComponent } from 'services/view-components.jsx';
import { rest } from 'services/rest.jsx';
import { Uint8Array } from 'services/crypto/Uint8Array.jsx';

/**
 * Opens a modal showing the public keys of an email.
 *
 * @param {string} email The email.
 */
export function openCheckKeysModal(email) {

    rest({
        method: 'get',
        url: '/api/accounts/public-keys',
        params: {
            'email': email
        },
        callback: (response) => {

            getViewComponent('app').setState({
                checkKeysModalOpen: true,
                checkKeysEmail: email,
                checkKeysEncryptionPublicKey: Uint8Array(response.encryptionPublicKey, 'base64').toString('hex'),
                checkKeysSigningPublicKey: Uint8Array(response.signingPublicKey, 'base64').toString('hex')
            });

        }
    });

}

/**
 * Closed the check keys modal.
 */
export function closeCheckKeysModal() {

    getViewComponent('app').setState({
        checkKeysModalOpen: false,
        checkKeysEmail: '',
        checkKeysEncryptionPublicKey: '',
        checkKeysSigningPublicKey: ''
    });

}
