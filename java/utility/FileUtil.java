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

import java.io.File;
import java.io.IOException;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
public class FileUtil {

	 /**
	  * Build a relative path to the given base path.
	  * @param base - the path used as the base
	  * @param path - the path to compute relative to the base path
	  * @return A relative path from base to path
	  * @throws IOException
	  */ 
	public static String findRelativePath(String base, String path)
    throws IOException
 {
    String a = new File(base).getCanonicalFile().toURI().getPath();
    System.out.println(a);
    String b = new File(path).getCanonicalFile().toURI().getPath();
    System.out.println(b);
    String[] basePaths = a.split("/");
    String[] otherPaths = b.split("/");
    int n = 0;
    for(; n < basePaths.length && n < otherPaths.length; n ++)
    {
       if( basePaths[n].equals(otherPaths[n]) == false )
          break;
    }
   // System.out.println("Common length: "+n);
    StringBuffer tmp = new StringBuffer("./");
    for(int m = n; m < basePaths.length - 1; m ++)
       tmp.append("./");
    for(int m = n; m < otherPaths.length; m ++)
    {
       tmp.append(otherPaths[m]);
       if(m < otherPaths.length - 1 ) {
    	   tmp.append("/");
       }
    }

    return tmp.toString();
 }
	
}