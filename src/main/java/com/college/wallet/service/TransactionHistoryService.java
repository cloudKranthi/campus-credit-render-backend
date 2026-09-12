 package com.college.wallet.service;
import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.college.wallet.dto.TransactionHistorydto;
import com.college.wallet.model.Transaction;
import com.college.wallet.repository.TransactionHistoryRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    public Page<TransactionHistorydto> getTransactionHistory(String phoneNumber,Pageable pageable){
        String cacheKey="transactionHistory:Phonenumber:"+phoneNumber+":page:"+pageable.getPageNumber()+":size:"+pageable.getPageSize();
        try{
            Page<TransactionHistorydto> history=(Page<TransactionHistorydto>)redisTemplate.opsForValue().get(cacheKey);
            if(history!=null){
                return history;
            }
            else{
                return transactionHistory(phoneNumber,pageable,cacheKey);
            }
        }catch(Exception e){
            System.err.println("Error retriving cache: "+e.getMessage());
            return transactionHistory(phoneNumber,pageable,cacheKey);
        }

    }
    public Page<TransactionHistorydto> transactionHistory(String phoneNumber,Pageable pageable,String cacheKey){
    
        Page<Transaction>tx= transactionHistoryRepository.findByHistory(phoneNumber,pageable);
        Page<TransactionHistorydto>history= tx.map(t->{
            boolean isSentUser=t.getSentBy().getUser().getPhoneNumber().equals(phoneNumber);
            String partnerPhoneNumber=isSentUser?t.getReceivedBy().getUser().getPhoneNumber():t.getSentBy().getUser().getPhoneNumber();
            String partnerUsername=isSentUser?t.getReceivedBy().getUser().getUsername():t.getSentBy().getUser().getUsername();
            BigDecimal Amount=isSentUser?t.getAmount().negate():t.getAmount();
            String TransactionType=isSentUser?"SENT":"RECEIVED";
        
         return new TransactionHistorydto(partnerPhoneNumber,partnerUsername,phoneNumber,Amount,TransactionType,t.getCreatedAt());
        });
        try{
              redisTemplate.opsForValue().set(cacheKey,history,Duration.ofMinutes(10));
        }catch(Exception e){
         System.err.println("Error while caching Transaction History:");
          return history;
        }
        return history;

    }
    public void clearTransactionHistoryCache(String phoneNumber){
        try{
            String pattern="transactionHistory:Phonenumber:"+phoneNumber+"*";
            String keys=redisTemplate.keys(pattern).toString();
            if(keys!=null&&!keys.isEmpty()){
                          redisTemplate.delete(pattern);
            }

        }catch(Exception e){}
    System.err.println("Clearing transaction history cache for phone number: "+phoneNumber);
    }
}
