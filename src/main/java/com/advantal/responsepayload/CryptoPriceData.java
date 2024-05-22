package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoPriceData {
	
private String name;
	
	private String symbol;
	
	private String id;
	
	private List<CryptoMarketChart> market_chart;
	
	private String vs_currency;
}
