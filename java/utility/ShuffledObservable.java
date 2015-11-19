/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012, 2013 John Cartlidge 
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
package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * Subclass of Observable. Enables observers to be shuffled, so 
 * observers not notified of changes in the same order each time.
 *
 */
public class ShuffledObservable extends Observable{

	public static Logger logger = Logger.getLogger(ShuffledObservable.class);
	
	private List<Observer> obs;
	private Random prng;
	private boolean shuffleObservers = false;
	
	/**
	 * Subclass of java.util.Observable.
	 * Enables observer list to be shuffled before notify.
	 * 
	 * @param prng - Random number generator
	 * @param shuffleObservers - true if observers are to be shuffled prior to notify. 
	 * If false, then ShuffledObservable behaves the same as java.util.Observable
	 */
	public ShuffledObservable(Random prng, boolean shuffleObservers) {
		super();
		obs = new ArrayList<Observer>();
		this.prng = prng;
		this.shuffleObservers = shuffleObservers;
	}
	
	@Override
	public void addObserver(Observer o) {
		obs.add(o);
		super.addObserver(o);
	}
	
	@Override
	public void deleteObserver(Observer o) {
		super.deleteObserver(o);
		obs.remove(o);
	}
	
	/**
	 * Are observers shuffled before notify called?
	 * 
	 * @return true if observers shuffled, false otherwise (=> java.util.Observer implementation)
	 */
	public boolean isShuffleObservers() {
		return shuffleObservers;
	}
	
	/**
	 * Set shuffle observers before notify on/off
	 * @param shuffleOn true to shuffle, false otherwise
	 */
	public void setShuffle(boolean shuffleOn) {
		shuffleObservers = shuffleOn;
	}
	/**
	 * Shuffle the observers list
	 */
	protected void shuffleObservers() {
		
		logger.info("Shuffling observers list...");
		logger.debug("#Observers before shuffle = " + super.countObservers());
		logger.debug("Deleting observers...");
		super.deleteObservers();
		logger.debug("#Observers = " + super.countObservers());
		Collections.shuffle(obs,prng);
		for(Observer o: obs) {
			super.addObserver(o);
		}
		logger.debug("Observers shuffled and re-added: #Observers = " + super.countObservers());
	}
	
	@Override
	public void notifyObservers() {
		
		notifyObservers(null);
	}
	
	@Override
	public void notifyObservers(Object arg) {
		
		if(shuffleObservers) shuffleObservers();
		super.notifyObservers(arg);
	}
}
