package tn.esprit.se.pispring.utils;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ReportAbstract {
    public XSSFWorkbook workbook;
    public XSSFSheet sheet;

    public void newReportExcel() {
        workbook = new XSSFWorkbook();
    }

    public HttpServletResponse initResponseForExportExcel(HttpServletResponse response, String fileName) {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName + "_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        return response;
    }

    public void writeTableHeaderExcel(String sheetName, String titleName, String[] headers) {

        // sheet
        sheet = workbook.createSheet(sheetName);
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);


        // title
        createCell(row, 0, titleName, style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
        font.setFontHeightInPoints((short) 10);

        // header
        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }
    }

    public void createCell(org.apache.poi.ss.usermodel.Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    public CellStyle getFontContentExcel() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        return style;
    }

    private CellStyle createHyperlinkStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        return style;
    }
    public void createSheetLink(String workSheetName){
        CreationHelper creationHelper = workbook.getCreationHelper();
        XSSFSheet workSheet = workbook.getSheet(workSheetName);
        int noOfRows = workSheet.getLastRowNum() + 1;
        for(int i= 2; i< noOfRows; i++){
            Hyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
            Row row = workSheet.getRow(i);
            Cell cell = row.getCell(0);
            String targetSheet = cell.getStringCellValue();
            System.out.println("targetSheet "+targetSheet);
            hyperlink.setAddress("'"+targetSheet+"'"+"!A1");
            cell.setHyperlink(hyperlink);
            cell.setCellStyle(createHyperlinkStyle());
        }
    }

}

