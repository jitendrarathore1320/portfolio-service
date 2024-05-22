package com.advantal.serviceimpl;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.advantal.model.Assets;
import com.advantal.model.Broker;
import com.advantal.model.Cash;
import com.advantal.model.Crypto;
import com.advantal.model.DividendPeriod;
import com.advantal.model.GlobalCurrencies;
import com.advantal.model.Portfolio;
import com.advantal.model.Stock;
import com.advantal.model.Transaction;
import com.advantal.model.User;
import com.advantal.model.Wallet;
import com.advantal.repository.AssetsRepository;
import com.advantal.repository.BrokerRepository;
import com.advantal.repository.CryptoExchangeRepository;
import com.advantal.repository.CryptoRepository;
import com.advantal.repository.DividendPeriodRepository;
import com.advantal.repository.GlobalCurrenciesRepository;
import com.advantal.repository.ManageBalanceRepository;
import com.advantal.repository.StockRepository;
import com.advantal.repository.TransactionRepository;
import com.advantal.repository.UserRepository;
import com.advantal.repository.WalletRepository;
import com.advantal.requestpayload.AvailableCashRequestPayload;
import com.advantal.requestpayload.CurrencyConverterRequestPayload;
import com.advantal.requestpayload.DividendPeriodRequestPayload;
import com.advantal.requestpayload.ManageCashRequestPayload;
import com.advantal.requestpayload.PriceRequest;
import com.advantal.requestpayload.TransactionPaginationPayload;
import com.advantal.requestpayload.TransactionRequest;
import com.advantal.responsepayload.AvailableCashResponse;
import com.advantal.responsepayload.ConvertersResponse;
import com.advantal.responsepayload.CryptoExchange;
import com.advantal.responsepayload.CryptoMarketChart;
import com.advantal.responsepayload.CryptoPriceResponse;
import com.advantal.responsepayload.CurrencyConverterResponse;
import com.advantal.responsepayload.PriceResponse;
import com.advantal.responsepayload.TimeSeriesDetails;
import com.advantal.responsepayload.TransactionRes;
import com.advantal.responsepayload.TransactionResponse;
import com.advantal.responsepayload.TransactionResponsePage;
import com.advantal.responsepayload.TransactionResponseWithPagation;
import com.advantal.responsepayload.TsCurrenciesSupportedResponse;
import com.advantal.responsepayload.TsTradingMarket;
import com.advantal.responsepayload.TsTradingPairResponse;
import com.advantal.service.ManageBalanceService;
import com.advantal.utils.Constant;
import com.advantal.utils.DateUtil;
import com.advantal.utils.ThirdPartyApiUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ManageBalanceServiceImpl implements ManageBalanceService {

	@Autowired
	ManageBalanceRepository manageBalanceRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GlobalCurrenciesRepository globalCurrenciesRepository;

	@Autowired
	ThirdPartyApiUtil thirdPartyApiUtil;

	@Autowired
	CryptoExchangeRepository cryptoExchangeRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private AssetsRepository assetsRepository;

	@Autowired
	private DividendPeriodRepository dividendPeriodRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private CryptoRepository cryptoRepository;

	@Autowired
	private BrokerRepository brokerRepository;

	@Override
	public Map<String, Object> manageBalance(ManageCashRequestPayload manageCashRequestPayload) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (manageCashRequestPayload.getTransactionType().equalsIgnoreCase("deposit")
					|| manageCashRequestPayload.getTransactionType().equalsIgnoreCase("withdrawal")) {
				User user = userRepository.findByIdAndStatus(manageCashRequestPayload.getUserId(), Constant.ONE);
				if (user != null) {
					Broker broker = brokerRepository.findBybrokerAndStatus(
							manageCashRequestPayload.getTradingServiceProviderName(), Constant.ONE);
					if (broker != null) {
						if (manageCashRequestPayload.getId() != 0) {
							Cash oldCash = manageBalanceRepository.findByIdAndStatusAndTradingServiceProviderName(
									manageCashRequestPayload.getId(), Constant.ONE, broker.getBroker());
							if (oldCash != null) {
								BeanUtils.copyProperties(manageCashRequestPayload, oldCash);
								if (manageCashRequestPayload.getTransactionType().equalsIgnoreCase("withdrawal")) {
									String amount = "-" + manageCashRequestPayload.getAmount();
									oldCash.setAmount(Double.parseDouble(amount));
								}
								oldCash.setTransactionDate(
										DateUtil.StringToDate(manageCashRequestPayload.getTransactionDate()));
								oldCash.setUpdationDate(new Date());
								manageBalanceRepository.save(oldCash);
								map.put(Constant.RESPONSE_CODE, Constant.OK);
								map.put(Constant.MESSAGE, Constant.TRANSACTION_UPDATED_SUCCESSFULL);
								log.info(Constant.TRANSACTION_UPDATED_SUCCESSFULL + " status - {} " + Constant.OK);
							} else {
								map.put(Constant.RESPONSE_CODE, Constant.OK);
								map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
								log.info(Constant.ID_NOT_FOUND_MESSAGE + " status - {} " + Constant.OK);
							}
						} else {
							Cash cash = new Cash();
							BeanUtils.copyProperties(manageCashRequestPayload, cash);
							if (manageCashRequestPayload.getTransactionType().equalsIgnoreCase("withdrawal")) {
								String amount = "-" + manageCashRequestPayload.getAmount();
								cash.setAmount(Double.parseDouble(amount));
							}
							cash.setTradingServiceProviderName(broker.getBroker());
							cash.setTransactionDate(
									DateUtil.StringToDate(manageCashRequestPayload.getTransactionDate()));
							cash.setBroker(broker);
							cash.setStatus(Constant.ONE);
							cash.setCreationDate(new Date());
							cash.setUserId(user.getId());
							manageBalanceRepository.save(cash);
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.TRANSACTION_CREATED_SUCCESSFULL);
							log.info(Constant.TRANSACTION_CREATED_SUCCESSFULL + " status - {} " + Constant.OK);
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.BROKER_NOT_FOUND_MESSAGE);
						log.info(Constant.ID_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
					log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				map.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
				log.info(" Invalid transcation type. please select valid transcation type - status {} "
						+ Constant.BAD_REQUEST);
			}
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.info("Exception! status - {}", e.getMessage());
		}
		return map;
	}

	@Override
	public Map<String, Object> getCurrencies() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<GlobalCurrencies> globalCurrencies = globalCurrenciesRepository.findAllCurrencies();
			if (!globalCurrencies.isEmpty()) {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.DATA_FOUND);
				map.put(Constant.DATA, globalCurrencies);
				log.info(Constant.DATA_FOUND + " status - {} " + Constant.OK);
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
				map.put(Constant.DATA, globalCurrencies);
				log.info(Constant.DATA_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
			}
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.info("Exception! status - {}", e.getMessage());
		}
		return map;
	}

