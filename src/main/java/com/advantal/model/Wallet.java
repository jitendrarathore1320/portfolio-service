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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="portfolio_wallet")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String walletName;

	private String apiKey;

	private String apiSecret;
	
	private Double latestTotalBalance;
	
	private Double prevTotalBalance;
	
	private Double dailyOpeningBalance;
	
	private Double dailyPriceGain;

	private Double dailyPercentageGain;

	private Double totalPriceGain;

	private Double totalPercentageGain;

	private Date creationDate;

	private Date openTime;
	
	private Date updationDate;
	
	private Date autoSyncWalletAt;//autoSyncPortfolioAt

	private Short status;
	
	private Long portfolioIdFk;

	@OneToMany(targetEntity = Assets.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "walletIdFk", referencedColumnName = "id")
	private List<Assets> assetsList;
	
	private String type;// new

}
