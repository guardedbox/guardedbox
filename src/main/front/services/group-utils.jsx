import { app } from 'services/views.jsx';

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
