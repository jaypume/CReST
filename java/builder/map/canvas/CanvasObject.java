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

import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.Color;

import javax.swing.*;

/**
 * Dummy object for now to illustrate the concept; Draws Circles. Should
 * eventually serve as a parent class for all objects to be drawn on the Canvas
 * overloading *at least* the draw() method to represent itself on the canvas.
 */
public class CanvasObject implements ZoomLevelListener, ZoomFactorListener
{
	// Zoom threshold at which we draw a different picture
	public static final double SUPER_ZOOM_THRESHOLD = 10.0;

	// Current Zoom level
	protected ZoomLevel mZLevel;
	protected float mWidth, mHeight;
	protected float mX, mY;
	
	private static final ZoomLevel flipLevel = ZoomLevelFactory.getZoomLevelByName("rack");
	
	
	public CanvasObject(float x, float y, float width, float height,
			ZoomLevelReporter lReport, ZoomFactorReporter fReport)
	{
		mX = x;
		mY = y;
		mWidth = width;
		mHeight = height;
		// Connect up listeners
		lReport.addZoomLevelListener(this);
		fReport.addZoomFactorListener(this);

		mZLevel = lReport.getZoomLevel();
	}

	/**
	 * Draws itself on the Graphics2D object provided. Must always fit in the
	 * box defined by x, y (upper left corner) and width x height.
	 */
	public void draw(Graphics2D g)
	{
		int compare = mZLevel.compareTo(flipLevel);
		System.out.println(compare);
		if (compare > 0)
		{
			g.setColor(Color.BLACK);
			g.fill(new Ellipse2D.Float(mX, mY, mWidth, mHeight));
		} else
		{
			g.setColor(Color.RED);
			for (float i = 0f; i < mWidth; i += mWidth / 5)
				for (float j = 0f; j < mHeight; j += mHeight / 10)
					g.fill(new Ellipse2D.Float(mX + i, mY + j, mWidth / 5,
							mHeight / 10));
		}
	}

	/**
	 * Simple function to decide if the object should be drawn on the canvas or
	 * not; Checks if the bounding box of the objects intersects with the
	 * bounding box defined by x1, y1(upper left corner) and x2, y2(lower
	 * right). Useful when this isn't just a dummy object, but needs to access
	 * the Database to retrieve data as we obviously don't need to draw objects
	 * that are outside the canvas bounds.
	 */
	public boolean intersectsWith(double x1, double y1, double x2, double y2)
	{
		return !((x1 > mX + mWidth) || (x2 < mX) || (y1 > mY + mHeight) || (y2 < mY));
	}

	/**
	 * Checks if the bounding box of the object contains point (x,y). Used by
	 * the Canvas class to determine whether to to show the popup.
	 */
	public boolean containsPoint(double x, double y)
	{
		return ((mX < x) && (mX + mWidth > x))
				&& ((mY < y) && (mY + mHeight > y));
	}

	/**
	 * Generates a dummy pop-up for now.
	 */
	public void rightClickPopup(JPanel parent, int x, int y)
	{
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();
		menuItem = new JMenuItem("A popup menu item");
		// menuItem.addActionListener(this);
		popup.add(menuItem);
		menuItem = new JMenuItem("Another popup menu item");
		// menuItem.addActionListener(this);
		popup.add(menuItem);

		popup.show(parent, x, y);

	}

	/**
	 * Updates coordinates of the object.
	 */
	public void moveTo(float x, float y)
	{
		mX = x;
		mY = y;
	}

	/**
	 * Overload if you want to do something interesting with the zoom factor
	 */
	@Override
	public void updatedZoomLevel(double pZoomLevel)
	{
	}

	/**
	 * Overload to react to the zoom Level changing.
	 */
	@Override
	public void zoomLevelChanged(ZoomLevel pNewZoomLevel)
	{
		// TODO Auto-generated method stub
		mZLevel = pNewZoomLevel;
	}
}
