package br.com.rick.frederico.file.codec.coding;

import br.com.rick.frederico.file.codec.util.BitInputStream;
import br.com.rick.frederico.file.codec.util.BitOutputStream;
import br.com.rick.frederico.file.codec.util.BitWiseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EliasGamaCodec extends Codec {

    public static String codecName() {
        return "eliasgama";
    }

    public static int codecCode() {
        return 1;
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
        while (currentByte != -1) {
            currentByte++;

            int exponent = (int) (Math.log(currentByte) / Math.log(2));
            int remainder = currentByte - (int) Math.pow(2, exponent);

            // exponent as unary prefix
            for (int i = 0; i < exponent; i++) {
                bitOutputStream.write(0);
            }

            // stopbit
            bitOutputStream.write(1);

            // remiander as binary sufix
            for (int i = exponent; i > 0; i--) {
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
            int exponent = 0;
            int remainder = 0;

            // reading exponent
            while (currentBit == 0) {
                exponent++;
                currentBit = bitInputStream.read();
                if (currentBit == -1) {
                    return;
                }
            }
            // reading remainder
            for (int i = exponent; i > 0; i--) {
                currentBit = bitInputStream.read();
                if (currentBit == -1) {
                    return;
                }
                remainder = BitWiseUtils.setBit(remainder, i, currentBit);
            }

            int newValue =  (int) Math.pow(2, exponent) + remainder - 1;
            outputStream.write(newValue);

            currentBit = bitInputStream.read();
        }
    }

}
