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
/**
 * @created 19 Jul 2011
 */
package sim.physical;

import java.sql.Time;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import sim.module.failure.event.FailureEvent.ObjectType;
import sim.module.log.LogManager;
import sim.physical.network.IP;
import utility.time.TimeManager;

/**
 * Class representing the simulation world.
 * 
 * Singleton class. There should only be one world instantiated at any point in time.
 */
public class World
{
	static Logger logger = Logger.getLogger(World.class);
	
    private static final int     INTRA_WORLD_NETWORK_DISTANCE = 1000;

    // Singleton.
    private static World         instance                     = null;

    // Member variables.
    public ArrayList<Datacentre> mDatacentres                 = new ArrayList<Datacentre>();
    private int                  mIntraWorldNetworkDistance;
    private long                 mTime = 0;
    
    private LogManager			 logManager;

    /**
     * Constructor.
     * 
     * @param pNetDistance the network distance between each datacentre in this world.
     */
    private World(int pNetDistance)
    {
        mTime = 0;
        mIntraWorldNetworkDistance = pNetDistance;
    }
    
    /**
     * Replaces the current instance of the singleton World object with a new one.
     * 
     * @WARNING This will overwrite the current instance of the world! Make sure
     *          this is what you want to do before you call it.
     * 
     * @return the new World object.
     */
    protected static World create(int pNetDistance)
    {
    	logger.info("Creating world...");
        instance = new World(pNetDistance);
        logger.info("New world created");
        return instance;
    }

    /**
     * Get the singleton World object.
     * 
     * WARNING This method will create a basic world if one has not already been instantiated.
     * 
     * @return the singleton World object.
     */
    public static World getInstance()
    {
        if (instance == null)
        {
        	logger.info("World instance is null.  Creating new world...");
            create(INTRA_WORLD_NETWORK_DISTANCE);
        }

        return instance;
    }
    
