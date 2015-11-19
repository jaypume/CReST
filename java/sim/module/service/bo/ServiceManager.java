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

import java.util.HashMap;

import org.apache.log4j.Logger;

import sim.module.service.bo.Service.Status;
import sim.module.service.event.ServiceStartEvent;
import sim.physical.Datacentre;
import sim.physical.World;
import sim.physical.network.IP;
import sim.probability.RandomStd;
import utility.time.TimeManager;

/**
 * Manages the distribution of service tasks to servers
 */
public class ServiceManager
{	
	public static Logger logger = Logger.getLogger(ServiceManager.class);
	
	public enum TaskAllocationMethod
	{
		FromLeft, Random, NearestNeighbour
	}

	//Create a mapping<Key,Value> from Service Object to ID Key
	protected HashMap<Integer, Service> runningServicesMap = new HashMap<Integer,Service>();
	
	//Map for services that cannot be started.  Queue these... //TODO:- reschedule these...
	protected HashMap<Integer, Service> queuedServicesMap = new HashMap<Integer, Service>();
	
	protected int numberOfCompletedServices = 0;
		
	private Datacentre mDatacentre;	
	private long mEventExpectedStopTime;
	public static final int SERVICE_UNUSED = -1; //Only here to stop breaking ServiceOLD.java. Callum can remove when he doesn't need it.

	/**
	 * Initialises the Service Manager for the given datacentre
	 * 
	 * @param pDatacentre the datacentre 
	 */
	public ServiceManager(Datacentre pDatacentre)
	{
		logger.debug("Creating new ServiceManager for datacentre " + pDatacentre.getName());
		this.mDatacentre = pDatacentre;
	}

	/**
	 * Queue a service to be started later
	 * 
	 * //TODO: Reschedule these services
	 */
	public void queueService(int pID, ServiceStartEvent serviceStartEvent) {
		
		Service s = new Service(pID, serviceStartEvent.getDuration(), null);		
		serviceStartEvent.setService(s);
		
		//JC: add service and ID to hashmap		
		queuedServicesMap.put(pID,s);
		logger.debug("Added new Service to the queue.  Size is now: " + queuedServicesMap.size());
	}
	
	/**
	 * Queue a service to be started later
	 * 
	 * //TODO: Reschedule these services
	 */
	public void queueService(Service s) {

		//JC: add service and ID to hashmap		
		queuedServicesMap.put(s.getID(),s);
		logger.debug("Added new Service to the queue.  Size is now: " + queuedServicesMap.size());
	}
	
	/**
	 * 	Starts a new service
	 * 
	 * @param serviceID the ID of the service to start
	 * @param serviceStartEvent 
	 * 
	 * @return whether the service started
	 */
	public boolean startService(int serviceID, ServiceStartEvent serviceStartEvent)
	{
		Service s = new Service(serviceID, serviceStartEvent.getDuration(), null);		
		serviceStartEvent.setService(s);
		mEventExpectedStopTime = serviceStartEvent.getStopTime();
		
		if (checkDependencies(s) == false)
		{
			return false;
		}
		
		//JC: add service and ID to hashmap
		runningServicesMap.put(serviceID,s);			
		
		//Allocate tasks of the latest service (that is the service at the end of the servicesList
		if(!allocateTasks(serviceID)) {
			logger.debug(TimeManager.log("Could not allocate service.  Queuing service: " + s));
			logger.debug(TimeManager.log("TODO: Reschedule service when server becomes available"));
			
			//ignore this start event //TODO Add event to queue and create a new start event at later date
			if (serviceStartEvent.getStopEvent() != null) {
				serviceStartEvent.getStopEvent().setIgnored(); 
			}
			//remove service from services map
			runningServicesMap.remove(serviceID);
			//add the service to the service queues
			queueService(s);
			
			//TODO: IMPORTANT we need to reschedule these events onto the queue for when there is available space
			
			return false;
		}
		
		return true;
	}
	
	public Service getServiceWithID(int id) {
		//search through all hashmaps
		if(runningServicesMap.get(id) != null) {
			return runningServicesMap.get(id);
		} else if(queuedServicesMap.get(id) != null) {
			return queuedServicesMap.get(id);
		} else {
			logger.debug("Trying to get server with id="+id+".  None exists, returning null...");
			return null;
		}
	}

	public void removeServiceWithID(int id) {
		runningServicesMap.remove(id);
		
		logger.debug(runningServicesMap);
	}
	
