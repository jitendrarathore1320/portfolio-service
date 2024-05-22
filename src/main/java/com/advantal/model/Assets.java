package com.advantal.model;

import java.util.Date;

import javax.persistence.CascadeType;

//ApiKeySecretEntity.java

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "portfolio_wallet_assets")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Assets {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String symbol;

	private Double quantity;

	private Double previousClosePrice;

	private Double totalPrice;// changed from closePrice

	private Double priceGain;

	private Double pricePercentageGain;

	private Date creationDate;

	private Date updationDate;

	private Short status;

	private Long walletIdFk;

	private String instrumentType;

	@OneToOne(targetEntity = Stock.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "stockIdFk", referencedColumnName = "id")
	private Stock stock;
	
	@OneToOne(targetEntity = Crypto.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "cryptoIdFk", referencedColumnName = "id")
	private Crypto crypto;

	private String exchange;//new
	
	private Long userId;//new
	
}
