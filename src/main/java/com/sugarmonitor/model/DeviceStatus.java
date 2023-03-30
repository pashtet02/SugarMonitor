package com.sugarmonitor.model;

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
  private String createdAt;

  private long utcOffset;

  public LocalDateTime getCreatedAt() {
    return LocalDateTime.parse(createdAt.replace("Z", ""));
  }
}
