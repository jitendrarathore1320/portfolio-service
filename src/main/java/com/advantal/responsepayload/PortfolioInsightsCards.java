package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioInsightsCards {

	private Double totalValue;
	
	private String type;
	
	private String currency;
	
	private Double ytd_high;
}
