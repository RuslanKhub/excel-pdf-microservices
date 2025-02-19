package com.example.pdf_microservice.service;


public interface ExcelToPdfService {

  /**
   * Метод получения ссылки на файл xlsx и конвертации в pdf.
   * @param downloadUrl ссылка на файл.
   */
  void listenDownloadLink(String downloadUrl);
}
