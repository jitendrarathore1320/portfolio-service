package com.advantal.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import com.advantal.requestpayload.CurrencyConverterRequestPayload;
import com.advantal.requestpayload.PriceRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ThirdPartyApiUtil {
	@Autowired
	WebClient webClient1 = WebClient.create();
	@Autowired
	private WebClient webClient;

	String responseStr = "";

	// https://api.tokeninsight.com/api/v1/coins/ethereum/markets?limit=900&offset=0
	public String getTradingPair(String crytpoId, Integer limit, Integer offset) {
		String responseStr = null;
		try {
			responseStr = webClient.get()
					.uri("https://api.tokeninsight.com/api/v1/coins/" + crytpoId + "/markets?limit=900&offset=0")
					.header(Constant.TS_API_KEY, Constant.TS_API_KEY_VALUE).retrieve().bodyToMono(String.class).block();
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
		return responseStr;

	}

	// https://api.tokeninsight.com/api/v1/simple/supported_vs_currencies
	public String getCurrencies() {
		String responseStr = null;
		try {
			responseStr = webClient.get().uri(Constant.TS_BASE_URL + Constant.CURRENCIES_SUPPORTED_ENDPOINT)
					.header(Constant.TS_API_KEY, Constant.TS_API_KEY_VALUE).retrieve().bodyToMono(String.class).block();
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
		return responseStr;

	}

	// https://api.tokeninsight.com/api/v1/history/coins/bitcoin?interval=hour&length=8784&vs_currency=TRY
	public String getPriceByCurrency(PriceRequest priceRequest) {
		String responseStr = null;
		try {
			responseStr = webClient.get()
					.uri(Constant.TS_BASE_URL + Constant.GET_PRICE_BY_CURRENCY_ENDPOINT + priceRequest.getCryptoId()
							+ "?interval=hour&length=8784&vs_currency=" + priceRequest.getCurrency())
					.header(Constant.TS_API_KEY, Constant.TS_API_KEY_VALUE).retrieve().bodyToMono(String.class).block();
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
		return responseStr;

	}

	public static String getBinanceAccountInfo(String apiKey, String apiSecret) {
		Map<String, String> params = new HashMap<>();
		long timestamp = System.currentTimeMillis();
		params.put("timestamp", String.valueOf(timestamp));
		String signature = "";
		String responseStr = "";
		try {
			signature = generateSignature(params, apiSecret);
			params.put("signature", signature);
		} catch (Exception e) {
			e.printStackTrace();
			return responseStr;
		}
		String queryString = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));
		String requestUrl = Constant.BINANCE_API_URL + Constant.WALLET_BALANCE_ENDPOINT + "?" + queryString;
		WebClient webClient = WebClient.create();
		try {
			RequestHeadersSpec<?> requestSpec = webClient.get().uri(requestUrl).header("X-MBX-APIKEY", apiKey);
			responseStr = requestSpec.exchangeToMono(response -> response.bodyToMono(String.class)).block();
		} catch (Exception e) {
			e.printStackTrace();
			return responseStr;
		}
		return responseStr;
	}

	private static String generateSignature(Map<String, String> params, String apiSecret)
			throws NoSuchAlgorithmException, InvalidKeyException {
		String queryString = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(java.util.stream.Collectors.joining("&"));

		Mac hmacSha256 = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		hmacSha256.init(secretKeySpec);
		byte[] hmacData = hmacSha256.doFinal(queryString.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(hmacData);
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte b : bytes) {
			result.append(String.format("%02x", b));
		}
		return result.toString();
	}

	public static String getTickerPrice(StringBuilder symbolString, String apiKey, String apiSecret) {
//		Map<String, String> params = new HashMap<>();
//		long timestamp = System.currentTimeMillis();
//		params.put("timestamp", String.valueOf(timestamp));
//		String signature = "";
		String responseStr = "";
//		try {
//			signature = generateSignature(params, apiSecret);
//		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
//			e.printStackTrace();
//			return responseStr;
//		}
//		params.put("signature", signature);
//		String queryString = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
//				.collect(Collectors.joining("&"));
		String requestUrl = Constant.BINANCE_API_URL + Constant.TICKER_PRICE_ENDPOINT + "?" + "symbols="
				+ symbolString.toString();
		WebClient webClient = WebClient.create();
		try {
			RequestHeadersSpec<?> requestSpec = webClient.get().uri(requestUrl).header("X-MBX-APIKEY", apiKey);
			responseStr = requestSpec.exchangeToMono(response -> response.bodyToMono(String.class)).block();
		} catch (Exception e) {
			e.printStackTrace();
			return responseStr;
		}
		return responseStr;
	}

	public static String getBtcTickerPrice(String apiKey, String apiSecret) {
//		Map<String, String> params = new HashMap<>();
//		long timestamp = System.currentTimeMillis();
//		params.put("timestamp", String.valueOf(timestamp));
//		String signature = "";
//		try {
//			signature = generateSignature(params, apiSecret);
//		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
//			e.printStackTrace();
//			return null;
//		}
//		params.put("signature", signature);
		String requestUrl = Constant.BINANCE_API_URL + Constant.TICKER_PRICE_ENDPOINT + "?" + "symbol=BTCUSDT";
		WebClient webClient = WebClient.create();
		String responseStr = "";
		try {
			RequestHeadersSpec<?> requestSpec = webClient.get().uri(requestUrl).header("X-MBX-APIKEY", apiKey);
			responseStr = requestSpec.exchangeToMono(response -> response.bodyToMono(String.class)).block();
		} catch (Exception e) {
			e.printStackTrace();
			return responseStr;
		}
		return responseStr;
	}

	public static String getAssetList(String apiKey, String apiSecret) {
		String apiUrl = Constant.BINANCE_API_URL + Constant.ASSETS_LIST_ENDPOINT;
		Map<String, String> params = new HashMap<>();
		long timestamp = System.currentTimeMillis();
		params.put("timestamp", String.valueOf(timestamp));
		String signature;
		String responseStr = "";
		try {
			signature = generateSignature(params, apiSecret);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return responseStr;
		}
		params.put("signature", signature);
		String queryString = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));
		String requestUrl = apiUrl + "?" + queryString;
		WebClient webClient = WebClient.create();
		try {
			RequestHeadersSpec<?> requestSpec = webClient.post().uri(requestUrl).header("X-MBX-APIKEY", apiKey);
			responseStr = requestSpec.exchangeToMono(response -> response.bodyToMono(String.class)).block();
		} catch (Exception e) {
			log.error("Error making request: {}", e.getMessage());
			return responseStr;
		}
		return responseStr;
	}
