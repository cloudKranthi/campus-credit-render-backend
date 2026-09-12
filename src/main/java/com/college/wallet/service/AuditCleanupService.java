package com.college.wallet.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.college.wallet.repository.AuditRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditCleanupService {
    private final AuditRepository auditRepository;
    @Scheduled(cron="0 0 2  * * SUN")
    @Transactional
    public void cleanupOldAuditLogs(){
        LocalDateTime expiryTime=LocalDateTime.now().minusDays(7);
        auditRepository.deleteByCreatedAtBefore(expiryTime);
        System.out.println("Old audit logs cleaned up at "+LocalDateTime.now());
    }
}
