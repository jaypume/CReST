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
package app.bo;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

/**
 * Used to parse command line arguments for CloudSim application
 * 
 * Makes use of jcommander library: 
 * 	https://github.com/cbeust/jcommander/
 *  http://jcommander.org/
 *  
 * Enables arguments to be passed in any order as long as correct flags are used.
 * 
 * Options:
 * 	-config		followed by name of configuration file *REQUIRED
 *  -params	    followed by the name of parameters file
 *  -events     followed by the name of the events file
 * 	-nogui		do not use graphical interface
 * 	-gui		use graphical interface
 *  -version	return version information
 *  -help		return help	
 * 
 * @author cszjpc
 *
 */
public class CReSTCommandlineParser {
	  @Parameter
	  public List<String> parameters = Lists.newArrayList();
	 
	  @Parameter(names = {"--config", "-config", "-c"}, description = "Configuration filename: xml.gz file containing full datacentre specification", required = true)
	  public String configFileName = "default";
	
	  @Parameter(names = {"--params", "-params", "-p"}, description = "Parameters filename: Text file detailing simulation parameters", required = false)
	  public String paramsFileName = "none";
	  
	  @Parameter(names = {"--events", "-events", "-e"}, description = "User Events filename: Text file detailing simulation events defined by user", required = false)
	  public String eventsFileName = "none";
	  
	  @Parameter(names = { "--nogui", "-nogui", "-ng" }, description = "No graphical interface")
	  public boolean nogui = false;
	  
	  @Parameter(names = { "--help", "-help", "-h" }, description = "Help")
	  public boolean help = false;
	  
	  @Parameter(names = { "--version", "-version", "-v" }, description = "Software version")
	  public boolean version = false;

//	  @Parameter(names = { "--warranty", "-warranty", "-w"}, description = "Software warranty")
//	  public boolean warranty = false;
//	  
//	  @Parameter(names = { "--copyright", "-copyright", "-copy"}, description = "Copyright statement")
//	  public boolean copyright = false;
//	  
	  @Parameter(names = { "--information", "--info", "-information", "-info", "-i"}, description = "Information")
	  public boolean information = false;
}
