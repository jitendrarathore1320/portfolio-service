package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRes {

	private Long id;

	private String transactionType;

	private String symbol;

	private Double buyingPrice;//changed price to buyingPrice;

	private Double quantity;

	private Double transactionFees;

	private Double totalPrice;
	
	private Double marketValue;//new currentPrice*quantity

	private String exchangeName;

	private String brokerName;
	
	private String tradingPair;//for crypto
	
	private String transactionDate;
	
	private String note;
	
	private String instrumentType;
	
	private String currency;
	
	private Long instrumentId;//new 

}
