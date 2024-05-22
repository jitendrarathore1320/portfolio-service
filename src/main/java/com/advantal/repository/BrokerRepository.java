package com.advantal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advantal.model.Broker;

@Repository
public interface BrokerRepository extends JpaRepository<Broker, Long> {

	Broker findBybrokerAndStatus(String tradingServiceProviderName, Short one);

}
