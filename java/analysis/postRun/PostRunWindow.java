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

package analysis.postRun;

import gui.util.GUIFileReader;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * Post-run analysis window 
 * 
 * @author Sarah Haswell
 */
@Deprecated
public class PostRunWindow implements ActionListener {

	private static PostRunWindow instance = null;
	
	private LinkedList<String[]> dataList;
	private DefaultXYDataset failureSet, costSet, cpuSet, servSet, conSet;
	private LinkedList<DefaultXYDataset[]> statsSets;
	private double[][][] failureStatSeries, costStatSeries, cpuStatSeries, servStatSeries, conStatSeries; //arrays of series to plot with error bars as can't remove series from datasets
	private double[][][] failErrSeries, costErrSeries, cpuErrSeries, servErrSeries, conErrSeries;
	private String currFile, parent;
	
	private JFrame MainFrame;
	private JPanel MainPanel;
	private JPanel ButtonPanel;
	private JTabbedPane tabbedPane;
	private Boolean dcLegend, save;
	private Boolean stats, readStats;
	
	 /**
     * Constructor - creates window and tabs.
     */
	private PostRunWindow() {
		dcLegend = false;
		stats=false;
		readStats = false;
		save=false;
	
		// Create frame
		MainFrame = new JFrame("Cloudsim Post-Run Analysis");
		MainFrame.setSize(1150, 768);
		MainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//MainFrame.addComponentListener(this);
		
		// Create panel
		MainPanel = new JPanel();
		MainPanel.setLayout(new BorderLayout());
		MainFrame.add(MainPanel, BorderLayout.CENTER);
		
		// Create button panel
		ButtonPanel = new JPanel();
		ButtonPanel.setLayout(new BorderLayout());
		MainFrame.add(ButtonPanel, BorderLayout.NORTH);
		
		// Create "change log" button
		JButton fileButton = new JButton("Change log");
		fileButton.addActionListener(this);
		ButtonPanel.add(fileButton, BorderLayout.WEST);
		
		// Create "Compare multiple logs" button
		JButton compButton = new JButton("Compare multiple logs");
		compButton.addActionListener(this);
		ButtonPanel.add(compButton, BorderLayout.CENTER);
		
		// Create "Save Charts" button
		JButton saveButton = new JButton("Save Charts");
		saveButton.addActionListener(this);
		ButtonPanel.add(saveButton, BorderLayout.EAST);
		
		// Find out which log file to look at & load the data
		failureSet = new DefaultXYDataset();
		cpuSet = new DefaultXYDataset();
		servSet = new DefaultXYDataset();
		costSet = new DefaultXYDataset();
		
		chooseFile1();
		//getFileName();
		getData(currFile);
		
		// Create tabs
		tabbedPane = new JTabbedPane();
		failureTab(true);
		cpuTab(true);
		servTab(true);
		costTab(true);
		consistencyTab(true);
		tabbedPane.setSelectedIndex(0);

		// Add + show window
		MainPanel.add(tabbedPane, BorderLayout.CENTER);
		MainFrame.add(MainPanel, BorderLayout.CENTER);
		MainFrame.add(ButtonPanel, BorderLayout.NORTH);
		MainFrame.setLocationByPlatform(true);
		MainFrame.setVisible(true);
	}
	
	 /**
     * Calls the constructor.
     */
	public static void create() {
		if (instance == null)
		{
			instance = new PostRunWindow();
		}
	}
	
	 /**
     * Method which returns the current instance of PostRunWindow - useful for redrawing?
     */
	public PostRunWindow getInstance() {
		return instance;
	}
		
	 /**
     * Method which creates the failure graph tab.
     * 
     * @param state
     * 				Whether this tab should be included or not.
     */
	private void failureTab(Boolean state)
	{
		if (state)
		{
			ChartPanel fPan;
			if(!stats) {fPan= getChart(failureSet, "Server Liveness", "Time / Days", "Live Servers / %", dcLegend, true);}
			else {fPan = getSChart(failureStatSeries, failErrSeries, "Server Liveness", "Time / Days", "Live Servers / %", dcLegend, true);}
			
			
			tabbedPane.addTab("Liveness", fPan);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
		}
		else
		{
			tabbedPane.remove(tabbedPane.indexOfTab("Liveness"));
		}
	}
	
