package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponsePage {
	
	private List<AvailableCashResponse> availableCashResponseList;

	private List<TransactionResponse> transactionHistoryList;
	
	private Double availableBalance;

}
