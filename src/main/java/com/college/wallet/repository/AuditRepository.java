package com.college.wallet.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.college.wallet.model.AuditEntity;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity,UUID> {
    void deleteByCreatedAtBefore(LocalDateTime expiryTime);
    Page<AuditEntity> findByPhoneNumber(String phoneNumber,Pageable pageable);
}