	//TODO DO not call this, instead call "start-service" with a restart flag
	/** 
	 * Reallocate Tasks of this service.
	 * @deprecated
	 * @param serviceIndex
	 */
	@Deprecated
	public boolean reallocateTasks(int serviceIndex) {
		
		Service s = runningServicesMap.get(serviceIndex);
		
		s.restartService(); //increment the issue number
		logger.debug(TimeManager.log("Reallocating serviceID=" + s.getID() + "." + s.getIssueNumber()));
		
		Service temp = runningServicesMap.get(serviceIndex);
		logger.debug(TimeManager.log("Using hashmap: serviceID=" + temp.getID() + "." + temp.getIssueNumber()));
		
		if(s.getID()==temp.getID() && s.getIssueNumber()==temp.getIssueNumber()) {
			//do nothing, this is what we want!
		} else {
			logger.warn("Hasmap method returns different service to list method!");
			System.exit(0);
		}
		
		return allocateTasks(serviceIndex);
	}
	
	/**
     * Decides which way to allocate the tasks for this service
     * 
	 * @param serviceIndex the service to allocate
	 * 
	 * @return true if service is scheduled, false otherwise
     */
    public boolean allocateTasks(int serviceIndex)
    {   	
    	Service s = runningServicesMap.get(serviceIndex);
    	
    	if (s.getmTaskAllocationMethod().equals(TaskAllocationMethod.FromLeft)) 
		{
			return scheduleTasksLeftmost(serviceIndex);
		}
		else if (s.getmTaskAllocationMethod().equals(TaskAllocationMethod.Random))
		{ 
			return scheduleTasksRandom(serviceIndex);
		}
		else if (s.getmTaskAllocationMethod().equals(TaskAllocationMethod.NearestNeighbour))
		{ 
			return scheduleTasksNNeighbour(serviceIndex);
		}
		else
		{
			logger.warn("Task allocation type not implemented");
		}
    	
    	return false;
    }
    
