package com.amee.base.crypto;

import org.junit.Before;
import org.junit.Test;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InternalCryptoTest {

    @Before
    public void setUp() throws CryptoException {
        InternalCrypto.initialise((SecretKeySpec) InternalCrypto.getNewKey(), "1234123412341234".getBytes());
    }

    @Test
    public void shouldEncryptAndDecrypt() throws CryptoException {
        String test = "hello";
        String encrypted = InternalCrypto.encrypt(test);
        String decrypted = InternalCrypto.decrypt(encrypted);
        assertEquals(test, decrypted);
    }

    @Test
    public void shouldFailWithBadInput() throws CryptoException {
        String test = "hello";
        String encrypted = InternalCrypto.encrypt(test);
        encrypted = encrypted.substring(1);
        try {
            InternalCrypto.decrypt(encrypted);
            fail("Decryption should have thrown an IllegalBlockSizeException.");
        } catch (CryptoException e) {
            assertEquals(e.getCause().getClass(), IllegalBlockSizeException.class);
        }
    }
}
