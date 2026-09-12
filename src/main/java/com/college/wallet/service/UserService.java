package com.college.wallet.service;
import java.math.BigDecimal;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.college.wallet.model.Purse;
import com.college.wallet.model.User;
import com.college.wallet.repository.PurseRepository;
import com.college.wallet.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class UserService {
        private final BCryptPasswordEncoder passwordEncoder;
        private final UserRepository userRepository;
        private final PurseRepository purseRepository;
        private final JwtService jwtService;
    @Transactional
    public User RegisterUser(User user,String TransactionPin){
        user.setPhoneNumber(NormalizePhoneNumber(user.getPhoneNumber()));
        BigDecimal initialBalance = new BigDecimal("0.00");
         String hashedPassword=passwordEncoder.encode(user.getPassword());
         String hashedTransaction=passwordEncoder.encode(TransactionPin);
        user.setPassword(hashedPassword);
        User savedUser=userRepository.save(user);
        Purse purse = new Purse();
      purse.setBalance(initialBalance);
            purse.setUser(user);
            purse.setTransactionPin(hashedTransaction);

      purseRepository.save(purse);

      user.setPurse(purse);
      return savedUser;
    }
    public String NormalizePhoneNumber(String phoneNumber){
        if(phoneNumber.isEmpty())return null;
        String normalizedNumber=phoneNumber.replaceAll("[^0-9]","");
        return normalizedNumber.length()>10?normalizedNumber.substring(normalizedNumber.length()-10):normalizedNumber;
    }
    public void logoutUser(String phoneNumber){
        User loggeduser=userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new RuntimeException("User not found"));
        loggeduser.setRefreshToken(null);
        userRepository.save(loggeduser);
    }
    public void refreshTokenUser(){
        String phoneNumber= SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new RuntimeException("User not found"));
        String refreshToken=user.getRefreshToken();
        
         Map<String,String> tokens= jwtService.tokens(user.getId());
      String accessToken = tokens.getOrDefault("accessToken","");
      String newrefreshToken = tokens.getOrDefault("refreshToken","");
      user.setRefreshToken(refreshToken);
      userRepository.save(user);
    }

}
