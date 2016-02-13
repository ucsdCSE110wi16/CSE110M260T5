import java.io.IOException;
import java.math.BigDecimal;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class getquote {

	public double getprice (String symbol) throws IOException
	{
		Stock stock = YahooFinance.get(symbol);
		
		String name = stock.getName();
		if(name.compareTo("N/A") == 0)
			return -1;
		
		BigDecimal price_dec = stock.getQuote().getPrice();
		Double price_dub = price_dec.doubleValue();
		
		return price_dub;
	}
}