/*
CLASS NAME:

          StockResult

SYNOPSIS

          String stockName
				Holds Name of Stock
		  
		  double[] actual
		  		Stores Actual stock value of next week
		  		
		  double[] results
		  		Stores Predicted value of next week
		  
		  int[] invest
		  		Stores whether to sell, buy or hold.
		  		-1 = sell
		  		1 = buy
		  		0 = hold
		  
		  int[] volumeEachDay
		  		Stores the volume of the stock for each day
		  
		  boolean trade
		  		Determine if this stock should be include in trade
		  		True by default.
		  		Changes to false if user unchecks the stock in RunPredictor class.
		  
		  int totalVolume
		  		Store the total volume of stock own
		  		
		  double startingStockPrice
		  		Stores the starting stock price (the price before the prediction week)

         function public void StartPerformanceTest(double stockValue)
				
				Compares the predicted value for next day and decides whether or not to invest.
				Updates invest array element for each day

         function private int InvestOrNot(double a, double b)
				
				Called by StartPerformanceTest.
				Determine if increase or decrease and returns sell/buy/hold code
				


DESCRIPTION

		Stores the simulator results of a particular stock
		Compares the predicted value for next day and decides whether or not to invest.
		Stores the investment code in int[] invest array for each day 

RETURNS

        Not Applicable

AUTHOR

       Arjun Bastola

DATE

       6:57 PM 02/29/2017

*/

public class StockResult
{

	private String stockName;
	private double[] actual;
	private double[] results;
	public int[] invest = { -5, -5, -5, -5, -5 };
	public int[] volumeEachDay = { 0, 0, 0, 0, 0 };
	private boolean trade = true;
	private int totalVolume = 0;
	private double startingStockPrice = 0;

	// Setters and getters

	public int getVolumeEachDay(int a)
	{
		return volumeEachDay[a];
	}

	public void setStockName(String stockName)
	{
		this.stockName = stockName;
	}

	public boolean getTrade()
	{
		return trade;
	}

	public void setTrade(boolean trade)
	{
		this.trade = trade;
	}

	public String getStockName()
	{
		return stockName;
	}

	public void setTotalVolume(int totalVolume)
	{
		this.totalVolume = totalVolume;
	}

	public int getTotalVolume()
	{
		return totalVolume;
	}

	public void setResults(double[] results)
	{
		this.results = results;
	}

	public double getResults(int a)
	{
		return results[a];
	}

	public void setActual(double[] actual)
	{
		this.actual = actual;
	}

	public double[] getActual()
	{
		return actual;
	}

	public double getActuals(int a)
	{
		return actual[a];
	}

	public int[] getInvest()
	{
		return invest;
	}

	public int getInvest(int a)
	{
		return invest[a];
	}

	public double getStartingStockPrice()
	{
		return startingStockPrice;
	}

	public void setInvest(int[] invest)
	{
		this.invest = invest;
	}

	public void setStartingStockPrice(double startingStockPrice)
	{
		this.startingStockPrice = startingStockPrice;
	}

	public void setVolumeEachDay(int[] volumeEachDay)
	{
		this.volumeEachDay = volumeEachDay;
	}

	/*
	 * 
	 * function public void StartPerformanceTest(double stockValue)
	 * 
	 * Compares the predicted value for next day and decides whether or not to
	 * invest. Updates invest array element for each day
	 * 
	 */

	public void StartPerformanceTest(double stockValue)
	{
		startingStockPrice = stockValue;
		
		//for first day
		invest[0] = InvestOrNot(stockValue, results[0]);
		
		//check if we should invest or not
		for (int i = 1; i < 5; i++)
		{
			invest[i] = InvestOrNot(results[i - 1], results[i]);
		}
	}

	/*
	 * function private int InvestOrNot(double a, double b)
	 * 
	 * Called by StartPerformanceTest. Determine if increase or decrease Returns
	 * sell/buy/hold code
	 * 
	 */

	private int InvestOrNot(double a, double b)
	{
		//check percentage diffrence
		double percentage = (b - a) / a * 100.00;
		
		//if decrease return -1
		if (percentage < -0.500000)
		{
			return -1;
		} 
		
		//if increase return 1
		else if (percentage > 0.500000)
		{
			return 1;
		} 
		
		//if constant return 0
		else
		{
			return 0;
		}
	}

}
