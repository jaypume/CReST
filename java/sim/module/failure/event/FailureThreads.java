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
 * @author Luke Drury (ld8192)
 * Created on 11 Aug 2011
 */
package sim.module.failure.event;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.failure.bo.FailureGroups;
import sim.module.failure.event.FailureEvent.ObjectType;
import sim.physical.Server;
import sim.physical.World;
import sim.physical.network.IP;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;
import cern.jet.random.Exponential;
import cern.jet.random.engine.MersenneTwister;

/**
 * Class to allow generation of new failure events based on the idea of
 * 'threads' which will exist, one for each type of server failure distribution.
 * 
 * Currently these "types" are purely based on the mean server failure chance,
 * i.e. one thread exists for each mean.
 * 
 * warning This class assumes that a World singleton has already been
 *          instantiated.
 */
public class FailureThreads
{
	public static Logger logger = Logger.getLogger(FailureThreads.class);
	
    // Constants.
    private final int                    NUMBER_INITIAL_FAILURE_MONTHS = 12;
    private final int                    TOTAL_INITIAL_FAILURE_CHANCE  = 10;

    // Singleton instance.
    private static FailureThreads        instance                      = null;

    // Member variables.
    private final RandomSingleton        mRandomGenerator;
    private final FailureGroups          mFailureGroups;
    private final ArrayList<Exponential> mThreads                      = new ArrayList<Exponential>();
    private double[]                     mInitialFailureMonths;

    private boolean initialFailuresGenerated = false; //TODO: DEC2011: JC - this is a bit of a hack, look into this
    
    /**
     * Create and return a FailureThread object.
     * 
     * @WARNING This object is intended to be used as a singleton. Call
     *          FailureThreads.create() to instantiated the singleton object.
     * 
     * @param pGen
     *            The underlying RandomSingleton to use for probability
     *            distribution calculations.
     * @param pFailureGroups
     *            the FailureGroups from which to create the FailureThreads.
     */
    private FailureThreads(RandomSingleton pGen, FailureGroups pFailureGroups)
    {
        mRandomGenerator = pGen;
        final MersenneTwister engine = mRandomGenerator.getEngine();

        mFailureGroups = pFailureGroups;

        logger.info("Creating failure thread for each failure group...");
        // Make failure thread for each failure group.
        for (int i = 0; i < mFailureGroups.getNumGroups(); i++)
        {
            final double groupSize = mFailureGroups.getSize(i);
            final double mean = mFailureGroups.getMean(i);
            final double chance = groupSize / mean;

            logger.info("Creating failure thread with: groupSize="+groupSize+", mean="+mean+", chance="+chance);
            mThreads.add(new Exponential(chance, engine));
        }
    }

    /**
     * Create and return the singleton FailureThread object.
     * 
     * INFO To get access to the singleton after calling this method, use
     *       FailureThreads.getInstance().
     * 
     * WARNING Calling this will replace the current instantiation of the
     *          FailureThread object. Make sure this is what you want before
     *          calling create().
     * 
     * @param pGen
     *            The underlying RandomSingleton to use for probability
     *            distribution calculations.
     * @param pFailureGroups
     *            the FailureGroups from which to create the FailureThreads.
     * @return the singleton FailureThread object.
     */
    public static FailureThreads create(RandomSingleton pGen, FailureGroups pFailureGroups)
    {
    	logger.info("Creating new FailureThreads Object...");
        instance = new FailureThreads(pGen, pFailureGroups);

        logger.info("Instance is now: " + instance);
        return instance;
    }

    /**
     * Get the singleton FailureThreads object.
     * 
     * @return the singleton FailureThreads object.
     */
    public static FailureThreads getInstance()
    {
    	logger.debug("Getting FailureThreads instance...");
    	if(instance==null) logger.error("Instance is null!");
        return instance;
    }

    /**
     * Get the number of failure threads that exist.
     * 
     * @return the number of failure threads.
     */
    public int size()
    {
        return mThreads.size();
    }

