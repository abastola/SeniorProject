/*
CLASS NAME:

          RunPredictor

SYNOPSIS

          	ArrayList<String> SelectedStocks
          		Stores the names of the stocks the user intially selected in SelectStocks
          		
			double OriginalPortfolioValue
				Stores the amount of OriginalPortflio Value
				Grabbed from JTextField investmentAmount
				
			double MaximumPercentagePerStock
				Stores maximum percentage of portfolio a stock can utilize
				Grabbed from JTextField maximumInvestmentPerStock
				
			int trainFrom
				Stores training start date
				Grabbed from JTextField traingStart
			
			int trainTo
				Stores training end date
				Grabbed from JTextField traingEnd
			
			int testFrom	
				Stores testing start date
				Grabbed from JTextField testingStart
				
			int testTo
				Stores testing end date
				Grabbed from JTextField testingEnd
			
			double[] actualTotal
				Stores the actual total at the end of the simulation
				
			double[] predictedTotal
				Stores the predicted total at the end of the simulation
			
			List<JCheckBox> checkboxesAddRemove
				Stores the list of JCheckBox of stocks selected for trade
			
			SimulatorResults simulatorResults
				SimulatorResults object that stores the result of simulator for each Stock

			function private void SetStockLists()
				Gets lists of filenames of available stocks.
				Dynamically draws checkboxes for each stock.
				
			function private void EnableCheckBoxes()
				Enables the checkboxes for user selection after the simulation ends so the user can perform water if analysis.
				
			function void startSimulator()
				Starts the simulation
			
			function void calculateTradingDataForWeekPredicted()
				Calculates the final summary of trading simulation using predicted Data
				
			function void calculateTradingDataForWeekActual()
				Calculates the final summary of trading simulation using actual Data
				
			function private XYDataset createDataset()
				Create dataset of (day, profit/loss) to plot the graph.
				One each dataset of Predicted and Actual Results
				Returns XYDateset of points to be plotted
			
			function private JPanel createChartPanel()
				Create a line charts with datasets created in CreateDataSet
				Returns a Panel with the plotted chart
			
			function private void customizeChart(JFreeChart chart)
				Customize the design line chart created in createChartPanel
			
			function private void refreshChart()
				Re-render the chart using updated datasets
				Dataset is updated when the user changes the checkbox selections
			
			function private void DisplaySummary(double pT, double aT, double oT)
				Grabs the trading summary and displays it on the right panel
				
			function private double round(double value, int places)
				Rounds the given double value of given decimal places
				Alawys 2 in case of this project
			

DESCRIPTION

		Gets the simulator parameters from the user.
		Runs the simulator using the provided parameters.
		Gets the simulator results.
		Makes trades and displays the output.
		Displays trade summary.
		Renders graph of the profit/loss per day.
		Allows user to select/unselect stocks after running the simulator so that the user can see what would have happened if a stock was removed/added
		Updates the graph and trading summary depending on the user selections.
		

RETURNS

        Not Applicable

AUTHOR

       Arjun Bastola

# DATE

#       6:57 PM 02/29/2017

*/



import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JSplitPane;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JTextPane;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;

public class RunPredictor extends JFrame
{

	//Java Swing Elements
	
	private JPanel contentPane;
	private JPanel GraphPanel;
	private JPanel StocksTrading;
	private JPanel summaryPanel;
	private JTextField trainingStart;
	private JTextField trainingEnd;
	private JTextField testingStart;
	private JTextField testingEnd;
	private JTextField investmentAmount;
	private JTextField maximumInvestmentPerStock;

	private JLabel oInvestment;
	private JLabel pTotal;
	private JLabel pProfitLoss;
	private JLabel aTotal;
	private JLabel aProfitLoss;

