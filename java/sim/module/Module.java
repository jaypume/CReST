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
package sim.module;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import sim.module.broker.configparams.BrokerModuleConfigParams;
import sim.module.configparams.ModuleParamsInterface;
import sim.module.costs.configparams.CostsModuleConfigParams;
import sim.module.demand.DemandModuleRunner;
import sim.module.demand.configparams.DemandModuleConfigParams;
import sim.module.event.configparams.EventsModuleConfigParams;
import sim.module.failure.FailureModuleRunner;
import sim.module.failure.configparams.FailureModuleConfigParams;
import sim.module.pricing.configparams.PricingModuleConfigParams;
import sim.module.replacements.configparams.ReplacementModuleConfigParams;
import sim.module.service.ServiceModuleRunner;
import sim.module.service.configparams.ServiceModuleConfigParams;
import sim.module.sim.configparams.SimModuleConfigParams;
import sim.module.subscriptions.SubscriptionsModuleRunner;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.module.thermal.ThermalModuleRunner;
import sim.module.thermal.configParams.ThermalModuleConfigParams;
import sim.module.userevents.configparams.UserEventsModuleConfigParams;

/**
 * Enumeration class for all Modules - Used for configuration
 * 
 * To add a new module thread, add name to Module enumeration and extend AbstractModuleEventThread
 * 
 * @author cszjpc
 *
 */
public enum Module { 
	//TODO - JC, May 2012: This has become more of a configuration class, 
	//so should perhaps be moved to a different package
	
	BROKER_MODULE("Broker", "BROKER_MODULE", true, false, BrokerModuleConfigParams.getDefault()),
	PRICING_MODULE("Pricing", "PRICING_MODULE", true, false, PricingModuleConfigParams.getDefault()),
	COSTS_MODULE("Costs", "COSTS_MODULE", true, true, CostsModuleConfigParams.getDefault()),
	DEMAND_MODULE("Demand", "DEMAND_MODULE", false, true, DemandModuleConfigParams.getDefault(), DemandModuleRunner.getInstance()),
	EVENTS_MODULE("Events", "EVENTS_MODULE", true, true, EventsModuleConfigParams.getDefault()),
	FAILURE_MODULE("Failures", "FAILURE_MODULE", true, true, FailureModuleConfigParams.getDefault(), FailureModuleRunner.getInstance()),
	GUI_MODULE("GUI", "GUI_MODULE", false, true, "gui"), 
	LOG_MODULE("Log", "LOG_MODULE", false, true, "log"), 
//	NETWORK_MODULE("Network Traffic", "NETWORK_MODULE", true, false, "networkTraffic"),
//	POWER_MODULE("Power Usage", "POWER_MODULE", true, false, "powerUsage"),
	REPLACEMENTS_MODULE("Replacements", "REPLACEMENTS_MODULE", true, false, ReplacementModuleConfigParams.getDefault()),
	SERVICE_MODULE("Services", "SERVICE_MODULE", true, true, ServiceModuleConfigParams.getDefault(), ServiceModuleRunner.getInstance()),
	SIM_MODULE("Simulation", "SIM_MODULE", true, true, SimModuleConfigParams.getDefault()),
	SUBSCRIPTION_MODULE("Subscriptions", "SUBSCRIPTION_MODULE", true, true, SubscriptionsModuleConfigParams.getDefault(), SubscriptionsModuleRunner.getInstance()),
	THERMAL_MODULE("Thermal", "THERMAL_MODULE", true, true, ThermalModuleConfigParams.getDefault(), ThermalModuleRunner.getInstance()),
	USER_EVENTS_MODULE("User Events", "USER_EVENTS_MODULE", true, true, UserEventsModuleConfigParams.getDefault())
	; 
	
	public static Logger logger = Logger.getLogger(Module.class);
	
	public static final String XML_ELEMENT_LABEL = "modules";
	
	private String humanReadableString;
	private String nameString;
	private boolean userEditable; //Is module editable by user (via GUI?)  
	private String XMLElementNameString = "none";
	private boolean status; //true = on, false = off
	
	private ModuleParamsInterface params = null;
	private AbstractModuleRunner runner = null; //the module runner associated with the module
	
	/**
	 * Constructor for Enum Class
	 * 
	 * @param humanReadableDescription - a description of the enum type
	 * @param nameString - must be exactly the same as the enum type name
	 * @param userEditable - can user edit module on/off. If false, do not enable editing on GUI builder
	 * @param defaultOn - default setting of module. If true, module is set on - if false set off
	 */
	private Module(String humanReadableDescription, String nameString, boolean userEditable, boolean defaultOn) {
		this.humanReadableString = humanReadableDescription;
		this.nameString = nameString;
		this.userEditable = userEditable;
		this.status = defaultOn;
	}
	
	/**
	 * Constructor for Enum Class
	 * 
	 * @param humanReadableDescription - a description of the enum type
	 * @param nameString - must be exactly the same as the enum type name
	 * @param userEditable - can user edit module on/off. If false, do not enable editing on GUI builder
	 * @param defaultOn - default setting of module. If true, module is set on - if false set off
	 */
	private Module(String humanReadableDescription, String nameString, boolean userEditable, boolean defaultOn, String xmlElementName) {
		this(humanReadableDescription, nameString, userEditable, defaultOn);
		this.XMLElementNameString = xmlElementName;
	}
	
