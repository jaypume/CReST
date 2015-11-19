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
package builder.map.canvas;

/**
 * Represents a single zoom 'level'. This is a continuous subset of the Real
 * numbers between this and the next lower (further zoomed out) level. These
 * clases are immutable and it is reccomnded that they only be created by
 * ZoomLevelFactory and passed, by referance, from that class' static method
 * getZoomLevels(). This will return all supported zoom levels.
 * 
 * @author james
 * 
 */
public class ZoomLevel implements Comparable<ZoomLevel>
{

	// The name of the zoom level, for display in the GUI
	private final String mName;
	// The minimum amount of 'zoom' needed to enter this level
	private final float mZoomFactor;
	// The ordering of the level as compared with others. This is to allow
	// comparison.
	private final int mOrdering;

	ZoomLevel(String pName, float pZoomFactor, int pOrdering)
	{
		mName = pName;
		mZoomFactor = pZoomFactor;
		mOrdering = pOrdering;
	}

	/**
	 * Get the text name of this level. This is to be used in the user interface
	 * to help better describe what each level is for.
	 * 
	 * @return name
	 */
	public String getName()
	{
		return mName;
	}

	/**
	 * Get the zoom factor described by this level. Remember that the level
	 * itself describes this zoom factor inclusively down to the next lowest
	 * zoom factor, not inclusive.
	 * 
	 * @return zoom factor
	 */
	public float getZoomFactor()
	{
		return mZoomFactor;
	}

	public int getOrdering()
	{
		return mOrdering;
	}

	@Override
	public int compareTo(ZoomLevel z)
	{
		return this.getOrdering() - z.getOrdering();
	}
}
