import { app } from 'services/views.jsx';

/**
 * Opens the participants modal.
 *
 * @param {string} header The participants modal header.
 * @param {function} loadParticipantsFunction Function to load participants. Will receive functionsArg as first argument and must expect a callback function as second argument that expects the loaded accounts as first argument.
 * @param {function} addParticipantFunction Function to add a participant. Will receive functionsArg as first argument, the account to be added as second argument and must expect a callback function as third argument.
 * @param {function} removeParticipantFunction Function to remove a participant. Will receive functionsArg as first argument, the account to be added as second argument and must expect a callback function as third argument.
 * @param {any} functionsArg This argument will be passed to the functions as first argument.
 */
export function participantsModal(header, loadParticipantsFunction, addParticipantFunction, removeParticipantFunction, functionsArg) {

    loadParticipantsFunction(functionsArg, (accounts) => {

        sortAccounts(accounts);

        app().setState({
            participantsModalActive: true,
            participantsModalHeader: header,
            participantsModalAccounts: accounts,
            participantsModalEmail: '',
            participantsModalLoadParticipantsFunction: loadParticipantsFunction,
            participantsModalAddParticipantFunction: addParticipantFunction,
            participantsModalRemoveParticipantFunction: removeParticipantFunction,
            participantsModalFunctionsArg: functionsArg
        }, () => {

            setTimeout(() => {
                app().participantsModalTxtEmail.current.focus();
            }, 25);

        });

    });

}

/**
 * Closes the participants modal.
 *
 * @param {function} callback This function will be invoked when the participants modal is closed.
 */
export function closeParticipantsModal(callback) {

    app().setState({
        participantsModalActive: false,
        participantsModalHeader: '',
        participantsModalAccounts: [],
        participantsModalEmail: '',
        participantsModalLoadParticipantsFunction: null,
        participantsModalAddParticipantFunction: null,
        participantsModalRemoveParticipantFunction: null,
        participantsModalFunctionsArg: null
    }, callback);

}

/**
 * Adds a participant to the participants modal.
 *
 * @param {object} account The account representing the participant to be added.
 */
export function participantsModalAddParticipant(account) {

    app().state.participantsModalAddParticipantFunction(app().state.participantsModalFunctionsArg, account, () => {

        app().state.participantsModalLoadParticipantsFunction(app().state.participantsModalFunctionsArg, (accounts) => {

            sortAccounts(accounts);

            app().setState({
                participantsModalAccounts: accounts,
                participantsModalEmail: ''
            }, () => {

                app().participantsModalTxtEmail.current.value = '';

                setTimeout(() => {
                    app().participantsModalTxtEmail.current.focus();
                }, 25);

            });

        });

    });

}

/**
 * Removes a participant from the participants modal.
 *
 * @param {object} account The account representing the participant to be removed.
 */
export function participantsModalRemoveParticipant(account) {

    app().state.participantsModalRemoveParticipantFunction(app().state.participantsModalFunctionsArg, account, () => {

        app().state.participantsModalLoadParticipantsFunction(app().state.participantsModalFunctionsArg, (accounts) => {

            sortAccounts(accounts);

            app().setState({
                participantsModalAccounts: accounts
            }, () => {

                setTimeout(() => {
                    app().participantsModalTxtEmail.current.focus();
                }, 200);

            });

        });

    });

}

/**
 * Sorts an array of accounts.
 *
 * @param {array} accountsArray The array of accounts.
 */
export function sortAccounts(accountsArray) {

    accountsArray.sort((account1, account2) => {
        return account1.email > account2.email ? 1 : account1.email < account2.email ? -1 : 0;
    });

}
