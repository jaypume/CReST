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
 * @created 12 Jul 2011
 */
package sim.probability;

import java.util.Random;

import org.apache.log4j.Logger;

import cern.jet.random.engine.MersenneTwister;

/**
 * Singleton randomness object.
 */
public class RandomSingleton
{
	
	//log4j logger
	public static Logger logger = Logger.getLogger(RandomSingleton.class);
	
    // Singleton instance.
    private static RandomSingleton instance   = null;

    // Member variables.
    private MersenneTwister        mGenerator = new MersenneTwister();

    private long seed = -1;
    
    /*** java.util.Random class. Necessary for using Collections.shuffle(List, Random) ***/
    private Random java_util_Random;
    
    /**
     * Basic constructor.
     */
    private RandomSingleton()
    {

    }

    /**
     * Method to access the singleton random number generator.
     * 
     * @return A singleton random number generator.
     */
    public static RandomSingleton getInstance()
    {
        if (instance == null)
        {
            instance = new RandomSingleton();
        }

        return instance;
    }

    /**
     * Method to generate a random number x, 0.0 <= x < 1.0.
     * 
     * @return A random double between 0.0 and 1.0.
     */
    public double randomDouble()
    {
        return mGenerator.nextDouble();
    }

    /**
     * Method to generate a positive random int.
     * 
     * @return A random int.
     */
    public int randomInt()
    {
        return Math.abs(mGenerator.nextInt());
    }

    /**
     * Get the next (positive) pseudo-random long.
     * 
     * @return a pseudo-random long.
     */
    public long randomLong()
    {
        return Math.abs(mGenerator.nextLong());
    }

    /**
     * Reset the random number generator with the given seed.
     * 
     * @param seed
     *            The seed to use to reset the random number generator.
     */
    public void reset(final long seed)
    {
    	logger.info("Resetting random seed to: " + seed);
    	this.seed = seed;
        mGenerator = new MersenneTwister((int) seed);

       	java_util_Random = new Random(seed);
    }

    /**
     * Get the RandomEngine object responsible for the actual random number generation.
     * 
     * @return the MersenneTwister, RandomEngine object.
     */
    public MersenneTwister getEngine()
    {
        return mGenerator;
    }
    
    /**
     * Get a new copy of the RandomEngine object responsible for random number generation
     * 
     * @return a new copy of RandomEngine with original seed.
     */
    public MersenneTwister getNewEngine()
    {
    	return new MersenneTwister((int) seed);
    }
    
    /**
     * Get a java.util.Random class.  
     * 
     * @WARNING Only use when necessary, e.g., for Collections.shuffle(List,Random).
     * 
     * @return java.util.Random
     */
    public Random getJavaUtilRandom() {
    	return java_util_Random;
    }
}
