package com.example.excel_microservice.service.impl;

import com.example.excel_microservice.config.MinioProperties;
import com.example.excel_microservice.service.MinioService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

  private final MinioClient client;
  private final MinioProperties properties;
  private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";


  @Override
  public String uploadFile(String objectName, InputStream inputStream, long size) throws Exception {
    client.putObject(
            PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(CONTENT_TYPE)
                    .build());

    return client.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .expiry(60 * 60 * 24)
                    .build());
  }
}
