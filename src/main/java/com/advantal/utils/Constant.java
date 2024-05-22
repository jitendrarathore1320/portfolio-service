package com.advantal.utils;

public class Constant {

//	 ---------------------------- THIRD PARTY API KEY ---------------------------------
	public static final String API_KEY_VALUE = "67e8091a7e1419da6d82f2d1869ef63c";
	public static final String TS_API_KEY_VALUE = "0562240a5d7244aa8261b7c09a9e475c";
	public static final String NEWS_API_TOKEN_VALUE = "AMqEE0xhOHaq67zFfySEE8a0geEMQWbP5rIBaejt";
	public static final String EXCHANGERATE_API_KEY = "8f2dcf231b781df41d3fc772";
	
//	 ---------------------------- THIRD PARTY API BASE URL ---------------------------------
	public static final String BASE_URL = "https://financialmodelingprep.com/api/v3";
	public static final String FMPP_BASE_URL = "https://financialmodelingprep.com/api/v4";
	public static final String TS_BASE_URL = "https://api.tokeninsight.com";
	public static final String BINANCE_API_URL = "https://api.binance.com";
	public static final String EXCHANGERATE_BASE_URL="https://v6.exchangerate-api.com";

//	 ---------------------------- THIRD PARTY API END-POINTS ---------------------------------
	public static final String ACCOUNT_ENDPOINT = "/api/v3/account";
	public static final String WALLET_BALANCE_ENDPOINT = "/sapi/v1/asset/wallet/balance";
	public static final String ASSETS_LIST_ENDPOINT = "/sapi/v1/asset/get-funding-asset";
	public static final String TICKER_PRICE_ENDPOINT = "/api/v3/ticker/tradingDay";
	public static final String EXCHANGERATE_VERSION = "/v6/";
	public static final String TS_CRYPTO_DETAIL_ENDPOINT = "/api/v1/coins/";

	/* 1=active/unblocked, 0=deactive/delete, 2=blocked */
	public static final Short ZERO = 0;
	public static final Short ONE = 1;
	public static final Short TWO = 2;
	public static final Boolean FALSE = false;
	public static final Short THREE = 3;
	public static final Boolean TRUE = true;
	public static final String ACTIVATE = "activate";
	public static final String ONE_DAY = "1day";
	/*------------------------------------------ STATUS CODE -------------------------------------*/
	public static final String CREATE = "201";
	public static final String OK = "200";
	public static final String BAD_REQUEST = "400";
	public static final String NOT_AUTHORIZED = "401";
	public static final String FORBIDDEN = "403";
	public static final String WRONGEMAILPASSWORD = "402";
	public static final String NOT_FOUND = "404";
	public static final String SERVER_ERROR = "500";
	public static final String DB_CONNECTION_ERROR = "502";
	public static final String ENCRYPTION_DECRYPTION_ERROR = "503";
	public static final String NOT_EXIST = "405";
	public static final String CONFLICT = "409";

	/*--------------------------------------END STATUS CODE---------------------------------------*/

	/*--------------------------------------- RESPONSE KEY ---------------------------------------*/
	public static final String RESPONSE_CODE = "responseCode";
	public static final String OBJECT = "object";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	public static final String AUTH_KEY = "authKey";
	public static final String API_KEY = "apikey=";
	public static final String TS_API_KEY = "TI_API_KEY";
	public static final String MESSAGE = "message";
	public static final String DATA = "data";
	public static final String TOKEN = "token";

	/* ---------------------- END RESPONSE KEY ----------------- */

	/* ------------------------- RESPONSE MESSAGES ---------------------- */

