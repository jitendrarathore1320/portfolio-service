package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConverterResponse {

	private String base_code;//convert currency

	private String target_code;//target currency

	private Double amount;
}
