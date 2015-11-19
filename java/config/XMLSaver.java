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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import sim.module.Module;
import config.physical.ConfigServerType;

public class XMLSaver
{

	public static void save(File pFile, EditorConfiguration toSave)
			throws IOException
	{
		// output
		System.out.println("Starting save to " + pFile.getAbsolutePath());
		// Build root
		Element root = new Element("configuration");
		root.setAttribute("name", toSave.getName());
		// config
		Element config = new Element("settings");
		root.addContent(config);
		
		//modules
		config.addContent(Module.getModulesXMLElement());	
		
		// Replacements
		Element replacements = new Element("replacementServers");
		root.addContent(replacements);
		for(ConfigServerType c : toSave.getReplacements())
		{
			replacements.addContent(c);
		}
		
		// World
		root.addContent(toSave.getConfWorld());

		System.out.println("XML tree constructed, saving...");
		// Output to file
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		Document doc = new Document(root);
		GZIPOutputStream stream = new GZIPOutputStream( new FileOutputStream(pFile));
		xout.output(doc,stream);
		stream.finish();
		stream.close();
		// Deconstruct XML TRee
		System.out.println("Deconstructing XML Tree");
		for(ConfigServerType c : toSave.getReplacements())
		{
			replacements.removeContent(c);
		}
		root.removeContent(toSave.getConfWorld());
		System.out.println("Save Complete");
		
		
	}

}
