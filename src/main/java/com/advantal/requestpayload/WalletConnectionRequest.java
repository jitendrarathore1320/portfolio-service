package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WalletConnectionRequest {

	@NotNull(message = "userId can't be null !!")
	private Long userId;
	
	@NotEmpty(message = "ApiKey can't be empty !!")
	@NotNull(message = "ApiKey can't be null !!")
	private String apiKey;
	
	@NotEmpty(message = "ApiSecret can't be empty !!")
	@NotNull(message = "ApiSecret can't be null !!")
	private String apiSecret;
	
	@NotNull(message = "PortfolioId can't be null !!")
	private Long portfolioId;
	
	@NotEmpty(message = "WalletName can't be empty !!")
	@NotNull(message = "WalletName can't be null !!")
	private String walletName;
	
	@NotEmpty(message = "Type can't be empty !!")
	@NotNull(message = "Type can't be null !!")
	private String type;

}
