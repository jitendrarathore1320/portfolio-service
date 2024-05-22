package com.advantal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.advantal.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	// ORDER BY sc.complaince_degree DESC
	@Query(value = "SELECT * FROM transaction tran WHERE tran.status!=0 and tran.user_id_fk=?1 and tran.symbol=?2 ORDER BY tran.id DESC", countQuery = "SELECT count(*) from transaction tran WHERE tran.status!=0 and tran.user_id_fk=?1 and tran.symbol=?2 ORDER BY tran.id DESC", nativeQuery = true)
	Page<Transaction> getAllTransaction(Long userId, String symbol, Pageable pageable);

//	@Query(value = "SELECT * FROM transaction WHERE type IN ('?1','?1')",nativeQuery = true)
//	List<Transaction> findByTypeIn(List<String> transactionTypes);

}
