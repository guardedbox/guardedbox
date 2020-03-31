import { app } from 'services/views.jsx';

/**
 * Opens the message modal.
 *
 * @param {string} header The message modal header.
 * @param {string} body The message modal body.
 * @param {function} [exitCallback] This function will be invoked when the message modal is closed.
 */
export function messageModal(header, body, exitCallback) {

    app().setState({
        messageModalActive: true,
        messageModalHeader: header,
        messageModalBody: body,
        messageModalExitCallback: exitCallback
    });

}

/**
 * Closes the message modal and invokes the exitCallback function introduced in the messageModal method.
 */
export function closeMessageModal() {

    var callback = app().state.messageModalExitCallback;

    app().setState({
        messageModalActive: false,
        messageModalHeader: '',
        messageModalBody: '',
        messageModalExitCallback: null
    }, callback);

}

/**
 * Opens the confirmation modal.
 *
 * @param {string} header The confirmation modal header.
 * @param {string} body The confirmation modal body.
 * @param {function} yesCallback This function will be invoked when the Yes button is clicked.
 * @param {function} [noCallback] This function will be invoked when the No button is clicked, or when the confirmation modal is closed.
 * @param {boolean} [lock] True not to allow to close the modal without clicking one of the buttons.
 */
export function confirmationModal(header, body, yesCallback, noCallback, lock = false) {

    app().setState({
        confirmationModalActive: true,
        confirmationModalHeader: header,
        confirmationModalBody: body,
        confirmationModalYesCallback: yesCallback,
        confirmationModalNoCallback: noCallback,
        confirmationModalLock: lock
    });

}

/**
 * Closes the confirmation modal and invokes the yesCallback or noCallback function introduced in the confirmationModal method.
 *
 * @param {string} button 'yes' to invoke the yesCallback function, 'no' to invoke the noCallback function.
 */
export function closeConfirmationModal(button) {

    var callback = null;
    if (button === 'yes')
        callback = app().state.confirmationModalYesCallback;
    else if (button === 'no')
        callback = app().state.confirmationModalNoCallback;

    app().setState({
        confirmationModalActive: false,
        confirmationModalHeader: '',
        confirmationModalBody: '',
        confirmationModalYesCallback: null,
        confirmationModalNoCallback: null,
        confirmationModalLock: false,
    }, callback);

}
