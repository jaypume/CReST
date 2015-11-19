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
package config;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import sim.module.Module;
import sim.module.replacements.ReplacementsModuleRunner;
import sim.physical.AirConditioner;
import sim.physical.Aisle;
import sim.physical.CPU;
import sim.physical.Datacentre;
import sim.physical.Harddisk;
import sim.physical.RAM;
import sim.physical.Rack;
import sim.physical.Server;
import sim.physical.World;
import sim.physical.bo.ServerWontFitException;
import utility.direction.CompassDirection;
import utility.time.TimeManager;
import builder.datacentre.LayoutMethod;
import builder.datacentre.layout.LayoutPatternFactory;
import builder.datacentre.layout.LayoutPatternFactory.LayoutPatternType;
import config.physical.ConfigAirCon;
import config.physical.ConfigAisle;
import config.physical.ConfigCPU;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRAM;
import config.physical.ConfigRack;
import config.physical.ConfigServer;
import config.physical.ConfigServerType;
import config.physical.ConfigWorld;
import config.physical.ServerType;

/**
 * 
 * This file describes and object that can load a world configuration from an
 * XML file. The important thing to note when looking though this file is that
 * loading occurs in stages.
 * 
 * Firstly, in "Stage 1", a JDOM document is created containing objects that
 * mirror the XML structure of the orginal file. Then in "Stage 2" this is
 * converted to a tree of Config* objects. The reason for this is that these
 * Config objects are 'pure' in the sense that they have internal defaults,
 * little to no internal logic and are made of getters and setters. This means
 * that the data can be read in any order.
 * 
 * Finally, in "Stage 3" the Config* objects are converted into sets of objects
 * from sim.physical to be passed to the Simulator.
 * 
 * When loading the Builder, only Stages 1 and 2 should occur. When loading the
 * Simulator all three stages should happen.
 * 
 * @author James Laverack
 * 
 */

public class XMLLoader
{

	private static Logger logger = Logger.getLogger(XMLLoader.class);
	
	// //////////////////////////////////// //
	// CONFIG LOADERS (stage 1 and stage 2) //
	// //////////////////////////////////// //

	public static EditorConfiguration loadConfig(File pFile)
			throws IOException, FileNotFoundException, java.lang.OutOfMemoryError
	{
		logger.debug("Loading configuration...");
		
		// Local Varables
		SettingsManager workingConfig;
		String name;
		ConfigWorld confWorld;
		List<ConfigServerType> replacements = new ArrayList<ConfigServerType>();
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try
		{
			// / Loading Stage 1
			InputStream fileStream = new GZIPInputStream( new FileInputStream(pFile));
			doc = builder.build(fileStream);
			Element root = doc.getRootElement();
			// / Loading Stage 2
			// Load name
			name = root.getAttributeValue("name"); //set the name from the file

			// Config manager loading
			workingConfig = createConfigManager(root.getChild("settings"));
			// Do world Loading
			confWorld = loadConfigWorld(root.getChild("world"));
			// Load replacement servers
			@SuppressWarnings("unchecked")
			List<Element> rep = root.getChild("replacementServers").getChildren("server");
			for( Element e : rep)
			{
				replacements.add(loadConfigServerType(e));
			}
			
			// create and return EditorConfiguration object
			return new EditorConfiguration(name, confWorld, workingConfig, replacements);
		} 
		catch (JDOMException e)
		{
			String errorMessage = "XML error in loading file " + pFile + " :"
			+ e.getMessage();
			logger.error(errorMessage);
			System.err.println(errorMessage);
			throw new IOException();
		}
	}

	@SuppressWarnings("unchecked")
	private static ConfigWorld loadConfigWorld(Element world)
	{
		// Create Config World Object to use
		ConfigWorld conf = new ConfigWorld();
		// netWorkDistance
		try
		{
			conf.setNetworkDistance(Integer.valueOf(world
					.getAttributeValue("networkDistance")));
		} catch (NumberFormatException e)
		{
			logger.warn("World networkDistance attribute missing or invalid");
		}
		// Build Datacentres
		List<Element> datacentreElements = world.getChildren("datacentre");
		for (Element e : datacentreElements)
		{
			conf.addDatacentre(loadConfigDatacentre(e));
		}

		// Finished
		return conf;
	}

