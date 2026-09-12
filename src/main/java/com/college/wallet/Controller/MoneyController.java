package com.college.wallet.Controller;
import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.college.wallet.repository.PurseRepository;
import com.college.wallet.repository.UserRepository;
import com.college.wallet.service.PurseService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/money")
public class MoneyController {
    private final PurseService purseService;
    private final PurseRepository purseRepository;
    private final UserRepository UserRepository;
    @PostMapping("/addmoney")
    public ResponseEntity<?> addMoney(@RequestBody Map<String,String> request){
        String amount = request.get("amount");
        amount=amount.replaceAll("[^\\d.]", "");// Remove non-numeric characters except decimal point
        if(purseService.addMoney(new BigDecimal(amount))){
        return ResponseEntity.status(HttpStatus.CREATED).body("Money added successfully");}
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("money added  failed");
        }
    }
    @GetMapping("/getBalance")
    public ResponseEntity<?> getBalance(){

        return ResponseEntity.ok(purseService.getBalance());
    }
    
}

