import { Buffer } from 'safe-buffer';

export const FILL_MODE_MOSAIC = 'mosaic';
export const FILL_MODE_LEFT = 'left';
export const FILL_MODE_RIGHT = 'right';

export function Uint8Array(arg1, arg2, arg3, arg4, arg5) {

    if (arg1 == null) {

        return Buffer.from([]);

    } else if (typeof arg1 === 'number') {

        var size = arg1;

        if (arg2 == null) {

            return Buffer.alloc(size);

        } else if (typeof arg2 === 'number') {

            var fill = arg2;
            return Buffer.alloc(size, fill);

        } else if (typeof arg2 === 'string') {

            var str = arg2;
            var encoding = arg3;
            var fillMode = arg4;

            var buffer = Buffer.from(str, encoding);
            return bufferAllocAndFill(size, buffer, fillMode);

        } else if (arg2 instanceof Array || arg2 instanceof Buffer) {

            var arrayOrBuffer = arg2;
            var fillMode = arg3;

            var buffer = Buffer.from(arrayOrBuffer);
            return bufferAllocAndFill(size, buffer, fillMode);

        } else if (arg2 instanceof ArrayBuffer) {

            var arrayBuffer = arg2;
            var byteOffset = arg3;
            var length = arg4;
            var fillMode = arg5;

            var buffer = Buffer.from(arrayBuffer, byteOffset, length);
            return bufferAllocAndFill(size, buffer, fillMode);

        }

    } else if (typeof arg1 === 'string') {

        var str = arg1;
        var encoding = arg2;

        if (encoding === 'hex' && str.length % 2) str = '0' + str; // Add a 0 before odd length hex strings.

        return Buffer.from(str, encoding);

    } else if (arg1 instanceof Array || arg1 instanceof Buffer) {

        var arrayOrBuffer = arg1;

        return Buffer.from(arrayOrBuffer);

    } else if (arg1 instanceof ArrayBuffer) {

        var arrayBuffer = arg1;
        var byteOffset = arg2;
        var length = arg3;

        return Buffer.from(arrayBuffer, byteOffset, length);

    }

}

function bufferAllocAndFill(size, fill, fillMode) {

    if (fillMode == null || fillMode === FILL_MODE_MOSAIC) {

        return Buffer.alloc(size, fill);

    } else if (fillMode === FILL_MODE_LEFT) {

        return Buffer.alloc(size).fill(fill, 0, fill.length);

    } else if (fillMode === FILL_MODE_RIGHT) {

        return Buffer.alloc(size).fill(fill, size - fill.length);

    }

}

export function concatenate() {

    var totalLength = 0;
    for (var arg of arguments) {
        totalLength += arg.length;
    }

    var output = Uint8Array(totalLength);
    var accumulatedLength = 0;
    for (var arg of arguments) {
        output.fill(arg, accumulatedLength, accumulatedLength += arg.length);
    }

    return output;

}

export function split() {

    var input = arguments[0].buffer;
    var output = new Array(arguments.length);

    var offset = 0;
    for (var i = 1; i <= arguments.length; i++) {
        output[i - 1] = Uint8Array(input, offset, arguments[i]);
        offset += arguments[i];
    }

    return output;

}
