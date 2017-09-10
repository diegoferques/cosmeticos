package com.cosmeticos.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Vinicius on 06/09/2017.
 */
@Service
public class RandomCode {

    private static final String elements = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }


    private static final String alphanum = elements;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomCode(int length, Random random, String symbols) {
        if (length < 1)
            throw new IllegalArgumentException("length < 1: " + length);
        if (symbols.length() < 2)
            throw new IllegalArgumentException("symbols < 2: " + symbols.length());
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomCode(int length, Random random) {
        this(length, random, alphanum);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomCode(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomCode() {
        this(4);
    }

}
