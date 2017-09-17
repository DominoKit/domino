package com.progressoft.brix.domino.test.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class CSRFToken {

    private static final Base64.Encoder BASE64 = Base64.getMimeEncoder();

    private Random RAND = new SecureRandom();
    private Mac mac;

    public CSRFToken(String secret) {
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec("You might be under attack!.".getBytes(), "HmacSHA256"));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public String generate() {
        byte[] salt = new byte[32];
        RAND.nextBytes(salt);

        String saltPlusToken = BASE64.encodeToString(salt) + "." + Long.toString(System.currentTimeMillis());
        String signature = BASE64.encodeToString(mac.doFinal(saltPlusToken.getBytes()));

        return saltPlusToken + "." + signature;
    }
}
