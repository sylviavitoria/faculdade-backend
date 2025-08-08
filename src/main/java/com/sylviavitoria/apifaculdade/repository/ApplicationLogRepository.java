package com.sylviavitoria.apifaculdade.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sylviavitoria.apifaculdade.model.ApplicationLog;

@Repository
public interface ApplicationLogRepository extends MongoRepository<ApplicationLog, String> {
    List<ApplicationLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<ApplicationLog> findByLevel(String level);
    List<ApplicationLog> findByUserId(String userId);
}
