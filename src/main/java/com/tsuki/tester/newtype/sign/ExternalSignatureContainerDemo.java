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
 * @program: demo
 * @description:
 * @create: 2021-06-09 09:51
 **/
public class ExternalSignatureContainerDemo {

    public static final String SRC = "/Users/startsi/Downloads/dddd.pdf";
    public static final String DEST = "/Users/startsi/Downloads/signed.pdf";
//
//    public static final String SRC = "/Users/startsi/Downloads/signed2.pdf";
//    public static final String DEST = "/Users/startsi/Downloads/signed3.pdf";

    public static final String p12File = "/Users/startsi/Documents/csr/newesign/user.p12";

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        ExternalSignatureContainerDemo newSign = new ExternalSignatureContainerDemo();
        // sign pdf document
        newSign.sign();
        // verify signature
        newSign.verySign();
    }

    public void sign() throws IOException, GeneralSecurityException {
        int estimatedSize = 100;

        File outputFile = new File(DEST);
        if(outputFile.exists()) {
            outputFile.delete();
        }

        IExternalSignatureContainer externalP7DetachSignatureContainer = new IExternalSignatureContainer() {
            @Override
            public byte[] sign(InputStream data) throws GeneralSecurityException {

                byte[] result = null;
                try {
                    result = SignContainerUtil.P7DetachSigned(data, getPrivateKey(), estimatedSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            public void modifySigningDictionary(PdfDictionary signDic) {
                signDic.put(PdfName.Filter, PdfName.Adobe_PPKLite);
                signDic.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
            }
        };

        PdfReader pdfReader = new PdfReader(SRC);
        StampingProperties pros = new StampingProperties();
//        pros.useAppendMode();
        StartsiSigner pdfSigner = new StartsiSigner(pdfReader, new FileOutputStream(DEST), pros);
//        pdfSigner.setCertificationLevel(PdfSigner.NOT_CERTIFIED);
//        pdfSigner.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);
        pdfSigner.signExternalContainer(externalP7DetachSignatureContainer, estimatedSize);
    }

    public boolean verySign() {
        boolean result = false;

        try {
            PdfReader pdfReader = new PdfReader(DEST);
            PdfDocument pdfDocument = new PdfDocument(pdfReader);
            SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
            List<String> signedNames = signatureUtil.getSignatureNames();

            for (String signedName : signedNames) {
                System.out.println("signname: " + signedName);

                System.out.println("Signature covers whole document: " + signatureUtil.signatureCoversWholeDocument(signedName));
                System.out.println("Document revision: " + signatureUtil.getRevision(signedName) + " of " + signatureUtil.getTotalRevisions());

                // original pdf document data
                byte[] originData = getOriginData(pdfReader, signatureUtil, signedName);
                // signature data
                byte[] signedData = getSignData(signatureUtil , signedName);

                result = SignContainerUtil.verifyP7DetachData( originData , signedData, getPublicKey());
                System.out.println("signname result: " + result);
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * get the original pdf document by byterange
     * @param pdfReader
     * @param signatureUtil
     * @param signedName
     * @return
     * @throws IOException
     */
    private byte[] getOriginData(PdfReader pdfReader, SignatureUtil signatureUtil, String signedName) throws IOException {
        byte[] originData = null;

        try {
            PdfSignature pdfSignature = signatureUtil.getSignature(signedName);
            PdfArray pdfArray = pdfSignature.getByteRange();
            long[] longs = pdfArray.toLongArray();
            Arrays.stream(longs).forEach(System.out::println);
            RandomAccessFileOrArray randomAccessFileOrArray = pdfReader.getSafeFile();
            RASInputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(randomAccessFileOrArray.createSourceView(), longs));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n = 0;
            while (-1 != (n = rg.read(buf, 0, buf.length))) {
                outputStream.write(buf, 0, n);
            }
            outputStream.flush();
            originData = outputStream.toByteArray();

            return originData;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return originData;

    }

    /**
     * return signature data
     * @param signatureUtil
     * @param signedName
     * @return
     */
    private byte[] getSignData(SignatureUtil signatureUtil, String signedName) {
        PdfDictionary pdfDictionary = signatureUtil.getSignatureDictionary(signedName);
        PdfString contents = pdfDictionary.getAsString(PdfName.Contents);
        return contents.getValueBytes();
    }

    // return private key
    public PrivateKey getPrivateKey() throws NoSuchProviderException, KeyStoreException {
        char[] pwd = "111111".toCharArray();
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File)) {
            store.load(fIn, pwd);
            PrivateKey privateKey = (PrivateKey) store.getKey("private", pwd);
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

    // return public key
    public PublicKey getPublicKey() throws NoSuchProviderException, KeyStoreException {
        char[] pwd = "111111".toCharArray();
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        try (FileInputStream fIn = new FileInputStream(p12File)) {
            store.load(fIn, pwd);
            Certificate cert = store.getCertificate("private");
            PublicKey pubkey = cert.getPublicKey();
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
}