	 /**
     * Method which creates the cpu utilisation graph tab.
     * 
     * @param state
     * 				Whether this tab should be included or not.
     */
	private void cpuTab(Boolean state)
	{
		if (state)
		{
			ChartPanel uPan;
			if(!stats) {uPan = getChart(cpuSet, "CPU Utilisation", "Time / Days", "CPU Utilisation / %", dcLegend, true);}
			else {uPan = getSChart(cpuStatSeries, cpuErrSeries, "CPU Utilisation", "Time / Days", "CPU Utilisation / %", dcLegend, true);}
			
			tabbedPane.addTab("CPU Utilisation", uPan);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
		}
		else
		{
			tabbedPane.remove(tabbedPane.indexOfTab("CPU Utilisation"));
		}
	}
	
	 /**
     * Method which creates the services graph tab.
     * 
     * @param state
     * 				Whether this tab should be included or not.
     */
	private void servTab(Boolean state)
	{
		if (state)
		{
			ChartPanel sPan;
			if(!stats) {sPan = getChart(servSet, "Services Running", "Time / Days", "Number of Services", true, false);}
			else{sPan = getSChart(servStatSeries, servErrSeries, "Services Running", "Time / Days", "Number of Services", true, false);}
			
			tabbedPane.addTab("Services", sPan);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
		}
		else
		{
			tabbedPane.remove(tabbedPane.indexOfTab("Services"));
		}
	}
	
	 /**
     * Method which creates the average cost graph tab.
     * 
     * @param state
     * 				Whether this tab should be included or not.
     */
	private void costTab(Boolean state)
	{
		if (state)
		{
			ChartPanel cPan;
			if(!stats) {cPan = getChart(costSet, "Cost", "Time / Days", "Cost / £ per Hour", dcLegend, false);}
			else {cPan = getSChart(costStatSeries, costErrSeries, "Cost", "Time / Days", "Cost / £ per Hour", dcLegend, false);}

			tabbedPane.addTab("Cost", cPan);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
		}
		else
		{
			tabbedPane.remove(tabbedPane.indexOfTab("Cost"));
		}
	}

	 /**
     * Method which creates the consistency graph tab.
     * 
     * @param state
     * 				Whether this tab should be included or not.
     */
	private void consistencyTab(Boolean state)
	{
		if (state)
		{
			ChartPanel cnPan;
			if(!stats) {cnPan = getChart(conSet, "Inconsistency", "Time / Days", "Inconsistent Nodes / %", dcLegend, true);}
			else {cnPan = getSChart(conStatSeries, conErrSeries, "Inconsistency", "Time / Days", "Inconsistent Nodes / %", dcLegend, true);}

			tabbedPane.addTab("Inconsistency", cnPan);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
		}
		else
		{
			tabbedPane.remove(tabbedPane.indexOfTab("Inconsistency"));
		}
	}
	
	/**
	 * Method which creates and returns a graph in a chartpanel.
	 * 
	 * @param dset
	 * 				Which dataset to make a graph of.
	 * 		title
	 * 				Title for the graph.
	 *      xTitle
	 * 				Title for the x axis.     
	 * 		yTitle
	 * 				Title for the y axis.
	 *      legend
	 * 				Whether or not to include a legend - true if multiple series are to be shown.
	 */
	private ChartPanel getChart(DefaultXYDataset dset, String title, String xTitle, String yTitle, Boolean legend, Boolean percentage) {
		JFreeChart chart = ChartFactory.createXYLineChart(title, xTitle, yTitle, dset, PlotOrientation.VERTICAL, legend, true, false);
		if(percentage) {chart.getXYPlot().getRangeAxis().setRange(0.0, 100.0);}
		
		if(save) {try {
			//System.out.println("Saving charts.");
			ChartUtilities.saveChartAsPNG(new File(currFile.substring(0, currFile.length()-4)+title+"Chart"), chart, 1200, 800 );
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} }
		return new ChartPanel(chart);
	}

