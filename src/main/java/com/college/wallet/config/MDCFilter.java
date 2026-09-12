package com.college.wallet.config;


import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


@Component
public class MDCFilter implements Filter{
    @Override
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws ServletException,IOException{
         var auth=SecurityContextHolder.getContext().getAuthentication();
         String UserphoneNumber="";
         if(auth!=null&&auth.getName()!=null){
            UserphoneNumber=auth.getName();
         }
         try{
                                  MDC.put("UserPhoneNumber",UserphoneNumber);   
             chain.doFilter(request,response);
         } finally {
            MDC.clear();
         } 
        } 
}
