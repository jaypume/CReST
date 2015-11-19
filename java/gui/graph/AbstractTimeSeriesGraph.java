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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import sim.module.gui.event.UpdateGUIEvent;
import sim.physical.World;
import utility.time.TimeManager;

/**
 * Class that contains a data against Time Graph. Includes data structures and
 * methods to update the graph.
 */
public abstract class AbstractTimeSeriesGraph extends JPanel implements Observer
{
	public static Logger logger = Logger.getLogger(AbstractTimeSeriesGraph.class);
	
	private static final long serialVersionUID = -6483311352605713521L;

	// Constants.
	public static final int TIME_BOUNDS = 100; 

    // Member variables.
	protected TimeManager.UnitTime unitTime; //what time units is the graph in?
	
    protected final String       mTitle;
    protected String       		 mXTitle;
    protected final String       mYTitle;
    protected final int          mNumItemsOfDataPerDC;

    protected JFreeChart         mChart      = null;
    private ChartPanel         	 mChartPanel = null;

    protected DefaultXYDataset   mXYData;
    protected GetData            mGetData;

    private boolean    mFirstDraw  = true;
    private final int  mTimeBounds;

    /**
     * 
     */
    protected AbstractTimeSeriesGraph(final String pTitle, final String pYTitle, final int pNumItemsDataPerDC, final int pTimeBounds, final TimeManager.UnitTime unitTime)
    {
        mTitle = pTitle;
        mXTitle = "Time (" + TimeManager.getTimeUnitString(unitTime) + ")";
        mYTitle = pYTitle;
        mNumItemsOfDataPerDC = pNumItemsDataPerDC;
        mTimeBounds = pTimeBounds;
        this.unitTime = unitTime;
        
        mXYData = new DefaultXYDataset();
        mGetData = new GetData();
    }
    
    /**
     * Initialise the graph data structures 
     */
    public void initialise()
    {
    	mXYData = new DefaultXYDataset();
    	mGetData = new GetData();
    	mGetData.initialise();
    }
    
    /**
     * Set the unit time of the graph
     * @param unitTime
     */
    public void setUnitTime(TimeManager.UnitTime unitTime) {
        mXTitle = "Time (" + TimeManager.getTimeUnitString(unitTime) + ")";
        this.unitTime = unitTime;
    }

    /**
     * Updates the time series graph using the latest data
     * 
     */
    public void update()
    {
        this.removeAll();

    	//JC, Jan 2012, Memory leak fix.  
        //If DefaultXYDataset is not renewed its memory footprint continues to grow
    	mXYData = new DefaultXYDataset();
    	
        updateData();
        updateGUI();
    }
    
    /**
     * Re-draws the chart with the latest data, keeps range of x axis within the time bounds set.
     */
    private void updateGUI()
    {
        mChart = ChartFactory.createXYLineChart(mTitle, mYTitle, mXTitle, mXYData, PlotOrientation.HORIZONTAL, true, false, false);
        final double time = (double) TimeManager.getTime(unitTime); //units will depend on the given graph (months, days, hours, minutes...)

        final double buffer = mTimeBounds / 2;
        double lower = time - (0.9 * mTimeBounds);
        double upper = time + buffer;

        if (lower < 0.0)
        {
            lower = 0.0;
        }
        if (upper < mTimeBounds + buffer)
        {
            upper = mTimeBounds + buffer;
        }

        logger.debug(TimeManager.log(mTitle+ ": Setting XRange: lower="+lower+", upper="+upper + " Unit=" + unitTime));
        mChart.getXYPlot().getRangeAxis().setRange(lower, upper);
        setXAxisRange();
        setYAxisRange();

        if (mFirstDraw == true)
        {
            setChartPanel(new ChartPanel(mChart));
        }
        else
        {
            getChartPanel().setChart(mChart);
        }

        getChartPanel().setPreferredSize(getSize());
        getChartPanel().repaint();
        getChartPanel().revalidate();

        this.add(getChartPanel());

        getChartPanel().updateUI();

        mFirstDraw = false;
    }

    /**
     * Implemented in subclasses as each one uses a different data source. Basically adds the latest value to the end of the dataset. Deletes first if dataset is too long.
     */
    abstract void updateData();

    /**
     * Implemented in subclasses. 
     * Set graph to only show last x datapoints or a certain time period. 
     * This stops the graphs becoming unreadable as more data is squashed into the same width.
     */
    abstract void setXAxisRange();
    
    /**
     * Implemented in subclasses. Set Y-axis range
     */
    abstract void setYAxisRange();


    public ChartPanel getChartPanel() {
		return mChartPanel;
	}

	public void setChartPanel(ChartPanel mChartPanel) {
		this.mChartPanel = mChartPanel;
	}

	class GetData
    {
        ArrayList<ArrayList<Double>> mData;
        ArrayList<ArrayList<Double>> mTimes;

        /**
         * Initialises the data structure
         * 
         * @author Alex Sheppard
         */
        private void initialise()
        {
            mData = new ArrayList<ArrayList<Double>>();
            mTimes = new ArrayList<ArrayList<Double>>();

            for (int i = 0; i < World.getInstance().getNumberOfDatacentres() * mNumItemsOfDataPerDC; i++)
            {
                mData.add(new ArrayList<Double>());
                mTimes.add(new ArrayList<Double>());
            }
        }

        /**
         * Adds require data value.
         * 
         * @param pData
         * @param pTime
         * @param pDatacentre
         */
        protected void add(double pData, double pTime, int pDatacentre)
        {
            mData.get(pDatacentre).add(pData);
            mTimes.get(pDatacentre).add(pTime);

            if (mData.get(pDatacentre).size() > mTimeBounds)
            {
                mData.get(pDatacentre).remove(0);
                mTimes.get(pDatacentre).remove(0);
            }
        }

        protected int sizeData()
        {
            return mData.get(0).size();
        }

        protected int sizeTime()
        {
            return mTimes.get(0).size();
        }

        /**
         * Returns the required data for the given datacentre / dataset
         * 
         * @author Alex Sheppard
         * @param pDatacentre
         *            the required datacentre / dataset
         * @return the required data for the given datacentre / dataset
         */
        protected double[][] get(int pDatacentre)
        {
            double[][] data = new double[2][mData.get(pDatacentre).size()];

            for (int i = 0; i < mData.get(pDatacentre).size(); i++)
            {
                data[0][i] = mData.get(pDatacentre).get(i);
                data[1][i] = mTimes.get(pDatacentre).get(i);
            }
            return data;
        }
    }
	

	/**
	 * Update the time series graph if an UpdateGUI event is passed
	 */
	@Override
	public void update(Observable arg0, Object event) {
		
		//Is it an UpdateGUI event?  
		if (event instanceof UpdateGUIEvent) { 
			
			logger.debug(TimeManager.log("Updating graph: '" + mTitle + "'" + " received event " + event));
            update();
            
		} else { //else ignore
//			logger.debug("Ignoring event: " + event);
		}
	}
}