//	@Override
//	public Map<String, Object> getTransactionHistory(Long userId, Long portfolioId) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			List<TransactionResponse> transactionList = new ArrayList<TransactionResponse>();
//			TransactionResponsePage transactionResponsePage = new TransactionResponsePage();
//			Set<String> currencieList = new HashSet<String>();
//			List<KeyResponse> keyResponsesList = new ArrayList<KeyResponse>();
//			List<GlobalCurrencies> globalCurrencieList = globalCurrenciesRepository.findAllCurrencies();
////			List<ManageBalance> manageBalanceList = manageBalanceRepository.findByUser(userId);
//			List<Cash> manageBalanceList = manageBalanceRepository.findByUserAndPortfolioId(userId,
//					portfolioId);
//			if (!manageBalanceList.isEmpty() && !globalCurrencieList.isEmpty()) {
//				for (int i = 0; i <= manageBalanceList.size() - 1; i++) {
//					for (GlobalCurrencies currencies : globalCurrencieList) {
//						if (currencies.getCurrency().equalsIgnoreCase(manageBalanceList.get(i).getCurrency())) {
//							if (!currencieList.contains(manageBalanceList.get(i).getCurrency())) {
//								currencieList.add(currencies.getCurrency());
//							}
//						}
//					}
//				}
//				// Create a List from the HashSet to maintain order
//				List<String> currencyStringList = new ArrayList<>(currencieList);
//
//				KeyResponse keyResponse = new KeyResponse();
//				while (currencyStringList.size() > 0) {
//					Double totalBalance = 0.0;
//					for (String str : currencyStringList) {
//						int i = 0;
//						for (Cash balance : manageBalanceList) {
//							/*------------ check deposit only -------------*/
//							if (balance.getType().equalsIgnoreCase("deposit")) {
//								if (str.equalsIgnoreCase(balance.getCurrency())) {
//									if (!keyResponsesList.isEmpty()) {
////										for (KeyResponse keyResponse1 : keyResponsesList) {
//										for (; i < keyResponsesList.size();) {
//											log.info(" keyResponseList size :- " + keyResponsesList.size());
//											if (str.equalsIgnoreCase(keyResponsesList.get(i).getKey())) {
//												totalBalance = keyResponsesList.get(i).getValue();
//												totalBalance = totalBalance + balance.getAmount();
//												keyResponsesList.get(i).setKey(balance.getCurrency());
//												keyResponsesList.get(i).setValue(totalBalance);
//												if (!keyResponsesList.get(i).getKey()
//														.equalsIgnoreCase(balance.getCurrency())) {
//													keyResponsesList.add(keyResponsesList.get(i));
//												}
//												i = keyResponsesList.size() - 1;
//												break;
//											} else {
//												KeyResponse keyResponse2 = new KeyResponse();
//												keyResponse2.setKey(balance.getCurrency());
//												keyResponse2.setValue(balance.getAmount());
//												keyResponsesList.add(keyResponse2);
//												i = keyResponsesList.size() - 1;
//												break;
//											}
//										}
//									} else {
//										keyResponse.setKey(balance.getCurrency());
//										keyResponse.setValue(balance.getAmount());
//										keyResponsesList.add(keyResponse);
//									}
//								}
//								/*------------ check withdrawal only ----------*/
//							} else if (balance.getType().equalsIgnoreCase("withdrawal")) {
//								if (str.equalsIgnoreCase(balance.getCurrency())) {
//									if (!keyResponsesList.isEmpty()) {
//										for (; i < keyResponsesList.size();) {
//											log.info(" keyResponseList size :- " + keyResponsesList.size());
//											if (str.equalsIgnoreCase(keyResponsesList.get(i).getKey())) {
//												totalBalance = keyResponsesList.get(i).getValue();
//												totalBalance = totalBalance + balance.getAmount();
//												keyResponsesList.get(i).setKey(balance.getCurrency());
//												keyResponsesList.get(i).setValue(totalBalance);
//												if (!keyResponsesList.get(i).getKey()
//														.equalsIgnoreCase(balance.getCurrency())) {
//													keyResponsesList.add(keyResponsesList.get(i));
//												}
//												i = keyResponsesList.size() - 1;
//												break;
//											} else {
//												KeyResponse keyResponse2 = new KeyResponse();
//												keyResponse2.setKey(balance.getCurrency());
//												keyResponse2.setValue(balance.getAmount());
//												keyResponsesList.add(keyResponse2);
//												i = keyResponsesList.size() - 1;
//												break;
//											}
//										}
//									} else {
//										keyResponse.setKey(balance.getCurrency());
//										keyResponse.setValue(balance.getAmount());
//										keyResponsesList.add(keyResponse);
//									}
//								}
//							}
//						}
//					}
//					break;
//				}
//				for (Cash balance : manageBalanceList) {
//					TransactionResponse response = new TransactionResponse();
//					BeanUtils.copyProperties(balance, response);
//					response.setTransactionDate(DateUtil.convertDateToStringDateTime(balance.getTransactionDate()));
////					response.setDueToBuyOf(balance.getDue_to_buy_of());
////					response.setDueToSellOf(balance.getDue_to_sell_of());
//					transactionList.add(response);
//				}
//				/* set keyResponsesList & manageBalanceList */
//				transactionResponsePage.setManageBalanceList(keyResponsesList);
//				transactionResponsePage.setTransactionHistoryList(transactionList);
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.DATA_FOUND);
//				map.put(Constant.DATA, transactionResponsePage);
//				log.info(Constant.DATA_FOUND + " - status {} " + Constant.OK);
//				log.info("list of amount :- " + transactionResponsePage);
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.HISTORY_NOT_FOUND_MESSAGE);
//				map.put(Constant.DATA, transactionResponsePage);
//				log.info(Constant.HISTORY_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
//			}
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.info("Exception! status - {}", e.getMessage());
//		}
//		return map;
//	}

	@Override
	public Map<String, Object> getTransactionHistory(Long userId, String filterBy) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<TransactionResponse> transactionList = new ArrayList<TransactionResponse>();
			TransactionResponsePage transactionResponsePage = new TransactionResponsePage();
			List<AvailableCashResponse> availableCashResponseList = new ArrayList<>();
			List<Cash> cashList = null;
			List<Broker> brokerList = brokerRepository.findAll();
			if (!brokerList.isEmpty()) {
				if (!filterBy.isBlank()) {
					cashList = manageBalanceRepository.getCashList(userId, filterBy);
				} else {
					cashList = manageBalanceRepository.getCashList(userId);
				}
				if (!cashList.isEmpty()) {
					List<Cash> uniqueList = new ArrayList<>();
					for (Broker broker : brokerList) {
						for (Cash cash : cashList) {
							if (cash.getTradingServiceProviderName().equalsIgnoreCase(broker.getBroker())) {
								Cash cash2 = new Cash();
								BeanUtils.copyProperties(cash, cash2);
								uniqueList.add(cash);
								break;
							}
						}
					}
					Double availableBalance = 0.0;
					for (Cash uniqueCash : uniqueList) {
						Double totalBalance = 0.0;
						AvailableCashResponse availableCashResponse = new AvailableCashResponse();
						for (Cash cash : cashList) {
							if (cash.getTradingServiceProviderName()
									.equalsIgnoreCase(uniqueCash.getTradingServiceProviderName())) {
								totalBalance = totalBalance + cash.getAmount();
							}
						}
						availableBalance = availableBalance + totalBalance;
						availableCashResponse.setLogo(uniqueCash.getBroker().getLogo());
						availableCashResponse.setTradingServiceProviderName(uniqueCash.getTradingServiceProviderName());
						availableCashResponse.setCurrency(uniqueCash.getCurrency());
						availableCashResponse.setBalance(totalBalance);
						availableCashResponseList.add(availableCashResponse);
					}
					for (Cash cash : cashList) {
						TransactionResponse response = new TransactionResponse();
						BeanUtils.copyProperties(cash, response);
						response.setTransactionDate(DateUtil.convertDateToStringDate(cash.getTransactionDate()));
						transactionList.add(response);
					}
					transactionResponsePage.setAvailableCashResponseList(availableCashResponseList);
					transactionResponsePage.setTransactionHistoryList(transactionList);
					transactionResponsePage.setAvailableBalance(availableBalance);
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.DATA_FOUND);
					map.put(Constant.DATA, transactionResponsePage);
					log.info(Constant.DATA_FOUND + " - status {} " + Constant.OK);
					log.info("list of amount :- " + transactionResponsePage);
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.HISTORY_NOT_FOUND_MESSAGE);
					map.put(Constant.DATA, transactionResponsePage);
					log.info(Constant.HISTORY_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.BROKER_LIST_EMPTY_MESSAGE);
				map.put(Constant.DATA, transactionResponsePage);
				log.info(Constant.BROKER_LIST_EMPTY_MESSAGE + " - status {} " + Constant.OK);
			}
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.info("Exception! status - {}", e.getMessage());
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteTransaction(Long transactionId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
//			ManageBalance oldTransaction = manageBalanceRepository.findByTransaction(userId, type, id);
//			if (oldTransaction != null) {
			if (manageBalanceRepository.existsById(transactionId)) {
				manageBalanceRepository.deleteById(transactionId);
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_DELETED_SUCESSFULLY);
				log.info(Constant.MANUAL_TRANSACTION_DELETED_SUCESSFULLY + " status - {} " + Constant.OK);
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.TRANSACTION_ID_NOT_FOUND_MESSAGE);
				log.info(Constant.TRANSACTION_ID_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
			}
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.info("Exception! status - {}", e.getMessage());
		}
		return map;
	}

	@Override
	public Map<String, Object> getTradingPair(String cryptoId, String exchangeName) {
		Map<String, Object> response = new HashMap<String, Object>();
		List<String> pairList = new ArrayList<String>();
		TsTradingPairResponse pairResponse = new TsTradingPairResponse();
		TsCurrenciesSupportedResponse responseSupportedResponse = new TsCurrenciesSupportedResponse();
		int limit = 1500, offset = 0;
		List<String> currenciesList = new ArrayList<String>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (cryptoId != null && !cryptoId.isBlank()) {
				String tradingPairResponse = thirdPartyApiUtil.getTradingPair(cryptoId, limit, offset);
				/* get currencies supported end point */
				String currencies = thirdPartyApiUtil.getCurrencies();
				if (currencies != null && !currencies.isBlank()) {
					log.info(" third party response currencies list ! status - {}", Constant.OK);
					Map<?, ?> mapResponse = mapper.readValue(currencies, Map.class);
					responseSupportedResponse = mapper.convertValue(mapResponse, TsCurrenciesSupportedResponse.class);
					if (responseSupportedResponse.getStatus().getCode() == 0) {
						currenciesList = responseSupportedResponse.getData();
					} else {
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
						response.put(Constant.DATA, pairList);
						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
						return response;
					}
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
					response.put(Constant.DATA, currencies);
					log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
					return response;
				}
				if (tradingPairResponse != null && !tradingPairResponse.isBlank()) {
					log.info(" third party response tradingPair list ! status - {}", Constant.OK);
					Map<?, ?> mapResponse = mapper.readValue(tradingPairResponse, Map.class);
					pairResponse = mapper.convertValue(mapResponse, TsTradingPairResponse.class);
					if (pairResponse.getStatus().getCode() == 0) {
						log.info(" Trading Pair data found successfully ! status - {}" + Constant.OK);
						for (TsTradingMarket tsTradingMarket : pairResponse.getData().getMarkets()) {
							if (tsTradingMarket.getExchange_name().equalsIgnoreCase(exchangeName)) {
								for (String currrency : currenciesList) {
									if (tsTradingMarket.getQuote().toUpperCase().equalsIgnoreCase(currrency)) {
										String base_quote = tsTradingMarket.getBase().toUpperCase() + "/"
												+ tsTradingMarket.getQuote().toUpperCase();
										pairList.add(base_quote);
									}
								}
							}
						}
						// Create a Set to store unique values
						Set<String> uniqueSet = new HashSet<>();
						// Iterate through the list and add unique values to the set
						for (String item : pairList) {
							uniqueSet.add(item);
						}
						// Clear the original list
						pairList.clear();
						// Reconstruct the list with unique values
						pairList.addAll(uniqueSet);
						// Sort the ArrayList in ascending order
						Collections.sort(pairList);
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.MESSAGE, Constant.DATA_FOUND);
						response.put(Constant.DATA, pairList);
						log.info(pairList.size() + " Trading pair data found successfully !" + Constant.OK);
					} else {
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
						response.put(Constant.DATA, pairList);
						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
					}
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
					response.put(Constant.DATA, pairList);
					log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
					return response;
				}
				log.info(" All crypto list trading pair data save successfully ! status - {}" + Constant.OK);
			} else {
				log.info("crypto list not found in your database ! status - {}", Constant.NOT_FOUND);
			}
		} catch (Exception e) {
			log.info("Exception! status - {}", e.getMessage());
		}
		return response;
	}

	@Override
	public Map<String, Object> getCryptoExchangeList() {
		Map<String, Object> response = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		try {
			List<CryptoExchange> exchangeList = cryptoExchangeRepository.findAll();
			for (CryptoExchange cryptoExchange : exchangeList) {
				list.add(cryptoExchange.getExchangeName());
			}
			// Sort the ArrayList in ascending order
			Collections.sort(list);
			if (exchangeList != null && !exchangeList.isEmpty()) {
				response.put(Constant.RESPONSE_CODE, Constant.OK);
				response.put(Constant.MESSAGE, Constant.DATA_FOUND);
				response.put(Constant.DATA, list);
				log.info(exchangeList.size() + " Exchange list data found successfully !" + Constant.OK);
			} else {
				response.put(Constant.RESPONSE_CODE, Constant.OK);
				response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
				response.put(Constant.DATA, exchangeList);
				log.info("Exchange list data not found ! status - {}", Constant.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception! status - {}", e.getMessage());
		}
		return response;
	}

//	@Override
//	public Map<String, Object> saveTransaction(TransactionRequest trasnsactionRequest) {
//		Map<String, Object> response = new HashMap<String, Object>();
//		Transaction transaction = new Transaction();
//		try {
//			User user = userRepository.findByIdAndStatus(trasnsactionRequest.getUserId(), Constant.ONE);
//			if (user != null) {
//				if (trasnsactionRequest.getType().equalsIgnoreCase("BUY")
//						&& !trasnsactionRequest.getDateTime().isBlank() && trasnsactionRequest.getPrice() != 0
//						&& trasnsactionRequest.getAmount() != 0) {
//					// only for buying
//					transaction.setTradingPair(
//							!trasnsactionRequest.getTradingPair().isBlank() ? trasnsactionRequest.getTradingPair()
//									: "");
//					transaction.setAmountAdded(trasnsactionRequest.getAmount());
//					transaction.setCreationDate(new Date());
//					transaction.setPrice(trasnsactionRequest.getPrice());
//					transaction.setNote(trasnsactionRequest.getNote());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					// calculate the cost values
//					transaction.setCost(trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount()
//							+ Double.parseDouble(trasnsactionRequest.getTransaction_fee()));
//					transaction.setWorth(trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount());
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2] + " via "
//							+ trasnsactionRequest.getExchangeName();
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Buy");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					transaction.setCrypto_or_stock_symbol(trasnsactionRequest.getCrypto_or_stock_symbol());
//					// save the buying data
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.TRANSACTION_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//					log.info(" Buying transaction save successfully !! status - {} " + Constant.OK);
//				} else if (trasnsactionRequest.getType().equalsIgnoreCase("SELL")
//						&& !trasnsactionRequest.getDateTime().isBlank() && trasnsactionRequest.getPrice() != 0
//						&& trasnsactionRequest.getAmount() != 0) {
//					// only for selling
//					transaction.setTradingPair(
//							!trasnsactionRequest.getTradingPair().isBlank() ? trasnsactionRequest.getTradingPair()
//									: "");
//					transaction.setAmountDeducated(trasnsactionRequest.getAmount());
//					transaction.setCreationDate(new Date());
//					transaction.setPrice(trasnsactionRequest.getPrice());
//					transaction.setNote(trasnsactionRequest.getNote());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					// calculate the proceeds values
//					transaction.setProceeds(trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount()
//							- Double.parseDouble(trasnsactionRequest.getTransaction_fee()));
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2] + " via "
//							+ trasnsactionRequest.getExchangeName();
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Sell");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					transaction.setCrypto_or_stock_symbol(trasnsactionRequest.getCrypto_or_stock_symbol());
//					// save the selling data
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.TRANSACTION_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//					log.info(" Selling transaction save successfully !! status - {} " + Constant.OK);
//				} else if (trasnsactionRequest.getType().equalsIgnoreCase("TRANSFER")
//						&& !trasnsactionRequest.getTransfer_sent_received_from().isBlank()
//						&& !trasnsactionRequest.getTransfer_sent_to().isBlank()
//						&& trasnsactionRequest.getAmount_transferred() != 0
//						&& !trasnsactionRequest.getDateTime().isBlank()) {
//					// only for transfer
//					transaction.setCreationDate(new Date());
//					transaction.setTransfer_from(trasnsactionRequest.getTransfer_sent_received_from());
//					transaction.setAmountTransferred(trasnsactionRequest.getAmount_transferred());
//					transaction.setTransfer_to(trasnsactionRequest.getTransfer_sent_to());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					transaction.setNote(trasnsactionRequest.getNote());
//					// calculate the values
//					transaction.setTransfer_worthNow(
//							trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount_transferred());
//					transaction.setTransfer_worthThen(
//							trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount_transferred());
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2];
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Transfer");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					transaction.setCrypto_or_stock_symbol(trasnsactionRequest.getCrypto_or_stock_symbol());
//					// save the transfer data
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.TRANSACTION_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//					log.info(" Transfer transaction save successfully !! status - {} " + Constant.OK);
//				} else if (trasnsactionRequest.getType().equalsIgnoreCase("DIVIDEND")
//						&& !trasnsactionRequest.getDateTime().isBlank() && trasnsactionRequest.getPrice() != 0
//						&& !trasnsactionRequest.getDividend_period().isBlank()) {
//					transaction.setCreationDate(new Date());
//					transaction.setDividend_amount(trasnsactionRequest.getPrice());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					transaction.setPeriod(trasnsactionRequest.getDividend_period());
//					transaction.setNote(trasnsactionRequest.getNote());
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2];
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Dividend");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					transaction.setCrypto_or_stock_symbol(trasnsactionRequest.getCrypto_or_stock_symbol());
//					// save the transfer data
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.DIVIDEND_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//				} else {
//					response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//					response.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
//					log.info("invalid transaction type. Please enter valid transaction type ! status - {}",
//							Constant.OK);
//				}
//			} else {
//				response.put(Constant.RESPONSE_CODE, Constant.OK);
//				response.put(Constant.MESSAGE, Constant.USER_NOT_FOUND);
//				log.info(Constant.USER_NOT_FOUND + " " + trasnsactionRequest.getUserId() + " status - {} "
//						+ Constant.NOT_FOUND);
//			}
//		} catch (Exception e) {
//			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception! status - {}", e.getMessage());
//		}
//		return response;
//	}

	public Wallet saveWalletData(Long portfolioId, TransactionRequest transactionRequest) {
		Wallet wallet = new Wallet();
		try {
			wallet.setDailyOpeningBalance(transactionRequest.getPrice() * transactionRequest.getQuantity());
			wallet.setLatestTotalBalance(transactionRequest.getPrice() * transactionRequest.getQuantity());
			wallet.setPrevTotalBalance(transactionRequest.getPrice() * transactionRequest.getQuantity());
			wallet.setStatus(Constant.ONE);
			wallet.setCreationDate(new Date());
			wallet.setOpenTime(new Date());
			wallet.setAutoSyncWalletAt(new Date());
			wallet.setPortfolioIdFk(portfolioId);
			if (transactionRequest.getInstrumentType().equalsIgnoreCase("crypto"))
				wallet.setWalletName(transactionRequest.getExchangeName());
			else
				wallet.setWalletName(transactionRequest.getBrokerName());
			wallet.setDailyPercentageGain(0.0);
			wallet.setDailyPriceGain(0.0);
			wallet.setTotalPercentageGain(0.0);
			wallet.setTotalPriceGain(0.0);
			wallet.setType(transactionRequest.getInstrumentType());
			wallet = walletRepository.save(wallet);
			log.info("New wallet created successfully ! status - {} " + Constant.OK);
		} catch (Exception e) {
			log.error(e.getMessage() + " status - {}", Constant.SERVER_MESSAGE);
		}
		return wallet;
	}

	public Assets saveAssets(Wallet wallet, TransactionRequest transactionRequest, Stock stock, Crypto crypto) {
		Boolean isFound = false;
		Assets assets = new Assets();
		Stock stock2 = new Stock();
		Crypto crypto2 = new Crypto();
		try {
			if (transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
					|| transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.UNITED_STATES)) {
				BeanUtils.copyProperties(stock, stock2);
				assets.setStock(stock2);
			} else if (transactionRequest.getInstrumentType().equalsIgnoreCase("crypto")) {
				BeanUtils.copyProperties(crypto, crypto2);
				assets.setCrypto(crypto2);
			}

			if (wallet.getAssetsList() == null || wallet.getAssetsList().isEmpty()) {
				assets.setExchange(transactionRequest.getExchangeName());
				assets.setInstrumentType(transactionRequest.getInstrumentType());
				assets.setSymbol(transactionRequest.getSymbol());
				assets.setQuantity(transactionRequest.getQuantity());
				assets.setPriceGain(0.0);
				assets.setPricePercentageGain(0.0);
				assets.setWalletIdFk(wallet.getId());
				assets.setCreationDate(new Date());
				assets.setStatus(Constant.ONE);
				assets.setTotalPrice((transactionRequest.getPrice() * transactionRequest.getQuantity()));
				assets.setPreviousClosePrice((transactionRequest.getPrice() * transactionRequest.getQuantity()));
				assets.setUserId(transactionRequest.getUserId());
				assetsRepository.save(assets);
				log.info("New assets data saved successfully ! status - {}", Constant.OK);
			} else {
				Wallet oldWallet = new Wallet();
				BeanUtils.copyProperties(wallet, oldWallet);
				Assets oldAssets = new Assets();
				for (Assets assets2 : wallet.getAssetsList()) {
					if (assets2.getSymbol().equalsIgnoreCase(transactionRequest.getSymbol())) {
						isFound = true;
						BeanUtils.copyProperties(assets2, oldAssets);
						break;
					}
				}
				if (isFound == false) {
					if (transactionRequest.getTransactionType().equalsIgnoreCase("buy")) {
						oldWallet.setLatestTotalBalance(oldWallet.getLatestTotalBalance()
								+ (transactionRequest.getPrice() * transactionRequest.getQuantity()));
					} else if (transactionRequest.getTransactionType().equalsIgnoreCase("sell")) {
						oldWallet.setLatestTotalBalance(oldWallet.getLatestTotalBalance()
								- (transactionRequest.getPrice() * transactionRequest.getQuantity()));
					} else if (transactionRequest.getTransactionType().equalsIgnoreCase("dividend")) {
						oldWallet.setLatestTotalBalance(oldWallet.getLatestTotalBalance()
								+ (transactionRequest.getPrice() * transactionRequest.getQuantity()));
					}
					oldWallet.setUpdationDate(new Date());
					walletRepository.save(oldWallet);
					log.info(oldWallet.getWalletName().toUpperCase()
							+ " : Old wallet modified successfully ! status - {}", Constant.OK);

					assets.setExchange(transactionRequest.getExchangeName());
					assets.setInstrumentType(transactionRequest.getInstrumentType());
					assets.setSymbol(transactionRequest.getSymbol());
					assets.setQuantity(transactionRequest.getQuantity());
					assets.setPriceGain(0.0);
					assets.setPricePercentageGain(0.0);
					assets.setWalletIdFk(wallet.getId());
					assets.setCreationDate(new Date());
					assets.setStatus(Constant.ONE);
					assets.setTotalPrice((transactionRequest.getPrice() * transactionRequest.getQuantity()));
					assets.setPreviousClosePrice((transactionRequest.getPrice() * transactionRequest.getQuantity()));
					assets.setUserId(transactionRequest.getUserId());
					assetsRepository.save(assets);
					log.info("New assets data saved successfully ! status - {}", Constant.OK);
				} else {
					if (transactionRequest.getTransactionType().equalsIgnoreCase("buy")) {
						oldWallet.setLatestTotalBalance(oldWallet.getLatestTotalBalance()
								+ (transactionRequest.getPrice() * transactionRequest.getQuantity()));
						oldAssets.setTotalPrice(oldAssets.getTotalPrice()
								+ (transactionRequest.getPrice() * transactionRequest.getQuantity()));
						oldAssets.setQuantity(oldAssets.getQuantity() + transactionRequest.getQuantity());
					} else if (transactionRequest.getTransactionType().equalsIgnoreCase("sell")) {
						oldWallet.setLatestTotalBalance(oldWallet.getLatestTotalBalance()
								- (transactionRequest.getPrice() * transactionRequest.getQuantity()));
						oldAssets.setTotalPrice(oldAssets.getTotalPrice()
								- (transactionRequest.getPrice() * transactionRequest.getQuantity()));
						oldAssets.setQuantity(oldAssets.getQuantity() - transactionRequest.getQuantity());
					} else if (transactionRequest.getTransactionType().equalsIgnoreCase("dividend")) {
						oldWallet.setLatestTotalBalance(oldWallet.getLatestTotalBalance()
								+ (transactionRequest.getPrice() * transactionRequest.getQuantity()));
						oldAssets.setTotalPrice(oldAssets.getTotalPrice() + transactionRequest.getPrice());
					}
					oldWallet.setUpdationDate(new Date());
					walletRepository.save(oldWallet);
					log.info(oldWallet.getWalletName().toUpperCase()
							+ " : Old wallet modified successfully ! status - {}", Constant.OK);

					oldAssets.setUpdationDate(new Date());
					oldAssets = assetsRepository.save(oldAssets);
					BeanUtils.copyProperties(oldAssets, assets);
					log.info(
							oldAssets.getSymbol().toUpperCase()
									+ " : Old assets price and quantity modified successfully ! status - {}",
							Constant.OK);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage() + " status - {}", Constant.SERVER_MESSAGE);
		}
		return assets;
	}

	public Transaction saveTransaction(User user, TransactionRequest transactionRequest) {
		Transaction transaction = new Transaction();
		try {
			BeanUtils.copyProperties(transactionRequest, transaction);
			transaction.setBuyingPrice(transactionRequest.getPrice());
			transaction.setUserIdFk(transactionRequest.getUserId());
			transaction.setStatus(Constant.ONE);
			transaction.setCreationDate(new Date());
			transaction = transactionRepository.save(transaction);
		} catch (Exception e) {
			log.error(e.getMessage() + " status - {}", Constant.SERVER_MESSAGE);
		}
		return transaction;
	}

	// @ 1change
