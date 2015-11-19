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

import java.util.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.Dimension;
import java.awt.event.*;
import java.awt.*;

public class Canvas extends JPanel implements MouseWheelListener,
		MouseInputListener, ZoomFactorReporter, ZoomLevelReporter
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double mZoom;
	private double mOffsetX, mOffsetY;
	private int mLastX, mLastY;
	private ArrayList<ZoomFactorListener> mZoomFactorListeners;
	private ArrayList<ZoomLevelListener> mZoomLevelListeners;
	// Note that 'max' is the most zoomed in, and that 'min' is the most zoomed
	// out.
	private float mMaxZoom = 20;
	private float mMinZoom = 0.1f;
	private ZoomLevel mZoomLevel;
	private JPanel mDraw;

	public Canvas(int width, int height, JPanel toDraw)
	{
		setPreferredSize(new Dimension(width, height));
		// Init variables
		add(toDraw);
		mDraw = toDraw;
		mZoom = 1.0f;
		mZoomLevel = getCurrentZoomLevel();
		mOffsetX = 0f;
		mOffsetY = 0f;
		mLastX = 0;
		mLastY = 0;
		// Mouse events
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		// Zoom factor listeners
		mZoomFactorListeners = new ArrayList<ZoomFactorListener>();
		// Zoom level listeners
		mZoomLevelListeners = new ArrayList<ZoomLevelListener>();		
		// Background colour
		this.setBackground(Color.lightGray);
		
	}

	
	/**
	 * 
	 * Function for running as a stand-alone JFrame for testing purposes
	 * 
	 * public static void main(String[] args) { JFrame thingie = new
	 * JFrame("Canvas"); Canvas mt = new Canvas(800, 800); thingie.add(mt,
	 * BorderLayout.NORTH); //Some test data mt.mObjects.add(new
	 * CanvasObject(10f, 10f, 50f, 50f)); mt.mObjects.add(new CanvasObject(100f,
	 * 100f, 50f, 50f)); mt.mObjects.add(new CanvasObject(10f, 100f, 50f, 50f));
	 * mt.mObjects.add(new CanvasObject(100f, 10f, 50f, 50f)); //TEST
	 * CODE+++++=====+++++++++++++ // DraggablePanel dp = new DraggablePanel();
	 * JLabel jl = new JLabel("Drag thingie"); dp.add(jl); thingie.add(dp,
	 * BorderLayout.SOUTH);
	 * 
	 * CanvasTransferHandler cth = new CanvasTransferHandler();
	 * 
	 * DragSource ds = new DragSource(); new DropTarget( mt,
	 * DnDConstants.ACTION_MOVE, cth ); ds.createDefaultDragGestureRecognizer(
	 * dp, DnDConstants.ACTION_MOVE, cth);
	 * 
	 * //END OF TEST CODE====+++++++===++++ thingie.setVisible(true);
	 * thingie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); }
	 */

	/**
	 * Draws the canvas contents
	 */
	public void paint(Graphics g)
	{
		
		
		// Initialize/declare local variables
		Graphics2D g2d = (Graphics2D) g;
		Rectangle boundBox;
		@SuppressWarnings("unused")
		double x1, x2, y1, y2;
		// Clear, zoom and translate the canvas
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.translate(mOffsetX, mOffsetY);
		//g2d.scale(mZoom, mZoom);
		// Calculate get the bounds
		boundBox = g.getClipBounds();
		x1 = boundBox.getX();
		y1 = boundBox.getY();
		x2 = x1 + boundBox.getWidth();
		y2 = y1 + boundBox.getHeight();
		// Draw the background
		g2d.setColor(this.getBackground());
		g2d.fill(boundBox);
		// Decide which objects need to be drawn
		mDraw.setPreferredSize(new Dimension((int) (1000*mZoom),(int) (1000*mZoom)));
		mDraw.revalidate();
		
		//mDraw.setSize(100, 100);
		//mDraw.repaint();
		super.paint(g);
		//mDraw.paint(g);
	}

	/**
	 * Drags the canvas along with the mouse.
	 */
	public void mouseDragged(MouseEvent e)
	{
		mOffsetX -= mLastX - e.getX();
		mOffsetY -= mLastY - e.getY();
		mLastX = e.getX();
		mLastY = e.getY();
		//System.out.println("Offset: " + mOffsetX + "," + mOffsetY);
		// Enforce Bounds
		// TODO enforce the bounds
		/*
		if (mGlobalObject != null)
		{
			if (mOffsetX < 0) {
				System.out.println("StopX 0");
				mOffsetX = 0;
			}
			if (mOffsetX > mGlobalObject.mWidth) {
				System.out.println("StopY 0");
				mOffsetX = mGlobalObject.mWidth;
			}
			if (mOffsetY < 0){
				System.out.println("StopX Max");
				mOffsetY = 0;
			}
			if (mOffsetY > mGlobalObject.mHeight) {
				System.out.println("StopY Max");
				mOffsetY = mGlobalObject.mHeight;
			}
		}
		*/
		repaint();
	}

	/**
	 * Zooms in and out
	 */
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		setZoomFactor(mZoom * (1 - ((double) e.getWheelRotation()) / 10),
				e.getX(), e.getY());
	}

	/**
	 * Stores initial mouse coordinates before dragging.
	 */
	public void mousePressed(MouseEvent e)
	{
		mLastX = e.getX();
		mLastY = e.getY();
	}

	/**
	 * Called when a mouse is pressed an released without moving (clicked); So
	 * far only used to Super-Zoom on double-click.
	 */
	public void mouseClicked(MouseEvent e)
	{
		switch (e.getClickCount())
		{
		case 1:
//			if (e.getButton() == MouseEvent.BUTTON3)
//				for (CanvasObject co : mObjects)
//					if (co.containsPoint(convertMouseXToCanvasUnits(e.getX()),
//							convertMouseYToCanvasUnits(e.getY())))
//						co.rightClickPopup(this, e.getX(), e.getY());
//			break;
		case 2:
			setZoomFactor(CanvasObject.SUPER_ZOOM_THRESHOLD, e.getX(), e.getY());
			repaint();
			break;
		default:
			break;
		}
	}

	/**
	 * Updates the mZoom value and makes sure that point (cX, cY) stays in the
	 * position in the program window.
	 */
	public void setZoomFactor(double zoom, int cX, int cY)
	{
		// Enforce bounds
		if (zoom > mMaxZoom)
			zoom = mMaxZoom;
		if (zoom < mMinZoom)
			zoom = mMinZoom;
		double oldZoom = mZoom;
		mZoom = zoom;
		mOffsetX += (mOffsetX - cX) * (mZoom / oldZoom - 1);
		mOffsetY += (mOffsetY - cY) * (mZoom / oldZoom - 1);
		// Update the listeners
		for (ZoomFactorListener lz : this.mZoomFactorListeners)
		{
			lz.updatedZoomLevel((float) mZoom);
		}
		// Have we changed zoom level?
		ZoomLevel newZoomLevel = getCurrentZoomLevel();
		if (newZoomLevel != mZoomLevel)
		{
			// Notify all ZoomLevel Listeners
			for(ZoomLevelListener l : mZoomLevelListeners)
			{
				l.zoomLevelChanged(newZoomLevel);
			}
			// Update the variable
			mZoomLevel = newZoomLevel;
		}
		repaint();
	}

	/**
	 * Calculates the current Zoom Level based on the current mZoom factor.
	 * @return the ZoomLevel;
	 */
	private ZoomLevel getCurrentZoomLevel()
	{
		ZoomLevel[] zooms = ZoomLevelFactory.getZoomLevels();
		
		for(int i=zooms.length-1;i>0;i--)
		{
			// Check to see if we're in this lump of zoom level
			if (mZoom < zooms[i-1].getZoomFactor())
			{
				return zooms[i];
			}
			
		}
		// We're checking the last zoom level, this must be it.
		return zooms[0];
	}
	
	public void setZoomFactor(double zoom)
	{
		setZoomFactor(zoom, this.getWidth() / 2, this.getHeight() / 2);
	}

	public void addZoomFactorListener(ZoomFactorListener pListen)
	{
		this.mZoomFactorListeners.add(pListen);
	}

	public void removeZoomFactorListener(ZoomFactorListener pListen)
	{
		this.mZoomFactorListeners.remove(pListen);
	}

	public void addZoomLevelListener(ZoomLevelListener pListen)
	{
		this.mZoomLevelListeners.add(pListen);
	}
	
	public void removeZoomLevelListener(ZoomLevelListener pListen)
	{
		this.mZoomLevelListeners.remove(pListen);
	}
	
	/**
	 * Because of scaling, need to convert mouse coordinates into canvas
	 * coordinates arises; This function implements that.
	 */
	protected double convertMouseXToCanvasUnits(int x)
	{
		return (x - mOffsetX) / mZoom;
	}

	/**
	 * Analogue to above fucntion
	 */
	protected double convertMouseYToCanvasUnits(int y)
	{
		return (y - mOffsetY) / mZoom;
	}

	/**
	 * A method to add objects to the canvas. Now used by drag-and-drop editing
	 * of the canvas.
	 */
//	public void addObject(CanvasObject obj, int x, int y)
//	{
//		mObjects.add(obj);
//		// Again, due to zooming and scaling coordinates are not the same as
//		// mouse x and y.
//		obj.moveTo((float) convertMouseXToCanvasUnits(x),
//				(float) convertMouseYToCanvasUnits(y));
//		repaint();
//	}

	// Unused Mouse events, that are required to implement Interfaces
	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public ZoomLevel getZoomLevel()
	{
		return mZoomLevel;
	}


	@Override
	public double getZoomFactor()
	{
		return mZoom;
	}

}
