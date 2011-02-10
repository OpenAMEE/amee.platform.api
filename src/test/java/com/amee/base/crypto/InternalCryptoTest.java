package com.amee.base.crypto;

public class InternalCryptoTest {


//    @Before
//    public void setUp() throws CryptoException {
//        InternalCrypto.initialise((SecretKeySpec) InternalCrypto.getNewKey(), "1234123412341234".getBytes());
//    }
//
//    @Test
//    public void shouldEncryptAndDecrypt() throws CryptoException {
//        String test = "hello";
//        String encrypted = InternalCrypto.encrypt(test);
//        String decrypted = InternalCrypto.decrypt(encrypted);
//        assertEquals(test, decrypted);
//    }
//
//    @Test
//    public void shouldFailWithBadInput() throws CryptoException {
//        String test = "hello";
//        String encrypted = InternalCrypto.encrypt(test);
//        encrypted = encrypted.substring(1);
//        try {
//            InternalCrypto.decrypt(encrypted);
//            fail("Decryption should have thrown an IllegalBlockSizeException.");
//        } catch (CryptoException e) {
//            assertEquals(e.getCause().getClass(), IllegalBlockSizeException.class);
//        }
//    }
}
