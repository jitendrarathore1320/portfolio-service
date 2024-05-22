package com.advantal.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advantal.model.WalletGraphHistory;

@Repository
public interface WalletGraphHistoryRepository extends JpaRepository<WalletGraphHistory, Long> {

	List<WalletGraphHistory> findByWalletId(Long walletId);

	List<WalletGraphHistory> findByDateBefore(Date date);

}