	/** Method which creates and returns a graph in a chartpanel.
	 * 
	 * @param dSeries
	 * 				Which dataSeries to make a graph of.
	 * 		title
	 * 				Title for the graph.
	 *      xTitle
	 * 				Title for the x axis.     
	 * 		yTitle
	 * 				Title for the y axis.
	 *      legend
	 * 				Whether or not to include a legend - true if multiple series are to be shown.
	 */
	private ChartPanel getSChart(double[][][] dSeries, double[][][] err, String title, String xTitle, String yTitle, Boolean legend, Boolean percentage) {
		
		int seriesNum = dSeries.length;
		DefaultXYDataset temp = new DefaultXYDataset();
		for(int j=0; j<seriesNum; j++) {
			temp.addSeries(j, dSeries[j]);
		}
		JFreeChart chart = ChartFactory.createXYLineChart(title, xTitle, yTitle, temp, PlotOrientation.VERTICAL, legend, true, false);
		
		if(percentage) {chart.getXYPlot().getRangeAxis().setRange(0.0, 100.0);}
		
		for(int i=0; i<seriesNum; i++) {
			plotErrorBars(chart, dSeries[i], err[i]);
		}
		
		if(save) {try {
			//System.out.println("Saving charts.");
			ChartUtilities.saveChartAsPNG(new File(currFile.substring(0, currFile.length()-4)+title+"Chart"), chart, 1200, 800 );
		} catch (IOException e) {
			System.err.println(e.getMessage());
			//e.printStackTrace();
		} }
		
		return new ChartPanel(chart);
	}

	/** Method which draws error bars from the given series of errors onto the given chart.
	 * 
	 * @param chart
	 * 				The chart to draw the error bars on.
	 * @param values
	 * 				The data series which the errors correspond to.
	 * @param errors
	 * 				The data series containing all the error values at times.
	 */
	private void plotErrorBars(JFreeChart chart, double[][] values, double[][] errors) {
		Stroke stroke = new BasicStroke();
		Paint paint = Color.black;
		XYPlot plot = chart.getXYPlot();
		if (values.length != errors.length) return;
		for (int i=0; i<values[0].length; i=i+5) {
			double x = values[0][i];
			double y = values[1][i];
			//double dx = errors[0][i];
			double dy = errors[1][i];
			//System.out.println(dy);
			XYLineAnnotation vertical = new XYLineAnnotation(x, (y-dy), x, (y+dy), stroke, paint);
			plot.addAnnotation(vertical);
			XYLineAnnotation topBar = new XYLineAnnotation(x-0.25, y+dy, x+0.25, y+dy, stroke, paint);
			plot.addAnnotation(topBar);
			XYLineAnnotation bottomBar = new XYLineAnnotation(x-0.25, y-dy, x+0.25, y-dy, stroke, paint);
			plot.addAnnotation(bottomBar);
		}
	}

