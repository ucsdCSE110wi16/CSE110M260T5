package com.example.quick619.project;

import java.io.IOException;
import java.math.BigDecimal;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


/**
 * Created by Ty Stahnke
 *
 * Class that handles access the API
 * Used to get current price, current change, and the company name
 * All methods have checks that prevent user from inputting unaccepted input to API
 */

public class getquote {

	/**
	 * getprice()
	 *
	 * @param symbol Ticker symbol to get price for e.g. "AAPL" for Apple Inc.
	 * @return price from API
	 * @throws IOException
	 */
	public double getprice (String symbol) throws IOException
	{

		if(!isAlpha(symbol))
			return -1;

		if(symbol.contains(".") || symbol.contains(",") || symbol.contains("@"))
			return -1;

		Stock stock = YahooFinance.get(symbol);

		// Null check
		// - Added by Sam to get rid of a documented bug
		if (stock == null)
			return -1;

		String name = stock.getName();
		if(name.compareTo("N/A") == 0)
			return -1;
		
		BigDecimal price_dec = stock.getQuote().getPrice();
		Double price_dub = price_dec.doubleValue();
		
		return price_dub;
	}

	/**
	 * getchange()
	 *
	 * @param symbol Ticker symbol to get price for e.g. "AAPL" for Apple Inc.
	 * @return change in price from API
	 * @throws IOException
	 */
	public double getchange (String symbol) throws IOException
	{

		if(!isAlpha(symbol))
			return -1;

		Stock stock = YahooFinance.get(symbol);
		if(stock == null)
			return -1;

		String name = stock.getName();
		if (name.compareTo("N/A") == 0)
				return -1;

		BigDecimal change_dec = stock.getQuote().getChange();
		Double change_dub = change_dec.doubleValue();

		return change_dub;
	}

	/**
	 * getname()
	 *
	 * @param symbol Ticker symbol to get price for e.g. "AAPL" for Apple Inc.
	 * @return Comapny Name from API
	 * @throws IOException
	 */
	public String getname (String symbol) throws IOException
	{

		if(!isAlpha(symbol))
			return null;

		Stock stock = YahooFinance.get(symbol);
		if(stock == null)
			return null;

		String name = stock.getName();
		if (name.compareTo("N/A") == 0)
			return null;


		return name;
	}

	/**Helper method for determining if the inputted symbol contains only letters*/
	public boolean isAlpha(String name) {
		char[] chars = name.toCharArray();

		for (char c : chars) {
			if(!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}
}