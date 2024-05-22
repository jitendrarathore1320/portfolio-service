package com.advantal.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cash {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String transactionType;
	
	private String type;
		
	private String tradingServiceProviderName;

	private Double amount;
	
	private Date transactionDate;
	
	private String notes;	

	private Short status;

	private Date creationDate;

	private Date updationDate;
	
//	@OneToOne(targetEntity = User.class)
//	@JoinColumn(name = "userIdFk", referencedColumnName = "id")
//	private User user;

	
//	private String location;
	
	private String currency;

	private Long userId;
	
//	private Long portfolioId;
	
	@OneToOne(targetEntity = Broker.class)
	@JoinColumn(name = "brokerIdFk", referencedColumnName = "id")
	private Broker broker;
	
}