//	@Override
//	public Map<String, Object> saveTransaction(TransactionRequest transactionRequest) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			User user = userRepository.findByIdAndStatus(transactionRequest.getUserId(), Constant.ONE);
//			if (user != null) {
//				Stock stock = stockRepository.findByIdAndSymbol(transactionRequest.getInstrumentId(),
//						transactionRequest.getSymbol());
//				if (stock != null) {
//					if (transactionRequest.getId() == 0) {
//						if (transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
//								|| transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.UNITED_STATES)
//								|| transactionRequest.getInstrumentType().equalsIgnoreCase("crypto")) {
//							if (transactionRequest.getTransactionType().equalsIgnoreCase("buy")
//									|| transactionRequest.getTransactionType().equalsIgnoreCase("sell")
//									|| transactionRequest.getTransactionType().equalsIgnoreCase("dividend")) {
//								if (!user.getPortfolioList().isEmpty()) {
//									Boolean isFound = false;
//									Portfolio portfolio = new Portfolio();
//									for (Portfolio portfolio2 : user.getPortfolioList()) {
//										if (portfolio2.getId() == transactionRequest.getPortfolioId()) {
//											BeanUtils.copyProperties(portfolio2, portfolio);
//											break;
//										}
//									}
//									Wallet wallet = new Wallet();
//									Transaction transaction = null;
//									Assets assets = null;
//									for (Wallet wallet2 : portfolio.getWalletList()) {
//										if (wallet2.getWalletName()
//												.equalsIgnoreCase(transactionRequest.getExchangeName())) {
//											isFound = true;
//											BeanUtils.copyProperties(wallet2, wallet);
//											assets = saveAssets(wallet, transactionRequest, stock);
//											if (assets != null) {
//												transaction = saveTransaction(user, transactionRequest);
//												if (transaction != null) {
//													map.put(Constant.RESPONSE_CODE, Constant.OK);
//													map.put(Constant.MESSAGE,
//															Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY);
//													map.put(Constant.DATA, transaction);
//													log.info(Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY
//															+ " ! status - {} " + Constant.OK);
//													return map;
//												} else {
//													map.put(Constant.RESPONSE_CODE, Constant.OK);
//													map.put(Constant.MESSAGE,
//															Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//													log.info(
//															Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//															Constant.OK);
//												}
//											} else {
//												map.put(Constant.RESPONSE_CODE, Constant.OK);
//												map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//												log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//														Constant.OK);
//											}
//											break;
//										}
//									}
//									if (isFound == false) {
//										log.info("For this transaction "
//												+ transactionRequest.getExchangeName().toUpperCase()
//												+ " named new wallet creating ! status - {} " + Constant.OK);
//										wallet = saveWalletData(transactionRequest.getExchangeName(), portfolio.getId(),
//												transactionRequest);
//										if (wallet != null) {
//											assets = saveAssets(wallet, transactionRequest, stock);
//											if (assets != null) {
//												transaction = saveTransaction(user, transactionRequest);
//												if (transaction != null) {
//													map.put(Constant.RESPONSE_CODE, Constant.OK);
//													map.put(Constant.MESSAGE,
//															Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY);
//													map.put(Constant.DATA, transaction);
//													log.info(Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY
//															+ " ! status - {} " + Constant.OK);
//													return map;
//												} else {
//													map.put(Constant.RESPONSE_CODE, Constant.OK);
//													map.put(Constant.MESSAGE,
//															Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//													log.info(
//															Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//															Constant.OK);
//												}
//											} else {
//												map.put(Constant.RESPONSE_CODE, Constant.OK);
//												map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//												log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//														Constant.OK);
//											}
//										} else {
//											map.put(Constant.RESPONSE_CODE, Constant.OK);
//											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//											log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//													Constant.OK);
//										}
//									}
//								} else {
//									map.put(Constant.RESPONSE_CODE, Constant.OK);
//									map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_NOT_COMPLETED_MESSAGE);
//									log.info(Constant.MANUAL_TRANSACTION_NOT_COMPLETED_MESSAGE + " status - {}",
//											Constant.OK);
//								}
//							} else {
//								map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//								map.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
//								log.info(Constant.INVALID_TRANSACTION_TYPE + " ! status - {}", Constant.OK);
//							}
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//							map.put(Constant.MESSAGE, Constant.INVALID_INSTRUMENT_TYPE_MESSAGE);
//							log.info(Constant.INVALID_INSTRUMENT_TYPE_MESSAGE + " ! status - {}", Constant.OK);
//						}
//					} else if (transactionRequest.getId() > 0) {
//						Transaction oldTransaction = new Transaction();
//						Boolean isFound = false;
//						for (Transaction transaction2 : user.getTransactionList()) {
//							if (transaction2.getId() == transactionRequest.getId()) {
//								BeanUtils.copyProperties(transaction2, oldTransaction);
//								isFound = true;
//								break;
//							}
//						}
//						if (isFound == true) {
//							BeanUtils.copyProperties(transactionRequest, oldTransaction);
//							oldTransaction.setUpdationDate(new Date());
//							transactionRepository.save(oldTransaction);
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_UPDATED_SUCESSFULLY);
//							map.put(Constant.DATA, oldTransaction);
//							log.info(Constant.MANUAL_TRANSACTION_UPDATED_SUCESSFULLY + " ! status - {} " + Constant.OK);
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//							log.info(Constant.ID_NOT_FOUND_MESSAGE + " " + transactionRequest.getUserId()
//									+ " status - {} " + Constant.NOT_FOUND);
//						}
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
//					log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//				log.info(Constant.ID_NOT_FOUND_MESSAGE + " " + transactionRequest.getUserId() + " status - {} "
//						+ Constant.NOT_FOUND);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception! status - {}", e.getMessage());
//		}
//		return map;
//	}

	// @ 2change
