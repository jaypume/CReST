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
package builder.map.canvas;

import java.awt.*;

public class GlobalObject extends CanvasObject
{
	//private String mName;
	//private Image mGlobeMap;
	//private double fadeInValue = 0.4;
	private static ZoomLevel mGlobalLevel = ZoomLevelFactory
			.getZoomLevelByName("global");
	private float mAlpha = 0;

	public GlobalObject(float x, float y, float width, float height,
			Canvas parent, String name, Image map)
	{
		super(x, y, width, height, parent, parent);
		//mName = name;
		//mGlobeMap = map;
	}

	public void draw(Graphics2D g)
	{
		// if (this.mZLevel.equals(mGlobalLevel))
		// {
		//AlphaComposite alpha = AlphaComposite.getInstance(
				//AlphaComposite.SRC_OVER, mAlpha);
		//g.setComposite(alpha);
		//g.drawImage(mGlobeMap, (int) mX, (int) mY, null);
		// }
	}

	/**
	 * Overload if you want to do something interesting with the zoom factor
	 */
	@Override
	public void updatedZoomLevel(double pZoomLevel)
	{
		if (pZoomLevel <= mGlobalLevel.getZoomFactor())
		{
			mAlpha = 1;
		} else
		{
			// TODO implement map fading as you scroll to it.
			// mAlpha=(float) ((1/pZoomLevel)/20);
			mAlpha = 0;
		}
		// System.out.println("Alpha:" + mAlpha);
		if (mAlpha < 0)
			mAlpha = 0;
		if (mAlpha > 1)
			mAlpha = 1;
	}
}
