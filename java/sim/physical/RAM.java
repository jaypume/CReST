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
/**
 * @author Luke Drury (ld8192)
 * @created 7 Jul 2011
 */
package sim.physical;

/**
 * Class representing physical RAM within a server.
 */
public class RAM
{
	private final String mModel;
	private final double mSpeed; // MHz
	private final int mSize; // MB

	/**
	 * Default constructor to create new preset RAM.
	 */
	public RAM()
	{
		mModel = "SimCPU";
		mSpeed = 1066;
		mSize = 4096;
	}
	
	public RAM(String pModel, double pSpeed, int pSize)
	{
		mModel = pModel;
		mSpeed = pSpeed;
		mSize = pSize;
	}

	public String getModel()
	{
		return mModel;
	}

	public double getSpeed()
	{
		return mSpeed;
	}

	public int getSize()
	{
		return mSize;
	}

	/**
	 * Method to return useful information about this RAM.
	 * 
	 * @return A String of useful information about this RAM.
	 */
	public String toString()
	{
		String string;

		string = "Model: " + mModel + ", Speed: " + mSpeed + " MHz, Size: "
				+ mSize + " MB";

		return string;
	}
}
