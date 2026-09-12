package com.college.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

import com.college.wallet.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig{
  private final AuditFilter auditFilter;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final MDCFilter mdcFilter;
  private final RateLimitFilter rateLimitFilter;

@Bean
public BCryptPasswordEncoder passwordencoder(){
    return new  BCryptPasswordEncoder();
}


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
  http
.csrf(csrf->csrf.disable())
.cors(Customizer.withDefaults())
.authorizeHttpRequests(auth->auth
.requestMatchers("/api/users/login","/api/users/register").permitAll()
.requestMatchers("/user/").hasAnyRole("ADMIN","USER")
.requestMatchers("/admin/").hasRole("ADMIN")
.anyRequest().authenticated()).addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)
.addFilterAfter(mdcFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(rateLimitFilter, JwtAuthenticationFilter.class)
.addFilterAfter(auditFilter, UsernamePasswordAuthenticationFilter.class);
return http.build();
}

}
