package com.advantal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.advantal.model.Cash;

@Repository
public interface ManageBalanceRepository extends JpaRepository<Cash, Long> {

//	@Query(value = "SELECT * FROM manage_balance mb WHERE mb.user_id_fk=?1 And currency=?2 And mb.status!=0 " ,countQuery = "SELECT * FROM manage_balance mb WHERE mb.user_id_fk=?1 And currency=?2 And mb.status!=0 ",nativeQuery = true)
//	List<ManageBalance> findByAllTransactionByUserId(Long userId,String currency);

	@Query(value = "SELECT * FROM cash ca WHERE ca.user_id=?1 And ca.trading_service_provider_name=?2 And ca.status!=0", countQuery = "SELECT count(*) FROM cash ca WHERE ca.user_id=?1 And ca.trading_service_provider_name=?2 And ca.status!=0 ", nativeQuery = true)
	List<Cash> findByAllTransactionByUserId(Long userId, String brokerName);

//	Cash findByIdAndStatusAndCurrency(Long id, Short status, String currency);

	@Query(value = "SELECT * FROM cash ca WHERE ca.user_id=?1 And ca.status!=0 ORDER BY ca.id DESC", nativeQuery = true)
	List<Cash> findByUser(Long userId);

	@Query(value = "SELECT * FROM cash mb WHERE (mb.user_id_fk=?1 And mb.type=?2 And mb.id=?3 and mb.status!=0)", nativeQuery = true)
	Cash findByTransaction(Long userId, String type, Long id);

//	Cash findByIdAndStatusAndTradingServiceProviderName(Long id, Short one, String tradingServiceProviderName);

	@Query(value = "SELECT * FROM cash ca WHERE ca.user_id=?1 And ca.trading_service_provider_name=?2 And ca.status!=0 ORDER BY ca.id DESC", nativeQuery = true)
	List<Cash> getCashList(Long userId, String filterBy);

	@Query(value = "SELECT * FROM cash ca WHERE ca.user_id=?1 And ca.status!=0 ORDER BY ca.id DESC", nativeQuery = true)
	List<Cash> getCashList(Long userId);

	Cash findByIdAndStatusAndTradingServiceProviderName(Long id, Short one, String brokerName);

	List<Cash> findByUserIdAndType(Long userId, String type);

	List<Cash> findByUserIdAndTradingServiceProviderName(Long userId, String broker);

//	@Query(value = "SELECT * FROM cash mb WHERE mb.user_id_fk=?1 And mb.portfolio_id=?2 And mb.status!=0 ORDER BY mb.id DESC" ,countQuery = "SELECT * FROM cash mb WHERE mb.user_id_fk=?1 And mb.portfolio_id=?2 And mb.status!=0 ORDER BY mb.id DESC",nativeQuery = true)
//	List<Cash> findByUserAndPortfolioId(Long userId, Long portfolioId);

}