	private ArrayList<String> SelectedStocks;
	private double OriginalPortfolioValue;
	private double MaximumPercentagePerStock;
	private SimulatorResults simulatorResults = new SimulatorResults();
	private int trainFrom;
	private int trainTo;
	private int testFrom;
	private int testTo;
	private double[] actualTotal = { 0.00, 0.00, 0.00, 0.00, 0.00 };
	private double[] predictedTotal = { 0.00, 0.00, 0.00, 0.00, 0.00 };
	List<JCheckBox> checkboxesAddRemove = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			//make the frame visible
			public void run()
			{
				try
				{
					RunPredictor frame = new RunPredictor();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	//Set the selected stocks
	public void setSelectedStocks(ArrayList<String> stocks)
	{
		SelectedStocks = stocks;
		
		//print the stocks name for debug purpose
		for (String string : SelectedStocks)
		{
			System.out.println(string);
		}
		
		//Display the stocks so user can select it
		SetStockLists();
	}

	/**
	 * Create the frame.
	 */
	public RunPredictor()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1254, 648);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblStockMarketPredictor = new JLabel("Stock Market Predictor");
		lblStockMarketPredictor.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblStockMarketPredictor.setBounds(22, 13, 878, 68);
		contentPane.add(lblStockMarketPredictor);

		JLabel lblStocksLists = new JLabel("Add/Remove Stocks");
		lblStocksLists.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblStocksLists.setBounds(25, 104, 187, 16);
		contentPane.add(lblStocksLists);

		StocksTrading = new JPanel();
		StocksTrading.setBackground(Color.WHITE);
		StocksTrading.setBounds(22, 134, 208, 139);
		contentPane.add(StocksTrading);

		GraphPanel = new JPanel();
		GraphPanel.setBackground(Color.WHITE);
		GraphPanel.setBounds(242, 134, 594, 421);
		contentPane.add(GraphPanel);

		JLabel lblResult = new JLabel("Result");
		lblResult.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblResult.setBounds(242, 104, 232, 16);
		contentPane.add(lblResult);

		summaryPanel = new JPanel();
		summaryPanel.setBackground(Color.WHITE);
		summaryPanel.setBounds(848, 134, 362, 421);
		contentPane.add(summaryPanel);
		summaryPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Predicted Result:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setBounds(12, 42, 156, 16);
		summaryPanel.add(lblNewLabel);

		JLabel lblOriginalInvestment = new JLabel("Original Investment:");
		lblOriginalInvestment.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblOriginalInvestment.setBounds(12, 9, 156, 27);
		summaryPanel.add(lblOriginalInvestment);

		oInvestment = new JLabel("0");
		oInvestment.setFont(new Font("Tahoma", Font.PLAIN, 15));
		oInvestment.setBounds(180, 14, 156, 16);
		summaryPanel.add(oInvestment);

		JLabel lblFinalPortfolioAmount = new JLabel("Final Portfolio:");
		lblFinalPortfolioAmount.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblFinalPortfolioAmount.setBounds(37, 70, 97, 16);
		summaryPanel.add(lblFinalPortfolioAmount);

		JLabel lblProfitloss = new JLabel("Profit/Loss:");
		lblProfitloss.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProfitloss.setBounds(37, 99, 82, 16);
		summaryPanel.add(lblProfitloss);

		pTotal = new JLabel("0");
		pTotal.setFont(new Font("Tahoma", Font.PLAIN, 15));
		pTotal.setBounds(135, 71, 156, 16);
		summaryPanel.add(pTotal);

		pProfitLoss = new JLabel("0");
		pProfitLoss.setFont(new Font("Tahoma", Font.PLAIN, 15));
		pProfitLoss.setBounds(135, 99, 156, 16);
		summaryPanel.add(pProfitLoss);

		JLabel lblActualResult = new JLabel("Actual Result:");
		lblActualResult.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblActualResult.setBounds(12, 142, 156, 16);
		summaryPanel.add(lblActualResult);

		JLabel label_5 = new JLabel("Final Portfolio:");
		label_5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		label_5.setBounds(37, 170, 97, 16);
		summaryPanel.add(label_5);

		JLabel label_6 = new JLabel("Profit/Loss:");
		label_6.setFont(new Font("Tahoma", Font.PLAIN, 15));
		label_6.setBounds(37, 199, 82, 16);
		summaryPanel.add(label_6);

		aProfitLoss = new JLabel("0");
		aProfitLoss.setFont(new Font("Tahoma", Font.PLAIN, 15));
		aProfitLoss.setBounds(135, 199, 156, 16);
		summaryPanel.add(aProfitLoss);

		aTotal = new JLabel("0");
		aTotal.setFont(new Font("Tahoma", Font.PLAIN, 15));
		aTotal.setBounds(135, 171, 156, 16);
		summaryPanel.add(aTotal);

		JLabel lblProfitlossPerStock = new JLabel("Trading Summary");
		lblProfitlossPerStock.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblProfitlossPerStock.setBounds(848, 100, 232, 24);
		contentPane.add(lblProfitlossPerStock);

		JLabel lblTrainingDate = new JLabel("Training Date");
		lblTrainingDate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTrainingDate.setBounds(22, 318, 187, 24);
		contentPane.add(lblTrainingDate);

		trainingStart = new JTextField();
		trainingStart.setText("20170123");
		trainingStart.setToolTipText("Format: yyyymmdd");
		trainingStart.setBounds(22, 347, 89, 22);
		contentPane.add(trainingStart);
		trainingStart.setColumns(10);

		trainingEnd = new JTextField();
		trainingEnd.setText("20170127");
		trainingEnd.setToolTipText("Format: yyyymmdd");
		trainingEnd.setColumns(10);
		trainingEnd.setBounds(141, 347, 89, 22);
		contentPane.add(trainingEnd);

		JLabel lblTo = new JLabel("to");
		lblTo.setBounds(123, 350, 16, 16);
		contentPane.add(lblTo);

		JLabel label = new JLabel("to");
		label.setBounds(123, 414, 16, 16);
		contentPane.add(label);

		testingStart = new JTextField();
		testingStart.setText("20170130");
		testingStart.setToolTipText("Format: yyyymmdd");
		testingStart.setColumns(10);
		testingStart.setBounds(22, 411, 89, 22);
		contentPane.add(testingStart);

		testingEnd = new JTextField();
		testingEnd.setText("20170203");
		testingEnd.setToolTipText("Format: yyyymmdd");
		testingEnd.setColumns(10);
		testingEnd.setBounds(141, 411, 89, 22);
		contentPane.add(testingEnd);

		JLabel lblTestingDate = new JLabel("Testing Date");
		lblTestingDate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTestingDate.setBounds(22, 382, 187, 24);
		contentPane.add(lblTestingDate);

		JLabel lblInvestment = new JLabel("Investment");
		lblInvestment.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblInvestment.setBounds(22, 443, 89, 24);
		contentPane.add(lblInvestment);

		investmentAmount = new JTextField();
		investmentAmount.setText("50000.00");
		investmentAmount.setToolTipText("Amount you wish to trade");
		investmentAmount.setColumns(10);
		investmentAmount.setBounds(22, 473, 89, 22);
		contentPane.add(investmentAmount);

		maximumInvestmentPerStock = new JTextField();
		maximumInvestmentPerStock.setText("35.00");
		maximumInvestmentPerStock
				.setToolTipText("Maximum percentage of your portfolio you wish to trade in a particular stock");
		maximumInvestmentPerStock.setColumns(10);
		maximumInvestmentPerStock.setBounds(141, 473, 89, 22);
		contentPane.add(maximumInvestmentPerStock);

		JLabel lblMaximum = new JLabel("Max/Stock");
		lblMaximum.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblMaximum.setBounds(141, 443, 89, 24);
		contentPane.add(lblMaximum);

		JButton btnStartTrading = new JButton("Start Trading");
		btnStartTrading.setBounds(22, 530, 152, 25);
		contentPane.add(btnStartTrading);

		JButton btnUpdate = new JButton("Update Selections");
		btnUpdate.setBounds(22, 286, 152, 25);
		contentPane.add(btnUpdate);

		// Add Action Listener
		btnStartTrading.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				//Get the user inputs for amount, percentage and dates
				OriginalPortfolioValue = Double.parseDouble(investmentAmount.getText());
				MaximumPercentagePerStock = Double.parseDouble(maximumInvestmentPerStock.getText());
				trainFrom = Integer.parseInt(trainingStart.getText());
				trainTo = Integer.parseInt(trainingEnd.getText());
				testFrom = Integer.parseInt(testingStart.getText());
				testTo = Integer.parseInt(testingEnd.getText());
				
				try
				{
					//start the simulator
					startSimulator();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//perform trade using predicted data
				calculateTradingDataForWeekPredicted();
				
				//perform trade using actual data
				calculateTradingDataForWeekActual();
				
				//show the graph
				GraphPanel = createChartPanel();
				GraphPanel.setBounds(242, 134, 594, 421);
				contentPane.add(GraphPanel);
				
				//Enable the checkboxes so user can select/unselect 
				EnableCheckBoxes();
				
				//Print total for debug purpose
				System.out.println(actualTotal[4] + " " + predictedTotal[4]);
				
				//Display the trade summary on right pane
				DisplaySummary(predictedTotal[4], actualTotal[4], OriginalPortfolioValue);
			}

		});

		// Add Action Listener
		btnUpdate.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				
				//Set all stocks trade to false intially
				for (int i = 0; i < simulatorResults.SelectedStocksInformation.size(); i++)
				{
					simulatorResults.SelectedStocksInformation.get(i).setTrade(false);
				}

				//Store the selected stocks
				ArrayList<String> SelectedStocks = new ArrayList<String>();
				
				//Check which stocks are selected and add them to the list
				for (Component comp : StocksTrading.getComponents())
				{
					if (comp instanceof JCheckBox)
					{
						if (((JCheckBox) comp).isSelected())
						{
							String addToList = ((JCheckBox) comp).getText();
							SelectedStocks.add(addToList);
						}
					}
				}
				
				//Make trade true for all the selected stocks
				for (int j = 0; j < SelectedStocks.size(); j++)
				{
					for (int i = 0; i < simulatorResults.SelectedStocksInformation.size(); i++)
					{
						if (SelectedStocks.get(j)
								.equals(simulatorResults.SelectedStocksInformation.get(i).getStockName()))
						{
							simulatorResults.SelectedStocksInformation.get(i).setTrade(true);
						}
					}
				}
				
				// Perform the trade using Predicted Data
				calculateTradingDataForWeekPredicted();
				
				//Perform the trade using Actual Data
				calculateTradingDataForWeekActual();
				
				//Refresh the chart to show updated data
				refreshChart();
				
				//Display total for debug purpose
				DisplaySummary(predictedTotal[4], actualTotal[4], OriginalPortfolioValue);

			}

		});
	}

	/*
	 * 	function private void SetStockLists()
•	Gets lists of filenames of available stocks.
•	Dynamically draws checkboxes for each stock.

	 */
	private void SetStockLists()
	{

		// Add each list of available stocks to check box so user can select it
		for (String element : SelectedStocks)
		{
			JCheckBox addRemove = new JCheckBox(element);
			addRemove.setSelected(true);
			addRemove.setEnabled(false);
			checkboxesAddRemove.add(addRemove);
			StocksTrading.add(addRemove);
		}

	}
	
	/*
	 * 	function private void EnableCheckBoxes()
			Enables the checkboxes for user selection after the simulation ends so the user can perform water if analysis.

	 */

	private void EnableCheckBoxes()
	{
		for (JCheckBox checkBox : checkboxesAddRemove)
		{
			checkBox.setEnabled(true);
		}
	}
	
	/*
		function void startSimulator()
	•	Starts the simulation
	*/


	public void startSimulator() throws FileNotFoundException, IOException
	{

		// Maximum Portfolio Paramter
		simulatorResults.setOriginalPortfolioValue(OriginalPortfolioValue);

		// Maximum Percentage of Portfolio Per Stock
		simulatorResults.setMaximumPerStock(MaximumPercentagePerStock);

		// File Names
		String[] fileNames = new String[SelectedStocks.size()];
		fileNames = SelectedStocks.toArray(fileNames);

		// Training and Testing Data
		simulatorResults.getPredictionResults(trainFrom, trainTo, testFrom, testTo, fileNames);

		simulatorResults.PerformTradeForPredicted();
		simulatorResults.SummarizeTradingData();

	}

	/*
		function void calculateTradingDataForWeekPredicted()
	•	Calculates the final summary of trading simulation using predicted Data
	*/

	
	public void calculateTradingDataForWeekPredicted()
	{
		// get lists of StockResult
		ArrayList<StockResult> temp = simulatorResults.SelectedStocksInformation;
		
		//Stores current cash amount
		double CashAmount = OriginalPortfolioValue;
		
		//Loop through each day
		for (int i = 0; i < 5; i++)
		{
			double InvestedAmount = 0.00;

			//Loop through each stock
			for (int j = 0; j < temp.size(); j++)
			{
				if (temp.get(j).getTrade())
				{
					// In case of first day
					if (i == 0)
					{
						CashAmount = CashAmount - temp.get(j).getStartingStockPrice() * temp.get(j).getVolumeEachDay(i);

						InvestedAmount += temp.get(j).getResults(i) * temp.get(j).getVolumeEachDay(i);
					} else
						//In case of other days
					{
						
						//get volume of the day
						int volumeToday = temp.get(j).getVolumeEachDay(i);
						
						//get volume of previous day
						int volumeYesterday = temp.get(j).getVolumeEachDay(i - 1);

						//if volume is 0 and previous day wasn't 0, add sold amount to total cash amount
						if (volumeToday == 0)
						{
							if (volumeYesterday != 0)
							{
								CashAmount = CashAmount + temp.get(j).getResults(i - 1) * volumeYesterday;
							}
						} else if (volumeToday > volumeYesterday)
							
						//if volume today us greater than volume yesterday, deduct cash amount and add invested amount
						{
							CashAmount = CashAmount - temp.get(j).getResults(i - 1) * (volumeToday - volumeYesterday);
						}

						
						InvestedAmount += temp.get(j).getResults(i) * temp.get(j).getVolumeEachDay(i);
					}

				}
			}
			
			//Update the total for the day
			predictedTotal[i] = InvestedAmount + CashAmount;
		}
	}
