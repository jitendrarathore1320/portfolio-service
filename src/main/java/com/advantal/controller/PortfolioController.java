package com.advantal.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advantal.requestpayload.CreatePortfolioRequestPayload;
import com.advantal.requestpayload.DisconnectWalletRequestPayload;
import com.advantal.requestpayload.GainerLoserRequestPayload;
import com.advantal.requestpayload.GraphRequestPayload;
import com.advantal.requestpayload.MostActiveRequestPayload;
import com.advantal.requestpayload.WalletConnectionRequest;
import com.advantal.service.PortfolioService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

	@Autowired
	PortfolioService portfolioService;

	@PostMapping("/create_portfolio")
	public ResponseEntity<Map<String, Object>> createPortfolio(
			@RequestBody @Valid CreatePortfolioRequestPayload createPortfolioRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.createPortfolio(createPortfolioRequestPayload),
				HttpStatus.OK);
	}

	@GetMapping("/portfolios")
	public ResponseEntity<Map<String, Object>> getPortfolio(@RequestParam Long userId, @RequestParam String type,
			@RequestParam Long portfolioId) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.getPortfolio(userId, type, portfolioId),
				HttpStatus.OK);
	}

	@PostMapping("/connect_wallet")
	public ResponseEntity<Map<String, Object>> connectWallet(
			@RequestBody @Valid WalletConnectionRequest walletConnectionRequest) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.connectWallet(walletConnectionRequest),
				HttpStatus.OK);
	}

	@PostMapping("/disconnect")
	public ResponseEntity<Map<String, Object>> disconnetWallet(
			@RequestBody @Valid DisconnectWalletRequestPayload disconnectWalletRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.disconnetWallet(disconnectWalletRequestPayload),
				HttpStatus.OK);
	}

	@DeleteMapping("/delete_portfolio")
	public ResponseEntity<Map<String, Object>> deletePorfolio(@RequestParam Long userId,
			@RequestParam Long portfolioId) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.deletePorfolio(userId, portfolioId),
				HttpStatus.OK);
	}

	@GetMapping("/wallets")
	public ResponseEntity<Map<String, Object>> getWalletList(@RequestParam Long userId, @RequestParam String type,
			@RequestParam Long walletId) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.getWalletList(userId, type, walletId),
				HttpStatus.OK);
	}

	@PostMapping("/graph")
	public ResponseEntity<Map<String, Object>> getGraph(@RequestBody @Valid GraphRequestPayload graphRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.getGraph(graphRequestPayload), HttpStatus.OK);
	}

	@GetMapping("/doughnut_chart")
	public ResponseEntity<Map<String, Object>> getDoughtChart(@RequestParam(required = true) Long userId) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.getDoughtChart(userId), HttpStatus.OK);
	}

	@PostMapping("/portfolio_gainer_loser")
	public ResponseEntity<Map<String, Object>> getPortfolioGainerAndLoser(
			@RequestBody @Valid GainerLoserRequestPayload gainerLoserRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(
				portfolioService.getPortfolioGainerAndLoser(gainerLoserRequestPayload), HttpStatus.OK);
	}

	@GetMapping("/enable_available_cash")
	public ResponseEntity<Map<String, Object>> enableAvailableCash(@RequestParam Long portfolioId,
			@RequestParam boolean isAvailableCashEnabled) {
		return new ResponseEntity<Map<String, Object>>(
				portfolioService.enableAvailableCash(portfolioId, isAvailableCashEnabled), HttpStatus.OK);
	}
	
	@PostMapping("/most_active_assets")
	public ResponseEntity<Map<String, Object>> getMostActiveAssets(@RequestBody @Valid MostActiveRequestPayload mostActiveRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(portfolioService.getMostActiveAssets(mostActiveRequestPayload), HttpStatus.OK);
	}

}
