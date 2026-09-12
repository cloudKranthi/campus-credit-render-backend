package com.college.wallet.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.college.wallet.model.Purse;
import com.college.wallet.model.User;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
@Repository
public interface PurseRepository extends JpaRepository<Purse,UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)

    @QueryHints({@QueryHint(name="jakarta.persistence.lock.timeout",value="3000")})
    Optional<Purse> findByUser(User user);
}