	 /**
     * Open log file and read lines of data into dataList, then calls makeSeries to format this data (only calls if file read has worked).
     * If the file is one generated from averages, draw error bars.
     * @param fileName
     * 					File name to open log/ * .csv
     */
	private void getData(String fileName) {
		dataList=new LinkedList<String[]>() ;
		String input[];
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String strln = new String();
			while((strln = in.readLine()) != null) {
				input = strln.split(",");
				dataList.add(input);
			}
			in.close();
			if(fileName.contains(",")) { 
				//System.out.println("Reading averages");
				readStats=true; stats=true;
			}
			makeSeries();
		}
		catch (IOException e) {
			if(!stats) {
				chooseFile1();
				getData(currFile);
				}
			else {System.err.println("Error: File error - "+fileName);}
			//System.err.println("Error: " + e.getMessage());
		}
	}

	 /**
     * Method which iterates through dataList building series to use in graphs.
     */
	private void makeSeries() {
//		System.out.println("in makeSeries, readStats is "+readStats+ " stats is "+stats);
		//reinitialise datasets
		failureSet = new DefaultXYDataset();
		costSet = new DefaultXYDataset();
		cpuSet = new DefaultXYDataset();
		servSet = new DefaultXYDataset();
		conSet = new DefaultXYDataset();
		
		int l = dataList.size();
		String fails, cpus, costs, cons;
		String[] buffer, sFails, sCPUs, sServs, sCosts, test, servS, sErrs, sCons;
		double ts;
		
		//find out how many datacentres there are by doing a trial split
		test = dataList.get(0)[1].split(" ");
		int dcs = test.length;
		if(dcs>1) {dcLegend = true;}
		
		double[][][] failureSeries = new double[dcs][2][l];
		double[][][] costSeries = new double[dcs][2][l];
		double[][][] cpuSeries = new double[dcs][2][l];
		double[][][] servSeries = new double[dcs*4][2][l];
		double[][][] conSeries = new double[dcs][2][l];
		
		//iterate through list
		for(int k=0; k<l; k++) {
			buffer = dataList.get(k);
			ts = Double.parseDouble(buffer[0]);

			//Split into useful data & add to series. May be multiple space-separated datacentres.
			sFails = buffer[1].split(" ");
			sCosts = buffer[2].split(" ");
			sCPUs = buffer[3].split(" ");
			sServs = buffer[4].split(" ");
			sCons = buffer[5].split(" ");
			sErrs = new String[dcs];
			if(readStats==true ) { sErrs = buffer[6].split("'");}

			for(int i=0; i<dcs; i++) { //add a value to the series for each datacentre
				fails = sFails[i];
				fails=(String) fails.subSequence(1, fails.length()-1);
				failureSeries[i][0][k] = ts;
				failureSeries[i][1][k] = (Double.parseDouble(fails));

				costs = sCosts[i];
				costs=(String) costs.subSequence(1, costs.length()-1);
				costSeries[i][0][k] = ts;
				costSeries[i][1][k] = Double.parseDouble(costs);

				cpus = sCPUs[i];
				cpus=(String) cpus.subSequence(1, cpus.length()-1);
				cpuSeries[i][0][k] = ts;
				cpuSeries[i][1][k] = (Double.parseDouble(cpus));

				//services have multiple values
				servS = sServs[i].split("'");
				for(int x=0; x<4; x++) {
					servSeries[i+x][0][k] = ts;
					servSeries[i+x][1][k] = Double.parseDouble(servS[x+1]);
				}
				
				cons = sCons[0];
				cons = (String) cons.subSequence(1, cons.length()-1);
				conSeries[i][0][k] = ts;
				conSeries[i][1][k] = (Double.parseDouble(cons));

				failErrSeries = new double[dcs][2][l];
				costErrSeries = new double[dcs][2][l];
				cpuErrSeries = new double[dcs][2][l];
				servErrSeries = new double[dcs*4][2][l];
				conErrSeries = new double[dcs][2][l];

				if(readStats==true ) {	
					failErrSeries[i][1][k] = Double.parseDouble(sErrs[1]);
					failErrSeries[i][0][k] = ts;
					costErrSeries[i][1][k] = Double.parseDouble(sErrs[2]);
					costErrSeries[i][0][k] = ts;
					cpuErrSeries[i][1][k] = Double.parseDouble(sErrs[3]);
					cpuErrSeries[i][0][k] = ts;

					servErrSeries[i][1][k] = Double.parseDouble(sErrs[4]);
					servErrSeries[i][0][k] = ts;
					servErrSeries[dcs+i][1][k] = Double.parseDouble(sErrs[5]);
					servErrSeries[dcs+i][0][k] = ts;
					servErrSeries[(2*dcs)+i][1][k] = Double.parseDouble(sErrs[6]);
					servErrSeries[(2*dcs)+i][0][k] = ts;
					servErrSeries[(3*dcs)+i][1][k] = Double.parseDouble(sErrs[7]);
					servErrSeries[(3*dcs)+i][0][k] = ts;
					
					conErrSeries[i][1][k] = Double.parseDouble(sErrs[8]);
					conErrSeries[i][0][k] = ts;
					//System.out.println(sErrs[8]);
				}

			}
		}
		
		for(int j=0; j<dcs; j++) {
			failureSet.addSeries("Datacentre " + j, failureSeries[j]);
			costSet.addSeries("Datacentre " + j, costSeries[j]);
			cpuSet.addSeries("Datacentre " + j, cpuSeries[j]);
			servSet.addSeries("Total - Dc " + j, servSeries[j*4]);
			servSet.addSeries("Complete - Dc " + j, servSeries[(j*4)+1]);
			servSet.addSeries("Failed - Dc " + j, servSeries[(j*4)+2]);
			servSet.addSeries("Running - Dc " + j, servSeries[(j*4)+3]);
			conSet.addSeries("Consistency - Dc " + j, conSeries[j]);
		}
		
		failureStatSeries= new double[failureSeries.length][failureSeries[0].length][failureSeries[0][0].length];
		costStatSeries = new double[costSeries.length][costSeries[0].length][costSeries[0][0].length];
		cpuStatSeries = new double[cpuSeries.length][cpuSeries[0].length][cpuSeries[0][0].length];
		servStatSeries = new double[servSeries.length][servSeries[0].length*4][servSeries[0][0].length];
		conStatSeries = new double[conSeries.length][conSeries[0].length*4][conSeries[0][0].length];
		
		if(readStats==true) {
			failureStatSeries = failureSeries;
			costStatSeries = costSeries;
			cpuStatSeries = cpuSeries;
			servStatSeries = servSeries;
			conStatSeries = conSeries;
		}	
	}
	
	/**
	 * Method which creates a dialogue asking the user to input the name of the log file they wish to view.
	 */
	@SuppressWarnings("unused")
	private void getFileName() {
		ImageIcon icon = new ImageIcon();
		Object[] possibilities = null;
		String s = (String)JOptionPane.showInputDialog(
				MainFrame,
				"Enter filename of log, looking in folder log.",
				"Inspect Run",
				JOptionPane.PLAIN_MESSAGE,
				icon,
				possibilities,
				"log/*.csv");

		//If a string was returned, set it as the fileName and call getData.
		if ((s != null) && (s.length() > 0)) {
			//System.out.println("Filename entered: " + s);
			currFile = s;
			return;
		}

		//If you're here, the return value was null/empty.
		JOptionPane.showMessageDialog(MainFrame, "No filename entered.");
	}
	
	/**
     * Method which creates a dialogue asking the user to input the indexes of the log files they wish to compare.
     */
	private String getFileList() {
			ImageIcon icon = new ImageIcon();
		Object[] possibilities = null;
		String s = (String)JOptionPane.showInputDialog(
		                    MainFrame,
		                    "Enter indexes of log files to compare, separated by commas. e.g. '1,2'",
		                    "Compare Log Files",
		                    JOptionPane.PLAIN_MESSAGE,
		                    icon,
		                    possibilities,
		                    ",");

		//If a string was returned, set it as the fileName and call getData.
		if ((s != null) && (s.length() > 0)) {
			//System.out.println("File indexes entered: " + s);
			return s;
		}
		else {
		//If you're here, the return value was null/empty.
			JOptionPane.showMessageDialog(MainFrame, "No filename entered.");
			return s;
		}
	}
	
	/**
     * Method which amalgamates data from several different log files into exciting statistics.
     */
	private void genStats() {
		// Stage 1: Get file names & load all data to statsSet
		String fileList = null;
		while(fileList == null || fileList.equals(",") || (fileList.split(",").length)==0) { //if fileList isn't useable, ask again
			fileList = getFileList();	
		}

		String[] fList = fileList.split(",");
		statsSets = new LinkedList<DefaultXYDataset[]>();
		DefaultXYDataset[] sets =  new DefaultXYDataset[4];

		for(int i=0; i<fList.length; i++) {
			getData("log/log" + fList[i] + ".csv");
			sets=new DefaultXYDataset[5];
			sets[0] = failureSet;
			sets[1] = costSet;
			sets[2] = cpuSet;
			sets[3] = servSet;
			sets[4] = conSet;
			statsSets.add(sets);
			//System.out.println("added "+fList[i]);
		}

		//Stage 2: Go through statsSets and make exciting data. Create a list of arrays, one for averages at each timestep.
		int fNum = statsSets.size();
		int setLength = statsSets.get(0)[0].getItemCount(0);
		//Make set as long as the shortest run to avoid array size errors.
		for(int z=0; z<fNum; z++) {
			setLength = Math.min(setLength, statsSets.get(z)[0].getItemCount(0)) ;
		}
		
		// If files have different number of datasets calculations won't work & comparison isn't meaningful.
		int dcNum = statsSets.get(0)[0].getSeriesCount();
		for(int y=0; y<fNum; y++) {
			if(dcNum != statsSets.get(y)[0].getSeriesCount()) {
				System.err.println("Error: Datacentre counts not equal.");
				System.exit(1); //TODO: change this to not exit but stop everything?
				}
		}

		LinkedList<String[]> writeList = new LinkedList<String[]>();

		//loop through time/length of datacentre
		for(int i = 0; i<setLength; i++) {
			String[] wLine = new String[1 + (6*dcNum)]; //make a new string array for each line

			//loop through each datacentre
			for(int j = 0; j<dcNum; j++) {

				double time = statsSets.get(0)[0].getXValue(0, i); //get timestamp from one of the files
				double[] fVals, costVals, CPUVals, serv0Vals, serv1Vals, serv2Vals, serv3Vals, conVals;
				fVals= new double[fNum];
				costVals= new double[fNum];
				CPUVals= new double[fNum];
				serv0Vals= new double[fNum];
				serv1Vals=new double[fNum];
				serv2Vals=new double[fNum];
				serv3Vals=new double[fNum];
				conVals= new double[fNum];

				//loop through files adding values from each file to arrays
				for(int k = 0; k < fNum ; k++) {
					fVals[k] = statsSets.get(k)[0].getYValue(j, i); //failures series item i in datacentre j at time k
					costVals[k] = statsSets.get(k)[1].getYValue(j, i); //cost series item i in datacentre j at time k
					CPUVals[k] = statsSets.get(k)[2].getYValue(j, i); //cpu series item i in datacentre j at time k
					serv0Vals[k] = statsSets.get(k)[3].getYValue(j*4, i); //services series item i in datacentre j at time k
					serv1Vals[k] = statsSets.get(k)[3].getYValue((j*4)+1, i); //services series item i in datacentre j at time k - complete
					serv2Vals[k] = statsSets.get(k)[3].getYValue((j*4)+2, i); //services series item i in datacentre j at time k - failed		
					serv3Vals[k] = statsSets.get(k)[3].getYValue((j*4)+3, i); //services series item i in datacentre j at time k - running
					conVals[k] = statsSets.get(k)[4].getYValue(j, i);
				}

				//get average & standard deviation of values collected, store them so don't have to calculate twice.
				double[] fAvs = getAvs(fVals);
				double[] costAvs = getAvs(costVals);
				double[] CPUAvs = getAvs(CPUVals);
				double[] serv0Avs = getAvs(serv0Vals);
				double[] serv1Avs = getAvs(serv1Vals);
				double[] serv2Avs = getAvs(serv2Vals);
				double[] serv3Avs = getAvs(serv3Vals);
				double[] conAvs = getAvs(conVals);

				//String.format("%.3g", #) rounds the number to 3dp - doesn't work well on large numbers
				// Stick calculated values into wLine for printing, space to leave room for data on other datacentres.
				wLine[0] = time + ",";
				wLine[1+j] = "'"+ String.format("%.3g", fAvs[0]) + "' ";
				if(j==dcNum-1) {wLine[1+j] = wLine[1+j] + ",";}
				wLine[(1 + dcNum) +j] = "'"+ String.format("%.3g",costAvs[0]) + "' ";
				if(j==dcNum-1) {wLine[1 + dcNum +j] = wLine[1 + dcNum +j] + ",";}
				wLine[1 + (2*dcNum) +j] = "'"+ String.format("%.3g", CPUAvs[0]) + "' ";
				if(j==dcNum-1) {wLine[1 + (2*dcNum) +j] = wLine[1 + (2*dcNum) +j] + ",";}
				wLine[1 + (3*dcNum) +j] = "'"+ String.format("%.3g",serv0Avs[0]) + "'"+ String.format("%.3g",serv1Avs[0]) +"'"+ String.format("%.3g",serv2Avs[0]) + "'"+ String.format("%.3g",serv3Avs[0]) + "' ";
				if(j==dcNum-1) {wLine[1 + (3*dcNum) +j] = wLine[1 + (3*dcNum) +j] + ",";}
				wLine[1 + (4*dcNum) +j] = "'"+ String.format("%.3g", conAvs[0])+"' ";
				if(j==dcNum-1) {wLine[1 + (4*dcNum) +j] = wLine[1 + (4*dcNum) +j] + ",";}
				wLine[1 + (5*dcNum) +j] = "'" + String.format("%.3g",fAvs[1]) + "'" + String.format("%.3g",costAvs[1]) + "'" + String.format("%.3g",CPUAvs[1]) + "'" + String.format("%.3g",serv0Avs[1]) + "'" + String.format("%.3g",serv1Avs[1]) + "'" + String.format("%.3g",serv2Avs[1]) + "'" + String.format("%.3g",serv3Avs[1]) + "'" + String.format("%.3g",conAvs[1])  +"' ";
				if(j==dcNum-1) {wLine[1 + (5*dcNum) +j] = wLine[2 + (4*dcNum) +j] + ",";}
			}
			writeList.add(wLine);
		}

		String filePath = parent+"/"+fileList+".csv";
		writeToFile(filePath, writeList);
		readStats=true;
		//System.out.println(readStats);
		getData(filePath);
	}

	/**
	 * Method which calculates the mean and standard deviation of an array of doubles.
	 * @param vals
	 * 				Array of doubles to find average and sd of.
	 */
	private double[] getAvs(double[] vals) {
		double[] avSd = new double[2];
		double tot=0;
		for(int i=0; i<vals.length; i++) {
			tot=tot+vals[i];
		}
		avSd[0]=tot/vals.length;
		double total=0; double buff=0;
		for(int j=0; j<vals.length; j++) {
			buff = vals[j] - avSd[0];
			total = total+ Math.pow(buff,2);
		}
		avSd[1]=Math.sqrt((total/vals.length));
//		System.out.println(avSd[0]);
//		System.out.println(avSd[1]);
		return avSd;
	}
	
	/**
	 * Method which writes the text passed to the file specified.
	 * @param fileName
	 * 					Which file to write to.
	 * 		  text
	 * 					List of string arrays to write.
	 */
	private void writeToFile(String fileName, LinkedList<String[]> text) {
		//append text to file fileName
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(fileName));
			for(int k=0; k<text.size();k++) {
				for(int l=0; l<text.get(0).length; l++) {
					b.append(text.get(k)[l]);
				}
				if(k!=(text.size()-1)) {b.append('\n');}
			}
			b.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
		
	/**
     * Method which redraws all tabs.
     */
	private void redrawAll() {
		MainPanel.remove(tabbedPane);
		tabbedPane = new JTabbedPane();
		failureTab(true);
		cpuTab(true);
		servTab(true);
		costTab(true);
		consistencyTab(true);
		tabbedPane.setSelectedIndex(0);
		MainPanel.add(tabbedPane, BorderLayout.CENTER);
		MainPanel.revalidate(); //makes it actually appear on redraw!
	}
	
	/**
	 * Method which opens a file chooser so the user can select a log to view.
	 */
