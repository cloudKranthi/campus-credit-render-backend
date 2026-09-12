package com.college.wallet.service;
import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.college.wallet.exception.BusinessException;

import jakarta.annotation.PostConstruct;


@Service
public class WalletService {
   private final TransactionHistoryService transactionHistoryService;
    private final  @Qualifier("redisTemplate")RedisTemplate<String,Object> redisTemplate;
    private final MoneyTransferService moneyTransferService;
  
    @Autowired
    public WalletService(RedisTemplate<String,Object> redisTemplate,MoneyTransferService moneyTransferService,TransactionHistoryService transactionHistoryService){
      this.redisTemplate=redisTemplate;
      this.moneyTransferService=moneyTransferService;
      this.transactionHistoryService=transactionHistoryService;
    }
    @PostConstruct
    public void validateInjection() {
        if (this.redisTemplate == null) {
            throw new IllegalStateException("CRITICAL: redisTemplate failed to inject!");
        }
        System.out.println("✅ RedisTemplate successfully injected into WalletService");
    }

    public void checkToken(String token){
      String setToken="set:Lock:"+token;
    Boolean isNew =redisTemplate.opsForValue().setIfAbsent(setToken,"PROCCESSING",Duration.ofMinutes(5));
    if(Boolean.FALSE.equals(isNew)){
      throw new BusinessException("Duplicate request",HttpStatus.CONFLICT);
    }
    }
//Atomic safety 
    public void transferMoney(String ReceiverUserPhonenumber,BigDecimal Amount,String idempotencyKey,String clientIp,String pin){
         String senderPhonenumber=(SecurityContextHolder.getContext().getAuthentication().getName());
         if(senderPhonenumber==null || senderPhonenumber.isEmpty()){
          throw new BusinessException("Unauthorized",HttpStatus.UNAUTHORIZED);
         }
         System.out.println("Receiver user phone number in wallet is  "+ReceiverUserPhonenumber);
         if(senderPhonenumber.equals(ReceiverUserPhonenumber)){
            throw new BusinessException("Sender and receiver phone numbers cannot be the same",HttpStatus.BAD_REQUEST);
         }
         if(Amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Amount must be greater than zero",HttpStatus.BAD_REQUEST);
         }
         
      try{
        validateInjection();
        checkToken(idempotencyKey);
         moneyTransferService.moneyTransfer(senderPhonenumber,ReceiverUserPhonenumber,Amount,idempotencyKey,clientIp,pin);
           try{
                    String senderCacheKey="balance:phoneNumber:"+senderPhonenumber;
                    String receiverCacheKey="balance:phoneNumber:"+ReceiverUserPhonenumber;
                    redisTemplate.delete(senderCacheKey);
                    redisTemplate.delete(receiverCacheKey);
                    transactionHistoryService.clearTransactionHistoryCache(senderPhonenumber);
                    transactionHistoryService.clearTransactionHistoryCache(ReceiverUserPhonenumber);
                }catch(Exception e){
                    System.out.println("Error in caching redis");
                }
        }
      catch(Exception e){
                        try{
                  redisTemplate.delete("set:Lock:"+idempotencyKey);
                 }catch(Exception ex){
                  throw new BusinessException("Error in releasing lock",HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if((e instanceof BusinessException)){
                  throw (BusinessException)e;
                }

                throw new BusinessException("Transaction failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      
    
      
    }
    
}
