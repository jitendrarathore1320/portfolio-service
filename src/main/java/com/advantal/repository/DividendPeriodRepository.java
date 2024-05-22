package com.advantal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advantal.model.DividendPeriod;
@Repository
public interface DividendPeriodRepository extends JpaRepository<DividendPeriod, Long>{

	DividendPeriod findByIdAndStatus(Long id, Short one);

	DividendPeriod findByName(String dividendName);

}
