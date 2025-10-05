package com.example.ecommerce.config;

import com.example.ecommerce.jobs.CancelUnpaidOrdersJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz configuration for scheduling background jobs.
 *
 * Registers the {@link CancelUnpaidOrdersJob} as a durable job
 * and defines a trigger that executes the job every 5 minutes.
 *
 * This ensures that unpaid orders older than the cutoff time
 * are regularly cancelled and their reserved inventory is released.
 */
@Configuration
public class QuartzConfig {

    /**
     * Defines the durable job detail for {@link CancelUnpaidOrdersJob}.
     *
     * @return a Quartz {@link JobDetail} representing the cancel unpaid orders job
     */
    @Bean
    public JobDetail cancelUnpaidOrdersJobDetail() {
        return JobBuilder.newJob(CancelUnpaidOrdersJob.class)
                .withIdentity("cancelUnpaidOrdersJob")
                .storeDurably()
                .build();
    }

    /**
     * Configures a Quartz trigger that fires every 5 minutes
     * to execute {@link CancelUnpaidOrdersJob}.
     *
     * @return a Quartz {@link Trigger} for scheduling the job
     */
    @Bean
    public Trigger cancelUnpaidOrdersTrigger() {
        // Run every 5 minutes (adjustable as needed)
        return TriggerBuilder.newTrigger()
                .forJob(cancelUnpaidOrdersJobDetail())
                .withIdentity("cancelUnpaidOrdersTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(5)
                        .repeatForever())
                .build();
    }

}
