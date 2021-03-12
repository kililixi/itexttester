package com.tsuki.tester.itext;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.layout.element.Image;
import com.itextpdf.signatures.*;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class SignPDF2 {
//    public static final String KEYSTORE = "/Users/startsi/Documents/csr/pdf/new_KeyStore.keystore";//keystore文件路径
//    public static final String KEYSTORE = "/Users/startsi/Documents/csr/pdf/nroot.keystore";//keystore文件路径
    public static final String KEYSTORE = "/Users/startsi/Documents/csr/esign/st.keystore";//keystore文件路径
//    public static final String KEYSTORE = "/Users/startsi/.keystore";//keystore文件路径
//    public static final String KEYSTORE = "/Users/startsi/Documents/csr/esign/aaa.keystore";//keystore文件路径

//    public static final char[] PASSWORD = "startsi".toCharArray();    // keystore密码
    public static final char[] PASSWORD = "123456".toCharArray();    // keystore密码
    public static final String SRC = "/Users/startsi/Downloads/ApplicationForm.pdf";//需要盖章的pdf文件路径
    public static final String DEST = "/Users/startsi/Downloads/ApplicationForm2.pdf";//盖章后生产的pdf文件路径
    public static final String stamperSrc = "/Users/startsi/Downloads/camera.png";//印章路径

//    public static final String CERT = "/Users/startsi/Documents/csr/pdf/hubc.cer";
    public static final String CERT = "/Users/startsi/Documents/csr/pdf/shayne.cer";
    public static final String INTERMEDIACERT = "/Users/startsi/Documents/csr/pdf/subca.cer";
    public static final String ROOTCERT = "/Users/startsi/Documents/csr/pdf/nrootca.cer";

    public void sign(String src  //需要签章的pdf文件路径
            , String dest  // 签完章的pdf文件路径
            , Certificate[] chain //证书链
            , PrivateKey pk //签名私钥
            , String digestAlgorithm  //摘要算法名称，例如SHA-1
            , String provider  // 密钥算法提供者，可以为null
            , CryptoStandard subfilter //数字签名格式，itext有2种
            , String reason  //签名的原因，显示在pdf签名属性中，随便填
            , String location) //签名的地点，显示在pdf签名属性中，随便填
            throws GeneralSecurityException, IOException {
        //下边的步骤都是固定的，照着写就行了，没啥要解释的
        PdfReader reader = new PdfReader(src);
//        PdfDocument document = new PdfDocument(reader);
//        document.setDefaultPageSize(PageSize.TABLOID);
        //目标文件输出流
        FileOutputStream os = new FileOutputStream(dest);
        //创建签章工具PdfSigner ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        PdfSigner stamper = new PdfSigner(reader, os, true);
                Rectangle rect = new Rectangle(36, 648, 200, 100);
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
                appearance
                        .setReason(reason)
                        .setLocation(location)
                        .setPageRect(rect)
                        .setPageNumber(1);
                stamper.setFieldName("sig");
        // 这里的itext提供了2个用于签名的接口，可以自己实现，后边着重说这个实现
        // 摘要算法
        IExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
        System.out.println(BouncyCastleProvider.PROVIDER_NAME);
        IExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        // 调用itext签名方法完成pdf签章
        Security.addProvider(new BouncyCastleProvider());
        stamper.signDetached(digest,signature, chain, null, null, null, 0, subfilter);
    }

    public static void main(String[] args) {
        try {
            // 读取keystore ，获得私钥和证书链 jks
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(new FileInputStream(KEYSTORE), PASSWORD);
            String alias = (String) ks.aliases().nextElement();
            System.out.println(alias);
            alias = "admin";
            PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
            System.out.println(ks);
            //            Certificate[] chain = ks.getCertificateChain(alias);

//            CertificateFactory factory = CertificateFactory.getInstance("X.509");
//            Certificate[] chain = new Certificate[3];
//            chain[0] = factory.generateCertificate(new FileInputStream(CERT));
//            chain[1] = factory.generateCertificate(new FileInputStream(INTERMEDIACERT));
//            chain[2] = factory.generateCertificate(new FileInputStream(ROOTCERT));
//
//            System.out.println(chain.length);
//            // new一个上边自定义的方法对象，调用签名方法
//            SignPDF2 app = new SignPDF2();
//            app.sign(SRC, String.format(DEST, 1), chain, pk, DigestAlgorithms.SHA256, null, CryptoStandard.CADES, "Test 1",
//                    "Ghent");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }
}