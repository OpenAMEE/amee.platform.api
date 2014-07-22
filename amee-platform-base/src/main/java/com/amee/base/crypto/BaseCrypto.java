package com.amee.base.crypto;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Provides a collection of String encryption utility methods. Methods cover encryption, decryption and key and salt
 * generation and file management.
 * <p/>
 * Supports encryption with AES using CBC mode and PKCS #5 padding. Strings are encoded into UTF-8 before encryption
 * and decoded after decryption. There are no String length or content limitations.
 * <p/>
 * All methods are static and no instances are expected.
 * <p/>
 * The {@link InternalCrypto} sub-class provides extra utility functions.
 */
public class BaseCrypto {

    /**
     * Protected constructor to prevent direct instantiation but allow inheritance.
     */
    protected BaseCrypto() {
        throw new AssertionError();
    }

    public static final int KEY_SIZE = 256;

    /**
     * Encrypt the supplied String and return it.
     *
     * @param secretKeySpec The {@link SecretKeySpec} to use
     * @param iv            The {@link IvParameterSpec to use}
     * @param toBeEncrypted The String to be encrypted
     * @return The encrypted String
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    protected static String encrypt(SecretKeySpec secretKeySpec, IvParameterSpec iv, String toBeEncrypted) throws CryptoException {
        try {
            byte[] data = toBeEncrypted.getBytes("UTF-8");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            byte[] result = cipher.doFinal(data);
            result = Base64.encodeBase64(result);
            return new String(result);
        } catch (UnsupportedEncodingException e) {
            throw new CryptoException(true, e);
        } catch (InvalidKeyException e) {
            throw new CryptoException(true, e);
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException(true, e);
        } catch (BadPaddingException e) {
            throw new CryptoException(true, e);
        } catch (NoSuchPaddingException e) {
            throw new CryptoException(true, e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(true, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * Decrypt the supplied String and return it.
     *
     * @param secretKeySpec The {@link SecretKeySpec} to use
     * @param iv            The {@link IvParameterSpec to use}
     * @param toBeDecrypted The String to be decrypted
     * @return The decrypted String.
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    protected static String decrypt(SecretKeySpec secretKeySpec, IvParameterSpec iv, String toBeDecrypted) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] data = toBeDecrypted.getBytes("UTF-8");
            data = Base64.decodeBase64(data);
            byte[] result = cipher.doFinal(data);
            return new String(result);
        } catch (UnsupportedEncodingException e) {
            throw new CryptoException(true, e);
        } catch (InvalidKeyException e) {
            throw new CryptoException(true, e);
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException(false, e);
        } catch (BadPaddingException e) {
            throw new CryptoException(false, e);
        } catch (NoSuchPaddingException e) {
            throw new CryptoException(true, e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(true, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * Returns, in Base64 String form, a new AES {@link SecretKey} with a key size
     * of {@link com.amee.base.crypto.BaseCrypto#KEY_SIZE}.
     *
     * @return The a {@link SecretKey} key as a String
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static String getNewKeyAsString() throws CryptoException {
        return getKeyAsString(getNewKey());
    }

    /**
     * Returns a new AES {@link SecretKey} with a key size
     * of {@link com.amee.base.crypto.BaseCrypto#KEY_SIZE}.
     *
     * @return A new {@link SecretKey}
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    protected static SecretKey getNewKey() throws CryptoException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_SIZE);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * Converts the supplied {@link SecretKey} into Base64 String form. Will internally use an AES
     * configured {@link SecretKeySpec}.
     *
     * @param key The {@link SecretKey} to convert to a String
     * @return The {@link SecretKey} in String form.
     */
    protected static String getKeyAsString(SecretKey key) {
        // convert key to byte array
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
        byte[] keyAsBytes = secretKeySpec.getEncoded();
        // convert key bytes to Base64 encoded String
        return new String(Base64.encodeBase64(keyAsBytes));
    }

