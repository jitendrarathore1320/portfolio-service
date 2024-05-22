package com.advantal.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//ApiKeySecretRepository.java

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advantal.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Page<Wallet> findByAutoSyncWalletAtBefore(Date olderDateObj, Pageable pageable);

	Page<Wallet> findByOpenTimeBefore(Date olderDateObj, Pageable pageable);
//
//	Wallet findByApiKeyAndApiSecret(String apiKey, String apiSecret);

}

