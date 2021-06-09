package com.tsuki.tester.newtype.sign;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-07 14:21
 **/
public class TestSign {

    public static final String str = "/Users/startsi/Downloads/dddd.pdf";
    public static final String p12File = "/Users/startsi/Documents/csr/newesign/user.p12";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        TestSign newSign = new TestSign();
        System.out.println("--------------签名--------------");
        byte[] sign = newSign.sign();
        System.out.println("--------------验签--------------");
        System.out.println(newSign.verySign(sign));
    }

    public PrivateKey getPrivateKey() throws NoSuchProviderException, KeyStoreException {
        char[] pwd = "111111".toCharArray();
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File)) {
            store.load(fIn, pwd);
            PrivateKey privateKey = (PrivateKey) store.getKey("private", pwd);
            System.out.println("privateKey:" + privateKey.toString());
            return privateKey;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PublicKey getPublicKey() throws NoSuchProviderException, KeyStoreException {
        char[] pwd = "111111".toCharArray();
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File)) {
            store.load(fIn, pwd);
            Certificate cert = store.getCertificate("private");
//            X509Certificate cert = store.getCertificateChain("private")[0];
            PublicKey pubkey = cert.getPublicKey();
            System.out.println("publickey:" + pubkey.toString());
            return pubkey;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] sign() throws IOException, GeneralSecurityException {
        Signature signature = Signature.getInstance(
                GMObjectIdentifiers.sm2sign_with_sm3.toString()
                , new BouncyCastleProvider());
        signature.initSign(getPrivateKey());
        // 签名原文
        byte[] plainText = str.getBytes();
        // 写入签名原文到算法中
        signature.update(plainText);
        // 计算签名值
        byte[] signatureValue = signature.sign();
        System.out.println(signatureValue.length);
        return signatureValue;
    }

    public boolean verySign(byte[] signValue) throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(
                GMObjectIdentifiers.sm2sign_with_sm3.toString()
                , new BouncyCastleProvider());
        signature.initVerify(getPublicKey());
        // 写入待验签的签名原文到算法中
        signature.update((str).getBytes());
        // 验签
        return signature.verify(signValue);
    }

}
