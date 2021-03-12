package com.tsuki.tester.itext;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class C4_07_ClientServerSigning2 {
    public static final String DEST = "/Users/startsi/Downloads/";

    public static final String SRC = "/Users/startsi/Downloads/ApplicationForm.pdf";
    public static final String CERT = "/Users/startsi/Documents/csr/pdf/hubcn.cer";
    public static final String ROOTCERT = "/Users/startsi/Documents/csr/pdf/rootca.cer";

//    public static final String KEYSTORE = "/Users/startsi/.keystore";
    public static final String KEYSTORE = "/Users/startsi/Documents/csr/pdf/hubc.keystore";
    public static final char[] PASSWORD = "123456".toCharArray();

    public static final String[] RESULT_FILES = new String[] {
            "hello_server.pdf"
    };

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        File file = new File(DEST);
        file.mkdirs();

        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate[] chain = new Certificate[2];
        chain[0] = factory.generateCertificate(new FileInputStream(CERT));
        chain[1] = factory.generateCertificate(new FileInputStream(ROOTCERT));

        new C4_07_ClientServerSigning2().sign(SRC, DEST + RESULT_FILES[0], chain, PdfSigner.CryptoStandard.CMS,
                "Test", "Ghent");
    }

    public void sign(String src, String dest, Certificate[] chain, PdfSigner.CryptoStandard subfilter,
                     String reason, String location) throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        // Create the signature appearance
        Rectangle rect = new Rectangle(36, 648, 200, 100);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setReason(reason)
                .setLocation(location)
                .setPageRect(rect)
                .setPageNumber(1);
        signer.setFieldName("sig");

        IExternalDigest digest = new BouncyCastleDigest();

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(KEYSTORE), PASSWORD);
        String alias = ks.aliases().nextElement();

        alias = "hubcn";
        System.out.println(alias);
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);

//        chain = ks.getCertificateChain(alias);

        System.out.println(provider.getName());
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider.getName());
        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null,
                0, subfilter);
    }
}