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
 * Class to get the currency required for the pricings (default GBP)
 * 
 * @author Alex Sheppard
 */

package sim.module.costs.bo;

import java.text.NumberFormat;
import java.util.Locale;

public class Currency
{	
	private static Locale getCurrency()
	{
		String currency = "GBP"; //TODO: get from config
		Locale locale = null;
		
		if (currency.equals("GBP"))
		{
			locale = Locale.UK;
		}
		else if (currency.equals("USD"))
		{
			locale = Locale.US;
		}
		else if (currency.equals("YEN"))
		{
			locale = Locale.CHINA;
		}
		else if (currency.equals("EUR"))
		{
			locale = Locale.FRANCE;
		}
		else
		{
			locale = Locale.UK;
		}
		
		return locale;
	}
	
	/**
	 * Returns the currency required for the pricings (default GBP)
	 * 
	 * @return The required currency
	 */
	public static NumberFormat getNumberFormat()
	{
		return NumberFormat.getCurrencyInstance(getCurrency());
	}
}
