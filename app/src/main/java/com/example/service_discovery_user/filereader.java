package com.example.service_discovery_user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class filereader {
        public static String getstr(String filename) throws IOException {
            // Read key from file
            String strFile = "";
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                strFile += line + "\n";
            }
            br.close();
            return strFile;

        }

//    public static String getPrivateKeyStringFromFile(String file) throws IOException, GeneralSecurityException {
//        String privateKeyPEM = getstr(file);
//        privateKeyPEM = privateKeyPEM.replace("-----BEGIN EC PRIVATE KEY-----\n", "");
//        privateKeyPEM = privateKeyPEM.replace("-----END EC PRIVATE KEY-----", "");
//        return privateKeyPEM;
//    }
    public static String getPublicKeyStringFromFile(String file) throws IOException, GeneralSecurityException {
        String publicKeyPEM = getstr(file);
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        return publicKeyPEM;
    }
    public static PrivateKey getPrivateKeyFromFile(String file) throws IOException, GeneralSecurityException {
            String privateKeyPEM = getstr(file);
            privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
            privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
            byte[] encoded = Base64.getMimeDecoder().decode(privateKeyPEM);
            String k1 = Base64.getMimeEncoder().encode(encoded).toString();
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");
            PrivateKey privKey = kf.generatePrivate(pkcs8);
            return privKey;
        }

        public static PublicKey getPublicKeyFromFile(String file) throws IOException, GeneralSecurityException {
            String publicKeyPEM = getstr(file);
            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
            publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
            byte[] encoded = Base64.getMimeDecoder().decode(publicKeyPEM);
            KeyFactory kf = KeyFactory.getInstance("EC");
            PublicKey pubKey = (PublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
            return pubKey;
        }

        public static String sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
            Signature sign = Signature.getInstance("SHA256withECDSA");
            sign.initSign(privateKey);
            sign.update(message.getBytes("UTF-8"));
            return new String(Base64.getEncoder().encode(sign.sign()), "UTF-8");
        }


        public static boolean verify(PublicKey publicKey, String message, String signature) throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
            Signature sign = Signature.getInstance("SHA256withECDSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes("UTF-8"));
            return sign.verify(Base64.getDecoder().decode(signature.getBytes("UTF-8")));
        }

        public static String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(rawText.getBytes("UTF-8")));
        }

        public static String decrypt(String cipherText, PrivateKey privateKey) throws IOException, GeneralSecurityException {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), "UTF-8");
        }

    private static final String HMAC_SHA512 = "HmacSHA512";

    public static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String calculateHMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes()));
    }

}
