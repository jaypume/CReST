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
 * @created 4 Jul 2011
 */

package sim.physical;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.failure.bo.Failable;
import sim.module.failure.event.FailureEvent.FailType;
import sim.module.failure.event.FailureThreads;
import sim.module.replacements.ReplacementsModuleRunner;
import sim.module.service.ServiceModuleRunner;
import sim.module.service.bo.Service;
import sim.module.service.bo.Service.Status;
import sim.module.service.bo.ServiceManager;
import sim.module.service.event.ServiceStopEvent;
import sim.module.thermal.bo.ThermalGrid;
import sim.physical.network.IP;
import utility.direction.CompassDirection;
import utility.time.TimeManager;
import config.physical.ServerType;

/**
 * Class representing a physical server.
 */
public class Server implements Failable
{
	public static Logger logger = Logger.getLogger(Server.class);

    // ///**** Constants ****\\\\\
    private static final long    MINUTES_TO_TEMPERATURE_FROM_ZERO = 10;
    private static final long    TIME_TO_TEMPERATURE_FROM_ZERO    = TimeManager.minutesToSimulationTime(MINUTES_TO_TEMPERATURE_FROM_ZERO);
    private static final double  DEFAULT_TEMPERATURE			  = ThermalGrid.DEFAULT_TEMPERATURE;
    private static final double  FAILURE_TEMPERATURE              = 90;
    public static final double   DEFAULT_DEFAULT_POWER_CONSUMPTION= 200;
    public static final double   DEFAULT_MAX_POWER_CONSUMED       = 1000;
    public static final double   DEFAULT_PURCHASE_COST            = 1000;

    // ///**** Member variables ****\\\\\
    // Temperature.
    private final double         mTempGenPerTimeUnit              = (8.0 * TimeManager.secondsToSimulationTime(1) * FAILURE_TEMPERATURE) / TIME_TO_TEMPERATURE_FROM_ZERO;
    private double               mTemp                            = DEFAULT_TEMPERATURE;
    private boolean              mHasOverHeated                   = false;

    // Physical.
    private final int            mHeight;
    private final CompassDirection		 mDirection;
    private final Point          mRelativePosition;

    // Components.
    private CPU[]                mCPUs;
    private RAM                  mRAM;
    private Harddisk             mDisk;

    // Failure.
    private boolean              mIsAlive                         = true;
    private FailType             mFailureType                     = FailType.fix;
    private long                 mMeanFailTime;

    // Cost & power.
    private double               mDefaultPowerConsumed;
    private double               mMaxPowerConsumed;
    private double               mCurrentPowerConsumed;
    private double               mPurchaseCost;
    private Software             mSoftware                        = new Software();

    // Identification.
    private String               mModel;
    private IP                   mIP;
    private int                  mID;

    private ArrayList<Service> servicesRunning; //TODO: JC, Oct 2011. Do i need this?  it holds lots of data, remove. Its also not updating correctly! (Maybe it is)
    
    /**
     * Create a new custom server from the specified parameters.
     * 
     * @param cpus
     *            selection of CPUs that this server consists of.
     * @param memory
     *            the memory that this server contains.
     * @param pHeight
     *            the height (in 'U's) of this server.
     * @param pModel
     *            the model name of this server.
     * @param pMeanFailTime
     *            the average fail time of this server (i.e. long it normally
     *            takes to fail after initial usage).
     */
    public Server(Point pRelativePosition, CPU[] cpus, RAM memory, int pHeight, CompassDirection pDirection, String pModel, final long pMeanFailTime)
    {
        mCPUs = cpus;
        mDefaultPowerConsumed = DEFAULT_DEFAULT_POWER_CONSUMPTION;
        mDisk = new Harddisk();
        mMaxPowerConsumed = DEFAULT_MAX_POWER_CONSUMED;
        mMeanFailTime = pMeanFailTime;
        mModel = pModel;
        mPurchaseCost = DEFAULT_PURCHASE_COST;
        mRAM = memory;

        mHeight = pHeight;
        mDirection = pDirection;
        mRelativePosition = pRelativePosition;
        mCurrentPowerConsumed = mDefaultPowerConsumed;

        mIP = IP.create("127.0.0.1");
        addSoftware("some os", 0.0);
        addSoftware("mysql", 0.0);
        
        //logger.debug("New server.  Mean fail time = " + mMeanFailTime);
        
        servicesRunning = new ArrayList<Service>();
    }

