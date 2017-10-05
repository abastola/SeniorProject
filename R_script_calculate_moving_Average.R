#####################################################################################################
# File Description

# FILE NAME:
#
#         R_script_calculate_moving_Average.R

# NAME
#
#         R_script_calculate_moving_Average.R

# SYNOPSIS
#
#         function Moving_Average(data, numberofDays) <- Calculates Moving Averages and returns a list
#         function CaclculateMovingAveragesOfStocks   <- Grabs all available stock csvs.
#                                                        Calculates MAs and ouputs CSVs with MAs
#         Dir./ouput  <- includes all final output of each csv files
#         ./stock.txt <- inlcudes names of all available stocks

# DESCRIPTION
#
#         Grabs csvs files from the input folder
#         Caclulates Moving Averages for 2, 3, 5, 7, 10 Days
#         Adds those to the csv files and ouputs a finalized csv files of each stock
#         Also outputs a stock.txt files with all available stocks

# RETURNS
#
#        Not Applicable

# AUTHOR
#
#       Arjun Bastola

# DATE

#       6:07 PM 02/29/2017

#####################################################################################################

#####################################################################################################
# Function Description:
# FUNCTION NAME:
#
#         CaclculateMovingAveragesOfStocks

# SYNOPSIS
#
#         stock_filelists        <- list of all available csv files

# DESCRIPTION
#
#         Calculates moving averages for 2, 3, 5, 7, and 10 days
#         Ouputs new csv file which including above mentioned MAs for each stocks

# RETURNS
#
#        None

# AUTHOR
#
#       Arjun Bastola

# DATE

#       6:17 PM 02/29/2017

#####################################################################################################



Moving_Average <- function(data, numberOfDays)
{
  stats::filter(data, rep(1 / numberOfDays, numberOfDays), sides = 1)
}


#####################################################################################################
# Function Description:
# FUNCTION NAME:
#
#         Moving_Average

# SYNOPSIS
#
#         data         <- raw data that contains adjusted close value of stocks
#         numberofDays <- number of days for which moving average should be calculated

# DESCRIPTION
#
#         Calculates moving average for n number of days and returns a list

# RETURNS
#
#        List of moving averages

# AUTHOR
#
#       Arjun Bastola

# DATE

#       6:015 PM 02/29/2017

#####################################################################################################


CaclculateMovingAveragesOfStocks <- function (stockfiles_list)
{
  # Loop through each file
  # Grab Header from each file
  # Temporarily store the header
  # Import csv file as a data frame
  # Skip first line
  # Reverse the data frame so the dates are in decreasing order
  # Add headers to the data frame
  # Subset "Date" and "AdjustedClose" Columns
  
  # Store names of all stocks so a text file of stock names can be created
  # This will make our life easier when populating stocks on our Java Application
  
  # Initialize filenames vector
  
  FileNames = c()
  
  # for (i in 1:length(stockfiles_list))
  for (i in 1:15)
  {
    file_name             <-
      paste(".\\input\\", stockfiles_list[i], sep = "")
    file_ouput_name       <- stockfiles_list[i]
    
    # Add file_ouput_name to Filenames list
    FileNames = c(FileNames, file_ouput_name)
    
    file_header           <-
      read.csv(
        file_name,
        skip = 1,
        header = F,
        nrows = 1,
        as.is = T
      )
    stock_df              <-
      read.csv(file_name, skip = 2, header = F)
    stock_df              <- stock_df[seq(dim(stock_df)[1], 1), ]
    colnames(stock_df)    <- file_header
    stock_df              <- stock_df[, c(1, 7)]
    
    # Calculate 2 Day, 3 Day, 5 Day, 7 Day, 10 Day Moving Average
    
    Moving_Average_2Day   <- Moving_Average(stock_df[, 2], 2)
    Moving_Average_3Day   <- Moving_Average(stock_df[, 2], 3)
    Moving_Average_5Day   <- Moving_Average(stock_df[, 2], 5)
    Moving_Average_7Day   <- Moving_Average(stock_df[, 2], 7)
    Moving_Average_10Day  <- Moving_Average(stock_df[, 2], 10)
    
    # Add Moving Average Columns to stock_df data frame
    
    stock_df              <-
      cbind(stock_df, as.data.frame(Moving_Average_2Day))
    stock_df              <-
      cbind(stock_df, as.data.frame(Moving_Average_3Day))
    stock_df              <-
      cbind(stock_df, as.data.frame(Moving_Average_5Day))
    stock_df              <-
      cbind(stock_df, as.data.frame(Moving_Average_7Day))
    stock_df              <-
      cbind(stock_df, as.data.frame(Moving_Average_10Day))
    
    # Add column headers for moving averages
    
    colnames(stock_df)    <-
      c("DATE",
        "AdjustedClosing",
        "MA2",
        "MA3",
        "MA5",
        "MA7",
        "MA10")
    
    # Write csv file to hard drive so Java Part can use it
    
    write.csv(stock_df,
              paste(".\\src\\", file_ouput_name, sep = ""),
              row.names = F)
  }
  
  # Create a text file of list of all stocks available
  
  write(FileNames, file = "stock.txt", sep = ", ")
  
}

#####################################################################################################

# Get Names of All  Available CSV Files

files_list       <-
  list.files(path = ".\\input", pattern = "*.csv")

# Create csv file of each stock with moving averages

CaclculateMovingAveragesOfStocks(files_list)
