package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

	@NotNull(message = "TransactionId can't be null !!")
	private Long id;
	
	@NotNull(message = "UserId can't be null !!")
	private Long userId;
	
	@NotNull(message = "PortfolioId can't be null !!")
	private Long portfolioId;
	
	@NotEmpty(message = "TransactionType can't be empty !!")
	@NotNull(message = "TransactionType can't be null !!")
	private String transactionType;
	
	@NotEmpty(message = "Symbol can't be empty !!")
	@NotNull(message = "Symbol can't be null !!")
	private String symbol;
	
	@NotEmpty(message = "ExchangeName can't be empty !!")
	@NotNull(message = "ExchangeName can't be null !!")
	private String exchangeName;
	
	@NotNull(message = "TradingPair can't be null !!")
	private String tradingPair;
	
	@NotEmpty(message = "TransactionDate can't be empty !!")
	@NotNull(message = "TransactionDate can't be null !!")
	private String transactionDate;
	
	@NotNull(message = "Price can't be null !!")
	private Double price;
	
	@NotNull(message = "Quantity can't be null !!")
	private Double quantity;
	
	@NotEmpty(message = "Currency can't be empty !!")
	@NotNull(message = "Currency can't be null !!")
	private String currency;
	
	@NotNull(message = "TranscationFees can't be null !!")
	private Double transactionFees;
	
	@NotNull(message = "Dividend period can't be null !!")
	private String dividendPeriod;
	
	@NotNull(message = "Note can't be null !!")
	private String note;
	
	@NotEmpty(message = "InstrumentType can't be empty !!")
	@NotNull(message = "InstrumentType can't be null !!")
	private String instrumentType;
	
	@NotNull(message = "BrokerName can't be null !!")
	private String brokerName;
	
	@NotNull(message = "InstrumentId can't be null !!")
	private Long instrumentId;//new 

	
}
