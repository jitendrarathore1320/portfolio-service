package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConverterRequestPayload {

	@NotEmpty(message = "Base code can't be empty !!")
	@NotNull(message = "Base code can't be null !!")
	private String base_code;
	
	@NotEmpty(message = "Target code can't be empty !!")
	@NotNull(message = "Target code can't be null !!")
	private String target_code;
	
	@NotNull(message = "amount can't be null !!")
	private Double amount;
}
