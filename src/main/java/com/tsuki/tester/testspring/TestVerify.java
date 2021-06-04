package com.tsuki.tester.testspring;

import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;

import java.io.*;
import java.util.List;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-01 17:41
 **/
public class TestVerify {

    public static final String SRC = "/Users/startsi/Downloads/responsen.pdf";
    public static final String DEST = "/Users/startsi/Downloads/output.pdf";

    public static void main(String[] args) {
        verifyPdf(null);
    }

    public static boolean verifyPdf(byte[] pdf) {

        boolean result = false;

        try {
            PdfReader pdfReader = new PdfReader(SRC);
//            PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(pdf));
            PdfDocument pdfDocument = new PdfDocument(pdfReader);
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            List<String> signedNames = signatureUtil.getSignatureNames();

            //遍历签名的内容并做验签
            for (String signedName : signedNames) {

                System.out.println(signedName);
//                //获取源数据
                byte[] originData = getOriginData(pdfReader, signatureUtil, signedName);

                FileOutputStream output = new FileOutputStream(new File(DEST));
                output.write(originData);
                output.flush();
//
//                //获取签名值
//                byte[] signedData = getSignData(signatureUtil , signedName);
//
//                //校验签名
//                result = SignUtil.verifyP7DetachData(originData , signedData);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取签名数据
     * @param signatureUtil
     * @param signedName
     * @return
     */
    private byte[] getSignData(SignatureUtil signatureUtil, String signedName) {
        PdfDictionary pdfDictionary = signatureUtil.getSignatureDictionary(signedName);
        PdfString contents = pdfDictionary.getAsString(PdfName.Contents);
        return contents.getValueBytes();
    }

    /**
     * 获取源数据（如果subFilter使用的是Adbe.pkcs7.detached就需要在验签的时候获取 源数据 并与 签名数据 进行 p7detach 校验）
     * @param pdfReader
     * @param signatureUtil
     * @param signedName
     * @return
     */
    private static byte[] getOriginData(PdfReader pdfReader, SignatureUtil signatureUtil, String signedName) {

        byte[] originData = null;

        try {
            PdfSignature pdfSignature = signatureUtil.getSignature(signedName);
            PdfArray pdfArray = pdfSignature.getByteRange();
            RandomAccessFileOrArray randomAccessFileOrArray = pdfReader.getSafeFile();
            InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(randomAccessFileOrArray.createSourceView(), SignatureUtil.asLongArray(pdfArray)));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n = 0;
            while (-1 != (n = rg.read(buf))) {
                outputStream.write(buf, 0, n);
            }

            originData = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return originData;

    }
}
