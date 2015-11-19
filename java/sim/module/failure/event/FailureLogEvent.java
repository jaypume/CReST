package sim.module.failure.event;

import org.apache.log4j.Logger;

import sim.module.event.EventQueue;
import sim.module.failure.FailureModuleRunner;
import sim.module.log.Log;
import sim.module.log.LogManager;
import sim.module.log.event.LogEvent;
import sim.physical.World;
import utility.Debug;
import utility.time.TimeManager;

public class FailureLogEvent extends LogEvent {
	
	public static Logger logger = Logger.getLogger(FailureLogEvent.class);

    // Member variables.
    private final long mLogPeriod;


    public final static int TIME_STRING_COLUMN = 0;
    public final static int FAILURES_COLUMN_PERCENT = 1;
    
    public final static String COLUMN_TITLE_STRING = "Simulation Time, " +
    		"Servers Working (%)";
    
	protected FailureLogEvent(long pStartTime) {
		
		super(pStartTime);
		
		mLogPeriod = FailureModuleRunner.TIME_BETWEEN_LOGS;
	}

    /**
     * Create and return a new log event.
     * 
     * @param pStartTime
     *            the time at which to execute the log event.
     * @return the new log event.
     */
    public static FailureLogEvent create(final long pStartTime)
    {
        return new FailureLogEvent(pStartTime); 
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
        EventQueue.getInstance().addEvent(new FailureLogEvent(mStartTime + mLogPeriod));
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
        	LogManager.writeLog(FailureModuleRunner.getInstance().getLogWriter(i), toLog(i));
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
        final String failures = getFailureString(dc_id);

        // Add the vales to the log object and return it.
        log.add(timeString);
        log.add(failures);

        return log;
    }

}
