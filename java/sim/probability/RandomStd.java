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

/*************************************************************************
 *  Compilation:  javac StdRandom.java
 *  Execution:    java StdRandom
 *
 *  A library of static methods to generate random numbers from
 *  different distributions (bernoulli, uniform, gaussian,
 *  discrete, and exponential). Also includes a method for
 *  shuffling an array.
 *
 *  % java StdRandom 5
 *  90 26.36076 false 8.79269 0
 *  13 18.02210 false 9.03992 1
 *  58 56.41176 true  8.80501 0
 *  29 16.68454 false 8.90827 0
 *  85 86.24712 true  8.95228 0
 *
 *
 *  Remark
 *  ------
 *    - Relies on randomness of nextDouble() method in java.util.Random
 *      to generate pseudorandom numbers in [0, 1).
 *
 *    - This library allows you to set the pseudorandom number seed.
 *
 *    - See http://www.honeylocust.com/RngPack/ for an industrial
 *      strength random number generator in Java.
 *
 *************************************************************************/

import java.util.Random;

/**
 *  <i>Standard random</i>. This class provides methods for generating
 *  random number from various distributions.
 *  <p>
 *  For additional documentation, see <a href="http://www.cs.princeton.edu/introcs/22library">Section 2.2</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public class RandomStd {

    private static Random random = new Random();

    /**
     * Set the seed of the psedurandom number generator.
     * Creates new prng if none currently exists
     */
    public static void setSeed(long seed) {
        random = new Random(seed);
    }

    /**
     * Return real number uniformly in [0, 1).
     */
    public static double uniform() {
        return random.nextDouble();
    }

    /**
     * Return real number uniformly in [0, 1).
     */
    public static double random() {
        return random.nextDouble();
    }

    /**
     * Return an integer uniformly between 0 and N-1.
     */
    public static int uniform(int N) {
        return random.nextInt(N);
    }

    ///////////////////////////////////////////////////////////////////////////
    //  STATIC METHODS BELOW RELY ON JAVA.UTIL.RANDOM ONLY INDIRECTLY VIA
    //  THE STATIC METHODS ABOVE.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Return int uniformly in [a, b).   ( a <= x < b )
     */
    public static int uniform(int a, int b) {
        return a + uniform(b - a);
    }

    /**
     * Return real number uniformly in [a, b).
     */
    public static double uniform(double a, double b) {
        return a + uniform() * (b-a);
    }

    /**
     * Return a boolean, which is true with probability p, and false otherwise.
     */
    public static boolean bernoulli(double p) {
        return uniform() < p;
    }

    /**
     * Return a boolean, which is true with probability .5, and false otherwise.
     */
    public static boolean bernoulli() {
        return bernoulli(0.5);
    }

    /**
     * Return a real number with a standard Gaussian distribution.
     */
    public static double gaussian() {
        // use the polar form of the Box-Muller transform
        double r, x, y;
        do {
            x = uniform(-1.0, 1.0);
            y = uniform(-1.0, 1.0);
            r = x*x + y*y;
        } while (r >= 1 || r == 0);
        return x * Math.sqrt(-2 * Math.log(r) / r);

        // Remark:  y * Math.sqrt(-2 * Math.log(r) / r)
        // is an independent random gaussian
    }

    /**
     * Return a real number from a gaussian distribution with given mean and stddev
     */
    public static double gaussian(double mean, double stddev) {
        return mean + stddev * gaussian();
    }

    /**
     * Return an integer with a geometric distribution with mean 1/p.
     */
    public static int geometric(double p) {
        // using algorithm given by Knuth
        return (int) Math.ceil(Math.log(uniform()) / Math.log(1.0 - p));
    }

    /**
     * Return an integer with a Poisson distribution with mean lambda.
     */
    public static int poisson(double lambda) {
        // using algorithm given by Knuth
        // see http://en.wikipedia.org/wiki/Poisson_distribution
        int k = 0;
        double p = 1.0;
        double L = Math.exp(-lambda);
        do {
            k++;
            p *= uniform();
        } while (p >= L);
        return k-1;
    }

    /**
     * Return a real number with a Pareto distribution with parameter alpha.
     */
    public static double pareto(double alpha) {
        return Math.pow(1 - uniform(), -1.0/alpha) - 1.0;
    }

    /**
     * Return a real number with a Cauchy distribution.
     */
    public static double cauchy() {
        return Math.tan(Math.PI * (uniform() - 0.5));
    }

    /**
     * Return a number from a discrete distribution: i with probability a[i].
     */
    public static int discrete(double[] a) {
        // precondition: sum of array entries equals 1
        double r = uniform();
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
            if (sum >= r) return i;
        }
        assert (false);
        return -1;
    }

    /**
     * Return a real number from an exponential distribution with rate lambda.
     * BugFix (JPC-03-02-2011): Method now repeatable  
     */
    public static double exp(double lambda) {
        return -Math.log(1 - uniform()) / lambda;
    }


    // swaps array elements i and j
    protected static void exch(String[] a, int i, int j) {
        String swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    /**
     * Rearrange the elements of an array in random order.
     */
    public static void shuffle(Object[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of a double array in random order.
     */
    public static void shuffle(double[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of an int array in random order.
     */
    public static void shuffle(int[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }


    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public static void shuffle(Object[] a, int lo, int hi) {
        if (lo < 0 || lo > hi || hi >= a.length)
            throw new RuntimeException("Illegal subarray range");
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public static void shuffle(double[] a, int lo, int hi) {
        if (lo < 0 || lo > hi || hi >= a.length)
            throw new RuntimeException("Illegal subarray range");
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public static void shuffle(int[] a, int lo, int hi) {
        if (lo < 0 || lo > hi || hi >= a.length)
            throw new RuntimeException("Illegal subarray range");
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Return double array as a string
     * @param array 
     * @return string representation of double array
     */
    public static String arrayToString(double[] array) {
    	 String s = "{";
         for(int i=0; i<array.length;i++) {
         	s+=array[i];
         	if(i<array.length-1) s+=", ";
         	else s+="}";
         }
         return s;
    }
    
    /**
     * Return int array as a string
     * @param array 
     * @return string representation of int array
     */
    public static String arrayToString(int[] array) {
   	 String s = "{";
        for(int i=0; i<array.length;i++) {
        	s+=array[i];
        	if(i<array.length-1) s+=", ";
        	else s+="}";
        }
        return s;
   }

	public static double drawGamma(double k, double theta) {
		//from vyshemirsky.blogspot.com/2007/11/sample-from-gamma-distribution-in-java.html
		boolean accept = false;
		if (k < 1) {
			// Weibull algorithm
			double c = (1 / k);
			double d = ((1 - k) * Math.pow(k, (k / (1 - k))));
			double u, v, z, e, x;
			do {
				u = RandomStd.uniform();
				v = RandomStd.uniform();
				z = -Math.log(u);
				e = -Math.log(v);
				x = Math.pow(z, c);
				if ((z + e) >= (d + x)) {
					accept = true;
				}
			} while (!accept);
			return (x * theta);
		} else {
			// Cheng's algorithm
			double b = (k - Math.log(4));
			double c = (k + Math.sqrt(2 * k - 1));
			double lam = Math.sqrt(2 * k - 1);
			double cheng = (1 + Math.log(4.5));
			double u, v, x, y, z, r;
			do {
				u = RandomStd.uniform();
				v = RandomStd.uniform();
				y = ((1 / lam) * Math.log(v / (1 - v)));
				x = (k * Math.exp(y));
				z = (u * v * v);
				r = (b + (c * y) - x);
				if ((r >= ((4.5 * z) - cheng)) ||
						(r >= Math.log(z))) {
					accept = true;
				}
			} while (!accept);
			return (x * theta);
		}
	}
	   
    /**
     * Unit test.
     */
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        if (args.length == 2) RandomStd.setSeed(Long.parseLong(args[1]));

        double[] t = { .5, .3, .1, .1 };
        
        for (int i = 0; i < N; i++) {
            System.out.println("Uniform(100): "+ uniform(100));
            System.out.println("Uniform(10,99): "+ uniform(10.0, 99.0));
            System.out.println("Bernoulli(0.5): "+ bernoulli(.5));
            System.out.println("Gaussian(9.0,0.2): "+ gaussian(9.0, .2));
            System.out.println("Discrete("+arrayToString(t)+"): "+ discrete(t));
        }
    }

}
