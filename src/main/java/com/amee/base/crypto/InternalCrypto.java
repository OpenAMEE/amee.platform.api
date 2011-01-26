package com.amee.base.crypto;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;

/**
 * Cryptography for internal use with a private local key and salt.
 */
public class InternalCrypto extends BaseCrypto {

    private final static String KEY_FILE = "amee.keyFile";
    private final static String SALT_FILE = "amee.saltFile";
    private static byte[] salt = null;
    private static SecretKeySpec secretKeySpec = null;
    private static IvParameterSpec iv = null;

    public InternalCrypto() {
        super();
    }

    protected synchronized static void initialise() throws CryptoException {
        if (InternalCrypto.secretKeySpec == null) {
            String keyFileName = System.getProperty(KEY_FILE);
            String saltFileName = System.getProperty(SALT_FILE);
            if ((keyFileName != null) && (saltFileName != null)) {
                File keyFile = new File(keyFileName);
                File saltFile = new File(saltFileName);
                if (keyFile.isFile() && saltFile.isFile()) {
                    initialise(InternalCrypto.readKeyFromFile(keyFile), InternalCrypto.readSaltFromFile(saltFile));
                }
            }
            if ((secretKeySpec == null) || (iv == null)) {
                throw new RuntimeException("Could not create SecretKeySpec or IvParameterSpec instances. Check key and salt files.");
            }
        }
    }

    protected synchronized static void initialise(SecretKeySpec newSecretKey, byte[] newSalt) throws CryptoException {
        if (InternalCrypto.secretKeySpec == null) {
            InternalCrypto.secretKeySpec = newSecretKey;
            InternalCrypto.salt = newSalt;
            InternalCrypto.iv = new javax.crypto.spec.IvParameterSpec(newSalt);
        }
    }

    public static String encrypt(String toBeEncrypted) throws CryptoException {
        InternalCrypto.initialise();
        return encrypt(secretKeySpec, iv, toBeEncrypted);
    }

    public static String decrypt(String toBeDecrypted) throws CryptoException {
        InternalCrypto.initialise();
        return decrypt(secretKeySpec, iv, toBeDecrypted);
    }

    public static String getAsMD5AndBase64(String s) throws CryptoException {
        InternalCrypto.initialise();
        return getAsMD5AndBase64(salt, s);
    }
}
