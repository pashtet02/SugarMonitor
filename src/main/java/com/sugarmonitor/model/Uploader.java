package com.sugarmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Uploader {
  private int battery;
  private String type;

  @Override
  public String toString() {
    return "Uploader{" + "battery=" + battery + ", type='" + type + '\'' + '}';
  }
}
