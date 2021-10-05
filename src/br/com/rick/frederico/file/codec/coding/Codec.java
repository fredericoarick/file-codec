package br.com.rick.frederico.file.codec.coding;

import br.com.rick.frederico.file.codec.util.BitInputStream;
import br.com.rick.frederico.file.codec.util.BitOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Codec {

    public abstract int metaInfoByte();

    protected abstract int code();

    protected abstract void performEncoding(InputStream inputStream, BitOutputStream bitOutputStream) throws IOException;

    protected abstract void performDecoding(BitInputStream bitInputStream, OutputStream outputStream) throws IOException;

    public byte[] encode(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream)) {
            addForEncodingHeader(bitOutputStream);
            performEncoding(inputStream, bitOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] decode(InputStream input) throws IOException {
        BitInputStream bitInputStream = new BitInputStream(input);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        performDecoding(bitInputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void addForEncodingHeader(BitOutputStream bitOutputStream) throws IOException {
        bitOutputStream.writeByte(this.code());
        bitOutputStream.writeByte(this.metaInfoByte());
    }

}
