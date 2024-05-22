package com.advantal.repository;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advantal.model.Portfolio;
import com.advantal.model.PortfolioGraphHistory;

@Repository
public interface PortfolioGraphHistoryRepository extends JpaRepository<PortfolioGraphHistory, Long> {

	List<PortfolioGraphHistory> findByDateBefore(Date date);

	List<PortfolioGraphHistory> findByPortfolioId(Long portfolioId);


}