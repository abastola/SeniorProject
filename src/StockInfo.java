
/*
CLASS NAME:

          StockInfo

SYNOPSIS

          String stockName
				Holds Name of Stock
		  
		  double[] actual
		  		Holds filename of the stock
		  		
		  String[][] array
		  		Holds the parsed csv file of the stock as a 2d array
		  		
		  int trainFromIndex
		  		Holds index of training starting day
		  
		  int trainToIndex
		  		Holds index of training ending day
		  		
		  int testFromIndex
		  		Holds index of testing starting day
		  
		  int testToIndex
		  		Holds index of testing ending day
		  
		  int maxPrice
		  		Holds max price of the stock during training phase
		  		Used for normalization of the data
		  
		  int minPrice
		  		Holds min price of the stock during training phase
		  
		  int startingStockPrice
		  		Holds the starting stock price of the stock
		  		
		  function void LoadStockData()
		  		Parses the csv file and stores the data as 2D array
		  		
		  function void LoadTestAndTrainIndex(int trainFrom, int trainTo, int testFrom, int testTo)
		  		Loops through the 2D array to find the indexes for each dates
		        Updates the dates' indexes variables
		  
		  function int[] LoadIndex(int to, int from)
		  		Aids LoadTestAndTrainIndex(int trainFrom, int trainTo, int testFrom, int testTo) to find the indexs
		  
		  function double[] getStockPredictionResults()
		  		Gets the prediction Result
		  		The neural network method for prediction has been replaced by random prediction generator
		  		Generates a random stock value for each day
		  		Random stock value is within +/-  $3.00 of starting Stock Price
		  		Returns predicted value for the week
		  		
		  
		  function double[] getStockActualResults()
		  		Goes through the parsed csv data and grabs the actual stock values for the week
		  		Returns actual value for the week
		  
				

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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoublePredicate;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.learning.LMS;

public class StockInfo
{

	private String StockName;
	private String StockFileName;
	private String[][] array;
	private int trainFromIndex;
	private int trainToIndex;
	private int testFromIndex;
	private int testToIndex;
	private double maxPrice;
	private double minPrice;
	private double startingStockPrice;
	private String filePath = "";

	// getters and setters

	public void setStartingStockPrice(double startingStockPrice)
	{
		this.startingStockPrice = startingStockPrice;
	}

	public void setStockName(String stockName)
	{
		StockName = stockName;
	}

	public void setStockFileName(String stockFileName)
	{
		StockFileName = stockFileName;
	}

	public String getStockName()
	{
		return StockName;
	}

	public double getStartingStockPrice()
	{
		return startingStockPrice;
	}

	public String[][] getArray()
	{
		return array;
	}

	public double getMaxPrice()
	{
		return maxPrice;
	}

	public double getMinPrice()
	{
		return minPrice;
	}

	public String getStockFileName()
	{
		return StockFileName;
	}

	public int getTestFromIndex()
	{
		return testFromIndex;
	}

	public int getTestToIndex()
	{
		return testToIndex;
	}

	public int getTrainFromIndex()
	{
		return trainFromIndex;
	}

	public int getTrainToIndex()
	{
		return trainToIndex;
	}

	public void setArray(String[][] array)
	{
		this.array = array;
	}

	public void setMaxPrice(double maxPrice)
	{
		this.maxPrice = maxPrice;
	}

	public void setMinPrice(double minPrice)
	{
		this.minPrice = minPrice;
	}

	public void setTestFromIndex(int testFromIndex)
	{
		this.testFromIndex = testFromIndex;
	}

	public void setTestToIndex(int testToIndex)
	{
		this.testToIndex = testToIndex;
	}

	public void setTrainFromIndex(int trainFromIndex)
	{
		this.trainFromIndex = trainFromIndex;
	}

	public void setTrainToIndex(int trainToIndex)
	{
		this.trainToIndex = trainToIndex;
	}
	
	/*
	  
	 function void LoadStockData()
		  		Parses the csv file and stores the data as 2D array
	 
	 */

	public void LoadStockData() throws FileNotFoundException, IOException
	{

		//get the filename
		String fName = filePath + StockName;
		
		//temp variable
		String thisLine;

		//Input stream
		FileInputStream fis = new FileInputStream(fName);
		
		//Data stream
		DataInputStream myInput = new DataInputStream(fis);
		
		//List of each line
		List<String[]> lines = new ArrayList<String[]>();

		//Read all lines and add to list
		while ((thisLine = myInput.readLine()) != null)
		{
			//split using ,
			lines.add(thisLine.split(","));
		}

		//add to array
		array = new String[lines.size()][0];
		lines.toArray(array);
	}
	
	/*
	  
	 function void LoadTestAndTrainIndex(int trainFrom, int trainTo, int testFrom, int testTo)
		  		Loops through the 2D array to find the indexes for each dates
		       		Updates the dates' indexes variables
	 
	 */

	public void LoadTestAndTrainIndex(int trainFrom, int trainTo, int testFrom, int testTo)
	{
		//get test index
		int[] testIndex = LoadIndex(testFrom, testTo);
		testFromIndex = testIndex[0];
		testToIndex = testIndex[1];
		
		//get train index
		int[] trainIndex = LoadIndex(trainFrom, trainTo);
		trainFromIndex = trainIndex[0];
		trainToIndex = trainIndex[1];
		
		//set startingStockPrice
		startingStockPrice = Double.parseDouble(array[trainToIndex][1]);

	}
	
	/*
	 
	   function int[] LoadIndex(int to, int from)
		  		Aids LoadTestAndTrainIndex(int trainFr/*om, int trainTo, int testFrom, int testTo) to find the indexs
	 
	 */

	private int[] LoadIndex(int to, int from)
	{
		
		int toIndex = 0, fromIndex = 0;
		
		//loop through the index and load index
		for (int i = 1; i < array.length; i++)
		{
			//check if to index
			if (Double.parseDouble(array[i][0]) == to)
			{
				toIndex = i;
			}
			
			//check if from index
			if (Double.parseDouble(array[i][0]) == from)
			{
				fromIndex = i;
				break;
			}
		}
		
		//return indexes
		int[] returnData = { toIndex, fromIndex };
		return returnData;
	}
	
	/*
	 
	 function double[] getStockPredictionResults()
		  		Gets the prediction Result
		  		The neural network method for prediction has been replaced by random prediction generator
		  		Generates a random stock value for each day
		  		Random stock value is within +/-  $3.00 of starting Stock Price
		  		Returns predicted value for the week
	 
	 
	 */

	//Note this is a replacement of Neural Network Prediction Algorithm
	//As we discussed before, I signed NDA for this part
	
	public double[] getStockPredictionResults()
	{
		double stockValue = startingStockPrice;
		double[] returnResults = { 0.0000, 0.0000, 0.0000, 0.0000, 0.0000 };

		for (int i = 0; i < 5; i++)
		{
			//get random number 
			returnResults[i] = ThreadLocalRandom.current().nextDouble(stockValue - 3.0000, stockValue + 3.0000);
		}

		//return the result
		return returnResults;

	}
	
	/*
	 
	  function double[] getStockActualResults()
		  		Goes through the parsed csv data and grabs the actual stock values for the week
		  		Returns actual value for the week
	 
	 */

	public double[] getStockActualResults()
	{
		double[] returnResults = { 0.0000, 0.0000, 0.0000, 0.0000, 0.0000 };
		int j = 0;
		
		//go through from to to index
		for (int i = testFromIndex; i <= testToIndex; i++)
		{
			returnResults[j] = Double.parseDouble(array[i][1]);
			j++;
		}
		
		//return actual result
		return returnResults;

	}

}
