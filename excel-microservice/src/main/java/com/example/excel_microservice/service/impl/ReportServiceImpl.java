package com.example.excel_microservice.service.impl;

import com.example.excel_microservice.model.entity.Employee;
import com.example.excel_microservice.service.ReportService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class ReportServiceImpl implements ReportService {

  private static final String RESOURCE_LOCATION = "classpath:reports/employee_report.jrxml";

  @Override
  public byte[] exportReport(List<Employee> employees) throws FileNotFoundException, JRException {
    File template = ResourceUtils.getFile(RESOURCE_LOCATION);
    JasperReport jasperReport = JasperCompileManager.compileReport(template.getAbsolutePath());
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);
    Map<String, Object> parameters = new HashMap<>();

    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JRXlsxExporter exporter = getJrXlsxExporter(jasperPrint, outputStream);

    exporter.exportReport();

    return outputStream.toByteArray();
  }

  private JRXlsxExporter getJrXlsxExporter(JasperPrint jasperPrint, ByteArrayOutputStream outputStream) {
    JRXlsxExporter exporter = new JRXlsxExporter();
    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

    SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
    configuration.setOnePagePerSheet(false);
    configuration.setRemoveEmptySpaceBetweenRows(true);
    exporter.setConfiguration(configuration);
    return exporter;
  }
}
