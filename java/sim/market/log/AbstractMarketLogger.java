/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012, 2013 John Cartlidge 
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
package sim.market.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

public abstract class AbstractMarketLogger {

	public static Logger logger = Logger.getLogger(AbstractMarketLogger.class);

	protected DecimalFormat format = new DecimalFormat("#.##");
	
	protected BufferedWriter resultsLog;
	
	protected String logFileName = "";
	
	protected int month;				// Month
	
	public AbstractMarketLogger(String name) {
		logFileName = name;
	}
	
	/**
	 * Get the name of the log file
	 * @return log file name
	 */
	public String getLogFileName() {
		return logFileName;
	}

	/**
	 * Get the title row (column headers) for the log
	 * @return column headers string
	 */
	protected abstract String getLogTitleString();
	
	/**
	 * Reset log values
	 */
	public abstract void resetValues();
	
	/**
	 * Write the log to file
	 */
	public abstract void writeLog();
	
	/**
	 * Initialise the LogManager for the module using the passed directory name.
	 * 
	 * @param dirName - the directory name of the module log file
	 */
	public void initLogs(String dirName) {
				
		try {
				String fileName = dirName+"_" + getLogFileName() + ".csv";
				File file = new File(fileName);
				
				// Try to create/open a file with the given filename.
		        // Write column headers to log file
		        try
		        {
		            resultsLog = new BufferedWriter(new FileWriter(file));
		            writeLogColumnHeaders(resultsLog);
		            logger.info("Created new results log: '" + fileName + "'");
		        } catch (IOException e)
	            {
	                logger.error("Could not find a suitable filename for the results logging module: " + e.getMessage());
	            }
			} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Write column headers to logfile.
	 * 
	 * @param pBW
	 *            the buffered writer to use to write to file.
	 */
	private void writeLogColumnHeaders(BufferedWriter pBW)
	{
	    try
	    {
	        pBW.append(getLogTitleString() + "\n");
	        pBW.flush();
	    }
	    catch (IOException e)
	    {
	        System.err.println();
	        e.printStackTrace();
	    }
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
}
