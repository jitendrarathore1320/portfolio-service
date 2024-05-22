package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoughNutChart {

	private String type;
	
	private String holdingCount;
	
	private String portfolio;
	
	private String todayGainPercent;
	
	private String todayGainValue;
}
