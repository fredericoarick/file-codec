package br.com.rick.frederico.file.codec.coding;

import br.com.rick.frederico.file.codec.util.BitInputStream;
import br.com.rick.frederico.file.codec.util.BitOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FibonacciCodec extends Codec {

    private List<Integer> fibonacciSequence;

    public FibonacciCodec() {
        initializeFibonacciSequence();
    }

    public static String codecName() {
        return "fibonacci";
    }

    public static int codecCode() {
        return 2;
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
            currentByte += 1;
            List<Integer> bitList = new ArrayList(Collections.nCopies(fibonacciSequence.size(), -1));
            mapValueToFibonacciBits(currentByte, bitList);
            for (Integer bit : bitList) {
                if (bit == -1) {
                    break;
                }
                bitOutputStream.write(bit);
            }
            bitOutputStream.write(1);
            currentByte = inputStream.read();
        }
    }

    @Override
    protected void performDecoding(BitInputStream bitInputStream, OutputStream outputStream) throws IOException {
        int currentBit = bitInputStream.read();
        int currentBitPosition = 0;
        int previousBit = 0;
        int byteValue = 0;
        while (currentBit != -1) {
            if (currentBit == 1 && previousBit == 1) {
                outputStream.write(byteValue - 1);
                byteValue = 0;
                currentBitPosition = 0;
                previousBit = 0;
            } else {
                if (currentBit == 1) {
                    byteValue += fibonacciSequence.get(currentBitPosition);
                }
                currentBitPosition++;
                previousBit = currentBit;
            }
            currentBit = bitInputStream.read();
        }
    }


    private void initializeFibonacciSequence() {
        fibonacciSequence = Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .map(ints -> ints[0])
                .skip(2)
                .limit(13)
                .collect(Collectors.toList());
    }

    private void mapValueToFibonacciBits(int currentTerm, List<Integer> bitList) {
        for (int i = 0; i < fibonacciSequence.size(); i++) {
            if (fibonacciSequence.get(i) == currentTerm) {
                bitList.set(i, 1);
                break;
            } else if (fibonacciSequence.get(i) > currentTerm) {
                bitList.set(i - 1, 1);
                int nextTerm = currentTerm - fibonacciSequence.get(i - 1);
                mapValueToFibonacciBits(nextTerm, bitList);
                break;
            } else {
                bitList.set(i, 0);
            }
        }
    }

}
