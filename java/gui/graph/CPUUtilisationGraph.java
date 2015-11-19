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

import sim.physical.World;
import utility.time.TimeManager;
import config.SettingsManager;

@SuppressWarnings("serial")
public class CPUUtilisationGraph extends AbstractTimeSeriesGraph
{
    // Constants.
	public static final String TAB_STRING = "CPU Utilisation Graph";
    private static final String        TITLE                    = "CPU Utilisation";
    private static final String        Y_TITLE                  = "CPU Utilisation (%)";
    private static final int           NUM_ITEMS_OF_DATA_PER_DC = 1;

    // Singleton Instance.
    private static CPUUtilisationGraph instance                 = null;

    /**
     * TODO
     */
    private CPUUtilisationGraph()
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
            instance = new CPUUtilisationGraph();
        }
    }
    
    /**
     * Get the CPU Utilisation TimeSeries Graph Singleton instance
	 * 
     * @return - the CPUUtilisationGraph Singleton
     */
    public static CPUUtilisationGraph getInstance()
    {
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.TimeSeriesGraph#update()
     */
    void updateData() {
        for (int i = 0; i < World.getInstance().getNumberOfDatacentres(); i++) {
            mGetData.add(World.getInstance().getDatacentre(i).getUtilisation(), (double) TimeManager.getTime(unitTime), i);
            mXYData.addSeries(World.getInstance().getDatacentre(i).getName(), mGetData.get(i));
//            mXYData.addSeries("Utilisation " + i, mGetData.get(i));
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
		 mChart.getXYPlot().getDomainAxis().setRange(-1, 101);	
	}

}
