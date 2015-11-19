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
package builder.datacentre.layout;

import java.util.List;
import java.util.Random;

import config.DatacentreGeneratorProgressListener;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRack;
import config.physical.ConfigServer;
import builder.datacentre.layout.LayoutPatternFactory.LayoutPatternType;

public abstract class AbstractLayoutPattern
{
	protected LayoutPatternType layoutPatternType;	

	protected ConfigDatacentre mGeneratedDatacentre;
	protected int mWidth;
	protected int mLength;
	protected boolean mTopAirCon;
	protected boolean mBottomAirCon;
	protected int[] mNetworkDistances;
	
	protected int mAisles;
	protected int mRacksnumber;
	protected ConfigServer[] mServers;
	protected ConfigRack[] mRacks;
	
	protected Random r;
	
	public AbstractLayoutPattern(LayoutPatternType layoutPatternType)
	{
		this.layoutPatternType = layoutPatternType;
	}
	
	/**
	 * Performs the generating & filling of the datacentre for a particular layout pattern
	 */
	public abstract void generateLayout(ConfigDatacentre pGeneratedDatacentre, int[] pNetworkDistances, List<DatacentreGeneratorProgressListener> listeners);
		
	/**
	 * The Layout Pattern Type for this Layout Pattern
	 * 
	 * @return layoutPatternType
	 */
	public LayoutPatternType getLayoutPatternType()
	{
		return layoutPatternType;
	}
}
