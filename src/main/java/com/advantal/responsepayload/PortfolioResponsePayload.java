package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PortfolioResponsePayload {

	private List<PortfolioResponse> portfolioResponseList;

	private WalletResponsePayload walletResponsePayload;

	private List<AssetsRes2> assetsResList;

	private KeyResponse availableCash;// to display available cash with currency

	private Boolean isAvailableCashEnabled;// new

}