    /**
     * Save the supplied {@link SecretKey} to the supplied File. Will internally use an AES
     * configured {@link SecretKeySpec}.
     *
     * @param key  {@link SecretKey} to save
     * @param file File to save the {@link SecretKey} to
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static void saveKeyToFile(SecretKey key, File file) throws CryptoException {
        try {
            // convert key to byte array
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
            byte[] keyAsBytes = secretKeySpec.getEncoded();
            // save key to file
            FileOutputStream output = new FileOutputStream(file);
            output.write(keyAsBytes);
            output.close();
        } catch (IOException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * Loads a {@link SecretKeySpec} from the File.
     *
     * @param file File to load the {@link SecretKeySpec} from
     * @return The {@link SecretKeySpec} loaded from the File
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static SecretKeySpec readKeyFromFile(File file) throws CryptoException {
        try {
            // read key byte array from file
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            byte[] keyAsBytes = new byte[(int) file.length()];
            input.readFully(keyAsBytes);
            input.close();
            return new SecretKeySpec(keyAsBytes, "AES");
        } catch (IOException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * Loads a salt from the supplied File. Salt must be exactly 16 bytes long otherwise a
     * {@link RuntimeException} is thrown.
     *
     * @param file to load salt from
     * @return the salt as a byte array
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static byte[] readSaltFromFile(File file) throws CryptoException {
        try {
            // Read salt byte array from file.
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            byte[] salt = new byte[(int) file.length()];
            input.readFully(salt);
            input.close();
            // Salt must be 16 bytes.
            if (salt.length != 16) {
                throw new RuntimeException("Salt from '" + file.getAbsolutePath() + "' is not 16 bytes.");
            }
            return salt;
        } catch (IOException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * Loads a {@link SecretKeySpec} from a file on the classpath.
     *
     * @param keyFileName       the name of the classpath resource to load the key from
     * @return                  the SecretKeySpec loaded from the classpath
     * @throws CryptoException  encapsulates various potential cryptography exceptions
     */
    public static SecretKeySpec readKeyFromClasspath(String keyFileName) throws CryptoException {
        try {
            DataInputStream in = new DataInputStream(BaseCrypto.class.getClassLoader().getResourceAsStream(keyFileName));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            out.flush();
            byte[] keyAsBytes = out.toByteArray();
            return new SecretKeySpec(keyAsBytes,  "AES");
        } catch (IOException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * Loads a salt from a file on the classpath.  Salt must be exactly 16 bytes long otherwise a 
     * RuntimeException is thrown.
     *
     * @param saltFileName      the name of the classpath resource to load the key from
     * @return                  the salt as a byte array
     * @throws CryptoException  encapsulates various potential cryptography exceptions
     */
    public static byte[] readSaltFromClasspath(String saltFileName) throws CryptoException {
        try{
            DataInputStream in = new DataInputStream(BaseCrypto.class.getClassLoader().getResourceAsStream(saltFileName));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i;
            while((i = in.read()) != -1) {
                out.write(i);
            }
            out.flush();
            byte[] salt = out.toByteArray();
            // Salt must be 16 bytes.
            if(salt.length != 16) {
                throw new RuntimeException("Salt from '" + saltFileName + "' is not 16 bytes.");
            }
            return salt;
        }catch(IOException e){
            throw new CryptoException(true, e);
        }
    }

    /**
     * Read key byte array from base 64 encoded string.
     *
     * @param base64Encoded
     * @return
     */
    public static SecretKeySpec readKeyFromString(String base64Encoded) {
        byte[] keyAsBytes = DatatypeConverter.parseBase64Binary(base64Encoded);
        return new SecretKeySpec(keyAsBytes, "AES");
    }

    public static byte[] readSaltFromString(String salt) {
        return salt.getBytes();
    }


    /**
     * Return an MD5 Base64 encoded digest of the supplied String & salt. Only the first
     * 8 bytes of the salt will be included.
     *
     * @param salt to use
     * @param s    to digest
     * @return MD5 Base64 representation of the supplied String
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static String getAsMD5AndBase64(byte[] salt, String s) throws CryptoException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(ArrayUtils.subarray(salt, 0, 8));
            md.update(s.getBytes());
            return new String(Base64.encodeBase64(md.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(true, e);
        }
    }

    /**
     * A main method to create a new {@link SecretKey} and save it to a file.
     * <p/>
     * The first and only command line argument is expected to be a file name which the new {@link SecretKey} can
     * be saved to.
     *
     * @param args command line arguments
     * @throws CryptoException encapsulates various potential cryptography exceptions
     */
    public static void main(String[] args) throws CryptoException {
        System.out.println("Creating new key...");
        System.out.flush();
        SecretKey secretKey = getNewKey();
        if (args.length > 0) {
            saveKeyToFile(secretKey, new File(args[0]));
            System.out.println("...done.");
        } else {
            System.out.println(getKeyAsString(secretKey));
        }
    }
}