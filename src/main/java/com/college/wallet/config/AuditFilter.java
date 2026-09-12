package com.college.wallet.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
 
import com.college.wallet.service.AuditService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditFilter extends OncePerRequestFilter {
    private final AuditService auditService;
    @Override
    public  void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain ) throws ServletException,IOException{
       long startTime=System.currentTimeMillis();
       filterChain.doFilter(request, response);
       long duration=System.currentTimeMillis()-startTime;
       Authentication auth=SecurityContextHolder.getContext().getAuthentication();
       String user=auth!=null?auth.getName():"ANONYMOUS";
      String ip=request.getRemoteAddr();
      String action=request.getMethod()+" "+request.getServletPath();
      String path=request.getServletPath();
      
      String status=String.valueOf(response.getStatus());
       boolean isSensitive = !request.getMethod().equals("GET") 
    || path.contains("/addmoney") 
    || path.contains("/admin") 
    || path.contains("/transactions/money-transfer") 
    || path.contains("/scheduled/transactions") 
    || response.getStatus() >= 400;
      
      auditService.logAudit(user, action, status, duration,ip,isSensitive);
    }
}
