package com.rapidtech.walletservice.repository;

import com.rapidtech.walletservice.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    List<Wallet> findByUserNameIn(List<String> userName);
    Wallet findByUserName(String userName);
}
