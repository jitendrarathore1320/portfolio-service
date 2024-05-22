package com.advantal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.advantal.model.GlobalCurrencies;

@Repository
public interface GlobalCurrenciesRepository extends JpaRepository<GlobalCurrencies, Long>{

	@Query(value = "SELECT * FROM global_currencies" ,countQuery = "SELECT * FROM global_currencies",nativeQuery = true)
	List<GlobalCurrencies> findAllCurrencies();

}
