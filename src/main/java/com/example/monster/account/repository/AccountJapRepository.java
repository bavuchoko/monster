package com.example.monster.account.repository;

import com.example.monster.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJapRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);
}
