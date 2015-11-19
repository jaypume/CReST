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
package sim.module.replacements.configparams;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;
import sim.physical.Block;

public class ReplacementModuleConfigParams implements ModuleParamsInterface{

	protected static final String XML_ELEMENT_NAME = "replacements";
	public static String REPLACE_IN_BLOCKS_XML_TAG = "replaceInBlocks";
	public static String USE_REPLACEMENT_FN_XML_TAG = "replaceUsingFunction";
	public static String USE_PRESET_SERVER_TYPES_XML_TAG = "replaceUsingPresets";
	public static String REPLACEMENT_THRESHOLD_AISLE_XML_TAG = "replacementThresholdAisle";
	public static String REPLACEMENT_THRESHOLD_CONTAINER_XML_TAG = "replacementThresholdContainer";
	public static String REPLACEMENT_THRESHOLD_RACK_XML_TAG = "replacementThresholdRack";
	
	private static final boolean DEFAULT_REPLACE_IN_BLOCKS = true;
	private static final boolean DEFAULT_IS_ON_PRESET_SERVER_TYPES = true;
	private static final boolean DEFAULT_IS_ON_REPLACEMENT_FUNCTION = false;
	private static final double DEFAULT_REPLACEMENT_THRESHOLD_AISLE = 0.5;
	private static final double DEFAULT_REPLACEMENT_THRESHOLD_CONTAINER = 0.5;
	private static final double DEFAULT_REPLACEMENT_THRESHOLD_RACK = 0.5;
	
	//What method is used to replace servers?
	protected boolean mReplaceInBlocks = DEFAULT_REPLACE_IN_BLOCKS;
    protected boolean mIsOnPresetServerTypes = DEFAULT_IS_ON_PRESET_SERVER_TYPES;
    protected boolean mIsOnReplacementFunction = DEFAULT_IS_ON_REPLACEMENT_FUNCTION;
    // Failure config.
    protected double mReplacementThresholdAisle = DEFAULT_REPLACEMENT_THRESHOLD_AISLE;
    protected double mReplacementThresholdContainer = DEFAULT_REPLACEMENT_THRESHOLD_CONTAINER;
    protected double mReplacementThresholdRack = DEFAULT_REPLACEMENT_THRESHOLD_RACK;
	
	public ReplacementModuleConfigParams(boolean replaceInBlocks, boolean replaceWithPresetServers, boolean replaceUsingFunction, double replacementThresholdAisle, double replacementThresholdContainer, double replacementThresholdRack) {
		
		this.mReplaceInBlocks = replaceInBlocks;
		this.mIsOnPresetServerTypes = replaceWithPresetServers;
		this.mIsOnReplacementFunction = replaceUsingFunction;
		this.mReplacementThresholdAisle = replacementThresholdAisle;
		this.mReplacementThresholdContainer = replacementThresholdContainer;
		this.mReplacementThresholdRack = replacementThresholdRack;
	}
	
	public static ReplacementModuleConfigParams getDefault() {
		return new ReplacementModuleConfigParams(
				DEFAULT_REPLACE_IN_BLOCKS,
				DEFAULT_IS_ON_PRESET_SERVER_TYPES,
				DEFAULT_IS_ON_REPLACEMENT_FUNCTION,
				DEFAULT_REPLACEMENT_THRESHOLD_AISLE,
				DEFAULT_REPLACEMENT_THRESHOLD_CONTAINER,
				DEFAULT_REPLACEMENT_THRESHOLD_RACK
				);
	}
	
