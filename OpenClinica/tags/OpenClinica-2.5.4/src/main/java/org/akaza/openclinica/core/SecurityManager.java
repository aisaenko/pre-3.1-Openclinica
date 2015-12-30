/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core;

import org.akaza.openclinica.exception.OpenClinicaException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author jxu
 *
 * Provides a singleton Security Manager
 */
public class SecurityManager {

    /**
     * A handle to the unique SecurityManager instance.
     */
    static private SecurityManager secInstance = null;

    /**
     * @return The unique instance of this class.
     */
    static public SecurityManager getInstance() {
        if (secInstance == null) {
            secInstance = new SecurityManager();
        }
        return secInstance;
    }

    /**
     * Generates a random password with default length
     *
     */
    public String genPassword() {
        return genPassword(8);
    }

    /**
     * Generates a random password by length
     *
     * @param howmany
     */
    public String genPassword(int howmany) {

        String ret = "";
        String core = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();

        for (int i = 0; i < howmany; i++) {
            int thisOne = rand.nextInt(core.length());
            char thisOne2 = core.charAt(thisOne);
            ret += thisOne2;
        }

        return ret;
    }

    /**
     * Encrypts a plaintext password with MD5 and hexes the results Does the
     * same function as RealmBase.Digest does Using Java 's built-in
     * MessageDigest class to do password encryption so we don't have to put
     * catalina.jar into WEB-INF/lib, which might be dangerous.catalina.jar is a
     * part of TOMCAT server/lib and cannot be put into common/lib. Also this
     * can make the war file smaller
     *
     * @param password
     * @return encryted password
     */
    public String encrytPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer(2 * digest.length);

        char[] hex = "0123456789abcdef ".toCharArray();

        for (int i = 0; i < digest.length; ++i) {
            int high = (digest[i] & 0xf0) >> 4;
            int low = digest[i] & 0x0f;

            sb.append(hex[high]);
            sb.append(hex[low]);
        }

        return sb.toString();
        // return RealmBase.Digest(password,"MD5");

    }

    // private String characterEncoding;
    // private Cipher encryptCipher;
    // private Cipher decryptCipher;
    // private BASE64Encoder base64Encoder = new BASE64Encoder();
    // private BASE64Decoder base64Decoder = new BASE64Decoder();
    //
    // public void DESPasswordEncrypt(byte[] keyBytes, byte[] ivBytes, String
    // characterEncoding) throws Exception
    // {
    // SecretKey key = new SecretKeySpec(keyBytes, "DES");
    // IvParameterSpec iv = new IvParameterSpec(ivBytes);
    // this.characterEncoding = characterEncoding;
    // this.encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding",
    // "SunJCE");
    // this.encryptCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, iv);
    // this.decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding",
    // "SunJCE");
    // this.decryptCipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, iv);
    // }
    //
    // synchronized public String encrypt(String password) throws Exception
    // {
    // byte[] passwordBytes = password.getBytes(characterEncoding);
    // byte[] encryptedPasswordBytes =
    // this.encryptCipher.doFinal(passwordBytes);
    // String encodedEncryptedPassword =
    // this.base64Encoder.encode(encryptedPasswordBytes);
    // return encodedEncryptedPassword;
    // }
    //
    // synchronized public String decrypt(String encodedEncryptedPassword)
    // throws Exception
    // {
    // byte[] encryptedPasswordBytes =
    // this.base64Decoder.decodeBuffer(encodedEncryptedPassword);
    // byte[] passwordBytes =
    // this.decryptCipher.doFinal(encryptedPasswordBytes);
    // String recoveredPassword = new String(passwordBytes, characterEncoding);
    // return recoveredPassword;
    // }
    //
    // private static byte[] iv =
    // {0x0a, 0x01, 0x02, 0x03, 0x04, 0x0b, 0x0c, 0x0d};

    private static byte[] encrypt(byte[] phi) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        javax.crypto.spec.SecretKeySpec skeySpec = new javax.crypto.spec.SecretKeySpec(raw, "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(phi);
        return encrypted;
    }

    private static byte[] decrypt(byte[] encrypted) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        javax.crypto.spec.SecretKeySpec skeySpec = new javax.crypto.spec.SecretKeySpec(raw, "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] phi = cipher.doFinal(encrypted);
        return phi;
    }

    //
    // private static byte[] encrypt(byte[] empBytes, SecretKey key, String
    // xform)
    // throws NoSuchAlgorithmException, NoSuchPaddingException {
    // Cipher cipher = Cipher.getInstance(xform);;
    // byte[] retMe = empBytes;//if something thrown, returns same message
    //
    // IvParameterSpec ips = new IvParameterSpec(iv);
    // try {
    // cipher.init(Cipher.ENCRYPT_MODE, key);
    // } catch (InvalidKeyException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // try {
    // retMe = cipher.doFinal(empBytes);
    // } catch (IllegalStateException e2) {
    // // TODO Auto-generated catch block
    // e2.printStackTrace();
    // } catch (IllegalBlockSizeException e2) {
    // // TODO Auto-generated catch block
    // e2.printStackTrace();
    // } catch (BadPaddingException e2) {
    // // TODO Auto-generated catch block
    // e2.printStackTrace();
    // }
    // return retMe;
    // }

    // private static byte[] decrypt(byte[] empBytes, SecretKey key, String
    // xform)
    // throws NoSuchAlgorithmException, NoSuchPaddingException {
    // Cipher cipher = Cipher.getInstance(xform);
    // byte[] retMe = empBytes;
    // IvParameterSpec ips = new IvParameterSpec(iv);
    // try {
    // try {
    // cipher.init(Cipher.DECRYPT_MODE, key, ips);
    // } catch (InvalidAlgorithmParameterException e2) {
    // // TODO Auto-generated catch block
    // e2.printStackTrace();
    // }
    // } catch (InvalidKeyException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // try {
    // retMe = cipher.doFinal(empBytes);
    // } catch (IllegalStateException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // } catch (IllegalBlockSizeException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // } catch (BadPaddingException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // return retMe;
    // }
    /**
     * EncryptPHI -- new security manager version that will encrypt plaintext
     * and send it to the database. Only DecryptPHI should be able to decode the
     * PHI for the end user.
     *
     * @param phi
     *            non-encrypted PHI
     * @return a string encrypted
     */
    public String encryptPHI(String phi) throws OpenClinicaException {
        // String xform = "DES/ECB/PKCS5Padding";
        String retMe = "";
        try {
            // KeyGenerator kg = KeyGenerator.getInstance("DES");
            // kg.init(56);
            // SecretKey key = kg.generateKey();
            // byte[] dataBytes = phi.getBytes();
            byte[] encBytes = encrypt(phi.getBytes());
            retMe = encBytes.toString();
        } catch (Exception nsae) {
            throw new OpenClinicaException(nsae.getMessage(), "");
        }
        return retMe;
    }

    public String decryptPHI(String phi) throws OpenClinicaException {
        String xform = "DES/ECB/PKCS5Padding";
        String retMe = "";
        try {
            // KeyGenerator kg = KeyGenerator.getInstance("DES");
            // kg.init(56);
            // SecretKey key = kg.generateKey();
            // byte[] dataBytes = phi.getBytes();
            byte[] decBytes = decrypt(phi.getBytes());
            retMe = decBytes.toString();
        } catch (Exception nsae) {
            throw new OpenClinicaException(nsae.getMessage(), "");
        }
        return retMe;
    }
}