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
package sim.module.service.bo;

import sim.module.service.bo.ServiceManager.TaskAllocationMethod;
import sim.physical.World;
import sim.physical.network.IP;

/**
 * Class to represent a service that will be run within a datacentre.
 */
public class Service
{
	
	private static int service_id = -1; //increment
	
    /**
     * The current running status of a service.
     */
    public enum Status
    {
        running, failed, complete, stopped
    }
    
    private int          mID; 								//ID of the service
    private int 		 issueNumber = 0;					//Issue number of service (increment each time service must be restarted)
    private IP           serverID;                          //ID of the server the service is running on
    private int          mCPUID;                            //The CPU in the server the service is running on
    private Status       status         = Status.stopped; 	//Current state
    private long         mStopTime;                         //The expected stop time for this service
    private final int    mTasksToComplete;					//Number of tasks in the service
    private int          mTasksCompleted = 0;
    private TaskAllocationMethod mTaskAllocationMethod;		//How the tasks are allocated to servers
    private final int    mMaxInstances;
    private int          mCurrentInstances;
    private double       mCPUUtilisation = 0.1;				//How much CPU this service requires
    private int[]        mDependencies = {-1};				//Services this service depends upon
    private int          mRedundancies = 1;					//Number of redundant copies of this
    private long 		 duration = -1;						//The duration of this service

    
    //TODO - why are we passing Server IP in constructor???
	/**
     * Default constructor for creating a service.
     */
    public Service(int pID, long duration, IP pServerID)
    {
    	mID = pID;
    	this.duration = duration;
        mTasksToComplete = 1;
        mTaskAllocationMethod = TaskAllocationMethod.FromLeft;
//        mTaskAllocationMethod = TaskAllocationMethod.Random; //this scheduler has a bug. falls into infinite loop trying to guess random IDs rather than shuffle through known
        mMaxInstances = mTasksToComplete;
        mRedundancies = 1;
    }

    /** Get the next service ID **/
    public static int getNextServiceID() {
    	return ++service_id;
    }
    
    /** 
     * 
     * Resets next service ID to 0
     * 
     * WARNING Use with caution!  
     * Should only be called at end of run
     * 
     **/
    public static void resetNextServiceIDToZero() {
    	service_id=-1;
    }
    
    /**
     * Restart the service
     * 
     * side effect: increments issue number
     */
    public void restartService() {
    	issueNumber++;
    }
    
    /**
     * Return the issue number of the service
     * 
     * Service issue numbers are incremented each time a service is restarted
     * 
     * @return the current service issue number
     */
    public int getIssueNumber() {
    	return issueNumber;
    }
    
    /**
     * Service constructor to duplicate a given service.
     * 
     * @param pService
     *            The service to duplicate.
     */
    protected Service(Service pService)
    {
    	mID = pService.mID;
    	serverID = pService.serverID;
    	mCPUID = pService.mCPUID;
        status = pService.status;
        mTasksToComplete = pService.mTasksToComplete;
        mTasksCompleted = pService.mTasksCompleted;
        mTaskAllocationMethod = pService.mTaskAllocationMethod;
        mMaxInstances = pService.mMaxInstances;
        mCurrentInstances = pService.mCurrentInstances;
        mCPUUtilisation = pService.mCPUUtilisation;
        mDependencies = pService.mDependencies;
        mRedundancies = pService.mRedundancies;
    }

    /**
     * Method to set the currently running number of instances of this service
     * across the datacentres.
     * 
     * @param pNumInstances
     *            The number of instances of this service that are now running.
     */
    public void setNumInstances(final int pNumInstances)
    {
        mCurrentInstances = pNumInstances;
    }

    /**
     * Method to fetch the current number of instances of this service.
     * 
     * @return The number of instances of this service.
     */
    public int getNumInstances()
    {
        return mCurrentInstances;
    }

    /**
     * Set the status of this services to the given one. A service that is
     * stopped will not be able to run until it is freshly started, i.e. cannot
     * be unpaused.
     * 
     * @param pStatus
     *            The status to set this service to.
     */
    public void setState(Status pStatus)
    {
        //if (mStatus != Status.failed && mStatus != Status.complete)
        {
            status = pStatus;
        }
        //else
        {
        	//System.out.println("bang");//CHANGE THIS
        }
    }

    /**
     * Returns the Status of this service
     * 
     * {running, failed, complete, stopped}
     * 
	 * @return the Status of this service
	 */
	public Status getStatus()
	{
		return status;
	}

	/**
     * Crate a copy of this service (for example to be used locally on a server).
     * 
     * @return A copy of this service.
     */
    protected Service duplicate()
    {
        Service service = new Service(this);

        return service;
    }
    
    /**
     * Returns the CPU utilisation this service would require
     * 
     * @return the CPU utilisation required
     */
    public double getUtilisation()
    {
    	return mCPUUtilisation;
    }

	/**
	 * Returns the Task Allocation Method of this service
	 * @return the Task Allocation Method of this service
	 */
	public TaskAllocationMethod getmTaskAllocationMethod()
	{
		return mTaskAllocationMethod;
	}

	/**
	 * Returns the Tasks To Complete of this service
	 * @return the Tasks To Complete of this service
	 */
	public int getTasksToComplete()
	{
		return mTasksToComplete;
	}

	/**
	 * Returns the Max Instances of this service
	 * @return the Max Instances of this service
	 */
	public int getMaxInstances()
	{
		return mMaxInstances;
	}

	/**
	 * Returns the ID of this service
	 * @return the ID of this service
	 */
	public int getID()
	{
		return mID;
	}

	/** 
	 * Returns the ID of the server the service is running on
	 * @return the ID of the server the service is running on
	 */
	public IP getServerID()
	{
		return serverID;
	}

	/** 
	 * Sets the ID of the server the service is running on
	 */
	public void setServerID(IP pServerID)
	{
		this.serverID = pServerID;
	}

	/**
	 * Returns the dependencies of this service
	 * @return the dependencies of this service
	 */
	public int[] getmDependencies()
	{
		return mDependencies;
	}

	/**
	 * Sets the dependencies of this service
	 * @param pDependencies the dependencies of this service
	 */
	public void setDependencies(int[] pDependencies)
	{
		this.mDependencies = pDependencies;
	}

	/**
	 * Returns the number of redundant copies of this service
	 * @return the number of redundant copies of this service
	 */
	public int getRedundancies()
	{
		return mRedundancies;
	}

	public int getCPU()
	{
		return mCPUID;
	}

	public void setCPU(int pCPU)
	{
		this.mCPUID = pCPU;
	}

	public long getStopTime()
	{
		return mStopTime;
	}

	public void setStopTime(long pStopTime)
	{
		this.mStopTime = pStopTime;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public String toString() {
		int serverNumber = -1;
		if(World.getInstance().getServer(serverID) == null) {
			//do nothing
		} else {
			serverNumber = World.getInstance().getServer(serverID).getID();
		}
			String s = "id=" + mID + " issue="+issueNumber+" {serverID: " + serverNumber + " serverIP:" + serverID + ", CPU_ID:" + mCPUID + ", Status:" + status +
				", CPU_util:" + mCPUUtilisation + "}";
			return s;
	}
	
	/**
	 * Compare two services.  Are their IDs and issue number equal? 
	 * @param other
	 * @return true if equal, false otherwise
	 */
    public boolean equals(Service other) {
    	if(this.getID()==other.getID() && this.getIssueNumber()==other.getIssueNumber()) return true;
    	else return false;
    }
}
