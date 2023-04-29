package com.sugarmonitor.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("profile")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Profile {

  @Id private String id;

  private String units;

  @Field("created_at")
  private String createdAt;

  @Field("language")
  private String language;

  private double lowerBoundLimit;

  private double highBoundLimit;

  private int timeFormat;

  private boolean highAlertSoundEnabled;

  private boolean lowAlertSoundEnabled;

  public int getTimeFormat() {
    if (timeFormat != 12 && timeFormat != 24) {
      throw new RuntimeException(
          "Invalid timeFormat number provided: " + timeFormat + ". Expected values are (12, 24)");
    }
    return timeFormat;
  }

  public LocalDateTime getCreatedAt() {
    return LocalDateTime.parse(createdAt.replace("Z", ""));
  }

  @Override
  public String toString() {
    return "Profile{"
        + "id='"
        + id
        + '\''
        + ", units='"
        + units
        + '\''
        + ", createdAt='"
        + createdAt
        + '\''
        + ", language='"
        + language
        + '\''
        + ", lowerBoundLimit="
        + lowerBoundLimit
        + ", highBoundLimit="
        + highBoundLimit
        + ", timeFormat="
        + timeFormat
        + ", highAlertSoundEnabled="
        + highAlertSoundEnabled
        + ", lowAlertSoundEnabled="
        + lowAlertSoundEnabled
        + '}';
  }
}
