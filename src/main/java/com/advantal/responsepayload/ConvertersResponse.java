package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertersResponse {

	private String result;
	
	private String base_code;
	
	private String target_code;
	
	private Double conversion_rate;
}
