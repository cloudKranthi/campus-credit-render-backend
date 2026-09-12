package com.college.wallet.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name="Transactions")
public class Transaction extends BaseEntity {

    @Column(unique=true,nullable=false,updatable=false)
    private String idempotencyKey;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sent_id",nullable=false)
    private Purse sentBy;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="receiver_id",nullable=false)
    private Purse receivedBy;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TransactionStatus status =TransactionStatus.NORMAL;
    @Column(nullable=false,precision=19,scale=4)
    private BigDecimal amount;

    private String EncryptedData;
}
