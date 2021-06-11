package com.tsuki.tester.newtype.sign;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-09 09:47
 **/
public class StartsiSigner extends PdfSigner {
    /**
     * Creates a PdfSigner instance. Uses a {@link ByteArrayOutputStream} instead of a temporary file.
     *
     * @param reader       PdfReader that reads the PDF file
     * @param outputStream OutputStream to write the signed PDF file
     * @param properties   {@link StampingProperties} for the signing document. Note that encryption will be
     *                     preserved regardless of what is set in properties.
     * @throws IOException if some I/O problem occurs
     */
    public StartsiSigner(PdfReader reader, OutputStream outputStream, StampingProperties properties) throws IOException {
        super(reader, outputStream, properties);
    }

    public void signExternalContainer(IExternalSignatureContainer externalSignatureContainer, int estimatedSize) throws GeneralSecurityException, IOException {
        if (closed) {
            throw new PdfException(PdfException.ThisInstanceOfPdfSignerAlreadyClosed);
        }

        PdfSignature dic = new PdfSignature();
        PdfSignatureAppearance appearance = getSignatureAppearance();
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setSignatureCreator(appearance.getSignatureCreator());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(getSignDate())); // time-stamp will over-rule this
        externalSignatureContainer.modifySigningDictionary(dic.getPdfObject());
        cryptoDictionary = dic;

        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, estimatedSize * 2 + 2);
        preClose(exc);

        InputStream data = getRangeStream();
        byte[] encodedSig = externalSignatureContainer.sign(data);
        byte[] paddedSig = new byte[estimatedSize];
        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);

        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.Contents, new PdfString(paddedSig).setHexWriting(true));
        close(dic2);

        closed = true;
    }


}