    /**
     * Create a new server from the given present type.
     * 
     * @param pNewServer
     *            the preset server type to create a new server from.
     */
    public Server(final ServerType pNewServer, final Point pRelativePosition, final int pHeight, final CompassDirection pDirection)
    {
        mCPUs = pNewServer.getCPUs();
        mDefaultPowerConsumed = pNewServer.getDefaultPowerCunsumption();
        mDisk = pNewServer.getHarddisk();
        mMaxPowerConsumed = pNewServer.getMaxPowerConsumption();
        mMeanFailTime = pNewServer.getMeanFailTime();
        mModel = pNewServer.getModelName();
        mPurchaseCost = pNewServer.getPurchaseCost();
        mRAM = pNewServer.getRAM();

        mHeight = pHeight;
        mDirection = pDirection;
        mRelativePosition = pRelativePosition;
        mCurrentPowerConsumed = mDefaultPowerConsumed;

        mIP = IP.create("127.0.0.1");
        addSoftware("some os", 0.0);
        addSoftware("mysql", 0.0);
        
        servicesRunning = new ArrayList<Service>();
    }

    public String toStringLong()
    {
        String string = "";

        string += "ID=" + mID + "\n";
        string += "IP=" + mIP + "\n";
        string += "Has failed: " + (!isAlive()) + "\n";
        string += "Current temperature: " + mTemp + "C\nPosition: " + mRelativePosition.toString();
        string += "\nHeight: " + mHeight;
        string += "\nCPUs - ";
        for (CPU c : mCPUs)
        {
            string += c.toString() + ", ";
        }
        string += "\nRAM - " + mRAM.toString();
        string += "\nHarddisk - " + mDisk.toString();
        string += "\nMax Power Usage - " + mMaxPowerConsumed + " Watts";
        string += "\nPurchase Cost - £ " + mPurchaseCost;
        string += "\nSoftware Installed - " + mSoftware.toString();
        string += "\nSoftware Cost - £" + getSoftwareCost();

        string += "\nServices Running: " + servicesRunning;
        return string;
    }
    
    /**
     * Return server string
     */
    public String toString() {
    	DecimalFormat df = new DecimalFormat("000000");
    	return "(" + df.format(mID) + ") " + mIP.toString() + " " + getStatusString();
    }

    /** Return failure status as a string */
    public String getStatusString() {
    	String s = "ServerID=" + mID + " ";
    	if(mIsAlive) s+="[isAlive]";
    	else s+="[isNotAlive:"+ mFailureType +"]";
    	return s;
    }
    
    /**
     * Returns the 2D relative (to its parent block) position of this server in
     * the simulated world.
     * 
     * @return The 2D relative position of the server.
     */
    public Point getRelativePosition()
    {
        return mRelativePosition;
    }

    /**
     * Get the height of this server in 'U's.
     * 
     * @return this server's height in 'U's.
     */
    public int getHeight()
    {
        return mHeight;
    }

    /**
     * Returns the CPU of this server.
     * 
     * @return The CPU of this server.
     */
    public CPU[] getCPUs()
    {
        return mCPUs;
    }

    /**
     * Returns the CPU Utilisation of all CPUs in this server
     * 
     * @return the CPU Utilisation of all CPUs in this server
     */
    public double getCPUUtilisation()
    {
    	int numCPUs =  mCPUs.length;
    	
    	if (numCPUs == 0) return 0;
    	
        double total = 0;
        for (int i = 0; i < numCPUs; i++)
        {
            total += this.getCPUs()[i].getCPUUtilisation();
        }
        total /= numCPUs;

        if (total < 0 || total > 1)
        {
        	logger.warn("The stored CPU utilisation \'" + total + "\' is outside the range 0-1 due to a precision error with doubles.");
        }
        return total;
    }

