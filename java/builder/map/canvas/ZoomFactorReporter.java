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
 * Enables a class to report about it's Zoom Level to a number of listeners. It
 * is designed to be used with the ZoomLevelListener interface.
 * 
 * Note that throughout this program the phrases "Zoom Level" and "Zoom Factor"
 * describe two different things. One of them is the discrete "Zoom Level" (as
 * the object) that we are currently in. This influlances things such as how the
 * objects on the canvas are displayed. The second is the continuous
 * "Zoom Factor", which is a double, and a measure of how zoomed in we are on
 * the canvas.
 * 
 * @author james
 * 
 */
public interface ZoomFactorReporter
{

	/**
	 * This adds a ZoomLevelListner to the object. The listener will be updated
	 * (by calling it's updatedZoomLevel method) whenever the zoom level is
	 * changed.
	 * 
	 * @param pListener
	 *            the listener to add.
	 */
	public void addZoomFactorListener(ZoomFactorListener pListener);

	/**
	 * Removes a listener from the object. This ZoomLevelListner will no longer
	 * be updated about zoom level changes.
	 * 
	 * @param pListener
	 *            the listener to remove
	 */
	public void removeZoomFactorListener(ZoomFactorListener pListener);

	/**
	 * Allows another object (typically a ZoomlevelListener) to directly change
	 * the zoom level of the reporter. This WILL trigger an update to all
	 * listeners. (possibly including the object that updated this in the first
	 * place)
	 * 
	 * @param zoom
	 *            the new zoom level
	 */
	public void setZoomFactor(double zoom);
	
	/**
	 * Allows a registered (or theoretically an unregistered) object to query
	 * the ZoomFactorReporter directly about it's current Zoom Factor. It is
	 * NOT recommended to poll this method. Use it only in initial setup. (such
	 * as constructors.)
	 * 
	 * @return zoom factor
	 */
	public double getZoomFactor();
}
