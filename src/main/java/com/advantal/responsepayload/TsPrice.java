package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TsPrice {

	String currency;
	Double price_latest;
	Double market_cap;
	Double price_change_percentage_24h;
	Double fully_diluted_valuation;
	Double high_24h;
	Double low_24h;
	Double high_7d;
	Double low_7d;
	Double vol_spot_24h;
		
}
