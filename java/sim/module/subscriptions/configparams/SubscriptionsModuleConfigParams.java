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
package sim.module.subscriptions.configparams;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;
import sim.module.subscriptions.topology.NetworkTopologyFactory.NetworkTopology;

/**
 * Configuration Parameters Class for Subscription Network Topology and Protocol Classes 
 * 
 * @author cszjpc
 *
 */
public class SubscriptionsModuleConfigParams implements ModuleParamsInterface
{	
	protected Protocol protocolType;
	protected NetworkTopology topologyType;
	protected int maxSubscriptions;
	protected double miu;
	protected double rewire;
	protected boolean singleNodeUpdate;
		
	public static final double DEFAULT_MIU = 0.15;
	public static final double DEFAULT_REWIRE = 0.1;
	public static final int DEFAULT_MAX_SUBSCRIPTIONS = 10;
	public static final Protocol DEFAULT_PROTOCOL_TYPE = Protocol.P2P;
	public static final NetworkTopology DEFAULT_TOPOLOGY_TYPE = NetworkTopology.RANDOM_NETWORK;	
	public static final boolean DEFAULT_SINGLE_NODE_UPDATE = true;
	
	/**
	 * Probability of inverting the direction of an edge.
	 * 
	 * Used by Klemm-Eguiluz and Barabasi-Albert network topologies.
	 * 
	 * Value of 0.15 used by Ilango in SPECI, following:
	 * Yuan, Chen & Wang (2007) "Growing Directed Networks: Organization and Dynamics",
	 * New J. Physics, 9:*, 282-290, [Online] http://arxiv.org/pdf/cond-mat/0408391
	 */
	public static final double DEFAULT_INVERT_EDGE_PROBABILITY = 0.15;	

	protected static final String XML_ELEMENT_NAME = "subscriptions";
	
	public static final String REWIRE_XML_TAG = "rewire";
	public static final String MAX_SUBSCRIPTIONS_XML_TAG = "maxSubscribers";
	public static final String MIU_XML_TAG = "miu";
	public static final String TOPOLOGY_XML_TAG = "topology";
	public static final String PROTOCOL_XML_TAG = "protocol";
	public static final String SINGLE_NODE_UPDATE_XML_TAG = "singleNodeUpdate";
	
	public SubscriptionsModuleConfigParams(Protocol protocolType, NetworkTopology topologyType,
			int maxSubscriptions, double miu, double rewire, boolean singleNodeUpdate) {
		this.protocolType = protocolType;
		this.topologyType = topologyType;
		this.maxSubscriptions = maxSubscriptions;
		this.miu = miu;
		this.rewire = rewire;
		this.singleNodeUpdate = singleNodeUpdate;
	}
	
	public void setProtocol(Protocol p) {
		this.protocolType = p;
	}
	
	public void setTopology(NetworkTopology t) {
		this.topologyType = t;
	}
	
	public void setMaxSubscriptions(int n) {
		this.maxSubscriptions = n;
	}
	
	public void setRewire(double r) {
		this.rewire = r;
	}
	
	public void setMiu(double m) {
		this.miu = m;
	}
	
	public void setSingleNodeUpdate(boolean m) {
		this.singleNodeUpdate = m;
	}
	
	/**
	 * Get the update subscriptions protocol type
	 * 
	 * @return - the update subscriptions protocol type
	 */
	public Protocol getProtocolType() { return protocolType; }
	
	/**
	 * Get the network topology type of the subscriptions network
	 * 
	 * @return - the network topology type
	 */
	public NetworkTopology getTopologyType() { return topologyType; }
	
	/**
	 * Get the value of miu
	 * 
	 * @return - the value of miu
	 */
	public double getMiu() { return miu; }
	
	/**
	 * Get the probability of rewiring an edge when building a subscriptions network
	 * 
	 * @return - the rewiring probability
	 */
	public double getRewire() {	return rewire; }
	
	/**
	 * Get the maximum number of subscriptions for a Subscription Network
	 * 
	 * @return - the maximum number of subscriptions
	 */
	public int getMaxSubscriptions() { return maxSubscriptions; }
	
	/**
	 * How do we update subscriptions?  
	 * If SingleNodeUpdate, update each node's subscriptions individually
	 * If AllNodesUpdate, update all nodes' subscriptions at the same time
	 *  
	 * true if SingleNodeUpdate, false otherwise
	 */ 
	public boolean getSingleNodeUpdate() { return singleNodeUpdate; }

