import { app } from 'services/views.jsx';
import { decryptSecret, encryptSecret } from 'services/secret-utils.jsx';
import { rest } from 'services/rest.jsx';

/**
 * Opens the group modal.
 *
 * @param {string} header The groups modal header.
 * @param {object} group A group that will be loaded in the group modal.
 * @param {function} [acceptCallback] This function will be invoked when the Accept button is clicked.
 */
export function groupModal(header, group, acceptCallback) {

    var groupName = group ? group.name : '';

    app().setState({
        groupModalActive: true,
        groupModalHeader: header,
        groupModalGroupName: groupName,
        groupModalOriginalGroup: group,
        groupModalAcceptCallback: acceptCallback
    }, () => {

        setTimeout(() => {
            app().groupModalTxtName.current.value = groupName;
            app().groupModalTxtName.current.focus();
        }, 25);

    });

}

/**
 * Closes the group modal.
 *
 * @param {function} callback This function will be invoked when the group modal is closed.
 */
export function closeGroupModal(callback) {

    app().setState({
        groupModalActive: false,
        groupModalHeader: '',
        groupModalGroupName: '',
        groupModalOriginalGroup: null,
        groupModalAcceptCallback: null,
    }, callback);

}

/**
 * @returns {object} The json object corresponding to the current state of the group modal.
 */
export function buildGroupModalSecret() {

    var group = {
        name: app().state.groupModalGroupName
    };

    return group;

}

/**
 * Sorts an array of groups.
 *
 * @param {array} groupsArray The array of groups.
 */
export function sortGroups(groupsArray) {

    groupsArray.sort((group1, group2) => {
        return group1.name > group2.name ? 1 : group1.name < group2.name ? -1 : 0;
    });

}

/**
 * Rotates the symmetric key of a group.
 *
 * @param {string} groupId The group id.
 * @param {object} editedGroup The new group data, in case it is being edited.
 * @param {function} callback This function will be invoked once the group symmetric key has been rotated.
 */
export function rotateGroupKey(groupId, editedGroup, callback) {

    rest({
        method: 'get',
        url: '/api/groups/{group-id}',
        pathVariables: {
            'group-id': groupId
        },
        loadingChained: true,
        loadingChain: true,
        callback: (response) => {

            var group = response;

            rest({
                method: 'get',
                url: '/api/groups/{group-id}/participants',
                pathVariables: {
                    'group-id': groupId
                },
                loadingChained: true,
                loadingChain: true,
                callback: (response) => {

                    var participants = response;

                    if (!editedGroup) {

                        var groupNameDecryption = decryptSecret(group.name, null, group.encryptedKey);
                        if (!groupNameDecryption) { notLoading(); return; };

                        editedGroup = {
                            name: groupNameDecryption.decryptedSecret
                        };

                    }

                    var groupEncryption = encryptSecret(editedGroup, null, null, null, participants);
                    if (!groupEncryption) { notLoading(); return; };

                    var secrets = [];
                    for (var groupSecret of group.secrets) {

                        var groupSecretDecryption = decryptSecret(JSON.parse(groupSecret.value), null, group.encryptedKey, null, false);
                        if (!groupSecretDecryption) { notLoading(); return; };

                        var groupSecretEncryption = encryptSecret(groupSecretDecryption.decryptedSecret, null, groupEncryption.encryptedSymmetricKeyForMe);
                        if (!groupSecretEncryption) { notLoading(); return; };

                        secrets.push({
                            secretId: groupSecret.secretId,
                            value: JSON.stringify(groupSecretEncryption.encryptedSecret)
                        });

                    }

                    rest({
                        method: 'post',
                        url: '/api/groups/{group-id}',
                        pathVariables: {
                            'group-id': groupId
                        },
                        body: {
                            name: groupEncryption.encryptedSecret.name,
                            encryptedKey: groupEncryption.encryptedSymmetricKeyForMe,
                            secrets: secrets,
                            participants: groupEncryption.encryptedSymmetricKeyForOthers
                        },
                        loadingChained: true,
                        loadingChain: true,
                        callback: (response) => {

                            if (callback) callback(groupEncryption.encryptedSymmetricKeyForMe);

                        }
                    });

                }
            });

        }
    });

}
