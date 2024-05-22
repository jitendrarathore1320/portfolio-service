package com.advantal.serviceimpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.advantal.externalservice.UserService;
import com.advantal.model.Assets;
import com.advantal.model.Cash;
import com.advantal.model.Portfolio;
import com.advantal.model.PortfolioGraphHistory;
import com.advantal.model.Stock;
import com.advantal.model.User;
import com.advantal.model.Wallet;
import com.advantal.model.WalletGraphHistory;
import com.advantal.repository.AssetsRepository;
import com.advantal.repository.ManageBalanceRepository;
import com.advantal.repository.PortfolioGraphHistoryRepository;
import com.advantal.repository.PortfolioRepository;
import com.advantal.repository.TransactionRepository;
import com.advantal.repository.UserRepository;
import com.advantal.repository.WalletGraphHistoryRepository;
import com.advantal.repository.WalletRepository;
import com.advantal.requestpayload.CreatePortfolioRequestPayload;
import com.advantal.requestpayload.DisconnectWalletRequestPayload;
import com.advantal.requestpayload.GainerLoserRequestPayload;
import com.advantal.requestpayload.GraphRequestPayload;
import com.advantal.requestpayload.MostActiveRequestPayload;
import com.advantal.requestpayload.WalletConnectionRequest;
import com.advantal.responsepayload.AssetsRes;
import com.advantal.responsepayload.AssetsRes2;
import com.advantal.responsepayload.DoughNutChart;
import com.advantal.responsepayload.DoughNutChartResponse;
import com.advantal.responsepayload.ErrorResponse;
import com.advantal.responsepayload.KeyResponse;
import com.advantal.responsepayload.PortfolioResponse;
import com.advantal.responsepayload.PortfolioResponsePayload;
import com.advantal.responsepayload.TickerDetail;
import com.advantal.responsepayload.TimeSeriesDetails;
import com.advantal.responsepayload.TsCryptoDetailsResponse;
import com.advantal.responsepayload.TsPrice;
import com.advantal.responsepayload.WalletBalance;
import com.advantal.responsepayload.WalletResponse;
import com.advantal.responsepayload.WalletResponsePayload;
import com.advantal.service.PortfolioService;
import com.advantal.utils.Constant;
import com.advantal.utils.DateUtil;
import com.advantal.utils.MethodUtil;
import com.advantal.utils.ThirdPartyApiUtil;
import com.advantal.utils.UtilityMethods;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.core.util.Fields;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PortfolioServiceImpl implements PortfolioService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AssetsRepository assetsRepository;

	@Autowired
	private PortfolioRepository portfolioRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	TransactionRepository transactionRepository;

	/* External service */
	@Autowired
	private UserService userService;

	@Autowired
	private ThirdPartyApiUtil thirdPartyApiUtil;

	@Autowired(required = true)
	MethodUtil methodUtil;

	@Autowired
	private WalletGraphHistoryRepository walletGraphHistoryRepository;

	@Autowired
	private PortfolioGraphHistoryRepository portfolioGraphHistoryRepository;

	@Autowired
	private ManageBalanceRepository manageBalanceRepository;

//	@Override
//	public Map<String, Object> createPortfolio(CreatePortfolioRequestPayload createPortfolioRequestPayload) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			if (createPortfolioRequestPayload.getUserId() != 0) {
//				User user = null;
//				/* Calling USER-SERVICE */
//				try {
//					Map<String, Object> res = userService.getProfileById(createPortfolioRequestPayload.getUserId());
//					Object obj = res.get("data");
//					ObjectMapper mapper = new ObjectMapper();
//					user = mapper.convertValue(obj, User.class);
//					log.info(Constant.DATA_FOUND + " ! status - {}", Constant.OK);
//				} catch (Exception e) {
//					map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//					map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//					log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
//					return map;
//				}
//				if (user != null) {
//					Portfolio portfolio = new Portfolio();
//					if (createPortfolioRequestPayload.getPortfolioId() == 0) {
//						if (user.getPortfolioList() == null || user.getPortfolioList().isEmpty()) {
//							portfolio.setPortfolioName("Main Portfolio");
//							portfolio.setStatus(Constant.ONE);
//							portfolio.setCreationDate(new Date());
//							portfolio.setUserIdFk(createPortfolioRequestPayload.getUserId());
//						} else {
//							if (createPortfolioRequestPayload.getPortfolioName().isBlank()) {
//								portfolio.setPortfolioName("New Portfolio_" + user.getPortfolioList().size());
//							} else {
//								portfolio.setPortfolioName(createPortfolioRequestPayload.getPortfolioName());
//							}
//							portfolio.setStatus(Constant.ONE);
//							portfolio.setCreationDate(new Date());
//							portfolio.setUserIdFk(createPortfolioRequestPayload.getUserId());
//						}
//
//						portfolioRepository.save(portfolio);
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.PORTFOLIO_CREATED_MESSAGE);
//						map.put("portfolioId", portfolio.getId());
//						log.info(Constant.PORTFOLIO_CREATED_MESSAGE + " Status - {}", Constant.OK);
//					} else {
//						Boolean isFound = false;
//						for (Portfolio portfolio2 : user.getPortfolioList()) {
//							if (portfolio2.getId() == createPortfolioRequestPayload.getPortfolioId()) {
//								isFound = true;
//								break;
//							}
//						}
//						if (isFound == true) {
//							for (Portfolio portfolio3 : user.getPortfolioList()) {
//								if (portfolio3.getId() == createPortfolioRequestPayload.getPortfolioId()) {
//									if (!createPortfolioRequestPayload.getPortfolioName().isBlank()) {
//										portfolio3.setPortfolioName(createPortfolioRequestPayload.getPortfolioName());
//									}
//									portfolio3.setUpdationDate(new Date());
//									portfolioRepository.save(portfolio3);
//									map.put(Constant.RESPONSE_CODE, Constant.OK);
//									map.put(Constant.MESSAGE, Constant.PORTFOLIO_UPDATED_MESSAGE);
//									map.put("portfolioId", portfolio3.getId());
//									log.info(Constant.PORTFOLIO_UPDATED_MESSAGE + " Status - {}", Constant.OK);
//									break;
//								}
//							}
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//							log.info(Constant.ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//						}
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//					log.info(Constant.ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
//				log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception : " + e.getMessage());
//		}
//		return map;
//	}

	@Override
	public Map<String, Object> createPortfolio(CreatePortfolioRequestPayload createPortfolioRequestPayload) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (createPortfolioRequestPayload.getUserId() != 0) {
				User user = null;
				/* Calling USER-SERVICE */
				try {
					Map<String, Object> res = userService.getProfileById(createPortfolioRequestPayload.getUserId());
					Object obj = res.get("data");
					ObjectMapper mapper = new ObjectMapper();
					user = mapper.convertValue(obj, User.class);
					log.info(Constant.DATA_FOUND + " ! status - {}", Constant.OK);
				} catch (Exception e) {
					map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
					map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
					log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
					return map;
				}
				if (user != null) {
					Portfolio portfolio = new Portfolio();
					if (createPortfolioRequestPayload.getPortfolioId() == 0) {
						if (user.getPortfolioList() == null || user.getPortfolioList().isEmpty()) {
							portfolio.setPortfolioName("Main Portfolio");
							portfolio.setStatus(Constant.ONE);
							portfolio.setIsAvailableCashEnabled(Constant.FALSE);
							portfolio.setCreationDate(new Date());
							portfolio.setUserIdFk(createPortfolioRequestPayload.getUserId());
//							portfolioRepository.save(portfolio);
						} else {
							if (createPortfolioRequestPayload.getPortfolioName().isBlank()) {
//								portfolio.setPortfolioName("New Portfolio_" + user.getPortfolioList().size());
								portfolio.setPortfolioName("Main Portfolio");
							} else {
								portfolio.setPortfolioName(createPortfolioRequestPayload.getPortfolioName());
							}
							portfolio.setIsAvailableCashEnabled(Constant.FALSE);
							portfolio.setStatus(Constant.ONE);
							portfolio.setCreationDate(new Date());
							portfolio.setUserIdFk(createPortfolioRequestPayload.getUserId());
						}
						portfolioRepository.save(portfolio);
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.PORTFOLIO_CREATED_MESSAGE);
						map.put("portfolioId", portfolio.getId());
						log.info(Constant.PORTFOLIO_CREATED_MESSAGE + " Status - {}", Constant.OK);
					} else {
						Boolean isFound = false;
						for (Portfolio portfolio2 : user.getPortfolioList()) {
							if (portfolio2.getId() == createPortfolioRequestPayload.getPortfolioId()) {
								isFound = true;
								BeanUtils.copyProperties(portfolio2, portfolio);
								break;
							}
						}
						if (isFound == true) {
//							for (Portfolio portfolio3 : user.getPortfolioList()) {
//								if (portfolio3.getId() == createPortfolioRequestPayload.getPortfolioId()) {
							if (!createPortfolioRequestPayload.getPortfolioName().isBlank()) {
								portfolio.setPortfolioName(createPortfolioRequestPayload.getPortfolioName());
							}
							portfolio.setUpdationDate(new Date());
							portfolioRepository.save(portfolio);
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.PORTFOLIO_UPDATED_MESSAGE);
							map.put("portfolioId", portfolio.getId());
							log.info(Constant.PORTFOLIO_UPDATED_MESSAGE + " Status - {}", Constant.OK);
//									break;
//								}
//							}
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
							log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
						}
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
					log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
				log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception : " + e.getMessage());
		}
		return map;
	}

//	@Override
//	public Map<String, Object> getPortfolio(Long userId) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			if (userId != null && userId != 0) {
//				User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
//				if (user != null) {
//					List<PortfolioResponsePayload> portfolioResponsePayloadList = new ArrayList<>();
//					List<WalletResponse> walletResponseList2 = new ArrayList<>();
//					if (user.getPortfolioList() != null && !user.getPortfolioList().isEmpty()) {
//						Double allPortfolioValue = 0.0, allPortfolioDailyPriceGain = 0.0,
//								allPortfolioDailyPercentageGain = 0.0, allPortfolioTotalPriceGain = 0.0,
//								allPortfolioTotalPercentageGain = 0.0;
//						for (int i = 0; i < user.getPortfolioList().size(); i++) {
//							Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
//									totalPriceGain = 0.0, totalPercentageGain = 0.0;
//							PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//							List<WalletResponse> walletResponseList = new ArrayList<>();
//							for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
//								if (wallet.getStatus().equals(Constant.ONE)) {
//									Double totalBalance = 0.0;
//									List<AssetsRes2> assetsRes2List = new ArrayList<>();
//									WalletResponse walletResponse = new WalletResponse();
//									for (Assets assets : wallet.getAssetsList()) {
//										if (!assets.getSymbol().equalsIgnoreCase("USDT")) {
//											AssetsRes2 assetsRes2 = new AssetsRes2();
//											BeanUtils.copyProperties(assets, assetsRes2);
//											assetsRes2.setClosePrice(Double.valueOf(assets.getStock().getPrice()));
//											assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
//											assetsRes2.setExchange(assets.getStock().getExchange());
//											assetsRes2.setInstrumentId(assets.getStock().getId());
//											
//											totalBalance = totalBalance + assets.getTotalPrice();
//											assetsRes2List.add(assetsRes2);
//										}
//									}
//									walletResponse.setWalletId(wallet.getId());
//									walletResponse.setWalletName(wallet.getWalletName());
//									walletResponse.setTotalBalance(totalBalance);
//									walletResponse.setNoOfAssets(assetsRes2List.size());
//									walletResponse.setAssetsResList(assetsRes2List);
//									walletResponseList.add(walletResponse);
//
////									totalValue = totalValue + wallet.getLatestTotalBalance();
//									totalValue = totalValue + walletResponse.getTotalBalance();
//									dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
//									dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
//									totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
//									totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
//								}
//							}
//
//							allPortfolioValue = allPortfolioValue + totalValue;
//							allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain + dailyPercentageGain;
//							allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
//							allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain + totalPercentageGain;
//							allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;
//
//							if (i >= 1 || user.getPortfolioList().size() == 1) {
//								BeanUtils.copyProperties(user.getPortfolioList().get(i), portfolioResponsePayload);
//								portfolioResponsePayload.setTotalPortfolioValue(totalValue != null ? totalValue : 0.0);
//								portfolioResponsePayload.setDailyPercentageGain(
//										dailyPercentageGain != null ? dailyPercentageGain : 0.0);
//								portfolioResponsePayload
//										.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
//								portfolioResponsePayload.setTotalPercentageGain(
//										totalPercentageGain != null ? totalPercentageGain : 0.0);
//								portfolioResponsePayload
//										.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
//								portfolioResponsePayload.setCurrency("USD");
//								portfolioResponsePayload.setWalletResponseList(walletResponseList);
//								portfolioResponsePayloadList.add(portfolioResponsePayload);
//							}
//							if (i == 0) {
//								walletResponseList2.addAll(walletResponseList);
//							}
//						}
//
//						if (user.getPortfolioList().size() > 1) {
//							PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//							BeanUtils.copyProperties(user.getPortfolioList().get(0), portfolioResponsePayload);
//							portfolioResponsePayload
//									.setTotalPortfolioValue(allPortfolioValue != null ? allPortfolioValue : 0.0);
//							portfolioResponsePayload.setDailyPercentageGain(
//									allPortfolioDailyPercentageGain != null ? allPortfolioDailyPercentageGain : 0.0);
//							portfolioResponsePayload.setDailyPriceGain(
//									allPortfolioDailyPriceGain != null ? allPortfolioDailyPriceGain : 0.0);
//							portfolioResponsePayload.setTotalPercentageGain(
//									allPortfolioTotalPercentageGain != null ? allPortfolioTotalPercentageGain : 0.0);
//							portfolioResponsePayload.setTotalPriceGain(
//									allPortfolioTotalPriceGain != null ? allPortfolioTotalPriceGain : 0.0);
//							portfolioResponsePayload.setCurrency("USD");
//							portfolioResponsePayload.setWalletResponseList(walletResponseList2);
//							portfolioResponsePayloadList.add(portfolioResponsePayload);
//						}
//						Collections.sort(portfolioResponsePayloadList,
//								Comparator.comparingLong(PortfolioResponsePayload::getId));
//
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
//						map.put(Constant.DATA, portfolioResponsePayloadList);
//						log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
//						map.put(Constant.DATA, portfolioResponsePayloadList);
//						log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//					log.info(Constant.ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
//				log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception : " + e.getMessage());
//		}
//		return map;
//	}

	// @ imp
//	@Override
//	public Map<String, Object> getPortfolio(Long userId, String portfolioType) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			if (portfolioType != null) {
//				if (userId != null && userId != 0) {
//					User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
//					if (user != null) {
//						List<PortfolioResponsePayload> portfolioResponsePayloadList = new ArrayList<>();
//						List<WalletResponse> walletResponseList2 = new ArrayList<>();
//						if (user.getPortfolioList() != null && !user.getPortfolioList().isEmpty()) {
//							Double allPortfolioValue = 0.0, allPortfolioDailyPriceGain = 0.0,
//									allPortfolioDailyPercentageGain = 0.0, allPortfolioTotalPriceGain = 0.0,
//									allPortfolioTotalPercentageGain = 0.0;
//
//							//
//							List<Portfolio> portfolioList = new ArrayList<>();
//							if (portfolioType.isBlank()) {
//								portfolioList.addAll(user.getPortfolioList());
//							} else if (!portfolioType.isBlank()
//									&& (portfolioType.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//											|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)
//											|| portfolioType.equalsIgnoreCase("crypto"))) {
//								portfolioList = user.getPortfolioList().stream()
//										.filter(obj -> obj.getPortfolioType().equals(portfolioType))
//										.collect(Collectors.toList());
//							} else {
//								map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//								map.put(Constant.MESSAGE, Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
//								log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE + " ! status - {}", Constant.OK);
//								return map;
//							}
//							//
//
//							for (int i = 0; i < portfolioList.size(); i++) {
//								Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
//										totalPriceGain = 0.0, totalPercentageGain = 0.0;
//								PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//								List<WalletResponse> walletResponseList = new ArrayList<>();
//								for (Wallet wallet : portfolioList.get(i).getWalletList()) {
//									if (wallet.getStatus().equals(Constant.ONE)) {
//										Double totalBalance = 0.0;
//										List<AssetsRes2> assetsRes2List = new ArrayList<>();
//										WalletResponse walletResponse = new WalletResponse();
//										for (Assets assets : wallet.getAssetsList()) {
//											if (!assets.getSymbol().equalsIgnoreCase("USDT")) {
//												//
//												Stock stock = null;
//												Crypto crypto = null;
//												AssetsRes2 assetsRes2 = new AssetsRes2();
//												if (!portfolioType.isBlank()) {
//													if (portfolioType.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//															|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														stock = stockRepository.findByIdAndSymbol(
//																assets.getInstrumentId(), assets.getSymbol());
//														if (stock != null) {
//															BeanUtils.copyProperties(assets, assetsRes2);
//															assetsRes2.setClosePrice(Double.valueOf(stock.getPrice()));
//															assetsRes2.setLogo(stock.getStockProfile().getLogo());
//															assetsRes2.setExchange(stock.getExchange());
//															assetsRes2.setInstrumentId(stock.getId());
//
//															totalBalance = totalBalance + assets.getTotalPrice();
//															assetsRes2List.add(assetsRes2);
//														} else {
//															map.put(Constant.RESPONSE_CODE, Constant.OK);
//															map.put(Constant.MESSAGE,
//																	Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
//															log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE
//																	+ "! status - {}", Constant.OK);
//															return map;
//														}
//													} else if (portfolioType.equalsIgnoreCase("crypto")) {
//														crypto = cryptoRepository.findByIdAndCryptoId(
//																assets.getInstrumentId(), assets.getSymbol());
//														if (crypto != null) {
//															BeanUtils.copyProperties(assets, assetsRes2);
////														assetsRes2.setClosePrice(Double.valueOf(crypto.getPrice()));
//															assetsRes2.setLogo(crypto.getLogo());
////														assetsRes2.setExchange(crypto.getExchange());
//															assetsRes2.setInstrumentId(crypto.getId());
//
//															totalBalance = totalBalance + assets.getTotalPrice();
//															assetsRes2List.add(assetsRes2);
//														} else {
//															map.put(Constant.RESPONSE_CODE, Constant.OK);
//															map.put(Constant.MESSAGE,
//																	Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
//															log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE
//																	+ "! status - {}", Constant.OK);
//															return map;
//														}
//													}
//												} else {
//													if (assets.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
//															|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														stock = stockRepository.findByIdAndSymbol(
//																assets.getInstrumentId(), assets.getSymbol());
//														if (stock != null) {
//															BeanUtils.copyProperties(assets, assetsRes2);
//															assetsRes2.setClosePrice(Double.valueOf(stock.getPrice()));
//															assetsRes2.setLogo(stock.getStockProfile().getLogo());
//															assetsRes2.setExchange(stock.getExchange());
//															assetsRes2.setInstrumentId(stock.getId());
//
//															totalBalance = totalBalance + assets.getTotalPrice();
//															assetsRes2List.add(assetsRes2);
//														} else {
//															map.put(Constant.RESPONSE_CODE, Constant.OK);
//															map.put(Constant.MESSAGE,
//																	Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
//															log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE
//																	+ "! status - {}", Constant.OK);
//															return map;
//														}
//													} else if (assets.getInstrumentType().equalsIgnoreCase("crypto")) {
//														crypto = cryptoRepository.findByIdAndCryptoId(
//																assets.getInstrumentId(), assets.getSymbol());
//														if (crypto != null) {
//															BeanUtils.copyProperties(assets, assetsRes2);
////														assetsRes2.setClosePrice(Double.valueOf(crypto.getPrice()));
//															assetsRes2.setLogo(crypto.getLogo());
////														assetsRes2.setExchange(crypto.getExchange());
//															assetsRes2.setInstrumentId(crypto.getId());
//
//															totalBalance = totalBalance + assets.getTotalPrice();
//															assetsRes2List.add(assetsRes2);
//														} else {
//															map.put(Constant.RESPONSE_CODE, Constant.OK);
//															map.put(Constant.MESSAGE,
//																	Constant.INSTRUMENT_NOT_FOUND_MESSAGE);
//															log.info(Constant.INSTRUMENT_NOT_FOUND_MESSAGE
//																	+ "! status - {}", Constant.OK);
//															return map;
//														}
//													}
//												}
//												//
//
////												AssetsRes2 assetsRes2 = new AssetsRes2();
////												BeanUtils.copyProperties(assets, assetsRes2);
////												assetsRes2.setClosePrice(Double.valueOf(assets.getStock().getPrice()));
////												assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
////												assetsRes2.setExchange(assets.getStock().getExchange());
////												assetsRes2.setInstrumentId(assets.getStock().getId());
//
////												totalBalance = totalBalance + assets.getTotalPrice();
////												assetsRes2List.add(assetsRes2);
//											}
//										}
//										walletResponse.setWalletId(wallet.getId());
//										walletResponse.setWalletName(wallet.getWalletName());
//										walletResponse.setTotalBalance(totalBalance);
//										walletResponse.setNoOfAssets(assetsRes2List.size());
//										walletResponse.setAssetsResList(assetsRes2List);
//										walletResponseList.add(walletResponse);
//
////										totalValue = totalValue + wallet.getLatestTotalBalance();
//										totalValue = totalValue + walletResponse.getTotalBalance();
//										dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
//										dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
//										totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
//										totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
//									}
//								}
//
//								allPortfolioValue = allPortfolioValue + totalValue;
//								allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain + dailyPercentageGain;
//								allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
//								allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain + totalPercentageGain;
//								allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;
//
//								if (i >= 1 || portfolioList.size() == 1) {
//									BeanUtils.copyProperties(portfolioList.get(i), portfolioResponsePayload);
//									portfolioResponsePayload.setPortfolioType(portfolioList.get(i).getPortfolioType());
//									portfolioResponsePayload
//											.setTotalPortfolioValue(totalValue != null ? totalValue : 0.0);
//									portfolioResponsePayload.setDailyPercentageGain(
//											dailyPercentageGain != null ? dailyPercentageGain : 0.0);
//									portfolioResponsePayload
//											.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
//									portfolioResponsePayload.setTotalPercentageGain(
//											totalPercentageGain != null ? totalPercentageGain : 0.0);
//									portfolioResponsePayload
//											.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
//									portfolioResponsePayload.setCurrency("USD");
//									portfolioResponsePayload.setWalletResponseList(walletResponseList);
//									portfolioResponsePayloadList.add(portfolioResponsePayload);
//								}
//								if (i == 0) {
//									walletResponseList2.addAll(walletResponseList);
//								}
//							}
//
//							if (portfolioList.size() > 1) {
//								PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//								BeanUtils.copyProperties(portfolioList.get(0), portfolioResponsePayload);
//								portfolioResponsePayload.setPortfolioType(portfolioList.get(0).getPortfolioType());
//								portfolioResponsePayload
//										.setTotalPortfolioValue(allPortfolioValue != null ? allPortfolioValue : 0.0);
//								portfolioResponsePayload.setDailyPercentageGain(
//										allPortfolioDailyPercentageGain != null ? allPortfolioDailyPercentageGain
//												: 0.0);
//								portfolioResponsePayload.setDailyPriceGain(
//										allPortfolioDailyPriceGain != null ? allPortfolioDailyPriceGain : 0.0);
//								portfolioResponsePayload.setTotalPercentageGain(
//										allPortfolioTotalPercentageGain != null ? allPortfolioTotalPercentageGain
//												: 0.0);
//								portfolioResponsePayload.setTotalPriceGain(
//										allPortfolioTotalPriceGain != null ? allPortfolioTotalPriceGain : 0.0);
//								portfolioResponsePayload.setCurrency("USD");
//								portfolioResponsePayload.setWalletResponseList(walletResponseList2);
//								portfolioResponsePayloadList.add(portfolioResponsePayload);
//							}
//							Collections.sort(portfolioResponsePayloadList,
//									Comparator.comparingLong(PortfolioResponsePayload::getId));
//
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
//							map.put(Constant.DATA, portfolioResponsePayloadList);
//							log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
//							map.put(Constant.DATA, portfolioResponsePayloadList);
//							log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
//						}
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//						log.info(Constant.ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
//					log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//				map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
//				log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " ! status - {}", Constant.OK);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception : " + e.getMessage());
//		}
//		return map;
//	}

