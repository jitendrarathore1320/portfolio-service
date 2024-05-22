package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GraphRequestPayload {

	@NotEmpty(message = "GraphType can't be empty !!")
	@NotNull(message = "GraphType can't be null !!")
	private String graphType;
	
	@NotNull(message = "PortfolioId can't be null !!")
	private Long portfolioId;
	
	@NotNull(message = "WalletId can't be null !!")
	private Long walletId;
	
	@NotNull(message = "UserId can't be null !!")
	private Long userId;//new

}
