package com.sugarmonitor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private String dateString;

  private long sgv;

  private double delta;

  private String direction;

  private String type;

  private double filtered;

  private double unfiltered;

  private long rssi;

  private int noise;

  @Override
  public String toString() {
    return "Entry{"
        + "id='"
        + id
        + '\''
        + ", device='"
        + device
        + '\''
        + ", date="
        + date
        + ", dateString='"
        + dateString
        + '\''
        + ", sgv="
        + sgv
        + ", delta="
        + delta
        + ", direction='"
        + direction
        + '\''
        + ", type='"
        + type
        + '\''
        + ", filtered="
        + filtered
        + ", unfiltered="
        + unfiltered
        + ", rssi="
        + rssi
        + ", noise="
        + noise
        + ", sysTime='"
        + sysTime
        + '\''
        + ", utcOffset="
        + utcOffset
        + '}';
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private String sysTime;

  private long utcOffset;

  public LocalDateTime getSysTime() {
    LocalDateTime result;
    try {
      result = LocalDateTime.parse(sysTime.replace("Z", ""));
    } catch (Exception e) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      result = LocalDateTime.parse(dateString, formatter);
    }
    return result;
  }

  public Double getSgvInMmol() {
    return (double) sgv / (double) 18;
  }
}
