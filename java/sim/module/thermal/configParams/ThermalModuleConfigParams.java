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
package sim.module.thermal.configParams;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;
import sim.module.thermal.model.ThermalModelFactory.ThermModel;


public class ThermalModuleConfigParams implements ModuleParamsInterface {
	
	public static final String XML_NAME_STRING = "temperature";
	public static final String PERIOD_XML_TAG = "eventPeriod";
	public static final String MODEL_XML_TAG = "thermalModel";
	
	public static final Long DEFAULT_PERIOD = 1l;
	protected Long period = DEFAULT_PERIOD;
	
	protected ThermModel modelType;
	
	public String getXMLElementNameString() {
		return XML_NAME_STRING;
	}
	
	public long getEventPeriod() {
		return period;
	}
	
	public static ThermalModuleConfigParams getDefault() {
		return new ThermalModuleConfigParams(ThermModel.DEFAULT,DEFAULT_PERIOD);
	}
	
	public ThermalModuleConfigParams(ThermModel modelType, long updatePeriod) {
		this.modelType = modelType;
		this.period = updatePeriod;
	}

	/**
	 * Get the thermal model type
	 * 
	 * @return - the thermal model type
	 */
	public ThermModel getThermalModelType() { return modelType; }
	
	
	@Override
	public Element getXML() {
		
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);		
		e.setAttribute(PERIOD_XML_TAG, Long.toString(period));	
		e.setAttribute(MODEL_XML_TAG, modelType.getNameString());
		return e;
	}
	
	/**
	 */
	public static Element getXML(ThermalModuleConfigParams params) {		
		return params.getXML();
	}
	
	@Override
	public void updateUsingXML(Element e) 
	{
		try
		{	
			modelType = ThermModel.valueOf(e.getAttributeValue(MODEL_XML_TAG));
			period = Long.parseLong(e.getAttributeValue(PERIOD_XML_TAG));
		}
		catch (Exception ex)
		{
			logger.warn("Thermal Module ConfigParams attribute missing or invalid! " + ex.getMessage());
		}
	}
	
	/**
	 * Create and return a new SubscriptionConfigParams class from and XML Element
	 * 
	 * @param XMLElement - XML Element from which to create SubscriptionConfigParams object
	 * 
	 * @return new SubscriptionConfigParams object
	 */
	public static ThermalModuleConfigParams createUsingXML(Element XMLElement){
		
		ThermalModuleConfigParams p = ThermalModuleConfigParams.getDefault();
		p.updateUsingXML(XMLElement);
		return p;
	}
	
	public String toString() {
		String s = "ThermalModuleConfigParams [";
		s+= PERIOD_XML_TAG+"='" + period + "', " + MODEL_XML_TAG + "='" + modelType +
				"']";
		return s;
	}

	@Override
	public void clone(ModuleParamsInterface params) {
		if(params.getClass().equals(this.getClass())) {
			this.period = ((ThermalModuleConfigParams) params).period;
			this.modelType = ((ThermalModuleConfigParams) params).modelType;
		} else {
			logger.warn("Ignoring changes: Attempting to clone parameters of incorrect class: " + params.getClass());
		}	
	}

}
