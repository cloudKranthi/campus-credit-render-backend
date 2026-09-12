package com.college.wallet.model;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
    @Getter
     @Setter
@Entity

@Table(name="purses")
public class  Purse extends BaseEntity{

@OneToOne(fetch=FetchType.EAGER)
@JoinColumn(name= "user_id")
private User user;
@Column(precision=19,scale=4)
@Min(value=0,message="Balance cannot be negative")
private BigDecimal balance =BigDecimal.ZERO;
private String CardDetailsEncryString;
@Enumerated(EnumType.STRING)
@Column(nullable=false)
private  WalletStatus status=WalletStatus.INCOMPLETE;
@Column(name="transaction_pin",length=60,nullable=false)
private String transactionPin;


}