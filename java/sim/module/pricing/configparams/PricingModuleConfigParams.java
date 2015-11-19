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
package sim.module.pricing.configparams;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;

public class PricingModuleConfigParams implements ModuleParamsInterface {

	private static final String XML_ELEMENT_NAME = "pricing";
	
	public static String ON_DEMAND_PRICE_UPFRONT_XML_TAG = "onDemandUpFront";
	public static String ON_DEMAND_PRICE_MONTHLY_XML_TAG = "onDemandMonthly";
	public static String RESERVED_PRICE_UPFRONT_XML_TAG = "reservedUpfront";
	public static String RESERVED_PRICE_MONTHLY_XML_TAG = "reservedMonthly";
	public static String RESERVATION_LENGTH_XML_TAG = "reservationLength";
	
	private static double DEFAULT_ON_DEMAND_UPFRONT_PRICE = 0.0;
	private static double DEFAULT_ON_DEMAND_MONTHLY_PRICE = 62.0;	
	private static double DEFAULT_RESERVE_UPFRONT_PRICE = 350.0; 
	private static double DEFAULT_RESERVE_MONTHLY_PRICE = 21.88;
	private static int DEFAULT_RESERVATION_LENGTH = 36;
	
	protected double onDemandUpfrontPrice = DEFAULT_ON_DEMAND_UPFRONT_PRICE;
	protected double onDemandMonthlyPrice = DEFAULT_ON_DEMAND_MONTHLY_PRICE;
	protected double reservedUpfrontPrice = DEFAULT_RESERVE_UPFRONT_PRICE;
	protected double reservedMonthlyPrice = DEFAULT_RESERVE_MONTHLY_PRICE;
	protected int reservationLength = DEFAULT_RESERVATION_LENGTH;
	
	public PricingModuleConfigParams(double onDemandUpfrontPrice, double onDemandMonthlyPrice, double reservedUpfrontPrice, double reservedHourlyPrice, int reservationLength) {
		this.onDemandUpfrontPrice = onDemandUpfrontPrice;
		this.onDemandMonthlyPrice = onDemandMonthlyPrice;
		this.reservedUpfrontPrice = reservedUpfrontPrice;
		this.reservedMonthlyPrice = reservedHourlyPrice;
		this.reservationLength = reservationLength;
	}
	
	public static PricingModuleConfigParams getDefault() {
		return new PricingModuleConfigParams(
				DEFAULT_ON_DEMAND_UPFRONT_PRICE,
				DEFAULT_ON_DEMAND_MONTHLY_PRICE,
				DEFAULT_RESERVE_UPFRONT_PRICE,
				DEFAULT_RESERVE_MONTHLY_PRICE,
				DEFAULT_RESERVATION_LENGTH);		
	}
	
	@Override
	public Element getXML() {
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);
		e.setAttribute(ON_DEMAND_PRICE_UPFRONT_XML_TAG, String.valueOf(onDemandUpfrontPrice));
		e.setAttribute(ON_DEMAND_PRICE_MONTHLY_XML_TAG, String.valueOf(onDemandMonthlyPrice));
		e.setAttribute(RESERVED_PRICE_UPFRONT_XML_TAG, String.valueOf(reservedUpfrontPrice));
		e.setAttribute(RESERVED_PRICE_MONTHLY_XML_TAG, String.valueOf(reservedMonthlyPrice));
		e.setAttribute(RESERVATION_LENGTH_XML_TAG, String.valueOf(reservationLength));
		return e;
	}

	@Override
	public void updateUsingXML(Element e) {
		try {
			onDemandUpfrontPrice = Double.parseDouble(e.getAttributeValue(ON_DEMAND_PRICE_UPFRONT_XML_TAG));
			onDemandMonthlyPrice = Double.parseDouble(e.getAttributeValue(ON_DEMAND_PRICE_MONTHLY_XML_TAG));
			reservedUpfrontPrice = Double.parseDouble(e.getAttributeValue(RESERVED_PRICE_UPFRONT_XML_TAG));
			reservedMonthlyPrice = Double.parseDouble(e.getAttributeValue(RESERVED_PRICE_MONTHLY_XML_TAG));
			reservationLength = Integer.parseInt(e.getAttributeValue(RESERVATION_LENGTH_XML_TAG));
		}
		catch (NumberFormatException ex) {
			logger.warn("Pricing Module ConfigParams attribute missing or invalid");
		}
	}

	@Override
	public void clone(ModuleParamsInterface params) {
		if (params.getClass().equals(this.getClass())) {
			this.onDemandUpfrontPrice = ((PricingModuleConfigParams) params).onDemandUpfrontPrice;
			this.onDemandMonthlyPrice = ((PricingModuleConfigParams) params).onDemandMonthlyPrice;
			this.reservedUpfrontPrice = ((PricingModuleConfigParams) params).reservedUpfrontPrice;
			this.reservedMonthlyPrice = ((PricingModuleConfigParams) params).reservedMonthlyPrice;
			this.reservationLength = ((PricingModuleConfigParams) params).reservationLength;
		} else {
			logger.warn("Ignoring changes: Attempting to clone parameters of incorrect class: " + params.getClass());
		}
	}
	
	@Override
	public String toString() {
		String s = "BrokerModuleConfigParams [";
		s+= ON_DEMAND_PRICE_UPFRONT_XML_TAG+"='" + onDemandUpfrontPrice + 
				"', ";
		s+= ON_DEMAND_PRICE_MONTHLY_XML_TAG+"='" + onDemandMonthlyPrice + 
				"', ";
		s+= RESERVED_PRICE_UPFRONT_XML_TAG+"='" + reservedUpfrontPrice + 
				"', ";
		s+= RESERVED_PRICE_MONTHLY_XML_TAG+"='" + reservedMonthlyPrice + 
				"', ";
		s+= RESERVATION_LENGTH_XML_TAG+"='" + reservationLength + 
				"']";
		return s;
	}

	@Override
	public String getXMLElementNameString() {
		return XML_ELEMENT_NAME;
	}
	
	public double getOnDemandUpfrontPrice() {
		return onDemandUpfrontPrice;
	}
	
	public double getOnDemandMonthlyPrice() {
		return onDemandMonthlyPrice;
	}
	
	public double getReservedUpfrontPrice() {
		return reservedUpfrontPrice;
	}
	
	public double getReservedHourlyPrice() {
		return reservedMonthlyPrice;
	}
	
	public int getReservationLength() {
		return reservationLength;
	}
	
	public void setOnDemandUpfrontPrice(double onDemandUpfrontPrice) {
		this.onDemandUpfrontPrice = onDemandUpfrontPrice;
	}
	
	public void setOnDemandMonthlyPrice(double onDemandPrice) {
		this.onDemandMonthlyPrice = onDemandPrice;
	}
	
	public void setReservedUpfrontPrice(double reservedUpfrontPrice) {
		this.reservedUpfrontPrice = reservedUpfrontPrice;
	}
	
	public void setReservedHourlyPrice(double reservedHourlyPrice) {
		this.reservedMonthlyPrice = reservedHourlyPrice;
	}
	
	public void setReservationLength(int reservationLength) {
		this.reservationLength = reservationLength;
	}
}