package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TsCryptoDetailsData {
       
	String id;
	String name;
	String rank;
	String symbol;
	String logo;
//	List<TsDescription> localization;
	TsMarketData market_data;
	}
