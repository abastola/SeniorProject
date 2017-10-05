/*
CLASS NAME:

          SimulatorResults

SYNOPSIS

          ArrayList<StockResult> SelectedStocksInformation
				Array of StockResult
				Holds the user selected stocks' information
		  
		  double MaximumPerStock;
		  		The maximum percentage of portfolio amount a stock can use
		  		This limits the simulator from dumping all money to a single stock
		  		
		  double OriginalPortfolioValue
		  		Holds the original principal
		  		
		  function getPredictionResults(int trainFrom, int trainTo, int testFrom, int testTo, String[] fileNames)
		  		Loops through all the selected stock files
		  		Runs the simulator for each stock using StockInfo class
		  		Gets the results from StockInfo object and stores the stock results as StockResult object
		  		Adds each StockResult object to the ArrayList<StockResult> SelectedStocksInformation which is accessed from RunPredictor
		  
		  function void PerformTradeForPredicted()
		  		Loops through each StockResult objects.
		  		Decides if stock should be bought, hold still or sold. 
		  		Updates the Volume in StockResult objects accordingly.
		  
		  function int BuySellStocks(int a, int totalStockVolume, double currentPrice)
		  		a -> The investment code. 0 : hold, -1 : Sell, 1 : Buy.
		  		Returns the total number of stocks to buy.
		  		Returns negative number in case of selling.
		  		Returns positive number in case of buying. 
		  		Returns zero in case of hold.
		  		Also checks if stock crosses the maximum percentage threshold.
		        If stock does cross maximum percentage threshold, advises not to buy anymore. (i.e. Returns 0)
		  		
		  
		  public void SummarizeTradingData()
		  		Summarizes the entire trading.
		  		Essential for log.
		  		
		  
				

DESCRIPTION

		Gets the following simulator parameters from the RunPredictor:
			Filename
			Training and Testing Data Indexes
		Parses the csv file and stores the csv file as a 2D array.
		Predicts the next week data.
		Parses the actual data for next week from the csv file.
		

RETURNS

        Not Applicable

AUTHOR

       Arjun Bastola

DATE

       6:57 PM 02/29/2017

*/


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class SimulatorResults
{

	public ArrayList<StockResult> SelectedStocksInformation = new ArrayList<StockResult>();
	private double MaximumPerStock;
	private double OriginalPortfolioValue;

	//setters and getters
	
	public void setMaximumPerStock(double maximumPerStock)
	{
		MaximumPerStock = maximumPerStock;
	}

	public void setOriginalPortfolioValue(double originalPortfolioValue)
	{
		OriginalPortfolioValue = originalPortfolioValue;
	}
	
	public void setSelectedStocksInformation(ArrayList<StockResult> selectedStocksInformation)
	{
		SelectedStocksInformation = selectedStocksInformation;
	}
	
	public double getMaximumPerStock()
	{
		return MaximumPerStock;
	}
	
	public double getOriginalPortfolioValue()
	{
		return OriginalPortfolioValue;
	}
	
	public ArrayList<StockResult> getSelectedStocksInformation()
	{
		return SelectedStocksInformation;
	}

	/*
	 
	 function getPredictionResults(int trainFrom, int trainTo, int testFrom, int testTo, String[] fileNames)
		  		Loops through all the selected stock files
		  		Runs the simulator for each stock using StockInfo class
		  		Gets the results from StockInfo object and stores the stock results as StockResult object
		  		Adds each StockResult object to the ArrayList<StockResult> SelectedStocksInformation which is accessed from RunPredictor
	 
	 */
	
	public void getPredictionResults(int trainFrom, int trainTo, int testFrom, int testTo, String[] fileNames)
			throws FileNotFoundException, IOException
	{

		// go through csv of each stock
		for (int i = 0; i < fileNames.length; i++)
		{
			StockInfo temp = new StockInfo();
			
			//set the stock name
			temp.setStockName(fileNames[i]);
			
			//parse the csv file
			temp.LoadStockData();
			
			//load the date
			temp.LoadTestAndTrainIndex(trainFrom, trainTo, testFrom, testTo);

			//get predicted result
			double[] PredictionResults = temp.getStockPredictionResults();
			
			//get actual result
			double[] ActualResults = temp.getStockActualResults();

			//create stockResult object for each stock
			StockResult stockResult = new StockResult();
			
			//set stock name
			stockResult.setStockName(temp.getStockName());
			
			//set prediction result
			stockResult.setResults(PredictionResults);
			
			//set actual results
			stockResult.setActual(ActualResults);

			//start peformance test to determine sell/hold/lost
			stockResult.StartPerformanceTest(temp.getStartingStockPrice());

			//add stockresult to the list
			SelectedStocksInformation.add(stockResult);
		}
	}

	/*
	 
	  function void PerformTradeForPredicted()
		  		Loops through each StockResult objects.
		  		Decides if stock should be bought, hold still or sold. 
		  		Updates the Volume in StockResult objects accordingly.
	 
	 */
	
	public void PerformTradeForPredicted()
	{

		//loop through each day
		for (int j = 0; j < 5; j++)
		{
			
			//loop through each stock
			for (int i = 0; i < SelectedStocksInformation.size(); i++)
			{

				//set temp for easiness
				StockResult temp = SelectedStocksInformation.get(i);
				double StockPrice = 0;
				
				//case for day i
				if (i == 0)
				{
					StockPrice = temp.getStartingStockPrice();
				} else
					
				//case of other days
				{
					StockPrice = temp.getResults(i - 1);
				}
				
				//get bought or sold amount
				int BuyOrSellAmount = BuySellStocks(temp.invest[j], temp.getTotalVolume(), StockPrice);
				
				//set total volume
				temp.setTotalVolume(temp.getTotalVolume() + BuyOrSellAmount);
				
				//set total volume for the day
				temp.volumeEachDay[j] = temp.getTotalVolume();
			}
		}
	}

	/*
	 
	 function int BuySellStocks(int a, int totalStockVolume, double currentPrice)
		  		a -> The investment code. 0 : hold, -1 : Sell, 1 : Buy.
		  		Returns the total number of stocks to buy.
		  		Returns negative number in case of selling.
		  		Returns positive number in case of buying. 
		  		Returns zero in case of hold.
		  		Also checks if stock crosses the maximum percentage threshold.
		        If stock does cross maximum percentage threshold, advises not to buy anymore. (i.e. Returns 0)
	 
	 */
	
	public int BuySellStocks(int a, int totalStockVolume, double currentPrice)
	{
		//if hold still do nothing
		if (a == 0)
		{
			return 0;
		} 
		
		//if sell, return volume
		else if (a == -1)
		{
			if (totalStockVolume == 0)
			{
				return 0;
			} else
			{
				return (0 - totalStockVolume);
			}
		} 
		
		//if buy, check max percentage condition and return buy volume
		else
		{

			if ((((totalStockVolume + 30) * currentPrice) / OriginalPortfolioValue * 100.00) > MaximumPerStock)
			{
				return 0;
			} else
			{
				return 30;
			}

		}
	}
	
	/*
	 
	   public void SummarizeTradingData()
		  		Summarizes the entire trading.
		  		Essential for log.
	 
	 */

	public void SummarizeTradingData()
	{
		for (int i = 0; i < 5; i++)
		{
			//Print Day
			System.out.println("Day " + (i + 1));
			
			//Print Stock information
			for (int j = 0; j < SelectedStocksInformation.size(); j++)
			{
				StockResult temp = SelectedStocksInformation.get(j);
				System.out.println("	Stock: " + temp.getStockName());
				System.out.println("        Predicted: " + temp.getResults(i));
				System.out.println("        Actual   : " + temp.getActuals(i));
				System.out.println("        Volume   : " + temp.getVolumeEachDay(i));
				System.out.println();
			}
			System.out.println();
		}
	}

}
