package com.advantal.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class StockProfile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String symbol;

	private String companyName;
	
	private String isin;
	
	private String cusip;
	
	private String exchange;
	
	private String industry;
	
	private String website;
	
	private String description;
	
	private String ceo;
	
	private String sector;
	
	private String country;

	private String noOfEmployees;

	private String phone;

	private String address;

	private String city;

	private String state;

	private String zip;
	
	private String logo;
	
	private Boolean isActivelyTrading;
	
	private String ipoDate;

	private Date creationDate;
	
	private Date updationDate;
	
}
