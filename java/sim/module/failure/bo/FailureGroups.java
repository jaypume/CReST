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
 * Created on 27 Aug 2011
 */
package sim.module.failure.bo;

import java.util.ArrayList;
import java.util.HashMap;

import sim.physical.AirConditioner;
import sim.physical.Server;

/**
 * Class to represent the failure groups within a simulation.
 * 
 * A failure group is a collection of servers which have the same mean failure
 * time under a Poisson distribution. There will be one failure group for each
 * mean failure rate.
 * 
 * A FailureGroups object can be used to create the failure threads associated
 * with a simulation.
 */
public class FailureGroups
{
    private int                                mNumGroups = 0;
    private HashMap<Long, ArrayList<Failable>> mGroups    = new HashMap<Long, ArrayList<Failable>>();
    private ArrayList<Long>                    mMeans     = new ArrayList<Long>();

    /**
     * Basic constructor.
     */
    public FailureGroups()
    {
    }

    /**
     * Add the given failable object to the failure group that it belongs to
     * (based on mean failure time).
     * 
     * @param pObject
     *            the failable object to add to the failure groups.
     */
    public void add(final Failable pObject)
    {
        final Long mean = Long.valueOf(pObject.getMeanFailTime());

        // Add the object to the group with the corresponding mean failure time,
        // or created a group if one doesn't already exist.
        if (mGroups.get(mean) == null)
        {
            ArrayList<Failable> group = new ArrayList<Failable>();
            group.add(pObject);
            mGroups.put(mean, group);

            mMeans.add(mean);
            mNumGroups++;
        }
        else
        {
            mGroups.get(mean).add(pObject);
        }
    }

    /**
     * Get the number of failure groups that exist.
     * 
     * @return the number of failure groups.
     */
    public int getNumGroups()
    {
        return mNumGroups;
    }

    /**
     * Get the mean failure time of the group with the given index.
     * 
     * @param pIndex
     *            the index of the group to fetch the mean failure time of.
     * @return the mean failure time of the group with the given index, else -1
     *         if the group does not exist.
     */
    public long getMean(final int pIndex)
    {
        long mean;

        try
        {
            mean = mMeans.get(pIndex);
        }
        catch (IndexOutOfBoundsException e)
        {
            mean = -1;
        }

        return mean;
    }

    /**
     * Get the index of the group with the corresponding mean failure time.
     * 
     * @param pMean
     *            the mean failure time of the group to fetch.
     * 
     * @return the index of the group with the corresponding mean failure time,
     *         else -1 if the group does not exist.
     */
    public int getIndex(final long pMean)
    {
        int index = -1;

        ArrayList<Failable> group = mGroups.get(pMean);

        if (group != null)
        {
            for (int i = 0; i < mMeans.size(); i++)
            {
                if (mMeans.get(i) == pMean)
                {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    /**
     * Get all the servers in the group with the corresponding mean failure
     * time.
     * 
     * @param pMean
     *            the mean failure time of the group to fetch.
     * 
     * @return all the servers in the group with the given mean failure time.
     */
    public ArrayList<Server> getServers(final long pMean)
    {
        ArrayList<Server> servers = new ArrayList<Server>();
        ArrayList<Failable> group = mGroups.get(pMean);

        if (group != null)
        {
            for (Failable failable : group)
            {
                if (failable.isServer())
                {
                    servers.add((Server) failable);
                }
            }
        }

        return servers;
    }

    /**
     * Get all the aircon units in the group with the corresponding mean failure
     * time.
     * 
     * @param pMean
     *            the mean failure time of the group to fetch.
     * 
     * @return all the aircon units in the group with the given mean failure
     *         time.
     */
    public ArrayList<AirConditioner> getAircons(final long pMean)
    {
        ArrayList<AirConditioner> airCons = new ArrayList<AirConditioner>();
        ArrayList<Failable> group = mGroups.get(pMean);

        if (group != null)
        {
            for (Failable object : group)
            {
                if (object.isAircon())
                {
                    airCons.add((AirConditioner) object);
                }
            }
        }

        return airCons;
    }

    /**
     * Get all the servers in the group with the given index.
     * 
     * @param pIndex
     *            the index of the group to fetch.
     * 
     * @return the group of servers with the given index.
     */
    public ArrayList<Server> getServers(final int pIndex)
    {
        return getServers(getMean(pIndex));
    }

    /**
     * Get the number of servers in the group with the given index.
     * 
     * @param pIndex
     *            the index of the group to fetch the size of.
     * 
     * @return the number of servers in the group with the given index.
     */
    public int getSize(final int pIndex)
    {
        return getServers(pIndex).size();
    }

    /**
     * Remove the given server from the failure groups.
     * 
     * @param pServerID
     *            the ID of the old server to remove from the failure groups.
     * @param pOldMeanFailTime
     *            the mean fail time of the old server to be removed.
     * 
     * @return true if the server was found and removed from the failure groups.
     */
    public boolean remove(final int pServerID, final long pOldMeanFailTime)
    {
        boolean removedGroup = false;
        ArrayList<Server> servers = getServers(pOldMeanFailTime);

        for (int i = 0; i < servers.size(); i++)
        {
            if (servers.get(i).getID() == pServerID)
            {
                servers.remove(i);

                if (servers.size() == 0)
                {
                    // TODO: Make it so that a group is not destroyed when it
                    // reaches 0 members. Make FailureThreads account for this
                    // too.
                    removedGroup = true;
                    mMeans.remove(Long.valueOf(pOldMeanFailTime));
                    mNumGroups--;
                }

                break;
            }
        }

        return removedGroup;
    }
}
