package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TsCryptoDetailsResponse {
 
	TsStatus status;
	
	TsCryptoDetailsData data;

}