    /**
     * Get the next failure event for the given failure thread.
     * 
     * @param pThread
     *            the thread for which to get the failure event.
     * @return the next failure event for the given thread.
     */
    public Event nextFailure(int pThread)
    {
        // TODO: Replace this botch fix for when an old failure event tries to
        // spawn a new failure on a thread that doesn't exist anymore.
        if (pThread >= mThreads.size())
        {
            logger.error("Trying to fail a server that is not in an existing failure group.");
            pThread = mThreads.size() - 1;
        }

        // Choose a time, server and failure type at random.
        final long randomTime = (long) Math.abs(mThreads.get(pThread).nextDouble());
        final long startTime = World.getInstance().getTime() + randomTime;
        final int serverIndex = getRandomServer(pThread);
        final FailureEvent.FailType failureType = randomFailureType();

    	//get datacentre ID - will throw exceptions if objectID does not exist
    	final int dc_id = World.getInstance().getDatacentreID(ObjectType.server, serverIndex);
        
        return FailureEvent.create(dc_id, failureType, startTime, serverIndex, ObjectType.server, pThread);
    }

    /**
     * Update the failure thread probability distributions based on the changes
     * to the server at the given IP.
     * 
     * @param pIP
     *            the IP of the server that has been modified/replaced and we
     *            need to recalculate the probability distributions from.
     */
    public void update(final IP pIP, final long pOldMeanFailTime)
    {
        Server server = World.getInstance().getServer(pIP);
        final int oldGroupIndex = mFailureGroups.getIndex(pOldMeanFailTime);
        final boolean removedGroup;

        removedGroup = updateGroups(server, pOldMeanFailTime);
        updateThreads(server.getMeanFailTime(), removedGroup, oldGroupIndex);
    }

    /**
     * Update the groups with the new server's mean failure time and remove the
     * old mean failure time.
     * 
     * @param pServer
     *            the server that has been added.
     * @param pOldMeanFailTime
     *            the old mean failure time to remove.
     * 
     * @return true if a failure group was rendered empty and so was removed.
     */
    private boolean updateGroups(Server pServer, final long pOldMeanFailTime)
    {
        boolean removedGroup = false;

        removedGroup = mFailureGroups.remove(pServer.getID(), pOldMeanFailTime);
        mFailureGroups.add(pServer);

        return removedGroup;
    }

    /**
     * Update the failure threads after a new mean failure time has been added
     * to the failure groups, and an old one has been removed.
     * 
     * @param pNewMeanFailTime
     *            the mean failure time that has recently been added to the
     *            failure groups.
     * @param pRemovedGroup
     *            true if a failure group was rendered empty when the old mean
     *            failure time was removed.
     * @param pOldGroupIndex
     *            the index of the group that the old mean failure time was
     *            removed from.
     */
    private void updateThreads(final long pNewMeanFailTime, final boolean pRemovedGroup, final int pOldGroupIndex)
    {
        final int groupIndex = mFailureGroups.getIndex(pNewMeanFailTime);

        // If a failure group was deleted when the old mean failure time was
        // removed, then also delete the corresponding thread.
        if (pRemovedGroup)
        {
            mThreads.remove(pOldGroupIndex);
        }

        // Calculate the lambda value to use for the Exponential distribution.
        final double groupSize = mFailureGroups.getSize(groupIndex);
        final double mean = mFailureGroups.getMean(groupIndex);
        final double chance = groupSize / mean;

        // Add a new failure thread if a failure group was also added.
        if (groupIndex >= mThreads.size())
        {
            mThreads.add(new Exponential(chance, mRandomGenerator.getEngine()));
        }
        else
        {
            mThreads.get(groupIndex).setState(chance);
        }
    }

    /**
     * Randomly choose a server from the given failure group.
     * 
     * @param pGroup
     *            the group to select a server from.
     * 
     * @return the ID of the selected server.
     */
    public int getRandomServer(final int pGroup)
    {
        ArrayList<Server> servers = mFailureGroups.getServers(pGroup);
        int randomServerIndex = -1;

        // Choose a random server ID from the failure group.
        if (servers.size() > 0)
        {
            final int randomServerGroupIndex = mRandomGenerator.randomInt() % servers.size();
            randomServerIndex = servers.get(randomServerGroupIndex).getID();
        }

        return randomServerIndex;
    }

