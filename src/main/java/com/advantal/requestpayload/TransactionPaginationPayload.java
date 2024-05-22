package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPaginationPayload {

	@NotNull(message = "Page index can't be null !!")
	private Integer pageIndex;

	@NotNull(message = "Page size can't be null !!")
	private Integer pageSize;
	
	@NotNull(message = "UserId can't be null !!")
	private Long userId;

	@NotEmpty(message = "symbol can't be empty !!")
	@NotNull(message = "symbol can't be null !!")
	private String symbol;

}