    /**
     * Returns the RAM of this server.
     * 
     * @return The RAM of this server.
     */
    public RAM getRAM()
    {
        return mRAM;
    }

    /**
     * Returns the harddisk of this server.
     * 
     * @return The harddisk of this server.
     */
    Harddisk getDisk()
    {
        return mDisk;
    }

    /**
     * Method to fetch the current alive state of the server. Will return true
     * if the server is running without any problems, else will return false.
     * 
     * @return True if the server if running without any problems, else false.
     */
    public boolean isAlive()
    {
        final boolean isAlive = mIsAlive && (!mHasOverHeated);

        return isAlive;
    }
    
    /**
     * Sets IsAlive of the server
     * @param state
     */
    public void setIsAlive(boolean state)
    {
        mIsAlive = state;
    }

    /**
     * Returns the software installed on the server
     * 
     * @author Alex Sheppard
     * @return the software installed on the server
     */
    String getSoftware()
    {
        return mSoftware.toString();
    }

    /**
     * Adds software to the server
     * 
     * @author Alex Sheppard
     * @param softwareName
     *            Name of the piece of software
     * @param softwarePrice
     *            Price of the software
     */
    void addSoftware(String softwareName, Double softwarePrice)
    {
        mSoftware.addSoftware(softwareName, softwarePrice);
    }

    /**
     * Try starting the service on this server. Return 1 if it is now running,
     * else return 0.
     * 
     * @param s - the service to run
     * @return 1 if the service is now running, else 0.
     */
    public int startTask(Service s)
    {
        int newInstances = 0;

        //JC (23/11/11) - BUG Fix, now checks to see if server is alive before starting new task
        if (canHandleService(s))
        {
            newInstances += 1;
            logger.debug(TimeManager.log("Starting serviceID=" + s.getID() + "." + s.getIssueNumber() + " on serverID=" + this.getID()));
        } else {
        	//do nothing
        }
        return newInstances;
    }
    
    /**
     * Start service on this server.  Return true if started correctly.
     * @param s
     * @return true
     */
    public boolean startService(Service s) {
    	
    	servicesRunning.add(s);
//    	printServicesRunning("Server.startService()");
    	return true;
    }
    
    public String printServicesRunning(String initialMessage) {
    	return initialMessage + " Server" + this.getID() + " running services " + servicesRunning;
    }
    
     /**
     * Method to check if this server is ready to handle a new (given) service.
     * 
     * Checks:
     * (1) Is server alive?
     * (2) CPU utilisation
     * (3) Server Power
     * 
     * SideEffect: Service is started if checks pass
     * 
     * @param index
     *            The service to check against.
     *            
     * @return True if the service started, else false
     */
    boolean canHandleService(Service s)
    {
        
    	if(this.isBroken()) {
    		return false;
    	}
    	
        boolean canHandleService = false;

        // Looks for a CPU with spare utilisation in this server and returns
        // true
        Boolean CPUUtilisation = false;
        lookforacore: for (int i = 0; i < mCPUs.length; i++)
        {
            // for (int j = 0; j < mCPUs[i].getNumCores(); j++)
            {
                if (mCPUs[i].serviceWillRun(s.getUtilisation()))
                {
                    mCPUs[i].adjustUtilisation(s.getUtilisation());
                    CPUUtilisation = true;

                    s.setCPU(i);
                    startService(s);
                    
                    break lookforacore;
                }
                else
                {
                    // System.out.println("Cannot add service, CPU" + i +
                    // " already fully utilised");
                }
            }
        }
        

        canHandleService = CPUUtilisation && 
        	setServerPower((mMaxPowerConsumed - mCurrentPowerConsumed) 
        			* s.getUtilisation()); // adjust power usage 
        
        return canHandleService;
    }

