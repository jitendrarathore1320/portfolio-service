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
@Table(name="portfolio_wallet_history")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WalletGraphHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Double price;
	
	private Date date;

	private Long walletId;
	
}