//	@Override
//	public Map<String, Object> saveTransaction(TransactionRequest transactionRequest) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			User user = userRepository.findByIdAndStatus(transactionRequest.getUserId(), Constant.ONE);
//			if (user != null) {
//
//				//
//				Stock stock = null;
//				Crypto crypto = null;
//				if (transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
//						|| transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.UNITED_STATES)) {
//					stock = stockRepository.findByIdAndSymbol(transactionRequest.getInstrumentId(),
//							transactionRequest.getSymbol());
//					if (stock == null) {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
//						log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
//						return map;
//					}
//				} else if (transactionRequest.getInstrumentType().equalsIgnoreCase("crypto")) {
//					crypto = cryptoRepository.findByIdAndCryptoId(transactionRequest.getInstrumentId(),
//							transactionRequest.getSymbol());
//					if (crypto == null) {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
//						log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
//						return map;
//					}
//				}
//				//
//
//				if (transactionRequest.getId() == 0) {
//					if (transactionRequest.getTransactionType().equalsIgnoreCase("buy")
//							|| transactionRequest.getTransactionType().equalsIgnoreCase("sell")
//							|| transactionRequest.getTransactionType().equalsIgnoreCase("dividend")) {
//						if (!user.getPortfolioList().isEmpty()) {
//							Boolean isFound = false;
//							Portfolio portfolio = new Portfolio();
//							if (user.getPortfolioList().size() == 1) {
//								for (Portfolio portfolio2 : user.getPortfolioList()) {
//									if (portfolio2.getId() == transactionRequest.getPortfolioId()) {
//										BeanUtils.copyProperties(portfolio2, portfolio);
////										if (portfolio.getPortfolioType() == null
////												|| portfolio.getPortfolioType().isBlank()) {
////											portfolio.setPortfolioType(transactionRequest.getInstrumentType());
////											portfolio.setUpdationDate(new Date());
////											portfolioRepository.save(portfolio);
////											log.info(Constant.PORTFOLIO_UPDATED_MESSAGE + " ! status - {} "
////													+ Constant.OK);
////										}
//										break;
//									}
//								}
//							} else if (user.getPortfolioList().size() > 1) {
//								for (Portfolio portfolio2 : user.getPortfolioList()) {
//									if (portfolio2.getId() == transactionRequest.getPortfolioId()) {
//										BeanUtils.copyProperties(portfolio2, portfolio);
////										if (portfolio.getPortfolioType() == null
////												|| portfolio.getPortfolioType().isBlank()) {
////											portfolio.setPortfolioType(transactionRequest.getInstrumentType());
////											portfolio.setUpdationDate(new Date());
////											portfolioRepository.save(portfolio);
////											log.info(Constant.PORTFOLIO_UPDATED_MESSAGE + " ! status - {} "
////													+ Constant.OK);
////										}
//										break;
//									}
//								}
//							}
//
//							Wallet wallet = new Wallet();
//							Transaction transaction = null;
//							Assets assets = null;
//							for (Wallet wallet2 : portfolio.getWalletList()) {
//								if (wallet2.getWalletName().equalsIgnoreCase(transactionRequest.getExchangeName())) {
//									isFound = true;
//									BeanUtils.copyProperties(wallet2, wallet);
//									assets = saveAssets(wallet, transactionRequest, stock, crypto);
//									if (assets != null) {
//										transaction = saveTransaction(user, transactionRequest);
//										if (transaction != null) {
//											map.put(Constant.RESPONSE_CODE, Constant.OK);
//											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY);
//											map.put(Constant.DATA, transaction);
//											log.info(Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY + " ! status - {} "
//													+ Constant.OK);
//											return map;
//										} else {
//											map.put(Constant.RESPONSE_CODE, Constant.OK);
//											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//											log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//													Constant.OK);
//										}
//									} else {
//										map.put(Constant.RESPONSE_CODE, Constant.OK);
//										map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//										log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//												Constant.OK);
//									}
//									break;
//								}
//							}
//							if (isFound == false) {
//								log.info("For this transaction " + transactionRequest.getExchangeName().toUpperCase()
//										+ " named new wallet creating ! status - {} " + Constant.OK);
//								wallet = saveWalletData(transactionRequest.getExchangeName(), portfolio.getId(),
//										transactionRequest);
//								if (wallet != null) {
//									assets = saveAssets(wallet, transactionRequest, stock, crypto);
//									if (assets != null) {
//										transaction = saveTransaction(user, transactionRequest);
//										if (transaction != null) {
//											map.put(Constant.RESPONSE_CODE, Constant.OK);
//											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY);
//											map.put(Constant.DATA, transaction);
//											log.info(Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY + " ! status - {} "
//													+ Constant.OK);
//											return map;
//										} else {
//											map.put(Constant.RESPONSE_CODE, Constant.OK);
//											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//											log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//													Constant.OK);
//										}
//									} else {
//										map.put(Constant.RESPONSE_CODE, Constant.OK);
//										map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//										log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
//												Constant.OK);
//									}
//								} else {
//									map.put(Constant.RESPONSE_CODE, Constant.OK);
//									map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
//									log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}", Constant.OK);
//								}
//							}
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_NOT_COMPLETED_MESSAGE);
//							log.info(Constant.MANUAL_TRANSACTION_NOT_COMPLETED_MESSAGE + " status - {}", Constant.OK);
//						}
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//						map.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
//						log.info(Constant.INVALID_TRANSACTION_TYPE + " ! status - {}", Constant.OK);
//					}
//				} else if (transactionRequest.getId() > 0) {
//					Transaction oldTransaction = new Transaction();
//					Boolean isFound = false;
//					for (Transaction transaction2 : user.getTransactionList()) {
//						if (transaction2.getId() == transactionRequest.getId()) {
//							BeanUtils.copyProperties(transaction2, oldTransaction);
//							isFound = true;
//							break;
//						}
//					}
//					if (isFound == true) {
//						BeanUtils.copyProperties(transactionRequest, oldTransaction);
//						oldTransaction.setUpdationDate(new Date());
//						transactionRepository.save(oldTransaction);
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_UPDATED_SUCESSFULLY);
//						map.put(Constant.DATA, oldTransaction);
//						log.info(Constant.MANUAL_TRANSACTION_UPDATED_SUCESSFULLY + " ! status - {} " + Constant.OK);
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//						log.info(Constant.ID_NOT_FOUND_MESSAGE + " " + transactionRequest.getUserId() + " status - {} "
//								+ Constant.NOT_FOUND);
//					}
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//				log.info(Constant.ID_NOT_FOUND_MESSAGE + " " + transactionRequest.getUserId() + " status - {} "
//						+ Constant.NOT_FOUND);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception! status - {}", e.getMessage());
//		}
//		return map;
//	}

	@Override
	public Map<String, Object> saveTransaction(TransactionRequest transactionRequest) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			User user = userRepository.findByIdAndStatus(transactionRequest.getUserId(), Constant.ONE);
			if (user != null) {
				Stock stock = null;
				Crypto crypto = null;
				if (transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
						|| transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.UNITED_STATES)) {
					stock = stockRepository.findByIdAndSymbol(transactionRequest.getInstrumentId(),
							transactionRequest.getSymbol());
					if (stock == null) {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
						log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
						return map;
					}
				} else if (transactionRequest.getInstrumentType().equalsIgnoreCase("crypto")) {
					crypto = cryptoRepository.findByIdAndCryptoId(transactionRequest.getInstrumentId(),
							transactionRequest.getSymbol());
					if (crypto == null) {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
						log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
						return map;
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
					map.put(Constant.MESSAGE, Constant.INVALID_INSTRUMENT_TYPE_MESSAGE);
					log.info(Constant.INVALID_INSTRUMENT_TYPE_MESSAGE + "! status - {}", Constant.OK);
					return map;
				}
				Portfolio portfolio = new Portfolio();
				Wallet wallet = new Wallet();
				if (!user.getPortfolioList().isEmpty()) {
					if (user.getPortfolioList().size() == 2) {
						if (user.getPortfolioList().get(1).getId().equals(transactionRequest.getPortfolioId())) {
							BeanUtils.copyProperties(user.getPortfolioList().get(1), portfolio);
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
							log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
							return map;
						}
					} else if (user.getPortfolioList().size() > 1) {
						boolean isFound = false;
						for (int i = 1; i < user.getPortfolioList().size(); i++) {
							if (user.getPortfolioList().get(i).getId().equals(transactionRequest.getPortfolioId())) {
								isFound = true;
								BeanUtils.copyProperties(user.getPortfolioList().get(i), portfolio);
								break;
							}
						}
						if (isFound == false) {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
							log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
							return map;
						}
					} else if (user.getPortfolioList().size() == 1) {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.CREATE_NEW_PORTFOLIO_MESSAGE);
						log.info(Constant.CREATE_NEW_PORTFOLIO_MESSAGE + " status - {}", Constant.OK);
						return map;
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_NOT_COMPLETED_MESSAGE);
					log.info(Constant.MANUAL_TRANSACTION_NOT_COMPLETED_MESSAGE + " status - {}", Constant.OK);
					return map;
				}

				if (transactionRequest.getId() == 0) {
					if (transactionRequest.getTransactionType().equalsIgnoreCase("buy")
							|| transactionRequest.getTransactionType().equalsIgnoreCase("sell")
							|| transactionRequest.getTransactionType().equalsIgnoreCase("dividend")) {
						Transaction transaction = null;
						Assets assets = null;
						Boolean isFound = false;
						for (Wallet wallet2 : portfolio.getWalletList()) {
							if (transactionRequest.getInstrumentType().equalsIgnoreCase("crypto")) {
								if (wallet2.getWalletName().equalsIgnoreCase(transactionRequest.getExchangeName())) {
									isFound = true;
									BeanUtils.copyProperties(wallet2, wallet);
									assets = saveAssets(wallet, transactionRequest, stock, crypto);
									if (assets != null) {
										transaction = saveTransaction(user, transactionRequest);
										if (transaction != null) {
											map.put(Constant.RESPONSE_CODE, Constant.OK);
											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY);
											map.put(Constant.DATA, transaction);
											log.info(Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY + " ! status - {} "
													+ Constant.OK);
											return map;
										} else {
											map.put(Constant.RESPONSE_CODE, Constant.OK);
											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
											log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
													Constant.OK);
										}
									} else {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
										log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
												Constant.OK);
									}
									break;
								}
							} else if (transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
									|| transactionRequest.getInstrumentType()
											.equalsIgnoreCase(Constant.UNITED_STATES)) {
								if (wallet2.getWalletName().equalsIgnoreCase(transactionRequest.getBrokerName())) {
									isFound = true;
									BeanUtils.copyProperties(wallet2, wallet);
									assets = saveAssets(wallet, transactionRequest, stock, crypto);
									if (assets != null) {
										transaction = saveTransaction(user, transactionRequest);
										if (transaction != null) {
											map.put(Constant.RESPONSE_CODE, Constant.OK);
											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY);
											map.put(Constant.DATA, transaction);
											log.info(Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY + " ! status - {} "
													+ Constant.OK);
											return map;
										} else {
											map.put(Constant.RESPONSE_CODE, Constant.OK);
											map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
											log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
													Constant.OK);
										}
									} else {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
										log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
												Constant.OK);
									}
									break;
								}
							}
						}
						if (isFound == false) {
							log.info("For this transaction " + transactionRequest.getExchangeName().toUpperCase()
									+ " named new wallet creating ! status - {} " + Constant.OK);
							wallet = saveWalletData(portfolio.getId(), transactionRequest);
							if (wallet != null) {
								assets = saveAssets(wallet, transactionRequest, stock, crypto);
								if (assets != null) {
									transaction = saveTransaction(user, transactionRequest);
									if (transaction != null) {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY);
										map.put(Constant.DATA, transaction);
										log.info(Constant.MANUAL_TRANSACTION_SAVED_SUCESSFULLY + " ! status - {} "
												+ Constant.OK);
										return map;
									} else {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
										log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
												Constant.OK);
									}
								} else {
									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
									log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}", Constant.OK);
								}
							} else {
								map.put(Constant.RESPONSE_CODE, Constant.OK);
								map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
								log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}", Constant.OK);
							}
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
						map.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
						log.info(Constant.INVALID_TRANSACTION_TYPE + " ! status - {}", Constant.OK);
					}
				} else if (transactionRequest.getId() > 0) {
					Transaction oldTransaction = new Transaction();
					Boolean isFound = false;
					for (Transaction transaction2 : user.getTransactionList()) {
						if (transaction2.getId().equals(transactionRequest.getId())) {
							BeanUtils.copyProperties(transaction2, oldTransaction);
							isFound = true;
							break;
						}
					}
					if (isFound == true) {
						for (Wallet wallet2 : portfolio.getWalletList()) {
							if (transactionRequest.getInstrumentType().equalsIgnoreCase("crypto")) {
								if (wallet2.getWalletName().equalsIgnoreCase(transactionRequest.getExchangeName())) {
									isFound = true;
									BeanUtils.copyProperties(wallet2, wallet);
									break;
								}
							} else if (transactionRequest.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
									|| transactionRequest.getInstrumentType()
											.equalsIgnoreCase(Constant.UNITED_STATES)) {
								if (wallet2.getWalletName().equalsIgnoreCase(transactionRequest.getBrokerName())) {
									isFound = true;
									BeanUtils.copyProperties(wallet2, wallet);
									break;
								}
							}
						}
						Assets assets = saveAssets(wallet, transactionRequest, stock, crypto);
						if (assets != null) {
							BeanUtils.copyProperties(transactionRequest, oldTransaction);
							oldTransaction.setUpdationDate(new Date());
							transactionRepository.save(oldTransaction);

							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_UPDATED_SUCESSFULLY);
							map.put(Constant.DATA, oldTransaction);
							log.info(Constant.MANUAL_TRANSACTION_UPDATED_SUCESSFULLY + " ! status - {} "
									+ Constant.OK);
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_FAILED_MESSAGE);
							log.info(Constant.MANUAL_TRANSACTION_FAILED_MESSAGE + " status - {}",
									Constant.OK);
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.TRANSACTION_ID_NOT_FOUND_MESSAGE);
						log.info(Constant.TRANSACTION_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
					}
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
				log.info(Constant.ID_NOT_FOUND_MESSAGE + " " + transactionRequest.getUserId() + " status - {} "
						+ Constant.NOT_FOUND);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception! status - {}", e.getMessage());
		}
		return map;
	}

	@Override
	public Map<String, Object> getTransaction(@Valid TransactionPaginationPayload paginationPayLoad) {
		Map<String, Object> response = new HashMap<String, Object>();
		List<Transaction> list = new ArrayList<Transaction>();
		Pageable pageable = null;
		Page<Transaction> page = null;
		TransactionResponseWithPagation responsePage = new TransactionResponseWithPagation();
		Double total_buying_values = 0.0, quantity = 0.0, avg_buying_price = 0.0, total_selling_values = 0.0,
				avg_selling_price = 0.0;
		try {
			if (paginationPayLoad.getPageSize() > 0) {
				pageable = PageRequest.of(paginationPayLoad.getPageIndex(), paginationPayLoad.getPageSize());
				page = transactionRepository.getAllTransaction(paginationPayLoad.getUserId(),
						paginationPayLoad.getSymbol(), pageable);
				if (page != null && !page.isEmpty()) {
					list = page.getContent();
					List<TransactionRes> transactionResList = new ArrayList<>();
					for (Transaction transaction : list) {
						TransactionRes transactionRes = new TransactionRes();
						BeanUtils.copyProperties(transaction, transactionRes);
						if (transaction.getTransactionType().equalsIgnoreCase("Buy")) {
							total_buying_values = total_buying_values
									+ transaction.getBuyingPrice() * transaction.getQuantity();
							quantity = quantity + transaction.getQuantity();
							avg_buying_price = total_buying_values / quantity;
							transactionRes.setTotalPrice(transaction.getBuyingPrice() * transaction.getQuantity()
									+ transaction.getTransactionFees());
						} else if (transaction.getTransactionType().equalsIgnoreCase("Sell")) {
							total_selling_values = total_selling_values
									+ transaction.getBuyingPrice() * transaction.getQuantity();
							quantity = quantity + transaction.getQuantity();
							avg_selling_price = total_selling_values / quantity;
							transactionRes.setTotalPrice(transaction.getBuyingPrice() * transaction.getQuantity()
									+ transaction.getTransactionFees());
						}
						transactionResList.add(transactionRes);
					}
					responsePage.setAvg_buying_price(avg_buying_price);
					responsePage.setAvg_selling_price(avg_selling_price);
					responsePage.setPageIndex(page.getNumber());
					responsePage.setPageSize(page.getSize());
					responsePage.setIsFirstPage(page.isFirst());
					responsePage.setIsLastPage(page.isLast());
					responsePage.setTotalElement(page.getTotalElements());
					responsePage.setTotalPages(page.getTotalPages());
					responsePage.setTransactionResponseList(transactionResList);
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.MESSAGE, Constant.RECORD_FOUND_MESSAGE);
					response.put(Constant.DATA, responsePage);
					log.info("Transaction history found  successfully !! status - {} " + Constant.OK);
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
					response.put(Constant.DATA, responsePage);
					log.info(Constant.RECORD_NOT_FOUND_MESSAGE + " ! status - {}", Constant.NOT_FOUND);
				}
			} else {
				response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				response.put(Constant.MESSAGE, Constant.PAGE_SIZE_MESSAGE);
				log.info(Constant.PAGE_SIZE_MESSAGE + " ! status - {}", Constant.OK);
			}
		} catch (DataAccessResourceFailureException e) {
			response.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			response.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception! status - {}", e.getMessage());
		}
		return response;
	}

	@Override
	public Map<String, Object> updateTransaction(TransactionRequest trasnsactionRequest) {
		Map<String, Object> response = new HashMap<String, Object>();
//		try {
//			if (trasnsactionRequest.getType().equalsIgnoreCase("BUY") && !trasnsactionRequest.getDateTime().isBlank()
//					&& trasnsactionRequest.getPrice() != 0 && trasnsactionRequest.getAmount() != 0
//					&& trasnsactionRequest.getTransactionId() != 0) {
//				Transaction transaction = transactionRepository.findById(trasnsactionRequest.getTransactionId())
//						.orElse(null);
//				if (transaction != null) {
//					// only for buying
//					transaction.setTradingPair(
//							!trasnsactionRequest.getTradingPair().isBlank() ? trasnsactionRequest.getTradingPair()
//									: "");
//					transaction.setAmountAdded(trasnsactionRequest.getAmount());
//					transaction.setUpdationDate(new Date());
//					transaction.setPrice(trasnsactionRequest.getPrice());
//					transaction.setNote(trasnsactionRequest.getNote());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					// calculate the cost values
//					transaction.setCost(trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount());
//					transaction.setWorth(trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount());
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2] + " via "
//							+ trasnsactionRequest.getExchangeName();
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Buy");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					// save the buying data
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.TRANSACTION_UPDATED_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//					log.info(" Buying transaction data updated successfully !! status - {} " + Constant.OK);
//				} else {
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
//					response.put(Constant.DATA, transaction);
//					log.info("data not found ! status - {}", Constant.NOT_FOUND);
//				}
//			} else if (trasnsactionRequest.getType().equalsIgnoreCase("SELL")
//					&& !trasnsactionRequest.getDateTime().isBlank() && trasnsactionRequest.getPrice() != 0
//					&& trasnsactionRequest.getAmount() != 0 && trasnsactionRequest.getTransactionId() != 0) {
//				Transaction transaction = transactionRepository.findById(trasnsactionRequest.getTransactionId())
//						.orElse(null);
//				if (transaction != null) {
//					// only for selling
//					transaction.setTradingPair(
//							!trasnsactionRequest.getTradingPair().isBlank() ? trasnsactionRequest.getTradingPair()
//									: "");
//					transaction.setAmountDeducated(trasnsactionRequest.getAmount());
//					transaction.setUpdationDate(new Date());
//					transaction.setPrice(trasnsactionRequest.getPrice());
//					transaction.setNote(trasnsactionRequest.getNote());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					// calculate the proceeds values
//					transaction.setProceeds(trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount());
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2] + " via "
//							+ trasnsactionRequest.getExchangeName();
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Sell");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					// save the selling data
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.TRANSACTION_UPDATED_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//					log.info(" Selling transaction data updated successfully !! status - {} " + Constant.OK);
//				} else {
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
//					response.put(Constant.DATA, transaction);
//					log.info("data not found ! status - {}", Constant.NOT_FOUND);
//				}
//			} 
//			else if (trasnsactionRequest.getType().equalsIgnoreCase("TRANSFER")
//					&& !trasnsactionRequest.getTransfer_sent_received_from().isBlank()
//					&& !trasnsactionRequest.getTransfer_sent_to().isBlank()
//					&& trasnsactionRequest.getAmount_transferred() != 0 && !trasnsactionRequest.getDateTime().isBlank()
//					&& !trasnsactionRequest.getTransaction_fee().isBlank()
//					&& trasnsactionRequest.getTransactionId() != 0) {
//				Transaction transaction = transactionRepository.findById(trasnsactionRequest.getTransactionId())
//						.orElse(null);
//				if (transaction != null) {
//					// only for transfer
//					transaction.setUpdationDate(new Date());
//					transaction.setTransfer_from(trasnsactionRequest.getTransfer_sent_received_from());
//					transaction.setAmountTransferred(trasnsactionRequest.getAmount_transferred());
//					transaction.setTransfer_to(trasnsactionRequest.getTransfer_sent_to());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					transaction.setNote(trasnsactionRequest.getNote());
//					// calculate the values
//					transaction.setTransfer_worthNow(
//							trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount_transferred());
//					transaction.setTransfer_worthThen(
//							trasnsactionRequest.getPrice() * trasnsactionRequest.getAmount_transferred());
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2];
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Transfer");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.TRANSACTION_UPDATED_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//					log.info(" Transfer transaction data updated successfully !! status - {} " + Constant.OK);
//				} else {
//					response.put(Constant.RESPONSE_CODE, Constant.NOT_FOUND);
//					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
//					response.put(Constant.DATA, transaction);
//					log.info("data not found ! status - {}", Constant.NOT_FOUND);
//				}
//			}
//			else if (trasnsactionRequest.getType().equalsIgnoreCase("DIVIDEND")
//					&& !trasnsactionRequest.getDateTime().isBlank() && trasnsactionRequest.getPrice() != 0
//					&& !trasnsactionRequest.getDividend_period().isBlank()
//					&& trasnsactionRequest.getTransactionId() != null) {
//				Transaction transaction = transactionRepository.findById(trasnsactionRequest.getTransactionId())
//						.orElse(null);
//				if (transaction != null) {
//					transaction.setUpdationDate(new Date());
//					transaction.setDividend_amount(trasnsactionRequest.getPrice());
//					transaction.setTransfer_fee(trasnsactionRequest.getTransaction_fee());
//					transaction.setPeriod(trasnsactionRequest.getDividend_period());
//					transaction.setNote(trasnsactionRequest.getNote());
//					// set date time & exchange name
//					String[] dateTime = trasnsactionRequest.getDateTime().split(",");
//					String calculated_date_time = dateTime[1] + " at " + dateTime[2];
//					transaction.setDateTimeAndExchnageName(calculated_date_time);
//					// set currency
////					String[] currency = trasnsactionRequest.getTradingPair().split("/");
//					transaction.setCurrency(trasnsactionRequest.getCurrency());
//					transaction.setExchange(trasnsactionRequest.getExchangeName());
//					transaction.setType("Dividend");
//					transaction.setInstrumentType(trasnsactionRequest.getInstrumentType());
//					transaction.setStatus(Constant.ONE);
//					transaction.setCrypto_or_stock_symbol(trasnsactionRequest.getCrypto_or_stock_symbol());
//					// save the transfer data
//					transactionRepository.save(transaction);
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.TRANSACTION_UPDATED_SUCESSFULLY);
//					response.put(Constant.DATA, transaction);
//					log.info(" Dividend transaction data updated successfully !! status - {} " + Constant.OK);
//				} else {
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
//					response.put(Constant.DATA, transaction);
//					log.info("data not found ! status - {}", Constant.NOT_FOUND);
//				}
//			} else {
//				response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//				response.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
//				log.info("invalid transaction type. Please enter valid transaction type ! status - {}", Constant.OK);
//			}
//		} catch (Exception e) {
//			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception! status - {}", e.getMessage());
//		}
		return response;
	}

	@Override
	public Map<String, Object> deleteTransactionById(Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		Transaction transaction = null;
		try {
			transaction = transactionRepository.findById(id).orElse(null);
			if (transaction != null) {
				transactionRepository.delete(transaction);
				response.put(Constant.RESPONSE_CODE, Constant.OK);
				response.put(Constant.MESSAGE, Constant.MANUAL_TRANSACTION_DELETED_SUCESSFULLY);
				response.put(Constant.DATA, transaction);
				log.info("transaction data deleted successfully !! status - {} " + Constant.OK);
			} else {
				response.put(Constant.RESPONSE_CODE, Constant.OK);
				response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
				response.put(Constant.DATA, transaction);
				log.info("data not found ! status - {}", Constant.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception! status - {}", e.getMessage());
		}
		return response;
	}

	@Override
	public Map<String, Object> getTransactionByFilter(List<String> types) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			if (!types.isEmpty()) {
				List<Transaction> transactions = null;// transactionRepository.findByTypeIn(types);
				log.info(" get transaction with type " + transactions.size());
				if (!transactions.isEmpty() && transactions != null) {
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.MESSAGE, Constant.RECORD_FOUND_MESSAGE);
					response.put(Constant.DATA, transactions);
					log.info("Transaction history found  successfully !! status - {} " + Constant.OK);
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.NOT_FOUND);
					response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
					response.put(Constant.DATA, transactions);
					log.info("data not found ! status - {}", Constant.RECORD_FOUND_MESSAGE);
				}
			} else {
				response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				response.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
				log.info("invalid transaction type. Please enter valid transaction type ! status - {}", Constant.OK);
			}
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception! status - {}", e.getMessage());
		}
		return response;
	}