    /**
     * Method to cause this server to fail.
     */
    public void performFailure(final FailType pFailureType)
    {
    	
    	logger.info("Fail " + pFailureType + " received by server = " + this);
    	
    	//ignore failure if server has already failed! Exit routine
    	if(!mIsAlive) {
    		logger.info("Server is ignoring fail message since already failed: " + this);
    		//exit the routine
    		return;
    	} 
    	
    	//else, perform failure
    	logger.info("Server is currently alive. Performing failure...");
    	
        mIsAlive = false;

        performServerFailure(pFailureType);
        performUtilisationFailure(); 
        
        if(logger.isDebugEnabled()) {
        	logger.debug("Server.performFailure(" + pFailureType + "),  Server_ID=" + this.getID() + " Server utilisation is now "  + this.getCPUUtilisation());
        }
    }

    /**
     * Fail the server and add to block's dead server list if it's a hard failure
     */
    private void performServerFailure(final FailType pFailureType)
    {
        mFailureType = pFailureType;
        if(pFailureType == FailType.hard)
        {
        	World.getInstance().getBlock(mIP).setDead(mIP, World.getInstance().getTime());
        }
        
        logger.info("Server has been failed: " + this + " ...");
    }

    /**
     * Recalculate the utilisation of each CPU in this server based on the
     * failure.
     */
    private void performUtilisationFailure()
    {
    	//TODO JC: May 2012 - this looks a bit dubious.
    	
        // Set CPU utilisation and power usage to defaults
        for (int i = 0; i < mCPUs.length; i++)
        {
            mCPUs[i].setUtilisation(0);
        }
        setServerPower(-mCurrentPowerConsumed + mDefaultPowerConsumed);
    }

    /**
     * Method to fix this server if it is broken (via soft fail).
     */
    @Override
    public void performFix()
    {
    	logger.info("Fixing server " + this + " ...");
        if (isBroken() && (mFailureType == FailType.soft))
        {
    		mIsAlive = true;
			mFailureType = FailType.fix;
            logger.info("Server fixed: " + this);
        } else {
        	//do nothing
        	logger.info("Ignoring fix: " + this);
        }
    }

    /**
     * TODO
     * 
     * @return
     */
    protected boolean isChildOfAContainer()
    {
        boolean isChild = World.getInstance().getBlock(mIP).isContainer();

        return isChild;
    }

    /**
     * Method to fix this server if it is broken.
     */
    @Deprecated
    void forceFix()
    {
    	logger.info("A fix has been forced...");
        performServerFix();
        performServiceFix();
    }

    /**
     * Fix this server based on the type of failure that occurred.
     */
    private void performServerFix()
    {
        mHasOverHeated = false;

        // TODO
        switch (mFailureType)
        {
            case soft:
            {
                mIsAlive = true;
                break;
            }
            case hard:
            {
                mIsAlive = true;
                ReplacementsModuleRunner.getInstance().replaceServer(this);
                break;
            }
            default:
            {
                break;
            }
        }
    }

    /**
     * TODO
     */
    private void performServiceFix()
    {
    	logger.info("Performing service fix on Server " + this + " ...");
    	
    	int datacentre_index = World.getInstance().mDatacentres.get(mIP.dc()).getID();
    	ServiceManager sm = ServiceModuleRunner.getInstance().getServiceManager(datacentre_index);
    	    	
    	//TODO JC May 2012 - I dont think this is doing what we would like. Check...
        //Iterate through all running services and check if running on *this* server
        for (Service service : sm.getMap().values())
        {
            if (service.getServerID() == mIP)
            {
                if (service.getStatus() == Status.failed)
                {
                    EventQueue queue = EventQueue.getInstance();
                    
                    Event event = ServiceStopEvent.create((getTime() + service.getDuration()), service.getID(), service.getIssueNumber(), mIP.dc());
                    queue.addEvent(event);
                    logger.info("Added event to queue: " + event);
                    
                    service.setState(Status.running);
                    service.setStopTime(getTime() + service.getDuration());
                   
                    logger.info(service.getID() + " fixed and " + service.getStatus());

                    // Set CPU utilisation and power usage
                    boolean found = false;
                    for (int i = 0; i < mCPUs.length; i++)
                    {
                        if (mCPUs[i].serviceWillRun(service.getUtilisation()))
                        {
                            mCPUs[i].adjustUtilisation(service.getUtilisation());

                            found = true;
                            service.setCPU(i);
                            setServerPower((mMaxPowerConsumed - mCurrentPowerConsumed) * service.getUtilisation());
                            break;
                        }
                    }
                    if (!found)
                    {
                    	logger.info("Failed to find a CPU to start this service on again");
                    } else {
                    	logger.info("Service started on CPU: " + service.getCPU());
                    }
                }
            }
        }
    }

