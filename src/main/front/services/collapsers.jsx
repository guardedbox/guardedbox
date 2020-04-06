
/**
 * Loads the status of the collapsers of a React component, mixing them with the already existing ones in the state attribute 'collapsersOpen', and setting the non-existing ones to false (collapsed).
 *
 * @param {React.Component} reactComponent The React component.
 * @param {Array} items The items from which the collapsers ids will be read.
 * @param {string} idAttribute The name of the id attribute corresponding to the collapsers ids in the items.
 * @returns {object} The object of collapsers status by id, ready to set in the status of the React component, in the state attribute 'collapsersOpen'.
 */
export function loadCollapsersOpen(reactComponent, items, idAttribute) {

    var collapsersOpen = reactComponent.state.collapsersOpen;

    for (var item of items) {
        if (!(item[idAttribute] in collapsersOpen)) {
            collapsersOpen[item[idAttribute]] = false;
        }
    }

    return collapsersOpen;

}

/**
 * Toggles the status of a collapser of a React component, toggling the collapser id in the 'collapsersOpen' attribute of the state of the component.
 *
 * @param {React.Component} reactComponent The React component.
 * @param {string} id The collapser id.
 */
export function toggleCollapser(reactComponent, id) {

    var collapsersOpen = reactComponent.state.collapsersOpen;
    collapsersOpen[id] = !collapsersOpen[id];
    reactComponent.setState({ collapsers: collapsersOpen });

}

/**
 * Expands a collapser of a React component, setting the collapser id to true in the 'collapsersOpen' attribute of the state of the component.
 *
 * @param {React.Component} reactComponent The React component.
 * @param {string} id The collapser id.
 */
export function expandCollapser(reactComponent, id) {

    var collapsersOpen = reactComponent.state.collapsersOpen;
    collapsersOpen[id] = true;
    reactComponent.setState({ collapsers: collapsersOpen });

}

/**
 * Expands a collapser of a React component, setting the collapser id to false in the 'collapsersOpen' attribute of the state of the component.
 *
 * @param {React.Component} reactComponent The React component.
 * @param {string} id The collapser id.
 */
export function collapseCollapser(reactComponent, id) {

    var collapsersOpen = reactComponent.state.collapsersOpen;
    collapsersOpen[id] = false;
    reactComponent.setState({ collapsers: collapsersOpen });

}

/**
 * Expands all the collapsers of a React component, setting the collapsers ids to true in the 'collapsersOpen' attribute of the state of the component.
 *
 * @param {React.Component} reactComponent The React component.
 */
export function expandAllCollapsers(reactComponent) {

    var collapsersOpen = reactComponent.state.collapsersOpen;

    for (var collapser in collapsersOpen) {
        collapsersOpen[collapser] = true;
    }

    reactComponent.setState({
        collapsersOpen: collapsersOpen
    });

}

/**
 * Collapses all the collapsers of a React component, setting the collapsers ids to false in the 'collapsersOpen' attribute of the state of the component.
 *
 * @param {React.Component} reactComponent The React component.
 */
export function collapseAllCollapsers(reactComponent) {

    var collapsersOpen = reactComponent.state.collapsersOpen;

    for (var collapser in collapsersOpen) {
        collapsersOpen[collapser] = false;
    }

    reactComponent.setState({
        collapsersOpen: collapsersOpen
    });

}
