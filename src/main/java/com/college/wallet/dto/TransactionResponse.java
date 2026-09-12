package com.college.wallet.dto;

import java.math.BigDecimal;

import org.springframework.transaction.TransactionStatus;

import com.college.wallet.model.Purse;

public record TransactionResponse(Purse sentBy,Purse receivedBy,TransactionStatus status,BigDecimal Amount) {}