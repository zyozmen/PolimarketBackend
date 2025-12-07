package com.polimarket.util;

/**
 * Clase auxiliar para generar contraseñas cifradas
 * Ejecutar este programa para obtener contraseñas cifradas con AES-256
 * y actualizarlas en data.sql
 */
public class GeneradorPasswordsCifradas {

    public static void main(String[] args) {
        PasswordEncryptionUtil encryptor = new PasswordEncryptionUtil();
        
        System.out.println("=".repeat(60));
        System.out.println("GENERADOR DE CONTRASEÑAS CIFRADAS AES-256");
        System.out.println("=".repeat(60));
        
        // Definir las contraseñas a cifrar
        String[] passwords = {
            "admin123",
            "password123",
            "ventas123",
            "test123"
        };
        
        String[] usuarios = {
            "admin",
            "jperez",
            "mlopez",
            "inactivo"
        };
        
        try {
            System.out.println("\nContraseñas cifradas para data.sql:\n");
            
            for (int i = 0; i < passwords.length; i++) {
                String encrypted = encryptor.encrypt(passwords[i]);
                System.out.printf("Usuario: %-12s | Password: %-15s | Cifrado: %s%n", 
                    usuarios[i], 
                    passwords[i], 
                    encrypted);
            }
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Copia los valores cifrados al archivo data.sql");
            System.out.println("=".repeat(60));
            
            // Mostrar ejemplo de uso en data.sql
            System.out.println("\nEjemplo para data.sql:");
            System.out.println("-".repeat(60));
            System.out.println("INSERT INTO persona (..., password, ...) VALUES");
            System.out.println("(..., '" + encryptor.encrypt(passwords[0]) + "', ...);");
            System.out.println("-".repeat(60));
            
        } catch (Exception e) {
            System.err.println("Error al cifrar contraseñas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
