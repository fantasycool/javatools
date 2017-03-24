package com.frio.tools.encryption;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

/**
 * Symetrical Entryption
 * Authenticated encryption mode of operation
 * GCM
 * When doing encryption, we can also get the MAC(Message Authenticated Code)
 * Created by frio on 17/3/23.
 */
public class AESGCMUtil {
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
            cipher.updateAAD(aad.getBytes());
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
            cipher.updateAAD(aad.getBytes());
            return new String(cipher.doFinal(encryptedBytes, 0, encryptedBytes.length - 16));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String privateKey = SymmetricalEncryptionUtil.generatePrivateKeyFor(128, "AES");
        String encryptedStr = encryptData("sadfsafads", Base64.decodeBase64(privateKey), "abc");
        System.out.println(encryptedStr);
        System.out.println(decryptData(encryptedStr, Base64.decodeBase64(privateKey), "abc"));
    }
}
