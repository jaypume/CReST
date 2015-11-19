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
package sim.module.subscriptions.protocol;

import org.apache.log4j.Logger;

public class UpdateProtocolFactory {

	public static Logger logger = Logger.getLogger(UpdateProtocolFactory.class);
	
	/**
	 * Enumeration class for Subscription Update Protocols
	 * 
	 * @author cszjpc
	 *
	 */
	public enum Protocol { 
		
		//The Protocols
		P2P("Simple Peer-To-Peer", "P2P"), 
		TP2P("Transitive Peer-To-Peer", "TP2P"), 
		CENTRAL("Centralised", "CENTRAL");
		
		private String humanReadableString;
		private String nameString;
		
		/**
		 * Constructor for Enum Class
		 * 
		 * @param humanReadableDescription - a description of the enum type
		 * @param nameString - must be exactly the same as the enum type name
		 */
		Protocol(String humanReadableDescription, String nameString) {
			this.humanReadableString = humanReadableDescription;
			this.nameString = nameString;
		}
		
		/**
		 * Human-readable Protocol string 
		 */
		public String toString() {
			return humanReadableString;
		}
		
		/**
		 * Enumeration name as string
		 */
		public String getNameString() {
			return nameString;
		}
		
		/**
		 * A short label description of the Enum class
		 * 
		 * @return name label
		 */
		public static String getLabel() {
			return "Update Protocol";
		}
	}
	
	/**
	 * Do not instantiate an object of this type (hence, private)
	 */
	private UpdateProtocolFactory() {}
	
	public static AbstractUpdateProtocol getProtocol(Protocol protocolType, int datacentreID) {
		
		logger.debug("Creating AbstractUpdateProtocol of type: " + protocolType);
		
		//Return Correct Topology Type
		switch(protocolType) {
		
			case P2P: return (AbstractUpdateProtocol) new P2PUpdateProtocol();
			case TP2P: return (AbstractUpdateProtocol) new TP2PUpdateProtocol();
			case CENTRAL: return (AbstractUpdateProtocol) new CentralUpdateProtocol(datacentreID);
			
			default: {
				logger.fatal("Uknown UpdateProtocol type: " + protocolType + " Exiting System...");
				System.exit(0);
				return null;				
			}	
		}
	}
}
