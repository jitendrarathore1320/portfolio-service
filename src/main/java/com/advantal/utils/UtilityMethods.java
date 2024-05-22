package com.advantal.utils;

import com.advantal.responsepayload.TickerDetail;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtilityMethods {

	public static Double convertBtcToUSD(Double balance, TickerDetail tickerDetail) {
		try {
			return balance * Double.valueOf(tickerDetail.getLastPrice());
		} catch (Exception e) {
			log.error("Error occurred while calculating portfolio value", e);
		}
		return 0.0;
	}

}