//	@Override
//	public Map<String, Object> checkAvailableCash(AvailableCashRequestPayload availableCashRequestPayload) {
//		Map<String, Object> response = new HashMap<String, Object>();
//		try {
//			User user = userRepository.findByIdAndStatus(availableCashRequestPayload.getUserId(), Constant.ONE);
//			if (user != null) {
//				if(!availableCashRequestPayload.getTradingPair().isEmpty()) {
//					
//				}
//				String[] symbol_currency = availableCashRequestPayload.getTradingPair().split("/");
//				String symbol = symbol_currency[0];
//				String currency = symbol_currency[1];
//				List<ManageBalance> manageBalanceList = manageBalanceRepository
//						.findByAllTransactionByUserId(availableCashRequestPayload.getUserId(), currency);
//				ManageBalance balance = new ManageBalance();
//				/* calculate total price */
//				Double totalPrice = availableCashRequestPayload.getPrice() * availableCashRequestPayload.getAmount();
//				if (availableCashRequestPayload.getType().equalsIgnoreCase("buy")) {
//					Double oldTotalBalance = 0d;
//					for (ManageBalance manageBalance : manageBalanceList) {
//						oldTotalBalance = oldTotalBalance + manageBalance.getAmount();
//					}
//					if (manageBalanceList.size() > 0) {
//						if (totalPrice <= oldTotalBalance) {
//							String amount = "-" + totalPrice;
//							balance.setAmount(Double.parseDouble(amount));
//							balance.setCreationDate(new Date());
//							balance.setStatus(Constant.ONE);
//							balance.setCurrency(currency);
//							balance.setType("withdrawal");
//							balance.setTransactionDate(
//									DateUtil.convertStringToDateTime(availableCashRequestPayload.getTranscationDate()));
//							balance.setUser(user);
//							balance.setDue_to_sell_of(symbol);
//							manageBalanceRepository.save(balance);
//							response.put(Constant.RESPONSE_CODE, Constant.OK);
//							response.put(Constant.MESSAGE, "balance available");
//							log.info("balance available. status - {}", Constant.OK);
//						} else {
//							String amount = "-" + totalPrice;
//							balance.setAmount(Double.parseDouble(amount));
//							balance.setCreationDate(new Date());
//							balance.setStatus(Constant.ONE);
//							balance.setCurrency(currency);
//							balance.setType("withdrawal");
//							balance.setTransactionDate(
//									DateUtil.convertStringToDateTime(availableCashRequestPayload.getTranscationDate()));
//							balance.setUser(user);
//							balance.setDue_to_sell_of(symbol);
//							manageBalanceRepository.save(balance);
//							response.put(Constant.RESPONSE_CODE, Constant.OK);
//							response.put(Constant.MESSAGE, "Deducting from your " + currency
//									+ " holdings might result in a negative balance.");
//							log.info("Deducting from your " + currency
//									+ " holdings might result in a negative balance. status - {}", Constant.OK);
//						}
//					} else {
//						String amount = "-" + totalPrice;
//						balance.setAmount(Double.parseDouble(amount));
//						balance.setCreationDate(new Date());
//						balance.setStatus(Constant.ONE);
//						balance.setCurrency(currency);
//						balance.setType("withdrawal");
//						balance.setTransactionDate(
//								DateUtil.convertStringToDateTime(availableCashRequestPayload.getTranscationDate()));
//						balance.setUser(user);
//						balance.setDue_to_sell_of(symbol);
//						manageBalanceRepository.save(balance);
//						response.put(Constant.RESPONSE_CODE, Constant.OK);
//						response.put(Constant.MESSAGE,
//								"Deducting from your " + currency + " holdings might result in a negative balance.");
//						log.info("Deducting from your " + currency
//								+ " holdings might result in a negative balance. status - {}", Constant.OK);
//					}
//				} else if (availableCashRequestPayload.getType().equalsIgnoreCase("sell")) {
//					Double oldTotalBalance = 0d;
//					for (ManageBalance manageBalance : manageBalanceList) {
//						oldTotalBalance = oldTotalBalance + manageBalance.getAmount();
//					}
//					if (manageBalanceList.size() > 0) {
//						if (totalPrice <= oldTotalBalance) {
//							balance.setAmount(totalPrice);
//							balance.setCreationDate(new Date());
//							balance.setStatus(Constant.ONE);
//							balance.setCurrency(currency);
//							balance.setType("deposit");
//							balance.setTransactionDate(
//									DateUtil.convertStringToDateTime(availableCashRequestPayload.getTranscationDate()));
//							balance.setUser(user);
//							balance.setDue_to_buy_of(symbol);
//							manageBalanceRepository.save(balance);
//							response.put(Constant.RESPONSE_CODE, Constant.OK);
//							response.put(Constant.MESSAGE, "balance added successfully !!");
//							log.info("balance added successfully !!. status - {}", Constant.OK);
//						} else {
//							balance.setAmount(totalPrice);
//							balance.setCreationDate(new Date());
//							balance.setStatus(Constant.ONE);
//							balance.setCurrency(currency);
//							balance.setType("deposit");
//							balance.setTransactionDate(
//									DateUtil.convertStringToDateTime(availableCashRequestPayload.getTranscationDate()));
//							balance.setUser(user);
//							balance.setDue_to_buy_of(symbol);
//							manageBalanceRepository.save(balance);
//							response.put(Constant.RESPONSE_CODE, Constant.OK);
//							response.put(Constant.MESSAGE, "balance added successfully !!");
//							log.info("balance added successfully !!. status - {}", Constant.OK);
//
//						}
//					} else {
//						balance.setAmount(totalPrice);
//						balance.setCreationDate(new Date());
//						balance.setStatus(Constant.ONE);
//						balance.setCurrency(currency);
//						balance.setType("deposit");
//						balance.setTransactionDate(
//								DateUtil.convertStringToDateTime(availableCashRequestPayload.getTranscationDate()));
//						balance.setUser(user);
//						balance.setDue_to_buy_of(symbol);
//						manageBalanceRepository.save(balance);
//						response.put(Constant.RESPONSE_CODE, Constant.OK);
//						response.put(Constant.MESSAGE, "balance added successfully !!");
//						log.info("balance added successfully !!. status - {}", Constant.OK);
//					}
//				} else if (availableCashRequestPayload.getType().equalsIgnoreCase("transfer")) {
//
//				}
//			} else {
//				response.put(Constant.RESPONSE_CODE, Constant.OK);
//				response.put(Constant.MESSAGE, Constant.USER_NOT_FOUND);
//				log.info(Constant.USER_NOT_FOUND + " " + availableCashRequestPayload.getUserId() + " status - {} "
//						+ Constant.NOT_FOUND);
//			}
//		} catch (Exception e) {
//			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception! status - {}", e.getMessage());
//		}
//		return response;
//	}

	@Override
	public Map<String, Object> checkAvailableCash(AvailableCashRequestPayload availableCashRequestPayload) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			User user = userRepository.findByIdAndStatus(availableCashRequestPayload.getUserId(), Constant.ONE);
			if (user != null) {
				Broker broker = brokerRepository.findBybrokerAndStatus(
						availableCashRequestPayload.getTradingServiceProviderName(), Constant.ONE);
				if (broker != null) {
					String[] symbol_currency = availableCashRequestPayload.getTradingPair().split("/");
					String currency = symbol_currency[1];
					List<Cash> manageBalanceList = manageBalanceRepository.findByAllTransactionByUserId(
							availableCashRequestPayload.getUserId(),
							availableCashRequestPayload.getTradingServiceProviderName());
					Cash cash = new Cash();
					/* calculate total price */
					Double totalPrice = availableCashRequestPayload.getPrice()
							* availableCashRequestPayload.getQuantity();
					if (availableCashRequestPayload.getTransactionType().equalsIgnoreCase("buy")) {
						Double oldTotalBalance = 0d;
						for (Cash manageBalance : manageBalanceList) {
							oldTotalBalance = oldTotalBalance + manageBalance.getAmount();
						}
						if (manageBalanceList.size() > 0) {
							if (totalPrice <= oldTotalBalance) {
								String amount = "-" + totalPrice;
								cash.setAmount(Double.parseDouble(amount));
								cash.setCreationDate(new Date());
								cash.setStatus(Constant.ONE);
								cash.setCurrency(currency);
								cash.setTransactionType("withdrawal");
								cash.setType(availableCashRequestPayload.getType());
								cash.setTransactionDate(
										DateUtil.StringToDate(availableCashRequestPayload.getTranscationDate()));
								cash.setNotes(Constant.AUTOMATICALLY_BALANCE_DEDUCTED_MESSAGE);
								cash.setTradingServiceProviderName(broker.getBroker());
								cash.setBroker(broker);
								cash.setUserId(availableCashRequestPayload.getUserId());
								manageBalanceRepository.save(cash);
								response.put(Constant.RESPONSE_CODE, Constant.OK);
								response.put(Constant.MESSAGE, "balance available");
								log.info("balance available. status - {}", Constant.OK);
							} else {
								String amount = "-" + totalPrice;
								cash.setAmount(Double.parseDouble(amount));
								cash.setCreationDate(new Date());
								cash.setStatus(Constant.ONE);
								cash.setCurrency(currency);
								cash.setTransactionType("withdrawal");
								cash.setType(availableCashRequestPayload.getType());
								cash.setTransactionDate(
										DateUtil.StringToDate(availableCashRequestPayload.getTranscationDate()));
								cash.setNotes(
										Constant.AUTOMATICALLY_BALANCE_DEDUCTED_DUE_TO_INSUFFICIENT_BALANCE_MESSAGE);
								cash.setTradingServiceProviderName(broker.getBroker());
								cash.setBroker(broker);
								cash.setUserId(availableCashRequestPayload.getUserId());
								manageBalanceRepository.save(cash);
								response.put(Constant.RESPONSE_CODE, Constant.OK);
								response.put(Constant.MESSAGE, "Deducting from your " + currency
										+ " holdings might result in a negative balance.");
								log.info(
										"Deducting from your " + currency
												+ " holdings might result in a negative balance. status - {}",
										Constant.OK);
							}
						} else {
							String amount = "-" + totalPrice;
							cash.setAmount(Double.parseDouble(amount));
							cash.setCreationDate(new Date());
							cash.setStatus(Constant.ONE);
							cash.setCurrency(currency);
							cash.setTransactionType("withdrawal");
							cash.setType(availableCashRequestPayload.getType());
							cash.setTransactionDate(
									DateUtil.StringToDate(availableCashRequestPayload.getTranscationDate()));
							cash.setNotes(Constant.AUTOMATICALLY_BALANCE_DEDUCTED_MESSAGE);
							cash.setTradingServiceProviderName(broker.getBroker());
							cash.setBroker(broker);
							cash.setUserId(availableCashRequestPayload.getUserId());
							manageBalanceRepository.save(cash);
							response.put(Constant.RESPONSE_CODE, Constant.OK);
							response.put(Constant.MESSAGE, "Deducting from your " + currency
									+ " holdings might result in a negative balance.");
							log.info("Deducting from your " + currency
									+ " holdings might result in a negative balance. status - {}", Constant.OK);
						}
					} else if (availableCashRequestPayload.getTransactionType().equalsIgnoreCase("sell")) {
						Double oldTotalBalance = 0d;
						for (Cash manageBalance : manageBalanceList) {
							oldTotalBalance = oldTotalBalance + manageBalance.getAmount();
						}
						if (manageBalanceList.size() > 0) {
							if (totalPrice <= oldTotalBalance) {
								cash.setAmount(totalPrice);
								cash.setCreationDate(new Date());
								cash.setStatus(Constant.ONE);
								cash.setCurrency(currency);
								cash.setTransactionType("deposit");
								cash.setType(availableCashRequestPayload.getType());
								cash.setTransactionDate(
										DateUtil.StringToDate(availableCashRequestPayload.getTranscationDate()));
								cash.setNotes(Constant.AUTOMATICALLY_BALANCE_ADDED_MESSAGE);
								cash.setTradingServiceProviderName(broker.getBroker());
								cash.setBroker(broker);
								cash.setUserId(availableCashRequestPayload.getUserId());
								manageBalanceRepository.save(cash);
								response.put(Constant.RESPONSE_CODE, Constant.OK);
								response.put(Constant.MESSAGE, "balance added successfully !!");
								log.info("balance added successfully !!. status - {}", Constant.OK);
							} else {
								cash.setAmount(totalPrice);
								cash.setCreationDate(new Date());
								cash.setStatus(Constant.ONE);
								cash.setCurrency(currency);
								cash.setTransactionType("deposit");
								cash.setType(availableCashRequestPayload.getType());
								cash.setTransactionDate(
										DateUtil.StringToDate(availableCashRequestPayload.getTranscationDate()));
								cash.setNotes(Constant.AUTOMATICALLY_BALANCE_ADDED_MESSAGE);
								cash.setTradingServiceProviderName(broker.getBroker());
								cash.setBroker(broker);
								cash.setUserId(availableCashRequestPayload.getUserId());
								manageBalanceRepository.save(cash);
								response.put(Constant.RESPONSE_CODE, Constant.OK);
								response.put(Constant.MESSAGE, "balance added successfully !!");
								log.info("balance added successfully !!. status - {}", Constant.OK);

							}
						} else {
							cash.setAmount(totalPrice);
							cash.setCreationDate(new Date());
							cash.setStatus(Constant.ONE);
							cash.setCurrency(currency);
							cash.setTransactionType("deposit");
							cash.setType(availableCashRequestPayload.getType());
							cash.setTransactionDate(
									DateUtil.StringToDate(availableCashRequestPayload.getTranscationDate()));
							cash.setNotes(Constant.AUTOMATICALLY_BALANCE_ADDED_MESSAGE);
							cash.setTradingServiceProviderName(broker.getBroker());
							cash.setBroker(broker);
							cash.setUserId(availableCashRequestPayload.getUserId());
							manageBalanceRepository.save(cash);
							response.put(Constant.RESPONSE_CODE, Constant.OK);
							response.put(Constant.MESSAGE, "balance added successfully !!");
							log.info("balance added successfully !!. status - {}", Constant.OK);
						}
					} else {
						response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
						response.put(Constant.MESSAGE, Constant.INVALID_TRANSACTION_TYPE);
						log.info(Constant.INVALID_TRANSACTION_TYPE + " - status {} " + Constant.BAD_REQUEST);
					}
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.MESSAGE, Constant.BROKER_NOT_FOUND_MESSAGE);
					log.info(Constant.BROKER_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
				}
			} else {
				response.put(Constant.RESPONSE_CODE, Constant.OK);
				response.put(Constant.MESSAGE, Constant.USER_NOT_FOUND);
				log.info(Constant.USER_NOT_FOUND + " " + availableCashRequestPayload.getUserId() + " status - {} "
						+ Constant.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception! status - {}", e.getMessage());
		}
		return response;
	}

	/* Getting cryptos every 1 hours and stock every 5 mint data */
