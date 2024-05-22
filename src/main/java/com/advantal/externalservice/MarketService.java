package com.advantal.externalservice;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.advantal.requestpayload.StockRequest;

@FeignClient(name = "market-service-new")
public interface MarketService {

	@PostMapping(value = "/api/stock/stock")
	public ResponseEntity<Map<String, Object>> getStock(@RequestBody @Valid StockRequest request);

}