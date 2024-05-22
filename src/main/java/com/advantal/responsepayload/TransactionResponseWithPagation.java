package com.advantal.responsepayload;

import java.util.List;

import com.advantal.model.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseWithPagation {

	private Integer pageIndex;

	private Integer pageSize;

	private Long totalElement;

	private Integer totalPages;

	private Boolean isLastPage;

	private Boolean isFirstPage;
	
	//add new param
	private Double avg_buying_price;
	
	private Double avg_selling_price;
	
//	private Long total_transaction;

//	private List<Transaction> transactionResponseList;
	private List<TransactionRes> transactionResponseList;
}
