package com.college.wallet.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="valuts")
public class valuts extends BaseEntity {
    @Column(name="parent_phone_number",nullable=false)
    private String parent_phone_number;
    @Column(name="child_phone_number",nullable=false)
    private String student_phone_number;
    @Column(name="daily_Limit",precision=19,scale=4,nullable=false)
    private BigDecimal dailyLimit;
    @Column(name="current_balance",precision=19,scale=4,nullable=false)
    private BigDecimal currentbalance;

}
