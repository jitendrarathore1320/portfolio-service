package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PortfolioResponse {

	private Long id;

	private String name;// ;//portfolioName

	private Double totalValue;// ;// totalPortfolioValue
	
	private String currency;

	private Double dailyPriceGain;

	private Double dailyPercentageGain;

	private Double totalPriceGain;

	private Double totalPercentageGain;

	private Short status;
	
//	private List<WalletResponse> walletResponseList;
	
}
