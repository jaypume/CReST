package sim.module.service.event;

import org.apache.log4j.Logger;

import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.log.LogManager;
import sim.module.log.event.LogEvent;
import sim.module.service.ServiceModuleRunner;
import sim.physical.World;
import utility.Debug;
import utility.time.TimeManager;

public class ServiceLogEvent extends LogEvent {
	
	public static Logger logger = Logger.getLogger(ServiceLogEvent.class);

    // Member variables.
    private final long mLogPeriod;


    public final static int TIME_STRING_COLUMN = 0;
    public final static int SERVICES_RUNNING_COLUMN = 1;
    public final static int SERVICES_FAILED_COLUMN = 2;
    public final static int SERVICES_QUEUED_COLUMN = 3; //services that could not be started are queued   
    public final static int SERVICES_COMPLETE_COLUMN = 4;
    public final static int SERVICES_TOTAL_COLUMN = 5;
    public final static int UTILISATION_COLUMN_PERCENT = 6;
    public final static int FAILURES_COLUMN_PERCENT = 7;
    
    public final static String COLUMN_TITLE_STRING = "Simulation Time, " +
    		"Services (Running), Services (Failed), Services (Queued), " +
    		"Services (Completed),  Services (Total), " +
    		"Server Utilisation (%), Servers Working (%)";
    
	protected ServiceLogEvent(long pStartTime) {
		
		super(pStartTime);
		
		mLogPeriod = ServiceModuleRunner.getInstance().getLogPeriod();
	}

    /**
     * Create and return a new log event.
     * 
     * @param pStartTime
     *            the time at which to execute the log event.
     * @return the new log event.
     */
    public static ServiceLogEvent create(final long pStartTime)
    {
        return new ServiceLogEvent(pStartTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
        EventQueue.getInstance().addEvent(new ServiceLogEvent(mStartTime + mLogPeriod));
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
        	LogManager.writeLog(ServiceModuleRunner.getInstance().getLogWriter(i), toLog(i));
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
        final String services = getServiceString(dc_id);
        final String utilisation = getUtilisationString(dc_id);
        final String failures = getFailureString(dc_id);

        // Add the vales to the log object and return it.
        log.add(timeString);
        log.add(services);
        log.add(utilisation);
        log.add(failures);

        return log;
    }

}
