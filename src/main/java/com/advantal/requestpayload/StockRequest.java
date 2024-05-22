package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class StockRequest {
			
	@NotEmpty(message = "Symbol name can't be empty !!")
	private String symbol;
	
	@NotEmpty(message = "Exchange name can't be empty !!")
	private String exchange;
	
	@NotEmpty(message = "Country name can't be empty !!")
	private String country;
	
	@NotNull(message = "User id can't be null !!")
	private Long userId;//
	
	@NotNull(message = "Instrument id can't be null !!")
	private Long instrumentId;//
}
