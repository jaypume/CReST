/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012 John Cartlidge 
 * 
 *   For a full list of contributors, refer to file CONTRIBUTORS.txt 
 *
 *   CReST was developed at the University of Bristol, UK, using 
 *   financial support from the UK's Engineering and Physical 
 *   Sciences Research Council (EPSRC) grant EP/H042644/1 entitled 
 *   "Cloud Computing for Large-Scale Complex IT Systems". Refer to
 *   <http://gow.epsrc.ac.uk/NGBOViewGrant.aspx?GrantRef=EP/H042644/1>
 * 
 *   CReST is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 *
 *   For further information, contact: 
 *
 *   Dr. John Cartlidge: john@john-cartlidge.co.uk
 *   Department of Computer Science,
 *   University of Bristol, The Merchant Venturers Building,
 *   Woodland Road, Bristol, BS8-1UB, United Kingdom.
 *
 */
package sim.module.demand.bo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import sim.probability.RandomSingleton;

/**
 * Class to manage the input of previous demand data and return whether a
 * higher/lower price should be given depending on demand
 * 
 */
public class Demand {
	public static Logger logger = Logger.getLogger(Demand.class);

	//Read demand from CSV file every time value is required, or use stored value
	//Useful if using multiple demand profiles, or a very large file, otherwise inefficient
	protected static boolean READ_DEMAND_FROM_FILE_EVERY_TIME = false;
	
	private String mDataFile; //Demand data file
	private int counter = 0; // Current position in file (row)
	private int requiredCategory = 39; // Default column
	
	
	public List<String> csvHeader; //TODO JPC Jul 2013 We don't need to save the whole list - just the title of the one we want
	private List<String> csvData;  //TODO JPC Jul 2013 This should be a list of doubles, surely?
	private List<String> csvDates;

	public Demand(String dataFile) {
		mDataFile = dataFile;
		readHeaderCSV();
	}

	/**
	 * Return the maximum number of months (rows) in the demand file
	 * @return number of months (rows)
	 */
	public int getMaxDemandMonths() {
		return csvData.size();
	}
	
	/**
	 * Creates new demand
	 * 
	 * @param datafile
	 *            is string of filename
	 * @param rCategory
	 *            is column number from file
	 */
	public Demand(String datafile, int rCategory) {
		mDataFile = datafile;
		requiredCategory = rCategory;

		logger.info("Reading Demand from data file: '" + mDataFile + "', demand profile column #"+requiredCategory);
		readHeaderCSV();
		readACategoryCSV(requiredCategory);
		logger.info("Demand profile header: " + csvHeader.get(requiredCategory));
	}

	/**
	 * Get demand for specific month
	 * 
	 * @param mon
	 * @return -1 if no more, or demand
	 */
	public double getDemand(int mon) {
		
		if (READ_DEMAND_FROM_FILE_EVERY_TIME)
			readACategoryCSV(requiredCategory);

		double actualDemand = 0;
		try {
			if (counter >= csvData.size()) {
				return -1;
			}
			String out = csvData.get(mon);
			actualDemand = Double.parseDouble(out);
			return actualDemand;

		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Month not found " + e);
			return -1;
		}
	}

	/**
	 * Randomises the demand actualDemand by +/- variance ratio
	 * 
	 * @param actualDemand
	 * @param variance
	 * @return the new demand
	 */
	public double randomiseDemand(double actualDemand, double variance) {
		double lower = (1 - variance) * actualDemand;
		double range = 2 * variance * actualDemand;
		
		logger.debug("Demand: " + actualDemand);

		double newDemand = (lower + RandomSingleton.getInstance().randomDouble() * range);
		if (newDemand > 1) {
			newDemand = 1.0;
		}
		else if (newDemand < 0) {
			newDemand = 0.0;
		}
		
		return newDemand;
	}

	/**
	 * Moves the demand forward by one time period Used where multiple users
	 * might share one demandProfile and so each user should not move demand
	 * forward in isolation
	 */
	public void advanceDemand() {
		
		//TODO: JPC Jul 2013 - the only thing this appears to do, is increment counter!
		// Let's just do that instead!
		if (READ_DEMAND_FROM_FILE_EVERY_TIME)
			readACategoryCSV(requiredCategory);
		
		double actualDemand = 0;
		try {
			String out = csvData.get(counter);
			counter++; 
			actualDemand = Double.parseDouble(out); //TODO: JPC Jul 2013 - Clean up, this is never used!

			logger.info(csvDates.get(counter) + " Demand = " + actualDemand);
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Month not found " + e);
		}
	}
	
	public List<Double> getDemandList() {
		
		if (READ_DEMAND_FROM_FILE_EVERY_TIME)
			readACategoryCSV(requiredCategory);
		
		List<Double> convertedDemand = new ArrayList<Double>();
		for (String st : csvData) {
			convertedDemand.add(Double.parseDouble(st));
		}
		
		return convertedDemand;
	}
	
	public void reverseDemand(int months) {
		
		if (READ_DEMAND_FROM_FILE_EVERY_TIME)
			readACategoryCSV(requiredCategory);

		counter -= months;
		
		double actualDemand = 0;
		try {
			String out = csvData.get(counter);
			counter++;
			actualDemand = Double.parseDouble(out);

			logger.info("Demand: " + actualDemand);
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Month not found " + e);
		}
	}

	/**
	 * Get the current demand from the demand file
	 * 
	 * @return current demand, -1 if end of demand file
	 */
	public double getCurrentDemand() {
		try {
			logger.debug("Getting current demand from file: " + mDataFile + ". Row counter = " + counter);
			String out = csvData.get(counter);
			logger.debug("Row counter = " + counter+ ". Returning current demand: " + out);
			return (Double.parseDouble(out));
		} catch (Exception e) {
			logger.warn("Demand data, end of file.  Not returning new demand.");
			return -1;
		}
	}
	
