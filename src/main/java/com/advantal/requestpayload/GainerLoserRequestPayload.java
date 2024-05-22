package com.advantal.requestpayload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GainerLoserRequestPayload {
	
//	 @NotNull(message = "PageIndex can't be null !!")
//	private Integer pageIndex;
//	@NotNull(message = "PageSize can't be null And Zero!!")
//	private Integer pageSize;
 
	@NotNull(message = "User id can't be null !!")
	private Long userId;
 
//	@NotNull(message = "PageIndex can't be null !!")
//	private Integer pageIndex;
	
//	@NotNull(message = "PageSize can't be null And Zero!!")
//	private Integer pageSize;

//	@NotEmpty(message = "Country can'e be empty !!")
	@NotNull(message = "PortfolioId can't be null !!")
	private Long portfolioId;
	
	@NotEmpty(message = "Direction can'e be empty !!")
	@NotNull(message = "Direction can't be null !!")
	private String direction;
	
//	@NotEmpty(message = "Country can'e be empty !!")
	@NotNull(message = "Type can't be null !!")
	private String type;
	
//	@NotNull(message = "KeyWord can't be null !!")
//	private String keyWord;
	
	
}