    /**
     * Randomly select a failure type (hard or soft).
     * 
     * @return the randomly chosen failure type.
     */
    private static FailureEvent.FailType randomFailureType()
    {
        final FailureEvent.FailType failureType;

        if ((RandomSingleton.getInstance().randomInt() % 2) == 0)
        {
            failureType = FailureEvent.FailType.soft;
        }
        else
        {
            failureType = FailureEvent.FailType.hard;
        }

        return failureType;
    }

    /**
     * For each server in the world, with a certain probability spawn an initial
     * failure.
     */
    public void spawnInitialFailures()
    {
    	logger.info("Spawning initial failures...");
        World world = World.getInstance();
        final int numServers = world.getNumServers();
        generateFailureMonths();

        for (int i = 0; i < numServers; i++)
        {
            spawnInitialFailure(i);
        }
    }

    /**
     * Generate the probabilities for initial failure in each of the months.
     */
    private void generateFailureMonths()
    {
        final int n = NUMBER_INITIAL_FAILURE_MONTHS;
        final int totalChance = TOTAL_INITIAL_FAILURE_CHANCE;
        double x = calculateBaseValue(totalChance, n);
        mInitialFailureMonths = new double[n];

        logger.debug("Generating failure months for server...");
        
        // For each month calculate a probability.
        for (int i = n - 1; i >= 0; i--)
        {
            if (i == n - 1)
            {
                mInitialFailureMonths[i] = (x / Math.pow(2.0, i));
            }
            else
            {
                mInitialFailureMonths[i] = mInitialFailureMonths[i + 1] + (x / Math.pow(2.0, i));
            }
        }
        
        logger.debug("Initial failure months = " + Arrays.toString(mInitialFailureMonths));
    }

    /**
     * With a certain chance, create an initial failure for the given server and
     * add it to the event queue.
     * 
     * @param pServerID
     *            the server to potentially fail.
     */
    public boolean spawnInitialFailure(final int pServerID)
    {
        boolean hasFailed = false;
        final double chanceFail = mRandomGenerator.randomDouble() * 100.0;
        int failureMonth = -1;

        if(!initialFailuresGenerated) generateFailureMonths(); //JC, Dec2011, a bit of a hack to fix a bug
        //TODO - maybe this throws error because user events are OFF!
        
        logger.debug(TimeManager.log("Spawning initial failure for server" + pServerID));
        logger.debug(mInitialFailureMonths);
        
        // Calculate which month to spawn an initial failure in (if any).
        for (int i = mInitialFailureMonths.length - 1; i >= 0; i--)
        {
            if (chanceFail < mInitialFailureMonths[i])
            {
                failureMonth = i;
                break;
            }
        }

        // If a month was selected, spawn an initial event in it.
        if (failureMonth != -1)
        {
        	//get datacentre ID - will throw exceptions if objectID does not exist
        	final int dc_id = World.getInstance().getDatacentreID(ObjectType.server, pServerID);
        	
            final long dayOfMonth = mRandomGenerator.randomLong() % TimeManager.monthsToSimulationTime(1);
            final long monthOffset = TimeManager.monthsToSimulationTime(failureMonth);
            final long failureTime = dayOfMonth + monthOffset;

            EventQueue.getInstance().addEvent(FailureEvent.create(dc_id, failureTime, pServerID, ObjectType.server));
            hasFailed = true;
        }

        return hasFailed;
    }

    /**
     * Calculate base chance of initial failure on a month.
     * 
     * @param pTotalChance
     *            the total chance of failure in the given period of months.
     * @param pNumMonths
     *            the number of months over which the failure may occur.
     * @return the base value of chance of failure in an initial month.
     */
    private double calculateBaseValue(double pTotalChance, double pNumMonths)
    {
        return (Math.pow(2, pNumMonths - 1) * pTotalChance) / (Math.pow(2, pNumMonths) - 1);
    }
}
