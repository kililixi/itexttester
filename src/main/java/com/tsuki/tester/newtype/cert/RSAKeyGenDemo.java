package com.tsuki.tester.newtype.cert;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.encoders.Base64;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * RSA 密钥对生成并存储
 *
 * @author 权观宇
 * @since 2020-04-30 09:25:00
 */
public class RSAKeyGenDemo {

    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, OperatorCreationException, IOException, KeyStoreException, CertificateException {
        Security.addProvider(new BouncyCastleProvider());
        int length = 1024;
        /*
         * 生成密钥对
         */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(length);
        KeyPair kp = keyPairGenerator.generateKeyPair();

        /*
         * 构造证书请求
         */
        X500Name subject = SM2PKCS10Tools.dn();
        PKCS10CertificationRequest req = SM2PKCS10Tools.generate("sha1withRsa",kp, subject);
        String p10 = Base64.toBase64String(req.getEncoded());


        /*
         * 生成证书
         */
        String p12File = "/Users/startsi/Documents/csr/esign/airport.p12";
        char[] pwd = "111111".toCharArray();
        X509Certificate userCert = null;
        Certificate root = null;
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File)) {
            store.load(fIn, pwd);
            Enumeration<String> aaa = store.aliases();
            while(aaa.hasMoreElements()) {
                System.out.println(aaa.nextElement());
            }
            root = store.getCertificateChain("serverkey")[0];
            PrivateKey privateKey = (PrivateKey) store.getKey("serverkey", pwd);
            userCert = SM2SubCertGenerateDemo.issue(p10, root, privateKey);
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (PKCSException e) {
            e.printStackTrace();
        }

        /*
         * 存储证书和密钥
         */
        store = KeyStore.getInstance("PKCS12", "BC");
        // 3.1 初始化
        store.load(null, null);
        // 3.2 写入证书以及公钥
        store.setKeyEntry("private", kp.getPrivate(), pwd, new Certificate[]{userCert, root});
        try (FileOutputStream out = new FileOutputStream("/Users/startsi/Documents/csr/esign/DocuUseRSA" + length + ".p12");
             FileWriter fw = new FileWriter("UseRSA" + length + ".cer")) {
            // 3.3 加密写入文件
            store.store(out, pwd);
            fw.write(Base64.toBase64String(userCert.getEncoded()));
        }

    }
}
