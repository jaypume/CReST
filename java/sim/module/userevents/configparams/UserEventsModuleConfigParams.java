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
package sim.module.userevents.configparams;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;


public class UserEventsModuleConfigParams implements ModuleParamsInterface {
	
	public static final String XML_NAME_STRING = "userEvents";
	
	public static final String FILENAME_XML_TAG = "file";

	private static String DEFAULT_FILENAME = "default-filename";
	
	protected String filename = DEFAULT_FILENAME;
	
	public UserEventsModuleConfigParams(String filename) {
		this.filename = filename;
	}
	
	public String getXMLElementNameString() {
		return XML_NAME_STRING ;
	}
	
	public String getFilename() {
		return filename;
	}
	
	/** 
	 * Set the user events filename: over-write any previous user events filename 
	 *
	 * @param filename - the user events filename
	 */
	public void setFilename(String filename) {
		logger.warn("Overwriting user events filename. Old='"+this.filename+"', new='"+filename+"'");
		this.filename = filename;
	}
	
	public static UserEventsModuleConfigParams getDefault() {
		return new UserEventsModuleConfigParams(DEFAULT_FILENAME);
	}
	
	@Override
	public Element getXML() {
		
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);		
		e.setAttribute(FILENAME_XML_TAG, filename);		
		return e;
	}

	@Override
	public void updateUsingXML(Element e) {		
		filename = e.getAttributeValue(FILENAME_XML_TAG);	
	}
	
	public String toString() {
		String s = "UserEventsConfigParams [";
		s+= FILENAME_XML_TAG+"='" + filename + 
				"']";
		return s;
	}
	
	@Override
	public void clone(ModuleParamsInterface params) {
		if(params.getClass().equals(this.getClass())) {
			this.filename = ((UserEventsModuleConfigParams) params).filename;
		} else {
			logger.warn("Ignoring changes: Attempting to clone parameters of incorrect class: " + params.getClass());
		}	
	}

}
