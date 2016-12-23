package com.vorontsov.Services;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SupportService {
    /**
     * Generates random String object 20 symbols length
     * @return random String 20 syblols length
     */
    public static String generateSaltString()
    {
        String symbols = "qwertyuiopasdfghjklzxcvbnm";
        StringBuilder salt = new StringBuilder();
        int saltLength = 20;

        for(int i=0; i < saltLength ; i++)
            salt.append(symbols.charAt((int)(Math.random() * symbols.length())));

        return salt.toString();
    }

    /**
     * Encodes String by MD5
     * @param input String to be encoded
     * @return String encoded by MD5
     */
    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