    /**
     * Returns the power used by this server
     * 
     * @author Alex Sheppard
     * @return the power used by the server
     */
    double getServerPower()
    {
        return mCurrentPowerConsumed;
    }

    /**
     * Returns the power used by this server at full load
     * 
     * @author Alex Sheppard
     * @return the max power used by the server at full load
     */
    double getMaxServerPower()
    {
        return mMaxPowerConsumed;
    }

    /**
     * Changes the power usage for the server when a service is started or
     * stopped
     * 
     * @author Alex Sheppard
     * @param powerChange
     *            the change in power (+ or -)
     * @return whether the server can handle another service or not
     */
    boolean setServerPower(double powerChange)
    {
        if (mCurrentPowerConsumed + powerChange < mDefaultPowerConsumed)
        {
            logger.error("Something has gone wrong, you've removed a service and you are using less power than the idle power for the server");
            mCurrentPowerConsumed = mDefaultPowerConsumed;
            return false;
        }
        else if (mCurrentPowerConsumed + powerChange > mMaxPowerConsumed)
        {
            logger.error("Something has gone wrong, you've added a service and you are using more power than the max power of the server at full load");
            mCurrentPowerConsumed = mMaxPowerConsumed;
            return false;
        }
        else
        {
            mCurrentPowerConsumed += powerChange;
            return true;
        }
    }

    /**
     * Returns the purchase cost of this server
     * 
     * @author Alex Sheppard
     * @return the purchase cost of the server
     */
    double getPurchaseCost()
    {
        return mPurchaseCost;
    }

    /**
     * Returns the cost of the software running on this server
     * 
     * @author Alex Sheppard
     * @return the cost of the software running on the server
     */
    double getSoftwareCost()
    {
        return mSoftware.getCost();
    }

    /**
     * Get the current time in the simulator.
     * 
     * @return the current simulation time.
     */
    private long getTime()
    {
        final long currentTime = World.getInstance().getTime();

        return currentTime;
    }

    /**
     * Get the temperature of this server.
     * 
     * @return the temperature of this server.
     */
    public double getTemperature()
    {
        return mTemp;
    }

    /**
     * Get the IP referring the this server.
     * 
     * @return the IP of this server.
     */
    @Override
    public IP getIP()
    {
        return mIP;
    }

    /**
     * Set the IP of this server given the server, rack, aisle and datacentre
     * level addresses.
     * 
     * @param pServer
     *            the specific server.
     * @param pRack
     *            the rack that the server is contained in.
     * @param pAisle
     *            the aisle that the server is contained in.
     * @param pDC
     *            the datacentre that the server is contained in.
     * @param pNumServersSeen
     *            the number of servers that have already had an IP/ID allocated
     *            to them.
     */
    public int setIP(final int pServer, final int pRack, final int pAisle, final int pDC, final int pNumServersSeen)
    {
        mIP = IP.create(pServer, pRack, pAisle, pDC);
        mID = pNumServersSeen;

        return pNumServersSeen + 1;
    }

    /**
     * Update and return the temperature of this server.
     * 
     * @return the updated temperature of this server.
     */
    public double updateTemperature()
    {
        if (isAlive())
        {
            generateHeat();
        }
        else if (mIsAlive == true)
        {
            if (mTemp <= FAILURE_TEMPERATURE) //TODO AS 5.9.12- is failure temp a good idea? as soon as temp < 90, server restarts then instantly overheats again.
            {
                mHasOverHeated = false;
            }
        }

        return mTemp;
    }

