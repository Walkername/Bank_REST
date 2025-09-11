package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CardNumberCrypto {

    private final SecretKeySpec secretKey;

    @Autowired
    public CardNumberCrypto(
            @Value("${spring.card-number.encryption.secret}") String secret
    ) {
        this.secretKey = new SecretKeySpec(secret.getBytes(), "AES");
    }

    public String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    public String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }

    public String transformToMaskedNumber(String cardNumber) {
        String lastFourDigits = cardNumber.substring(12);
        return "**** **** **** " + lastFourDigits;
    }

}
