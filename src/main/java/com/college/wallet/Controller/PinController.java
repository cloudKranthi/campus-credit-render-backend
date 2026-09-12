package com.college.wallet.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.college.wallet.dto.PinChangeResponse;
import com.college.wallet.service.PurseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pin")
public class PinController {
    private final PurseService purseService;
    @PutMapping("/change-pin")
    public ResponseEntity<?> pinchangeController(@Valid @RequestBody PinChangeResponse pinChangeResponse ){
    
        purseService.setTransactionPin(pinChangeResponse.getOldpin(), pinChangeResponse.getNewpin());
        return ResponseEntity.ok("Transaction pin changed");
    }
   
}