	/**
	 * Return default config parameters class
	 */
	public static SubscriptionsModuleConfigParams getDefault() {
		return new SubscriptionsModuleConfigParams(DEFAULT_PROTOCOL_TYPE, DEFAULT_TOPOLOGY_TYPE, DEFAULT_MAX_SUBSCRIPTIONS,
				DEFAULT_MIU, DEFAULT_REWIRE, DEFAULT_SINGLE_NODE_UPDATE);
	}
	
	@Override
	public String getXMLElementNameString() {
		return XML_ELEMENT_NAME;
	}
	
	/**
	 * Return an XML Element representation of a SubscriptionsConfigParameters 
	 * object (which can be used for saving to file)
	 * 
	 * @param params - the SubscriptionsConfigParameters we want to represent
	 * @return Element XML representation of params object
	 */
	public static Element getXML(SubscriptionsModuleConfigParams params) {
		
		Element e = new Element(XML_ELEMENT_NAME);		
		e.setAttribute(TOPOLOGY_XML_TAG, params.getTopologyType().getNameString());
		e.setAttribute(PROTOCOL_XML_TAG, params.getProtocolType().getNameString());
		e.setAttribute(MIU_XML_TAG, String.valueOf(params.getMiu()));
		e.setAttribute(MAX_SUBSCRIPTIONS_XML_TAG, String.valueOf(params.getMaxSubscriptions()));
		e.setAttribute(REWIRE_XML_TAG, String.valueOf(params.getRewire()));
		e.setAttribute(SINGLE_NODE_UPDATE_XML_TAG, String.valueOf(params.getSingleNodeUpdate()));
		
		return e;
	}
	
	/**
	 * Create and return a new SubscriptionConfigParams class from and XML Element
	 * 
	 * @param XMLElement - XML Element from which to create SubscriptionConfigParams object
	 * 
	 * @return new SubscriptionConfigParams object
	 */
	public static SubscriptionsModuleConfigParams createUsingXML(Element XMLElement){
		
		SubscriptionsModuleConfigParams p = SubscriptionsModuleConfigParams.getDefault();
		p.updateUsingXML(XMLElement);
		return p;
	}
	
	public String toString() {
		
		String s = "SubscriptionsConfigParams [";
		s+= "topology='" + topologyType + 
				"', protocol='" + protocolType + 
				"', maxSubscriptions='"+maxSubscriptions+
				"', miu='" + miu + 
				", rewire='"+rewire+
				", singleNodeUpdate='"+singleNodeUpdate+
				"']";
		return s;
	}

	@Override
	public Element getXML() {
		
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);		
		e.setAttribute(TOPOLOGY_XML_TAG, topologyType.getNameString());
		e.setAttribute(PROTOCOL_XML_TAG, protocolType.getNameString());
		e.setAttribute(MIU_XML_TAG, String.valueOf(miu));
		e.setAttribute(MAX_SUBSCRIPTIONS_XML_TAG, String.valueOf(maxSubscriptions));
		e.setAttribute(REWIRE_XML_TAG, String.valueOf(rewire));
		e.setAttribute(SINGLE_NODE_UPDATE_XML_TAG, String.valueOf(singleNodeUpdate));
		
		return e;
	}

	@Override
	public void updateUsingXML(Element e) 
	{
		try
		{	
			protocolType = Protocol.valueOf(e.getAttributeValue(PROTOCOL_XML_TAG));
			topologyType = NetworkTopology.valueOf(e.getAttributeValue(TOPOLOGY_XML_TAG));
			maxSubscriptions = Integer.parseInt(e.getAttributeValue(MAX_SUBSCRIPTIONS_XML_TAG));
			miu = Double.parseDouble(e.getAttributeValue(MIU_XML_TAG));
			rewire = Double.parseDouble(e.getAttributeValue(REWIRE_XML_TAG));
			singleNodeUpdate = Boolean.parseBoolean(e.getAttributeValue(SINGLE_NODE_UPDATE_XML_TAG));
		}
		catch (Exception ex)
		{
			logger.warn("Subscription Module ConfigParams attribute missing or invalid");
		}
	}
	
	@Override
	public void clone(ModuleParamsInterface params) {
		if(params.getClass().equals(this.getClass())) {
			this.protocolType = ((SubscriptionsModuleConfigParams) params).protocolType;
			this.topologyType = ((SubscriptionsModuleConfigParams) params).topologyType;
			this.maxSubscriptions = ((SubscriptionsModuleConfigParams) params).maxSubscriptions;
			this.miu = ((SubscriptionsModuleConfigParams) params).miu;
			this.rewire = ((SubscriptionsModuleConfigParams) params).rewire;
			this.singleNodeUpdate = ((SubscriptionsModuleConfigParams) params).singleNodeUpdate;
		} else {
			logger.warn("Ignoring changes: Attempting to clone parameters of incorrect class: " + params.getClass());
		}	
	}
}