	@SuppressWarnings("unchecked")
	private static ConfigDatacentre loadConfigDatacentre(Element dc)
	{
		ConfigDatacentre data = new ConfigDatacentre();
		// name
		data.setDatacentreName(dc.getAttributeValue("name"));
		// count
		try
		{
			data.setCount(Integer.parseInt(dc.getAttributeValue("count")));
		} catch (NumberFormatException e)
		{
			logger.warn("Datacentre count attribute missing or invalid");
		}
		// networkDistance
		try
		{
			data.setNetworkDistance(Integer.parseInt(dc
					.getAttributeValue("networkDistance")));
		} catch (NumberFormatException e)
		{
			logger.warn("Datacentre networkDistance attribute missing or invalid");
		}
		// dimensions
		try
		{
			String[] dim = dc.getAttributeValue("dimensions").split(" ");
			data.setDimX(Integer.parseInt(dim[0]));
			data.setDimY(Integer.parseInt(dim[1]));
		} catch (Exception e)
		{
			logger.warn("Datacentre dimensions attribute missing or invalid");
		}
		//numservers/ratio
		try
		{
			String[] dim = dc.getAttributeValue("numservers").split(" ");
			data.setNumServers(Integer.parseInt(dim[0]));
			data.setRatio(Double.parseDouble(dim[1]));
		} catch (Exception e)
		{
			logger.warn("Datacentre numservers & ratio attribute missing or invalid");
		}
		//layout method
		try
		{
			data.setLayoutMethod(LayoutMethod.valueOf(dc.getAttributeValue("layoutmethod")));
		} catch (Exception e)
		{
			logger.warn("Datacentre layout method attribute missing or invalid");
		}
		//aircon
		try
		{
			data.setAircon(Boolean.parseBoolean(dc.getAttributeValue("aircon")));
		} catch (Exception e)
		{
			logger.warn("Datacentre aircon attribute missing or invalid");
		}
		//layout pattern
		try
		{
			data.setLayoutPattern(LayoutPatternFactory.getLayoutPattern(LayoutPatternType.valueOf(dc.getAttributeValue("layoutpattern"))));
		} catch (Exception e)
		{
			logger.warn("Datacentre layout pattern attribute missing or invalid");
		}
		
		/**
		 * TODO expand this out into seperate try-catch blocks for each statement.
		 */
		try
		{
			data.setTypeOfBusiness(dc.getAttributeValue("pricing.typeOfBusiness"));
			data.setSuppliedPower(Double.parseDouble(dc.getAttributeValue("pricing.suppliedPower")));
			data.setGridPowerCost(Double.parseDouble(dc.getAttributeValue("pricing.gridPowerCost")));
			data.setMaintenancePowerCost(Double.parseDouble(dc.getAttributeValue("pricing.maintenancePowerCost")));
			data.setMaintenanceCoolingCost(Double.parseDouble(dc.getAttributeValue("pricing.maintenanceCoolingCost")));
			data.setEmployeesPerRack(Double.parseDouble(dc.getAttributeValue("pricing.employeesPerRack")));
			data.setCostofEmployees(Double.parseDouble(dc.getAttributeValue("pricing.costOfEmployees")));
			data.setHardwareLifetime(Integer.parseInt(dc.getAttributeValue("pricing.hardwareLifetime")));
			data.setRent(Double.parseDouble(dc.getAttributeValue("pricing.rent")));
		} catch(Exception e2)
		{
			logger.warn("Datacentre Pricing data missing or invalid");
		}

		// Aisles
		List<Element> aisleElements = dc.getChildren("aisle");
		for (Element e3 : aisleElements)
		{
			data.addAisle(loadConfigAisle(e3));
		}
		// Finished
		return data;
	}

