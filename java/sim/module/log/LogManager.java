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
package sim.module.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.log.event.LogEvent;
import sim.physical.World;

/**
 * Singleton manager class which will handle all printing of logs.
 */
public class LogManager
{
	//public static final long FIRST_LOG_EVENT_TIME = TimeManager.secondsToSimulationTime(1); //do the first log after 1 minute
	//public static final long TIME_BETWEEN_LOGS = TimeManager.daysToSimulationTime(28); //every hour
	
	private DecimalFormat df = new DecimalFormat("000");
	
	//log4j logger
	public static Logger logger = Logger.getLogger(LogManager.class);
	
    // Singleton instance.
    @SuppressWarnings("unused")
	private static LogManager instance = null;

    protected String logDirName = "";

    // Member variables.
//    private BufferedWriter    mFileLog;
    private ArrayList<BufferedWriter> resultsLogs;

    protected int dirIterator = 1;
    
    /**
     * Constructor to create log manager and prepare each type of logging module
     * (e.g. by opening files).
     */
    public LogManager()
    {
////        prepareLog();
//        prepareLogNewWay();
    }
    
    /**
     * Prepare a log files for results data
     * 
     * WARNING This will create a new log file
     */
    @Deprecated
    public void prepareLog()
    {
    	final String dirPrefix = "log/results";
    	int dirIterator = 1;
    	
    	try {
    		//create results directory	
    		while (( new File(dirPrefix+df.format(dirIterator)) ).mkdir() != true) {
    			dirIterator++;
    		}
    		logger.info("Created new results directory: '" + dirPrefix+df.format(dirIterator) + "'");
    		
    		//create log files (one for each data_centre)
    		int numDCs = -1;
    		numDCs = World.getInstance().getNumberOfDatacentres();
    		
    		resultsLogs = new ArrayList<BufferedWriter>();
    		
    		
    		for(int i=0; i< numDCs; i++) {
    			String fileName = dirPrefix+df.format(dirIterator)+"/run"+df.format(dirIterator)+"_dc"+ i + "_"+World.getInstance().getDatacentre(i).getName() + ".csv";
    			File file = new File(fileName);
    			
    			// Try and create/open a file with the given filename.
    	        // Write column headers to log file
    	        try
    	        {
    	            resultsLogs.add(new BufferedWriter(new FileWriter(file)));
    	            writeLogColumnHeaders(resultsLogs.get(i));
    	            logger.info("Created new results log: '" + fileName + "'");
    	        } catch (IOException e)
	            {
	                logger.error("Could not find a suitable filename for the failure logging module: " + e.getMessage());
	            }
    		}
    	} catch (Exception e) {
    		logger.error(e);
    	}
    }
    
    public String getLogResultsDirName() {
    	return logDirName;
    }
    
    public int getLogResultsNumber() {
    	return dirIterator;
    }
    
    public String getLogResultsNumberString() {
    	return df.format(dirIterator);
    }
    
    /**
     * Prepare a log files for results data
     * 
     * WARNING This will create a new log file
     */
    public void prepareLogFiles()
    {
    	logger.info("Preparing log files...");
    	
    	final String dirPrefix = "log/results";
		dirIterator = 1;
    	
    	try {
    		//create results directory	
    		while (( new File(dirPrefix+df.format(dirIterator)) ).mkdir() != true) {
    			dirIterator++;
    		}
    		logger.info("Created new results directory: '" + dirPrefix+df.format(dirIterator) + "'");

    		//create log files for each ModuleRunner...
    		logDirName = dirPrefix+df.format(dirIterator);
    		
    		//For each Module, if Module is active, initialise the Module log.
    		for(AbstractModuleRunner moduleRunner: AbstractModuleRunner.getModuleRunners()) {
    			if(moduleRunner.isActive()) {
    				logger.info(moduleRunner + " is active.  Initialising Logs...");
    				moduleRunner.initLogs(logDirName+"/run"+df.format(dirIterator));
    			}
    		}
    		
    		logger.info("Log files created.");
    		
    	} catch (Exception e) {
    		logger.error(e);
    	}
    }

    /**
     * Write the given log to file.
     * @param dc_number - the number of the datacentre we are logging
     * @param pLog - the log to write to file.
     */
    public void log(int dc_number, final Log pLog)
    {
        doLog(resultsLogs.get(dc_number), pLog.toString());
    }

    /**
     * Write the given log to file.
     * 
     * @param buffWriter - the buffered writer for the logger
     * 
     * @param pLog - the log to write to file.
     */
    public static void writeLog(BufferedWriter buffWriter, final Log pLog)
    {
        try
        {
            buffWriter.append(pLog + "\n");
            buffWriter.flush();
        }
        catch (IOException e)
        {
            System.err.println();
            logger.error("Error writing log: " + e.getMessage());
        }
    }
    
    /**
     * Write a given log to file using the given BufferedWriter.
     * 
     * @param pBW
     *            the buffered writer to use to write to file.
     * @param pLog
     *            The log to write to file.
     */
    private void doLog(BufferedWriter pBW, final String pLog)
    {
        try
        {
            pBW.append(pLog + "\n");
            pBW.flush();
        }
        catch (IOException e)
        {
            System.err.println();
            e.printStackTrace();
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
            pBW.append(LogEvent.columnTitleString + "\n");
            pBW.flush();
        }
        catch (IOException e)
        {
            System.err.println();
            e.printStackTrace();
        }
    }

}