	// ============================ Common Message =========================
	public static final String BAD_REQUEST_MESSAGE = "Bad request!!";
	public static final String ERROR_MESSAGE = "Please try again!!";
	public static final String RECORD_NOT_FOUND_MESSAGE = "Record not found!!";
	public static final String RECORD_FOUND_MESSAGE = "Record found!!";
	public static final String RECORD_SAVED_MESSAGE = "Record saved successfully!!";
	public static final String SERVER_MESSAGE = "Technical issue";
	public static final String PAGE_SIZE_MESSAGE = "Page size can't be zero, it should be more then zero!!";
	public static final String PAGE_LIMIT_MESSAGE = "limit and page can't be zero, it should be more then zero!!";
	public static final String ALREADY_DELETED_MESSAGE = "Already deleted!!";
	public static final String DELETED_MESSAGE = "Deleted successfully!!";
	public static final String ID_NOT_FOUND_MESSAGE = "Given id not found into the database!!";
	public static final String RECORD_BLOCKED_OR_DELETED_MESSAGE = "Record not found, because it may be blocked or deleted!!";
	public static final String PAGE_SIZE_AND_INDEX_CANT_NULL_MESSAGE = "Page size and Page index can't be null!!";
	public static final String DATA_FOUND = "Data found";
	public static final String RECORD_NOT_UPDATED_MESSAGE = "Record not updated because, given id not found into the database!!";
	public static final String ID_CAN_NOT_NULL_MESSAGE = "Id can not null, it should be valid!!";
	public static final String INTERNAL_SERVER_ERROR_MESSAGE = "There is an error on the server-side. Try again later";
	public static final String DATA_FOUND_MESSAGE = "Data found!!";
	public static final String NO_DB_SERVER_CONNECTION = "The server was found but the connection to its local database was not possible.";
	public static final String DATA_NOT_FOUND_MESSAGE = "Data not found!!";
	public static final String STATUS_VALUE_INVALID_MESSAGE = "Status value should be either 0 for make favorite or 1 for remove from favorite !!";
	public static final String SYMBOL_NOT_BLANK = "Symbol can't be blank or empty!!";
	public static final String STATUS_INVALID_MESSAGE = "Invalid status !!";
	public static final String CRYPTO_ID_NOT_EMPTY = "Crypto id can't be null";
	public static final String INVALID_COUNTRY = "Invalid country please enter the only US and SA country";
	public static final String NO_SERVER_CONNECTION = "The server was found but the connection to its local database was not possible.";
	public static final String SYMBOL_AND_COUNTRY_NOT_BLANK = "Symbol and Country can't be blank or empty!!";
	public static final String COUNTRY_NOT_BLANK = "Country can't be blank or empty!!";
	public static final String THIRD_PARTY_SERVER_ERROR_MESSAGE = "Error on the third party server-side.";
	public static final String CRYPTO_EXCHANGE_LIST_SAVE_SUCCESSFULLY = "Crypto exchange list save successfully !!";
	
	public static final String INVALID_INSTRUMENT_TYPE_MESSAGE = "Invalid instrument type !!";
	public static final String INVALID_USER_REQUEST_MESSAGE = "Invalid user request !!";
	public static final String DATA_NOT_FOUND_FROM_THIRD_PARTY_MESSAGE = "Data not available on the third party server !!";
	
	public static final String DIVIDEND_SUCESSFULLY = "Dividend saved successfully !!";
	public static final String USER_ID_CANT_NULL_MESSAGE = "User id can't be null or zero !!";
//	public static final String ID_CANT_NULL_MESSAGE = "Id can't be null !!";
	public static final String USER_ID_NOT_FOUND_MESSAGE = "Given user id not found into the database!!";
	public static final String ID_CANT_NULL_OR_ZERO_MESSAGE = "Id can't be null or zero !!";
	public static final String USER_NOT_FOUND = "User not found !!";
	
	public static final String BINANCE_ENDPOINT = "/api/v3/account?";
	public static final String LOGIN_SUCESSFULLLY = "User login successfully !!";
	public static final String ACCOUNT_INFO_FOUND_SUCESSFULLY = "Account info found sucessfully!!";

	public static final String ACCOUNT_INFO_SUCCESSFUL = "Account info found successfully!";

