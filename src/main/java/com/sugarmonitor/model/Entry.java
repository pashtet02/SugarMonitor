package com.sugarmonitor.model;

import static java.lang.Math.round;

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
      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        result = LocalDateTime.parse(dateString, formatter);
      } catch (Exception e2) {
        try {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
          result = LocalDateTime.parse(dateString, formatter);
        } catch (Exception e3) {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
          result = LocalDateTime.parse(dateString, formatter);
        }
      }
    }
    return result;
  }

  public Double getSgv(String units) {
    if (units.equals("mmol")) {
      return round(((double) sgv / (double) 18) * 10.0) / 10.0;
    } else return (double) sgv;
  }
}
