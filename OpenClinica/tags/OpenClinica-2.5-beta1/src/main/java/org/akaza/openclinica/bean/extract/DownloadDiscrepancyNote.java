package org.akaza.openclinica.bean.extract;

import javax.servlet.ServletOutputStream;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.control.extract.DiscrepancyNoteOutputServlet;

import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.*;

import java.io.OutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 *  This class converts or serializes DiscrepancyNoteBeans to Strings or iText-related
 * classes so that they can be compiled into a file and downloaded to the user. This is a
 * convenience class with a number of different methods for serializing beans to Strings.
 * @see org.akaza.openclinica.control.extract.DiscrepancyNoteOutputServlet
 * @author Bruce W. Perry
 *
 */
public class DownloadDiscrepancyNote implements DownLoadBean{
    public static String CSV ="text/plain";
    public static String PDF = "application/pdf";
    public static String COMMA = ",";
    public static Map<Integer,String> RESOLUTION_STATUS_MAP = new HashMap<Integer,String> ();
    static{
        RESOLUTION_STATUS_MAP.put(1,"Open");
        RESOLUTION_STATUS_MAP.put(2,"Updated");
        RESOLUTION_STATUS_MAP.put(3,"Resolved");
        RESOLUTION_STATUS_MAP.put(4,"Closed");
    }

    //Does the user want the first line of the CSV to be column headers
    private boolean firstColumnHeaderLine;
    //A list of DiscrepancyNoteBeans to be downloaded together
    private List<DiscrepancyNoteBean> discrepancyBeanList =
      new ArrayList<DiscrepancyNoteBean>();

    public DownloadDiscrepancyNote() {
        this.firstColumnHeaderLine = false;
    }

    public DownloadDiscrepancyNote(boolean firstColumnHeaderLine) {
        this.firstColumnHeaderLine = firstColumnHeaderLine;
    }