	public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";
	public static final String API_KEY_SECRET_BLANK_MESSAGE = "API Key and API Secret must not be blank.";
	public static final String CANDLESTICK_DATA_SUCCESSFUL = "Candlestick data found sucessfully!!";

	public static final String APPLICATION_FORM_URLENCODED = "application";
	public static final String ASSET_NOT_FOUND_MESSAGE = "asset not found";
	public static final String PROVIDE_VALID_SYMBOL = "Please provide a valid symbol";
	public static final String PROVIDE_VALID_INTERVAL = "Please provide a valid interval";
	public static final String CURRENCIES_SUPPORTED_ENDPOINT = "/api/v1/simple/supported_vs_currencies";
	public static final String GET_PRICE_BY_CURRENCY_ENDPOINT = "/api/v1/history/coins/";
	public static final String TIME_SERIES_ENDPOINT = "/historical-chart/";
	public static final String UNITED_STATES = "united states";
	public static final String SAUDI_ARABIA = "saudi arabia";
	public static final String FIVE_MINUTE = "5min";
	public static final String PORTFOLIO_DELETED_MESSAGE = "Portfolio deleted successfully !!";
	
	// ========================== Wallet message ============================
	public static final String WALLET_CONNECTED_MESSAGE = "Wallet connected successfully !!";
	public static final String WALLET_CONNECTION_FAILED_MESSAGE = "Wallet connection failed !!";
	public static final String APIKEY_SECRETKEY_INVALID_MESSAGE = "Wallet connection failed, because you may entered invalid apikey or secretKey !!";
	public static final String WALLET_DISCONNECTED_MESSAGE = "Wallet disconnected successfully !!";
	public static final String WALLET_DATA_NOT_FOUND_MESSAGE = "Wallet data not found !!";
	public static final String WALLET_ALREADY_DISCONNECTED_MESSAGE = "Wallet already disconnected !!";
	public static final String WALLET_ALREADY_CONNECTED_MESSAGE = "Wallet already connected !!";
	public static final String WALLET_NOT_DISCONNECTED_MESSAGE = "Wallet not able to disconnect !!";
	public static final String WALLET_CREDENTILAS_UPDATED_MESSAGE = "Wallet credentilas updated successfully !!";
	public static final String WALLET_ID_NOT_FOUND_MESSAGE = "Given wallet id not found into the database!!";
	public static final String WALLET_IS_EMPTY_MESSAGE = "Wallet is empty !!";
	public static final String WALLET_TYPE_CANT_NULL_MESSAGE = "Wallet type can't be null or empty !!";

	// ========================== Portfolio message ============================
	public static final String PORTFOLIO_CREATED_MESSAGE = "Portfolio created successfully !!";
	public static final String PORTFOLIO_IS_EMPTY_MESSAGE = "Portfolio is empty !!";
	public static final String CREATE_PORTFOLIO_MESSAGE = "Wallet connection failed, create portfolio first !!";
	public static final String MAIN_PORTFOLIO_CANT_DELETE_MESSAGE = "Main portfolio can't be delete because, it holds other portfolios data, delete first other portfolio !!";
	public static final String PORTFOLIO_UPDATED_MESSAGE = "Portfolio updated successfully !!";
	public static final String INVALID_PORTFOLIO_TYPE_MESSAGE = "Invalid portfolio type !!";
	public static final String PORTFOLIO_TYPE_CANT_NULL_MESSAGE = "Portfolio type can't be null !!";
	public static final String PORTFOLIO_ID_NOT_FOUND_MESSAGE = "Given portfolio id not found into the database!!";
	public static final String CREATE_NEW_PORTFOLIO_MESSAGE ="You have only main portfolio, you can't add any wallet here! need to create new portfolio !!";
	public static final String INVALID_GRAPH_TYPE_MESSAGE = "Invalid graph type !!";
	public static final String GAINERS = "gainers";
	public static final String LOSERS = "losers";
	public static final String CRYPTO = "crypto";
	public static final String STOCK = "Stock";
	public static final String USD = "USD";
	public static final String SAR = "SAR";
	public static final String INVALID_MARKET_TYPE_MESSAGE = "Invalid market type !!";
	
