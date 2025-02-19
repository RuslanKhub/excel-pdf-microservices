package com.example.pdf_microservice.service;

import jakarta.mail.MessagingException;
import java.io.File;

public interface EmailService {

  /**
   * Метод отправки сообщения на почту.
   * @param to почта для отправки
   * @param subject заголовок
   * @param text текст
   * @param pdfFile файл
   * @throws MessagingException ошибка.
   */
  void sendPdfEmail(String to, String subject, String text, File pdfFile) throws MessagingException;
}
