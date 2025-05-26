package com.Ankit.Score.Score.Config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    public static boolean verifySignature(String payload, String actualSignature, String secret) throws Exception {
        String generatedSignature = hmacSHA256(payload, secret);
         System.out.println("Generated Signature: " + generatedSignature);
         System.out.println("Actual Signature: " + actualSignature);
        return generatedSignature.equals(actualSignature);
    }

    private static String hmacSHA256(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());

        // convert to hex string (not base64)
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
