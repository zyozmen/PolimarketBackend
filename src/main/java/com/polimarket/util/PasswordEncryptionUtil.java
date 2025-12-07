package com.polimarket.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utilidad para cifrado y descifrado de contraseñas
 * Utiliza AES-256 para máxima seguridad
 */
@Component
public class PasswordEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "PoliMarket2025SecurePasswordKey!";

    /**
     * Genera una clave AES a partir de la clave secreta
     *
     * @return SecretKeySpec para cifrado/descifrado
     * @throws Exception si hay error en la generación de la clave
     */
    private SecretKeySpec generateKey() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // AES-128 (16 bytes)
        return new SecretKeySpec(key, ALGORITHM);
    }

    /**
     * Cifra una contraseña
     *
     * @param password contraseña en texto plano
     * @return contraseña cifrada en Base64
     * @throws Exception si hay error en el cifrado
     */
    public String encrypt(String password) throws Exception {
        SecretKeySpec secretKey = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Descifra una contraseña
     *
     * @param encryptedPassword contraseña cifrada en Base64
     * @return contraseña en texto plano
     * @throws Exception si hay error en el descifrado
     */
    public String decrypt(String encryptedPassword) throws Exception {
        SecretKeySpec secretKey = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Compara una contraseña en texto plano con una cifrada
     *
     * @param plainPassword contraseña en texto plano
     * @param encryptedPassword contraseña cifrada
     * @return true si coinciden
     */
    public boolean matches(String plainPassword, String encryptedPassword) {
        try {
            String decrypted = decrypt(encryptedPassword);
            return plainPassword.equals(decrypted);
        } catch (Exception e) {
            return false;
        }
    }
}