	@SuppressWarnings("unchecked")
	private static ConfigAisle loadConfigAisle(Element ai)
	{
		ConfigAisle aisle = new ConfigAisle();
		// name
		aisle.setAisleName(ai.getAttributeValue("name"));
		// count
		try
		{
			aisle.setCount(Integer.parseInt(ai.getAttributeValue("count")));
		} catch (NumberFormatException e)
		{
			logger.warn("Aisle count attribute missing or invalid");
		}
		// networkDistance
		try
		{
			aisle.setNetworkDistance(Integer.parseInt(ai
					.getAttributeValue("networkDistance")));
		} catch (NumberFormatException e)
		{
			logger.warn("Aisle networkDistance attribute missing or invalid");
		}
		// Position
		try
		{
			String[] pos = ai.getAttributeValue("position").split(" ");
			aisle.setLocation(new Point(Integer.parseInt(pos[0]), Integer
					.parseInt(pos[1])));
		} catch (Exception e)
		{
			logger.warn("Aisle position attribute missing or invalid");
		}
		// Racks
		List<Element> rackElements = ai.getChildren("rack");
		for (Element e : rackElements)
		{
			aisle.addRack(loadConfigRack(e));
		}
		// Aircon Units
		List<Element> acElements = ai.getChildren("airConditioner");
		for (Element e : acElements)
		{
			aisle.addAirCon(loadConfigAirCon(e));
		}
		// Finished
		return aisle;
	}

	private static ConfigAirCon loadConfigAirCon(Element ac)
	{
		ConfigAirCon aircon = new ConfigAirCon();
		// Position
		try
		{
			String[] pos = ac.getAttributeValue("position").split(" ");
			aircon.setLocation(new Point(Integer.parseInt(pos[0]), Integer
					.parseInt(pos[1])));
		} catch (Exception e)
		{
			logger.warn("Air Conditioner position attribute missing or invalid");
		}
		
		return aircon;
	}
	
	@SuppressWarnings("unchecked")
	private static ConfigRack loadConfigRack(Element rk)
	{
		ConfigRack rack = new ConfigRack();
		// name
		rack.setRackName(rk.getAttributeValue("name"));
		// count
		try
		{
			rack.setCount(Integer.parseInt(rk.getAttributeValue("count")));
		} catch (NumberFormatException e)
		{
			logger.warn("Rack count attribute missing or invalid");
		}
		// networkDistance
		try
		{
			rack.setNetworkDistance(Integer.parseInt(rk
					.getAttributeValue("networkDistance")));
		} catch (NumberFormatException e)
		{
			logger.warn("Rack networkDistance attribute missing or invalid");
		}
		// Position
		try
		{
			String pos = rk.getAttributeValue("position");
			rack.setLocation((Integer.parseInt(pos)));
		} catch (Exception e)
		{
			logger.warn("Rack position attribute missing or invalid");
		}
		// Servers		
		List<Element> serverElements = rk.getChildren("server");
		for (Element e : serverElements)
		{
			try    
			{
				ConfigServer s = loadConfigServer(e);
				rack.addServer(s);
			} catch (ServerWontFitException e1)
			{
				logger.warn("too many servers for rack, ignoring extra servers");
			}
		}
		// Direction
		try
		{
			CompassDirection dir = CompassDirection.valueOf(rk.getAttributeValue("serversDirection"));
			rack.setServersDirection(dir);
			logger.debug("Setting rack direction: " + dir);
		} catch (Exception e)
		{
			logger.warn("Rack servers direction attribute missing or invalid");
		}	
		// Finished
		return rack;
	}

	@SuppressWarnings("unchecked")
	private static ConfigServer loadConfigServer(Element sv)
	{
		ConfigServer serve = new ConfigServer();
		// model
		serve.setModel(sv.getAttributeValue("model"));
		// size
		try
		{
			serve.setSize(Integer.parseInt(sv.getAttributeValue("size")));
		} catch (NumberFormatException e)
		{
			logger.warn("server size attribute missing or invalid");
		}
		// direction
		try
		{
			serve.setDirection(CompassDirection.valueOf(sv.getAttributeValue("direction")));
		} catch (Exception e)
		{
			logger.warn("server direction attribute missing or invalid");
		}
		// RAM
		List<Element> ramElements = sv.getChildren("ram");
		if (ramElements.size() == 0)
		{
			logger.warn("Server has no RAM");
		} else
		{
			if (ramElements.size() > 1)
			{
				logger.warn("Server has more than one RAM attribute, using the first one only");
			}
			serve.setRAM(loadConfigRAM(ramElements.get(0)));
		}
		// CPU's
		List<Element> cpuElements = sv.getChildren("cpu");
		for (Element e : cpuElements)
		{
			serve.addCPU(loadConfigCPU(e));
		}

        // Mean failure time for probability distribution in failure threads.
        try
        {
            serve.setMeanFailTimeInDays(Integer.parseInt(sv.getAttributeValue("probMean")));
//            logger.debug("Mean failure time for failure thread probability distribution: " + serve.getMeanFailTime() + " " + serve.getName());
        }
        catch (NumberFormatException e)
        {
            logger.warn("server mean failure for probability distribution attribute missing or invalid");
        }
		
		// Finished
		return serve;
	}

