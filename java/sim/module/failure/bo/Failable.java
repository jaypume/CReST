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
 * Created on 23 Sep 2011
 */
package sim.module.failure.bo;

import sim.module.failure.event.FailureEvent.FailType;
import sim.physical.network.IP;

/**
 * Interface to interact with a failable object in the simulation world.
 */
public interface Failable
{
    /**
     * Get the mean failure time of this object.
     * 
     * @return the mean failure time of this object.
     */
    public long getMeanFailTime();

    /**
     * Check if this object is a server.
     * 
     * @return true if this object is a server, else false.
     */
    public boolean isServer();

    /**
     * Check if this object is an air conditioning unit
     * 
     * @return true if this object is an air conditioning unit, else false.
     */
    public boolean isAircon();

    /**
     * Check to see if this object is alive.
     * 
     * @return true if this object is alive, else false.
     */
    public boolean isAlive();

    /**
     * Check to see if this object is broken.
     * 
     * @return true if this object is broken, else false.
     */
    public boolean isBroken();

    /**
     * Perform a failure on this failable object.
     * 
     * @param pFailureType
     *            the type of failure to perform on this object (hard or soft).
     */
    public void performFailure(final FailType pFailureType);

    /**
     * Perform a fix of this failable object.
     */
    public void performFix();
    
    /**
     * Get the failure type of this failable object.
     * 
     * @return the failure type of this failable object.
     */
    public FailType getFailType();

    /**
     *  Set the IsAlive state of this failable object.
     *  
     * @param state the IsAlive state of this failable object.
     */
	public void setIsAlive(boolean state);

	/**
	 * Set the failure type of this failable object.
	 * 
	 * @param type the failure type of this failable object.
	 */
	public void setFailType(FailType type);

	/**
	 * Get the IP  of this failable object.
	 * @return the IP of this failable object.
	 */
	public IP getIP();
}
