package com.amee.base.crypto;

import java.io.File;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cryptography for internal use with a private local key and salt.
 * <p/>
 * See the {@link BaseCrypto} class for detailed information on cryptography used in this class.
 */
public class InternalCrypto extends BaseCrypto {

    private final static String KEY_FILE = "amee.keyFile";
    private final static String SALT_FILE = "amee.saltFile";
    private final static String AMEE_KEY = "AMEE_KEY";
    private final static String AMEE_SALT = "AMEE_SALT";
    private static byte[] salt = null;
    private static SecretKeySpec secretKeySpec = null;
    private static IvParameterSpec iv = null;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private InternalCrypto() {
        throw new AssertionError();
    }

    /**
     * Initialise the static properties of this Class from the key and salt files. The key file is identified by
     * the amee.keyFile system property and the salt file by the amee.saltFile system property. See
     * {@link BaseCrypto} for details on how key and salt files should be formed.
     *
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    private synchronized static void initialise() throws CryptoException {//NOPMD
        if (InternalCrypto.secretKeySpec == null) {

            // First try environment
            String key = System.getenv(AMEE_KEY);
            String salt = System.getenv(AMEE_SALT);
            if (key != null && salt != null) {
                initialise(InternalCrypto.readKeyFromString(System.getenv(AMEE_KEY)),
                    InternalCrypto.readSaltFromString(System.getenv(AMEE_SALT)));
            } else {

                // Try loading from files
                String keyFileName = System.getProperty(KEY_FILE, "amee.key");
                String saltFileName = System.getProperty(SALT_FILE, "amee.salt");
                if ((keyFileName != null) && (saltFileName != null)) {
                    File keyFile = new File(keyFileName);
                    File saltFile = new File(saltFileName);
                    if (keyFile.isFile() && saltFile.isFile()) {
                        // Load from filesystem
                        initialise(InternalCrypto.readKeyFromFile(keyFile), InternalCrypto.readSaltFromFile(saltFile));
                    } else {
                        // Load from classpath
                        initialise(InternalCrypto.readKeyFromClasspath(keyFileName), InternalCrypto.readSaltFromClasspath(saltFileName));
                    }
                }
            }
            if ((secretKeySpec == null) || (iv == null)) {
                throw new RuntimeException("Could not create SecretKeySpec or IvParameterSpec instances. Check key and salt files.");
            }
        }
    }

    /**
     * Initialise the static properties of this Class from the supplied {@link SecretKeySpec} and salt.
     *
     * @param newSecretKey a {@link SecretKeySpec}
     * @param newSalt      a salt
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    protected synchronized static void initialise(SecretKeySpec newSecretKey, byte[] newSalt) throws CryptoException {
        if (InternalCrypto.secretKeySpec == null) {
            if ((newSecretKey != null) && (newSalt != null) && (newSalt.length == 16)) {
                InternalCrypto.secretKeySpec = newSecretKey;
                InternalCrypto.salt = newSalt;
                InternalCrypto.iv = new javax.crypto.spec.IvParameterSpec(newSalt);
            } else {
                throw new IllegalStateException("Could not set SecretKeySpec or IvParameterSpec instances.");
            }
        }
    }

    /**
     * Returns an encrypted version of the supplied string using the key and salt on the file system.
     *
     * @param toBeEncrypted the string to be encrypted
     * @return the encrypted string
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static String encrypt(String toBeEncrypted) throws CryptoException {
        InternalCrypto.initialise();
        return encrypt(secretKeySpec, iv, toBeEncrypted);
    }

    /**
     * Returns a decrypted version of the supplied string using the key and salt on the file system.
     *
     * @param toBeDecrypted the string to be decrypted
     * @return the decrypted string
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static String decrypt(String toBeDecrypted) throws CryptoException {
        InternalCrypto.initialise();
        return decrypt(secretKeySpec, iv, toBeDecrypted);
    }

    /**
     * Returns the supplied string in MD5 digested and Base64 encoded form.
     *
     * @param source string to be digested and encoded
     * @return the supplied string in MD5 digested and Base64 encoded form
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static String getAsMD5AndBase64(String source) throws CryptoException {
        InternalCrypto.initialise();
        return getAsMD5AndBase64(salt, source);
    }
}