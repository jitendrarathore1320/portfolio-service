package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TsTradingMarket {

	private String base;
	private String quote;
	private String base_id;
	private String pair_type;
	private String exchange_name;
	private String price_latest;
}
