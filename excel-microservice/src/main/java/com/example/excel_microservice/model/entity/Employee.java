package com.example.excel_microservice.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("employee")
public class Employee implements Serializable {
  @Id
  private Long id;

  private String name;
  private int age;
  private int workExperience;
  private double salary;
  private String position;
}