//https://financialmodelingprep.com/api/v3/historical-chart/1day/2222.SR?apikey=67e8091a7e1419da6d82f2d1869ef63c
	public String getTimeSeries(PriceRequest priceRequest, String interval) {
		try {
			if (!priceRequest.getCountry().equalsIgnoreCase(Constant.SAUDI_ARABIA)) {
				responseStr = webClient.get()
						.uri(Constant.BASE_URL + Constant.TIME_SERIES_ENDPOINT + interval + "/"
								+ priceRequest.getSymbol() + "?" + Constant.API_KEY + Constant.API_KEY_VALUE)
						.exchangeToMono(response -> {
							return response.bodyToMono(String.class);
						}).block();
			} else {
				responseStr = webClient.get()
						.uri(Constant.BASE_URL + Constant.TIME_SERIES_ENDPOINT + interval + "/"
								+ priceRequest.getSymbol() + ".SR" + "?" + Constant.API_KEY + Constant.API_KEY_VALUE)
						.exchangeToMono(response -> {
							return response.bodyToMono(String.class);
						}).block();
			}
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
		}
		return responseStr;
	}

	public String getCryptoDetails(String cryptoId) {
		String cryptoResponseStr = "";
		try {
			cryptoResponseStr = webClient.get()
					.uri(Constant.TS_BASE_URL + Constant.TS_CRYPTO_DETAIL_ENDPOINT + cryptoId)
					.header(Constant.TS_API_KEY, Constant.TS_API_KEY_VALUE).retrieve().bodyToMono(String.class).block();
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
//			System.out.println("Exception :" + e.getMessage());
			return cryptoResponseStr;
		}
		return cryptoResponseStr;

	}

	// https://v6.exchangerate-api.com/v6/8f2dcf231b781df41d3fc772/pair/USD/SAR
	public String getCurrencyConverter(CurrencyConverterRequestPayload converterRequestPayload) {
		String res = new String();
		try {
			res = webClient.get()
					.uri(Constant.EXCHANGERATE_BASE_URL + Constant.EXCHANGERATE_VERSION + Constant.EXCHANGERATE_API_KEY
							+ "/pair/" + converterRequestPayload.getBase_code() + "/"
							+ converterRequestPayload.getTarget_code())
					.exchangeToMono(response -> {
						return response.bodyToMono(String.class);
					}).block();
		} catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			return res;
		}
		return res;
	}

}
