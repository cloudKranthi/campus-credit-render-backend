package com.college.wallet.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.college.wallet.model.User;


@Repository
public interface  UserRepository extends JpaRepository<User,UUID>{
    Optional<User>   findByPhoneNumber(String phoneNumber);
}
