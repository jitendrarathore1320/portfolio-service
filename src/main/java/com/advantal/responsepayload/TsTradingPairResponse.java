package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TsTradingPairResponse {

	private TsStatus status;
	
	private TsTradingPairData data;
	
}
