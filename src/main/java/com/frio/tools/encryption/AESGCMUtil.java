package com.frio.tools.encryption;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

/**
 * Symetrical Entryption
 * Authenticated encryption mode of operation
 * GCM
 * When doing encryption, we can also get the MAC(Message Authenticated Code)
 * Created by frio on 17/3/23.
 */
public class AESGCMUtil {
    public static final int AES_KEY_SIZE = 128;
    public static final int GCM_NONCE_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    public static final String PROVIDER = "SunJCE";
    public static final String ALGORITHM = "AES/GCM/NoPadding";

    /**
     * use AES/GCM to encrypt data
     *
     * @param plainText
     * @param keys
     * @param aad
     * @return
     */
    public static String encryptData(String plainText, byte[] keys, String aad) {
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            byte[] iv = new byte[16];
            random.nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keys, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
            cipher.updateAAD(Base64.decodeBase64(aad));
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            byte[] resultData = new byte[encryptedBytes.length + 16];
            System.arraycopy(encryptedBytes, 0, resultData, 0, encryptedBytes.length);
            System.arraycopy(iv, 0, resultData, encryptedBytes.length, iv.length);
            return Base64.encodeBase64String(resultData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * decrypt AES/GCM encrypted data
     * @param encodedText
     * @param keys
     * @param aad
     * @return
     */
    public static String decryptData(String encodedText, byte[] keys, String aad) {
        try {
            byte[] encryptedBytes = Base64.decodeBase64(encodedText);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, encryptedBytes, encryptedBytes.length - 16, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keys, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            cipher.update(aad.getBytes());
            return new String(cipher.doFinal(encryptedBytes, 0, encryptedBytes.length - 16));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        int testNum = 0;

        byte[] input = "Hello AES_GCM World".getBytes();

        SecureRandom random = SecureRandom.getInstanceStrong();
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE, random);
        SecretKey key = keyGen.generateKey();
        System.out.println(key.getEncoded().length); // length is 16;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");

        final byte[] nonce = new byte[GCM_NONCE_LENGTH];
        random.nextBytes(nonce);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        System.out.println("tLength is:" + spec.getTLen());
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] aad = "Whatever I like".getBytes();
        cipher.updateAAD(aad);
        byte[] cipherText = cipher.doFinal(input);


        Cipher cipher1 = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
        cipher1.init(Cipher.DECRYPT_MODE, key, spec);

//        if(testNum == 1){
//            aad[1] ++;
//        }
        cipher1.updateAAD("Whatever I like".getBytes());
        if (testNum == 2) {
            cipherText[10]++;
        }
        if (testNum == 3) {
            cipherText[cipherText.length - 2]++;
        }
        try {
            byte[] plainText = cipher1.doFinal(cipherText);
            String text = new String(plainText);
            if (testNum != 0) {
                System.out.println("Test Failed: expected  AEADBadTagException not thrown!");
            } else {
                if (Arrays.equals(input, plainText)) {
                    System.out.println("Test passed, match!");
                } else {
                    System.out.println("Test failed: result mismatch!");
                    System.out.println(new String(plainText));
                }
            }
        } catch (AEADBadTagException e) {
            if (testNum == 0) {
                System.out.println("Test failed: unexpected ex" + e);
                e.printStackTrace();
            } else {
                System.out.println("Test passed!expected ex" + e);
            }
        }

    }
}
