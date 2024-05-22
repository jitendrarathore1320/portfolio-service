package com.advantal.responsepayload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponsePayload {

//	private String type;
//
//	private List<WalletResponse> walletResponseList;
	

	private List<WalletResponse> usaWalletResponseList;

	private List<WalletResponse> saudiWalletResponseList;

	private List<WalletResponse> cryptoWalletResponseList;

}
