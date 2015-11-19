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
package gui.graph;

import sim.module.thermal.ThermalModuleRunner;
import sim.module.thermal.bo.ThermalGrid;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.TimeManager;
import config.SettingsManager;

public class TemperatureTimeGraph extends AbstractTimeSeriesGraph
{

	private static final long serialVersionUID = 1L;
	// Constants.
	public static final String TAB_STRING = "Temperature Graph";
    private static final String     TITLE                    = "Average Temperature per Datacentre"; 
    private static final String     Y_TITLE                  = "Average temperature (\u2103C)";
    private static final int        NUM_ITEMS_OF_DATA_PER_DC = 100;

    // Singleton Instance.
    private static TemperatureTimeGraph instance                 = null;

    /**
     * TODO
     */
    private TemperatureTimeGraph()
    {
        super(TITLE, Y_TITLE, NUM_ITEMS_OF_DATA_PER_DC, TIME_BOUNDS, SettingsManager.getInstance().getUnitTimeForGraphs());
    }

    /**
     * TODO
     */
    public static void create()
    {
        if (instance == null)
        {
            instance = new TemperatureTimeGraph();
        }
    }

    /**
     * Return the TemperatureTimeGraph Singleton instance
     * 
     * @return - the TemperatureTimeGraph Singleton
     */
    public static TemperatureTimeGraph getInstance()
    {
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.TimeSeriesGraph#update()
     */
    void updateData()
    {
        logger.debug("--------start update data.");
        
        World world = World.getInstance();
        final double time = (double) TimeManager.getTime(unitTime);

        for (int i = 0; i < world.mDatacentres.size(); i++)
        {
            final Datacentre dc = world.mDatacentres.get(i);
            
            ThermalGrid grid = ThermalModuleRunner.getInstance().getThermalGrid(dc.getID());
            
            mGetData.add(grid.getAverageTemperature(), time, i);
            mXYData.addSeries(dc.getName(), mGetData.get(i));
        }
//        System.out.println("--------end update data.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.TimeSeriesGraph#setXAxisRange()
     */
    void setXAxisRange()
    {
        
    }

	/*
	 * (non-Javadoc)
	 * @see gui.graph.TimeSeriesGraph#setYAxisRange()
	 */
	void setYAxisRange() {
		mChart.getXYPlot().getDomainAxis().setRange(-1, 101);	
	}
}
