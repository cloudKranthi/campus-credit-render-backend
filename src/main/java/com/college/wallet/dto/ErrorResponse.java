package com.college.wallet.dto;
import java.time.LocalDateTime; 
public record ErrorResponse(String RequestId,String Type,String Method,String url, String Message,LocalDateTime RequestTime,String StackTrace) {}