	// ========================== Transaction message ============================
	public static final String MANUAL_TRANSACTION_NOT_COMPLETED_MESSAGE = "Manual transaction could not be completed because, there is no portfolio! create portfolio first !!";
	public static final String MANUAL_TRANSACTION_FAILED_MESSAGE = "Manual transaction failed, pleaese try again later !!";
	public static final String DATA_UPDATED_SUCCESSFULLLY = "Data updated successfully !!";
	public static final String DATA_SAVED_SUCCESSFULLY = "Data saved successfully !!";
	public static final String DIVIDEND_NAME_ALREADY_EXIT = "Dividend name already exit !!";
	public static final String INSTRUMENT_NOT_FOUND_MESSAGE = "Given instrument not found into the database !!";
	public static final String TRANSACTION_ID_NOT_FOUND_MESSAGE = "Given transaction id not found into the database!!";
	public static final String MANUAL_TRANSACTION_SAVED_SUCESSFULLY = "Manual transaction saved seccussfully !!";
	public static final String MANUAL_TRANSACTION_UPDATED_SUCESSFULLY = "Manual transaction updated successfully";
	public static final String MANUAL_TRANSACTION_DELETED_SUCESSFULLY = "Manual transaction deleted successfully";
	public static final String INVALID_TRANSACTION_TYPE = "Invalid transcation type !!";
	
	// ========================== Available Balance message ============================
	public static final String TRANSACTION_CREATED_SUCCESSFULL = "Transaction created successfully !!";
	public static final String TRANSACTION_UPDATED_SUCCESSFULL = "Transaction updated successfully !!";
	public static final String WITHDRAWAL_SUCCESSFULLY = "Withdrawal transaction created successfully !!";
	public static final String WITHDRAWAL_UPDATE_SUCCESSFULLY = "Withdrawal transaction updated successfully !!";
	public static final String HISTORY_NOT_FOUND_MESSAGE = "History not found !!";
	public static final String TRANSACTION_NOT_FOUND_MESSAGE = "Transaction not found !!";
	public static final String BALANACE_NOT_FOUND_MESSAGE = "Deducting from your USD holdings might result in a negative balance.";
	public static final String AUTOMATICALLY_BALANCE_DEDUCTED_MESSAGE = "Amount deducted from your available balance ! due to performing manual transaction !!";
	public static final String AUTOMATICALLY_BALANCE_DEDUCTED_DUE_TO_INSUFFICIENT_BALANCE_MESSAGE = "Amount deducted from your available balance ! Due to insufficient balance , balance my be goes into negative !!";
	public static final String AUTOMATICALLY_BALANCE_ADDED_MESSAGE = "Amount added in your available balance ! due to performing manual transaction !!";
	public static final String ENABLED_SUCCESSFULLY_MESSAGE = "Enabled successfully !!";
	public static final String DISABLED_SUCCESSFULLY_MESSAGE = "Disabled successfully !!";	
	public static final String BROKER_LIST_EMPTY_MESSAGE = "Broker list is empty !!";	
	public static final String BROKER_NOT_FOUND_MESSAGE = "Broker not found !!";
	public static final String BROKER_CANT_BE_EMPTY = "Broker or Exchange can't be null or empty !!";
	public static final String BALANCE_NOT_AVAILABLE_MESSAGE = "Balance not available !!";
	public static final String BALANCE_AVAILABLE_MESSAGE = "Balance available !!";
	
	// ========================== Graph message ============================
	public static final String GRAPH_DATA_NOT_AVAILABLE_MESSAGE = "Graph data not available at this movement !!";

	
	
	
	
	// ========================== Currency Converter message ============================

	

	
}
