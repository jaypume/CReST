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
 * Created on 18 Aug 2011
 */
package sim.physical.network;

/**
 * A class to represent a physical IP of a server. Can be used to find a server
 * in a World, Datacentre, Aisle or Rack.
 */
public class IP
{
	protected enum AddressType{SERVER, RACK, AISLE, DC};
	
    protected final int mServer;
    protected final int mSubBlock;
    protected final int mBlock;
    protected final int mDC;
    
	protected IP.AddressType addressType = AddressType.SERVER; //IP address of a server

    /**
     * Create a new IP from the given network positions.
     * 
     * @param pServer
     *            the specific server this IP points to.
     * @param pRack
     *            the rack which the server is contained in.
     * @param pAisle
     *            the aisle that the server is contained in.
     * @param pDC
     *            the datacentre that the server is contained in.
     */
    protected IP(final int pServer, final int pSubBlock, final int pBlock, final int pDC)
    {
        mServer = pServer;
        mSubBlock = pSubBlock;
        mBlock = pBlock;
        mDC = pDC;
    }

    /**
     * Create a new IP from the given string representation of an IP.
     * 
     * @param pIP
     *            the string representation of the IP of a server
     *            ("Server.Rack.Aisle.DC").
     * @return the new IP object.
     */
    public static IP create(final String pIP)
    {
        final String[] tokenIP = pIP.split("\\.");

        final int server = Integer.valueOf(tokenIP[0]);
        final int rack = Integer.valueOf(tokenIP[1]);
        final int aisle = Integer.valueOf(tokenIP[2]);
        final int dc = Integer.valueOf(tokenIP[3]);

        return new IP(server, rack, aisle, dc);
    }

    /**
     * Create a new IP from the given network positions.
     * 
     * @param pServer
     *            the specific server this IP points to.
     * @param pRack
     *            the rack which the server is contained in.
     * @param pAisle
     *            the aisle that the server is contained in.
     * @param pDC
     *            the datacentre that the server is contained in.
     * @return the new IP object.
     */
    public static IP create(final int pServer, final int pRack, final int pAisle, final int pDC)
    {
        return new IP(pServer, pRack, pAisle, pDC);
    }

    /**
     * Get the address type of this IP address
     * @return - the address type 
     */
    public IP.AddressType getAddressType() {
    	return addressType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return mServer + "." + mSubBlock + "." + mBlock + "." + mDC;
    }

    /**
     * Get the datacentre level IP-ID of the server corresponding to this IP.
     * 
     * @return the datacentre level IP-ID of the server corresponding to this
     *         IP.
     */
    public int dc()
    {
        return mDC;
    }

    /**
     * Get the block level IP-ID of the server corresponding to this IP.
     * 
     * @return the block level IP-ID of the server corresponding to this IP.
     */
    public int block()
    {
        return mBlock;
    }

    /**
     * Get the sub-block level IP-ID of the server corresponding to this IP.
     * 
     * @return the sub-block level IP-ID of the server corresponding to this IP.
     */
    public int subBlock()
    {
        return mSubBlock;
    }

    /**
     * Get the server level IP-ID of the server corresponding to this IP.
     * 
     * @return the server level IP-ID of the server corresponding to this IP.
     */
    public int server()
    {
        return mServer;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object pObject)
    {
        boolean equals = false;
        
        if (pObject == null)
        {
            equals = false;
        }
        else if (pObject.getClass().getName().equals(IP.class.getName()))
        {
            IP ip = (IP) pObject;
            
            if (this.dc() == ip.dc() && this.mBlock == ip.block() && this.subBlock() == ip.subBlock() && this.server() == ip.server())
            {
                equals = true;
            }
        }
        
        return equals;
    }
    
    /**
     * Navigate network between 2 IP addresses: point to point distance
     * 
     * Return the network "hops" (rack, aisle, dc)
     * Return 0 if same server
     * 
     * @param from - IP address of origin 
     * @param to - IP address of destination
     * @return number of network "hops" between network hubs/switches
     * 
     * ---
     * e.g. (1) between servers on same rack:
     * 	navigateIP(0.0.0.0, 3.0.0.0) returns 2
     * 
     * from(server 0, rack 0, aisle 0, dc 0)
     * to(server 3, rack 0, aisle 0, dc 0)
     * 
     * hops = 2 (server->rack () rack->server)
     * ---
     * 
     * e.g. (2) between servers on different rack, same aisle
     * 	navigateIP(0.1.0.0, 3.0.0.0) returns 4
     * 
     * from(server 0, rack 1, aisle 0, dc 0)
     * to(server 3, rack 0, aisle 0, dc 0)
     * 
     * hops = 4 (server->rack () rack->aisle () aisle->rack () rack->server)
     * ---
     * 
     * e.g. (3) between servers on different aisles
     * 	navigateIP(0.1.0.0, 3.0.1.0) returns 6
     * 
     * from(server 0, rack 1, aisle 0, dc 0)
     * to(server 3, rack 0, aisle 1, dc 0)
     * 
     * hops = 6 (server->rack () rack->aisle () aisle->dc () dc->aisle () aisle->rack () rack->server) 
     * ---
     * 
     * e.g. (4) between servers on different datacentre
     * 	navigateIP(0.1.0.0, 3.0.1.1) returns 8
     * 
     * from(server 0, rack 1, aisle 0, dc 0)
     * to(server 3, rack 0, aisle 1, dc 1)
     * 
     * hops = 8 (server->rack () rack->aisle () aisle->dc () dc->[internet] () [internet]->dc () dc->aisle () aisle->rack () rack->server)  
     * ---
     */
    public static int navigatePointToPoint(IP from, IP to) {
    	
    	int hops = 0; //same server
    	
    	if(from.dc()!=to.dc()) {
    		//different datacentres
    		hops = 8;
    	} else if (from.block()!=to.block()){
    		//different aisles
    		hops = 6;
    	} else if (from.subBlock()!=to.subBlock()){
    		//different rack
    		hops = 4;
    	} else if (from.server()!=to.server()) {
    		//different server
    		hops = 2;
    	}
    	
    	return hops;	
    }
    
    /**
     * Navigate network between this IP address and destination.
     * @param destinationAddress - IP address of destination server
     * @return - number of network "hops"
     */
    public int navigateToIP(IP destinationAddress) {
    	return navigatePointToPoint(this,destinationAddress);
    }
}
