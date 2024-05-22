package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
	
	private Long walletId;
	
	private String walletName;

	private Integer noOfAssets;

	private Double totalBalance;

	private List<AssetsRes2> assetsResList;

}
