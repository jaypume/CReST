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
package builder.map;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import builder.map.canvas.*;

public class ZoomBar extends JPanel implements ZoomFactorListener
{

	/**
	 * Auto generated serialize ID
	 */
	private static final long serialVersionUID = 3473093342023472948L;

	JSlider mSlide;
	private double mScaleValue = 10;
	private ZoomFactorReporter mReporter;
	private boolean mSuppressScrollUpdate = false;

	ZoomBar(ZoomFactorReporter p)
	{
		p.addZoomFactorListener(this);
		mReporter = p;
		
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Zoom"), new EmptyBorder(10, 10, 10, 10)));
		// Set the maximum and minimum levels based on the minimum and maximum
		// zoom level factors.
		ZoomLevel levels[] = ZoomLevelFactory.getZoomLevels();
		Arrays.sort(levels);
		mSlide = new JSlider(JSlider.VERTICAL,
				scaleZoomToBar(levels[levels.length - 1].getZoomFactor()),
				scaleZoomToBar(levels[0].getZoomFactor()), 10);
		this.setLayout(new BorderLayout());
		this.add(mSlide, BorderLayout.CENTER);
		mSlide.addChangeListener(new scrollStateHandler());
		// Create buttons for each zoom level
		ZoomLevel zoomL[] = ZoomLevelFactory.getZoomLevels();
		Hashtable<Integer,JLabel> buttonTable = new Hashtable<Integer,JLabel>();
		for(ZoomLevel z : zoomL)
		{
			buttonTable.put(scaleZoomToBar(z.getZoomFactor()), new JLabel(z.getName()));
		}
		mSlide.setLabelTable(buttonTable);
		//mSlide.setPaintLabels(true);
	}

	@Override
	public void updatedZoomLevel(double zoomLevel)
	{
		mSuppressScrollUpdate = true;
		mSlide.setValue(scaleZoomToBar(zoomLevel));
		mSuppressScrollUpdate = false;
	}

	/**
	 * Scales the zoom level (a double) to a value the JSlier can understand (an
	 * int). This relationship is not certain to be linear.
	 * 
	 * @param zoom
	 *            the zoom level to be converted
	 * @return the integer representation of this, for use in the JSlider
	 */
	private int scaleZoomToBar(double zoom)
	{
		return (int) (zoom * mScaleValue);
	}

	/**
	 * Scales the value of the JSlider (an int) to the corresponding zoom level
	 * (a double). This is not certain to be a linear relationship.
	 * 
	 * @param value
	 *            the JSlider's value
	 * @return the true zoom level as chosen by the user with the JSlider
	 */
	private double scaleBarToZoom(int value)
	{
		return  value / mScaleValue;
	}

	/*
	private class ZoomLevelButton extends JButton implements ActionListener
	{
		private static final long serialVersionUID = -1229011040752699628L;
		ZoomLevel mZLevel;
		
		ZoomLevelButton(ZoomLevel pZLevel)
		{
			super(pZLevel.getName());
			mZLevel = pZLevel;
			addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			mSlide.setValue(scaleZoomToBar(mZLevel.getZoomFactor()));
		}
		
	}
	*/
	
	/**
	 * This class listens to events from the JSlider and responds to them. If
	 * mSuppressScrollUpdate is false (that is, we're not updating the position
	 * of the JSlider ourselves because the ZoomLevel changed some other way)
	 * then we change the zoom level based on the position of the slider.
	 * 
	 * @author james
	 * 
	 */
	private class scrollStateHandler implements ChangeListener
	{

		@Override
		public void stateChanged(ChangeEvent e)
		{
			// We have updated, we can assume that we're listening only one
			// thing so inform that thing. Also check to make sure that the
			// slider isn't be changed by an outside source as per
			// updatedZoomLevel().
			if (!mSuppressScrollUpdate)
			{
				mReporter.setZoomFactor(scaleBarToZoom(mSlide.getValue()));
			}
		}

	}

}
