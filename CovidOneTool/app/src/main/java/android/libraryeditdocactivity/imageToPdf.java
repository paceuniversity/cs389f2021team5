package android.libraryeditdocactivity;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;

public class imageToPdf {
    public static void imageToPDF(String imgPaths, String pdf_save_address) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdf_save_address));
            document.open();
            Image img = Image.getInstance(imgPaths);
            float scale = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scale);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
