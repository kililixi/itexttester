package com.tsuki.tester.itext;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;

public class C5_01_SignatureIntegrity {
    public static final String DEST = "/Users/startsi/Downloads/";

    public static final String EXAMPLE1 = "hello_server.pdf";
    public static final String EXAMPLE2 = "hello_signed2.pdf";
    public static final String EXAMPLE3 = "hello_signed3.pdf";
    public static final String EXAMPLE4 = "hello_signed4.pdf";
    public static final String EXAMPLE5 = "ApplicationForm2.pdf";

    public static final String EXPECTED_OUTPUT = "./src/test/resources/pdfs/hello_level_1_annotated.pdf\n" +
            "===== sig =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 1 of 2\n" +
            "Integrity check OK? true\n" +
            "./src/test/resources/pdfs/step_4_signed_by_alice_bob_carol_and_dave.pdf\n" +
            "===== sig1 =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 1 of 4\n" +
            "Integrity check OK? true\n" +
            "===== sig2 =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 2 of 4\n" +
            "Integrity check OK? true\n" +
            "===== sig3 =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 3 of 4\n" +
            "Integrity check OK? true\n" +
            "===== sig4 =====\n" +
            "Signature covers whole document: true\n" +
            "Document revision: 4 of 4\n" +
            "Integrity check OK? true\n" +
            "./src/test/resources/pdfs/step_6_signed_by_dave_broken_by_chuck.pdf\n" +
            "===== sig1 =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 1 of 5\n" +
            "Integrity check OK? true\n" +
            "===== sig2 =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 2 of 5\n" +
            "Integrity check OK? true\n" +
            "===== sig3 =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 3 of 5\n" +
            "Integrity check OK? true\n" +
            "===== sig4 =====\n" +
            "Signature covers whole document: false\n" +
            "Document revision: 4 of 5\n" +
            "Integrity check OK? true\n";

    public void verifySignatures(String path) throws IOException, GeneralSecurityException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path));
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        List<String> names = signUtil.getSignatureNames();

        System.out.println(path);
        for (String name : names) {
            System.out.println("===== " + name + " =====");
            verifySignature(signUtil, name);
        }
        System.out.println("");
        pdfDoc.close();
    }

    public PdfPKCS7 verifySignature(SignatureUtil signUtil, String name) throws GeneralSecurityException {
        PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
        System.out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
        System.out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
        System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
        System.out.println("Integrity check OK? " + pkcs7.getSigningInfoVersion());

        return pkcs7;
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        C5_01_SignatureIntegrity app = new C5_01_SignatureIntegrity();
        app.verifySignatures(DEST + EXAMPLE1);
//        app.verifySignatures(DEST + EXAMPLE2);
//        app.verifySignatures(DEST + EXAMPLE3);
//        app.verifySignatures(DEST + EXAMPLE4);
//        app.verifySignatures(DEST + EXAMPLE5);
    }
}