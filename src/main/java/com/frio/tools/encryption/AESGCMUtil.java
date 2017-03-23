package com.frio.tools.encryption;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.*;

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
        if (args.length >= 0) {
            testNum = Integer.parseInt(args[0]);
            if (testNum < 0 || testNum > 3) {
                System.exit(-1);
            }
        }
        byte[] input = "Hello AES_GCM World".getBytes();
        SecureRandom random = SecureRandom.getInstanceStrong();
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE, random);
        SecretKey key = keyGen.generateKey();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
        final byte[] nonce = new byte[GCM_NONCE_LENGTH];
        random.nextBytes(nonce);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] aad = "Whatever I like".getBytes();
        cipher.updateAAD(aad);

        byte[] cipherText = cipher.doFinal(input);
        cipher.init(Cipher.DECRYPT_MODE, key ,spec);

        if(testNum == 1){
            aad[1] ++;
        }


    }
}
