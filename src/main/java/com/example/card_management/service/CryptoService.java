package com.example.card_management.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final String ALG = "AES";
    private static final String TRANS = "AES/GCM/NoPadding";

    private final SecureRandom rnd = new SecureRandom();

    @Value("${card.crypto.secret}")
    private String secretProp;

    @Value("${card.crypto.gcm-tag-bits:128}")
    private int gcmTagBits;

    @Value("${card.crypto.iv-len:12}")
    private int ivLen;

    private SecretKey key;

    @PostConstruct
    void init() {
        byte[] raw;
        if (secretProp.startsWith("base64:")) {
            raw = Base64.getDecoder().decode(secretProp.substring("base64:".length()));
        } else {
            raw = secretProp.getBytes(StandardCharsets.UTF_8);
        }
        if (raw.length < 32) {
            throw new IllegalArgumentException("card.crypto.secret must be at least 32 bytes");
        }
        byte[] k = new byte[32];
        System.arraycopy(raw, 0, k, 0, 32);
        this.key = new SecretKeySpec(k, ALG);
    }

    public String encryptToB64(String plain) {
        try {
            byte[] iv = new byte[ivLen];
            rnd.nextBytes(iv);

            Cipher c = Cipher.getInstance(TRANS);
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(gcmTagBits, iv));
            byte[] ct = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buf = ByteBuffer.allocate(iv.length + ct.length);
            buf.put(iv).put(ct);
            return Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            throw new IllegalStateException("encrypt error", e);
        }
    }

    public String decryptFromB64(String b64) {
        try {
            byte[] all = Base64.getDecoder().decode(b64);
            byte[] iv = new byte[ivLen];
            byte[] ct = new byte[all.length - ivLen];
            System.arraycopy(all, 0, iv, 0, ivLen);
            System.arraycopy(all, ivLen, ct, 0, ct.length);

            Cipher c = Cipher.getInstance(TRANS);
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(gcmTagBits, iv));
            return new String(c.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("decrypt error", e);
        }
    }
}
