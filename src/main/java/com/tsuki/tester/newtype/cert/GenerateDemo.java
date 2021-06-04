package com.tsuki.tester.newtype.cert;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Date;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-04 09:28
 **/
public class GenerateDemo {

    private static final Provider BC = new BouncyCastleProvider();

    public GenerateDemo() throws NoSuchAlgorithmException {
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, CertIOException {
        // 获取一个椭圆曲线类型的密钥对生成器
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", BC);

        // 产生密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 构造X.509 第3版的证书构建者
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                // 颁发者信息
                createStdBuilder().build()
                // 证书序列号
                , BigInteger.valueOf(1)
                // 证书生效日期
                , new Date(System.currentTimeMillis() - 50 * 1000)
                // 证书失效日期
                , new Date(System.currentTimeMillis() + 50 * 1000)
                // 使用者信息（PS：由于是自签证书，所以颁发者和使用者DN都相同）
                , createStdBuilder().build()
                // 证书公钥
                , keyPair.getPublic())
                /*
                设置证书扩展
                证书扩展属性，请根据需求设定，参数请参考 《RFC 5280》
                 */
                // 设置密钥用法
                .addExtension(Extension.keyUsage, false
                        , new X509KeyUsage(X509KeyUsage.digitalSignature | X509KeyUsage.nonRepudiation))
                // 设置扩展密钥用法：客户端身份认证、安全电子邮件
//                .addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage())
                // 基础约束,标识是否是CA证书，这里false标识为实体证书
                .addExtension(Extension.basicConstraints, false, new BasicConstraints(false))
                // Netscape Cert Type SSL客户端身份认证
                .addExtension(MiscObjectIdentifiers.netscapeCertType, false, new NetscapeCertType(NetscapeCertType.sslClient));

    }

    private static X500NameBuilder createStdBuilder() {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        // 国家代码
        builder.addRDN(BCStyle.C, "CN");
        // 组织
        builder.addRDN(BCStyle.O, "HZNU");
        // 省份
        builder.addRDN(BCStyle.ST, "Zhejiang");
        // 地区
        builder.addRDN(BCStyle.L, "Hangzhou");
        return builder;
    }
}
