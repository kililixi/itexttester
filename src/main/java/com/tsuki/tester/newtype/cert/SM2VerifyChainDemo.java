package com.tsuki.tester.newtype.cert;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collection;

/**
 * SM2 证书验证证书链
 * <p>
 *
 */
public class SM2VerifyChainDemo {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // 该文件的生成可以参考  SM2CARootKeyStoreDemo
        String p12File = "/Users/startsi/Documents/csr/newesign/root.cer";

        String out = "/Users/startsi/Documents/csr/newesign/user2.cer";

        // 默认不支持sm2加密，所以提供BouncyCastleProvider provider
        final CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");

        byte[] rootBytes = Files.readAllBytes(Paths.get(p12File));
        // 这里的cer导出时是base64格式的，所以先从base64转为二进制的
        // 如果是二进制der编码的证书，则不需要先解码
        X509Certificate root = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(rootBytes)));
        root.checkValidity();
        PublicKey rootp = root.getPublicKey();

        byte[] bytes = Files.readAllBytes(Paths.get(out));
        X509Certificate cer = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(bytes));

        cer.verify(rootp);
        System.out.println("fuhe");

    }
}
