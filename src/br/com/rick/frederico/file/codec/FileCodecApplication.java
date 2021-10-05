package br.com.rick.frederico.file.codec;

import br.com.rick.frederico.file.codec.coding.*;
import br.com.rick.frederico.file.codec.coding.ecc.ErrorCorrectionCodec;
import br.com.rick.frederico.file.codec.error.CodecException;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class FileCodecApplication {

    public static void main(String[] args) {
        try {
            Queue<String> applicationArgs = new LinkedList<>(Arrays.asList(args));
            String action = applicationArgs.poll();
            if (action == null) {
                throw new CodecException("WARN: Missing operation");
            }
            switch (action.toLowerCase()) {
                case "encode":
                    encode(applicationArgs);
                    break;
                case "decode":
                    decode(applicationArgs);
                    break;
                case "encodeecc":
                    encodeECC(applicationArgs);
                    break;
                case "decodeecc":
                    decodeECC(applicationArgs);
                    break;
                default:
                    throw new CodecException("WARN: Missing operation");
            }
        } catch (CodecException exception) {
            System.out.println(exception.getMessage());
            System.out.println("Application execution interrupted");
            System.out.println();
        }
    }

    private static void encode(Queue<String> args) {
        String inputFilePath = args.poll();
        if (inputFilePath == null) {
            throw new CodecException("WARN: Missing input file");
        }

        String outputFilePath = args.poll();
        if (outputFilePath == null) {
            throw new CodecException("WARN: Missing output file");
        }

        String codecName = args.poll();
        if (codecName == null) {
            throw new CodecException("WARN: Encoding not especified");
        }

        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream inputStream = new FileInputStream(inputFile);
            OutputStream outputStream = new FileOutputStream(outputFile)) {
            Codec codec = CodecFactory.getForEncoding(codecName, args);
            byte[] encodedBytes = codec.encode(inputStream);
            outputStream.write(encodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decode(Queue<String> args) {
        String inputFilePath = args.poll();
        if (inputFilePath == null) {
            throw new CodecException("WARN: Missing input file");
        }

        String outputFilePath = args.poll();
        if (outputFilePath == null) {
            throw new CodecException("WARN: Missing output file");
        }

        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream inputStream = new FileInputStream(inputFile);
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            Codec codec = CodecFactory.getForDecoding(inputStream);
            byte[] decodedBytes = codec.decode(inputStream);
            outputStream.write(decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void encodeECC(Queue<String> args) {
        String inputFilePath = args.poll();
        if (inputFilePath == null) {
            throw new CodecException("WARN: Missing input file");
        }

        String outputFilePath = args.poll();
        if (outputFilePath == null) {
            throw new CodecException("WARN: Missing output file");
        }


        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream inputStream = new FileInputStream(inputFile);
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            ErrorCorrectionCodec errorCorrectionCodec = new ErrorCorrectionCodec();
            byte[] decodedBytes = errorCorrectionCodec.encode(inputStream);
            outputStream.write(decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decodeECC(Queue<String> args) {
        String inputFilePath = args.poll();
        if (inputFilePath == null) {
            throw new CodecException("WARN: Missing input file");
        }

        String outputFilePath = args.poll();
        if (outputFilePath == null) {
            throw new CodecException("WARN: Missing output file");
        }

        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream inputStream = new FileInputStream(inputFile);
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            ErrorCorrectionCodec errorCorrectionCodec = new ErrorCorrectionCodec();
            byte[] decodedBytes = errorCorrectionCodec.decode(inputStream);
            outputStream.write(decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
