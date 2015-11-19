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
package utility;

public class Copyright {

	public static String versionString = "CReST alpha release, version 0.3.0, compiled 03/07/2013";
	
	public static String license = "CReST: The Cloud Research Simulation Toolkit Copyright (C) " +
			"2011, 2012, 2013 John Cartlidge\n" +
			"CReST is free software: you can redistribute it and/or modify " +
			"it under the terms of the GNU General Public License as published by " +
			"the Free Software Foundation, either version 3 of the License, or " +
			"(at your option) any later version.\n\n" +

	    	"This program is distributed in the hope that it will be useful, " +
	    	"but WITHOUT ANY WARRANTY; without even the implied warranty of " +
	    	"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
	    	"GNU General Public License for more details. \n\n" +
	    	
	    	"You should have received a copy of the GNU General Public License " +
	    	"along with this program.  If not, see <http://www.gnu.org/licenses/gpl.txt>\n";
	
	public static String warranty = "THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY \n" +
	    		"APPLICABLE LAW.  EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT \n" +
	    		"HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY \n" +
	    		"OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, \n" +
	    		"THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR \n" +
	    		"PURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM \n" +
	    		"IS WITH YOU.  SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF \n" +
	    		"ALL NECESSARY SERVICING, REPAIR OR CORRECTION.\n";
	
	public static String contributors = "Contributors: Philip Clamp, Dave Cliff, Luke Drury, Tomas Grazys, Sarah Haswell, \n" +
			"James Laverack, Callum Muir, Owen Rogers, Alex Sheppard and Ilango Sriram.\n";
	
	public static String info = "CReST: The Cloud Research Simulation Toolkit, " +
			"Copyright (C) 2011, 2012, 2013 John Cartlidge \n\n" +
			
			Copyright.contributors +
			
			"\nCReST was developed at the University of Bristol, UK, using \n" +
			"financial support from the UK's Engineering and Physical \n" +
			"Sciences Research Council (EPSRC) grant EP/H042644/1 entitled \n" +
			"\"Cloud Computing for Large-Scale Complex IT Systems\". Refer to \n" +
			"<http://gow.epsrc.ac.uk/NGBOViewGrant.aspx?GrantRef=EP/H042644/1> \n" +
			"" +
			"\nCReST is free software: you can redistribute it and/or modify \n" +
			"it under the terms of the GNU General Public License as published by \n" +
			"the Free Software Foundation, either version 3 of the License, or \n" +
			"(at your option) any later version. \n\n" +

			Copyright.warranty + 

			"\nFor further information, contact:\n" +
			"Dr. John Cartlidge: john@john-cartlidge.co.uk\n" +
			"Department of Computer Science,\n" +
			"University of Bristol, The Merchant Venturers Building,\n" +
			"Woodland Road, Bristol, BS8-1UB, United Kingdom.\n" +
			
			"\n" + Copyright.versionString;
	
	public static String header = "CReST: The Cloud Research Simulation Toolkit, \n" +
			"Copyright (C) 2011, 2012, 2013 John Cartlidge\n\n" +
			"CReST comes with ABSOLUTELY NO WARRANTY; for details type `--warranty'. \n" +
			"CReST is free software, and you are welcome to redistribute it under \n" +
			"certain conditions; type '--copyright' for details\n" +
			"For further information, type '--info'\n";
}
