package com.college.wallet.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.college.wallet.exception.IdempotencyKey;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final IdempotencyKey idempotencyKeyInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(idempotencyKeyInterceptor).addPathPatterns("/api/money/money-transfer","/api/money/add-money","/transactions/money-transfer");
    }
    
}
