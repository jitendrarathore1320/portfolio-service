package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageCashRequestPayload {
	
	@NotNull(message = "Id can't be null !!")
	private Long id;
	
	@NotEmpty(message = "TransactionType can't be empty !!")
	@NotNull(message = "TransactionType can't be null !!")
	private String transactionType;
	
	@NotEmpty(message = "Type can't be empty !!")
	@NotNull(message = "Type can't be null !!")
	private String type;
	
	@NotEmpty(message = "Trading service provider name can't be empty !!")
	@NotNull(message = "Trading service provider name can't be null !!")
	private String tradingServiceProviderName;//new 
	
//	@NotNull(message = "BrokerId can't be null !!")
//	private Long brokerId;
	
	@NotNull(message = "Amount can't be null !!")
	private Double amount;

	@NotEmpty(message = "TranscationDate can't be empty !!")
	@NotNull(message = "TranscationDate can't be null !!")
	private String transactionDate;
	
	@NotNull(message = "Notes can't be null !!")
	private String notes;
	
	@NotNull(message = "UserId can't be null !!")
	private Long userId;
	
	
//	@NotNull(message = "PortfolioId can't be null !!")
//	private Long portfolioId;
	
	@NotEmpty(message = "Currency can't be empty !!")
	@NotNull(message = "Currency can't be null !!")
	private String currency ;
	
//	@NotNull(message = "Location can't be null !!")
//	private String location;
	
}