//	@Override
//	public Map<String, Object> getPriceByCurrency(PriceRequest priceRequest) {
//		Map<String, Object> response = new HashMap<String, Object>();
//		CryptoPriceResponse cryptoPriceResponse = new CryptoPriceResponse();
//		PriceResponse priceResponse = new PriceResponse();
//		try {
//			if (priceRequest.getCountry().isBlank()) {
//				/* crypto */
//				ObjectMapper mapper = new ObjectMapper();
//				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				String currencyResponse = thirdPartyApiUtil.getPriceByCurrency(priceRequest);
//				if (currencyResponse != null && !currencyResponse.isBlank()) {
//					log.info(" third party response price list ! status - {}", Constant.OK);
//					Map<?, ?> mapResponse = mapper.readValue(currencyResponse, Map.class);
//					cryptoPriceResponse = mapper.convertValue(mapResponse, CryptoPriceResponse.class);
//					if (cryptoPriceResponse != null) {
//						for (CryptoMarketChart marketChart : cryptoPriceResponse.getData().getMarket_chart()) {
//							Date date = new Date(marketChart.getTimestamp());
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//							String formattedDate = sdf.format(date);
//							System.out.println(formattedDate);
//							if (formattedDate.equals(priceRequest.getTime())) {
//								priceResponse.setPrice(marketChart.getPrice());
//								priceResponse.setDateTime(formattedDate);
//								break;
//							}
//						}
//						if (priceResponse.getPrice() != null) {
//							response.put(Constant.MESSAGE, Constant.RECORD_FOUND_MESSAGE);
//						} else {
//							response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
//						}
//						response.put(Constant.RESPONSE_CODE, Constant.OK);
//						response.put(Constant.DATA, priceResponse);
//						log.info(Constant.RECORD_FOUND_MESSAGE + "! status - {}", Constant.OK);
//					} else {
//						response.put(Constant.RESPONSE_CODE, Constant.OK);
//						response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
//						response.put(Constant.DATA, priceResponse);
//						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
//					}
//				} else {
//					response.put(Constant.RESPONSE_CODE, Constant.OK);
//					response.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
//					response.put(Constant.DATA, priceResponse);
//					log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {} " + Constant.NOT_FOUND);
//				}
//			} else {
//				/* stock */
//
////				Country country = countryRepository.findByCountry(priceRequest.getCountry());
////				if (country != null) {
//				String apiResponse = thirdPartyApiUtil.getTimeSeries(priceRequest, Constant.FIVE_MINUTE);
//				if (!apiResponse.isBlank()) {
//					Type collectionType = new TypeToken<List<TimeSeriesDetails>>() {
//					}.getType();
//					List<TimeSeriesDetails> timeSeriesDetailsList = new Gson().fromJson(apiResponse, collectionType);
//
//					if (!timeSeriesDetailsList.isEmpty()) {
//						for (TimeSeriesDetails details : timeSeriesDetailsList) {
//							if (details.getDate().equals(priceRequest.getTime())
//									|| details.getDate().compareTo(priceRequest.getTime()) < 0) {
//								priceResponse.setPrice(Double.parseDouble(details.getOpen()));
//								priceResponse.setDateTime(details.getDate());
//								break;
//							}
//						}
//						if (priceResponse.getPrice() != null && priceResponse.getDateTime() != null) {
//							response.put(Constant.RESPONSE_CODE, Constant.OK);
//							response.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
//							response.put(Constant.DATA, priceResponse);
//							log.info("Data found! status - {}", Constant.OK);
//						} else {
//							response.put(Constant.RESPONSE_CODE, Constant.OK);
//							response.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
//							response.put(Constant.DATA, priceResponse);
//							log.info(Constant.DATA_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
//						}
//					} else {
//						response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//						response.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_FROM_THIRD_PARTY_MESSAGE);
//						log.info(Constant.DATA_NOT_FOUND_FROM_THIRD_PARTY_MESSAGE + "! status - {}",
//								Constant.SERVER_ERROR);
//					}
//				} else {
//					response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//					response.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
//					log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
//				}
////				} else {
////					map.put(Constant.RESPONSE_CODE, Constant.OK);
////					map.put(Constant.MESSAGE, Constant.COUNTRY_NOT_FOUND_MESSAGE);
////					log.info(Constant.COUNTRY_NOT_FOUND_MESSAGE + " ! status - {}", Constant.OK);
////				}
//			}
//		} catch (Exception e) {
//			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception! status - {}", e.getMessage());
//		}
//		return response;
//	}

	@Override
	public Map<String, Object> getPriceByCurrency(PriceRequest priceRequest) {
		Map<String, Object> response = new HashMap<String, Object>();
		CryptoPriceResponse cryptoPriceResponse = new CryptoPriceResponse();
		PriceResponse priceResponse = new PriceResponse();
		try {
			if (priceRequest.getCountry().isBlank()) {
				/* crypto */
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				String currencyResponse = thirdPartyApiUtil.getPriceByCurrency(priceRequest);
				if (currencyResponse != null && !currencyResponse.isBlank()) {
					log.info(" third party response price list ! status - {}", Constant.OK);
					Map<?, ?> mapResponse = mapper.readValue(currencyResponse, Map.class);
					cryptoPriceResponse = mapper.convertValue(mapResponse, CryptoPriceResponse.class);
					if (cryptoPriceResponse != null) {
						for (CryptoMarketChart marketChart : cryptoPriceResponse.getData().getMarket_chart()) {
							Date date = new Date(marketChart.getTimestamp());
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String formattedDate = sdf.format(date);
							System.out.println(formattedDate);
							if (formattedDate.contains(priceRequest.getTime())) {
								priceResponse.setPrice(marketChart.getPrice());
								priceResponse.setDateTime(formattedDate);
								break;
							}
						}
						if (priceResponse.getPrice() != null) {
							response.put(Constant.MESSAGE, Constant.RECORD_FOUND_MESSAGE);
						} else {
							response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
						}
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.DATA, priceResponse);
						log.info(Constant.RECORD_FOUND_MESSAGE + "! status - {}", Constant.OK);
					} else {
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.MESSAGE, Constant.RECORD_NOT_FOUND_MESSAGE);
						response.put(Constant.DATA, priceResponse);
						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
					}
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
					response.put(Constant.DATA, priceResponse);
					log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {} " + Constant.NOT_FOUND);
				}
			} else {
				/* stock */

//				Country country = countryRepository.findByCountry(priceRequest.getCountry());
//				if (country != null) {
				String apiResponse = thirdPartyApiUtil.getTimeSeries(priceRequest, Constant.ONE_DAY);
				if (!apiResponse.isBlank()) {
					Type collectionType = new TypeToken<List<TimeSeriesDetails>>() {
					}.getType();
					List<TimeSeriesDetails> timeSeriesDetailsList = new Gson().fromJson(apiResponse, collectionType);

					if (!timeSeriesDetailsList.isEmpty()) {
						for (TimeSeriesDetails details : timeSeriesDetailsList) {
							if (details.getDate().contains(priceRequest.getTime())) {
								priceResponse.setPrice(Double.parseDouble(details.getOpen()));
								priceResponse.setDateTime(details.getDate());
								break;
							}
						}
						if (priceResponse.getPrice() != null && priceResponse.getDateTime() != null) {
							response.put(Constant.RESPONSE_CODE, Constant.OK);
							response.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
							response.put(Constant.DATA, priceResponse);
							log.info("Data found! status - {}", Constant.OK);
						} else {
							response.put(Constant.RESPONSE_CODE, Constant.OK);
							response.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
							response.put(Constant.DATA, priceResponse);
							log.info(Constant.DATA_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
						}
					} else {
						response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
						response.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_FROM_THIRD_PARTY_MESSAGE);
						log.info(Constant.DATA_NOT_FOUND_FROM_THIRD_PARTY_MESSAGE + "! status - {}",
								Constant.SERVER_ERROR);
					}
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
					response.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
					log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
				}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.COUNTRY_NOT_FOUND_MESSAGE);
