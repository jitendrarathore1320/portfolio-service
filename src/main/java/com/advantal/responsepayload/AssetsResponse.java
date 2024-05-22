package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetsResponse {

	private Integer totalCoin;
	
	private Double totalBalance;
	
	private List<AssetsRes2> assetsResList;
}