    /**
     * Generate heat from this server and update its temperature from it.
     * 
     * @param pDuration
     *            the duration of time to generate heat for.
     */
    private void generateHeat()
    {
        double ratio = getCPUUtilisation();
        
        if (ratio < 0.5)
        {
            ratio = 0.5;
        }
        
        incrementTemperature(ratio * mTempGenPerTimeUnit);
    }

    /**
     * Update the position of this server to the given one.
     * 
     * @param pRelativePosition
     *            the new position of this server.
     */
    public void setRelativePosition(final Point pRelativePosition)
    {
        mRelativePosition.setLocation(pRelativePosition);
    }

    /**
     * Fetches the mean failure time of this server, as used for failure
     * probability distribution calculations.
     * 
     * @return the mean failure time of this server.
     */
    public long getMeanFailTime()
    {
        return mMeanFailTime;
    }

    /**
     * Spawn a failure event to kill this server.
     */
    public void spawnFailureEvent()
    {
        // TODO: Actually create a failure event instead of killing it manually.
        mHasOverHeated = true;
    }

    /**
     * Get the failure temperature of the server
     * 
     * @return the failure temperature of the server
     */
    public double getFailureTemperature()
    {
        return FAILURE_TEMPERATURE;
    }
    
    /**
     * Gets the failure type of the server
     * @return the failure type of the server
     */
    @Override
    public FailType getFailType()
    {
    	return mFailureType;
    }
    
    /**
     * Sets the failure type of the server
     * @param failType the failure type of the server
     */
    public void setFailType(FailType failType)
    {
    	mFailureType = failType;
    }

    /**
     * Increment the temperature of this server by the given amount.
     * 
     * @param pIncrement
     *            amount to increment the temperature of this server by.
     */
    public void incrementTemperature(final double pIncrement)
    {
        // System.out.println("--Server:incrementTemperature()");
        // System.out.println("Time: " +
        // TimeManager.simulationTimeToMinutes(World.getInstance().getTime()));
        // System.out.println("Old Temp: " + mTemp);
        // System.out.println("Increment: " + pIncrement);

        mTemp += pIncrement;

        // System.out.println("New Temp: " + mTemp);
        // TODO A server should not have a max temperature.
        final double maxTemperature = 100;

        if (mTemp > maxTemperature)
        {
        	//int i = 0;
//            mTemp = maxTemperature;
        }
    }

    /**
     * Get the model name of this server.
     * 
     * @return the new model name of this server.
     */
    public String getmModel()
    {
        return mModel;
    }

    /**
     * Set the model name of this server.
     * 
     * @param mModel
     *            the new model name of this server.
     */
    public void setmModel(String mModel)
    {
        this.mModel = mModel;
    }

    /**
     * Get the absolute position of this server in its parent datacentre.
     * 
     * @return the absolute position of this server in its parent datacentre.
     */
    public Point getAbsolutePosition()
    {
        // TODO Code to calculate the absolute position of this server based on
        // its parents.
        Datacentre dc = World.getInstance().getDatacentre(mIP);

        return dc.calcAbsolutePosition(mIP);
    }

    /**
     * Get the direction the front of this server faces
     * 
     * @return the direction the front of this server faces
     */
    public CompassDirection getDirection()
	{
		return mDirection;
	}

	/**
     * Get the unique position ID of this server.
     * 
     * @return the unique position ID of this server.
     */
    public int getID()
    {
        return mID;
    }

