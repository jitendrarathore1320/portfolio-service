package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableCashResponse {

	private String Logo;

	private String tradingServiceProviderName;
	
	private String currency;
	
	private Double balance;
	
}