/*
 * void calculateTradingDataForWeekActual()
•	Calculates the final summary of trading simulation using actual Data

 * 
 */
	public void calculateTradingDataForWeekActual()
	{
		ArrayList<StockResult> temp = simulatorResults.SelectedStocksInformation;
		double CashAmount = OriginalPortfolioValue;
		for (int i = 0; i < 5; i++)
		{
			double InvestedAmount = 0.00;

			for (int j = 0; j < temp.size(); j++)
			{
				if (temp.get(j).getTrade())
				{
					if (i == 0)
					{
						CashAmount = CashAmount - temp.get(j).getStartingStockPrice() * temp.get(j).getVolumeEachDay(i);

						InvestedAmount += temp.get(j).getActuals(i) * temp.get(j).getVolumeEachDay(i);
					} else
					{
						int volumeToday = temp.get(j).getVolumeEachDay(i);
						int volumeYesterday = temp.get(j).getVolumeEachDay(i - 1);

						if (volumeToday == 0)
						{
							if (volumeYesterday != 0)
							{
								CashAmount = CashAmount + temp.get(j).getActuals(i - 1) * volumeYesterday;
							}
						} else if (volumeToday > volumeYesterday)
						{
							CashAmount = CashAmount - temp.get(j).getActuals(i - 1) * (volumeToday - volumeYesterday);
						}

						InvestedAmount += temp.get(j).getActuals(i) * temp.get(j).getVolumeEachDay(i);
					}

				}
			}
			
			//Update the total for the day
			actualTotal[i] = InvestedAmount + CashAmount;
		}
	}
