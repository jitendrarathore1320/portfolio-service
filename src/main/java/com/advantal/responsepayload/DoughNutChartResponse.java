package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoughNutChartResponse {

	private List<DoughNutChart> doughNutChartList;

	private String totalPortfolio;

	private String totalGainPercent;

	private String totalGainValue;

//	private String todayGainPercent;

//	private String todayGainValue;
}
