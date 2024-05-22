package com.advantal.requestpayload;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostActiveRequestPayload {
	
	@NotNull(message = "PageIndex can't be null !!")
	private Integer pageIndex;
	
	@NotNull(message = "PageSize can't be null And Zero!!")
	private Integer pageSize;
 
	@NotNull(message = "User id can't be null !!")
	private Long userId;
	
	@NotNull(message = "Type can't be null !!")
	private String type;
	
}
