package com.sugarmonitor.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("entries")
@AllArgsConstructor
@NoArgsConstructor
@Data
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
    return LocalDateTime.parse(sysTime.replace("Z", ""));
  }
}
