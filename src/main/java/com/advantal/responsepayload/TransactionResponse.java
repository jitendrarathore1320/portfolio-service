package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
	
	private Long id;

	private Double amount;

	private String location;

	private String notes;

	private String transactionDate;

	private String type;

	private String currency;
	
	private String tradingServiceProviderName;
	
	private String transactionType;
	
}
