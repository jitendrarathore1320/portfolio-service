package com.advantal.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long userIdFk;// new param

	private String transactionType;//transactionType
	
	private String symbol;//symbol
	
	private String exchangeName;// exchangeAndBroker
	
	private String brokerName;
	
	private String tradingPair;//for crypto
	
	private String transactionDate;

	private Double buyingPrice;//buyingPrice
	
	private Double quantity;//quantity new param
	
	private String currency;
	
	private Double transactionFees;//new
	
	private String dividendPeriod;//dividendPeriod
	
	private String note;
	
	private String instrumentType;

	private Date creationDate;

	private Date updationDate;
	
	private int status;
	
	private Long portfolioId;//new
	
	private Long instrumentId;//new 

}
