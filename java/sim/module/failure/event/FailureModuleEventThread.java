package sim.module.failure.event;

import org.apache.log4j.Logger;

import sim.module.event.AbstractModuleEventThread;
import sim.module.event.EventQueue;
import config.SettingsManager;

public class FailureModuleEventThread extends AbstractModuleEventThread {

	public FailureModuleEventThread() {
		logger = Logger.getLogger(FailureModuleEventThread.class);
	}
	
	@Override
  
	/**
	 *  Generate all 'failure threads' and add them to the event queue. These
	 * 'threads' will then be responsible for generated all future failures (and
	 * fixes).
	 * 
	 * Return the number of new events
	 */
	public int generateEvents() {
	
		//  Generate all 'failure threads' and add them to the event queue. These
	    //  'threads' will then be responsible for generated all future failures (and
	    //  fixes).
		
		int newEventsGenerated = 0;
		
		logger.info("Adding Failure Events to the event queue...");
        
        EventQueue queue = EventQueue.getInstance();
        
        logger.info("Getting failure threads...");
        FailureThreads failureThreads = FailureThreads.getInstance();

        logger.info("Spawning initial failures...");
        failureThreads.spawnInitialFailures();

        for (int i = 0; i < failureThreads.size(); i++)
        {
            queue.addEvent(failureThreads.nextFailure(i));
            newEventsGenerated++;
        }
        
        logger.info(newEventsGenerated + " new Failure Event(s) added");
        
        //Adding Logging event for failures module...
        logger.info("Adding Logging event for failures. First log time = " + SettingsManager.getInstance().getFirstLogTime());
        EventQueue.getInstance().addEvent(FailureLogEvent.create(SettingsManager.getInstance().getFirstLogTime()));
        newEventsGenerated++;
		return newEventsGenerated;        
	}
}
