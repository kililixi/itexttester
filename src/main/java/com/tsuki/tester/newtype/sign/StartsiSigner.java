package com.tsuki.tester.newtype.sign;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public InputStream getInputStream(int estimatedSize) throws IOException {
        Map<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.Contents, estimatedSize * 2 + 2);
        preClose(exc);
        return super.getRangeStream();
    }
}
