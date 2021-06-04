package com.tsuki.tester.itext;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.signatures.SignatureUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-03-12 18:36
 **/
public class FlattenSignature {

    public static final String DEST = "/Users/startsi/Downloads/ApplicationForm2_r.pdf";
//    public static final String SRC = "/Users/startsi/Downloads/input_signed.pdf";
    public static final String SRC = "/Users/startsi/Downloads/response2.pdf";
//    public static final String SRC = "/Users/startsi/Downloads/ApplicationForm2.pdf";
    public static final String SRC2 = "/Users/startsi/Downloads/hello_signed2.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        new FlattenSignature().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws Exception {
        StampingProperties properties = new StampingProperties();
        properties.useAppendMode();
//        properties.preserveEncryption();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest), properties);
//        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));
//        PdfReader reader = new PdfReader(SRC);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> aa = form.getFormFields();
        aa.forEach( (key, value) -> {
            System.out.println(key);
            System.out.println(value);
        });
        
        String delField = "star";
        PdfFormField field = form.getField(delField);

//        field.getPdfObject().remove(PdfName.V);
//        field.getPdfObject().setModified();

//        form.removeField("star2");
//        form.flattenFields();
        clearLastSignature(pdfDoc, delField);
        form.removeField(delField);
        pdfDoc.close();
    }

    String clearLastSignature(PdfDocument pdfDocument, String delField) {
//        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);
        PdfFormField lastSignatureField = acroForm.getField(delField);
        if (null != lastSignatureField.getPdfObject().remove(PdfName.V))
            lastSignatureField.getPdfObject().clear();
//            lastSignatureField.getPdfObject().setModified();
        for (PdfWidgetAnnotation pdfWidgetAnnotation : lastSignatureField.getWidgets()) {
            if (pdfWidgetAnnotation.getPdfObject().remove(PdfName.AP) != null)
                pdfWidgetAnnotation.getPdfObject().setModified();
//                pdfWidgetAnnotation.getPdfObject().clear();
        }
        return delField;
    }

    @Test
    public void test() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC2));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> aa = form.getFormFields();
        System.out.println(aa.size());
        aa.forEach( (key, value) -> {
            System.out.println(key);
            System.out.println(value);
        });
    }
}
