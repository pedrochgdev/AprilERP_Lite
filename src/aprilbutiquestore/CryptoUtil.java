/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aprilbutiquestore;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {
    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = Base64.getDecoder().decode("7A2B4C6D8E0F1A3B5C7D9E0F2A4B6C8D");

    public static String encrypt(char[] input) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, ALGORITHM));
        return Base64.getEncoder().encodeToString(cipher.doFinal(new String(input).getBytes()));
    }

    public static char[] decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, ALGORITHM));
        return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted))).toCharArray();
    }
}


