package com.advantal.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String symbol;

	private String name;

	private String currency;

	private String exchange;
	
	@Transient
	private String exchangeShortName;//exchangeShortName
	
	@Transient
	private String type;

	private String country;

	private String instrumentType;
	
	private Boolean favorite;

	private String sector;

	
	private Float price;
	
	private Float percent_change;
	
	private Float price_change;
	
	private Float low;
	
	private Float high;
	
	private Float ftw_high;
	
	private Float ftw_low;
	
	private Long marketCap;
	
	private Long volume;
	
	private Long avgVolume;
	
	private Float open;
	
	private Float previousClose;
	
	private Float eps;
	
	private Float pe;
	
	private Long sharesOutstanding;
	
	private Long timestamp;
	
	private Boolean isActivelyTrading;
	
	
	private Short status;
	
	private Date lastSyncDate;
	
	private Date lastUpdatedMarketData;
	
	private Date creationDate;
	
	private Date updationDate;

	@OneToOne(targetEntity =StockProfile.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "profileIdFk", referencedColumnName = "id")
	private StockProfile stockProfile;

//	@OneToOne(targetEntity =StockStatistics.class, cascade = CascadeType.ALL)
//	@JoinColumn(name = "statistics_id_fk", referencedColumnName = "id")
//	private StockStatistics stockStatistics;

}
