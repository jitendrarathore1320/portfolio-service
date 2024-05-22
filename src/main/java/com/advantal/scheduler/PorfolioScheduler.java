package com.advantal.scheduler;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.advantal.serviceimpl.WalletDataProcessUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PorfolioScheduler {

	@Autowired
	WalletDataProcessUtil walletDataProcessUtil;

	/*
	 * Intervals: 5Min=300000, 15Min=900000, 30min=1800000, 1Hr=3600000,
	 * 2.5Hr=9000000, 24Hr=86400000 *
	 */

	/* Scheduler - 1 : Working */
	@Scheduled(cron = "0 0 3 * * *")
//	@PostConstruct
	public void hourlySyncWallet() {
		log.info("Scheduler name : hourlySyncWalletData() | Task name : Update Wallet balance data");
//		walletDataProcessUtil.hourlySyncWallet();
		walletDataProcessUtil.dailySyncAssets();
		log.info("<<<--- Done --->>>");
	}
	
	/* Scheduler - 2 : Working */
	@Scheduled(cron = "0 30 03 * * *")
//	@PostConstruct
	public void dailySyncWallet() {
		log.info("Scheduler name : dailySyncWallet() | Task name : Sync daily Wallet data");
		walletDataProcessUtil.dailySyncWallet();
		log.info("<<<--- Done --->>>");
	}
	
	/* Scheduler - 3 : Working */
	@Scheduled(cron = "0 0 4 * * *")
	public void saveWalletHistory() {	
		log.info("Scheduler name : saveWalletHistory() | Task name : Save wallet History");
		walletDataProcessUtil.saveWalletHistory();
		log.info("<<<--- Done --->>>");
	}
	
	/* Scheduler - 4 : Working */
	@Scheduled(cron = "0 30 4 * * *")
	public void savePortfolioHistory() {
		log.info("Scheduler name : savePortfolioHistory() | Task name : Save portfolio History");
		walletDataProcessUtil.savePortrfolioHistory();
		log.info("<<<--- Done --->>>");
	}

}
