package com.college.wallet.service;

import java.math.BigDecimal;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.college.wallet.dto.NotificationMessageResponse;
import com.college.wallet.exception.BusinessException;
import com.college.wallet.model.Purse;
import com.college.wallet.model.Transaction;
import com.college.wallet.model.User;
import com.college.wallet.repository.PurseRepository;
import com.college.wallet.repository.TransactionRepositry;
import com.college.wallet.repository.UserRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class MoneyTransferService {
        private final TransactionRepositry transactionRepositry;
    private  final UserRepository userRepository;
    private   final PurseRepository purseRepository;
    private final PurseService purseService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final AuditService auditService;
    private final TransactionHistoryService transactionHistoryService;
    private final RabbitTemplate rabbitTemplate;
    @Transactional(propagation=Propagation.REQUIRES_NEW,rollbackFor=Exception.class)
             public void moneyTransfer(String senderPhonenumber,String ReceiverUserPhonenumber,BigDecimal Amount,String idempotencyKey,String clientIp,String pin){
           purseService.pinTransactionCheck(senderPhonenumber,pin);

           System.out.println("Receiver phone number in money Transfer service is  "+ReceiverUserPhonenumber);
         if(transactionRepositry.findByIdempotencyKey(idempotencyKey).isPresent()){throw new BusinessException("Transaction already exists",HttpStatus.CONFLICT);}//versioning
                User senderUser=userRepository.findByPhoneNumber(senderPhonenumber).orElseThrow(() -> new BusinessException("Sender user not found", HttpStatus.NOT_FOUND));
    // Use the phone number for the receiver lookup
    User receiverUser = userRepository.findByPhoneNumber(ReceiverUserPhonenumber)
        .orElseThrow(() -> new BusinessException("Receiver user not found", HttpStatus.NOT_FOUND));

    // 3. FETCH ALL PURSES IMMEDIATELY AFTER
    // Use the actual phone numbers from the User objects to be 100% sure
    Purse senderspurse = purseRepository.findByUser(senderUser)
        .orElseThrow(() -> new BusinessException("Sender purse not found", HttpStatus.NOT_FOUND));
        
    Purse receiverpurse = purseRepository.findByUser(receiverUser)
        .orElseThrow(() -> new BusinessException("Receiver purse not found", HttpStatus.NOT_FOUND));
            

                if("INACTIVE".equals(senderspurse.getStatus().name())){
                      throw new BusinessException("Account is not in Active",HttpStatus.FORBIDDEN);
                    
                }
                    
                else if(("INACTIVE").equals(receiverpurse.getStatus().name())){
                    throw new BusinessException("Account is not in Active",HttpStatus.FORBIDDEN);
                      
                }
                else if(senderspurse.getBalance().compareTo(Amount)<0){
                  throw new BusinessException("Insufficient balance",HttpStatus.BAD_REQUEST);
                }
                Transaction tx=new Transaction();
                senderspurse.setBalance(senderspurse.getBalance().subtract(Amount));
                receiverpurse.setBalance(receiverpurse.getBalance().add(Amount));
                tx.setIdempotencyKey(idempotencyKey);
                tx.setSentBy(senderspurse);
                tx.setReceivedBy(receiverpurse);
                tx.setAmount(Amount);
                
                purseRepository.save(senderspurse);
                purseRepository.save(receiverpurse);
                transactionRepositry.save(tx);
                NotificationMessageResponse msg=new NotificationMessageResponse(senderPhonenumber,ReceiverUserPhonenumber,Amount);
                rabbitTemplate.convertAndSend("notificationExchange","routing.key",msg);
                System.out.println("✅ Transaction Saved! ID: " + tx.getId());
System.out.println("✅ Records in DB count: " + transactionRepositry.count());
               
                
            
            }
              }

    



