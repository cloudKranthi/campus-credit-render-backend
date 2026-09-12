package com.college.wallet.dto;
import java.math.BigDecimal;
import com.college.wallet.model.WalletStatus;
public record PurseResponse(BigDecimal balance,WalletStatus walletStatus){}