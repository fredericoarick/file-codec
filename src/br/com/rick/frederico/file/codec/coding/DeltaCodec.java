package br.com.rick.frederico.file.codec.coding;

import br.com.rick.frederico.file.codec.util.BitInputStream;
import br.com.rick.frederico.file.codec.util.BitOutputStream;
import br.com.rick.frederico.file.codec.util.BitWiseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DeltaCodec extends Codec {

    private static final int deltaBitSize = 8;

    public static String codecName() {
        return "delta";
    }

    public static int codecCode() {
        return 4;
    }

    @Override
    protected int code() {
        return codecCode();
    }

    @Override
    public int metaInfoByte() {
        return 0;
    }

    @Override
    protected void performEncoding(InputStream inputStream, BitOutputStream bitOutputStream) throws IOException {
        int currentByte = inputStream.read();

        if (currentByte == -1) {
            return;
        }

        // first byte
        for (int i = deltaBitSize; i > 0; i--) {
            int nextBit = BitWiseUtils.getBit(currentByte, i);
            bitOutputStream.write(nextBit);
        }
        int previousByte = currentByte;
        int delta = 0;

        currentByte = inputStream.read();
        while (currentByte != -1) {
            delta = currentByte - previousByte;
            if (delta == 0) {
                bitOutputStream.write(1);
            } else {
                bitOutputStream.write(0);
                if (delta > 0) {
                    bitOutputStream.write(0);
                } else {
                    delta = Math.abs(delta);
                    bitOutputStream.write(1);
                }
                for (int i = deltaBitSize; i > 0; i--) {
                    int nextBit = BitWiseUtils.getBit(delta, i);
                    bitOutputStream.write(nextBit);
                }
            }
            previousByte = currentByte;
            currentByte = inputStream.read();
        }
    }

    @Override
    protected void performDecoding(BitInputStream bitInputStream, OutputStream outputStream) throws IOException {
        int currentBit;
        int byteValue = 0;

        // decode first value
        for (int i = 8; i > 0; i--) {
            currentBit = bitInputStream.read();
            if (currentBit == -1) {
                return;
            }
            byteValue = BitWiseUtils.setBit(byteValue, i, currentBit);
        }
        outputStream.write(byteValue);

        currentBit = bitInputStream.read();
        // decode next bytes loop
        while (currentBit != -1) {
            if (currentBit == 1) {
                outputStream.write(byteValue);
                currentBit = bitInputStream.read();
                continue;
            }

            currentBit = bitInputStream.read();
            if (currentBit == -1) {
                return;
            }
            boolean isNegativeDelta = currentBit == 1;
            int delta = 0;

            for (int i = deltaBitSize; i > 0; i--) {
                currentBit = bitInputStream.read();
                if (currentBit == -1) {
                    return;
                }
                delta = BitWiseUtils.setBit(delta, i, currentBit);
            }

            if (isNegativeDelta) {
                byteValue -= delta;
            } else {
                byteValue += delta;
            }

            outputStream.write(byteValue);
            currentBit = bitInputStream.read();
        }
    }

}
