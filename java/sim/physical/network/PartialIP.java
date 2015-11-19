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
package sim.physical.network;

import org.apache.log4j.Logger;

public class PartialIP extends IP {

	public static final int UNKNOWN = -1;  
	
	public static Logger logger = Logger.getLogger(PartialIP.class);
	
	public PartialIP(final int pServer, final int pSubBlock, final int pBlock, final int pDC) {
		super(pServer, pSubBlock, pBlock, pDC);
		
		if(this.checkLegal()) {
			//do nothing, address type is set
		} else {
			logger.error("Illegal PartialIP: " + this);
		}
	}
	
	/**
	 * Construct a PartialIP address from a full IP address
	 * @param ip
	 */
	public PartialIP(IP ip) {
		super(ip.mServer, ip.mSubBlock, ip.mBlock, ip.mDC);
	}
	
	
    /**
     * Create a new PartialIP from the given string representation of an IP.
     * 
     * @param ip_string
     *            the string representation of the IP of a server
     *            ("Server.Rack.Aisle.DC").
     * @return the new IP object.
     */
    public static PartialIP create(final String ip_string)
    {
        final String[] tokenIP = ip_string.split("\\.");

        final int server = Integer.valueOf(tokenIP[0]);
        final int rack = Integer.valueOf(tokenIP[1]);
        final int aisle = Integer.valueOf(tokenIP[2]);
        final int dc = Integer.valueOf(tokenIP[3]);

        return new PartialIP(server, rack, aisle, dc);
    }
	/**
	 * Check this is a legal PartialIP address
	 * 
	 * SideEffect: Sets the AddressType of this PartialIP
	 * 
	 * @return true if legal, false otherwise
	 */
	public boolean checkLegal() {
		
		logger.debug(this);
		
		if(mDC < 0) { //we have unknown DC
			logger.debug("Error: Unknown DC. ");
			return false;
		} else if(mBlock < 0){ //we have unknown Aisle, thus a partial ID of a DC
			//check the rest are unknown
			if(mServer==UNKNOWN && mSubBlock==UNKNOWN && mBlock==UNKNOWN){
				//a valid partial ID of a DC
				logger.debug("Valid partial ID of Datacentre "+mDC);
				addressType = AddressType.DC;
				return true;
			} else {
				logger.debug("Error: Malformed Partial ID. ");
				return false;
			}
		} else if(mSubBlock < 0) { //We have unknown Rack, thus a partial ID of an Aisle
			//check the rest
			if(mServer==UNKNOWN && mSubBlock==UNKNOWN){
				//a valid partial ID of an Aisle
				logger.debug("Valid partial ID of Aisle "+mBlock);
				addressType = AddressType.AISLE;
				return true;
			} else {
				logger.debug("Error: Malformed Partial ID. ");
				return false;
			}
		} else if(mServer < 0) { //We have unknown server, thus a partial ID of a rack
			//check the rest
			if(mServer==UNKNOWN ){
				//a valid partial ID of a Rack
				logger.debug("Valid partial ID of Rack "+mSubBlock);
				addressType = AddressType.RACK;
				return true;
			} else {
				logger.debug("Error: Malformed Partial ID. ");
				return false;
			}	
		} else { //We have a fully formed IP address - this is legal
			//a valid IP of a server
			logger.debug("Valid partial ID of Server "+mSubBlock);
			addressType = AddressType.SERVER;
			return true;	
		}
		
	}
		
    /**
     * Navigate a round trip from a partial IP address to a partial IP address
     * 
     * Return the network "hops" (rack, aisle, dc)
     * Return 0 if same server
     * 
     * For each AddressType={DC, Aisle, Rack, Server},
     * if (different && both known) add two hops
     * if (different && one unknown) add one hop
     * 
     * @param from - PartialIP address of origin 
     * @param to - PartialIP address of destination
     * @return number of network "hops" between network hubs/switches
     * 
	*/
    public static int navigatePointToPoint(PartialIP from, PartialIP to) {
    	
    	int hops = 0; //same server
    	
    	if(from.equals(to)) { //same server - no network hops
    		return hops; 
    	}
    	
    	if(from.dc()!=to.dc()) { //different datacentres
    		if(from.dc()>=0) hops++;
    		if(to.dc()>=0) hops++;
    	} 
    	if (from.block()!=to.block()) { //different aisles
    		if(from.block()>=0) hops++; 
    		if(to.block()>=0) hops++;
    	} 
    	if (from.subBlock()!=to.subBlock()) { //different racks
    		if(from.subBlock()>=0) hops++;
    		if(to.subBlock()>=0) hops++;
    	} 
    	if (from.server()!=to.server()) { //different servers
    		if(from.server()>=0) hops++;
    		if(to.server()>=0) hops++;
    	} 
    	
    return hops;	
    }
    
    /**
     * Navigate network between this partial IP address and destination.
     * @param destinationAddress - IP address of destination server
     * @return - number of network "hops"
     */
    public int navigateToIP(PartialIP destinationAddress) {
    	return PartialIP.navigatePointToPoint(this,destinationAddress);
    }
}