	@SuppressWarnings("unchecked")
	private static ConfigServerType loadConfigServerType(Element sv)
	{
		ConfigServerType serve = new ConfigServerType();
		// model
		serve.setModel(sv.getAttributeValue("model"));
		// size
		try
		{
			serve.setSize(Integer.parseInt(sv.getAttributeValue("size")));
		} catch (NumberFormatException e)
		{
			logger.warn("server size attribute missing or invalid");
		}
		// RAM
		List<Element> ramElements = sv.getChildren("ram");
		if (ramElements.size() == 0)
		{
			logger.warn("Server has no RAM");
		} else
		{
			if (ramElements.size() > 1)
			{
				logger.warn("Server has more than one RAM attribute, using the first one only");
			}
			serve.setRAM(loadConfigRAM(ramElements.get(0)));
		}
		// CPU's
		List<Element> cpuElements = sv.getChildren("cpu");
		for (Element e : cpuElements)
		{
			serve.addCPU(loadConfigCPU(e));
		}

        // Mean failure time for probability distribution in failure threads.
        try
        {
            serve.setMeanFailTime(Integer.parseInt(sv.getAttributeValue("probMean")));
        }
        catch (NumberFormatException e)
        {
            logger.warn("server mean failure for probability distribution attribute missing or invalid");
        }
        
        try
        {
        	serve.setTimeAvailableFrom(Integer.parseInt(sv.getAttributeValue("availableFrom")));
        }
        catch (NumberFormatException e)
        {
            logger.warn("server time avalable from attribute missing or invalid");
        }
		
		// Finished
		return serve;
	}
	
	private static ConfigCPU loadConfigCPU(Element c)
	{
		ConfigCPU cpu = new ConfigCPU();
		// model
		cpu.setModel(c.getAttributeValue("model"));
		// cores
		try
		{
			cpu.setCores(Integer.parseInt(c.getAttributeValue("cores")));
		} catch (NumberFormatException e)
		{
			logger.warn("CPU cores attribute missing or invalid");
		}
		// speed
		try
		{
			cpu.setSpeed(Integer.parseInt(c.getAttributeValue("speed")));
		} catch (NumberFormatException e)
		{
			logger.warn("CPU speed attribute missing or invalid");
		}

		// Finished
		return cpu;
	}

	private static ConfigRAM loadConfigRAM(Element r)
	{
		ConfigRAM ram = new ConfigRAM();
		// Model
		ram.setModel(r.getAttributeValue("model"));
		// Speed
		try
		{
			ram.setSpeed(Double.parseDouble(r.getAttributeValue("speed")));
		} catch (Exception e)
		{
			logger.warn("RAM speed attribute missing or invalid");
		}
		// Size
		try
		{
			ram.setSize(Integer.parseInt(r.getAttributeValue("size")));
		} catch (Exception e)
		{
			logger.warn("RAM size attribute missing or invalid");
		}
		return ram;
	}

	// //////////////////////// //
	// OBJECT LOADERS (Stage 3) //
	// //////////////////////// //

	public static SimulatorConfiguration load(File pFile) throws IOException,
			FileNotFoundException
	{
		// Perform loading Stages 1 & 2
		EditorConfiguration stage2 = loadConfig(pFile);
		// Perform stage 3
		World stage3 = loadWorld(stage2.getConfWorld(), stage2.getConfig(), stage2.getReplacements());
		// Create SimulatorConfiguration
		return new SimulatorConfiguration(stage2.getName(), stage2.getConfig(),
				stage3);

	}

