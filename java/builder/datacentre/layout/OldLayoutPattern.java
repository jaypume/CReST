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
package builder.datacentre.layout;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import sim.physical.bo.ServerWontFitException;
import utility.direction.CompassDirection;
import config.DatacentreGeneratorProgressListener;
import config.physical.ConfigAirCon;
import config.physical.ConfigAisle;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRack;
import config.physical.ConfigServer;
import builder.datacentre.layout.LayoutPatternFactory.LayoutPatternType;

public class OldLayoutPattern extends AbstractLayoutPattern
{
	// Constants
	private static final boolean ALTERNATE_AISLE_DIRECTION = true;

	public OldLayoutPattern()
	{
		super(LayoutPatternType.OLD_METHOD);
	}

	@Override
	public void generateLayout(ConfigDatacentre pGeneratedDatacentre, int[] pNetworkDistances, List<DatacentreGeneratorProgressListener> listeners)	
	{
		mGeneratedDatacentre = pGeneratedDatacentre;		
		mWidth = mGeneratedDatacentre.getDimX();
		mLength = mGeneratedDatacentre.getDimY();
		mTopAirCon = mGeneratedDatacentre.getAircon();
		mNetworkDistances = pNetworkDistances;
		// Randomiser
		r = new Random();		
		
		mAisles = (int) Math.floor((mLength - 1) / 2);
		mRacksnumber = mWidth - 2; // -2 to leave a gap at each end

		mServers = new ConfigServer[3];
		mServers[0] = new ConfigServer(1);
		mServers[1] = new ConfigServer(2);
		mServers[2] = new ConfigServer(3);
		mRacks = new ConfigRack[mServers.length];

		// Generate a rack for each given type of server, each populated with just that type server.
		for (int i = 0; i < mServers.length; i++)
		{
			mRacks[i] = new ConfigRack();
			mRacks[i].setNetworkDistance(mNetworkDistances[2]);
			// Fill with servers
			boolean filling = true;
			do
			{
				try
				{
					mRacks[i].addServer(mServers[i].duplicate());
				}
				catch (ServerWontFitException e)
				{
					// Rack full
					filling = false;
				}
			}
			while (filling);
		}

		// Do generation
		for (int i = 0; i < mAisles; i++)
		{
			ConfigAisle aisle = new ConfigAisle();
			aisle.setNetworkDistance(mNetworkDistances[1]);
			aisle.setAisleName("Aisle #" + i);
			aisle.setLocation(new Point(0, i * 2 + 1));
			mGeneratedDatacentre.addAisle(aisle);

			// If using aircon- start of aisle
			if (mTopAirCon)
			{
				ConfigAirCon air0 = new ConfigAirCon();
				air0.setLocation(new Point(0, 0));
				aisle.addAirCon(air0);
			}
			
			// If using aircon- end of aisle
			if (mTopAirCon)
			{
				ConfigAirCon airMax = new ConfigAirCon();
				airMax.setLocation(new Point(mWidth - 1, 0));
				aisle.addAirCon(airMax);
			}

			// Generate Racks
			for (int j = 1; j <= mRacksnumber; j++)
			{
				//Pick a random rack
				ConfigRack rack = mRacks[r.nextInt(mRacks.length)].duplicate(true);
				rack.setRackName("Rack #" + j);
				rack.setLocation(j);
				
				if(ALTERNATE_AISLE_DIRECTION && (i % 2 == 1))
				{
					rack.setServersDirection(CompassDirection.NORTH);
				} else {
					rack.setServersDirection(CompassDirection.SOUTH);
				}
				aisle.addRack(rack);
			}

			// Update
			double percent = ((double) i) / mAisles;
			for (DatacentreGeneratorProgressListener l : listeners)
			{
				l.updated(percent);
			}
		}
	}
}