	public String getCurrentDate() {
		try {
			String date = csvDates.get(counter);
			return date;
		} catch (Exception e) {
			logger.info("Demand data, end of file.  Not returning Date.");
			return "";
		}
	}

	/**
	 * Moves demand forward one time period and returns demand
	 */
	public double nextD() {
		
		if (READ_DEMAND_FROM_FILE_EVERY_TIME)
			readACategoryCSV(requiredCategory);
		
		logger.info(csvData.size() + " Data items in Category "
				+ requiredCategory + "- " + csvHeader.get(requiredCategory) /*
																			 * ":\n"
																			 * +
																			 * csvData
																			 */);

		double actualDemand = 0;
		try {
			if (counter >= csvData.size()) {
				return -1;
			}

			String out = csvData.get(counter);
			counter++;
			actualDemand = Double.parseDouble(out);

			logger.debug("Demand: " + actualDemand);

			return (actualDemand);

		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Month not found " + e);
			return -1;
		}

	}

	/**
	 * Returns current index(row/timeperiod) of demandfile
	 */
	public int getCounter() {
		return counter - 1;
	}

	/**
	 * Reads the first row of the CSV file containing the categories
	 * 
	 * @author Alex Sheppard
	 * @param name
	 *            CSV filename
	 * @return Arraylist containing the row category names
	 */
	private void readHeaderCSV() {
		try {
			// Set up to read a file
			File file = new File(mDataFile);
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line = "";
			StringTokenizer stringtoken = null;
			@SuppressWarnings("unused")
			int tokenNumber = 0;

			if ((line = reader.readLine()) != null) {
				// arraylist to hold the strings in this line of the file
				csvHeader = new ArrayList<String>();

				// look through the line, and split up strings by ','
				stringtoken = new StringTokenizer(line, ",");
				stringtoken.nextToken();
				tokenNumber++;

				while (stringtoken.hasMoreTokens()) {
					tokenNumber++;

					String temp = stringtoken.nextToken();
					temp = temp.replace("\\c", ",");
					temp = temp.replace("\\n", "\n");
					csvHeader.add(temp);
					logger.debug("Demand header: " + temp);
					// if (CloudSimApp.debugSatsuma)
					// System.out.println("Token # " + tokenNumber +
					// ", Token : "+ csvHeader.get(tokenNumber-1));
				}

				// if (CloudSimApp.debugSatsuma) System.out.println(csvHeader);
			}
			logger.info("Demand profile headers: " + csvHeader);
			reader.close();

		} catch (Exception e) {
			logger.error("Error reading headers in file: " + e);
		}
	}

	//TODO: Can't we save this to memory?
	/**
	 * Reads the demand data for the given category
	 * 
	 * @author Alex Sheppard
	 * @param requiredCategory
	 *            category to read data from
	 */
	private void readACategoryCSV(int requiredCategory) {
		try {
			File file = new File(mDataFile);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			logger.info("Reading demand profile #" + requiredCategory + " from: " + mDataFile);
			
			String line = "";
			StringTokenizer stringtoken = null;
			int tokenNumber = 0;

			csvData = new ArrayList<String>();
			csvDates = new ArrayList<String>();
			
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				stringtoken = new StringTokenizer(line, ",");
				csvDates.add(stringtoken.nextToken());
				
				while (tokenNumber < requiredCategory) { //BUG Fixed 03/07/2013 JPC! Was originally <=, should be strictly less than!
					stringtoken.nextToken();
					tokenNumber++;
				}

				String temp = stringtoken.nextToken();
				temp = temp.replace("\\c", ",");
				temp = temp.replace("\\n", "\n");

				csvData.add(temp);

				tokenNumber = 0;
			}
			reader.close();
			
			logger.debug("Demand for profile: " + csvHeader.get(requiredCategory));
			logger.debug(csvData);
			
		} catch (Exception e) {
			logger.error("Error reading data from file: " + e);
		}
	}
	
	public void reset() {
		this.counter = 0;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public void setRequiredCategory(int category) {
		this.requiredCategory = category;
	}
	
	/**
	 * Old method that reads a CSV file into memory
	 * 
	 * @author Alex Sheppard
	 * @param name
	 *            CSV filename
	 */
	@SuppressWarnings(value = { "unused" })
	@Deprecated
	private double readWholeCSV(String name) {
		try {
			// Set up to read a file
			File file = new File(name);
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line = "";
			StringTokenizer stringtoken = null;
			int tokenNumber = 0, lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				// arraylist to hold the strings in this line of the file
				ArrayList<String> csv = new ArrayList<String>();

				// look through the line, and split up strings by ','
				stringtoken = new StringTokenizer(line, ",");
				while (stringtoken.hasMoreTokens()) {
					tokenNumber++;

					String temp = stringtoken.nextToken();
					temp = temp.replace("\\c", ",");
					temp = temp.replace("\\n", "\n");
					csv.add(temp);
					logger.debug("Line # " + lineNumber + ", Token # "
							+ tokenNumber + ", Token : "
							+ csv.get(tokenNumber - 1));
				}

				/*
				 * At the end of the line, turn the arraylist into a string
				 * array, and add the record using this string array,like before
				 */
				String[] str = new String[csv.size()];
				csv.toArray(str);
				// newRecord = new Record(str);
				// table.add(newRecord);
				logger.debug(csv);

				tokenNumber = 0;
				lineNumber++;
			}
			reader.close();
		} catch (Exception e) {
			logger.error("Error reading file: " + e);
		}
		return 1;
	}

	@Override
	public String toString() {
		return "Demand [csvData=" + csvData + ", csvDates=" + csvDates + "]";
	}
}	
