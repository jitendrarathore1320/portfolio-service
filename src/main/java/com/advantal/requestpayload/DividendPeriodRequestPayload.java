package com.advantal.requestpayload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DividendPeriodRequestPayload {

	@NotNull(message = "Id can't be null !!")
	private Long id;
	
	@NotBlank(message = "DividendName can't be empty !!")
	@NotNull(message = "DividendName can't be null !!")
	private String dividendName;
}
