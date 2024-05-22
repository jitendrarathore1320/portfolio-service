package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TsTradingPairData {

	private String name;
	
	private List<TsTradingMarket> markets;
	
	private PageInfo page_info;
}
