package com.sylviavitoria.apifaculdade.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "application_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLog {
    @Id
    private String id;
    private LocalDateTime timestamp;
    private String level;
    private String message;
    private String className;
    private String method;
    private String userId;
    private String operation;
    private Map<String, Object> metadata;
}
