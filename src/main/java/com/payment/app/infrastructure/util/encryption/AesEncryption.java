package com.payment.app.infrastructure.util.encryption;

import com.payment.app.application.port.out.EncryptionStringPort;
import org.springframework.stereotype.Component;

@Component
public class AesEncryption implements EncryptionStringPort {
    @Override
    public String encrypt(String plainText) {
        return "****encrypted****";
    }
}
