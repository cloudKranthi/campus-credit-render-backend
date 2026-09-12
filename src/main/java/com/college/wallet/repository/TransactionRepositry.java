package com.college.wallet.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.college.wallet.model.Transaction;
@Repository
public interface TransactionRepositry extends  JpaRepository<Transaction, UUID>{
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    //to prevent double click join
}
