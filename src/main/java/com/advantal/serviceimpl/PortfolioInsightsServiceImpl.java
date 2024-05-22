package com.advantal.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.advantal.model.Assets;
import com.advantal.model.User;
import com.advantal.model.Wallet;
import com.advantal.repository.UserRepository;
import com.advantal.responsepayload.KeyResponse;
import com.advantal.responsepayload.PortfolioInsightsCards;
import com.advantal.responsepayload.PortfolioInsightsResponse;
import com.advantal.service.PortfolioInsightsService;
import com.advantal.utils.Constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PortfolioInsightsServiceImpl implements PortfolioInsightsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Map<String, Object> getPortfolioInsights(Long userId, String type, String subType) {
		Map<String, Object> map = new HashMap<>();
		try {
			Double stockTotalValue = 0.0, cryptoTotalValue = 0.0;
			PortfolioInsightsCards stockPortfolioInsightsCards = new PortfolioInsightsCards();
			PortfolioInsightsCards cryptoPortfolioInsightsCards = new PortfolioInsightsCards();
			List<PortfolioInsightsCards> insightsCardsList = new ArrayList<PortfolioInsightsCards>();
			PortfolioInsightsResponse portfolioResponseInsightsResponse = new PortfolioInsightsResponse();
			Double stockPercentage = 0.0d, cryptopercentage = 0.0d, totalAssetPrice = 0.0d, exchangePercentage = 0.0d,
					sectorTotalValues = 0.0d;
			KeyResponse keyResponse = new KeyResponse();
			List<KeyResponse> keyResponsesList = new ArrayList<KeyResponse>();
			if (type != null) {
				if (userId != null && userId != 0) {
					User user = userRepository.findByIdAndStatus(userId, Constant.ONE);
					if (user != null) {
						/* get all portFolio sum */
						for (int i = 1; i < user.getPortfolioList().size(); i++) {
							for (Wallet wallet : user.getPortfolioList().get(i).getWalletList()) {
								if (wallet.getStatus().equals(Constant.ONE)) {
									KeyResponse keyRes = new KeyResponse();
									totalAssetPrice = 0.0;
									for (Assets assets : wallet.getAssetsList()) {
										KeyResponse assetKeyResponse = new KeyResponse();
										if (!type.isBlank()) {
											if (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
													|| type.equalsIgnoreCase(Constant.UNITED_STATES)) {
												if (assets.getInstrumentType().equalsIgnoreCase(type)) {
													stockTotalValue = stockTotalValue + assets.getTotalPrice();
													/* ----- portfolio allocation ----- */
													if (subType.equalsIgnoreCase("Exchange")) {
														totalAssetPrice = totalAssetPrice + assets.getTotalPrice();
													} else if (subType.equalsIgnoreCase("Asset")) {
														assetKeyResponse.setKey(assets.getSymbol());
														assetKeyResponse.setValue(assets.getTotalPrice());
														keyResponsesList.add(assetKeyResponse);
													} else if (subType.equalsIgnoreCase("Sector")) {
														if (!keyResponsesList.isEmpty()) {
															for (KeyResponse SectorKeyResponse : keyResponsesList) {
																KeyResponse response = new KeyResponse();
																if (SectorKeyResponse.getKey().equalsIgnoreCase(
																		assets.getStock().getSector())) {
																	keyResponse.setValue(
																			sectorTotalValues + assets.getTotalPrice());
																	keyResponsesList.add(keyResponse);
																	break;
																} else {
																	if (!assets.getStock().getSector().isBlank()) {
																		response.setKey(assets.getStock().getSector());
																		response.setValue(sectorTotalValues
																				+ assets.getTotalPrice());
																		keyResponsesList.add(response);
																		break;
																	}
																}
															}
														} else {
															if (!assets.getStock().getSector().isBlank()) {
																keyResponse.setKey(assets.getStock().getSector());
																keyResponse.setValue(
																		sectorTotalValues + assets.getTotalPrice());
																keyResponsesList.add(keyResponse);
															}
														}
													}
												}
											} else if (type.equalsIgnoreCase(Constant.CRYPTO)) {
												if (assets.getInstrumentType().equalsIgnoreCase(type)) {
													cryptoTotalValue = cryptoTotalValue + assets.getTotalPrice();
													/* ----- portfolio allocation ----- */
													if (subType.equalsIgnoreCase("Exchange")) {
														totalAssetPrice = totalAssetPrice + assets.getTotalPrice();
													} else if (subType.equalsIgnoreCase("Asset")) {
														assetKeyResponse.setKey(assets.getSymbol());
														assetKeyResponse.setValue(assets.getTotalPrice());
														keyResponsesList.add(assetKeyResponse);
													} else if (subType.equalsIgnoreCase("Sector")) {

													}
												}

											}
										} else {
											/* -------- stock --------- */
											if (assets.getInstrumentType().equalsIgnoreCase(Constant.SAUDI_ARABIA)
													|| assets.getInstrumentType()
															.equalsIgnoreCase(Constant.UNITED_STATES)) {
												stockTotalValue = stockTotalValue + assets.getTotalPrice();
											} else {
												/* --------- crypto -------- */
												cryptoTotalValue = cryptoTotalValue + assets.getTotalPrice();
											}
											/* ----- portfolio allocation ----- */
											if (subType.equalsIgnoreCase("Exchange")) {
												totalAssetPrice = totalAssetPrice + assets.getTotalPrice();
											} else if (subType.equalsIgnoreCase("Asset")) {
												assetKeyResponse.setKey(assets.getSymbol());
												assetKeyResponse.setValue(assets.getTotalPrice());
												keyResponsesList.add(assetKeyResponse);
											} else if (subType.equalsIgnoreCase("Sector") && (assets.getInstrumentType()
													.equalsIgnoreCase(Constant.SAUDI_ARABIA)
													|| assets.getInstrumentType()
															.equalsIgnoreCase(Constant.UNITED_STATES))) {
												if (!keyResponsesList.isEmpty()) {
													for (KeyResponse SectorKeyResponse : keyResponsesList) {
														KeyResponse response = new KeyResponse();
														if (SectorKeyResponse.getKey()
																.equalsIgnoreCase(assets.getStock().getSector())) {
//															keyResponse.setKey(assets.getStock().getSector());
															keyResponse.setValue(
																	sectorTotalValues + assets.getTotalPrice());
															keyResponsesList.add(keyResponse);
															break;
														} else {
															if (!assets.getStock().getSector().isBlank()) {
																response.setKey(assets.getStock().getSector());
																response.setValue(
																		sectorTotalValues + assets.getTotalPrice());
																keyResponsesList.add(response);
																break;
															}
														}
													}
												} else {
													if (!assets.getStock().getSector().isBlank()) {
														keyResponse.setKey(assets.getStock().getSector());
														keyResponse
																.setValue(sectorTotalValues + assets.getTotalPrice());
														keyResponsesList.add(keyResponse);
													}
												}
											}
										}
									}
									if (!type.isBlank()) {
										if (wallet.getType().equalsIgnoreCase(type)
												&& subType.equalsIgnoreCase("Exchange")) {
											keyRes.setKey(wallet.getWalletName());
											keyRes.setValue(totalAssetPrice);
											keyResponsesList.add(keyRes);
										}
									} else {
										if (subType.equalsIgnoreCase("Exchange")) {
											keyRes.setKey(wallet.getWalletName());
											keyRes.setValue(totalAssetPrice);
											keyResponsesList.add(keyRes);
										}

									}
								}
							}
						}
						/* -------- stock ---------- */
						stockPortfolioInsightsCards.setTotalValue(stockTotalValue);
						stockPortfolioInsightsCards.setType(Constant.STOCK);
						if (!type.isBlank()) {
							if (type.equalsIgnoreCase(Constant.UNITED_STATES)) {
								stockPortfolioInsightsCards.setCurrency(Constant.USD);
								stockPortfolioInsightsCards.setYtd_high(0.0);
							} else {
								stockPortfolioInsightsCards.setCurrency(Constant.SAR);
								stockPortfolioInsightsCards.setYtd_high(0.0);
							}
						} else {
							stockPortfolioInsightsCards.setCurrency(Constant.USD);
							stockPortfolioInsightsCards.setYtd_high(0.0);
						}
						/* -------- crypto --------- */
						cryptoPortfolioInsightsCards.setTotalValue(cryptoTotalValue);
						cryptoPortfolioInsightsCards.setType(Constant.CRYPTO);
						cryptoPortfolioInsightsCards.setCurrency(Constant.USD);
						cryptoPortfolioInsightsCards.setYtd_high(0.0);

						if (type.equalsIgnoreCase(Constant.CRYPTO)) {
							if (subType.equalsIgnoreCase("Overview")) {
								if(cryptoTotalValue == 0.0 ) {
									cryptopercentage = 0.0;
								}else {
									cryptopercentage = cryptoTotalValue * 100 / cryptoTotalValue;
								}
								keyResponse.setKey("crypto");
								keyResponse.setValue(cryptopercentage);
								keyResponsesList.add(keyResponse);
							} else if (subType.equalsIgnoreCase("Exchange") || subType.equalsIgnoreCase("Asset")) {
								for (KeyResponse keyResponse2 : keyResponsesList) {
									if(keyResponse2.getValue().equals("0.0")) {
										exchangePercentage = 0.0;
									}else {
										exchangePercentage = ((Number) keyResponse2.getValue()).doubleValue() * 100
												/ stockTotalValue;
									}
									keyResponse2.setValue(exchangePercentage);
								}
							}
							insightsCardsList.add(cryptoPortfolioInsightsCards);
						} else if (type.equalsIgnoreCase(Constant.SAUDI_ARABIA)
								|| type.equalsIgnoreCase(Constant.UNITED_STATES)) {
							if (subType.equalsIgnoreCase("Overview")) {
								if(stockTotalValue == 0.0) {
									stockPercentage = 0.0;
								}else {
									stockPercentage = stockTotalValue * 100 / stockTotalValue;
								}
								keyResponse.setKey("stock");
								keyResponse.setValue(stockPercentage);
								keyResponsesList.add(keyResponse);
							} else if (subType.equalsIgnoreCase("Exchange") || subType.equalsIgnoreCase("Asset")) {
								for (KeyResponse keyResponse2 : keyResponsesList) {
									if(keyResponse2.getValue().equals("0.0")) {
										exchangePercentage = 0.0;
									}else {
										exchangePercentage = ((Number) keyResponse2.getValue()).doubleValue() * 100
												/ stockTotalValue;
									}
									keyResponse2.setValue(exchangePercentage);
								}
							}
							insightsCardsList.add(stockPortfolioInsightsCards);
						} else {
							Double totalValues = stockTotalValue + cryptoTotalValue;
							if (subType.equalsIgnoreCase("Overview")) {
								KeyResponse keyResponse1 = new KeyResponse();
								if(stockTotalValue == 0.0 ) {
									stockPercentage = 0.0;
								}else {
									stockPercentage = stockTotalValue * 100 / totalValues;
								}
								keyResponse.setKey("stock");
								keyResponse.setValue(stockPercentage);
								keyResponsesList.add(keyResponse);

								if(cryptoTotalValue == 0.0 ) {
									cryptopercentage = 0.0;
								}else {
									cryptopercentage = cryptoTotalValue * 100 / totalValues;
								}
								keyResponse1.setKey("crypto");
								keyResponse1.setValue(cryptopercentage);
								keyResponsesList.add(keyResponse1);
							} else if (subType.equalsIgnoreCase("Exchange") || subType.equalsIgnoreCase("Asset")) {
								for (KeyResponse keyResponse2 : keyResponsesList) {
									if(keyResponse2.getValue().equals("0.0")) {
										exchangePercentage = 0.0;
									}else {
										exchangePercentage = ((Number) keyResponse2.getValue()).doubleValue() * 100
												/ stockTotalValue;
									}
									keyResponse2.setValue(exchangePercentage);
								}
							}
							insightsCardsList.add(stockPortfolioInsightsCards);
							insightsCardsList.add(cryptoPortfolioInsightsCards);
						}

						portfolioResponseInsightsResponse.setKeyResponseList(keyResponsesList);
						portfolioResponseInsightsResponse.setPortfolioInsightsCardList(insightsCardsList);

						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.DATA_FOUND_MESSAGE);
						map.put(Constant.DATA, portfolioResponseInsightsResponse);
						log.info(Constant.DATA_FOUND_MESSAGE + " Status - {}", Constant.OK);
					} else {
						map.put(Constant.RESPONSE_CODE, Constant.OK);
						map.put(Constant.MESSAGE, Constant.USER_ID_NOT_FOUND_MESSAGE);
						log.info(Constant.USER_ID_NOT_FOUND_MESSAGE + " status - {}", Constant.OK);
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
		} catch (Exception e) {
			map.put(Constant.RESPONSE_CODE, Constant.SERVER_ERROR);
			map.put(Constant.MESSAGE, Constant.SERVER_MESSAGE);
			log.error(e.getMessage() + "! status - {}", Constant.SERVER_ERROR);
		}
		return map;
	}

}