    /**
     * Destroy singleton World object.
     * 
     * WARNING This method will destroy world object.  Use with care!!!
     *
     * @return true if world is destroyed, false if no world object exists to destroy
     */
    public boolean destroy()
    {
    	logger.debug("Destroying world object!");
    	if(instance != null) {
    		logger.debug("Removing datacentres...");
    		mDatacentres.clear();
    	    logger.debug("Listing Datacentres...");
    	    for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
    	    	logger.info(World.getInstance().getDatacentre(i));
    	    }
    	    logger.debug("Resetting time...");
    	    mTime = 0;
    	    logger.debug("Time is now: " + TimeManager.getTimeString());
    	    
    		instance = null;
    		return true;   		
    	} else {
    		logger.info("No world object exists to destroy...");
    		return false;
    	} 		
    }
    
    /**
     * Clears the datacentres from the singleton World object
     * 
     * @return true if world is cleared, false if no world object exists to clear
     */
    public boolean clear()
    {
    	logger.debug("Clearing world object!");
    	if(instance != null) {
    		logger.debug("Removing datacentres...");
    		mDatacentres.clear();
    	    logger.debug("Listing Datacentres...");
    	    for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
    	    	logger.info(World.getInstance().getDatacentre(i));
    	    }
    	    return true;
    	} else {
    		logger.info("No world object exists to clear...");
    		return false;
    	} 	
    }
    
    /**
     * Get the singleton World object.
     * 
     * WARNING This method will create a basic world if one has not already been instantiated.
     * 
     * @return the singleton World object.
     */
    public static World getInstance(int pNetDistance)
    {
        if (instance == null)
        {
        	logger.warn("World instance is null.  Creating new world...");
            create(pNetDistance);
        }

        return instance;
    }

    /**
     * Get the total number of servers in the world.
     * 
     * @return the number total of servers in the world.
     */
    public int getNumServers()
    {
        int num = 0;
        
        for (Datacentre data : mDatacentres)
        {
            num += data.getNumServers();
        }

        return num;
    }

    /**
     * Get the number of servers that have failed in the world.
     * 
     * @return the number of failed servers in this world.
     */
    public int getNumFailedServers()
    {
        int total = 0;

        for (Datacentre dc : mDatacentres)
        {
            total += dc.getNumFailedServers();
        }

        return total;
    }

	/**
     * Create Log Manager for this world
     * 
     * WARNING This will create a new log file
     * 
     */
    public void createLogManager() {
    	logManager = new LogManager();
    }

    //TODO - JC May 2012 - The World object should not be responsible for logging. Move to Module.
	/**
     * Get the LogManager for this world
     * 
     * @return log manager
     */
    public LogManager getLogManager() {
    	return logManager;
    }
    
    /**
     * Add the given datacentre to the world.
     * 
     * @param pDC the data centre to add to the world.
     */
    public void addDatacentre(Datacentre pDC)
    {
        this.mDatacentres.add(pDC);
    }

    /**
     * Remove the given datacentre from the world.
     * 
     * @param pDC the datacentre to remove from the world.
     */
    public void removeDatacentre(Datacentre pDC)
    {
        this.mDatacentres.remove(pDC);
    }

    /**
     * Returns the server with the given ID, null if it does not exist. ID
     * represents server location as if all servers in all racks in all aisles
     * in all datacentres were rolled out into a line.
     * 
     * @param pID
     *            The ID of the server to fetch.
     * @return The server corresponding the to given ID, or null if it does not
     *         exist.
     */
    public Server getServer(final int pID)
    {
        Server server = null;
        int seenServers = 0;

        for (Datacentre dc : mDatacentres)
        {
            final int localID = pID - seenServers;
            seenServers += dc.getNumServers();

            if (pID < seenServers)
            {
                server = dc.getServer(localID);
                break;
            }
        }

        return server;
    }

    // TODO - JC May 2012: Should the World really have the time class?  Perhaps it should.
    /**
     * Set the current simulation time to a new value.
     * 
     * WARNING This will directly affect the simulation and should only be used
     *          to update time after an event has occurred.
     * 
     * @param pNewTime
     *            the new current time.
     */
    public void setTime(final long pNewTime)
    {
        mTime = pNewTime;
    }

    /**
     * Get the current simulation time.
     * 
     * @return the current simulation time.
     */
    public long getTime()
    {
        return mTime;
    }
    
    public Time getTimeClass() {
    	Time time = new Time(mTime);
    	return time;
    }

    /**
     * TODO Luke Sep 2011: This is both a mess and massively inefficient. Fine if called every
     * once in a while, but not if it is called often. Needs to be tidied up and
     * put into a generic method for each level of server organisation to use.
     * 
     * @param pServerIDA
     *            the first server
     * @param pServerIDB
     *            the second server
     * @return the network distance between the two given servers.
     */
    public int calcNetworkDistance(final int pServerIDA, final int pServerIDB)
    {
        int distance = 0;
        int seenServers = 0;
        boolean serverAFound = false, serverBFound = false;

        for (Datacentre hardware : mDatacentres)
        {
            final int localIDA = pServerIDA - seenServers;
            final int localIDB = pServerIDB - seenServers;

            seenServers += hardware.getNumServers();

            if (pServerIDA < seenServers && pServerIDB < seenServers && !(serverAFound || serverBFound))
            {
                distance = hardware.calcNetworkDistance(localIDA, localIDB);
                break;
            }
            else if (pServerIDA < seenServers && !serverAFound)
            {
                final int temp = hardware.calcNetworkDistance(localIDA);

                if (temp == -1)
                {
                    distance = temp;
                    break;
                }
                else
                {
                    distance += temp;
                    distance += getNetworkDistance();
                    serverAFound = true;
                }
            }
            else if (pServerIDB < seenServers && !serverBFound)
            {
                final int temp = hardware.calcNetworkDistance(localIDB);

                if (temp == -1)
                {
                    distance = temp;
                    break;
                }
                else
                {
                    distance += temp;
                    distance += getNetworkDistance();
                    serverBFound = true;
                }
            }

            if (serverAFound && serverBFound)
            {
                break;
            }
        }

        if (!serverAFound || !serverBFound)
        {
            distance = -1;
        }

        return distance;
    }

    /**
     * Calculate the network distance between the given server and the world
     * level switch.
     * 
     * @param pServerID
     *            the server to calculate the network distance of.
     * @return the network distance between given server and world level switch.
     */
    public int calcNetworkDistance(final int pServerID)
    {
        int distance = -1;
        int seenServers = 0;

        for (Datacentre dc : mDatacentres)
        {
            final int localID = pServerID - seenServers;
            seenServers += dc.getNumServers();

            if (pServerID < seenServers)
            {
                distance = dc.calcNetworkDistance(localID);

                if (distance == -1)
                {
                    break;
                }
                else
                {
                    distance += mIntraWorldNetworkDistance;
                    break;
                }
            }
        }

        return distance;
    }

    /**
     * Get the network distance between each datacentre in this world.
     * 
     * @return the network distance between each datacentre.
     */
    public int getNetworkDistance()
    {
    	return mIntraWorldNetworkDistance;
    }
    
    /**
     * Sets the network distance between each datacentre in this world.
     * 
     * @param pDistance the new network distance.
     */
    public void setNetworkDistance(int pDistance)
    {
    	mIntraWorldNetworkDistance = pDistance;
    }

    /**
     * Get the IP corresponding to the server with the given ID.
     * 
     * @param pServerID the ID of the server.
     * @return the IP associated with the server of the given ID.
     */
    public IP getIP(final int pServerID)
    {
        return getServer(pServerID).getIP();
    }

    /**
     * Get an array of all the server IPs. 'ID[i]' refers to the IP of server
     * with ID 'i'.
     * 
     * @return an array of all server IPs.
     */
    public IP[] getAllIP()
    {
        int numServers = getNumServers();
        IP[] IPs = new IP[getNumServers()];

        for (int i = 0; i < numServers; i++)
        {
            IPs[i] = getIP(i);
        }

        return IPs;
    }

    /**
     * Gets an array of all the datacenteres in the world.
     * 
     * @return the array of all datacentres.
     */
    public Datacentre[] getDatacentres()
    {
        return this.mDatacentres.toArray(new Datacentre[mDatacentres.size()]);
    }
     
    /**
     * Get the number of datacentres in the world
     * @return the number of datacentres
     */
    public int getNumberOfDatacentres() {
    	if(mDatacentres!=null) {
    		return mDatacentres.size();
    	} else {
    		return 0;
    	}
    }
    
    
    /**
     * Calculate and assign IPs to each server in the world.
     * 
     * SideEffect: Distributes unique integer IDs to each server
     */
    public void distributeIPs()
    {
        int numServersSeen = 0;
        
        logger.info("Distributing IPs...");
        
        logger.info("Number of DCs = " + mDatacentres.size());
        for (int i = 0; i < mDatacentres.size(); i++)
        {
            numServersSeen = mDatacentres.get(i).distributeIPs(i, numServersSeen);
            
            //TODO: debug - remove this.  Output IPs of all servers in datacentres
//            logger.warn(mDatacentres.get(i).getName() + ": IPs of servers..." + Arrays.toString(mDatacentres.get(i).getServerIPs()));
//            logger.warn(mDatacentres.get(i).getName() + ": IDs of servers..." + Arrays.toString(mDatacentres.get(i).getServerIDs()));
            
        }
    }
    
    /**
     * Get the server associated with the given IP.
     * 
     * @param pIP the IP of the server to fetch.
     * @return the server of the given IP.  null if IP does not exist
     */
    public Server getServer(final IP pIP)
    {
    	Server s = null;
    	try {
    		s = mDatacentres.get(pIP.dc()).getServer(pIP);
    	} catch (NullPointerException e) {
    		//do nothing
    		logger.debug(TimeManager.log("Warning: World.getServer().  No server wth IP=" + pIP + " returning null"));
    	}
        return s;
    }
    
    public Rack getRack(final IP pIP)
    {
        return mDatacentres.get(pIP.dc()).getRack(pIP);
    }
    
    /**
     * Get the parent datacentre corresponding to the object of the given IP.
     * 
     * @param pIP the IP of the object who's parent datacentre we want to find.
     * @return the parent datacentre.
     */
    public Datacentre getDatacentre(final IP pIP)
    {
    	//System.out.println(pIP.dc());
        return mDatacentres.get(pIP.dc());
    }

    /** Return datacentre with number */
    public Datacentre getDatacentre(int number) {
    	if(number>=0 && number<mDatacentres.size()) {
    		return mDatacentres.get(number);
    	} else {
    		logger.warn("No datacentre with number " + number + ".  Returning null...");
    		return null;
    	}
    }
    
    /**
     * Get a block by IP address
     * 
     * @param pIP - the IP address of the block
     * @return the Block
     */
    public Block getBlock(final IP pIP)
    {
        return mDatacentres.get(pIP.dc()).getBlock(pIP);
    }
    
    /**
     * Get all air con units as an array
     * 
     * @return array of AirConditioners
     */
    public AirConditioner[] getAirCons()
    {
        ArrayList<AirConditioner> airCons = new ArrayList<AirConditioner>();

        for (Datacentre dc : mDatacentres)
        {
            airCons.addAll(dc.getAirCons());
        }
        
        return airCons.toArray(new AirConditioner[airCons.size()]);
    }

    /**
     * Fetch a single aircon unit.
     * 
     * @param pAirConID
     * @return the AirConditioner
     */
    public AirConditioner getAirCon(final int pAirConID)
    {
        return getAirCons()[pAirConID];
    }
    
    /**
     * Return the datacentre ID of a given object ID (aircon/server)
     * @param type
     * @param objectID
     * 
     * Warning will throw ArrayIndexOutOfBoundsException or NullPointerException if objectID doesnt exist
     */
    public int getDatacentreID(ObjectType type, int objectID){
    	
    	int dc_id;
    	if(type.equals(ObjectType.aircon)) {
    		dc_id = World.getInstance().getDatacentre(World.getInstance().getAirCon(objectID).getIP()).getID(); //throws index out of bounds exception if no aircon exists
    	} else {
    		dc_id = World.getInstance().getDatacentre(World.getInstance().getServer(objectID).getIP()).getID();
    	}
    	return dc_id;   
    }
}
