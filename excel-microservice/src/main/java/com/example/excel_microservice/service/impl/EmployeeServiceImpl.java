package com.example.excel_microservice.service.impl;

import com.example.excel_microservice.repository.EmployeeRepository;
import com.example.excel_microservice.service.EmployeeService;
import com.example.excel_microservice.service.MinioService;
import com.example.excel_microservice.service.ReportService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository repository;
  private final ReportService reportService;
  private final MinioService minioService;
  private final KafkaTemplate<String, String> kafkaTemplate;

  private static final String TOPIC = "file-download-links";

  @Override
  public Boolean doReport() {
    var employees = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    try {
      var report = reportService.exportReport(employees);
      String objectName = "employee_report_" + System.currentTimeMillis() + ".xlsx";

      try (InputStream inputStream = new ByteArrayInputStream(report)) {
        var downloadUrl = minioService.uploadFile(objectName, inputStream, report.length);
        kafkaTemplate.send(TOPIC, downloadUrl);
      } catch (Exception e) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
