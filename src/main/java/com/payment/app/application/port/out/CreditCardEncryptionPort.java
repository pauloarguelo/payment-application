package com.payment.app.application.port.out;

public interface CreditCardEncryptionPort {
    String encrypt(String plainText);
}