	/**
	 * Constructor for Enum Class
	 * 
	 * @param humanReadableDescription - a description of the enum type
	 * @param nameString - must be exactly the same as the enum type name
	 * @param userEditable - can user edit module on/off. If false, do not enable editing on GUI builder
	 * @param defaultOn - default setting of module. If true, module is set on - if false set off
	 */
	private Module(String humanReadableDescription, String nameString, boolean userEditable, 
			boolean defaultOn, ModuleParamsInterface params) {
		this(humanReadableDescription, nameString, userEditable, defaultOn);
		this.params = params;
		this.XMLElementNameString = params.getXMLElementNameString();
	}
	
	/**
	 * Constructor for Enum Class
	 * 
	 * @param humanReadableDescription - a description of the enum type
	 * @param nameString - must be exactly the same as the enum type name
	 * @param userEditable - can user edit module on/off. If false, do not enable editing on GUI builder
	 * @param defaultOn - default setting of module. If true, module is set on - if false set off
	 */
	private Module(String humanReadableDescription, String nameString, boolean userEditable, 
			boolean defaultOn, ModuleParamsInterface params, AbstractModuleRunner runner) {
		this(humanReadableDescription, nameString, userEditable, defaultOn);
		this.params = params;
		this.XMLElementNameString = params.getXMLElementNameString();
		this.runner = runner;
	}
	
	/**
	 * Configure modules using XML modules element
	 * @param modulesElement
	 */
	public static void configureUsingXML(Element modulesElement) {
		
		for(Module m: Module.values()) {
//		for(Module m: Module.getGUIEditableModules()) {
			
			Element e; //element for the module
			try {
				
				logger.debug("module = " + m);
				
				e = modulesElement.getChild(m.getXMLElementNameString());		
				
				logger.debug("element = " + e);
				
				m.setActive(Boolean.parseBoolean(e.getAttribute("on").getValue()));
				
				if(m.params!=null) {
					Element configElement = e.getChild(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);
					m.params.updateUsingXML(configElement);
				} else {
					//do nothing - there are no more config parameters
				}
				
			} catch (NullPointerException exception) {
				logger.warn("Module: " + m + ", Missing attribute: " + m.getXMLElementNameString() + ".  Defaulting to value = " + m.isActive() + " Attribute = " + exception.getMessage() );
			}
		}
	}

	/**
	 * Get Module XML Element for all Modules
	 * @return Element
	 */
	public static Element getModulesXMLElement() {
		
		//Create Module Element
		Element element = new Element(Module.XML_ELEMENT_LABEL);
		
		//Add SubElements for each Module type
		for(Module m: Module.values()) {
//		for(Module m: Module.getGUIEditableModules()) {
			Element subElement = m.getElement();
			element.addContent(subElement);
		}	
		
		return element;
	}
	
	/**
	 * Get Module as XML Element Tree
	 * 
	 * @return Element
	 */
	private Element getElement() {
		
		logger.info("Getting XML element for Module " + this.humanReadableString);
		//Create Element with Module name 
		Element e = new Element(this.XMLElementNameString);
		
		//Set "on" attribute (required)
		e.setAttribute("on", String.valueOf(status));
	
		if(params!=null) {
			logger.info("Config params = " + params);
			Element child = params.getXML();
			e.addContent(child);
		} else {
			//do nothing - there are no configuration parameters
			logger.info("There are no config params for this module");
		}	
		return e;
	}

	/**
	 * Human-readable string 
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
	 * @return label
	 */
	public static String getGUILabel() {
		return "Active Modules";
	}
	
	public String getXMLElementNameString() {
		return XMLElementNameString;
	}

	public String getFullDescriptionString() {
		String s = nameString + ": on = " + (status);
		s+= ", params = " + params;
		return s;
	}
	
	public boolean isActive() {
		return status;
	}
	
	public void setActive(boolean status) {
		this.status = status;
	}

	/**
	 * Get the config params for the module
	 * 
	 * @return the module params
	 */
	public ModuleParamsInterface getParams() {
		return params;
	}
	
	/**
	 * Get the ModuleRunner associated with the Module...
	 * 
	 * @return AbstactModuleRunner
	 */
	public AbstractModuleRunner getModuleRunner() {
		return runner;
	}
	
	/**
	 * Set new config parameters for this module
	 * @param params - the config parameters
	 */
	public void setParams(ModuleParamsInterface params) {
		this.params.clone(params); //Must clone otherwise parameters are not copied
	}

	/**
	 * Get all modules that are user editable (via the GUI)
	 * @return - array of gui editable modules
	 */
	public static Module[] getGUIEditableModules() {
		Module[] allMods = Module.values();
		List<Module> editableMods = new ArrayList<Module>();
		
		for(Module m: allMods) {
			if(m.userEditable) editableMods.add(m);
		}
		return editableMods.toArray(new Module[0]);
	}

	/** 
	 * Return a String description of all Modules
	 * 
	 * @return String description
	 */
	public static String describeAllModules() {
		
		String s = "\nFull description of all Modules:\n";
		for(Module m: Module.values()) {
			s += m.getFullDescriptionString() + "\n";
		}
		s+="-\n";
		return s;
	}
}
