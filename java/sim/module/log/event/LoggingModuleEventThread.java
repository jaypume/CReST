package sim.module.log.event;

import org.apache.log4j.Logger;

import sim.module.event.AbstractModuleEventThread;
import sim.module.event.EventQueue;
import config.SettingsManager;

public class LoggingModuleEventThread extends AbstractModuleEventThread {

	public LoggingModuleEventThread() {
		logger = Logger.getLogger(LoggingModuleEventThread.class);
	}
	
	@Override
  
	/**
	 * Generate the log event thread to write logs to file at regular intervals.
	 * 
	 * Return the number of new events
	 */
	public int generateEvents() {

		int newEventsGenerated = 0;
		
		logger.warn("Adding Logging Events to the event queue...");
        
        EventQueue queue = EventQueue.getInstance();
        
        logger.info("First log time = " + SettingsManager.getInstance().getFirstLogTime());
        queue.addEvent(LogEvent.create(SettingsManager.getInstance().getFirstLogTime()));
		
        newEventsGenerated++;
       
        logger.info(newEventsGenerated + " new Logging Event(s) added");
		return newEventsGenerated;        
	}
}
