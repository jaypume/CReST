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
package sim.module.subscriptions.bo;

import org.apache.log4j.Logger;

import sim.module.subscriptions.SubscriptionsModuleRunner;

/**
 * Subscription between a subscribing node (A) and the node it is subscribed to (B): A->B
 * 
 * A->B "A subscribes to B"
 * 
 * @author cm9757
 * */
public class Subscription implements Cloneable
{
	public static Logger logger = Logger.getLogger(Subscription.class);
	
	/**
	 * status of subscription.  
	 * If true then the subscription is valid (i.e., neither nodes have failed) 
	 * [Note: this is *not* the status of either node]
	 */
	public boolean status;
	
	/**
	 * Time the subscription was last checked.
	 */
	public long timestamp;
	
	/**
	 * Construct a new subscription 
	 * 
	 * @param status - the status of the subscription
	 * @param timestamp - the time the subscription shows
	 */
	public Subscription(boolean status, long timestamp)
	{		
		this.status = status;
		this.timestamp = timestamp;
	}
	
	/**
	 * Update the Subscription with a new status and new time stamp
	 * and sets the time of last subscription update
	 * 
	 * @param newStatus - the new status of the subscription
	 * @param newTimestamp - the new time stamp of the subscription
	 */
	public void update(boolean newStatus, long newTimestamp) {
		status = newStatus;
		timestamp = newTimestamp;
		SubscriptionsModuleRunner.getInstance().setTimeOfLastUpdate(newTimestamp);
	}
	
	/**
	 * Get the time subscription was last changed
	 * 
	 * @return time
	 */
	public long getTimeStamp()
	{
		return timestamp;
	}

	public Object clone() 
	{
		logger.debug("Cloning subscription: " + this);
		try 
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			throw new Error("This should not occur since we implement Cloneable");
		}
	}

	public String toString() {
		
		return "Subscription: [status="+status+", timestamp="+timestamp+"]";
	}

}