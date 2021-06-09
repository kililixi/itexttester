package com.tsuki.tester.newtype.sign;

import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-07 14:21
 **/
public class NewSign {

    public static final String SRC = "/Users/startsi/Downloads/dddd.pdf";//需要盖章的pdf文件路径
    public static final String DEST = "/Users/startsi/Downloads/signed.pdf";
    public static final String p12File = "/Users/startsi/Documents/csr/newesign/user.p12";
    int estimatedSize = 80;

    public byte[] source = null;

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        NewSign newSign = new NewSign();
        System.out.println("--------------签名--------------");
        newSign.sign();
        System.out.println("--------------验签--------------");
        System.out.println(newSign.verySign());
    }

    public PrivateKey getPrivateKey() throws NoSuchProviderException, KeyStoreException {
        char[] pwd = "111111".toCharArray();
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File)) {
            store.load(fIn, pwd);
            PrivateKey privateKey = (PrivateKey) store.getKey("private", pwd);
//            System.out.println("privateKey:" + privateKey.toString());
            return privateKey;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PublicKey getPublicKey() throws NoSuchProviderException, KeyStoreException {
        char[] pwd = "111111".toCharArray();
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File)) {
            store.load(fIn, pwd);
            Certificate cert = store.getCertificate("private");
//            X509Certificate cert = store.getCertificateChain("private")[0];
            PublicKey pubkey = cert.getPublicKey();
//            System.out.println("publickey:" + pubkey.toString());
            return pubkey;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void sign() throws IOException, GeneralSecurityException {

        File outputFile = new File(DEST);
        if(outputFile.exists()) {
            outputFile.delete();
        }

        PdfReader pdfReader = new PdfReader(SRC);
        PdfSigner pdfSigner = new PdfSigner(pdfReader, new FileOutputStream(DEST), false);

        //estimatedSize可以自己设置预估大小
        //但建议开启一个循环来判断，如果太小就增大值，直到签名成功
//        pdfSigner.get
        pdfSigner.signExternalContainer(externalP7DetachSignatureContainer, estimatedSize);
    }

    public PdfPKCS7 verifySignature(SignatureUtil signUtil, String name) throws GeneralSecurityException {
//            PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
            System.out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
            System.out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
//            System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
//            System.out.println("Integrity check OK? " + pkcs7.getSigningInfoVersion());
            return null;
    }

    IExternalSignatureContainer externalP7DetachSignatureContainer = new IExternalSignatureContainer() {
        @Override
        public byte[] sign(InputStream data) throws GeneralSecurityException {

            //将要签名的数据进行 P7不带原文 签名
            byte[] result = null;
            try {
                result = SignUtil.P7DetachSigned(data, getPrivateKey());
//                source = result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        //必须设置 PdfName.Filter 和 PdfName.SubFilter
        @Override
        public void modifySigningDictionary(PdfDictionary signDic) {
            signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
            //注意这里
            signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
        }
    };

    public boolean verySign() {
        boolean result = false;

        try {
            PdfReader pdfReader = new PdfReader(DEST);
            PdfDocument pdfDocument = new PdfDocument(pdfReader);
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            List<String> signedNames = signatureUtil.getSignatureNames();

            //遍历签名的内容并做验签
            for (String signedName : signedNames) {
                System.out.println("signname: " + signedName);
                // 校验签名
                verifySignature(signatureUtil, signedName);

                //获取源数据
                byte[] originData = getOriginData(pdfReader, signatureUtil, signedName);
                //获取签名值
                byte[] signedData = getSignData(signatureUtil , signedName);
                //校验签名
                result = SignUtil.verifyP7DetachData( originData , signedData, getPublicKey());
                System.out.println("signname result: " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取源数据（如果subFilter使用的是Adbe.pkcs7.detached就需要在验签的时候获取 源数据 并与 签名数据 进行 p7detach 校验）
     * @param pdfReader
     * @param signatureUtil
     * @param signedName
     * @return
     */
    private byte[] getOriginData(PdfReader pdfReader, SignatureUtil signatureUtil, String signedName) throws IOException {
        byte[] originData = null;

        try {
            PdfSignature pdfSignature = signatureUtil.getSignature(signedName);
            PdfArray pdfArray = pdfSignature.getByteRange();
            RandomAccessFileOrArray randomAccessFileOrArray = pdfReader.getSafeFile();
            long[] longs = pdfArray.toLongArray();
            Arrays.stream(longs).forEach(System.out::println);

//                                    new RASInputStream(fac.createRanged(getUnderlyingSource(), range))
            RASInputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(randomAccessFileOrArray.createSourceView(), longs));
            System.out.println("rg: " + rg.available());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n = 0;
            while (-1 != (n = rg.read(buf, 0, buf.length))) {
                outputStream.write(buf, 0, n);
            }
            outputStream.flush();
            originData = outputStream.toByteArray();

            FileOutputStream fio = new FileOutputStream(new File(DEST + "2"));
            fio.write(originData, 0, originData.length);
            fio.flush();

//            byte[] newData = new byte[originData.length - 25];
//            System.arraycopy(originData, 0, newData, 0, originData.length - 25);
            return originData;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return originData;

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
}
