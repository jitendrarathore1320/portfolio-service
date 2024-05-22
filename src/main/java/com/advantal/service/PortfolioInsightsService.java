package com.advantal.service;

import java.util.Map;

public interface PortfolioInsightsService {

	Map<String, Object> getPortfolioInsights(Long userId, String type, String subType);

}
