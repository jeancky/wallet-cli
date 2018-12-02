package org.tron.script;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTasks {

    public static void main(String[] args) throws SchedulerException {
        // Grab the Scheduler instance from the Factory
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(
                AwdJob.getJobDetail("AwdJob", "every_1_sec"),
                AwdJob.getTrigger("AwdJobTrigger", "every_1_sec"));

        scheduler.scheduleJob(
                CheckTxJob.getJobDetail("CheckTxJob", "every_3_sec"),
                CheckTxJob.getTrigger("CheckTxJobTrigger", "every_3_sec"));

        // and start it off
        scheduler.start();
    }
}