package com.tsuki.tester.service;

import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-02-22 16:24
 **/
public class GenerateCSR2 {
    private static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;
    private static KeyPairGenerator keyGen = null;
    private static GenerateCSR2 gcsr = null;

    public static final String KEYSTORE = "/Users/startsi/Documents/csr/pdf/new_KeyStore.keystore";//keystore文件路径
    public static final char[] PASSWORD = "123456".toCharArray();    // keystore密码

    private GenerateCSR2() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {

        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(new FileInputStream(KEYSTORE), PASSWORD);
        String alias = (String) ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        System.out.println(pk.getAlgorithm());

        publicKey = ks.getCertificate(alias).getPublicKey();
        privateKey = pk;
    }

    public static GenerateCSR2 getInstance() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        if (gcsr == null)
            gcsr = new GenerateCSR2();
        return gcsr;
    }

    public String getCSR(String cn) throws Exception {
        byte[] csr = generatePKCS10();
        return new String(csr);
    }

    private static byte[] generatePKCS10() throws Exception {
        // generate PKCS10 certificate request
        String sigAlg = "SHA1withRSA";
        PKCS10 pkcs10 = new PKCS10(publicKey);
        Signature signature = Signature.getInstance(sigAlg);
        signature.initSign(privateKey);
        // common, orgUnit, org, locality, state, country
        X500Principal principal = new X500Principal( "CN=www.ctbri.com, OU=IT, O=startsi, L=beijing, ST=beijing, C=beijing");

        //     pkcs10CertificationRequest kpGen = new PKCS10CertificationRequest(sigAlg, principal, publicKey, null, privateKey);
        //   byte[] c = kpGen.getEncoded();
        X500Name x500name=null;
        x500name= new X500Name(principal.getEncoded());
        pkcs10.encodeAndSign(x500name, signature);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bs);
        pkcs10.print(ps);
        byte[] c = bs.toByteArray();
        try {
            if (ps != null)
                ps.close();
            if (bs != null)
                bs.close();
        } catch (Throwable th) {
        }
        return c;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static void main(String[] args) throws Exception {
        GenerateCSR2 gcsr = GenerateCSR2.getInstance();

        System.out.println("Public Key:\n"+gcsr.getPublicKey().toString());

        System.out.println("Private Key:\n"+gcsr.getPrivateKey().toString());
        String csr = gcsr.getCSR("");
        System.out.println("CSR Request Generated!!");
        System.out.println(csr);
    }
}
