package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetsRes2 {

	private String symbol;	
			
	private Double closePrice;//
	
	private Double priceGain;

	private Double pricePercentageGain;
	
	private Double quantity;//new
	
	private Double totalPrice;//new
	
	private String logo;
	
	private String exchange;
	
	private Long instrumentId;
	
	private String instrumentType;
	
	private String currency;
		
	private Long portfolioId;//new
	
}