//	@Override
//	public Map<String, Object> getPortfolio(Long userId, String portfolioType) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			if (portfolioType != null) {
//				if (userId != null && userId != 0) {
//					User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
//					if (user != null) {
//						List<PortfolioResponsePayload> portfolioResponsePayloadList = new ArrayList<>();
//						List<WalletResponse> walletResponseList2 = new ArrayList<>();
//						if (user.getPortfolioList() != null && !user.getPortfolioList().isEmpty()) {
//							Double allPortfolioValue = 0.0, allPortfolioDailyPriceGain = 0.0,
//									allPortfolioDailyPercentageGain = 0.0, allPortfolioTotalPriceGain = 0.0,
//									allPortfolioTotalPercentageGain = 0.0;
//
//							//
//							List<Portfolio> portfolioList = new ArrayList<>();
//							if (portfolioType.isBlank()) {
//								portfolioList.addAll(user.getPortfolioList());
//							} else if (!portfolioType.isBlank()
//									&& (portfolioType.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//											|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)
//											|| portfolioType.equalsIgnoreCase("crypto"))) {
////								portfolioList = user.getPortfolioList().stream()
////										.filter(obj -> obj.getPortfolioType().equals(portfolioType))
////										.collect(Collectors.toList());
//							} else {
//								map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//								map.put(Constant.MESSAGE, Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
//								log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE + " ! status - {}", Constant.OK);
//								return map;
//							}
//							//
//
//							for (int i = 0; i < portfolioList.size(); i++) {
//								Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
//										totalPriceGain = 0.0, totalPercentageGain = 0.0;
//								PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//								List<WalletResponse> walletResponseList = new ArrayList<>();
//								for (Wallet wallet : portfolioList.get(i).getWalletList()) {
//									if (wallet.getStatus().equals(Constant.ONE)) {
//										Double totalBalance = 0.0;
//										List<AssetsRes2> assetsRes2List = new ArrayList<>();
//										WalletResponse walletResponse = new WalletResponse();
//										for (Assets assets : wallet.getAssetsList()) {
//											if (!assets.getSymbol().equalsIgnoreCase("USDT")) {
//												//
//												Stock stock = null;
//												Crypto crypto = null;
//												AssetsRes2 assetsRes2 = new AssetsRes2();
//												if (!portfolioType.isBlank()) {
//													if (portfolioType.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//															|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
//														assetsRes2
//																.setLogo(assets.getStock().getStockProfile().getLogo());
//														assetsRes2.setExchange(assets.getStock().getExchange());
//														assetsRes2.setInstrumentId(assets.getStock().getId());
//
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//
//													} else if (portfolioType.equalsIgnoreCase("crypto")) {
//														//
//														String price = "0";
//														String cryptoDetailsResponse = thirdPartyApiUtil
//																.getCryptoDetails(assets.getCrypto().getCryptoId());
//														if ((!cryptoDetailsResponse.isBlank())) {
//															log.info("data found ! status - {}", cryptoDetailsResponse);
//															/* using objectMapper */
//															ObjectMapper mapper = new ObjectMapper();
//															mapper.configure(
//																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//																	false);
//															/* get CryptoMarketDetails */
//															Map<?, ?> mapResponse = mapper
//																	.readValue(cryptoDetailsResponse, Map.class);
//
//															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
//															data = mapper.convertValue(mapResponse,
//																	TsCryptoDetailsResponse.class);
//															log.info("data found ! status - {}", data);
//
//															/* save market data in third party apis */
//															for (TsPrice cryptoListPrice : data.getData()
//																	.getMarket_data().getPrice()) {
//																/* CryptoMarketDetails */
//																if (cryptoListPrice.getPrice_latest() == null) {
//																	price = "0";
//																} else {
//																	price = methodUtil.formattedValues(
//																			cryptoListPrice.getPrice_latest());
//																}
//															}
//														} else {
//															map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//															map.put(Constant.MESSAGE,
//																	Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//															log.info(Constant.INTERNAL_SERVER_ERROR_MESSAGE
//																	+ "! status - {}", Constant.SERVER_ERROR);
//															return map;
//														}
//														//
//
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(Double.valueOf(price));
//														assetsRes2.setLogo(assets.getCrypto().getLogo());
////														assetsRes2.setExchange(crypto.getExchange());
//														assetsRes2.setInstrumentId(assets.getCrypto().getId());
//
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//													}
//												} else {
//													if (assets.getInstrumentType()
//															.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//															|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
//														assetsRes2
//																.setLogo(assets.getStock().getStockProfile().getLogo());
//														assetsRes2.setExchange(assets.getStock().getExchange());
//														assetsRes2.setInstrumentId(assets.getStock().getId());
//
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//
//													} else if (assets.getInstrumentType().equalsIgnoreCase("crypto")) {
//														//
//														String price = "0";
//														String cryptoDetailsResponse = thirdPartyApiUtil
//																.getCryptoDetails(assets.getCrypto().getCryptoId());
//														if ((!cryptoDetailsResponse.isBlank())) {
//															log.info("data found ! status - {}", cryptoDetailsResponse);
//															/* using objectMapper */
//															ObjectMapper mapper = new ObjectMapper();
//															mapper.configure(
//																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//																	false);
//															/* get CryptoMarketDetails */
//															Map<?, ?> mapResponse = mapper
//																	.readValue(cryptoDetailsResponse, Map.class);
//
//															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
//															data = mapper.convertValue(mapResponse,
//																	TsCryptoDetailsResponse.class);
//															log.info("data found ! status - {}", data);
//
//															/* save market data in third party apis */
//															for (TsPrice cryptoListPrice : data.getData()
//																	.getMarket_data().getPrice()) {
//																/* CryptoMarketDetails */
//																if (cryptoListPrice.getPrice_latest() == null) {
//																	price = "0";
//																} else {
//																	price = methodUtil.formattedValues(
//																			cryptoListPrice.getPrice_latest());
//																}
//															}
//														} else {
//															map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//															map.put(Constant.MESSAGE,
//																	Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//															log.info(Constant.INTERNAL_SERVER_ERROR_MESSAGE
//																	+ "! status - {}", Constant.SERVER_ERROR);
//															return map;
//														}
//														//
//														
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(Double.valueOf(price));
//														assetsRes2.setLogo(assets.getCrypto().getLogo());
////														assetsRes2.setExchange(crypto.getExchange());
//														assetsRes2.setInstrumentId(assets.getCrypto().getId());
//
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//
//													}
//												}
//												//
//
////												AssetsRes2 assetsRes2 = new AssetsRes2();
////												BeanUtils.copyProperties(assets, assetsRes2);
////												assetsRes2.setClosePrice(Double.valueOf(assets.getStock().getPrice()));
////												assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
////												assetsRes2.setExchange(assets.getStock().getExchange());
////												assetsRes2.setInstrumentId(assets.getStock().getId());
//
////												totalBalance = totalBalance + assets.getTotalPrice();
////												assetsRes2List.add(assetsRes2);
//											}
//										}
//										walletResponse.setWalletId(wallet.getId());
//										walletResponse.setWalletName(wallet.getWalletName());
//										walletResponse.setTotalBalance(totalBalance);
//										walletResponse.setNoOfAssets(assetsRes2List.size());
//										walletResponse.setAssetsResList(assetsRes2List);
//										walletResponseList.add(walletResponse);
//
////										totalValue = totalValue + wallet.getLatestTotalBalance();
//										totalValue = totalValue + walletResponse.getTotalBalance();
//										dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
//										dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
//										totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
//										totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
//									}
//								}
//
//								allPortfolioValue = allPortfolioValue + totalValue;
//								allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain + dailyPercentageGain;
//								allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
//								allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain + totalPercentageGain;
//								allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;
//
//								if (i >= 1 || portfolioList.size() == 1) {
//									BeanUtils.copyProperties(portfolioList.get(i), portfolioResponsePayload);
////									portfolioResponsePayload.setPortfolioType(portfolioList.get(i).getPortfolioType());
//									portfolioResponsePayload
//											.setTotalPortfolioValue(totalValue != null ? totalValue : 0.0);
//									portfolioResponsePayload.setDailyPercentageGain(
//											dailyPercentageGain != null ? dailyPercentageGain : 0.0);
//									portfolioResponsePayload
//											.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
//									portfolioResponsePayload.setTotalPercentageGain(
//											totalPercentageGain != null ? totalPercentageGain : 0.0);
//									portfolioResponsePayload
//											.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
//									portfolioResponsePayload.setCurrency("USD");
//									portfolioResponsePayload.setWalletResponseList(walletResponseList);
//									portfolioResponsePayloadList.add(portfolioResponsePayload);
//								}
//								if (i == 0) {
//									walletResponseList2.addAll(walletResponseList);
//								}
//							}
//
//							if (portfolioList.size() > 1) {
//								PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//								BeanUtils.copyProperties(portfolioList.get(0), portfolioResponsePayload);
////								portfolioResponsePayload.setPortfolioType(portfolioList.get(0).getPortfolioType());
//								portfolioResponsePayload
//										.setTotalPortfolioValue(allPortfolioValue != null ? allPortfolioValue : 0.0);
//								portfolioResponsePayload.setDailyPercentageGain(
//										allPortfolioDailyPercentageGain != null ? allPortfolioDailyPercentageGain
//												: 0.0);
//								portfolioResponsePayload.setDailyPriceGain(
//										allPortfolioDailyPriceGain != null ? allPortfolioDailyPriceGain : 0.0);
//								portfolioResponsePayload.setTotalPercentageGain(
//										allPortfolioTotalPercentageGain != null ? allPortfolioTotalPercentageGain
//												: 0.0);
//								portfolioResponsePayload.setTotalPriceGain(
//										allPortfolioTotalPriceGain != null ? allPortfolioTotalPriceGain : 0.0);
//								portfolioResponsePayload.setCurrency("USD");
//								portfolioResponsePayload.setWalletResponseList(walletResponseList2);
//								portfolioResponsePayloadList.add(portfolioResponsePayload);
//							}
//							Collections.sort(portfolioResponsePayloadList,
//									Comparator.comparingLong(PortfolioResponsePayload::getId));
//
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
//							map.put(Constant.DATA, portfolioResponsePayloadList);
//							log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
//							map.put(Constant.DATA, portfolioResponsePayloadList);
//							log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
//						}
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
//						log.info(Constant.ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
//					log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//				map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
//				log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " ! status - {}", Constant.OK);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception : " + e.getMessage());
//		}
//		return map;
//	}

	// @ using portfolio id
