package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceRequest {

//	@NotEmpty(message = "Symbol name can't be empty !!")
	@NotNull(message = "Symbol can't be null !!")
	private String symbol;
	
//	@NotEmpty(message = "CryptoId can't be empty")
	@NotNull(message = "CryptoId can't be null")
	private String cryptoId;

	@NotEmpty(message = "Time can't be empty")
	@NotNull(message = "Time can't be null")
	private String time;

//	@NotEmpty(message = "Currency can't be empty")
	@NotNull(message = "Currency can't be null")
	private String currency;
	
//	@NotEmpty(message = "Country name can't be empty !!")
	@NotNull(message = "Country can't be null !!")
	private String country;//new
}
