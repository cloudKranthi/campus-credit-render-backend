package com.college.wallet.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.college.wallet.model.AuditEntity;
import com.college.wallet.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/audit")
public class AuditController {
    private final AuditLogService auditLogService;
    @GetMapping("/AllAudits")
    public ResponseEntity<Page<AuditEntity>> getAllAudits(@PageableDefault(size=15,sort="createdAt")Pageable pageable){
        return ResponseEntity.ok(auditLogService.findAll(pageable));
    }
    @PostMapping("/phone")
    public ResponseEntity<Page<AuditEntity>> getByPhone(@PageableDefault(size = 15 ,sort="createdAt") Pageable pageable,@RequestBody String phoneNumber){
        return ResponseEntity.ok(auditLogService.findByPhoneNumber(pageable, phoneNumber));

    }
}
