package br.com.rick.frederico.file.codec.util;

public class BitWiseUtils {

    private BitWiseUtils() {

    }

    public static int getBit(int byteValue, int position) {
        return (byteValue >> (position - 1)) & 0b1;
    }

    public static int setBit(int byteValue, int position, int value) {
        byteValue |= (value << (position - 1));
        return byteValue;
    }

}
