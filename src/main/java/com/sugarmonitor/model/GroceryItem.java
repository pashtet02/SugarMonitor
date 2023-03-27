package com.sugarmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("groceryitems")
@Data
@AllArgsConstructor
public class GroceryItem {

  @Id private String id;

  private String name;
  private int quantity;
  private String category;
}
