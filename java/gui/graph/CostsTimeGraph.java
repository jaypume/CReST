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

import org.apache.log4j.Logger;

import sim.module.costs.CostsModuleRunner;
import sim.physical.World;
import utility.time.LengthOfTime;
import utility.time.TimeManager;
import config.SettingsManager;


public class CostsTimeGraph extends AbstractTimeSeriesGraph
{
	
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(CostsTimeGraph.class);
	
    // Constants.
	public static final String TAB_STRING = "Costs Graph";
    private static final String   TITLE                    = "Costs per Hour";
    private static final String   Y_TITLE                  = "Cost ($)";
    private static final int      NUM_ITEMS_OF_DATA_PER_DC = 1;

    // Singleton Instance.
    private static CostsTimeGraph instance                 = null;

    /**
     * TODO
     */
    private CostsTimeGraph()
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
            instance = new CostsTimeGraph();
        }
    }

    /**
	 * Get the CostsTimeGraph Singleton instance
	 * 
     * @return - the CostsTimeGraph Singleton
     */
    public static CostsTimeGraph getInstance()
    {
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.TimeSeriesGraph#update()
     */
    public void updateData()
    {
        
    	logger.debug(TimeManager.log("updating data..."));
	
	    World mWorld = World.getInstance();

        for (int i = 0; i < mWorld.mDatacentres.size(); i++)
        {
        	
            mGetData.add(CostsModuleRunner.getInstance().getCosts(i).totalCost(LengthOfTime.HOUR.getTimeInSeconds()), (double) TimeManager.getTime(unitTime), i);
//            mXYData.addSeries("Price " + i, mGetData.get(i));
            mXYData.addSeries(mWorld.mDatacentres.get(i).getName(), mGetData.get(i));
            
        }
    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.TimeSeriesGraph#setXAxisRange()
     */
    void setXAxisRange()
    {
    	//mChart.getXYPlot().getDomainAxis().setLowerBound(0.0); //can set lower bound to zero but line disappears off top of chart
        // mChart.getXYPlot().getDomainAxis().setRange(-0.1, 1.1);
    }

	/*
	 * (non-Javadoc)
	 * @see gui.graph.TimeSeriesGraph#setYAxisRange()
	 */
	void setYAxisRange() {
		//let y-range auto-set
	}
}
