package com.guardedbox.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

/**
 * Random Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class RandomService {

    /** Alphanumeric charset. */
    private static final char[] ALPHANUMERIC_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    /** Random generator. */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a random boolean.
     *
     * @return The generated boolean.
     */
    public boolean randomBoolean() {

        return secureRandom.nextBoolean();

    }

    /**
     * Generates a random int.
     *
     * @return The generated int.
     */
    public int randomInt() {

        return secureRandom.nextInt();

    }

    /**
     * Generates a random int between min and max.
     *
     * @param min Minimum generated int.
     * @param max Maximum generated int.
     * @return The generated int.
     */
    public int randomInt(
            int min,
            int max) {

        int randomInt = randomInt();

        randomInt %= (max - min);
        if (randomInt < 0) {
            randomInt += (max - min);
        }

        randomInt += min;

        return randomInt;

    }

    /**
     * Generates a random long.
     *
     * @return The generated long.
     */
    public long randomLong() {

        return secureRandom.nextLong();

    }

    /**
     * Generates a random long between min and max.
     *
     * @param min Minimum generated long.
     * @param max Maximum generated long.
     * @return The generated long.
     */
    public long randomLong(
            long min,
            long max) {

        long randomLong = randomLong();

        randomLong %= (max - min);
        if (randomLong < 0) {
            randomLong += (max - min);
        }

        randomLong += min;

        return randomLong;

    }

    /**
     * Generates a random float between 0 and 1.
     *
     * @return The generated float.
     */
    public float randomFloat() {

        return secureRandom.nextFloat();

    }

    /**
     * Generates a random float between min and max.
     *
     * @param min Minimum generated float.
     * @param max Maximum generated float.
     * @return The generated float.
     */
    public float randomFloat(
            float min,
            float max) {

        return randomFloat() * (max - min) + min;

    }

    /**
     * Generates a random double between 0 and 1.
     *
     * @return The generated double.
     */
    public double randomDouble() {

        return secureRandom.nextDouble();

    }

    /**
     * Generates a random double between min and max.
     *
     * @param min Minimum generated double.
     * @param max Maximum generated double.
     * @return The generated double.
     */
    public double randomDouble(
            double min,
            double max) {

        return randomDouble() * (max - min) + min;

    }

    /**
     * Generates a random array of bytes of the introduced length.
     *
     * @param length The length of the array.
     * @return The generated array.
     */
    public byte[] randomBytes(
            int length) {

        byte[] randomBytes = new byte[length];

        secureRandom.nextBytes(randomBytes);

        return randomBytes;

    }

    /**
     * Generates a random array of bytes of the introduced length and encodes it to hexadecimal format.
     *
     * @param length The length of the array.
     * @return The generated hexadecimal String.
     */
    public String randomBytesHex(
            int length) {

        byte[] randomBytes = randomBytes(length);

        return new String(Hex.encode(randomBytes));

    }

    /**
     * Generates a random array of bytes of the introduced length and encodes it to base64 format.
     *
     * @param length The length of the array.
     * @return The generated base64 String.
     */
    public String randomBytesBase64(
            int length) {

        byte[] randomBytes = randomBytes(length);

        return Base64.getEncoder().encodeToString(randomBytes);

    }

    /**
     * Generates a random alphanumeric String of the introduced length.
     *
     * @param length The length of the String.
     * @return The generated String.
     */
    public String randomAlphanumericString(
            int length) {

        StringBuilder str = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            str.append(ALPHANUMERIC_CHARSET[secureRandom.nextInt(ALPHANUMERIC_CHARSET.length)]);
        }

        return str.toString();

    }

}
