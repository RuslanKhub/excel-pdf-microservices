package com.example.excel_microservice.service;

import com.example.excel_microservice.model.entity.Employee;
import java.io.FileNotFoundException;
import java.util.List;
import net.sf.jasperreports.engine.JRException;

public interface ReportService {

  byte[] exportReport(List<Employee> employees) throws FileNotFoundException, JRException;
}
