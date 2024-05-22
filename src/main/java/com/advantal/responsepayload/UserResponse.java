package com.advantal.responsepayload;


import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	
	private Long id;
	
	private String phoneNo;
	
	private String deviceId;  

	private String deviceToken;

	private String deviceType;
	
	private String countryCode;
	
	private Short status;

	private String name;
	
	private String imageUrl;
	
	private String email;
	
	private Date creationDate;//

	private Date updationDate;//
	
//	private List<PortfolioResponse> portfolioResponseList;
		
}
