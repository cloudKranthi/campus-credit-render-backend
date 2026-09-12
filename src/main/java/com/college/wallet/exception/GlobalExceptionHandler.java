package com.college.wallet.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.college.wallet.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @Value("${app.mode}")
    private String mode;
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> BusinessExceptionHandler(BusinessException ex,HttpServletRequest request){
        String RandomUUid= UUID.randomUUID().toString();
      log.warn("OPERATIONAL ERROR[{}] Method[{}] URL[{}] Message[{}] Time[{}] ",RandomUUid,request.getMethod(),request.getRequestURL(),ex.getMessage(),LocalDateTime.now());
        ErrorResponse er= new ErrorResponse(
            RandomUUid,"OPERATIONAL",request.getMethod(),request.getPathInfo(),ex.getMessage(),LocalDateTime.now(),null
        );
        return new ResponseEntity<>(er,ex.getHttpStatus());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> ExceptionHandler(Exception e,HttpServletRequest request){
        String RandomUUid=  UUID.randomUUID().toString();
                  log.error("FATAL  ERROR[{}] Method[{}] URL[{}] Message[{}] Time[{}] ",RandomUUid,request.getMethod(),request.getRequestURL(),e.getMessage(),LocalDateTime.now());
        ErrorResponse er= new ErrorResponse(
            RandomUUid,"Fatal",request.getMethod(),request.getRequestURI(),e.getMessage(),LocalDateTime.now(),getstackTrace(e)
        );
        return new ResponseEntity<>(er,HttpStatus.INTERNAL_SERVER_ERROR);

    
    }
    public String getstackTrace(Exception e){
        if("prod".equals(mode)){
            return null;
        }
        StringWriter stringWriter=new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}