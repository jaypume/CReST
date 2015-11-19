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
package sim.module.failure.configparams;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;

public class FailureModuleConfigParams implements ModuleParamsInterface {
	
	private static final String XML_ELEMENT_NAME = "failure";
	public static String MEAN_HARD_FIX_TIME_XML_TAG = "meanHardFixtime";
	public static String STDDEV_HARD_FIX_TIME_XML_TAG = "stdDevHardFixTime";
	public static String MEAN_SOFT_FIX_TIME_XML_TAG = "meanSoftFixTime";
	public static String STDDEV_SOFT_FIX_TIME_XML_TAG = "stdDevSoftFixTime";
	
	private static final double DEFAULT_MEAN_HARD_FIX_TIME_DAYS = 5;
    private static final double DEFAULT_STDDEV_HARD_FIX_TIME_DAYS = 1;    
    private static final double DEFAULT_MEAN_SOFT_FIX_TIME_MINUTES = 1;
    private static final double DEFAULT_STDDEV_SOFT_FIX_TIME_SECONDS = 30;
    
    protected double mMeanHardFixTime = DEFAULT_MEAN_HARD_FIX_TIME_DAYS;
	protected double mStdDevHardFixTime = DEFAULT_STDDEV_HARD_FIX_TIME_DAYS;
	protected double mMeanSoftFixTime = DEFAULT_MEAN_SOFT_FIX_TIME_MINUTES;
	protected double mStdDevSoftFixTime = DEFAULT_STDDEV_SOFT_FIX_TIME_SECONDS;
	
	public FailureModuleConfigParams(double meanHardFixTime, double stdDevHardFixTime, double meanSoftFixTime, double stdDevSoftFixTime) {
		this.mMeanHardFixTime = meanHardFixTime;
		this.mStdDevHardFixTime = stdDevHardFixTime;
		this.mMeanSoftFixTime = meanSoftFixTime;
		this.mStdDevSoftFixTime = stdDevSoftFixTime;
	}
	
	@Override
	public String getXMLElementNameString() {
		return XML_ELEMENT_NAME;
	}

	public static FailureModuleConfigParams getDefault() {
		return new FailureModuleConfigParams(
				DEFAULT_MEAN_HARD_FIX_TIME_DAYS, 
				DEFAULT_STDDEV_HARD_FIX_TIME_DAYS, 
				DEFAULT_MEAN_SOFT_FIX_TIME_MINUTES,
				DEFAULT_STDDEV_SOFT_FIX_TIME_SECONDS);
	}
	
	@Override
	public Element getXML() {	
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);
		e.setAttribute(MEAN_HARD_FIX_TIME_XML_TAG, String.valueOf(mMeanHardFixTime));
		e.setAttribute(STDDEV_HARD_FIX_TIME_XML_TAG, String.valueOf(mStdDevHardFixTime));
		e.setAttribute(MEAN_SOFT_FIX_TIME_XML_TAG, String.valueOf(mMeanSoftFixTime));
		e.setAttribute(STDDEV_SOFT_FIX_TIME_XML_TAG, String.valueOf(mStdDevSoftFixTime));
		return e;
	}

	@Override
	public void updateUsingXML(Element e) 
	{
		try
		{
			mMeanHardFixTime = Double.parseDouble(e.getAttributeValue(MEAN_HARD_FIX_TIME_XML_TAG));
			mStdDevHardFixTime = Double.parseDouble(e.getAttributeValue(STDDEV_HARD_FIX_TIME_XML_TAG));
			mMeanSoftFixTime = Double.parseDouble(e.getAttributeValue(MEAN_SOFT_FIX_TIME_XML_TAG));
			mStdDevSoftFixTime = Double.parseDouble(e.getAttributeValue(STDDEV_SOFT_FIX_TIME_XML_TAG));
		}
		catch (NumberFormatException ex)
		{
			logger.warn("Failures Module ConfigParams attribute missing or invalid");
		}
	}
	
	public String toString() {
		String s = "FailureModuleConfigParams [";
		s+= MEAN_HARD_FIX_TIME_XML_TAG+"='" + mMeanHardFixTime + 
				"', ";
		s+= STDDEV_HARD_FIX_TIME_XML_TAG+"='" + mStdDevHardFixTime + 
				"', ";
		s+= MEAN_SOFT_FIX_TIME_XML_TAG+"='" + mMeanSoftFixTime + 
				"', ";
		s+= STDDEV_SOFT_FIX_TIME_XML_TAG+"='" + mStdDevSoftFixTime + 
				"']";
		return s;
	}
	
	@Override
	public void clone(ModuleParamsInterface params) {
		if(params.getClass().equals(this.getClass())) {
			this.mMeanHardFixTime = ((FailureModuleConfigParams) params).mMeanHardFixTime;
			this.mStdDevHardFixTime = ((FailureModuleConfigParams) params).mStdDevHardFixTime;
			this.mMeanSoftFixTime = ((FailureModuleConfigParams) params).mMeanSoftFixTime;
			this.mStdDevSoftFixTime = ((FailureModuleConfigParams) params).mStdDevSoftFixTime;
		} else {
			logger.warn("Ignoring changes: Attempting to clone parameters of incorrect class: " + params.getClass());
		}	
	}
	
	public double getMeanHardFixTime()
	{
		return mMeanHardFixTime;
	}

	public double getStdDevHardFixTime()
	{
		return mStdDevHardFixTime;
	}

	public double getMeanSoftFixTime()
	{
		return mMeanSoftFixTime;
	}

	public double getStdDevSoftFixTime()
	{
		return mStdDevSoftFixTime;
	}

	public void setMeanHardFixTime(double meanHardFixTime)
	{
		this.mMeanHardFixTime = meanHardFixTime;
	}

	public void setStdDevHardFixTime(double stdDevHardFixTime)
	{
		this.mStdDevHardFixTime = stdDevHardFixTime;
	}

	public void setMeanSoftFixTime(double meanSoftFixTime)
	{
		this.mMeanSoftFixTime = meanSoftFixTime;
	}

	public void setStdDevSoftFixTime(double stdDevSoftFixTime)
	{
		this.mStdDevSoftFixTime = stdDevSoftFixTime;
	}
}
