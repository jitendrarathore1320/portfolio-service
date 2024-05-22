package com.advantal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advantal.service.PortfolioInsightsService;

@RestController
@RequestMapping("/api/portfolioInsights")
public class PortfolioInsightsController {

	@Autowired
	PortfolioInsightsService portfolioInsightsService;

	@GetMapping("/portfolioInsights")
	public ResponseEntity<Map<String, Object>> getPortfolioInsights(@RequestParam Long userId,
			@RequestParam String type, @RequestParam String subType) {
		return new ResponseEntity<Map<String, Object>>(
				portfolioInsightsService.getPortfolioInsights(userId, type, subType), HttpStatus.OK);
	}
}
