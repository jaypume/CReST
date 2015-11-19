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
package builder.replacements;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import builder.MainWindow;

import sim.physical.bo.ServerWontFitException;
import utility.time.LengthOfTime;

import config.physical.ConfigAisle;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRack;
import config.physical.ConfigServer;
import config.physical.ConfigServerType;

/**
 * Static class that performs the builder failures, to 'age' the designed datacentre.
 * Produces an aged instance of the original datacentre.
 *
 */
public class ReplacementServers
{
	public static Logger logger = Logger.getLogger(ReplacementServers.class);
	
	private static MainWindow MainWindow;
	private static ConfigDatacentre mGeneratedDatacentre;

	private static long mAgeTime = 200;
	private static LengthOfTime mAgeTimeUnit = LengthOfTime.DAY; //Not used yet
	
	public static long getAgeTime()
	{
		return mAgeTime;
	}

	public static LengthOfTime getAgeTimeUnit()
	{
		return mAgeTimeUnit;
	}

	@Deprecated
	public static void init(ConfigDatacentre pWorkingDC)
	{
		mGeneratedDatacentre = pWorkingDC;
	}
	
	/**
	 * Perform failures to 'age' the datacentre
	 * @param pMainWindow 
	 */
	public static void update(ConfigDatacentre pWorkingDC, MainWindow pMainWindow)
	{
		MainWindow = pMainWindow;
		mGeneratedDatacentre = pWorkingDC;
		
		//for each rack
		//calculate mean fail time
			//loop through each server
		
		//move to point in time
		//find racks with fail time less than current
		
		//replace rack
			//fill with replacement servers
		
		calculateRackFailTime();
		//checkRacks();
		
		//Update UI
		mGeneratedDatacentre.setDatacentreName("Aged " + pWorkingDC.getDatacentreName());
//		MainWindow.getmViewEdit().updateAvailableDCList();
		
		logger.info("Ageing done");
	}
	
	/**
	 * Calculates the mean failtime of each rack
	 * Checks if existing value exists, or calculates
	 */
	private static void calculateRackFailTime()
	{
		ConfigAisle[] aisles = mGeneratedDatacentre.getAisles();
		
		for (ConfigAisle aisle : aisles)
		{
			ConfigRack[] racks = aisle.getRacks();
			for (ConfigRack rack : racks)
			{
				long rackFailTime = rack.getMeanFailTime();
				if (rackFailTime != -1)
				{
					//If a rack changes after being calculated once, failtime not recalculated
					//TODO AS 27.7.12- calculate as servers are added, or calculate every time
					//logger.info("Mean Rack failtime: " + rackFailTime);
				}
//				else
				{
					//Calculate the failtime of each rack by looping through each server in the rack
					rackFailTime = 0;
					ConfigServer[] servers = rack.getServers();
					for (ConfigServer server : servers)
					{
						rackFailTime += server.getMeanFailTime();
					}
					rackFailTime /= servers.length;
					rack.setMeanFailTime(rackFailTime);
					logger.info("Mean Rack failtime not set, calculated as: " + rackFailTime);
					
					checkRack(aisle, rack);					
				}
			}
		}
	}
	
	/**
	 * Checks through each rack to see if it needs replacing 
	 * and carries out the replacement
	 */
	@SuppressWarnings("unused")
	private static void checkRacks()
	{
		ConfigAisle[] aisles = mGeneratedDatacentre.getAisles();
		
		for (ConfigAisle aisle : aisles)
		{
			ConfigRack[] racks = aisle.getRacks();
			for (ConfigRack rack : racks)
			{
				long rackFailTime = rack.getMeanFailTime();
				if (rackFailTime <= mAgeTime)
				{
					//logger.info("Mean Rack failtime less than sim time: " + rackFailTime + ". Replacing");					
					
					ConfigRack newRack = replaceRack(rack);
					aisle.removeRack(rack);
					aisle.addRack(newRack);
				}
				else
				{
					//logger.info("Mean Rack failtime in the future: " + rackFailTime + ". Not replacing");
				}
			}
		}
	}
	
	/**
	 * Checks if the given rack needs replacing
	 * and carries out the replacement
	 * 
	 * @param pAisle the current aisle
	 * @param pRack the current rack
	 */
	private static void checkRack(ConfigAisle pAisle, ConfigRack pRack)
	{		
		long rackFailTime = pRack.getMeanFailTime();
		if (rackFailTime <= mAgeTime) //TODO AS 27.7.12- use a distribution to calculate probability of failure
		{
			//logger.info("Mean Rack failtime less than sim time: " + rackFailTime + ". Replacing");					
			
			ConfigRack newRack = replaceRack(pRack);
			pAisle.removeRack(pRack);
			pAisle.addRack(newRack);
		}
		else
		{
			//logger.info("Mean Rack failtime in the future: " + rackFailTime + ". Not replacing");
		}
	}
	
	/**
	 * Replaces the input rack with a new rack containing replacement servers
	 * 
	 * @param pRack the rack to replace
	 * @return the new replacement rack
	 */
	private static ConfigRack replaceRack(ConfigRack pRack)
	{		
		//Create new rack with same basic info as old rack
		ConfigRack rack = pRack.duplicate(false);
		rack.setRackName("Replacement " + pRack.getRackName());		

		//Get replacement servers
		List<ConfigServerType> replacementServers = MainWindow.getEditConfig().getReplacements();
		Random r = new Random();
		ConfigServer server = replacementServers.get(r.nextInt(replacementServers.size())).getConfigServerObject();
		
		//Fill with new servers
		boolean filling = true;
		do{
			try
			{
				rack.addServer(server.duplicate());
			} catch (ServerWontFitException e)
			{
				//Rack full
				filling = false;
			}
		}while(filling);
		
		return rack;
	}
}
