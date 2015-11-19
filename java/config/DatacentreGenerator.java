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
package config;

import java.awt.Point;
import java.util.*;

import builder.datacentre.LayoutMethod;
import builder.datacentre.layout.AbstractLayoutPattern;
import sim.physical.bo.ServerWontFitException;
import config.physical.*;

public class DatacentreGenerator extends Thread
{
	private List<DatacentreGeneratorProgressListener> listeners = new ArrayList<DatacentreGeneratorProgressListener>();

	private ConfigServer[] mServers;
	private ConfigRack[] mRacks;
	private int mAisles;
	private int mRacksnumber;
	private int[] mNetworkDistances;
	private int mWidth;
	private int mLength;
	private int mNumServers;
	private double mRatio;
	private LayoutMethod mLayoutMethod;
	private AbstractLayoutPattern mLayoutPattern;
	private String mName;
	private boolean mTopAirCon;
	private boolean mBottomAirCon;
	// private boolean mAirCon;

	protected boolean running;
	private Random r;
	private ConfigDatacentre generatedDatacentre;
	public static int mCalledFromView = 0; // where the generate was called from- 0 for ViewEdit, 1 for SimplePhysical

	/**
	 * Constructor for SimplePhysicalView
	 * 
	 * @param pServers
	 * @param pAisles
	 * @param pRacks
	 * @param pNetworkDistances
	 * @param pSizes
	 * @param pName
	 * @param pTopAirCon
	 * @param pBottomAirCon
	 * @param pPattern
	 * @param pCalledFromView
	 */
	public DatacentreGenerator(ConfigServer[] pServers, int pAisles, int pRacks, int[] pNetworkDistances, int[] pSizes, String pName, boolean pTopAirCon, boolean pBottomAirCon, AbstractLayoutPattern pPattern, int pCalledFromView)
	{
		mServers = pServers;
		mAisles = pAisles;
		mRacksnumber = pRacks;
		mNetworkDistances = pNetworkDistances;
		mWidth = pSizes[0];
		mLength = pSizes[1];
		mName = pName;
		mTopAirCon = pTopAirCon;
		mBottomAirCon = pBottomAirCon;
		mLayoutPattern = pPattern;
		mCalledFromView = pCalledFromView;
	};

	/**
	 * Constructor for ViewEditDatacentre View with Datacentre width + length
	 * 
	 * @param pWidth
	 * @param pLength
	 * @param pAircon
	 * @param pName
	 */
	public DatacentreGenerator(int pWidth, int pLength, boolean pAircon, AbstractLayoutPattern pPattern, int[] pNetworkDistances, String pName, int pCalledFromView)
	{
		mWidth = pWidth;
		mLength = pLength;
		mNumServers = ConfigDatacentre.DEFAULT_NUM_SERVERS; //set to default so they are not 0
		mRatio = ConfigDatacentre.DEFAULT_RATIO;
		// mAirCon = pAircon;
		mTopAirCon = pAircon;
		mBottomAirCon = pAircon;
		mLayoutPattern = pPattern;
		mNetworkDistances = pNetworkDistances;
		mName = pName;
		mCalledFromView = pCalledFromView;
		mLayoutMethod = LayoutMethod.DATACENTRE_SIZE;
	}

	/**
	 * Constructor for ViewEditDatacentre View with Datacentre num servers + ratio
	 * 
	 * @param pNumServers
	 * @param pRatio
	 * @param pAircon
	 * @param pPattern
	 * @param pNetworkDistances
	 * @param pName
	 */
	public DatacentreGenerator(int pNumServers, double pRatio, boolean pAircon, AbstractLayoutPattern pPattern, int[] pNetworkDistances, String pName, int pCalledFromView)
	{
		mNumServers = pNumServers;
		mRatio = pRatio;
		// mAirCon = pAircon;
		mTopAirCon = pAircon;
		mBottomAirCon = pAircon;
		mLayoutPattern = pPattern;
		mNetworkDistances = pNetworkDistances;
		mName = pName;
		mCalledFromView = pCalledFromView;
		mLayoutMethod = LayoutMethod.NUMBER_SERVERS;
	}

	/**
	 * Just run the setup with these values
	 * 
	 * @param Racks
	 * @param pAisles
	 * @param pRacks
	 * @param pNetworkDistances
	 * @param pSizes
	 * @param pName
	 * @param topAirCon
	 * @param bottomAirCon
	 * @param pPattern
	 */
	@Deprecated
	public DatacentreGenerator(ConfigRack[] Racks, int pAisles, int pRacks, int[] pNetworkDistances, int[] pSizes, String pName, boolean topAirCon, boolean bottomAirCon, AbstractLayoutPattern pPattern)
	{
		mRacks = Racks;
		mAisles = pAisles;
		mRacksnumber = pRacks;
		mNetworkDistances = pNetworkDistances;
		mWidth = pSizes[0];
		mLength = pSizes[1];
		mName = pName;
		mTopAirCon = topAirCon;
		mBottomAirCon = bottomAirCon;
		mLayoutPattern = pPattern;
	}