//	@Override
//	public Map<String, Object> getPortfolio(Long userId, String type, Long portfolioId) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			if (type != null) {
//				if (userId != null && userId != 0) {
//					User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
//					if (user != null) {
//						List<PortfolioResponsePayload> portfolioResponsePayloadList = new ArrayList<>();
//						List<WalletResponse> walletResponseList2 = new ArrayList<>();
//						if (user.getPortfolioList() != null && !user.getPortfolioList().isEmpty()) {
//							Double allPortfolioValue = 0.0, allPortfolioDailyPriceGain = 0.0,
//									allPortfolioDailyPercentageGain = 0.0, allPortfolioTotalPriceGain = 0.0,
//									allPortfolioTotalPercentageGain = 0.0;
//							List<Portfolio> portfolioList = new ArrayList<>();
//
//							/* filter portFolio */
////							if (type.isBlank()) {
//								portfolioList.addAll(user.getPortfolioList());
////							} else if (!type.isBlank() && (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
////									|| type.equalsIgnoreCase(Constant.UNITED_STATES)
////									|| type.equalsIgnoreCase("crypto"))) {
//////								portfolioList = user.getPortfolioList().stream()
//////										.filter(obj -> obj.getPortfolioType().equals(portfolioType))
//////										.collect(Collectors.toList());
////							} else {
////								map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
////								map.put(Constant.MESSAGE, Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
////								log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE + " ! status - {}", Constant.OK);
////								return map;
////							}
//
//							//
//							/* creating portfolio list and calculate sum of all in main portfolio */
//							PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//							List<PortfolioResponse> portfolioResponseList = new ArrayList<>();
//							for (int i = 1; i < portfolioList.size(); i++) {
//								Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
//										totalPriceGain = 0.0, totalPercentageGain = 0.0;
//								PortfolioResponse portfolioResponse = new PortfolioResponse();
//								for (Wallet wallet : portfolioList.get(i).getWalletList()) {
//									if (wallet.getStatus().equals(Constant.ONE)) {
//
//										totalValue = totalValue + wallet.getLatestTotalBalance();
////										totalValue = totalValue + walletResponse.getTotalBalance();
//										dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
//										dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
//										totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
//										totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
//									}
//								}
//								allPortfolioValue = allPortfolioValue + totalValue;
//								allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain + dailyPercentageGain;
//								allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
//								allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain + totalPercentageGain;
//								allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;
//
//								BeanUtils.copyProperties(portfolioList.get(i), portfolioResponse);
//								portfolioResponse.setTotalPortfolioValue(totalValue != null ? totalValue : 0.0);
//								portfolioResponse.setDailyPercentageGain(
//										dailyPercentageGain != null ? dailyPercentageGain : 0.0);
//								portfolioResponse.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
//								portfolioResponse.setTotalPercentageGain(
//										totalPercentageGain != null ? totalPercentageGain : 0.0);
//								portfolioResponse.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
//								portfolioResponse.setCurrency("USD");
//								portfolioResponseList.add(portfolioResponse);
//							}
//
//							PortfolioResponse portfolioResponse = new PortfolioResponse();
//							BeanUtils.copyProperties(portfolioList.get(0), portfolioResponse);
//							portfolioResponse
//									.setTotalPortfolioValue(allPortfolioValue != null ? allPortfolioValue : 0.0);
//							portfolioResponse.setDailyPercentageGain(
//									allPortfolioDailyPercentageGain != null ? allPortfolioDailyPercentageGain : 0.0);
//							portfolioResponse.setDailyPriceGain(
//									allPortfolioDailyPriceGain != null ? allPortfolioDailyPriceGain : 0.0);
//							portfolioResponse.setTotalPercentageGain(
//									allPortfolioTotalPercentageGain != null ? allPortfolioTotalPercentageGain : 0.0);
//							portfolioResponse.setTotalPriceGain(
//									allPortfolioTotalPriceGain != null ? allPortfolioTotalPriceGain : 0.0);
//							portfolioResponse.setCurrency("USD");
//							portfolioResponseList.add(portfolioResponse);
//
//							portfolioResponsePayload.setPortfolioResponseList(portfolioResponseList);
//							Collections.sort(portfolioResponseList, Comparator.comparingLong(PortfolioResponse::getId));
//							//
//
//							//
//							/* creating wallet list */
//							WalletResponsePayload walletResponsePayload = new WalletResponsePayload();
//							List<WalletResponsePayload> walletResponsePayloadList = new ArrayList<WalletResponsePayload>();
//							List<WalletResponse> walletResponseList = new ArrayList<>();
//							List<WalletResponse> usaWalletResponseList = new ArrayList<>();
//							List<WalletResponse> saudiWalletResponseList = new ArrayList<>();
//							List<WalletResponse> cryptoWalletResponseList = new ArrayList<>();
//
//							//
//							if (portfolioId == 0) {// portfolio id 0 for main portfolio
//								for (int i = 1; i < portfolioList.size(); i++) {
//									for (Wallet wallet : portfolioList.get(i).getWalletList()) {
//										if (wallet.getStatus().equals(Constant.ONE)) {
//											Double totalBalance = 0.0;
//											List<AssetsRes2> assetsRes2List = new ArrayList<>();
//											WalletResponse walletResponse = new WalletResponse();
//											for (Assets assets : wallet.getAssetsList()) {
//												Stock stock = null;
//												Crypto crypto = null;
//												AssetsRes2 assetsRes2 = new AssetsRes2();
//												if (!type.isBlank()) {
//													if (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//															|| type.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
//														assetsRes2
//																.setLogo(assets.getStock().getStockProfile().getLogo());
//														assetsRes2.setExchange(assets.getStock().getExchange());
//														assetsRes2.setInstrumentId(assets.getStock().getId());
//
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//													}
//												} else {
//													if (assets.getInstrumentType()
//															.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//															|| assets.getInstrumentType()
//																	.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
//														assetsRes2
//																.setLogo(assets.getStock().getStockProfile().getLogo());
//														assetsRes2.setExchange(assets.getStock().getExchange());
//														assetsRes2.setInstrumentId(assets.getStock().getId());
//
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//													}
//												}
//											}
//											walletResponse.setWalletId(wallet.getId());
//											walletResponse.setWalletName(wallet.getWalletName());
//											walletResponse.setTotalBalance(totalBalance);
//											walletResponse.setNoOfAssets(assetsRes2List.size());
//											walletResponse.setAssetsResList(assetsRes2List);
//											if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//												saudiWalletResponseList.add(walletResponse);
//											} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
//												usaWalletResponseList.add(walletResponse);
//											} else if (wallet.getType().equalsIgnoreCase("crypto")) {
//												cryptoWalletResponseList.add(walletResponse);
//											}
//
//										}
//									}
//								}
//								walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
//								walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
//								walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
//								portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
//							} else {
//								boolean isFound = false;
//								Portfolio portfolio = new Portfolio();
//								for (int i = 1; i < portfolioList.size(); i++) {
//									if (portfolioId == portfolioList.get(i).getId()) {
//										isFound = true;
//										BeanUtils.copyProperties(portfolioList.get(i), portfolio);
//										break;
//									}
//								}
//								if (isFound == false) {
//									map.put(Constant.RESPONSE_CODE, Constant.OK);
//									map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
//									log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//									return map;
//								}
//
//								for (Wallet wallet : portfolio.getWalletList()) {
//									if (wallet.getStatus().equals(Constant.ONE)) {
//										Double totalBalance = 0.0;
//										List<AssetsRes2> assetsRes2List = new ArrayList<>();
//										WalletResponse walletResponse = new WalletResponse();
//										for (Assets assets : wallet.getAssetsList()) {
//											Stock stock = null;
//											Crypto crypto = null;
//											AssetsRes2 assetsRes2 = new AssetsRes2();
//											if (!type.isBlank()) {
//												if (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//														|| type.equalsIgnoreCase(Constant.UNITED_STATES)) {
//													BeanUtils.copyProperties(assets, assetsRes2);
//													assetsRes2.setClosePrice(
//															Double.valueOf(assets.getStock().getPrice()));
//													assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
//													assetsRes2.setExchange(assets.getStock().getExchange());
//													assetsRes2.setInstrumentId(assets.getStock().getId());
//
//													totalBalance = totalBalance + assets.getTotalPrice();
//													assetsRes2List.add(assetsRes2);
//												}
//											} else {
//												if (assets.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
//														|| assets.getInstrumentType()
//																.equalsIgnoreCase(Constant.UNITED_STATES)) {
//													BeanUtils.copyProperties(assets, assetsRes2);
//													assetsRes2.setClosePrice(
//															Double.valueOf(assets.getStock().getPrice()));
//													assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
//													assetsRes2.setExchange(assets.getStock().getExchange());
//													assetsRes2.setInstrumentId(assets.getStock().getId());
//
//													totalBalance = totalBalance + assets.getTotalPrice();
//													assetsRes2List.add(assetsRes2);
//												}
//											}
//										}
//										walletResponse.setWalletId(wallet.getId());
//										walletResponse.setWalletName(wallet.getWalletName());
//										walletResponse.setTotalBalance(totalBalance);
//										walletResponse.setNoOfAssets(assetsRes2List.size());
//										walletResponse.setAssetsResList(assetsRes2List);
//										if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//											saudiWalletResponseList.add(walletResponse);
//										} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
//											usaWalletResponseList.add(walletResponse);
//										} else if (wallet.getType().equalsIgnoreCase("crypto")) {
//											cryptoWalletResponseList.add(walletResponse);
//										}
//
//									}
//								}
////								}
//								walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
//								walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
//								walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
//								portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
//							}
//							//
//
////							for (int i = 1; i < portfolioList.size(); i++) {
////								for (Wallet wallet : portfolioList.get(i).getWalletList()) {
////									if (wallet.getStatus().equals(Constant.ONE)) {
////										Double totalBalance = 0.0;
////										List<AssetsRes2> assetsRes2List = new ArrayList<>();
////										WalletResponse walletResponse = new WalletResponse();
////										for (Assets assets : wallet.getAssetsList()) {
////											Stock stock = null;
////											Crypto crypto = null;
////											AssetsRes2 assetsRes2 = new AssetsRes2();
////											if (!type.isBlank()) {
////												if (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
////														|| type.equalsIgnoreCase(Constant.UNITED_STATES)) {
////													BeanUtils.copyProperties(assets, assetsRes2);
////													assetsRes2.setClosePrice(
////															Double.valueOf(assets.getStock().getPrice()));
////													assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
////													assetsRes2.setExchange(assets.getStock().getExchange());
////													assetsRes2.setInstrumentId(assets.getStock().getId());
////
////													totalBalance = totalBalance + assets.getTotalPrice();
////													assetsRes2List.add(assetsRes2);
////												}
////											} else {
////												if (assets.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
////														|| assets.getInstrumentType()
////																.equalsIgnoreCase(Constant.UNITED_STATES)) {
////													BeanUtils.copyProperties(assets, assetsRes2);
////													assetsRes2.setClosePrice(
////															Double.valueOf(assets.getStock().getPrice()));
////													assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
////													assetsRes2.setExchange(assets.getStock().getExchange());
////													assetsRes2.setInstrumentId(assets.getStock().getId());
////
////													totalBalance = totalBalance + assets.getTotalPrice();
////													assetsRes2List.add(assetsRes2);
////												}
////											}
////										}
////										walletResponse.setWalletId(wallet.getId());
////										walletResponse.setWalletName(wallet.getWalletName());
////										walletResponse.setTotalBalance(totalBalance);
////										walletResponse.setNoOfAssets(assetsRes2List.size());
////										walletResponse.setAssetsResList(assetsRes2List);
////										if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
////											saudiWalletResponseList.add(walletResponse);
////										} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
////											usaWalletResponseList.add(walletResponse);
////										} else if (wallet.getType().equalsIgnoreCase("crypto")) {
////											cryptoWalletResponseList.add(walletResponse);
////										}
////
////									}
////								}
////							}
////							walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
////							walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
////							walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
////							portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
//							//
//
////							for (int i = 0; i < portfolioList.size(); i++) {
////								Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
////										totalPriceGain = 0.0, totalPercentageGain = 0.0;
//////								PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
////								PortfolioResponse portfolioResponse = new PortfolioResponse();
////								List<WalletResponse> walletResponseList = new ArrayList<>();
////								for (Wallet wallet : portfolioList.get(i).getWalletList()) {
////									if (wallet.getStatus().equals(Constant.ONE)) {
////										Double totalBalance = 0.0;
////										List<AssetsRes2> assetsRes2List = new ArrayList<>();
////										WalletResponse walletResponse = new WalletResponse();
////										for (Assets assets : wallet.getAssetsList()) {
////											if (!assets.getSymbol().equalsIgnoreCase("USDT")) {
////												//
////												Stock stock = null;
////												Crypto crypto = null;
////												AssetsRes2 assetsRes2 = new AssetsRes2();
////												if (!portfolioType.isBlank()) {
////													if (portfolioType.equalsIgnoreCase(Constant.SAUDI_ARABIA)
////															|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)) {
////														BeanUtils.copyProperties(assets, assetsRes2);
////														assetsRes2.setClosePrice(
////																Double.valueOf(assets.getStock().getPrice()));
////														assetsRes2
////																.setLogo(assets.getStock().getStockProfile().getLogo());
////														assetsRes2.setExchange(assets.getStock().getExchange());
////														assetsRes2.setInstrumentId(assets.getStock().getId());
////
////														totalBalance = totalBalance + assets.getTotalPrice();
////														assetsRes2List.add(assetsRes2);
////
////													} else if (portfolioType.equalsIgnoreCase("crypto")) {
////														//
////														String price = "0";
////														String cryptoDetailsResponse = thirdPartyApiUtil
////																.getCryptoDetails(assets.getCrypto().getCryptoId());
////														if ((!cryptoDetailsResponse.isBlank())) {
////															log.info("data found ! status - {}", cryptoDetailsResponse);
////															/* using objectMapper */
////															ObjectMapper mapper = new ObjectMapper();
////															mapper.configure(
////																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
////																	false);
////															/* get CryptoMarketDetails */
////															Map<?, ?> mapResponse = mapper
////																	.readValue(cryptoDetailsResponse, Map.class);
////
////															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
////															data = mapper.convertValue(mapResponse,
////																	TsCryptoDetailsResponse.class);
////															log.info("data found ! status - {}", data);
////
////															/* save market data in third party apis */
////															for (TsPrice cryptoListPrice : data.getData()
////																	.getMarket_data().getPrice()) {
////																/* CryptoMarketDetails */
////																if (cryptoListPrice.getPrice_latest() == null) {
////																	price = "0";
////																} else {
////																	price = methodUtil.formattedValues(
////																			cryptoListPrice.getPrice_latest());
////																}
////															}
////														} else {
////															map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
////															map.put(Constant.MESSAGE,
////																	Constant.INTERNAL_SERVER_ERROR_MESSAGE);
////															log.info(Constant.INTERNAL_SERVER_ERROR_MESSAGE
////																	+ "! status - {}", Constant.SERVER_ERROR);
////															return map;
////														}
////														//
////
////														BeanUtils.copyProperties(assets, assetsRes2);
////														assetsRes2.setClosePrice(Double.valueOf(price));
////														assetsRes2.setLogo(assets.getCrypto().getLogo());
//////														assetsRes2.setExchange(crypto.getExchange());
////														assetsRes2.setInstrumentId(assets.getCrypto().getId());
////
////														totalBalance = totalBalance + assets.getTotalPrice();
////														assetsRes2List.add(assetsRes2);
////													}
////												} else {
////													if (assets.getInstrumentType()
////															.equalsIgnoreCase(Constant.SAUDI_ARABIA)
////															|| portfolioType.equalsIgnoreCase(Constant.UNITED_STATES)) {
////														BeanUtils.copyProperties(assets, assetsRes2);
////														assetsRes2.setClosePrice(
////																Double.valueOf(assets.getStock().getPrice()));
////														assetsRes2
////																.setLogo(assets.getStock().getStockProfile().getLogo());
////														assetsRes2.setExchange(assets.getStock().getExchange());
////														assetsRes2.setInstrumentId(assets.getStock().getId());
////
////														totalBalance = totalBalance + assets.getTotalPrice();
////														assetsRes2List.add(assetsRes2);
////
////													} else if (assets.getInstrumentType().equalsIgnoreCase("crypto")) {
////														//
////														String price = "0";
////														String cryptoDetailsResponse = thirdPartyApiUtil
////																.getCryptoDetails(assets.getCrypto().getCryptoId());
////														if ((!cryptoDetailsResponse.isBlank())) {
////															log.info("data found ! status - {}", cryptoDetailsResponse);
////															/* using objectMapper */
////															ObjectMapper mapper = new ObjectMapper();
////															mapper.configure(
////																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
////																	false);
////															/* get CryptoMarketDetails */
////															Map<?, ?> mapResponse = mapper
////																	.readValue(cryptoDetailsResponse, Map.class);
////
////															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
////															data = mapper.convertValue(mapResponse,
////																	TsCryptoDetailsResponse.class);
////															log.info("data found ! status - {}", data);
////
////															/* save market data in third party apis */
////															for (TsPrice cryptoListPrice : data.getData()
////																	.getMarket_data().getPrice()) {
////																/* CryptoMarketDetails */
////																if (cryptoListPrice.getPrice_latest() == null) {
////																	price = "0";
////																} else {
////																	price = methodUtil.formattedValues(
////																			cryptoListPrice.getPrice_latest());
////																}
////															}
////														} else {
////															map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
////															map.put(Constant.MESSAGE,
////																	Constant.INTERNAL_SERVER_ERROR_MESSAGE);
////															log.info(Constant.INTERNAL_SERVER_ERROR_MESSAGE
////																	+ "! status - {}", Constant.SERVER_ERROR);
////															return map;
////														}
////														//
////
////														BeanUtils.copyProperties(assets, assetsRes2);
////														assetsRes2.setClosePrice(Double.valueOf(price));
////														assetsRes2.setLogo(assets.getCrypto().getLogo());
//////														assetsRes2.setExchange(crypto.getExchange());
////														assetsRes2.setInstrumentId(assets.getCrypto().getId());
////
////														totalBalance = totalBalance + assets.getTotalPrice();
////														assetsRes2List.add(assetsRes2);
////
////													}
////												}
////												//
////
//////												AssetsRes2 assetsRes2 = new AssetsRes2();
//////												BeanUtils.copyProperties(assets, assetsRes2);
//////												assetsRes2.setClosePrice(Double.valueOf(assets.getStock().getPrice()));
//////												assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
//////												assetsRes2.setExchange(assets.getStock().getExchange());
//////												assetsRes2.setInstrumentId(assets.getStock().getId());
////
//////												totalBalance = totalBalance + assets.getTotalPrice();
//////												assetsRes2List.add(assetsRes2);
////											}
////										}
////										walletResponse.setWalletId(wallet.getId());
////										walletResponse.setWalletName(wallet.getWalletName());
////										walletResponse.setTotalBalance(totalBalance);
////										walletResponse.setNoOfAssets(assetsRes2List.size());
////										walletResponse.setAssetsResList(assetsRes2List);
////										walletResponseList.add(walletResponse);
////
//////										totalValue = totalValue + wallet.getLatestTotalBalance();
////										totalValue = totalValue + walletResponse.getTotalBalance();
////										dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
////										dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
////										totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
////										totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
////									}
////								}
////
////								allPortfolioValue = allPortfolioValue + totalValue;
////								allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain + dailyPercentageGain;
////								allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
////								allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain + totalPercentageGain;
////								allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;
////
////								if (i >= 1 || portfolioList.size() == 1) {
//////									BeanUtils.copyProperties(portfolioList.get(i), portfolioResponsePayload);
//////									portfolioResponsePayload
//////											.setTotalPortfolioValue(totalValue != null ? totalValue : 0.0);
//////									portfolioResponsePayload.setDailyPercentageGain(
//////											dailyPercentageGain != null ? dailyPercentageGain : 0.0);
//////									portfolioResponsePayload
//////											.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
//////									portfolioResponsePayload.setTotalPercentageGain(
//////											totalPercentageGain != null ? totalPercentageGain : 0.0);
//////									portfolioResponsePayload
//////											.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
//////									portfolioResponsePayload.setCurrency("USD");
//////									portfolioResponsePayload.setWalletResponseList(walletResponseList);
//////									portfolioResponsePayloadList.add(portfolioResponsePayload);
////								}
////								if (i == 0) {
////									walletResponseList2.addAll(walletResponseList);
////								}
////							}
//
////							if (portfolioList.size() > 1) {
////								PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
////								BeanUtils.copyProperties(portfolioList.get(0), portfolioResponsePayload);
//////								portfolioResponsePayload
//////										.setTotalPortfolioValue(allPortfolioValue != null ? allPortfolioValue : 0.0);
//////								portfolioResponsePayload.setDailyPercentageGain(
//////										allPortfolioDailyPercentageGain != null ? allPortfolioDailyPercentageGain
//////												: 0.0);
//////								portfolioResponsePayload.setDailyPriceGain(
//////										allPortfolioDailyPriceGain != null ? allPortfolioDailyPriceGain : 0.0);
//////								portfolioResponsePayload.setTotalPercentageGain(
//////										allPortfolioTotalPercentageGain != null ? allPortfolioTotalPercentageGain
//////												: 0.0);
//////								portfolioResponsePayload.setTotalPriceGain(
//////										allPortfolioTotalPriceGain != null ? allPortfolioTotalPriceGain : 0.0);
//////								portfolioResponsePayload.setCurrency("USD");
//////								portfolioResponsePayload.setWalletResponseList(walletResponseList2);
////								portfolioResponsePayloadList.add(portfolioResponsePayload);
////							}
////							Collections.sort(portfolioResponsePayloadList,
////									Comparator.comparingLong(PortfolioResponsePayload::getId));
//
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
////							map.put(Constant.DATA, portfolioResponsePayloadList);//portfolioResponsePayload
//							map.put(Constant.DATA, portfolioResponsePayload);
//							log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
//							map.put(Constant.DATA, portfolioResponsePayloadList);
//							log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
//						}
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
//						log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
//					log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//				map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
//				log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " ! status - {}", Constant.OK);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception : " + e.getMessage());
//		}
//		return map;
//	}

	@Override
	public Map<String, Object> getWalletList(Long userId, String type, Long walletId) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (type != null && type != "") {
				if (type.equalsIgnoreCase(Constant.SAUDI_ARABIA) || type.equalsIgnoreCase(Constant.UNITED_STATES)
						|| type.equalsIgnoreCase("crypto")) {
					if (userId != null && userId != 0) {
						User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
						if (user != null) {
							List<PortfolioResponsePayload> portfolioResponsePayloadList = new ArrayList<>();
							if (user.getPortfolioList() != null && !user.getPortfolioList().isEmpty()) {
								PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
								List<PortfolioResponse> portfolioResponseList = new ArrayList<>();
								String currency = "";
								for (int i = 1; i < user.getPortfolioList().size(); i++) {
									Wallet oldWallet = new Wallet();
									for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
										if (wallet.getType().equalsIgnoreCase(type)) {
											PortfolioResponse portfolioResponse = new PortfolioResponse();
											BeanUtils.copyProperties(wallet, oldWallet);
											if (wallet.getStatus().equals(Constant.ONE)) {
												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
													currency = "sar";
												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
														|| wallet.getType().equalsIgnoreCase("crypto")) {
													currency = "usd";
												}
												portfolioResponse.setId(oldWallet.getId());
												portfolioResponse.setName(oldWallet.getWalletName());
												portfolioResponse.setStatus(oldWallet.getStatus());
												portfolioResponse.setTotalValue(oldWallet.getLatestTotalBalance());
												portfolioResponse
														.setDailyPercentageGain(oldWallet.getDailyPercentageGain());
												portfolioResponse.setDailyPriceGain(oldWallet.getDailyPriceGain());
												portfolioResponse
														.setTotalPercentageGain(oldWallet.getTotalPercentageGain());
												portfolioResponse.setTotalPriceGain(oldWallet.getTotalPriceGain());
												portfolioResponse.setCurrency(currency);
												portfolioResponseList.add(portfolioResponse);
											}
										}
									}
								}

								//
								/* assets List */
//								for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
								boolean isFound = false;
								Wallet oldWallet = new Wallet();
								for (int i = 1; i < user.getPortfolioList().size(); i++) {
									for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
										if (walletId.equals(wallet.getId())) {
											isFound = true;
											BeanUtils.copyProperties(wallet, oldWallet);
											break;
										}
									}
								}
								if (isFound == false) {
									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.WALLET_ID_NOT_FOUND_MESSAGE);
									log.info(Constant.WALLET_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
									return map;
								}

								Double totalBalance = 0.0;
								List<AssetsRes2> assetsRes2List = new ArrayList<>();
//							WalletResponse walletResponse = new WalletResponse();
								if (!oldWallet.getAssetsList().isEmpty()) {
									for (Assets assets : oldWallet.getAssetsList()) {
										AssetsRes2 assetsRes2 = new AssetsRes2();
										if (type.equalsIgnoreCase(assets.getInstrumentType())) {
											if (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
													|| type.equalsIgnoreCase(Constant.UNITED_STATES)) {
												BeanUtils.copyProperties(assets, assetsRes2);
												assetsRes2.setCurrency(currency);
												assetsRes2.setClosePrice(Double.valueOf(assets.getStock().getPrice()));
												assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
												assetsRes2.setExchange(assets.getStock().getExchange());
												assetsRes2.setInstrumentId(assets.getStock().getId());

												totalBalance = totalBalance + assets.getTotalPrice();
												assetsRes2List.add(assetsRes2);
											} else if (type.equalsIgnoreCase(assets.getInstrumentType())
													&& type.equalsIgnoreCase("crypto")) {
												String price = "0";
												String cryptoDetailsResponse = "";
												if (assets.getCrypto() != null) {
													cryptoDetailsResponse = thirdPartyApiUtil
															.getCryptoDetails(assets.getCrypto().getCryptoId());
												}
												if (!cryptoDetailsResponse.isBlank()) {
													log.info("data found ! status - {}", cryptoDetailsResponse);
													/* using objectMapper */
													ObjectMapper mapper = new ObjectMapper();
													mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
															false);
													/* get CryptoMarketDetails */
													Map<?, ?> mapResponse = mapper.readValue(cryptoDetailsResponse,
															Map.class);

													TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
													data = mapper.convertValue(mapResponse,
															TsCryptoDetailsResponse.class);
													log.info("data found ! status - {}", data);

													/* save market data in third party apis */
													for (TsPrice cryptoListPrice : data.getData().getMarket_data()
															.getPrice()) {
														/* CryptoMarketDetails */
														if (cryptoListPrice.getPrice_latest() == null) {
															price = "0";
														} else {
															price = methodUtil
																	.formattedValues(cryptoListPrice.getPrice_latest());
														}
													}
												}
//												else {
//													map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//													map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//													log.info(Constant.INTERNAL_SERVER_ERROR_MESSAGE + "! status - {}",
//															Constant.SERVER_ERROR);
//													return map;
//												}

												BeanUtils.copyProperties(assets, assetsRes2);
												assetsRes2.setClosePrice(
														assets.getCrypto() != null ? Double.valueOf(price)
																: assets.getTotalPrice());
												assetsRes2.setLogo(
														assets.getCrypto() != null ? assets.getCrypto().getLogo() : "");
												assetsRes2.setInstrumentId(
														assets.getCrypto() != null ? assets.getCrypto().getId() : null);
												assetsRes2.setCurrency(currency);
//												assetsRes2.setClosePrice(Double.valueOf(price));
//												assetsRes2.setLogo(assets.getCrypto().getLogo());
												assetsRes2.setExchange(assets.getExchange());
//												assetsRes2.setInstrumentId(assets.getCrypto().getId());
												totalBalance = totalBalance + assets.getTotalPrice();
												assetsRes2List.add(assetsRes2);
											}
										}
									}

									portfolioResponsePayload.setAssetsResList(assetsRes2List);
									portfolioResponsePayload.setPortfolioResponseList(portfolioResponseList);
									Collections.sort(portfolioResponseList,
											Comparator.comparingLong(PortfolioResponse::getId));

									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
									map.put(Constant.DATA, portfolioResponsePayload);
									log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
								} else {
									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.WALLET_IS_EMPTY_MESSAGE);
									map.put(Constant.DATA, portfolioResponsePayloadList);
									log.info(Constant.WALLET_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
									return map;
								}
							} else {
								map.put(Constant.RESPONSE_CODE, Constant.OK);
								map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
								map.put(Constant.DATA, portfolioResponsePayloadList);
								log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
							}
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
							log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
						map.put(Constant.MESSAGE, Constant.USER_ID_CANT_NULL_MESSAGE);
						log.info(Constant.USER_ID_CANT_NULL_MESSAGE + " status - {}", Constant.SERVER_ERROR);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
					map.put(Constant.MESSAGE, Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
					log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE + " ! status - {}", Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				map.put(Constant.MESSAGE, Constant.WALLET_TYPE_CANT_NULL_MESSAGE);
				log.info(Constant.WALLET_TYPE_CANT_NULL_MESSAGE + " status - {}", Constant.SERVER_ERROR);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

//	@Override
//	public Map<String, Object> getPortfolio(Long userId, String type, Long portfolioId) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			if (type != null) {
//				if (userId != null && userId != 0) {
//					User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
//					if (user != null) {
////						List<PortfolioResponsePayload> portfolioResponsePayloadList = new ArrayList<>();
//						PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
//						if (user.getPortfolioList() != null && !user.getPortfolioList().isEmpty()) {
//							Double allPortfolioValue = 0.0, allPortfolioDailyPriceGain = 0.0,
//									allPortfolioDailyPercentageGain = 0.0, allPortfolioTotalPriceGain = 0.0,
//									allPortfolioTotalPercentageGain = 0.0;
//
//							/* creating portFolio list and calculate sum of all in main portfolio */
//							List<PortfolioResponse> portfolioResponseList = new ArrayList<>();
//							String currency = "";
//
//							//
//							/* check available cash */
//							KeyResponse keyResponse = new KeyResponse();
//							Double availableCash = 0.0;
//							List<ManageBalance> manageBalanceList = null;
//							if (portfolioId == 0) {
//								manageBalanceList = manageBalanceRepository.findByUser(userId);
//							} else if (portfolioId > 0) {
//								manageBalanceList = manageBalanceRepository.findByUserAndPortfolioId(userId,
//										portfolioId);
//							}
//							if (!manageBalanceList.isEmpty()) {
//								for (ManageBalance manageBalance : manageBalanceList) {
//									availableCash = availableCash + manageBalance.getAmount();
//								}
//								keyResponse.setKey("usd");
//								keyResponse.setValue(availableCash);
//								portfolioResponsePayload.setAvailableCash(keyResponse);
//							} else {
//								portfolioResponsePayload.setAvailableCash(null);
//							}
//							//
//
//							/* filter portFolio */
//							List<Portfolio> portfolioList = new ArrayList<>();
//							portfolioList.addAll(user.getPortfolioList());
//							for (int i = 1; i < portfolioList.size(); i++) {
//								Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
//										totalPriceGain = 0.0, totalPercentageGain = 0.0;
//								PortfolioResponse portfolioResponse = new PortfolioResponse();
//								for (Wallet wallet : portfolioList.get(i).getWalletList()) {
//									if (wallet.getStatus().equals(Constant.ONE)) {
//										totalValue = totalValue + wallet.getLatestTotalBalance();
//										dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
//										dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
//										totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
//										totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
//									}
//								}
//								allPortfolioValue = allPortfolioValue + totalValue;
//								allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain + dailyPercentageGain;
//								allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
//								allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain + totalPercentageGain;
//								allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;
//
//								BeanUtils.copyProperties(portfolioList.get(i), portfolioResponse);
//								portfolioResponse.setName(portfolioList.get(i).getPortfolioName());
//								if (portfolioId.equals(portfolioList.get(i).getId()))
//									portfolioResponse
//											.setTotalValue(totalValue != null ? totalValue + availableCash : 0.0);
//								else
//									portfolioResponse.setTotalValue(totalValue != null ? totalValue : 0.0);
//
//								portfolioResponse.setDailyPercentageGain(
//										dailyPercentageGain != null ? dailyPercentageGain : 0.0);
//								portfolioResponse.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
//								portfolioResponse.setTotalPercentageGain(
//										totalPercentageGain != null ? totalPercentageGain : 0.0);
//								portfolioResponse.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
//								portfolioResponse.setCurrency("USD");
//								portfolioResponseList.add(portfolioResponse);
//							}
//
//							PortfolioResponse portfolioResponse = new PortfolioResponse();
//							BeanUtils.copyProperties(portfolioList.get(0), portfolioResponse);
//							portfolioResponse.setName(portfolioList.get(0).getPortfolioName());
//							portfolioResponse
//									.setTotalValue(allPortfolioValue != null ? allPortfolioValue + availableCash : 0.0);
//							portfolioResponse.setDailyPercentageGain(
//									allPortfolioDailyPercentageGain != null ? allPortfolioDailyPercentageGain : 0.0);
//							portfolioResponse.setDailyPriceGain(
//									allPortfolioDailyPriceGain != null ? allPortfolioDailyPriceGain : 0.0);
//							portfolioResponse.setTotalPercentageGain(
//									allPortfolioTotalPercentageGain != null ? allPortfolioTotalPercentageGain : 0.0);
//							portfolioResponse.setTotalPriceGain(
//									allPortfolioTotalPriceGain != null ? allPortfolioTotalPriceGain : 0.0);
//							portfolioResponse.setCurrency("USD");
//							portfolioResponseList.add(portfolioResponse);
//
//							portfolioResponsePayload.setPortfolioResponseList(portfolioResponseList);
//							Collections.sort(portfolioResponseList, Comparator.comparingLong(PortfolioResponse::getId));
//
//							/* creating wallet list */
//							WalletResponsePayload walletResponsePayload = new WalletResponsePayload();
//							List<WalletResponsePayload> walletResponsePayloadList = new ArrayList<>();
//							List<WalletResponse> usaWalletResponseList = new ArrayList<>();
//							List<WalletResponse> saudiWalletResponseList = new ArrayList<>();
//							List<WalletResponse> cryptoWalletResponseList = new ArrayList<>();
////							Double availableCash = 0.0;
//							if (portfolioId == 0) {
//								if (!type.isBlank()) {
//									/* filter for KSA/USA/CRYPTO */
//									for (int i = 1; i < portfolioList.size(); i++) {
//										for (Wallet wallet : portfolioList.get(i).getWalletList()) {
//											if (wallet.getStatus().equals(Constant.ONE)) {
//												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//													currency = "sar";
//												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
//														|| wallet.getType().equalsIgnoreCase("crypto")) {
//													currency = "usd";
//												}
//												if (type.equalsIgnoreCase(wallet.getType())) {
//													Double totalBalance = 0.0;
//													List<AssetsRes2> assetsRes2List = new ArrayList<>();
//													WalletResponse walletResponse = new WalletResponse();
//													for (Assets assets : wallet.getAssetsList()) {
//														AssetsRes2 assetsRes2 = new AssetsRes2();
//														if (type.equalsIgnoreCase(assets.getInstrumentType())
//																&& (type.equalsIgnoreCase(Constant.SAUDI_ARABIA) || type
//																		.equalsIgnoreCase(Constant.UNITED_STATES))) {
//															BeanUtils.copyProperties(assets, assetsRes2);
//															assetsRes2.setClosePrice(
//																	Double.valueOf(assets.getStock().getPrice()));
//															assetsRes2.setLogo(
//																	assets.getStock().getStockProfile().getLogo());
//															assetsRes2.setExchange(assets.getStock().getExchange());
//															assetsRes2.setInstrumentId(assets.getStock().getId());
//															assetsRes2.setPortfolioId(portfolioList.get(i).getId());
//															assetsRes2.setCurrency(currency);
//															totalBalance = totalBalance + assets.getTotalPrice();
//															assetsRes2List.add(assetsRes2);
//														} else if (type.equalsIgnoreCase(assets.getInstrumentType())
//																&& type.equalsIgnoreCase("crypto")) {
//															String price = "0";
//															String cryptoDetailsResponse = "";
//															if (assets.getCrypto() != null) {
//																cryptoDetailsResponse = thirdPartyApiUtil
//																		.getCryptoDetails(
//																				assets.getCrypto().getCryptoId());
//															}
//															if (!cryptoDetailsResponse.isBlank()) {
//																log.info("data found ! status - {}",
//																		cryptoDetailsResponse);
//																/* using objectMapper */
//																ObjectMapper mapper = new ObjectMapper();
//																mapper.configure(
//																		DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//																		false);
//																/* get CryptoMarketDetails */
//																Map<?, ?> mapResponse = mapper
//																		.readValue(cryptoDetailsResponse, Map.class);
//
//																TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
//																data = mapper.convertValue(mapResponse,
//																		TsCryptoDetailsResponse.class);
//																log.info("data found ! status - {}", data);
//
//																/* save market data in third party apis */
//																for (TsPrice cryptoListPrice : data.getData()
//																		.getMarket_data().getPrice()) {
//																	/* CryptoMarketDetails */
//																	if (cryptoListPrice.getPrice_latest() == null) {
//																		price = "0";
//																	} else {
//																		price = methodUtil.formattedValues(
//																				cryptoListPrice.getPrice_latest());
//																	}
//																}
//															}
//															BeanUtils.copyProperties(assets, assetsRes2);
//															assetsRes2.setExchange(assets.getExchange());
//															assetsRes2.setClosePrice(
//																	assets.getCrypto() != null ? Double.valueOf(price)
//																			: assets.getTotalPrice());
//															assetsRes2.setLogo(assets.getCrypto() != null
//																	? assets.getCrypto().getLogo()
//																	: "");
//															assetsRes2.setInstrumentId(assets.getCrypto() != null
//																	? assets.getCrypto().getId()
//																	: null);
//															assetsRes2.setPortfolioId(portfolioList.get(i).getId());
//															assetsRes2.setCurrency(currency);
//															totalBalance = totalBalance + assets.getTotalPrice();
//															assetsRes2List.add(assetsRes2);
//														} else {
//															map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//															map.put(Constant.MESSAGE,
//																	Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
//															log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE
//																	+ " ! status - {}", Constant.OK);
//															return map;
//														}
//													}
//													walletResponse.setWalletId(wallet.getId());
//													walletResponse.setWalletName(wallet.getWalletName());
//													walletResponse.setTotalBalance(totalBalance);
//													walletResponse.setNoOfAssets(assetsRes2List.size());
//													walletResponse.setAssetsResList(assetsRes2List);
//													if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//														saudiWalletResponseList.add(walletResponse);
//													} else if (wallet.getType()
//															.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														usaWalletResponseList.add(walletResponse);
//													} else if (wallet.getType().equalsIgnoreCase("crypto")) {
//														cryptoWalletResponseList.add(walletResponse);
//													}
//												}
//											}
//										}
//									}
//									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
//									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
//									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
//									walletResponsePayloadList.add(walletResponsePayload);
//									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
//								} else if (type.isBlank()) {
//									/* filter for All */
//									for (int i = 1; i < portfolioList.size(); i++) {
//										for (Wallet wallet : portfolioList.get(i).getWalletList()) {
//											if (wallet.getStatus().equals(Constant.ONE)) {
//												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//													currency = "sar";
//												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
//														|| wallet.getType().equalsIgnoreCase("crypto")) {
//													currency = "usd";
//												}
//												Double totalBalance = 0.0;
//												List<AssetsRes2> assetsRes2List = new ArrayList<>();
//												WalletResponse walletResponse = new WalletResponse();
//												for (Assets assets : wallet.getAssetsList()) {
//													AssetsRes2 assetsRes2 = new AssetsRes2();
//													if (assets.getInstrumentType()
//															.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//															|| assets.getInstrumentType()
//																	.equalsIgnoreCase(Constant.UNITED_STATES)) {
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
//														assetsRes2
//																.setLogo(assets.getStock().getStockProfile().getLogo());
//														assetsRes2.setExchange(assets.getStock().getExchange());
//														assetsRes2.setInstrumentId(assets.getStock().getId());
//														assetsRes2.setPortfolioId(portfolioList.get(i).getId());
//														assetsRes2.setCurrency(currency);
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//													} else if (assets.getInstrumentType().equalsIgnoreCase("crypto")) {
//														String price = "0";
//														String cryptoDetailsResponse = "";
//														if (assets.getCrypto() != null) {
//															cryptoDetailsResponse = thirdPartyApiUtil
//																	.getCryptoDetails(assets.getCrypto().getCryptoId());
//														}
//														if (!cryptoDetailsResponse.isBlank()) {
//															log.info("data found ! status - {}", cryptoDetailsResponse);
//															/* using objectMapper */
//															ObjectMapper mapper = new ObjectMapper();
//															mapper.configure(
//																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//																	false);
//															/* get CryptoMarketDetails */
//															Map<?, ?> mapResponse = mapper
//																	.readValue(cryptoDetailsResponse, Map.class);
//
//															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
//															data = mapper.convertValue(mapResponse,
//																	TsCryptoDetailsResponse.class);
//															log.info("data found ! status - {}", data);
//
//															/* save market data in third party apis */
//															for (TsPrice cryptoListPrice : data.getData()
//																	.getMarket_data().getPrice()) {
//																/* CryptoMarketDetails */
//																if (cryptoListPrice.getPrice_latest() == null) {
//																	price = "0";
//																} else {
//																	price = methodUtil.formattedValues(
//																			cryptoListPrice.getPrice_latest());
//																}
//															}
//														}
//
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																assets.getCrypto() != null ? Double.valueOf(price)
//																		: assets.getTotalPrice());
//														assetsRes2.setExchange(assets.getExchange());
//														assetsRes2.setLogo(assets.getCrypto() != null
//																? assets.getCrypto().getLogo()
//																: "");
//														assetsRes2.setInstrumentId(
//																assets.getCrypto() != null ? assets.getCrypto().getId()
//																		: null);
//														assetsRes2.setPortfolioId(portfolioList.get(i).getId());
//														assetsRes2.setCurrency(currency);
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//													} else {
//														map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//														map.put(Constant.MESSAGE,
//																Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
//														log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE
//																+ " ! status - {}", Constant.OK);
//														return map;
//													}
//												}
//												walletResponse.setWalletId(wallet.getId());
//												walletResponse.setWalletName(wallet.getWalletName());
//												walletResponse.setTotalBalance(totalBalance);
//												walletResponse.setNoOfAssets(assetsRes2List.size());
//												walletResponse.setAssetsResList(assetsRes2List);
//												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//													saudiWalletResponseList.add(walletResponse);
//												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
//													usaWalletResponseList.add(walletResponse);
//												} else if (wallet.getType().equalsIgnoreCase("crypto")) {
//													cryptoWalletResponseList.add(walletResponse);
//												}
//											}
//										}
//									}
//									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
//									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
//									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
//									walletResponsePayloadList.add(walletResponsePayload);
//									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
//								}
//							} else if (portfolioId > 0) {
//								boolean isFound = false;
//								Portfolio portfolio = new Portfolio();
//								for (int i = 1; i < portfolioList.size(); i++) {
//									if (portfolioId.equals(portfolioList.get(i).getId())) {
//										isFound = true;
//										BeanUtils.copyProperties(portfolioList.get(i), portfolio);
//										break;
//									}
//								}
//								if (isFound == false) {
//									map.put(Constant.RESPONSE_CODE, Constant.OK);
//									map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
//									log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//									return map;
//								}
//								if (!type.isBlank()) {
//									for (Wallet wallet : portfolio.getWalletList()) {
//										if (wallet.getStatus().equals(Constant.ONE)) {
//											if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//												currency = "sar";
//											} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
//													|| wallet.getType().equalsIgnoreCase("crypto")) {
//												currency = "usd";
//											}
//											if (type.equalsIgnoreCase(wallet.getType())) {
//												Double totalBalance = 0.0;
//												List<AssetsRes2> assetsRes2List = new ArrayList<>();
//												WalletResponse walletResponse = new WalletResponse();
//												for (Assets assets : wallet.getAssetsList()) {
//													AssetsRes2 assetsRes2 = new AssetsRes2();
//													if (type.equalsIgnoreCase(assets.getInstrumentType())
//															&& (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
//																	|| type.equalsIgnoreCase(Constant.UNITED_STATES))) {
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
//														assetsRes2
//																.setLogo(assets.getStock().getStockProfile().getLogo());
//														assetsRes2.setExchange(assets.getStock().getExchange());
//														assetsRes2.setInstrumentId(assets.getStock().getId());
//														assetsRes2.setPortfolioId(portfolio.getId());
//														assetsRes2.setCurrency(currency);
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//													} else if (type.equalsIgnoreCase(assets.getInstrumentType())
//															&& type.equalsIgnoreCase("crypto")) {
//														String price = "0";
//														String cryptoDetailsResponse = "";
//														if (assets.getCrypto() != null) {
//															cryptoDetailsResponse = thirdPartyApiUtil
//																	.getCryptoDetails(assets.getCrypto().getCryptoId());
//														}
//														if (!cryptoDetailsResponse.isBlank()) {
//															log.info("data found ! status - {}", cryptoDetailsResponse);
//															/* using objectMapper */
//															ObjectMapper mapper = new ObjectMapper();
//															mapper.configure(
//																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//																	false);
//															/* get CryptoMarketDetails */
//															Map<?, ?> mapResponse = mapper
//																	.readValue(cryptoDetailsResponse, Map.class);
//
//															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
//															data = mapper.convertValue(mapResponse,
//																	TsCryptoDetailsResponse.class);
//															log.info("data found ! status - {}", data);
//
//															/* save market data in third party apis */
//															for (TsPrice cryptoListPrice : data.getData()
//																	.getMarket_data().getPrice()) {
//																/* CryptoMarketDetails */
//																if (cryptoListPrice.getPrice_latest() == null) {
//																	price = "0";
//																} else {
//																	price = methodUtil.formattedValues(
//																			cryptoListPrice.getPrice_latest());
//																}
//															}
//														}
//														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																assets.getCrypto() != null ? Double.valueOf(price)
//																		: assets.getTotalPrice());
//														assetsRes2.setExchange(assets.getExchange());
//														assetsRes2.setLogo(assets.getCrypto() != null
//																? assets.getCrypto().getLogo()
//																: "");
//														assetsRes2.setInstrumentId(
//																assets.getCrypto() != null ? assets.getCrypto().getId()
//																		: null);
//														assetsRes2.setPortfolioId(portfolio.getId());
//														assetsRes2.setCurrency(currency);
//														totalBalance = totalBalance + assets.getTotalPrice();
//														assetsRes2List.add(assetsRes2);
//													} else {
//														map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//														map.put(Constant.MESSAGE,
//																Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
//														log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE
//																+ " ! status - {}", Constant.OK);
//														return map;
//													}
//												}
//												walletResponse.setWalletId(wallet.getId());
//												walletResponse.setWalletName(wallet.getWalletName());
//												walletResponse.setTotalBalance(totalBalance);
//												walletResponse.setNoOfAssets(assetsRes2List.size());
//												walletResponse.setAssetsResList(assetsRes2List);
//												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//													saudiWalletResponseList.add(walletResponse);
//												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
//													usaWalletResponseList.add(walletResponse);
//												} else if (wallet.getType().equalsIgnoreCase("crypto")) {
//													cryptoWalletResponseList.add(walletResponse);
//												}
//											}
//										}
//									}
//									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
//									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
//									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
//									walletResponsePayloadList.add(walletResponsePayload);
//									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
//								} else if (type.isBlank()) {
//									for (Wallet wallet : portfolio.getWalletList()) {
//										if (wallet.getStatus().equals(Constant.ONE)) {
//											if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//												currency = "sar";
//											} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
//													|| wallet.getType().equalsIgnoreCase("crypto")) {
//												currency = "usd";
//											}
//											Double totalBalance = 0.0;
//											List<AssetsRes2> assetsRes2List = new ArrayList<>();
//											WalletResponse walletResponse = new WalletResponse();
//											for (Assets assets : wallet.getAssetsList()) {
//												AssetsRes2 assetsRes2 = new AssetsRes2();
//												if (assets.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
//														|| assets.getInstrumentType()
//																.equalsIgnoreCase(Constant.UNITED_STATES)) {
//													BeanUtils.copyProperties(assets, assetsRes2);
//													assetsRes2.setClosePrice(
//															Double.valueOf(assets.getStock().getPrice()));
//													assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
//													assetsRes2.setExchange(assets.getStock().getExchange());
//													assetsRes2.setInstrumentId(assets.getStock().getId());
//													assetsRes2.setPortfolioId(portfolio.getId());
//													assetsRes2.setCurrency(currency);
//													totalBalance = totalBalance + assets.getTotalPrice();
//													assetsRes2List.add(assetsRes2);
//												} else if (assets.getInstrumentType().equalsIgnoreCase("crypto")) {
//													String price = "0";
//													String cryptoDetailsResponse = "";
//													if (assets.getCrypto() != null) {
//														cryptoDetailsResponse = thirdPartyApiUtil
//																.getCryptoDetails(assets.getCrypto().getCryptoId());
//													}
//													if (!cryptoDetailsResponse.isBlank()) {
//														log.info("data found ! status - {}", cryptoDetailsResponse);
//														/* using objectMapper */
//														ObjectMapper mapper = new ObjectMapper();
//														mapper.configure(
//																DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//																false);
//														/* get CryptoMarketDetails */
//														Map<?, ?> mapResponse = mapper.readValue(cryptoDetailsResponse,
//																Map.class);
//
//														TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
//														data = mapper.convertValue(mapResponse,
//																TsCryptoDetailsResponse.class);
//														log.info("data found ! status - {}", data);
//
//														/* save market data in third party apis */
//														for (TsPrice cryptoListPrice : data.getData().getMarket_data()
//																.getPrice()) {
//															/* CryptoMarketDetails */
//															if (cryptoListPrice.getPrice_latest() == null) {
//																price = "0";
//															} else {
//																price = methodUtil.formattedValues(
//																		cryptoListPrice.getPrice_latest());
//															}
//														}
//													}
//													BeanUtils.copyProperties(assets, assetsRes2);
//													assetsRes2.setClosePrice(
//															assets.getCrypto() != null ? Double.valueOf(price)
//																	: assets.getTotalPrice());
//													assetsRes2.setExchange(assets.getExchange());
//													assetsRes2.setLogo(
//															assets.getCrypto() != null ? assets.getCrypto().getLogo()
//																	: "");
//													assetsRes2.setInstrumentId(
//															assets.getCrypto() != null ? assets.getCrypto().getId()
//																	: null);
//													assetsRes2.setPortfolioId(portfolio.getId());
//													assetsRes2.setCurrency(currency);
//													totalBalance = totalBalance + assets.getTotalPrice();
//													assetsRes2List.add(assetsRes2);
//												}
//											}
//											walletResponse.setWalletId(wallet.getId());
//											walletResponse.setWalletName(wallet.getWalletName());
//											walletResponse.setTotalBalance(totalBalance);
//											walletResponse.setNoOfAssets(assetsRes2List.size());
//											walletResponse.setAssetsResList(assetsRes2List);
//											if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
//												saudiWalletResponseList.add(walletResponse);
//											} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
//												usaWalletResponseList.add(walletResponse);
//											} else if (wallet.getType().equalsIgnoreCase("crypto")) {
//												cryptoWalletResponseList.add(walletResponse);
//											}
//										}
//									}
//									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
//									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
//									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
//									walletResponsePayloadList.add(walletResponsePayload);
//									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
//									portfolioResponsePayload
//											.setIsAvailableCashEnabled(portfolio.getIsAvailableCashEnabled());
//								}
//							}
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
//							map.put(Constant.DATA, portfolioResponsePayload);
//							log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
//							map.put(Constant.DATA, portfolioResponsePayload);
//							log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
//						}
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
//						log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
//					log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
//				map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
//				log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " ! status - {}", Constant.OK);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception : " + e.getMessage());
//		}
//		return map;
//	}

	@Override
	public Map<String, Object> getPortfolio(Long userId, String type, Long portfolioId) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (type != null) {
				if (userId != null && userId != 0) {
					User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
					if (user != null) {
						PortfolioResponsePayload portfolioResponsePayload = new PortfolioResponsePayload();
						if (user.getPortfolioList() != null && !user.getPortfolioList().isEmpty()) {
							Double allPortfolioValue = 0.0, allPortfolioDailyPriceGain = 0.0,
									allPortfolioDailyPercentageGain = 0.0, allPortfolioTotalPriceGain = 0.0,
									allPortfolioTotalPercentageGain = 0.0;

							/* check available cash */
							KeyResponse keyResponse = new KeyResponse();
							Double availableCash = 0.0;
							List<Cash> cashList = null;
//							if (portfolioId == 0) {
//								cashList = manageBalanceRepository.findByUser(userId);
//							} else if (portfolioId > 0) {
//								cashList = manageBalanceRepository.findByUserAndPortfolioId(userId,
//										portfolioId);
//							}
							if (!type.isBlank()) {
								cashList = manageBalanceRepository.findByUserIdAndType(userId, type);
							} else {
								cashList = manageBalanceRepository.findByUser(userId);
							}
//							cashList = manageBalanceRepository.findByUser(userId);
							if (!cashList.isEmpty()) {
								for (Cash cash : cashList) {
									availableCash = availableCash + cash.getAmount();
								}
								keyResponse.setKey("usd");
								keyResponse.setValue(availableCash);
								portfolioResponsePayload.setAvailableCash(keyResponse);
							} else {
								portfolioResponsePayload.setAvailableCash(null);
							}

							/* creating portFolio list and calculate sum of all in main portfolio */
							List<PortfolioResponse> portfolioResponseList = new ArrayList<>();
							String currency = "";
							//
							List<Portfolio> portfolioList = new ArrayList<>();
							portfolioList.addAll(user.getPortfolioList());
							if (portfolioId == 0) {
								/* get all portFolio sum */
								for (int i = 1; i < portfolioList.size(); i++) {
									Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
											totalPriceGain = 0.0, totalPercentageGain = 0.0;
									PortfolioResponse portfolioResponse = new PortfolioResponse();
									for (Wallet wallet : portfolioList.get(i).getWalletList()) {
										if (wallet.getStatus().equals(Constant.ONE)) {
											if (type.equalsIgnoreCase(wallet.getType())) {
												totalValue = totalValue + wallet.getLatestTotalBalance();
												dailyPercentageGain = dailyPercentageGain
														+ wallet.getDailyPercentageGain();
												dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
												totalPercentageGain = totalPercentageGain
														+ wallet.getTotalPercentageGain();
												totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
											} else if (type.isBlank()) {
												totalValue = totalValue + wallet.getLatestTotalBalance();
												dailyPercentageGain = dailyPercentageGain
														+ wallet.getDailyPercentageGain();
												dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
												totalPercentageGain = totalPercentageGain
														+ wallet.getTotalPercentageGain();
												totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
											}
										}
									}
									allPortfolioValue = allPortfolioValue + totalValue;
									allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain
											+ dailyPercentageGain;
									allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
									allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain
											+ totalPercentageGain;
									allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;

									BeanUtils.copyProperties(portfolioList.get(i), portfolioResponse);
									portfolioResponse.setName(portfolioList.get(i).getPortfolioName());
									if (portfolioId.equals(portfolioList.get(i).getId()))
										portfolioResponse
												.setTotalValue(totalValue != null ? totalValue + availableCash : 0.0);
									else
										portfolioResponse
												.setTotalValue(totalValue != null ? totalValue + availableCash : 0.0);

									portfolioResponse.setDailyPercentageGain(
											dailyPercentageGain != null ? dailyPercentageGain : 0.0);
									portfolioResponse.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
									portfolioResponse.setTotalPercentageGain(
											totalPercentageGain != null ? totalPercentageGain : 0.0);
									portfolioResponse.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
									portfolioResponse.setCurrency("USD");
									portfolioResponseList.add(portfolioResponse);
								}
							} else if (portfolioId > 0) {
								/* get specific portFolio */
								boolean isFound = false;
								Portfolio portfolio = new Portfolio();
								for (int i = 1; i < portfolioList.size(); i++) {
									if (portfolioId.equals(portfolioList.get(i).getId())) {
										isFound = true;
										BeanUtils.copyProperties(portfolioList.get(i), portfolio);
										break;
									}
								}
								if (isFound == false) {
									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
									log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
									return map;
								}
								Double totalValue = 0.0, dailyPriceGain = 0.0, dailyPercentageGain = 0.0,
										totalPriceGain = 0.0, totalPercentageGain = 0.0;
								PortfolioResponse portfolioResponse = new PortfolioResponse();
								for (Wallet wallet : portfolio.getWalletList()) {
									if (wallet.getStatus().equals(Constant.ONE)) {
										if (type.equalsIgnoreCase(wallet.getType())) {
											totalValue = totalValue + wallet.getLatestTotalBalance();
											dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
											dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
											totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
											totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
										} else if (type.isBlank()) {
											totalValue = totalValue + wallet.getLatestTotalBalance();
											dailyPercentageGain = dailyPercentageGain + wallet.getDailyPercentageGain();
											dailyPriceGain = dailyPriceGain + wallet.getDailyPriceGain();
											totalPercentageGain = totalPercentageGain + wallet.getTotalPercentageGain();
											totalPriceGain = totalPriceGain + wallet.getTotalPriceGain();
										}
									}
								}
								allPortfolioValue = allPortfolioValue + totalValue;
								allPortfolioDailyPercentageGain = allPortfolioDailyPercentageGain + dailyPercentageGain;
								allPortfolioDailyPriceGain = allPortfolioDailyPriceGain + dailyPriceGain;
								allPortfolioTotalPercentageGain = allPortfolioTotalPercentageGain + totalPercentageGain;
								allPortfolioTotalPriceGain = allPortfolioTotalPriceGain + totalPriceGain;

								BeanUtils.copyProperties(portfolio, portfolioResponse);
								portfolioResponse.setName(portfolio.getPortfolioName());
								if (portfolioId.equals(portfolio.getId()))
									portfolioResponse
											.setTotalValue(totalValue != null ? totalValue + availableCash : 0.0);
								else
									portfolioResponse.setTotalValue(totalValue != null ? totalValue : 0.0);

								portfolioResponse.setDailyPercentageGain(
										dailyPercentageGain != null ? dailyPercentageGain : 0.0);
								portfolioResponse.setDailyPriceGain(dailyPriceGain != null ? dailyPriceGain : 0.0);
								portfolioResponse.setTotalPercentageGain(
										totalPercentageGain != null ? totalPercentageGain : 0.0);
								portfolioResponse.setTotalPriceGain(totalPriceGain != null ? totalPriceGain : 0.0);
								portfolioResponse.setCurrency("USD");
								portfolioResponseList.add(portfolioResponse);
							}
							//

							PortfolioResponse portfolioResponse = new PortfolioResponse();
							BeanUtils.copyProperties(portfolioList.get(0), portfolioResponse);
							portfolioResponse.setName(portfolioList.get(0).getPortfolioName());
							portfolioResponse
									.setTotalValue(allPortfolioValue != null ? allPortfolioValue + availableCash : 0.0);
							portfolioResponse.setDailyPercentageGain(
									allPortfolioDailyPercentageGain != null ? allPortfolioDailyPercentageGain : 0.0);
							portfolioResponse.setDailyPriceGain(
									allPortfolioDailyPriceGain != null ? allPortfolioDailyPriceGain : 0.0);
							portfolioResponse.setTotalPercentageGain(
									allPortfolioTotalPercentageGain != null ? allPortfolioTotalPercentageGain : 0.0);
							portfolioResponse.setTotalPriceGain(
									allPortfolioTotalPriceGain != null ? allPortfolioTotalPriceGain : 0.0);
							portfolioResponse.setCurrency("USD");
							portfolioResponseList.add(portfolioResponse);

							portfolioResponsePayload.setPortfolioResponseList(portfolioResponseList);
							Collections.sort(portfolioResponseList, Comparator.comparingLong(PortfolioResponse::getId));

							/* creating wallet list */
							WalletResponsePayload walletResponsePayload = new WalletResponsePayload();
							List<WalletResponsePayload> walletResponsePayloadList = new ArrayList<>();
							List<WalletResponse> usaWalletResponseList = new ArrayList<>();
							List<WalletResponse> saudiWalletResponseList = new ArrayList<>();
							List<WalletResponse> cryptoWalletResponseList = new ArrayList<>();
//							Double availableCash = 0.0;
							if (portfolioId == 0) {
								if (!type.isBlank()) {
									/* filter for KSA/USA/CRYPTO */
									for (int i = 1; i < portfolioList.size(); i++) {
										for (Wallet wallet : portfolioList.get(i).getWalletList()) {
											if (wallet.getStatus().equals(Constant.ONE)) {
												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
													currency = "sar";
												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
														|| wallet.getType().equalsIgnoreCase("crypto")) {
													currency = "usd";
												}
												if (type.equalsIgnoreCase(wallet.getType())) {
													Double totalBalance = 0.0;
													List<AssetsRes2> assetsRes2List = new ArrayList<>();
													WalletResponse walletResponse = new WalletResponse();
													for (Assets assets : wallet.getAssetsList()) {
														AssetsRes2 assetsRes2 = new AssetsRes2();
														if (type.equalsIgnoreCase(assets.getInstrumentType())
																&& (type.equalsIgnoreCase(Constant.SAUDI_ARABIA) || type
																		.equalsIgnoreCase(Constant.UNITED_STATES))) {
															BeanUtils.copyProperties(assets, assetsRes2);
//															assetsRes2.setClosePrice(
//																	Double.valueOf(assets.getStock().getPrice()));
															assetsRes2.setClosePrice(Double.valueOf(
																	assets.getTotalPrice() / assets.getQuantity()));
															assetsRes2.setLogo(
																	assets.getStock().getStockProfile().getLogo());
															assetsRes2.setExchange(assets.getStock().getExchange());
															assetsRes2.setInstrumentId(assets.getStock().getId());
															assetsRes2.setPortfolioId(portfolioList.get(i).getId());
															assetsRes2.setCurrency(currency);
															totalBalance = totalBalance + assets.getTotalPrice();
															assetsRes2List.add(assetsRes2);
														} else if (type.equalsIgnoreCase(assets.getInstrumentType())
																&& type.equalsIgnoreCase("crypto")) {
															String price = "0";
															String cryptoDetailsResponse = "";
															if (assets.getCrypto() != null) {
																cryptoDetailsResponse = thirdPartyApiUtil
																		.getCryptoDetails(
																				assets.getCrypto().getCryptoId());
															}
															if (!cryptoDetailsResponse.isBlank()) {
																log.info("data found ! status - {}",
																		cryptoDetailsResponse);
																/* using objectMapper */
																ObjectMapper mapper = new ObjectMapper();
																mapper.configure(
																		DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
																		false);
																/* get CryptoMarketDetails */
																Map<?, ?> mapResponse = mapper
																		.readValue(cryptoDetailsResponse, Map.class);

																TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
																data = mapper.convertValue(mapResponse,
																		TsCryptoDetailsResponse.class);
																log.info("data found ! status - {}", data);

																/* save market data in third party apis */
																for (TsPrice cryptoListPrice : data.getData()
																		.getMarket_data().getPrice()) {
																	/* CryptoMarketDetails */
																	if (cryptoListPrice.getPrice_latest() == null) {
																		price = "0";
																	} else {
																		price = methodUtil.formattedValues(
																				cryptoListPrice.getPrice_latest());
																	}
																}
															}
															BeanUtils.copyProperties(assets, assetsRes2);
															assetsRes2.setExchange(assets.getExchange());
//															assetsRes2.setClosePrice(
//																	assets.getCrypto() != null ? Double.valueOf(price)
//																			: assets.getTotalPrice());
															assetsRes2.setClosePrice(Double.valueOf(
																	assets.getTotalPrice() / assets.getQuantity()));
															assetsRes2.setLogo(assets.getCrypto() != null
																	? assets.getCrypto().getLogo()
																	: "");
															assetsRes2.setInstrumentId(assets.getCrypto() != null
																	? assets.getCrypto().getId()
																	: null);
															assetsRes2.setPortfolioId(portfolioList.get(i).getId());
															assetsRes2.setCurrency(currency);
															totalBalance = totalBalance + assets.getTotalPrice();
															assetsRes2List.add(assetsRes2);
														} else {
															map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
															map.put(Constant.MESSAGE,
																	Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
															log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE
																	+ " ! status - {}", Constant.OK);
															return map;
														}
													}
													walletResponse.setWalletId(wallet.getId());
													walletResponse.setWalletName(wallet.getWalletName());
													walletResponse.setTotalBalance(totalBalance);
													walletResponse.setNoOfAssets(assetsRes2List.size());
													walletResponse.setAssetsResList(assetsRes2List);
													if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
														saudiWalletResponseList.add(walletResponse);
													} else if (wallet.getType()
															.equalsIgnoreCase(Constant.UNITED_STATES)) {
														usaWalletResponseList.add(walletResponse);
													} else if (wallet.getType().equalsIgnoreCase("crypto")) {
														cryptoWalletResponseList.add(walletResponse);
													}
												}
											}
										}
									}
									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
									walletResponsePayloadList.add(walletResponsePayload);
									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
								} else if (type.isBlank()) {
									/* filter for All */
									for (int i = 1; i < portfolioList.size(); i++) {
										for (Wallet wallet : portfolioList.get(i).getWalletList()) {
											if (wallet.getStatus().equals(Constant.ONE)) {
												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
													currency = "sar";
												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
														|| wallet.getType().equalsIgnoreCase("crypto")) {
													currency = "usd";
												}
												Double totalBalance = 0.0;
												List<AssetsRes2> assetsRes2List = new ArrayList<>();
												WalletResponse walletResponse = new WalletResponse();
												for (Assets assets : wallet.getAssetsList()) {
													AssetsRes2 assetsRes2 = new AssetsRes2();
													if (assets.getInstrumentType()
															.equalsIgnoreCase(Constant.SAUDI_ARABIA)
															|| assets.getInstrumentType()
																	.equalsIgnoreCase(Constant.UNITED_STATES)) {
														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
														assetsRes2.setClosePrice(Double.valueOf(
																assets.getTotalPrice() / assets.getQuantity()));
														assetsRes2
																.setLogo(assets.getStock().getStockProfile().getLogo());
														assetsRes2.setExchange(assets.getStock().getExchange());
														assetsRes2.setInstrumentId(assets.getStock().getId());
														assetsRes2.setPortfolioId(portfolioList.get(i).getId());
														assetsRes2.setCurrency(currency);
														totalBalance = totalBalance + assets.getTotalPrice();
														assetsRes2List.add(assetsRes2);
													} else if (assets.getInstrumentType().equalsIgnoreCase("crypto")) {
														String price = "0";
														String cryptoDetailsResponse = "";
														if (assets.getCrypto() != null) {
															cryptoDetailsResponse = thirdPartyApiUtil
																	.getCryptoDetails(assets.getCrypto().getCryptoId());
														}
														if (!cryptoDetailsResponse.isBlank()) {
															log.info("data found ! status - {}", cryptoDetailsResponse);
															/* using objectMapper */
															ObjectMapper mapper = new ObjectMapper();
															mapper.configure(
																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
																	false);
															/* get CryptoMarketDetails */
															Map<?, ?> mapResponse = mapper
																	.readValue(cryptoDetailsResponse, Map.class);

															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
															data = mapper.convertValue(mapResponse,
																	TsCryptoDetailsResponse.class);
															log.info("data found ! status - {}", data);

															/* save market data in third party apis */
															for (TsPrice cryptoListPrice : data.getData()
																	.getMarket_data().getPrice()) {
																/* CryptoMarketDetails */
																if (cryptoListPrice.getPrice_latest() == null) {
																	price = "0";
																} else {
																	price = methodUtil.formattedValues(
																			cryptoListPrice.getPrice_latest());
																}
															}
														}

														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																assets.getCrypto() != null ? Double.valueOf(price)
//																		: assets.getTotalPrice());
														assetsRes2.setClosePrice(Double.valueOf(
																assets.getTotalPrice() / assets.getQuantity()));
														assetsRes2.setExchange(assets.getExchange());
														assetsRes2.setLogo(assets.getCrypto() != null
																? assets.getCrypto().getLogo()
																: "");
														assetsRes2.setInstrumentId(
																assets.getCrypto() != null ? assets.getCrypto().getId()
																		: null);
														assetsRes2.setPortfolioId(portfolioList.get(i).getId());
														assetsRes2.setCurrency(currency);
														totalBalance = totalBalance + assets.getTotalPrice();
														assetsRes2List.add(assetsRes2);
													} else {
														map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
														map.put(Constant.MESSAGE,
																Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
														log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE
																+ " ! status - {}", Constant.OK);
														return map;
													}
												}
												walletResponse.setWalletId(wallet.getId());
												walletResponse.setWalletName(wallet.getWalletName());
												walletResponse.setTotalBalance(totalBalance);
												walletResponse.setNoOfAssets(assetsRes2List.size());
												walletResponse.setAssetsResList(assetsRes2List);
												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
													saudiWalletResponseList.add(walletResponse);
												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
													usaWalletResponseList.add(walletResponse);
												} else if (wallet.getType().equalsIgnoreCase("crypto")) {
													cryptoWalletResponseList.add(walletResponse);
												}
											}
										}
									}
									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
									walletResponsePayloadList.add(walletResponsePayload);
									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
								}
							} else if (portfolioId > 0) {
								boolean isFound = false;
								Portfolio portfolio = new Portfolio();
								for (int i = 1; i < portfolioList.size(); i++) {
									if (portfolioId.equals(portfolioList.get(i).getId())) {
										isFound = true;
										BeanUtils.copyProperties(portfolioList.get(i), portfolio);
										break;
									}
								}
								if (isFound == false) {
									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
									log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
									return map;
								}
								if (!type.isBlank()) {
									for (Wallet wallet : portfolio.getWalletList()) {
										if (wallet.getStatus().equals(Constant.ONE)) {
											if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
												currency = "sar";
											} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
													|| wallet.getType().equalsIgnoreCase("crypto")) {
												currency = "usd";
											}
											if (type.equalsIgnoreCase(wallet.getType())) {
												Double totalBalance = 0.0;
												List<AssetsRes2> assetsRes2List = new ArrayList<>();
												WalletResponse walletResponse = new WalletResponse();
												for (Assets assets : wallet.getAssetsList()) {
													AssetsRes2 assetsRes2 = new AssetsRes2();
													if (type.equalsIgnoreCase(assets.getInstrumentType())
															&& (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
																	|| type.equalsIgnoreCase(Constant.UNITED_STATES))) {
														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																Double.valueOf(assets.getStock().getPrice()));
														assetsRes2.setClosePrice(Double.valueOf(
																assets.getTotalPrice() / assets.getQuantity()));
														assetsRes2
																.setLogo(assets.getStock().getStockProfile().getLogo());
														assetsRes2.setExchange(assets.getStock().getExchange());
														assetsRes2.setInstrumentId(assets.getStock().getId());
														assetsRes2.setPortfolioId(portfolio.getId());
														assetsRes2.setCurrency(currency);
														totalBalance = totalBalance + assets.getTotalPrice();
														assetsRes2List.add(assetsRes2);
													} else if (type.equalsIgnoreCase(assets.getInstrumentType())
															&& type.equalsIgnoreCase("crypto")) {
														String price = "0";
														String cryptoDetailsResponse = "";
														if (assets.getCrypto() != null) {
															cryptoDetailsResponse = thirdPartyApiUtil
																	.getCryptoDetails(assets.getCrypto().getCryptoId());
														}
														if (!cryptoDetailsResponse.isBlank()) {
															log.info("data found ! status - {}", cryptoDetailsResponse);
															/* using objectMapper */
															ObjectMapper mapper = new ObjectMapper();
															mapper.configure(
																	DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
																	false);
															/* get CryptoMarketDetails */
															Map<?, ?> mapResponse = mapper
																	.readValue(cryptoDetailsResponse, Map.class);

															TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
															data = mapper.convertValue(mapResponse,
																	TsCryptoDetailsResponse.class);
															log.info("data found ! status - {}", data);

															/* save market data in third party apis */
															for (TsPrice cryptoListPrice : data.getData()
																	.getMarket_data().getPrice()) {
																/* CryptoMarketDetails */
																if (cryptoListPrice.getPrice_latest() == null) {
																	price = "0";
																} else {
																	price = methodUtil.formattedValues(
																			cryptoListPrice.getPrice_latest());
																}
															}
														}
														BeanUtils.copyProperties(assets, assetsRes2);
//														assetsRes2.setClosePrice(
//																assets.getCrypto() != null ? Double.valueOf(price)
//																		: assets.getTotalPrice());
														assetsRes2.setClosePrice(Double.valueOf(
																assets.getTotalPrice() / assets.getQuantity()));
														assetsRes2.setExchange(assets.getExchange());
														assetsRes2.setLogo(assets.getCrypto() != null
																? assets.getCrypto().getLogo()
																: "");
														assetsRes2.setInstrumentId(
																assets.getCrypto() != null ? assets.getCrypto().getId()
																		: null);
														assetsRes2.setPortfolioId(portfolio.getId());
														assetsRes2.setCurrency(currency);
														totalBalance = totalBalance + assets.getTotalPrice();
														assetsRes2List.add(assetsRes2);
													} else {
														map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
														map.put(Constant.MESSAGE,
																Constant.INVALID_PORTFOLIO_TYPE_MESSAGE);
														log.info(Constant.INVALID_PORTFOLIO_TYPE_MESSAGE
																+ " ! status - {}", Constant.OK);
														return map;
													}
												}
												walletResponse.setWalletId(wallet.getId());
												walletResponse.setWalletName(wallet.getWalletName());
												walletResponse.setTotalBalance(totalBalance);
												walletResponse.setNoOfAssets(assetsRes2List.size());
												walletResponse.setAssetsResList(assetsRes2List);
												if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
													saudiWalletResponseList.add(walletResponse);
												} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
													usaWalletResponseList.add(walletResponse);
												} else if (wallet.getType().equalsIgnoreCase("crypto")) {
													cryptoWalletResponseList.add(walletResponse);
												}
											}
										}
									}
									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
									walletResponsePayloadList.add(walletResponsePayload);
									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
									portfolioResponsePayload
											.setIsAvailableCashEnabled(portfolio.getIsAvailableCashEnabled());
								} else if (type.isBlank()) {
									for (Wallet wallet : portfolio.getWalletList()) {
										if (wallet.getStatus().equals(Constant.ONE)) {
											if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
												currency = "sar";
											} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)
													|| wallet.getType().equalsIgnoreCase("crypto")) {
												currency = "usd";
											}
											Double totalBalance = 0.0;
											List<AssetsRes2> assetsRes2List = new ArrayList<>();
											WalletResponse walletResponse = new WalletResponse();
											for (Assets assets : wallet.getAssetsList()) {
												AssetsRes2 assetsRes2 = new AssetsRes2();
												if (assets.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
														|| assets.getInstrumentType()
																.equalsIgnoreCase(Constant.UNITED_STATES)) {
													BeanUtils.copyProperties(assets, assetsRes2);
//													assetsRes2.setClosePrice(
//															Double.valueOf(assets.getStock().getPrice()));
													assetsRes2.setClosePrice(Double
															.valueOf(assets.getTotalPrice() / assets.getQuantity()));
													assetsRes2.setLogo(assets.getStock().getStockProfile().getLogo());
													assetsRes2.setExchange(assets.getStock().getExchange());
													assetsRes2.setInstrumentId(assets.getStock().getId());
													assetsRes2.setPortfolioId(portfolio.getId());
													assetsRes2.setCurrency(currency);
													totalBalance = totalBalance + assets.getTotalPrice();
													assetsRes2List.add(assetsRes2);
												} else if (assets.getInstrumentType().equalsIgnoreCase("crypto")) {
													String price = "0";
													String cryptoDetailsResponse = "";
													if (assets.getCrypto() != null) {
														cryptoDetailsResponse = thirdPartyApiUtil
																.getCryptoDetails(assets.getCrypto().getCryptoId());
													}
													if (!cryptoDetailsResponse.isBlank()) {
														log.info("data found ! status - {}", cryptoDetailsResponse);
														/* using objectMapper */
														ObjectMapper mapper = new ObjectMapper();
														mapper.configure(
																DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
																false);
														/* get CryptoMarketDetails */
														Map<?, ?> mapResponse = mapper.readValue(cryptoDetailsResponse,
																Map.class);

														TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
														data = mapper.convertValue(mapResponse,
																TsCryptoDetailsResponse.class);
														log.info("data found ! status - {}", data);

														/* save market data in third party apis */
														for (TsPrice cryptoListPrice : data.getData().getMarket_data()
																.getPrice()) {
															/* CryptoMarketDetails */
															if (cryptoListPrice.getPrice_latest() == null) {
																price = "0";
															} else {
																price = methodUtil.formattedValues(
																		cryptoListPrice.getPrice_latest());
															}
														}
													}
													BeanUtils.copyProperties(assets, assetsRes2);
//													assetsRes2.setClosePrice(
//															assets.getCrypto() != null ? Double.valueOf(price)
//																	: assets.getTotalPrice());
													assetsRes2.setClosePrice(Double
															.valueOf(assets.getTotalPrice() / assets.getQuantity()));
													assetsRes2.setExchange(assets.getExchange());
													assetsRes2.setLogo(
															assets.getCrypto() != null ? assets.getCrypto().getLogo()
																	: "");
													assetsRes2.setInstrumentId(
															assets.getCrypto() != null ? assets.getCrypto().getId()
																	: null);
													assetsRes2.setPortfolioId(portfolio.getId());
													assetsRes2.setCurrency(currency);
													totalBalance = totalBalance + assets.getTotalPrice();
													assetsRes2List.add(assetsRes2);
												}
											}
											walletResponse.setWalletId(wallet.getId());
											walletResponse.setWalletName(wallet.getWalletName());
											walletResponse.setTotalBalance(totalBalance);
											walletResponse.setNoOfAssets(assetsRes2List.size());
											walletResponse.setAssetsResList(assetsRes2List);
											if (wallet.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
												saudiWalletResponseList.add(walletResponse);
											} else if (wallet.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
												usaWalletResponseList.add(walletResponse);
											} else if (wallet.getType().equalsIgnoreCase("crypto")) {
												cryptoWalletResponseList.add(walletResponse);
											}
										}
									}
									walletResponsePayload.setCryptoWalletResponseList(cryptoWalletResponseList);
									walletResponsePayload.setSaudiWalletResponseList(saudiWalletResponseList);
									walletResponsePayload.setUsaWalletResponseList(usaWalletResponseList);
									walletResponsePayloadList.add(walletResponsePayload);
									portfolioResponsePayload.setWalletResponsePayload(walletResponsePayload);
									portfolioResponsePayload
											.setIsAvailableCashEnabled(portfolio.getIsAvailableCashEnabled());
								}
							}
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
							map.put(Constant.DATA, portfolioResponsePayload);
							log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
							map.put(Constant.DATA, portfolioResponsePayload);
							log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + " status - {}", Constant.OK);
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
						log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
					log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " status - {}", Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
				log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " ! status - {}", Constant.OK);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception : " + e.getMessage());
		}
		return map;
	}

	public Wallet saveWalletData(WalletConnectionRequest walletConnectionRequest, Double totalBalance) {
		Wallet wallet = new Wallet();
		try {
			wallet.setApiKey(walletConnectionRequest.getApiKey());
			wallet.setApiSecret(walletConnectionRequest.getApiSecret());
			wallet.setDailyOpeningBalance(totalBalance);
			wallet.setLatestTotalBalance(totalBalance);
			wallet.setPrevTotalBalance(totalBalance);
			wallet.setStatus(Constant.ONE);
			wallet.setCreationDate(new Date());
			wallet.setOpenTime(new Date());
			wallet.setAutoSyncWalletAt(new Date());
			wallet.setPortfolioIdFk(walletConnectionRequest.getPortfolioId());
			wallet.setWalletName(walletConnectionRequest.getWalletName());
			wallet.setDailyPercentageGain(0.0);
			wallet.setDailyPriceGain(0.0);
			wallet.setTotalPercentageGain(0.0);
			wallet.setTotalPriceGain(0.0);
			wallet.setType(walletConnectionRequest.getType());
			wallet = walletRepository.save(wallet);
		} catch (Exception e) {
			log.error(e.getMessage() + " status - {}", Constant.SERVER_MESSAGE);
			return null;
		}
		return wallet;
	}