	@Override
	public Element getXML() {
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);
		e.setAttribute(REPLACE_IN_BLOCKS_XML_TAG, String.valueOf(mReplaceInBlocks));
		e.setAttribute(USE_PRESET_SERVER_TYPES_XML_TAG, String.valueOf(mIsOnPresetServerTypes));
		e.setAttribute(USE_REPLACEMENT_FN_XML_TAG, String.valueOf(mIsOnReplacementFunction));
		e.setAttribute(REPLACEMENT_THRESHOLD_AISLE_XML_TAG, String.valueOf(mReplacementThresholdAisle));
		e.setAttribute(REPLACEMENT_THRESHOLD_CONTAINER_XML_TAG, String.valueOf(mReplacementThresholdContainer));
		e.setAttribute(REPLACEMENT_THRESHOLD_RACK_XML_TAG, String.valueOf(mReplacementThresholdRack));
		return e;
	}

	@Override
	public void updateUsingXML(Element e) 
	{
		try
		{
			mReplaceInBlocks = Boolean.parseBoolean(e.getAttributeValue(REPLACE_IN_BLOCKS_XML_TAG));
			mIsOnPresetServerTypes = Boolean.parseBoolean(e.getAttributeValue(USE_PRESET_SERVER_TYPES_XML_TAG));
			mIsOnReplacementFunction = Boolean.parseBoolean(e.getAttributeValue(USE_REPLACEMENT_FN_XML_TAG));		
			mReplacementThresholdAisle = Double.parseDouble(e.getAttributeValue(REPLACEMENT_THRESHOLD_AISLE_XML_TAG));
			mReplacementThresholdContainer = Double.parseDouble(e.getAttributeValue(REPLACEMENT_THRESHOLD_CONTAINER_XML_TAG));
			mReplacementThresholdRack = Double.parseDouble(e.getAttributeValue(REPLACEMENT_THRESHOLD_RACK_XML_TAG));
		}
		catch (Exception ex)
		{
			logger.warn("Replacement Module ConfigParams attribute missing or invalid");
		}
	}

	@Override
	public void clone(ModuleParamsInterface params) {
		this.mReplaceInBlocks = ((ReplacementModuleConfigParams) params).mReplaceInBlocks;
		this.mIsOnPresetServerTypes = ((ReplacementModuleConfigParams) params).mIsOnPresetServerTypes;
		this.mIsOnReplacementFunction = ((ReplacementModuleConfigParams) params).mIsOnReplacementFunction;		
		this.mReplacementThresholdAisle = ((ReplacementModuleConfigParams) params).mReplacementThresholdAisle;
		this.mReplacementThresholdContainer = ((ReplacementModuleConfigParams) params).mReplacementThresholdContainer;
		this.mReplacementThresholdRack = ((ReplacementModuleConfigParams) params).mReplacementThresholdRack;
	}

	@Override
	public String getXMLElementNameString() {
		return XML_ELEMENT_NAME;
	}

	public String toString() {
		
		String s = "ReplacementModuleConfigParams [";
		s+= "replaceInBlocks='" + this.mReplaceInBlocks +
				"', replaceWithPresetServerTypes='" + this.mIsOnPresetServerTypes +
				"', replaceUsingFunction='" + this.mIsOnReplacementFunction + 
				"', mReplacementThresholdAisle='" + this.mReplacementThresholdAisle + 
				"', mReplacementThresholdContainer='" + this.mReplacementThresholdContainer + 
				"', mReplacementThresholdRack='" + this.mReplacementThresholdRack + 
				"']";
		return s;
	}	
	
	public boolean isReplacementInBlocks() {
		return this.mReplaceInBlocks;
	}
	
	public void setReplacementInBlocks(boolean status) {
		this.mReplaceInBlocks = status;
	}
	
	public boolean isReplacementViaPresetTypes() {
		return this.mIsOnPresetServerTypes;
	}
	
	public void setReplacemenetViaPresetTypes(boolean status) {
		this.mIsOnPresetServerTypes = status;
	}
	
	public boolean isReplacementViaFunctionTypes() {
		return this.mIsOnReplacementFunction;
	}
	
	public void setReplacementViaFunctionTypes(boolean status) {
		this.mIsOnReplacementFunction = status;
	}	
	
	public double getReplacementThresholdAisle() {
		return this.mReplacementThresholdAisle;
	}
	
	public void setReplacementThresholdAisle(double pReplacementThresholdAisle) {
		this.mReplacementThresholdAisle = pReplacementThresholdAisle;
	}	
	
	public double getReplacementThresholdContainer() {
		return this.mReplacementThresholdContainer;
	}
	
	public void setReplacementThresholdContainer(double pReplacementThresholdContainer) {
		this.mReplacementThresholdContainer = pReplacementThresholdContainer;
	}
	
	public double getReplacementThresholdRack() {
		return this.mReplacementThresholdRack;
	}
	
	public void setReplacementThresholdRack(double pReplacementThresholdRack) {
		this.mReplacementThresholdRack = pReplacementThresholdRack;
	}	
	
	/**
	 * Get the replacement threshold for a given hardware block	 
	 * @param pType - the block type
	 * @return - the replacement threshold
	 */
    public double getReplacementThreshold(Block.Type pType)
    {
        double threshold;

        switch (pType)
        {
            case aircon:
            {
                // An aircon should be replaced as soon as it is done.
                threshold = 1.0;
                break;
            }
            case aisle:
            {
                threshold = mReplacementThresholdAisle;
                break;
            }
            case container:
            {
                threshold = mReplacementThresholdContainer;
                break;
            }
            case rack:
            {
                threshold = mReplacementThresholdRack;
                break;
            }
            default:
            {
                // In case of a problem checking what type of block this is, replace immediately.
                threshold = 0.0;
                break;
            }
        }

        return threshold;
    }
}
