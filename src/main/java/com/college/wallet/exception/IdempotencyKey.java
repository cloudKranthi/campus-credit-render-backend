package com.college.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class IdempotencyKey implements HandlerInterceptor {

 public boolean preHandle(HttpServletRequest request,HttpServletResponse response){
    String clientKey=request.getHeader("IdemotencyKey");
    if(clientKey==null||clientKey.isEmpty()){
        throw new BusinessException("Idempotency key header is null", HttpStatus.NOT_FOUND);
    }
    return true;
 }
    
}
