package com.advantal.requestpayload;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DisconnectWalletRequestPayload {

	@NotNull(message = "UserId can't be null !!")
	private Long userId;

	@NotNull(message = "PortfolioId can't be null !!")
	private Long portfolioId;
	
	@NotNull(message = "WalletId can't be null !!")
	private Long walletId;

}
