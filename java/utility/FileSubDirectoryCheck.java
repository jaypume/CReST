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
 * JCommon : a free general purpose class library for the Java(tm) platform
 * 
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 * 
 * Project Info:  http://www.jfree.org/jcommon/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 * ------------
 * IOUtils.java
 * ------------
 * (C)opyright 2002-2004, by Thomas Morgner and Contributors.
 *
 * Original Author:  Thomas Morgner;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: IOUtils.java,v 1.8 2009/01/22 08:34:58 taqua Exp $
 *
 * Changes
 * -------
 * 26-Jan-2003 : Initial version
 * 23-Feb-2003 : Documentation
 * 25-Feb-2003 : Fixed Checkstyle issues (DG);
 * 29-Apr-2003 : Moved to jcommon
 * 04-Jan-2004 : Fixed JDK 1.2.2 issues with createRelativeURL;
 *               added support for query strings within these urls (TM);
 */

public class FileSubDirectoryCheck {

	  /**
	   * Checks, whether the child directory is a subdirectory of the base 
	   * directory.
	   *
	   * @param base the base directory.
	   * @param child the suspected child directory.
	   * @return true, if the child is a subdirectory of the base directory.
	   * @throws IOException if an IOError occured during the test.
	   */
	  public static boolean isSubDirectory(File base, File child)
	      throws IOException {
	      base = base.getCanonicalFile();
	      child = child.getCanonicalFile();

	      File parentFile = child;
	      while (parentFile != null) {
	          if (base.equals(parentFile)) {
	              return true;
	          }
	          parentFile = parentFile.getParentFile();
	      }
	      return false;
	  }
	  
}
