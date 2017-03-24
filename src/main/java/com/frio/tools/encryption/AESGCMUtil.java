package com.frio.tools.encryption;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
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
        cipher.init(Cipher.DECRYPT_MODE, key ,spec);

        if(testNum == 1){
            aad[1] ++;
        }
        cipher.updateAAD(aad);
        if(testNum == 2){
            cipherText[10] ++;
        }
        if(testNum == 3){
            cipherText[cipherText.length - 2] ++;
        }
        try{
            byte[] plainText = cipher.doFinal(cipherText);
            if(testNum != 0){
                System.out.println("Test Failed: expected  AEADBadTagException not thrown!");
            }else{
                if(Arrays.equals(input, plainText)){
                    System.out.println("Test passed, match!");
                }else{
                    System.out.println("Test failed: result mismatch!");
                    System.out.println(new String(plainText));
                }
            }
        }catch(AEADBadTagException e){
            if(testNum == 0){
                System.out.println("Test failed: unexpected ex" + e);
                e.printStackTrace();
            }else{
                System.out.println("Test passed!expected ex" + e);
            }
        }

    }
}
