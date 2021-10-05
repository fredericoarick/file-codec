package br.com.rick.frederico.file.codec.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends FilterInputStream {

    private int currentByte;
    private int currentBitPosition;

    public BitInputStream(InputStream in) {
        super(in);
        currentByte = 0;
        currentBitPosition = 0;
    }

    @Override
    public int read() throws IOException {
        if (currentBitPosition == 0) {
            currentByte = super.read();
            currentBitPosition = 8;
        }
        if (currentByte == -1) {
            return -1;
        }
        int bit = BitWiseUtils.getBit(currentByte, currentBitPosition);
        currentBitPosition--;
        return bit;
    }





}
