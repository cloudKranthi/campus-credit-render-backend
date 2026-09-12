package com.college.wallet.service;

import org.springframework.stereotype.Service;

import com.college.wallet.exception.BusinessException;
import com.college.wallet.model.AuditEntity;
import com.college.wallet.repository.AuditRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditRepository auditRepository;
    public Page<AuditEntity> findAll(Pageable pageable){
        return auditRepository.findAll(pageable);
    }
    public Page<AuditEntity> findByPhoneNumber(Pageable pageable,String phone){
        Page<AuditEntity> logPage = auditRepository.findByPhoneNumber(phone, pageable);

    // 2. Check if the page has any content
    if (logPage.isEmpty()) {
        throw new BusinessException("No audit logs found for this phone number", HttpStatus.NOT_FOUND);
    }
     return logPage;

}
}
