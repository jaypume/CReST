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
package sim.module.event.configparams;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;


import config.ProbabilityDistribution;

public class EventsModuleConfigParams implements ModuleParamsInterface {
	
	public static final String FREQUENCY_XML_TAG = "spawnerFrequency";
	public static final String DIST_XML_TAG = "typeDistribution";

	private static final long DEFAULT_FREQUENCY = 1l;
	private static final ProbabilityDistribution DEFAULT_DIST = ProbabilityDistribution.poisson;
	
	protected long spawnerFrequency = DEFAULT_FREQUENCY;	
	protected ProbabilityDistribution dist = DEFAULT_DIST;
	
	public EventsModuleConfigParams(long frequency, ProbabilityDistribution dist) {
		this.spawnerFrequency = frequency;
		this.dist = dist;
	}
	
	@Override
	public String getXMLElementNameString() {
		return "event";
	}
	
	public long getSpawnerFrequency() {
		return spawnerFrequency;
	}
	
	public ProbabilityDistribution getProbabilityDistribution() {
		return dist;
	}
	
	public static EventsModuleConfigParams getDefault() {
		return new EventsModuleConfigParams(DEFAULT_FREQUENCY, DEFAULT_DIST);
	}
	
	@Override
	public Element getXML() {
		
		System.out.println("EventsModuleConfigParams = " + this);
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);		
		e.setAttribute(FREQUENCY_XML_TAG, Long.toString(spawnerFrequency));	
		e.setAttribute(DIST_XML_TAG, dist.toString());
		return e;
	}

	@Override
	public void updateUsingXML(Element e) {		
		spawnerFrequency = Long.parseLong(e.getAttributeValue(FREQUENCY_XML_TAG));	
		dist = ProbabilityDistribution.parseProb(e.getAttributeValue(DIST_XML_TAG));
	}
	
	public String toString() {
		String s = "EventsConfigParams [";
		s+= FREQUENCY_XML_TAG+"='" + spawnerFrequency + "', " + 
				DIST_XML_TAG+"='" + dist + "']";
		return s;
	}
	
	@Override
	public void clone(ModuleParamsInterface params) {
		if(params.getClass().equals(this.getClass())) {
			this.spawnerFrequency = ((EventsModuleConfigParams) params).spawnerFrequency;
			this.dist = ((EventsModuleConfigParams) params).dist;
		} else {
			logger.warn("Ignoring changes: Attempting to clone parameters of incorrect class: " + params.getClass());
		}	
	}
}
