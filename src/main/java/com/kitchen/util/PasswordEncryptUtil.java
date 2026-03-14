package com.kitchen.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 独立的密码加密工具类
 * 不依赖外部库，使用Java内置AES加密
 */
public class PasswordEncryptUtil {

    private static final String AES_KEY = "KitchenAesKey2026";
    private static final String ALGORITHM = "AES";

    public static void main(String[] args) {
        try {
            System.out.println("========== 密码加密工具 ==========");
            System.out.println("AES密钥: " + AES_KEY);
            System.out.println();
            
            String[] passwords = {"123456", "admin", "admin123", "password"};
            
            System.out.println("========== 常用密码加密对照表 ==========");
            for (String pwd : passwords) {
                String encrypted = encrypt(pwd);
                System.out.println("明文: " + pwd + " -> 密文: " + encrypted);
            }
            
            System.out.println();
            System.out.println("========== SQL更新语句 ==========");
            System.out.println("-- 更新所有用户密码为 123456");
            System.out.println("UPDATE sys_user SET password = '" + encrypt("123456") + "';");
            System.out.println();
            System.out.println("-- 更新管理员密码为 admin123");
            System.out.println("UPDATE sys_user SET password = '" + encrypt("admin123") + "' WHERE username = 'chef';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * AES加密
     */
    public static String encrypt(String plainText) throws Exception {
        String key = AES_KEY;
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES解密
     */
    public static String decrypt(String cipherText) throws Exception {
        String key = AES_KEY;
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