//	void chooseFile()
//	{
//		JFileChooser fc = new JFileChooser();
//		fc.setDialogTitle("Select Log");
//		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/log"));
//		fc.setAcceptAllFileFilterUsed(false);
//		FileFilter filter = new FileNameExtensionFilter(" file", "csv");
//		fc.addChoosableFileFilter(filter);
//		int rVal = fc.showOpenDialog(null);
//
//		if (rVal == JFileChooser.APPROVE_OPTION)
//		{
//			currFile = fc.getSelectedFile().getAbsolutePath();
//			//System.out.println(currFile);
//			parent=fc.getSelectedFile().getParent();
//			System.out.println(parent);
//			getData(fc.getSelectedFile().getAbsolutePath());
//		}
//		else {
//			System.exit(0);
//		}
//	}
	
	//dialogTitle="select log"
	//directoryPath="/log"
	//allFileFilterUsed=false
	//description= "file"
	//FileNameExtensionFilter
	//extension = "csv"
	//FileFilter filter = new FileNameExtensionFilter(" file", "csv");
	void chooseFile1()
	{
		JFileChooser fc = GUIFileReader.getFileChooser("Select Log", "/log", 
				new FileNameExtensionFilter(" file", "csv"));
		
		int rVal = fc.showOpenDialog(null);

		if (rVal == JFileChooser.APPROVE_OPTION)
		{
			currFile = fc.getSelectedFile().getAbsolutePath();
			//System.out.println(currFile);
			parent=fc.getSelectedFile().getParent();
			System.out.println(parent);
			getData(fc.getSelectedFile().getAbsolutePath());
		}
		else {
			System.exit(0);
		}
	}
	
	/**
     * Method which handles any action events, i.e. when the log chooser button is pressed.
     * @param e
     * 			Event thrown by button.
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String buttonText = e.getActionCommand();
		if (buttonText.equals("Change log")) //if update button is pressed, update
		{
			chooseFile1();
			//getFileName();
			redrawAll();
		}
		else if(buttonText.equals("Compare multiple logs")) {
			stats=true;
			genStats();
			redrawAll();
			readStats=false;
			stats=false;
		}
		else if(buttonText.equals("Save Charts")) {
			save=true;
			redrawAll();
			save=false;
		}
	}
	
	
}
