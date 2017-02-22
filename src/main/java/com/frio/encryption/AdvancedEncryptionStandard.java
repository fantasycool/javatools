package com.frio.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;

import java.util.*;


public class AdvancedEncryptionStandard {
    //third party key
    public static final byte[] THIRD_PARTY_ENCRYPTION_KEY = new String("25727f30c9a81980").getBytes();
    //alihealth key
    public static final byte[] encryptionKey = Base64.decodeBase64("su5QdYC6oPwxCLn8G5G5WA==");

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, encryptionKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        return Base64.encodeBase64String(encryptedBytes);
    }

    public static String encrypt(String plainText, byte[] keys) throws Exception{
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, keys);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.encodeBase64String(encryptedBytes);
    }

    public static String decrypt(String encrypted) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, encryptionKey);
        byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

        return new String(plainBytes);
    }

    public static String decrypt(String encrypted, byte[] key) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key);
        byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));
        return new String(plainBytes);
    }


    private static Cipher getCipher(int cipherMode, byte[] key)
            throws Exception {
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(
                key, encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);
        return cipher;
    }
}