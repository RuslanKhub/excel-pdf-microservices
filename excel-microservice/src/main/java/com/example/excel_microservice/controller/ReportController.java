package com.example.excel_microservice.controller;

import com.example.excel_microservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("report")
public class ReportController {

  private final EmployeeService employeeService;

  @PostMapping("/doreport")
  public Boolean doReport() {
    return employeeService.doReport();
  }
}
