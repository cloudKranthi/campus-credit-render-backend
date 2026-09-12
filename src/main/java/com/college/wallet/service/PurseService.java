package com.college.wallet.service;
import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.college.wallet.exception.BusinessException;
import com.college.wallet.model.Purse;
import com.college.wallet.model.Role;
import com.college.wallet.model.User;
import com.college.wallet.repository.PurseRepository;
import com.college.wallet.repository.UserRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class PurseService {
    private final PurseRepository purseRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    public Purse getPurseByUser(){
    String phoneNumber=SecurityContextHolder.getContext().getAuthentication().getName();
    User user=userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new BusinessException("User not found", HttpStatus.NOT_FOUND));
    return purseRepository.findByUser(user).orElseThrow(()->new BusinessException("Purse not found for user", HttpStatus.NOT_FOUND));
    }
    @Transactional
    public boolean addMoney(BigDecimal amount){
        try{
       String phoneNumber=SecurityContextHolder.getContext().getAuthentication().getName();
        Purse purse=getPurseByUser();
        purse.setBalance(purse.getBalance().add(amount));
        purseRepository.save(purse);
        try{
         String cacheKey="balance:phoneNumber:"+phoneNumber;
        redisTemplate.delete(cacheKey);
        }catch(Exception e){
            System.err.println("Error deleting cache: " + e.getMessage());
        }
        return true;
        }catch(Exception e){
            return false;
        }
    }
    @Transactional
    public BigDecimal getBalance(){
    String phoneNumber=SecurityContextHolder.getContext().getAuthentication().getName();
    String cacheKey="balance:phoneNumber:"+phoneNumber;
        try{
         Object cached=redisTemplate.opsForValue().get(cacheKey);
         if(cached!=null){
            return new BigDecimal(cached.toString());
         }
         else{
            return getBalanceFromDb(phoneNumber, cacheKey);
         }
        }catch(Exception e){
          System.out.println("Error retrieving cached balance: " + e.getMessage());
                      return getBalanceFromDb(phoneNumber, cacheKey);
        }
    }
    public BigDecimal getBalanceFromDb(String phoneNumber,String cacheKey){
          User user=userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new BusinessException("No such User found", HttpStatus.NOT_FOUND));
         BigDecimal balance=purseRepository.findByUser(user).orElseThrow(()->new BusinessException("Purse not found for user", HttpStatus.NOT_FOUND)).getBalance();
             try{
                redisTemplate.opsForValue().set(cacheKey, balance, Duration.ofMinutes(10));
             }catch(Exception e){
                System.out.println("Error while setting redis");
                return balance;
             }
             return balance;
    }
    @Transactional(propagation=Propagation.REQUIRED)
    public void pinTransactionCheck(String phoneNumber,String pin){
        User user=userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new BusinessException("No such user present", HttpStatus.NOT_FOUND));
        Purse purse=purseRepository.findByUser(user).orElseThrow(()->new BusinessException("No such purse exists for that phone number", HttpStatus.NOT_FOUND));
        System.out.println(purse);
        if(!passwordEncoder.matches(pin,purse.getTransactionPin())){
            throw new BusinessException("Invalid Transaction Pin",HttpStatus.UNAUTHORIZED);
        }

    }
    
    public void setTransactionPin(String oldpin,String newpin){
     String phoneNumber=SecurityContextHolder.getContext().getAuthentication().getName();
     User newuser=userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new BusinessException("No such user present", HttpStatus.NOT_FOUND));
     Purse purse=purseRepository.findByUser(newuser).orElseThrow(()->new BusinessException("No such Purse exists for this User",HttpStatus.NOT_FOUND));
     if(!passwordEncoder.matches(oldpin,purse.getTransactionPin())){
        throw new BusinessException("Invalid old Transaction Pin",HttpStatus.UNAUTHORIZED);
     }
      purse.setTransactionPin(passwordEncoder.encode(newpin));
       purseRepository.save(purse);
    }
    @Transactional
    public void SaveNewPin(String phoneNumber,String pin){
        User u=userRepository.findByPhoneNumber(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new BusinessException("No such User exists",HttpStatus.NOT_FOUND));

        if(u.getRole()!=Role.ADMIN){
          throw new BusinessException("User must be admin",HttpStatus.UNAUTHORIZED);
        }
        User n=userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new BusinessException("No such user present", HttpStatus.NOT_FOUND));
        Purse purse =purseRepository.findByUser(n).orElseThrow(()->new BusinessException("No such User exists",HttpStatus.NOT_FOUND));
        purse.setTransactionPin(passwordEncoder.encode(pin));
        purseRepository.save(purse);

    }
    
}