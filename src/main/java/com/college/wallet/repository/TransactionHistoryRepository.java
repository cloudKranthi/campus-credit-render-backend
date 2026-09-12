package com.college.wallet.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; 

import com.college.wallet.model.Transaction;



public interface TransactionHistoryRepository extends JpaRepository<Transaction,UUID> {
@Query(value = "SELECT * FROM transactions", 
           countQuery = "SELECT count(*) FROM transactions", 
           nativeQuery = true)
Page<Transaction> findByHistory(@Param("phoneNumber") String phoneNumber, Pageable pageable);

}
