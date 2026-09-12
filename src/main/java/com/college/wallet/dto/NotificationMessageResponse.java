package com.college.wallet.dto;

import java.math.BigDecimal;

public record NotificationMessageResponse(
    String ReceiverPhoneNumber,String SenderPhoneNumber,BigDecimal Amount
) {
    
}
