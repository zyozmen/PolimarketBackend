package com.polimarket.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para PasswordEncryptionUtil
 * Verifica el cifrado y descifrado correcto de contraseñas
 */
class PasswordEncryptionUtilTest {

    private final PasswordEncryptionUtil passwordUtil = new PasswordEncryptionUtil();

    @Test
    void testEncryptDecrypt() throws Exception {
        String originalPassword = "password123";
        
        // Cifrar
        String encrypted = passwordUtil.encrypt(originalPassword);
        assertNotNull(encrypted);
        assertNotEquals(originalPassword, encrypted);
        
        // Descifrar
        String decrypted = passwordUtil.decrypt(encrypted);
        assertEquals(originalPassword, decrypted);
    }

    @Test
    void testMatches() throws Exception {
        String password = "mySecurePassword";
        String encrypted = passwordUtil.encrypt(password);
        
        assertTrue(passwordUtil.matches(password, encrypted));
        assertFalse(passwordUtil.matches("wrongPassword", encrypted));
    }

    @Test
    void testEncryptSamePasswordProducesSameResult() throws Exception {
        String password = "test123";
        
        String encrypted1 = passwordUtil.encrypt(password);
        String encrypted2 = passwordUtil.encrypt(password);
        
        // AES con misma clave produce el mismo resultado
        assertEquals(encrypted1, encrypted2);
    }

    @Test
    void testDecryptInvalidData() {
        assertThrows(Exception.class, () -> {
            passwordUtil.decrypt("invalid_base64_data");
        });
    }

    /**
     * Método auxiliar para generar contraseñas cifradas para pruebas
     * Ejecutar este test para obtener contraseñas cifradas
     */
    @Test
    void generateEncryptedPasswordsForTesting() throws Exception {
        String[] passwords = {"password123", "admin123", "test123"};
        
        System.out.println("\n=== Contraseñas Cifradas para Pruebas ===");
        for (String password : passwords) {
            String encrypted = passwordUtil.encrypt(password);
            System.out.println("Original: " + password);
            System.out.println("Cifrada:  " + encrypted);
            System.out.println("---");
        }
    }
}
