package com.college.wallet.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.wallet.model.valuts;
public interface  ValutRepository extends JpaRepository<valuts,UUID> {
    
}
