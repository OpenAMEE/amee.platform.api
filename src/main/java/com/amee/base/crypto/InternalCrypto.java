package com.amee.base.crypto;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;

/**
 * Cryptography for internal use with a private local key and salt.
 * <p/>
 * See the {@link BaseCrypto} class for detailed information on cryptography used in this class.
 */
public abstract class InternalCrypto extends BaseCrypto {

    private final static String KEY_FILE = "amee.keyFile";
    private final static String SALT_FILE = "amee.saltFile";
    private static byte[] salt = null;
    private static SecretKeySpec secretKeySpec = null;
    private static IvParameterSpec iv = null;

    /**
     * Initialise the static properties of this Class from the key and salt files. The key file is identified by
     * the amee.keyFile system property and the salt file by the amee.saltFile system property. See
     * {@link BaseCrypto} for details on how key and salt files should be formed.
     *
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    private synchronized static void initialise() throws CryptoException {
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
