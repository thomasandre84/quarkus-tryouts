package org.acme.quartz;

import io.quarkus.runtime.StartupEvent;
import org.quartz.SchedulerException;

import javax.enterprise.event.Observes;
/*
public class MyListenerManager {
    void onStart(@Observes StartupEvent event, org.quartz.Scheduler scheduler) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(new MyJogListener());
        scheduler.getListenerManager().addTriggerListener(new MyTriggerListener());
    }
}
*/