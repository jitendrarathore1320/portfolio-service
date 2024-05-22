package com.advantal.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

//import org.apache.commons.collections4.functors.ExceptionClosure;
@Slf4j
public class DateUtil {

	public static String convertDateToStringDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = formatter.format(date);
//		System.out.println("Date Format with yyyy-MM-dd : " + strDate);
		return strDate;
	}

	public static String convertDateToStringDateTime(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String strDateTime = "null";
		if (date != null) {
			strDateTime = formatter.format(date);
//			System.out.println("Date Format with yyyy-MM-dd hh:mm:ss: " + strDateTime);
		}
		return strDateTime;
	}

	public static String convertDOBToAge(String DOB) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate date = LocalDate.parse(DOB, formatter);
		Period period = Period.between(date, LocalDate.now());
		String age = "0";
//		String age = period.getYears() + " Years " + period.getMonths() + " Months";
		if (period.getYears() != 0) {
			age = period.getYears() + " Years ";
		} else if (period.getMonths() != 0) {
			age = period.getMonths() + " Months ";
		} else {
			age = period.getDays() + " Days ";
		}
		return age;
	}

	public static String dateToString(Date date) {
		SimpleDateFormat fromDateFormat = new SimpleDateFormat("ddMMMYY");
		String strDate = fromDateFormat.format(date);
		return strDate;
	}

