/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valstreamtools;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provide the arguments to split the page page by page.
 *
 * @author Nagandla
 */
public class ValStrSplitPage {

    /**
     * Please provide args as splitNo which is how many times the page needs to
     * splitted.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args){
        try {
            new ValStrSplitPage().splitDocument(args);
        } catch (IOException ex) {
            Logger.getLogger(ValStrSplitPage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ValStrSplitPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void splitDocument(String[] args) throws IOException, DocumentException {
        /**
         * int splitNo = 4; String inputFileName =
         * "C:\\tmp\\valuestream_export_input_2015-03-03-22-18-37.pdf"; String
         * outputFileName = "C:\\tmp\\valuestream_export_op_2015-03-03-22-18-37.pdf";*
         */
        int splitNo = Integer.parseInt(args[0]);
        String inputFileName = args[1];
        if(splitNo > 1){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String splitOutFileName = "/var/tmp/PDF_SPLIT_" + sdf.format(new Date())+".pdf";
            PdfReader reader = new PdfReader(inputFileName);
            Rectangle pagesize = reader.getPageSize(1);
            float pageHeight = pagesize.getHeight();
            float newpagewidth = (pagesize.getWidth() / splitNo);
            Rectangle newPapeSize = new Rectangle(0, 0, newpagewidth, pageHeight);
            Document document = new Document(newPapeSize);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(splitOutFileName));
            document.open();
            int pageNos = reader.getNumberOfPages();
            PdfContentByte content = writer.getDirectContent();
            for (int i = 1; i <= pageNos; i++) {
                PdfImportedPage page = writer.getImportedPage(reader, i);
                if (i > 1) {// In case of a new inpit page, need to create this.
                    document.newPage();
                }
                for (int j = 0; j < splitNo; j++) {
                    if (j == 0) {//This condition is used to skip the adding of new page.
                        content.addTemplate(page, 0, 0);
                    } else {
                        document.newPage();
                        content.addTemplate(page, (-1 * j * newpagewidth), 0);
                    }
                }
            }
            document.close();
            reader.close();
            createPageNo(splitOutFileName, args[2], pageHeight);
            File f = new File(splitOutFileName);
            if(f.exists()){
                f.delete();
            }
        }
        else{
            PdfReader reader = new PdfReader(inputFileName);
            float pageHeight = reader.getPageSize(1).getHeight();
            reader.close();
            createPageNo(inputFileName, args[2], pageHeight);
        }
    }

    private void createPageNo(String inputFileName, String outputFileName, float pageHeight ) throws IOException, DocumentException {
        
        PdfReader pdfReader = new PdfReader(inputFileName);
        PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream(outputFileName));
        int pageCount = pdfReader.getNumberOfPages();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);
        for(int i=1;i<=pageCount;i++){            
            PdfContentByte content = pdfStamper.getOverContent(i);
                content.beginText();
                content.setFontAndSize(bf, 8);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT,"Page " + i+"/"+pageCount,5, pageHeight-15,0);
                content.endText();
        }
        pdfStamper.close();        
        pdfReader.close();
    }
}
