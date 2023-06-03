/*
 * RandomGenerator.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.system.command.util;

import java.util.Random;


/**
 * Random generator
 * 
 * @author patrick
 */
public final class RandomGenerator {
    
    /**
     * Constructor for RandomGenerator
     */
    private RandomGenerator() {
    }

    
    /**
     * Generate random words
     *
     * @param numberOfWords the number of words
     * @return the generated words
     */
    public static String[] generateRandomWords(int numberOfWords) {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for (int i = 0; i < numberOfWords; i++) {
            char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter words are boring)
            for (int j = 0; j < word.length; j++) {
                word[j] = (char) ('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }
    
    
    /**
     * Generate random words separated by space and random newlines
     *
     * @param len the max length
     * @return the generated content
     */
    public static String generateRandom(int len) {
        boolean newLine = true;
        StringBuilder result = new StringBuilder();
        String[] words = null;
        int i = 0;
        while (result.length() < len) {
            if (words == null || i == words.length) {
                words = generateRandomWords(40);
                i = 0;
            }

            if (!newLine && result.length() > 0) {
                result.append(" ");
            }
            result.append(words[i]);
            
            
            int num = (int)(Math.random() * ((10) + 1));
            if (i > 0 && num % i == 0) {
                result.append("\n");
                newLine = true;
            } else {
                newLine = false;
            }
            i++;
        }

        return result.toString().substring(0, len);
    }

}
