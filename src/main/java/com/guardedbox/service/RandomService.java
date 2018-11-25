package com.guardedbox.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Random Utils Service.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class RandomService {

    /** Alphanumeric charset. */
    private static final char[] ALPHANUMERIC_CHARSET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    /** Random generator. */
    private final SecureRandom secureRandom;

    /**
     * Default Constructor.
     */
    public RandomService() {
        this.secureRandom = new SecureRandom();
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
