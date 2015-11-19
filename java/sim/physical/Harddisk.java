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
 * @created 7 Jul 2011
 */
package sim.physical;

/**
 * Class representing a physical harddisk.
 */
public class Harddisk
{
    private final int    mMaxSpace;               // GB
    private int          mFreeSpace;              // GB
    private final double mReadSpeed;              // MB/s
    private final double mWriteSpeed;             // MB/s
    private boolean      mIsAlive          = true;
    protected double     CHANCE_OF_FAILURE = 0;

    /**
     * Default constructor to create a new preset harddisk.
     */
    public Harddisk()
    {
        mMaxSpace = 8 * 1024;
        mFreeSpace = mMaxSpace;
        mReadSpeed = 300;
        mWriteSpeed = 300;
        setAge(0);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String string;

        string = "Free/Max space: " + mFreeSpace + "/" + mMaxSpace + "GB, Read/Write speed: " + mReadSpeed + " / " + mWriteSpeed + " MB/s";

        return string;
    }

    /**
     * Check if this harddisk is alive.
     * 
     * @return true if the harddisk is alive, else false.
     */
    boolean isAlive()
    {
        return mIsAlive;
    }

    /**
     * Set the age of this harddisk.
     * 
     * @param age the age to set the harddisk to.
     */
    void setAge(int age)
    {
        if (age == 0)
            CHANCE_OF_FAILURE = 0.005747;
        else if (age == 1)
            CHANCE_OF_FAILURE = 0.005473;
        else if (age >= 2)
            CHANCE_OF_FAILURE = 0.021894;
    }
}