    public void downLoad(EntityBean bean,
                         String format,
                         OutputStream stream) {
        if(bean == null || stream == null ||
          !( bean instanceof org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean)){
            throw new IllegalStateException(
              "An invalid parameter was passed to the DownloadDiscrepancyNote.downLoad method.");
        }
        DiscrepancyNoteBean discNBean = (DiscrepancyNoteBean) bean;
        //This must be a ServletOutputStream for our purposes
        ServletOutputStream servletStream = (ServletOutputStream) stream;

        try{
            if(CSV.equalsIgnoreCase(format))  {
                servletStream.print(serializeToString(discNBean, false));
            } else {

                //Create PDF version
                serializeToPDF(discNBean,servletStream);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(servletStream != null){
                try {
                    servletStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void downLoad(List<EntityBean> listOfBeans, String format,
                         OutputStream stream) {

        //The List must be of DiscrepancyNoteBeans
        if (listOfBeans == null ) {
            return;
        }
        StringBuilder allContent = new StringBuilder();
        String singleBeanContent="";

        for(EntityBean discNoteBean : listOfBeans){
            if(! (discNoteBean instanceof DiscrepancyNoteBean)) return;

            DiscrepancyNoteBean discNBean = (DiscrepancyNoteBean) discNoteBean;
            singleBeanContent = serializeToString(discNBean, false);
            allContent.append(singleBeanContent);
            allContent.append("\n");

        }

        //This must be a ServletOutputStream for our purposes
        ServletOutputStream servletStream = (ServletOutputStream) stream;

        try{
            if(CSV.equalsIgnoreCase(format))  {
                servletStream.print(allContent.toString());
            } else {

                //Create PDF version
                serializeListToPDF(allContent.toString(),servletStream);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(servletStream != null){
                try {
                    servletStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public int getContentLength(EntityBean bean, String format) {
        return serializeToString(bean, false).getBytes().length;
    }

    public int getListContentLength(List<DiscrepancyNoteBean> beans, String format) {
        int totalLength = 0;
        int count = 0;
        for(DiscrepancyNoteBean bean : beans) {
            ++count;
            //Only count the byte length of a CSV header row for the first DNote
            totalLength += serializeToString(bean, (count == 1)).getBytes().length;
            totalLength += "\n".getBytes().length;

        }
        return totalLength;
    }

    public String serializeToString(EntityBean bean, boolean includeHeaderRow){

        DiscrepancyNoteBean discNoteBean = (DiscrepancyNoteBean) bean;
        StringBuilder writer  = new StringBuilder("");
        //If includeHeaderRow = true, the first row of the output consists of header names
        if(includeHeaderRow) {
            writer.append("id");
            writer.append(",");
            writer.append("Subject name");
            writer.append(",");
            writer.append("CRF name");
            writer.append(",");
            writer.append("Description");
            writer.append(",");
            if(discNoteBean.getDisType() != null)  {
                writer.append("Descrepancy type");
                writer.append(",");
            }

            writer.append("Event name");
            writer.append(",");
            writer.append("Resolution status");
            writer.append(",");
            writer.append("Detailed notes");
            writer.append(",");
            writer.append("Entity name");
            writer.append(",");
            writer.append("Entity value");
            writer.append(",");
            writer.append("Field");
            writer.append(",");
            writer.append("Last date updated");
            writer.append(",");
            writer.append("Study id");
            writer.append("\n");
        }

        //Fields with embedded commas must be
        // delimited with double-quote characters.
        writer.append(escapeQuotesInCSV(discNoteBean.getId()+""));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getSubjectName()));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getCrfName()));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getDescription()+""));
        writer.append(",");
        if(discNoteBean.getDisType() != null)  {
            writer.append(escapeQuotesInCSV(discNoteBean.getDisType().getName()));
            writer.append(",");
        }


        writer.append(escapeQuotesInCSV(discNoteBean.getEventName()));
        writer.append(",");

        writer.append(escapeQuotesInCSV(
          RESOLUTION_STATUS_MAP.get(discNoteBean.getResolutionStatusId())+""));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getDetailedNotes()+""));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getEntityName()));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getEntityValue()));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getField()));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getLastDateUpdated()+""));
        writer.append(",");

        writer.append(escapeQuotesInCSV(discNoteBean.getStudyId()+""));

        writer.append("\n");
        return writer.toString();


    }

    private void serializeToPDF(EntityBean bean, OutputStream stream) {

        ServletOutputStream servletStream = (ServletOutputStream) stream;
        DiscrepancyNoteBean discNBean = (DiscrepancyNoteBean) bean;
        StringBuilder writer  = new StringBuilder();
        writer.append(serializeToString(discNBean, false));

        Document pdfDoc = new Document();

        try {
            PdfWriter.getInstance(pdfDoc,
              servletStream);
            pdfDoc.open();
            pdfDoc.add(new Paragraph(writer.toString()));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        pdfDoc.close();

    }

    public void serializeListToPDF(String content, OutputStream stream) {

        ServletOutputStream servletStream = (ServletOutputStream) stream;

        Document pdfDoc = new Document();

        try {
            PdfWriter.getInstance(pdfDoc,
              servletStream);
            pdfDoc.open();
            pdfDoc.add(new Paragraph(content));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        pdfDoc.close();

    }

    public void serializeListToPDF(List<DiscrepancyNoteBean> listOfBeans,
                                   OutputStream stream) {

        ServletOutputStream servletStream = (ServletOutputStream) stream;

        Document pdfDoc = new Document();

        try {
            PdfWriter.getInstance(pdfDoc,
              servletStream);
            pdfDoc.open();
            for(DiscrepancyNoteBean discNoteBean : listOfBeans){
                pdfDoc.add(this.createTableFromBean(discNoteBean));
                pdfDoc.add(new Paragraph("\n"));
            }
            //pdfDoc.add(new Paragraph(content));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        pdfDoc.close();

    }


    public void downLoadDiscBeans(List<DiscrepancyNoteBean> listOfBeans,
                                  String format,
                                  OutputStream stream) {

        if (listOfBeans == null ) {
            return;
        }
        StringBuilder allContent = new StringBuilder();
        String singleBeanContent="";
        int counter=0;

        if(CSV.equalsIgnoreCase(format))  {

            for(DiscrepancyNoteBean discNoteBean : listOfBeans){
                ++counter;

                singleBeanContent = (counter == 1 ? serializeToString(discNoteBean, true) : serializeToString(discNoteBean, false));
                allContent.append(singleBeanContent);
                allContent.append("\n");

            }
        }

        //This must be a ServletOutputStream for our purposes
        ServletOutputStream servletStream = (ServletOutputStream) stream;

        try{
            if(CSV.equalsIgnoreCase(format))  {
                servletStream.print(allContent.toString());
            } else {

                //Create PDF version
                //  serializeListToPDF(allContent.toString(),servletStream);
                this.serializeListToPDF(listOfBeans,servletStream);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(servletStream != null){
                try {
                    servletStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String escapeQuotesInCSV(String csvValue){

        if(csvValue == null) return "";
        if(csvValue.contains(",")){

            return new StringBuilder("\"").append(csvValue).append("\"").toString();

        }  else {
            return csvValue;
        }

    }

    private Table createTableFromBean(DiscrepancyNoteBean discBean) throws
      BadElementException {

        Table table = new Table(2);
        table.setTableFitsPage(true);
        table.setCellsFitPage(true);
        table.setBorderWidth(1);
        table.setBorderColor(new java.awt.Color(0, 0, 0));
        table.setPadding(4);
        table.setSpacing(4);
        Cell cell = new Cell("Discrepancy note id: "+discBean.getId());
        cell.setHeader(true);
        cell.setColspan(2);
        table.addCell(cell);
        table.endHeaders();

        cell = new Cell("Subject name: "+discBean.getSubjectName());
        table.addCell(cell);
        cell = new Cell("CRF name: "+discBean.getCrfName());
        table.addCell(cell);
        cell = new Cell("Description: "+discBean.getDescription());
        table.addCell(cell);
        if(discBean.getDisType() != null)  {
            cell = new Cell("Discrepancy note type: "+discBean.getDisType().getName());
            table.addCell(cell);
        }
        cell = new Cell("Event name: "+discBean.getEventName());
        table.addCell(cell);
        cell = new Cell("Resolution status: "+discBean.getResolutionStatusId());
        table.addCell(cell);
        cell = new Cell("Detailed notes: "+discBean.getDetailedNotes());
        table.addCell(cell);
        cell = new Cell("Entity name: "+discBean.getEntityName());
        table.addCell(cell);
        cell = new Cell("Entity value: "+discBean.getEntityValue());
        table.addCell(cell);
        cell = new Cell("Field: "+discBean.getField());
        table.addCell(cell);
        cell = new Cell("Last date updated: "+discBean.getLastDateUpdated());
        table.addCell(cell);
        cell = new Cell("Study ID: "+discBean.getStudyId());
        table.addCell(cell);



        return table;

    }

}
