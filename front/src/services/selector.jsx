/**
 * Selects the text in a DOM element.
 * 
 * @param {DomEl} domEl The DOM element.
 * 
 */
export function select(domEl) {

    setTimeout(() => {
        window.getSelection().removeAllRanges();
        var rangeObj = document.createRange();
        rangeObj.selectNodeContents(domEl);
        window.getSelection().addRange(rangeObj);
    }, 50);


}

/**
 * 
 * Selects the text in a table cell.
 * 
 * @param {tbody} tableBody The table body DOM element.
 * @param {number} rowIndex The row index of the cell.
 * @param {number} colIndex The column index of the cell.
 * 
 */
export function selectTableBodyCell(tableBody, rowIndex, colIndex) {

    var tr = tableBody.rows[rowIndex];
    var td = tr.cells[colIndex];

    select(td);

}
