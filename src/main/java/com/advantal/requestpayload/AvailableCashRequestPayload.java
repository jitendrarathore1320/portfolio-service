package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableCashRequestPayload {

	@NotNull(message = "UserId can't be null !!")
	private Long userId;
	
	@NotEmpty(message = "Trading service provider name can't be empty !!")
	@NotNull(message = "Trading service provider name can't be null !!")
	private String tradingServiceProviderName;//new 
	
	@NotEmpty(message = "TransactionType can't be empty !!")
	@NotNull(message = "TransactionType can't be null !!")
	private String transactionType;
	
	@NotEmpty(message = "Type can't be empty !!")
	@NotNull(message = "Type can't be null !!")
	private String type;
	
	@NotEmpty(message = "TradingPair can't be empty !!")
	@NotNull(message = "TradingPair can't be null !!")
	private String tradingPair;
	
	@NotNull(message = "Price can't be null !!")
	private Double price;
	
//	@NotNull(message = "Amount can't be null !!")
//	private Double amount;
	
	@NotNull(message = "Quantity can't be null !!")
	private Double quantity;//amount
	
	@NotEmpty(message = "TranscationDate can't be empty !!")
	@NotNull(message = "TranscationDate can't be null !!")
	private String transcationDate;
	
//	@NotNull(message = "portfolioId can't be null !!")
//	private Long portfolioId;
	
}
