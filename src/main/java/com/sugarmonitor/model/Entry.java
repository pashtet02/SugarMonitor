package com.sugarmonitor.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("entries")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Entry {

  @Id private String id;

  private String device;

  private long date;

  private String dateString;

  private long sgv;

  private double delta;

  private String direction;

  private String type;

  private double filtered;

  private double unfiltered;

  private long rssi;

  private int noise;

  private String sysTime;

  private long utcOffset;

  public LocalDateTime getSysTime() {
    System.out.println("\nSYS time not formatted: " + sysTime);
    System.out.println("\nSYS time FORMATTED: " + LocalDateTime.parse(sysTime.replace("Z", "")));
    return LocalDateTime.parse(sysTime.replace("Z", ""));
  }

  public Double getSgvInMmol() {
    return (double) sgv / (double) 18;
  }
}