/*
 
function private JPanel createChartPanel()
•	Create a line charts with datasets created in CreateDataSet
•	Returns a Panel with the plotted chart

 
 */
	//Adapted from: http://www.codejava.net/java-se/graphics/using-jfreechart-to-draw-xy-line-chart-with-xydataset?ref=dzone
	private JPanel createChartPanel()
	{
		//Set the chart paramters
		String chartTitle = "Profit/Loss for the Week";
		String xAxisLabel = "Day";
		String yAxisLabel = "Profit or Loss";

		//Create the dataset
		XYDataset dataset = createDataset();

		//get the chart
		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

		//customize the chart
		customizeChart(chart);

		//return final panel
		return new ChartPanel(chart);
	}

/*
 
 private XYDataset createDataset()
•	Create dataset of (day, profit/loss) to plot the graph.
•	One each dataset of Predicted and Actual Results
•	Returns XYDateset of points to be plotted

 
 */
	
	//Adpated from: http://www.codejava.net/java-se/graphics/using-jfreechart-to-draw-xy-line-chart-with-xydataset?ref=dzone
	private XYDataset createDataset()
	{
		//create dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		//Create predicted and actual series
		XYSeries series1 = new XYSeries("Predicted");
		XYSeries series2 = new XYSeries("Actual");

		XYSeries[] series = { series1, series2 };

		//Populate the data set
		for (int j = 0; j < 5; j++)
		{
			series[0].add((j + 1), (predictedTotal[j]) - OriginalPortfolioValue);
			series[1].add((j + 1), (actualTotal[j]) - OriginalPortfolioValue);
		}

		//add the series
		dataset.addSeries(series1);
		dataset.addSeries(series2);

		//return the dataset
		return dataset;
	}
