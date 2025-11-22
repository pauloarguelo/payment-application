package com.payment.app.infrastructure.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CreditCardEncryptionImpl Tests")
class CreditCardEncryptionImplTest {

    private CreditCardEncryptionImpl encryption;
    private String validKey;

    @BeforeEach
    void setUp() {
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < 32; i++) {
            keyBytes[i] = (byte) i;
        }
        validKey = Base64.getEncoder().encodeToString(keyBytes);
        encryption = new CreditCardEncryptionImpl(validKey);
    }

    @Test
    @DisplayName("Should encrypt card number successfully")
    void encrypt_shouldEncryptSuccessfully() {
        String cardNumber = "1234567812345678";

        String encrypted = encryption.encrypt(cardNumber);

        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEmpty();
        assertThat(encrypted).isNotEqualTo(cardNumber);
    }

    @Test
    @DisplayName("Should generate different encrypted values for same input")
    void encrypt_shouldGenerateDifferentEncryptedValues() {
        String cardNumber = "1234567812345678";

        String encrypted1 = encryption.encrypt(cardNumber);
        String encrypted2 = encryption.encrypt(cardNumber);

        assertThat(encrypted1).isNotEqualTo(encrypted2);
    }

    @Test
    @DisplayName("Should encrypt empty string")
    void encrypt_shouldEncryptEmptyString() {
        String emptyString = "";

        String encrypted = encryption.encrypt(emptyString);

        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEmpty();
    }

    @Test
    @DisplayName("Should throw exception when key length is invalid")
    void constructor_shouldThrowException_whenKeyLengthInvalid() {
        String invalidKey = Base64.getEncoder().encodeToString(new byte[16]);

        assertThatThrownBy(() -> new CreditCardEncryptionImpl(invalidKey))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Encryption Key must be 256 bits");
    }

    @Test
    @DisplayName("Should return base64 encoded result")
    void encrypt_shouldReturnBase64EncodedResult() {
        String cardNumber = "1234567812345678";

        String encrypted = encryption.encrypt(cardNumber);

        byte[] decoded = Base64.getDecoder().decode(encrypted);
        assertThat(decoded).isNotEmpty();
    }
}

