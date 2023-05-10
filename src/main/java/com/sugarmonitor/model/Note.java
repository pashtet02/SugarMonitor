package com.sugarmonitor.model;

import com.sugarmonitor.model.enums.ValueOfEnum;
import java.util.Date;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("notes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Note {

  @Id private String id;

  @Field("updated_at")
  private Date updatedAt;

  @ValueOfEnum(
      enumClass = MealType.class,
      message = "Must be one of (BREAKFAST, DINNER, SUPPER, OTHER) values")
  public String mealType;

  @Size(
      min = 10,
      max = 2048,
      message = "Note text cannot be less than 10 or exceed limit of 2048 characters")
  private String noteText;

  @Size(max = 1024, message = "Medication notes cannot exceed limit of 1024 characters")
  private String medications;

  @Min(value = 0, message = "Exercise level cannot be less than 0")
  @Max(value = 5, message = "Exercise level cannot be more than 5")
  private int exerciseLevel;

  @Min(value = 0, message = "Exercise level cannot be less than 0")
  @Max(value = 5, message = "Exercise level cannot be more than 5")
  private int stressLevel;

  @PositiveOrZero(message = "Weight must be positive value")
  private double weight;

  @PositiveOrZero(message = "Short-Acting Insulin must be positive value")
  private double insulinShortAction;

  @PositiveOrZero(message = "Long-Acting Insulin must be positive value")
  private double insulinLongAction;

  // A double representing the number of grams of carbohydrates in the food.
  @PositiveOrZero(message = "Carbs must be positive value")
  private double carbs;

  // A double representing the number of calories consumed.
  @PositiveOrZero(message = "Calories must be positive value")
  private double calories;

  // A double representing the current blood glucose level.
  @PositiveOrZero(message = "Blood Glucose Level must be positive value")
  private double bloodGlucoseLevel;
}
