package com.advantal.utils;

import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MethodUtil {

	public String formattedValues(Double value) {
		// Create a DecimalFormat instance to display without scientific notation
		DecimalFormat decimalFormat = new DecimalFormat("0.###############");

		// Format the original value without scientific notation
		String formattedPriceValue = decimalFormat.format(value);
//		Double result = Double.parseDouble(formattedPriceValue);
//		result = Math.round(result * 10000.0) / 10000.0;
		return formattedPriceValue;
	}

	

}