//					log.info(Constant.COUNTRY_NOT_FOUND_MESSAGE + " ! status - {}", Constant.OK);
//				}
			}
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			response.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception! status - {}", e.getMessage());
		}
		return response;
	}

	@Override
	public Map<String, Object> getDividendPeriod() {
		Map<String, Object> response = new HashMap<String, Object>();
		List<String> dividendPeriodResponse = new ArrayList<String>();
		List<DividendPeriod> dividendPeriodList = dividendPeriodRepository.findAll();
		if (!dividendPeriodList.isEmpty()) {
			for (DividendPeriod dividendPeriod : dividendPeriodList) {
				dividendPeriodResponse.add(dividendPeriod.getName());
			}
			response.put(Constant.RESPONSE_CODE, Constant.OK);
			response.put(Constant.DATA, dividendPeriodResponse);
			response.put(Constant.MESSAGE, Constant.DATA_FOUND);
			log.info(Constant.DATA_FOUND + "! status - {}", Constant.OK);
		} else {
			response.put(Constant.RESPONSE_CODE, Constant.OK);
			response.put(Constant.DATA, dividendPeriodResponse);
			response.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
			log.info(Constant.DATA_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
		}
		return response;
	}

	@Override
	public Map<String, Object> getAddDividendPeriod(DividendPeriodRequestPayload dividendPeriodPayload) {
		Map<String, Object> response = new HashMap<String, Object>();
		DividendPeriod dividendPeriod = new DividendPeriod();
		if (dividendPeriodPayload != null) {
			if (dividendPeriodPayload.getId() > 0) {
				/* --------- Update here ----------- */
				dividendPeriod = dividendPeriodRepository.findByIdAndStatus(dividendPeriodPayload.getId(),
						Constant.ONE);
				if (dividendPeriod != null) {
					if (!dividendPeriod.getName().equals(dividendPeriodPayload.getDividendName())) {
						dividendPeriod.setName(dividendPeriodPayload.getDividendName());
						dividendPeriod.setUpdationDate(new Date());
						dividendPeriodRepository.save(dividendPeriod);
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.DATA, dividendPeriod);
						response.put(Constant.MESSAGE, Constant.DATA_UPDATED_SUCCESSFULLLY);
						log.info(
								"Dividend period updated successfully !! status - { } " + Constant.OK + dividendPeriod);
					} else {
						response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
						response.put(Constant.DATA, dividendPeriod.getName());
						response.put(Constant.MESSAGE, Constant.DIVIDEND_NAME_ALREADY_EXIT);
						log.info(Constant.DIVIDEND_NAME_ALREADY_EXIT + "! status - {}", Constant.BAD_REQUEST);
					}
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.NOT_FOUND);
					response.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
					log.info(Constant.ID_NOT_FOUND_MESSAGE + "! status - {}", Constant.NOT_FOUND);
				}
			} else {
				/* --------- Add here ---------- */
				DividendPeriod oldDividendPeriod = dividendPeriodRepository
						.findByName(dividendPeriodPayload.getDividendName());
				if (oldDividendPeriod == null) {
					dividendPeriod.setName(dividendPeriodPayload.getDividendName());
					dividendPeriod.setCreationDate(new Date());
					dividendPeriod.setStatus(Constant.ONE);
					dividendPeriodRepository.save(dividendPeriod);
					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.DATA, dividendPeriod);
					response.put(Constant.MESSAGE, Constant.DATA_SAVED_SUCCESSFULLY);
					log.info("Dividend period saved successfully !! status - { } " + Constant.OK + dividendPeriod);
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
					response.put(Constant.DATA, oldDividendPeriod.getName());
					response.put(Constant.MESSAGE, Constant.DIVIDEND_NAME_ALREADY_EXIT);
					log.info(Constant.DIVIDEND_NAME_ALREADY_EXIT + "! status - {}", Constant.BAD_REQUEST);
				}
			}
		} else {
			response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
			response.put(Constant.MESSAGE, Constant.BAD_REQUEST_MESSAGE);
			log.info(Constant.BAD_REQUEST_MESSAGE + "! status - {}", Constant.BAD_REQUEST);
		}
		return response;
	}

	@Override
	public Map<String, Object> currencyConverter(CurrencyConverterRequestPayload converterRequestPayload) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			Double amount = 0d;
			CurrencyConverterResponse currencyConverterResponse = new CurrencyConverterResponse();
			String responsestr = thirdPartyApiUtil.getCurrencyConverter(converterRequestPayload);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (!responsestr.isBlank()) {
				Map<?, ?> mapResponse = mapper.readValue(responsestr, Map.class);
				ConvertersResponse convertersResponse = mapper.convertValue(mapResponse, ConvertersResponse.class);
				if (convertersResponse.getResult().equals("success")) {
					amount = convertersResponse.getConversion_rate() * converterRequestPayload.getAmount();
					currencyConverterResponse.setAmount(amount);
					currencyConverterResponse.setBase_code(converterRequestPayload.getBase_code());
					currencyConverterResponse.setTarget_code(converterRequestPayload.getTarget_code());

					response.put(Constant.RESPONSE_CODE, Constant.OK);
					response.put(Constant.DATA, currencyConverterResponse);
					response.put(Constant.MESSAGE, Constant.DATA_SAVED_SUCCESSFULLY);
					log.info("Currency converter successfully !! status - { } " + Constant.OK
							+ currencyConverterResponse);
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
					response.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
					log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
				}
			} else {
				response.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
				response.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
				log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
			}
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
			response.put(Constant.MESSAGE, Constant.BAD_REQUEST_MESSAGE);
			log.info(Constant.BAD_REQUEST_MESSAGE + "! status - {}", Constant.BAD_REQUEST);
		}

		return response;
	}

	@Override
	public Map<String, Object> getAvailableBalance(Long userId, String broker) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			Double availableBalance = 0.0d;
			if (userId != null) {
				if (broker != null && !broker.isBlank()) {
					List<Cash> cashList = manageBalanceRepository.findByUserIdAndTradingServiceProviderName(userId,
							broker);
					if (!cashList.isEmpty()) {
						for (Cash cash : cashList) {
							availableBalance = availableBalance + cash.getAmount();
						}
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.DATA, availableBalance);
						response.put(Constant.MESSAGE, Constant.BALANCE_AVAILABLE_MESSAGE);
						log.info(
								Constant.BALANCE_AVAILABLE_MESSAGE + " status - { } " + Constant.OK + availableBalance);
					} else {
						response.put(Constant.RESPONSE_CODE, Constant.OK);
						response.put(Constant.MESSAGE, Constant.BALANCE_NOT_AVAILABLE_MESSAGE);
						response.put(Constant.DATA, availableBalance);
						log.info(Constant.BALANCE_NOT_AVAILABLE_MESSAGE + "! status - {}", Constant.OK);
					}
				} else {
					response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
					response.put(Constant.MESSAGE, Constant.BROKER_CANT_BE_EMPTY);
					log.info(Constant.BROKER_CANT_BE_EMPTY + "! status - {}", Constant.BAD_REQUEST);
				}
			} else {
				response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				response.put(Constant.MESSAGE, Constant.USER_ID_CANT_NULL_MESSAGE);
				log.info(Constant.USER_ID_CANT_NULL_MESSAGE + "! status - {}", Constant.BAD_REQUEST);
			}
		} catch (Exception e) {
			response.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
			response.put(Constant.MESSAGE, Constant.BAD_REQUEST_MESSAGE);
			log.info(Constant.BAD_REQUEST_MESSAGE + "! status - {}", Constant.BAD_REQUEST);
		}
		return response;
	}

}
