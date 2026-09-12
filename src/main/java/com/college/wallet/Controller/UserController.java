package com.college.wallet.Controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import com.college.wallet.dto.LoginRequest;
import com.college.wallet.dto.RegisterRequest;
import com.college.wallet.dto.UserResponse;
import com.college.wallet.dto.pinChangeAdminResponse;
import com.college.wallet.exception.BusinessException;
import com.college.wallet.model.Role;
import com.college.wallet.model.User;
import com.college.wallet.repository.UserRepository;
import com.college.wallet.service.JwtService;
import com.college.wallet.service.PurseService;
import com.college.wallet.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController{
    @Value("${app.cookies.secure:false}")
    private boolean cookieSecure;
    @Value("${app.cookies.same-site:Strict}")
    private String cookieSameSite;
    
    private final UserRepository userRepository;
    private final UserService userService;
    private final PurseService purseService;
    private final BCryptPasswordEncoder passwordEncoder;
    
    private final JwtService jwtService;
        
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request){
      Role role=Role.USER;
       if(request.getRole()!=null){
            role=request.getRole();
       }
       System.out.println("DEBUG: Username from JSON -> " + request.getUsername());
    System.out.println("DEBUG: Password from JSON -> " + request.getPassword());
    System.out.println("DEBUG: PIN from JSON -> " + request.getTransactionPin());
        User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setPassword(request.getPassword());
    user.setRole(role);
        User savedUser=userService.RegisterUser(user,request.getTransactionPin());
        UserResponse userResponse= new UserResponse(savedUser.getId(),savedUser.getUsername(),savedUser.getEmail(),savedUser.getPhoneNumber());
        return new ResponseEntity<> (userResponse,HttpStatus.CREATED);
    }
  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequestUser){
    String normalizedPhoneNumber=userService.NormalizePhoneNumber(loginRequestUser.getPhoneNumber());
    User user= userRepository.findByPhoneNumber(normalizedPhoneNumber).orElseThrow(()->new BusinessException("No such User found",HttpStatus.NOT_FOUND));
    if(passwordEncoder.matches(loginRequestUser.getPassword(),user.getPassword())){
        Map<String,String> tokens= jwtService.tokens(user.getId());
      String accessToken = tokens.getOrDefault("accessToken","");
      String refreshToken = tokens.getOrDefault("refreshToken","");
      user.setRefreshToken(refreshToken);
      userRepository.save(user);
              UserResponse userResponse= new UserResponse(user.getId(),user.getUsername(),user.getEmail(),user.getPhoneNumber());
            Map<String,Object> map=  Map.of("User Logged in succesfully",userResponse);
      ResponseCookie cookies= ResponseCookie.from("accessToken",Objects.requireNonNull(accessToken)).httpOnly(true).secure(cookieSecure).path("/").sameSite(cookieSameSite).build();
      return ResponseEntity.ok().header("Set-Cookie",cookies.toString()).body(map);
    }
    else{
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid passowrd");
    }
  }    
  @PutMapping("/admin/phoneNumber")
    public ResponseEntity<?>pinchangeAdminController(@Valid @RequestBody pinChangeAdminResponse pinChangeAdminResponse ){
        purseService.SaveNewPin(pinChangeAdminResponse.getPhonenumber(), pinChangeAdminResponse.getPin());
        return ResponseEntity.ok("Transaction pin changed ");
    }
  @GetMapping("/logout")
  public ResponseEntity<?> logoutUser(){
    String phoneNumber=SecurityContextHolder.getContext().getAuthentication().getName();
    userService.logoutUser(phoneNumber);
    ResponseCookie cookie=ResponseCookie.from("accessToken",null).httpOnly(true).secure(cookieSecure).path("/").sameSite(cookieSameSite).build();
    return ResponseEntity.ok().header("Set-Cookie",cookie.toString()).body(Map.of("message","Logged out successfully"));
  }
}
