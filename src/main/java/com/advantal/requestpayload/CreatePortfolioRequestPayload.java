package com.advantal.requestpayload;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreatePortfolioRequestPayload {

	@NotNull(message = "userId can't be null !!")
	private Long userId;

	@NotNull(message = "PortfolioName can't be null !!")
	private String portfolioName;
	
	@NotNull(message = "PortfolioId can't be null !!")
	private Long portfolioId;

}
