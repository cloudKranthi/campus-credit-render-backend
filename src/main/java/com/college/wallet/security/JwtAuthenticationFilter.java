package com.college.wallet.security;
import java.io.IOException;
import java.util.List; // Use YOUR entity
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder; // ADDED: Missing import
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.college.wallet.model.User; // Standard Spring NonNull
import com.college.wallet.repository.UserRepository;
import com.college.wallet.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepositry;
    @Override
protected  void  doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)throws ServletException,IOException{
 String jwt=null;
 Cookie Cookies[]= request.getCookies();
 if(Cookies!=null){
 for(Cookie cookie:Cookies){
    if(("accessToken").equals(cookie.getName())){
        jwt=cookie.getValue();
        break;
    }
 }
}
 if(jwt!=null&&SecurityContextHolder.getContext().getAuthentication()==null){
  try {
      String userId=jwtService.findUserId(jwt);
      User user=userRepositry.findById(UUID.fromString(userId)).orElse(null);
      if(user!=null &&jwtService.checkToken(jwt,user)){
        String roleFromName=user.getRole().name();
        var authorities =List.of(new SimpleGrantedAuthority("ROLE_"+roleFromName));
            UsernamePasswordAuthenticationToken authtoken= new UsernamePasswordAuthenticationToken(user.getPhoneNumber(),null,authorities);
            authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authtoken);
      }
  } catch (Exception e) {
    logger.error("Could not set User Authentication");
  }
}
  filterChain.doFilter(request,response);

}
}

