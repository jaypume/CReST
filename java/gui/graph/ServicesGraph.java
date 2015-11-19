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

import sim.module.service.ServiceModuleRunner;
import sim.module.service.bo.Service;
import sim.module.service.bo.ServiceManager;
import sim.physical.World;
import utility.time.TimeManager;
import config.SettingsManager;

public class ServicesGraph extends AbstractTimeSeriesGraph
{

	private static final long serialVersionUID = 1L;
	// Constants.
	public static final String TAB_STRING = "Services Graph";
    private static final String  TITLE                    = "Services Running";
    private static final String  Y_TITLE                  = "Number of Services";
    private static final int     NUM_ITEMS_OF_DATA_PER_DC = 5;

    // Singleton Instance.
    private static ServicesGraph instance                 = null;

    private ServicesGraph()
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
            instance = new ServicesGraph();
        }
    }

    /**
     * Get the ServicesGraph Singleton Instance
     * 
     * @return the ServicesGraph Singleton
     */
    public static ServicesGraph getInstance()
    {
        return instance;
    }

    /**
     * Update the graph with latest data
     * 
     */
    public void updateData()
    {
        World mWorld = World.getInstance();
        
        double time = (double) TimeManager.getTime(unitTime);
        
        for (int i = 0; i < mWorld.mDatacentres.size(); i++)
        {
        	ServiceManager sm = ServiceModuleRunner.getInstance().getServiceManager(i);
        	
            mGetData.add(sm.getTotalServices(), time, i * 5);
            mXYData.addSeries(i + "Total", mGetData.get(i * 5));

            mGetData.add(sm.getRunningServices(), time, i * 5 + 1);
            mXYData.addSeries(i + " " + Service.Status.running, mGetData.get(i * 5 + 1));

            mGetData.add(sm.getFailedServices(), time, i * 5 + 2);
            mXYData.addSeries(i + " " + Service.Status.failed, mGetData.get(i * 5 + 2));

            mGetData.add(sm.getCompletedServices(), time, i * 5 + 3);
            mXYData.addSeries(i + " " + Service.Status.complete, mGetData.get(i * 5 + 3));

            mGetData.add(sm.getStoppedServices(), time, i * 5 + 4);
            mXYData.addSeries(i + " " + Service.Status.stopped, mGetData.get(i * 5 + 4));
        }
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
		 mChart.getXYPlot().getDomainAxis().setLowerBound(-5);	
	}
	
}
