package com.amee.base.crypto;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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

public class BaseCrypto {

    public static final int KEY_SIZE = 256;

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

    public static String getNewKeyAsString() throws CryptoException {
        return getKeyAsString(getNewKey());
    }

    protected static SecretKey getNewKey() throws CryptoException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_SIZE);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(true, e);
        }
    }

    protected static String getKeyAsString(SecretKey key) {
        // convert key to byte array
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");
        byte[] keyAsBytes = secretKeySpec.getEncoded();
        // convert key bytes to Base64 encoded String
        return new String(Base64.encodeBase64(keyAsBytes));
    }

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

    public static byte[] readSaltFromFile(File file) throws CryptoException {
        try {
            // read salt byte array from file
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            byte[] salt = new byte[(int) file.length()];
            input.readFully(salt);
            input.close();
            // must be 16 bytes
            if (salt.length != 16) {
                throw new RuntimeException("Salt from '" + file.getAbsolutePath() + "' is not 16 bytes.");
            }
            return salt;
        } catch (IOException e) {
            throw new CryptoException(true, e);
        }
    }

    public static String getAsMD5AndBase64(byte[] salt, String s) throws CryptoException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt);
            md.update(s.getBytes());
            return new String(Base64.encodeBase64(md.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(true, e);
        }
    }

    public static void main(String[] args) throws CryptoException {
        System.out.println("Creating new key...");
        System.out.flush();
        SecretKey secretKey = getNewKey();
        saveKeyToFile(secretKey, new File(args[0]));
        System.out.println("...done.");
    }
}