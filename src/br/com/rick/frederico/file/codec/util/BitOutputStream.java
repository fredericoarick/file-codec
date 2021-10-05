package br.com.rick.frederico.file.codec.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends FilterOutputStream {

    private int currentByte;
    private int currentBitPosition;

    public BitOutputStream(OutputStream out) {
        super(out);
        currentByte = 0;
        currentBitPosition = 8;
    }

    @Override
    public void write(int bit) throws IOException {
        bit = bit > 0 ? 1 : 0;
        currentByte = BitWiseUtils.setBit(currentByte, currentBitPosition, bit);
        currentBitPosition--;
        if (currentBitPosition == 0) {
            super.write(currentByte);
            currentByte = 0;
            currentBitPosition = 8;
        }
    }

    public void writeByte(int byteValue) throws IOException {
        super.write(byteValue);
    }

    @Override
    public void flush() throws IOException {
        if (currentBitPosition != 8) {
            super.write(currentByte);
            currentByte = 0;
            currentBitPosition = 8;
        }
        super.flush();
    }
}
