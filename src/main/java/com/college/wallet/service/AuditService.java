package com.college.wallet.service;

import org.springframework.stereotype.Service;

import com.college.wallet.model.AuditEntity;
import com.college.wallet.repository.AuditRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;
    public void logAudit(String phoneNumber,String action,String Status,long  duration,String ip,boolean isSensitive){
        if(isSensitive){
        AuditEntity auditEntity=new AuditEntity();
        auditEntity.setPhoneNumber(phoneNumber);
        auditEntity.setAction(action);
        auditEntity.setStatus(Status);
        auditEntity.setDuration(duration);
        auditEntity.setIp(ip);
        auditRepository.save(auditEntity);
        }
        else{
             System.out.println(String.format("[Operational Log]  User Phone Number: %s| Status:%s |action:%s|duration: %d| IpAdress : %s ",phoneNumber,Status,action,duration,ip));
        }
    }
}
