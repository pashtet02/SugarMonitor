package com.sugarmonitor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("devicestatus")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceStatus {
  @Id private String id;

  private String device;

  private Uploader uploader;

  @Field("created_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private String createdAt;

  private long utcOffset;

  @Field("_class")
  private String className;

  public LocalDateTime getCreatedAt() {
    // 2023-04-26 22:22:50
    return LocalDateTime.parse(createdAt.replace("Z", ""));
  }

  @Override
  public String toString() {
    return "DeviceStatus{"
        + "id='"
        + id
        + '\''
        + ", device='"
        + device
        + '\''
        + ", uploader="
        + uploader
        + ", createdAt='"
        + createdAt
        + '\''
        + ", utcOffset="
        + utcOffset
        + ", className='"
        + className
        + '\''
        + '}';
  }
}
