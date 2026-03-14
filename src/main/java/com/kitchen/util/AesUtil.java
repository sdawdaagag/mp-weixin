package com.kitchen.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * AES对称加密工具类
 * 用于密码加密和解密
 */
@Slf4j
@Component
public class AesUtil {

    /**
     * AES密钥，从配置文件读取
     * 必须是16位字符
     */
    @Value("${aes.key:KitchenAesKey2026}")
    private String aesKey;

    private AES aes;

    /**
     * 初始化AES加密器
     */
    @PostConstruct
    public void init() {
        String key = aesKey;
        if (key.length() < 16) {
            key = key + "0000000000000000".substring(0, 16 - key.length());
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        this.aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
        log.info("AES加密工具初始化完成");
    }

    /**
     * 加密明文
     * 
     * @param plainText 明文
     * @return 密文（Base64编码）
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        return aes.encryptBase64(plainText);
    }

    /**
     * 解密密文
     * 
     * @param cipherText 密文（Base64编码）
     * @return 明文
     */
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            return aes.decryptStr(cipherText);
        } catch (Exception e) {
            log.error("解密失败: {}", e.getMessage());
            return cipherText;
        }
    }

    /**
     * 校验密码
     * 
     * @param plainPassword 明文密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean verify(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null) {
            return false;
        }
        String encrypted = encrypt(plainPassword);
        return encrypted.equals(encryptedPassword);
    }
}
