package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            Rabbit rabbit = new Rabbit();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(rabbit.getInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }

        public Integer getInterval() {
            ClassLoader loader = AlertRabbit.class.getClassLoader();
            Properties properties = new Properties();
            Integer interval = null;
            try (InputStream io = loader.getResourceAsStream("rabbit.properties")) {
                properties.load(io);
                interval = Integer.valueOf(properties.getProperty("rabbit.interval"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return interval;
        }
    }
}