package com.college.wallet.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.college.wallet.model.valuts;
import com.college.wallet.service.valutService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/scheduled")
public class ValutController {
    private final valutService valutService;
    @PostMapping("/transaction")
    private ResponseEntity<String> addmoney(@RequestBody valuts valut){
     try{
        valutService.adddrip(valut);
        return ResponseEntity.status(HttpStatus.CREATED).body("redis initiated and succefully Transaction occured");
     }
     catch(Exception e){
        System.out.println(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body("error catched while initating transaction");
     }
    }
}
