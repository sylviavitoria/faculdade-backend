package com.sylviavitoria.apifaculdade.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sylviavitoria.apifaculdade.model.ApplicationLog;
import com.sylviavitoria.apifaculdade.repository.ApplicationLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {
    private final ApplicationLogRepository logRepository;
    
    public void saveLog(String level, String message, String className, String method, String userId, String operation) {
        ApplicationLog log = new ApplicationLog();
        log.setTimestamp(LocalDateTime.now());
        log.setLevel(level);
        log.setMessage(message);
        log.setClassName(className);
        log.setMethod(method);
        log.setUserId(userId);
        log.setOperation(operation);
        
        logRepository.save(log);
    }
}
