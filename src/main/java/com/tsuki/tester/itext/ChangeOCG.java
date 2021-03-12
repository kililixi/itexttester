package com.tsuki.tester.itext;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.layer.PdfLayer;

import java.io.File;
import java.util.List;

public class ChangeOCG {
    public static final String DEST = "/Users/startsi/Downloads/change_ocg.pdf";
    public static final String SRC = "/Users/startsi/Downloads/add_ocg.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        new ChangeOCG().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));

        List<PdfLayer> layers = pdfDoc.getCatalog().getOCProperties(true).getLayers();
        for (PdfLayer layer : layers) {
            System.out.println(layer.getPdfObject().get(PdfName.Name));
//            if ("Nested layer 1".equals(layer.getPdfObject().get(PdfName.Name).toString())) {
//                layer.setOn(false);
//                break;
//            }
        }

        pdfDoc.close();
    }
}