//	public static long differenceBetweenDate(Date oldDate, LocalDateTime currentDate) throws ParseException {
//		try {
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////			Date date = formatter.parse(currentDate);
////			long differenceInTime = date.getTime() - oldDate.getTime();
//			long differenceInMinute = TimeUnit.MILLISECONDS.toMinutes(differenceInTime);
////	long differenceInMinute = differenceInTime/ (1000 * 60);
//			return differenceInMinute;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0L;
//		
//	}

	public static long differenceBetweenDate(Date oldDate, LocalDateTime currentDate) throws ParseException {
		try {
			if (oldDate != null) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String cDate = currentDate.format(dateTimeFormatter);

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = formatter.parse(cDate);
				long differenceInTime = date.getTime() - oldDate.getTime();
				long differenceInMinute = TimeUnit.MILLISECONDS.toMinutes(differenceInTime);
				return differenceInMinute;
			} else
				return 0L;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public static String convertTimestampToString(Timestamp timeStampDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		String date = formatter.format(timeStampDate);
		return date;
	}

	public static Timestamp convertStringToTimeStamp(String stringDate) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = formatter.parse(stringDate);
		Timestamp timeStapDate = new Timestamp(date.getTime());
		return timeStapDate;
	}

	public static String changeStringaDateFormat(String inputDateTimeString) {
		String outputDateTimeString = "";

		// Define the input and output date formats
		SimpleDateFormat inputDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat outputDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			// Parse the input date and time string
			Date date = inputDateTimeFormat.parse(inputDateTimeString);

			// Format the date into the desired output format
			outputDateTimeString = outputDateTimeFormat.format(date);

//			System.out.println("Input Date Time String: " + inputDateTimeString);
//			System.out.println("Output Date Time String: " + outputDateTimeString);
		} catch (ParseException e) {
			System.err.println("Error parsing date and time: " + e.getMessage());
		}
		return outputDateTimeString;
	}

	public static String convertDateToStringDDMMYYYY(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}
	}

	public static String convertDateToString(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}
	}

	public static String convertDateToStringYYYYMMDD(Date date) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strDate = formatter.format(date);
			return strDate;
		} else {
			return "";
		}
	}

	public static String convertDateToStringWithTime(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}
	}

	public static String dateToStringWithTime(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}
	}

	public static String dateToStringWithTimeslashFormat(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}
	}

	public static String convertDateToStringExpiry(Integer duration) {
		if (duration != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy hh:mm:ss");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, duration - 1);
			String newDate = sdf.format(c.getTime());
			return newDate;
		} else {
			return "";
		}
	}

	public static String convertTimeStampToDate(long timeStamp) {
		if (timeStamp != 0L) {
			Date currentDate = new Date(timeStamp);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			String newDate = df.format(currentDate);
			return newDate;
		} else {
			return "";
		}
	}

	public static long getMintue(Date expriyDate) throws ParseException {
		Date date = new Date();

		long timeDiff;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String strDate = dateFormat.format(date);
		String expirydate = dateFormat.format(expriyDate);
		Date startDateObj = dateFormat.parse(strDate);
		Date endDateObj = dateFormat.parse(expirydate);
		if (strDate.compareTo(expirydate) > 0) {
			timeDiff = startDateObj.getTime() - endDateObj.getTime();
		} else if (strDate.compareTo(expirydate) < 0) {
			timeDiff = startDateObj.getTime() - endDateObj.getTime();
		} else {
			timeDiff = endDateObj.getTime() - startDateObj.getTime();
		}
		long minDiff = timeDiff / (1000 * 60);
		return minDiff;
	}

	public static Date convertStringToDateTime(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		}catch (ParseException e) {
			System.out.println("Parsing failed. Invalid date format.");
            e.printStackTrace();
            return null;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date convertStringToDateTimeddMMYY(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date slashFormatDateTime(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date slashFormatDateTimeddMMYYY(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date convertStringToDateTimeYYYY(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Date StringToDate(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String formatDate(Date date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
			String strDate = dateFormat.format(date);
			return strDate;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date convertLocalDateToGMTDateTimeZone(Date date) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDateTime = formatter.format(date);
		System.out.println("Date Format with yyyy-MM-dd hh:mm:ss: " + strDateTime);

		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Date gmtDate = formatter1.parse(strDateTime);
		System.out.println("converted gmtDate==>" + gmtDate);
		return gmtDate;
	}

	public static Long StringDateToLongDate00(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date dateTime = dateFormat.parse(date);
//				System.out.println("converted from string to date : " + dateTime);

				DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
				String newDate = dateFormat2.format(dateTime);
//				System.out.println("converted again into string date : " + newDate);

				DateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dt = dateFormat3.parse(newDate);
//				System.out.println("converted again into  date formate : " + dt);

				Long longDateTime = dt.getTime();
//				System.out.println("Timestamp date : " + longDateTime);
				return longDateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Long StringDateToLongDate23(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date dateTime = dateFormat.parse(date);
//				System.out.println("converted from string to date : " + dateTime);

				DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
				String newDate = dateFormat2.format(dateTime);
//				System.out.println("converted again into string date : " + newDate);

				DateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dt = dateFormat3.parse(newDate);
//				System.out.println("converted again into  date formate : " + dt);

				Long longDateTime = dt.getTime();
//				System.out.println("Timestamp date : " + longDateTime);
				return longDateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Long StringDateToLongDate(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dt = dateFormat.parse(date);
//				System.out.println("converted again into  date formate : " + dt);

				Long longDateTime = dt.getTime();
//				System.out.println("Timestamp date : " + longDateTime);
				return longDateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Long StringDateToLongDateTime(String date) {
		Long longDateTime = 0L;
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dt = dateFormat.parse(date);
				longDateTime = dt.getTime();
				return longDateTime;
			} else {
				return longDateTime;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return longDateTime;
	}

	public static String convertTimeStampToStringDate(long timeStamp) {
		try {
			if (timeStamp != 0) {
				Date currentDate = new Date(timeStamp);
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss");
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String newDate = df.format(currentDate);
				return newDate;
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String convertUnixTimeStampToStringDate(long timeStamp, String zoneId) {
		if (timeStamp != 0L) {
			Date currentDate = new Date(TimeUnit.MILLISECONDS.convert(timeStamp, TimeUnit.SECONDS));
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss");//dd MMMM yyyy, HH:mm
			DateFormat df = new SimpleDateFormat("dd MMM yyyy, HH:mm");// dd MMMM yyyy, HH:mm
			df.setTimeZone(TimeZone.getTimeZone(zoneId));
			log.info("Date is : " + df.format(currentDate));
			return df.format(currentDate);
		} else {
			return "";
		}
	}

	public static String convertUnixTimeStampToStringDate2(long timeStamp, ZoneId zoneId) {
		if (timeStamp != 0L) {
			LocalDateTime dateTime = LocalDateTime.now(zoneId);
//			Date currentDate = new Date(timeStamp * 1000L);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String newDate = df.format(dateTime);
			return newDate;
		} else {
			return "";
		}
	}

	// Interval Type: daily/weekly/monthly/yearly
//	public static String convertTimeStampToStringDate1(long timeStamp) {
//		if (timeStamp != 0L) {
//			Date currentDate = new Date(timeStamp);
//			DateFormat df = new SimpleDateFormat("d/MM/yyyy");
//			String newDate = df.format(currentDate);
//			return newDate;
//		} else {
//			return "";
//		}
//	}

	public static String getPreviousDate(String intervalType) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		String startrDate = localDate.format(dateTimeFormatter);
		String endDate = "";
		if (intervalType.equals("daily")) {
			LocalDate previousDate = localDate.minusDays(1);
			endDate = previousDate.format(dateTimeFormatter);
			System.out.println("Previous date: " + endDate);
			return endDate;
		} else if (intervalType.equals("weekly")) {
			LocalDate previousDate = localDate.minusDays(7);
			endDate = previousDate.format(dateTimeFormatter);
			System.out.println("Previous date: " + endDate);
			System.out.println("Current date: " + startrDate);
			return endDate;
		} else if (intervalType.equals("monthly")) {
			LocalDate previousDate = localDate.minusDays(30);
			endDate = previousDate.format(dateTimeFormatter);
			System.out.println("Previous month String date: " + endDate);
			System.out.println("Current String date: " + startrDate);
			return endDate;
		} else if (intervalType.equals("yearly")) {
			LocalDate previousDate = localDate.minusDays(365);
			endDate = previousDate.format(dateTimeFormatter);
			System.out.println("Previous year String date: " + endDate);
			System.out.println("Current String date: " + startrDate);
			return endDate;
		}
		return "";
	}

	public static LocalDateTime stringToLocalDateTime(String date) {
		LocalDateTime localDateTime = null;
		try {
			if (date != null) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
			} else {
				return localDateTime;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localDateTime;
	}

	public static LocalDateTime stringToLocalDate(String date) {
		if (date != null) {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
			return localDateTime;
		} else {
			return null;
		}
	}

	public static LocalDateTime dateToLocalDate(Date date) {
		if (date != null) {
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Instant instant = date.toInstant();
			LocalDateTime localDate = instant.atZone(defaultZoneId).toLocalDateTime();
			System.out.println("Local date time : " + localDate);
			return localDate;
		} else {
			return null;
		}
	}

	// difference localDateime
	public static String CurrentLocalDateTime() {
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
			LocalDateTime now = LocalDateTime.now();
			log.info("Current time : " + dtf.format(now));
			return dtf.format(now);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String CurrentToOneHourDiff() {
		// get current date time with Calendar()
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
//			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -1);
			log.info("Before One Hour Date Time : " + dateFormat.format(cal.getTime()));
			return dateFormat.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String CurrentToOneDayDiff() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		log.info("Subtract one day from current date : " + dateFormat.format(cal.getTime()));
		return dateFormat.format(cal.getTime());

	}

	public static String currentToOneWeek() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		log.info("Subtract one day from current date : " + dateFormat.format(cal.getTime()));
		return dateFormat.format(cal.getTime());
	}

	public static String currentToOneMonth() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		log.info("Subtract one day from current date : " + dateFormat.format(cal.getTime()));
		return dateFormat.format(cal.getTime());
	}

	public static String currentDateTimeToOneMonth() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		log.info("Subtract one day from current date : " + dateFormat.format(cal.getTime()));
		return dateFormat.format(cal.getTime());
	}

	public static String currentToOneYear() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		log.info("Subtract one year from current date : " + dateFormat.format(cal.getTime()));
		return dateFormat.format(cal.getTime());

	}

	public static String currentToAllTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		log.info("Subtract one year from current date : " + dateFormat.format(cal.getTime()));
		return dateFormat.format(cal.getTime());

	}

	public static String locaTimeToTwelveHour() {
		try {
			String currentTime = "";
			LocalTime localTime = LocalTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
			currentTime = localTime.format(dateTimeFormatter);
			return currentTime;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Long StringTimeToLongTime(String time) {
		try {
			if (time != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
				Date date = dateFormat.parse(time);
				Long longDateTime = date.getTime();
//				log.info("Timestamp is : " + longDateTime);
				return longDateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getTimeInZoneWise(ZoneId zoneId) {
		try {
			LocalTime currentTime = LocalTime.now(zoneId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
			String time = currentTime.format(formatter);
//			log.info("Current Time is : " + time);
			return time;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getNextDateTimeZoneWise(ZoneId zone, LocalTime localTime) {
		try {
			int hours, minutes, seconds;
			hours = localTime.getHour();
			minutes = localTime.getMinute();
			seconds = localTime.getSecond();
			DateFormat formatter = new SimpleDateFormat("dd MMM yyyy, HH:mm");
			formatter.setTimeZone(TimeZone.getTimeZone(zone));
			Calendar cal = Calendar.getInstance();
			log.info("Current Date Time is : " + formatter.format(cal.getTime()));
			cal.add(Calendar.HOUR, +hours);
			cal.add(Calendar.MINUTE, +minutes);
			cal.add(Calendar.SECOND, +seconds);
			log.info("Market will be Open at : " + formatter.format(cal.getTime()));
			return formatter.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getCurrentDateTimeInZoneWise(ZoneId zoneId) {
		try {
			LocalDateTime currentDateTime = LocalDateTime.now(zoneId);
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:00 a");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			log.info("Current Date is : " + currentDateTime.format(formatter));
			return currentDateTime.format(formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getCurrentDateTimeToOneHourDiffInZoneWise(ZoneId zoneId) {
		try {
			LocalDateTime dateTime = LocalDateTime.now(zoneId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:00 a");
			dateTime = dateTime.minusHours(1);
			log.info("Before One Hour DateTime : " + dateTime.format(formatter));
			return dateTime.format(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static String getPreviousDateTime(String dateTime, TimeSeriesRequest timeSeriesRequest) {
//		try {
//			DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//			LocalDateTime localdateTime = LocalDateTime.parse(dateTime, formate);
//			if (timeSeriesRequest.getInterval().equalsIgnoreCase(Constant.ONE_DAY))
//				localdateTime = localdateTime.minusHours(24);
//			else if (timeSeriesRequest.getInterval().equalsIgnoreCase(Constant.ONE_WEEK))
//				localdateTime = localdateTime.minusDays(7);
//			else if (timeSeriesRequest.getInterval().equalsIgnoreCase(Constant.ONE_MONTH))
//				localdateTime = localdateTime.minusDays(29);
//			else if (timeSeriesRequest.getInterval().equalsIgnoreCase(Constant.SIX_MONTH))
//				localdateTime = localdateTime.minusDays(179);
//			else if (timeSeriesRequest.getInterval().equalsIgnoreCase(Constant.ONE_YEAR))
//				localdateTime = localdateTime.minusDays(364);
//			else if (timeSeriesRequest.getInterval().equalsIgnoreCase(Constant.FIVE_YEAR))
//				localdateTime = localdateTime.minusDays(1824);
////			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//			return localdateTime.format(formatter);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	public static String getCurrentDateTimeToOneWeekDiffInZoneWise(ZoneId zoneId) {
		try {
			LocalDateTime dateTime = LocalDateTime.now(zoneId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:00 a");
			dateTime = dateTime.minusWeeks(1);
			log.info("Before One Week DateTime : " + dateTime.format(formatter));
			return dateTime.format(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentDateTimeToOneMonthDiffInZoneWise(ZoneId zoneId) {
		try {
			LocalDateTime dateTime = LocalDateTime.now(zoneId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:00 a");
			dateTime = dateTime.minusMonths(1);
			log.info("Before Six Month DateTime : " + dateTime.format(formatter));
			return dateTime.format(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentDateTimeToSixMonthDiffInZoneWise(ZoneId zoneId) {
		try {
			LocalDateTime dateTime = LocalDateTime.now(zoneId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:00 a");
			dateTime = dateTime.minusMonths(6);
			log.info("Before Six Month DateTime : " + dateTime.format(formatter));
			return dateTime.format(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentDateTimeToOneYearDiffInZoneWise(ZoneId zoneId) {
		try {
			LocalDateTime dateTime = LocalDateTime.now(zoneId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:00 a");
			dateTime = dateTime.minusYears(1);
			log.info("Before One Year DateTime : " + dateTime.format(formatter));
			return dateTime.format(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentDateTimeToStartDateTimeOfInstrumentDiffInZoneWise(ZoneId zoneId, int year) {
		try {
			LocalDateTime dateTime = LocalDateTime.now(zoneId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:00 a");
			dateTime = dateTime.minusYears(year);
			log.info("Start DateTime of the Instrument : " + dateTime.format(formatter));
			return dateTime.format(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static LocalTime stringToLocalTime(String time) {
		if (time != null) {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			LocalTime localDateTime;
			localDateTime = LocalTime.parse(time, dateTimeFormatter);
			log.info("Time is : " + localDateTime);
			return localDateTime;
		} else {
			return null;
		}

	}

	public static String OneWeekDateTimeDiff(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// Parse the input string to LocalDate
		LocalDate inputDate = LocalDate.parse(date, formatter);

		// Subtract one week
		LocalDate oneWeekAgo = inputDate.minus(1, ChronoUnit.WEEKS);

		// Format the result back to string
		String resultDateString = oneWeekAgo.format(formatter);

		System.out.println("Input Date: " + date);
		System.out.println("Date one week ago: " + resultDateString);
		return resultDateString;
	}

//	public static Long getMarketClosingTime(LocalTime localTime) {
	public static Long getMarketClosingTime(String time) {
		try {
			String hours, minutes, seconds;
			hours = time.substring(0, 2);
			minutes = time.substring(3, 5);
			seconds = time.substring(6);

//			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//			LocalTime localDateTime;
//			localDateTime = LocalTime.parse(time, dateTimeFormatter);
//			int hours, minutes, seconds;
//			hours = localDateTime.getHour();
//			minutes = localDateTime.getMinute();
//			seconds = localDateTime.getSecond();

			DateFormat formatter = new SimpleDateFormat("dd MMM yyyy, HH:mm a");
			Calendar cal = Calendar.getInstance();
			log.info("Current Date Time is : " + formatter.format(cal.getTime()));
			cal.add(Calendar.HOUR, +Integer.valueOf(hours));
			cal.add(Calendar.MINUTE, +Integer.valueOf(minutes));
			cal.add(Calendar.SECOND, +Integer.valueOf(seconds));
			log.info("Market open/close at : " + formatter.format(cal.getTime()));
			return cal.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String convertTimeStampToString(long timeStamp) {
		try {
			if (timeStamp != 0) {
				Date currentDate = new Date(timeStamp);
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String newDate = df.format(currentDate);
				return newDate;
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String convertDateFromYYYYMMDDDToMinHoure(String inputDateTimeString) {
		String convertingTime = "";
		try {
//			String inputDateTimeString = "10/10/2023 08:41:58";
			LocalDateTime inputDateTime = parseDateTime(inputDateTimeString);
			LocalDateTime now = LocalDateTime.now();
			long minutesDifference = ChronoUnit.MINUTES.between(inputDateTime, now);
			if (minutesDifference < 60) {
				convertingTime = minutesDifference == 1 ? "1 minute ago" : minutesDifference + " minutes ago";
				System.out.println(minutesDifference == 1 ? "1 minute ago" : minutesDifference + " minutes ago");
			} else {
				long hoursDifference = ChronoUnit.HOURS.between(inputDateTime, now);
				if (hoursDifference < 24) {
					convertingTime = hoursDifference == 1 ? "1 hour ago" : hoursDifference + " hours ago";
					System.out.println(hoursDifference == 1 ? "1 hour ago" : hoursDifference + " hours ago");
				} else {
					long daysDifference = ChronoUnit.DAYS.between(inputDateTime, now);
					if (daysDifference < 7) {
						convertingTime = daysDifference == 1 ? "1 day ago" : daysDifference + " days ago";
						System.out.println(daysDifference == 1 ? "1 day ago" : daysDifference + " days ago");
					} else {
						long weeksDifference = daysDifference / 7;
						if (weeksDifference < 4) {
							convertingTime = weeksDifference == 1 ? "1 week ago" : weeksDifference + " weeks ago";
							System.out.println(weeksDifference == 1 ? "1 week ago" : weeksDifference + " weeks ago");
						} else {
							long monthsDifference = ChronoUnit.MONTHS.between(inputDateTime, now);
							convertingTime = monthsDifference == 1 ? "1 month ago" : monthsDifference + " months ago";
							System.out
									.println(monthsDifference == 1 ? "1 month ago" : monthsDifference + " months ago");
						}
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception :- " + e.getMessage());
		}
		return convertingTime;
	}

	private static LocalDateTime parseDateTime(String dateTimeString) {
		// Replace this with your desired parsing logic
		// This is a simple example assuming the format "dd/MM/yyyy HH:mm:ss"
		// You may need to customize the DateTimeFormatter based on your input format
		return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
	}

	/* example 1 min ago to string datetime like yyyy/MM/dd HH:ss:mm */
	public static String MinToStringDateTimeFormat(String datetime) {
		try {
			String timeAgo = datetime;// "5 minutes ago"; // Example time ago expression
			String aa[] = timeAgo.split(" ");
			System.out.println("first index lenght :- " + aa[0].length());
			Long subtime = null;
			if (aa[0].length() > 3) {
				subtime = Long.valueOf(aa[0].substring(aa[0].length() - 2));
//				if(aa[0].length()==4) {
//					subtime = Long.valueOf(aa[0].substring(aa[0].length() - 2));
//				}else {
//					System.out.println(aa[1].length());
//					System.out.println("second index:-"+aa[1].substring(0,aa[1].length()-1));
////					subtime = Long.valueOf(aa[1].substring(aa[1].length()));
//					subtime = Long.valueOf(aa[1].substring(0, aa[1].length()-1));
//				}
			} else if (aa[0].length() > 1) {
				subtime = Long.valueOf(aa[0].substring(aa[0].length() - 1));
			} else {
				subtime = Long.valueOf(aa[0]);
			}
			System.out.println("number " + subtime);
			// Calculate the date and time based on the time ago expression
			LocalDateTime dateTime = calculateDateTime(timeAgo, subtime);

			// Format the date and time
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = dateTime.format(formatter);

//			System.out.println("Time Ago Expression: " + timeAgo);
//			System.out.println("Formatted Date Time: " + formattedDateTime);
			return formattedDateTime;

		} catch (Exception e) {
			log.info("Exception :- " + e.getMessage());
			;
		}
		return null;
	}

	private static LocalDateTime calculateDateTime(String timeAgo, Long subtime) {
		LocalDateTime now = LocalDateTime.now();

		if (timeAgo.contains("min")) {
			return now.minusMinutes(subtime);
		} else if (timeAgo.contains("hour")) {
			return now.minusHours(subtime);
		} else if (timeAgo.contains("day")) {
			return now.minusDays(subtime);
		} else if (timeAgo.contains("week")) {
			return now.minusWeeks(subtime);
		} else if (timeAgo.contains("month")) {
			return now.minusMonths(subtime);
		} else {
			return now; // Default to current date and time if no match
		}
	}

//	public static void main(String args[]) throws ParseException {
//		ZoneId zoneId = ZoneId.of("America/New_York");
////		getOneDayPreviousDateTimeInZoneWise(zoneId);
////		getCurrentDateTimeInZoneWise(zoneId);
//
//		DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		LocalDateTime dateTime = LocalDateTime.parse("2023-12-22 15:59:00", formate);
//		log.info("date is : " + dateTime);
//
//		dateTime = dateTime.minusHours(24);
//		log.info("date is : " + dateTime);
//	}

}