//	public Map<String, Object> saveAssets(WalletConnectionRequest walletConnectionRequest, Wallet wallet) {
//		Map<String, Object> map = new HashMap<>();
//		/* Getting assets */
//		String apiResponse = ThirdPartyApiUtil.getAssetList(walletConnectionRequest.getApiKey(),
//				walletConnectionRequest.getApiSecret());
//		if (!apiResponse.isBlank()) {
//			List<AssetsRes> assetsResList = new ArrayList<>();
//			try {
//				Type collectionType = new TypeToken<List<AssetsRes>>() {
//				}.getType();
//				assetsResList = new Gson().fromJson(apiResponse, collectionType);
//			} catch (Exception e) {
//				map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//				map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//				log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
//				return map;
//			}
//			if (!assetsResList.isEmpty()) {
//				if (wallet.getAssetsList() == null || wallet.getAssetsList().isEmpty()) {
//					List<Assets> assetList = new ArrayList<>();
//					StringBuilder symbolString = new StringBuilder();
//					symbolString.append("[");
//					for (AssetsRes assetsRes : assetsResList) {
//						if (!assetsRes.getAsset().equalsIgnoreCase("USDT")) {
//							assetsRes.setAsset(assetsRes.getAsset() + "USDT");
//							symbolString.append("\"" + assetsRes.getAsset() + "\"" + ",");
//						}
//					}
//					if (symbolString != null) {
//						int index = symbolString.lastIndexOf(",");
//						symbolString.deleteCharAt(index);
//						symbolString.append("]");
//					}
//
//					apiResponse = ThirdPartyApiUtil.getTickerPrice(symbolString, wallet.getApiKey(),
//							wallet.getApiSecret());
//					if (!apiResponse.isBlank()) {
//						List<TickerDetail> tickerDetailsList = new ArrayList<>();
//						try {
//							Type type = new TypeToken<List<TickerDetail>>() {
//							}.getType();
//							tickerDetailsList = new Gson().fromJson(apiResponse, type);
//						} catch (Exception e) {
//							map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//							map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//							log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
//							return map;
//						}
//						if (!tickerDetailsList.isEmpty()) {
//							for (AssetsRes assetsRes : assetsResList) {
//								Assets assets = new Assets();
//								assets.setSymbol(assetsRes.getAsset());
//								assets.setQuantity(Double.valueOf(assetsRes.getFree()));
//								assets.setPriceGain(0.0);
//								assets.setPricePercentageGain(0.0);
//								assets.setWalletIdFk(wallet.getId());
//								assets.setCreationDate(new Date());
//								assets.setStatus(Constant.ONE);
//								assetList.add(assets);
//							}
//							for (TickerDetail tickerDetail : tickerDetailsList) {
//								for (Assets assets : assetList) {
//									if (assets.getSymbol().equalsIgnoreCase(tickerDetail.getSymbol())) {
//										assets.setClosePrice((tickerDetail.getLastPrice() * assets.getQuantity()));
//										assets.setPreviousClosePrice(
//												(tickerDetail.getLastPrice() * assets.getQuantity()));
//										break;
//									}
//								}
//							}
//							assetsRepository.saveAll(assetList);
//
//							wallet.setAssetsList(assetList);
//							log.info("Assets data saved successfully ! status - {}", Constant.OK);
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.OK);
//							map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
//							log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}", Constant.OK);
//							return map;
//						}
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//						map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
//						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}", Constant.SERVER_ERROR);
//						return map;
//					}
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
//				log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}", Constant.OK);
//				return map;
//			}
//		} else {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
//			log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}", Constant.SERVER_ERROR);
//			return map;
//		}
//		return map;
//	}

	public Map<String, Object> saveAssets(WalletConnectionRequest walletConnectionRequest, Wallet wallet) {
		Map<String, Object> map = new HashMap<>();
		/* Getting assets */
		String apiResponse = ThirdPartyApiUtil.getAssetList(walletConnectionRequest.getApiKey(),
				walletConnectionRequest.getApiSecret());
		if (!apiResponse.isBlank()) {
			List<AssetsRes> assetsResList = new ArrayList<>();
			try {
				Type collectionType = new TypeToken<List<AssetsRes>>() {
				}.getType();
				assetsResList = new Gson().fromJson(apiResponse, collectionType);
			} catch (Exception e) {
				map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
				map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
				log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
				return map;
			}
			if (!assetsResList.isEmpty()) {
				if (wallet.getAssetsList() == null || wallet.getAssetsList().isEmpty()
						|| !wallet.getAssetsList().isEmpty()) {
					List<Assets> assetList = new ArrayList<>();
					StringBuilder symbolString = new StringBuilder();
					symbolString.append("[");
					for (AssetsRes assetsRes : assetsResList) {
						if (!assetsRes.getAsset().equalsIgnoreCase("USDT")) {
							assetsRes.setAsset(assetsRes.getAsset() + "USDT");
							symbolString.append("\"" + assetsRes.getAsset() + "\"" + ",");
						}
					}
					if (symbolString != null) {
						int index = symbolString.lastIndexOf(",");
						symbolString.deleteCharAt(index);
						symbolString.append("]");
					}

					apiResponse = ThirdPartyApiUtil.getTickerPrice(symbolString, wallet.getApiKey(),
							wallet.getApiSecret());
					if (!apiResponse.isBlank()) {
						List<TickerDetail> tickerDetailsList = new ArrayList<>();
						try {
							Type type = new TypeToken<List<TickerDetail>>() {
							}.getType();
							tickerDetailsList = new Gson().fromJson(apiResponse, type);
						} catch (Exception e) {
							map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
							map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
							log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
							return map;
						}
						if (!tickerDetailsList.isEmpty()) {
							for (AssetsRes assetsRes : assetsResList) {
								if (!assetsRes.getAsset().equalsIgnoreCase("USDT")) {
									Assets assets = new Assets();
									assets.setSymbol(assetsRes.getAsset());
									assets.setQuantity(Double.valueOf(assetsRes.getFree()));
									assets.setPriceGain(0.0);
									assets.setPricePercentageGain(0.0);
									assets.setWalletIdFk(wallet.getId());
									assets.setCreationDate(new Date());
									assets.setStatus(Constant.ONE);
									assets.setExchange(wallet.getWalletName());
									assets.setInstrumentType(wallet.getType());
									assetList.add(assets);
								}
							}
							for (TickerDetail tickerDetail : tickerDetailsList) {
								for (Assets assets : assetList) {
									if (assets.getSymbol().equalsIgnoreCase(tickerDetail.getSymbol())) {
										assets.setTotalPrice((tickerDetail.getLastPrice() * assets.getQuantity()));
										assets.setPreviousClosePrice(
												(tickerDetail.getLastPrice() * assets.getQuantity()));
										break;
									}
								}
							}
							assetsRepository.saveAll(assetList);

							wallet.setAssetsList(assetList);
							log.info("Assets data saved successfully ! status - {}", Constant.OK);
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
							log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}", Constant.OK);
							return map;
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
						map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}", Constant.SERVER_ERROR);
						return map;
					}
				} else if (!wallet.getAssetsList().isEmpty()) {
					List<Assets> assetList = new ArrayList<>();
					StringBuilder symbolString = new StringBuilder();
					symbolString.append("[");
					for (AssetsRes assetsRes : assetsResList) {
						if (!assetsRes.getAsset().equalsIgnoreCase("USDT")) {
							assetsRes.setAsset(assetsRes.getAsset() + "USDT");
							symbolString.append("\"" + assetsRes.getAsset() + "\"" + ",");
						}
					}
					if (symbolString != null) {
						int index = symbolString.lastIndexOf(",");
						symbolString.deleteCharAt(index);
						symbolString.append("]");
					}

					apiResponse = ThirdPartyApiUtil.getTickerPrice(symbolString, wallet.getApiKey(),
							wallet.getApiSecret());
					if (!apiResponse.isBlank()) {
						List<TickerDetail> tickerDetailsList = new ArrayList<>();
						try {
							Type type = new TypeToken<List<TickerDetail>>() {
							}.getType();
							tickerDetailsList = new Gson().fromJson(apiResponse, type);
						} catch (Exception e) {
							map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
							map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
							log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
							return map;
						}
						if (!tickerDetailsList.isEmpty()) {
							for (AssetsRes assetsRes : assetsResList) {
								if (!assetsRes.getAsset().equalsIgnoreCase("USDT")) {
									Assets assets = new Assets();
									assets.setSymbol(assetsRes.getAsset());
									assets.setQuantity(Double.valueOf(assetsRes.getFree()));
									assets.setPriceGain(0.0);
									assets.setPricePercentageGain(0.0);
									assets.setWalletIdFk(wallet.getId());
									assets.setCreationDate(new Date());
									assets.setStatus(Constant.ONE);
									assets.setExchange(wallet.getWalletName());
									assets.setInstrumentType(wallet.getType());
									assetList.add(assets);
								}
							}
							for (TickerDetail tickerDetail : tickerDetailsList) {
								for (Assets assets : assetList) {
									if (assets.getSymbol().equalsIgnoreCase(tickerDetail.getSymbol())) {
										assets.setTotalPrice((tickerDetail.getLastPrice() * assets.getQuantity()));
										assets.setPreviousClosePrice(
												(tickerDetail.getLastPrice() * assets.getQuantity()));
										break;
									}
								}
							}
							assetsRepository.saveAll(assetList);

							wallet.setAssetsList(assetList);
							log.info("Assets data saved successfully ! status - {}", Constant.OK);
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
							log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}", Constant.OK);
							return map;
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
						map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}", Constant.SERVER_ERROR);
						return map;
					}
				}
			} else

			{
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
				log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}", Constant.OK);
				return map;
			}
		} else {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
			log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}", Constant.SERVER_ERROR);
			return map;
		}
		return map;
	}

