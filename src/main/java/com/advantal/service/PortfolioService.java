package com.advantal.service;

import java.util.Map;

import com.advantal.requestpayload.CreatePortfolioRequestPayload;
import com.advantal.requestpayload.DisconnectWalletRequestPayload;
import com.advantal.requestpayload.GainerLoserRequestPayload;
import com.advantal.requestpayload.GraphRequestPayload;
import com.advantal.requestpayload.MostActiveRequestPayload;
import com.advantal.requestpayload.WalletConnectionRequest;

public interface PortfolioService {

	Map<String, Object> connectWallet(WalletConnectionRequest walletConnectionRequest);

	Map<String, Object> disconnetWallet(DisconnectWalletRequestPayload disconnectWalletRequestPayload);

	Map<String, Object> createPortfolio(CreatePortfolioRequestPayload createPortfolioRequestPayload);

	Map<String, Object> getPortfolio(Long userId, String type, Long portfolioId);

	Map<String, Object> deletePorfolio(Long userId, Long portfolioId);

	Map<String, Object> getWalletList(Long userId, String type, Long walletId);

	Map<String, Object> getGraph(GraphRequestPayload graphRequestPayload);

	Map<String, Object> getDoughtChart(Long userId);

	Map<String, Object> getPortfolioGainerAndLoser(GainerLoserRequestPayload gainerLoserRequestPayload);

	Map<String, Object> enableAvailableCash(Long portfolioId, boolean isAvailableCashEnabled);

	Map<String, Object> getMostActiveAssets(MostActiveRequestPayload mostActiveRequestPayload);


}