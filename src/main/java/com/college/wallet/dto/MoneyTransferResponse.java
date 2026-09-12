package com.college.wallet.dto;

public record MoneyTransferResponse(String receiverPhoneNumber,String Amount,String idempotencyKey,String pin) {
    
}