//	@Override
//	public Map<String, Object> connectWallet(WalletConnectionRequest walletConnectionRequest) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			if (walletConnectionRequest.getUserId() != 0 && walletConnectionRequest.getPortfolioId() != 0) {
//				/* Calling USER-SERVICE */
//				User user = null;
//				try {
//					Map<String, Object> res = userService.getProfileById(walletConnectionRequest.getUserId());
//					Object obj = res.get("data");
//					ObjectMapper mapper = new ObjectMapper();
//					user = mapper.convertValue(obj, User.class);
//					log.info(Constant.DATA_FOUND + " ! status - {}", Constant.OK);
//				} catch (Exception e) {
//					map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//					map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//					log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
//					return map;
//				}
//				if (user != null) {
//					if (!user.getPortfolioList().isEmpty()) {
//						Portfolio portfolio = new Portfolio();
//						Double totalBalance = 0.0;
//						String apiResponse = ThirdPartyApiUtil.getBinanceAccountInfo(
//								walletConnectionRequest.getApiKey(), walletConnectionRequest.getApiSecret());
//						if (!apiResponse.isBlank()) {
//							List<WalletBalance> walletBalanceList = new ArrayList<>();
//							try {
//								char c = apiResponse.charAt(0);
//								if (c == '[') {
//									Type collectionType = new TypeToken<List<WalletBalance>>() {
//									}.getType();
//									walletBalanceList = new Gson().fromJson(apiResponse, collectionType);
//								} else if (c == '{') {
//									Type collectionType = new TypeToken<ErrorResponse>() {
//									}.getType();
//									ErrorResponse errorResponse = new Gson().fromJson(apiResponse, collectionType);
//									String code = errorResponse.getCode();
//									if (code.equals("-2008") || code.equals("-1022")) {
//										map.put(Constant.RESPONSE_CODE, Constant.OK);
//										map.put(Constant.MESSAGE, Constant.APIKEY_SECRETKEY_INVALID_MESSAGE);
//										log.info(Constant.APIKEY_SECRETKEY_INVALID_MESSAGE + " status - {}",
//												Constant.OK);
//										return map;
//									} else {
//										map.put(Constant.RESPONSE_CODE, Constant.OK);
//										map.put(Constant.MESSAGE, errorResponse.getMsg());
//										log.info(errorResponse.getMsg() + " status - {}", Constant.OK);
//										return map;
//									}
//								}
//							} catch (Exception e) {
//								map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//								map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//								log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
//								return map;
//							}
//							if (!walletBalanceList.isEmpty()) {
//								for (WalletBalance walletBalance : walletBalanceList) {
//									if (!walletBalance.getWalletName().equalsIgnoreCase("spot"))
//										totalBalance = totalBalance + Double.valueOf(walletBalance.getBalance());
//								}
//								apiResponse = ThirdPartyApiUtil.getBtcTickerPrice(walletConnectionRequest.getApiKey(),
//										walletConnectionRequest.getApiSecret());
//								if (!apiResponse.isBlank()) {
//									TickerDetail tickerDetails = new TickerDetail();
//									try {
//										Type type = new TypeToken<TickerDetail>() {
//										}.getType();
//										tickerDetails = new Gson().fromJson(apiResponse, type);
//									} catch (Exception e) {
//										map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//										map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
//										log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
//										return map;
//									}
//									if (tickerDetails != null) {
//										totalBalance = UtilityMethods.convertBtcToUSD(totalBalance, tickerDetails);
//
//										for (Portfolio portfolio2 : user.getPortfolioList()) {
//											if (portfolio2.getId().equals(walletConnectionRequest.getPortfolioId())) {
//												BeanUtils.copyProperties(portfolio2, portfolio);
//												break;
//											}
//										}
//
//										if (portfolio.getWalletList() == null || portfolio.getWalletList().isEmpty()) {
//											/* To create new connection */
//											Wallet wallet = saveWalletData(walletConnectionRequest, totalBalance);
//											if (wallet != null) {
//												log.info("Wallet data saved successfully ! status - {}", Constant.OK);
//
//												/* Getting assets */
//												saveAssets(walletConnectionRequest, wallet);
//
//												map.put(Constant.RESPONSE_CODE, Constant.OK);
//												map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
//												log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
//														Constant.OK);
//											} else {
//												map.put(Constant.RESPONSE_CODE, Constant.OK);
//												map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
//												log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}",
//														Constant.OK);
//												return map;
//											}
//										} else {
//											Wallet wallet1 = new Wallet();
//											Boolean isPresent = false;
//											for (Wallet oldWallet : portfolio.getWalletList()) {
//												if (walletConnectionRequest.getWalletName()
//														.equalsIgnoreCase(oldWallet.getWalletName())) {
//													isPresent = true;
//													BeanUtils.copyProperties(oldWallet, wallet1);
//													break;
//												}
//											}
//											if (isPresent == false) {
//												/* To create new connection */
//												Wallet wallet = saveWalletData(walletConnectionRequest, totalBalance);
//												if (wallet != null) {
//													log.info("Wallet data saved successfully ! status - {}",
//															Constant.OK);
//
//													/* Getting assets */
//													saveAssets(walletConnectionRequest, wallet);
//
//													map.put(Constant.RESPONSE_CODE, Constant.OK);
//													map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
//													log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
//															Constant.OK);
//												} else {
//													map.put(Constant.RESPONSE_CODE, Constant.OK);
//													map.put(Constant.MESSAGE,
//															Constant.WALLET_CONNECTION_FAILED_MESSAGE);
//													log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}",
//															Constant.OK);
//													return map;
//												}
//											} else {
//												/* To update old connection */
//												if (wallet1.getApiKey() == null && wallet1.getApiSecret() == null) {
//													wallet1.setApiKey(walletConnectionRequest.getApiKey());
//													wallet1.setApiSecret(walletConnectionRequest.getApiSecret());
//													wallet1.setApiKey(walletConnectionRequest.getApiKey());
//													wallet1.setApiSecret(walletConnectionRequest.getApiSecret());
//													wallet1.setDailyOpeningBalance(
//															wallet1.getDailyOpeningBalance() + totalBalance);
//													wallet1.setLatestTotalBalance(
//															wallet1.getLatestTotalBalance() + totalBalance);
//													wallet1.setPrevTotalBalance(
//															wallet1.getPrevTotalBalance() + totalBalance);
//													wallet1.setOpenTime(new Date());
//													wallet1.setAutoSyncWalletAt(new Date());
//													wallet1.setUpdationDate(new Date());
//													wallet1 = walletRepository.save(wallet1);
//													log.info("Wallet data update successfully ! status - {}",
//															Constant.OK);
//
//													/* Getting assets */
//													saveAssets(walletConnectionRequest, wallet1);
//
//													map.put(Constant.RESPONSE_CODE, Constant.OK);
//													map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
//													log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
//															Constant.OK);
//												} else {
//													if ((!wallet1.getApiKey()
//															.matches(walletConnectionRequest.getApiKey())
//															|| !wallet1.getApiSecret()
//																	.matches(walletConnectionRequest.getApiSecret()))
//															|| (!wallet1.getApiKey()
//																	.matches(walletConnectionRequest.getApiKey())
//																	&& !wallet1.getApiSecret().matches(
//																			walletConnectionRequest.getApiSecret()))) {
//														wallet1.setApiKey(walletConnectionRequest.getApiKey());
//														wallet1.setApiSecret(walletConnectionRequest.getApiSecret());
//														wallet1.setUpdationDate(new Date());
//														walletRepository.save(wallet1);
//														log.info("Wallet data update successfully ! status - {}",
//																Constant.OK);
//
//														map.put(Constant.RESPONSE_CODE, Constant.OK);
//														map.put(Constant.MESSAGE,
//																Constant.WALLET_CREDENTILAS_UPDATED_MESSAGE);
//														log.info(Constant.WALLET_CREDENTILAS_UPDATED_MESSAGE
//																+ " Status - {}", Constant.OK);
//														return map;
//													} else if (wallet1.getApiKey()
//															.matches(walletConnectionRequest.getApiKey())
//															&& wallet1.getApiSecret()
//																	.matches(walletConnectionRequest.getApiSecret())
//															&& wallet1.getStatus() == Constant.TWO) {
//														wallet1.setStatus(Constant.ONE);
//														wallet1.setUpdationDate(new Date());
//														walletRepository.save(wallet1);
//														log.info("Wallet data update successfully ! status - {}",
//																Constant.OK);
//
//														map.put(Constant.RESPONSE_CODE, Constant.OK);
//														map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
//														log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
//																Constant.OK);
//														return map;
//													} else if (wallet1.getApiKey()
//															.matches(walletConnectionRequest.getApiKey())
//															&& wallet1.getApiSecret()
//																	.matches(walletConnectionRequest.getApiSecret())
//															&& wallet1.getStatus() == Constant.ONE) {
//														map.put(Constant.RESPONSE_CODE, Constant.OK);
//														map.put(Constant.MESSAGE,
//																Constant.WALLET_ALREADY_CONNECTED_MESSAGE);
//														log.info(Constant.WALLET_ALREADY_CONNECTED_MESSAGE
//																+ " status - {}", Constant.OK);
//														return map;
//													}
//												}
//											}
//										}
//									} else {
//										map.put(Constant.RESPONSE_CODE, Constant.OK);
//										map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
//										log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}",
//												Constant.OK);
//									}
//								} else {
//									map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//									map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
//									log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}",
//											Constant.SERVER_ERROR);
//								}
//							} else {
//								map.put(Constant.RESPONSE_CODE, Constant.OK);
//								map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
//								log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}", Constant.OK);
//							}
//						} else {
//							map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//							map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
//							log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}", Constant.SERVER_ERROR);
//						}
//					} else {
//						map.put(Constant.RESPONSE_CODE, Constant.OK);
//						map.put(Constant.MESSAGE, Constant.CREATE_PORTFOLIO_MESSAGE);
//						log.info(Constant.CREATE_PORTFOLIO_MESSAGE + " status - {}", Constant.OK);
//					}
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
//					log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
//				}
//			} else {
//				map.put(Constant.RESPONSE_CODE, Constant.OK);
//				map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
//				log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error("Exception : " + e.getMessage());
//		}
//		return map;
//	}

	@Override
	public Map<String, Object> connectWallet(WalletConnectionRequest walletConnectionRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (walletConnectionRequest.getUserId() != 0 && walletConnectionRequest.getPortfolioId() != 0) {
				/* Calling USER-SERVICE */
				User user = null;
				try {
					Map<String, Object> res = userService.getProfileById(walletConnectionRequest.getUserId());
					Object obj = res.get("data");
					ObjectMapper mapper = new ObjectMapper();
					user = mapper.convertValue(obj, User.class);
					log.info(Constant.DATA_FOUND + " ! status - {}", Constant.OK);
				} catch (Exception e) {
					map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
					map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
					log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
					return map;
				}
				if (user != null) {
					if (!user.getPortfolioList().isEmpty()) {
						Portfolio portfolio = new Portfolio();
						Double totalBalance = 0.0;
						String apiResponse = ThirdPartyApiUtil.getBinanceAccountInfo(
								walletConnectionRequest.getApiKey(), walletConnectionRequest.getApiSecret());
						if (!apiResponse.isBlank()) {
							List<WalletBalance> walletBalanceList = new ArrayList<>();
							try {
								char c = apiResponse.charAt(0);
								if (c == '[') {
									Type collectionType = new TypeToken<List<WalletBalance>>() {
									}.getType();
									walletBalanceList = new Gson().fromJson(apiResponse, collectionType);
								} else if (c == '{') {
									Type collectionType = new TypeToken<ErrorResponse>() {
									}.getType();
									ErrorResponse errorResponse = new Gson().fromJson(apiResponse, collectionType);
									String code = errorResponse.getCode();
									if (code.equals("-2008") || code.equals("-1022")) {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.APIKEY_SECRETKEY_INVALID_MESSAGE);
										log.info(Constant.APIKEY_SECRETKEY_INVALID_MESSAGE + " status - {}",
												Constant.OK);
										return map;
									} else {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, errorResponse.getMsg());
										log.info(errorResponse.getMsg() + " status - {}", Constant.OK);
										return map;
									}
								}
							} catch (Exception e) {
								map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
								map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
								log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
								return map;
							}
							if (!walletBalanceList.isEmpty()) {
								for (WalletBalance walletBalance : walletBalanceList) {
									if (!walletBalance.getWalletName().equalsIgnoreCase("spot"))
										totalBalance = totalBalance + Double.valueOf(walletBalance.getBalance());
								}
								apiResponse = ThirdPartyApiUtil.getBtcTickerPrice(walletConnectionRequest.getApiKey(),
										walletConnectionRequest.getApiSecret());
								if (!apiResponse.isBlank()) {
									TickerDetail tickerDetails = new TickerDetail();
									try {
										Type type = new TypeToken<TickerDetail>() {
										}.getType();
										tickerDetails = new Gson().fromJson(apiResponse, type);
									} catch (Exception e) {
										map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
										map.put(Constant.MESSAGE, Constant.INTERNAL_SERVER_ERROR_MESSAGE);
										log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
										return map;
									}
									if (tickerDetails != null) {
										totalBalance = UtilityMethods.convertBtcToUSD(totalBalance, tickerDetails);
//										for (Portfolio portfolio2 : user.getPortfolioList()) {
//											if (portfolio2.getId().equals(walletConnectionRequest.getPortfolioId())) {
//												BeanUtils.copyProperties(portfolio2, portfolio);
//												break;
//											}
//										}

										//
										boolean isFoundPortrfolio = false;
										for (int i = 1; i < user.getPortfolioList().size(); i++) {
											if (user.getPortfolioList().get(i).getId() == walletConnectionRequest
													.getPortfolioId()) {
												isFoundPortrfolio = true;
												BeanUtils.copyProperties(user.getPortfolioList().get(i), portfolio);
												break;
											}
										}
										if (isFoundPortrfolio == false) {
											map.put(Constant.RESPONSE_CODE, Constant.OK);
											map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
											log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + " status - {}",
													Constant.OK);
											return map;
										}
										//

										if (portfolio.getWalletList() == null || portfolio.getWalletList().isEmpty()) {
											/* To create new connection */
											Wallet wallet = saveWalletData(walletConnectionRequest, totalBalance);
											if (wallet != null) {
												log.info("Wallet data saved successfully ! status - {}", Constant.OK);

												/* Getting assets */
												saveAssets(walletConnectionRequest, wallet);

												map.put(Constant.RESPONSE_CODE, Constant.OK);
												map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
												log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
														Constant.OK);
											} else {
												map.put(Constant.RESPONSE_CODE, Constant.OK);
												map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
												log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}",
														Constant.OK);
												return map;
											}
										} else {
											Wallet wallet1 = new Wallet();
											Boolean isPresent = false;
											for (Wallet oldWallet : portfolio.getWalletList()) {
												if (walletConnectionRequest.getWalletName()
														.equalsIgnoreCase(oldWallet.getWalletName())) {
													isPresent = true;
													BeanUtils.copyProperties(oldWallet, wallet1);
													break;
												}
											}
											if (isPresent == false) {
												/* To create new connection */
												Wallet wallet = saveWalletData(walletConnectionRequest, totalBalance);
												if (wallet != null) {
													log.info("Wallet data saved successfully ! status - {}",
															Constant.OK);

													/* Getting assets */
													saveAssets(walletConnectionRequest, wallet);

													map.put(Constant.RESPONSE_CODE, Constant.OK);
													map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
													log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
															Constant.OK);
												} else {
													map.put(Constant.RESPONSE_CODE, Constant.OK);
													map.put(Constant.MESSAGE,
															Constant.WALLET_CONNECTION_FAILED_MESSAGE);
													log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}",
															Constant.OK);
													return map;
												}
											} else {
												/* To update old connection */
												if (wallet1.getApiKey() == null && wallet1.getApiSecret() == null) {
													wallet1.setApiKey(walletConnectionRequest.getApiKey());
													wallet1.setApiSecret(walletConnectionRequest.getApiSecret());
//													wallet1.setApiKey(walletConnectionRequest.getApiKey());
//													wallet1.setApiSecret(walletConnectionRequest.getApiSecret());
													wallet1.setDailyOpeningBalance(
															wallet1.getDailyOpeningBalance() + totalBalance);
													wallet1.setLatestTotalBalance(
															wallet1.getLatestTotalBalance() + totalBalance);
													wallet1.setPrevTotalBalance(
															wallet1.getPrevTotalBalance() + totalBalance);
													wallet1.setOpenTime(new Date());
													wallet1.setAutoSyncWalletAt(new Date());
													wallet1.setUpdationDate(new Date());
													wallet1 = walletRepository.save(wallet1);
													log.info("Wallet data update successfully ! status - {}",
															Constant.OK);

													/* Getting assets */
													saveAssets(walletConnectionRequest, wallet1);

													map.put(Constant.RESPONSE_CODE, Constant.OK);
													map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
													log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
															Constant.OK);
												} else {
													if ((!wallet1.getApiKey()
															.matches(walletConnectionRequest.getApiKey())
															|| !wallet1.getApiSecret()
																	.matches(walletConnectionRequest.getApiSecret()))
															|| (!wallet1.getApiKey()
																	.matches(walletConnectionRequest.getApiKey())
																	&& !wallet1.getApiSecret().matches(
																			walletConnectionRequest.getApiSecret()))) {
														wallet1.setApiKey(walletConnectionRequest.getApiKey());
														wallet1.setApiSecret(walletConnectionRequest.getApiSecret());
														wallet1.setUpdationDate(new Date());
														walletRepository.save(wallet1);
														log.info("Wallet data update successfully ! status - {}",
																Constant.OK);

														map.put(Constant.RESPONSE_CODE, Constant.OK);
														map.put(Constant.MESSAGE,
																Constant.WALLET_CREDENTILAS_UPDATED_MESSAGE);
														log.info(Constant.WALLET_CREDENTILAS_UPDATED_MESSAGE
																+ " Status - {}", Constant.OK);
														return map;
													} else if (wallet1.getApiKey()
															.matches(walletConnectionRequest.getApiKey())
															&& wallet1.getApiSecret()
																	.matches(walletConnectionRequest.getApiSecret())
															&& wallet1.getStatus() == Constant.TWO) {
														wallet1.setStatus(Constant.ONE);
														wallet1.setUpdationDate(new Date());
														walletRepository.save(wallet1);
														log.info("Wallet data update successfully ! status - {}",
																Constant.OK);

														map.put(Constant.RESPONSE_CODE, Constant.OK);
														map.put(Constant.MESSAGE, Constant.WALLET_CONNECTED_MESSAGE);
														log.info(Constant.WALLET_CONNECTED_MESSAGE + " Status - {}",
																Constant.OK);
														return map;
													} else if (wallet1.getApiKey()
															.matches(walletConnectionRequest.getApiKey())
															&& wallet1.getApiSecret()
																	.matches(walletConnectionRequest.getApiSecret())
															&& wallet1.getStatus() == Constant.ONE) {
														map.put(Constant.RESPONSE_CODE, Constant.OK);
														map.put(Constant.MESSAGE,
																Constant.WALLET_ALREADY_CONNECTED_MESSAGE);
														log.info(Constant.WALLET_ALREADY_CONNECTED_MESSAGE
																+ " status - {}", Constant.OK);
														return map;
													}
												}
											}
										}
									} else {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
										log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}",
												Constant.OK);
									}
								} else {
									map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
									map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
									log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}",
											Constant.SERVER_ERROR);
								}
							} else {
								map.put(Constant.RESPONSE_CODE, Constant.OK);
								map.put(Constant.MESSAGE, Constant.WALLET_CONNECTION_FAILED_MESSAGE);
								log.info(Constant.WALLET_CONNECTION_FAILED_MESSAGE + " status - {}", Constant.OK);
							}
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
							map.put(Constant.MESSAGE, Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE);
							log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}", Constant.SERVER_ERROR);
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.CREATE_PORTFOLIO_MESSAGE);
						log.info(Constant.CREATE_PORTFOLIO_MESSAGE + " status - {}", Constant.OK);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
					log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
				}
			} else

			{
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
				log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error("Exception : " + e.getMessage());
		}
		return map;
	}

	@Override
	public Map<String, Object> disconnetWallet(DisconnectWalletRequestPayload disconnectWalletRequestPayload) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (disconnectWalletRequestPayload.getUserId() != 0 && disconnectWalletRequestPayload.getPortfolioId() != 0
					&& disconnectWalletRequestPayload.getWalletId() != 0) {
				User user = userRepository.findByIdAndStatus(disconnectWalletRequestPayload.getUserId(), Constant.ONE);
				if (user != null) {
					for (Portfolio portfolio : user.getPortfolioList()) {
						if (portfolio.getId() == disconnectWalletRequestPayload.getPortfolioId()) {
							for (Wallet wallet : portfolio.getWalletList()) {
								if (wallet.getId() == disconnectWalletRequestPayload.getWalletId()) {
									if (wallet.getStatus() == Constant.ONE) {
										wallet.setUpdationDate(new Date());
										wallet.setStatus(Constant.TWO);
										walletRepository.save(wallet);

										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.WALLET_DISCONNECTED_MESSAGE);
										log.info(Constant.WALLET_DISCONNECTED_MESSAGE + " Status - {}", Constant.OK);
									} else if (wallet.getStatus() == Constant.TWO) {
										map.put(Constant.RESPONSE_CODE, Constant.OK);
										map.put(Constant.MESSAGE, Constant.WALLET_ALREADY_DISCONNECTED_MESSAGE);
										log.info(Constant.WALLET_ALREADY_DISCONNECTED_MESSAGE + " Status - {}",
												Constant.OK);
									}
									break;
								}
							}
							break;
						}
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.WALLET_NOT_DISCONNECTED_MESSAGE);
					log.info(Constant.WALLET_NOT_DISCONNECTED_MESSAGE + " status - {}", Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				map.put(Constant.MESSAGE, Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE);
				log.info(Constant.PORTFOLIO_TYPE_CANT_NULL_MESSAGE + " Status - {}", Constant.OK);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

	@Override
	public Map<String, Object> deletePorfolio(Long userId, Long portfolioId) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (portfolioId != null && portfolioId != 0) {
				User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
				if (user != null) {
					if (!user.getPortfolioList().isEmpty()) {
						List<Portfolio> portfolioList = user.getPortfolioList();
						if (portfolioList.size() == Constant.ONE) {
							if (portfolioList.get(0).getId().equals(portfolioId)) {
								transactionRepository.deleteAll(user.getTransactionList());
								log.info("transaction data deleted successfully !! status - {} " + Constant.OK);

								portfolioRepository.deleteById(portfolioId);
								log.info(Constant.PORTFOLIO_DELETED_MESSAGE + " Status - {}", Constant.OK);
							} else {
								map.put(Constant.RESPONSE_CODE, Constant.OK);
								map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
								log.info(Constant.ID_NOT_FOUND_MESSAGE + " Status - {}", Constant.OK);
								return map;
							}
						} else if (portfolioList.size() > Constant.ONE) {
							Boolean isFound = false;
							for (Portfolio portfolio : portfolioList) {
								if (portfolio.getId().equals(portfolioId)) {
									isFound = true;
									break;
								}
							}
							if (isFound == true) {
								if (portfolioList.get(0).getId().equals(portfolioId)) {
									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.MAIN_PORTFOLIO_CANT_DELETE_MESSAGE);
									log.info(Constant.MAIN_PORTFOLIO_CANT_DELETE_MESSAGE + " Status - {}", Constant.OK);
									return map;
								}
								for (int i = 1; i < portfolioList.size(); i++) {
									if (portfolioList.get(i).getId().equals(portfolioId)) {
										transactionRepository.deleteAll(user.getTransactionList());
										log.info("transaction data deleted successfully !! status - {} " + Constant.OK);

										portfolioRepository.deleteById(portfolioList.get(i).getId());
										log.info(Constant.PORTFOLIO_DELETED_MESSAGE + " Status - {}", Constant.OK);
										break;
									}
								}
							} else {
								map.put(Constant.RESPONSE_CODE, Constant.OK);
								map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
								log.info(Constant.ID_NOT_FOUND_MESSAGE + " Status - {}", Constant.OK);
								return map;
							}
						}
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.PORTFOLIO_DELETED_MESSAGE);
						log.info(Constant.PORTFOLIO_DELETED_MESSAGE + " Status - {}", Constant.OK);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.ID_NOT_FOUND_MESSAGE);
					log.info(Constant.ID_NOT_FOUND_MESSAGE + " Status - {}", Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
				log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + " status - {}", Constant.OK);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

//	@Override
//	public Map<String, Object> getGraph(GraphRequestPayload graphRequestPayload) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			List<TimeSeriesDetails> timeSeriesDetailList = new ArrayList<>();
//			if (graphRequestPayload.getGraphType().equalsIgnoreCase("portfolio")
//					&& graphRequestPayload.getPortfolioId() != 0 && graphRequestPayload.getWalletId() == 0) {
//				List<PortfolioGraphHistory> portfoliotGraphHistoryList = portfolioGraphHistoryRepository
//						.findByPortfolioId(graphRequestPayload.getPortfolioId());
//				if (!portfoliotGraphHistoryList.isEmpty()) {
//					for (PortfolioGraphHistory graphHistory : portfoliotGraphHistoryList) {
//						TimeSeriesDetails timeSeriesDetails = new TimeSeriesDetails();
//						timeSeriesDetails.setClose(String.valueOf(graphHistory.getPrice()));
//						timeSeriesDetails.setDate(DateUtil.convertDateToStringDateTime(graphHistory.getDate()));
//						timeSeriesDetailList.add(timeSeriesDetails);
//					}
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
//					map.put(Constant.DATA, timeSeriesDetailList);
//					log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
//					map.put(Constant.DATA, timeSeriesDetailList);
//					log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + "! status - {}", Constant.OK);
//				}
//			} else if (graphRequestPayload.getGraphType().equalsIgnoreCase("wallet")
//					&& graphRequestPayload.getPortfolioId() == 0 && graphRequestPayload.getWalletId() != 0) {
//				List<WalletGraphHistory> walletGraphHistoryList = walletGraphHistoryRepository
//						.findByWalletId(graphRequestPayload.getWalletId());
//				if (!walletGraphHistoryList.isEmpty()) {
//					for (WalletGraphHistory graphHistory : walletGraphHistoryList) {
//						TimeSeriesDetails timeSeriesDetails = new TimeSeriesDetails();
//						timeSeriesDetails.setClose(String.valueOf(graphHistory.getPrice()));
//						timeSeriesDetails.setDate(DateUtil.convertDateToStringDateTime(graphHistory.getDate()));
//						timeSeriesDetailList.add(timeSeriesDetails);
//					}
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
//					map.put(Constant.DATA, timeSeriesDetailList);
//					log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
//				} else {
//					map.put(Constant.RESPONSE_CODE, Constant.OK);
//					map.put(Constant.MESSAGE, Constant.WALLET_IS_EMPTY_MESSAGE);
//					map.put(Constant.DATA, timeSeriesDetailList);
//					log.info(Constant.WALLET_IS_EMPTY_MESSAGE + "! status - {}", Constant.OK);
//				}
//			}
//		} catch (DataAccessResourceFailureException e) {
//			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
//			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
//			log.error("Exception : " + e.getMessage());
//		} catch (Exception e) {
//			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
//			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
//			log.error(e.getMessage() + "! status - {}", Constant.SERVER_ERROR);
//		}
//		return map;
//	}

	@Override
	public Map<String, Object> getGraph(GraphRequestPayload graphRequestPayload) {
		Map<String, Object> map = new HashMap<>();
		try {
			List<TimeSeriesDetails> timeSeriesDetailList = new ArrayList<>();
			if (graphRequestPayload.getGraphType().equalsIgnoreCase("portfolio")) {
				//
				if (graphRequestPayload.getPortfolioId() == 0 && graphRequestPayload.getWalletId() == 0) {
					User user = userRepository.findByIdAndStatus(graphRequestPayload.getUserId(), Constant.ONE);
					if (user != null) {
						if (user.getPortfolioList() != null && user.getPortfolioList().size() >= 2) {
							List<List<PortfolioGraphHistory>> list = new ArrayList<>();
							for (int i = 1; i < user.getPortfolioList().size(); i++) {
								List<PortfolioGraphHistory> portfolioGraphHistoryList = portfolioGraphHistoryRepository
										.findByPortfolioId(user.getPortfolioList().get(i).getId());
								if (portfolioGraphHistoryList.size() > 0) {
									list.add(portfolioGraphHistoryList);
								} else {
									map.put(Constant.RESPONSE_CODE, Constant.OK);
									map.put(Constant.MESSAGE, Constant.GRAPH_DATA_NOT_AVAILABLE_MESSAGE);
									map.put(Constant.DATA, timeSeriesDetailList);
									log.info(Constant.GRAPH_DATA_NOT_AVAILABLE_MESSAGE + "! status - {}", Constant.OK);
									return map;
								}
							}

							List<PortfolioGraphHistory> list1 = null;
							/* sum of the portFolio */
							for (int i = 1; i < list.size(); i++) {
								list1 = list.get(0);
								List<PortfolioGraphHistory> list2 = list.get(i);
								for (int j = 0; j < list1.size(); j++) {
									TimeSeriesDetails timeSeriesDetails = new TimeSeriesDetails();
									Double price = 0.0;
									PortfolioGraphHistory portfolioGraphHistory = new PortfolioGraphHistory();
									BeanUtils.copyProperties(list1.get(j), portfolioGraphHistory);
									price = portfolioGraphHistory.getPrice();
									timeSeriesDetails.setDate(
											DateUtil.convertDateToStringDateTime(portfolioGraphHistory.getDate()));

									String strDate1 = DateUtil.convertDateToStringDate(portfolioGraphHistory.getDate());
									boolean isPresent = list2.stream().anyMatch(
											obj -> DateUtil.convertDateToStringDate(obj.getDate()).contains(strDate1));
									if (isPresent) {
										price = price + list2.get(j).getPrice();
										timeSeriesDetails.setClose(String.valueOf(price));
									}
									timeSeriesDetails.setClose(String.valueOf(price));
									timeSeriesDetailList.add(timeSeriesDetails);
								}
								list1.removeAll(list1);
								list1 = list2;
							}
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
							map.put(Constant.DATA, timeSeriesDetailList);
							log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
							return map;
						} else {
							map.put(Constant.RESPONSE_CODE, Constant.OK);
							map.put(Constant.MESSAGE, Constant.GRAPH_DATA_NOT_AVAILABLE_MESSAGE);
							map.put(Constant.DATA, timeSeriesDetailList);
							log.info(Constant.GRAPH_DATA_NOT_AVAILABLE_MESSAGE + "! status - {}", Constant.OK);
							return map;
						}
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
						log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
						return map;
					}
				} else if (graphRequestPayload.getPortfolioId() != 0 && graphRequestPayload.getWalletId() == 0) {
					List<PortfolioGraphHistory> portfoliotGraphHistoryList = portfolioGraphHistoryRepository
							.findByPortfolioId(graphRequestPayload.getPortfolioId());
					if (!portfoliotGraphHistoryList.isEmpty()) {
						for (PortfolioGraphHistory graphHistory : portfoliotGraphHistoryList) {
							TimeSeriesDetails timeSeriesDetails = new TimeSeriesDetails();
							timeSeriesDetails.setClose(String.valueOf(graphHistory.getPrice()));
							timeSeriesDetails.setDate(DateUtil.convertDateToStringDateTime(graphHistory.getDate()));
							timeSeriesDetailList.add(timeSeriesDetails);
						}
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
						map.put(Constant.DATA, timeSeriesDetailList);
						log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.PORTFOLIO_IS_EMPTY_MESSAGE);
						map.put(Constant.DATA, timeSeriesDetailList);
						log.info(Constant.PORTFOLIO_IS_EMPTY_MESSAGE + "! status - {}", Constant.OK);
					}
				}
			} else if (graphRequestPayload.getGraphType().equalsIgnoreCase("wallet")
					&& graphRequestPayload.getPortfolioId() == 0 && graphRequestPayload.getWalletId() != 0) {
				List<WalletGraphHistory> walletGraphHistoryList = walletGraphHistoryRepository
						.findByWalletId(graphRequestPayload.getWalletId());
				if (!walletGraphHistoryList.isEmpty()) {
					for (WalletGraphHistory graphHistory : walletGraphHistoryList) {
						TimeSeriesDetails timeSeriesDetails = new TimeSeriesDetails();
						timeSeriesDetails.setClose(String.valueOf(graphHistory.getPrice()));
						timeSeriesDetails.setDate(DateUtil.convertDateToStringDateTime(graphHistory.getDate()));
						timeSeriesDetailList.add(timeSeriesDetails);
					}
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
					map.put(Constant.DATA, timeSeriesDetailList);
					log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.WALLET_IS_EMPTY_MESSAGE);
					map.put(Constant.DATA, timeSeriesDetailList);
					log.info(Constant.WALLET_IS_EMPTY_MESSAGE + "! status - {}", Constant.OK);
				}
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + "! status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

	@Override
	public Map<String, Object> getDoughtChart(Long userId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Double stockHolding = 0d, stockTodayGainPercent = 0d, stockTodayGainValue = 0d, stockPortfolio = 0d;
			Double cryptoHolding = 0d, cryptoTodayGainPercent = 0d, cryptoTodayGainValue = 0d, cryptoPortfolio = 0d;
			DoughNutChartResponse doughNutChartResponse = new DoughNutChartResponse();
			List<DoughNutChart> list = new ArrayList<DoughNutChart>();
			DoughNutChart stockDoughNutChart = new DoughNutChart();
			DoughNutChart cryptoDoughNutChart = new DoughNutChart();
			if (userId != null && userId != 0) {
				User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
				if (user != null) {
					if (!user.getPortfolioList().isEmpty()) {
						for (int i = 1; i < user.getPortfolioList().size(); i++) {
							for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
								if (wallet.getStatus().equals(Constant.ONE)) {
									/* check US & Saudi */
									if (wallet.getType().equalsIgnoreCase("Stock")) {
										stockHolding = stockHolding + wallet.getAssetsList().size();
										stockTodayGainPercent = stockTodayGainPercent + wallet.getDailyPercentageGain();
										stockTodayGainValue = stockTodayGainValue + wallet.getDailyPriceGain();
										stockPortfolio = stockPortfolio + wallet.getLatestTotalBalance();
										/* check Crypto */
									} else if (wallet.getType().equalsIgnoreCase("crypto")) {
										cryptoHolding = cryptoHolding + wallet.getAssetsList().size();
										cryptoTodayGainPercent = cryptoTodayGainPercent
												+ wallet.getDailyPercentageGain();
										cryptoTodayGainValue = cryptoTodayGainValue + wallet.getDailyPriceGain();
										cryptoPortfolio = cryptoPortfolio + wallet.getLatestTotalBalance();
									}
								}
							}
						}
						/* set stock */

						stockDoughNutChart.setType("Stock");
						stockDoughNutChart.setHoldingCount(String.valueOf(stockHolding));
						stockDoughNutChart.setTodayGainPercent(String.valueOf(stockTodayGainPercent));
						stockDoughNutChart.setTodayGainValue(String.valueOf(stockTodayGainValue));
						stockDoughNutChart.setPortfolio(String.valueOf(stockPortfolio));
						/* set crypto */
						cryptoDoughNutChart.setType("Crypto");
						cryptoDoughNutChart.setHoldingCount(String.valueOf(cryptoHolding));
						cryptoDoughNutChart.setTodayGainPercent(String.valueOf(cryptoTodayGainPercent));
						cryptoDoughNutChart.setTodayGainValue(String.valueOf(cryptoTodayGainValue));
						cryptoDoughNutChart.setPortfolio(String.valueOf(cryptoPortfolio));
						/* set dought list */
						list.add(stockDoughNutChart);
						list.add(cryptoDoughNutChart);
						/* set totol portfolio & gain */
						doughNutChartResponse
								.setTotalGainPercent(String.valueOf(stockTodayGainPercent + cryptoTodayGainPercent));
						doughNutChartResponse
								.setTotalGainValue(String.valueOf(stockTodayGainValue + cryptoTodayGainValue));
						doughNutChartResponse.setTotalPortfolio(String.valueOf(stockPortfolio + cryptoPortfolio));
						doughNutChartResponse.setDoughNutChartList(list);
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
						map.put(Constant.DATA, doughNutChartResponse);
						log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.NOT_FOUND);
						map.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
						map.put(Constant.DATA, doughNutChartResponse);
						log.info(Constant.DATA_NOT_FOUND_MESSAGE + "! status - {}", Constant.NOT_FOUND);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.NOT_FOUND);
					map.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
					map.put(Constant.DATA, user);
					log.info(Constant.DATA_NOT_FOUND_MESSAGE + "! status - {}", Constant.NOT_FOUND);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				map.put(Constant.MESSAGE, Constant.USER_ID_CANT_NULL_MESSAGE);
				map.put(Constant.DATA, userId);
				log.info(Constant.USER_ID_CANT_NULL_MESSAGE + "! status - {}", Constant.BAD_REQUEST);
			}
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + "! status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

	@Override
	public Map<String, Object> getPortfolioGainerAndLoser(GainerLoserRequestPayload gainerLoserRequestPayload) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<AssetsRes2> assetList = new ArrayList<AssetsRes2>();
		try {
			User user = userRepository.findByIdAndStatus(gainerLoserRequestPayload.getUserId(), Constant.ONE);
			if (user != null) {
				if (!user.getPortfolioList().isEmpty()) {
					if (gainerLoserRequestPayload.getPortfolioId() != 0) {
						for (int i = 1; i < user.getPortfolioList().size(); i++) {
							for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
								if (wallet.getPortfolioIdFk().equals(gainerLoserRequestPayload.getPortfolioId())) {
									for (Assets assets : wallet.getAssetsList()) {
										AssetsRes2 assetsRes2 = new AssetsRes2();
										if (assets.getStock() != null) {
											assetsRes2.setSymbol(!assets.getStock().getSymbol().isBlank()
													? assets.getStock().getSymbol()
													: null);
											assetsRes2.setClosePrice(assets.getStock().getPrice() != null
													? Double.valueOf(assets.getStock().getPrice())
													: 0.0d);
											assetsRes2.setPriceGain(assets.getStock().getPrice_change() != null
													? Double.valueOf(assets.getStock().getPrice_change())
													: 0.0d);
											assetsRes2.setPricePercentageGain(
													assets.getStock().getPercent_change() != null
															? Double.valueOf(assets.getStock().getPercent_change())
															: 0.0d);
											assetsRes2.setLogo(!assets.getStock().getStockProfile().getLogo().isBlank()
													? assets.getStock().getStockProfile().getLogo()
													: null);
											assetsRes2.setQuantity(
													assets.getQuantity() != null ? assets.getQuantity() : 0.0d);
											assetsRes2.setTotalPrice(
													assets.getTotalPrice() != null ? assets.getTotalPrice() : 0.0d);
											assetsRes2.setExchange(!assets.getStock().getExchange().isBlank()
													? assets.getStock().getExchange()
													: null);
											assetsRes2.setInstrumentId(
													assets.getStock().getId() != null ? assets.getStock().getId()
															: null);
											assetsRes2.setInstrumentType(
													!assets.getInstrumentType().isBlank() ? assets.getInstrumentType()
															: null);
											assetsRes2.setCurrency(!assets.getStock().getCurrency().isBlank()
													? assets.getStock().getCurrency()
													: null);
										} else if (assets.getCrypto() != null) {

											String price = "0";
											String cryptoDetailsResponse = "";
											if (assets.getCrypto() != null) {
												cryptoDetailsResponse = thirdPartyApiUtil
														.getCryptoDetails(assets.getCrypto().getCryptoId());
											}
											if (!cryptoDetailsResponse.isBlank()) {
												log.info("data found ! status - {}", cryptoDetailsResponse);
												/* using objectMapper */
												ObjectMapper mapper = new ObjectMapper();
												mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
														false);
												/* get CryptoMarketDetails */
												Map<?, ?> mapResponse = mapper.readValue(cryptoDetailsResponse,
														Map.class);

												TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
												data = mapper.convertValue(mapResponse, TsCryptoDetailsResponse.class);
												log.info("data found ! status - {}", data);

												/* save market data in third party apis */
												for (TsPrice cryptoListPrice : data.getData().getMarket_data()
														.getPrice()) {
													/* CryptoMarketDetails */
													if (cryptoListPrice.getPrice_latest() == null) {
														price = "0";
													} else {
														price = methodUtil
																.formattedValues(cryptoListPrice.getPrice_latest());
													}
												}
											}

											assetsRes2.setSymbol(!assets.getCrypto().getCryptoId().isBlank()
													? assets.getCrypto().getCryptoId()
													: null);
											assetsRes2.setClosePrice(assets.getCrypto() != null ? Double.valueOf(price)
													: assets.getTotalPrice());
											assetsRes2.setPriceGain(assets.getPriceGain() != null
													? Double.valueOf(assets.getPriceGain())
													: 0.0d);
											assetsRes2.setPricePercentageGain(assets.getPricePercentageGain() != null
													? Double.valueOf(assets.getPricePercentageGain())
													: 0.0d);
											assetsRes2.setLogo(!assets.getCrypto().getLogo().isBlank()
													? assets.getCrypto().getLogo()
													: null);
											assetsRes2.setPriceGain(
													assets.getPriceGain() != null ? assets.getPriceGain() : 0.0d);
											assetsRes2.setPricePercentageGain(assets.getPricePercentageGain() != null
													? assets.getPricePercentageGain()
													: 0.0d);
											assetsRes2.setQuantity(
													assets.getQuantity() != null ? assets.getQuantity() : 0.0d);
											assetsRes2.setTotalPrice(
													assets.getTotalPrice() != null ? assets.getTotalPrice() : 0.0d);
											assetsRes2.setExchange(null);
											assetsRes2.setInstrumentId(
													assets.getCrypto().getId() != null ? assets.getCrypto().getId()
															: null);
											assetsRes2
													.setInstrumentType(!assets.getCrypto().getInstrumentType().isBlank()
															? assets.getCrypto().getInstrumentType()
															: null);
											assetsRes2.setCurrency(!assets.getCrypto().getCurrency().isBlank()
													? assets.getCrypto().getCurrency()
													: null);
										}

										if (assetsRes2.getSymbol() != null) {
											if (gainerLoserRequestPayload.getType()
													.equalsIgnoreCase(assets.getInstrumentType())
													|| gainerLoserRequestPayload.getType()
															.equalsIgnoreCase(assets.getInstrumentType())) {
												assetList.add(assetsRes2);
											} else if (gainerLoserRequestPayload.getType()
													.equalsIgnoreCase(assets.getInstrumentType())) {
												assetList.add(assetsRes2);
											} else if (gainerLoserRequestPayload.getType().equalsIgnoreCase("")) {
												assetList.add(assetsRes2);
											}
										}

//										if (gainerLoserRequestPayload.getType()
//												.equalsIgnoreCase(assets.getInstrumentType())
//												|| gainerLoserRequestPayload.getType()
//														.equalsIgnoreCase(assets.getInstrumentType())) {
//											assetList.add(assetsRes2);
//										} else if (gainerLoserRequestPayload.getType()
//												.equalsIgnoreCase(assets.getInstrumentType())) {
//											assetList.add(assetsRes2);
//										} else if (gainerLoserRequestPayload.getType().equalsIgnoreCase("")) {
//											assetList.add(assetsRes2);
//										}
									}
								}
							}
						}
					} else {
						for (int i = 1; i < user.getPortfolioList().size(); i++) {
							for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
								for (Assets assets : wallet.getAssetsList()) {
									AssetsRes2 assetsRes2 = new AssetsRes2();
									if (assets.getStock() != null) {
										assetsRes2.setSymbol(
												!assets.getStock().getSymbol().isBlank() ? assets.getStock().getSymbol()
														: null);
										assetsRes2.setClosePrice(assets.getStock().getPrice() != null
												? Double.valueOf(assets.getStock().getPrice())
												: 0.0d);
										assetsRes2.setPriceGain(assets.getStock().getPrice_change() != null
												? Double.valueOf(assets.getStock().getPrice_change())
												: 0.0d);
										assetsRes2.setPricePercentageGain(assets.getStock().getPercent_change() != null
												? Double.valueOf(assets.getStock().getPercent_change())
												: 0.0d);
										assetsRes2.setLogo(!assets.getStock().getStockProfile().getLogo().isBlank()
												? assets.getStock().getStockProfile().getLogo()
												: null);
										assetsRes2.setQuantity(
												assets.getQuantity() != null ? assets.getQuantity() : 0.0d);
										assetsRes2.setTotalPrice(
												assets.getTotalPrice() != null ? assets.getTotalPrice() : 0.0d);
										assetsRes2.setExchange(!assets.getStock().getExchange().isBlank()
												? assets.getStock().getExchange()
												: null);
										assetsRes2.setInstrumentId(
												assets.getStock().getId() != null ? assets.getStock().getId() : null);
										assetsRes2.setInstrumentType(
												!assets.getInstrumentType().isBlank() ? assets.getInstrumentType()
														: null);
										assetsRes2.setCurrency(!assets.getStock().getCurrency().isBlank()
												? assets.getStock().getCurrency()
												: null);
									} else if (assets.getCrypto() != null) {

										String price = "0";
										String cryptoDetailsResponse = "";
										if (assets.getCrypto() != null) {
											cryptoDetailsResponse = thirdPartyApiUtil
													.getCryptoDetails(assets.getCrypto().getCryptoId());
										}
										if (!cryptoDetailsResponse.isBlank()) {
											log.info("data found ! status - {}", cryptoDetailsResponse);
											/* using objectMapper */
											ObjectMapper mapper = new ObjectMapper();
											mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
											/* get CryptoMarketDetails */
											Map<?, ?> mapResponse = mapper.readValue(cryptoDetailsResponse, Map.class);

											TsCryptoDetailsResponse data = new TsCryptoDetailsResponse();
											data = mapper.convertValue(mapResponse, TsCryptoDetailsResponse.class);
											log.info("data found ! status - {}", data);

											/* save market data in third party apis */
											for (TsPrice cryptoListPrice : data.getData().getMarket_data().getPrice()) {
												/* CryptoMarketDetails */
												if (cryptoListPrice.getPrice_latest() == null) {
													price = "0";
												} else {
													price = methodUtil
															.formattedValues(cryptoListPrice.getPrice_latest());
												}
											}
										}

										assetsRes2.setSymbol(!assets.getCrypto().getCryptoId().isBlank()
												? assets.getCrypto().getCryptoId()
												: null);
										assetsRes2.setClosePrice(assets.getCrypto() != null ? Double.valueOf(price)
												: assets.getTotalPrice());
										assetsRes2.setPriceGain(
												assets.getPriceGain() != null ? Double.valueOf(assets.getPriceGain())
														: 0.0d);
										assetsRes2.setPricePercentageGain(assets.getPricePercentageGain() != null
												? Double.valueOf(assets.getPricePercentageGain())
												: 0.0d);
										assetsRes2.setLogo(
												!assets.getCrypto().getLogo().isBlank() ? assets.getCrypto().getLogo()
														: null);
										assetsRes2.setPriceGain(
												assets.getPriceGain() != null ? assets.getPriceGain() : 0.0d);
										assetsRes2.setPricePercentageGain(assets.getPricePercentageGain() != null
												? assets.getPricePercentageGain()
												: 0.0d);
										assetsRes2.setQuantity(
												assets.getQuantity() != null ? assets.getQuantity() : 0.0d);
										assetsRes2.setTotalPrice(
												assets.getTotalPrice() != null ? assets.getTotalPrice() : 0.0d);
										assetsRes2.setExchange(null);
										assetsRes2.setInstrumentId(
												assets.getCrypto().getId() != null ? assets.getCrypto().getId() : null);
										assetsRes2.setInstrumentType(!assets.getCrypto().getInstrumentType().isBlank()
												? assets.getCrypto().getInstrumentType()
												: null);
										assetsRes2.setCurrency(!assets.getCrypto().getCurrency().isBlank()
												? assets.getCrypto().getCurrency()
												: null);
									}

									if (assetsRes2.getSymbol() != null) {
										if (gainerLoserRequestPayload.getType()
												.equalsIgnoreCase(assets.getInstrumentType())
												|| gainerLoserRequestPayload.getType()
														.equalsIgnoreCase(assets.getInstrumentType())) {
											assetList.add(assetsRes2);
										} else if (gainerLoserRequestPayload.getType()
												.equalsIgnoreCase(assets.getInstrumentType())) {
											assetList.add(assetsRes2);
										} else if (gainerLoserRequestPayload.getType().equalsIgnoreCase("")) {
											assetList.add(assetsRes2);
										}
									}

//									if (gainerLoserRequestPayload.getType().equalsIgnoreCase(assets.getInstrumentType())
//											|| gainerLoserRequestPayload.getType()
//													.equalsIgnoreCase(assets.getInstrumentType())) {
//										assetList.add(assetsRes2);
//									} else if (gainerLoserRequestPayload.getType()
//											.equalsIgnoreCase(assets.getInstrumentType())) {
//										assetList.add(assetsRes2);
//									} else if (gainerLoserRequestPayload.getType().equalsIgnoreCase("")) {
//										assetList.add(assetsRes2);
//									}
								}
							}
						}
					}

					if (gainerLoserRequestPayload.getDirection().equalsIgnoreCase(Constant.GAINERS)) {
						// Sort assetList based on the 'priceGain' parameter in descending order
						Collections.sort(assetList,
								Comparator.comparingDouble(AssetsRes2::getPricePercentageGain).reversed());
					} else if (gainerLoserRequestPayload.getDirection().equalsIgnoreCase(Constant.LOSERS)) {
						// Sort assetList based on the 'priceGain' parameter in ascending order
						Collections.sort(assetList, Comparator.comparingDouble(AssetsRes2::getPricePercentageGain));
					}

					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
					map.put(Constant.DATA, assetList);
					log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
					map.put(Constant.DATA, user.getPortfolioList());
					log.info(Constant.DATA_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.OK);
				map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
				map.put(Constant.DATA, user);
				log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
			}

		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + "! status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

	@Override
	public Map<String, Object> enableAvailableCash(Long portfolioId, boolean isAvailableCashEnabled) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (portfolioId != null && portfolioId != 0) {
				Portfolio portfolio = portfolioRepository.findById(portfolioId).orElse(null);
				if (portfolio != null) {
					portfolio.setIsAvailableCashEnabled(isAvailableCashEnabled);
					portfolio.setUpdationDate(new Date());
					portfolioRepository.save(portfolio);
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					if (isAvailableCashEnabled == true) {
						map.put(Constant.MESSAGE, Constant.ENABLED_SUCCESSFULLY_MESSAGE);
						log.info(Constant.ENABLED_SUCCESSFULLY_MESSAGE + "! status - {}", Constant.OK);
					} else {
						map.put(Constant.MESSAGE, Constant.DISABLED_SUCCESSFULLY_MESSAGE);
						log.info(Constant.DISABLED_SUCCESSFULLY_MESSAGE + "! status - {}", Constant.OK);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE);
					log.info(Constant.PORTFOLIO_ID_NOT_FOUND_MESSAGE + "! status - {}", Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				map.put(Constant.MESSAGE, Constant.ID_CANT_NULL_OR_ZERO_MESSAGE);
				log.info(Constant.ID_CANT_NULL_OR_ZERO_MESSAGE + "! status - {}", Constant.BAD_REQUEST);
			}
		} catch (DataAccessResourceFailureException e) {
			map.put(Constant.RESPONSE_CODE, Constant.DB_CONNECTION_ERROR);
			map.put(Constant.MESSAGE, Constant.NO_DB_SERVER_CONNECTION);
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + "! status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

	@Override
	public Map<String, Object> getMostActiveAssets(MostActiveRequestPayload mostActiveRequestPayload) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<AssetsRes2> assetRes2List = new ArrayList<AssetsRes2>();
			if (mostActiveRequestPayload != null) {
				User user = userRepository.findByIdAndStatus(mostActiveRequestPayload.getUserId(), Constant.ONE);
				if (user != null) {
					Page<Object> page = null;
					if (mostActiveRequestPayload.getType().isBlank()) {
						page = portfolioRepository.getAllMostActiveAssets(mostActiveRequestPayload.getUserId(),
								PageRequest.of(mostActiveRequestPayload.getPageIndex(),
										mostActiveRequestPayload.getPageSize()));
					} else if (mostActiveRequestPayload.getType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
							|| mostActiveRequestPayload.getType().equalsIgnoreCase(Constant.UNITED_STATES)) {
						page = portfolioRepository.getMostActiveAssetsCountryWise(mostActiveRequestPayload.getUserId(),
								mostActiveRequestPayload.getType(),
								PageRequest.of(mostActiveRequestPayload.getPageIndex(),
										mostActiveRequestPayload.getPageSize()));
					}
					else {
						map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
						map.put(Constant.MESSAGE, Constant.INVALID_MARKET_TYPE_MESSAGE);
						log.info(Constant.INVALID_MARKET_TYPE_MESSAGE + "! status - {}", Constant.OK);
						return map;
					}
					if (page != null && page.getContent().size() > Constant.ZERO) {
						List<Object> rawObjectList = page.getContent();
						for (int i = 0; i < rawObjectList.size(); i++) {
							AssetsRes2 assetsRes2 = new AssetsRes2();
							if (rawObjectList.get(i) instanceof Object[]) {
								Object[] response = (Object[]) rawObjectList.get(i);
								assetsRes2.setInstrumentType((String) response[2]);
								assetsRes2.setPriceGain((Double) response[4]);
								assetsRes2.setPricePercentageGain((Double) response[5]);
								assetsRes2.setQuantity((Double) response[6]);
								assetsRes2.setSymbol((String) response[8]);
								assetsRes2.setInstrumentId(((BigInteger) response[13]).longValueExact());
								assetsRes2.setExchange((String) response[14]);
								assetsRes2.setLogo((String) response[16]);
								assetsRes2.setCurrency((String) response[17]);
								assetsRes2.setTotalPrice((Float) response[18] * (Double) response[6]);
								assetRes2List.add(assetsRes2);
							} else {
								throw new IllegalArgumentException(
										"Expected a array but received " + rawObjectList.getClass());
							}
						}
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
						map.put(Constant.DATA, assetRes2List);
						log.info(Constant.DATA_FOUND_MESSAGE + "! status - {}", Constant.OK);
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.DATA_NOT_FOUND_MESSAGE);
						map.put(Constant.DATA, assetRes2List);
						log.info(Constant.DATA_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
					}
				} else {
					map.put(Constant.RESPONSE_CODE, Constant.OK);
					map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
					log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " - status {} " + Constant.OK);
				}
			} else {
				map.put(Constant.RESPONSE_CODE, Constant.BAD_REQUEST);
				map.put(Constant.MESSAGE, Constant.BAD_REQUEST_MESSAGE);
				log.info(Constant.BAD_REQUEST_MESSAGE + "! status - {}", Constant.BAD_REQUEST);
			}
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.info(e.getMessage() + "! status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

}
