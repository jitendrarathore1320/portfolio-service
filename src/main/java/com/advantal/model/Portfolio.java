package com.advantal.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;

//ApiKeySecretEntity.java

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Portfolio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String portfolioName;

	private Date creationDate;
	
	private Date updationDate;
	
	private Short status;
	
	private Long userIdFk;
	
	@OneToMany(targetEntity = Wallet.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "portfolioIdFk", referencedColumnName = "id")
	private List<Wallet> walletList;
	
	private Boolean isAvailableCashEnabled=false;// new

}
