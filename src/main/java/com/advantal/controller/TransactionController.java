package com.advantal.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advantal.requestpayload.AvailableCashRequestPayload;
import com.advantal.requestpayload.CurrencyConverterRequestPayload;
import com.advantal.requestpayload.DividendPeriodRequestPayload;
import com.advantal.requestpayload.ManageCashRequestPayload;
import com.advantal.requestpayload.PriceRequest;
import com.advantal.requestpayload.TransactionPaginationPayload;
import com.advantal.requestpayload.TransactionRequest;
import com.advantal.service.ManageBalanceService;

@RestController
@RequestMapping("/api/transcation")
public class TransactionController {

	@Autowired
	ManageBalanceService manageBalanceService;

	@PostMapping("/check_available_cash")
	public ResponseEntity<Map<String, Object>> checkAvailableCash(
			@RequestBody @Valid AvailableCashRequestPayload availableCashRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(
				manageBalanceService.checkAvailableCash(availableCashRequestPayload), HttpStatus.OK);
	}

//	@PostMapping("/manage_balance")
	@PostMapping("/manage_cash")
	public ResponseEntity<Map<String, Object>> manageBalance(
			@RequestBody @Valid ManageCashRequestPayload manageBalanceRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.manageBalance(manageBalanceRequestPayload),
				HttpStatus.OK);
	}

	@GetMapping("/get_currencies")
	public ResponseEntity<Map<String, Object>> getCurrencies() {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getCurrencies(), HttpStatus.OK);
	}

	@GetMapping("/get_available_balance_transaction_list")
	public ResponseEntity<Map<String, Object>> getTransactionHistory(@RequestParam(required = true) Long userId,
			@RequestParam(required = true) String filterBy) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getTransactionHistory(userId, filterBy),
				HttpStatus.OK);
	}

	@DeleteMapping("/delete_available_balance_transaction")
	public ResponseEntity<Map<String, Object>> deleteTransaction(@RequestParam(required = true) Long transactionId) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.deleteTransaction(transactionId),
				HttpStatus.OK);
	}

	/* transaction apis inside the crypto & stock */
	/* get cryptoExchange List */
	@GetMapping("/get_crypto_exchange_list")
	public ResponseEntity<Map<String, Object>> getCryptoExchangeList() {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getCryptoExchangeList(), HttpStatus.OK);
	}

	/* get trading pair List */
	@GetMapping("/get_trading_pair")
	public ResponseEntity<Map<String, Object>> getTradingPair(@RequestParam(required = true) String cryptoId,
			@RequestParam(required = true) String exchangeName) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getTradingPair(cryptoId, exchangeName),
				HttpStatus.OK);
	}

	/* add and update transaction */
	@PostMapping("/add_transaction")
	public ResponseEntity<Map<String, Object>> saveTransaction(
			@RequestBody @Valid TransactionRequest trasnsactionRequest) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.saveTransaction(trasnsactionRequest),
				HttpStatus.OK);
	}

	/* update transaction */
	@PutMapping("/update_transaction")
	public ResponseEntity<Map<String, Object>> updateTransaction(
			@RequestBody @Valid TransactionRequest trasnsactionRequest) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.updateTransaction(trasnsactionRequest),
				HttpStatus.OK);
	}

	/* get transaction */
	@PostMapping("/get_transaction")
	public ResponseEntity<Map<String, Object>> getTransaction(
			@RequestBody @Valid TransactionPaginationPayload paginationPayLoad) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getTransaction(paginationPayLoad),
				HttpStatus.OK);
	}

	/* get transaction */
	@DeleteMapping("/delete_transaction_by_id")
	public ResponseEntity<Map<String, Object>> deleteTransactionById(@RequestParam(required = true) Long id) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.deleteTransactionById(id), HttpStatus.OK);
	}

	/* get transaction */
	@PostMapping("/get_transaction_by_filter")
	public ResponseEntity<Map<String, Object>> getTransactionByFilter(
			@RequestParam(required = true) List<String> types) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getTransactionByFilter(types),
				HttpStatus.OK);
	}

	@PostMapping("/get_price_by_currency")
	public ResponseEntity<Map<String, Object>> getPriceByCurrency(@RequestBody @Valid PriceRequest priceRequest) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getPriceByCurrency(priceRequest),
				HttpStatus.OK);
	}

	@PostMapping("/add_dividend_period")
	public ResponseEntity<Map<String, Object>> getAddDividendPeriod(
			@RequestBody @Valid DividendPeriodRequestPayload dividendPeriodPayload) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getAddDividendPeriod(dividendPeriodPayload),
				HttpStatus.OK);
	}

	@GetMapping("/get_dividend_period")
	public ResponseEntity<Map<String, Object>> getDividendPeriod() {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getDividendPeriod(), HttpStatus.OK);
	}

	@PostMapping("/currency_converter")
	public ResponseEntity<Map<String, Object>> currencyConverter(
			@RequestBody @Valid CurrencyConverterRequestPayload converterRequestPayload) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.currencyConverter(converterRequestPayload),
				HttpStatus.OK);
	}

	@GetMapping("/get_available_balance")
	public ResponseEntity<Map<String, Object>> getAvailableBalance(@RequestParam Long userId,
			@RequestParam String broker) {
		return new ResponseEntity<Map<String, Object>>(manageBalanceService.getAvailableBalance(userId, broker),
				HttpStatus.OK);
	}
	

}
