package br.com.rick.frederico.file.codec.coding;

import br.com.rick.frederico.file.codec.error.CodecException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

public class CodecFactory {

    public static Codec getForEncoding(String codecName, Queue<String> additionalArgs) {
        if (GolombCodec.codecName().equals(codecName.toLowerCase())) {
            String golombDivider = additionalArgs.poll();
            if (golombDivider == null) {
                throw new CodecException("WARN: Missing arg for encoding");
            }
            return new GolombCodec(Integer.parseInt(golombDivider));
        } else if (EliasGamaCodec.codecName().equals(codecName.toLowerCase())) {
            return new EliasGamaCodec();
        } else if (FibonacciCodec.codecName().equals(codecName.toLowerCase())) {
            return new FibonacciCodec();
        } else if (UnaryCodec.codecName().equals(codecName.toLowerCase())) {
            return new UnaryCodec();
        } else if (DeltaCodec.codecName().equals(codecName.toLowerCase())) {
            return new DeltaCodec();
        }
        throw new CodecException("WARN: Encoding not supported");
    }

    public static Codec getForDecoding(InputStream inputStream) throws IOException {
        int codecCode = inputStream.read();
        int additionalArgs = inputStream.read();
        if (GolombCodec.codecCode() == codecCode) {
            return new GolombCodec(additionalArgs);
        } else if (EliasGamaCodec.codecCode() == codecCode) {
            return new EliasGamaCodec();
        } else if (FibonacciCodec.codecCode() == codecCode) {
            return new FibonacciCodec();
        } else if (UnaryCodec.codecCode() == codecCode) {
            return new UnaryCodec();
        } else if (DeltaCodec.codecCode() == codecCode) {
            return new DeltaCodec();
        }
        throw new CodecException("WARN: Encoding not supported");
    }

}
