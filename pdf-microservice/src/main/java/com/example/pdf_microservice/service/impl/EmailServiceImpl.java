package com.example.pdf_microservice.service.impl;

import com.example.pdf_microservice.config.MailProperties;
import com.example.pdf_microservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final MailProperties properties;

  @Override
  public void sendPdfEmail(String to, String subject, String text, File pdfFile) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(text);
    helper.setFrom(properties.getUsername());

    FileSystemResource file = new FileSystemResource(pdfFile);
    helper.addAttachment("report.pdf", file);

    mailSender.send(message);
  }
}
