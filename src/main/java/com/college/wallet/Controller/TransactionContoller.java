package com.college.wallet.Controller;

import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.college.wallet.dto.MoneyTransferResponse;
import com.college.wallet.service.PurseService;
import com.college.wallet.service.TransactionHistoryService;
import com.college.wallet.service.WalletService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionContoller {

    private final TransactionHistoryService transactionHistoryService;
     private final WalletService walletService;
     private final PurseService purseService;
    @PostMapping("/money-transfer")
    public ResponseEntity<?> moneyTransfer(@Valid @RequestBody MoneyTransferResponse request,@RequestHeader("idempotencyKey") String idempotencyKey,HttpServletRequest httpServletRequest){
        System.out.println("DEBUG: PIN received in Controller: " + request.pin());
        System.out.println(" Receiver phone number is "+request.receiverPhoneNumber());
        String receiverPhoneNumber = request.receiverPhoneNumber();
        String Amount = request.Amount();
       String pin=request.pin();
        Amount=Amount.replaceAll("[^\\d.]", "");
        String clientIp=httpServletRequest.getRemoteAddr();
       walletService.transferMoney( receiverPhoneNumber,new BigDecimal(Amount), idempotencyKey,clientIp,pin);
        
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction succesfull");
    }
    @PostMapping("/history")
    public ResponseEntity<?> getTransactionHistory(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int limit
        
    ) {
        String phoneNumber=SecurityContextHolder.getContext().getAuthentication().getName();
    
        //created pageable object
        Pageable pageable = PageRequest.of(page, limit,Sort.by("created_at").descending());
       var history=transactionHistoryService.getTransactionHistory(phoneNumber, pageable);
       var historyList=history.getContent();
       if(!historyList.isEmpty()){
        return ResponseEntity.ok(historyList);
       }
       return ResponseEntity.ok("No transaction history found");
    }
}

