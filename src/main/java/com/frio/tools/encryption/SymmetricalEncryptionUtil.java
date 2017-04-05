package com.frio.tools.encryption;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;


/**
 * Symmetrical encryption need two things to encrypt messages
 * 1. private key
 * 2. IV
 * Because when symmetrical encryption is doing encryption, it cuts message to a lot of block.
 * then it use one block to XOR it's previous entrypted block messsage to get a new entrypted block.
 * But the problem is the first block doesn't have a previous block, so we have to create 0 the block
 * for the first block.
 * We have to make sure every request message use the different start IV.
 * The dangerous point is, for example:
 * AAAAAA
 * BBBBBB
 * <p>
 * AAAAAA
 * CCCCCC
 * <p>
 * These two messages will have the same head encrypted data.
 * So, we have to generate differcent iv parameters every time we encrypt different message request.
 * <p>
 * But, how does the others know what the iv is ?
 * The method is, we append the iv bytes to the encryted result, and when the receiver got a encrypted message,
 * he can cut off the last 16 num bytes as the iv parameters.
 */
public class SymmetricalEncryptionUtil {
    public final static String DEFAULT_ALGORITHM = "AES/CBC/PKCS5PADDING";

    /**
     * We default use AES/CBC/PKCS5PADDING algorithm
     * default use 16 length IV
     * 16 length iv bytes is append to the last of encrypted data,
     * please subtract it first.
     *
     * @param plainText
     * @param keys
     * @return
     * @throws Exception
     */
    public static String encryptData(String plainText, byte[] keys, String algorithm) {
        if (StringUtils.isBlank(algorithm)) {
            algorithm = DEFAULT_ALGORITHM;
        }
        try {
            // init cipher
            if(algorithm.equals("AES") || algorithm.contains("ECB")){
                Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, algorithm, keys, null);
                byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
                return Base64.encodeBase64String(encryptedBytes);
            }else{
                // generate iv
                SecureRandom random = new SecureRandom();
                byte[] iv = new byte[16];
                random.nextBytes(iv);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, algorithm, keys, ivParameterSpec);
                byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
                byte[] resultData = new byte[encryptedBytes.length + 16];
                System.arraycopy(encryptedBytes, 0, resultData, 0, encryptedBytes.length);
                System.arraycopy(ivParameterSpec.getIV(), 0, resultData, encryptedBytes.length, ivParameterSpec.getIV().length);
                return Base64.encodeBase64String(resultData);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * We default use AES/CBC/PKCS5PADDING algorithm
     * default use 16 length IV
     * 16 length iv bytes is append to the last of encrypted data,
     * please subtract it first.
     *
     * @param encrypted
     * @param key
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static String decryptData(String encrypted, byte[] key, String algorithm) throws Exception {
        if (StringUtils.isBlank(algorithm)) {
            algorithm = DEFAULT_ALGORITHM;
        }
        byte[] encryptedBytes = Base64.decodeBase64(encrypted);
        if(algorithm.equals("AES") || algorithm.contains("ECB")){
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, algorithm, key, null);
            return new String(cipher.doFinal(encryptedBytes));
        }else{
            IvParameterSpec ivParameterSpec = new IvParameterSpec(encryptedBytes, encryptedBytes.length - 16, 16);
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, algorithm, key, ivParameterSpec);
            return new String(cipher.doFinal(encryptedBytes, 0, encryptedBytes.length - 16));
        }
    }

    private static Cipher getCipher(int cipherMode, String encryptionAlgorithm,
                                    byte[] key, IvParameterSpec ivParameterSpec)
            throws Exception {
        String algorithmType = encryptionAlgorithm.split("/")[0];
        SecretKeySpec keySpecification = new SecretKeySpec(
                key, algorithmType);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        // ECB don't need IV, it's not chain encryption
        if(null == ivParameterSpec){
            cipher.init(cipherMode, keySpecification);
        }else{
            cipher.init(cipherMode, keySpecification, ivParameterSpec);
        }
        return cipher;
    }

    /**
     * we use this method to generate a private key for use
     * @param length
     * @return
     */
    public static String generatePrivateKeyFor(int length, String algorithm){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            keyGen.init(length, secureRandom);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.encodeBase64String(secretKey.getEncoded());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        try {
            String privateKey = generatePrivateKeyFor(128, "AES");
            String encryptedResult = encryptData("aaa", Base64.decodeBase64(privateKey), "AES");
            System.out.println(encryptedResult);
            System.out.println(decryptData(encryptedResult, Base64.decodeBase64(privateKey), "AES"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}