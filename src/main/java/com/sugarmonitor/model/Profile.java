package com.sugarmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("profile")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Profile {

    @Id
    private String id;


}