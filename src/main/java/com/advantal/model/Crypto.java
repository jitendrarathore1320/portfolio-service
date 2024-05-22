package com.advantal.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Crypto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String cryptoId;
	
	private String symbol;

	private String name;

	private String currency;
	
	private String instrumentType;//

	private String logo;

	private Date creationDate;

	private Date updationDate;

	private Short status;
	
	private Boolean favorite;
	
	private String listing_time;
	
}