/*
 * private void customizeChart(JFreeChart chart)
		Customize the design line chart created in createChartPanel

 * 
 */
	//Adapted from: http://www.codejava.net/java-se/graphics/using-jfreechart-to-draw-xy-line-chart-with-xydataset?ref=dzone
	private void customizeChart(JFreeChart chart)
	{
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// sets paint color for each series
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);

		// sets thickness for series (using strokes)
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));

		// sets paint color for plot outlines
		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		// sets renderer for lines
		plot.setRenderer(renderer);

		// sets plot background
		plot.setBackgroundPaint(Color.DARK_GRAY);

		// sets paint color for the grid lines
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

	}

	// Imported from:
	// https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	/*
	 * private double round(double value, int places)
	•	Rounds the given double value of given decimal places
	•	Alawys 2 in case of this project

	 */
	public static double round(double value, int places)
	{
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	/*
	 
	 function private void refreshChart()
		•	Re-render the chart using updated datasets
		•	Dataset is updated when the user changes the checkbox selections

	 
	 */

	private void refreshChart()
	{
		// This removes the old chart
		GraphPanel.removeAll();
		GraphPanel.revalidate(); 
		
		//create new chart
		GraphPanel = createChartPanel();
		GraphPanel.setBounds(242, 134, 594, 421);
		contentPane.add(GraphPanel);
		
		// This method makes the new chart appear
		GraphPanel.repaint(); 
	}

	/*
	 * function private void DisplaySummary(double pT, double aT, double oT)
		Grabs the trading summary and displays it on the right panel

	 */
	
	private void DisplaySummary(double pT, double aT, double oT)
	{
		oInvestment.setText("" + round(oT, 2));

		pTotal.setText("" + round(pT, 2));
		aTotal.setText("" + round(aT, 2));

		pProfitLoss.setText("" + round((pT - oT), 2));
		aProfitLoss.setText("" + round((aT - oT), 2));

	}
}
