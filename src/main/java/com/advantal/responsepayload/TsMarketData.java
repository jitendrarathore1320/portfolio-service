package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TsMarketData {

	Double circulating_supply;
	Double max_supply;
	Long last_updated;
	List<TsPrice> price;
}