	public static World loadWorld(ConfigWorld confWorld, SettingsManager confMang, List<ConfigServerType> confReplacementServers)
	{
		World w = World.getInstance(confWorld.getNetworkDistance());
		w.clear();
		logger.debug("Loading world instance = " + w);		
		
		//Create replacement servers
		for (ConfigServerType replacementServer : confReplacementServers)
		{
			logger.debug("Adding next replacement server model: " + replacementServer.getModel());
			buildReplacementServer(replacementServer);
		}		

		int counter = 0;
		
		// Create Datacentres
		for (ConfigDatacentre c : confWorld.getDCs())
		{
			//JC: Dec 2011 - this is the *new* way
			logger.debug("Next DC... Calling buildDatacentre with id=" + counter);
			w.addDatacentre(buildDatacentre(c, counter, confMang));
			counter++;
		}

		logger.info("World created with " + w.getNumberOfDatacentres() + " datacentres");
		logger.debug("Creating new log manager...");
		w.createLogManager();
		return w;
	}

	/**
	 * Build Datacentre
	 * @param c - configuration of datacentre
	 * @param dcID - id of datacentre
	 * @param confMang - settings manager
	 * @return the datacentre
	 */
	public static Datacentre buildDatacentre(ConfigDatacentre c, int dcID, SettingsManager confMang)
	{
		
		logger.debug("Building datacentre, ID#=" + dcID);
		
		Datacentre data = new Datacentre(c.getNetworkDistance(),
				c.getDatacentreName(), dcID,
				c.getDimX(), c.getDimY());
		
		//Set NonPhysicalDatacentreRelatedConfigurationStuffs
		//data.p.setTypeOfBusiness(c.getTypeOfBusiness());
		
		//TDOO - WARNING! costs will not have been instantiated yet!!!!!! ARRRRGH!
		//TODO - JC Jun 2012: If there is a problem with this, just create a default (Null) costs class that can be used as a placeholder
		
		data.getCosts().setmSuppliedPower(c.getSuppliedPower());
		data.getCosts().setmGridPowerCost(c.getGridPowerCost());
		data.getCosts().setmMaintenanceCostPowerSystem(c.getMaintenancePowerCost());
		data.getCosts().setmMaintenanceCostCoolingSystem(c.getMaintenanceCoolingCost());
		data.getCosts().setmEmployeesPerRack(c.getEmployeesPerRack());
		data.getCosts().setmCostOfFullyLoadedEmployees(c.getCostofEmployees());
		data.getCosts().setmHardwareLifetime(c.getHardwareLifetime());
		data.getCosts().setmRent(c.getRent());
		
		// Create Aisles
		for (ConfigAisle a : c.getAisles())
		{
			for (int j = 0; j < a.getCount(); j++)
			{                
                Aisle ais = new Aisle(a.getNetworkDistance(),
                        a.getAisleName(), a.getLocation(), a.getDimensions());
                data.addAisle(ais);
				// Create Racks
				for (ConfigRack r : a.getRacks())
				{
					for (int k = 0; k < r.getCount(); k++)
					{
						Point rackLoc = new Point( r.getLocation(), 0);
						//System.out.println("Create Rack at: " + rackLoc.x + ", " + rackLoc.y);
						Rack rks = new Rack(r.getNetworkDistance(),
								r.getRackName(),rackLoc);
						rks.setDirection(r.getServersDirection());
						ais.addRack(rks);
						// Create servers
						for (ConfigServer s : r.getServers())
						{
							try
							{
								rks.addServer(buildServer(s,
								        rackLoc));
							} catch (Exception e)
							{
								// must just be too full
								logger.warn("Rack too full, ignoring extra server");
							}
						}
					}
				}
				// Create Air Con
				for (ConfigAirCon ac : a.getAirCons())
				{
				    //TODO Read in from config.
				    final long meanFailureTime = TimeManager.hoursToSimulationTime(6);
				    
					AirConditioner aircon = new AirConditioner(ac.getLocation(), meanFailureTime);
					ais.addAirCon(aircon);
				}
			}
		}
		return data;
	}
	
