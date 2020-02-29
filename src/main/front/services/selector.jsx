/**
 * Selects the text in a DOM element.
 * 
 * @param {DomEl} domEl The DOM element.
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
 */
export function selectTableBodyCell(tableBody, rowIndex, colIndex) {

    var tr = tableBody.rows[rowIndex];
    var td = tr.cells[colIndex];

    select(td);

}

/**
 * Copies a string to the clipboard.
 * 
 * @param {string} str The string to copy.
 */
export function copyToClipboard(str) {

    if (!str) return;

    var selected = document.getSelection().rangeCount > 0 ? document.getSelection().getRangeAt(0) : false;

    var el = document.createElement('textarea');
    el.value = str;
    el.setAttribute('readonly', '');
    el.style.height = '0px';
    el.style.width = '0px';
    el.style.position = 'absolute';
    el.style.left = '-9999px';
    document.body.appendChild(el);
    el.select();
    document.execCommand('copy');
    document.body.removeChild(el);

    if (selected) {
        document.getSelection().removeAllRanges();
        document.getSelection().addRange(selected);
    }

}
