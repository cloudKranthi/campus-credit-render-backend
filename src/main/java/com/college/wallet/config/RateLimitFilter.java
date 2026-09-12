package com.college.wallet.config;
import com.college.wallet.service.RateLimittingService;
import com.college.wallet.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.springframework.http.HttpStatus;
@Component

@Slf4j
@RequiredArgsConstructor
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimittingService rateLimitService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
    Authentication auth=SecurityContextHolder.getContext().getAuthentication();
   if(auth==null){
    log.warn("User is not Authenticated");
    filterChain.doFilter(request,response);
    return;
   }
   String phoneNumber=auth.getName();
   String path=request.getRequestURI();
   if(rateLimitService.isAllowed(phoneNumber,path)){
    filterChain.doFilter(request,response);
   }
   else{
    log.error("Rate limit exceeded for user: {} on path: {}",phoneNumber,path);
    throw new BusinessException("Too many request try again later",HttpStatus.TOO_MANY_REQUESTS);
   }
   }
}
