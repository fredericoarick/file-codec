package br.com.rick.frederico.file.codec.coding.ecc;

import br.com.rick.frederico.file.codec.error.CodecException;
import br.com.rick.frederico.file.codec.util.BitInputStream;
import br.com.rick.frederico.file.codec.util.BitOutputStream;
import br.com.rick.frederico.file.codec.util.BitWiseUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ErrorCorrectionCodec {

    public byte[] encode(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        encodeHeader(inputStream, byteArrayOutputStream);
        try (BitInputStream bitInputStream = new BitInputStream(inputStream);
             BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream)) {
            encodeFile(bitInputStream, bitOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] decode(InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        decodeHeader(input, byteArrayOutputStream);
        try (BitInputStream bitInputStream = new BitInputStream(input);
             BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream)) {
            decodeFile(bitInputStream, bitOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void encodeHeader(InputStream inputStream, OutputStream outputStream) throws IOException {
        int firstByte = inputStream.read();
        int secondByte = inputStream.read();

        int header = (firstByte << 8) | secondByte;
        int crc = performCRC8(header, 16);

        outputStream.write(firstByte);
        outputStream.write(secondByte);
        outputStream.write(crc);
    }

    private void encodeFile(BitInputStream bitInputStream, BitOutputStream bitOutputStream) throws IOException {
        while (true) {
            int firstBit = bitInputStream.read();
            if (firstBit == -1) {
                return;
            }
            int secondBit = bitInputStream.read();
            if (secondBit == -1) {
                return;
            }
            int thirdBit = bitInputStream.read();
            if (thirdBit == -1) {
                return;
            }
            int fourthBit = bitInputStream.read();
            if (fourthBit == -1) {
                return;
            }
            int firstParityBit =  firstBit ^ secondBit ^ thirdBit;
            int secondParityBit = secondBit ^ thirdBit ^ fourthBit;
            int thirdParityBit = firstBit ^ thirdBit ^ fourthBit;

            bitOutputStream.write(firstBit);
            bitOutputStream.write(secondBit);
            bitOutputStream.write(thirdBit);
            bitOutputStream.write(fourthBit);
            bitOutputStream.write(firstParityBit);
            bitOutputStream.write(secondParityBit);
            bitOutputStream.write(thirdParityBit);
        }
    }

    private void decodeHeader(InputStream inputStream, OutputStream outputStream) throws IOException {
        int firstByte = inputStream.read();
        int secondByte = inputStream.read();
        int crc = inputStream.read();

        int header = (firstByte << 8) | secondByte;
        if (crc != performCRC8(header, 16)) {
            throw new CodecException("WARN: Error detected on header CRC. Decoding not possible");
        }

        outputStream.write(firstByte);
        outputStream.write(secondByte);
    }

    private void decodeFile(BitInputStream bitInputStream, BitOutputStream bitOutputStream) throws IOException {
        while (true) {
            int firstBit = bitInputStream.read();
            if (firstBit == -1) {
                return;
            }
            int secondBit = bitInputStream.read();
            if (secondBit == -1) {
                return;
            }
            int thirdBit = bitInputStream.read();
            if (thirdBit == -1) {
                return;
            }
            int fourthBit = bitInputStream.read();
            if (fourthBit == -1) {
                return;
            }
            int firstParityBit = bitInputStream.read();
            if (firstParityBit == -1) {
                return;
            }
            int secondParityBit = bitInputStream.read();
            if (secondParityBit == -1) {
                return;
            }
            int thirdParityBit = bitInputStream.read();
            if (thirdParityBit == -1) {
                return;
            }

            int expectedFirstParityBit =  firstBit ^ secondBit ^ thirdBit;
            int expectedSecondParityBit = secondBit ^ thirdBit ^ fourthBit;
            int expectedThirdParityBit = firstBit ^ thirdBit ^ fourthBit;

            boolean problemOnFirstParityBit = expectedFirstParityBit != firstParityBit;
            boolean problemOnSecondParityBit = expectedSecondParityBit != secondParityBit;
            boolean problemOnThirdParityBit = expectedThirdParityBit != thirdParityBit;

            if (problemOnFirstParityBit && problemOnSecondParityBit && problemOnThirdParityBit) {
                thirdBit ^= 1;
                System.out.println("WARN: flipped bit detected while decoding hamming word. Bit corrected");
            } else if (problemOnFirstParityBit && problemOnSecondParityBit) {
                secondBit ^= 1;
                System.out.println("WARN: flipped bit detected while decoding hamming word. Bit corrected");
            } else if (problemOnFirstParityBit && problemOnThirdParityBit) {
                firstBit ^= 1;
                System.out.println("WARN: flipped bit detected while decoding hamming word. Bit corrected");
            } else if (problemOnSecondParityBit && problemOnThirdParityBit) {
                fourthBit ^= 1;
                System.out.println("WARN: flipped bit detected while decoding hamming word. Bit corrected");
            } else if (problemOnFirstParityBit || problemOnSecondParityBit || problemOnThirdParityBit) {
                System.out.println("WARN: flipped parity bit detected while decoding hamming word");
            }

            bitOutputStream.write(firstBit);
            bitOutputStream.write(secondBit);
            bitOutputStream.write(thirdBit);
            bitOutputStream.write(fourthBit);
        }
    }

    private int performCRC8(int data, int dataBitSize) {
        int divisor = 0b100000111 << (dataBitSize - 1);
        int result = data << 8;
        for (int i = dataBitSize; i > 0; i--) {
            if (1 == BitWiseUtils.getBit(result, i + 8)) {
                result ^= divisor;
            }
            divisor >>= 1;
        }
        return result;
    }

}
