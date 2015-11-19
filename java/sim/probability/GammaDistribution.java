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

package sim.probability;

/**
 * Gamma distribution and lookup table for useful parameter combinations
 * 
 */
public final class GammaDistribution {

	/*
	 * Find the Gamma values:
	 * @Param Key base10 log of Nodes divided by failure rate
	 * e.g. 100, 10% -> 1000 -> 3
	 * e.g.2: 1.000.000, 0.01% -> 10.000.000.000 -> 10
	 */
	private static final java.util.HashMap<Integer,GammaParams> gammaParams = new java.util.HashMap<Integer, GammaParams>();
	static {	
		gammaParams.put(-2, new GammaParams(71.3, 73.3));
		gammaParams.put(-1, new GammaParams(24.3, 24.3));
		gammaParams.put(0, new GammaParams(7.3, 7.4));
		gammaParams.put(1, new GammaParams(2.5, 2.5));

		gammaParams.put(2, new GammaParams(0.8, 0.75));
		gammaParams.put(3, new GammaParams(0.19, 0.32));
		gammaParams.put(4, new GammaParams(0.07, 0.09));
		gammaParams.put(5, new GammaParams(0.03, 0.02));
	}

	public static Double getGammaUpperIntegral(Integer lookupParam){
//		return gammaParams.get(lookupParam).failureRandomA;
		return 71.3;
	}
	public static Double getGammaShapeParam(Integer lookupParam){
//		return gammaParams.get(lookupParam).failureRandomB;
		return 73.3;
	}	

}

final class GammaParams{
	Double failureRandomA;
	Double failureRandomB;

	public GammaParams(Double failureRandomA, Double failureRandomB) {
		this.failureRandomA = failureRandomA;
		this.failureRandomB = failureRandomB;
	}
}

