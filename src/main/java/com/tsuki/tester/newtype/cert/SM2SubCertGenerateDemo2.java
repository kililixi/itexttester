package com.tsuki.tester.newtype.cert;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * SM2 证书签发
 * <p>
 * 需要一个存放了CA证书和私钥的P12文件，该文件的生成可以参考 {@link SM2CARootKeyStoreDemo}
 *
 * @author 权观宇
 * @since 2019-11-26 15:34:10
 */
public class SM2SubCertGenerateDemo2 {

    /**
     * 签发证书
     *
     * @param p10Str     证书请求Base64 编码
     * @param root       CA根证书
     * @param privateKey CA私钥
     * @return X509证书对象
     * @throws CertificateException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws OperatorCreationException
     */
    public static X509Certificate issue(String p10Str, Certificate root, PrivateKey privateKey) throws CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException, OperatorCreationException, PKCSException {

        // 1. 验证PKCS10 是否有效
        boolean isValid = SM2PKCS10Tools.verifyP10(p10Str);
        if (!isValid) {
            throw new IllegalArgumentException("非法的P10请求");
        }
        // 2. 解析PKCS10
        byte[] p10Der = Base64.decode(p10Str);
        JcaPKCS10CertificationRequest req = new JcaPKCS10CertificationRequest(p10Der)
                .setProvider("BC");
        // 从证书请求中获取到使用DN
        X500Name subject = req.getSubject();

        // 3. 取得根证书的Subject，签发证书的使用者就是根证书的使用者
        X500Name issuer = new X509CertificateHolder(root.getEncoded())
                .getSubject();

        // 4. 根据需求构造实体证书
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                // 颁发者信息
                issuer
                // 证书序列号
                , BigInteger.valueOf(Instant.now().toEpochMilli())
                // 证书生效日期
                , Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                // 证书失效日期(5天后)
                , Date.from(LocalDateTime.now().plusDays(5).atZone(ZoneId.systemDefault()).toInstant())
                // 使用者信息
                , subject
                // 证书公钥
                , req.getPublicKey())
                // 设置密钥用法
                .addExtension(Extension.keyUsage, false
                        , new X509KeyUsage(X509KeyUsage.digitalSignature | X509KeyUsage.nonRepudiation))
                // 设置扩展密钥用法：客户端身份认证
                .addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth))
                // 基础约束,标识是否是CA证书，这里false标识为实体证书
                .addExtension(Extension.basicConstraints, false, new BasicConstraints(false))
                // Netscape Cert Type SSL客户端身份认证
                .addExtension(MiscObjectIdentifiers.netscapeCertType, false, new NetscapeCertType(NetscapeCertType.sslClient));

        // 5. 证书签名实现类
        ContentSigner sigGen = new JcaContentSignerBuilder("SM3withSM2")
//        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(privateKey);

        // 6. 签发证书
        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certGen.build(sigGen));
    }

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // 该文件的生成可以参考  SM2CARootKeyStoreDemo
        String p12File = "/Users/startsi/Documents/csr/newesign/ROOT.p12";
        char[] pwd = "111111".toCharArray();

        String out = "/Users/startsi/Documents/csr/newesign/user2.cer";
        String out2 = "/Users/startsi/Documents/csr/newesign/user2.p12";
        File file = new File(out);
        if (file.exists()) {
            file.delete();
        }
        File file2 = new File(out2);
        if (file2.exists()) {
            file2.delete();
        }

        // 1. 载入P12得到证书和私钥
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File);
             FileOutputStream fw = new FileOutputStream(out)) {
            store.load(fIn, pwd);
            // 2. 取得CA根证书
            Certificate root = store.getCertificateChain("private")[0];
            // 3. 取得CA根证书的私钥
            PrivateKey privateKey = (PrivateKey) store.getKey("private", pwd);
            // 4-1. 生成证书请求文件
            KeyPair kp = SM2KeyGenerateFactory.generator().generateKeyPair();
            System.out.println("publicKey: " + kp.getPublic().toString());
            System.out.println("privateKey: " + kp.getPrivate().toString());
            X500Name subject = SM2PKCS10Tools.dn();
            PKCS10CertificationRequest req = SM2PKCS10Tools.generate("SM3withSM2", kp, subject);
            String p10 = Base64.toBase64String(req.getEncoded());
            // 4-2. 签发证书
            X509Certificate userCert = issue(p10, root, privateKey);

            // 5. 保存到本地.
            // base64
//            fw.write(Base64.toBase64String(userCert.getEncoded()));
            byte[] der = userCert.getEncoded();
            fw.write(der);
            // 6. 保存p12
            KeyStore store2 = KeyStore.getInstance("PKCS12", "BC");
            store.load(null, null);
            store.setKeyEntry("private", kp.getPrivate(), pwd, new Certificate[]{userCert});

            store.store(new FileOutputStream(out2), pwd);
        }
    }
}
