package com.advantal.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.advantal.requestpayload.AvailableCashRequestPayload;
import com.advantal.requestpayload.CurrencyConverterRequestPayload;
import com.advantal.requestpayload.DividendPeriodRequestPayload;
import com.advantal.requestpayload.ManageCashRequestPayload;
import com.advantal.requestpayload.PriceRequest;
import com.advantal.requestpayload.TransactionPaginationPayload;
import com.advantal.requestpayload.TransactionRequest;

public interface ManageBalanceService {

	Map<String, Object> manageBalance(@Valid ManageCashRequestPayload manageBalanceRequestPayload);

	Map<String, Object> getCurrencies();

	Map<String, Object> getTransactionHistory(Long userId, String filterBy);

	Map<String, Object> deleteTransaction(Long transactionId);
	
	Map<String, Object> getTradingPair(String cryptoId,String exchangeName);

	Map<String, Object> getCryptoExchangeList();

	Map<String, Object> saveTransaction(TransactionRequest trasnsactionRequest);

	Map<String, Object> getTransaction(TransactionPaginationPayload paginationPayLoad);

	Map<String, Object> updateTransaction(TransactionRequest trasnsactionRequest);

	Map<String, Object> deleteTransactionById(Long id);

	Map<String, Object> getTransactionByFilter(List<String> types);

	Map<String, Object> checkAvailableCash(AvailableCashRequestPayload availableCashRequestPayload);

	Map<String, Object> getPriceByCurrency( PriceRequest priceRequest);

	Map<String, Object> getAddDividendPeriod(DividendPeriodRequestPayload dividendPeriodPayload);

	Map<String, Object> getDividendPeriod();

	Map<String, Object> currencyConverter(CurrencyConverterRequestPayload converterRequestPayload);

	Map<String, Object> getAvailableBalance(Long userId, String broker);

}
