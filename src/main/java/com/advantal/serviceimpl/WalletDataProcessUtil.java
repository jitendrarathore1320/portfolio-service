package com.advantal.serviceimpl;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.advantal.model.Assets;
import com.advantal.model.Portfolio;
import com.advantal.model.PortfolioGraphHistory;
import com.advantal.model.Wallet;
import com.advantal.model.WalletGraphHistory;
import com.advantal.repository.AssetsRepository;
import com.advantal.repository.PortfolioGraphHistoryRepository;
import com.advantal.repository.PortfolioRepository;
import com.advantal.repository.WalletGraphHistoryRepository;
import com.advantal.repository.WalletRepository;
import com.advantal.responsepayload.AssetsRes;
import com.advantal.responsepayload.AssetsRes2;
import com.advantal.responsepayload.TickerDetail;
import com.advantal.responsepayload.TsCryptoDetailsResponse;
import com.advantal.responsepayload.TsPrice;
import com.advantal.responsepayload.WalletBalance;
import com.advantal.responsepayload.WalletResponse;
import com.advantal.utils.Constant;
import com.advantal.utils.MethodUtil;
import com.advantal.utils.ThirdPartyApiUtil;
import com.advantal.utils.UtilityMethods;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WalletDataProcessUtil {

	@Autowired
	private PortfolioRepository portfolioRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private Executor executor;

//	public static Long oldDataCount = 0L;

	@Autowired
	private AssetsRepository assetsRepository;

	@Autowired
	private WalletGraphHistoryRepository walletGraphHistoryRepository;

	@Autowired
	private PortfolioGraphHistoryRepository portfolioGraphHistoryRepository;

	@Autowired
	private ThirdPartyApiUtil thirdPartyApiUtil;

	@Autowired(required = true)
	MethodUtil methodUtil;

	/* update wallet hourly */
	public void hourlySyncWallet() {
		try {
			Integer pageIndex = 0;
			final Integer pageSize = 100;
			Page<Wallet> page = null;
			Calendar cal = Calendar.getInstance();
			Date olderDateObj = null;
			cal.add(Calendar.MINUTE, -15);
			olderDateObj = cal.getTime();

			page = walletRepository.findByAutoSyncWalletAtBefore(olderDateObj, PageRequest.of(pageIndex, pageSize));
			if (page.getContent().size() > 0) {
				log.info("records for page " + pageIndex + " is " + page.getContent().size());
				while (page.hasNext()) {
					WalletTask task = new WalletTask(page);
					executor.execute(task);
					pageIndex = pageIndex + 1;
					page = walletRepository.findByAutoSyncWalletAtBefore(olderDateObj,
							PageRequest.of(pageIndex, pageSize));
					log.info("records for page " + pageIndex + " is " + page.getContent().size());
				}
				WalletTask task = new WalletTask(page);
				executor.execute(task);
			}
		} catch (JsonSyntaxException e) {
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	private class WalletTask implements Runnable {
		private Page<Wallet> page;

		WalletTask(Page<Wallet> page) {
			super();
			this.page = page;
		}

		@Override
		public void run() {
			hourlySyncWallet(page);
		}
	}

	public void hourlySyncWallet(Page<Wallet> page) {
		try {
			String apiResponse = "";
			List<Wallet> oldList = new ArrayList<>();
			Long oldDataCount = 0L;
			oldList = page.getContent();
			for (Wallet wallet : oldList) {
				//
				Double totalBalance = 0.0, totalPriceGain = 0.0, totalPercentageGain = 0.0, dailyPriceGain = 0.0,
						dailyPercentageGain = 0.0;
				if (wallet.getApiKey() != null && wallet.getApiSecret() != null) {
					apiResponse = ThirdPartyApiUtil.getBinanceAccountInfo(wallet.getApiKey(), wallet.getApiSecret());
					if (!apiResponse.isEmpty()) {
						List<WalletBalance> walletList = new ArrayList<>();
						try {
							Type collectionType = new TypeToken<List<WalletBalance>>() {
							}.getType();
							walletList = new Gson().fromJson(apiResponse, collectionType);
						} catch (Exception e) {
							log.info(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
						}

						if (!walletList.isEmpty()) {
							for (WalletBalance walletBalance : walletList) {
								totalBalance = totalBalance + Double.valueOf(walletBalance.getBalance());
							}

							apiResponse = ThirdPartyApiUtil.getBtcTickerPrice(wallet.getApiKey(),
									wallet.getApiSecret());
							if (!apiResponse.isBlank()) {
								TickerDetail tickerDetails = new TickerDetail();
								try {
									Type type = new TypeToken<TickerDetail>() {
									}.getType();
									tickerDetails = new Gson().fromJson(apiResponse, type);
								} catch (Exception e) {
									log.info(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
								}
								if (tickerDetails != null) {
//									totalBalance = UtilityMethods.convertBtcToUSD(totalBalance, tickerDetails);

									//
									for (Assets assets : wallet.getAssetsList()) {
										totalBalance = totalBalance + assets.getTotalPrice();
										dailyPriceGain = dailyPriceGain + assets.getPriceGain();
										dailyPercentageGain = dailyPercentageGain + assets.getPricePercentageGain();

									}
									//

									totalPriceGain = totalBalance - wallet.getPrevTotalBalance();
									totalPercentageGain = ((totalPriceGain / wallet.getPrevTotalBalance()) * 100);
//									dailyPriceGain = totalBalance - wallet.getDailyOpeningBalance();
//									dailyPercentageGain = ((dailyPriceGain / wallet.getDailyOpeningBalance()) * 100);

									wallet.setTotalPriceGain(totalPriceGain);
									wallet.setTotalPercentageGain(totalPercentageGain);
									wallet.setDailyPriceGain(dailyPriceGain);
									wallet.setDailyPercentageGain(dailyPercentageGain);

									wallet.setLatestTotalBalance(totalBalance);
									wallet.setAutoSyncWalletAt(new Date());
									walletRepository.save(wallet);
									oldDataCount++;
									log.info(oldDataCount + ": Wallet update successfully! | User id is - "
											+ wallet.getId() + " | status - {}", Constant.OK);
								} else {
									log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
								}
							} else {
								log.error(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}",
										Constant.SERVER_ERROR);
							}
						} else {
							log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
						}
					} else {
						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
					}
				} else {
					for (Assets assets : wallet.getAssetsList()) {
						totalBalance = totalBalance + assets.getTotalPrice();
						dailyPriceGain = dailyPriceGain + assets.getPriceGain();
						dailyPercentageGain = dailyPercentageGain + assets.getPricePercentageGain();

					}
//					totalBalance = wallet.getLatestTotalBalance();
					totalPriceGain = totalBalance - wallet.getPrevTotalBalance();
					totalPercentageGain = ((totalPriceGain / wallet.getPrevTotalBalance()) * 100);
//					dailyPriceGain = totalBalance - wallet.getDailyOpeningBalance();
//					dailyPercentageGain = ((dailyPriceGain / wallet.getDailyOpeningBalance()) * 100);

					wallet.setTotalPriceGain(totalPriceGain);
					wallet.setTotalPercentageGain(totalPercentageGain);
					wallet.setDailyPriceGain(dailyPriceGain);
					wallet.setDailyPercentageGain(dailyPercentageGain);

					wallet.setLatestTotalBalance(totalBalance);
					wallet.setAutoSyncWalletAt(new Date());
					walletRepository.save(wallet);
					oldDataCount++;
					log.info(oldDataCount + ": Wallet update successfully! status - {}", Constant.OK);
				}
				//
			}
			log.info("Total Data Processed! " + "[ Total Wallet Updated : " + oldDataCount + " ] ! status - {}",
					Constant.OK);
			if (page.isLast()) {
				oldDataCount = 0L;
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	/* update port_folio in 24 hour */
	public void dailySyncWallet() {
		try {
			Integer pageIndex = 0;
			final Integer pageSize = 100;
			Page<Wallet> page = null;
			Calendar cal = Calendar.getInstance();
			Date olderDateObj = null;
			cal.add(Calendar.HOUR, -1);
			olderDateObj = cal.getTime();

//			page = walletRepository.findByOpenTimeBefore(olderDateObj, PageRequest.of(pageIndex, pageSize));
			page = walletRepository.findByAutoSyncWalletAtBefore(olderDateObj, PageRequest.of(pageIndex, pageSize));
			if (page.getContent().size() > 0) {
				log.info("records for page " + pageIndex + " is " + page.getContent().size());
				while (page.hasNext()) {
					DailyWalletTask task = new DailyWalletTask(page);
					executor.execute(task);
					pageIndex = pageIndex + 1;
//					page = walletRepository.findByOpenTimeBefore(olderDateObj, PageRequest.of(pageIndex, pageSize));
					page = walletRepository.findByAutoSyncWalletAtBefore(olderDateObj,
							PageRequest.of(pageIndex, pageSize));
					log.info("records for page " + pageIndex + " is " + page.getContent().size());
				}
				DailyWalletTask task = new DailyWalletTask(page);
				executor.execute(task);
			}
		} catch (JsonSyntaxException e) {
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	private class DailyWalletTask implements Runnable {
		private Page<Wallet> page;

		DailyWalletTask(Page<Wallet> page) {
			super();
			this.page = page;
		}

		@Override
		public void run() {
			dailySyncWallet(page);
		}
	}

	public void dailySyncWallet(Page<Wallet> page) {
		try {
			String apiResponse = "";
			List<Wallet> oldList = new ArrayList<>();
			oldList = page.getContent();
			Long oldDataCount = 0L;
			for (Wallet wallet : oldList) {
				//
				Double totalBalance = 0.0, totalPriceGain = 0.0, totalPercentageGain = 0.0, dailyPriceGain = 0.0,
						dailyPercentageGain = 0.0;
//				if (wallet.getApiKey() != null && wallet.getApiSecret() != null) {
				if ((wallet.getApiKey() != null && !wallet.getApiKey().isEmpty())
						&& (wallet.getApiSecret() != null && !wallet.getApiSecret().isEmpty())) {
					apiResponse = ThirdPartyApiUtil.getBinanceAccountInfo(wallet.getApiKey(), wallet.getApiSecret());
					if (!apiResponse.isEmpty()) {
						List<WalletBalance> walletList = new ArrayList<>();
						try {
							Type collectionType = new TypeToken<List<WalletBalance>>() {
							}.getType();
							walletList = new Gson().fromJson(apiResponse, collectionType);
						} catch (Exception e) {
							log.info(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
						}
						if (!walletList.isEmpty()) {
//							Double totalBalance = 0.0;
//							for (WalletBalance walletBalance : walletList) {
//								totalBalance = totalBalance + Double.valueOf(walletBalance.getBalance());
//							}
							apiResponse = ThirdPartyApiUtil.getBtcTickerPrice(wallet.getApiKey(),
									wallet.getApiSecret());
							if (!apiResponse.isBlank()) {
								TickerDetail tickerDetails = new TickerDetail();
								try {
									Type type = new TypeToken<TickerDetail>() {
									}.getType();
									tickerDetails = new Gson().fromJson(apiResponse, type);
								} catch (Exception e) {
									log.info(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
								}
								if (tickerDetails != null) {
//									totalBalance = UtilityMethods.convertBtcToUSD(totalBalance, tickerDetails);
//									wallet.setDailyOpeningBalance(totalBalance);
//									wallet.setAutoSyncWalletAt(new Date());
//									wallet.setOpenTime(new Date());
//									walletRepository.save(wallet);

									//
									for (Assets assets : wallet.getAssetsList()) {
										totalBalance = totalBalance + assets.getTotalPrice();
										dailyPriceGain = dailyPriceGain + assets.getPriceGain();
										dailyPercentageGain = dailyPercentageGain + assets.getPricePercentageGain();

									}
									totalPriceGain = totalBalance - wallet.getPrevTotalBalance();
									totalPercentageGain = ((totalPriceGain / wallet.getPrevTotalBalance()) * 100);
//									dailyPriceGain = totalBalance - wallet.getDailyOpeningBalance();
//									dailyPercentageGain = ((dailyPriceGain / wallet.getDailyOpeningBalance()) * 100);

									wallet.setTotalPriceGain(totalPriceGain);
									wallet.setTotalPercentageGain(totalPercentageGain);
									wallet.setDailyPriceGain(dailyPriceGain);
									wallet.setDailyPercentageGain(dailyPercentageGain);
//									wallet.setDailyOpeningBalance(totalBalance);
									wallet.setLatestTotalBalance(totalBalance);
									wallet.setAutoSyncWalletAt(new Date());
//									wallet.setOpenTime(new Date());
									walletRepository.save(wallet);
									//

									oldDataCount++;
									log.info(oldDataCount + ": Wallet update successfully! status - {}", Constant.OK);
								} else {
									log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
								}
							} else {
								log.error(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + " status - {}",
										Constant.SERVER_ERROR);
							}
						} else {
							log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
						}
					} else {
						log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}", Constant.SERVER_ERROR);
					}
				} else {
					for (Assets assets : wallet.getAssetsList()) {
						totalBalance = totalBalance + assets.getTotalPrice();
						dailyPriceGain = dailyPriceGain + assets.getPriceGain();
						dailyPercentageGain = dailyPercentageGain + assets.getPricePercentageGain();

					}
//					totalBalance = wallet.getLatestTotalBalance();
					totalPriceGain = totalBalance - wallet.getPrevTotalBalance();
					totalPercentageGain = ((totalPriceGain / wallet.getPrevTotalBalance()) * 100);
//					dailyPriceGain = totalBalance - wallet.getDailyOpeningBalance();
//					dailyPercentageGain = ((dailyPriceGain / wallet.getDailyOpeningBalance()) * 100);

					wallet.setTotalPriceGain(totalPriceGain);
					wallet.setTotalPercentageGain(totalPercentageGain);
					wallet.setDailyPriceGain(dailyPriceGain);
					wallet.setDailyPercentageGain(dailyPercentageGain);

					wallet.setLatestTotalBalance(totalBalance);
					wallet.setAutoSyncWalletAt(new Date());
					walletRepository.save(wallet);
					oldDataCount++;
					log.info(oldDataCount + ": Wallet update successfully! status - {}", Constant.OK);
				}
				//

			}
			log.info("Total Data Processed! " + "[ Total Wallet Updated : " + oldDataCount + " ] ! status - {}",
					Constant.OK);
			if (page.isLast()) {
				oldDataCount = 0L;
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	/* update assets in 24 hour */
	public void dailySyncAssets() {
		try {
			Integer pageIndex = 0;
			final Integer pageSize = 3;
			Page<Wallet> page = null;
//			page = assetsRepository.findAll(PageRequest.of(pageIndex, pageSize));
			page = walletRepository.findAll(PageRequest.of(pageIndex, pageSize));
			if (page.getContent().size() > 0) {
				log.info("records for page " + pageIndex + " is " + page.getContent().size());
				while (page.hasNext()) {
					DailyAssetsTask task = new DailyAssetsTask(page);
					executor.execute(task);
					pageIndex = pageIndex + 1;
//					page = assetsRepository.findAll(PageRequest.of(pageIndex, pageSize));
					page = walletRepository.findAll(PageRequest.of(pageIndex, pageSize));
					log.info("records for page " + pageIndex + " is " + page.getContent().size());
				}
				DailyAssetsTask task = new DailyAssetsTask(page);
				executor.execute(task);
			}
		} catch (JsonSyntaxException e) {
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	private class DailyAssetsTask implements Runnable {
		private Page<Wallet> page;

		DailyAssetsTask(Page<Wallet> page) {
			super();
			this.page = page;
		}

		@Override
		public void run() {
			dailySyncAssets(page);
		}
	}

	public void dailySyncAssets(Page<Wallet> page) {
		try {
			String apiResponse = "";
			Long oldDataCount = 0L;
			List<Wallet> oldList = new ArrayList<>();
			Double totalBalance = 0.0;
			oldList = page.getContent();
			for (Wallet wallet : oldList) {
				if (!wallet.getAssetsList().isEmpty()) {
					//
					if ((wallet.getApiKey() != null && !wallet.getApiKey().isEmpty())
							&& (wallet.getApiSecret() != null && !wallet.getApiSecret().isEmpty())) {
						apiResponse = ThirdPartyApiUtil.getAssetList(wallet.getApiKey(), wallet.getApiSecret());
						if (!apiResponse.isEmpty()) {
							List<AssetsRes> assetsResList = new ArrayList<>();
							try {
								Type collectionType = new TypeToken<List<AssetsRes>>() {
								}.getType();
								assetsResList = new Gson().fromJson(apiResponse, collectionType);
							} catch (Exception e) {
								log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
							}
							if (!assetsResList.isEmpty()) {
								List<Assets> oldAssetList = new ArrayList<>();
								List<Assets> tempAssetList = new ArrayList<>();
								List<TickerDetail> tickerDetailsList = new ArrayList<>();
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
									try {
										Type type = new TypeToken<List<TickerDetail>>() {
										}.getType();
										tickerDetailsList = new Gson().fromJson(apiResponse, type);
									} catch (Exception e) {
										log.error(e.getMessage() + " status - {}", Constant.SERVER_ERROR);
									}
									if (!tickerDetailsList.isEmpty()) {
										assetsResList.remove(assetsResList.size() - 1);
										oldAssetList = wallet.getAssetsList();
										for (int i = 0; i < oldAssetList.size(); i++) {
											for (int j = 0; j < assetsResList.size(); j++) {
												Assets assets = new Assets();
												if (assetsResList.get(j).getAsset()
														.equalsIgnoreCase(oldAssetList.get(i).getSymbol())) {
													if (assetsResList.get(j).getAsset()
															.equalsIgnoreCase(tickerDetailsList.get(j).getSymbol())) {
														BeanUtils.copyProperties(oldAssetList.get(i), assets);
														totalBalance = tickerDetailsList.get(j).getLastPrice()
																* Double.valueOf(assetsResList.get(j).getFree());
														Double priceGain = totalBalance
																- assets.getPreviousClosePrice();
														Double pricePercentageGain = ((priceGain
																/ assets.getPreviousClosePrice()) * 100);

														assets.setQuantity(
																Double.valueOf(assetsResList.get(j).getFree()));
														assets.setTotalPrice((tickerDetailsList.get(j).getLastPrice()
																* Double.valueOf(assetsResList.get(j).getFree())));
														assets.setPreviousClosePrice(
																(tickerDetailsList.get(j).getLastPrice() * Double
																		.valueOf(assetsResList.get(j).getFree())));
														assets.setPriceGain(priceGain);
														assets.setPricePercentageGain(pricePercentageGain);
														assets.setUpdationDate(new Date());

														tempAssetList.add(assets);
														assetsResList.remove(j);
														tickerDetailsList.remove(j);
														break;
													}
												}
											}
											oldDataCount++;
										}
										for (int j = 0; j < assetsResList.size(); j++) {
											Assets assets = new Assets();
											assets.setSymbol(assetsResList.get(j).getAsset());
											assets.setQuantity(Double.valueOf(assetsResList.get(j).getFree()));
											assets.setTotalPrice((tickerDetailsList.get(j).getLastPrice()
													* Double.valueOf(assetsResList.get(j).getFree())));
											assets.setPreviousClosePrice((tickerDetailsList.get(j).getLastPrice()
													* Double.valueOf(assetsResList.get(j).getFree())));
											assets.setPriceGain(0.0);
											assets.setPricePercentageGain(0.0);
											assets.setWalletIdFk(wallet.getId());
											assets.setCreationDate(new Date());
											assets.setStatus(Constant.ONE);
											tempAssetList.add(assets);
											oldDataCount++;
										}
										assetsRepository.saveAll(tempAssetList);
//										oldDataCount++;
										log.info(oldDataCount + ": Wallet Assets update successfully!  | status - {}",
												Constant.OK);
									}
								} else {
									log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}",
											Constant.SERVER_ERROR);
								}
							} else {
								log.info(Constant.DATA_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
							}
						} else {
							log.info(Constant.THIRD_PARTY_SERVER_ERROR_MESSAGE + "! status - {}",
									Constant.SERVER_ERROR);
						}
					} else {
						List<Assets> assetsList = new ArrayList<>();
						for (Assets assets : wallet.getAssetsList()) {
							if ((!assets.getInstrumentType().equalsIgnoreCase("crypto"))) {
								totalBalance = Double.valueOf(assets.getStock().getPrice() * assets.getQuantity());
								Double priceGain = totalBalance - assets.getPreviousClosePrice();
								Double pricePercentageGain = ((priceGain / assets.getPreviousClosePrice()) * 100);
								assets.setPreviousClosePrice((assets.getTotalPrice()));
								assets.setTotalPrice(
										Double.valueOf(assets.getStock().getPrice() * assets.getQuantity()));
								assets.setPriceGain(priceGain);
								assets.setPricePercentageGain(pricePercentageGain);
								assets.setUpdationDate(new Date());
								assetsList.add(assets);
								oldDataCount++;
							} else {
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
											price = methodUtil.formattedValues(cryptoListPrice.getPrice_latest());
										}
									}
								}
								totalBalance = Double.valueOf(assets.getStock().getPrice() * assets.getQuantity());
								Double priceGain = totalBalance - assets.getPreviousClosePrice();
								Double pricePercentageGain = ((priceGain / assets.getPreviousClosePrice()) * 100);
								assets.setPreviousClosePrice((assets.getTotalPrice()));
								assets.setTotalPrice(
										assets.getCrypto() != null ? Double.valueOf(price) : assets.getTotalPrice());
								assets.setPriceGain(priceGain);
								assets.setPricePercentageGain(pricePercentageGain);
								assets.setUpdationDate(new Date());
								assetsList.add(assets);
								oldDataCount++;
							}
						}
						assetsRepository.saveAll(assetsList);
//						oldDataCount++;
						log.info(oldDataCount + ": Wallet Assets update successfully!  | status - {}", Constant.OK);
					}
					//
				}
			}
			log.info("Total Data Processed! " + "[ Total Wallet Assets Updated : " + oldDataCount + " ] ! status - {}",
					Constant.OK);
			if (page.isLast()) {
				oldDataCount = 0L;
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	/* create wallet history on daily bases */
	public void saveWalletHistory() {
		try {
			Integer pageIndex = 0;
			final Integer pageSize = 100;
			Page<Wallet> page = null;
			page = walletRepository.findAll(PageRequest.of(pageIndex, pageSize));
			if (page.getContent().size() > 0) {
				log.info("records for page " + pageIndex + " is " + page.getContent().size());
				while (page.hasNext()) {
					WalletHistoryTask task = new WalletHistoryTask(page);
					executor.execute(task);
					pageIndex = pageIndex + 1;
					page = walletRepository.findAll(PageRequest.of(pageIndex, pageSize));
					log.info("records for page " + pageIndex + " is " + page.getContent().size());
				}
				WalletHistoryTask task = new WalletHistoryTask(page);
				executor.execute(task);
			}
		} catch (JsonSyntaxException e) {
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	private class WalletHistoryTask implements Runnable {
		private Page<Wallet> page;

		WalletHistoryTask(Page<Wallet> page) {
			super();
			this.page = page;
		}

		@Override
		public void run() {
			saveWalletHistory(page);
		}
	}

	private void saveWalletHistory(Page<Wallet> page) {
		try {
			Long newDataCount = 0L;
			for (Wallet oldWallet : page.getContent()) {
				WalletGraphHistory walletGraphHistory = new WalletGraphHistory();
				walletGraphHistory.setWalletId(oldWallet.getId());
				walletGraphHistory.setPrice(oldWallet.getLatestTotalBalance());
				walletGraphHistory.setDate(new Date());
				walletGraphHistoryRepository.save(walletGraphHistory);
				newDataCount++;
				log.info(newDataCount + ": Wallet history created successfully! status - {}", Constant.OK);
			}

			LocalDateTime dateTime = LocalDateTime.now();
			dateTime = dateTime.minusMonths(1);
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String cDate = dateTime.format(dateTimeFormatter);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = formatter.parse(cDate);
			List<WalletGraphHistory> walletGraphHistorieList = walletGraphHistoryRepository.findByDateBefore(date);
			if (!walletGraphHistorieList.isEmpty()) {
				walletGraphHistoryRepository.deleteAll(walletGraphHistorieList);
				log.info("Deleted all wallet historical data successfully, before one month! status - {}", Constant.OK);
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	/* create portfolio history on daily bases */
	public void savePortrfolioHistory() {
		try {
			Integer pageIndex = 0;
			final Integer pageSize = 100;
			Page<Portfolio> page = null;
			page = portfolioRepository.findAll(PageRequest.of(pageIndex, pageSize));
			if (page.getContent().size() > 0) {
				log.info("records for page " + pageIndex + " is " + page.getContent().size());
				while (page.hasNext()) {
					PortfolioHistoryTask task = new PortfolioHistoryTask(page);
					executor.execute(task);
					pageIndex = pageIndex + 1;
					page = portfolioRepository.findAll(PageRequest.of(pageIndex, pageSize));
					log.info("records for page " + pageIndex + " is " + page.getContent().size());
				}
				PortfolioHistoryTask task = new PortfolioHistoryTask(page);
				executor.execute(task);
			}
		} catch (JsonSyntaxException e) {
			log.error("Exception : " + e.getMessage());
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

	private class PortfolioHistoryTask implements Runnable {
		private Page<Portfolio> page;

		PortfolioHistoryTask(Page<Portfolio> page) {
			super();
			this.page = page;
		}

		@Override
		public void run() {
			savePortrfolioHistory(page);
		}
	}

	public void savePortrfolioHistory(Page<Portfolio> page) {
		try {
			Long newDataCount = 0L;
//			for (Portfolio oldPortfolio : page.getContent()) {
			for (int i = 1; i < page.getContent().size(); i++) {
				Double totalPrice = 0.0;
				for (Wallet wallet : page.getContent().get(i).getWalletList()) {
					totalPrice = totalPrice + wallet.getLatestTotalBalance();
				}
				PortfolioGraphHistory portfolioGraphHistory = new PortfolioGraphHistory();
				portfolioGraphHistory.setPortfolioId(page.getContent().get(i).getId());
				portfolioGraphHistory.setUserId(page.getContent().get(i).getUserIdFk());
				portfolioGraphHistory.setPrice(totalPrice);
				portfolioGraphHistory.setDate(new Date());
				portfolioGraphHistoryRepository.save(portfolioGraphHistory);
				newDataCount++;
				log.info(newDataCount + ": Portfolio history created successfully! status - {}", Constant.OK);
			}

			LocalDateTime dateTime = LocalDateTime.now();
			dateTime = dateTime.minusMonths(1);
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String cDate = dateTime.format(dateTimeFormatter);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = formatter.parse(cDate);
			List<PortfolioGraphHistory> portfolioGraphHistorieList = portfolioGraphHistoryRepository
					.findByDateBefore(date);
			if (!portfolioGraphHistorieList.isEmpty()) {
				portfolioGraphHistoryRepository.deleteAll(portfolioGraphHistorieList);
				log.info("Deleted all portfolio historical data successfully, before one month! status - {}",
						Constant.OK);
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
	}

}