package br.com.rick.frederico.file.codec.coding;

import br.com.rick.frederico.file.codec.util.BitInputStream;
import br.com.rick.frederico.file.codec.util.BitOutputStream;
import br.com.rick.frederico.file.codec.util.BitWiseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GolombCodec extends Codec {

    private int dividerValue;
    private int remainderSize;

    public GolombCodec(int dividerValue) {
        this.dividerValue = dividerValue;
        this.remainderSize = (int) (Math.log(dividerValue) / Math.log(2));
    }

    public static String codecName() {
        return "golomb";
    }

    public static int codecCode() {
        return 0;
    }

    @Override
    protected int code() {
        return codecCode();
    }

    @Override
    public int metaInfoByte() {
        return dividerValue;
    }

    @Override
    protected void performEncoding(InputStream inputStream, BitOutputStream bitOutputStream) throws IOException {
        int currentByte = inputStream.read();
        while (currentByte != -1) {

            int quotient = currentByte / dividerValue;
            int remainder = currentByte % dividerValue;

            // quotient as unary prefix
            for (int i = 0; i < quotient; i++) {
                bitOutputStream.write(0);
            }

            // stopbit
            bitOutputStream.write(1);

            // remainder as binary sufix
            for (int i = remainderSize; i > 0; i--) {
                int nextBit = BitWiseUtils.getBit(remainder, i);
                bitOutputStream.write(nextBit);
            }

            // next byte
            currentByte = inputStream.read();
        }
    }

    @Override
    protected void performDecoding(BitInputStream bitInputStream, OutputStream outputStream) throws IOException {
        int currentBit = bitInputStream.read();

        while (currentBit != -1) {
            int quotient = 0;
            int remainder = 0;

            // reading quotient
            while (currentBit == 0) {
                quotient++;
                currentBit = bitInputStream.read();
                if (currentBit == -1) {
                    return;
                }
            }
            // reading remainder
            for (int i = remainderSize; i > 0; i--) {
                currentBit = bitInputStream.read();
                if (currentBit == -1) {
                    return;
                }
                remainder = BitWiseUtils.setBit(remainder, i, currentBit);
            }

            int newValue = quotient * dividerValue + remainder;
            outputStream.write(newValue);

            currentBit = bitInputStream.read();
        }
    }

}