    /**
     * Allocates tasks within this service to servers using the next lowest free server
     * 
     * @param serviceIndex = index service
     * 
     * @return true is service scheduled, false otherwise
     * @author Alex Sheppard
     */
	private boolean scheduleTasksLeftmost(int serviceIndex)
	{		
		Service s = runningServicesMap.get(serviceIndex);
		
		boolean isAllocated = false;
		int r;
		int numInstances = 0;
		for (r = 0; r < s.getRedundancies(); r++)
		{			
			//Distribute the task to servers until the maximum number of instances of the service are running.
			for (int j = 0; j < mDatacentre.getNumAisles() && numInstances < s.getMaxInstances(); j++)
			{
				for (int k = 0; k < mDatacentre.getmAisles().get(j).getNumRacks() && numInstances < s.getMaxInstances(); k++)
				{
					for (int l = 0; l < mDatacentre.getmAisles().get(j).getmRacks().get(k).getNumServers() && numInstances < s.getMaxInstances(); l++)
					{
						int returned = mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).startTask(s);
						
						if (returned == 1)
						{
							isAllocated = true;	
							s.setState(Status.running);							
							s.setStopTime(mEventExpectedStopTime);
							numInstances += returned;
							s.setNumInstances(numInstances);
							s.setServerID(mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getIP());						
						}
						else
						{
//							logger.debug(TimeManager.log("ServiceManager.scheduleTasksFromLeft() Could not start Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + r + ", started on: [" + j + "," + k + "," + l + "]  IP-address: " + mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getIP() + " ServerID=" + mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getID() + " Server status=" + mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getStatus()));
							
						}
					}
				}			
			}			
		}		
		if (!isAllocated)
		{
			logger.debug("Warning: ServiceManager.scheduleTasksLeftMost(): Could not allocate service: " + s);
			return false;
		}
		return true;
	}
	
	//TODO JC DEC: There is a bug. ... falls into infinite loop.  Do *not* randomly guess a server, shuffle working servers.
	/**
	 * Allocates tasks within this service to servers randomly
     * 
     * @author Alex Sheppard
	 * @param mStartTime 
	 *      
	 * @return true is service scheduled, false otherwise
	 */
	private boolean scheduleTasksRandom(int pIndex)
	{
		logger.debug("ServiceManager.scheduleTasksRandom //TODO - FIX this, there is a bug. Rather than shuffle known empty servers, we keep choosing random number until satisfied (for infinity)");
//		Service s = mServicesList.get(pIndex);
		
		Service s = runningServicesMap.get(pIndex);

		boolean isAllocated = false;
		int i;
		int numInstances = 0;
		for (i = 0; i < s.getRedundancies(); i++)
		{			
			//Distribute the task to servers until the maximum number of instances of the service are running.
			while (numInstances < s.getMaxInstances())
			{
				int aisleChosen = RandomStd.uniform(mDatacentre.getNumAisles());
				int rackChosen = RandomStd.uniform(mDatacentre.getmAisles().get(aisleChosen).getNumRacks());
				int serverChosen = RandomStd.uniform(mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getNumServers());
				
//				int returned = mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).startTask(pIndex, this);
				int returned = mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).startTask(s);
				
				if (returned == 1)
				{
					isAllocated = true;	
					s.setState(Status.running);							
					s.setStopTime(mEventExpectedStopTime);
					numInstances += returned;
					s.setNumInstances(numInstances);
					s.setServerID(mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).getIP());

					logger.debug("Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + ", started on: " + aisleChosen + "," + rackChosen + "," + serverChosen + ". " + mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).getIP());
				}
				else
				{
					logger.debug("Could not start Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + ", on: " + aisleChosen + "," + rackChosen + "," + serverChosen);	
				}		
			}			
		}		
		if (!isAllocated)
		{
			logger.debug("Could not start Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + " ANYWAR");
			return false;
		}
		return true;
	}
	
	/**
	 * Allocates tasks within this service to servers nearest the initial starting server
     * 
     * @author Alex Sheppard
	 * @param mStartTime 
	 * 
	 * 
 	 * @return true is service scheduled, false otherwise
	 */
	private boolean scheduleTasksNNeighbour(int pIndex)
	{
		logger.debug("ServiceManager.scheduleTasksNN //TODO - test this works");
//		Service s = mServicesList.get(pIndex);
		
		Service s = runningServicesMap.get(pIndex);

		boolean isAllocated = false;
		int i;
		int numInstances = 0;
		for (i = 0; i < s.getRedundancies(); i++)
		{
			//Choose initial starting server
			int aisleChosen = RandomStd.uniform(mDatacentre.getNumAisles());
			int rackChosen = RandomStd.uniform(mDatacentre.getmAisles().get(aisleChosen).getNumRacks());
			int serverChosen = RandomStd.uniform(mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getNumServers());

//			int returned = mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).startTask(pIndex, this);
			int returned = mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).startTask(s);
			
			if (returned == 1)
			{
				isAllocated = true;	
				s.setState(Status.running);							
				s.setStopTime(mEventExpectedStopTime);
				numInstances += returned;
				s.setNumInstances(numInstances);
				s.setServerID(mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).getIP());

				logger.debug("Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + ", started on: " + aisleChosen + "," + rackChosen + "," + serverChosen + ". " + mDatacentre.getmAisles().get(aisleChosen).getmRacks().get(rackChosen).getmServers().get(serverChosen).getIP());
			}
			else
			{
				logger.debug("Could not start Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + ", on: " + aisleChosen + "," + rackChosen + "," + serverChosen);	
			}			
			
			//Distribute the task to servers until the maximum number of instances of the service are running,
			//using servers nearest to the above initial server.

			//TODO: Create a proper clustering algorithm based on Callum's protocols.
			for (int j = aisleChosen; j < mDatacentre.getNumAisles() && numInstances < s.getMaxInstances(); j++)
			{
				for (int k = rackChosen; k < mDatacentre.getmAisles().get(j).getNumRacks() && numInstances < s.getMaxInstances(); k++)
				{
					for (int l = serverChosen; l < mDatacentre.getmAisles().get(j).getmRacks().get(k).getNumServers() && numInstances < s.getMaxInstances(); l++)
					{
//						returned = mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).startTask(pIndex, this);
						returned = mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).startTask(s);
						
						if (returned == 1)
						{
							isAllocated = true;	
							s.setState(Status.running);							
							s.setStopTime(mEventExpectedStopTime);
							numInstances += returned;
							s.setNumInstances(numInstances);
							s.setServerID(mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getIP());
						
							logger.debug("Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + ", started on: " + j + "," + k + "," + l + ". " + mDatacentre.getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getIP());
						}
						else
						{
							logger.debug("Could not start Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + ", on: " + j + "," + k + "," + l);	
						}
					}
				}			
			}
		}		
		if (!isAllocated)
		{
			logger.debug("Could not start Service: " + s.getID() + ", Instance/Task: " + numInstances + ", Redundancy: " + i + " ANYWAR");
			return false;
		}
		return true;
	}	
	
	/**
	 * Checks if the given service requires any other services to be completed before this one can run.
	 * 
	 * @param pService the service that needs to be checked
	 * @return true if ok to run, false if dependent service still running/queued
	 */
    public boolean checkDependencies(Service pService)
    {
    	if (pService.getmDependencies()[0] == -1 && pService.getmDependencies().length == 1) {
    		
    		logger.debug("No dependencies for service " + pService.getID());
    		return true;
    		
    	} else {
    		//find the dependent service
    		for (int i = 0; i < pService.getmDependencies().length; i++)
    		{    		
    			//is this service dependent on another service that hasnt completed?
    			if(this.getServiceWithID(pService.getmDependencies()[i])!=null){
    				logger.warn("Service " + pService.getID() + " depends on " + pService.getmDependencies()[i]);
    				return false; //dependencies are still running/queueing
    			} else {
    				//do nothing - this dependency has finished, check the next...
    			}
    		}

    		return true;
    	}	
    }

    /**
     * Finds the service that has finished and marks it as complete
     * 
     * @param pServiceID  the ID of the service to finish
     */
	public void completeService(int pServiceID)
	{
		boolean found = false;
		
		Service serv = this.getServiceWithID(pServiceID);
		logger.debug(TimeManager.log("Completing serviceID=" + serv.getID() + "." + serv.getIssueNumber() + " service:" + serv));
		
		Service s = runningServicesMap.get(pServiceID);
 		if (s!=null)
 		{
 			found = true;
 			if (s.getStatus() == Status.running && s.getStopTime() == World.getInstance().getTime())
 			{
 				int runningInstances = s.getNumInstances();
 				IP ip = s.getServerID();	 				
 				mDatacentre.getServer(ip).getCPUs()[s.getCPU()].adjustUtilisation(-(s.getUtilisation() * runningInstances));
 				
 				if (mDatacentre.getServer(ip).getCPUs()[s.getCPU()].getCPUUtilisation() < 0)
 				{
 					logger.debug("util < 0. " + mDatacentre.getServer(ip).getCPUs()[s.getCPU()].getCPUUtilisation());
 				}

 				s.setState(Status.complete);
 				s.setNumInstances(0);
 				logger.debug(TimeManager.log("Service " + pServiceID + " has completed on on serverIP=" + ip.toString()));
 				
 				//remove from map and increment completed services record
 				runningServicesMap.remove(s.getID());
 				numberOfCompletedServices++;

 			}
 			else
 			{
 				logger.debug(TimeManager.log("Service " + pServiceID + " can't complete, it's " + s.getStatus()));
 			}
 		}
		if (!found)
		{
			logger.debug("Service " + pServiceID + " does not exist and cannot be stopped.");
		}
	}
	
	/**
	 * Returns the number of running services in this datacentre
	 *
	 * @return the number of running services in this datacentre
	 */
	public int getRunningServices()
	{
		int total = 0;
	
		for (Service service : runningServicesMap.values())
		{
			if(service.getStatus() == Status.running)
			{
				total++;
			}
		}		
		return total;
	}
	
	/**
	 * Returns the number of failed services in this datacentre
	 * 
	 * @return the number of failed services in this datacentre
	 */
	public int getFailedServices()
	{
		int total = 0;
	
		for (Service service : runningServicesMap.values())
		{
			if(service.getStatus() == Status.failed)
			{
				total++;
			}
		}		
		return total;
	}
	
	/**
	 * Returns the total number of services in this datacentre
	 * 
	 * @return the total number of services in this datacentre
	 */
	public int getTotalServices()
	{
		return runningServicesMap.size() + getCompletedServices() + getQueuedServices();
	}
	
	/**
	 * Returns the number of completed services in this datacentre
	 * 
	 * @return the number of completed services in this datacentre
	 */
	public int getCompletedServices()
	{
		return numberOfCompletedServices;		
	}
	
	/**
	 * Returns the number of stopped services in this datacentre
	 * 
	 * @return the number of stopped services in this datacentre
	 */
	public int getStoppedServices()
	{
		int total = 0;
	
		for (Service service : runningServicesMap.values())
		{
			if(service.getStatus() == Status.stopped)
			{
				total++;
			}
		}		
		return total;
	}
	
	/**
	 * Returns the number of queued services in this datacentre

	 * @return the number of queued services in this datacentre
	 */
	public int getQueuedServices()
	{
		return queuedServicesMap.size();
	}
	
	/**
	 * Get the hashmap for this service manager
	 * 
	 * The mapping is from ID to Service
	 * 
	 * @return the map of running services
	 */
	public HashMap<Integer,Service> getMap() {
		return runningServicesMap;
	}
	
	/**
	 * Log the internal details of the ServiceManager
	 * 
	 * //TODO: Remove this, for debugging only
	 */
	public void logDetailsString() {
		String s = "Debugging output for ServiceManager (" +mDatacentre.getName() +")...";
				
		s += "\nNew HashMap method: [size=" + runningServicesMap.size() + ", total=" + getTotalServices() +
			", failed=" + getFailedServices() + ", completed= " + getCompletedServices() + 
			", running=" + getRunningServices() + ", queued=" + getQueuedServices() + "]";
		
		logger.info(s);
	}
}