	private static Server buildServer(ConfigServer s, Point loc)
	{
		// Get CPU information
		ConfigCPU[] confCpus = s.getCPUs();
		CPU[] cpus = new CPU[confCpus.length];
		for (int i=0;i<confCpus.length;i++)
		{
			cpus[i] = new CPU(confCpus[i].getModel(), confCpus[i].getSpeed(), confCpus[i].getCores());
		}
		// get Memory information
		RAM ram = new RAM(s.getRAM().getModel(), s.getRAM().getSpeed(), s.getRAM().getSize());
		//System.out.println("Create server at: " + loc.x + ", " + loc.y);
		// Create our server object
		Server server = new Server(loc, cpus, ram, s.getSize(), s.getDirection(), s.getModel(), TimeManager.daysToSimulationTime(s.getMeanFailTime()));

		// server.setHeight(Integer.parseInt(e.getAttribute("size").getValue()));
		// return finished server object
		return server;
	}
	
	private static void buildReplacementServer(ConfigServerType s)
	{		
		ConfigCPU[] confCpus = s.getCPUs();
		CPU[] cpus = new CPU[confCpus.length];
		for (int i = 0; i < confCpus.length; i++)
		{
			cpus[i] = new CPU(confCpus[i].getModel(), confCpus[i].getSpeed(), confCpus[i].getCores());
		}
		
		ServerType newType = new ServerType(
				cpus, 
				new RAM(s.getRAM().getModel(), s.getRAM().getSpeed(), s.getRAM().getSize()), 
				new Harddisk(), 
				s.getSize(),
				TimeManager.daysToSimulationTime(s.getMeanFailTime()),
				s.getModel(), 
				Server.DEFAULT_DEFAULT_POWER_CONSUMPTION, 
				Server.DEFAULT_MAX_POWER_CONSUMED, 
				Server.DEFAULT_PURCHASE_COST, 
				TimeManager.daysToSimulationTime(s.getTimeAvailableFrom())
				);
		
		ReplacementsModuleRunner.addServerType(newType);
	}

	// //////////////////// //
	// CONFIG ACCESS LOADER //
	// //////////////////// //

	/**
	 * Creates a new ConfigManager object based on values read in from the XML
	 * file. This is performed in early Stage 2 loading.
	 * 
	 * @param configElement
	 * @param modulesElement
	 * @return
	 */
	private static SettingsManager createConfigManager(Element configElement)
	{		
		
		// modules
		Module.configureUsingXML(configElement.getChild(Module.XML_ELEMENT_LABEL));
		
		SettingsManager config = new SettingsManager();
		
		return config;
	}

	// /////////////// //
	// UTILITY METHODS //
	// /////////////// //

//	private static void addServerTypes()
//	{
//	    CPU[] CPUs = {new CPU("Awesome", 4000, 6), new CPU("Awesome", 4000, 6), new CPU("Awesome", 4000, 6), new CPU("Awesome", 4000, 6), new CPU("Awesome", 4000, 6), new CPU("Awesome", 4000, 6)};
//	    RAM ram = new RAM();
//	    Harddisk harddisk = new Harddisk();
//	    long baseMeanFailTime = TimeManager.daysToSimulationTime(1); //TODO: JC Dec 2011, MEAN FAIL TIME should not be hardcoded 
////	    long baseMeanFailTime = TimeManager.daysToSimulationTime(50);
//	    
//	    double defaultPowerConsumption = 200;
//	    double maxPowerConsumption = 1000;
//	    double purchaseCost = 1000;
//	    long baseTimeAvailableFrom = TimeManager.daysToSimulationTime(30);
//	    
//	    for (int i = 0; i < 3; i++)
//	    {
//	        final String model = "ServerType_" + i;
//	        final long timeAvailableFrom = baseTimeAvailableFrom * (i+1);
//	        final long meanFailTime = baseMeanFailTime * (i+1);
//	        
//	        logger.info(model + " mean fail time = " + meanFailTime);
//	        
//	        ServerType newType = new ServerType(CPUs, ram, harddisk, meanFailTime, model, defaultPowerConsumption, maxPowerConsumption, purchaseCost, timeAvailableFrom);
//	        ReplacementsModuleRunner.addServerType(newType);
//	    }
//	}
}
