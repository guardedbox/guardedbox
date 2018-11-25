/**
 * Opens a modal message.
 * 
 * @param {string} header The message header.
 * @param {string} body The message body.
 * @param {function} exitCallback This function will be called when the modal message is closed.
 * 
 */
export function modalMessage(header, body, exitCallback) {

    window.views.app.setState({
        modalMessageActive: true,
        modalMessageHeader: header,
        modalMessageBody: body,
        modalMessageExitCallback: exitCallback
    });

}

/**
 * Opens a modal confirmation with two buttons: Yes and No.
 * 
 * @param {string} header The message header.
 * @param {string} body The message body.
 * @param {function} yesCallback This function will be called when the button Yes in clicked.
 * @param {function} noCallback This function will be called when the button No in clicked.
 * 
 */
export function modalConfirmation(header, body, yesCallback, noCallback) {

    window.views.app.setState({
        modalConfirmationActive: true,
        modalConfirmationHeader: header,
        modalConfirmationBody: body,
        modalConfirmationYesCallback: yesCallback,
        modalConfirmationNoCallback: noCallback
    });

}
