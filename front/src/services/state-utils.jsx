/**
 * Adds an element to an array in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {any} element The element to add.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function addElementToStateArray(reactComponent, stateAttribute, element, callback) {

    var arrayCopy = reactComponent.state[stateAttribute].slice();
    arrayCopy.push(element);

    reactComponent.setState({
        [stateAttribute]: arrayCopy
    }, callback);

}

/**
 * Adds all the elements in an array to an array in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {any[]} elements The elements to add.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function addElementsToStateArray(reactComponent, stateAttribute, elements, callback) {

    var arrayCopy = reactComponent.state[stateAttribute].slice();
    arrayCopy = arrayCopy.concat(elements);

    reactComponent.setState({
        [stateAttribute]: arrayCopy
    }, callback);

}

/**
 * Adds an element or all the elements in an array to multiple arrays in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {object[]} stateAttributesAndElements The array of names of the array attributes in the state of the component and elements or arrays of elements to add.
 * @param {string} stateAttributesAndElements.stateAttribute The name of the array attribute in the state of the component.
 * @param {any} [stateAttributesAndElements.element] The element to add.
 * @param {any[]} [stateAttributesAndElements.elements] The array of elements to add.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function addElementsToStateArrays(reactComponent, stateAttributesAndElements, callback) {

    var stateChange = {};
    for (var stateAttributeAndElements of stateAttributesAndElements) {
        var arrayCopy = reactComponent.state[stateAttributeAndElements.stateAttribute].slice();
        if (typeof stateAttributeAndElements.element != 'undefined') arrayCopy.push(stateAttributeAndElements.element);
        if (typeof stateAttributeAndElements.elements == 'array') arrayCopy = arrayCopy.concat(stateAttributeAndElements.elements);
        stateChange[stateAttributeAndElements.stateAttribute] = arrayCopy;
    }

    reactComponent.setState(stateChange, callback);

}

/**
 * Sets the element at an index in an array in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {number} arrayIndex The array index to set.
 * @param {any} element The element to set.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function setStateArrayElement(reactComponent, stateAttribute, arrayIndex, element, callback) {

    var arrayCopy = reactComponent.state[stateAttribute].slice();
    arrayCopy[arrayIndex] = element;

    reactComponent.setState({
        [stateAttribute]: arrayCopy
    }, callback);

}

/**
 * Sets the elements at multiple indexes in an array in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {object[]} arrayIndexesAndElements The array of array indexes and elements to set.
 * @param {number} arrayIndexesAndElements.index The array index to set.
 * @param {any} arrayIndexesAndElements.element The element to set.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function setStateArrayElements(reactComponent, stateAttribute, arrayIndexesAndElements, callback) {

    var arrayCopy = reactComponent.state[stateAttribute].slice();
    for (var arrayIndexAndElement of arrayIndexesAndElements) {
        arrayCopy[arrayIndexAndElement.index] = arrayIndexAndElement.element;
    }

    reactComponent.setState({
        [stateAttribute]: arrayCopy
    }, callback);

}

/**
 * Sets the element or elements at one or multiple indexes in multiple arrays in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {object[]} stateAttributesAndArrayIndexesAndElements The array of names of the array attributes in the state of the component and index and element or array of array indexes and elements to set.
 * @param {string} stateAttributesAndArrayIndexesAndElements.stateAttribute The name of the array attribute in the state of the component.
 * @param {number} [stateAttributesAndArrayIndexesAndElements.arrayIndex] The array index to set.
 * @param {any} [stateAttributesAndArrayIndexesAndElements.element] The element to set.
 * @param {object[]} [stateAttributesAndArrayIndexesAndElements.arrayIndexesAndElements] The array of array indexes and elements to set.
 * @param {number} stateAttributesAndArrayIndexesAndElements.arrayIndexesAndElements.arrayIndex The array index to set.
 * @param {any} [stateAttributesAndArrayIndexesAndElements.arrayIndexesAndElements.element] The element to set.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function setStateArraysElements(reactComponent, stateAttributesAndArrayIndexesAndElements, callback) {

    var stateChange = {};
    for (var stateAttributeAndArrayIndexesAndElements of stateAttributesAndArrayIndexesAndElements) {
        var arrayCopy = reactComponent.state[stateAttributeAndArrayIndexesAndElements.stateAttribute].slice();
        if (typeof stateAttributeAndArrayIndexesAndElements.arrayIndex == 'number' && stateAttributeAndArrayIndexesAndElements.element != 'undefined') {
            arrayCopy[stateAttributeAndArrayIndexesAndElements.arrayIndex] = stateAttributeAndArrayIndexesAndElements.element;
        }
        if (typeof stateAttributeAndArrayIndexesAndElements.arrayIndexesAndElements == 'array') {
            for (var arrayIndexAndElement of stateAttributeAndArrayIndexesAndElements.arrayIndexesAndElements) {
                arrayCopy[arrayIndexAndElement.arrayIndex] = arrayIndexAndElement.element;
            }
        }
        stateChange[stateAttributeAndArrayIndexesAndElements.stateAttribute] = arrayCopy;
    }

    reactComponent.setState(stateChange, callback);

}

/**
 * Removes the element at an index in an array in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {number} arrayIndex The array index to remove.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function removeStateArrayElement(reactComponent, stateAttribute, arrayIndex, callback) {

    var arrayCopy = reactComponent.state[stateAttribute].slice();
    arrayCopy.splice(arrayIndex, 1);

    reactComponent.setState({
        [stateAttribute]: arrayCopy
    }, callback);

}

/**
 * Removes the element at multiple indexes in an array in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {string} stateAttribute The name of the array attribute in the state of the component.
 * @param {number[]} arrayIndexes The array of array indexes to remove.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function removeStateArrayElements(reactComponent, stateAttribute, arrayIndexes, callback) {

    var arrayCopy = reactComponent.state[stateAttribute].slice();
    for (var arrayIndex of arrayIndexes) {
        arrayCopy.splice(arrayIndex, 1);
    }

    reactComponent.setState({
        [stateAttribute]: arrayCopy
    }, callback);

}

/**
 * Removes the elements at one or multiple indexes in multiple arrays in the state of a React component.
 * 
 * @param {React.Component} reactComponent The React component.
 * @param {object[]} stateAttributesAndArrayIndexes The array of names of array attributes and indexes or arrays of indexes to remove.
 * @param {string} stateAttributesAndArrayIndexes.stateAttribute The name of the array attribute in the state of the component.
 * @param {number} [stateAttributesAndArrayIndexes.arrayIndex] The array index to remove.
 * @param {number[]} [stateAttributesAndArrayIndexes.arrayIndexex] The array of array indexes to remove.
 * @param {function} [callback] A callback function that will be passed to the component setState call.
 */
export function removeStateArraysElements(reactComponent, stateAttributesAndArrayIndexes, callback) {

    var stateChange = {};
    for (var stateAttributeAndArrayIndexes of stateAttributesAndArrayIndexes) {
        var arrayCopy = reactComponent.state[stateAttributeAndArrayIndexes.stateAttribute].slice();
        if (typeof stateAttributeAndArrayIndexes.arrayIndex == 'number') {
            arrayCopy.splice(stateAttributeAndArrayIndexes.arrayIndex, 1);
        }
        if (typeof stateAttributeAndArrayIndexes.arrayIndexex == 'array') {
            for (var arrayIndex of stateAttributeAndArrayIndexes.arrayIndexex) {
                arrayCopy.splice(arrayIndex, 1);
            }
        }
        stateChange[stateAttributeAndArrayIndexes.stateAttribute] = arrayCopy;
    }

    reactComponent.setState(stateChange, callback);

}
