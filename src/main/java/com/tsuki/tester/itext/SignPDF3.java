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

public class SignPDF3 {
//    public static final String KEYSTORE = "/Users/startsi/Documents/csr/pdf/new_KeyStore.keystore";//keystore文件路径
//    public static final String KEYSTORE = "/Users/startsi/Documents/csr/pdf/startsi-pdf.keystore";//keystore文件路径
    public static final String KEYSTORE = "/Users/startsi/.keystore";//keystore文件路径
    public static final char[] PASSWORD = "startsi".toCharArray();    // keystore密码
//    public static final char[] PASSWORD = "123456".toCharArray();    // keystore密码
    public static final String SRC = "/Users/startsi/Downloads/ApplicationForm.pdf";//需要盖章的pdf文件路径
    public static final String DEST = "/Users/startsi/Downloads/ApplicationForm2.pdf";//盖章后生产的pdf文件路径
    public static final String stamperSrc = "/Users/startsi/Downloads/sss.bmp";//印章路径

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
        stamper.setFieldName("startsi");
        // 获取数字签章属性对象，设定数字签章的属性
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
//        appearance.getLayer2();
        appearance.setReason(reason);
        appearance.setLocation(location);
        //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名与名称不能一样
        //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
        //四个参数的分别是，图章左下角x，图章左下角y，图章宽度，图章高度
        appearance.setPageNumber(1);
        appearance.setPageRect(new Rectangle(350, 100, 200, 100));
//        appearance.getPageRect().set
        //插入盖章图片

        // 摘要算法
        IExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
        IExternalSignature signature = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        // 调用itext签名方法完成pdf签章
        stamper.setCertificationLevel(1);
        Security.addProvider(new BouncyCastleProvider());
        stamper.signDetached(digest, signature, chain, null, null, null, 0, CryptoStandard.CADES);
    }

    public static void main(String[] args) {
        try {
            // 读取keystore ，获得私钥和证书链 jks
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(new FileInputStream(KEYSTORE), PASSWORD);
            String alias = (String) ks.aliases().nextElement();
            System.out.println(alias);
            alias = "hubc";
            PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
            Certificate[] chain = ks.getCertificateChain(alias);
            System.out.println(chain.length);
            // new一个上边自定义的方法对象，调用签名方法
            SignPDF3 app = new SignPDF3();
            app.sign(SRC, String.format(DEST, 1), chain, pk, DigestAlgorithms.SHA256, null, CryptoStandard.CADES, "Test 1",
                    "Ghent");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }
}