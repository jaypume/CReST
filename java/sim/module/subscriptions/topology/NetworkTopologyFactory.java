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
package sim.module.subscriptions.topology;

import org.apache.log4j.Logger;

import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.physical.network.IP;

public class NetworkTopologyFactory {

	public static Logger logger = Logger.getLogger(NetworkTopologyFactory.class);
	
	/**
	 * Enumeration class for Network Topologies
	 * 
	 * @author cszjpc
	 *
	 */
	public enum NetworkTopology { 
	
		//The Topologies
		RANDOM_NETWORK("Random", "RANDOM_NETWORK"), 
		NEAREST_NEIGHBOURS("Nearest Neighbours", "NEAREST_NEIGHBOURS"), 
		REGULAR_GRID_LATTICE("Regular Grid Lattice", "REGULAR_GRID_LATTICE"),
		BARABASI_ALBERT_SCALE_FREE("Barabasi-Albert (Scale-Free)", "BARABASI_ALBERT_SCALE_FREE"),
		WATTS_STROGATZ_SMALL_WORLD("Watts-Strogatz (Small-World)", "WATTS_STROGATZ_SMALL_WORLD"),
		KLEMM_EGUILUZ_SCALE_FREE_SMALL_WORLD("Klemm-Eguiluz (Scale-Free Small-World)", "KLEMM_EGUILUZ_SCALE_FREE_SMALL_WORLD"),
		TEST_NETWORK("Pre-Defined (For Testing Only)", "TEST_NETWORK");
		
		private String humanReadableString;
		private String nameString;
		
		/**
		 * Constructor for Enum Class
		 * 
		 * @param humanReadableDescription - a description of the enum type
		 * @param nameString - must be exactly the same as the enum type name
		 */
		NetworkTopology(String humanReadableDescription, String nameString) {
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
			return "Network Topology";
		}
	};
	
	/**
	 * Do not instantiate an object of this type (hence, private)
	 */
	private NetworkTopologyFactory() {}
	
	public static AbstractNetworkTopology getTopology(IP[] ips_array, int[] ids_array, SubscriptionsModuleConfigParams configParams, long timestamp) {
	
		logger.debug("Creating AbstractNetworkTopology with parameters: " + configParams);
		
		//Return Correct Topology Type
		switch(configParams.getTopologyType()) {
		
			case RANDOM_NETWORK: return (AbstractNetworkTopology) new RandomNetworkTopology(ips_array, ids_array, configParams, timestamp);
			case WATTS_STROGATZ_SMALL_WORLD: return (AbstractNetworkTopology) new SmallWorldNetworkTopology(ips_array, ids_array, configParams, timestamp);
			case BARABASI_ALBERT_SCALE_FREE: return (AbstractNetworkTopology) new ScaleFreeNetworkTopology(ips_array, ids_array, configParams, timestamp);
			case KLEMM_EGUILUZ_SCALE_FREE_SMALL_WORLD: return (AbstractNetworkTopology) new KlemmEguiluzNetworkTopology(ips_array, ids_array, configParams, timestamp);
			case REGULAR_GRID_LATTICE: return (AbstractNetworkTopology) new GridLatticeNetworkTopology(ips_array, ids_array, configParams, timestamp);
			case NEAREST_NEIGHBOURS: return (AbstractNetworkTopology) new NearestNeighbourNetworkTopology(ips_array, ids_array, configParams, timestamp);
			case TEST_NETWORK: return (AbstractNetworkTopology) new TestNetworkTopology(ips_array, ids_array, configParams, timestamp);
			
			default: {
				logger.fatal("Uknown NetworkTopology type: " + configParams.getTopologyType() + " Exiting System...");
				System.exit(0);
				return null;
			}	
		}
	}
}
