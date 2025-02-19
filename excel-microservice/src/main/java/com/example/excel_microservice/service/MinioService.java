package com.example.excel_microservice.service;

import java.io.InputStream;

public interface MinioService {

  String uploadFile(String objectName, InputStream inputStream, long size) throws Exception;
}
