package com.advantal.responsepayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeSeriesDetails {

	private String open;

	private String high;

	private String low;

	private String close;

	private String date;

//	private String change;

//	private String percentChange;

//	private String previous_close;
}
