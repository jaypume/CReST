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
package sim.module.configparams;

import org.apache.log4j.Logger;
import org.jdom.Element;

public interface ModuleParamsInterface {
	
	public static Logger logger = Logger.getLogger(ModuleParamsInterface.class);
	
	public static String XML_ELEMENT_NAME_STRING = "configParams";
	
	/**
	 * Return an XML Element representation of the ModuleConfigParams 
	 * object (which can be used for saving to file)
	 * 
	 * @return Element XML representation of params object
	 */
	public abstract Element getXML();

	/**
	 * Update ModuleConfigParams class using an XML Element
	 * 
	 * @param e - XML Element from which to update the ModuleConfigParams object
	 */
	public abstract void updateUsingXML(Element e);
	
	/**
	 * Output config params as a string
	 * 
	 * @return parameters string
	 */
	public abstract String toString();
	
	/**
	 * make a clone of ModuleParams
	 */
	public abstract void clone(ModuleParamsInterface params);
	
	/**
	 * Get the XML Element name for this ModuleParams class
	 * @return - String XML Element Name
	 */
	public abstract String getXMLElementNameString();
}
