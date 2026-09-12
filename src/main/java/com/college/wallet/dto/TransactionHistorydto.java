package com.college.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistorydto(
    String partnerPhoneName,String PartnerUserName,String presentPhoneNumber,BigDecimal Amount,String TransactionType,LocalDateTime createdAt
) {
    
}
