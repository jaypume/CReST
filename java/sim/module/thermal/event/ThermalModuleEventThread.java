package sim.module.thermal.event;

import org.apache.log4j.Logger;

import sim.module.event.AbstractModuleEventThread;
import sim.module.event.EventQueue;
import config.SettingsManager;

public class ThermalModuleEventThread extends AbstractModuleEventThread {

	public ThermalModuleEventThread() {
		logger = Logger.getLogger(ThermalModuleEventThread.class);
	}
	
	@Override
  /**
  * Generate the thermal events to update at regular intervals
  * 
  * Return the number of new events
  */
	public int generateEvents() {
	
		int newEventsGenerated = 0;
		
        logger.info("Adding Thermal Events to the event queue...");
        EventQueue.getInstance().addEvent(ThermalEvent.create(0));
        newEventsGenerated++;
       
        logger.info(newEventsGenerated + " new Thermal Event(s) added");
        
        //Adding Logging event for thermal module...
        logger.info("Adding Logging event for thermal. First log time = " + SettingsManager.getInstance().getFirstLogTime());
        EventQueue.getInstance().addEvent(ThermalLogEvent.create(SettingsManager.getInstance().getFirstLogTime()));
        newEventsGenerated++;
        
        logger.info("New Logging Event added");
		return newEventsGenerated;
	}
}