    /**
     * Use the given server type to replace all the values current stored in
     * this server.
     * 
     * @param pNewServer
     *            the new server to replace the values contained within this
     *            one.
     */
    public void replaceMemberVariables(final ServerType pNewServer)
    {
        CPU[] newCPUs = pNewServer.getCPUs();
  
        for (int i = 0; i < mCPUs.length; i++)
        {
        	try
        	{
	        	//TODO AS 7.9.12- surely getting the utilisation of the server you are replacing will always be 0, seeing as it has failed?
	            newCPUs[i].setUtilisation(mCPUs[i].getCPUUtilisation());
        	}
        	catch(ArrayIndexOutOfBoundsException e)
        	{
        		logger.error("CPU " + i + " does not exist in new server- ignoring");
        	}
        }

        mCPUs = newCPUs;
        mDefaultPowerConsumed = pNewServer.getDefaultPowerCunsumption();
        mDisk = pNewServer.getHarddisk();
        mMaxPowerConsumed = pNewServer.getMaxPowerConsumption();
        mMeanFailTime = pNewServer.getMeanFailTime();
        mModel = pNewServer.getModelName();
        mPurchaseCost = pNewServer.getPurchaseCost();
        mRAM = pNewServer.getRAM();

        mCurrentPowerConsumed = mDefaultPowerConsumed;
    }

    /**
     * TODO
     * 
     * @param pOldMeanFailTime
     */
    public void updateFailureThreads(final long pOldMeanFailTime)
    {
        FailureThreads.getInstance().update(mIP, pOldMeanFailTime);
    }

    /**
     * Generate a server with processing capabilities, costs, efficiency, etc.
     * based on a function over time.
     * 
     * @param pTime
     *            the current time.
     * @return a new server.
     */
    public static ServerType generateServerType(final long pTime)
    {
        ArrayList<CPU> arrayListCPUs = new ArrayList<CPU>();
        long size = 2;
        long baseMeanFailTime = TimeManager.daysToSimulationTime(100);
        long baseTimeAvailableFrom = pTime;
        String model = "Function_server";

        final int baseNumCPUs = 6;
        final double modifier = 1 + ((1.0 * pTime) / TimeManager.daysToSimulationTime(365 + 182));

        final int numCPUs = (int) (baseNumCPUs * modifier);

        for (int i = 0; i < numCPUs; i++)
        {
            arrayListCPUs.add(new CPU("Function_CPU", 4000, 6));
        }

        ServerType newServer = new ServerType(
        		arrayListCPUs.toArray(new CPU[numCPUs]),
        		new RAM(), 
        		new Harddisk(),
        		baseMeanFailTime, 
        		size, 
        		model, 
        		DEFAULT_DEFAULT_POWER_CONSUMPTION,
        		DEFAULT_MAX_POWER_CONSUMED,
        		DEFAULT_PURCHASE_COST, 
        		baseTimeAvailableFrom
        		);

        return newServer;
    }

    /**
     * Check if this server is broken (i.e. has suffered a hard or soft fail).
     * 
     * Note This method call is equivalent to !isAlive().
     * 
     * @return true if this server is broken, else false.
     */
    public boolean isBroken()
    {
        return !isAlive();
    }

    /*
     * (non-Javadoc)
     * @see sim.failure.Failable#isServer()
     */
    @Override
    public boolean isServer()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see sim.failure.Failable#isAircon()
     */
    @Override
    public boolean isAircon()
    {
        return false;
    }
    
    /**
     * Return a test server (for debugging)
     * 
     * @return the test server
     */
    public static Server getTestServer() {
    	

    	CPU cpu[] = new CPU[2];
    	cpu[0] = new CPU();
    	cpu[1] = new CPU();
    	
    	Server s = new Server(new Point(0,0), cpu, new RAM("Test Model", 1000, 1), 2, CompassDirection.getDefault(), "TestServer", 1000);
    	s.mID = Server.getNextServerID();
    	return s;
    }
    
    private static int nextServerID = 0; 
    
    /**
     * Get the next server ID for the Server class.  Used for testing.
     */
    public static int getNextServerID() {
    	return Server.nextServerID++;
    }
    
    /**
     * Reset next server ID for the Server class to 0.  Used for testing.
     */
    public static void resetNextServerID() {
    	Server.nextServerID=0;
    }
}
