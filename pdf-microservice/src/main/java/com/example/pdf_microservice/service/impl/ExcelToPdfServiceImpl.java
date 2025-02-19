package com.example.pdf_microservice.service.impl;

import com.example.pdf_microservice.service.EmailService;
import com.example.pdf_microservice.service.ExcelToPdfService;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelToPdfServiceImpl implements ExcelToPdfService {

  private final EmailService emailService;

  @Override
  @KafkaListener(topics = "file-download-links", groupId = "file-download-group")
  public void listenDownloadLink(String downloadUrl) {
    try {
      byte[] excelData = downloadFile(downloadUrl);

      File tempExcelFile = File.createTempFile("temp-excel", ".xlsx");
      try (FileOutputStream fos = new FileOutputStream(tempExcelFile)) {
        fos.write(excelData);
      }

      File pdfFile = convertExcelToPdf(tempExcelFile);
      emailService.sendPdfEmail(
              "hubijoker@yandex.ru",
              "employee report",
              "employee report",
              pdfFile);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка обработки XLSX в PDF", e);
    }
  }

  private byte[] downloadFile(String fileUrl) throws IOException {
    URL url = new URL(fileUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException("Ошибка загрузки файла: " + connection.getResponseMessage());
    }

    try (InputStream inputStream = connection.getInputStream();
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }

      return outputStream.toByteArray();
    }
  }

  private File convertExcelToPdf(File excelFile) throws Exception {
    File pdfFile = File.createTempFile("report", ".pdf");

    try (InputStream excelStream = new FileInputStream(excelFile);
         OutputStream pdfStream = new FileOutputStream(pdfFile)) {

      Workbook workbook = new XSSFWorkbook(excelStream);
      Sheet sheet = workbook.getSheetAt(0);

      Document document = new Document();
      PdfWriter.getInstance(document, pdfStream);
      document.open();

      PdfPTable table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells());
      table.setWidthPercentage(100);

      for (Cell cell : sheet.getRow(0)) {
        table.addCell(new PdfPCell(new Phrase(cell.getStringCellValue(), FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
      }

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        for (Cell cell : row) {
          table.addCell(cell.toString());
        }
      }

      document.add(table);
      document.close();
      workbook.close();
    }
    return pdfFile;
  }
}
