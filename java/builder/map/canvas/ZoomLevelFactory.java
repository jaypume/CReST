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
 * Generates objects for the zoom levels supported. (These are immutable) Only
 * one copy of each object is created and as they are Immutable it is
 * recommended that these are always used in a PassByReferance way.
 * 
 * @author james
 * 
 */

public class ZoomLevelFactory
{

	// Static instances of all of the supported zoom levels
	public static final ZoomLevel[] LEVELS =
	{ new ZoomLevel("Server", 20f, 0), new ZoomLevel("Rack", 10f, 1),
			new ZoomLevel("Datacenter", 0.5f, 2),
			/*new ZoomLevel("Global", 0.5f, 3)*/ };

	/**
	 * The (empty) constructor is set as private to make this a 'private' class.
	 */
	private ZoomLevelFactory()
	{
	};

	/**
	 * Gives the array of immutableZoom Level objects supported by the program.
	 * Simply calling ZoomLevelFactory.LEVELS also gives access to the same
	 * thing, but for coding standard reasons use of this method is recommended.
	 * 
	 * @return the immutable array of supported zoom levels.
	 */
	public static ZoomLevel[] getZoomLevels()
	{
		return LEVELS;
	}

	/**
	 * Find a Zoom Level by it's specific text name.
	 * 
	 * @param name
	 *            the name to find
	 * @return returns the ZoomLevel requested, null otherwise.
	 */
	public static ZoomLevel getZoomLevelByName(String name)
	{
		for (ZoomLevel z : getZoomLevels())
		{
			if (z.getName().toLowerCase().equals(name.toLowerCase()))
			{
				return z;
			}
		}
		return null;
	}

}
