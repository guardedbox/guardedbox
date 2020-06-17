import { app } from 'services/views.jsx';
import { t } from 'services/translation.jsx';
import { rest } from 'services/rest.jsx';
import { messageModal, confirmationModal } from 'services/modal.jsx';

/**
 * Opens the participants modal.
 *
 * @param {string} literals The participants modal literals.
 * @param {function} loadParticipantsFunction Function to load participants. Will receive functionsArg as first argument and must expect a callback function as second argument that expects the loaded accounts as first argument.
 * @param {function} addParticipantFunction Function to add a participant. Will receive functionsArg as first argument, the account to be added as second argument and must expect a callback function as third argument.
 * @param {function} removeParticipantFunction Function to remove a participant. Will receive functionsArg as first argument, the account to be added as second argument and must expect a callback function as third argument.
 * @param {any} functionsArg This argument will be passed to the functions as first argument.
 */
export function participantsModal(literals, loadParticipantsFunction, addParticipantFunction, removeParticipantFunction, functionsArg) {

    loadParticipantsFunction(functionsArg, (participants) => {

        sortAccounts(participants.accounts);
        if (participants.registrationPendingAccounts) sortAccounts(participants.registrationPendingAccounts);
        if (participants.exMembers) sortAccounts(participants.exMembers);

        app().setState({
            participantsModalActive: true,
            participantsModalLiterals: literals,
            participantsModalAccounts: participants.accounts,
            participantsModalRegistrationPendingAccounts: participants.registrationPendingAccounts,
            participantsModalExMembers: participants.exMembers,
            participantsModalEmail: '',
            participantsModalLoadParticipantsFunction: loadParticipantsFunction,
            participantsModalAddParticipantFunction: addParticipantFunction,
            participantsModalRemoveParticipantFunction: removeParticipantFunction,
            participantsModalFunctionsArg: functionsArg
        }, () => {

            setTimeout(() => {
                if (app().participantsModalTxtEmail.current) app().participantsModalTxtEmail.current.focus();
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
        participantsModalLiterals: '',
        participantsModalAccounts: [],
        participantsModalRegistrationPendingAccounts: [],
        participantsModalExMembers: [],
        participantsModalEmail: '',
        participantsModalLoadParticipantsFunction: null,
        participantsModalAddParticipantFunction: null,
        participantsModalRemoveParticipantFunction: null,
        participantsModalFunctionsArg: null
    }, callback);

}

/**
 * Loads the participants in the participants modal.
 *
 * @param {boolean} clearTxtEmail True to clear the email textbox.
 * @param {function} callback This function will be invoked when the participants are loaded.
 */
export function participantsModalLoadParticipants(clearTxtEmail, callback) {

    app().state.participantsModalLoadParticipantsFunction(app().state.participantsModalFunctionsArg, (participants) => {

        sortAccounts(participants.accounts);
        if (participants.registrationPendingAccounts) sortAccounts(participants.registrationPendingAccounts);
        if (participants.exMembers) sortAccounts(participants.exMembers);

        app().setState({
            participantsModalAccounts: participants.accounts,
            participantsModalRegistrationPendingAccounts: participants.registrationPendingAccounts,
            participantsModalExMembers: participants.exMembers,
            participantsModalEmail: clearTxtEmail ? '' : app().state.participantsModalEmail
        }, () => {

            if (clearTxtEmail) {
                app().participantsModalTxtEmail.current.value = '';
            }

            setTimeout(() => {
                if (app().participantsModalTxtEmail.current) app().participantsModalTxtEmail.current.focus();
            }, 500);

            if (callback) callback();

        });

    });

}

/**
 * Adds a participant to the participants modal.
 *
 * @param {object} account The account representing the participant to be added.
 */
export function participantsModalAddParticipant(account) {

    app().state.participantsModalAddParticipantFunction(app().state.participantsModalFunctionsArg, account, (callback) => {
        participantsModalLoadParticipants(true, callback);
    });

}

/**
 * Removes a participant from the participants modal.
 *
 * @param {object} account The account representing the participant to be removed.
 */
export function participantsModalRemoveParticipant(account) {

    app().state.participantsModalRemoveParticipantFunction(app().state.participantsModalFunctionsArg, account, (callback) => {
        participantsModalLoadParticipants(true, callback);
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

/**
 * Invites an email to register.
 *
 * @param {string} email The email.
 * @param {string} secretId A secret ID to create an invitation pending action.
 * @param {string} groupId A group ID to create an invitation pending action.
 * @param {boolean} reinvitation True to indicate that this invitation is a reinvitation.
 */
export function inviteEmail(email, secretId, groupId, reinvitation) {

    confirmationModal(
        t(reinvitation ? 'global.confirmation' : 'global.information'),
        t(reinvitation ? 'accounts.confirm-resend-invitation' : 'accounts.email-not-registered-invite', { email: email }),
        () => {

            rest({
                method: 'post',
                url: '/api/invitation-pending-action',
                body: {
                    receiverEmail: email,
                    secretId: secretId,
                    groupId: groupId
                },
                loadingChain: true,
                callback: (response) => {

                    participantsModalLoadParticipants(true);

                    rest({
                        method: 'post',
                        url: '/api/registrations',
                        body: {
                            email: email
                        },
                        loadingChained: true,
                        callback: (response) => {

                            messageModal(t('accounts.invitation-success-modal-title'), t('accounts.invitation-success-modal-body', { email: email }));

                        }
                    });

                }
            });

        }
    );

}