	/**
	 * Calculate the datacentre dimensions for the number of servers and ratio provided
	 */
	private void generateNumServersRatio()
	{
		//Calculate the number of servers that fit in a rack
		//TODO AS 15.8.12- is there a more sensible way to do this?
		ConfigRack rack = new ConfigRack();
		ConfigServer server = new ConfigServer(1);

		// Fill with servers
		boolean filling = true;
		do
		{
			try
			{
				rack.addServer(server.duplicate());
			}
			catch (ServerWontFitException e)
			{
				// Rack full
				filling = false;
			}
		}
		while (filling);
		int serversInRack = rack.getServers().length;
		
		
		//How many racks are needed for the required number of servers?
		int numRacks = (int) Math.ceil(mNumServers / (double) serversInRack);
		
		//Use quadratic forumula to find the datacentre length (num aisles)
		double a = mRatio;
		double b = -mRatio - 2;
		double c = -2 * numRacks;
		
		double discriminant = Math.pow(b, 2) - 4 * a * c;		
		
		double y1 = (-b + Math.sqrt(discriminant)) / (2 * a);
		double y2 = (-b - Math.sqrt(discriminant)) / (2 * a);
		
		//Find the positive root and round up
		int y;
		if (y1 > y2)
			y = (int) Math.ceil(y1);
		else
			y = (int) Math.ceil(y2);
		
		//If the shorter dimension is an even number, there will be 2 empty rows at the end. Remove extra row.
		if (y % 2 == 0)
			y--;
		
		//Make sure there is space for at least 1 aisle with gap either side
		if(y < 3) 
			y = 3;
		
		//Make sure that there are not more aisles than racks required!
		int ySpaceRequiredForRacks = numRacks * 2 + 1;
		if (y > ySpaceRequiredForRacks)
			y = ySpaceRequiredForRacks;
		
		//Number of aisles that fit in the shorter dimension
		int numAisles =  (int) (Math.floor((y - 1) / 2));
		
		//With this many aisles, how long does each aisle need to be to fit the number of racks
		int x = (int) Math.ceil(((double) numRacks / numAisles));
		x += 2; //space at each end to walk around
		

		//Set datacentre dimensions now we know what they should be
		generatedDatacentre.setDimX(x);
		generatedDatacentre.setDimY(y);
		
		mLayoutPattern.generateLayout(generatedDatacentre, mNetworkDistances, listeners);
	}

	private void generateOld()
	{
		// Randomiser
		r = new Random();
		
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
			// System.out.println("i:" + i + " size[0]:" + size[0] + " div:" + (i*2)/size[0] + " rounded:" + ((int) Math.floor((i*2)/size[0])));
			aisle.setLocation(new Point((getPhysicalAisleLength() + 2) * ((int) Math.floor((i * 2) / mLength)), (i % (mLength / 2)) * 2)); // TODO AS 25.7.12- How the hell does this work?!
			generatedDatacentre.addAisle(aisle);

			// Generate Racks
			for (int j = 0; j < getPhysicalAisleLength(); j++)
			{
				// Do AirCon
				if (j == 0 & mTopAirCon)
				{
					ConfigAirCon air0 = new ConfigAirCon();
					air0.setLocation(new Point(0, 0));
					aisle.addAirCon(air0);
				}
				else if (j == (getPhysicalAisleLength() - 1) && mBottomAirCon)
				{
					ConfigAirCon airMax = new ConfigAirCon();
					airMax.setLocation(new Point(getPhysicalAisleLength() - 1, 0));
					aisle.addAirCon(airMax);
				}
				else
				{
					// Pick a random rack
					ConfigRack rack = mRacks[r.nextInt(mRacks.length)].duplicate(true);
					rack.setRackName("Rack #" + j);
					rack.setLocation(j);
					aisle.addRack(rack);
				}
			}
			// Generate Aircon

			// Update
			double percent = ((double) i) / mAisles;
			for (DatacentreGeneratorProgressListener l : listeners)
			{
				l.updated(percent);
			}
		}
	}

	/**
	 * Returns the length of a Aisle including any air-con units
	 * 
	 * @return
	 */
	private int getPhysicalAisleLength()
	{
		int h = mRacksnumber;
		if (mBottomAirCon)
			h++;
		if (mTopAirCon)
			h++;
		return h;
	}

	public void run()
	{
		// Begin generation
		generatedDatacentre = new ConfigDatacentre();

		// Keep running
		running = true;

		// Set base values
		generatedDatacentre.setDatacentreName(mName);
		generatedDatacentre.setNetworkDistance(mNetworkDistances[0]);
		generatedDatacentre.setDimX(mWidth);
		generatedDatacentre.setDimY(mLength);
		generatedDatacentre.setNumServers(mNumServers);
		generatedDatacentre.setRatio(mRatio);
		generatedDatacentre.setLayoutMethod(mLayoutMethod);
		generatedDatacentre.setLayoutPattern(mLayoutPattern);
		generatedDatacentre.setAircon(mTopAirCon || mBottomAirCon);

		// Run generation method
		if (mLayoutMethod == LayoutMethod.DATACENTRE_SIZE)
		{
			mLayoutPattern.generateLayout(generatedDatacentre, mNetworkDistances, listeners);
		}
		else if (mLayoutMethod == LayoutMethod.NUMBER_SERVERS)
		{
			generateNumServersRatio();
		}
		else
		{
			// If no layout method, must be generating from SimplePhysicalView
			generateOld();
		}

		// Done!
		for (DatacentreGeneratorProgressListener l : listeners)
		{
			l.done(generatedDatacentre);
		}
	}

	public void stopGen()
	{
		running = false;
	}

	public void addGeneratorListener(DatacentreGeneratorProgressListener listener)
	{
		listeners.add(listener);
	}

	public ConfigDatacentre getGeneratedDatacentre()
	{
		return generatedDatacentre;
	}
}
