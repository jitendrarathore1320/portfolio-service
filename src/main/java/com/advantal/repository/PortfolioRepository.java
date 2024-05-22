package com.advantal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//ApiKeySecretRepository.java

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.advantal.model.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	@Query(value = "SELECT assets.*,profile.logo,st.currency,st.price FROM portfolio_wallet_assets assets LEFT JOIN stock st ON st.symbol=assets.symbol LEFT JOIN stock_profile profile ON profile.symbol=st.symbol WHERE assets.user_id=?1 ORDER BY st.volume DESC", countQuery = "SELECT count(*) FROM portfolio_wallet_assets assets LEFT JOIN stock st ON st.symbol=assets.symbol LEFT JOIN stock_profile profile ON profile.symbol=st.symbol WHERE assets.user_id=?1 ORDER BY st.volume DESC", nativeQuery = true)
	Page<Object> getAllMostActiveAssets(Long userId, Pageable pageable);

	@Query(value = "SELECT assets.*,profile.logo,st.currency,st.price FROM portfolio_wallet_assets assets LEFT JOIN stock st ON st.symbol=assets.symbol LEFT JOIN stock_profile profile ON profile.symbol=st.symbol WHERE assets.user_id=?1 AND assets.instrument_type=?2 ORDER BY st.volume DESC", countQuery = "SELECT count(*) FROM portfolio_wallet_assets assets LEFT JOIN stock st ON st.symbol=assets.symbol LEFT JOIN stock_profile profile ON profile.symbol=st.symbol WHERE assets.user_id=?1 AND assets.instrument_type=?2 ORDER BY st.volume DESC", nativeQuery = true)
	Page<Object> getMostActiveAssetsCountryWise(Long userId, String type, Pageable pageable);

//	Page<Portfolio> findByAutoSyncPortfolioAtBefore(Date olderDateObj, Pageable pageable);

//	Page<Portfolio> findByOpenTimeBefore(Date olderDateObj, Pageable pageable);
//
//	Portfolio findByApiKeyAndApiSecret(String apiKey, String apiSecret);

}
