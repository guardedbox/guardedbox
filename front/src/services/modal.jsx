import { getViewComponent } from 'services/view-components.jsx';

/**
 * Opens a modal message.
 * 
 * @param {string} header The modal message header.
 * @param {string} body The modal message body.
 * @param {function} [exitCallback] This function will be invoked when the modal message is closed.
 */
export function modalMessage(header, body, exitCallback) {

    getViewComponent('app').setState({
        modalMessageActive: true,
        modalMessageHeader: header,
        modalMessageBody: body,
        modalMessageExitCallback: exitCallback
    });

}

/**
 * Closes the modal message and invokes the exitCallback function introduced in the modalMessage method.
 */
export function closeModalMessage() {

    var app = getViewComponent('app');

    var callback = app.state.modalMessageExitCallback;

    app.setState({
        modalMessageActive: false,
        modalMessageHeader: '',
        modalMessageBody: '',
        modalMessageExitCallback: null
    }, callback);

}

/**
 * Opens a modal confirmation with two buttons: Yes and No.
 * 
 * @param {string} header The modal message header.
 * @param {string} body The modal message body.
 * @param {function} yesCallback This function will be invoked when the Yes button is clicked.
 * @param {function} [noCallback] This function will be invoked when the No button  is clicked, or when the confirmation modal is closed.
 */
export function modalConfirmation(header, body, yesCallback, noCallback) {

    getViewComponent('app').setState({
        modalConfirmationActive: true,
        modalConfirmationHeader: header,
        modalConfirmationBody: body,
        modalConfirmationYesCallback: yesCallback,
        modalConfirmationNoCallback: noCallback
    });

}

/**
 * Closes the modal confirmation and invokes the yesCallback or noCallback function introduced in the modalConfirmation method.
 * 
 * @param {string} button 'yes' to invoke the yesCallback function, 'no' to invoke the noCallback function.
 */
export function closeModalConfirmation(button) {

    var app = getViewComponent('app');

    var callback = null;
    if (button === 'yes')
        callback = app.state.modalConfirmationYesCallback;
    else if (button === 'no')
        callback = app.state.modalConfirmationNoCallback;

    app.setState({
        modalConfirmationActive: false,
        modalConfirmationHeader: '',
        modalConfirmationBody: '',
        modalConfirmationYesCallback: null,
        modalConfirmationNoCallback: null
    }, callback);

}
