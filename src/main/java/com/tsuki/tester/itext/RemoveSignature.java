package com.tsuki.tester.itext;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.signatures.SignatureUtil;

import java.io.*;
import java.util.List;
import java.util.Map;

public class RemoveSignature {

    public static final String DEST = "/Users/startsi/Downloads/aaaaaaaaaa.pdf";
//    public static final String SRC = "/Users/startsi/Downloads/cmp_step6_signed_by_alice_bob_carol_and_dave.pdf";
    public static final String SRC = "/Users/startsi/Downloads/input_signed.pdf";

    public static void main(String[] args) throws Exception {
        new RemoveSignature().manipulatePdf(DEST);
    }

    protected void manipulatePdf3() throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        String lastName = clearLastSignature(pdfDoc);
        pdfDoc.close();
    }

    String clearLastSignature(PdfDocument pdfDocument) {
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

        List<String> signatureNames = signatureUtil.getSignatureNames();
        if (signatureNames != null && signatureNames.size() > 0) {
            String lastSignatureName = signatureNames.get(signatureNames.size() - 1);
            PdfFormField lastSignatureField = acroForm.getField(lastSignatureName);
            if (null != lastSignatureField.getPdfObject().remove(PdfName.V))
                lastSignatureField.getPdfObject().setModified();
            for (PdfWidgetAnnotation pdfWidgetAnnotation : lastSignatureField.getWidgets()) {
                if (pdfWidgetAnnotation.getPdfObject().remove(PdfName.AP) != null)
                    pdfWidgetAnnotation.getPdfObject().setModified();
            }
            return lastSignatureName;
        }
        return null;
    }


    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));
//        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);
//        System.out.println(form.getDefaultAppearance());
//        Map<String, PdfFormField> aa = form.getFormFields();
//        aa.forEach( (key, value) -> {
//            System.out.println(key);
//            System.out.println(value);
//        });
//        PdfFormField fff = form.getField("star2");
//        form.partialFormFlattening("sig");
//        form.flattenFields();
//        form.removeField("star2");
        pdfDoc.close();
    }

    protected void manipulatePdf2( byte[] src , String dest) throws Exception {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

//        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(bos));
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new RandomAccessSourceFactory().createSource(src) , new ReaderProperties()), new PdfWriter(bos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
//        System.out.println(form.getDefaultAppearance());
//        form.get
//        form = form.getField("");
//        Map<String, PdfFormField> aa = form.getFormFields();
//        aa.forEach( (key, value) -> {
//            System.out.println(key);
//            System.out.println(value);
//        });

//        form.flattenFields();
        form.removeField("star");
        pdfDoc.close();

        FileOutputStream fos = new FileOutputStream(new File("/Users/startsi/Downloads/response1_remove.pdf"));
        byte[] a = bos.toByteArray();
        bos.close();
        fos.write(a);
        fos.close();
    }

    public static byte[] removeSign(byte[] src  // 需要撤章的pdf文件
            , String field // 盖章的field
    ) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new RandomAccessSourceFactory().createSource(src) , new ReaderProperties());
        PdfDocument pdfDoc = new PdfDocument( reader, new PdfWriter(bos));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        form.removeField(field);

        byte[] removeSignPdf = bos.toByteArray();
        bos.close();
        reader.close();

        return removeSignPdf;
    }

    public void tess() throws Exception {

        FileInputStream fis = new FileInputStream(new File(SRC));

        byte[] byt = new byte[fis.available()];

        fis.read(byt);
        manipulatePdf2(byt, null);
//        removeSign(byt, "star");
    }




}