package br.com.rick.frederico.file.codec.coding;

import br.com.rick.frederico.file.codec.util.BitInputStream;
import br.com.rick.frederico.file.codec.util.BitOutputStream;

import java.io.*;

public class UnaryCodec extends Codec {

    public static String codecName() {
        return "unary";
    }

    public static int codecCode() {
        return 3;
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
            for (int i = 0; i < currentByte; i++) {
                bitOutputStream.write(0);
            }
            bitOutputStream.write(1);
            currentByte = inputStream.read();
        }
    }

    @Override
    protected void performDecoding(BitInputStream bitInputStream, OutputStream outputStream) throws IOException {
        int currentBit = bitInputStream.read();
        int byteValue = 0;
        while (currentBit != -1) {
            if (currentBit == 1) {
                outputStream.write(byteValue);
                byteValue = 0;
            } else {
                byteValue++;
            }
            currentBit = bitInputStream.read();
        }
    }

}
