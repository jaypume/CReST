package sim.module.thermal.event;

import org.apache.log4j.Logger;

import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.log.LogManager;
import sim.module.log.event.LogEvent;
import sim.module.thermal.ThermalModuleRunner;
import sim.physical.World;
import utility.Debug;
import utility.time.TimeManager;

public class ThermalLogEvent extends LogEvent {
	
	public static Logger logger = Logger.getLogger(ThermalLogEvent.class);

    // Member variables.
    private final long mLogPeriod;

    public final static int TIME_STRING_COLUMN = 0;
    public final static int TEMPERATURE_STRING_COLUMN = 1;
    public final static int FAILURES_COLUMN_PERCENT = 2;
    public final static int UTILISATION_COLUMN_PERCENT = 3;

    public final static String COLUMN_TITLE_STRING = "Simulation Time, " +
    		"Temperature, Servers Working (%), Server Utilisation (%)";
    
	protected ThermalLogEvent(long pStartTime) {
		
		super(pStartTime);
		
		mLogPeriod = ThermalModuleRunner.TIME_BETWEEN_LOGS;
	}

    /**
     * Create and return a new log event.
     * 
     * @param pStartTime
     *            the time at which to execute the log event.
     * @return the new log event.
     */
    public static ThermalLogEvent create(final long pStartTime)
    {
        return new ThermalLogEvent(pStartTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
        EventQueue.getInstance().addEvent(new ThermalLogEvent(mStartTime + mLogPeriod));
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#performEvent()
     */
    @Override
    protected boolean performEvent()
    {
        boolean continueSimulation = true;

        logger.debug(TimeManager.log("Performing log event for each datacentre..."));
        
        // Create a log for each datacentre and print it to file via the log manager.
        for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
        	LogManager.writeLog(ThermalModuleRunner.getInstance().getLogWriter(i), toLog(i));
        }
        return continueSimulation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#toLog()
     */
    protected Log toLog(int dc_id)
    {
        Log log = new Log();

        //the datacentre to log
        logger.info("Logging results for dc_number="+dc_id);
        logger.info(Debug.getMemoryUsage());
        
        // Fetch all the values for the various modules we want to log.
        final String timeString = "\""+TimeManager.getTimeString(mStartTime)+"\"";
        final String temperature = getTemperatureString(dc_id);
        final String failures = getFailureString(dc_id);
        final String utilisation = getUtilisationString(dc_id);
        
        // Add the vales to the log object and return it.
        log.add(timeString);
        log.add(temperature);
        log.add(failures);
        log.add(utilisation);

        return log;
    }

}
