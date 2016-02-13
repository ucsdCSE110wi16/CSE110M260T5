import java.io.IOException;

public class api_main {

	private static getquote my_quote = new getquote();
	
	public static void main (String args[]) throws IOException
	{

		double my_price = my_quote.getprice(args[0]);
		
		if(my_price < 0)
			System.out.println("Error");
		else
			System.out.println(my_price);
		
	}
	
}
